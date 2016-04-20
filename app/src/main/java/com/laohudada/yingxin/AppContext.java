package com.laohudada.yingxin;

import com.laohudada.yingxin.base.BaseApplication;
import com.laohudada.yingxin.utils.SPUtils;
import com.laohudada.yingxin.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;
import cn.finalteam.okhttpfinal.Part;
import okhttp3.Headers;
import okhttp3.Interceptor;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * Created by tao on 2016/1/6.
 */
public class AppContext extends BaseApplication {

    private static AppContext instance;
    private static String LAST_REFRESH_TIME = "last_refresh_time.pref";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        initOkhttpFinal();
    }

    private void initOkhttpFinal() {
        List<Part> commonParams = new ArrayList<>();
        Headers commonHeaders = new Headers.Builder().build();

        List<Interceptor> interceptorList = new ArrayList<>();
        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder()
                .setCommenParams(commonParams)
                .setCommenHeaders(commonHeaders)
                .setTimeout(Constants.REQ_TIMEOUT)
                .setInterceptors(interceptorList)
                .setDebug(true);
        OkHttpFinal.getInstance().init(builder.build());

    }

    public static AppContext getInstance() {
        return instance;
    }

    /**
     * 记录列表上次的刷新时间
     * @param key
     * @param value
     */
    public static void putToLastRefreshTime(String key, String value) {
        SPUtils.put(getInstance(), LAST_REFRESH_TIME, StringUtils.getCurTimeStr());
    }

    /**
     * 获取列表的上次刷新时间
     * @param key
     * @return
     */
    public static String getLaseRefreshTime(String key) {
        return (String) SPUtils.get(getInstance(), LAST_REFRESH_TIME, StringUtils.getCurTimeStr());
    }
}
