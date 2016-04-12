/*
 *  ARSimpleApplication.java
 *  ARToolKit5
 *
 *  Disclaimer: IMPORTANT:  This Daqri software is supplied to you by Daqri
 *  LLC ("Daqri") in consideration of your agreement to the following
 *  terms, and your use, installation, modification or redistribution of
 *  this Daqri software constitutes acceptance of these terms.  If you do
 *  not agree with these terms, please do not use, install, modify or
 *  redistribute this Daqri software.
 *
 *  In consideration of your agreement to abide by the following terms, and
 *  subject to these terms, Daqri grants you a personal, non-exclusive
 *  license, under Daqri's copyrights in this original Daqri software (the
 *  "Daqri Software"), to use, reproduce, modify and redistribute the Daqri
 *  Software, with or without modifications, in source and/or binary forms;
 *  provided that if you redistribute the Daqri Software in its entirety and
 *  without modifications, you must retain this notice and the following
 *  text and disclaimers in all such redistributions of the Daqri Software.
 *  Neither the name, trademarks, service marks or logos of Daqri LLC may
 *  be used to endorse or promote products derived from the Daqri Software
 *  without specific prior written permission from Daqri.  Except as
 *  expressly stated in this notice, no other rights or licenses, express or
 *  implied, are granted by Daqri herein, including but not limited to any
 *  patent rights that may be infringed by your derivative works or by other
 *  works in which the Daqri Software may be incorporated.
 *
 *  The Daqri Software is provided by Daqri on an "AS IS" basis.  DAQRI
 *  MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 *  THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS
 *  FOR A PARTICULAR PURPOSE, REGARDING THE DAQRI SOFTWARE OR ITS USE AND
 *  OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS.
 *
 *  IN NO EVENT SHALL DAQRI BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL
 *  OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION,
 *  MODIFICATION AND/OR DISTRIBUTION OF THE DAQRI SOFTWARE, HOWEVER CAUSED
 *  AND WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE),
 *  STRICT LIABILITY OR OTHERWISE, EVEN IF DAQRI HAS BEEN ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 *
 *  Copyright 2015 Daqri, LLC.
 *  Copyright 2011-2015 ARToolworks, Inc.
 *
 *  Author(s): Philip Lamb
 *
 */

//
// This class provides a subclass of Application to enable app-wide behavior.
// 

package com.davidchatting.google.theremin;

import org.artoolkit.ar.base.assets.AssetHelper;

import android.app.Application;
import android.media.AudioManager;

import android.media.SoundPool;
import android.media.SoundPool.*;
import android.widget.TextView;

public class ThereminApplication extends Application {

	private static Application sInstance;
	private SoundPool soundPool;
	private int soundID;
	private int currentStreamID;
	boolean soundLoaded = false, soundPlaying = false;
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
    	((ThereminApplication) sInstance).initializeInstance();
    	
    	// AudioManager audio settings for adjusting the volume
    	audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
  
    	// Load the sounds
    	soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
    	soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
	    	@Override
	    	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
	    		soundLoaded = true;
	    	}
    	});
    	soundID = soundPool.load(this, R.raw.thereminf3, 1);
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
    
    public void playSound(float rate,float volume){
		//rate: range is 0.5 to 2 only
    	if(soundLoaded){
    		if(soundPlaying){
    			soundPool.setVolume(currentStreamID,volume,volume);
        		soundPool.setRate(currentStreamID,rate);
        	}
    		else{
    			currentStreamID=soundPool.play(soundID,volume,volume,1,-1,rate);	//loops forever
	    		soundPlaying=true;
	    	}
    	}
    }
    
    public void stopSound(){
    	if(soundPlaying){
    		soundPool.stop(currentStreamID);
    		soundPlaying=false;
    	}
    }
    
    public void setDebug(boolean d){
    	debug=d;
    }
    
    public boolean getDebug(){
    	return(debug);
    }
}
