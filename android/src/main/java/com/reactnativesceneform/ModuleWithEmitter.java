package com.reactnativesceneform;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class ModuleWithEmitter {
  public static final String EMIT_GET_NAME = "EMIT_GET_NAME";
  public static final String EMIT_GET_PATH_FILE_SCREEN_SHORT = "EMIT_GET_PATH_FILE_SCREEN_SHORT";

  public static final String ON_SESSION_CREATE = "onSessionCreate";
  public static final String ON_ANCHOR_CREATE = "onAnchorCreate";
  public static final String ON_ANCHOR_RESOLVE = "onAnchorResolve";
  public static final String ON_TAP_PLANE = "onTapPlane";
  public static final String ON_FEATURE_MAP_QUALITY_CHANGE = "onFeatureMapQualityChange";
  public static final String ON_ANCHOR_HOST = "onAnchorHost";
  //public static final String ON_AUGMENTED_FACE_MODEL_READY = "onAugmentedFaceModelReady";

  public static void sendEvent(ReactContext context, String name, WritableMap body) {
    if (context != null) {
      context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(name, body);
    }
  }
}
