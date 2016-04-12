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

package com.davidchatting.google.drums;

import org.artoolkit.ar.base.assets.AssetHelper;
//import org.artoolkit.ar.samples.ARSimple.R;

import android.app.Application;
import android.media.AudioManager;

import android.media.SoundPool;
import android.media.SoundPool.*;

public class DrumsApplication extends Application {

	private static Application sInstance;
	private SoundPool soundPool;
	public static int drum1_SoundID,drum2_SoundID,drum3_SoundID;
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
    	((DrumsApplication) sInstance).initializeInstance();
    	
    	// AudioManager audio settings for adjusting the volume
    	audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
  
    	// Load the sounds
    	soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
    	soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
	    	@Override
	    	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
	    	}
    	});
    	drum1_SoundID = soundPool.load(this, R.raw.snaredrum, 1);
    	drum2_SoundID = soundPool.load(this, R.raw.bass, 1);
    	drum3_SoundID = soundPool.load(this, R.raw.hat, 1);
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
