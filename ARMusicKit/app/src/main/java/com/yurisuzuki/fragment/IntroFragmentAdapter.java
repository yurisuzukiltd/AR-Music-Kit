/*
 *  Author(s): Takamitsu Mizutori, Goldrush Computing Inc.
 */

package com.yurisuzuki.fragment;


import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v13.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;
import com.yurisuzuki.playsound.R;

import java.util.ArrayList;

//import android.support.v13.app.FragmentPagerAdapter; <- Should I use this instead of v4

public class IntroFragmentAdapter extends FragmentPagerAdapter implements
		IconPagerAdapter {



	public String type;

	protected static final String[] TEXTS = new String[] { "Intro1",
            "Intro1", "Intro1"};



	protected static final int[] guitarIllustArray = new int[] {
			R.drawable.inst_guitar_0, R.drawable.inst_guitar_1,
			R.drawable.inst_guitar_2, R.drawable.inst_guitar_3};

	protected static final int[] pianoIllustArray = new int[] {
			R.drawable.inst_piano_0, R.drawable.inst_piano_1};

	protected static final int[] musicIllustArray = new int[] {
			R.drawable.inst_mb_0, R.drawable.inst_mb_1,
			R.drawable.inst_mb_2};

	protected static final int[] traceIllustArray = new int[] {
			R.drawable.inst_trace_0, R.drawable.inst_trace_1};


	protected static final int[] guitarTextArray = new int[] {
			R.string.inst_guitar_0, R.string.inst_guitar_1,
			R.string.inst_guitar_2, R.string.inst_guitar_3};

	protected static final int[] pianoTextArray = new int[] {
			R.string.inst_piano_0, R.string.inst_piano_1
			};

	protected static final int[] musicTextArray = new int[] {
			R.string.inst_mb_0, R.string.inst_mb_1,
			R.string.inst_mb_2};

	protected static final int[] traceTextArray = new int[] {
			R.string.inst_trace_0, R.string.inst_trace_1};

    /*
	protected static final int[] IMAGES_BLUR = new int[] {
		R.drawable.img_intro_bg_blur_0, R.drawable.img_intro_bg_blur_1,
		R.drawable.img_intro_bg_blur_2, R.drawable.img_intro_bg_blur_3 };
		*/


	public IntroFragmentAdapter(FragmentManager fm) {
        super(fm);

	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment;
		int illutDrawableResourceId;
		int descriptionStringResourceId;

		if(type != null){
			if(type.equals("guitar")){
				illutDrawableResourceId = guitarIllustArray[position];
				descriptionStringResourceId = guitarTextArray[position];
			}else if(type.equals("piano")){
				illutDrawableResourceId = pianoIllustArray[position];
				descriptionStringResourceId = pianoTextArray[position];
			}else if(type.equals("musicbox")){
				illutDrawableResourceId = musicIllustArray[position];
				descriptionStringResourceId = musicTextArray[position];
			}else if(type.equals("trace")){
				illutDrawableResourceId = traceIllustArray[position];
				descriptionStringResourceId = traceTextArray[position];
			}else{
				illutDrawableResourceId = -1;
				descriptionStringResourceId = -1;
			}

			if(illutDrawableResourceId == -1){
				fragment = null;
			}else{
				fragment = FragmentInstruction.newInstance(illutDrawableResourceId, descriptionStringResourceId);
			}
		}else{
			fragment = null;
		}

        return fragment;
	}

	@Override
	public int getCount() {
		int cnt = 0;

		if(type != null){
			if(type.equals("guitar")){
				cnt = guitarIllustArray.length;
			}else if(type.equals("piano")){
				cnt = pianoIllustArray.length;
			}else if(type.equals("musicbox")){
				cnt = musicIllustArray.length;
			}else if(type.equals("trace")){
				cnt = traceIllustArray.length;
			}
		}else{
			cnt = 0;
		}
		return cnt;
	}

	@Override
	public CharSequence getPageTitle(int position) {
        //String text = IntroFragmentAdapter.TEXTS[position % TEXTS.length];
		return null;
	}


	@Override
	public int getIconResId(int index) {
		//return IMAGES[index % IMAGES.length];
		return 0;
	}


	/*
	public void setCount(int count) {
		if (count > 0 && count <= 10) {
			mCount = count;
			notifyDataSetChanged();
		}
	}
	*/
}