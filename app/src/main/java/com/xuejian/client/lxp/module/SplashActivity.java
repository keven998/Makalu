package com.xuejian.client.lxp.module;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.SharePrefUtil;
import com.easemob.EMCallBack;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.util.EMLog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ContactListBean;
import com.xuejian.client.lxp.bean.CoverStoryBean;
import com.xuejian.client.lxp.bean.PeachUser;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.UpdateUtil;
import com.xuejian.client.lxp.config.Constant;
import com.xuejian.client.lxp.db.IMUser;
import com.xuejian.client.lxp.db.respository.IMUserRepository;
import com.xuejian.client.lxp.module.my.LoginActivity;
import com.xuejian.client.lxp.module.my.RegActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    int REGESTER_REQUEST=5;

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
                            findViewById(R.id.bar_lyt).setVisibility(View.VISIBLE);
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
                if (storyResult.code == 0) {
                    if (!storyResult.result.image.equals(storyImageUrl)) {
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
                startActivityForResult(regActivity, REGESTER_REQUEST);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGESTER_REQUEST &&resultCode == RESULT_OK) {
            PeachUser user = (PeachUser) data.getSerializableExtra("user");
            DialogManager.getInstance().showLoadingDialog(mContext, "正在登录");
            imLogin(user);
        }
    }

    private void imLogin(final PeachUser user) {
        EMChatManager.getInstance().login(user.easemobUser, user.easemobPwd, new EMCallBack() {

            @Override
            public void onSuccess() {

                // 登陆成功，保存用户名密码
                // demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
                AccountManager.getInstance().saveLoginAccount(mContext, user);
                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(user.nickName);
                if (!updatenick) {
                    EMLog.e("LoginActivity", "update current user nick fail");
                }

                // 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中
//                    List<String> usernames = EMContactManager.getInstance().getContactUserNames();
                final Map<String, IMUser> userlist = new HashMap<String, IMUser>();
                // 添加user"申请与通知"
                IMUser newFriends = new IMUser();
                newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
                newFriends.setNick("申请与通知");
                newFriends.setHeader("");
                newFriends.setIsMyFriends(true);
                userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);


//                    // 添加"群聊"
//                    IMUser groupUser = new IMUser();
//                    groupUser.setUsername(Constant.GROUP_USERNAME);
//                    groupUser.setNick("群聊");
//                    groupUser.setHeader("");
//                    groupUser.setUnreadMsgCount(0);
//                    userlist.put(Constant.GROUP_USERNAME, groupUser);
                // 存入内存
                AccountManager.getInstance().setContactList(userlist);
                List<IMUser> users = new ArrayList<IMUser>(userlist.values());
                IMUserRepository.saveContactList(mContext, users);


                // 获取群聊列表(群聊里只有groupid和groupname的简单信息),sdk会把群组存入到内存和db中
                final long startTime = System.currentTimeMillis();
//                LogUtil.d("getGroupFromServer", startTime + "");
                EMGroupManager.getInstance().asyncGetGroupsFromServer(new EMValueCallBack<List<EMGroup>>() {
                    @Override
                    public void onSuccess(List<EMGroup> emGroups) {
//                        long endTime = System.currentTimeMillis();
//                        LogUtil.d("getGroupFromServer", endTime - startTime + "--groudSize=" + emGroups.size());

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });

                // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                // ** manually load all local groups and
                // conversations in case we are auto login
                EMGroupManager.getInstance().loadAllGroups();
                EMChatManager.getInstance().loadAllConversations();
                String result= UserApi.getAsynContact();
                CommonJson<ContactListBean> contactResult = CommonJson.fromJson(result, ContactListBean.class);
                if (contactResult.code == 0) {
                    for (PeachUser peachUser : contactResult.result.contacts) {
                        IMUser user = new IMUser();
                        user.setUserId(peachUser.userId);
                        user.setMemo(peachUser.memo);
                        user.setNick(peachUser.nickName);
                        user.setUsername(peachUser.easemobUser);
                        user.setUnreadMsgCount(0);
                        user.setAvatar(peachUser.avatar);
                        user.setAvatarSmall(peachUser.avatarSmall);
                        user.setSignature(peachUser.signature);
                        user.setIsMyFriends(true);
                        user.setGender(peachUser.gender);
                        IMUtils.setUserHead(user);
                        userlist.put(peachUser.easemobUser, user);
                    }
                    // 存入内存
                    AccountManager.getInstance().setContactList(userlist);
                    // 存入db
                    List<IMUser> netusers = new ArrayList<IMUser>(userlist.values());
                    IMUserRepository.saveContactList(mContext, netusers);
                }
                // 进入主页面
                runOnUiThread(new Runnable() {
                    public void run() {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        ToastUtil.getInstance(SplashActivity.this).showToast("欢迎回到旅行派");
                        setResult(RESULT_OK);
                        finish();
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        overridePendingTransition(0,R.anim.push_bottom_out);

                    }
                });


            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, final String message) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        ToastUtil.getInstance(getApplicationContext()).showToast("登录失败 " + message);
                    }
                });
            }
        });


    }

}
