/*
 *  Author(s): Takamitsu Mizutori, Goldrush Computing Inc.
 */

package com.yurisuzuki.fragment;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

//import android.support.v13.app.FragmentPagerAdapter; <- Should I use this instead of v4

public class IntroFragmentAdapter extends FragmentPagerAdapter implements
		IconPagerAdapter {

	public String type;

	protected static final String[] TEXTS = new String[] { "Intro1",
            "Intro1", "Intro1"};


	/*
	protected static final int[] IMAGES = new int[] {
			R.drawable.intro_bg_long, R.drawable.intro_bg_long,
			R.drawable.intro_bg_long };
			*/


    /*
	protected static final int[] IMAGES_BLUR = new int[] {
		R.drawable.img_intro_bg_blur_0, R.drawable.img_intro_bg_blur_1,
		R.drawable.img_intro_bg_blur_2, R.drawable.img_intro_bg_blur_3 };
		*/

	private int mCount = TEXTS.length;

	public IntroFragmentAdapter(FragmentManager fm) {
        super(fm);
	}

	@Override
	public Fragment getItem(int position) {
        Fragment fragment = FragmentInstruction.newInstance(type);

		if(position == 0){

        }else if(position == 1){

        }else{

        }

        return fragment;
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public CharSequence getPageTitle(int position) {
        String text = IntroFragmentAdapter.TEXTS[position % TEXTS.length];

		return text;
	}


	@Override
	public int getIconResId(int index) {
		//return IMAGES[index % IMAGES.length];
		return 0;
	}


	public void setCount(int count) {
		if (count > 0 && count <= 10) {
			mCount = count;
			notifyDataSetChanged();
		}
	}
}