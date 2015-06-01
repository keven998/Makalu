/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xuejian.client.lxp.module.toolbox.im;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.xuejian.client.lxp.common.task.DownloadImage;

import com.easemob.util.ImageUtils;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.ChatBaseActivity;
import com.xuejian.client.lxp.common.task.LoadLocalBigImgTask;
import com.xuejian.client.lxp.common.utils.ImageCache;
import com.xuejian.client.lxp.common.widget.photoview.PhotoView;
import com.xuejian.client.lxp.common.widget.photoview.PhotoViewAttacher;

import java.io.File;

/**
 * 下载显示大图
 * 
 */
public class ShowBigImage extends ChatBaseActivity {

	private ProgressDialog pd;
	private PhotoView image;
	private int default_res = R.drawable.avatar_placeholder_round;
	// flag to indicate if need to delete image on server after download
//	private boolean deleteAfterDownload;
	private boolean showAvator;
	private String localFilePath;
//	private String username;
	private Bitmap bitmap;
	private boolean isDownloaded;
	private ProgressBar loadLocalPb;
    private String downloadFilePath;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_show_big_image);
		super.onCreate(savedInstanceState);

		image = (PhotoView) findViewById(R.id.image);
		loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);

		default_res = getIntent().getIntExtra("default_image", R.drawable.avatar_placeholder_round);
		showAvator = getIntent().getBooleanExtra("showAvator", false);
//		username = getIntent().getStringExtra("username");
//		deleteAfterDownload = getIntent().getBooleanExtra("delete", false);

		Uri uri = getIntent().getParcelableExtra("uri");
		String remotepath = getIntent().getExtras().getString("remotepath");
		String secret = getIntent().getExtras().getString("secret");
        downloadFilePath=getIntent().getStringExtra("downloadFilePath");
//		System.err.println("show big image uri:" + uri + " remotepath:" + remotepath);

		//本地存在，直接显示本地的图片
		if (uri != null && new File(uri.getPath()).exists()) {
//			System.err.println("showbigimage file exists. directly show it");
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			// int screenWidth = metrics.widthPixels;
			// int screenHeight =metrics.heightPixels;
			bitmap = ImageCache.getInstance().get(uri.getPath());
			if (bitmap == null) {
				LoadLocalBigImgTask task = new LoadLocalBigImgTask(this, uri.getPath(), image, loadLocalPb, ImageUtils.SCALE_IMAGE_WIDTH,
						ImageUtils.SCALE_IMAGE_HEIGHT);
				if (android.os.Build.VERSION.SDK_INT > 10) {
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					task.execute();
				}
			} else {
				image.setImageBitmap(bitmap);
			}
		} else if (remotepath != null) { //去服务器下载图片
			downloadImage(remotepath,downloadFilePath);
		} else {
			image.setImageResource(default_res);
		}

		image.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finishWithNoAnim();
            }
        });
	}

	private void downloadImage(final String url,final String filename ) {
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage("下载图片: 0%");
		pd.show();

        new DownloadImage(url,filename).download(new DownloadImage.DownloadListener() {
            @Override
			public void onSuccess() {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DisplayMetrics metrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(metrics);
						int screenWidth = metrics.widthPixels;
						int screenHeight = metrics.heightPixels;
						bitmap = ImageUtils.decodeScaleImage(filename, screenWidth, screenHeight);
						if (bitmap == null) {
							image.setImageResource(default_res);
						} else {
							image.setImageBitmap(bitmap);
							ImageCache.getInstance().put(url, bitmap);
							isDownloaded = true;

						}
						if (pd != null) {
							pd.dismiss();
						}
					}
				});
			}
            @Override
			public void onFail() {
				File file = new File(localFilePath);
				if (file.exists()) {
					file.delete();
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pd.dismiss();
						image.setImageResource(default_res);
					}
				});
			}
            @Override
			public void onProgress(final int progress) {
				Log.d("ease", "Progress: " + progress);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pd.setMessage("下载图片: " + progress + "%");
					}
				});
			}
            });

//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				httpFileMgr.downloadFile(remoteFilePath, localFilePath, EMChatConfig.getInstance().APPKEY, headers, callback);
//			}
//		}).start();

	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pd!=null)
        pd.dismiss();
    }

    @Override
	public void onBackPressed() {
		if (isDownloaded)
			setResult(RESULT_OK);
		finish();
	}
}
