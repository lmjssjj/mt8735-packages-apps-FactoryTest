package com.mediatek.factorymode;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;

import com.mediatek.factorymode.version.version;
import com.mediatek.factorymode.Utils;

import java.util.ArrayList;
import java.util.List;

// import dalvik.annotation.Signature;

public class FactoryMode extends Activity implements AdapterView.OnItemClickListener {
	public View.OnClickListener cl;
	final int[] itemString;
	private MyAdapter mAdapter;
	private GridView mGrid;
	private String TAG = "FactoryMode";
	private static final String LIGHT_CALI = "com.yyd.ircam.ProjectionTouchService";
	private List mListData;
	private SharedPreferences mSp = null;
	private PenCaliReceiver penCaliReceiver;

	public FactoryMode() {
		int[] arrayOfInt = { R.string.touchscreen_name, R.string.lcd_name, R.string.battery_name,
				R.string.speaker_name, R.string.microphone_name, R.string.wifi_name, R.string.bluetooth_name, R.string.telephone_name,
				R.string.backlight_name, R.string.memory_name,R.string.gsensor, R.string.gsensor_name, R.string.camera_name,
				R.string.motor, R.string.touch, R.string.screen,R.string.signal_lamp,R.string.infrared_camera,R.string.projection_lamp,
				R.string.otg,R.string.fall_arrest,R.string.projection_mic,R.string.five_mic,R.string.light_pen_cali};
		this.itemString = arrayOfInt;
	}
	//暂时取消主板gesnor ,R.string.projection_gsensor,R.string.projection_gsensor_cali
	private void SetColor(TextView paramTextView) {
		if (this.itemString.length == 0) {
			return;
		}
		SharedPreferences localSharedPreferences1 = getSharedPreferences("FactoryMode", 0);
		this.mSp = localSharedPreferences1;
		int localObject = 0;
		int i = this.itemString.length;
		while (true) {
			if (localObject >= i)
				break;
			Resources localResources = getResources();
			int j = this.itemString[localObject];
			String str1 = localResources.getString(j);
			String str2 = paramTextView.getText().toString();
			String str4;
			if (str1.equals(str2)) {
				SharedPreferences localSharedPreferences2 = this.mSp;
				int k = this.itemString[localObject];
				String str3 = getString(k);
				str4 = localSharedPreferences2.getString(str3, null);
				if (str4.equals("success")) {
					int l = getApplicationContext().getResources().getColor(R.color.Blue);
					paramTextView.setTextColor(l);
				} else if (str4.equals("default")) {
					int i1 = getApplicationContext().getResources().getColor(R.color.black);
					paramTextView.setTextColor(i1);
				} else if (str4.equals("failed")) {
					int i2 = getApplicationContext().getResources().getColor(R.color.Red);
					paramTextView.setTextColor(i2);
				}
			}
			++localObject;
		}
	}

	private List getData() {
		boolean bool1 = true;
		ArrayList localArrayList = new ArrayList();
		SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		int localObject = 0;
		while (true) {
			int i = this.itemString.length;
			if (localObject >= i) {
				break;
			}
			int j = this.itemString[localObject];
			String str1 = getString(j);
			boolean bool2 = localSharedPreferences.getBoolean(str1, bool1);
			if (bool1 == bool2) {
				int k = this.itemString[localObject];
				String str2 = getString(k);
				localArrayList.add(str2);
			}
			++localObject;
		}
		return localArrayList;
	}

	private void init() {
		String str1 = "default";
		SharedPreferences localSharedPreferences = getSharedPreferences("FactoryMode", 0);
		this.mSp = localSharedPreferences;
		SharedPreferences.Editor localEditor = this.mSp.edit();
		int localObject = 0;
		while (true) {
			int i = this.itemString.length;
			if (localObject >= i)
				break;
			int j = this.itemString[localObject];
			String str2 = getString(j);
			String exist = localSharedPreferences.getString(str2, null);
			if (exist == null)
				localEditor.putString(str2, str1);
			++localObject;
		}
		String str3 = getString(R.string.headsethook_name);
		localEditor.putString(str3, str1);
		localEditor.commit();
	}

	protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
		System.gc();
		Intent localIntent = new Intent();
		localIntent.setClassName(this, "com.mediatek.factorymode.Report");
		startActivity(localIntent);
	}

	public void onCreate(Bundle paramBundle) {
		Log.d(TAG, "onCreate");

		sendBroadcast(new Intent("com.yongyida.robot.FACTORYSTART"));
		sendBroadcast(new Intent("com.yydrobot.STOP"));

		requestWindowFeature(1);
		super.onCreate(paramBundle);
		setContentView(R.layout.main);

		init();
		
		/*注册光笔校准广播*/
		IntentFilter intentMicFilter=new IntentFilter();
        intentMicFilter.addAction("com.yydrobot.LIGHTPEN");
		penCaliReceiver=new PenCaliReceiver();
        registerReceiver(penCaliReceiver,intentMicFilter);
		
		/*注册光机重力感应校准广播*/
		IntentFilter intentCaliFilter=new IntentFilter();
        intentCaliFilter.addAction("com.yongyida.factorytest.caliIsSuccess");
        registerReceiver(caliBroadcastReceiver,intentCaliFilter);
		
		this.mGrid = (GridView) findViewById(R.id.main_grid);
		this.mListData = getData();
		this.mAdapter = new MyAdapter(this);
	}

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(penCaliReceiver);     	//注销光笔校准广播
		unregisterReceiver(caliBroadcastReceiver);  //注销光机重力感应广播
		
		sendBroadcast(new Intent("com.yongyida.robot.FACTORYCLOSE"));
		Process.killProcess(Process.myPid());
	}

	public void button001(View view) {
		onDestroy();
	}

	public void button002(View view) {
		Intent intent = new Intent().setClass(this, version.class);
		startActivity(intent);
	}

	public void onItemClick(AdapterView paramAdapterView, View paramView, int paramInt, long paramLong) {

		Intent localIntent = new Intent();
		localIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
		String str1 = (String) this.mListData.get(paramInt);
		String str2 = null;
		Log.d(TAG, "onItemClick");
		if (getString(R.string.speaker_name).equals(str1)) {
			str2 = "com.mediatek.factorymode.audio.AudioTest";
		} else if (getString(R.string.battery_name).equals(str1)) {
			str2 = "com.mediatek.factorymode.BatteryLog";
		} else if (getString(R.string.touchscreen_name).equals(str1)) {
			str2 = "com.mediatek.factorymode.touchscreen.TouchPadTest";
		} else if (getString(R.string.camera_name).equals(str1)) {
			str2 = "com.mediatek.factorymode.camera.CameraTest";
		} else if (getString(R.string.wifi_name).equals(str1)) {
			str2 = "com.mediatek.factorymode.wifi.WiFiTest";
		} else if (getString(R.string.bluetooth_name).equals(str1)) {
			str2 = "com.mediatek.factorymode.bluetooth.Bluetooth";
		} else if (getString(R.string.telephone_name).equals(str1)) {
			str2 = "com.mediatek.factorymode.signal.Signal";
		} else if (getString(R.string.backlight_name).equals(str1)) {
			str2 = "com.mediatek.factorymode.backlight.BackLight";
		} else if (getString(R.string.memory_name).equals(str1)) {
			str2 = "com.mediatek.factorymode.memory.Memory";
		} else if (getString(R.string.microphone_name).equals(str1)) {
			str2 = "com.mediatek.factorymode.microphone.MicRecorder";
		} else if (getString(R.string.gsensor_name).equals(str1)) {
			str2 = "com.mediatek.factorymode.sensor.GSensor";
		} else if (getString(R.string.msensor_name).equals(str1)) {
			str2 = "com.mediatek.factorymode.sensor.MSensor";
		} else if (getString(R.string.lsensor_name).equals(str1)) {
			str2 = "com.mediatek.factorymode.sensor.LSensor";
		} else if (getString(R.string.psensor_name).equals(str1)) {
			str2 = "com.mediatek.factorymode.sensor.PSensor";
		} else if (getString(R.string.sdcard_name).equals(str1)) {
			str2 = "com.mediatek.factorymode.sdcard.SDCard";
		} else if (getString(R.string.lcd_name).equals(str1)) {
			str2 = "com.mediatek.factorymode.lcd.LCD";
		} else if (getString(R.string.subcamera_name).equals(str1)) {
			str2 = "com.mediatek.factorymode.camera.SubCamera";
		} else if (getString(R.string.led_name).equals(str1)) {
			str2 = "com.mediatek.factorymode.led.Led";
		} else if (getString(R.string.version).equals(str1)) {
			str2 = "com.mediatek.factorymode.version.version";
		} else if (getString(R.string.motor).equals(str1)) {
			str2 = "com.mediatek.factorymode.motor.MotorActivity";
		} else if (getString(R.string.touch).equals(str1)) {
			str2 = "com.mediatek.factorymode.touch.TouchActivity";
		} else if (getString(R.string.gsensor).equals(str1)) {
			sendBroadcast(new Intent("com.yydrobot.STARTGSENSOR").setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES));
		} else if(getString(R.string.screen).equals(str1)){
			str2="com.mediatek.factorymode.screen.ChangeScreen";
		} else if(getString(R.string.signal_lamp).equals(str1)){
			str2="com.mediatek.factorymode.lamp.SignalLamp";
		}else if(getString(R.string.infrared_camera).equals(str1)){
			str2="com.mediatek.factorymode.camera.InfraredCamera";
		}else if(getString(R.string.projection_lamp).equals(str1)){
			str2="com.mediatek.factorymode.lamp.ProjectionLamp";
		}else if(getString(R.string.otg).equals(str1)){
			str2="com.mediatek.factorymode.otg.OTG";
		}else if(getString(R.string.fall_arrest).equals(str1)){
			str2="com.mediatek.factorymode.fall.FallArrest";
		}else if(getString(R.string.touch_five_point).equals(str1)){
			str2="com.mediatek.factorymode.touchscreen.TouchFivePoint";
		}else if(getString(R.string.projection_gsensor).equals(str1)){
			str2="com.mediatek.factorymode.sensor.ProjectionGSensor";
		}else if(getString(R.string.projection_gsensor_cali).equals(str1)){
			str2="com.mediatek.factorymode.sensor.ProjectionGSensorCali";
		}else if(getString(R.string.projection_mic).equals(str1)){
			str2="com.mediatek.factorymode.microphone.ProjectionMic";
		}else if(getString(R.string.five_mic).equals(str1)){
			str2="com.mediatek.factorymode.microphone.FiveMIC";
		}else if(getString(R.string.light_pen_cali).equals(str1)){
			Intent intent=getPackageManager().getLaunchIntentForPackage("com.yongyida.robot.lightpentest");
			if(isRunning(LIGHT_CALI)){
				Toast.makeText(this, "工厂测试前请先关闭投影触控功能！", Toast.LENGTH_LONG).show();
			}else{
				startActivity(intent);
			}
		}
		if (str2 != null) {
			localIntent.setClassName(this, str2);
			startActivity(localIntent);
		}
	}

	protected void onResume() {
		super.onResume();
		Log.e(TAG, "onResume");
		this.mGrid.setAdapter(this.mAdapter);
		this.mGrid.setOnItemClickListener(this);
	}

	public class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter(Context arg2) {
			LayoutInflater localLayoutInflater = LayoutInflater.from(arg2);
			this.mInflater = localLayoutInflater;
		}

		public MyAdapter(FactoryMode paramInt, int arg3) {
		}

		public int getCount() {
			if (FactoryMode.this.mListData == null)
				return 0;
			return FactoryMode.this.mListData.size();
		}

		public Object getItem(int paramInt) {
			return Integer.valueOf(paramInt);
		}

		public long getItemId(int paramInt) {
			return paramInt;
		}
		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			View localView = this.mInflater.inflate(R.layout.main_grid, null);
			TextView localTextView = (TextView) localView.findViewById(R.id.factor_button);
			localTextView.setTextSize(20);
			CharSequence localCharSequence = (CharSequence) FactoryMode.this.mListData.get(paramInt);
			localTextView.setText(localCharSequence);
			FactoryMode.this.SetColor(localTextView);
			int pWidth = mGrid.getWidth();
			int pHight = mGrid.getHeight();
			GridView.LayoutParams params = new GridView.LayoutParams(pWidth / 4, pHight / 7);
			localView.setLayoutParams(params);
			return localView;
		}
	}
	/*光笔校准前判断是否已经开启*/
	private boolean isRunning(String name) {
        ActivityManager myManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(40);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(name)) {
                return true;
            }
        }
        return false;
    }
	/*光笔校准成功或失败广播*/
	class PenCaliReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("com.yydrobot.LIGHTPEN")){
                if(intent.getBooleanExtra("result",false)){
                    SharedPreferences sharedPreferences = context.getSharedPreferences("FactoryMode", 0);
                    Utils.SetPreferences(context, sharedPreferences, R.string.light_pen_cali, "success");
                }else {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("FactoryMode", 0);
                    Utils.SetPreferences(context, sharedPreferences, R.string.light_pen_cali, "failed");
                }
            }
        }
	}
	/*光机校准成功或失败广播*/
	private BroadcastReceiver caliBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.yongyida.factorytest.caliIsSuccess")){
                if(intent.getBooleanExtra("result",false)){
                    SharedPreferences sharedPreferences = context.getSharedPreferences("FactoryMode", 0);
                    Utils.SetPreferences(context, sharedPreferences, R.string.gsensor, "success");
                }else {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("FactoryMode", 0);
                    Utils.SetPreferences(context, sharedPreferences, R.string.gsensor, "failed");
                }
            }
        }
    };
	
}
