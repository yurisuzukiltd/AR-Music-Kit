/*
 *  Author(s): Kosuke Miyoshi, Narrative Nights
 */
package com.yurisuzuki.ar;

import com.yurisuzuki.CameraActivity;

public class PianoMarker extends Marker {
	void checkPlaySound(long now, CameraActivity activity) {
		if (isTracked()) {
			// マーカーを認識していたら、lastTrackedTimeを更新
			lastTrackedTime = now;
		} else {
			// 現在認識しておらず、最後に認識してから1000msec以内だったら、発音する
			if (lastTrackedTime > 0 && (now - lastTrackedTime) < 1000) {
				lastTrackedTime = -1;
				lastPlayTime = now;
				activity.playSound(soundId);
			}
		}
	}
}
