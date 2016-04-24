package com.goldrushcomputing.playsound;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Plane {
	private static final String TAG = "Plane";

	private int[] textureId = new int[1];
	private FloatBuffer vertexBuffer;

	private float vertices[] = {
			-0.5f, -0.5f, 0.0f,
			-0.5f, 0.5f, 0.0f,
			0.5f, -0.5f, 0.0f,
			0.5f, 0.5f, 0.0f
	};

	private float scaledVertices[];

	private FloatBuffer texcoordBuffer;
	private float texcoord[] = {
			0.0f, 1.0f,        // top left		(V2)
			0.0f, 0.0f,        // bottom left	(V1)
			1.0f, 1.0f,        // top right	(V4)
			1.0f, 0.0f        // bottom right	(V3)
	};

	public Plane(float size) {
		this(size, 0.0f);
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
		// bind the previously generated texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId[0]);

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
		// loading texture
		Bitmap bitmap;

		try {
			InputStream is = context.getResources().getAssets().open(assetPath);
			bitmap = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			Log.d(TAG, "texture load failed:" + assetPath);
			return false;
		}

		if (bitmap == null) {
			Log.d(TAG, "texture load failed:" + assetPath);
			return false;
		}

		// generate one texture pointer
		gl.glGenTextures(1, textureId, 0);
		// and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId[0]);

		// create nearest filtered texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		// Use Android GLUtils to specify a two-dimensional texture image from our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		// Clean up
		bitmap.recycle();

		return true;
	}

	private Vector4f workVec0 = new Vector4f();
	private Vector4f workVec1 = new Vector4f();

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
			if( sx < -rangeX || sx > rangeX || sy < -rangeY || sy > rangeY ) {
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
