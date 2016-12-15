package com.hpsvse.keeponepx;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

public class KeepLiveActivity extends Activity {

	private static final String TAG = "kepp";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "KeepLiveActivity----onCreate!!!");
		
		Window window = getWindow();
		window.setGravity(Gravity.LEFT|Gravity.TOP);
		LayoutParams params = window.getAttributes();
		params.height = 1;
		params.width = 1;
		params.x = 0;
		params.y = 0;
		
		window.setAttributes(params);
		
		KeepLiveActivityManager.getInstance(this).setKeepLiveActivity(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "KeepLiveActivity----onDestroy!!!");
	}
	
}
