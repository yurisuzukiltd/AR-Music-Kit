/*
 * Copyright (C) 2011 Goldrush Computing Inc. All right reserved.
 */
package com.yurisuzuki.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class SlidingFrameLayout extends FrameLayout {
   
    public SlidingFrameLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}


	public SlidingFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public SlidingFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	
	public float getXFraction() {
        int width = getWidth();
        return (width == 0) ? 0 : getX() / (float) width;
    }

    public void setXFraction(float xFraction) {
        // TODO: cache width
        final int width = getWidth();
        //setX((width > 0) ? (xFraction * width) : -9999);
        setX((width > 0) ? (xFraction * width) : 0);
    }
   
}