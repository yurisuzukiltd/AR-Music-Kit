/*
 *  Author(s): Takamitsu Mizutori, Goldrush Computing Inc.
 */

package com.yurisuzuki;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yurisuzuki.playsound.R;

public class ActivityMakeMarkerTrace extends Activity {

    public enum Tab {
        GUITAR, MUSICBOX, PIANO
    };




    protected static final int[] guitarMarkerArray = new int[] {
            R.drawable.mk_guitar_hit, R.drawable.mk_guitar_c,
            R.drawable.mk_guitar_a, R.drawable.mk_guitar_g,
            R.drawable.mk_guitar_e, R.drawable.mk_guitar_d,
            R.drawable.mk_guitar_am, R.drawable.mk_guitar_em,
            R.drawable.mk_guitar_f, R.drawable.mk_guitar_a,
            R.drawable.mk_guitar_g
    };

    protected static final int[] musicBoxMarkerArray = new int[] {
            R.drawable.mk_piano_do, R.drawable.mk_piano_re,
            R.drawable.mk_piano_mi, R.drawable.mk_piano_fa,
            R.drawable.mk_piano_so, R.drawable.mk_piano_la,
            R.drawable.mk_piano_si, R.drawable.mk_piano_do_
    };

    protected static final int[] pianoMarkerArray = new int[] {
            R.drawable.mk_piano_do, R.drawable.mk_piano_re,
            R.drawable.mk_piano_mi, R.drawable.mk_piano_fa,
            R.drawable.mk_piano_so, R.drawable.mk_piano_la,
            R.drawable.mk_piano_si, R.drawable.mk_piano_do_
    };

    private Tab currentTab;
    private int[] currentMarkerSet;
    private int currentPosition;

    private ImageView markerView;

    TextView guitarTabTitle;
    TextView musicBoxTabTitle;
    TextView pianoTabTitle;


    Button leftButton;
    Button rightButton;

    Typeface tfLight;
    Typeface tfMedium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_trace);

        tfLight = Typeface.createFromAsset(getAssets(),
                "fonts/HelveticaNeueLTStd-Lt.otf");
        tfMedium = Typeface.createFromAsset(getAssets(),
                "fonts/HelveticaNeueLTStd-Md.otf");

        markerView = (ImageView)findViewById(R.id.trace_marker_image);
        leftButton = (Button) findViewById(R.id.to_left_marker_button);
        rightButton = (Button) findViewById(R.id.to_right_marker_button);

        guitarTabTitle = (TextView) findViewById(R.id.marker_trace_guitar_tab_button);
        musicBoxTabTitle = (TextView) findViewById(R.id.marker_trace_musicbox_tab_button);
        pianoTabTitle = (TextView) findViewById(R.id.marker_trace_piano_tab_button);


        guitarTabTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabSelected((String)v.getTag());
            }
        });

        musicBoxTabTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabSelected((String)v.getTag());
            }
        });

        pianoTabTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabSelected((String)v.getTag());
            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftButtonPressed();
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightButtonPressed();
            }
        });

        tabSelected("guitar");


    }


    private void tabSelected(String tabName){
        if(tabName.equals("guitar")){
            currentTab = Tab.GUITAR;
            currentMarkerSet = guitarMarkerArray;
            currentPosition = 0;
            guitarTabTitle.setTypeface(tfMedium);
            musicBoxTabTitle.setTypeface(tfLight);
            pianoTabTitle.setTypeface(tfLight);
        }else if(tabName.equals("musicbox")){
            currentTab = Tab.MUSICBOX;
            currentMarkerSet = musicBoxMarkerArray;
            currentPosition = 0;
            guitarTabTitle.setTypeface(tfLight);
            musicBoxTabTitle.setTypeface(tfMedium);
            pianoTabTitle.setTypeface(tfLight);
        }else if(tabName.equals("piano")){
            currentTab = Tab.PIANO;
            currentMarkerSet = pianoMarkerArray;
            currentPosition = 0;
            guitarTabTitle.setTypeface(tfLight);
            musicBoxTabTitle.setTypeface(tfLight);
            pianoTabTitle.setTypeface(tfMedium);
        }
        updateUI();
    }

    private void leftButtonPressed(){
        if(currentPosition > 0){
            currentPosition--;
        }

        updateUI();
    }

    private void rightButtonPressed(){
        if(currentPosition < currentMarkerSet.length-1){
            currentPosition++;
        }

        updateUI();
    }

    private void updateUI(){
        markerView.setImageBitmap(BitmapFactory.decodeResource(getResources(), currentMarkerSet[currentPosition]));

        if(currentPosition == 0){
            fadeOutButton(leftButton);
            leftButton.setEnabled(false);
            fadeInButton(rightButton);
            rightButton.setEnabled(true);
        }else if(currentPosition == currentMarkerSet.length-1){
            fadeOutButton(rightButton);
            rightButton.setEnabled(false);
            fadeInButton(leftButton);
            leftButton.setEnabled(true);
        }else{
            fadeInButton(leftButton);
            leftButton.setEnabled(true);
            fadeInButton(rightButton);
            rightButton.setEnabled(true);
        }

    }

    private void fadeOutButton(Button btn){
        float alpha = 0.5f;
        AlphaAnimation alphaUp = new AlphaAnimation(alpha, alpha);
        alphaUp.setFillAfter(true);
        btn.startAnimation(alphaUp);
    }

    private void fadeInButton(Button btn){
        float alpha = 1.0f;
        AlphaAnimation alphaUp = new AlphaAnimation(alpha, alpha);
        alphaUp.setFillAfter(true);
        btn.startAnimation(alphaUp);
    }




}

