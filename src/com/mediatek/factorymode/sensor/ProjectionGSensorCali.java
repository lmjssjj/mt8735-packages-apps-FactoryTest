package com.mediatek.factorymode.sensor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;

/**
 * Created by sunyibin on 2016/6/24 0024.
 */
public class ProjectionGSensorCali extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.projection_gsensor_cali);
    }
    public void button001(View view){
        SharedPreferences sharedPreferences = getSharedPreferences("FactoryMode", 0);
        Utils.SetPreferences(this, sharedPreferences, R.string.projection_gsensor_cali, "success");
        finish();
    }
    public void button002(View view){
        SharedPreferences sharedPreferences = getSharedPreferences("FactoryMode", 0);
        Utils.SetPreferences(this, sharedPreferences, R.string.projection_gsensor_cali, "failed");
        finish();
    }
}
