package com.aizou.peachtravel.module.my;


import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.ContactListBean;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.thirdpart.SnsAccountsUtils;
import com.aizou.peachtravel.common.thirdpart.weiboapi.User;
import com.aizou.peachtravel.common.thirdpart.weiboapi.WeiboUsersAPI;
import com.aizou.peachtravel.common.thirdpart.weixin.WeixinApi;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.utils.ShareUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.config.Constant;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.easemob.EMCallBack;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends PeachBaseActivity {
    public final static int REQUEST_CODE_REG=101;

    @ViewInject(R.id.et_user)
    private EditText loginNameEt;
    @ViewInject(R.id.et_password)
    private EditText pwdEt;

    @ViewInject(R.id.btn_login)
    private Button loginBtn;
    @ViewInject(R.id.title_bar)
    private TitleHeaderBar titleBar;
    //	private View mTitlebar;
    // sina weibo sso handler
    private SsoHandler mWeiboSsoHandler;
    // tencent sso handler
//	private QQAuth mQQAuth;
    private Tencent mTencentHandler;

    public JSONObject uploadJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);
        initTitlebar();
        findViewById(R.id.btn_weixin_login).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager.getInstance().showProgressDialog(mContext, "正在登录");
                weixinLogin();
//                UserApi.authSignUp("123456",new HttpCallBack() {
//                    @Override
//                    public void doSucess(Object result, String method) {
//
//                    }
//
//                    @Override
//                    public void doFailure(Exception error, String msg, String method) {
//
//                    }
//                });
            }
        });
        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent =new Intent(mContext,AccountActvity.class);
//                startActivity(intent);
                signIn();
            }
        });
        findViewById(R.id.tv_forget_pwd).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ForgetPwdActivity.class);
                startActivityForResult(intent,REQUEST_CODE_REG);
            }
        });
    }

    private void initTitlebar() {
        titleBar.getRightTextView().setText("注册");
        titleBar.setRightOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RegActivity.class);
                startActivity(intent);
            }
        });

        titleBar.getTitleTextView().setText("登录");
        titleBar.enableBackKey(true);
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
    }
    private void imLogin(final PeachUser user){
        EMChatManager.getInstance().login(user.easemobUser, user.easemobPwd, new EMCallBack() {

            @Override
            public void onSuccess() {

                // 登陆成功，保存用户名密码
                try {
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
                    List <IMUser> users = new ArrayList<IMUser>(userlist.values());
                    IMUserRepository.saveContactList(mContext,users);
                    // 获取群聊列表(群聊里只有groupid和groupname的简单信息),sdk会把群组存入到内存和db中
                    EMGroupManager.getInstance().asyncGetGroupsFromServer(new EMValueCallBack<List<EMGroup>>() {
                        @Override
                        public void onSuccess(List<EMGroup> emGroups) {
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
                    UserApi.getContact(new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {
                            CommonJson<ContactListBean> contactResult = CommonJson.fromJson(result,ContactListBean.class);
                            if(contactResult.code==0){
                                for (PeachUser peachUser : contactResult.result.contacts) {
                                    IMUser user = new IMUser();
                                    user.setUserId(peachUser.userId);
                                    user.setMemo(peachUser.memo);
                                    user.setNick(peachUser.nickName);
                                    user.setUsername(peachUser.easemobUser);
                                    user.setUnreadMsgCount(0);
                                    user.setAvatar(peachUser.avatar);
                                    user.setSignature(peachUser.signature);
                                    user.setIsMyFriends(true);
                                    user.setGender(peachUser.gender);
                                    IMUtils.setUserHead(user);
                                    userlist.put(peachUser.easemobUser, user);
                                }
                                // 存入内存
                                AccountManager.getInstance().setContactList(userlist);
                                // 存入db
                                List <IMUser> users = new ArrayList<IMUser>(userlist.values());
                                IMUserRepository.saveContactList(mContext,users);
                            }

                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {

                        }
                    });
                    // 进入主页面
                    runOnUiThread(new Runnable() {
                        public void run() {
                            DialogManager.getInstance().dissMissProgressDialog();

                        }
                    });
                    setResult(RESULT_OK);
                    finish();


                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            DialogManager.getInstance().dissMissProgressDialog();
                            Toast.makeText(getApplicationContext(), "登录失败: ", Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, final String message) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        DialogManager.getInstance().dissMissProgressDialog();
                        Toast.makeText(getApplicationContext(), "登录失败: " + message, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });


}

    private void signIn() {
        if (TextUtils.isEmpty(loginNameEt.getText())) {
            ToastUtil.getInstance(mContext).showToast("请输入手机号、昵称或ID");
            return;
        }
        if (TextUtils.isEmpty(pwdEt.getText())) {
            ToastUtil.getInstance(mContext).showToast("请输入密码");
            return;
        }
        DialogManager.getInstance().showProgressDialog(this);
        UserApi.signIn(loginNameEt.getText().toString().trim(), pwdEt.getText().toString().trim(), new HttpCallBack<String>() {

            @Override
            public void doSucess(String result, String method) {

                CommonJson<PeachUser> userResult = CommonJson.fromJson(result, PeachUser.class);
                if (userResult.code == 0) {
//                    userResult.result.easemobUser="rjm4413";
//                    userResult.result.easemobPwd="123456";
                    imLogin(userResult.result);

//                    imLogin("rjm4413","123456","小明");
                } else {
                    DialogManager.getInstance().dissMissProgressDialog();
                    ToastUtil.getInstance(mContext).showToast(userResult.err.message);
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissProgressDialog();
            }
        });
    }

    private void qqLogin(boolean login) {

        mTencentHandler = Tencent.createInstance(SnsAccountsUtils.TencentConstants.APP_KEY,
                this.getApplicationContext());
        if (login) {
            if (!mTencentHandler.isSessionValid()) {
                mTencentHandler.login(this, SnsAccountsUtils.TencentConstants.SCOPE,
                        new TencentUiListener(TencentUiListener.TYPE_LOGIN));
            }
        } else {
            mTencentHandler.logout(this.getApplicationContext());
        }
    }

    public void weixinLogin() {
        ShareUtils.configPlatforms(this);
        WeixinApi.getInstance().auth(this, new WeixinApi.WeixinAuthListener() {
            @Override
            public void onComplete(String code) {
                ToastUtil.getInstance(mContext).showToast("授权成功");
                UserApi.authSignUp(code, new HttpCallBack<String>() {
                    @Override
                    public void doSucess(String result, String method) {
                        DialogManager.getInstance().dissMissProgressDialog();
                        CommonJson<PeachUser> userResult = CommonJson.fromJson(result, PeachUser.class);
                        if (userResult.code == 0) {
//                            userResult.result.easemobUser="rjm4413";
//                            userResult.result.easemobPwd="123456";
                            imLogin(userResult.result);
//                            imLogin("rjm4413","123456","小明");
                        } else {
                            ToastUtil.getInstance(mContext).showToast(userResult.err.message);
                        }
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {

                    }
                });
            }

            @Override
            public void onError(int errCode) {
                DialogManager.getInstance().dissMissProgressDialog();
                ToastUtil.getInstance(mContext).showToast("授权失败");

            }

            @Override
            public void onCancel() {
                DialogManager.getInstance().dissMissProgressDialog();
                ToastUtil.getInstance(mContext).showToast("授权取消");
            }
        });

    }

    private void weiboLogin(boolean login) {
        if (login) {
            WeiboAuth.AuthInfo authInfo = new WeiboAuth.AuthInfo(this, SnsAccountsUtils.WeiboConstants.APP_KEY,
                    SnsAccountsUtils.WeiboConstants.REDIRECT_URL, SnsAccountsUtils.WeiboConstants.SCOPE);
            WeiboAuth weiboAuth = new WeiboAuth(this, authInfo);
            if (mWeiboSsoHandler == null) {
                mWeiboSsoHandler = new SsoHandler((Activity) this, weiboAuth);
            }
            mWeiboSsoHandler.authorize(new SinaWeiboAuthListener());
        } else {
            // new
            // LogoutAPI(AccessTokenKeeper.readAccessToken(this)).logout(mLogoutListener);
        }

    }

    /**
     * sina weibo登入按钮的监听器，接收授权结果。
     */
    private class SinaWeiboAuthListener implements WeiboAuthListener {
        @SuppressLint("NewApi")
        @Override
        public void onComplete(Bundle values) {
            Oauth2AccessToken accessToken = Oauth2AccessToken
                    .parseAccessToken(values);
            LogUtil.d("test", "weibo = " + values);
            if (accessToken != null && accessToken.isSessionValid()) {
                try {
                    uploadJson.put("oauthId", values.getString("uid"));
                    uploadJson.put("token", values.get("access_token"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                WeiboUsersAPI userApi = new WeiboUsersAPI(accessToken);
                userApi.show(Long.parseLong(values.getString("uid")), new RequestListener() {

                    @Override
                    public void onWeiboException(WeiboException arg0) {
                        arg0.printStackTrace();

                    }

                    @Override
                    public void onComplete(String arg0) {
                        LogUtil.d(arg0);
                        if (!TextUtils.isEmpty(arg0)) {
                            User user = User.parse(arg0);
                            try {
                                uploadJson.put("nickName", user.screen_name);
                                uploadJson.put("avatar", user.avatar_large);
                                uploadJson.put("provider", "weibo");
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            syncAccount(uploadJson.toString());
                        }

                    }
                });
            } else {
                String code = values.getString("code", "");
                LogUtil.d("test", "login weibo error code = " + code);
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            DialogManager.getInstance().dissMissProgressDialog();
            Toast.makeText(LoginActivity.this, e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            DialogManager.getInstance().dissMissProgressDialog();
            Toast.makeText(LoginActivity.this, "微博登录取消", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private class TencentUiListener implements IUiListener {
        public static final int TYPE_LOGIN = 1;
        public static final int TYPE_GETINFO = 2;

        private int type;

        public TencentUiListener(int t) {
            super();
            type = t;
        }

        @Override
        public void onComplete(Object response) {
            doComplete((JSONObject) response);
            LogUtil.d("test", "qq response = " + response);
//			syncAccount("qq", response.toString());
        }

        protected void doComplete(JSONObject values) {
            // Toast.makeText(LoginActivity.this, "QQ登录成功", Toast.LENGTH_SHORT)
            // .show();
            if (type == TYPE_LOGIN) {
                QQAuth qa = QQAuth.createInstance(SnsAccountsUtils.TencentConstants.APP_KEY,
                        LoginActivity.this.getApplicationContext());
                try {
                    uploadJson.put("oauthId", values.getString("openid"));
                    uploadJson.put("token", values.getString("access_token"));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                UserInfo info = new UserInfo(LoginActivity.this,
                        qa.getQQToken());
                info.getUserInfo(new TencentUiListener(TYPE_GETINFO));
            } else {
                try {
                    uploadJson.put("nickName", values.getString("nickname"));
                    uploadJson.put("avatar", values.getString("figureurl_qq_2"));
                    uploadJson.put("provider", "tencent");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                syncAccount(uploadJson.toString());
            }
        }

        @Override
        public void onError(UiError e) {
            DialogManager.getInstance().dissMissProgressDialog();
            Toast.makeText(LoginActivity.this, "QQ登录失败", Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onCancel() {
            DialogManager.getInstance().dissMissProgressDialog();
            Toast.makeText(LoginActivity.this, "QQ登录取消", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void syncAccount(String data) {
        DialogManager.getInstance().showProgressDialog(mContext, "正在登录");
        LogUtil.d(data);


    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mWeiboSsoHandler = null;
        mTencentHandler = null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        super.onActivityResult(requestCode, resultCode, data);
        if (mWeiboSsoHandler != null) {
            mWeiboSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        if (mTencentHandler != null) {
            mTencentHandler.onActivityResult(requestCode, resultCode, data);
        }
        if(resultCode==RESULT_OK&&requestCode==REQUEST_CODE_REG){
            PeachUser user = (PeachUser) data.getSerializableExtra("user");
            DialogManager.getInstance().showProgressDialog(mContext,"正在登录");
            imLogin(user);
        }
    }

}
