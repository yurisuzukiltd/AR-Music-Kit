package com.davidchatting.google.musicbox;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;
import org.artoolkit.ar.base.rendering.Cube;

import android.content.Context;
import android.opengl.Matrix;

public class MusicBoxRenderer extends ARRenderer {
	private Context context;
	
	private int markerID_a  = -1;
	private int markerID_b  = -1;
	private int markerID_c  = -1;
	private int markerID_c1 = -1;
	private int markerID_d  = -1;
	private int markerID_e  = -1;
	private int markerID_f  = -1;
	private int markerID_g  = -1;
	
	private long lastSeen_a = -1;
	private long lastSeen_b = -1;
	private long lastSeen_c = -1;
	private long lastSeen_c1 = -1;
	private long lastSeen_d = -1;
	private long lastSeen_e = -1;
	private long lastSeen_f = -1;
	private long lastSeen_g = -1;

	public MusicBoxRenderer(Context context){
		this.context=context;
	}
	
	@Override
	public boolean configureARScene() {
		markerID_a	= ARToolKit.getInstance().addMarker("single;Data/patt.a;64");
		markerID_b	= ARToolKit.getInstance().addMarker("single;Data/patt.b;64");
		markerID_c	= ARToolKit.getInstance().addMarker("single;Data/patt.c;64");
		markerID_c1	= ARToolKit.getInstance().addMarker("single;Data/patt.c1;64");
		markerID_d	= ARToolKit.getInstance().addMarker("single;Data/patt.d;64");
		markerID_e	= ARToolKit.getInstance().addMarker("single;Data/patt.e;64");
		markerID_f	= ARToolKit.getInstance().addMarker("single;Data/patt.f;64");
		markerID_g	= ARToolKit.getInstance().addMarker("single;Data/patt.g;64");
		
		return true;
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl,config);
	}

	/**
	 * Override the draw function from ARRenderer.
	 */
	@Override
	public void draw(GL10 gl) {
		ARToolKit ar=ARToolKit.getInstance();
		long now=System.currentTimeMillis();
		
		if(ar.queryMarkerVisible(markerID_a)){
			if((now-lastSeen_a)>800){
				playSound(MusicBoxApplication.soundID_a,1.0f,1.0f);
			}
			lastSeen_a=now;
		}
		
		if(ar.queryMarkerVisible(markerID_b)){
			if((now-lastSeen_b)>800){
				playSound(MusicBoxApplication.soundID_b,1.0f,1.0f);
			}
			lastSeen_b=now;
		}
		
		if(ar.queryMarkerVisible(markerID_c)){
			if((now-lastSeen_c)>800){
				playSound(MusicBoxApplication.soundID_c,1.0f,1.0f);
			}
			lastSeen_c=now;
		}
		
		if(ar.queryMarkerVisible(markerID_c1)){
			if((now-lastSeen_c1)>800){
				playSound(MusicBoxApplication.soundID_c1,1.0f,1.0f);
			}
			lastSeen_c1=now;
		}
		
		if(ar.queryMarkerVisible(markerID_d)){
			if((now-lastSeen_d)>800){
				playSound(MusicBoxApplication.soundID_d,1.0f,1.0f);
			}
			lastSeen_d=now;
		}
		
		if(ar.queryMarkerVisible(markerID_e)){
			if((now-lastSeen_e)>800){
				playSound(MusicBoxApplication.soundID_e,1.0f,1.0f);
			}
			lastSeen_e=now;
		}
		
		if(ar.queryMarkerVisible(markerID_f)){
			if((now-lastSeen_f)>800){
				playSound(MusicBoxApplication.soundID_f,1.0f,1.0f);
			}
			lastSeen_f=now;
		}
		
		if(ar.queryMarkerVisible(markerID_g)){
			if((now-lastSeen_g)>800){
				playSound(MusicBoxApplication.soundID_g,1.0f,1.0f);
			}
			lastSeen_g=now;
		}
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
	
	void playSound(int soundID,float rate,float volume){
		((MusicBoxApplication)(MusicBoxApplication.getInstance())).playSound(soundID,rate,volume);
	}
}