package com.laohudada.yingxin.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast统一管理类
 * Created by Administrator on 2016/4/7.
 */
public class T {

    private static String lastToast = "";
    private static long lastToastTime;

    private T() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isShow = true;

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, String message) {
        if (isShow) {
            showToast(context, message, Toast.LENGTH_SHORT);
        }
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, int message) {
        if (isShow) {
            showToast(context, context.getString(message), Toast.LENGTH_SHORT);
        }
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, String message) {
        if (isShow) {
            showToast(context, message, Toast.LENGTH_LONG);
        }
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, int message) {
        if (isShow) {
            showToast(context, context.getString(message), Toast.LENGTH_LONG);
        }
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, String message, int duration) {
        if (isShow) {
            showToast(context, message, duration);
        }
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, int message, int duration) {
        if (isShow) {
            showToast(context, context.getString(message), duration);
        }
    }

    /**
     * 相同的message的Toast显示不超过2s
     * @param context
     * @param message
     * @param duration
     */
    private static void showToast(Context context, String message, int duration) {
        if (message != null && !message.equalsIgnoreCase("")) {
            long time = System.currentTimeMillis();
            if (!message.equalsIgnoreCase(lastToast)
                    || Math.abs(time - lastToastTime) > 2000) {
                Toast.makeText(context, message, duration).show();
                lastToast = message;
                lastToastTime = System.currentTimeMillis();
            }
        }
    }
}
