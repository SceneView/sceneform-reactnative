package com.reactnativesceneform.scene;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.reactnativesceneform.R;
import com.reactnativesceneform.utils.ModelManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.VideoNode;
import java.lang.ref.WeakReference;

public class Model {
  private final ThemedReactContext mContext;
  private Renderable mModel;
  private final ARScene mParent;
  private String mModelUri;
  private Anchor mAnchor;
  private ModelManager mModelManager;
  private TransformableNode mNode;
  public String mAnchorId;
  private boolean destroyed = false;

  //setSource
  //fetchModel
  //setAnchor
  //place

  public Model(ARScene parent){
    mParent = parent;
    mContext = parent.context;
    mModelManager = parent.mModelManager;
  }

  public void setSource(String uri){
    mModelUri = uri;
  }

  public void setAnchor(Anchor anchor){
    mAnchor = anchor;
  }

  public void place(){
    if(mModelUri == null || mAnchor == null){
      return;
    }
    ArFragment arFragment = mParent.arFragment;
    AnchorNode anchorNode = new AnchorNode(mAnchor);
    anchorNode.setParent(arFragment.getArSceneView().getScene());
    Vector3 localScale = new Vector3((float)1, (float)1, (float)1);
    mNode = new TransformableNode(arFragment.getTransformationSystem());
    mNode.setParent(anchorNode);
    //mNode.select();
    //mNode.setWorldScale(localScale);
    //mNode.setEnabled(true);
  }

  public void onUpdate(FrameTime frame){
    if(destroyed){
      return;
    }
    mNode.onUpdate(frame);
  }

  public void destroy(){
    destroyed = true;

    /*
    mNode.setEnabled(false);
    ((AnchorNode)mNode.getParent()).setAnchor(null);
    mNode.setParent(null);

     */
  }

  public void updateModel(Renderable renderable){
    mNode.setRenderable(renderable)
      .animate(true).start();
  }

  public void fetchModel(){
    WeakReference<ARScene> weakActivity = new WeakReference<>(mParent);
    ModelRenderable.builder()
      .setSource(mContext, Uri.parse(mModelUri))
      .setIsFilamentGltf(true)
      .setAsyncLoadEnabled(true)
      .build()
      .thenAccept(renderable -> {
        ARScene activity = weakActivity.get();
        if (activity != null) {
          //mModelManager.cachedModels.put(mModelUri, renderable);
          if(mNode != null){
            this.updateModel(renderable);
          }
        }
      })
      .exceptionally(throwable -> {
        Toast.makeText(
          mContext, "Unable to load model", Toast.LENGTH_LONG).show();
        return null;
      });
  }

  public Anchor getAnchor(){
    return mAnchor;
  }
}
