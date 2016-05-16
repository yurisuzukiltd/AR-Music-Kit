/*
 *  Author(s): Takamitsu Mizutori, Goldrush Computing Inc.
 */

package com.yurisuzuki;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import com.yurisuzuki.playsound.R;

public class AboutActivity extends Activity {
    private final static String GPL_LINK = "http://www.gnu.org/licenses/gpl-3.0.en.html";
    private final static String LGPL_LINK = "http://www.gnu.org/licenses/lgpl-3.0.en.html";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView gplTextView = (TextView)findViewById(R.id.about_gpl_link);
        TextView lgplTextView = (TextView)findViewById(R.id.about_lgpl_link);

        String gplLinkText = "<" + GPL_LINK + ">";
        String gplWholeText = "GPLv3. " + gplLinkText;

        String lgplLinkText = "<" + GPL_LINK + ">";
        String lgplWholeText = "LGPLv3. " + lgplLinkText;


        int start = gplWholeText.indexOf(gplLinkText);
        int end = start + gplLinkText.length();

        SpannableStringBuilder spanTxt = new SpannableStringBuilder(gplWholeText);

        if(start > -1){
            spanTxt.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GPL_LINK));
                    startActivity(browserIntent);
                }
            }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanTxt.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        start = lgplWholeText.indexOf(lgplLinkText);
        end = start + lgplLinkText.length();

        spanTxt = new SpannableStringBuilder(lgplWholeText);

        if(start > -1){
            spanTxt.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(LGPL_LINK));
                    startActivity(browserIntent);
                }
            }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanTxt.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

}

