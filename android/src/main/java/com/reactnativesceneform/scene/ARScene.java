package com.reactnativesceneform.scene;

import static com.reactnativesceneform.utils.HelperFuncions.checkIsSupportedDeviceOrFinish;
import static com.reactnativesceneform.utils.HelperFuncions.saveBitmapToDisk;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.CamcorderProfile;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.google.android.filament.ColorGrading;
import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.rendering.EngineInstance;
import com.google.ar.sceneform.rendering.Renderer;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.reactnativesceneform.ModuleWithEmitter;
import com.reactnativesceneform.R;
import com.reactnativesceneform.utils.ModelManager;
import com.reactnativesceneform.utils.VideoRecorder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import uk.co.appoly.arcorelocation.LocationScene;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;

@SuppressLint("ViewConstructor")
public class ARScene extends FrameLayout implements BaseArFragment.OnTapArPlaneListener, BaseArFragment.OnSessionConfigurationListener {
  public ThemedReactContext context;
  public ArFragment arFragment;
  public ModelManager mModelManager;
  private final List<Model> mChildren = new ArrayList<>();
  private final List<Anchor> mAnchors = new ArrayList<>();
  public LocationScene locationScene;
  private HostResolveMode viewMode = HostResolveMode.NONE;
  private Session.FeatureMapQuality mFeatureMapQuality;
  private boolean mHosting = false;
  private boolean mHosted = false;
  private Anchor mAnchorToHost;
  private VideoRecorder mVideoRecorder;
  private boolean mIsRecording = false;
  private boolean mDiscoverModeObjects = true;
  private ReadableArray mLocationMarkersData;
  private List<Anchor> mResolvingAnchors = new ArrayList<>();
  private final List<CompletableFuture<Void>> futures = new ArrayList<>();
  private boolean initialised = false;

  private enum HostResolveMode {
    NONE,
    HOSTING,
    RESOLVING,
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  public ARScene(ThemedReactContext context) {
    super(context);
    this.context = context;
    mModelManager = new ModelManager(this);
    if(!initialised) {
      init();
    }
  }

  @SuppressLint("ShowToast")
  @RequiresApi(api = Build.VERSION_CODES.N)
  public void init() {
    if (!checkIsSupportedDeviceOrFinish((AppCompatActivity) Objects.requireNonNull(context.getCurrentActivity()))) {
      return;
    }
    inflate((AppCompatActivity) context.getCurrentActivity(), R.layout.activity_main, this);

    arFragment = (ArFragment) ((AppCompatActivity) context.getCurrentActivity()).getSupportFragmentManager().findFragmentById(R.id.arFragment);
    assert arFragment != null;

    arFragment.getArSceneView().getViewTreeObserver().addOnWindowFocusChangeListener( hasFocus -> {
      if(hasFocus){
        context.getCurrentActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
      }
    });

    arFragment.setOnSessionConfigurationListener(this);
    arFragment.setOnTapArPlaneListener(this);
    arFragment.getArSceneView().setFrameRateFactor(SceneView.FrameRate.FULL);

    WritableMap event = Arguments.createMap();
    event.putBoolean("onSessionCreate", true);
    ModuleWithEmitter.sendEvent(context, ModuleWithEmitter.ON_SESSION_CREATE, event);
    arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateListener);

    mVideoRecorder = new VideoRecorder();
    mVideoRecorder.setSceneView(arFragment.getArSceneView());
    int orientation = getResources().getConfiguration().orientation;
    mVideoRecorder.setVideoQuality(CamcorderProfile.QUALITY_480P, orientation);

    initialised = true;
  }

  private void setOcclusionEnabled(boolean enabled){
    // TODO
  }

  public void onSessionConfiguration(Session session, Config config) {
    if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
      config.setDepthMode(Config.DepthMode.AUTOMATIC);
    }
    config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
    config.setCloudAnchorMode(Config.CloudAnchorMode.ENABLED);
  }

  public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
    Anchor anchor = hitResult.createAnchor();
    mAnchors.add(anchor);
    int index = mAnchors.indexOf(anchor);

    WritableMap event = Arguments.createMap();
    event.putBoolean("onTapPlane", true);
    event.putString("planeId", ""+index);
    ModuleWithEmitter.sendEvent(context, ModuleWithEmitter.ON_TAP_PLANE, event);
  }

  public void addObject(ReadableMap object){
    if(!mDiscoverModeObjects){
      return;
    }
    try{
      String name           = object.getString("name");//source
      String anchorId       = object.getString("anchorId");
      boolean isCloudAnchor = object.getBoolean("isCloudAnchor");

      if(isCloudAnchor){
        Log.i("Resolving anchor?", ""+isCloudAnchor);
        Anchor anchor = Objects.requireNonNull(arFragment.getArSceneView().getSession()).resolveCloudAnchor(anchorId);
        Log.i("Resolved anchor: ", anchor.getCloudAnchorId());
        Model model = new Model(this);
        model.setSource(name);
        model.fetchModel();
        model.setAnchor(anchor);
        model.place();
        mChildren.add(model);

        WritableMap event = Arguments.createMap();
        event.putBoolean("onAnchorResolve", true);
        event.putString("anchorId",         anchor.getCloudAnchorId());
        event.putString("pose",             anchor.getPose().toString());
        event.putString("trackingState",    anchor.getTrackingState().toString());
        event.putString("cloudState",       anchor.getCloudAnchorState().toString());
        event.putInt("HashCode",            anchor.hashCode());
        ModuleWithEmitter.sendEvent(context, ModuleWithEmitter.ON_ANCHOR_RESOLVE, event);
      }
      else{
        Log.i("TapPlane", "Plane tapped and putting model!");
        Anchor anchor = mAnchors.get(Integer.parseInt(anchorId));
        Model model = new Model(this);
        model.setSource(name);
        model.fetchModel();
        model.setAnchor(anchor);
        model.place();
        mChildren.add(model);
      }
    }
    catch(Exception e){
      Log.e("TapPlane", e.toString());
    }
  }

  public void setCurrentMode(boolean host){
    if(host){
      viewMode = HostResolveMode.HOSTING;
    }
    else{
      viewMode = HostResolveMode.RESOLVING;
    }
  }

  public void setPlaneVisibility(boolean visible){
    if(visible){
      if(arFragment != null && arFragment.getArSceneView().getSession() != null){
        arFragment.getArSceneView().getSession().getConfig().setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL);
      }
    }
    else{
      if(arFragment != null && arFragment.getArSceneView().getSession() != null){
        arFragment.getArSceneView().getSession().getConfig().setPlaneFindingMode(Config.PlaneFindingMode.DISABLED);
      }
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

  public void resolveCloudAnchor(String anchorId){
    if(!mDiscoverModeObjects){
      return;
    }
    if(arFragment !=null && arFragment.getArSceneView().getSession() != null){
      Anchor anchor = Objects.requireNonNull(arFragment.getArSceneView().getSession()).resolveCloudAnchor(anchorId);
      mResolvingAnchors.add(anchor);
    }
  }

  public void hostCloudAnchor(int planeIndex) {
    if(!mHosting){
      if(arFragment !=null && arFragment.getArSceneView().getSession() != null) {
        Anchor localAnchor = mAnchors.get(planeIndex);
        if (localAnchor != null) {
          mAnchorToHost = Objects.requireNonNull(arFragment.getArSceneView().getSession()).hostCloudAnchorWithTtl(localAnchor, 365);
          mHosting = true;
        }
      }
    }
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

  public void setDiscoverMode(boolean objects) {
    mDiscoverModeObjects = objects;
    if(objects){
      if(locationScene != null){
        locationScene.clearMarkers();
      }
      setOcclusionEnabled(true);
    }
    else{
      List<Node> children  = new ArrayList<>(arFragment.getArSceneView().getScene().getChildren());
      for(Node node : children){
        if(node instanceof AnchorNode){
          if(((AnchorNode) node).getAnchor() != null){
            Objects.requireNonNull(((AnchorNode) node).getAnchor()).detach();
          }
        }
        if(!(node instanceof Camera)){
          node.setParent(null);
        }
      }
      try {
        mChildren.clear();
        mAnchors.clear();
      }
      catch(Exception e){
        Log.e("ViewMode", e.toString());
      }
      redrawLocationMarkers();
    }
  }

  public void setLocationMarkers(ReadableArray data) {
    mLocationMarkersData = data;
    redrawLocationMarkers();
  }

  private void redrawLocationMarkers(){
    if(mDiscoverModeObjects){
      Log.d("LocationMarkers", "Returning due misconfiguration");
      return;
    }
    setOcclusionEnabled(false);
    if(mLocationMarkersData != null){
      for(int index = 0; index < mLocationMarkersData.size(); index++){
        ReadableMap element = mLocationMarkersData.getMap(index);
        LocationMarker lMarker = new LocationMarker(this);
        assert element != null;
        lMarker.setTitle(element.getString("title"));
        lMarker.setType(element.getBoolean("isAnchor"));
        lMarker.setPos(element.getDouble("lng"), element.getDouble("lat"));
        lMarker.create();
      }
      //Log.d("LocationMarkers", "Created " + locationScene.mLocationMarkers.size() + " markers");
    }
    if (locationScene != null) {
      locationScene.refreshAnchors();
    }
  }

  private void onUpdateListener(FrameTime frameTime) {
    try {
      Frame frame = arFragment.getArSceneView().getArFrame();
      if(frame == null){
        return;
      }
      for(Model model : mChildren){
        model.onUpdate(frameTime);
      }
      com.google.ar.core.Camera camera = frame.getCamera();
      if(camera.getTrackingState() == TrackingState.TRACKING && viewMode == HostResolveMode.HOSTING) {
        Session.FeatureMapQuality featureMapQuality = Objects.requireNonNull(arFragment.getArSceneView().getSession()).estimateFeatureMapQualityForHosting(frame.getCamera().getPose());
        if (mFeatureMapQuality != featureMapQuality) {
          int quality;
          if (featureMapQuality == Session.FeatureMapQuality.INSUFFICIENT) {
            quality = 0;
          } else if (featureMapQuality == Session.FeatureMapQuality.SUFFICIENT) {
            quality = 1;
          } else {
            quality = 2;
          }
          WritableMap event = Arguments.createMap();
          event.putBoolean("onFeatureMapQualityChange", true);
          event.putInt("quality", quality);
          ModuleWithEmitter.sendEvent(context, ModuleWithEmitter.ON_FEATURE_MAP_QUALITY_CHANGE, event);
          mFeatureMapQuality = featureMapQuality;
        }
      }
      if(mHosting && !mHosted){
        Anchor.CloudAnchorState anchorState = mAnchorToHost.getCloudAnchorState();
        if(!anchorState.isError() && anchorState == Anchor.CloudAnchorState.SUCCESS){
          mHosted = true;
          WritableMap event = Arguments.createMap();
          event.putBoolean("onAnchorHost", true);
          event.putString("anchorId", mAnchorToHost.getCloudAnchorId());
          ModuleWithEmitter.sendEvent(context, ModuleWithEmitter.ON_ANCHOR_HOST, event);
        }
      }

      /*
      for(Plane plane : frame.getUpdatedTrackables(Plane.class)) {
        //addObjectModel(Uri.parse("pluto.sfb"));
        break;
      }
       */

      if (locationScene == null) {
        locationScene = new LocationScene(this.context.getCurrentActivity(), arFragment.getArSceneView());
        redrawLocationMarkers();
      }
      if (locationScene != null) {
        locationScene.processFrame(frame);
      }
    }
    catch(Exception e){
      Log.e("onUpdateListener", e.toString());
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    List<Node> children  = new ArrayList<>(arFragment.getArSceneView().getScene().getChildren());
    for(Node node : children){
      if(node instanceof AnchorNode){
        if(((AnchorNode) node).getAnchor() != null){
          Objects.requireNonNull(((AnchorNode) node).getAnchor()).detach();
        }
      }
      if(!(node instanceof Camera)){
        node.setParent(null);
      }
    }
    mResolvingAnchors.clear();
    mAnchors.clear();
    if(mIsRecording){
      mVideoRecorder.onToggleRecord();
    }
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
