package com.goldrushcomputing.playsound.ar;

import android.util.Log;
import com.goldrushcomputing.playsound.geom.Matrix4f;
import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Portraitでの回転問題対応.
 */
public class InstrumentsRenderer extends ARRenderer {
	private static final String TAG = "InstrumentsRenderer";

	private Matrix4f projectionMatrix = new Matrix4f();

	private Matrix4f workMat = new Matrix4f();
	private float[] workArray = new float[16];

	protected void setProjectionMatrix(GL10 gl) {
		gl.glMatrixMode(GL10.GL_PROJECTION);
		projectionMatrix.set(ARToolKit.getInstance().getProjectionMatrix());

		/*
		// Portraitでの回転に対応してProjectionMatrixの中身を変更
		float tmp = projectionMatrix.m11;
		projectionMatrix.m11 = projectionMatrix.m00;
		projectionMatrix.m00 = tmp;

		workMat.rotZ((float) Math.PI * -0.5f);
		projectionMatrix.mul(workMat);

		//flipMat(projectionMatrix);

		projectionMatrix.get(workArray);
		*/

		//gl.glLoadMatrixf(workArray, 0);
		gl.glLoadMatrixf(ARToolKit.getInstance().getProjectionMatrix(), 0);
	}

	/*
	// NG
	private void flipMat(Matrix4f mat) {
		// x軸を反転
		//mat.m00 = -mat.m00;
		//mat.m10 = -mat.m10;

		mat.m01 = -mat.m01;
		mat.m11 = -mat.m11;
	}
	*/

	Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
}
