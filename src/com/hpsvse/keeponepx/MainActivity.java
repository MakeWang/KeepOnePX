package com.hpsvse.keeponepx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent intent = new Intent(this, MyService.class);
		startService(intent);
	}
	
	public void btn_cal(View v){
		Intent intent = new Intent(this, KeepLiveActivity.class);
		startActivity(intent);
		finish();
	}
	
}
