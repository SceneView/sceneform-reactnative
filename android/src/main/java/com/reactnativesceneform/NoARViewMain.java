package com.reactnativesceneform;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.reactnativesceneform.scene.ARScene;
import com.facebook.react.uimanager.ThemedReactContext;
import com.reactnativesceneform.scene.NoARScene;

public class NoARViewMain extends LinearLayout {
  private Activity mActivity;
  private NoARScene arCoreView;

  @RequiresApi(api = Build.VERSION_CODES.N)
  public NoARViewMain(ThemedReactContext context) {
    super(context);
    //mActivity = activity;
    this.setOrientation(LinearLayout.VERTICAL);
    this.setBackgroundColor(Color.TRANSPARENT);
    arCoreView = new NoARScene(context);
    setLayoutParams(new android.view.ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.MATCH_PARENT));
    this.addView(arCoreView);
  }
}
