package com.netlab.actfreezer;

import android.app.ActivityThread;


import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;


/**
 * Created by ZQ on 2017/4/4.
 */

public class MainXposedMod implements  IXposedHookZygoteInit {
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {

        XposedBridge.log("in initZygote, call SystemServiceHook");
        XposedBridge.hookAllMethods(ActivityThread.class, "systemMain", new SystemServiceHook());
    }
}