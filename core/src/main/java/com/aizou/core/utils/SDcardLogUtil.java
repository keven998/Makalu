package com.aizou.core.utils;

import android.os.Environment;
import android.util.Log;


import com.aizou.core.base.BaseApplication;
import com.aizou.core.log.LogGloble;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SDcardLogUtil {

	/**
	 * 保存网络请求数据
	 * @param log 返回日志 
	 * @param requestMethod  请求接口名称
	 */
	public static void saveLog(final String log, String requestMethod) {
		try {
			String sdcardpath = android.os.Environment
					.getExternalStorageDirectory()  + File.separator + "TRAVELLOG";
			File f = new File(sdcardpath);
			if (!f.exists()) {
				f.mkdir();
			}
			File file = new File(sdcardpath, requestMethod.replaceAll("/", "_") + ".txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			writeToFile(log, file);
		} catch (final Exception e) {
			LogGloble.exceptionPrint(e);
		}
	}

	
	/**
	 * 保存网络请求数据
	 * @param log 返回日志 
	 * @param requestMethod  请求接口名称
	 */
	public static void processException(final String log, String requestMethod) {
		try {
			String sdcardpath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + File.separator + "ITLLOG";
			File f = new File(sdcardpath);
			if (!f.exists()) {
				f.mkdir();
			}
			File file = new File(sdcardpath, requestMethod + ".txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			writeToFile(log, file);
		} catch (final Exception e) {
			LogGloble.exceptionPrint(e);
		}
	}

	/**
	 * 读取本地的网络请求数据
	 * @param requestMethod  请求接口名称
	 */
	public static String readLog(String requestMethod) {
		try {
			String sdcardpath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + File.separator + "ITLLOG";
			File f = new File(sdcardpath);
			if (!f.exists()) {
				f.mkdir();
			}
			File file = new File(sdcardpath, requestMethod.replaceAll("/", "_") + ".txt");
			if (!file.exists()) {
//				ToastUtil.getInstance().toastInCenter(BaseApplication.getContext(), "本地未找到接口：  "+requestMethod+"  的数据");
				return "";
			}
			String json = getJsonFromSDCard(file.getAbsolutePath());
			Log.d("httplog", "读取的本地 " + requestMethod + "  json ==  " + json);
			return json;
		} catch (final Exception e) {
			LogGloble.exceptionPrint(e);
		}
		return "";
	}
	
	/**
	 * 读取raw中的json字符串
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getJsonFromSDCard( String filePath) {
		FileInputStream is;
		try {
			is = new FileInputStream(filePath);
			StringBuffer sb = getStringBuffer(is);
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 从输入流中获取字符串
	 * 
	 * @param is
	 * @return
	 */
	public static StringBuffer getStringBuffer(InputStream is) {
		BufferedReader br = new BufferedReader(new InputStreamReader(is), 8096);
		String line = null;
		StringBuffer sb = new StringBuffer("");
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			is.close();
			br.close();
		} catch (IOException e) {

		}
		return sb;
	}
	
	/**
	 * 写日志
	 * 
	 * @param stacktrace
	 * @param filename
	 */
	private static void writeToFile(final String stacktrace, final File filename) {
		try {
			final BufferedWriter bos = new BufferedWriter(new FileWriter(
					filename));
			bos.write(stacktrace);
			bos.flush();
			bos.close();
		} catch (final Exception e) {
		}
	}

}
