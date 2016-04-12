//
// This class provides a subclass of Application to enable app-wide behavior.
// 

package com.davidchatting.google.guitar;

import org.artoolkit.ar.base.assets.AssetHelper;

import android.app.Application;
import android.media.AudioManager;

import android.media.SoundPool;
import android.media.SoundPool.*;

public class GuitarApplication extends Application {

	private static Application sInstance;
	private SoundPool soundPool;
	public static int aNote_SoundID,bNote_SoundID,cNote_SoundID;
	private int currentStreamID=-1;
	AudioManager audioManager;
	
	private boolean debug=false;
	 
	// Anywhere in the application where an instance is required, this method
	// can be used to retrieve it.
    public static Application getInstance() {
    	return sInstance;
    }
    
    @Override
    public void onCreate() {
    	super.onCreate(); 
    	sInstance = this;
    	((GuitarApplication) sInstance).initializeInstance();
    	
    	// AudioManager audio settings for adjusting the volume
    	audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
  
    	// Load the sounds
    	soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
    	soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
	    	@Override
	    	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
	    	}
    	});
    	aNote_SoundID = soundPool.load(this, R.raw.c, 1);
    	bNote_SoundID = soundPool.load(this, R.raw.d, 1);
    	cNote_SoundID = soundPool.load(this, R.raw.e, 1);
    }
    
    // Here we do one-off initialisation which should apply to all activities
	// in the application.
    protected void initializeInstance() {
    	
		// Unpack assets to cache directory so native library can read them.
    	// N.B.: If contents of assets folder changes, be sure to increment the
    	// versionCode integer in the AndroidManifest.xml file.
		AssetHelper assetHelper = new AssetHelper(getAssets());        
		assetHelper.cacheAssetFolder(getInstance(), "Data");
    }
    
    public void playSound(int soundID,float rate,float volume){
		//rate: range is 0.5 to 2 only
    	currentStreamID=soundPool.play(soundID,volume,volume,1,0,rate);	//no loop
    }
    
    public void setRate(float rate){
    	//rate: range is 0.5 to 2 only
    	soundPool.setRate(currentStreamID,rate);
    }
    
    public void setDebug(boolean d){
    	debug=d;
    }
    
    public boolean getDebug(){
    	return(debug);
    }
}
