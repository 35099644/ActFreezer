package legacy;

import android.app.ActivityThread;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;

/**
 * Created by LZQ on 6/6/2016.
 */
public class XposedMod implements IXposedHookZygoteInit {
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XposedBridge.hookAllMethods(ActivityThread.class, "systemMain", new SystemServiceHook());
    }
}
