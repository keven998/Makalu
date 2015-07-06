package com.xuejian.client.lxp.module.my;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lv.im.IMClient;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.CustomLoadingDialog;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.thirdpart.weixin.WeixinApi;
import com.xuejian.client.lxp.common.utils.ShareUtils;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.MainActivity;

public class LoginActivity extends PeachBaseActivity {
    public final static int REQUEST_CODE_REG = 101;
    public final static int REQUEST_CODE_FIND_PASSWD = 102;

    @ViewInject(R.id.et_account)
    private EditText loginNameEt;
    @ViewInject(R.id.et_password)
    private EditText pwdEt;

    @ViewInject(R.id.btn_login)
    private Button loginBtn;
    private int request_code;
    private boolean autoLogin = false;
    private boolean isBackWeixinLoginPage = true;
    private boolean isWeixinClickLogin = false;
    CustomLoadingDialog dialog;

    //type
    private int LOGIN = 1;
    public int REGISTER = 2;
    private int WXLOGIN = 3;
    private int FINDPASSWORD = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        findViewById(R.id.iv_nav_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithNoAnim();
            }
        });
        findViewById(R.id.tv_reg).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegActivity.class);
                startActivityForResult(intent, REQUEST_CODE_REG);
            }
        });

        request_code = getIntent().getIntExtra("request_code", 0);
        if (request_code == REQUEST_CODE_REG) {
            Intent intent = new Intent(this, RegActivity.class);
            startActivityForResult(intent, REQUEST_CODE_REG);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("page_login");
        if (autoLogin) {
            return;
        }
        if (isBackWeixinLoginPage && isWeixinClickLogin) {
            dialog.dismiss();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_login");
    }

    private void initView() {
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);
        findViewById(R.id.tv_weixin_login).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //    MobclickAgent.onEvent(mContext,"event_login_with_weichat_account");
                weixinLogin();
            }
        });
        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        findViewById(R.id.tv_forget_pwd).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ForgetPwdActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FIND_PASSWD);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishWithNoAnim();
        overridePendingTransition(R.anim.slide_stay, R.anim.push_bottom_out);
    }

    private void imLogin(final User user, int type) {
        IMClient.getInstance().setCurrentUserId(String.valueOf(user.getUserId()));
        //初始化数据库，方便后面操作
        UserDBManager.getInstance().initDB(user.getUserId() + "");
        UserDBManager.getInstance().saveContact(user);
        IMClient.getInstance().initDB(String.valueOf(user.getUserId()));
        if (type == FINDPASSWORD) {
            //存入修改好的密码
        }

        //登录的时候需要新建用户名密码token表，方便用户自动登录的时候查询用户密码登录

        //3、存入内存
        AccountManager.getInstance().setLogin(true);
        AccountManager.getInstance().saveLoginAccount(mContext, user);
        AccountManager.setCurrentUserId(String.valueOf(user.getUserId()));
        // 进入主页面
        runOnUiThread(new Runnable() {
            public void run() {
                DialogManager.getInstance().dissMissLoadingDialog();
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivityWithNoAnim(intent);
                setResult(RESULT_OK);
                finishWithNoAnim();
            }
        });



       /* EMChatManager.getInstance().login(user.easemobUser, user.easemobPwd, new EMCallBack() {

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
                String result=UserApi.getAsynContact();
                System.out.println("result "+result);
                CommonJson<ContactListBean> contactResult = CommonJson.fromJson(result, ContactListBean.class);
              //  CommonJson<ContactListBean> contactResult=JSON.parseObject(result,ContactListBean.class);
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
                        ToastUtil.getInstance(LoginActivity.this).showToast("欢迎回到旅行派");
                        setResult(RESULT_OK);
                        finish();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
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
*/

    }

    private void signIn() {
        if (TextUtils.isEmpty(loginNameEt.getText()) || TextUtils.isEmpty(pwdEt.getText())) {
            ToastUtil.getInstance(mContext).showToast("我要账号和密码");
            return;
        }

        DialogManager.getInstance().showLoadingDialog(this);
        UserApi.signIn(loginNameEt.getText().toString().trim(), pwdEt.getText().toString().trim(), new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {

                CommonJson<User> userResult = CommonJson.fromJson(result, User.class);
                if (userResult.code == 0) {
                    imLogin(userResult.result, LOGIN);
                } else {
                    DialogManager.getInstance().dissMissLoadingDialog();
                    ToastUtil.getInstance(mContext).showToast(userResult.err.message);
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
//                error.printStackTrace();
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(LoginActivity.this).showToast(getResources().getString(R.string.request_network_failed));

            }
        });
    }


    public void weixinLogin() {
        isWeixinClickLogin = true;
        ShareUtils.configPlatforms(this);
        dialog = DialogManager.getInstance().showLoadingDialog(mContext, "正在授权");
        WeixinApi.getInstance().auth(this, new WeixinApi.WeixinAuthListener() {
            @Override
            public void onComplete(String code) {
                isBackWeixinLoginPage = false;
                ToastUtil.getInstance(mContext).showToast("授权成功");
                dialog.setContent("正在登录");
                UserApi.authSignUp(code, new HttpCallBack<String>() {
                    @Override
                    public void doSuccess(String result, String method) {
                        CommonJson<User> userResult = CommonJson.fromJson(result, User.class);
                        if (userResult.code == 0) {
                            imLogin(userResult.result, WXLOGIN);
                        } else {
                            ToastUtil.getInstance(mContext).showToast(userResult.err.message);
                        }
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        ToastUtil.getInstance(LoginActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                    }
                });
            }

            @Override
            public void onError(int errCode) {
                isBackWeixinLoginPage = false;
                DialogManager.getInstance().dissMissLoadingDialog();
                ToastUtil.getInstance(mContext).showToast("授权失败");
            }

            @Override
            public void onCancel() {
                isBackWeixinLoginPage = false;
                DialogManager.getInstance().dissMissLoadingDialog();
                ToastUtil.getInstance(mContext).showToast("授权取消");
            }


        });
        //dialog.dismiss();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REG && resultCode == RESULT_OK) {
            User user = (User) data.getSerializableExtra("user");
            loginNameEt.setText(user.getTel());
            DialogManager.getInstance().showLoadingDialog(this, "正在登录");
            imLogin(user, REGISTER);
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_FIND_PASSWD) {
            User user = (User) data.getSerializableExtra("user");
            DialogManager.getInstance().showLoadingDialog(this, "正在登录");
            imLogin(user, FINDPASSWORD);
        }
    }
}
