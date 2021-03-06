package com.xuejian.client.lxp.module;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.SharePrefUtil;
import com.aizou.core.utils.SharedPreferencesUtil;
import com.lv.im.IMClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.CoverStoryBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.UpdateUtil;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.my.LoginActivity;
import com.xuejian.client.lxp.module.my.RegActivity;

/**
 * 欢迎页，等待2秒，进入主界面
 */
public class SplashActivity extends Activity implements View.OnClickListener {
    private final int SPLASH_DISPLAY_LENGHT = 1500; // 延迟启动
    private boolean showSplash = true;
    private ImageView splashIv;
    private Button sp_log, sp_reg;
    private TextView sp_bounce;
    Handler handler;
    int REGESTER_REQUEST = 5;
    int LOGIN_REQUEST = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取用户登录状态的文件,如果不存在则新建一个
        SharedPreferences file = getSharedPreferences("isLogin", Activity.MODE_PRIVATE);
        if (file == null) {
            SharedPreferencesUtil.saveValue(SplashActivity.this, "isLogin", false);
        }
        IMClient.initIM(getApplicationContext());
        initView();
        MobclickAgent.updateOnlineConfig(this);
        initData();
    }

    protected void initData() {
        final User user = AccountManager.getInstance().getLoginAccount(this);
        if (user != null) {
            AccountManager.setCurrentUserId(String.valueOf(user.getUserId()));
            IMClient.getInstance().setCurrentUserId(String.valueOf(user.getUserId()));
        }
        final DisplayImageOptions picOptions = new DisplayImageOptions.Builder()
       //         .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnFail(R.drawable.bg_splash)
                .showImageForEmptyUri(R.drawable.bg_splash)
                .showImageOnLoading(R.drawable.bg_splash)
                .build();
        // .imageScaleType(ImageScaleType.)
        //
        // 延长2秒后进入主界面

        handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (user != null) {
                    imLogin(user);
                    //用户自动登录
                    //先从用户名密码Token表中取得用户信息然后自动登录
//                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    boolean hasLoad = SharePrefUtil.getBoolean(SplashActivity.this, "hasLoad_" + UpdateUtil.getVerName(SplashActivity.this), false);
                    if (showSplash && !hasLoad) {
                        Intent mainIntent = new Intent(SplashActivity.this, GuideActivity.class);
                        startActivity(mainIntent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        sp_log.setVisibility(View.VISIBLE);
                        sp_reg.setVisibility(View.VISIBLE);
                        sp_bounce.setVisibility(View.VISIBLE);
                        finish();
                    } else {
                        findViewById(R.id.bar_lyt).setVisibility(View.VISIBLE);
                        sp_log.setVisibility(View.VISIBLE);
                        sp_reg.setVisibility(View.VISIBLE);
                        sp_bounce.setVisibility(View.VISIBLE);
                        sp_log.setOnClickListener(SplashActivity.this);
                        sp_reg.setOnClickListener(SplashActivity.this);
                        sp_bounce.setOnClickListener(SplashActivity.this);
                    }
                }
            }
        };

        final String storyImageUrl = SharePrefUtil.getString(this, "story_image", "");
        ImageLoader.getInstance().displayImage("", splashIv, picOptions);
        OtherApi.getCoverStory(new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson<CoverStoryBean> storyResult = CommonJson.fromJson(result, CoverStoryBean.class);
                if (storyResult.code == 0) {
                    if (!storyResult.result.image.equals(storyImageUrl)) {
                        SharePrefUtil.saveString(SplashActivity.this, "story_image", "");
                        if (isFinishing()) return;
                        ImageLoader.getInstance().displayImage("", splashIv, picOptions, new ImageLoadingListener() {
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
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

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
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        splashIv = (ImageView) findViewById(R.id.iv_splash);
        sp_log = (Button) findViewById(R.id.sp_log);
        sp_reg = (Button) findViewById(R.id.sp_reg);
        sp_bounce = (TextView) findViewById(R.id.sp_bounce);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sp_log:
                Intent logActivity = new Intent(SplashActivity.this, LoginActivity.class); //
                logActivity.putExtra("isFromSplash", true);
                startActivityForResult(logActivity, LOGIN_REQUEST);
                overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
                break;

            case R.id.sp_reg:
                Intent regActivity = new Intent(SplashActivity.this, RegActivity.class);
                startActivityForResult(regActivity, REGESTER_REQUEST);
                overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
                break;

            case R.id.sp_bounce:
                Intent mainActivity = new Intent(SplashActivity.this, MainActivity.class);
                mainActivity.putExtra("FromBounce", true);
                startActivity(mainActivity);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == REGESTER_REQUEST){
                User user = data.getParcelableExtra("user");
                try {
                    DialogManager.getInstance().showLoadingDialog(this, "正在登录");
                } catch (Exception e) {
                    DialogManager.getInstance().dissMissLoadingDialog();
                }

                imLogin(user);
            }else if (requestCode ==LOGIN_REQUEST){
                finish();
            }
        }
    }

    private void imLogin(final User user) {
        //初始化数据库，方便后面操作
        UserDBManager.getInstance().initDB(user.getUserId() + "");
        UserDBManager.getInstance().saveContact(user);
        int version = SharePrefUtil.getInt(this, "dbversion", 0);
        IMClient.getInstance().initDB(String.valueOf(user.getUserId()), 1, version);
        SharePrefUtil.saveInt(this, "dbversion", 1);
        //3、存入内存
        AccountManager.getInstance().setLogin(true);
        AccountManager.getInstance().saveLoginAccount(this, user);
        AccountManager.setCurrentUserId(String.valueOf(user.getUserId()));
        // 进入主页面
        runOnUiThread(new Runnable() {
            public void run() {
                DialogManager.getInstance().dissMissLoadingDialog();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
}
