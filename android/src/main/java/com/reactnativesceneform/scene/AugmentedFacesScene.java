package com.reactnativesceneform.scene;

import static com.reactnativesceneform.utils.HelperFuncions.checkIsSupportedDeviceOrFinish;
import static com.reactnativesceneform.utils.HelperFuncions.saveBitmapToDisk;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.CamcorderProfile;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.PixelCopy;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.google.ar.core.AugmentedFace;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.RenderableInstance;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFrontFacingFragment;
import com.google.ar.sceneform.ux.AugmentedFaceNode;
import com.reactnativesceneform.R;
import com.reactnativesceneform.utils.VideoRecorder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@SuppressLint("ViewConstructor")
public class AugmentedFacesScene extends FrameLayout {
  public ThemedReactContext context;
  public ArFrontFacingFragment arFragment;

  private final Set<CompletableFuture<?>> loaders = new HashSet<>();
  private Texture faceTexture;
  private ModelRenderable faceModel;
  private boolean initialised = false;
  private boolean mIsRecording = false;
  private VideoRecorder mVideoRecorder;

  private final HashMap<AugmentedFace, AugmentedFaceNode> facesNodes = new HashMap<>();
  public final List<FaceModel> mFaces = new ArrayList<FaceModel>();

  public AugmentedFacesScene(ThemedReactContext context) {
    super(context);
    this.context = context;
    if(!initialised) {
      init();
    }
  }

  public void init() {
    if (!checkIsSupportedDeviceOrFinish((AppCompatActivity) Objects.requireNonNull(context.getCurrentActivity()))) {
      return;
    }
    inflate((AppCompatActivity) context.getCurrentActivity(), R.layout.augmented_faces, this);
    arFragment = (ArFrontFacingFragment) ((AppCompatActivity) context.getCurrentActivity()).getSupportFragmentManager().findFragmentById(R.id.augmentedFacesFragment);
    assert arFragment != null;
    arFragment.getArSceneView().setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);
    arFragment.setOnAugmentedFaceUpdateListener(this::onAugmentedFaceTrackingUpdate);
    arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateListener);
    mVideoRecorder = new VideoRecorder();
    mVideoRecorder.setSceneView(arFragment.getArSceneView());
    int orientation = getResources().getConfiguration().orientation;
    mVideoRecorder.setVideoQuality(CamcorderProfile.QUALITY_480P, orientation);
    initialised = true;
  }

  private void onUpdateListener(FrameTime frameTime) {
    for(FaceModel model : mFaces){
      model.onUpdate();
    }
  }

  public void setAugmentedFace(int index){
    FaceModel model = mFaces.get(index);
    if(model != null){
      faceModel = model.faceModel.makeCopy();
      faceTexture = model.faceTexture;
      clearScene();
    }
  }

  public void addFace(String modelUrl, String textureUrl, Promise promise){
    FaceModel model = new FaceModel(this);
    if(!modelUrl.equals("")){
      model.loadModel(modelUrl);
    }
    if(!textureUrl.equals("")){
      model.loadTexture(textureUrl);
    }
    model.setPromise(promise);
    mFaces.add(model);
    //Log.d("FaceModel", "Index: " + (mFaces.indexOf(model)));
  }

  private void clearScene(){
    for(AugmentedFace augmentedFace : facesNodes.keySet()){
      AugmentedFaceNode existingFaceNode = facesNodes.get(augmentedFace);
      if(existingFaceNode != null) {
        arFragment.getArSceneView().getScene().removeChild(existingFaceNode);
      }
      facesNodes.remove(augmentedFace);
    }
  }

  public void onAugmentedFaceTrackingUpdate(AugmentedFace augmentedFace) {
    if (faceModel == null) {
      return;
    }

    AugmentedFaceNode existingFaceNode = facesNodes.get(augmentedFace);
    if(existingFaceNode != null){

    }

    switch (augmentedFace.getTrackingState()) {
      case TRACKING:
        if (existingFaceNode == null) {
          AugmentedFaceNode faceNode = new AugmentedFaceNode(augmentedFace);
          RenderableInstance modelInstance = faceNode.setFaceRegionsRenderable(faceModel);
          modelInstance.setShadowCaster(false);
          modelInstance.setShadowReceiver(true);
          if(faceTexture != null) {
            faceNode.setFaceMeshTexture(faceTexture);
          }
          arFragment.getArSceneView().getScene().addChild(faceNode);
          facesNodes.put(augmentedFace, faceNode);
        }
        break;
      case STOPPED:
        if (existingFaceNode != null) {
          arFragment.getArSceneView().getScene().removeChild(existingFaceNode);
        }
        facesNodes.remove(augmentedFace);
        break;
    }
  }

  public void takeScreenshot(Promise promise) {
    ArSceneView view = arFragment.getArSceneView();
    final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
      Bitmap.Config.ARGB_8888);
    final HandlerThread handlerThread = new HandlerThread("PixelCopier");
    handlerThread.start();
    PixelCopy.request(view, bitmap, (copyResult) -> {
      if (copyResult == PixelCopy.SUCCESS) {
        try {
          saveBitmapToDisk(context, bitmap, promise);
        } catch (IOException e) {
          promise.reject("Create screenshot error");
        }
      }
      handlerThread.quitSafely();
    }, new Handler(handlerThread.getLooper()));
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    ((AppCompatActivity) Objects.requireNonNull(context.getCurrentActivity())).getSupportFragmentManager().beginTransaction().remove(arFragment).commitAllowingStateLoss();
    Thread threadPause = new Thread() {
      @Override
      public void run() {
        try {
          sleep(100);
          Objects.requireNonNull(arFragment.getArSceneView().getSession()).pause();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    Thread thread = new Thread() {
      @Override
      public void run() {
        try {
          threadPause.start();
          sleep(500);
          Objects.requireNonNull(arFragment.getArSceneView().getSession()).close();
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          Log.e("END_", "Finish");
        }
      }
    };
    thread.start();
  }

  public void startVideoRecording(Promise promise) {
    if(arFragment != null && arFragment.getArSceneView().getSession() != null && !mIsRecording){
      mIsRecording =  mVideoRecorder.onToggleRecord();
      WritableMap event = Arguments.createMap();
      if(mIsRecording){
        event.putBoolean("recording", true);
        promise.resolve(event);
      }
      else{
        promise.reject("recording", "false");
      }
    }
  }

  public void stopVideoRecording(Promise promise) {
    if(arFragment != null && arFragment.getArSceneView().getSession() != null && mIsRecording){
      mIsRecording = mVideoRecorder.onToggleRecord();
      String videoPath = mVideoRecorder.getVideoPath().getAbsolutePath();

      WritableMap event = Arguments.createMap();
      event.putString("path", videoPath);
      promise.resolve(event);
    }
  }

}
