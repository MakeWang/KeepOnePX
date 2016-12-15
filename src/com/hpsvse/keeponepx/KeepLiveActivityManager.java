package com.hpsvse.keeponepx;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class KeepLiveActivityManager {
	private static KeepLiveActivityManager instance;
	private Context context;
	private WeakReference<Activity> activityInstance;

	public static KeepLiveActivityManager getInstance(Context context) {
		if(instance==null){
			instance = new KeepLiveActivityManager(context.getApplicationContext());
		}
		return instance;
	}
	
	private KeepLiveActivityManager(Context context) {
		this.context = context;
	}
	
	public void setKeepLiveActivity(Activity activity){
		activityInstance = new WeakReference<Activity>(activity);
	}

	public void startKeepLiveActivity() {
		Intent intent = new  Intent(context, KeepLiveActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	public void finishKeepLiveActivity() {
		if(activityInstance!=null&&activityInstance.get()!=null){
			Activity activity = activityInstance.get();
			activity.finish();
		}
	}

}
