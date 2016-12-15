# KeepOnePX</br>
QQ采取在锁屏的时候启动一个1个像素的Activity，当用户解锁以后将这个Activity结束掉（顺便同时把自己的核心服务再开启一次）。被用户发现了就不好了。进行进程保护防止被系统干掉，这种方式其实就是提高进程的优先级。</br>

#进程的优先级（越往后越容易被系统杀死）</br>
1、前台进程；Foreground process。</br>
     1）用户正在交互的Activity（onResume）</br>
     2) 当某个Service绑定正在交互的Activity</br>
     3) 被主要调用前台Service（startForeground()）</br>
     4) 组件正在执行的生命周期回调（onCreate()/onStart()/onDestroy()）</br>
     5）BroadcastReceiver 正在执行onReceive();</br>
     
2、可见进程；Visible process</br>
      1) 我们的Activity处在onPause()（没有进入onStop()）</br>
	    2) 绑定到前台Activity的Service。</br>
      
3、服务进程；Service process</br>
      简单的startService()启动。</br>

4、后台进程；Background process</br>
       对用户没有直接影响的进程----Activity出于onStop()的时候。</br>
	     android:process=":xxx"</br>

5、空进程；Empty process</br>
       不含有任何的活动的组件。（android设计的，为了第二次启动更快，采取的一个权衡）</br>
       
# MainActivity</br>
```java
public class MainActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent intent = new Intent(this, MyService.class);
		startService(intent);
	}
	
	//按钮点击
	public void btn_cal(View v){
		Intent intent = new Intent(this, KeepLiveActivity.class);
		startActivity(intent);
		finish();
	}
	
}
```

#KeepLiveActivity</br>
显示一个像素的Activity，注意，这个界面不能设置主题<br>
```java
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
```
#不设置主题的样式</br>
```java
<style name="KeepLiveStyle">
        <item name="android:windowBackground">@android:color/transparent</item>
        <item name="android:windowFrame">@null</item>
        <item name="android:windowNoTitle">true</item>
        <item name="android:windowIsFloating">true</item>
        <item name="android:backgroundDimEnabled">false</item>
        <item name="android:windowContentOverlay">@null</item>
        <item name="android:windowIsTranslucent">true</item>
        <item name="android:windowAnimationStyle">@null</item>
        <item name="android:windowDisablePreview">true</item>
        <item name="android:windowNoDisplay">false</item>
    </style>
```

#MyService</br>
执行锁屏的服务类</br>
```java
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
				//解锁
				
			}
			
			@Override
			public void onScreenOn() {
				// 开屏---finish这个一个像素的Activity
				KeepLiveActivityManager.getInstance(MyService.this).finishKeepLiveActivity();
			}
			
			@Override
			public void onScreenOff() {
				// 锁屏---启动一个像素的Activity
				KeepLiveActivityManager.getInstance(MyService.this).startKeepLiveActivity();
			}
		});
		
		super.onCreate();
	}

}
```

#ScreenListener</br>
监听服务的广播类</br>
```java
public class ScreenListener {
    private Context mContext;
    private ScreenBroadcastReceiver mScreenReceiver;
    private ScreenStateListener mScreenStateListener;

    public ScreenListener(Context context) {
        mContext = context;
        mScreenReceiver = new ScreenBroadcastReceiver();
    }

    /**
     * screen状态广播接收者
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;
        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                mScreenStateListener.onScreenOn();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                mScreenStateListener.onScreenOff();
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
                mScreenStateListener.onUserPresent();
            }
        }
    }

    /**
     * 开始监听screen状态
     * 
     * @param listener
     */
    public void begin(ScreenStateListener listener) {
        mScreenStateListener = listener;
        registerListener();
        getScreenState();
    }

    /**
     * 获取screen状态
     */
    private void getScreenState() {
        PowerManager manager = (PowerManager) mContext
                .getSystemService(Context.POWER_SERVICE);
        if (manager.isScreenOn()) {
            if (mScreenStateListener != null) {
                mScreenStateListener.onScreenOn();
            }
        } else {
            if (mScreenStateListener != null) {
                mScreenStateListener.onScreenOff();
            }
        }
    }

    /**
     * 停止screen状态监听
     */
    public void unregisterListener() {
        mContext.unregisterReceiver(mScreenReceiver);
    }

    /**
     * 启动screen状态广播接收器
     */
    private void registerListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(mScreenReceiver, filter);
    }

    public interface ScreenStateListener {// 返回给调用者屏幕状态信息
        public void onScreenOn();

        public void onScreenOff();

        public void onUserPresent();
    }
}
```
#KeepLiveActivityManager</br>
处理Acticity的管理类</br>
```java
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

```
