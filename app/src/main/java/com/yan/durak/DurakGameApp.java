package com.yan.durak;

import android.app.Application;
import android.content.Context;

/**
 * Created by Yan-Home on 10/3/2014.
 */
public class DurakGameApp extends Application {
    /**
     * Here will be kept long lived reference to app context.
     */
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();


        //It's absolutely fine to keep reference to appContext
        //For those in doubt http://stackoverflow.com/questions/14871792/is-it-safe-to-keep-a-reference-to-android-application-context
        appContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }
}
