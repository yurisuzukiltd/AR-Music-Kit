/*
 *  Author(s):
 *  Takamitsu Mizutori, Goldrush Computing Inc.
 *  Kosuke Miyoshi, Narrative Nights
 */

package com.yurisuzuki;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.yurisuzuki.playsound.R;
import com.yurisuzuki.ar.GuitarRenderer;
import com.yurisuzuki.ar.MusicBoxRenderer;
import com.yurisuzuki.ar.PianoRenderer;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.camera.CaptureCameraPreview;
import org.artoolkit.ar.base.rendering.ARRenderer;
import org.fmod.FMODAudioDevice;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("JniMissingFunction")
public class CameraActivity extends ARActivity {
	static {
		System.loadLibrary("fmodex");
		System.loadLibrary("main");
	}

	public static final int INSTRUMENT_TYPE_PIANO = 0;
	public static final int INSTRUMENT_TYPE_MUSIC_BOX = 1;
	public static final int INSTRUMENT_TYPE_GUITAR = 2;

	public int currentOctave = 0;

	private static final String[] pianoSounds = {
			"piano/m-do.wav",
			"piano/m-re.wav",
			"piano/m-mi.wav",
			"piano/m-fa.wav",
			"piano/m-so.wav",
			"piano/m-la.wav",
			"piano/m-si.wav",
			"piano/m-do-.wav",
			"piano/kb1-do.wav",
			"piano/kb1-re.wav",
			"piano/kb1-mi.wav",
			"piano/kb1-fa.wav",
			"piano/kb1-so.wav",
			"piano/kb1-la.wav",
			"piano/kb1-si.wav",
			"piano/kb1-do-.wav",
			"piano/kb2-do.wav",
			"piano/kb2-re.wav",
			"piano/kb2-mi.wav",
			"piano/kb2-fa.wav",
			"piano/kb2-so.wav",
			"piano/kb2-la.wav",
			"piano/kb2-si.wav",
			"piano/kb2-do-.wav"
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

	private static final String[] guitarSounds = {
			"acousticguitar/C.wav",
			"acousticguitar/A.wav",
			"acousticguitar/G.wav",
			"acousticguitar/E.wav",
			"acousticguitar/D.wav",
			"acousticguitar/Am.wav",
			"acousticguitar/Em.wav",
			"acousticguitar/F.wav",
			"acousticguitar/Open.wav",
			"electronicguitar/C.wav",
			"electronicguitar/A.wav",
			"electronicguitar/G.wav",
			"electronicguitar/E.wav",
			"electronicguitar/D.wav",
			"electronicguitar/Am.wav",
			"electronicguitar/Em.wav",
			"electronicguitar/F.wav",
			"electronicguitar/Open.wav",
	};


	private static final String[][] instrumentSounds = {
			pianoSounds, musicBoxSounds, guitarSounds
	};

	private FMODAudioDevice mFMODAudioDevice = new FMODAudioDevice();

	/// 楽器タイプの切り替え
	private int instrumentType = INSTRUMENT_TYPE_GUITAR;

	private Handler mUpdateHandler = new Handler() {
		public void handleMessage(Message msg) {
			cUpdate();
			removeMessages(0);
			sendMessageDelayed(obtainMessage(0), 50);
		}
	};

	ImageButton currentInstrumentIcon;
	ImageButton infoButton;
	ImageButton guitarSwitch;
	ImageButton octaveSwitch;
	ImageButton cameraSwapButton;

	Bitmap guitarAcousticIcon;
	Bitmap guitarElecIcon;
	Bitmap guitarSwitchAcousticImage;
	Bitmap guitarSwitchElecImage;

	Bitmap pianoSwitchLImage;
	Bitmap pianoSwitchMImage;
	Bitmap pianoSwitchHImage;

	Bitmap musicBoxSwitchLImage;
	Bitmap musicBoxSwitchMImage;
	Bitmap musicBoxSwitchHImage;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String type = extras.getString("type");
			if (type != null) {
				if (type.equals("guitar")) {
					instrumentType = INSTRUMENT_TYPE_GUITAR;
				} else if (type.equals("musicbox")) {
					instrumentType = INSTRUMENT_TYPE_MUSIC_BOX;
				} else if (type.equals("piano")) {
					instrumentType = INSTRUMENT_TYPE_PIANO;
				}
			}
		}
		guitarAcousticIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_acoustic_guitar);
		guitarElecIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_electric_guitar);
		guitarSwitchAcousticImage = BitmapFactory.decodeResource(getResources(), R.drawable.switch_guitar_a);
		guitarSwitchElecImage = BitmapFactory.decodeResource(getResources(), R.drawable.switch_guitar_e);

		pianoSwitchLImage = BitmapFactory.decodeResource(getResources(), R.drawable.switch_piano_l);
		pianoSwitchMImage = BitmapFactory.decodeResource(getResources(), R.drawable.switch_piano_m);
		pianoSwitchHImage = BitmapFactory.decodeResource(getResources(), R.drawable.switch_piano_h);

		musicBoxSwitchLImage = BitmapFactory.decodeResource(getResources(), R.drawable.switch_mb_l);
		musicBoxSwitchMImage = BitmapFactory.decodeResource(getResources(), R.drawable.switch_mb_m);
		musicBoxSwitchHImage = BitmapFactory.decodeResource(getResources(), R.drawable.switch_mb_h);


		cameraSwapButton = (ImageButton) findViewById(R.id.rear_front_switch);
		cameraSwapButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CaptureCameraPreview cameraPreview = getCameraPreview();
				cameraPreview.swapCamera();

			}
		});

		currentInstrumentIcon = (ImageButton) findViewById(R.id.current_instrument_icon);
		infoButton = (ImageButton) findViewById(R.id.info_icon);
		guitarSwitch = (ImageButton) findViewById(R.id.guitar_switch);
		octaveSwitch = (ImageButton) findViewById(R.id.octave_switch);


		if (instrumentType == INSTRUMENT_TYPE_GUITAR) {
			guitarSwitch.setVisibility(View.VISIBLE);
			octaveSwitch.setVisibility(View.INVISIBLE);
			if (currentOctave == 0) {
				currentInstrumentIcon.setImageBitmap(guitarAcousticIcon);
				guitarSwitch.setImageBitmap(guitarSwitchAcousticImage);
			} else {
				currentInstrumentIcon.setImageBitmap(guitarElecIcon);
				guitarSwitch.setImageBitmap(guitarSwitchElecImage);
			}
			isRearCameraDefault = false;
		} else if (instrumentType == INSTRUMENT_TYPE_PIANO) {
			currentInstrumentIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_piano));
			guitarSwitch.setVisibility(View.INVISIBLE);
			octaveSwitch.setVisibility(View.VISIBLE);

			if (currentOctave == 0) {
				octaveSwitch.setImageBitmap(pianoSwitchLImage);
			} else if (currentOctave == 1) {
				octaveSwitch.setImageBitmap(pianoSwitchMImage);
			} else {
				octaveSwitch.setImageBitmap(pianoSwitchHImage);
			}
			isRearCameraDefault = true;
		} else if (instrumentType == INSTRUMENT_TYPE_MUSIC_BOX) {
			currentInstrumentIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_music_box));
			guitarSwitch.setVisibility(View.INVISIBLE);

			octaveSwitch.setVisibility(View.VISIBLE);

			if (currentOctave == 0) {
				octaveSwitch.setImageBitmap(musicBoxSwitchLImage);
			} else if (currentOctave == 1) {
				octaveSwitch.setImageBitmap(musicBoxSwitchMImage);
			} else {
				octaveSwitch.setImageBitmap(musicBoxSwitchHImage);
			}
			isRearCameraDefault = true;
		} else {
			currentInstrumentIcon.setVisibility(View.INVISIBLE);
			guitarSwitch.setVisibility(View.INVISIBLE);
			octaveSwitch.setVisibility(View.INVISIBLE);
		}

		guitarSwitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentOctave == 0) {
					currentOctave = 1;
					currentInstrumentIcon.setImageBitmap(guitarElecIcon);
					guitarSwitch.setImageBitmap(guitarSwitchElecImage);
				} else {
					currentOctave = 0;
					currentInstrumentIcon.setImageBitmap(guitarAcousticIcon);
					guitarSwitch.setImageBitmap(guitarSwitchAcousticImage);
				}
			}
		});

		octaveSwitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (instrumentType == INSTRUMENT_TYPE_PIANO) {
					if (currentOctave == 0) {
						currentOctave = 1;
						octaveSwitch.setImageBitmap(pianoSwitchMImage);
					} else if (currentOctave == 1) {
						currentOctave = 2;
						octaveSwitch.setImageBitmap(pianoSwitchHImage);
					} else {
						currentOctave = 0;
						octaveSwitch.setImageBitmap(pianoSwitchLImage);
					}
				} else if (instrumentType == INSTRUMENT_TYPE_MUSIC_BOX) {
					if (currentOctave == 0) {
						currentOctave = 1;
						octaveSwitch.setImageBitmap(musicBoxSwitchMImage);
					} else if (currentOctave == 1) {
						currentOctave = 2;
						octaveSwitch.setImageBitmap(musicBoxSwitchHImage);
					} else {
						currentOctave = 0;
						octaveSwitch.setImageBitmap(musicBoxSwitchLImage);
					}
				}

			}
		});

		infoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});


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

	public native int cDistortionToggle();

	/* FMOD Player */
	public void startPlayer() {
		mFMODAudioDevice.start();

		String[] sounds = instrumentSounds[instrumentType];
		loadSoundFiles(sounds);
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

		//cDistortionToggle();
	}

	public String getTrackDirectory() {
		return this.getCacheDir().getAbsolutePath() + "/Music";
	}

	public void endPlayer() {
		cEnd();
		mFMODAudioDevice.stop();
	}

	private int getCurrentOffset() {
		if (instrumentType == INSTRUMENT_TYPE_GUITAR) {
			return currentOctave * 9;
		} else {
			return currentOctave * 8;
		}
	}

	/**
	 * low,mid,highを切り替える時は、ここで値にoffsetを加えて、cPlaySound()を呼び出す.
	 */
	public void playSound(int soundId) {
		cPlaySound(soundId + getCurrentOffset());
	}

	private static class GuitarMarkerStates {
		private boolean soundEnableStates[] = new boolean[8];

		/**
		 * コードマーカーが認識された.
		 */
		void onMarkerDetected(int soundId) {
			soundEnableStates[soundId] = false;
		}

		/**
		 * マーカーが消えた.
		 */
		void onMarkerDisappeared(int soundId) {
			// 既存のonのを消して、新たにsoundIdのものをonにする.
			for (int i = 0; i < 8; ++i) {
				if (i == soundId) {
					soundEnableStates[i] = true;
				} else {
					soundEnableStates[i] = false;
				}
			}
		}

		/**
		 * マーカーがholdされていたのが期限が切れた.
		 */
		void onMarkerDisappearExpired(int soundId) {
			soundEnableStates[soundId] = false;
		}

		int getCurrentSoundId() {
			for(int i=0; i<soundEnableStates.length; ++i) {
				if (soundEnableStates[i]) {
					return i;
				}
			}
			return -1;
		}
	}

	private GuitarMarkerStates guitarMarkerStates;

	private void prepareGuitarMarkerStates() {
		if( guitarMarkerStates == null ) {
			guitarMarkerStates = new GuitarMarkerStates();
		}
	}

	/**
	 * ギター専用
	 */
	public void playCurrentSound() {
		prepareGuitarMarkerStates();
		int currentSoundId = guitarMarkerStates.getCurrentSoundId();

		Log.d(TAG, "playCurrentSound: currentSoundId=" + currentSoundId);

		if (currentSoundId >= 0) {
			cPlaySound(currentSoundId + getCurrentOffset());
		} else {
			// ギターの開放弦
			cPlaySound(8 + getCurrentOffset());
		}
	}

	/**
	 * マーカーが識された.
	 */
	public void suppressCurrentSound(int soundId) {
		Log.d(TAG, "suppressCurrentSound: soundId=" + soundId);
		prepareGuitarMarkerStates();
		guitarMarkerStates.onMarkerDetected(soundId);
	}

	/**
	 * ギターマーカーが消された.
	 */
	public void setCurrentSound(int soundId) {
		Log.d(TAG, "setCurrentSound: soundId=" + soundId);
		prepareGuitarMarkerStates();
		guitarMarkerStates.onMarkerDisappeared(soundId);
	}

	/**
	 * ギターマーカーが消されてから一定時間経ったのでholdが無効化された.
	 */
	public void stopCurrentSound(int soundId) {
		Log.d(TAG, "stopCurrentSound: soundId=" + soundId);
		prepareGuitarMarkerStates();
		guitarMarkerStates.onMarkerDisappearExpired(soundId);
	}

	/**
	 * 指定したマーカーがexclusiveにhold状態かどうか
	 */
	public boolean checkMarkerHolded(int soundId) {
		prepareGuitarMarkerStates();
 		return guitarMarkerStates.getCurrentSoundId() == soundId;
	}

	public int getCurrentOctave() {
		return currentOctave;
	}

	@Override
	protected ARRenderer supplyRenderer() {
		if (instrumentType == INSTRUMENT_TYPE_PIANO) {
			return new PianoRenderer(this);
		} else if (instrumentType == INSTRUMENT_TYPE_MUSIC_BOX) {
			return new MusicBoxRenderer(this);
		} else {
			return new GuitarRenderer(this);
		}
	}

	/**
	 * Use the FrameLayout in this Activity's UI.
	 */
	@Override
	protected FrameLayout supplyFrameLayout() {
		return (FrameLayout) this.findViewById(R.id.mainLayout);
	}

	@Override
	protected FrameLayout supplyOuterFrameLayout() {
		return (FrameLayout) this.findViewById(R.id.outerLayout);
	}
}

