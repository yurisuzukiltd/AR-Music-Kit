/*
 *  Author(s): Takamitsu Mizutori, Goldrush Computing Inc.
 */

package com.yurisuzuki;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.yurisuzuki.playsound.R;

public class ActivityAbout extends Activity {
    private final static String GPL_LINK = "http://www.gnu.org/licenses/gpl-3.0.en.html";
    private final static String LGPL_LINK = "http://www.gnu.org/licenses/lgpl-3.0.en.html";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView gplTextView = (TextView)findViewById(R.id.about_gpl_link);
        TextView lgplTextView = (TextView)findViewById(R.id.about_lgpl_link);


        String gplLinkText = "GPLv3. <a href=\"" + GPL_LINK + "\">" + GPL_LINK + "</a>";

        String lgplLinkText = "LGPLv3. <a href=\"" + LGPL_LINK + "\">" + LGPL_LINK + "</a>";



        gplTextView.setText(Html.fromHtml(gplLinkText));
        gplTextView.setMovementMethod(LinkMovementMethod.getInstance());
        lgplTextView.setText(Html.fromHtml(lgplLinkText));
        lgplTextView.setMovementMethod(LinkMovementMethod.getInstance());


    }

}

