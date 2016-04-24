package com.goldrushcomputing.playsound;

import android.util.Log;
import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MusicBoxRenderer extends ARRenderer {
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

	private Marker[] markers = new Marker[markerParams.length];

	// 発音時に表示する共通テクスチャ
	// (Zファイティングを避けるために若干上にずらしてみている)
	private Plane playPlane = new Plane(64.0f * 1.3f, 1.0f);

	private Matrix4f projMatrix = new Matrix4f();

	public MusicBoxRenderer(Example activity) {
		this.activity = activity;

		for (int i = 0; i < markers.length; ++i) {
			Marker marker = new Marker();
			markers[i] = marker;
		}
	}

	@Override
	public boolean configureARScene() {
		for (int i = 0; i < markers.length; ++i) {
			boolean ret = markers[i].init(markerParams[i]);
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
			boolean ret = markers[i].loadTexture(gl, activity, markerTexturePaths[i]);
			if (!ret) {
				Log.d(TAG, "marker texture failed:" + markerTexturePaths[i]);
			}
		}

		// 発音テクスチャロード
		playPlane.loadGLTexture(gl, activity, "Texture/play.png");

		// Enable Texture Mapping
		gl.glEnable(GL10.GL_TEXTURE_2D);
	}

	@Override
	public void draw(GL10 gl) {
		long now = System.currentTimeMillis();

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		for (Marker marker : markers) {
			marker.checkPlaySound(now, activity);
		}

		gl.glMatrixMode(GL10.GL_PROJECTION);
		float[] projectionMatrixArray = ARToolKit.getInstance().getProjectionMatrix();
		gl.glLoadMatrixf(projectionMatrixArray, 0);
		projMatrix.set(projectionMatrixArray);

		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glFrontFace(GL10.GL_CW);

		gl.glMatrixMode(GL10.GL_MODELVIEW);

		for (Marker marker : markers) {
			// TODO: テスト中
			marker.checkViewportInside(projMatrix, 0.5f, 0.5f);

			marker.draw(gl, playPlane, now);
		}
	}
}
