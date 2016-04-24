package com.goldrushcomputing.playsound;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.rendering.ARRenderer;
import org.fmod.FMODAudioDevice;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("JniMissingFunction")
public class Example extends ARActivity {
	static String TAG = "Example";

	static {
		System.loadLibrary("fmodex");
		System.loadLibrary("main");
	}

	private boolean isPlayerReady = false;
	private boolean isPlaying = false;

	private String soundFile0 = "bass.wav";
	private String soundFile1 = "hat.wav";
	private String soundFile2 = "snaredrum.wav";
	private String soundFile3 = "bosa.wav";

	private FMODAudioDevice mFMODAudioDevice = new FMODAudioDevice();

	private Handler mUpdateHandler = new Handler() {
		public void handleMessage(Message msg) {
			cUpdate();

			int position = cGetPosition();
			int length = cGetLength();
			int channels = cGetChannelsPlaying();

			((TextView) findViewById(R.id.txtState)).setText(cGetPlaying() ? "Playing" : "Stopped");
			((TextView) findViewById(R.id.txtPos)).setText(String.format("%02d:%02d:%02d / %02d:%02d:%02d", position / 1000 / 60, position / 1000 % 60, position / 10 % 100, length / 1000 / 60, length / 1000 % 60, length / 10 % 100));
			((TextView) findViewById(R.id.txtChans)).setText(String.format("%d", channels));

			removeMessages(0);
			sendMessageDelayed(obtainMessage(0), 50);
		}
	};

	/**
	 * The FrameLayout where the AR view is displayed.
	 */
	private FrameLayout mainLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	public void onStart() {
		super.onStart();
		mFMODAudioDevice.start();
		//cBegin();

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
		isPlayerReady = true;
		mFMODAudioDevice.start();

		//String directoryPath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + this.getPackageName() + "/track";

		String directoryPath = getTrackDirectory();

		//directoryPath = directoryPath.replace("/0/", "/legacy/");

		String path1 = directoryPath + "/" + soundFile0;
		String path2 = directoryPath + "/" + soundFile1;
		String path3 = directoryPath + "/" + soundFile2;
		String path4 = directoryPath + "/" + soundFile3;

		//path1 = path1.substring(path1.indexOf("/sdcard"));
		//path2 = path2.substring(path2.indexOf("/sdcard"));
		//path3 = path3.substring(path3.indexOf("/sdcard"));
		//path4 = path4.substring(path4.indexOf("/sdcard"));

		Log.d(TAG, path1);
		Log.d(TAG, path2);
		Log.d(TAG, path3);
		Log.d(TAG, path4);

		//path1 = "/sdcard/bosa.wav";
		//path2 = "/sdcard/bass.wav";
		//path3 = "/sdcard/hat.wav";
		//path4 = "/sdcard/snaredrum.wav";

		List<String> list = new ArrayList<String>();
		//add some stuff
		list.add(path1);
		list.add(path2);
		list.add(path3);
		list.add(path4);

		String[] stringArray = list.toArray(new String[0]);


		cBegin(stringArray);
		//cBegin();

		mUpdateHandler.sendMessageDelayed(mUpdateHandler.obtainMessage(0), 0);
	}

	public String getTrackDirectory() {
		return this.getCacheDir().getAbsolutePath() + "/Music";
	}

	public void endPlayer() {
		isPlayerReady = false;
		cEnd();
		mFMODAudioDevice.stop();
	}

	public void playSound1(View view) {
		cPlaySound(0);
	}

	public void playSound2(View view) {
		cPlaySound(1);
	}

	public void playSound3(View view) {
		cPlaySound(2);
	}

	public void playSound4(View view) {
		cPlaySound(3);
	}

	public void playSound(int trackIndex) {
		cPlaySound(trackIndex);
		this.isPlaying = true;
	}

	/*
	public void playSoundFrom(int trackIndex, int position) {
		Log.d(TAG, "PlaySoundFrom " + position);
		cPlaySoundFrom(trackIndex, position);
		this.isPlaying = true;
	}
	*/

	public void seekSoundTo(int position) {
		//cSeekSoundTo(position);
	}

	/*
	public void pauseSound() {
		cPauseSound();
		this.isPlaying = false;
	}
	*/

	/*
	public void stopSound(int trackIndex) {
		cStopSound();
		this.isPlaying = false;
	}
	*/

	/*
	public void inspectBufferSize() {
		int size = cGetDSPBufferSize();
		Log.d(TAG, "DSP Buffer Size is " + size);
	}
	*/


	public void showLoadingPanel() {
		/*
		View view = (View) this.findViewById(R.id.loading_panel);
		view.setVisibility(View.VISIBLE);
		
		ImageView loadingMark = (ImageView) this.findViewById(R.id.loading_mark);
		Animation animationSlideIn = AnimationUtils.loadAnimation(this, R.anim.spinning);
		loadingMark.startAnimation(animationSlideIn);
		*/
	}

	public void hideLoadingPanel() {
		/*
		View view = (View) this.findViewById(R.id.loading_panel);
		view.setVisibility(View.GONE);
		
		ImageView loadingMark = (ImageView) this.findViewById(R.id.loading_mark);
		loadingMark.clearAnimation();
		*/
	}

	@Override
	protected ARRenderer supplyRenderer() {
		//return new DrumsRenderer(this);
		//return new PianoRenderer(this);
		return new MusicBoxRenderer(this);
	}

	/**
	 * Use the FrameLayout in this Activity's UI.
	 */
	@Override
	protected FrameLayout supplyFrameLayout() {
		return (FrameLayout) this.findViewById(R.id.mainLayout);
	}
}
