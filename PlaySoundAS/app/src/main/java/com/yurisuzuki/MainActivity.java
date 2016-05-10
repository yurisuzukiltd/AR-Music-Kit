package com.yurisuzuki;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.yurisuzuki.playsound.R;
import com.yurisuzuki.fragment.FragmentInstruction;
import com.yurisuzuki.fragment.FragmentMenu;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.app.FragmentManager fm = getFragmentManager();
        FragmentMenu fragment = (FragmentMenu) fm.findFragmentByTag("FragmentMenu");
        if (fragment == null) {
            fragment = FragmentMenu.newInstance("foo");
        }

        if(fragment.isAdded() == false){
            fm.beginTransaction()
                    .add(R.id.main_container, fragment, "FragmentMenu")
                    .commit();
        }

    }



    public void openInstructionOld(){

        android.app.FragmentManager fm = getFragmentManager();

        FragmentInstruction fragment = (FragmentInstruction) fm.findFragmentByTag("FragmentInstruction");
        if (fragment == null) {
            fragment = FragmentInstruction.newInstance("guitar");
        }

        fm.beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_in_from_right,
                        R.animator.fragment_slide_out_to_right,
                        R.animator.fragment_slide_in_from_right,
                        R.animator.fragment_slide_out_to_right)
                .add(R.id.main_container, fragment, "FragmentInstruction")
                .addToBackStack(null)
                .commit();


    }

    public void jumpToCamera(){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
        //finish();
        //overridePendingTransition(R.anim.fade_in, R.anim.scale_out);
    }


    public void showInstruction(String tag) {
        Intent intent = new Intent(this, ActivityIntro.class);
        startActivity(intent);
        //finish();
        //overridePendingTransition(R.anim.fade_in, R.anim.scale_out);

    }





}

