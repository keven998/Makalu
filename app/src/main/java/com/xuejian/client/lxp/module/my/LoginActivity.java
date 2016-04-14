package com.xuejian.client.lxp.module.my;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.http.HttpManager;
import com.lv.im.IMClient;
import com.lv.utils.SharePrefUtil;
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

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends PeachBaseActivity {
    public final static int REQUEST_CODE_REG = 101;
    public final static int REQUEST_CODE_FIND_PASSWD = 102;

    @Bind(R.id.et_account)
    AutoCompleteTextView loginNameEt;
    @Bind(R.id.et_password)
    EditText pwdEt;

    @Bind(R.id.btn_login)
    Button loginBtn;
    private int request_code;
    private boolean autoLogin = false;
    private boolean isBackWeixinLoginPage = true;
    private boolean isWeixinClickLogin = false;
    CustomLoadingDialog dialog;
    private boolean isFromSplash,isFromTalkShare,isFromGoods;

    //type
    private int LOGIN = 1;
    public int REGISTER = 2;
    private int WXLOGIN = 3;
    private int FINDPASSWORD = 4;
    private String tempPhoneNum;

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
        setResult(RESULT_OK);
        isFromSplash = getIntent().getBooleanExtra("isFromSplash",false);
        isFromTalkShare = getIntent().getBooleanExtra("isFromTalkShare",false);
        isFromGoods = getIntent().getBooleanExtra("isFromGoods",false);
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
        ButterKnife.bind(this);
        String phoneNum = SharePrefUtil.getPhoneNum(this,"lastPhone");
        if (phoneNum!=null){
            String [] arr={""};
            arr[0]=phoneNum;
            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_login_input,arr);
            loginNameEt.setAdapter(arrayAdapter);
        }

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
                if (!TextUtils.isEmpty(tempPhoneNum))intent.putExtra("phone",tempPhoneNum);
                startActivityForResult(intent, REQUEST_CODE_FIND_PASSWD);
            }
        });
        pwdEt.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    signIn();
                }
                return false;
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
        int version = com.aizou.core.utils.SharePrefUtil.getInt(this, "dbversion", 0);
        IMClient.getInstance().initDB(String.valueOf(user.getUserId()),1,version);
        com.aizou.core.utils.SharePrefUtil.saveInt(this, "dbversion", 1);
        //登录的时候需要新建用户名密码token表，方便用户自动登录的时候查询用户密码登录

        //3、存入内存
        AccountManager.getInstance().setLogin(true);
        AccountManager.getInstance().saveLoginAccount(mContext, user);
        AccountManager.setCurrentUserId(String.valueOf(user.getUserId()));
        User wenwen=new User();
        wenwen.setNickName("旅行问问");
        wenwen.setUserId(10001l);
        UserDBManager.getInstance().saveContact(wenwen);
        User paipai=new User();
        paipai.setNickName("派派");
        paipai.setUserId(10000l);
        UserDBManager.getInstance().saveContact(paipai);

        User trade = new User();
        trade.setNickName("交易消息");
        trade.setUserId(10002l);
        trade.setType(1);
        UserDBManager.getInstance().saveContact(trade);

        User activity = new User();
        activity.setNickName("活动消息");
        activity.setUserId(10003l);
        activity.setType(1);
        UserDBManager.getInstance().saveContact(activity);

        // 进入主页面
        runOnUiThread(new Runnable() {
            public void run() {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (isFromTalkShare||isFromGoods) {
                    finish();
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("reLogin", true);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    if (isFromSplash) {
                       setResult(RESULT_OK);
                    }
                }
                // setResult(RESULT_OK);
                // finishWithNoAnim();
            }
        });

    }

    private void signIn() {
        if (TextUtils.isEmpty(loginNameEt.getText()) || TextUtils.isEmpty(pwdEt.getText())) {
            ToastUtil.getInstance(mContext).showToast("请输入账号和密码");
            return;
        }
//        if (!RegexUtils.isMobileNO(loginNameEt.getText().toString().trim())) {
//            ToastUtil.getInstance(this).showToast("请正确输入11位手机号");
//            return;
//        }
        try {
            DialogManager.getInstance().showLoadingDialog(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UserApi.signIn(loginNameEt.getText().toString().trim(), pwdEt.getText().toString().trim(), new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<User> userResult = CommonJson.fromJson(result, User.class);
                if (userResult.code == 0) {
                    SharePrefUtil.savePhoneNum(LoginActivity.this,"lastPhone",loginNameEt.getText().toString().trim());
                    imLogin(userResult.result, LOGIN);

                } else {
                    ToastUtil.getInstance(mContext).showToast(userResult.err.message);
                    tempPhoneNum = loginNameEt.getText().toString().trim();
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
//                error.printStackTrace();


            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {
                DialogManager.getInstance().dissMissLoadingDialog();
                tempPhoneNum = loginNameEt.getText().toString().trim();
                if (code == HttpManager.PWD_ERROR) {
                    ToastUtil.getInstance(LoginActivity.this).showToast("用户名或密码错误");
                } else if (!isFinishing())
                    ToastUtil.getInstance(LoginActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }


    public void weixinLogin() {
        if (!WeixinApi.getInstance().isWXinstalled(this)){
            ToastUtil.getInstance(mContext).showToast("你还没有安装微信");
            return;
        }
        isWeixinClickLogin = true;
        ShareUtils.configPlatforms(this);
        try {
            dialog =  DialogManager.getInstance().showLoadingDialog(mContext, "正在授权");
        }catch (Exception e){
            DialogManager.getInstance().dissMissLoadingDialog();
        }
        WeixinApi.getInstance().auth(this, new WeixinApi.WeixinAuthListener() {
            @Override
            public void onComplete(String code) {
                isBackWeixinLoginPage = false;
                ToastUtil.getInstance(mContext).showToast("授权成功");
                if (dialog!=null)dialog.setContent("正在登录");
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

                    @Override
                    public void doFailure(Exception error, String msg, String method, int code) {

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
            User user = data.getParcelableExtra("user");
            loginNameEt.setText(user.getTel());
            try {
                DialogManager.getInstance().showLoadingDialog(mContext, "正在登录");
            }catch (Exception e){
                DialogManager.getInstance().dissMissLoadingDialog();
            }
            imLogin(user, REGISTER);
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_FIND_PASSWD) {
            User user =  data.getParcelableExtra("user");
            try {
                DialogManager.getInstance().showLoadingDialog(mContext, "正在登录");
            }catch (Exception e){
                DialogManager.getInstance().dissMissLoadingDialog();
            }
            imLogin(user, FINDPASSWORD);
        }
    }
}
