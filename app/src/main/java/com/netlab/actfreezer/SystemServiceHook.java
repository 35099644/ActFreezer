package com.netlab.actfreezer;

//import android.app.IApplicationThread;

import android.app.IApplicationThread;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import com.netlab.util.GlobalSettings;
import com.netlab.util.Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;


import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by LZQ on 6/6/2016.
 */
public class SystemServiceHook extends XC_MethodHook {


    private static final String TAG = "SystemServiceHook";
    private static Method getRecordForAppLocked;

    private static Socket socket = null;

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        XposedBridge.log("in afterHookedMethod, call hookActivityManagerService");
        hookActivityManagerService(classLoader);

    }

    private void hookActivityManagerService(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        Class<?> activityManagerService = Class.forName("com.android.server.am.ActivityManagerService", false, classLoader);

        XposedBridge.log("in hookActivityManagerService, call a lot of hooking method.");

        hookActivityManagerServiceStartService(activityManagerService);
        //hookActivityManagerServiceBroadcastIntent(activityManagerService, classLoader);
        hookActivityManagerServiceBindService(activityManagerService, classLoader);
        getRecordForAppLocked = activityManagerService.getDeclaredMethod("getRecordForAppLocked", IApplicationThread.class);
        getRecordForAppLocked.setAccessible(true);
        hookhandleReceiver(classLoader);
        hookCheckBroadcast(classLoader);


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


                        int Xposed_pid = Process.myPid();
                        Log.d(TAG,"Xposed pid = " + Xposed_pid);
//                        /**
//                         * check if the broadcast is sent by Actfreezer!
//                         */
//
//                        if(intent!=null && intent.getAction()!= null && intent.getAction().equals("ActfreeezerData"))
//                        {
//                           Log.d(TAG, "receive broadcast from actfreezer " + intent.getStringExtra("configuration"));
//                        }


                        String resolvedType = (String)param.args[3];
                        if (intent == null) {
                            intent = new Intent("NULL_INTENT");
                        }

                        /**
                         * Get receiver package
                         */
                        if (intent.getComponent() == null) {
                            intent.setComponent(new ComponentName("NULL_PKG", "NULL_CLASS"));
                        }

                        final String receiver = intent.getComponent().getPackageName();

                        /**
                         * 如果接收的Receiver不是一个系统的Component，那么输出这一条；
                         */
                        if (!(receiver.startsWith("com.android") || receiver.equals("") || receiver.equals("android") || receiver.startsWith("com.google")))
                        {
                            String pkg_name ;
                            if(Tools.checkUid(receivingUid))
                            {
                                pkg_name = Tools.getPkgName(receivingUid);
                            }
                            else
                            {
                                pkg_name = "NOT FOUND!";
                            }
                            Log.d(TAG,"hooked CheckBroadcast, intent = " + intent.toString() +" receiver = "+ receiver +"  receiver uid = " + receivingUid + " resolved type = " + resolvedType + " pkg name = "+ pkg_name);
                        }
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
                        /**
                         * Here param[0] is a ReceiverData
                         */
                        Object data = (Object)param.args[0];
                        Class receiverData = data.getClass();
                        Method method = receiverData.getDeclaredMethod("toString");
                        String receiverInfo = (String)method.invoke(data);

                        Log.d(TAG, "hooked handleReceiver, receiver Info = " + receiverInfo);

                        XposedBridge.log("hooked handleReceiver!" +  "receiver Info = " + receiverInfo);
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
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
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
//            if (sender.startsWith("com.android") || sender.equals("") || sender.equals("android") || sender.startsWith("com.google"))
//                return;


            Log.d(TAG,"hooked BroadcastIntent, " + pid + " , " + sender + " , " + intent.toString() + " , " + System.currentTimeMillis());
            //XposedBridge.log("LZQ Hook Start Service Activation, "+pid+" , "+sender+" , "+cn.toString());


            /**
             * Get receiver package
             */
            if (intent.getComponent() == null) {
                intent.setComponent(new ComponentName("NULL_PKG", "NULL_CLASS"));
            }

            final String receiver = intent.getComponent().getPackageName();


            if (!sender.equals(receiver)) {
                XposedBridge.log("Cross-app Broadcast Intent activation: source = " + sender + " receiver = " + receiver);

               // param.setResult(null);
            }

            if (result != null && result >= 0 && intent != null) {
                // preventRunning.onBroadcastIntent(intent);
            }
        }
    }

    public static class BindServiceContextHook extends ContextHook {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
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


//            System.err.println(Environment.getExternalStorageDirectory());
            FileReader fr = new FileReader(new File("/data/system/tmp/log"));
            BufferedReader br = new BufferedReader(fr);
            System.err.println(br.readLine());
            fr.close();
            br.close();


            Log.d(TAG, "hooked bindService, from " + sender + " intent = " + intent.toString());

            if (intent.getComponent() == null) {
                intent.setComponent(new ComponentName("NULL_PKG", "NULL_CLASS"));
            }

            final String receiver = intent.getComponent().getPackageName();

            if (!sender.equals(receiver)) {
                XposedBridge.log("Cross-app bindService activation: source = " + sender + " receiver = " + receiver);
                XposedBridge.log("Stop cross-app activation, kill the activation");
               // param.setResult(null);
            }

        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            //ComponentName cn = (ComponentName) param.getResult();

            //XposedBridge.log("LZQ Hook Start Service Activation, "+pid+" , "+sender+" , "+cn.toString()+" , "+System.currentTimeMillis());
        }
    }

    public static class StartServiceContextHook extends ContextHook {


        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

            super.beforeHookedMethod(param);
            Intent intent = (Intent) param.args[1];
            // XposedBridge.log(intent.toString());


            /***
             * Get caller package
             */
            Object processRecord = getRecordForAppLocked(param.thisObject, param.args[0]);
            int pid = ProcessRecordUtils.getPid(processRecord);

            ApplicationInfo info = ProcessRecordUtils.getInfo(processRecord);
            final String sender = info == null ? "" : info.packageName;


            /**
             * 对于系统自己的唤醒，不管他
             */
            if (sender.startsWith("com.android") || sender.equals("") || sender.equals("android") || sender.startsWith("com.google"))
                return;


            Log.d(TAG, "hooked startService, from " + sender + " intent = " + intent.toString());

            /**
             * Get receiver package
             */
            if (intent.getComponent() == null) {
                intent.setComponent(new ComponentName("NULL_PKG", "NULL_CLASS"));
            }

            final String receiver = intent.getComponent().getPackageName();


            if (!sender.equals(receiver)) {
                XposedBridge.log("Cross-app startService activation: source = " + sender + " receiver = " + receiver);

                //boolean user_decision = askForUserDecision();

                //XposedBridge.log("Read from socket, user decision: "+user_decision);
                //if(user_decision)
                {
                    XposedBridge.log("Stop cross-app activation, kill the activation");
                  //  param.setResult(null);
                }
            }
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
//            ComponentName cn = (ComponentName) param.getResult();
//
//            Object processRecord = getRecordForAppLocked(param.thisObject, param.args[0]);
//            int pid = ProcessRecordUtils.getPid(processRecord);
//
//            ApplicationInfo info = ProcessRecordUtils.getInfo(processRecord);
//            String sender = info == null ? "" : info.packageName;
//            if (sender == null) {
//                sender = String.valueOf(Binder.getCallingUid());
//            }
//
//            /**
//             * 对于系统自己的唤醒，不管他
//             */
//            if (sender.startsWith("com.android") || sender.equals("") || sender.equals("android") || sender.startsWith("com.google"))
//                return;


            //XposedBridge.log(param.thisObject.getClass().toString());

            //XposedBridge.log("LZQ Hook: "+cn.toString()+" "+Thread.currentThread().getId());

//            String receiver = null;
//
//            if(cn!=null)
//            {
//                receiver = cn.getPackageName();
//               // XposedBridge.log(",Start Service Activation, " + Process.myPid()+","+ pid + " , " + sender + " , " +cn.getClassName()+","+cn.getPackageName()+","+ cn.toString() + " , " + System.currentTimeMillis());
//
//            }
//            else{
//                receiver = "NULL";
//            }


            /**
             * If this is a cross-app activation
             */
            // if(!sender.equals(receiver))
            //{
            //XposedBridge.log(", cross-app activation," + Process.myPid()+","+ pid + " , " + sender + " , " +receiver+ " , " + System.currentTimeMillis());
            //XposedBridge.log(", LZQ Hook Start Service Activation, " + Process.myPid()+","+ pid + " , " + sender + " , " +cn.getClassName()+","+cn.getPackageName()+","+ cn.toString() + " , " + System.currentTimeMillis());
            //Log.d(TAG,", LZQ Hook Start Service Activation, " + Process.myPid()+"," + pid + " , " + sender + " , " +cn.getClassName()+","+cn.getPackageName()+","+ cn.toString() + " , " + System.currentTimeMillis());
//                if(receiver.contains("shaojuanzi"))
//                {
//                    XposedBridge.log(", set result to null");
//                    param.setResult(null);
//                }


            // }


//            if (cn != null && cn.getPackageName().startsWith("!")) {
//                param.setResult(null);
//            }

//            if(cn != null && cn.getPackageName().contains("Alipay"))
//            {
//                param.setResult(null);
//            }
        }

    }

    public static class ContextHook extends XC_MethodHook {

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
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
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
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


    private static synchronized boolean askForUserDecision() {
        new Thread() {
            boolean result = false;

            public void run() {
                try {
                    socket = new Socket("127.0.0.1", 8888);

                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    result = (Boolean) ois.readObject();

                    XposedBridge.log(TAG + ", " + result);


                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    GlobalSettings.addUserDecision(result);
                }

            }

        }.start();
        return GlobalSettings.takeUserDecision();
    }

}

