package com.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.vuforia.samples.VuforiaSamples.R;
import com.vuforia.samples.VuforiaSamples.app.ImageTargets.ImageTargetActivity;
import com.vuforia.samples.VuforiaSamples.app.VirtualButtons.VirtualButtonActivity;


// This activity starts activities which demonstrate the Vuforia features
public class ActivityLauncher extends ListActivity {
	private String mActivities[] = {"Paino Marker Test", "Virtual Buttons Demo"};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
				R.layout.activities_list_text_view, mActivities);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activities_list);
		setListAdapter(adapter);
	}


	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		Intent intent = null;

		switch (position) {
			case 0:
				intent = new Intent(this, ImageTargetActivity.class);
				break;
			case 1:
				intent = new Intent(this, VirtualButtonActivity.class);
				break;
		}

		startActivity(intent);
	}
}
