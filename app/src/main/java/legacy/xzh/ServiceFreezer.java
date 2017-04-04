package legacy.xzh;

import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhx on 2016/8/11.
 */
//指数退避实现
public class ServiceFreezer {
    static int busyPeriod=1;//busy时间
    static int initialIdlePeriod=1;//初始idle周期
    private static Process proc = null;
    private static Runtime rt = null;
    private static String cmd;
    private static int serviceIdleTime[];
    private static boolean foreground[];//记录service所在app是否在前台或者该service是否被bind
    private static ArrayList<String> packageName;//记录service所在的package
    private static Map<String,Integer> serviceToIndex=new HashMap<>();//service名与index键值对
    private static Map<String,Integer> packageToIndex=new HashMap<>();//package与index键值对
    private static ArrayList<String> runningServices;
    private static ArrayList<String> lastRunningServices;
    private static ArrayList<String> serviceAndPackage;
    private static boolean inited=false;

    //构造函数
    ServiceFreezer(){
        if(!inited) {
            packageName = new ArrayList<>();
            serviceAndPackage = new ArrayList<>();
            runningServices = new ArrayList<>();
            lastRunningServices = new ArrayList<>();
            CsvWriter.readSDFile(serviceAndPackage, "oservice.txt");
            String splitLine[];
            serviceIdleTime = new int[serviceAndPackage.size()];
            foreground = new boolean[serviceAndPackage.size()];
            for (int i = 0; i < serviceAndPackage.size(); i++) {
                splitLine = serviceAndPackage.get(i).split("/");
                packageName.add(splitLine[0]);
                serviceToIndex.put(serviceAndPackage.get(i), i);
                if (!(packageToIndex.keySet() != null && packageToIndex.keySet().contains(packageName.get(i)))) {
                    packageToIndex.put(packageName.get(i), i);
                }
                serviceIdleTime[i] = 1;
                foreground[i] = false;
            }
            inited=true;
        }
    }

    public static void startService(String serviceAndPackage){

    }

    public static void cancelRunningTask(){
        //停止running的service，也要停止其task
        for(String item:lastRunningServices){
            if(!runningServices.contains(item))
                FreezerThread.cancelTask(serviceToIndex.get(item));
        }
    }

    public static void startTasks(){
        //对新加入的running service，如果不是属于前台app或者被bind,则开始其task
        for(String item:runningServices){
            if(serviceToIndex.containsKey(item)) {
                if (!foreground[serviceToIndex.get(item)]) {
                    if (!lastRunningServices.contains(item)) {
                        FreezerThread.startTask(serviceToIndex.get(item));
                    }
                }
            }
        }
    }

    public static void setRunningServices(ArrayList<String> runningserviceArgs){
        if(lastRunningServices!=null)
            lastRunningServices.clear();
        if(runningServices!=null) {
            for(String item:runningServices)
                lastRunningServices.add(item);
            runningServices.clear();
        }
        for(String serviceInArgs:runningserviceArgs){
            runningServices.add(serviceInArgs);
        }
    }

    public static boolean isRunningService(String serviceToJudge){
        return runningServices.contains(serviceToJudge);
    }

    public static int getServiceNum(){
        return serviceAndPackage.size();
    }
    //设置serviceRunningState
    public static void setRunningState(String serviceName,boolean running){
        if(serviceName!=null&&serviceToIndex.keySet().contains(serviceName)) {
            foreground[serviceToIndex.get(serviceName)] = running;
            if(running){
                FreezerThread.cancelTask(serviceToIndex.get(serviceName));
            }
        }
    }

    //
    public static void setRunningStateByPackage(String packagename,boolean running){
        if(packagename!=null&&packageToIndex.keySet().contains(packagename)) {
            int index = packageToIndex.get(packagename);
            if (packageName.get(index) == packagename) {
                if(running)
                    unfreezeService(index);
                setRunningState(serviceAndPackage.get(index), running);
                setServiceIdleTime(index,1);
                //freezerThread开始运行
                if (running) {
                    FreezerThread.cancelTask(index);
                }
            } else
                return;
        }else
            return;
    }



    //获取serviceRunningState
    public static boolean getRunningState(String serviceName){
        if(serviceName!=null&&serviceToIndex.keySet().contains(serviceName))
            return foreground[serviceToIndex.get(serviceName)];
        return true;
    }

    //获取serviceIdleTime
    public static int getServiceIdleTime(String serviceName){
        if(serviceName!=null&&serviceToIndex.keySet().contains(serviceName))
            return serviceIdleTime[serviceToIndex.get(serviceName)];
        return 1;
    }
    //设置serviceIdleTime
    public static void setServiceIdleTime(String serviceName,int time){
        if(serviceName!=null&&serviceToIndex.keySet().contains(serviceName))
            serviceIdleTime[serviceToIndex.get(serviceName)]=time;
    }

    //获取serviceIdleTime
    public static int getServiceIdleTime(int index){
        if (index<serviceAndPackage.size()&&index>=0)
            return serviceIdleTime[index];
        return 1;
    }
    //设置serviceIdleTime
    public static void setServiceIdleTime(int index,int time){
        if (index<serviceAndPackage.size()&&index>=0)
            serviceIdleTime[index]=time;
    }

    //冻结service
    public static void freezeService(String packageName,String serviceName){
        Log.i("freezer","calling freeze service");
        //如果不是running状态
        if(packageToIndex.keySet().contains(packageName)&&serviceAndPackage.contains(serviceName)) {
            if (!foreground[serviceToIndex.get(serviceName)]) {
                cmd = "su -c pm disable " + packageName + "/" + serviceName;
                try {
                    rt = Runtime.getRuntime();
                    proc = rt.exec(cmd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //冻结service
    public static void freezeService(int index){
        //如果不是running状态
        Log.i("freezer","calling freeze service");
        if(index>=0&&index<serviceAndPackage.size()) {
            if (!foreground[index]) {
                cmd = "su -c pm disable " + serviceAndPackage.get(index);
                try {
                    rt = Runtime.getRuntime();
                    proc = rt.exec(cmd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //解冻service
    public static void unfreezeService(String packageName,String serviceName){
        Log.i("freezer","calling unfreeze service");
        if(packageToIndex.keySet().contains(packageName)&&serviceAndPackage.contains(serviceName)) {
            cmd = "su -c pm enable " + packageName + "/" + serviceName;
            try {
                rt = Runtime.getRuntime();
                proc = rt.exec(cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //解冻service
    public static void unfreezeService(int index){
        Log.i("freezer","calling unfreeze service");
        if(index>=0&&index<serviceAndPackage.size()) {
            cmd = "su -c pm enable " + serviceAndPackage.get(index);
            try {
                rt = Runtime.getRuntime();
                proc = rt.exec(cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
