//
// This class provides a subclass of Application to enable app-wide behavior.
// 

package com.davidchatting.google.musicbox;

import org.artoolkit.ar.base.assets.AssetHelper;
//import org.artoolkit.ar.samples.ARSimple.R;

import android.app.Application;
import android.media.AudioManager;

import android.media.SoundPool;
import android.media.SoundPool.*;

public class MusicBoxApplication extends Application {

	private static Application sInstance;
	private SoundPool soundPool;
	public static int soundID_a,soundID_b,soundID_c,soundID_c1,soundID_d,soundID_e,soundID_f,soundID_g;
	private int currentPlayingStreamID=-1;
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
    	((MusicBoxApplication) sInstance).initializeInstance();
    	
    	// AudioManager audio settings for adjusting the volume
    	audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
  
    	// Load the sounds
    	soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
    	soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
	    	@Override
	    	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
	    	}
    	});
    	soundID_a = soundPool.load(this, R.raw.musicboxa, 1);
    	soundID_b = soundPool.load(this, R.raw.musicboxb, 1);
    	soundID_c = soundPool.load(this, R.raw.musicboxc, 1);
    	soundID_c1 = soundPool.load(this,R.raw.musicboxc1,1);
    	soundID_d = soundPool.load(this, R.raw.musicboxd, 1);
    	soundID_e = soundPool.load(this, R.raw.musicboxe, 1);
    	soundID_f = soundPool.load(this, R.raw.musicboxf, 1);
    	soundID_g = soundPool.load(this, R.raw.musicboxg, 1);
    }
    
    protected void initializeInstance() {
		// Unpack assets to cache directory so native library can read them.
    	// N.B.: If contents of assets folder changes, be sure to increment the
    	// versionCode integer in the AndroidManifest.xml file.
		AssetHelper assetHelper = new AssetHelper(getAssets());        
		assetHelper.cacheAssetFolder(getInstance(), "Data");
    }
    
    public void playSound(int soundID,float rate,float volume){
    	soundPool.play(soundID,volume,volume,1,0,rate);	//don't loop
    }
    
    public void setDebug(boolean d){
    	debug=d;
    }
    
    public boolean getDebug(){
    	return(debug);
    }
}
