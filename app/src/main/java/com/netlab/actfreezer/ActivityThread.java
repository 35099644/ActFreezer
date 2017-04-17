package com.netlab.actfreezer;

import android.app.Application;

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

}
