package com.aizou.peachtravel.common.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.aizou.core.utils.SharePrefUtil;
import com.aizou.peachtravel.common.dialog.ComfirmDialog;
import com.aizou.peachtravel.common.dialog.PeachMessageDialog;


public class UpdateUtil {

	public static final String DL_ID = "downloadId";

	public static void showUpdateDialog(final Context context, String content,
			final String downloadUrl) {
		final PeachMessageDialog dialog = new PeachMessageDialog(context);
        dialog.setTitle("提示");
		dialog.setMessage(content);
		dialog.setPositiveButton("下载更新", new OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.dismiss();
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// Intent updateIntent = new Intent(Intent.ACTION_VIEW, Uri
					// .parse(mUpgrade.data.url));
					// ct.startActivity(updateIntent);
					if (!TextUtils.isEmpty(downloadUrl)) {
						downloadApk(downloadUrl, context);
					}

				}
			}
		});
		dialog.setNegativeButton("忽略", new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}
		});
		dialog.show();

	}

	public static void downloadApk(String url, Context context) {
		DownloadManager manager = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		// 创建下载请求
		DownloadManager.Request down = new DownloadManager.Request(
				Uri.parse(url));
		
		// 设置允许使用的网络类型，这里是移动网络和wifi都可以
		down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
				| DownloadManager.Request.NETWORK_WIFI);
		down.setShowRunningNotification(true);
		down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        down.setMimeType("application/vnd.android.package-archive");
        // 设置为可被媒体扫描器找到
        down.allowScanningByMediaScanner();
		down.setVisibleInDownloadsUi(true);
		down.setTitle("更新") // 用于信息查看
				.setDescription("正在下载桃子旅行"); // 用于信息查看
		// 设置下载后文件存放的位置
		down.setDestinationInExternalFilesDir(context,
				Environment.DIRECTORY_DOWNLOADS, "TaoziLvxing.apk");

		// 将下载请求放入队列
		long id = manager.enqueue(down);
		SharePrefUtil.saveLong(context, DL_ID, id);
	}

	static String versionName = "";
	int versionCode;

	public static String getVerName(Context context) {

		try {
			versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (Exception e) {
		}
		return versionName;
	}

	public int getVerCode(Context context) {

		try {
			versionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (Exception e) {
		}
		return versionCode;
	}

}
