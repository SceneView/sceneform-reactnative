package com.reactnativesceneform;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SceneformPackage implements ReactPackage {
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
      List<NativeModule> modules = new ArrayList<>();
      modules.add(new SceneformViewModule(reactContext));
      modules.add(new AugmentedFacesViewModule(reactContext));
      return modules;
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
      List<ViewManager> managers = new ArrayList<>();
      managers.add(new SceneformViewManager());
      managers.add(new AugmentedFacesViewManager());
      return managers;
    }
}
