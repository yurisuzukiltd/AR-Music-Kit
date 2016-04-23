package com.goldrushcomputing.playsound;

import android.content.Context;
import android.util.Log;
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

	private Marker[] markers = new Marker[markerParams.length];

	// 発音時に表示するテクスチャ
	// (Zファイティングを避けるために若干上にずらしてみている)
	private Plane playPlane = new Plane(64.0f * 1.3f, 1.0f);

	public PianoRenderer(Example activity) {
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
		gl.glLoadMatrixf(ARToolKit.getInstance().getProjectionMatrix(), 0);
		//gl.glRotatef(180, 0.0f, 0.0f, 1.0f);

		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glFrontFace(GL10.GL_CW);

		gl.glMatrixMode(GL10.GL_MODELVIEW);

		for (Marker marker : markers) {
			marker.draw(gl, playPlane, now);
		}
	}

	private static class Marker {
		private int markerId;
		private long lastTrackedTime = -1L;
		private long lastPlayTime = -1L;

		private Plane plane = new Plane(64.0f);

		Marker() {
		}

		boolean init(String markerParam) {
			markerId = ARToolKit.getInstance().addMarker(markerParam);
			return markerId >= 0;
		}

		boolean loadTexture(GL10 gl, Context context, String textureAssetPath) {
			return plane.loadGLTexture(gl, context, textureAssetPath);
		}

		private boolean isTracked() {
			ARToolKit ar = ARToolKit.getInstance();
			return ar.queryMarkerVisible(markerId);
		}

		void checkPlaySound(long now, Example activity) {
			if (isTracked()) {
				lastTrackedTime = now;
			} else {
				if (lastTrackedTime > 0 && (now - lastTrackedTime) < 1000) {
					Log.d(TAG, "marker hidden detected");
					lastTrackedTime = -1;
					lastPlayTime = now;
					// TODO: 鳴らす音の指定
					activity.playSound1(null);
				}
			}
		}

		private float[] cachedMarkerMatrix = null;
		private boolean markerMatrixCached;

		private void cacheMarkerMatrix(float markerMatrix[]) {
			if (cachedMarkerMatrix == null || cachedMarkerMatrix.length != markerMatrix.length) {
				cachedMarkerMatrix = new float[markerMatrix.length];
			}
			System.arraycopy(markerMatrix, 0, cachedMarkerMatrix, 0, markerMatrix.length);
			markerMatrixCached = true;
		}

		void draw(GL10 gl, Plane playPlane, long now) {
			if (lastPlayTime > 0) {
				if (now - lastPlayTime < 200 & markerMatrixCached) {
					// 発音テクスチャを表示する
					gl.glLoadMatrixf(cachedMarkerMatrix, 0);
					playPlane.draw(gl);
				} else {
					lastPlayTime = -1L;
				}
			}

			if (!isTracked()) {
				return;
			}

			float markerMatrix[] = ARToolKit.getInstance().queryMarkerTransformation(markerId);
			if (markerMatrix == null) {
				return;
			}

			gl.glLoadMatrixf(markerMatrix, 0);
			plane.draw(gl);
			cacheMarkerMatrix(markerMatrix);
		}
	}
}
