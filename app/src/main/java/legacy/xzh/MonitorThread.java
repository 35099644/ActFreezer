package legacy.xzh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.TrafficStats;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LZQ on 5/25/2016.
 */
public class MonitorThread extends Thread {

    public static boolean started=false;
    Process proc = null;
    Runtime rt = null;
    BufferedReader br =null;
    String line;
    static String filename="statistics.txt";
    static String networkFile="network.txt";
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
    static private long lastTotalBytes=0;
    @Override
    public void run() {
        try {
            started=true;
            long nowTotalBytes;
            long speed;
            Log.i("test","MonitorThread is running");
            while (true) {
                rt = Runtime. getRuntime() ;
                proc = rt.exec("busybox top -b -n 1" );
                // proc.waitFor();
                br = new BufferedReader(new InputStreamReader(proc.getInputStream())) ;
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                CsvWriter.writeSDFile(filename,"Time:"+str+"\n");
                Log.i("statistics",str);
                for(int i=0;i<3;i++) {
                    line = br.readLine();
                    CsvWriter.writeSDFile(filename, line+"\n");
                    Log.i("statistics",line);
                }
               /* while((line =br.readLine())!= null) {
                    if( line.length()>0 ) {
                        Log.i("statistics",line);
                        Log.i("statistics", "pid: "+line .substring(0, 5));
                        Log.i("statistics","cpu_usage: " + line.substring( 41, 45 ));
                        Log.i("statistics","app command: " + line.substring( 46));
                        CsvWriter.writeSDFile(filename, line);
                        CsvWriter.writeSDFile(filename, "pid: "+line .substring(0, 5)+"\n");
                        CsvWriter.writeSDFile(filename, "cpu_usage: " + line.substring( 41, 45 )+"\n");
                        CsvWriter.writeSDFile(filename, "app command: " + line.substring( 46)+"\n");
                    }

//                    System. out.println();
//
//
//                    //System.out.println(line + " length " + line.split("\\s{1,}").length);
//                    int index= 0;
//                    for (String s: line.split( "\\s{1,}" ))
//                    {
//
//                        // System.out.print(s+" ## "+(index++)+" ## ");
//                    }
//                    System. out.println();
                }*/
                for(int i=0;i<10;i++){
                    nowTotalBytes= (TrafficStats.getTotalRxBytes()+TrafficStats.getTotalTxBytes())/1024;
                    if(lastTotalBytes!=0){
                        speed = nowTotalBytes - lastTotalBytes;
                        Log.i("net","network speed is "+speed);
                        CsvWriter.writeSDFile(networkFile,speed+"\n");
                    }
                    lastTotalBytes = nowTotalBytes;
                    Thread.sleep(1000);
                }
//                Thread.sleep(10000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}