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

import com.yurisuzuki.ActivityIntro;
import com.yurisuzuki.playsound.R;

public final class FragmentInstruction extends Fragment {
	public String type;
	private int containerId;
	private int containerWidth;

	private Bitmap guitarIcon;
	private Bitmap pianoIcon;
	private Bitmap musicBoxIcon;

	private Bitmap guitarIllust;
	private Bitmap pianoIllust;
	private Bitmap musicBoxIllust;



	public static FragmentInstruction newInstance(String type) {
		FragmentInstruction fragment = new FragmentInstruction();
		Bundle args = new Bundle();
		args.putString("type", type);
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
			type = getArguments().getString("type");
		}

		guitarIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.char_guitar);
		pianoIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.char_piano);
		musicBoxIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.char_music_box);

		guitarIllust = BitmapFactory.decodeResource(this.getResources(), R.drawable.illust_guitar);
		pianoIllust = BitmapFactory.decodeResource(this.getResources(), R.drawable.illust_piano);
		musicBoxIllust = BitmapFactory.decodeResource(this.getResources(), R.drawable.illust_music_box);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if(container != null){
			containerId = container.getId();
			//＊＊＊コンテナの幅を保存しておく＊＊＊
			containerWidth = container.getWidth();
		}


		View layout = (View) inflater.inflate(R.layout.fragment_instruction, container, false);

		TextView tilteView = (TextView)layout.findViewById(R.id.inst_title);
		ImageView iconView = (ImageView)layout.findViewById(R.id.inst_icon);
		ImageView illustView = (ImageView)layout.findViewById(R.id.inst_illust);
		TextView descriptionView = (TextView)layout.findViewById(R.id.inst_description);

		if(type != null){
			if(type.equals("guitar")){
				tilteView.setText("Guitar");
				iconView.setImageBitmap(guitarIcon);
				illustView.setImageBitmap(guitarIllust);
				descriptionView.setText("-------------------------------");
			}else if(type.equals("piano")){
				tilteView.setText("Piano");
				iconView.setImageBitmap(pianoIcon);
				illustView.setImageBitmap(pianoIllust);
				descriptionView.setText("-------------------------------");
			}else if(type.equals("musicbox")){
				tilteView.setText("Music Box");
				iconView.setImageBitmap(musicBoxIcon);
				illustView.setImageBitmap(musicBoxIllust);
				descriptionView.setText("-------------------------------");
			}
		}

		illustView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((ActivityIntro)FragmentInstruction.this.getActivity()).jumpToCamera();
			}
		});
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
