package com.netlab.ui;

import android.graphics.drawable.Drawable;

/**
 * Created by ZQ on 2017/5/2.
 */

public class AppStatus {
    public Drawable appIcon;
    public String packageName;

    public AppStatus(Drawable appIcon, String packageName) {
        this.appIcon = appIcon;
        this.packageName = packageName;
    }
}
