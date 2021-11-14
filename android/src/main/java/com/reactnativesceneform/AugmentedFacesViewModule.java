package com.reactnativesceneform;

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

public class AugmentedFacesViewModule extends ReactContextBaseJavaModule {

  @RequiresApi(api = Build.VERSION_CODES.N)
  AugmentedFacesViewModule(ReactApplicationContext context) {
    super(context);
  }

  @Override
  public String getName() {
    return "SceneformAugmentedFacesModule";
  }
}
