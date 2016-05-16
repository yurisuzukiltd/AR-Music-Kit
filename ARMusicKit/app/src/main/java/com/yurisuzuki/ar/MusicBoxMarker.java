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

	private Matrix4f markerMat;

	private Matrix4f workMat = new Matrix4f();
	private Vector4f workVec0 = new Vector4f();
	private Vector4f workVec1 = new Vector4f();

	/// 最後にトラックされた位置が画面の上半分か下半分か
	private int lastTrackSide = 0;

	MusicBoxMarker() {
		suppressMarkerPlaneWhenActionShown = true;
	}

	/**
	 * 上下のどちらにあるかどうかをチェック.
	 */
	private int calcTrackingSide(Matrix4f projMat, CameraRotationInfo cameraRotationInfo) {
		if (!isTracked()) {
			return 0;
		}

		float markerMatrix[] = ARToolKit.getInstance().queryMarkerTransformation(markerId);
		if (markerMatrix == null) {
			return 0;
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

		// ViewPort座標系でのY座標値を得る
		float sy = workVec1.y / workVec1.w;

		if (sy < 0.0f) {
			// portraitでみて画面の左半分
			return -1;
		} else {
			// portraitでみて画面の右半分
			return 1;
		}
	}

	void checkPlaySoundOverLine(long now, CameraActivity activity, Matrix4f projMat, CameraRotationInfo cameraRotationInfo) {
		int side = calcTrackingSide(projMat, cameraRotationInfo);
		if (side != 0 && lastTrackSide != 0 && side != lastTrackSide) {
			if (now - lastPlayTime > 100) {
				activity.playSound(soundId);
				lastPlayTime = now;
			}
		}
		lastTrackSide = side;
	}
}
