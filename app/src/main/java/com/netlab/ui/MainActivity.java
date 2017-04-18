package com.netlab.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.netlab.actfreezer.R;
import com.netlab.servicelogger.ServiceLogger;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    private Button startServiceButton = null;

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
        //startService(new Intent(this, ServiceLogger.class));
    }
}
