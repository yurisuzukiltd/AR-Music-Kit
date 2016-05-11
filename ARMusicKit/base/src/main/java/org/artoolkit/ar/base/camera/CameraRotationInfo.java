package org.artoolkit.ar.base.camera;

public class CameraRotationInfo {
	// 基準からの回転角度
	public int rotation;

	// 左右反転必要かどうか
	public boolean mirror;

	/**
	 * @param rotation setCameraDisplayOrientation()のboilarplateで、最後に算出されたresult角度.
	 * @param front フロントカメラかどうか
	 */
	public CameraRotationInfo(int rotation, boolean front) {
		if( front ) {
			// フロントカメラは180度がベース
			this.rotation = (rotation + 180) % 360;
			// 鏡面反転する
			this.mirror = true;
		} else {
			// リアカメラは0度がベース
			this.rotation = rotation;
			// 鏡面反転しない
			this.mirror = false;
		}
	}
}
