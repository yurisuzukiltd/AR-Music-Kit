package com.goldrushcomputing.playsound.ar;

import android.util.Log;
import com.goldrushcomputing.playsound.Example;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GuitarRenderer extends InstrumentsRenderer {
	private static final String TAG = "GuitarRenderer";
	private Example activity;

	// マーカーデータ
	private static final String[] acousticMarkerParams = {
			"single;Data/C.pat;64",
			"single;Data/Dm.pat;64",
			"single;Data/Em.pat;64",
			"single;Data/F.pat;64",
			"single;Data/G.pat;64",
			"single;Data/Am.pat;64",
			"single;Data/B5.pat;64",
	};

	private static final String acousticPlayMarkerParam = "single;Data/guitar.pat;64";

	private static final String[] electricMarkerParams = {
			"single;Data/C.pat;64",
			"single;Data/Dm.pat;64",
			"single;Data/Em.pat;64",
			"single;Data/F.pat;64",
			"single;Data/G.pat;64",
			"single;Data/Am.pat;64",
			"single;Data/B5.pat;64",
	};

	private static final String electricPlayMarkerParam = "single;Data/ElectricGuitar.pat;64";

	// 認識時のテクスチャ
	private static final String[] acousticMarkerTexturePaths = {
			"Texture/Code_C.png",
			"Texture/Code_Dm.png",
			"Texture/Code_Em.png",
			"Texture/Code_F.png",
			"Texture/Code_G.png",
			"Texture/Code_Am.png",
			"Texture/Code_B5.png",
	};

	private static final String acousticPlayMarkerTexturePath = "Texture/Guitar_Acoustic.png";

	private static final String[] electricMarkerTexturePaths = {
			"Texture/Code_C.png",
			"Texture/Code_Dm.png",
			"Texture/Code_Em.png",
			"Texture/Code_F.png",
			"Texture/Code_G.png",
			"Texture/Code_Am.png",
			"Texture/Code_B5.png",
	};

	// コードホールド時用テクスチャ
	private static final String[] holdTexturePaths = {
			"Texture/Action_purple.png",
			"Texture/Action_blue.png",
			"Texture/Action_green.png",
			"Texture/Action_yellow.png",
			"Texture/Action_orange.png",
			"Texture/Action_brown.png",
			"Texture/Action_black.png",
	};

	private static final String electricPlayMarkerTexturePath = "Texture/Guitar_Electric.png";

	// ギターの発音時のテクスチャ
	private static final String actionTexturePath = "Texture/Action_red.png";

	private boolean acoustic;
	private GuitarCodeMarker[] codeMarkers;
	private GuitarPlayMarker playMarker = new GuitarPlayMarker();

	public GuitarRenderer(Example activity, boolean acoustic) {
		this.activity = activity;
		this.acoustic = acoustic;

		if (acoustic) {
			codeMarkers = new GuitarCodeMarker[acousticMarkerParams.length];
		} else {
			codeMarkers = new GuitarCodeMarker[electricMarkerParams.length];
		}

		for (int i = 0; i < codeMarkers.length; ++i) {
			codeMarkers[i] = new GuitarCodeMarker();
		}
	}

	@Override
	public boolean configureARScene() {
		for (int i = 0; i < codeMarkers.length; ++i) {
			boolean ret;
			String markerParam;
			if (acoustic) {
				markerParam = acousticMarkerParams[i];
			} else {
				markerParam = electricMarkerParams[i];
			}
			ret = codeMarkers[i].init(markerParam, i);
			if (!ret) {
				Log.d(TAG, "marker load failed:" + markerParam);
				return false;
			}
		}

		if (acoustic) {
			playMarker.init(acousticPlayMarkerParam, -1);
		} else {
			playMarker.init(electricPlayMarkerParam, -1);
		}

		return true;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);

		// 各マーカーテクスチャロード
		for (int i = 0; i < codeMarkers.length; ++i) {
			String texturePath;
			if (acoustic) {
				texturePath = acousticMarkerTexturePaths[i];
			} else {
				texturePath = electricMarkerTexturePaths[i];
			}
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

		if (acoustic) {
			playMarker.loadMarkerTexture(gl, activity, acousticPlayMarkerTexturePath);
		} else {
			playMarker.loadMarkerTexture(gl, activity, electricPlayMarkerTexturePath);
		}
		playMarker.loadActionTexture(gl, activity, actionTexturePath);

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
		playMarker.checkPlaySound(now, activity);

		setProjectionMatrix(gl);

		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glFrontFace(GL10.GL_CW);

		gl.glMatrixMode(GL10.GL_MODELVIEW);

		for (Marker codeMarker : codeMarkers) {
			codeMarker.draw(gl, now);
		}
		playMarker.draw(gl, now);
	}
}
