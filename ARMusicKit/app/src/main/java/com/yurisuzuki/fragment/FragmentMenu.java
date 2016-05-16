/*
 *  Author(s): Takamitsu Mizutori, Goldrush Computing Inc.
 */

package com.yurisuzuki.fragment;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yurisuzuki.CustomTypefaceSpan;
import com.yurisuzuki.MainActivity;
import com.yurisuzuki.playsound.R;

import java.util.ArrayList;

public final class FragmentMenu extends Fragment {
	String type;
	ArrayList<TextView> textViews;

	private int containerId;
	private int containerWidth;

	public static FragmentMenu newInstance() {
		FragmentMenu fragment = new FragmentMenu();
		return fragment;
	}

	public FragmentMenu() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = (View) inflater.inflate(R.layout.fragment_menu, container, false);

		containerId = container.getId();
		//＊＊＊コンテナの幅を保存しておく＊＊＊
		containerWidth = container.getWidth();


		textViews = new ArrayList<TextView>();
		textViews.add((TextView) layout.findViewById(R.id.menu_title_make_markers));
		textViews.add((TextView) layout.findViewById(R.id.menu_title_play_guitar));
		textViews.add((TextView) layout.findViewById(R.id.menu_title_play_piano));
		textViews.add((TextView) layout.findViewById(R.id.menu_title_play_music_box));
		textViews.add((TextView) layout.findViewById(R.id.menu_title_about));

		configureMenus();

		return layout;
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {

		      
		} else {

			
		}
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	public void configureMenus(){
		Typeface tfLight = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/HelveticaNeueLTStd-Lt.otf");
		Typeface tfBold = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/HelveticaNeueLTStd-Bd.otf");

		for(TextView textView : textViews){
			final String text = (String)textView.getText();
			String[] list = text.split(" ");
			String firstWord = list[0];
			String secondWord = null;

			if(list.length > 1){
				firstWord += " ";
				secondWord = list[1];

				if(list.length == 3){
					secondWord = secondWord + " " + list[2];
				}
			}else{
				secondWord = "";
			}

			// Create a new spannable with the two strings
			Spannable spannable = new SpannableString(firstWord+secondWord);

			// Set the custom typeface to span over a section of the spannable object
			spannable.setSpan( new CustomTypefaceSpan("sans-serif",tfLight), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			spannable.setSpan( new CustomTypefaceSpan("sans-serif",tfBold), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			textView.setText(spannable);

			textView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//((MainActivity)getActivity()).openInstruction();
					String tag = (String)v.getTag();
					if(tag.equals("marker")){
						((MainActivity)getActivity()).showMakeMarkerTop();
					}else if(tag.equals("about")){
						((MainActivity)getActivity()).showAbout();
					}else{
						((MainActivity)getActivity()).showInstruction(tag);
					}

				}
			});
		}
	}

	/*
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
				.add(R.id.menu_container, fragment, "FragmentInstruction")
				.addToBackStack(null)
				.commit();
	}
	*/


}
