package com.laohudada.yingxin.http;


import com.laohudada.yingxin.http.mdel.BaseApiResponse;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;

/**
 * Created by tao on 2016/1/9.
 */
public class MyBaseHttpRequestCallback<T extends BaseApiResponse> extends BaseHttpRequestCallback<T> {

    @Override
    protected void onSuccess(T t) {
        int code = t.getErrNum();
        if (code == 0) {
            onLoginSuccess(t);
        } else {
            onLoginFailure(t);
        }
    }

    public void onLoginSuccess(T t) {

    }

    public void onLoginFailure(T t) {

    }
}
