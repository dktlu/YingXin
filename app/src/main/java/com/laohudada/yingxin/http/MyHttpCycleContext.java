package com.laohudada.yingxin.http;

import android.content.Context;

import cn.finalteam.okhttpfinal.HttpCycleContext;

/**
 * Created by tao on 2016/1/8.
 */
public interface MyHttpCycleContext extends HttpCycleContext {
    Context getContext();
}
