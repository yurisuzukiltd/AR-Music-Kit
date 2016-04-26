package com.goldrushcomputing.playsound.ar;

import com.goldrushcomputing.playsound.Example;

public class GuitarPlayMarker extends Marker {
	void checkPlaySound(long now, Example activity) {
		if (isTracked()) {
			// マーカーを認識していたら、lastTrackedTimeを更新
			lastTrackedTime = now;
		} else {
			// 現在認識しておらず、最後に認識してから1000msec以内だったら、発音する
			if (lastTrackedTime > 0 && (now - lastTrackedTime) < 1000) {
				lastTrackedTime = -1;
				lastPlayTime = now;
				activity.playCurrentSound();
			}
		}
	}
}
