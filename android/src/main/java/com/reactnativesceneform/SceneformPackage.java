package com.reactnativesceneform;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.reactnativesceneform.manager.AugmentedFacesViewManager;
import com.reactnativesceneform.manager.NoARViewManager;
import com.reactnativesceneform.manager.SceneformViewManager;
import com.reactnativesceneform.module.AugmentedFacesViewModule;
import com.reactnativesceneform.module.NoARViewModule;
import com.reactnativesceneform.module.SceneformViewModule;

import java.util.ArrayList;
import java.util.List;

public class SceneformPackage implements ReactPackage {
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
      List<NativeModule> modules = new ArrayList<>();
      modules.add(new SceneformViewModule(reactContext));
      modules.add(new AugmentedFacesViewModule(reactContext));
      modules.add(new NoARViewModule(reactContext));
      return modules;
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
      List<ViewManager> managers = new ArrayList<>();
      managers.add(new SceneformViewManager());
      managers.add(new AugmentedFacesViewManager());
      managers.add(new NoARViewManager());
      return managers;
    }
}
