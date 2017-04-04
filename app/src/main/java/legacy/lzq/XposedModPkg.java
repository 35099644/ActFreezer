package legacy.lzq;

//import android.app.IApplicationThread;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.IBinder;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class XposedModPkg implements IXposedHookLoadPackage {


    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals("backgroundstudy.lzq.com.backgroundstudyapp"))
            return;

        XposedBridge.log("LLZZQQ we are in the BackgroundStudy! Loaded app: " + lpparam.packageName);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        findAndHookMethod("backgroundstudy.lzq.com.backgroundstudyapp.MainActivity", lpparam.classLoader, "startService", Intent.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("LLZZQQ we are in the startService! Loaded app: " + lpparam.packageName);
            }
        });


//        Class<?> mainActivity = Class.forName("backgroundstudy.lzq.com.backgroundstudyapp.MainActivity", true, lpparam.classLoader);//.forName("backgroundstudy.lzq.com.backgroundstudyapp.MainActivity");
//        hookMethods(mainActivity, "startService", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                XposedBridge.log("LLZZQQ we are in the startService! Loaded app: " + lpparam.packageName);
//            }
//        });


    }


    private static void hookMethods(Class<?> hookClass, String methodName, XC_MethodHook hook) {
        hookMethods(hookClass, methodName, null, hook);
    }

    private static void hookMethods(Class<?> hookClass, String methodName, String returnName, XC_MethodHook hook) {
        Collection<Method> methods = findMethods(hookClass, methodName, returnName);
        if (methods.isEmpty()) {
            XposedBridge.log("cannot find " + hookClass.getSimpleName() + "." + methodName);
        }
        for (Method method : methods) {
            XposedBridge.hookMethod(method, hook);
            XposedBridge.log("hooked " + hookClass.getSimpleName() + "." + methodName);
        }
    }

    private static Collection<Method> findMethods(Class<?> hookClass, String methodName, String returnName) {
        Collection<Method> methods = new ArrayList<Method>();
        for (Method method : hookClass.getDeclaredMethods()) {
            if (!methodName.equals(method.getName())) {
                continue;
            }
            String returnType = method.getReturnType().getSimpleName();
            if (returnName == null || returnName.equals(returnType)) {
                XposedBridge.log("found " + hookClass.getSimpleName() + "." + methodName + ": " + method);
                methods.add(method);
            }
        }
        return methods;
    }
}