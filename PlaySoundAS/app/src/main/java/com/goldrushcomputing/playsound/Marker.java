package com.goldrushcomputing.playsound;

import android.content.Context;
import android.util.Log;
import org.artoolkit.ar.base.ARToolKit;

import javax.microedition.khronos.opengles.GL10;

public class Marker {
	private static final String TAG = "Marker";

	private int markerId;
	private int soundId;
	private long lastTrackedTime = -1L;
	private long lastPlayTime = -1L;
	private float[] cachedMarkerMatrix = null;
	private boolean markerMatrixCached;

	/// ストライプ画像Plane
	private Plane plane = new Plane(64.0f);

	private static final Matrix4f workMat = new Matrix4f();

	private Matrix4f markerMat;

	Marker() {
	}

	boolean init(String markerParam, int soundId) {
		markerId = ARToolKit.getInstance().addMarker(markerParam);
		this.soundId = soundId;
		return markerId >= 0;
	}

	/**
	 * マーカー認識時に表示するテクスチャ(ストライプ画像)をロード
	 * (MusicBoxの場合は呼ばれない)
	 */
	boolean loadTexture(GL10 gl, Context context, String textureAssetPath) {
		return plane.loadGLTexture(gl, context, textureAssetPath);
	}

	private boolean isTracked() {
		ARToolKit ar = ARToolKit.getInstance();
		return ar.queryMarkerVisible(markerId);
	}

	/**
	 * 指定した範囲内でトラックされているかどうか.
	 */
	private boolean isTrackedInsideRange(Matrix4f projMat, float rangeX, float rangeY) {
		if( !isTracked() ) {
			return false;
		}

		float markerMatrix[] = ARToolKit.getInstance().queryMarkerTransformation(markerId);
		if (markerMatrix == null) {
			return false;
		}

		if (markerMat == null) {
			markerMat = new Matrix4f();
		}

		markerMat.set(markerMatrix);

		// プロジェクション行列とマーカーのModelView行列をかけて、3D座標からViewPort座標へ変換する行列を作成する
		workMat.set(projMat);
		workMat.mul(markerMat);

		return plane.checkViewportInside(workMat, rangeX, rangeY);
	}

	void checkPlaySound(long now, Example activity) {
		if (isTracked()) {
			// マーカーを認識していたら、lastTrackedTimeを更新
			lastTrackedTime = now;
		} else {
			// 現在認識しておらず、最後に認識してから1000msec以内だったら、発音する
			if (lastTrackedTime > 0 && (now - lastTrackedTime) < 1000) {
				Log.d(TAG, "marker hidden detected");
				lastTrackedTime = -1;
				lastPlayTime = now;
				activity.playSound(soundId);
			}
		}
	}

	void checkPlaySoundWithRange(long now, Example activity, Matrix4f projMat, float rangeX, float rangeY) {
		if( isTrackedInsideRange(projMat, rangeX, rangeY ) ) {
			// 指定範囲内でマーカーを認識していたら、lastTrackedTimeを更新
			lastTrackedTime = now;
		} else {
			// 現在認識しておらず、最後に認識してから1000msec以内だったら、発音する
			if (lastTrackedTime > 0 && (now - lastTrackedTime) < 1000) {
				Log.d(TAG, "marker hidden detected");
				lastTrackedTime = -1;
				lastPlayTime = now;
				activity.playSound(soundId);
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

		// マーカーマトリクスをキャッシュしておく
		cacheMarkerMatrix(markerMatrix);

		// トラックマークを表示する
		if( plane.hasTexture() ) {
			// MusicBoxの場合はPlanceにテクスチャが無いので表示しない
			gl.glLoadMatrixf(markerMatrix, 0);
			plane.draw(gl);
		}
	}
}
