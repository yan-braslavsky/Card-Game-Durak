package com.yan.durak.gamelogic.utils;

/**
 * Created by Yan-Home on 3/8/2015.
 */
public class LogUtils {

    public static final boolean LOGGING_ENABLED = false;

    public static final void log(String msg) {
        if (!LOGGING_ENABLED)
            return;
        System.out.println(msg);
    }
}
