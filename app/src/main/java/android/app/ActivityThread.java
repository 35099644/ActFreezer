package android.app;

import android.app.Application;
import android.content.Intent;
import android.os.IBinder;

import legacy.lzq.IApplicationThread;

/**
  * @hide
  */
public class ActivityThread {

    public static Application currentApplication() {
        throw new UnsupportedOperationException();
    }

    public ApplicationThread getApplicationThread() {
        throw new UnsupportedOperationException();
    }

    private abstract class ApplicationThread implements IApplicationThread {
    }

    public static ActivityThread currentActivityThread() {
        throw new UnsupportedOperationException();
    }

    public ContextImpl getSystemContext() {
        throw new UnsupportedOperationException();
    }
}
