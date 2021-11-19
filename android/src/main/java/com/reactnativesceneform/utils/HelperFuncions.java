package com.reactnativesceneform.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.PixelCopy;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.ArFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HelperFuncions {

  public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
    String openGlVersionString =
      ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
        .getDeviceConfigurationInfo()
        .getGlEsVersion();
    if (Double.parseDouble(openGlVersionString) < 3.0) {
      Log.e("ArCoreView", "ArCore requires OpenGL ES 3.0 later");
      Toast.makeText(activity, "ArCore requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
        .show();
      activity.finish();
      return false;
    }
    return true;
  }

  public static void takeScreenshot(ArSceneView view, ThemedReactContext context, Promise promise) {
    //ArSceneView view = arFragment.getArSceneView();
    final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
      Bitmap.Config.ARGB_8888);
    final HandlerThread handlerThread = new HandlerThread("PixelCopier");
    handlerThread.start();
    PixelCopy.request(view, bitmap, (copyResult) -> {
      if (copyResult == PixelCopy.SUCCESS) {
        try {
          saveBitmapToDisk(context, bitmap, promise);
        } catch (IOException e) {
          promise.reject("Create screenshot error");
        }
      }
      handlerThread.quitSafely();
    }, new Handler(handlerThread.getLooper()));
  }

  public static void saveBitmapToDisk(ThemedReactContext context, Bitmap bitmap, Promise promise) throws IOException {
    File imageScreen = new File(context.getDir("images", Context.MODE_PRIVATE).toString());
    Calendar c = Calendar.getInstance();
    @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("HH.mm.ss dd-MM-yyyy");
    String formattedDate = df.format(c.getTime());

    File mediaFile = new File(imageScreen, "AVRScreenshot" + formattedDate + ".jpeg");

    FileOutputStream fileOutputStream = new FileOutputStream(mediaFile);
    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream);
    fileOutputStream.flush();
    fileOutputStream.close();
    WritableMap event = Arguments.createMap();
    event.putString("path", mediaFile.getPath());
    promise.resolve(event);
  }

  @SuppressLint("SetTextI18n")
  public void calcDistanceToObject() {
    /*
    Frame frame = arFragment.getArSceneView().getArFrame();
    assert frame != null;
    Pose camera = frame.getCamera().getPose();
    Vector3 objectMatrix = anchorNodeDelete.getWorldPosition();
    Double distance = calculateDistance(objectMatrix, camera);
     */
  }

  public Double calculateDistance(Vector3 objectPose0, Pose objectPose1) {
    return calculateDistance(
      objectPose0.x - objectPose1.tx(),
      objectPose0.y - objectPose1.ty(),
      objectPose0.z - objectPose1.tz()
    );
  }

  public Double calculateDistance(Float x, Float y, Float z) {
    return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
  }
}
