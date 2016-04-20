package com.laohudada.yingxin.utils;

import android.util.Log;

/**
 * Log统一管理类
 * Created by Administrator on 2016/4/7.
 */
public class L {

    public static boolean isDebug = true;   //是否需要打印bug,可以在application的onCreate函数中初始化
    private static final String TAG = "dkt";

    //下面四个是默认TAG的函数
    public static void i(String message) {
        if (isDebug) {
            Log.i(TAG, message);
        }
    }

    public static void d(String message) {
        if (isDebug) {
            Log.d(TAG, message);
        }
    }

    public static void e(String message) {
        if (isDebug) {
            Log.e(TAG, message);
        }
    }

    public static void v(String message) {
        if (isDebug) {
            Log.v(TAG, message);
        }
    }

    //下面四个是类名TAG函数
    public static void i(Class<?> cls, String message) {
        if (isDebug) {
            Log.i(cls.getSimpleName(), message);
        }
    }

    public static void d(Class<?> cls, String message) {
        if (isDebug) {
            Log.d(cls.getSimpleName(), message);
        }
    }

    public static void e(Class<?> cls, String message) {
        if (isDebug) {
            Log.e(cls.getSimpleName(), message);
        }
    }

    public static void v(Class<?> cls, String message) {
        if (isDebug) {
            Log.v(cls.getSimpleName(), message);
        }
    }

    //下面四个是自定义TAG函数
    public static void i(String tag, String message) {
        if (isDebug) {
            Log.i(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (isDebug) {
            Log.d(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (isDebug) {
            Log.e(tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (isDebug) {
            Log.v(tag, message);
        }
    }

}
