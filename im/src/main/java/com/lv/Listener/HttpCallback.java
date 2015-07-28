package com.lv.Listener;

/**
 * Created by q on 2015/4/27.
 */
public interface HttpCallback {
    public void onSuccess();
    public void onSuccess(String result);
    public void onFailed(int code);
}
