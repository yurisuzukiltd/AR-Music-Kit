package com.goldrushcomputing.playsound;

import android.content.Context;
import android.util.Log;
import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;
import org.artoolkit.ar.base.rendering.Cube;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class PianoRenderer extends ARRenderer {
	private static final String TAG = "PianoRenderer";
	private Example activity;

	private static final String[] markerParams = {
			"single;Data/Do.pat;64",
			"single;Data/Re.pat;64",
			"single;Data/Mi.pat;64",
			"single;Data/Fa.pat;64",
			"single;Data/So.pat;64",
			"single;Data/La.pat;64",
			"single;Data/Si.pat;64",
			"single;Data/Do-.pat;64",
	};

	private Marker[] markers = new Marker[markerParams.length];

	public PianoRenderer(Example activity) {
		this.activity = activity;
	}

	@Override
	public boolean configureARScene() {
		for(int i=0; i<markers.length; ++i) {
			Marker marker = new Marker();
			boolean ret = marker.init(markerParams[i]);
			if( !ret ) {
				Log.d(TAG, "marker load failed:" + markerParams[i]);
				return false;
			}
			markers[i] = marker;
		}
		return true;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);

		/*
		gl.glEnable(GL10.GL_TEXTURE_2D);           //Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL10.GL_SMOOTH);           //Enable Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);   //Black Background
		gl.glClearDepthf(1.0f);                    //Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST);           //Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL);            //The Type Of Depth Testing To Do

		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		*/
	}

	@Override
	public void draw(GL10 gl) {
		long now = System.currentTimeMillis();

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		for(Marker marker : markers) {
			marker.checkPlaySound(now, activity);
		}

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadMatrixf(ARToolKit.getInstance().getProjectionMatrix(), 0);
		//gl.glRotatef(180, 0.0f, 0.0f, 1.0f);

		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glFrontFace(GL10.GL_CW);

		gl.glMatrixMode(GL10.GL_MODELVIEW);

		for(Marker marker : markers) {
			marker.draw(gl);
		}
	}

	private static class Marker {
		private int markerId;
		private long lastTrackedTime = -1L;

		Marker() {
		}

		boolean init(String markerParam) {
			markerId = ARToolKit.getInstance().addMarker(markerParam);
			return markerId >= 0;
		}

		boolean loadTexture(GL10 gl, Context contxt) {
			//guitar.loadGLTexture(gl, context, R.drawable.android);

			return true;
		}

		private boolean isTracked() {
			ARToolKit ar = ARToolKit.getInstance();
			return ar.queryMarkerVisible(markerId);
		}

		void checkPlaySound(long now, Example activity) {
			if( isTracked() ) {
				lastTrackedTime = now;
			} else {
				if (lastTrackedTime > 0 && (now - lastTrackedTime) < 1000) {
					Log.d(TAG, "marker hidden detected");
					lastTrackedTime = -1;
					// TODO: 鳴らす音の指定
					activity.playSound1(null);
				}
			}
		}

		private Cube debugCube = new Cube(40.0f, 0.0f, 0.0f, 20.0f);

		void draw(GL10 gl) {
			if( !isTracked() ) {
				return;
			}

			float markerMatrix[] = ARToolKit.getInstance().queryMarkerTransformation(markerId);
			if (markerMatrix == null) {
				return;
			}

			gl.glLoadMatrixf(markerMatrix, 0);
			debugCube.draw(gl);
		}
	}
}
