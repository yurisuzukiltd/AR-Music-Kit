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

	private static final String[] guitarSounds = {
			"acousticguitar/C.wav",
			"acousticguitar/Dm.wav",
			"acousticguitar/Em.wav",
			"acousticguitar/F.wav",
			"acousticguitar/G.wav",
			"acousticguitar/Am.wav",
			"acousticguitar/B5.wav",
			"electronicguitar/C.wav",
			"electronicguitar/Dm.wav",
			"electronicguitar/Em.wav",
			"electronicguitar/F.wav",
			"electronicguitar/G.wav",
			"electronicguitar/Am.wav",
			"electronicguitar/B5.wav",
	};



	private static final String[][] instrumentSounds = {
			pianoSounds, musicBoxSounds, guitarSounds
	};

	private FMODAudioDevice mFMODAudioDevice = new FMODAudioDevice();

	/// 楽器タイプの切り替え
	//private int instrumentType = INSTRUMENT_TYPE_GUITAR;
	//private int instrumentType = INSTRUMENT_TYPE_MUSIC_BOX;
	private int instrumentType = INSTRUMENT_TYPE_PIANO;

	/// ギターで利用する現在設定されているサウンド(-1だと設定無し)
	private int currentSoundId = -1;

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

	Bitmap octaveSwitchLImage;
	Bitmap octaveSwitchMImage;
	Bitmap octaveSwitchHImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String type = extras.getString("type");
			if(type != null){
				if(type.equals("guitar")){
					instrumentType = INSTRUMENT_TYPE_GUITAR;
				}else if(type.equals("musicbox")){
					instrumentType = INSTRUMENT_TYPE_MUSIC_BOX;
				}else if(type.equals("piano")){
					instrumentType = INSTRUMENT_TYPE_PIANO;
				}
			}
		}
		guitarAcousticIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_acoustic_guitar);
		guitarElecIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_electric_guitar);
		guitarSwitchAcousticImage = BitmapFactory.decodeResource(getResources(), R.drawable.switch_guitar_a);
		guitarSwitchElecImage = BitmapFactory.decodeResource(getResources(), R.drawable.switch_guitar_e);

		octaveSwitchLImage = BitmapFactory.decodeResource(getResources(), R.drawable.switch_octave_l);
		octaveSwitchMImage = BitmapFactory.decodeResource(getResources(), R.drawable.switch_octave_m);
		octaveSwitchHImage = BitmapFactory.decodeResource(getResources(), R.drawable.switch_octave_h);


		cameraSwapButton = (ImageButton)findViewById(R.id.rear_front_switch);
		cameraSwapButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CaptureCameraPreview cameraPreview = getCameraPreview();
				cameraPreview.swapCamera();

			}
		});

		currentInstrumentIcon = (ImageButton)findViewById(R.id.current_instrument_icon);
		infoButton = (ImageButton)findViewById(R.id.info_icon);
		guitarSwitch = (ImageButton)findViewById(R.id.guitar_switch);
		octaveSwitch = (ImageButton)findViewById(R.id.octave_switch);

		if(instrumentType == INSTRUMENT_TYPE_GUITAR){
			guitarSwitch.setVisibility(View.VISIBLE);
			octaveSwitch.setVisibility(View.INVISIBLE);
			if(currentOctave == 0){
				currentInstrumentIcon.setImageBitmap(guitarAcousticIcon);
				guitarSwitch.setImageBitmap(guitarSwitchAcousticImage);
			}else{
				currentInstrumentIcon.setImageBitmap(guitarElecIcon);
				guitarSwitch.setImageBitmap(guitarSwitchElecImage);
			}

		}else if(instrumentType == INSTRUMENT_TYPE_PIANO){
			currentInstrumentIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_piano));
			guitarSwitch.setVisibility(View.INVISIBLE);
			octaveSwitch.setVisibility(View.VISIBLE);

			if(currentOctave == 0){
				octaveSwitch.setImageBitmap(octaveSwitchLImage);
			}else if(currentOctave == 1){
				octaveSwitch.setImageBitmap(octaveSwitchMImage);
			}else{
				octaveSwitch.setImageBitmap(octaveSwitchHImage);
			}
		}else if(instrumentType == INSTRUMENT_TYPE_MUSIC_BOX){
			currentInstrumentIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_music_box));
			guitarSwitch.setVisibility(View.INVISIBLE);

			octaveSwitch.setVisibility(View.VISIBLE);

			if(currentOctave == 0){
				octaveSwitch.setImageBitmap(octaveSwitchLImage);
			}else if(currentOctave == 1){
				octaveSwitch.setImageBitmap(octaveSwitchMImage);
			}else{
				octaveSwitch.setImageBitmap(octaveSwitchHImage);
			}
		}else{
			currentInstrumentIcon.setVisibility(View.INVISIBLE);
			guitarSwitch.setVisibility(View.INVISIBLE);
			octaveSwitch.setVisibility(View.INVISIBLE);
		}

		guitarSwitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currentOctave == 0){
					currentOctave = 1;
					currentInstrumentIcon.setImageBitmap(guitarElecIcon);
					guitarSwitch.setImageBitmap(guitarSwitchElecImage);
				}else{
					currentOctave = 0;
					currentInstrumentIcon.setImageBitmap(guitarAcousticIcon);
					guitarSwitch.setImageBitmap(guitarSwitchAcousticImage);
				}
			}
		});

		octaveSwitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currentOctave == 0){
					currentOctave = 1;
					octaveSwitch.setImageBitmap(octaveSwitchMImage);
				}else if(currentOctave == 1){
					currentOctave = 2;
					octaveSwitch.setImageBitmap(octaveSwitchHImage);
				}else{
					currentOctave = 0;
					octaveSwitch.setImageBitmap(octaveSwitchLImage);
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

	private int getCurrentOffset(){
		if(instrumentType == INSTRUMENT_TYPE_PIANO){
			return currentOctave * 8;
		}else if(instrumentType == INSTRUMENT_TYPE_MUSIC_BOX){
			return currentOctave * 8;
		}else if(instrumentType == INSTRUMENT_TYPE_GUITAR){
			return currentOctave * 7;
		}else{
			return 0;
		}
	}

	/**
	 * TODO:
	 * low,mid,highを切り替える時は、ここで値にoffsetを加えて、cPlaySound()を呼び出す.
	 */
	public void playSound(int soundId) {
		cPlaySound(soundId + getCurrentOffset());
	}

	public void playCurrentSound() {
		Log.d(TAG, "playCurrentSound: currentSoundId=" + currentSoundId);
		if( currentSoundId >= 0 ) {

			//cDistortionToggle();


			cPlaySound(currentSoundId  + getCurrentOffset());
		}
	}

	public void setCurrentSound(int soundId) {
		Log.d(TAG, "setCurrentSound: soundId=" + soundId);
		currentSoundId = soundId;
	}

	public void stopCurrentSound(int soundId) {
		Log.d(TAG, "stopCurrentSound: soundId=" + soundId + " current=" + currentSoundId);
		if( currentSoundId == soundId ) {
			currentSoundId = -1;
		}
	}

	@Override
	protected ARRenderer supplyRenderer() {
		if( instrumentType == INSTRUMENT_TYPE_PIANO) {
			return new PianoRenderer(this);
		} else if( instrumentType == INSTRUMENT_TYPE_MUSIC_BOX ) {
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


