package com.netlab.servicelogger;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.netlab.util.Tools;

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

        new LoggerThread().start();

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * run every one second to log the services running in the background.
     */
    class LoggerThread extends Thread
    {
        public void run()
        {
            Tools.initialize("/sdcard/actfreezer_log.csv", false);


            while(true)
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Activity.ACTIVITY_SERVICE);

                List<ActivityManager.RunningServiceInfo> service_list = am.getRunningServices(200);

                for(ActivityManager.RunningServiceInfo info :service_list)
                {
                    Tools.writeToLog(info.process+","+ SystemClock.elapsedRealtime());
                    Log.d(TAG,info.process+","+ SystemClock.elapsedRealtime());
                }
            }



//            {
                //Log.d(TAG,info.process);
//                if(!(info.process.contains("android")||info.process.contains("system")||info.process.contains("google")||info.process.contains("netlab")))
//                {
//
//                }
//            }
        }

    }
}
