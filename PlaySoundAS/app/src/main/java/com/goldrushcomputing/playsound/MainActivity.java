package com.goldrushcomputing.playsound;

import android.app.Activity;
import android.os.Bundle;

import com.goldrushcomputing.playsound.fragment.FragmentInstruction;
import com.goldrushcomputing.playsound.fragment.FragmentMenu;

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

        fm.beginTransaction()
                .add(R.id.main_container, fragment, "FragmentMenu")
                .commit();
    }



    public void openInstruction(){
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
}
