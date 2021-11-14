package com.reactnativesceneform.scene;

import static com.reactnativesceneform.utils.HelperFuncions.checkIsSupportedDeviceOrFinish;
import static com.reactnativesceneform.utils.HelperFuncions.saveBitmapToDisk;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.PixelCopy;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.react.bridge.Promise;
import com.facebook.react.uimanager.ThemedReactContext;
import com.google.ar.core.AugmentedFace;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.RenderableInstance;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFrontFacingFragment;
import com.google.ar.sceneform.ux.AugmentedFaceNode;
import com.reactnativesceneform.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
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
  private Texture foxTexture;
  private ModelRenderable foxModel;

  private int faceIndex = 0;

  private final HashMap<AugmentedFace, AugmentedFaceNode> facesNodes = new HashMap<>();

  public AugmentedFacesScene(ThemedReactContext context) {
    super(context);
    this.context = context;
    init();
  }

  public void init() {
    if (!checkIsSupportedDeviceOrFinish((AppCompatActivity) Objects.requireNonNull(context.getCurrentActivity()))) {
      return;
    }
    inflate((AppCompatActivity) context.getCurrentActivity(), R.layout.augmented_faces, this);
    arFragment = (ArFrontFacingFragment) ((AppCompatActivity) context.getCurrentActivity()).getSupportFragmentManager().findFragmentById(R.id.augmentedFacesFragment);
    assert arFragment != null;
    arFragment.getArSceneView().setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);

    loadModels();
    loadTextures();

    arFragment.setOnAugmentedFaceUpdateListener(this::onAugmentedFaceTrackingUpdate);
  }

  public void setAugmentedFace(int index){
    faceIndex = index;

    for(AugmentedFace augmentedFace : facesNodes.keySet()){
      AugmentedFaceNode existingFaceNode = facesNodes.get(augmentedFace);
      arFragment.getArSceneView().getScene().removeChild(existingFaceNode);
      facesNodes.remove(augmentedFace);
    }
  }

  public void onAugmentedFaceTrackingUpdate(AugmentedFace augmentedFace) {
    if (faceModel == null || faceTexture == null) {
      return;
    }

    AugmentedFaceNode existingFaceNode = facesNodes.get(augmentedFace);
    if(existingFaceNode != null){

    }

    switch (augmentedFace.getTrackingState()) {
      case TRACKING:
        if (existingFaceNode == null) {
          AugmentedFaceNode faceNode = new AugmentedFaceNode(augmentedFace);
          ModelRenderable model;
          Texture texture;
          if(faceIndex == 0){
            model = faceModel.makeCopy();
            texture = faceTexture;
          }
          else{
            model = foxModel.makeCopy();
            texture = foxTexture;
          }
          RenderableInstance modelInstance = faceNode.setFaceRegionsRenderable(model);
          modelInstance.setShadowCaster(false);
          modelInstance.setShadowReceiver(true);

          faceNode.setFaceMeshTexture(texture);

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

  private void loadModels() {
    loaders.add(ModelRenderable.builder()
      .setSource(context, Uri.parse("models/face.glb"))
      .setIsFilamentGltf(true)
      .build()
      .thenAccept(model -> faceModel = model)
      .exceptionally(throwable -> {
        Toast.makeText(context, "Unable to load renderable", Toast.LENGTH_LONG).show();
        return null;
      }));

    loaders.add(ModelRenderable.builder()
      .setSource(context, Uri.parse("models/fox.glb"))
      .setIsFilamentGltf(true)
      .build()
      .thenAccept(model -> foxModel = model)
      .exceptionally(throwable -> {
        Toast.makeText(context, "Unable to load renderable", Toast.LENGTH_LONG).show();
        return null;
      }));
  }

  private void loadTextures() {
    loaders.add(Texture.builder()
      .setSource(context, Uri.parse("textures/face.png"))
      .setUsage(Texture.Usage.COLOR_MAP)
      .build()
      .thenAccept(texture -> faceTexture = texture)
      .exceptionally(throwable -> {
        Toast.makeText(context, "Unable to load texture", Toast.LENGTH_LONG).show();
        return null;
      }));

    loaders.add(Texture.builder()
      .setSource(context, Uri.parse("textures/feckles.png"))
      .setUsage(Texture.Usage.COLOR_MAP)
      .build()
      .thenAccept(texture -> foxTexture = texture)
      .exceptionally(throwable -> {
        Toast.makeText(context, "Unable to load texture", Toast.LENGTH_LONG).show();
        return null;
      }));
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
}
