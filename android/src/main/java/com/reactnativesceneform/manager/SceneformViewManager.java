package com.reactnativesceneform.manager;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.reactnativesceneform.scene.ARScene;

public class SceneformViewManager extends SimpleViewManager<ARScene> {
    public static final String REACT_CLASS = "SceneformView";

    @Override
    @NonNull
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    @NonNull
    public ARScene createViewInstance(ThemedReactContext reactContext) {
        return new ARScene(reactContext);
    }

    @ReactProp(name="viewMode")
    public void viewMode(ARScene view, boolean host){
      view.setCurrentMode(host);
    }

    @ReactProp(name="discoverMode")
    public void discoverMode(ARScene view, boolean objects){
      view.setDiscoverMode(objects);
    }

    @ReactProp(name="locationMarkers")
    public void setLocationMarkers(ARScene view, ReadableArray data){
      view.setLocationMarkers(data);
    }

    @ReactProp(name="displayPlanes")
    public void displayPlanes(ARScene view, boolean visible){
      view.setPlaneVisibility(visible);
    }
}
