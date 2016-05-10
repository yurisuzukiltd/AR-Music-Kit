package com.yurisuzuki.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewHelveticaNeueBd extends TextView {

	public TextViewHelveticaNeueBd(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setFont(this, "fonts/HelveticaNeueLTStd-Bd.otf");
	}

	public TextViewHelveticaNeueBd(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setFont(this, "fonts/HelveticaNeueLTStd-Bd.otf");
	}

	public TextViewHelveticaNeueBd(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		setFont(this, "fonts/HelveticaNeueLTStd-Bd.otf");
	}

	public static void setFont(TextView textView, String fontPath) {
		Typeface tf = Typeface.createFromAsset(textView.getContext().getAssets(), fontPath);
		textView.setTypeface(tf);
	}

}
