package com.aizou.peachtravel.module;

import com.aizou.core.utils.SharePrefUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.BaseActivity;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.utils.UpdateUtil;
import com.aizou.peachtravel.module.toolbox.im.IMMainActivity;
import com.easemob.chat.EMMessage;
import com.igexin.sdk.PushManager;

import android.app.Activity;
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
	private final int SPLASH_DISPLAY_LENGHT = 1800; // 延迟启动
	private boolean showSplash = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}

	protected void initData() {
        final EMMessage emMessage = getIntent().getParcelableExtra("im_message");
        PushManager.getInstance().initialize(this.getApplicationContext());
        final PeachUser user = AccountManager.getInstance().getLoginAccount(mContext);
		// 延长2秒后进入主界面
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
                if(emMessage!=null&&user!=null){
                    Intent intent = new Intent(mContext, IMMainActivity.class);
                    startActivity(intent);
                }else{
                    boolean hasLoad = SharePrefUtil.getBoolean(SplashActivity.this, "hasLoad_" + UpdateUtil.getVerName(SplashActivity.this), false);
                    if (showSplash && !hasLoad) {
                        Intent mainIntent = new Intent(SplashActivity.this, GuideActivity.class);
                        startActivity(mainIntent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else {
                        Intent mainActivity = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(mainActivity);
                        Intent storyIntent = new Intent(SplashActivity.this, StoryActivity.class);
                        startActivity(storyIntent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
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
