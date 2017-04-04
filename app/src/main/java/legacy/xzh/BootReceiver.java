package legacy.xzh;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.*;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhx on 2016/7/11.
 */
public class BootReceiver extends BroadcastReceiver {
    private static short flag=0;
    private String[] toolPackage={
    "com.github.shadowsocks",
    "com.speedsoftware.rootexplorer",
    "com.sohu.inputmethod.sogou",
    "de.robv.android.xposed.installer",
    "com.xzh.xposeddemo",
    "com.xzh.helloworld", "com.cgollner.systemmonitor.lite",
            "com.kingroot.kinguser","ccc71.bmw",
            "ccc71.at.free"
    };
    public boolean isTool(String app){
        for(int i=0;i<toolPackage.length;i++){
            if(app.equals(toolPackage[i]))
                return true;
        }
        return false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("xposed","*********************开机了,action is  "+intent.getAction()+"****");
        CsvWriter.createDir();
        ComponentName com=new ComponentName("com.xzh.xposeddemo","com.xzh.xposeddemo.DetectionService");
        Intent serviceIntent=new Intent();
        serviceIntent.setComponent(com);
        if(flag==0) {
            //读取手机上已安装app的receiver/service/pkgName/uid/appName这些信息
            final PackageManager pm = context.getApplicationContext().getPackageManager();
            //读取pkgName,appName和uid
            try {
                CsvWriter.createDir();
                CsvWriter.deleteFile("pkgName.txt");
                CsvWriter.deleteFile("serviceAcName.txt");
                CsvWriter.deleteFile("receiverAcName.txt");
//                CsvWriter.deleteFile("receiverName.txt");
                CsvWriter.deleteFile("oservice.txt");
                CsvWriter.deleteFile("oreceiver.txt");
                List<ApplicationInfo> appInfo;
                ServiceInfo[] serviceInfo;
                ActivityInfo[] receiverInfo;
                ProviderInfo[] providerInfos;
                boolean isSystem;
                appInfo = pm.getInstalledApplications(PackageManager.GET_META_DATA);
                //读取每个package以及该package包含的Service和receiver
                for (int i = 0; i < appInfo.size(); i++) {
                    isSystem=(appInfo.get(i).flags& ApplicationInfo.FLAG_SYSTEM) >0?true:false;
                    try {
                        android.content.pm.PackageInfo packageInfo=pm.getPackageInfo(appInfo.get(i).packageName,PackageManager.GET_RECEIVERS|PackageManager.GET_SERVICES|PackageManager.GET_PROVIDERS);
                        serviceInfo=packageInfo.services;
                        receiverInfo=packageInfo.receivers;
                        providerInfos=packageInfo.providers;
                        if((!isSystem)&&(!isTool(appInfo.get(i).packageName))) {
                            Log.i("test","=========="+appInfo.get(i).packageName);
                                try {
                                    if(serviceInfo!=null) {
                                        for (int j = 0; j < serviceInfo.length; j++) {
                                            Log.i("xposed", "===========" + appInfo.get(i).packageName + "/" + serviceInfo[j].name);
                                            CsvWriter.writeSDFile("oservice.txt", appInfo.get(i).packageName + "/" + serviceInfo[j].name + "\n");
                                        }
                                    }
                                    if(receiverInfo!=null) {
                                        for (int j = 0; j < receiverInfo.length; j++) {
                                            Log.i("xposed", "===========" + appInfo.get(i).packageName + "/" + receiverInfo[j].name);
                                            CsvWriter.writeSDFile("oreceiver.txt", appInfo.get(i).packageName + "/" + receiverInfo[j].name + "\n");
                                        }
                                    }
                                    if(providerInfos!=null) {
                                        for (int j = 0; j < providerInfos.length; j++) {
                                            Log.i("xposed", "===========" + appInfo.get(i).packageName + "/" + providerInfos[j].name);
                                            CsvWriter.writeSDFile("oreceiver.txt", appInfo.get(i).packageName + "/" + providerInfos[j].name + "\n");
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    CsvWriter.writeSDFile("pkgName.txt",appInfo.get(i).packageName + "," + getProgramNameByPackageName(context, appInfo.get(i).packageName) + ","
                            + appInfo.get(i).uid +","+isSystem+"\n");
                }

                //读取手机上app中manifest文件中的receiver和service信息
                ActionReader acReader;
                ReceiverReader reReader;
                ArrayList<IntentFilterInfo> serviceResult;
                ArrayList<IntentFilterInfo> receiverResult;
                Log.i("ManifestReader", "*****************开始读取");
                acReader = new ActionReader(context);
                serviceResult = acReader.load();
                for (int i = 0; i < serviceResult.size(); i++) {
                    CsvWriter.writeSDFile("serviceName.txt", getProgramNameByPackageName(context,serviceResult.get(i).componentInfo.packageInfo.packageName)+","+serviceResult.get(i).componentInfo.packageInfo.packageName + "," + serviceResult.get(i).componentInfo.componentName
                            + "," + serviceResult.get(i).action + "\n");
                }

                reReader = new ReceiverReader(context);
                receiverResult = reReader.load();
                CsvWriter.writeSDFile("receiverAcName.txt", receiverResult.get(0).componentInfo.packageInfo.packageName + "," + receiverResult.get(0).componentInfo.componentName
                        + "," + receiverResult.get(0).action + "\n");
/*                CsvWriter.writeSDFile("receiverName.txt", receiverResult.get(0).componentInfo.packageInfo.packageName + "," + receiverResult.get(0).componentInfo.componentName
                        + "\n");*/
                for (int i = 1; i < receiverResult.size(); i++) {
                    CsvWriter.writeSDFile("receiverAcName.txt", receiverResult.get(i).componentInfo.packageInfo.packageName + "," + receiverResult.get(i).componentInfo.componentName
                            + "," + receiverResult.get(i).action + "\n");
/*                    if (receiverResult.get(i).componentInfo.componentName != receiverResult.get(i - 1).componentInfo.componentName) {
                        CsvWriter.writeSDFile("receiverName.txt", receiverResult.get(i).componentInfo.packageInfo.packageName + "," + receiverResult.get(i).componentInfo.componentName
                                + "\n");
                    }*/
                }
                flag++;
                context.startService(serviceIntent);
                //AppNameGetter.init();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getProgramNameByPackageName(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        String name = null;
        try {
            name = pm.getApplicationLabel(
                    pm.getApplicationInfo(packageName,
                            PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }
}
