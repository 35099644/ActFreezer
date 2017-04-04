package legacy.xzh;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by zhx on 2016/7/7.
 */
public class DetectionService extends AccessibilityService {

    final static String TAG = "DetectionService";
    final static AppNameGetter nameGetter = new AppNameGetter();
    static String foregroundPackageName;
    static String lastPackage="system";
    final String filename="foreground.txt";
    final static String packageHolder="currentPackage.txt";
    static ServiceFreezer serviceFreezer=new ServiceFreezer();
    static RunningServiceMonitor runningServiceMonitor=new RunningServiceMonitor();
    static MonitorThread monitorThread=new MonitorThread();
    static FreezerThread freezerThread=new FreezerThread();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("xposed","accessibilityService is started");
        Notification.Builder builder = new Notification.Builder(this);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker("Foreground Service Start");
        builder.setContentTitle("Foreground Service");
        builder.setContentText("Xposed Demo is running,don't kill it manually.");
        Notification notification = builder.build();
        startForeground(1,notification);
        if(!MonitorThread.started){
            monitorThread.start();
        }
        if(!RunningServiceMonitor.started) {
            RunningServiceMonitor.setContext(this);
            runningServiceMonitor.start();
        }
/*        if(!FreezerThread.started){
            freezerThread.start();
        }*/
        final BroadcastReceiver batteryReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 获取当前电量
                int current = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                // 获取总电量
                int total = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                float percent=(float)current * 100 /total;
                Log.i("battery","current battery is "+percent + "%");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                try {
                    CsvWriter.writeSDFile("battery.txt",str+","+percent+"\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver,intentFilter);
        return 0; // 根据需要返回不同的语义值
    }

    public  static boolean isForegroundPkgViaDetectionService(String appName){
        return appName.equals(nameGetter.getNameFromPkgName(foregroundPackageName));
    }

    public static String getForegroundApp(){
        return foregroundPackageName;
    }

    /**
     * 重载辅助功能事件回调函数，对窗口状态变化事件进行处理
     * @param event
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            /*
             * 如果 与 DetectionService 相同进程，直接比较 foregroundPackageName 的值即可
             * 如果在不同进程，可以利用 Intent 或 bind service 进行通信
             */

            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String str = formatter.format(curDate);
            foregroundPackageName = event.getPackageName().toString();
            //前台app发生了改变
            if(lastPackage != foregroundPackageName){
                serviceFreezer.setRunningStateByPackage(lastPackage,false);//将该package的所有service runningState设置为false
                serviceFreezer.setRunningStateByPackage(foregroundPackageName,true);//将前台package的所有service runningState设置为true
                try {
                    CsvWriter.writeSDFile(filename,"onAccessibilityEvent,"+","+str+","+BootReceiver.getProgramNameByPackageName(this,foregroundPackageName)+","
                            +foregroundPackageName+","+event.getClassName().toString()+"\n");
                    CsvWriter.writeNewSDFile(packageHolder,foregroundPackageName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.i("detection","onAccessibilityEvent,"+","+str+","+BootReceiver.getProgramNameByPackageName(this,foregroundPackageName)+","
                    +foregroundPackageName+","+event.getClassName().toString());
            /*
             * 基于以下还可以做很多事情，比如判断当前界面是否是 Activity，是否系统应用等，
             * 与主题无关就不再展开。
             */
            lastPackage=foregroundPackageName;
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected  void onServiceConnected() {
        super.onServiceConnected();
    }
}
