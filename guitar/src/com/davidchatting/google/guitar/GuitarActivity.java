package com.davidchatting.google.guitar;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.camera.CaptureCameraPreview;
import org.artoolkit.ar.base.rendering.ARRenderer;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class GuitarActivity extends ARActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);      
		setContentView(R.layout.main);
		
		FrameLayout frameLayout=(FrameLayout)this.findViewById(R.id.mainLayout);
		frameLayout.setOnTouchListener(new View.OnTouchListener() {
	         @Override
	         public boolean onTouch(View v, MotionEvent event) {
	            switch(event.getAction()){
	               case MotionEvent.ACTION_DOWN:{
	            	   setDebug(!getDebug());
	            	   
	            	   //TextView textView=(TextView)findViewById(R.id.debugText);
	            	   //textView.setVisibility(getDebug()?TextView.VISIBLE:TextView.GONE);
	               }
	            }
	            return false;
	         }
	      });
		
		//setCameraDisplayOrientation(this,0,getCameraInstance());
		//CaptureCameraPreview p=getCameraPreview();
	}
	
	@Override
	public View onCreateView(View parent, String name, Context context, AttributeSet attrs){
		View result=super.onCreateView(parent,name,context,attrs);
		
		return(result);
	}
	
	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs){
		View result=super.onCreateView(name,context,attrs);
		
		return(result);
	}
	
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
 
	/**
	 * Provide our own SimpleRenderer.
	 */
	@Override
	protected ARRenderer supplyRenderer() {
		return new GuitarRenderer(this.getBaseContext());
	}
	
	/**
	 * Use the FrameLayout in this Activity's UI.
	 */
	@Override
	protected FrameLayout supplyFrameLayout() {
		return (FrameLayout)this.findViewById(R.id.mainLayout);    	
	}
	
	@SuppressWarnings("deprecation")
	//back is 0 and for Front camera is 1
	public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
	     android.hardware.Camera.CameraInfo info =
	             new android.hardware.Camera.CameraInfo();
	     android.hardware.Camera.getCameraInfo(cameraId, info);
	     int rotation = Surface.ROTATION_180;	//activity.getWindowManager().getDefaultDisplay().getRotation();
	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     System.out.println("result: " + result);
	     camera.setDisplayOrientation(result);
	 }
	
	boolean getDebug(){
		return(((GuitarApplication)(GuitarApplication.getInstance())).getDebug());
	}
	
	void setDebug(boolean d){
		((GuitarApplication)(GuitarApplication.getInstance())).setDebug(d);
	}
}