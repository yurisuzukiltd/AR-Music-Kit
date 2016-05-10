package com.yurisuzuki.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yurisuzuki.MainActivity;
import com.yurisuzuki.playsound.R;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

/**
  * A simple {@link Fragment} subclass.
  * Use the {@link FragmentIntroBase#newInstance} factory method to
  * create an instance of this fragment.
  */
 public class FragmentIntroBase extends Fragment {


    IntroFragmentAdapter introFragmentAdapter;
    ViewPager viewPager;
    PageIndicator pageIndicator;
    public String type;

     /**
      * Use this factory method to create a new instance of
      * this fragment using the provided parameters.
      *
      * @param type Parameter 1.
      * @return A new instance of fragment FragmentIntroBase.
      */
     // TODO: Rename and change types and number of parameters
     public static FragmentIntroBase newInstance(String type) {
         FragmentIntroBase fragment = new FragmentIntroBase();
         Bundle args = new Bundle();
         args.putString("type", type);
         fragment.setArguments(args);
         return fragment;
     }

     public FragmentIntroBase() {
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
         // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_intro_base, container, false);

         return view;
     }

    @Override
         public void onActivityCreated(Bundle savedInstanceState) {
             super.onActivityCreated(savedInstanceState);

             if (savedInstanceState == null) {
                 viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
                 pageIndicator = (CirclePageIndicator) getActivity().findViewById(
                         R.id.indicator);

                 Button getStartedButton = (Button) getActivity().findViewById(
                         R.id.intro_get_started_button);


                 getStartedButton.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         ((MainActivity) getActivity()).jumpToCamera();

                     }
                 });

                 introFragmentAdapter = new IntroFragmentAdapter(this.getActivity()
                         .getFragmentManager());
                 introFragmentAdapter.type = this.type;
                 viewPager.setAdapter(introFragmentAdapter);

                 pageIndicator.setViewPager(viewPager);
                 introFragmentAdapter.notifyDataSetChanged();
             }

         }


     @Override
     public void onAttach(Activity activity) {
         super.onAttach(activity);
         try {
             //mListener = (OnFragmentInteractionListener) activity;
         } catch (ClassCastException e) {
             e.printStackTrace();
         }
     }

     @Override
     public void onDetach() {
         super.onDetach();
         //mListener = null;
     }



 }
