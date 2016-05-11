/*
 *  Author(s): Takamitsu Mizutori, Goldrush Computing Inc.
 */

package com.yurisuzuki;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.yurisuzuki.fragment.FragmentIntroBase;
import com.yurisuzuki.playsound.R;

;

public class ActivityIntro extends Activity {
    public static final String TAG = "ActivityIntro";
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getString("type");
        }

        showIntro();
    }

    private void showIntro(){
        //ActionBar actionBar = getActionBar();
        //actionBar.hide();

        Fragment fragment = FragmentIntroBase.newInstance(type);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment,
                        "FragmentIntroBase").commit();
    }


    public void jumpToCamera(){
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
        finish();
        //overridePendingTransition(R.anim.fade_in, R.anim.scale_out);
    }


    //@Override
    /*
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_intro, menu);
        return true;
    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        /*
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }
}
