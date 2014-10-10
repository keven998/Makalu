package com.aizou.core.http;

import android.content.Context;


import com.aizou.core.utils.SharedPreferencesUtil;
import com.aizou.core.utils.StringUtil;

import org.apache.http.client.methods.HttpPost;

/**
 * @ClassName：CookieFactory
 * @Description: Cookie 
 * @author xbybaoying
 * @data：2014-1-2下午4:02:50
 * Copyright (c) 2013-2016 北京联龙博通
 */
public class CookieFactory {
	public static final String COOKIEFILE = "cookiefile";
	/**
	 *@Title: saveCookie
	 *@Description: 保存Cooike(根据域名进行区分)
	 *@param context
	 *@param url
	 *@param cookieValue
	 */
	public static void saveCookie(Context context,String url, String cookieValue) {
		String cookieKey = getCooikeKey(url);
		SharedPreferencesUtil.saveValueTOFile(context, cookieKey, cookieValue, COOKIEFILE);
	}

	
	/**
	 *@Title: getCookie
	 *@Description: 获取对应url的Cookie值
	 *@param context
	 *@param url
	 *@return
	 */
	public static String getCookie(Context context,String url){
		String cookieKey = getCooikeKey(url);
		return SharedPreferencesUtil.getStringValueOfFile(context, cookieKey, "", COOKIEFILE);
	}
	
	/**
	 *@Title: clearCookie
	 *@Description: 清空指定url的Cookie值
	 *@param context
	 *@param url
	 */
	public static void clearCookie(Context context,String url){
		String cookieKey = getCooikeKey(url);
		SharedPreferencesUtil.saveValueTOFile(context, cookieKey, "",COOKIEFILE);
	}
	
	/**
	 *@Title: clearAllCookies
	 *@Description: 清除所有的Cookie
	 *@param context
	 */
	public static void clearAllCookies(Context context){
		SharedPreferencesUtil.clearFile(context, COOKIEFILE);
	}

	/**
	 *@Title: getCooikeKey
	 *@Description: 获取Cookie 的key值
	 *@param url
	 *@return
	 */
	private static String getCooikeKey(String url) {
		HttpPost httpPost = new HttpPost(url);
		String host = httpPost.getURI().getHost();
		String path = httpPost.getURI().getPath();
		String contextPath = "";
		if (!StringUtil.isNullOrEmpty(path)) {
			String[] paths = path.split("/");
			if (!StringUtil.isNullOrEmpty(path)) {
				if (paths.length > 1) {
					contextPath = paths[1];
				}
			}
		}
		String cookieKey = host + "." + contextPath;
		return cookieKey;
	}
}
