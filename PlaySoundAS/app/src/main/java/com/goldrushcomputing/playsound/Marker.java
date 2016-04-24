package com.goldrushcomputing.playsound;

import android.content.Context;
import android.util.Log;
import org.artoolkit.ar.base.ARToolKit;

import javax.microedition.khronos.opengles.GL10;

public class Marker {
	private static final String TAG = "Marker";

	private int markerId;
	private long lastTrackedTime = -1L;
	private long lastPlayTime = -1L;
	private float[] cachedMarkerMatrix = null;
	private boolean markerMatrixCached;

	private Plane plane = new Plane(64.0f);

	private Matrix4f markerMat;

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

	// TODO: テンポラリ staticにする？
	private Matrix4f outMat = new Matrix4f();

	boolean checkViewportInside(Matrix4f projMat, float rangeX, float rangeY) {
		float markerMatrix[] = ARToolKit.getInstance().queryMarkerTransformation(markerId);
		if (markerMatrix == null) {
			return false;
		}

		if( markerMat == null ) {
			markerMat = new Matrix4f();
		}

		markerMat.set(markerMatrix);
		outMat.set(projMat);
		outMat.mul(markerMat);

		return plane.checkViewportInside(outMat, rangeX, rangeY);
	}
}
