package com.vuforia.samples.VuforiaSamples.app.VirtualButtons;

import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.vuforia.CameraDevice;
import com.vuforia.DataSet;
import com.vuforia.ImageTarget;
import com.vuforia.ObjectTracker;
import com.vuforia.Rectangle;
import com.vuforia.State;
import com.vuforia.STORAGE_TYPE;
import com.vuforia.Trackable;
import com.vuforia.Tracker;
import com.vuforia.TrackerManager;
import com.vuforia.VirtualButton;
import com.vuforia.Vuforia;
import com.vuforia.samples.SampleApplication.SampleApplicationControl;
import com.vuforia.samples.SampleApplication.SampleApplicationException;
import com.vuforia.samples.SampleApplication.SampleApplicationSession;
import com.vuforia.samples.SampleApplication.utils.LoadingDialogHandler;
import com.vuforia.samples.SampleApplication.utils.SampleApplicationGLView;
import com.vuforia.samples.SampleApplication.utils.Texture;
import com.vuforia.samples.VuforiaSamples.R;
import com.vuforia.samples.VuforiaSamples.ui.SampleAppMenu.SampleAppMenu;
import com.vuforia.samples.VuforiaSamples.ui.SampleAppMenu.SampleAppMenuGroup;
import com.vuforia.samples.VuforiaSamples.ui.SampleAppMenu.SampleAppMenuInterface;


// The main activity for the VirtualButtons sample. 
public class VirtualButtonActivity extends Activity implements
		SampleApplicationControl, SampleAppMenuInterface {
	private static final String LOGTAG = "VirtualButtons";

	SampleApplicationSession vuforiaAppSession;

	// Our OpenGL view:
	private SampleApplicationGLView mGlView;

	// Our renderer:
	private VirtualButtonRenderer mRenderer;

	private RelativeLayout mUILayout;

	private GestureDetector mGestureDetector;

	private SampleAppMenu mSampleAppMenu;

	private LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(
			this);

	// The textures we will use for rendering:
	private Vector<Texture> mTextures;

	private DataSet dataSet = null;

	// Virtual Button runtime creation:
	private boolean updateBtns = false;
	public String virtualButtonColors[] = {"red", "blue", "yellow", "green"};

	// Enumeration for masking button indices into single integer:
	private static final int BUTTON_1 = 1;
	private static final int BUTTON_2 = 2;
	private static final int BUTTON_3 = 4;
	private static final int BUTTON_4 = 8;

	private byte buttonMask = 0;
	static final int NUM_BUTTONS = 4;

	// Alert Dialog used to display SDK errors
	private AlertDialog mErrorDialog;

	boolean mIsDroidDevice = false;


	// Called when the activity first starts or the user navigates back to an
	// activity.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(LOGTAG, "onCreate");
		super.onCreate(savedInstanceState);

		vuforiaAppSession = new SampleApplicationSession(this);

		startLoadingAnimation();

		vuforiaAppSession
				.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Load any sample specific textures:
		mTextures = new Vector<>();
		loadTextures();

		mGestureDetector = new GestureDetector(this, new GestureListener());

		mIsDroidDevice = android.os.Build.MODEL.toLowerCase().startsWith("droid");
	}

	// Process Single Tap event to trigger autofocus
	private class GestureListener extends
			GestureDetector.SimpleOnGestureListener {
		// Used to set autofocus one second after a manual focus is triggered
		private final Handler autofocusHandler = new Handler();


		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}


		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// Generates a Handler to trigger autofocus
			// after 1 second
			autofocusHandler.postDelayed(new Runnable() {
				public void run() {
					boolean result = CameraDevice.getInstance().setFocusMode(
							CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);

					if (!result)
						Log.e("SingleTapUp", "Unable to trigger focus");
				}
			}, 1000L);

			return true;
		}
	}


	// We want to load specific textures from the APK, which we will later use
	// for rendering.
	private void loadTextures() {
		mTextures.add(Texture.loadTextureFromApk("TextureTeapotBrass.png",
				getAssets()));
		mTextures.add(Texture.loadTextureFromApk("TextureTeapotRed.png",
				getAssets()));
		mTextures.add(Texture.loadTextureFromApk("TextureTeapotBlue.png",
				getAssets()));
		mTextures.add(Texture.loadTextureFromApk(
				"VirtualButtons/TextureTeapotYellow.png", getAssets()));
		mTextures.add(Texture.loadTextureFromApk(
				"VirtualButtons/TextureTeapotGreen.png", getAssets()));
	}


	// Called when the activity will start interacting with the user.
	@Override
	protected void onResume() {
		Log.d(LOGTAG, "onResume");
		super.onResume();

		// This is needed for some Droid devices to force portrait
		if (mIsDroidDevice) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		try {
			vuforiaAppSession.resumeAR();
		} catch (SampleApplicationException e) {
			Log.e(LOGTAG, e.getString());
		}

		// Resume the GL view:
		if (mGlView != null) {
			mGlView.setVisibility(View.VISIBLE);
			mGlView.onResume();
		}

	}


	@Override
	public void onConfigurationChanged(Configuration config) {
		Log.d(LOGTAG, "onConfigurationChanged");
		super.onConfigurationChanged(config);

		vuforiaAppSession.onConfigurationChanged();
	}


	// Called when the system is about to start resuming a previous activity.
	@Override
	protected void onPause() {
		Log.d(LOGTAG, "onPause");
		super.onPause();

		if (mGlView != null) {
			mGlView.setVisibility(View.INVISIBLE);
			mGlView.onPause();
		}

		try {
			vuforiaAppSession.pauseAR();
		} catch (SampleApplicationException e) {
			Log.e(LOGTAG, e.getString());
		}
	}


	// The final call you receive before your activity is destroyed.
	@Override
	protected void onDestroy() {
		Log.d(LOGTAG, "onDestroy");
		super.onDestroy();

		try {
			vuforiaAppSession.stopAR();
		} catch (SampleApplicationException e) {
			Log.e(LOGTAG, e.getString());
		}

		// Unload texture:
		mTextures.clear();
		mTextures = null;

		System.gc();
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Process the Gestures
		if (mSampleAppMenu != null && mSampleAppMenu.processEvent(event)) {
			return true;
		}

		return mGestureDetector.onTouchEvent(event);
	}


	private void startLoadingAnimation() {
		LayoutInflater inflater = LayoutInflater.from(this);
		mUILayout = (RelativeLayout) inflater.inflate(R.layout.camera_overlay, null, false);

		mUILayout.setVisibility(View.VISIBLE);
		mUILayout.setBackgroundColor(Color.BLACK);

		// Gets a reference to the loading dialog
		loadingDialogHandler.mLoadingDialogContainer = mUILayout.findViewById(R.id.loading_indicator);

		// Shows the loading indicator at start
		loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);

		// Adds the inflated layout to the view
		addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}


	// Initializes AR application components.
	private void initApplicationAR() {
		// Create OpenGL ES view:
		int depthSize = 16;
		int stencilSize = 0;
		boolean translucent = Vuforia.requiresAlpha();

		mGlView = new SampleApplicationGLView(this);
		mGlView.init(translucent, depthSize, stencilSize);

		mRenderer = new VirtualButtonRenderer(this, vuforiaAppSession);
		mRenderer.setTextures(mTextures);
		mGlView.setRenderer(mRenderer);
	}


	@Override
	public boolean doInitTrackers() {
		// Indicate if the trackers were initialized correctly
		boolean result = true;

		TrackerManager tManager = TrackerManager.getInstance();
		Tracker tracker;

		tracker = tManager.initTracker(ObjectTracker.getClassType());
		if (tracker == null) {
			Log.e(
					LOGTAG,
					"Tracker not initialized. Tracker already initialized or the camera is already started");
			result = false;
		} else {
			Log.i(LOGTAG, "Tracker successfully initialized");
		}

		return result;
	}


	@Override
	public boolean doStartTrackers() {
		// Indicate if the trackers were started correctly
		boolean result = true;

		Tracker objectTracker = TrackerManager.getInstance().getTracker(
				ObjectTracker.getClassType());
		if (objectTracker != null)
			objectTracker.start();

		return result;
	}


	@Override
	public boolean doStopTrackers() {
		// Indicate if the trackers were stopped correctly
		boolean result = true;

		Tracker objectTracker = TrackerManager.getInstance().getTracker(
				ObjectTracker.getClassType());
		if (objectTracker != null)
			objectTracker.stop();

		return result;
	}


	@Override
	public boolean doUnloadTrackersData() {
		// Indicate if the trackers were unloaded correctly
		boolean result = true;

		// Get the image tracker:
		TrackerManager trackerManager = TrackerManager.getInstance();
		ObjectTracker objectTracker = (ObjectTracker) trackerManager
				.getTracker(ObjectTracker.getClassType());
		if (objectTracker == null) {
			Log.d(LOGTAG,
					"Failed to destroy the tracking data set because the ObjectTracker has not been initialized.");
			return false;
		}

		if (dataSet != null) {
			if (!objectTracker.deactivateDataSet(dataSet)) {
				Log.d(LOGTAG,
						"Failed to destroy the tracking data set because the data set could not be deactivated.");
				result = false;
			} else if (!objectTracker.destroyDataSet(dataSet)) {
				Log.d(LOGTAG, "Failed to destroy the tracking data set.");
				result = false;
			}

			if (result)
				Log.d(LOGTAG, "Successfully destroyed the data set.");

			dataSet = null;
		}

		return result;
	}


	@Override
	public boolean doDeinitTrackers() {
		// Indicate if the trackers were deinitialized correctly
		boolean result = true;

		TrackerManager tManager = TrackerManager.getInstance();
		tManager.deinitTracker(ObjectTracker.getClassType());

		return result;
	}


	@Override
	public void onInitARDone(SampleApplicationException exception) {

		if (exception == null) {
			initApplicationAR();

			mRenderer.mIsActive = true;

			// Now add the GL surface view. It is important
			// that the OpenGL ES surface view gets added
			// BEFORE the camera is started and video
			// background is configured.
			addContentView(mGlView, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));

			// Sets the UILayout to be drawn in front of the camera
			mUILayout.bringToFront();

			// Hides the Loading Dialog
			loadingDialogHandler
					.sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);

			// Sets the layout background to transparent
			mUILayout.setBackgroundColor(Color.TRANSPARENT);

			try {
				vuforiaAppSession.startAR(CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_DEFAULT);
			} catch (SampleApplicationException e) {
				Log.e(LOGTAG, e.getString());
			}

			boolean result = CameraDevice.getInstance().setFocusMode(
					CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);

			if (!result)
				Log.e(LOGTAG, "Unable to enable continuous autofocus");

			mSampleAppMenu = new SampleAppMenu(this, this, "Virtual Buttons",
					mGlView, mUILayout, null);
			setSampleAppMenuSettings();

		} else {
			Log.e(LOGTAG, exception.getString());
			showInitializationErrorMessage(exception.getString());
		}
	}


	// Shows initialization error messages as System dialogs
	public void showInitializationErrorMessage(String message) {
		final String errorMessage = message;
		runOnUiThread(new Runnable() {
			public void run() {
				if (mErrorDialog != null) {
					mErrorDialog.dismiss();
				}

				// Generates an Alert Dialog to show the error message
				AlertDialog.Builder builder = new AlertDialog.Builder(
						VirtualButtonActivity.this);
				builder
						.setMessage(errorMessage)
						.setTitle(getString(R.string.INIT_ERROR))
						.setCancelable(false)
						.setIcon(0)
						.setPositiveButton(getString(R.string.button_OK),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										finish();
									}
								});

				mErrorDialog = builder.create();
				mErrorDialog.show();
			}
		});
	}


	@Override
	public void onVuforiaUpdate(State state) {
		if (updateBtns) {
			// Update runs in the tracking thread therefore it is guaranteed
			// that the tracker is
			// not doing anything at this point. => Reconfiguration is possible.

			ObjectTracker ot = (ObjectTracker) (TrackerManager.getInstance()
					.getTracker(ObjectTracker.getClassType()));
			assert (dataSet != null);

			// Deactivate the data set prior to reconfiguration:
			ot.deactivateDataSet(dataSet);

			assert (dataSet.getNumTrackables() > 0);
			Trackable trackable = dataSet.getTrackable(0);

			assert (trackable != null);
			assert (trackable.getType() == ObjectTracker.getClassType());
			ImageTarget imageTarget = (ImageTarget) (trackable);

			if ((buttonMask & BUTTON_1) != 0) {
				Log.d(LOGTAG, "Toggle Button 1");

				toggleVirtualButton(imageTarget, virtualButtonColors[0],
						-108.68f, -53.52f, -75.75f, -65.87f);

			}
			if ((buttonMask & BUTTON_2) != 0) {
				Log.d(LOGTAG, "Toggle Button 2");

				toggleVirtualButton(imageTarget, virtualButtonColors[1],
						-45.28f, -53.52f, -12.35f, -65.87f);
			}
			if ((buttonMask & BUTTON_3) != 0) {
				Log.d(LOGTAG, "Toggle Button 3");

				toggleVirtualButton(imageTarget, virtualButtonColors[2],
						14.82f, -53.52f, 47.75f, -65.87f);
			}
			if ((buttonMask & BUTTON_4) != 0) {
				Log.d(LOGTAG, "Toggle Button 4");

				toggleVirtualButton(imageTarget, virtualButtonColors[3],
						76.57f, -53.52f, 109.50f, -65.87f);
			}

			// Reactivate the data set:
			ot.activateDataSet(dataSet);

			buttonMask = 0;
			updateBtns = false;
		}
	}


	// Create/destroy a Virtual Button at runtime
	//
	// Note: This will NOT work if the tracker is active!
	boolean toggleVirtualButton(ImageTarget imageTarget, String name,
	                            float left, float top, float right, float bottom) {
		Log.d(LOGTAG, "toggleVirtualButton");

		boolean buttonToggleSuccess = false;

		VirtualButton virtualButton = imageTarget.getVirtualButton(name);
		if (virtualButton != null) {
			Log.d(LOGTAG, "Destroying Virtual Button> " + name);
			buttonToggleSuccess = imageTarget
					.destroyVirtualButton(virtualButton);
		} else {
			Log.d(LOGTAG, "Creating Virtual Button> " + name);
			Rectangle vbRectangle = new Rectangle(left, top, right, bottom);
			VirtualButton virtualButton2 = imageTarget.createVirtualButton(
					name, vbRectangle);

			if (virtualButton2 != null) {
				// This is just a showcase. The values used here a set by
				// default on Virtual Button creation
				virtualButton2.setEnabled(true);
				virtualButton2.setSensitivity(VirtualButton.SENSITIVITY.MEDIUM);
				buttonToggleSuccess = true;
			}
		}

		return buttonToggleSuccess;
	}


	private void addButtonToToggle(int virtualButtonIdx) {
		Log.d(LOGTAG, "addButtonToToggle");

		assert (virtualButtonIdx >= 0 && virtualButtonIdx < NUM_BUTTONS);

		switch (virtualButtonIdx) {
			case 0:
				buttonMask |= BUTTON_1;
				break;

			case 1:
				buttonMask |= BUTTON_2;
				break;

			case 2:
				buttonMask |= BUTTON_3;
				break;

			case 3:
				buttonMask |= BUTTON_4;
				break;
		}
		updateBtns = true;
	}


	@Override
	public boolean doLoadTrackersData() {
		// Get the image tracker:
		TrackerManager trackerManager = TrackerManager.getInstance();
		ObjectTracker objectTracker = (ObjectTracker) (trackerManager
				.getTracker(ObjectTracker.getClassType()));
		if (objectTracker == null) {
			Log.d(
					LOGTAG,
					"Failed to load tracking data set because the ObjectTracker has not been initialized.");
			return false;
		}

		// Create the data set:
		dataSet = objectTracker.createDataSet();
		if (dataSet == null) {
			Log.d(LOGTAG, "Failed to create a new tracking data.");
			return false;
		}

		// Load the data set:
		if (!dataSet.load("VirtualButtons/Wood.xml",
				STORAGE_TYPE.STORAGE_APPRESOURCE)) {
			Log.d(LOGTAG, "Failed to load data set.");
			return false;
		}

		// Activate the data set:
		if (!objectTracker.activateDataSet(dataSet)) {
			Log.d(LOGTAG, "Failed to activate data set.");
			return false;
		}

		Log.d(LOGTAG, "Successfully loaded and activated data set.");
		return true;
	}

	final public static int CMD_BACK = -1;
	final public static int CMD_BUTTON_RED = 1;
	final public static int CMD_BUTTON_BLUE = 2;
	final public static int CMD_BUTTON_YELLOW = 3;
	final public static int CMD_BUTTON_GREEN = 4;


	// This method sets the menu's settings
	private void setSampleAppMenuSettings() {
		SampleAppMenuGroup group;

		group = mSampleAppMenu.addGroup("", false);
		group.addTextItem(getString(R.string.menu_back), -1);

		group = mSampleAppMenu.addGroup("", true);
		group.addSelectionItem(getString(R.string.menu_button_red),
				CMD_BUTTON_RED, true);
		group.addSelectionItem(getString(R.string.menu_button_blue),
				CMD_BUTTON_BLUE, true);
		group.addSelectionItem(getString(R.string.menu_button_yellow),
				CMD_BUTTON_YELLOW, true);
		group.addSelectionItem(getString(R.string.menu_button_green),
				CMD_BUTTON_GREEN, true);

		mSampleAppMenu.attachMenu();
	}


	@Override
	public boolean menuProcess(int command) {
		switch (command) {
			case CMD_BACK:
				finish();
				break;

			case CMD_BUTTON_RED:
				addButtonToToggle(0);
				break;

			case CMD_BUTTON_BLUE:
				addButtonToToggle(1);
				break;

			case CMD_BUTTON_YELLOW:
				addButtonToToggle(2);
				break;

			case CMD_BUTTON_GREEN:
				addButtonToToggle(3);
				break;
		}
		return true;
	}
}
