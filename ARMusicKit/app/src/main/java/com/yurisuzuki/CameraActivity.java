/*
 *  Author(s):
 *  Takamitsu Mizutori, Goldrush Computing Inc.
 *  Kosuke Miyoshi, Narrative Nights
 */

package com.yurisuzuki;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.yurisuzuki.ar.GuitarRenderer;
import com.yurisuzuki.ar.MusicBoxRenderer;
import com.yurisuzuki.ar.PianoRenderer;
import com.yurisuzuki.playsound.R;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.camera.CaptureCameraPreview;
import org.artoolkit.ar.base.rendering.ARRenderer;
import org.fmod.FMODAudioDevice;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("JniMissingFunction")
public class CameraActivity extends ARActivity {

	public enum CameraOrientation {
		Portlait0, Landscape90, Portrailt180, Landscape270,
	};

	CameraOrientation currentOrientation;



	static {
		System.loadLibrary("fmodex");
		System.loadLibrary("main");
	}

	public static final int INSTRUMENT_TYPE_PIANO = 0;
	public static final int INSTRUMENT_TYPE_MUSIC_BOX = 1;
	public static final int INSTRUMENT_TYPE_GUITAR = 2;

	public int currentOctave = 0;

	private long lastTimeGuitarSoundPlayed = 0;

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

	ImageButton currentInstrumentIcon0;
	ImageButton currentInstrumentIcon0_Piano;
	ImageButton currentInstrumentIcon0_MusicBox;
	ImageButton infoButton0;
	ImageButton guitarSwitch0;
	ImageButton octaveSwitch0;
	ImageButton cameraSwapButton0;

	ImageButton currentInstrumentIcon90;
	ImageButton infoButton90;
	ImageButton guitarSwitch90;
	ImageButton octaveSwitch90;
	ImageButton cameraSwapButton90;

	ImageButton currentInstrumentIcon180;
	ImageButton currentInstrumentIcon180_Piano;
	ImageButton currentInstrumentIcon180_MusicBox;
	ImageButton infoButton180;
	ImageButton guitarSwitch180;
	ImageButton octaveSwitch180;
	ImageButton cameraSwapButton180;

	ImageButton currentInstrumentIcon270;
	ImageButton infoButton270;
	ImageButton guitarSwitch270;
	ImageButton octaveSwitch270;
	ImageButton cameraSwapButton270;

	Bitmap guitarAcousticIcon0;
	Bitmap guitarAcousticIcon90;
	Bitmap guitarAcousticIcon180;
	Bitmap guitarAcousticIcon270;
	Bitmap guitarElecIcon0;
	Bitmap guitarElecIcon90;
	Bitmap guitarElecIcon180;
	Bitmap guitarElecIcon270;

	Bitmap pianoIcon0;
	Bitmap pianoIcon90;
	Bitmap pianoIcon180;
	Bitmap pianoIcon270;

	Bitmap musicBoxIcon0;
	Bitmap musicBoxIcon90;
	Bitmap musicBoxIcon180;
	Bitmap musicBoxIcon270;

	Bitmap guitarSwitchAcousticImage0;
	Bitmap guitarSwitchAcousticImage90;
	Bitmap guitarSwitchAcousticImage180;
	Bitmap guitarSwitchAcousticImage270;
	Bitmap guitarSwitchElecImage0;
	Bitmap guitarSwitchElecImage90;
	Bitmap guitarSwitchElecImage180;
	Bitmap guitarSwitchElecImage270;

	Bitmap pianoSwitchLImage0;
	Bitmap pianoSwitchLImage90;
	Bitmap pianoSwitchLImage180;
	Bitmap pianoSwitchLImage270;
	Bitmap pianoSwitchMImage0;
	Bitmap pianoSwitchMImage90;
	Bitmap pianoSwitchMImage180;
	Bitmap pianoSwitchMImage270;
	Bitmap pianoSwitchHImage0;
	Bitmap pianoSwitchHImage90;
	Bitmap pianoSwitchHImage180;
	Bitmap pianoSwitchHImage270;

	Bitmap musicBoxSwitchLImage0;
	Bitmap musicBoxSwitchLImage90;
	Bitmap musicBoxSwitchLImage180;
	Bitmap musicBoxSwitchLImage270;
	Bitmap musicBoxSwitchMImage0;
	Bitmap musicBoxSwitchMImage90;
	Bitmap musicBoxSwitchMImage180;
	Bitmap musicBoxSwitchMImage270;
	Bitmap musicBoxSwitchHImage0;
	Bitmap musicBoxSwitchHImage90;
	Bitmap musicBoxSwitchHImage180;
	Bitmap musicBoxSwitchHImage270;

	View buttonLayout0;
	View buttonLayout90;
	View buttonLayout180;
	View buttonLayout270;

	OrientationEventListener mOrientationListener;


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


		guitarAcousticIcon0 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_acoustic_guitar0);
		guitarAcousticIcon90 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_acoustic_guitar90);
		guitarAcousticIcon180 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_acoustic_guitar180);
		guitarAcousticIcon270 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_acoustic_guitar);

		guitarElecIcon0 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_electric_guitar0);
		guitarElecIcon90 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_electric_guitar90);
		guitarElecIcon180 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_electric_guitar180);
		guitarElecIcon270 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_electric_guitar);

		pianoIcon0 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_piano0);
		pianoIcon90 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_piano90);
		pianoIcon180 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_piano180);
		pianoIcon270 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_piano);

		musicBoxIcon0 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_music_box0);
		musicBoxIcon90 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_music_box90);
		musicBoxIcon180 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_music_box180);
		musicBoxIcon270 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_music_box);

		guitarSwitchAcousticImage0 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_guitar_a0);
		guitarSwitchAcousticImage90 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_guitar_a90);
		guitarSwitchAcousticImage180 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_guitar_a180);
		guitarSwitchAcousticImage270 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_guitar_a);

		guitarSwitchElecImage0 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_guitar_e0);
		guitarSwitchElecImage90 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_guitar_e90);
		guitarSwitchElecImage180 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_guitar_e180);
		guitarSwitchElecImage270 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_guitar_e);

		pianoSwitchLImage0 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_piano_l0);
		pianoSwitchLImage90 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_piano_l90);
		pianoSwitchLImage180 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_piano_l180);
		pianoSwitchLImage270 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_piano_l);

		pianoSwitchMImage0 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_piano_m0);
		pianoSwitchMImage90 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_piano_m90);
		pianoSwitchMImage180 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_piano_m180);
		pianoSwitchMImage270 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_piano_m);

		pianoSwitchHImage0 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_piano_h0);
		pianoSwitchHImage90 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_piano_h90);
		pianoSwitchHImage180 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_piano_h180);
		pianoSwitchHImage270 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_piano_h);

		musicBoxSwitchLImage0 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_mb_l0);
		musicBoxSwitchLImage90 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_mb_l90);
		musicBoxSwitchLImage180 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_mb_l180);
		musicBoxSwitchLImage270 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_mb_l);

		musicBoxSwitchMImage0 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_mb_m0);
		musicBoxSwitchMImage90 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_mb_m90);
		musicBoxSwitchMImage180 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_mb_m180);
		musicBoxSwitchMImage270 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_mb_m);

		musicBoxSwitchHImage0 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_mb_h0);
		musicBoxSwitchHImage90 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_mb_h90);
		musicBoxSwitchHImage180 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_mb_h180);
		musicBoxSwitchHImage270 = BitmapFactory.decodeResource(getResources(), R.drawable.switch_mb_h);

		mOrientationListener = new OrientationEventListener(this,
				SensorManager.SENSOR_DELAY_NORMAL) {

			@Override
			public void onOrientationChanged(int orientation) {
				int margin = 10;
				//Log.v(TAG, "Orientation changed to " + orientation);

				CameraOrientation newOrientation = null;

				if((orientation >= 0 && orientation <= 10) || (orientation >= 350 && orientation <= 360)){
					newOrientation = CameraOrientation.Portlait0;

				}else if(orientation >= 80 && orientation <= 100){
					newOrientation = CameraOrientation.Landscape90;

				}else if(orientation >= 170 && orientation <= 190){
					newOrientation = CameraOrientation.Portrailt180;

				}else if(orientation >= 260 && orientation <= 290){
					newOrientation = CameraOrientation.Landscape270;

				}

				if(newOrientation != null){
					if(currentOrientation != newOrientation){
						currentOrientation = newOrientation;
						Log.v(TAG,
								"New screen orientation: " + currentOrientation);

						buttonLayout0.setVisibility(View.INVISIBLE);
						buttonLayout90.setVisibility(View.INVISIBLE);
						buttonLayout180.setVisibility(View.INVISIBLE);
						buttonLayout270.setVisibility(View.INVISIBLE);

						if(currentOrientation == CameraOrientation.Portlait0){
							buttonLayout0.setVisibility(View.VISIBLE);

							if (instrumentType == INSTRUMENT_TYPE_PIANO) {
								currentInstrumentIcon0.setVisibility(View.INVISIBLE);
								currentInstrumentIcon0_Piano.setVisibility(View.VISIBLE);
								currentInstrumentIcon0_MusicBox.setVisibility(View.INVISIBLE);
							}else if (instrumentType == INSTRUMENT_TYPE_MUSIC_BOX) {
								currentInstrumentIcon0.setVisibility(View.INVISIBLE);
								currentInstrumentIcon0_Piano.setVisibility(View.INVISIBLE);
								currentInstrumentIcon0_MusicBox.setVisibility(View.VISIBLE);
							}else{
								currentInstrumentIcon0.setVisibility(View.VISIBLE);
								currentInstrumentIcon0_Piano.setVisibility(View.INVISIBLE);
								currentInstrumentIcon0_MusicBox.setVisibility(View.INVISIBLE);
							}
						}else if(currentOrientation == CameraOrientation.Landscape90){
							buttonLayout90.setVisibility(View.VISIBLE);

						}else if(currentOrientation == CameraOrientation.Portrailt180){
							buttonLayout180.setVisibility(View.VISIBLE);

							if (instrumentType == INSTRUMENT_TYPE_PIANO) {
								currentInstrumentIcon180.setVisibility(View.INVISIBLE);
								currentInstrumentIcon180_Piano.setVisibility(View.VISIBLE);
								currentInstrumentIcon180_MusicBox.setVisibility(View.INVISIBLE);
							}else if (instrumentType == INSTRUMENT_TYPE_MUSIC_BOX) {
								currentInstrumentIcon180.setVisibility(View.INVISIBLE);
								currentInstrumentIcon180_Piano.setVisibility(View.INVISIBLE);
								currentInstrumentIcon180_MusicBox.setVisibility(View.VISIBLE);
							}else{
								currentInstrumentIcon180.setVisibility(View.VISIBLE);
								currentInstrumentIcon180_Piano.setVisibility(View.INVISIBLE);
								currentInstrumentIcon180_MusicBox.setVisibility(View.INVISIBLE);
							}
						}else if(currentOrientation == CameraOrientation.Landscape270){
							buttonLayout270.setVisibility(View.VISIBLE);

						}else{

						}


					}
				}



			}
		};

		if (mOrientationListener.canDetectOrientation() == true) {
			Log.v(TAG, "Can detect orientation");
			mOrientationListener.enable();
		} else {
			Log.v(TAG, "Cannot detect orientation");
			mOrientationListener.disable();
		}

		currentOrientation = CameraOrientation.Landscape270;

		buttonLayout0 = findViewById(R.id.buttons_portlait0);
		buttonLayout90 = findViewById(R.id.buttons_landscape90);
		buttonLayout180 = findViewById(R.id.buttons_portlait180);
		buttonLayout270 = findViewById(R.id.buttons_landscape270);

		buttonLayout0.setVisibility(View.VISIBLE);
		buttonLayout90.setVisibility(View.INVISIBLE);
		buttonLayout180.setVisibility(View.INVISIBLE);
		buttonLayout270.setVisibility(View.INVISIBLE);

		cameraSwapButton0 = (ImageButton) findViewById(R.id.rear_front_switch_portlait0);
		currentInstrumentIcon0 = (ImageButton) findViewById(R.id.current_instrument_icon_portlait0);
		currentInstrumentIcon0_Piano = (ImageButton) findViewById(R.id.current_instrument_icon_portlait0_piano);
		currentInstrumentIcon0_MusicBox = (ImageButton) findViewById(R.id.current_instrument_icon_portlait0_mb);
		infoButton0 = (ImageButton) findViewById(R.id.info_icon_portlait0);
		guitarSwitch0 = (ImageButton) findViewById(R.id.guitar_switch_portlait0);
		octaveSwitch0 = (ImageButton) findViewById(R.id.octave_switch_portlait0);

		cameraSwapButton90 = (ImageButton) findViewById(R.id.rear_front_switch_landscape90);
		currentInstrumentIcon90 = (ImageButton) findViewById(R.id.current_instrument_icon_landscape90);
		infoButton90 = (ImageButton) findViewById(R.id.info_icon_landscape90);
		guitarSwitch90 = (ImageButton) findViewById(R.id.guitar_switch_landscape90);
		octaveSwitch90 = (ImageButton) findViewById(R.id.octave_switch_landscape90);

		cameraSwapButton180 = (ImageButton) findViewById(R.id.rear_front_switch_portlait180);
		currentInstrumentIcon180 = (ImageButton) findViewById(R.id.current_instrument_icon_portlait180);
		currentInstrumentIcon180_Piano = (ImageButton) findViewById(R.id.current_instrument_icon_portlait180_piano);
		currentInstrumentIcon180_MusicBox = (ImageButton) findViewById(R.id.current_instrument_icon_portlait180_mb);
		infoButton180 = (ImageButton) findViewById(R.id.info_icon_portlait180);
		guitarSwitch180 = (ImageButton) findViewById(R.id.guitar_switch_portlait180);
		octaveSwitch180 = (ImageButton) findViewById(R.id.octave_switch_portlait180);

		cameraSwapButton270 = (ImageButton) findViewById(R.id.rear_front_switch_landscape270);
		currentInstrumentIcon270 = (ImageButton) findViewById(R.id.current_instrument_icon_landscape270);
		infoButton270 = (ImageButton) findViewById(R.id.info_icon_landscape270);
		guitarSwitch270 = (ImageButton) findViewById(R.id.guitar_switch_landscape270);
		octaveSwitch270 = (ImageButton) findViewById(R.id.octave_switch_landscape270);



		configureButton();


	}

	private void configureButton(){


		//currentInstrumentIcon = (ImageButton) findViewById(R.id.current_instrument_icon);
		//infoButton = (ImageButton) findViewById(R.id.info_icon);
		//guitarSwitch = (ImageButton) findViewById(R.id.guitar_switch);
		//octaveSwitch = (ImageButton) findViewById(R.id.octave_switch);

		if (instrumentType == INSTRUMENT_TYPE_GUITAR) {
			guitarSwitch0.setVisibility(View.VISIBLE);
			guitarSwitch90.setVisibility(View.VISIBLE);
			guitarSwitch180.setVisibility(View.VISIBLE);
			guitarSwitch270.setVisibility(View.VISIBLE);

			octaveSwitch0.setVisibility(View.INVISIBLE);
			octaveSwitch90.setVisibility(View.INVISIBLE);
			octaveSwitch180.setVisibility(View.INVISIBLE);
			octaveSwitch270.setVisibility(View.INVISIBLE);

			if (currentOctave == 0) {
				currentInstrumentIcon0.setImageBitmap(guitarAcousticIcon0);
				currentInstrumentIcon90.setImageBitmap(guitarAcousticIcon90);
				currentInstrumentIcon180.setImageBitmap(guitarAcousticIcon180);
				currentInstrumentIcon270.setImageBitmap(guitarAcousticIcon270);
				guitarSwitch0.setImageBitmap(guitarSwitchAcousticImage0);
				guitarSwitch90.setImageBitmap(guitarSwitchAcousticImage90);
				guitarSwitch180.setImageBitmap(guitarSwitchAcousticImage180);
				guitarSwitch270.setImageBitmap(guitarSwitchAcousticImage270);
			} else {
				currentInstrumentIcon0.setImageBitmap(guitarElecIcon0);
				currentInstrumentIcon90.setImageBitmap(guitarElecIcon90);
				currentInstrumentIcon180.setImageBitmap(guitarElecIcon180);
				currentInstrumentIcon270.setImageBitmap(guitarElecIcon270);
				guitarSwitch0.setImageBitmap(guitarSwitchElecImage0);
				guitarSwitch90.setImageBitmap(guitarSwitchElecImage90);
				guitarSwitch180.setImageBitmap(guitarSwitchElecImage180);
				guitarSwitch270.setImageBitmap(guitarSwitchElecImage270);
			}

			currentInstrumentIcon0.setVisibility(View.VISIBLE);
			currentInstrumentIcon0_Piano.setVisibility(View.INVISIBLE);
			currentInstrumentIcon0_MusicBox.setVisibility(View.INVISIBLE);
			currentInstrumentIcon180.setVisibility(View.VISIBLE);
			currentInstrumentIcon180_Piano.setVisibility(View.INVISIBLE);
			currentInstrumentIcon180_MusicBox.setVisibility(View.INVISIBLE);

		} else if (instrumentType == INSTRUMENT_TYPE_PIANO) {
			currentInstrumentIcon0_Piano.setImageBitmap(pianoIcon0);
			currentInstrumentIcon90.setImageBitmap(pianoIcon90);
			currentInstrumentIcon180_Piano.setImageBitmap(pianoIcon180);
			currentInstrumentIcon270.setImageBitmap(pianoIcon270);
			guitarSwitch0.setVisibility(View.INVISIBLE);
			guitarSwitch90.setVisibility(View.INVISIBLE);
			guitarSwitch180.setVisibility(View.INVISIBLE);
			guitarSwitch270.setVisibility(View.INVISIBLE);
			octaveSwitch0.setVisibility(View.VISIBLE);
			octaveSwitch90.setVisibility(View.VISIBLE);
			octaveSwitch180.setVisibility(View.VISIBLE);
			octaveSwitch270.setVisibility(View.VISIBLE);

			if (currentOctave == 0) {
				octaveSwitch0.setImageBitmap(pianoSwitchLImage0);
				octaveSwitch90.setImageBitmap(pianoSwitchLImage90);
				octaveSwitch180.setImageBitmap(pianoSwitchLImage180);
				octaveSwitch270.setImageBitmap(pianoSwitchLImage270);
			} else if (currentOctave == 1) {
				octaveSwitch0.setImageBitmap(pianoSwitchMImage0);
				octaveSwitch90.setImageBitmap(pianoSwitchMImage90);
				octaveSwitch180.setImageBitmap(pianoSwitchMImage180);
				octaveSwitch270.setImageBitmap(pianoSwitchMImage270);
			} else {
				octaveSwitch0.setImageBitmap(pianoSwitchHImage0);
				octaveSwitch90.setImageBitmap(pianoSwitchHImage90);
				octaveSwitch180.setImageBitmap(pianoSwitchHImage180);
				octaveSwitch270.setImageBitmap(pianoSwitchHImage270);
			}

			currentInstrumentIcon0.setVisibility(View.INVISIBLE);
			currentInstrumentIcon0_Piano.setVisibility(View.VISIBLE);
			currentInstrumentIcon0_MusicBox.setVisibility(View.INVISIBLE);
			currentInstrumentIcon180.setVisibility(View.INVISIBLE);
			currentInstrumentIcon180_Piano.setVisibility(View.VISIBLE);
			currentInstrumentIcon180_MusicBox.setVisibility(View.INVISIBLE);

		} else if (instrumentType == INSTRUMENT_TYPE_MUSIC_BOX) {
			currentInstrumentIcon0_MusicBox.setImageBitmap(musicBoxIcon0);
			currentInstrumentIcon90.setImageBitmap(musicBoxIcon90);
			currentInstrumentIcon180_MusicBox.setImageBitmap(musicBoxIcon180);
			currentInstrumentIcon270.setImageBitmap(musicBoxIcon270);
			guitarSwitch0.setVisibility(View.INVISIBLE);
			guitarSwitch90.setVisibility(View.INVISIBLE);
			guitarSwitch180.setVisibility(View.INVISIBLE);
			guitarSwitch270.setVisibility(View.INVISIBLE);

			octaveSwitch0.setVisibility(View.VISIBLE);
			octaveSwitch90.setVisibility(View.VISIBLE);
			octaveSwitch180.setVisibility(View.VISIBLE);
			octaveSwitch270.setVisibility(View.VISIBLE);

			if (currentOctave == 0) {
				octaveSwitch0.setImageBitmap(musicBoxSwitchLImage0);
				octaveSwitch90.setImageBitmap(musicBoxSwitchLImage90);
				octaveSwitch180.setImageBitmap(musicBoxSwitchLImage180);
				octaveSwitch270.setImageBitmap(musicBoxSwitchLImage270);
			} else if (currentOctave == 1) {
				octaveSwitch0.setImageBitmap(musicBoxSwitchMImage0);
				octaveSwitch90.setImageBitmap(musicBoxSwitchMImage90);
				octaveSwitch180.setImageBitmap(musicBoxSwitchMImage180);
				octaveSwitch270.setImageBitmap(musicBoxSwitchMImage270);
			} else {
				octaveSwitch0.setImageBitmap(musicBoxSwitchHImage0);
				octaveSwitch90.setImageBitmap(musicBoxSwitchHImage90);
				octaveSwitch180.setImageBitmap(musicBoxSwitchHImage180);
				octaveSwitch270.setImageBitmap(musicBoxSwitchHImage270);
			}

			currentInstrumentIcon0.setVisibility(View.INVISIBLE);
			currentInstrumentIcon0_Piano.setVisibility(View.INVISIBLE);
			currentInstrumentIcon0_MusicBox.setVisibility(View.VISIBLE);
			currentInstrumentIcon180.setVisibility(View.INVISIBLE);
			currentInstrumentIcon180_Piano.setVisibility(View.INVISIBLE);
			currentInstrumentIcon180_MusicBox.setVisibility(View.VISIBLE);

		} else {
			currentInstrumentIcon0.setVisibility(View.INVISIBLE);
			currentInstrumentIcon0_Piano.setVisibility(View.INVISIBLE);
			currentInstrumentIcon0_MusicBox.setVisibility(View.INVISIBLE);
			currentInstrumentIcon90.setVisibility(View.INVISIBLE);
			currentInstrumentIcon180.setVisibility(View.INVISIBLE);
			currentInstrumentIcon180_Piano.setVisibility(View.INVISIBLE);
			currentInstrumentIcon180_MusicBox.setVisibility(View.INVISIBLE);
			currentInstrumentIcon270.setVisibility(View.INVISIBLE);
			guitarSwitch0.setVisibility(View.INVISIBLE);
			guitarSwitch90.setVisibility(View.INVISIBLE);
			guitarSwitch180.setVisibility(View.INVISIBLE);
			guitarSwitch270.setVisibility(View.INVISIBLE);
			octaveSwitch0.setVisibility(View.INVISIBLE);
			octaveSwitch90.setVisibility(View.INVISIBLE);
			octaveSwitch180.setVisibility(View.INVISIBLE);
			octaveSwitch270.setVisibility(View.INVISIBLE);
		}

		//cameraSwapButton = (ImageButton) findViewById(R.id.rear_front_switch);
		cameraSwapButton0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CaptureCameraPreview cameraPreview = getCameraPreview();
				cameraPreview.swapCamera();

			}
		});
		cameraSwapButton90.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CaptureCameraPreview cameraPreview = getCameraPreview();
				cameraPreview.swapCamera();

			}
		});
		cameraSwapButton180.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CaptureCameraPreview cameraPreview = getCameraPreview();
				cameraPreview.swapCamera();

			}
		});
		cameraSwapButton270.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CaptureCameraPreview cameraPreview = getCameraPreview();
				cameraPreview.swapCamera();

			}
		});

		guitarSwitch0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				guitarSwitchAction();
			}
		});
		guitarSwitch90.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				guitarSwitchAction();
			}
		});
		guitarSwitch180.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				guitarSwitchAction();
			}
		});
		guitarSwitch270.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				guitarSwitchAction();
			}
		});

		octaveSwitch0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				octaveSwitchAction();
			}
		});
		octaveSwitch90.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				octaveSwitchAction();
			}
		});
		octaveSwitch180.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				octaveSwitchAction();
			}
		});
		octaveSwitch270.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				octaveSwitchAction();
			}
		});

		infoButton0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		infoButton90.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		infoButton180.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		infoButton270.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void guitarSwitchAction(){
		if (currentOctave == 0) {
			currentOctave = 1;
			currentInstrumentIcon0.setImageBitmap(guitarElecIcon0);
			currentInstrumentIcon90.setImageBitmap(guitarElecIcon90);
			currentInstrumentIcon180.setImageBitmap(guitarElecIcon180);
			currentInstrumentIcon270.setImageBitmap(guitarElecIcon270);
			guitarSwitch0.setImageBitmap(guitarSwitchElecImage0);
			guitarSwitch90.setImageBitmap(guitarSwitchElecImage90);
			guitarSwitch180.setImageBitmap(guitarSwitchElecImage180);
			guitarSwitch270.setImageBitmap(guitarSwitchElecImage270);
		} else {
			currentOctave = 0;
			currentInstrumentIcon0.setImageBitmap(guitarAcousticIcon0);
			currentInstrumentIcon90.setImageBitmap(guitarAcousticIcon90);
			currentInstrumentIcon180.setImageBitmap(guitarAcousticIcon180);
			currentInstrumentIcon270.setImageBitmap(guitarAcousticIcon270);
			guitarSwitch0.setImageBitmap(guitarSwitchAcousticImage0);
			guitarSwitch90.setImageBitmap(guitarSwitchAcousticImage90);
			guitarSwitch180.setImageBitmap(guitarSwitchAcousticImage180);
			guitarSwitch270.setImageBitmap(guitarSwitchAcousticImage270);
		}
	}

	private void octaveSwitchAction(){
		if (instrumentType == INSTRUMENT_TYPE_PIANO) {
			if (currentOctave == 0) {
				currentOctave = 1;
				octaveSwitch0.setImageBitmap(pianoSwitchMImage0);
				octaveSwitch90.setImageBitmap(pianoSwitchMImage90);
				octaveSwitch180.setImageBitmap(pianoSwitchMImage180);
				octaveSwitch270.setImageBitmap(pianoSwitchMImage270);
			} else if (currentOctave == 1) {
				currentOctave = 2;
				octaveSwitch0.setImageBitmap(pianoSwitchHImage0);
				octaveSwitch90.setImageBitmap(pianoSwitchHImage90);
				octaveSwitch180.setImageBitmap(pianoSwitchHImage180);
				octaveSwitch270.setImageBitmap(pianoSwitchHImage270);
			} else {
				currentOctave = 0;
				octaveSwitch0.setImageBitmap(pianoSwitchLImage0);
				octaveSwitch90.setImageBitmap(pianoSwitchLImage90);
				octaveSwitch180.setImageBitmap(pianoSwitchLImage180);
				octaveSwitch270.setImageBitmap(pianoSwitchLImage270);
			}
		} else if (instrumentType == INSTRUMENT_TYPE_MUSIC_BOX) {
			if (currentOctave == 0) {
				currentOctave = 1;
				octaveSwitch0.setImageBitmap(musicBoxSwitchMImage0);
				octaveSwitch90.setImageBitmap(musicBoxSwitchMImage90);
				octaveSwitch180.setImageBitmap(musicBoxSwitchMImage180);
				octaveSwitch270.setImageBitmap(musicBoxSwitchMImage270);
			} else if (currentOctave == 1) {
				currentOctave = 2;
				octaveSwitch0.setImageBitmap(musicBoxSwitchHImage0);
				octaveSwitch90.setImageBitmap(musicBoxSwitchHImage90);
				octaveSwitch180.setImageBitmap(musicBoxSwitchHImage180);
				octaveSwitch270.setImageBitmap(musicBoxSwitchHImage270);
			} else {
				currentOctave = 0;
				octaveSwitch0.setImageBitmap(musicBoxSwitchLImage0);
				octaveSwitch90.setImageBitmap(musicBoxSwitchLImage90);
				octaveSwitch180.setImageBitmap(musicBoxSwitchLImage180);
				octaveSwitch270.setImageBitmap(musicBoxSwitchLImage270);
			}
		}
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mOrientationListener.disable();
	}

	public native void cBegin(String[] soundPathArray, int isMonoPhone);

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

		int isMonoPhonic = 0;
		if (instrumentType == INSTRUMENT_TYPE_GUITAR) {
			isMonoPhonic = 1;
		}

		cBegin(filePathes, isMonoPhonic);

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

		long time= System.currentTimeMillis();
		long minTimeBetweenSounds = 200;

		// don't play sounds too close together (to avoid stutter)
		if (time > (lastTimeGuitarSoundPlayed + minTimeBetweenSounds)) {

			prepareGuitarMarkerStates();
			int currentSoundId = guitarMarkerStates.getCurrentSoundId();

			Log.d(TAG, "playCurrentSound: currentSoundId=" + currentSoundId);

			if (currentSoundId >= 0) {
				cPlaySound(currentSoundId + getCurrentOffset());
			} else {
				// ギターの開放弦
				cPlaySound(8 + getCurrentOffset());
			}

			lastTimeGuitarSoundPlayed = time;
		} else
		{
			Log.d(TAG, "TOO SOO!");
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

