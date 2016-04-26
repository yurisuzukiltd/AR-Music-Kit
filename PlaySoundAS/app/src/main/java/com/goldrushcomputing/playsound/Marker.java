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
	private Plane markerPlane = new Plane(64.0f);

	/// Action画像Plane (Zファイティングを避けるために若干上にずらしてみている)
	private Plane actionPlane = new Plane(64.0f * 1.3f, 1.0f);

	private static final Matrix4f workMat = new Matrix4f();
	private Vector4f workVec0 = new Vector4f();
	private Vector4f workVec1 = new Vector4f();

	private Matrix4f markerMat;

	/// 最後にトラックされた位置が画面の上半分か下半分か
	private int lastTrackSide = 0;

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
	boolean loadMarkerTexture(GL10 gl, Context context, String textureAssetPath) {
		return markerPlane.loadGLTexture(gl, context, textureAssetPath);
	}

	/**
	 * 発音時に表示するテクスチャ(パッ画像)をロード
	 */
	boolean loadActionTexture(GL10 gl, Context context, String textureAssetPath) {
		return actionPlane.loadGLTexture(gl, context, textureAssetPath);
	}

	private boolean isTracked() {
		ARToolKit ar = ARToolKit.getInstance();
		return ar.queryMarkerVisible(markerId);
	}

	/**
	 *
	 */
	private int calcTrackingSide(Matrix4f projMat) {
		if( !isTracked() ) {
			return 0;
		}

		float markerMatrix[] = ARToolKit.getInstance().queryMarkerTransformation(markerId);
		if (markerMatrix == null) {
			return 0;
		}

		if (markerMat == null) {
			markerMat = new Matrix4f();
		}

		markerMat.set(markerMatrix);

		// プロジェクション行列とマーカーのModelView行列をかけて、3D座標からViewPort座標へ変換する行列を作成する
		workMat.set(projMat);
		workMat.mul(markerMat);

		workVec0.set(0.0f, 0.0f, 0.0f, 1.0f);
		workMat.transform(workVec0, workVec1);

		// ViewPort座標系でのX座標値を得る
		// 縦横が反転しているので、縦方向がX軸
		float sx = workVec1.x / workVec1.w;

		if ( sx < 0.0f ) {
			// 画面の下半分
			return -1;
		} else {
			// 画面の上半分
			return 1;
		}
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

	void checkPlaySoundOverLine(long now, Example activity, Matrix4f projMat) {
		int side = calcTrackingSide(projMat);
		if( side != 0 && lastTrackSide != 0 && side != lastTrackSide ) {
			if( now - lastPlayTime > 100 ) {
				activity.playSound(soundId);
				lastPlayTime = now;
			}
		}
		lastTrackSide = side;
	}

	private void cacheMarkerMatrix(float markerMatrix[]) {
		if (cachedMarkerMatrix == null || cachedMarkerMatrix.length != markerMatrix.length) {
			cachedMarkerMatrix = new float[markerMatrix.length];
		}
		System.arraycopy(markerMatrix, 0, cachedMarkerMatrix, 0, markerMatrix.length);
		markerMatrixCached = true;
	}

	void draw(GL10 gl, long now) {
		if (lastPlayTime > 0) {
			if (now - lastPlayTime < 200 & markerMatrixCached) {
				// 発音テクスチャを表示する
				gl.glLoadMatrixf(cachedMarkerMatrix, 0);
				actionPlane.draw(gl);
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
		if( markerPlane.hasTexture() ) {
			// MusicBoxの場合はPlanceにテクスチャが無いので表示しない
			gl.glLoadMatrixf(markerMatrix, 0);
			markerPlane.draw(gl);
		}
	}
}
