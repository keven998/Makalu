package com.aizou.peachtravel.module;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.SharePrefUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.CoverStoryBean;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.OtherApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.UpdateUtil;
import com.aizou.peachtravel.module.my.LoginActivity;
import com.aizou.peachtravel.module.my.RegActivity;
import com.aizou.peachtravel.module.toolbox.im.IMMainActivity;
import com.easemob.chat.EMChat;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 欢迎页，等待2秒，进入主界面
 */
public class SplashActivity extends PeachBaseActivity implements View.OnClickListener{
	private final int SPLASH_DISPLAY_LENGHT = 1500; // 延迟启动
	private boolean showSplash = false;
    private ImageView splashIv;
    private Button sp_log,sp_reg;
    private TextView sp_bounce;
    Handler handler;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
//        MobclickAgent.openActivityDurationTrack(false);
	}

	protected void initData() {
        final boolean isFromTalk = getIntent().getBooleanExtra("isFromTalk",false);
//        PushManager.getInstance().initialize(this.getApplicationContext());
        final PeachUser user = AccountManager.getInstance().getLoginAccount(mContext);
        final DisplayImageOptions picOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .showImageOnFail(R.drawable.bg_splash)
                .showImageForEmptyUri(R.drawable.bg_splash)
                .showImageOnLoading(R.drawable.bg_splash)
//				.decodingOptions(D)
//                .displayer(new FadeInBitmapDisplayer(180, true, true, false))
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
		// 延长2秒后进入主界面

        handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (isFromTalk && user != null) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivityWithNoAnim(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    boolean hasLoad = SharePrefUtil.getBoolean(SplashActivity.this, "hasLoad_" + UpdateUtil.getVerName(SplashActivity.this), false);
//                    hasLoad=false;
                    if (showSplash && !hasLoad) {
                        Intent mainIntent = new Intent(SplashActivity.this, GuideActivity.class);
                        startActivityWithNoAnim(mainIntent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        if(EMChat.getInstance().isLoggedIn()){
                            Intent mainActivity = new Intent(SplashActivity.this, MainActivity.class);
                            startActivityWithNoAnim(mainActivity);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }else{
                            sp_log.setVisibility(View.VISIBLE);
                            sp_reg.setVisibility(View.VISIBLE);
                            sp_bounce.setVisibility(View.VISIBLE);
                            sp_log.setOnClickListener(SplashActivity.this);
                            sp_reg.setOnClickListener(SplashActivity.this);
                            sp_bounce.setOnClickListener(SplashActivity.this);
                           /* Intent mainActivity = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivityWithNoAnim(mainActivity);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);*/
                        }


//                        Intent storyIntent = new Intent(SplashActivity.this, StoryActivity.class);
//                        startActivityWithNoAnim(storyIntent);
                    }
                }
            }

        };

        final String storyImageUrl = SharePrefUtil.getString(this, "story_image", "");
        ImageLoader.getInstance().displayImage(storyImageUrl, splashIv, picOptions);
        OtherApi.getCoverStory(new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<CoverStoryBean> storyResult = CommonJson.fromJson(result, CoverStoryBean.class);
                if(storyResult.code == 0) {
                    if(!storyResult.result.image.equals(storyImageUrl)){
                        SharePrefUtil.saveString(SplashActivity.this, "story_image", storyResult.result.image);
                        if (isFinishing()) return;
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
        handler.postDelayed(runnable, SPLASH_DISPLAY_LENGHT);
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void initView() {
		// 全屏设置
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
        splashIv = (ImageView) findViewById(R.id.iv_splash);
        sp_log = (Button) findViewById(R.id.sp_log);
        sp_reg = (Button) findViewById(R.id.sp_reg);
        sp_bounce = (TextView) findViewById(R.id.sp_bounce);
	}

    private void getStroy(){

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sp_log:
                Intent logActivity = new Intent(SplashActivity.this, LoginActivity.class);
                startActivityWithNoAnim(logActivity);
                overridePendingTransition(R.anim.push_bottom_in, 0);
                break;

            case R.id.sp_reg:
                Intent regActivity = new Intent(SplashActivity.this, RegActivity.class);
                startActivityWithNoAnim(regActivity);
                overridePendingTransition(R.anim.push_bottom_in, 0);
                break;

            case R.id.sp_bounce:
                Intent mainActivity = new Intent(SplashActivity.this, MainActivity.class);
                startActivityWithNoAnim(mainActivity);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            default:break;
        }
    }

}
