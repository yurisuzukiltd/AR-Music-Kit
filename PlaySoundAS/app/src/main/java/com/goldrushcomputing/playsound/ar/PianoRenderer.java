package com.goldrushcomputing.playsound.ar;

import android.util.Log;
import com.goldrushcomputing.playsound.Example;
import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;

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

	private static final String[] markerTexturePaths = {
			"Texture/Do.png",
			"Texture/Re.png",
			"Texture/Mi.png",
			"Texture/Fa.png",
			"Texture/So.png",
			"Texture/La.png",
			"Texture/Si.png",
			"Texture/Do-.png",
	};

	private static final String[] actionTexturePaths = {
			"Texture/Action_purple.png",
			"Texture/Action_blue.png",
			"Texture/Action_green.png",
			"Texture/Action_yellow.png",
			"Texture/Action_orange.png",
			"Texture/Action_red.png",
			"Texture/Action_brown.png",
			"Texture/Action_black.png",
	};

	private PianoMarker[] markers = new PianoMarker[markerParams.length];

	public PianoRenderer(Example activity) {
		this.activity = activity;

		for (int i = 0; i < markers.length; ++i) {
			PianoMarker marker = new PianoMarker();
			markers[i] = marker;
		}
	}

	@Override
	public boolean configureARScene() {
		for (int i = 0; i < markers.length; ++i) {
			boolean ret = markers[i].init(markerParams[i], i);
			if (!ret) {
				Log.d(TAG, "marker load failed:" + markerParams[i]);
				return false;
			}
		}
		return true;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);

		// 各マーカーテクスチャロード
		for (int i = 0; i < markers.length; ++i) {
			boolean ret0 = markers[i].loadMarkerTexture(gl, activity, markerTexturePaths[i]);
			boolean ret1 = markers[i].loadActionTexture(gl, activity, actionTexturePaths[i]);
			if (!ret0) {
				Log.d(TAG, "marker texture failed:" + markerTexturePaths[i]);
			}
			if (!ret1) {
				Log.d(TAG, "action texture failed:" + actionTexturePaths[i]);
			}
		}

		// Enable Texture Mapping
		gl.glEnable(GL10.GL_TEXTURE_2D);

		// 発音テクスチャが重なって表示された時の抜き表示
		gl.glAlphaFunc(GL10.GL_GEQUAL, 0.5f);
		gl.glEnable(GL10.GL_ALPHA_TEST);
	}

	@Override
	public void draw(GL10 gl) {
		long now = System.currentTimeMillis();

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		for (PianoMarker marker : markers) {
			marker.checkPlaySound(now, activity);
		}

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadMatrixf(ARToolKit.getInstance().getProjectionMatrix(), 0);

		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glFrontFace(GL10.GL_CW);

		gl.glMatrixMode(GL10.GL_MODELVIEW);

		for (Marker marker : markers) {
			marker.draw(gl, now);
		}
	}
}
