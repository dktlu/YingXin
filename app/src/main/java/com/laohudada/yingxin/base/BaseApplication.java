package com.laohudada.yingxin.base;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

/**
 * Created by Administrator on 2016/4/7.
 */
public class BaseApplication extends Application {

    private static Context context;
    private static Resources resources;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        resources = context.getResources();
    }

    public static synchronized BaseApplication context() {
        return (BaseApplication) context;
    }

    public static Resources resources() {
        return resources;
    }
}
