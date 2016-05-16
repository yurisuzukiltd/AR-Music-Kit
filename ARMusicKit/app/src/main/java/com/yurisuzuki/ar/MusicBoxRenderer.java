/*
 *  Author(s): Kosuke Miyoshi, Narrative Nights
 */
package com.yurisuzuki.ar;

import android.content.Context;
import android.opengl.GLU;
import android.util.Log;

import com.yurisuzuki.CameraActivity;
import com.yurisuzuki.geom.Matrix4f;
import org.artoolkit.ar.base.camera.CameraRotationInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MusicBoxRenderer extends InstrumentsRenderer {
	private static final String TAG = "PianoRenderer";
	private CameraActivity activity;

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

	private static final String[] markerTexturePaths = {
			"Texture/Do.png",
			"Texture/Re.png",
			"Texture/Mi.png",
			"Texture/Fa.png",
			"Texture/So.png",
			"Texture/La.png",
			"Texture/Si.png",
			"Texture/Do-.png",
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

	public MusicBoxRenderer(CameraActivity activity) {
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

		// 各マーカーテクスチャ、発音テクスチャロード
		for (int i = 0; i < markers.length; ++i) {
			boolean ret0 = markers[i].loadMarkerTexture(gl, activity, markerTexturePaths[i]);
			boolean ret1 = markers[i].loadActionTexture(gl, activity, actionTexturePaths[i]);
			if (!ret0) {
				Log.d(TAG, "marker onTexture failed:" + markerTexturePaths[i]);
			}
			if (!ret1) {
				Log.d(TAG, "action onTexture failed:" + actionTexturePaths[i]);
			}
		}

		// UI用オーバーレイテクスチャロード
		ui.loadGLTexture(gl, activity, "Texture/blue65.png", "Texture/white65.png");

		// Enable Texture Mapping
		gl.glEnable(GL10.GL_TEXTURE_2D);
	}

	private void draw2D(GL10 gl, boolean actionPlaneDrawn) {
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_CULL_FACE);

		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_BLEND);

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, -1.0f, 1.0f, -1.0f, 1.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		ui.draw(gl, actionPlaneDrawn);

		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_CULL_FACE);
	}

	@Override
	public void draw(GL10 gl) {
		long now = System.currentTimeMillis();

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		setProjectionMatrix(gl);

		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);

		gl.glMatrixMode(GL10.GL_MODELVIEW);

		Matrix4f projectionMatrix = getProjectionMatrix();

		CameraRotationInfo cameraRotationInfo = activity.getCameraRotationInfo();

		boolean actionPlaneDrawn = false;

		for (MusicBoxMarker marker : markers) {
			// ラインをまたいだかどうかをチェックして発音
			marker.checkPlaySoundOverLine(now, activity, projectionMatrix, cameraRotationInfo);
			actionPlaneDrawn |= marker.draw(gl, now, cameraRotationInfo);
		}

		draw2D(gl, actionPlaneDrawn);
	}

	// トラック範囲を表示するためのグレーオーバーレイ
	public class UI {
		private Texture onTexture;
		private Texture offTexture;
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
			float sizeY = 0.01f;

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

		public void draw(GL10 gl, boolean actionPlaneDrawn) {
			if( actionPlaneDrawn ) {
				// draw blue line when action plane was drawn
				onTexture.bind(gl);
			} else {
				// draw white line
				offTexture.bind(gl);
			}

			// Point to our buffers
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

			// Set the face rotation
			//gl.glFrontFace(GL10.GL_CW);

			// Point to our vertex buffer
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texcoordBuffer);

			// Draw the vertices as triangle strip
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);

			// Disable the client state before leaving
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}

		public boolean loadGLTexture(GL10 gl, Context context, String onAssetPath, String offAssetPath) {
			onTexture = new Texture();
			boolean ret0 = onTexture.load(gl, context, onAssetPath);

			offTexture = new Texture();
			boolean ret1 = offTexture.load(gl, context, offAssetPath);

			return ret0 && ret1;
		}

		// TODO: テクスチャメモリの解放
	}
}
