package com.reactnativesceneform.utils;

import android.net.Uri;
import android.widget.Toast;

import com.reactnativesceneform.scene.ARScene;
import com.reactnativesceneform.scene.Model;
import com.facebook.react.uimanager.ThemedReactContext;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class ModelManager {
  public Map<String, Renderable> cachedModels = new HashMap<>();
  private ARScene mParent;
  private ThemedReactContext mContext;

  public ModelManager(ARScene parent){
    mParent = parent;
    mContext = parent.context;
  }
/*
  public void load(String uri, Model modelParent){
    Renderable cachedModel = cachedModels.get(uri);
    if(cachedModel == null){
      WeakReference<ARScene> weakActivity = new WeakReference<>(mParent);
      ModelRenderable.builder()
        .setSource(mContext, Uri.parse(uri))
        .setIsFilamentGltf(true)
        .setAsyncLoadEnabled(true)
        .build()
        .thenAccept(model -> {
          ARScene activity = weakActivity.get();
          if (activity != null) {
            cachedModels.put(uri, model);
            modelParent.updateModel(model);
          }
        })
        .exceptionally(throwable -> {
          Toast.makeText(
            mContext, "Unable to load model", Toast.LENGTH_LONG).show();
          return null;
        });
    }
    else{
      modelParent.updateModel(cachedModel);
    }
  }

 */
}
