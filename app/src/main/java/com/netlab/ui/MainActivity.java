package com.netlab.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.netlab.actfreezer.R;
import com.netlab.servicelogger.ServiceLogger;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    private Button startServiceButton = null;
    private ToggleButton confButton = null;

    private final String TAG = "MainActivity";


    private SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            e.printStackTrace();
        }


        startServiceButton = (Button) findViewById(R.id.startServiceButton);

        startServiceButton.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View view) {
                                                      startService(new Intent(MainActivity.this, ServiceLogger.class));
                                                  }
                                              }
        );


        sharedPreferences = getSharedPreferences("actfreezer", Context.MODE_WORLD_READABLE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("actfreezer", GlobalSettings.getConf());
        editor.commit();

        confButton = (ToggleButton) findViewById(R.id.ConfToggleButton);
        //System.err.println(GlobalSettings.getConf());
        confButton.setChecked(GlobalSettings.getConf());
        confButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    Log.d(TAG, "" + checked);
                    GlobalSettings.setConf(checked);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("actfreezer", checked);
                    editor.commit();
                } else {
                    Log.d(TAG, "" + checked);
                    GlobalSettings.setConf(checked);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("actfreezer", checked);
                    editor.commit();
                }
            }
        });
        //startService(new Intent(this, ServiceLogger.class));
    }
}
