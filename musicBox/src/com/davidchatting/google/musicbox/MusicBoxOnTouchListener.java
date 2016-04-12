package com.davidchatting.google.musicbox;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MusicBoxOnTouchListener implements OnTouchListener {
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		GLSurfaceView thisGLSurfaceView=(GLSurfaceView) v;
		
		//thisGLSurfaceView.
		
		final int actionPeformed = event.getAction();
        
        switch(actionPeformed){
           case MotionEvent.ACTION_DOWN:{
        	  final float x = event.getX();
              final float y = event.getY();
              System.out.println("ACTION_DOWN" + x + "	" + y);
        	  
        	  //ARToolKit.getInstance().
              break;
           }
           
           case MotionEvent.ACTION_MOVE:{
        	  //System.out.println("ACTION_MOVE");
              break;
           }
           
           case MotionEvent.ACTION_UP:{
        	  //System.out.println("ACTION_UP");
              break;
           }
        }
        return true;
	}
}