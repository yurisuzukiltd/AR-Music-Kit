package com.goldrushcomputing.playsound;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.FrameLayout;
import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.rendering.ARRenderer;
import org.fmod.FMODAudioDevice;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("JniMissingFunction")
public class Example extends ARActivity {
	static {
		System.loadLibrary("fmodex");
		System.loadLibrary("main");
	}

	public static final int INSTRUMENT_TYPE_PIANO = 0;
	public static final int INSTRUMENT_TYPE_MUSIC_BOX = 1;
	public static final int INSTRUMENT_TYPE_ACOUSTIC_GUITAR = 2;
	public static final int INSTRUMENT_TYPE_ELECTRIC_GUITAR = 3;

	private static final String[] pianoSounds = {
			"piano/l-do.wav",
			"piano/l-re.wav",
			"piano/l-mi.wav",
			"piano/l-fa.wav",
			"piano/l-so.wav",
			"piano/l-la.wav",
			"piano/l-si.wav",
			"piano/l-do-.wav",
			"piano/m-do.wav",
			"piano/m-re.wav",
			"piano/m-mi.wav",
			"piano/m-fa.wav",
			"piano/m-so.wav",
			"piano/m-la.wav",
			"piano/m-si.wav",
			"piano/m-do-.wav",
			"piano/h-do.wav",
			"piano/h-re.wav",
			"piano/h-mi.wav",
			"piano/h-fa.wav",
			"piano/h-so.wav",
			"piano/h-la.wav",
			"piano/h-si.wav",
			"piano/h-do-.wav"
	};

	private static final String[] musicBoxSounds = {
			"musicbox/l-do.wav",
			"musicbox/l-re.wav",
			"musicbox/l-mi.wav",
			"musicbox/l-fa.wav",
			"musicbox/l-so.wav",
			"musicbox/l-la.wav",
			"musicbox/l-si.wav",
			"musicbox/l-do-.wav",
			"musicbox/m-do.wav",
			"musicbox/m-re.wav",
			"musicbox/m-mi.wav",
			"musicbox/m-fa.wav",
			"musicbox/m-so.wav",
			"musicbox/m-la.wav",
			"musicbox/m-si.wav",
			"musicbox/m-do-.wav",
			"musicbox/h-do.wav",
			"musicbox/h-re.wav",
			"musicbox/h-mi.wav",
			"musicbox/h-fa.wav",
			"musicbox/h-so.wav",
			"musicbox/h-la.wav",
			"musicbox/h-si.wav",
			"musicbox/h-do-.wav"
	};

	private static final String[] acousticGuitarSounds = {
			"acousticguitar/C.wav",
			"acousticguitar/Dm.wav",
			"acousticguitar/Em.wav",
			"acousticguitar/F.wav",
			"acousticguitar/G.wav",
			"acousticguitar/Am.wav",
			"acousticguitar/B5.wav"
	};

	private static final String[] electricGuitarSounds = {
			"electronicguitar/C.wav",
			"electronicguitar/Dm.wav",
			"electronicguitar/Em.wav",
			"electronicguitar/F.wav",
			"electronicguitar/G.wav",
			"electronicguitar/Am.wav",
			"electronicguitar/B5.wav",
	};

	private static final String[] drumSounds = {
			"drum/bass.wav",
			"drum/hat.wav",
			"drum/snaredrum.wav",
			"drum/bosa.wav"
	};

	private FMODAudioDevice mFMODAudioDevice = new FMODAudioDevice();

	private Handler mUpdateHandler = new Handler() {
		public void handleMessage(Message msg) {
			cUpdate();
			removeMessages(0);
			sendMessageDelayed(obtainMessage(0), 50);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	public void onStart() {
		super.onStart();
		mFMODAudioDevice.start();

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				startPlayer();
			}
		}, 1000);

		mainLayout = (FrameLayout) this.findViewById(R.id.mainLayout);
	}

	@Override
	public void onStop() {
		mUpdateHandler.removeMessages(0);
		cEnd();
		mFMODAudioDevice.stop();
		super.onStop();
	}

	public native void cBegin(String[] soundPathArray);

	public native void cUpdate();

	public native void cEnd();

	public native void cPlaySound(int id);

	public native int cGetLength();

	public native int cGetPosition();

	public native boolean cGetPlaying();

	public native int cGetChannelsPlaying();

	/* FMOD Player */
	public void startPlayer() {
		mFMODAudioDevice.start();

		loadSoundFiles(pianoSounds);

		mUpdateHandler.sendMessageDelayed(mUpdateHandler.obtainMessage(0), 0);
	}

	private void loadSoundFiles(String[] soundPathes) {
		String directoryPath = getTrackDirectory();
		List<String> list = new ArrayList<>();
		for (String soundFilePath : soundPathes) {
			list.add(directoryPath + "/" + soundFilePath);
		}

		String[] filePathes = list.toArray(new String[list.size()]);
		cBegin(filePathes);
	}

	public String getTrackDirectory() {
		return this.getCacheDir().getAbsolutePath() + "/Music";
	}

	public void endPlayer() {
		cEnd();
		mFMODAudioDevice.stop();
	}

	public void playSound(int trackIndex) {
		cPlaySound(trackIndex);
	}

	@Override
	protected ARRenderer supplyRenderer() {
		//return new DrumsRenderer(this);
		return new PianoRenderer(this);
		//return new MusicBoxRenderer(this);
	}

	/**
	 * Use the FrameLayout in this Activity's UI.
	 */
	@Override
	protected FrameLayout supplyFrameLayout() {
		return (FrameLayout) this.findViewById(R.id.mainLayout);
	}
}
