/*
 *  Author(s): Takamitsu Mizutori, Goldrush Computing Inc.
 */

package com.yurisuzuki.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yurisuzuki.playsound.R;

public final class FragmentInstruction extends Fragment {

	private int illutDrawableResourceId;
	private int descriptionStringResourceId;


	private int containerId;
	private int containerWidth;



	public static FragmentInstruction newInstance(int illutDrawableResourceId, int descriptionStringResourceId) {
		FragmentInstruction fragment = new FragmentInstruction();
		Bundle args = new Bundle();
		args.putInt("illutDrawableResourceId", illutDrawableResourceId);
		args.putInt("descriptionStringResourceId", descriptionStringResourceId);
		fragment.setArguments(args);
		return fragment;
	}

	public FragmentInstruction() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			illutDrawableResourceId = getArguments().getInt("illutDrawableResourceId");
			descriptionStringResourceId = getArguments().getInt("descriptionStringResourceId");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if(container != null){
			containerId = container.getId();
			//＊＊＊コンテナの幅を保存しておく＊＊＊
			containerWidth = container.getWidth();
		}


		View layout = inflater.inflate(R.layout.fragment_instruction, container, false);

		ImageView illustView = (ImageView)layout.findViewById(R.id.inst_illust);
		TextView descriptionView = (TextView)layout.findViewById(R.id.inst_description);

		Bitmap illust = BitmapFactory.decodeResource(getResources(), illutDrawableResourceId);
		String text = getResources().getString(descriptionStringResourceId);

		illustView.setImageBitmap(illust);
		descriptionView.setText(text);


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

	@Override
	public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
		//＊＊＊FragmentTransactionにTRANSIT_FRAGMENT_OPENを指定しておくと、遷移時にはTRANSIT_FRAGMENT_OPEN、Back時にはTRANSIT_FRAGMENT_CLOSEが渡される＊＊＊
		if (transit == FragmentTransaction.TRANSIT_FRAGMENT_OPEN) {
			if (enter) {
				return ObjectAnimator.ofFloat(getView(), "x", containerWidth, 0.0f);
			} else {
				return ObjectAnimator.ofFloat(getView(), "x", 0.0f, -containerWidth);
			}
		} else if (transit == FragmentTransaction.TRANSIT_FRAGMENT_CLOSE) {
			if (enter) {
				return ObjectAnimator.ofFloat(getView(), "x", -containerWidth, 0.0f);
			} else {
				return ObjectAnimator.ofFloat(getView(), "x", 0.0f, containerWidth);
			}
		}

		return super.onCreateAnimator(transit, enter, nextAnim);
	}
}
