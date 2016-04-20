package com.laohudada.yingxin.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.laohudada.yingxin.http.MyHttpCycleContext;
import com.laohudada.yingxin.inter.DialogControl;
import com.laohudada.yingxin.utils.AppManager;
import com.laohudada.yingxin.widget.dialog.BaseDialogFragment;
import com.laohudada.yingxin.widget.dialog.DialogFactory;

import butterknife.ButterKnife;
import cn.finalteam.okhttpfinal.HttpTaskHandler;

/**
 * Created by Administrator on 2016/4/7.
 */
public class BaseActivity extends FragmentActivity implements DialogControl, MyHttpCycleContext {

    protected final String HTTP_TASK_KEY = "HttpTaskKey_" + hashCode();
    private boolean isVisible;

    protected DialogFactory mDialogFactory;

    public BaseDialogFragment.BaseDialogListener getDialogListener() {
        return mDialogFactory.mListenerHolder.getDialogListener();
    }

    /**
     * 清空DialogListenerHolder中的dialog listener
     */
    public void clearDialogListener() {
        mDialogFactory.mListenerHolder.setDialogListener(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mDialogFactory.mListenerHolder.saveDialogListenerKey(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //添加activity到activity管理列表中
        AppManager.getAppManager().addActivity(this);
        mDialogFactory = new DialogFactory(getSupportFragmentManager(), savedInstanceState);
        mDialogFactory.restoreDialogListener(this);
        onBeforeSetContentLayout();
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        //通过注解绑定控件
        ButterKnife.bind(this);

        init(savedInstanceState);

        isVisible = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HttpTaskHandler.getInstance().removeTask(HTTP_TASK_KEY);
    }

    protected void init(Bundle savedInstanceState) {

    }

    protected int getLayoutId() {
        return 0;
    }

    protected void onBeforeSetContentLayout() {

    }

    @Override
    public void hideWaitDialog() {
        if (isVisible && mDialogFactory != null) {
            mDialogFactory.dissProgressDialog();
        }
    }

    @Override
    public void showWaitDialog(boolean cancelable) {
        showWaitDialog("加载中...", cancelable);
    }

    @Override
    public void showWaitDialog(int resid, boolean cancelable) {
        showWaitDialog(getString(resid), cancelable);
    }

    @Override
    public void showWaitDialog(String message, boolean cancelable) {
        if (isVisible) {
            if (mDialogFactory != null) {
                mDialogFactory.showProgressDialog(message, cancelable);
            }
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public String getHttpTaskKey() {
        return HTTP_TASK_KEY;
    }
}
