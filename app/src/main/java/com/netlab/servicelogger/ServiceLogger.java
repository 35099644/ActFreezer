package com.netlab.servicelogger;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import com.netlab.ui.Activation;
import com.netlab.ui.GlobalSettings;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by ZQ on 2017/4/15.
 */

public class ServiceLogger extends Service {

    final String TAG = "ServiceLogger";


    boolean running = true;

    NotificationManager manager = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Log.d(TAG, "onBind!");

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand!");

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        new DialogThread().start();


        //ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Activity.ACTIVITY_SERVICE);

        //List<ActivityManager.RunningServiceInfo> service_list = am.getRunningServices(200);

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

        //new ServiceCleaner().start();

        //am.killBackgroundProcesses(TAG);
        return super.onStartCommand(intent, flags, startId);
    }


    private void buildDecisionDialgo(Activation act) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        // Add the buttons
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                GlobalSettings.addUserDecision(true);
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                GlobalSettings.addUserDecision(false);
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();

        Log.d(TAG, "call dialog.show();");
    }

    private void buildNotification(Activation act)
    {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setAutoCancel(true);

        builder.setContentTitle("Activation");
        builder.setContentText("Activation");
        builder.setDefaults(Notification.DEFAULT_SOUND| Notification.DEFAULT_VIBRATE);

        manager.notify(0, builder.build());
    }



    class DialogThread extends Thread{
        public void run()
        {
            while (running) {
                Log.d(TAG, "Take new cross-app activation");
                Activation act = GlobalSettings.takeActivation();

                Log.d(TAG, "Got new cross-app activation");

                buildDecisionDialgo(act);
                buildNotification(act);

                boolean decision = GlobalSettings.takeUserDecision();
                GlobalSettings.addDecision(decision);
            }
        }
    }
}
