package com.goldrushcomputing.playsound;

import android.util.Log;
import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GuitarRenderer extends ARRenderer {
private static final String TAG = "GuitarRenderer";
	private Example activity;

	private static final String[] acousticMarkerParams = {
			"single;Data/guitar.pat;64",
			"single;Data/C.pat;64",
			"single;Data/Dm.pat;64",
			"single;Data/Em.pat;64",
			"single;Data/F.pat;64",
			"single;Data/G.pat;64",
			"single;Data/Am.pat;64",
			"single;Data/B5.pat;64",
	};

	private static final String[] electricMarkerParams = {
			"single;Data/ElectricGuitar.pat;64",
			"single;Data/C.pat;64",
			"single;Data/Dm.pat;64",
			"single;Data/Em.pat;64",
			"single;Data/F.pat;64",
			"single;Data/G.pat;64",
			"single;Data/Am.pat;64",
			"single;Data/B5.pat;64",
	};

	private static final String[] acousticMarkerTexturePaths = {
			"Texture/Guitar_Acoustic.png",
			"Texture/Code_C.png",
			"Texture/Code_Dm.png",
			"Texture/Code_Em.png",
			"Texture/Code_F.png",
			"Texture/Code_G.png",
			"Texture/Code_Am.png",
			"Texture/Code_B5.png",
	};

	private static final String[] electricMarkerTexturePaths = {
			"Texture/Guitar_Electric.png",
			"Texture/Code_C.png",
			"Texture/Code_Dm.png",
			"Texture/Code_Em.png",
			"Texture/Code_F.png",
			"Texture/Code_G.png",
			"Texture/Code_Am.png",
			"Texture/Code_B5.png",
	};

	private static final String actionTexturePath = "Texture/Action_red.png";

	private boolean acoustic;
	private Marker[] markers;

	public GuitarRenderer(Example activity, boolean acoustic) {
		this.activity = activity;
		this.acoustic = acoustic;

		if( acoustic ) {
			markers = new Marker[acousticMarkerParams.length];
		} else {
			markers = new Marker[electricMarkerParams.length];
		}

		for (int i = 0; i < markers.length; ++i) {
			Marker marker = new Marker();
			markers[i] = marker;
		}
	}

	@Override
	public boolean configureARScene() {
		for (int i = 0; i < markers.length; ++i) {
			boolean ret;
			String markerParam;
			if( acoustic ) {
				markerParam = acousticMarkerParams[i];
			} else {
				markerParam = electricMarkerParams[i];
			}
			// TODO: サウンドIDの指定を仕様が固まったら対応すること
			int soundId = 0;
			ret = markers[i].init(markerParam, soundId);
			if (!ret) {
				Log.d(TAG, "marker load failed:" + markerParam);
				return false;
			}
		}
		return true;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);

		// 各マーカーテクスチャロード
		for (int i = 0; i < markers.length; ++i) {
			String texturePath;
			if( acoustic ) {
				texturePath = acousticMarkerTexturePaths[i];
			} else {
				texturePath = electricMarkerTexturePaths[i];
			}
			boolean ret = markers[i].loadMarkerTexture(gl, activity, texturePath);
			if (!ret) {
				Log.d(TAG, "marker texture failed:" + texturePath);
			}

			if( i == 0 ) {
				// ギターマーカーは、発音テクスチャ利用
				markers[i].loadActionTexture(gl, activity, actionTexturePath);
			}
		}

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

		for (Marker marker : markers) {
			marker.checkPlaySound(now, activity);
		}

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadMatrixf(ARToolKit.getInstance().getProjectionMatrix(), 0);

		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glFrontFace(GL10.GL_CW);

		gl.glMatrixMode(GL10.GL_MODELVIEW);

		for (Marker marker : markers) {
			marker.draw(gl, now);
		}
	}
}
