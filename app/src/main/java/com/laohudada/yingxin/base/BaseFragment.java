package com.laohudada.yingxin.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laohudada.yingxin.http.MyHttpCycleContext;
import com.laohudada.yingxin.widget.dialog.BaseDialogFragment;
import com.laohudada.yingxin.widget.dialog.DialogFactory;

import butterknife.ButterKnife;
import cn.finalteam.okhttpfinal.HttpTaskHandler;

/**
 * Fragment基类
 * Created by Administrator on 2016/4/7.
 */
public class BaseFragment extends Fragment implements MyHttpCycleContext {

    protected final String HTTP_TASK_KEY = "HttpTaskKey_" + hashCode();
    protected LayoutInflater mInflater;

    public static final int STATE_NONE = 0;
    public static final int STATE_REFRESH = 1;
    public static final int STATE_LOADMORE = 2;
    public static final int STATE_NOMORE = 3;
    public static final int STATE_PRESSNONE = 4;// 正在下拉但还没有到刷新的状态
    public static int mState = STATE_NONE;

    protected BaseActivity mBaseActivity;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDialogFactory = new DialogFactory(getChildFragmentManager(), savedInstanceState);
        mDialogFactory.restoreDialogListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof BaseActivity) {
            mBaseActivity = (BaseActivity) activity;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mInflater = inflater;
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);
        init(savedInstanceState);
        return view;
    }

    protected void init(Bundle savedInstanceState) {

    }

    protected int getLayoutId() {
        return 0;
    }

    public boolean onBackPressed() {
        return false;
    }

    protected View inflateView(int resId) {
        return this.mInflater.inflate(resId, null);
    }

    public void hideWaitDialog() {
        if (mDialogFactory != null) {
            mDialogFactory.dissProgressDialog();
        }
    }

    public void showWaitDialog(boolean cancelable) {
        showWaitDialog("加载中...", cancelable);
    }

    public void showWaitDialog(int resid, boolean cancelable) {
        showWaitDialog(getString(resid), cancelable);
    }

    public void showWaitDialog(String message, boolean cancelable) {
        if (mDialogFactory != null) {
            mDialogFactory.showProgressDialog(message, cancelable);
        }
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public String getHttpTaskKey() {
        return HTTP_TASK_KEY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HttpTaskHandler.getInstance().removeTask(HTTP_TASK_KEY);
    }
}
