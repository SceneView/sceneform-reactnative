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

public class SceneformViewModule extends ReactContextBaseJavaModule {

  @RequiresApi(api = Build.VERSION_CODES.N)
  public SceneformViewModule(ReactApplicationContext context) {
    super(context);
  }

  @Override
  public String getName() {
    return "SceneformModule";
  }

  @ReactMethod
  public void addObject(final int viewTag, ReadableMap object){
    //Log.e("TESTING", "Getting anchor from common module");
    UIManagerModule uiManager = getReactApplicationContext().getNativeModule(UIManagerModule.class);
    uiManager.addUIBlock(nativeViewHierarchyManager -> {
      View sceneView = nativeViewHierarchyManager.resolveView(viewTag);
      ((ARScene) sceneView).addObject(object);
    });
  }

  @ReactMethod
  public void takeSnapshot(final int viewTag, Promise promise){
    UIManagerModule uiManager = getReactApplicationContext().getNativeModule(UIManagerModule.class);
    uiManager.addUIBlock(nativeViewHierarchyManager -> {
      View sceneView = nativeViewHierarchyManager.resolveView(viewTag);
      ((ARScene) sceneView).takeScreenshot(promise);
    });
  }

  @ReactMethod
  public void resolveCloudAnchor(final int viewTag, String anchorId){
    UIManagerModule uiManager = getReactApplicationContext().getNativeModule(UIManagerModule.class);
    uiManager.addUIBlock(nativeViewHierarchyManager -> {
      View sceneView = nativeViewHierarchyManager.resolveView(viewTag);
      ((ARScene) sceneView).resolveCloudAnchor(anchorId);
    });
  }

  @ReactMethod
  public void hostCloudAnchor(final int viewTag, int planeIndex){
    //Log.e("TESTING", "Getting anchor from common module");
    UIManagerModule uiManager = getReactApplicationContext().getNativeModule(UIManagerModule.class);
    uiManager.addUIBlock(nativeViewHierarchyManager -> {
      View sceneView = nativeViewHierarchyManager.resolveView(viewTag);
      ((ARScene) sceneView).hostCloudAnchor(planeIndex);
    });
  }

  @ReactMethod
  public void startVideoRecording(final int viewTag, Promise promise){
    //Log.e("TESTING", "Getting anchor from common module");
    UIManagerModule uiManager = getReactApplicationContext().getNativeModule(UIManagerModule.class);
    uiManager.addUIBlock(nativeViewHierarchyManager -> {
      View sceneView = nativeViewHierarchyManager.resolveView(viewTag);
      ((ARScene) sceneView).startVideoRecording(promise);
    });
  }

  @ReactMethod
  public void stopVideoRecording(final int viewTag, Promise promise){
    //Log.e("TESTING", "Getting anchor from common module");
    UIManagerModule uiManager = getReactApplicationContext().getNativeModule(UIManagerModule.class);
    uiManager.addUIBlock(nativeViewHierarchyManager -> {
      View sceneView = nativeViewHierarchyManager.resolveView(viewTag);
      ((ARScene) sceneView).stopVideoRecording(promise);
    });
  }
}
