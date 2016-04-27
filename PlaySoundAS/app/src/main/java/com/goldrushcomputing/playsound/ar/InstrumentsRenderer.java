package com.goldrushcomputing.playsound.ar;

import com.goldrushcomputing.playsound.geom.Matrix4f;
import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Portraitでの回転問題対応.
 */
public class InstrumentsRenderer extends ARRenderer {

	private Matrix4f projectionMatrix = new Matrix4f();

	private Matrix4f workMat = new Matrix4f();
	private float[] workArray = new float[16];

	protected void setProjectionMatrix(GL10 gl) {
		gl.glMatrixMode(GL10.GL_PROJECTION);

		// Portraitでの回転に対応してProjectionMatrixの中身を変更
		projectionMatrix.set(ARToolKit.getInstance().getProjectionMatrix());
		float tmp = projectionMatrix.m11;
		projectionMatrix.m11 = projectionMatrix.m00;
		projectionMatrix.m00 = tmp;

		workMat.rotZ((float) Math.PI * -0.5f);
		projectionMatrix.mul(workMat);

		projectionMatrix.get(workArray);
		gl.glLoadMatrixf(workArray, 0);
	}

	Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
}
