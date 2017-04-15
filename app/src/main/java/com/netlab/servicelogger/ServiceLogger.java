package com.netlab.servicelogger;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * Created by ZQ on 2017/4/15.
 */

public class ServiceLogger extends Service{

    final String TAG = "ServiceLogger";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Log.d(TAG, "onBind!");

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand!");
        ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Activity.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> service_list = am.getRunningServices(200);

//        for(ActivityManager.RunningServiceInfo info :service_list)
//        {
//            //Log.d(TAG,info.process);
//            if(!(info.process.contains("android")||info.process.contains("system")||info.process.contains("google")||info.process.contains("netlab")))
//            {
//                Log.d(TAG,"kill "+info.process);
//
//                try {
//                    Process.killProcess(info.pid);
//                    java.lang.Process rootProcess = Runtime.getRuntime().exec(new String[]{ "su" , "-c", "kill -9", ""+info.pid });
//                    rootProcess = Runtime.getRuntime().exec("am force-stop "+info.process);
//                    Log.d(TAG,"am force-stop "+info.process);
//                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(rootProcess.getOutputStream()), 2048);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        new ServiceCleaner().start();

        //am.killBackgroundProcesses(TAG);
        return super.onStartCommand(intent, flags, startId);
    }
}
