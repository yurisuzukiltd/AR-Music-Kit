/*
 *  Author(s): Kosuke Miyoshi, Narrative Nights
 */
package com.yurisuzuki.ar;

import com.yurisuzuki.CameraActivity;
import com.yurisuzuki.geom.Matrix4f;
import com.yurisuzuki.geom.Vector4f;
import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.camera.CameraRotationInfo;

public class MusicBoxMarker extends Marker {
	private static final String TAG = "MusicBoxMarker";
	// play sound only if relative position is within this value
	private static final float TRACKING_POS_THREDHOLD = 0.15f;

	private Matrix4f markerMat;

	private Matrix4f workMat = new Matrix4f();
	private Vector4f workVec0 = new Vector4f();
	private Vector4f workVec1 = new Vector4f();

	private boolean lastTracked = false;
	private float lastTrackingSideValue = 0.0f;
	private float trackingSideValue = 0.0f;

	MusicBoxMarker() {
		suppressMarkerPlaneWhenActionShown = true;
	}

	/**
	 * 上下のどちらにあるかどうかをチェック.
	 */
	private boolean updateTrackingSideValue(Matrix4f projMat, CameraRotationInfo cameraRotationInfo) {
		if (!isTracked()) {
			return false;
		}

		float markerMatrix[] = ARToolKit.getInstance().queryMarkerTransformation(markerId);
		if (markerMatrix == null) {
			return false;
		}

		if (markerMat == null) {
			markerMat = new Matrix4f();
		}

		adjustMarkerMatrix(markerMatrix, adjustedMarkerMatrix, cameraRotationInfo);

		markerMat.set(adjustedMarkerMatrix);

		// プロジェクション行列とマーカーのModelView行列をかけて、3D座標からViewPort座標へ変換する行列を作成する
		workMat.set(projMat);
		workMat.mul(markerMat);

		workVec0.set(0.0f, 0.0f, 0.0f, 1.0f);
		workMat.transform(workVec0, workVec1);

		// get Y axis value in ViewPort coordinate.

		trackingSideValue = workVec1.y / workVec1.w;
		// if trackingSideValue < 0.0f, then marker is located left side when portrait.
		// otherwise right side

		return true;
	}

	void checkPlaySoundOverLine(long now, CameraActivity activity, Matrix4f projMat, CameraRotationInfo cameraRotationInfo) {
		boolean tracked = updateTrackingSideValue(projMat, cameraRotationInfo);
		if (tracked && lastTracked && lastTrackingSideValue * trackingSideValue < 0.0f &&
				 Math.abs(trackingSideValue) < TRACKING_POS_THREDHOLD) {
			if (now - lastPlayTime > 100) {
				activity.playSound(soundId);
				lastPlayTime = now;
			}
		}
		lastTracked = tracked;
		lastTrackingSideValue = trackingSideValue;
	}
}
