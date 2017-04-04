package legacy.lzq;

//import android.app.IApplicationThread;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Process;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by LZQ on 6/6/2016.
 */
public class SystemServiceHook extends XC_MethodHook {


    private static Method getRecordForAppLocked;


    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        hookActivityManagerService(classLoader);

    }

    private void hookActivityManagerService(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        Class<?> activityManagerService = Class.forName("com.android.server.am.ActivityManagerService", false, classLoader);
//        hookActivityManagerServiceStartService(activityManagerService);
//        hookActivityManagerServiceBroadcastIntent(activityManagerService, classLoader);
//        hookActivityManagerServiceBindService(activityManagerService, classLoader);
        getRecordForAppLocked = activityManagerService.getDeclaredMethod("getRecordForAppLocked", IApplicationThread.class);
        getRecordForAppLocked.setAccessible(true);
        //hookhandleReceiver(classLoader);
        //hookCheckBroadcast(classLoader);


//        Class<?> activityThread = Class.forName("backgroundstudy.lzq.com.backgroundstudyapp.MainActivity");
//        String method = "startService";
//        hookMethods(activityThread, method, new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        XposedBridge.log("LZQ Test, in MainActivity!");
//                    }
//                }
//        );

//        findAndHookMethod("android.app.ContextImpl",
//                classLoader,
//                "startService",
//                Intent.class,   // intent
//                new XC_MethodHook() {
//
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
////                        int callerUid = (int) param.args[1];
////                        int receivingUid = (int) param.args[4];
//                        Intent intent = (Intent) param.args[0];
//                        String action = intent.getAction();
//
//                        if(intent == null)
//                        {
//                            intent=new Intent("null intent");
//                        }
//                        XposedBridge.log("hook ContextImpl startService");
////                        if(!action.startsWith("android.intent")) {
////                            XposedBridge.log("hook IntentFirewall.checkBroadcast : " + "broadcast from " + callerUid + " to " + receivingUid + " , " + intent.toString());
////                        }
//
//                    }
//                });


    }


    private void hookCheckBroadcast(ClassLoader classLoader) throws ClassNotFoundException {
        findAndHookMethod("com.android.server.firewall.IntentFirewall",
                classLoader,
                "checkBroadcast",
                Intent.class,   // intent
                int.class,  // callerUid
                int.class,  // callerPid
                String.class,   // resolvedType
                int.class,  // receivingUid
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        int callerUid = (int) param.args[1];
                        int receivingUid = (int) param.args[4];
                        Intent intent = (Intent) param.args[0];
                        String action = intent.getAction();


                        
                        if(intent == null)
                        {
                            intent=new Intent("null intent");
                        }
                        if(!action.startsWith("android.intent")) {
                            XposedBridge.log("hook IntentFirewall.checkBroadcast : " + "broadcast from " + callerUid + " to " + receivingUid + " , " + intent.toString());
                        }
                        if (action == null)
                            return;
                        if (action.equals("android.intent.action.SCREEN_OFF"))
                            XposedBridge.log("hook IntentFirewall.checkBroadcast : " + "screen off");
                        if (action.equals("android.intent.action.SCREEN_ON"))
                            XposedBridge.log("hook IntentFirewall.checkBroadcast : " + "screen on");
                    }
                });
    }


    /**
     * Try to hook handleReceiver
     *
     * @param classLoader
     * @throws ClassNotFoundException Fail.
     */

    private void hookhandleReceiver(ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> activityThread = Class.forName("android.app.ActivityThread");
        String method = "handleReceiver";
        hookMethods(activityThread, method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("LZQ Test, in handleReceiver!");
                    }
                }
        );


    }


    private void hookRuntimeExecute(ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> runtime = Class.forName("java.lang.Runtime");
//        Class<?> processManager = Class.forName("java.lang.ProcessManager");
        String method = "getRuntime";
        XC_MethodHook hook = new RuntimeExecHook();
        String exec_method = "exec";
        hookMethods(runtime, method, new RuntimeGetRuntimeHook());
//        hookMethods(processManager, exec_method, new RuntimeExecHook());
//        findAndHookMethod("java.lang.Runtime", classLoader, "exec", String[].class, String[].class, File.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                XposedBridge.log("LZQ Hook f: Before exec( ... ) ");
//            }
//
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                XposedBridge.log("LZQ Hook f: After exec( ... ) ");
//            }
//        });

        //hookMethods(runtime, "getRuntime", hook);

        //findAndHookMethod("java.lang.Runtime", classLoader, "exec", String.class, hook);

    }

    private void hookActivityManagerServiceBindService(Class<?> activityManagerService, ClassLoader classLoader) throws ClassNotFoundException {
        int sdk = Build.VERSION.SDK_INT;
        String method = "bindService";
        XC_MethodHook hook = new BindServiceContextHook();
        hookMethods(activityManagerService, method, hook);
    }


    private void hookActivityManagerServiceStartService(Class<?> activityManagerService) {
        String method = "startService";
        XC_MethodHook hook = new StartServiceContextHook();
        hookMethods(activityManagerService, method, hook);

        //Runtime.getRuntime().exec
        //XposedHelpers.findAndHookMethod("");
    }


    private void hookActivityManagerServiceBroadcastIntent(Class<?> activityManagerService, ClassLoader classLoader) throws ClassNotFoundException {
        int sdk = Build.VERSION.SDK_INT;
        String method = "broadcastIntent";
        XC_MethodHook hook = new BroadcastIntentContextHook();
        hookMethods(activityManagerService, method, hook);
    }


    public static class BroadcastIntentContextHook extends ContextHook {

        @Override
        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            Intent intent = (Intent) param.args[0x1];
            Integer result = (Integer) param.getResult();


            Object processRecord = getRecordForAppLocked(param.thisObject, param.args[0]);
            int pid = ProcessRecordUtils.getPid(processRecord);


            ApplicationInfo info = ProcessRecordUtils.getInfo(processRecord);
            String sender = info == null ? "" : info.packageName;
            if (sender == null) {
                sender = String.valueOf(Binder.getCallingUid());
            }

            /**
             * 对于系统自己的唤醒，不管他
             */
            if (sender.startsWith("com.android") || sender.equals("") || sender.equals("android") || sender.startsWith("com.google"))
                return;


            XposedBridge.log(", LZQ Hook Send Broadcast, " + pid + " , " + sender + " , " + intent.toString() + " , " + System.currentTimeMillis());
            //XposedBridge.log("LZQ Hook Start Service Activation, "+pid+" , "+sender+" , "+cn.toString());


            if (result != null && result >= 0 && intent != null) {
                // preventRunning.onBroadcastIntent(intent);
            }
        }
    }

    public static class BindServiceContextHook extends ContextHook {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            //ComponentName cn = (ComponentName) param.getResult();
            Intent intent = (Intent) param.args[0x2];
            if (intent == null) {
                intent = new Intent("NULL");
            }
            Object processRecord = getRecordForAppLocked(param.thisObject, param.args[0]);
            int pid = ProcessRecordUtils.getPid(processRecord);

            ApplicationInfo info = ProcessRecordUtils.getInfo(processRecord);
            String sender = info == null ? "" : info.packageName;
            if (sender == null) {
                sender = String.valueOf(Binder.getCallingUid());
            }

            /**
             * 对于系统自己的唤醒，不管他
             */
            if (sender.startsWith("com.android") || sender.equals("") || sender.equals("android") || sender.startsWith("com.google"))
                return;

            XposedBridge.log(", LZQ Hook Bind Service Activation, " + pid + " , " + sender + " , " + intent.toString() + " , " + System.currentTimeMillis());
            //XposedBridge.log("LZQ Hook Start Service Activation, "+pid+" , "+sender+" , "+cn.toString()+" , "+System.currentTimeMillis());
        }
    }

    public static class StartServiceContextHook extends ContextHook {

        @Override
        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            ComponentName cn = (ComponentName) param.getResult();

            Object processRecord = getRecordForAppLocked(param.thisObject, param.args[0]);
            int pid = ProcessRecordUtils.getPid(processRecord);

            ApplicationInfo info = ProcessRecordUtils.getInfo(processRecord);
            String sender = info == null ? "" : info.packageName;
            if (sender == null) {
                sender = String.valueOf(Binder.getCallingUid());
            }

            /**
             * 对于系统自己的唤醒，不管他
             */
            if (sender.startsWith("com.android") || sender.equals("") || sender.equals("android") || sender.startsWith("com.google"))
                return;


            //XposedBridge.log(param.thisObject.getClass().toString());
            XposedBridge.log(", LZQ Hook Start Service Activation, " + pid + " , " + sender + " , " + cn.toString() + " , " + System.currentTimeMillis());
            //XposedBridge.log("LZQ Hook: "+cn.toString()+" "+Thread.currentThread().getId());

            if (cn != null && cn.getPackageName().startsWith("!")) {
                param.setResult(null);
            }

//            if(cn != null && cn.getPackageName().contains("Alipay"))
//            {
//                param.setResult(null);
//            }
        }

    }

    public static class ContextHook extends XC_MethodHook {

        @Override
        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//            for(int i=0;i<param.args.length;i++)
//            {
//                if(param.args[i]!=null)
//                XposedBridge.log("LZQ Class: "+ param.args[i].getClass().getName());
//            }


//            Object processRecord = getRecordForAppLocked(param.thisObject, param.args[0]);
//            ApplicationInfo info = ProcessRecordUtils.getInfo(processRecord);
//            String sender = info == null ? "" : info.packageName;
//            if (sender == null) {
//                sender = String.valueOf(Binder.getCallingUid());
//            }
//            int pid= ProcessRecordUtils.getPid(processRecord);


            /**
             * 对于系统自己的唤醒，不管他
             */
//            if(sender.startsWith("com.android")||sender.equals("")||sender.equals("android")||sender.startsWith("com.google"))
//                return;

//            XposedBridge.log("LZQ Hook Sender ("+pid+") : "+sender);
//            preventRunning.setSender(sender);
        }

        @Override
        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//            ComponentName cn = (ComponentName) param.getResult();
//            Object processRecord = getRecordForAppLocked(param.thisObject, param.args[0]);
//            int pid= ProcessRecordUtils.getPid(processRecord);
//
//            ApplicationInfo info = ProcessRecordUtils.getInfo(processRecord);
//            String sender = info == null ? "" : info.packageName;
//            if (sender == null) {
//                sender = String.valueOf(Binder.getCallingUid());
//            }

            /**
             * 对于系统自己的唤醒，不管他
             */
//            if(sender.startsWith("com.android")||sender.equals("")||sender.equals("android")||sender.startsWith("com.google"))
//                return;
//
//            XposedBridge.log("LZQ Hook bind Service ("+pid+" , "+Thread.currentThread().getId()+") : "+sender+"-->"+cn.toString());


        }
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


    private static Object getRecordForAppLocked(Object activityManagerService, Object thread) {
        if (getRecordForAppLocked == null) {
            return null;
        }
        try {
            return getRecordForAppLocked.invoke(activityManagerService, thread);
        } catch (IllegalAccessException e) {
            XposedBridge.log("cannot access getRecordForAppLocked");
        } catch (InvocationTargetException e) {
            XposedBridge.log("cannot invoke getRecordForAppLocked");
        }
        return null;
    }


    private static class RuntimeGetRuntimeHook extends XC_MethodHook {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            int pid = Process.myPid();
            XposedBridge.log("LZQ Hook (" + pid + "): Before getRuntime() ");
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            int pid = Process.myPid();
            XposedBridge.log("LZQ Hook (" + pid + "): After getRuntime()  ");
        }
    }

    private static class RuntimeExecHook extends XC_MethodHook {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

            int pid = Process.myPid();
            XposedBridge.log("LZQ Hook (" + pid + "): Before exec( ... ) ");
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {

            int pid = Process.myPid();
            XposedBridge.log("LZQ Hook (" + pid + "): After exec( ... ) ");
        }
    }

}

