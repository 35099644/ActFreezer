package legacy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by LZQ on 5/25/2016.
 */
public class MonitorThread implements Runnable {

    Process proc = null;
    Runtime rt = null;
    BufferedReader br =null;
    String line;
    @Override
    public void run() {
        try {
            while (true) {
                rt = Runtime. getRuntime() ;
                proc = rt.exec("top -b -n 1" );
                // proc.waitFor();
                br = new BufferedReader(new InputStreamReader(proc.getInputStream())) ;
                System.out.println( "Total Memory Usage: " +br.readLine()) ;
                System.out.println( "Total CPU Usage: " +br.readLine()) ;
                System.out.println( "Load Average: " +br.readLine()) ;
                System.out.println( "Label: " + br .readLine());



                while((line =br.readLine())!= null) {
                    if( line.length()>0 ) {
                        System. out.println(line );
                        System.out.println( "pid: "+line .substring(0, 5));
                        System.out.println( "cpu_usage: " + line.substring( 41, 45 ));
                        System.out.println( "app command: " + line.substring( 46));
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
                }
                Thread.sleep(1000 );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}