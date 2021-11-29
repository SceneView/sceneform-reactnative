package com.reactnativesceneform.manager;

import androidx.annotation.NonNull;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.reactnativesceneform.scene.AugmentedFacesScene;

public class AugmentedFacesViewManager extends SimpleViewManager<AugmentedFacesScene> {
  public static final String REACT_CLASS = "SceneformAugmentedFacesView";

  @Override
  @NonNull
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  @NonNull
  public AugmentedFacesScene createViewInstance(ThemedReactContext reactContext) {
    return new AugmentedFacesScene(reactContext);
  }

  @ReactProp(name = "setAugmentedFace")
  public void setAugmentedFace(AugmentedFacesScene view, int index){
    if(index > -1) {
      view.setAugmentedFace(index);
    }
  }
}
