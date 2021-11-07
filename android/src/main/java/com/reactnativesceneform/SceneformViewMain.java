package com.reactnativesceneform;

import android.app.Activity;
import android.os.Build;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.reactnativesceneform.scene.ARScene;
import com.facebook.react.uimanager.ThemedReactContext;

public class SceneformViewMain extends LinearLayout {
  private Activity mActivity;
  private ARScene arCoreView;

  @RequiresApi(api = Build.VERSION_CODES.N)
  public SceneformViewMain(ThemedReactContext context, Activity activity) {
    super(context);
    mActivity = activity;
    arCoreView = new ARScene(context);
  }
}
