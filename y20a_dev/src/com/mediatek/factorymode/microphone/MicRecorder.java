package com.mediatek.factorymode.microphone;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;
import com.mediatek.factorymode.VUMeter;
import java.io.File;
import java.io.IOException;

public class MicRecorder extends Activity implements View.OnClickListener {

	public Handler h;
	private Button mBtMicFailed;
	private Button mBtMicOk;
	private MediaPlayer mPlayer;
	private Button mRecord;
	private MediaRecorder mRecorder;
	public SharedPreferences mSp;

	VUMeter mVUMeter;
	Runnable ra;

	class MicRecorder1 implements Runnable {
		public void run() {
			mVUMeter.invalidate();
			h.postDelayed(this, 100L);
		}
	}

	public MicRecorder() {
		this.h = new Handler();
		this.ra = new MicRecorder1();
	}

	private void start() {
		this.h.post(this.ra);
		if (this.mPlayer != null)
			this.mPlayer.stop();
		if (!Environment.getExternalStorageState().equals("mounted"))
			this.mRecord.setText(R.string.sdcard_tips_failed);
		try {
			this.mRecorder = new MediaRecorder();
			this.mRecorder.setAudioSource(1);
			this.mRecorder.setOutputFormat(1);
			this.mRecorder.setAudioEncoder(3);
			this.mVUMeter.setRecorder(this.mRecorder);
			StringBuilder localStringBuilder = new StringBuilder();
			String str = null;
			File localFile = Environment.getExternalStorageDirectory();
			localStringBuilder.append(localFile).append(File.separator).append("test.mp3");
			str = localStringBuilder.toString();
			if (!new File(str).exists())
				new File(str).createNewFile();
			this.mRecorder.setOutputFile(str);
			this.mRecorder.prepare();
			this.mRecorder.start();
			this.mRecord.setTag("ing");
			this.mRecord.setText(R.string.Mic_stop);
		} catch (Exception localException) {
			String str3 = localException.getMessage();
			Toast.makeText(this, str3, Toast.LENGTH_SHORT);
			this.mRecord.setTag("ing");
		}
	}

	private void stopAndSave() {
		this.h.removeCallbacks(this.ra);
		this.mRecorder.stop();
		this.mRecorder.release();
		this.mRecorder = null;
		this.mRecord.setText(R.string.Mic_start);
		this.mRecord.setTag("");
		this.mVUMeter.SetCurrentAngle(0);
		try {
			MediaPlayer localMediaPlayer = new MediaPlayer();
			this.mPlayer = localMediaPlayer;
			this.mPlayer.setDataSource("/sdcard/test.mp3");
			this.mPlayer.prepare();
			this.mPlayer.start();
		} catch (IllegalArgumentException localIllegalArgumentException) {
			localIllegalArgumentException.printStackTrace();
		} catch (IllegalStateException localIllegalStateException) {
			localIllegalStateException.printStackTrace();
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
	}

	public void onClick(View paramView) {
		SharedPreferences localSharedPreferences = this.mSp;
		String str1 = Environment.getExternalStorageState();
		String str2 = "mounted";
		if (paramView.getId() == this.mBtMicOk.getId()) {
			this.mBtMicFailed.setBackgroundColor(getResources().getColor(R.color.gray));
			this.mBtMicOk.setBackgroundColor(getResources().getColor(R.color.Green));
			Utils.SetPreferences(this, localSharedPreferences, R.string.microphone_name, "success");
			finish();
		}

		if (paramView.getId() == this.mBtMicFailed.getId()) {
			this.mBtMicFailed.setBackgroundColor(getResources().getColor(R.color.Red));
			this.mBtMicOk.setBackgroundColor(getResources().getColor(R.color.gray));
			Utils.SetPreferences(this, localSharedPreferences, R.string.microphone_name, "failed");
			finish();
		}

		if(isFastClick(3000)){
			return;
		}
		if (paramView.getId() == this.mRecord.getId()) {
			if (str1.equals(str2)) {
				if ((this.mRecord.getTag() != null) && (this.mRecord.getTag().equals("ing")))
					stopAndSave();
				else
					start();
			} else {
				this.mRecord.setText(R.string.sdcard_tips_failed);
			}
		}
	}

	public void onCreate(Bundle paramBundle) {

		super.onCreate(paramBundle);
		setContentView(R.layout.micrecorder);

		this.mSp = getSharedPreferences("FactoryMode", 0);

		this.mRecord = (Button) findViewById(R.id.mic_bt_start);
		this.mRecord.setOnClickListener(this);
		this.mBtMicOk = (Button) findViewById(R.id.mic_bt_ok);
		this.mBtMicOk.setOnClickListener(this);
		this.mBtMicFailed = (Button) findViewById(R.id.mic_bt_failed);
		this.mBtMicFailed.setOnClickListener(this);

		this.mVUMeter = (VUMeter) findViewById(R.id.uvMeter);

	}

	protected void onDestroy() {
		super.onDestroy();
		new File("/sdcard/test.mp3").delete();
		if (this.mPlayer != null)
			this.mPlayer.stop();
		if (this.mRecorder != null)
			this.mRecorder.stop();
	}

	private static long lastClickTime;
	public static boolean isFastClick(long ClickIntervalTime) {
		long ClickingTime = System.currentTimeMillis();
		if ( ClickingTime - lastClickTime < ClickIntervalTime) {
			return true;
		}
		lastClickTime = ClickingTime;
		return false;
	}
}