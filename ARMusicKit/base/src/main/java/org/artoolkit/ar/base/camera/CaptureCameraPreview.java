/*
 *  CaptureCameraPreview.java
 *  ARToolKit5
 *
 *  This file is part of ARToolKit.
 *
 *  ARToolKit is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ARToolKit is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with ARToolKit.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  As a special exception, the copyright holders of this library give you
 *  permission to link this library with independent modules to produce an
 *  executable, regardless of the license terms of these independent modules, and to
 *  copy and distribute the resulting executable under terms of your choice,
 *  provided that you also meet, for each linked independent module, the terms and
 *  conditions of the license of that module. An independent module is a module
 *  which is neither derived from nor based on this library. If you modify this
 *  library, you may extend this exception to your version of the library, but you
 *  are not obligated to do so. If you do not wish to do so, delete this exception
 *  statement from your version.
 *
 *  Copyright 2015 Daqri, LLC.
 *  Copyright 2011-2015 ARToolworks, Inc.
 *
 *  Author(s): Julian Looser, Philip Lamb
 *
 */

package org.artoolkit.ar.base.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.artoolkit.ar.base.FPSCounter;

import java.io.IOException;
import java.util.List;

//import java.util.List;

@SuppressLint("ViewConstructor")
public class CaptureCameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

	/**
	 * Android logging tag for this class.
	 */
	private static final String TAG = "CameraPreview";

	/**
	 * The Camera doing the capturing.
	 */
	private Camera camera = null;
	//private CameraWrapper cameraWrapper = null;

	/**
	 * The camera capture width in pixels.
	 */
	private int captureWidth;

	/**
	 * The camera capture height in pixels.
	 */
	private int captureHeight;

	/**
	 * The camera capture rate in frames per second.
	 */
	private int captureRate;

	/**
	 * Counter to monitor the actual rate at which frames are captured from the camera.
	 */
	private FPSCounter fpsCounter = new FPSCounter();

	/**
	 * Listener to inform of camera related events: start, frame, and stop.
	 */
	private CameraEventListener listener;

	private Activity mActivity;

	boolean isRearCamera;

	// IMPORATNT:Cemera.paramsのなかではプレビューは横長なので, previewWidth > previewHeightとなっている点に注意
	int previewWidth;
	int previewHeight;

	//int textureWidth;
	//int textureHeight;

	private CameraRotationInfo cameraRotationInfo = new CameraRotationInfo(0, false);

	boolean isPreviewRunning = false;

	public boolean isUsingFrontCamera() {
		return !isRearCamera;
	}

	public CameraRotationInfo getCameraRotationInfo() {
		return cameraRotationInfo;
	}

	/**
	 * Constructor takes a {@link CameraEventListener} which will be called on
	 * to handle camera related events.
	 *
	 * @param cel CameraEventListener to use. Can be null.
	 */
	@SuppressWarnings("deprecation")
	public CaptureCameraPreview(Activity activity, CameraEventListener cel, boolean isRear) {
		super(activity);
		mActivity = activity;

		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // Deprecated in API level 11. Still required for API levels <= 10.

		setCameraEventListener(cel);


		isRearCamera = isRear;
	}

	public void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();

		Log.i(TAG, ">> info orientation=" + info.orientation);
		Log.i(TAG, ">> display rotation=" + rotation);

		int degrees = 0;
		switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
		}

		Log.i(TAG, ">> display degree=" + degrees);

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			Log.i(TAG, ">> face front");

			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;  // compensate the mirror

			cameraRotationInfo = new CameraRotationInfo(result, true);

		} else {  // back-facing
			Log.i(TAG, ">> face back");

			result = (info.orientation - degrees + 360) % 360;

			cameraRotationInfo = new CameraRotationInfo(result, false);
		}

		Log.i(TAG, ">> final rot result=" + result);

		camera.setDisplayOrientation(result);
	}

	/**
	 * Sets the {@link CameraEventListener} which will be called on to handle camera
	 * related events.
	 *
	 * @param cel CameraEventListener to use. Can be null.
	 */
	public void setCameraEventListener(CameraEventListener cel) {
		listener = cel;
	}


	@SuppressLint("NewApi")
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		openCamera(holder, isRearCamera);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
		closeCamera();
	}


	@SuppressWarnings("deprecation") // setPreviewFrameRate, getPreviewFrameRate
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

		if (camera == null) {
			// Camera wasn't opened successfully?
			Log.e(TAG, "No camera in surfaceChanged");
			return;
		}

		Log.i(TAG, "Surfaced changed, setting up camera and starting preview");

		startPreviewOriginal(w, h);
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {

		if (listener != null) {
			listener.cameraPreviewFrame(data);
		}

		//cameraWrapper.frameReceived(data);
		camera.addCallbackBuffer(data); //..

		if (fpsCounter.frame()) {
			Log.i(TAG, "Camera capture FPS: " + fpsCounter.getFPS());
		}
	}

	public void setCameraDirection(boolean isRear) {
		closeCamera();

		if (listener != null) {
			listener.cameraPreviewWillRestart();
		}

		final SurfaceHolder holder = this.getHolder();
		if (holder != null) {
			isRearCamera = isRear;

			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					openCamera(holder, isRearCamera);
					//startPreviewFrontFace(previewWidth, previewHeight);

					final Handler handler2 = new Handler();
					handler2.postDelayed(new Runnable() {
						@Override
						public void run() {
							startPreviewOriginal(previewWidth, previewHeight);
						}
					}, 500);
				}
			}, 200);
		}
	}

	public void swapCamera() {
		setCameraDirection(!isRearCamera);
	}

	public void openCamera(SurfaceHolder holder, boolean isRearCamera) {
		int cameraIndex;

		if (isRearCamera) {
			cameraIndex = getRearCameraId();
		} else {
			cameraIndex = getFrontCameraId();
		}

		Log.i(TAG, "Opening camera " + (cameraIndex));
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				camera = Camera.open(cameraIndex);
			} else {
				camera = Camera.open();
			}
		} catch (RuntimeException exception) {
			Log.e(TAG, "Cannot open camera. It may be in use by another process.");
			return;
		}

		Log.i(TAG, "Camera open");

		try {
			setCameraDisplayOrientation(cameraIndex, camera);
			camera.setPreviewDisplay(holder);
		} catch (IOException exception) {
			Log.e(TAG, "IOException setting display holder");
			camera.release();
			camera = null;
			Log.i(TAG, "Released camera");
		}
	}

	public void startPreviewOriginal(int width, int height) {
		if (camera == null) {
			// Camera wasn't opened successfully?
			Log.e(TAG, "No camera in surfaceChanged");
			return;
		}

		Log.i(TAG, "Surfaced changed, setting up camera and starting preview");

		if (isPreviewRunning) {
			Log.i(TAG, "Preview already running, not re-starting camera");
		} else {
			String camResolution = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pref_cameraResolution", "320x240");
			String[] dims = camResolution.split("x", 2);
			Camera.Parameters parameters = camera.getParameters();
			parameters.setPreviewSize(Integer.parseInt(dims[0]), Integer.parseInt(dims[1]));

			/* Insert new algo*/

			List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
			int previewWidth = width;
			int previewHeight = height;

			int minDiff = Integer.MAX_VALUE;

			for (int i = 0; i < sizes.size(); i++) {
				Camera.Size size = sizes.get(i);

				//Skip square
				if (size.width == size.height) {
					continue;
				}

				Log.d(TAG, "[PREVIEW] Supported Size #" + (i + 1) + ": " + size.width + "x" + size.height);

				int diff = Math.abs(size.height - height) + Math.abs(size.width - width);

				if (diff < minDiff) {
					previewWidth = size.width;
					previewHeight = size.height;
					minDiff = diff;
				}
			}

			//previewWidth = 320;
			//previewHeight = 240;

			// 強制的に解像度指定
			previewWidth = 640;
			previewHeight = 480;

			Log.d(TAG, "Set camera preview size to (" + previewWidth + "," + previewHeight + ")");

			this.previewWidth = previewWidth;
			this.previewHeight = previewHeight;

			/* Insert new algo*/

			parameters.setPreviewSize(previewWidth, previewHeight);

			int h = this.getHeight();

			double wDouble = (double) h * (double) previewWidth / (double) previewHeight;
			int w = (int) wDouble;

			//int w = this.getWidth();
			//double hDouble = (double)w * (double)previewWidth / (double)previewHeight;
			//int h = (int)hDouble;
			android.widget.FrameLayout.LayoutParams layoutParams = new android.widget.FrameLayout.LayoutParams(w, h);

			layoutParams.gravity = Gravity.CENTER_VERTICAL;

			Log.d(TAG, "surfaceView to (" + w + "," + h + ")");
			//this.setLayoutParams(layoutParams);
			if (listener != null) {
				listener.cameraPreviewSizeDetected(w, h);
			}

			List<String> focusModes = parameters.getSupportedFocusModes();
			for (String mode : focusModes) {
				Log.d(TAG, "focusModes to (" + mode + ")");
			}

			if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
				Log.d(TAG, "set focusMode to FOCUS_MODE_CONTINUOUS_VIDEO");
			}

			parameters.setPreviewFrameRate(30);
			// ここで要求パラメータをセット
			camera.setParameters(parameters);

			// セットされたパラメータを取得
			parameters = camera.getParameters();
			captureWidth = parameters.getPreviewSize().width;
			captureHeight = parameters.getPreviewSize().height;
			captureRate = parameters.getPreviewFrameRate();
			int pixelformat = parameters.getPreviewFormat(); // android.graphics.imageformat
			PixelFormat pixelinfo = new PixelFormat();
			PixelFormat.getPixelFormatInfo(pixelformat, pixelinfo);

			int cameraIndex = 0;
			boolean cameraIsFrontFacing = false;

			if (isRearCamera) {
				cameraIsFrontFacing = false;
				cameraIndex = getRearCameraId();
			} else {
				cameraIsFrontFacing = true;
				cameraIndex = getFrontCameraId();
			}

			int bufSize = captureWidth * captureHeight * pixelinfo.bitsPerPixel / 8; // For the default NV21 format, bitsPerPixel = 12.
			Log.i(TAG, "Camera buffers will be " + captureWidth + "x" + captureHeight + "@" + pixelinfo.bitsPerPixel + "bpp, " + bufSize + "bytes.");
			//cameraWrapper = new CameraWrapper(camera);
			//cameraWrapper.configureCallback(this, true, 10, bufSize); // For the default NV21 format, bitsPerPixel = 12.
			//..
			camera.setPreviewCallbackWithBuffer(this);
			for (int i = 0; i < 10; i++) {
				camera.addCallbackBuffer(new byte[bufSize]);
			}
			//..

			camera.startPreview();

			boolean enableContinuousAutoFocus = true;
			// calling "camera.autoFocus" causes the continous autofocus to stop
			// see: http://developer.android.com/reference/android/hardware/Camera.Parameters.html#FOCUS_MODE_CONTINUOUS_VIDEO
			if (!enableContinuousAutoFocus) {
				camera.autoFocus(new Camera.AutoFocusCallback() {
					@Override
					public void onAutoFocus(boolean success, Camera camera) {
						Log.i(TAG, "Autofocused....");
					}
				});
			}

			if (listener != null) {
				listener.cameraPreviewStarted(captureWidth, captureHeight, captureRate, cameraIndex, cameraIsFrontFacing);
			}

			isPreviewRunning = true;
		}
	}


	/*
	public void startPreviewFrontFace(int width, int height) {
		if (camera == null) {
			// Camera wasn't opened successfully?
			Log.e(TAG, "No camera in surfaceChanged");
			return;
		}

		Log.i(TAG, "Surfaced changed, setting up camera and starting preview");

		String camResolution = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pref_cameraResolution", "320x240");
		String[] dims = camResolution.split("x", 2);
		Camera.Parameters parameters = camera.getParameters();
		parameters.setPreviewSize(Integer.parseInt(dims[0]), Integer.parseInt(dims[1]));

        // Insert new algo

		List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
		int previewWidth = width;
		int previewHeight = height;

		int minDiff = Integer.MAX_VALUE;

		for (int i = 0; i < sizes.size(); i++) {
			Camera.Size size = sizes.get(i);

			//Skip square
			if (size.width == size.height) {
				continue;
			}

			Log.d(TAG, "[PREVIEW] Supported Size #" + (i + 1) + ": " + size.width + "x" + size.height);

			int diff = Math.abs(size.height - height) + Math.abs(size.width - width);
			if (diff < minDiff) {
				previewWidth = size.width;
				previewHeight = size.height;
				minDiff = diff;
			}
		}

		Log.d(TAG, "Set camera preview size to (" + previewWidth + "," + previewHeight + ")");
		this.previewWidth = previewWidth;
		this.previewHeight = previewHeight;

        // Insert new algo

		parameters.setPreviewSize(previewWidth, previewHeight);

		int w = this.getWidth();
		double hDouble = (double) w * (double) previewWidth / (double) previewHeight;
		int h = (int) hDouble;
		android.widget.FrameLayout.LayoutParams layoutParams = new android.widget.FrameLayout.LayoutParams(w, h);

		layoutParams.gravity = Gravity.CENTER_VERTICAL;

		Log.d(TAG, "surfaceView to (" + w + "," + h + ")");
		//this.setLayoutParams(layoutParams);

        //List<String> focusModes = params.getSupportedFocusModes();
        //for(String mode : focusModes){
        //    Log.d(TAG, "focusModes to (" + mode + ")");
        //}
		//
		//if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)){
        //    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        //    Log.d(TAG, "set focusMode to FOCUS_MODE_CONTINUOUS_VIDEO");
        //}

		//parameters.setPreviewFrameRate(30);
		camera.setParameters(parameters);

		parameters = camera.getParameters();
		captureWidth = parameters.getPreviewSize().width;
		captureHeight = parameters.getPreviewSize().height;
		captureRate = parameters.getPreviewFrameRate();
		int pixelformat = parameters.getPreviewFormat(); // android.graphics.imageformat
		PixelFormat pixelinfo = new PixelFormat();
		PixelFormat.getPixelFormatInfo(pixelformat, pixelinfo);

        //int cameraIndex = 0;
        //boolean cameraIsFrontFacing = false;
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
        //    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        //    cameraIndex = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pref_cameraIndex", "0"));
        //    Camera.getCameraInfo(cameraIndex, cameraInfo);
        //    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) cameraIsFrontFacing = true;
        //}

		int cameraIndex = 0;
		boolean cameraIsFrontFacing = false;

		if (isRearCamera) {
			cameraIsFrontFacing = false;
			cameraIndex = getRearCameraId();
		} else {
			cameraIsFrontFacing = true;
			cameraIndex = getFrontCameraId();
		}


		int bufSize = captureWidth * captureHeight * pixelinfo.bitsPerPixel / 8; // For the default NV21 format, bitsPerPixel = 12.
		Log.i(TAG, "Camera buffers will be " + captureWidth + "x" + captureHeight + "@" + pixelinfo.bitsPerPixel + "bpp, " + bufSize + "bytes.");
		cameraWrapper = new CameraWrapper(camera);
		cameraWrapper.configureCallback(this, true, 10, bufSize); // For the default NV21 format, bitsPerPixel = 12.

		camera.startPreview();

		camera.autoFocus(new Camera.AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				Log.i(TAG, "Autofocused....");
			}
		});

		if (listener != null) {
			listener.cameraPreviewStarted(captureWidth, captureHeight, captureRate, cameraIndex, cameraIsFrontFacing);
		}
	}

	public void startPreview(int width, int height) {
		Camera.Parameters params = camera.getParameters();

		List<Camera.Size> sizes = params.getSupportedPreviewSizes();
		int previewWidth = width;
		int previewHeight = height;

		int minDiff = Integer.MAX_VALUE;

		for (int i = 0; i < sizes.size(); i++) {
			Camera.Size size = sizes.get(i);

			//Skip square
			if (size.width == size.height) {
				continue;
			}

			Log.d(TAG, "[PREVIEW] Supported Size #" + (i + 1) + ": "
					+ size.width + "x" + size.height);
			int diff = Math.abs(size.height - height)
					+ Math.abs(size.width - width);
			if (diff < minDiff) {
				previewWidth = size.width;
				previewHeight = size.height;
				minDiff = diff;
			}
		}

		Log.d(TAG, "Unda:camera to (" + previewWidth + "," + previewHeight + ")");


		List<String> focusModes = params.getSupportedFocusModes();
		for (String mode : focusModes) {
			Log.d(TAG, "focusModes to (" + mode + ")");
		}

		if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			Log.d(TAG, "set focusMode to FOCUS_MODE_CONTINUOUS_VIDEO");
		}

		params.setPreviewSize(previewWidth, previewHeight);
		//params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
		//Hack for Sumsong
		//params.set("cam_mode", 1);

		camera.setParameters(params);

		this.previewWidth = previewWidth;
		this.previewHeight = previewHeight;
		Log.i(TAG, "Selected preview size: " + previewWidth + "x" + previewHeight);

		params = camera.getParameters();

		captureWidth = params.getPreviewSize().width;
		captureHeight = params.getPreviewSize().height;
		captureRate = params.getPreviewFrameRate();
		int pixelformat = params.getPreviewFormat(); // android.graphics.imageformat
		PixelFormat pixelinfo = new PixelFormat();
		PixelFormat.getPixelFormatInfo(pixelformat, pixelinfo);


		int cameraIndex = 0;
		boolean cameraIsFrontFacing = false;

		if (isRearCamera) {
			cameraIsFrontFacing = false;
			cameraIndex = getRearCameraId();
		} else {
			cameraIsFrontFacing = true;
			cameraIndex = getFrontCameraId();
		}

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
        //    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        //    cameraIndex = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pref_cameraIndex", "0"));
        //    Camera.getCameraInfo(cameraIndex, cameraInfo);
        //    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) cameraIsFrontFacing = true;
        //}

		int bufSize = captureWidth * captureHeight * pixelinfo.bitsPerPixel / 8; // For the default NV21 format, bitsPerPixel = 12.
		Log.i(TAG, "Camera buffers will be " + captureWidth + "x" + captureHeight + "@" + pixelinfo.bitsPerPixel + "bpp, " + bufSize + "bytes.");
		cameraWrapper = new CameraWrapper(camera);
		cameraWrapper.configureCallback(this, true, 10, bufSize); // For the default NV21 format, bitsPerPixel = 12.


		this.textureWidth = width;
		this.textureHeight = height;
		this.setupPreviewSize(width, height);
		int w = this.getWidth();
		double hDouble = (double) w * (double) previewWidth / (double) previewHeight;
		int h = (int) hDouble;
		android.widget.FrameLayout.LayoutParams layoutParams = new android.widget.FrameLayout.LayoutParams(
				w, h);
		layoutParams.gravity = Gravity.CENTER_VERTICAL;

		Log.d(TAG, "textureView to (" + w + "," + h + ")");
		this.setLayoutParams(layoutParams);

        //try {
        //    //camear.setPreviewTexture(surface);
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}

		camera.startPreview();

		camera.autoFocus(new Camera.AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				Log.i(TAG, "Autofocused....");
			}
		});

		if (listener != null) {
			listener.cameraPreviewStarted(captureWidth, captureHeight, captureRate, cameraIndex, cameraIsFrontFacing);
		}
	}
	*/

	public void closeCamera() {
		if (camera != null) {
			// camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.release();
			camera = null;
			isPreviewRunning = false;
		}

		if (listener != null) {
			listener.cameraPreviewStopped();
		}
	}

	static int getFrontCameraId() {
		Camera.CameraInfo ci = new Camera.CameraInfo();
		for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
			Camera.getCameraInfo(i, ci);
			if (ci.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				return i;
			}
		}
		return -1; // No front-facing camera found
	}

	static int getRearCameraId() {
		Camera.CameraInfo ci = new Camera.CameraInfo();
		for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
			Camera.getCameraInfo(i, ci);
			if (ci.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
				return i;
		}
		return -1; // No front-facing camera found
	}

	/*
	private void setupPreviewSize(int width, int height) {
		if (camera != null) {
			Camera.Parameters params = camera.getParameters();
			List<Camera.Size> sizes = params.getSupportedPreviewSizes();
			int previewWidth = width;
			int previewHeight = height;

			int minDiff = Integer.MAX_VALUE;

			for (int i = 0; i < sizes.size(); i++) {
				Camera.Size size = sizes.get(i);

				//Skip square
				if (size.width == size.height) {
					continue;
				}

				Log.d(TAG, "[PREVIEW] Supported Size #" + (i + 1) + ": " + size.width + "x" + size.height);
				int diff = Math.abs(size.height - height) + Math.abs(size.width - width);
				if (diff < minDiff) {
					previewWidth = size.width;
					previewHeight = size.height;
					minDiff = diff;
				}
			}

			Log.d(TAG, "Unda:camera to (" + previewWidth + "," + previewHeight + ")");

			List<String> focusModes = params.getSupportedFocusModes();
			for (String mode : focusModes) {
				Log.d(TAG, "focusModes to (" + mode + ")");
			}

			if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
				Log.d(TAG, "set focusMode to FOCUS_MODE_CONTINUOUS_VIDEO");
			}

			params.setPreviewSize(previewWidth, previewHeight);
			//params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			//Hack for Sumsong
			//params.set("cam_mode", 1);

			camera.setParameters(params);
			this.previewWidth = previewWidth;
			this.previewHeight = previewHeight;
			Log.i(TAG, "Selected preview size: " + previewWidth + "x" + previewHeight);
		}
	}
	*/
}
