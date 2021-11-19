package com.reactnativesceneform.scene;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Promise;
import com.google.ar.core.AugmentedFace;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Texture;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class FaceModel {
  private final Set<CompletableFuture<?>> loaders = new HashSet<>();
  public Texture faceTexture;
  public ModelRenderable faceModel;
  private AugmentedFacesScene mParent;
  private boolean ready = false;
  private Promise promise;
  private boolean error = false;
  private boolean hasTexture = false;

  public FaceModel(AugmentedFacesScene parent){
    mParent = parent;
  }

  public void setPromise(Promise mpromise){
    promise = mpromise;
  }

  public void loadTexture(String url){
    hasTexture = true;
    loaders.add(Texture.builder()
      .setSource(mParent.context, Uri.parse(url))
      .setUsage(Texture.Usage.COLOR_MAP)
      .build()
      .thenAccept(texture -> faceTexture = texture)
      .exceptionally(throwable -> {
        Toast.makeText(mParent.context, "Unable to load texture", Toast.LENGTH_LONG).show();
        return null;
      }));
  }

  public void loadModel(String url){
    loaders.add(ModelRenderable.builder()
      .setSource(mParent.context, Uri.parse(url))
      .setIsFilamentGltf(true)
      .build()
      .thenAccept(model -> faceModel = model)
      .exceptionally(throwable -> {
        Toast.makeText(mParent.context, "Unable to load renderable", Toast.LENGTH_LONG).show();
        return null;
      }));
  }

  public void onUpdate(){
   if(faceModel != null && (faceTexture != null && hasTexture || !hasTexture && faceTexture == null) && !ready && !error) {
     ready = true;
     int index = mParent.mFaces.indexOf(this);
     promise.resolve(index);
   }
  }
}
