package com.reactnativesceneform.scene;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.react.uimanager.ThemedReactContext;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.reactnativesceneform.R;
import com.reactnativesceneform.utils.ModelManager;
import java.lang.ref.WeakReference;
import uk.co.appoly.arcorelocation.LocationScene;

public class LocationMarker {
  private Activity mActivity;
  private ThemedReactContext mContext;
  private ARScene mParent;
  private ModelManager mModelManager;
  private String mTitle;
  private double mLng;
  private double mLat;
  private ViewRenderable viewRenderable;
  private LocationScene mLocationScene;
  private Node mNode;
  private uk.co.appoly.arcorelocation.LocationMarker mLocationMarker;
  private boolean isAnchor = false;

  public LocationMarker(ARScene parent){
    mParent = parent;
    mContext = parent.context;
    mActivity = (AppCompatActivity) mContext.getCurrentActivity();
    mModelManager = parent.mModelManager;
    mLocationScene = parent.locationScene;

    mNode = new Node();
    //mNode.setRenderable(viewRenderable);
    Context c = mContext;
    mNode.setOnTapListener((v, event) -> {
      Toast.makeText(
        c, "Touched.", Toast.LENGTH_LONG)
        .show();
    });
  }

  public void setTitle(String title){
    mTitle = title;
  }

  public void setPos(double lng, double lat){
    mLng = lng;
    mLat = lat;
  }

  public void setType(boolean anchor){
    isAnchor = anchor;
  }

  public void create(){
    WeakReference<ARScene> weakActivity = new WeakReference<>(mParent);
    if(!isAnchor){
      ViewRenderable.builder()
        .setView(mContext.getCurrentActivity(), R.layout.view_model_title)
        .build()
        .thenAccept(viewRenderable -> {
          ARScene activity = weakActivity.get();
          if (activity != null) {
            ((TextView)viewRenderable.getView()).setText(mTitle);
            this.viewRenderable = viewRenderable;
            mNode.setRenderable(viewRenderable);
            place();
          }
        })
        .exceptionally(throwable -> {
          return null;
        });
    }
    else{
      ViewRenderable.builder()
        .setView(mContext.getCurrentActivity(), R.layout.anchor)
        .build()
        .thenAccept(viewRenderable -> {
          ARScene activity = weakActivity.get();
          if (activity != null) {
            this.viewRenderable = viewRenderable;
            mNode.setRenderable(viewRenderable);
            place();
          }
        })
        .exceptionally(throwable -> {
          return null;
        });
    }
  }

  public void place(){
    //Log.d("LocationMarker", "Placing "+mTitle+" at: "+mLng+" - "+mLat);
    mLocationMarker = new uk.co.appoly.arcorelocation.LocationMarker(mLng, mLat, mNode);
    mParent.locationScene.mLocationMarkers.add(mLocationMarker);
  }

  public uk.co.appoly.arcorelocation.LocationMarker getMarker(){
    return mLocationMarker;
  }

  //setTitle
  //setPos
  //create
}
