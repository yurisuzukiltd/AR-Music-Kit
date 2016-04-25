package com.goldrushcomputing.playsound;

import android.content.Context;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Plane {
	private static final String TAG = "Plane";

	private Texture texture;
	private FloatBuffer vertexBuffer;

	private static final float vertices[] = {
			-0.5f, -0.5f, 0.0f,
			-0.5f, 0.5f, 0.0f,
			0.5f, -0.5f, 0.0f,
			0.5f, 0.5f, 0.0f
	};

	private float scaledVertices[];

	private FloatBuffer texcoordBuffer;
	private static final float texcoord[] = {
			0.0f, 1.0f, // top left		(V2)
			0.0f, 0.0f, // bottom left	(V1)
			1.0f, 1.0f, // top right	(V4)
			1.0f, 0.0f  // bottom right	(V3)
	};

	private Vector4f workVec0 = new Vector4f();
	private Vector4f workVec1 = new Vector4f();

	public Plane(float size) {
		this(size, 0.0f);
	}

	public boolean hasTexture() {
		return texture != null;
	}

	public Plane(float size, float offsetZ) {
		scaledVertices = new float[vertices.length];
		for (int i = 0; i < vertices.length; ++i) {
			scaledVertices[i] = size * vertices[i];
			if (i % 3 == 2) {
				// z座標にオフセットを加える
				scaledVertices[i] += offsetZ;
			}
		}

		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(scaledVertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuffer.asFloatBuffer();
		vertexBuffer.put(scaledVertices);
		vertexBuffer.position(0);

		byteBuffer = ByteBuffer.allocateDirect(texcoord.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		texcoordBuffer = byteBuffer.asFloatBuffer();
		texcoordBuffer.put(texcoord);
		texcoordBuffer.position(0);
	}

	public void draw(GL10 gl) {
		if (!hasTexture()) {
			return;
		}

		// bind the previously generated texture
		texture.bind(gl);

		// Point to our buffers
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// Set the face rotation
		gl.glFrontFace(GL10.GL_CW);

		// Point to our vertex buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texcoordBuffer);

		// Draw the vertices as triangle strip
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);

		// Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	public boolean loadGLTexture(GL10 gl, Context context, String assetPath) {
		texture = new Texture();
		return texture.load(gl, context, assetPath);
	}

	/**
	 * ViewPort座標内での各頂点の位置(-1.0~1.0)を算出し、それが指定の範囲内かどうかを調べる.
	 */
	public boolean checkViewportInside(Matrix4f mat, float rangeX, float rangeY) {
		for (int i = 0; i < 4; ++i) {
			float vx = scaledVertices[3 * i];
			float vy = scaledVertices[3 * i + 1];
			float vz = scaledVertices[3 * i + 2];
			workVec0.set(vx, vy, vz, 1.0f);

			mat.transform(workVec0, workVec1);

			float sx = workVec1.x / workVec1.w;
			float sy = workVec1.y / workVec1.w;
			if (sx < -rangeX || sx > rangeX || sy < -rangeY || sy > rangeY) {
				Log.d(TAG, "vertex outside of region");
				// どれかの頂点が範囲外に出ていた
				return false;
			}
		}

		Log.d(TAG, "all vertex inside region");
		return true;
	}

	// TODO: テクスチャメモリの解放
}
