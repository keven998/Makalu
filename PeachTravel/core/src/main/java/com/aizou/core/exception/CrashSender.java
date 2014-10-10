package com.aizou.core.exception;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * @ClassName: CrashSender
 * @Description: 发�?�崩溃信息文�?
 * @author luql
 * @date 2014-1-8 上午09:54:26
 */
// TODO
public class CrashSender {

	private static final String TAG = CrashSender.class.getSimpleName();
	private final static String ENDSWITH = ".bak";
	private CrashConfig crashConfig;

	/**
	 * 配置信息
	 * 
	 * @param config
	 */
	public CrashSender(Context context, CrashConfig config) {
		// 初始化日�?
		CLog.LOGFLAG = config.isDebug();
		this.crashConfig = config;
	}

	public void send() {
		try {
			CrashAsyncTask task = new CrashAsyncTask();
			task.execute(crashConfig);
		} catch (Exception e) {
			CLog.e(TAG, e.getMessage(), e);
		}
	}

	private class CrashAsyncTask extends AsyncTask<CrashConfig, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			// 是否继续�?�?
			if (result) {
				send();
			}
		}

		@Override
		protected Boolean doInBackground(CrashConfig... params) {
			// 获取文件
			CrashConfig config = params[0];
			String localPath = config.getLocalPath();
			File file = new File(localPath);
			if (file.exists() && file.isDirectory()) {
				File[] listFiles = file.listFiles();
				for (File f : listFiles) {
					// 判断文件名称
					String name = f.getName();
					if (!name.endsWith(ENDSWITH)) {
						// 读取内容
						String content = getFileContent(f.getAbsolutePath());
						// 发�?�到服务�?
						boolean isSuecct = sendContent(config, content);
						if (isSuecct) {
							// 发�?�成功修改文件名�? XXX.bak
							String srcFile = name;
							String descFile = srcFile + ENDSWITH;
							renameFile(srcFile, descFile);
							return true;
						}
					}
				}
			} else {
				CLog.e(TAG, localPath + "不存�?");
			}
			return false;
		}

		private String getFileContent(String filePath) {
			String result = null;
			try {
				FileInputStream fis = new FileInputStream(filePath);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int len = -1;
				while ((len = fis.read(buf)) > -1) {
					bos.write(buf, 0, len);
				}
				bos.flush();
				result = bos.toString();
			} catch (Exception e) {
				CLog.e(TAG, "filePath:[" + filePath + "]读取文件内容错误�?");
			}
			return result;
		}

		private boolean sendContent(CrashConfig config, String content) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(config.getRemoteUrl());
				StringEntity se = new StringEntity(content);
				post.setEntity(se);
				client.execute(post);
				return true;
			} catch (Exception e) {
				CLog.e(TAG, e.getMessage(), e);
			}
			return false;
		}

		private boolean renameFile(String srcFile, String descFile) {
			File src = new File(srcFile);
			File desc = new File(descFile);
			return src.renameTo(desc);
		}
	}

}
