package com.davidchatting.google.musicbox;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;
//import org.artoolkit.ar.samples.ARSimple.R;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;

public class MusicBoxActivity extends ARActivity {
	private MusicBoxOnTouchListener onTouchListener=new MusicBoxOnTouchListener();
	private boolean hasSetThereminOnTouchListener=false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setCameraDisplayOrientation(this,0,getCameraInstance());
		
		super.onCreate(savedInstanceState);      
		setContentView(R.layout.main);
		
		/*
		FrameLayout frameLayout=(FrameLayout)this.findViewById(R.id.mainLayout);
		frameLayout.setOnTouchListener(new View.OnTouchListener() {
	         @Override
	         public boolean onTouch(View v, MotionEvent event) {
	            final int actionPeformed = event.getAction();
	            
	            switch(actionPeformed){
	               case MotionEvent.ACTION_DOWN:{
	            	  System.out.println("ACTION_DOWN");
	            	  //final float x = event.getX();
	                  //final float y = event.getY();
	            	  
	            	  //ARToolKit.getInstance().
	                  break;
	               }
	               
	               case MotionEvent.ACTION_MOVE:{
	            	  System.out.println("ACTION_MOVE");
	                  break;
	               }
	               
	               case MotionEvent.ACTION_UP:{
	            	  System.out.println("ACTION_UP");
	                  break;
	               }
	            }
	            return true;
	         }
	      });
	      */
	}
	
	@Override
	public View onCreateView(View parent, String name, Context context, AttributeSet attrs){
		View result=super.onCreateView(parent,name,context,attrs);
		
		if(getGLView()!=null){
			getGLView().setOnTouchListener(onTouchListener);
		}
		
		return(result);
	}
	
	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs){
		View result=super.onCreateView(name,context,attrs);
		
		if(getGLView()!=null){
			getGLView().setOnTouchListener(onTouchListener);
		}
		
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
		return new MusicBoxRenderer(this.getBaseContext());
	}
	
	/**
	 * Use the FrameLayout in this Activity's UI.
	 */
	@Override
	protected FrameLayout supplyFrameLayout() {
		return (FrameLayout)this.findViewById(R.id.mainLayout);    	
	}
	
	@SuppressWarnings("deprecation")
	public static void setCameraDisplayOrientation(Activity activity,
	         int cameraId, android.hardware.Camera camera) {
	     android.hardware.Camera.CameraInfo info =
	             new android.hardware.Camera.CameraInfo();
	     android.hardware.Camera.getCameraInfo(cameraId, info);
	     int rotation = Surface.ROTATION_90;	//activity.getWindowManager().getDefaultDisplay().getRotation();
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
	     camera.setDisplayOrientation(result);
	 }
}