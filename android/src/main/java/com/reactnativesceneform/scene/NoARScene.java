package com.reactnativesceneform.scene;

import static com.reactnativesceneform.utils.HelperFuncions.checkIsSupportedDeviceOrFinish;
import static com.reactnativesceneform.utils.HelperFuncions.saveBitmapToDisk;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.CamcorderProfile;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.PixelCopy;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.google.ar.core.AugmentedFace;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.RenderableInstance;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFrontFacingFragment;
import com.google.ar.sceneform.ux.AugmentedFaceNode;
import com.reactnativesceneform.R;
import com.reactnativesceneform.utils.VideoRecorder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SuppressLint("ViewConstructor")
public class NoARScene extends FrameLayout {
  public ThemedReactContext context;
  private final Set<CompletableFuture<?>> loaders = new HashSet<>();
  private SceneView backgroundSceneView;
  private SceneView transparentSceneView;
  private boolean initialised = false;

  public NoARScene(ThemedReactContext context) {
    super(context);
    this.context = context;
    if(!initialised) {
      init();
    }
  }

  public void init() {
    inflate((AppCompatActivity) context.getCurrentActivity(), R.layout.backgroundview, this);
    backgroundSceneView = findViewById(R.id.backgroundSceneView);
    transparentSceneView = findViewById(R.id.transparentSceneView);
    transparentSceneView.setTransparent(true);
    initialised = true;
    loadModels();
  }

  public void loadModels() {
    CompletableFuture<ModelRenderable> dragon = ModelRenderable
      .builder()
      .setSource(context
        , Uri.parse("models/dragon.glb"))
      .setIsFilamentGltf(true)
      .setAsyncLoadEnabled(true)
      .build();

    CompletableFuture<ModelRenderable> backdrop = ModelRenderable
      .builder()
      .setSource(context
        , Uri.parse("models/backdrop.glb"))
      .setIsFilamentGltf(true)
      .setAsyncLoadEnabled(true)
      .build();


    CompletableFuture.allOf(dragon, backdrop)
      .handle((ok, ex) -> {
        try {
          Node modelNode1 = new Node();
          modelNode1.setRenderable(dragon.get());
          modelNode1.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
          modelNode1.setLocalRotation(Quaternion.multiply(
            Quaternion.axisAngle(new Vector3(1f, 0f, 0f), 45),
            Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 75)));
          modelNode1.setLocalPosition(new Vector3(0f, 0f, -1.0f));
          backgroundSceneView.getScene().addChild(modelNode1);

          Node modelNode2 = new Node();
          modelNode2.setRenderable(backdrop.get());
          modelNode2.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
          modelNode2.setLocalRotation(Quaternion.multiply(
            Quaternion.axisAngle(new Vector3(1f, 0f, 0f), 45),
            Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 75)));
          modelNode2.setLocalPosition(new Vector3(0f, 0f, -1.0f));
          backgroundSceneView.getScene().addChild(modelNode2);

          Node modelNode3 = new Node();
          modelNode3.setRenderable(dragon.get());
          modelNode3.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
          modelNode3.setLocalRotation(Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 35));
          modelNode3.setLocalPosition(new Vector3(0f, 0f, -1.0f));
          transparentSceneView.getScene().addChild(modelNode3);

          Node modelNode4 = new Node();
          modelNode4.setRenderable(backdrop.get());
          modelNode4.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
          modelNode4.setLocalRotation(Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 35));
          modelNode4.setLocalPosition(new Vector3(0f, 0f, -1.0f));
          transparentSceneView.getScene().addChild(modelNode4);
        } catch (InterruptedException | ExecutionException ignore) {
          Log.e("NoARView", ignore.toString());
        }
        return null;
      });
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();

  }
}
