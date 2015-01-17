package com.aizou.core.http;


import org.apache.http.Header;

import java.util.Map;

/**
 * 描述：通信回调类
 * @author  xby
 *
 */
public abstract class HttpCallBack<T> {
	/**
	 * 描述：通信成功的回调
	 * @param result 回调数据
	 * @param method 请求标示
	 * @return
	 */
	public abstract void doSucess(T result, String method);


    /**
     * 描述：通信成功的回调
     * @param result 回调数据
     * @param method 请求标示
     * @return
     */
    public void doSucess(T result, String method,Header[] headers){};
	
	/**
	 * 描述：通信异常的回调
	 * @param error 异常信息
	 * @param msg 错误信息
	 * @param method 请求标示
	 * @return
	 */
	public abstract void doFailure(Exception error, String msg, String method);
	
	/**
	 * 描述: 通讯回调前拦截
	 * @param result 回调数据
	 * @param method 请求标识
	 * @return	
	 */
	public boolean httpCallBackPreFilter(String result, String method){
        return false;
    }

    /**
     * 文件下载过程中的回调，可在此计算下载百分值进行显示
     * @param total 总大小
     * @param current 当前已下载大小
     * @param isUploading 是否正在下载
     */
    public void onLoading(long total, long current,
                          boolean isUploading) {

    }

    /**
     * 描述:文件开始下载的回调
     */
    public void onStart(){

    }


	

}

