/*
 *  Author(s): Kosuke Miyoshi, Narrative Nights
 */
package com.yurisuzuki.ar;

import android.content.Context;
import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.camera.CameraRotationInfo;

import javax.microedition.khronos.opengles.GL10;

public class Marker {
	protected int markerId;
	protected int soundId;
	protected long lastTrackedTime = -1L;
	protected long lastPlayTime = -1L;

	protected float[] cachedMarkerMatrix = null;
	protected boolean markerMatrixCached;

	/// trueならactionがなっている間、planeは表示しない. (musicでのみ利用)
	protected boolean suppressMarkerPlaneWhenActionShown = false;

	/// ストライプ画像Plane
	protected Plane markerPlane = new Plane(64.0f);

	/// Action画像Plane (Zファイティングを避けるために若干上にずらしてみている)
	protected Plane actionPlane = new Plane(64.0f * 1.3f, 1.0f);

	/// 画面の向き関連でmarker matrixを調整するためのバッファ
	protected float adjustedMarkerMatrix[] = new float[16];

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

	protected void adjustMarkerMatrix(float[] matrix, float[] targetMatrix, CameraRotationInfo cameraRotationInfo) {
		System.arraycopy(matrix, 0, targetMatrix, 0, 16);

		if (cameraRotationInfo.rotation == 180) {
			targetMatrix[0] = -targetMatrix[0];
			targetMatrix[4] = -targetMatrix[4];
			targetMatrix[8] = -targetMatrix[8];
			targetMatrix[12] = -targetMatrix[12];

			if (!cameraRotationInfo.mirror) {
				targetMatrix[1] = -targetMatrix[1];
				targetMatrix[5] = -targetMatrix[5];
				targetMatrix[9] = -targetMatrix[9];
				targetMatrix[13] = -targetMatrix[13];
			}
		} else {
			// Nexus6pでこの場合になる (フロント、リア共に)

			if (cameraRotationInfo.mirror) {
				// フロントカメラでミラーが必要な場合
				targetMatrix[1] = -targetMatrix[1];
				targetMatrix[5] = -targetMatrix[5];
				targetMatrix[9] = -targetMatrix[9];
				targetMatrix[13] = -targetMatrix[13];
			}
		}
	}

	void draw(GL10 gl, long now, CameraRotationInfo cameraRotationInfo) {
		boolean actionPlaneDrawn = false;

		if (lastPlayTime > 0) {
			if (now - lastPlayTime < 200 & markerMatrixCached) {
				// 発音テクスチャを表示する
				if (actionPlane.hasTexture()) {
					gl.glLoadMatrixf(cachedMarkerMatrix, 0);
					actionPlane.draw(gl);

					actionPlaneDrawn = true;
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

		adjustMarkerMatrix(markerMatrix, adjustedMarkerMatrix, cameraRotationInfo);

		// マーカーマトリクスをキャッシュしておく
		cacheMarkerMatrix(adjustedMarkerMatrix);

		// トラックマークを表示する
		if (markerPlane.hasTexture()) {
			if( !actionPlaneDrawn || !suppressMarkerPlaneWhenActionShown ) {
				// MusicBoxの場合はPlaneにテクスチャが無いので表示しない
				gl.glLoadMatrixf(adjustedMarkerMatrix, 0);
				markerPlane.draw(gl);
			}
		}
	}
}
