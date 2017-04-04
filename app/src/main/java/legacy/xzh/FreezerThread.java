package legacy.xzh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhx on 2016/8/11.
 */
public class FreezerThread extends Thread {
    private static ServiceFreezer serviceFreezer=new ServiceFreezer();
    private static int busyPeriod=20;//busy时间
    private static int idlePeriod=20;//初始idle周期
    private static FreezerScheduledTask[] tasks;
    private static ScheduledFuture<?>[] handlers;
    public static boolean started=false;
    public static ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    @Override
    public void run() {
        Log.i("xposed","**************Freezer Thread Started");
        tasks=new FreezerScheduledTask[serviceFreezer.getServiceNum()];
        handlers=new ScheduledFuture[serviceFreezer.getServiceNum()];
        for(int i=0;i<ServiceFreezer.getServiceNum();i++){
            Log.i("freezer","index is "+i);
            tasks[i]=new FreezerScheduledTask();
            tasks[i].setIndex(i);
            //handlers[i]=scheduledExecutor.schedule(tasks[i].getTask(),10*busyPeriod, TimeUnit.SECONDS);
        }
        started=true;
    }

    //取消任务执行
    public static void cancelTask(int taskIndex){
        if(handlers!=null&&handlers.length>taskIndex) {
            //取消task之前先unfreee service
            serviceFreezer.unfreezeService(taskIndex);
            if(handlers[taskIndex]!=null)
                handlers[taskIndex].cancel(true);
        }
    }

    public static void startTask(int taskIndex){
        if(tasks!=null&&tasks.length>taskIndex&&handlers!=null&&handlers.length>taskIndex) {
            if(tasks[taskIndex]!=null) {
                tasks[taskIndex].setShouldFreeze(true);
                serviceFreezer.setServiceIdleTime(taskIndex, 1);
                handlers[taskIndex] = scheduledExecutor.schedule(tasks[taskIndex].getTask(), 10 * busyPeriod, TimeUnit.SECONDS);
            }
        }
    }

    class FreezerScheduledTask{
        boolean shouldFreeze=true;
        int index=0;
        FreezerScheduledTask(){
            index=0;
            shouldFreeze=true;
        }
        public boolean isShouldFreeze() {
            return shouldFreeze;
        }

        public void setShouldFreeze(boolean shouldFreeze) {
            this.shouldFreeze = shouldFreeze;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public Runnable getTask(){
            return task;
        }

        final Runnable task=new Runnable() {
            @Override
            public void run() {
                if(shouldFreeze){
                    shouldFreeze=false;
                    serviceFreezer.freezeService(index);
                    Log.i("freezer","serviceFreezer,"+index+","+serviceFreezer.getServiceIdleTime(index)+",false");
                    handlers[index]=scheduledExecutor.schedule(this,serviceFreezer.getServiceIdleTime(index)*idlePeriod, TimeUnit.SECONDS);
                }else{
                    shouldFreeze=true;
                    serviceFreezer.unfreezeService(index);
                    serviceFreezer.setServiceIdleTime(index,2*serviceFreezer.getServiceIdleTime(index));
                    Log.i("freezer","serviceFreezer,"+index+","+serviceFreezer.getServiceIdleTime(index)+",true");
                    handlers[index]=scheduledExecutor.schedule(this,busyPeriod, TimeUnit.SECONDS);
                }
            }
        };
    }

    /*static class FreezerTask extends TimerTask {
        boolean freeze;
        int index;
        private static Timer taskTimer=new Timer();

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public boolean isFreeze() {
            return freeze;
        }

        public void setFreeze(boolean freeze) {
            this.freeze = freeze;
        }

        public void reversal(){
            freeze=freeze?false:true;
        }

        @Override
        public void run() {
            Log.i("freezer","Freezer task run method is called");
//            Timer timer=new Timer();
            //冻结service并且service所在app不在前台，service也没有被bind
            if(freeze){
                //serviceFreezer.freezeService(index);
                freeze=false;
                Log.i("freezer","serviceFreezer,"+index+","+serviceFreezer.getServiceIdleTime(index)+",false");
                taskTimer.schedule(tasks[index],serviceFreezer.getServiceIdleTime(index)*initialIdlePeriod);
            }
            else if(!freeze){
                //serviceFreezer.unfreezeService(index);
                serviceFreezer.setServiceIdleTime(index,2*serviceFreezer.getServiceIdleTime(index));
                freeze=true;
                Log.i("freezer","serviceFreezer,"+index+","+serviceFreezer.getServiceIdleTime(index)+",true");
                taskTimer.schedule(tasks[index],busyPeriod);
            }
        }
    }*/
}
