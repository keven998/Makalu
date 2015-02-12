package com.aizou.peachtravel.common.receiver;



import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.aizou.core.utils.SharePrefUtil;
import com.aizou.peachtravel.common.utils.UpdateUtil;

public class UpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		long cur = SharePrefUtil.getLong(context, UpdateUtil.DL_ID, 0);
		long myDwonloadID = intent.getLongExtra(
				DownloadManager.EXTRA_DOWNLOAD_ID, -1);
		if (cur == myDwonloadID) {
			DownloadManager manager = (DownloadManager) context
					.getSystemService(Context.DOWNLOAD_SERVICE);
			// queryDownloadStatus(context);
			
			Uri downloadFileUri = getUriForDownloadedFile(manager, myDwonloadID);
			if (downloadFileUri != null) {
//				Log.i("uri","not null");
				Intent install = new Intent(Intent.ACTION_VIEW);
				install.setDataAndType(downloadFileUri,
						"application/vnd.android.package-archive");
				install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(install);
			}else{
//				Log.i("uri"," null");
			}

		}

	}

	public Uri getUriForDownloadedFile(DownloadManager manager, long id) {
		// to check if the file is in cache, get its destination from the
		// database
		Query query = new Query().setFilterById(id);
		Cursor cursor = null;
		try {
			cursor = manager.query(query);
			if (cursor == null) {
				return null;
			}
			if (cursor.moveToFirst()) {
				int status = cursor.getInt(cursor
						.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
				if (DownloadManager.STATUS_SUCCESSFUL == status) {
					// return public uri
					String path = cursor
							.getString(cursor
									.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
//					Log.i("uri",path);
					return Uri.parse(path);
				}
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		// downloaded file not found or its status is not 'successfully
		// completed'
		return null;
	}
}
