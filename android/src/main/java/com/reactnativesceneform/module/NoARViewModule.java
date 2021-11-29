package com.reactnativesceneform.module;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

public class NoARViewModule extends ReactContextBaseJavaModule {
  @RequiresApi(api = Build.VERSION_CODES.N)
  public NoARViewModule(ReactApplicationContext context) {
    super(context);
  }

  @Override
  public String getName() {
    return "SceneformNoARModule";
  }
}
