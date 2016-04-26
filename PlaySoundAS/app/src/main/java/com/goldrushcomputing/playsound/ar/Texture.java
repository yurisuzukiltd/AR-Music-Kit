package com.goldrushcomputing.playsound.ar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.io.InputStream;

public class Texture {
	private static final String TAG = "Texture";

	private int[] textureId = new int[1];

	public boolean load(GL10 gl, Context context, String assetPath) {
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

	public void bind(GL10 gl) {
		// bind the previously generated texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId[0]);
	}
}
