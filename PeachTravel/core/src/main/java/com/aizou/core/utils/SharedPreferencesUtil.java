package com.aizou.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.aizou.core.constant.Constant;


/**
 * 描述:本地文件存储（形式：键值对）工具类
 * 
 * @author xby
 * 
 */
public class SharedPreferencesUtil {

	/**
	 * 描述：保存字段到本地默认文件 默认保留到chinamworldbocsp文件下
	 * @param context  上下文对象
	 * @param key  保存的key值
	 * @param value  保存的value值
	 */
	public static void saveValue(Context context, String key, String value) {
		saveValueTOFile(context, key, value, Constant.DEFAULT_SHAREDPREFERENCES_NAME);
	}
	/**
	 * 描述：保存字段到本地默认文件 默认保留到chinamworldbocsp文件下
	 * @param context  上下文对象
	 * @param key  保存的key值
	 * @param value  保存的value值
	 */
	public static void saveValue(Context context, String key, int value) {
		saveValueTOFile(context, key, value, Constant.DEFAULT_SHAREDPREFERENCES_NAME);

	}
	/**
	 * 描述：保存字段到本地默认文件 默认保留到chinamworldbocsp文件下
	 * @param context  上下文对象
	 * @param key  保存的key值
	 * @param value  保存的value值
	 */
	public static void saveValue(Context context, String key, boolean value) {
		saveValueTOFile(context, key, value, Constant.DEFAULT_SHAREDPREFERENCES_NAME);

	}
	/**
	 * 描述：获取保存的字段值
	 * @param context
	 * @param key  保存的key值
	 * @param defaultValue  获取不到的默认返回值
	 * @return String 保存的value值
	 */
	public static String getStringValue(Context context, String key,String defaultValue){
		return	getStringValueOfFile(context, key, defaultValue, Constant.DEFAULT_SHAREDPREFERENCES_NAME);
	}
	/**
	 * 描述：获取保存的字段值
	 * @param context
	 * @param key  保存的key值
	 * @param defaultValue  获取不到的默认返回值
	 * @return int 保存的value值
	 */
	public static int getIntValue(Context context, String key,int defaultValue){
		return getIntValueOfFile(context, key, defaultValue, Constant.DEFAULT_SHAREDPREFERENCES_NAME);
	}
	/**
	 * 描述：获取保存的字段值
	 * @param context
	 * @param key  保存的key值
	 * @param defaultValue  获取不到的默认返回值
	 * @return boolean 保存的value值
	 */
	public static boolean getBooleanValue(Context context, String key,boolean defaultValue){
		return getBooleanValueOfFile(context, key, defaultValue, Constant.DEFAULT_SHAREDPREFERENCES_NAME);
	}
	
	/**
	 * 描述：保存字段到指定文件夹
	 * @param context  上下文对象
	 * @param key  保存的key值
	 * @param value  保存的value值
	 * @param fileName 保存的文件名称
	 */
	public static void saveValueTOFile(Context context, String key, String value,String fileName) {
		// 获取SharedPreferences对象
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		// 存入数据
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();

	}
	/**
	 * 描述：保存字段到指定文件夹
	 * @param context  上下文对象
	 * @param key  保存的key值
	 * @param value  保存的value值
	 * @param fileName 保存的文件名称
	 */
	public static void saveValueTOFile(Context context, String key, int value,String fileName) {
		// 获取SharedPreferences对象
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		// 存入数据
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();

	}
	/**
	 * 描述：保存字段到指定文件夹
	 * @param context  上下文对象
	 * @param key  保存的key值
	 * @param value  保存的value值
	 * @param fileName 保存的文件名称
	 */
	public static void saveValueTOFile(Context context, String key, boolean value,String fileName) {
		// 获取SharedPreferences对象
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		// 存入数据
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();

	}
	/**
	 * 描述：获取指定文件下的保存的字段值
	 * @param context
	 * @param key  保存的key值
	 * @param defaultValue  获取不到的默认返回值
	 * @param fileName 保存的文件名称
	 * @return String 保存的value值
	 */
	public static String getStringValueOfFile(Context context, String key,String defaultValue,String fileName){
		// 获取SharedPreferences对象
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getString(key, defaultValue);
	}
	/**
	 * 描述：获取指定文件下的保存的字段值
	 * @param context
	 * @param key  保存的key值
	 * @param defaultValue  获取不到的默认返回值
	 * @param fileName 保存的文件名称
	 * @return int 保存的value值
	 */
	public static int getIntValueOfFile(Context context, String key,int defaultValue,String fileName){
		// 获取SharedPreferences对象
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getInt(key, defaultValue);
	}
	/**
	 * 描述：获取指定文件下的保存的字段值
	 * @param context
	 * @param key  保存的key值
	 * @param defaultValue  获取不到的默认返回值
	 * @param fileName 保存的文件名称
	 * @return boolean 保存的value值
	 */
	public static boolean getBooleanValueOfFile(Context context, String key,boolean defaultValue,String fileName){
		// 获取SharedPreferences对象
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getBoolean(key, defaultValue);
	}
	
	/**
	 * 描述：删除指定文件夹下的所有字段
	 * @param context
	 * @param fileName 文件名称
	 * @return boolean 操作是否成功
	 */
	public static boolean clearFile(Context context,String fileName){
		// 获取SharedPreferences对象
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.edit().clear().commit();
	}
	
	

}
