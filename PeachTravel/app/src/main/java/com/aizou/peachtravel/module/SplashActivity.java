package com.aizou.peachtravel.module;

import com.aizou.core.utils.SharePrefUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.common.utils.UpdateUtil;

import android.app.Notification;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

/**
 * 欢迎页，等待2秒，进入主界面
 */
public class SplashActivity extends PeachBaseActivity {
	private final int SPLASH_DISPLAY_LENGHT = 2000; // 延迟三秒
	private boolean showSplash = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}

	protected void initData() {

		// 延长2秒后进入主界面
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				boolean hasLoad = SharePrefUtil.getBoolean(mContext, "hasLoad_"
                        + UpdateUtil.getVerName(mContext), false);
				if (showSplash && !hasLoad) {
					Intent mainIntent = new Intent(SplashActivity.this,
							GuideActivity.class);
					SplashActivity.this.startActivity(mainIntent);
					// overridePendingTransition(R.anim.zoom_in,
					// R.anim.zoom_out);
					SplashActivity.this.finish();
				} else {
					Intent mainIntent = new Intent(SplashActivity.this,
							MainActivity.class);
					SplashActivity.this.startActivity(mainIntent);
					// overridePendingTransition(R.anim.zoom_in,
					// R.anim.zoom_out);
					SplashActivity.this.finish();
				}

			}

		}, SPLASH_DISPLAY_LENGHT);

	}

	protected void initView() {
		// 全屏设置
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
	}

}
