package com.netlab.actfreezer;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by ZQ on 2017/4/4.
 */

public class Tutorial implements  IXposedHookZygoteInit {

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
        XposedBridge.hookAllMethods(ActivityThread.class, "systemMain", new SystemServiceHook());
    }

}
