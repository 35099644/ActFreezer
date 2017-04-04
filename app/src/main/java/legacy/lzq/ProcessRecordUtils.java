package legacy.lzq;

import android.content.pm.ApplicationInfo;
import android.os.Build;

import java.lang.reflect.Field;

import de.robv.android.xposed.XposedBridge;


/**
 * Created by thom on 15/7/14.
 */
public class ProcessRecordUtils {

    private static Class<?> ProcessRecord;

    private static Field ProcessRecord$info;

    private static Field ProcessRecord$pid;

    private static Field ProcessRecord$killedByAm;


    private ProcessRecordUtils() {

    }

    static {
        initReflection();
    }

    public static void initReflection() {
        XposedBridge.log("init ProcessRecordUtils");
        //ClassLoader classLoader = SystemHook.getClassLoader();
        ClassLoader classLoader =  Thread.currentThread().getContextClassLoader();
        try {
            ProcessRecord = Class.forName("com.android.server.am.ProcessRecord", false, classLoader);
            ProcessRecord$info = ProcessRecord.getDeclaredField("info");
            ProcessRecord$info.setAccessible(true);

            ProcessRecord$pid = ProcessRecord.getDeclaredField("pid");
            ProcessRecord$pid.setAccessible(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ProcessRecord$killedByAm = ProcessRecord.getDeclaredField("killedByAm");
            } else {
                ProcessRecord$killedByAm = ProcessRecord.getDeclaredField("killedBackground");
            }
            ProcessRecord$killedByAm.setAccessible(true);
        } catch (ClassNotFoundException e) {
            XposedBridge.log("cannot find class for ProcessRecordUtils");
            //SystemHook.setNotSupported();
        } catch (NoSuchFieldException e) {
            XposedBridge.log("cannot find fields for ProcessRecordUtils");
            //SystemHook.setNotSupported();
        }
    }

    public static ApplicationInfo getInfo(Object pr) {
        if (pr == null || ProcessRecord$info == null || !ProcessRecord.isAssignableFrom(pr.getClass())) {
            return null;
        }
        try {
            return (ApplicationInfo) ProcessRecord$info.get(pr);
        } catch (IllegalAccessException e) {
            XposedBridge.log("cannot get info");
            return null;
        }
    }

    public static int getPid(Object pr) {
        if (pr == null || ProcessRecord$pid == null || !ProcessRecord.isAssignableFrom(pr.getClass())) {
            return 0;
        }
        try {
            return (Integer) ProcessRecord$pid.get(pr);
        } catch (IllegalAccessException e) {
            //PreventLog.e("cannot get pid", e);
            return 0;
        }
    }

    public static boolean isKilledByAm(Object pr) {
        if (pr == null || ProcessRecord$killedByAm == null || !ProcessRecord.isAssignableFrom(pr.getClass())) {
            return true;
        }
        try {
            return (Boolean) ProcessRecord$killedByAm.get(pr);
        } catch (IllegalAccessException e) {
            //PreventLog.e("cannot get killedByAm", e);
            return true;
        }
    }

}
