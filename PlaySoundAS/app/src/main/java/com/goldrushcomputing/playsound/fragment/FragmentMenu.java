package com.goldrushcomputing.playsound.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldrushcomputing.playsound.CustomTypefaceSpan;
import com.goldrushcomputing.playsound.R;

import java.util.ArrayList;

public final class FragmentMenu extends Fragment {
	String type;
	ArrayList<TextView> textViews;

	private int containerId;

	private int containerWidth;

	public static FragmentMenu newInstance(String type) {
		FragmentMenu fragment = new FragmentMenu();
		Bundle args = new Bundle();
		args.putString("type", type);
		fragment.setArguments(args);
		return fragment;
	}

	public FragmentMenu() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			type = getArguments().getString("type");
		}



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

		adjustTypeface();

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

	public void adjustTypeface(){
		Typeface tfLight = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/HelveticaNeueLTStd-Lt.otf");
		Typeface tfBold = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/HelveticaNeueLTStd-Bd.otf");
		Typeface tfHeavy = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/HelveticaNeueLTStd-Hv.otf");

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

					}else if(tag.equals("about")){

					}else{
						showInstruction(tag);
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


	private void showInstruction(String tag) {

		android.app.FragmentManager fm = getFragmentManager();

		FragmentInstruction fragment = (FragmentInstruction) fm.findFragmentByTag("FragmentInstruction");
		if (fragment == null) {
			fragment = FragmentInstruction.newInstance(tag);
		}else{
			fragment.type = tag;
		}


		FragmentTransaction ft = fm.beginTransaction();
		//＊＊＊TRANSIT_FRAGMENT_OPENを指定する＊＊＊
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.replace(containerId, fragment);
		ft.addToBackStack(null);
		ft.commit();
	}
}
