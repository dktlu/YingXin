package com.laohudada.yingxin.inter;

/**
 * Created by Administrator on 2016/4/7.
 */
public interface DialogControl {

    public abstract void hideWaitDialog();

    public abstract void showWaitDialog(boolean cancelable);

    public abstract void showWaitDialog(int resid, boolean cancelable);

    public abstract void showWaitDialog(String message, boolean cancelable);
}
