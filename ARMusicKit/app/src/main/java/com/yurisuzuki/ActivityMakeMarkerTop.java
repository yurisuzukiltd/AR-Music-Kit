/*
 *  Author(s): Takamitsu Mizutori, Goldrush Computing Inc.
 */

package com.yurisuzuki;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.yurisuzuki.playsound.R;

public class ActivityMakeMarkerTop extends Activity {

    private final static String PDF_LINK = "https://www.dropbox.com/s/oj1k0v0oef5aav1/overrayinteractionsV2.pdf?dl=0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_top);


        findViewById(R.id.menu_icon_trace_marker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTraceMarkerPage();
            }
        });

        findViewById(R.id.menu_title_trace_marker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTraceMarkerPage();
            }
        });

        findViewById(R.id.menu_icon_open_in_chrome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChrome();
            }
        });

        findViewById(R.id.menu_title_open_in_chrome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChrome();
            }
        });

        findViewById(R.id.menu_icon_email_pdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmail();
            }
        });

        findViewById(R.id.menu_title_email_pdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmail();
            }
        });
    }


    private void showTraceMarkerPage(){
        Intent intent = new Intent(this, ActivityMakeMarkerTrace.class);
        startActivity(intent);
    }

    private void openChrome(){
        try {
            Uri uri = Uri.parse("googlechrome://navigate?url=" + PDF_LINK);
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            // Chrome is probably not installed
        }
    }

    private void openEmail(){
        Uri uri = Uri.parse("mailto:" + "")
                .buildUpon()
                .appendQueryParameter("subject", "Link to ARMusicKit marker images")
                .appendQueryParameter("body", "Please download PDF from here.\n" + PDF_LINK)
                .build();

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
        startActivity(Intent.createChooser(emailIntent, "Send PDF link"));

    }

}

