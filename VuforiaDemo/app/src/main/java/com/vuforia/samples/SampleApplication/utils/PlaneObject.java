package com.vuforia.samples.SampleApplication.utils;

import java.nio.Buffer;

/**
 * Created by miyoshi on 2016/04/22.
 */
public class PlaneObject extends MeshObject {

	/*
	private static final double planeVertices[] = {
			-1.00f, 1.00f, 0.0f,
			 1.00f, 1.00f, 0.0f,
			 1.00f, -1.00f, 0.0f,
			-1.00f, -1.00f, 0.0f,
	};
	*/

	private static final double planeVertices[] = {
			-1.00f, -1.00f, 0.0f,
			 1.00f, -1.00f, 0.0f,
			 1.00f, 1.00f, 0.0f,
			-1.00f, 1.00f, 0.0f,
	};

	private static final double planeTexcoords[] = {
			0, 0, 1, 0, 1, 1, 0, 1,
	};


	private static final double planeNormals[] = {
			0, 0, 1,
			0, 0, 1,
			0, 0, 1,
			0, 0, 1,
	};

	private static final short planeIndices[] = {
			0, 1, 2, 0, 2, 3,
			//0, 2, 1, 0, 3, 2,
	};

	private Buffer mVertBuff;
	private Buffer mTexCoordBuff;
	private Buffer mNormBuff;
	private Buffer mIndBuff;


	public PlaneObject() {
		mVertBuff = fillBuffer(planeVertices);
		mTexCoordBuff = fillBuffer(planeTexcoords);
		mNormBuff = fillBuffer(planeNormals);
		mIndBuff = fillBuffer(planeIndices);
	}


	@Override
	public Buffer getBuffer(MeshObject.BUFFER_TYPE bufferType) {
		Buffer result = null;
		switch (bufferType) {
			case BUFFER_TYPE_VERTEX:
				result = mVertBuff;
				break;
			case BUFFER_TYPE_TEXTURE_COORD:
				result = mTexCoordBuff;
				break;
			case BUFFER_TYPE_INDICES:
				result = mIndBuff;
				break;
			case BUFFER_TYPE_NORMALS:
				result = mNormBuff;
			default:
				break;
		}
		return result;
	}


	@Override
	public int getNumObjectVertex() {
		return planeVertices.length / 3;
	}


	@Override
	public int getNumObjectIndex() {
		return planeIndices.length;
	}
}
