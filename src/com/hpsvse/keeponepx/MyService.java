package com.hpsvse.keeponepx;

import com.hpsvse.keeponepx.ScreenListener.ScreenStateListener;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		
		ScreenListener listener = new ScreenListener(this);
		listener.begin(new ScreenStateListener() {
			
			@Override
			public void onUserPresent() {
				//����
				
			}
			
			@Override
			public void onScreenOn() {
				// ����---finish���һ�����ص�Activity
				KeepLiveActivityManager.getInstance(MyService.this).finishKeepLiveActivity();
			}
			
			@Override
			public void onScreenOff() {
				// ����---����һ�����ص�Activity
				KeepLiveActivityManager.getInstance(MyService.this).startKeepLiveActivity();
			}
		});
		
		super.onCreate();
	}

}
