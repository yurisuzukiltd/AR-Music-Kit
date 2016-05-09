package com.goldrushcomputing.playsound.ar;

import android.content.Context;
import android.opengl.GLU;
import android.util.Log;
import com.goldrushcomputing.playsound.Example;
import com.goldrushcomputing.playsound.geom.Matrix4f;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class MusicBoxRenderer extends InstrumentsRenderer {
	private static final String TAG = "PianoRenderer";
	private Example activity;

	private static final String[] markerParams = {
			"single;Data/Do.pat;64",
			"single;Data/Re.pat;64",
			"single;Data/Mi.pat;64",
			"single;Data/Fa.pat;64",
			"single;Data/So.pat;64",
			"single;Data/La.pat;64",
			"single;Data/Ti.pat;64",
			"single;Data/Do-.pat;64",
	};

	private static final String[] actionTexturePaths = {
			"Texture/Action_purple.png",
			"Texture/Action_blue.png",
			"Texture/Action_green.png",
			"Texture/Action_yellow.png",
			"Texture/Action_orange.png",
			"Texture/Action_red.png",
			"Texture/Action_brown.png",
			"Texture/Action_black.png",
	};

	private MusicBoxMarker[] markers = new MusicBoxMarker[markerParams.length];

	private UI ui = new UI();

	public MusicBoxRenderer(Example activity) {
		this.activity = activity;

		for (int i = 0; i < markers.length; ++i) {
			MusicBoxMarker marker = new MusicBoxMarker();
			markers[i] = marker;
		}
	}

	@Override
	public boolean configureARScene() {
		for (int i = 0; i < markers.length; ++i) {
			boolean ret = markers[i].init(markerParams[i], i);
			if (!ret) {
				Log.d(TAG, "marker load failed:" + markerParams[i]);
				return false;
			}
		}
		return true;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);

		// 発音テクスチャロード

		// 各マーカーテクスチャロード
		for (int i = 0; i < markers.length; ++i) {
			boolean ret = markers[i].loadActionTexture(gl, activity, actionTexturePaths[i]);
			if (!ret) {
				Log.d(TAG, "action texture failed:" + actionTexturePaths[i]);
			}
		}

		// UI用オーバーレイテクスチャロード
		ui.loadGLTexture(gl, activity, "Texture/gray20.png");

		// Enable Texture Mapping
		gl.glEnable(GL10.GL_TEXTURE_2D);
	}

	private void draw2D(GL10 gl) {
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_CULL_FACE);

		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_BLEND);

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, -1.0f, 1.0f, -1.0f, 1.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		ui.draw(gl);

		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_CULL_FACE);
	}

	@Override
	public void draw(GL10 gl) {
		long now = System.currentTimeMillis();

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		setProjectionMatrix(gl);

		//gl.glEnable(GL10.GL_CULL_FACE);
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		//gl.glFrontFace(GL10.GL_CW);

		gl.glMatrixMode(GL10.GL_MODELVIEW);

		Matrix4f projectionMatrix = getProjectionMatrix();

		boolean usingFrontCamera = activity.isUsingFrontCamera();

		for (MusicBoxMarker marker : markers) {
			// ラインをまたいだかどうかをチェックして発音
			marker.checkPlaySoundOverLine(now, activity, projectionMatrix);
			marker.draw(gl, now, usingFrontCamera);
		}

		draw2D(gl);
	}

	// トラック範囲を表示するためのグレーオーバーレイ
	public class UI {
		private Texture texture;
		private FloatBuffer vertexBuffer;

		private final float vertices[] = {
				-1.0f, -1.0f, 0.0f,
				-1.0f, 1.0f, 0.0f,
				1.0f, -1.0f, 0.0f,
				1.0f, 1.0f, 0.0f
		};

		private float scaledVertices[];

		private FloatBuffer texcoordBuffer;
		private final float texcoord[] = {
				0.0f, 1.0f, // top left		(V2)
				0.0f, 0.0f, // bottom left	(V1)
				1.0f, 1.0f, // top right	(V4)
				1.0f, 0.0f  // bottom right	(V3)
		};

		public UI() {
			// 左右にラインを引く
			float sizeX = 1.0f;
			float sizeY = 0.005f;

			scaledVertices = new float[vertices.length];
			for (int i = 0; i < vertices.length; ++i) {
				scaledVertices[i] = vertices[i];
				if( i % 3 == 0 ) {
					scaledVertices[i] *= sizeX;
				} else if(i % 3 == 1) {
					scaledVertices[i] *= sizeY;
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

		// TODO: テクスチャメモリの解放
	}
}
