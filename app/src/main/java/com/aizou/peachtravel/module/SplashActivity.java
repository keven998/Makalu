package com.aizou.peachtravel.module;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.utils.SharePrefUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.BaseActivity;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.CoverStoryBean;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.OtherApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.utils.UpdateUtil;
import com.aizou.peachtravel.module.toolbox.im.IMMainActivity;
import com.easemob.chat.EMMessage;
import com.igexin.sdk.PushManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * 欢迎页，等待2秒，进入主界面
 */
public class SplashActivity extends PeachBaseActivity {
	private final int SPLASH_DISPLAY_LENGHT = 2000; // 延迟启动
	private boolean showSplash = true;
    private ImageView splashIv;
    Handler handler;

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
        final DisplayImageOptions picOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
//                .showImageOnFail(R.drawable.ic_launcher)
//                .showImageForEmptyUri(R.drawable.ic_launcher)
//				.decodingOptions(D)
//                .displayer(new FadeInBitmapDisplayer(180, true, true, false))
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
		// 延长2秒后进入主界面

        handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (emMessage != null && user != null) {
                    Intent intent = new Intent(mContext, IMMainActivity.class);
                    startActivityWithNoAnim(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    boolean hasLoad = SharePrefUtil.getBoolean(SplashActivity.this, "hasLoad_" + UpdateUtil.getVerName(SplashActivity.this), false);
//                    hasLoad=false;
                    if (showSplash && !hasLoad) {
                        Intent mainIntent = new Intent(SplashActivity.this, GuideActivity.class);
                        startActivityWithNoAnim(mainIntent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else {
                        Intent mainActivity = new Intent(SplashActivity.this, MainActivity.class);
                        startActivityWithNoAnim(mainActivity);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                        Intent storyIntent = new Intent(SplashActivity.this, StoryActivity.class);
//                        startActivityWithNoAnim(storyIntent);
                    }
                }
            }

        };
        handler.postDelayed(runnable, SPLASH_DISPLAY_LENGHT);

        final String storyImageUrl = SharePrefUtil.getString(this, "story_image", "");
        ImageLoader.getInstance().displayImage(storyImageUrl, splashIv, picOptions);
        OtherApi.getCoverStory(new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<CoverStoryBean> storyResult = CommonJson.fromJson(result, CoverStoryBean.class);
                if(storyResult.code == 0) {
                    if(!storyResult.result.image.equals(storyImageUrl)){
                        SharePrefUtil.saveString(SplashActivity.this, "story_image", storyResult.result.image);
                        ImageLoader.getInstance().displayImage(storyResult.result.image, splashIv, picOptions, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                if (handler != null && !isFinishing()) {
                                    handler.removeCallbacks(runnable);
                                    handler.postDelayed(runnable, 1200);
                                }
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                        });
                    }

                } else {
//                    ToastUtil.getInstance(StoryActivity.this).showToast("请求也是失败了");
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
            }
        });
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
    }

    protected void initView() {
		// 全屏设置
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
        splashIv = (ImageView) findViewById(R.id.iv_splash);
	}

    private void getStroy(){

    }

}
