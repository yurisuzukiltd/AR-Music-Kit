package com.vuforia.samples.VuforiaSamples.ui.SampleAppMenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.vuforia.samples.VuforiaSamples.R;


public class SampleAppMenuGroup {

	Activity mActivity;
	SampleAppMenuInterface mMenuInterface;
	LinearLayout mLayout;
	LayoutParams mLayoutParams;
	LayoutInflater inflater;
	int dividerResource;

	float mEntriesTextSize;
	int mEntriesSidesPadding;
	int mEntriesUpDownPadding;
	int mEntriesUpDownRadioPadding;
	Typeface mFont;

	int selectorResource;

	SampleAppMenu mSampleAppMenu;
	RadioGroup mRadioGroup;

	OnClickListener mClickListener;
	OnCheckedChangeListener mOnCheckedListener;
	OnCheckedChangeListener mOnRadioCheckedListener;


	@SuppressLint("InflateParams")
	public SampleAppMenuGroup(SampleAppMenuInterface menuInterface,
	                          Activity context, SampleAppMenu parent, boolean hasTitle, String title,
	                          int width) {
		mActivity = context;
		mMenuInterface = menuInterface;
		mSampleAppMenu = parent;
		mLayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		inflater = LayoutInflater.from(mActivity);
		mLayout = (LinearLayout) inflater.inflate(
				R.layout.sample_app_menu_group, null, false);
		mLayout.setLayoutParams(new LinearLayout.LayoutParams(width,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		mEntriesTextSize = mActivity.getResources().getDimension(
				R.dimen.menu_entries_text);

		mEntriesSidesPadding = (int) mActivity.getResources().getDimension(
				R.dimen.menu_entries_sides_padding);
		mEntriesUpDownPadding = (int) mActivity.getResources().getDimension(
				R.dimen.menu_entries_top_down_padding);
		mEntriesUpDownRadioPadding = (int) mActivity.getResources()
				.getDimension(R.dimen.menu_entries_top_down_radio_padding);
		dividerResource = R.layout.sample_app_menu_group_divider;

		selectorResource = android.R.drawable.list_selector_background;

		mFont = Typeface.create("sans-serif", Typeface.NORMAL);

		TextView titleView = (TextView) mLayout
				.findViewById(R.id.menu_group_title);
		titleView.setText(title);
		titleView.setTextSize(mActivity.getResources().getDimension(
				R.dimen.menu_entries_title));
		titleView.setClickable(false);

		if (!hasTitle) {
			mLayout.removeView(titleView);
			View dividerView = mLayout
					.findViewById(R.id.menu_group_title_divider);
			mLayout.removeView(dividerView);
		}

		mClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				int command = Integer.parseInt(v.getTag().toString());
				mMenuInterface.menuProcess(command);
				mSampleAppMenu.hideMenu();
			}

		};

		mOnCheckedListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton switchView,
			                             boolean isChecked) {
				boolean result;
				int command = Integer.parseInt(switchView.getTag().toString());
				result = mMenuInterface.menuProcess(command);
				if (!result) {
					switchView.setChecked(!isChecked);
				} else
					mSampleAppMenu.hideMenu();

			}

		};

		mOnRadioCheckedListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton switchView,
			                             boolean isChecked) {
				if (isChecked) {
					boolean result;
					int command = Integer.parseInt(switchView.getTag().toString());
					result = mMenuInterface.menuProcess(command);
					if (result) {
						mSampleAppMenu.hideMenu();
					}
				}
			}

		};

	}


	@SuppressWarnings("deprecation")
	public View addTextItem(String text, int command) {

		Drawable selectorDrawable = mActivity.getResources().getDrawable(
				selectorResource);

		TextView newTextView = new TextView(mActivity);
		newTextView.setText(text);

		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN)
			newTextView.setBackground(selectorDrawable);
		else
			newTextView.setBackgroundDrawable(selectorDrawable);

		newTextView.setTypeface(mFont);
		newTextView.setTextSize(mEntriesTextSize);
		newTextView.setTag(command);
		newTextView.setVisibility(View.VISIBLE);
		newTextView.setPadding(mEntriesSidesPadding, mEntriesUpDownPadding,
				mEntriesSidesPadding, mEntriesUpDownPadding);
		newTextView.setClickable(true);
		newTextView.setOnClickListener(mClickListener);
		mLayout.addView(newTextView, mLayoutParams);

		View divider = inflater.inflate(dividerResource, null);
		mLayout.addView(divider, mLayoutParams);

		return newTextView;

	}


	@SuppressWarnings("deprecation")
	public View addSelectionItem(String text, int command, boolean on) {

		Drawable selectorDrawable = mActivity.getResources().getDrawable(
				selectorResource);
		View returnView;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			Switch newSwitchView = new Switch(mActivity);
			newSwitchView.setText(text);

			newSwitchView.setBackground(selectorDrawable);

			newSwitchView.setTypeface(mFont);
			newSwitchView.setTextSize(mEntriesTextSize);
			newSwitchView.setTag(command);
			newSwitchView.setVisibility(View.VISIBLE);
			newSwitchView.setPadding(mEntriesSidesPadding,
					mEntriesUpDownPadding, mEntriesSidesPadding,
					mEntriesUpDownPadding);
			newSwitchView.setChecked(on);
			newSwitchView.setOnCheckedChangeListener(mOnCheckedListener);
			mLayout.addView(newSwitchView, mLayoutParams);
			returnView = newSwitchView;
		} else {
			CheckBox newView = new CheckBox(mActivity);

			int leftPadding = newView.getPaddingLeft();

			if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN)
				newView.setBackground(selectorDrawable);
			else
				newView.setBackgroundDrawable(selectorDrawable);

			newView.setText(text);
			newView.setTypeface(mFont);
			newView.setTextSize(mEntriesTextSize);
			newView.setTag(command);
			newView.setVisibility(View.VISIBLE);
			newView.setPadding(mEntriesSidesPadding + leftPadding,
					mEntriesUpDownPadding, mEntriesSidesPadding,
					mEntriesUpDownPadding);
			newView.setChecked(on);
			newView.setOnCheckedChangeListener(mOnCheckedListener);
			mLayout.addView(newView, mLayoutParams);
			returnView = newView;
		}

		View divider = inflater.inflate(dividerResource, null);
		mLayout.addView(divider, mLayoutParams);

		return returnView;
	}


	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	public View addRadioItem(String text, int command, boolean isSelected) {
		if (mRadioGroup == null) {
			mRadioGroup = new RadioGroup(mActivity);
			mRadioGroup.setVisibility(View.VISIBLE);
			mLayout.addView(mRadioGroup, mLayoutParams);
		}

		Drawable selectorDrawable = mActivity.getResources().getDrawable(
				selectorResource);

		RadioButton newRadioButton = (RadioButton) inflater.inflate(
				R.layout.sample_app_menu_group_radio_button, null, false);
		newRadioButton.setText(text);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			newRadioButton.setBackground(selectorDrawable);
		else
			newRadioButton.setBackgroundDrawable(selectorDrawable);

		newRadioButton.setTypeface(mFont);
		newRadioButton.setTextSize(mEntriesTextSize);
		newRadioButton.setPadding(mEntriesSidesPadding,
				mEntriesUpDownRadioPadding, mEntriesSidesPadding,
				mEntriesUpDownRadioPadding);
		newRadioButton.setCompoundDrawablePadding(0);
		newRadioButton.setTag(command);
		newRadioButton.setVisibility(View.VISIBLE);
		mRadioGroup.addView(newRadioButton, mLayoutParams);

		View divider = inflater.inflate(dividerResource, null);
		mRadioGroup.addView(divider, mLayoutParams);

		if (isSelected) {
			mRadioGroup.check(newRadioButton.getId());
		}

		// Set the listener after changing the UI state to avoid calling the radio button functionality when creating the menu
		newRadioButton.setOnCheckedChangeListener(mOnRadioCheckedListener);

		return mRadioGroup;
	}


	public LinearLayout getMenuLayout() {
		return mLayout;
	}

}
