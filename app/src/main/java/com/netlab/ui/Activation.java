package com.netlab.ui;

import java.io.Serializable;

/**
 * Created by ZQ on 2017/4/18.
 */

public class Activation implements Serializable{
    public String source;
    public String sink;
    public String method;

    public Activation(String source, String sink, String method) {
        this.source = source;
        this.sink = sink;
        this.method = method;
    }
}
