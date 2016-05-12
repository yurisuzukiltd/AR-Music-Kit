/*
 *  Author(s): Kosuke Miyoshi, Narrative Nights
 */
package com.yurisuzuki.ar;

import android.util.Log;
import com.yurisuzuki.CameraActivity;
import org.artoolkit.ar.base.camera.CameraRotationInfo;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GuitarRenderer extends InstrumentsRenderer {
	private static final String TAG = "GuitarRenderer";
	private CameraActivity activity;

	// マーカーデータ
	private static final String[] markerParams = {
			"single;Data/C.pat;64",
			"single;Data/A.pat;64",
			"single;Data/G.pat;64",
			"single;Data/E.pat;64",
			"single;Data/D.pat;64",
			"single;Data/Am.pat;64",
			"single;Data/Em.pat;64",
			"single;Data/F.pat;64",
	};

	private static final String playMarkerParam = "single;Data/guitar.pat;64";

	// 認識時のテクスチャ
	private static final String[] markerTexturePaths = {
			"Texture/Code_C.png",
			"Texture/Code_A.png",
			"Texture/Code_G.png",
			"Texture/Code_E.png",
			"Texture/Code_D.png",
			"Texture/Code_Am.png",
			"Texture/Code_Em.png",
			"Texture/Code_F.png",
	};

	private static final String playMarkerTexturePath = "Texture/Guitar_Acoustic.png";

	// コードホールド時用テクスチャ
	private static final String[] holdTexturePaths = {
			"Texture/Action_purple.png",
			"Texture/Action_blue.png",
			"Texture/Action_green.png",
			"Texture/Action_yellow.png",
			"Texture/Action_orange.png",
			"Texture/Action_brown.png",
			"Texture/Action_lightbrown.png", // Emのライトブラウンのものと新規に作って追加した
			"Texture/Action_black.png",
	};

	// ギターの発音時のテクスチャ
	private static final String actionTexturePath = "Texture/Action_red.png";

	// ギターのアウトラインのテクスチャ
	private static final String acousticOutlineTexturePath = "Texture/Outline_AcousticGuitar.png";
	private static final String electricOutlineTexturePath = "Texture/Outline_ElectricGuitar.png";

	private GuitarCodeMarker[] codeMarkers;
	private GuitarPlayMarker playMarker = new GuitarPlayMarker();


	private static final float GUITAR_WIDTH = 768.34f;

	private Plane acousticOutlinePlane = new Plane(GUITAR_WIDTH, GUITAR_WIDTH * 0.5f, GUITAR_WIDTH * 0.5f, 150.0f, 0.0f, 0.0f);
	private Plane electricOutlinePlane = new Plane(GUITAR_WIDTH, GUITAR_WIDTH * 0.5f, GUITAR_WIDTH * 0.5f, 150.0f, 0.0f, 0.0f);

	public GuitarRenderer(CameraActivity activity) {
		this.activity = activity;

		codeMarkers = new GuitarCodeMarker[markerParams.length];

		for (int i = 0; i < codeMarkers.length; ++i) {
			codeMarkers[i] = new GuitarCodeMarker();
		}
	}

	@Override
	public boolean configureARScene() {
		for (int i = 0; i < codeMarkers.length; ++i) {
			String markerParam = markerParams[i];
			boolean ret = codeMarkers[i].init(markerParam, i);
			if (!ret) {
				Log.d(TAG, "marker load failed:" + markerParam);
				return false;
			}
		}

		playMarker.init(playMarkerParam, -1);
		return true;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);

		// 各マーカーテクスチャロード
		for (int i = 0; i < codeMarkers.length; ++i) {
			String texturePath = markerTexturePaths[i];
			boolean ret0 = codeMarkers[i].loadMarkerTexture(gl, activity, texturePath);
			if (!ret0) {
				Log.d(TAG, "marker texture failed:" + texturePath);
			}

			// コードホールド時用のテクスチャ
			boolean ret1 = codeMarkers[i].loadActionTexture(gl, activity, holdTexturePaths[i]);
			if (!ret1) {
				Log.d(TAG, "marker texture failed:" + holdTexturePaths[i]);
			}
		}

		// プレイマーカーの発音テクスチャ
		playMarker.loadMarkerTexture(gl, activity, playMarkerTexturePath);
		playMarker.loadActionTexture(gl, activity, actionTexturePath);

		// ギターアウトラインテクスチャ
		acousticOutlinePlane.loadGLTexture(gl, activity, acousticOutlineTexturePath);
		electricOutlinePlane.loadGLTexture(gl, activity, electricOutlineTexturePath);

		// Enable Texture Mapping
		gl.glEnable(GL10.GL_TEXTURE_2D);

		// 発音テクスチャが重なって表示された時の抜き表示
		gl.glAlphaFunc(GL10.GL_GEQUAL, 0.5f);
		gl.glEnable(GL10.GL_ALPHA_TEST);
	}

	@Override
	public void draw(GL10 gl) {
		long now = System.currentTimeMillis();

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		for (GuitarCodeMarker codeMarker : codeMarkers) {
			codeMarker.checkHold(now, activity);
		}
		// チェックし終わったら排他的にholdされているかどうかの情報を更新
		for (GuitarCodeMarker codeMarker : codeMarkers) {
			codeMarker.updateExclusiveHold(activity);
		}

		playMarker.checkPlaySound(now, activity);

		setProjectionMatrix(gl);

		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);

		gl.glMatrixMode(GL10.GL_MODELVIEW);

		//boolean usingFrontCamera = activity.isUsingFrontCamera();
		CameraRotationInfo cameraRotationInfo = activity.getCameraRotationInfo();

		for (Marker codeMarker : codeMarkers) {
			codeMarker.draw(gl, now, cameraRotationInfo);
		}

		playMarker.draw(gl, now, cameraRotationInfo);
		if (activity.getCurrentOctave() == 0) {
			playMarker.drawOutline(gl, acousticOutlinePlane, now, cameraRotationInfo);
		} else {
			playMarker.drawOutline(gl, electricOutlinePlane, now, cameraRotationInfo);
		}
	}
}
