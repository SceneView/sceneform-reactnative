package com.reactnativesceneform.module;

import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.reactnativesceneform.scene.ARScene;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.UIManagerModule;
import com.reactnativesceneform.scene.AugmentedFacesScene;

public class AugmentedFacesViewModule extends ReactContextBaseJavaModule {

  @RequiresApi(api = Build.VERSION_CODES.N)
  public AugmentedFacesViewModule(ReactApplicationContext context) {
    super(context);
  }

  @Override
  public String getName() {
    return "SceneformAugmentedFacesModule";
  }

  @ReactMethod
  public void addFaceModel(final int viewTag, String model, String texture, Promise promise) {
    //Log.d("FaceModel", "Adding model to scene");
    UIManagerModule uiManager = getReactApplicationContext().getNativeModule(UIManagerModule.class);
    uiManager.addUIBlock(nativeViewHierarchyManager -> {
      View sceneView = nativeViewHierarchyManager.resolveView(viewTag);
      ((AugmentedFacesScene) sceneView).addFace(model, texture, promise);
    });
  }

  @ReactMethod
  public void takeSnapshot(final int viewTag, Promise promise){
    UIManagerModule uiManager = getReactApplicationContext().getNativeModule(UIManagerModule.class);
    uiManager.addUIBlock(nativeViewHierarchyManager -> {
      View sceneView = nativeViewHierarchyManager.resolveView(viewTag);
      ((AugmentedFacesScene) sceneView).takeScreenshot(promise);
    });
  }

  @ReactMethod
  public void startVideoRecording(final int viewTag, Promise promise){
    UIManagerModule uiManager = getReactApplicationContext().getNativeModule(UIManagerModule.class);
    uiManager.addUIBlock(nativeViewHierarchyManager -> {
      View sceneView = nativeViewHierarchyManager.resolveView(viewTag);
      ((AugmentedFacesScene) sceneView).startVideoRecording(promise);
    });
  }

  @ReactMethod
  public void stopVideoRecording(final int viewTag, Promise promise){
    UIManagerModule uiManager = getReactApplicationContext().getNativeModule(UIManagerModule.class);
    uiManager.addUIBlock(nativeViewHierarchyManager -> {
      View sceneView = nativeViewHierarchyManager.resolveView(viewTag);
      ((AugmentedFacesScene) sceneView).stopVideoRecording(promise);
    });
  }
}
