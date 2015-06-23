package com.xuejian.client.lxp.module.toolbox.im;

import android.os.Bundle;
import android.widget.ProgressBar;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.ChatBaseActivity;

import java.io.File;

public class ShowNormalFileActivity extends ChatBaseActivity {
	private ProgressBar progressBar;
	private File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_file);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

//		final FileMessageBody messageBody = getIntent().getParcelableExtra("msgbody");
//		file = new File(messageBody.getLocalUrl());
//		//set head map
//		final Map<String, String> maps = new HashMap<String, String>();
//		String accessToken=EMChatManager.getInstance().getAccessToken();
//		maps.put("Authorization", "Bearer " + accessToken);
//		if (!TextUtils.isEmpty(messageBody.getSecret())) {
//			maps.put("share-secret", messageBody.getSecret());
//		}
//		maps.put("Accept", "application/octet-stream");
		
		//下载文件
		new Thread(new Runnable() {
			public void run() {
//				HttpFileManager fileManager = new HttpFileManager(ShowNormalFileActivity.this, EMChatConfig.getInstance().getStorageUrl());
//				fileManager.downloadFile(messageBody.getRemoteUrl(), messageBody.getLocalUrl(), EMChatConfig.getInstance().APPKEY,maps,
//						new CloudOperationCallback() {
//
//							@Override
//							public void onSuccess(String result) {
//								runOnUiThread(new Runnable() {
//									public void run() {
//										FileUtils.openFile(file, ShowNormalFileActivity.this);
//										finish();
//									}
//								});
//							}
//
//							@Override
//							public void onProgress(final int progress) {
//								runOnUiThread(new Runnable() {
//									public void run() {
//										progressBar.setProgress(progress);
//									}
//								});
//							}
//
//							@Override
//							public void onError(final String msg) {
//                                if (!isFinishing())
//								runOnUiThread(new Runnable() {
//									public void run() {
//										if(file != null && file.exists())
//											file.delete();
////										Toast.makeText(ShowNormalFileActivity.this, "下载文件失败: "+msg,Toast.LENGTH_SHORT).show();
//                                        ToastUtil.getInstance(getApplicationContext()).showToast("下载失败");
//										finish();
//									}
//								});
//							}
//						});

			}
		}).start();
		
	}
}
