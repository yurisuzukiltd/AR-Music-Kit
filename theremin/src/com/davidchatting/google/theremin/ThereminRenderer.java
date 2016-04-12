package com.davidchatting.google.theremin;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;
import org.artoolkit.ar.base.rendering.Cube;

import android.app.Activity;
import android.content.Context;
import android.opengl.Matrix;
import android.widget.TextView;

public class ThereminRenderer extends ARRenderer {
	private ARActivity arAvtivity;
	private Context context;
	
	private int leftMarkerID = -1;
	private int rightMarkerID = -1;
	
	private long leftLastSeen = -1;
	private long rightLastSeen = -1;
	
	private Button left = new Button(1.0f,0.0f,0.0f);
	private Button right = new Button(0.0f,0.0f,1.0f);
	
	private Cube cube = new Cube(40.0f, 0.0f, 0.0f, 20.0f);
		
	private TextView textView;
	
	public ThereminRenderer(ARActivity arAvtivity,Context context){
		this.arAvtivity=arAvtivity;
		this.context=context;
	}
	
	@Override
	public boolean configureARScene() {

		leftMarkerID = ARToolKit.getInstance().addMarker("single;Data/patt.left;63");
		rightMarkerID = ARToolKit.getInstance().addMarker("single;Data/patt.right;63");
		if (leftMarkerID < 0 || rightMarkerID<0) return false;

		return true;
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl,config);
		
		/*
		// Load the texture for the square
		left.loadGLTexture(gl,this.context,R.drawable.android);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 	//Black Background
		gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
		
		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
		*/
	}

	@Override
	public void draw(GL10 gl) {
		long now=System.currentTimeMillis();
		ARToolKit ar=ARToolKit.getInstance();
		
    	boolean leftVisible=ar.queryMarkerVisible(leftMarkerID);
    	boolean rightVisible=ar.queryMarkerVisible(rightMarkerID);
    	
    	if(leftVisible)		leftLastSeen=now;
    	if(rightVisible)	rightLastSeen=now;
    	
    	gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    	
    	if(getDebug()){
    		gl.glMatrixMode(GL10.GL_PROJECTION);
    		gl.glLoadMatrixf(ARToolKit.getInstance().getProjectionMatrix(), 0);
    		//gl.glRotatef(180,0.0f,0.0f,1.0f);
    	
    		gl.glEnable(GL10.GL_CULL_FACE);
            gl.glShadeModel(GL10.GL_SMOOTH);
            gl.glEnable(GL10.GL_DEPTH_TEST);        
        	gl.glFrontFace(GL10.GL_CW);
    		
    		gl.glMatrixMode(GL10.GL_MODELVIEW);
    		
    		if(leftVisible){
    			float mLeft[]=ARToolKit.getInstance().queryMarkerTransformation(leftMarkerID);
    			if(mLeft!=null){
    				gl.glLoadMatrixf(mLeft, 0);
    				//left.draw(gl);
    				cube.draw(gl);
    			}
    		}
    		if(rightVisible){
    			float mRight[]=ARToolKit.getInstance().queryMarkerTransformation(rightMarkerID);
    			if(mRight!=null){
    				gl.glLoadMatrixf(mRight, 0);
    				//right.draw(gl);
    				cube.draw(gl);
    			}
    		}
    	}
    	
    	if(leftVisible && rightVisible){
    		float dLeft[]=getPosition(leftMarkerID);
    		float dRight[]=getPosition(rightMarkerID);
    		
    		float dBetween[]=new float[] {dLeft[0]-dRight[0],dLeft[1]-dRight[1],dLeft[2]-dRight[2]};
    		
    		float a=((800.0f-200.0f)/2.0f);
    		float distanceBetween=getDistance(dBetween);
    		float distanceToCamera=Math.min(getDistance(dLeft),getDistance(dRight));
    		
			float rate=Math.max(
					0.5f,
					Math.min((((distanceBetween-a)/a)+1.0f),2.0f));
    		float volume=1.0f-Math.min(
    				Math.max(0,(distanceToCamera-200)/500.0f),
    				1.0f);

    		System.out.println("*");
    		if(getDebug()){
//    			String s="Distance Between: " + distanceBetween + "\nDistance To Camera: " + distanceToCamera;
    			
//    			System.out.println(s);
    			
    			try{
//    	    		textView.setText(s);
//    	    		((Activity) this.context).runOnUiThread(new Runnable() {
//    	                @Override
//    	                public void run() {
////    	                    context.    
//    	                }
//    	            });
    			}
    			catch(Exception e){
    				System.out.println(e);
    			}
    			
    		}
    		
    		playSound(rate,volume);
    	}
    	else if((now-leftLastSeen)>3000 || (now-rightLastSeen)>3000){
    		((ThereminApplication)(ThereminApplication.getInstance())).stopSound();
    	}
	}
	
	boolean getDebug(){
		return(((ThereminApplication)(ThereminApplication.getInstance())).getDebug());
	}
	
	void setDebug(boolean d){
		((ThereminApplication)(ThereminApplication.getInstance())).setDebug(d);
	}
	
	void playSound(float rate,float volume){
		((ThereminApplication)(ThereminApplication.getInstance())).playSound(rate,volume);
	}
	
	float [] getPosition(int markerID) {
		float result[]=new float[] {0,0,0,0};
		
		if (ARToolKit.getInstance().queryMarkerVisible(markerID)) {
			float m[]=ARToolKit.getInstance().queryMarkerTransformation(markerID);
			Matrix.multiplyMV(result,0,m,0,new float[]{0,0,0,1},0);
		}

		return(result);
	}
	
	float getDistance(float d[]){
		return((float)Math.sqrt((d[0]*d[0])+(d[1]*d[1])+(d[2]*d[2])));
	}
}