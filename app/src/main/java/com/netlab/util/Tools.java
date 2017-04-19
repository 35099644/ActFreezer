package com.netlab.util;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by laizeqi on 4/16/17.
 */

public class Tools {


    private static String log_path = Environment.getExternalStorageDirectory().getPath();

    private static File log = null;

    private static BufferedWriter log_bw = null;

    private static boolean append = false ;

    public static void writeToLog(String line)
    {
            try {
                log_bw.append(line+"\n");
                log_bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    private static void setLogPath(String path)
    {
        log_path = path;
    }

    public static void setAppend(boolean append)
    {
        Tools.append = append;
    }

    public static void initialize(String path, boolean append)
    {
        Tools.setLogPath(path);
        Tools.setAppend(append);

        if(append)
        {
                log = new File(log_path);
                try {
                    log_bw = new BufferedWriter(new FileWriter(log,true));
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        else
        {
            log = new File(log_path);
            try {
                if(log.exists())
                {
                    log.delete();
                    log.createNewFile();
                }
                log_bw = new BufferedWriter(new FileWriter(log));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


}
