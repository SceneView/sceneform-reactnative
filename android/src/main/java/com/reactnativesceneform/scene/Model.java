package com.reactnativesceneform.scene;

import android.net.Uri;
import android.widget.Toast;
import com.facebook.react.uimanager.ThemedReactContext;
import com.google.ar.core.Anchor;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.reactnativesceneform.utils.ModelManager;
import java.lang.ref.WeakReference;

public class Model {
  private final ThemedReactContext mContext;
  private final ARScene mParent;
  private String mModelUri;
  private Anchor mAnchor;
  private TransformableNode mNode;

  public Model(ARScene parent){
    mParent = parent;
    mContext = parent.context;
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
    mNode = new TransformableNode(arFragment.getTransformationSystem());
    mNode.setParent(anchorNode);
  }

  public void onUpdate(FrameTime frame){
    TrackingState state = ((AnchorNode)mNode.getParent()).getAnchor().getTrackingState();
    mNode.setEnabled(state == TrackingState.TRACKING);
    mNode.onUpdate(frame);
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
