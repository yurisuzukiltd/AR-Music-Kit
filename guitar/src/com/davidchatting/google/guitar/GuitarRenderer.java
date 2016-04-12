package com.davidchatting.google.guitar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;
import org.artoolkit.ar.base.rendering.Cube;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.opengl.GLUtils;
import android.opengl.Matrix;

/**
 * A very simple Renderer that adds a marker and draws a cube on it.
 */
public class GuitarRenderer extends ARRenderer {
	private Context context;

	private int guitarMarkerID = -1;
	private int aNoteMarkerID=-1;
	private int bNoteMarkerID=-1;
	private int cNoteMarkerID=-1;

	private long guitarLastSeen=-1;
	private long aNoteLastSeen=-1;
	private long bNoteLastSeen=-1;
	private long cNoteLastSeen=-1;
	
	private float rotate = 0;
	
	private float mGuitar[]=null;
	private float mGuitar2[]=new float[16];

	private Button guitar = new Button(1.0f,0.0f,0.0f);
	private Cube cube = new Cube(40.0f, 0.0f, 0.0f, 20.0f);

	public GuitarRenderer(Context context){
		this.context=context;
	}

	@Override
	public boolean configureARScene() {
		ARToolKit ar=ARToolKit.getInstance();

		guitarMarkerID=ar.addMarker("single;Data/patt.guitar;100");
		aNoteMarkerID=ar.addMarker("single;Data/patt.capitala;100");
		bNoteMarkerID=ar.addMarker("single;Data/patt.capitalb;100");
		cNoteMarkerID=ar.addMarker("single;Data/patt.capitalc;100");

		return true;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl,config);

		// Load the texture for the square
		guitar.loadGLTexture(gl,this.context,R.drawable.guitaroutline);

		gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		//gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 	//Black Background
		gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do

		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
	}

	/**
	 * Override the draw function from ARRenderer.
	 */
	@Override
	public void draw(GL10 gl) {
		boolean debug=getDebug();
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		ARToolKit ar=ARToolKit.getInstance();
		long now=System.currentTimeMillis();

		boolean queueStrum=false;
		
		if(ar.queryMarkerVisible(guitarMarkerID)) {
			guitarLastSeen=now;
			
			float a[]=getAngles(guitarMarkerID);
			
			//System.out.println(Math.abs(a[1]));
			if(Math.abs(a[1])<1.0f){
				//System.out.println("***STRETCH***");
				setRate(1.10f);
			}
			else{
				setRate(1.0f);
			}
			
    		gl.glMatrixMode(GL10.GL_PROJECTION);
    		gl.glLoadMatrixf(ar.getProjectionMatrix(), 0);

    		gl.glDisable(GL10.GL_CULL_FACE);
            gl.glShadeModel(GL10.GL_SMOOTH);
            gl.glEnable(GL10.GL_DEPTH_TEST);        
        	gl.glFrontFace(GL10.GL_CW);

    		gl.glMatrixMode(GL10.GL_MODELVIEW);

    		mGuitar=ar.queryMarkerTransformation(guitarMarkerID);
		}
		else{
			if((now-guitarLastSeen)>0 && (now-guitarLastSeen)<100){
				if(!queueStrum){
					//System.out.println("***STRUM***"); 
					queueStrum=true;
				}
			}
		}
		
		boolean aNoteSeen=ar.queryMarkerVisible(aNoteMarkerID);
		boolean bNoteSeen=ar.queryMarkerVisible(bNoteMarkerID);
		boolean cNoteSeen=ar.queryMarkerVisible(cNoteMarkerID);
		
		if(aNoteSeen && bNoteSeen && cNoteSeen){
			//nothing to play
			queueStrum=false;
		}
		
		if(aNoteSeen) {
			if(debug){
				//gl.glMatrixMode(GL10.GL_MODELVIEW);
				gl.glLoadMatrixf(ARToolKit.getInstance().queryMarkerTransformation(aNoteMarkerID), 0);
				cube.draw(gl);
			}
			aNoteLastSeen=now;
		}
		if(bNoteSeen) {
			if(debug){
				//gl.glMatrixMode(GL10.GL_MODELVIEW);
				gl.glLoadMatrixf(ARToolKit.getInstance().queryMarkerTransformation(bNoteMarkerID), 0);
				cube.draw(gl);
			}
			bNoteLastSeen=now;
		}
		if(cNoteSeen) {
			if(debug){
				//gl.glMatrixMode(GL10.GL_MODELVIEW);
				gl.glLoadMatrixf(ARToolKit.getInstance().queryMarkerTransformation(cNoteMarkerID), 0);
				cube.draw(gl);
			}
			cNoteLastSeen=now;
		}
		
		if(!aNoteSeen && (now-aNoteLastSeen)<15000){
			if(queueStrum) {
				//System.out.println("***A***");
				playSound(GuitarApplication.aNote_SoundID,1.0f,1.0f);
				queueStrum=false;
			}
		}
		
		if(!bNoteSeen && (now-bNoteLastSeen)<15000){
			if(queueStrum) {
				//System.out.println("***B***");
				playSound(GuitarApplication.bNote_SoundID,1.0f,1.0f);
				queueStrum=false;
			}
		}
		
		if(!cNoteSeen && (now-cNoteLastSeen)<15000){
			if(queueStrum) {
				//System.out.println("***C***");
				playSound(GuitarApplication.cNote_SoundID,1.0f,1.0f);
				queueStrum=false;
			}
		}

		if(mGuitar!=null && (now-guitarLastSeen)<1000){
			gl.glLoadMatrixf(mGuitar, 0);
			gl.glRotatef(90,0.0f,0.0f,1.0f);
			
			guitar.draw(gl);
		}
	}
	
	void drawText(GL10 gl){
		/*
		// Create an empty, mutable bitmap
		Bitmap bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_4444);
		// get a canvas to paint over the bitmap
		Canvas canvas = new Canvas(bitmap);
		bitmap.eraseColor(0);

		// get a background image from resources
		// note the image format must match the bitmap format
		Drawable background = context.getResources().getDrawable(R.drawable.background);
		background.setBounds(0, 0, 256, 256);
		background.draw(canvas); // draw the background to our bitmap

		// Draw the text
		Paint textPaint = new Paint();
		textPaint.setTextSize(32);
		textPaint.setAntiAlias(true);
		textPaint.setARGB(0xff, 0x00, 0x00, 0x00);
		// draw the text centered
		canvas.drawText("Hello World", 16,112, textPaint);

		//Generate one texture pointer...
		gl.glGenTextures(1, textures, 0);
		//...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		//Create Nearest Filtered Texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		//Clean up
		bitmap.recycle();
		*/
	}

	void playSound(int soundID,float rate,float volume){
		((GuitarApplication)(GuitarApplication.getInstance())).playSound(soundID,rate,volume);
	}

	void setRate(float rate){
		((GuitarApplication)(GuitarApplication.getInstance())).setRate(rate);
	}

	float [] getPosition(int markerID) {
		float result[]=new float[] {0,0,0,0};

		if (ARToolKit.getInstance().queryMarkerVisible(markerID)) {
			float m[]=ARToolKit.getInstance().queryMarkerTransformation(markerID);
			Matrix.multiplyMV(result,0,m,0,new float[]{0,0,0,1},0);
		}

		return(result);
	}

	float getDistance(float d[]){
		return((float)Math.sqrt((d[0]*d[0])+(d[1]*d[1])+(d[2]*d[2])));
	}

	float [] getAngles(int markerID) {
		float result[]=new float[] {0,0,0};

		if(ARToolKit.getInstance().queryMarkerVisible(markerID)) {
			float m[]=ARToolKit.getInstance().queryMarkerTransformation(markerID);
		
			float yaw=0.0f, pitch=0.0f, roll=0.0f;
			if (m[0] == 1.0f) {
				yaw = (float)Math.atan2(m[8], m[14]);
				pitch = 0;
				roll = 0;
			}
			else if (m[0] == -1.0f) {
				yaw = (float)Math.atan2(m[8], m[14]);
				pitch = 0;
				roll = 0;
			}
			else {
				yaw = (float)Math.atan2(-m[2], m[0]);
				pitch = (float)Math.asin(m[1]);
				roll = (float)Math.atan2(-m[9], m[5]);
			}

			result[0]=-yaw;
			result[1]=-pitch;
			result[2]=-roll;
		}
		return(result);
	}
	
	boolean getDebug(){
		return(((GuitarApplication)(GuitarApplication.getInstance())).getDebug());
	}
	
	void setDebug(boolean d){
		((GuitarApplication)(GuitarApplication.getInstance())).setDebug(d);
	}
}