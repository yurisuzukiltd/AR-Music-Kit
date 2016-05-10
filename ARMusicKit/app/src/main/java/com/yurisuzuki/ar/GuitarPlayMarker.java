/*
 *  Author(s): Kosuke Miyoshi, Narrative Nights
 */
package com.yurisuzuki.ar;

import com.yurisuzuki.CameraActivity;
import org.artoolkit.ar.base.ARToolKit;

import javax.microedition.khronos.opengles.GL10;

public class GuitarPlayMarker extends Marker {
	private long lastOutlintDrawnWithTracked = -1L;

	void checkPlaySound(long now, CameraActivity activity) {
		if (isTracked()) {
			// マーカーを認識していたら、lastTrackedTimeを更新
			lastTrackedTime = now;
		} else {
			// 現在認識しておらず、最後に認識してから1000msec以内だったら、発音する
			if (lastTrackedTime > 0 && (now - lastTrackedTime) < 1000) {
				lastTrackedTime = -1;
				lastPlayTime = now;
				activity.playCurrentSound();
			}
		}
	}

	/**
	 * アウトラインの表示
	 */
	void drawOutline(GL10 gl, Plane outlinePlane, long now, boolean front) {
		if (isTracked()) {
			float markerMatrix[] = ARToolKit.getInstance().queryMarkerTransformation(markerId);
			if (markerMatrix == null) {
				return;
			}
			// TODO:
			if( front ) {
				// 反転させる
				markerMatrix[1] = -markerMatrix[1];
				markerMatrix[5] = -markerMatrix[5];
				markerMatrix[9] = -markerMatrix[9];
				markerMatrix[13] = -markerMatrix[13];
			}

			gl.glLoadMatrixf(markerMatrix, 0);

			outlinePlane.draw(gl);
			lastOutlintDrawnWithTracked = now;
		} else if( markerMatrixCached && (now - lastOutlintDrawnWithTracked) < 1000 ) {
			// マーカー外れて一定時間以内ならキャッシュを使ってアウトラインを表示する
			gl.glLoadMatrixf(cachedMarkerMatrix, 0);
			outlinePlane.draw(gl);
		}
	}
}
