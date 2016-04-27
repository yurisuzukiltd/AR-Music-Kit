package com.goldrushcomputing.playsound.ar;

import android.content.Context;
import org.artoolkit.ar.base.ARToolKit;

import javax.microedition.khronos.opengles.GL10;

public class Marker {
	protected int markerId;
	protected int soundId;
	protected long lastTrackedTime = -1L;
	protected long lastPlayTime = -1L;

	protected float[] cachedMarkerMatrix = null;
	protected boolean markerMatrixCached;

	/// ストライプ画像Plane
	protected Plane markerPlane = new Plane(64.0f);

	/// Action画像Plane (Zファイティングを避けるために若干上にずらしてみている)
	protected Plane actionPlane = new Plane(64.0f * 1.3f, 1.0f);

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

	protected boolean isTracked() {
		ARToolKit ar = ARToolKit.getInstance();
		return ar.queryMarkerVisible(markerId);
	}

	protected void cacheMarkerMatrix(float markerMatrix[]) {
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
				if( actionPlane.hasTexture() ) {
					gl.glLoadMatrixf(cachedMarkerMatrix, 0);
					actionPlane.draw(gl);
				}
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
