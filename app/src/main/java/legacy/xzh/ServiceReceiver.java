package legacy.xzh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhx on 2016/8/12.
 */
public class ServiceReceiver extends BroadcastReceiver {
    private static ServiceFreezer serviceFreezer=new ServiceFreezer();
    private static Map<String,String> serviceConnection = new HashMap<>();
    String bindAction="android.intent.action.SERVICEBIND";
    String unbindAction="android.intent.action.SERVICEUNBIND";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction()==bindAction) {
            String servicename = intent.getStringExtra("packageAndService");
            String serviceconnection = intent.getStringExtra("serviceConnection");
            serviceConnection.put(serviceconnection,servicename);
            serviceFreezer.setRunningState(servicename, true);
            Log.i("xposed","service binded========"+servicename+","+serviceconnection);
        }
        else if(intent.getAction()==unbindAction){
            String serviceconnection = intent.getStringExtra("serviceConnection");
            if(serviceConnection.keySet().contains(serviceconnection)){
                serviceFreezer.setRunningState(serviceConnection.get(serviceconnection), false);
                serviceConnection.remove(serviceconnection);
                Log.i("xposed","service unbinded========"+serviceConnection.get(serviceconnection)+","+serviceconnection);
            }
        }
    }
}
