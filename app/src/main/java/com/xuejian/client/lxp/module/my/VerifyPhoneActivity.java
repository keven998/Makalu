package com.xuejian.client.lxp.module.my;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.http.HttpManager;
import com.aizou.core.utils.SharePrefUtil;
import com.lv.im.IMClient;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ValidationBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.MainActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/10/13.
 */
public class VerifyPhoneActivity extends PeachBaseActivity implements View.OnClickListener {
    @InjectView(R.id.et_sms)
    EditText smsEt;
    @InjectView(R.id.btn_time_down)
    Button downTimeBtn;
    private CountDownTimer countDownTimer;
    private String tel, pwd, actionCode;
    private int countDown;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        ButterKnife.inject(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        downTimeBtn.setOnClickListener(this);
        initData();
        startCountDownTime();

        TitleHeaderBar titleBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
        titleBar.getTitleTextView().setText("验证");
        titleBar.enableBackKey(true);

        TextView tips = (TextView) findViewById(R.id.tips);
        tips.setText(String.format("验证码已发至：%s\n网络有延迟，请稍后", tel));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("page_verify_phone");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_verify_phone");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void initData() {
        tel = getIntent().getStringExtra("tel");
        pwd = getIntent().getStringExtra("pwd");
        countDown = getIntent().getIntExtra("countDown", 60);
        actionCode = getIntent().getStringExtra("actionCode");
        user = AccountManager.getInstance().getLoginAccount(this);
        if (user != null) {
            setAccountAbout(true);
        }
    }

    private void startCountDownTime() {
        downTimeBtn.setEnabled(false);
        countDownTimer = new CountDownTimer(countDown * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                downTimeBtn.setText(String.format("%ds后重发", (millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                downTimeBtn.setText("重新获取");
                downTimeBtn.setEnabled(true);
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                if (TextUtils.isEmpty(smsEt.getText().toString())) {
                    ToastUtil.getInstance(mContext).showToast("请输入验证码");
                }
//                else if (!RegexUtils.isVerfyCode(smsEt.getText().toString())){
//                    ToastUtil.getInstance(mContext).showToast("请正确输入验证码");
//                }
                else{
                    if (!CommonUtils.isNetWorkConnected(mContext)) {
                        ToastUtil.getInstance(this).showToast("无网络，请检查网络连接");
                        return;
                    }
                    if (actionCode.equals(UserApi.ValidationCode.REG_CODE)) {
                        try {
                            DialogManager.getInstance().showLoadingDialog(VerifyPhoneActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        UserApi.signUp(tel, pwd, smsEt.getText().toString().trim(), new HttpCallBack<String>() {
                            @Override
                            public void doSuccess(String result, String method) {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                CommonJson<User> userResult = CommonJson.fromJson(result, User.class);
                                if (userResult.code == 0) {
                                    imLogin(userResult.result);
                                    Intent accountIntent = new Intent(VerifyPhoneActivity.this, MainActivity.class);
                                   // SharePrefUtil.saveBoolean(getApplicationContext(), "firstReg", true);
                                    startActivityWithNoAnim(accountIntent);
                                    ToastUtil.getInstance(VerifyPhoneActivity.this).showToast("注册成功");
                                    Intent intent = getIntent();
                                    intent.putExtra("user", userResult.result);
                                    setResult(RESULT_OK, intent);
                                    finishWithNoAnim();
                                } else {
                                    ToastUtil.getInstance(mContext).showToast(userResult.err.message);
                                }

                            }

                            @Override
                            public void doFailure(Exception error, String msg, String method) {

                            }

                            @Override
                            public void doFailure(Exception error, String msg, String method, int code) {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                if (code == HttpManager.PWD_ERROR) {
                                    ToastUtil.getInstance(VerifyPhoneActivity.this).showToast("验证码错误");
                                } else if (code == HttpManager.RESOURSE_CONFLICT) {
                                    ToastUtil.getInstance(VerifyPhoneActivity.this).showToast("手机号已存在");
                                } else
                                    ToastUtil.getInstance(VerifyPhoneActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                            }
                        });
                    } else if (actionCode.equals(UserApi.ValidationCode.FIND_PWD)) {

                    }

                }
                break;
            case R.id.btn_time_down:
                if (!CommonUtils.isNetWorkConnected(mContext)) {
                    ToastUtil.getInstance(this).showToast("无网络，请检查网络连接");
                    return;
                }
                try {
                    DialogManager.getInstance().showLoadingDialog(VerifyPhoneActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String uid = null;
                if (user != null) {
                    uid = user.getUserId() + "";
                }
                UserApi.sendValidation(tel, actionCode, uid, new HttpCallBack<String>() {
                    @Override
                    public void doSuccess(String result, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        CommonJson<ValidationBean> validationResult = CommonJson.fromJson(result, ValidationBean.class);
                        if (validationResult.code == 0) {
                            countDown = validationResult.result.coolDown;
                            startCountDownTime();
                        } else {
                            ToastUtil.getInstance(mContext).showToast(validationResult.err.message);
                        }

                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method, int code) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        System.out.println(code);
                        if (code == HttpManager.PERMISSION_ERROR) {
                            if (!isFinishing())
                                ToastUtil.getInstance(VerifyPhoneActivity.this).showToast("发送短信过于频繁！");
                        } else if (!isFinishing())
                            ToastUtil.getInstance(VerifyPhoneActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                    }
                });

                break;
        }
    }
    private void imLogin(final User user) {
        //初始化数据库，方便后面操作
        IMClient.getInstance().setCurrentUserId(String.valueOf(user.getUserId()));
        UserDBManager.getInstance().initDB(user.getUserId() + "");
        UserDBManager.getInstance().saveContact(user);
        int version = SharePrefUtil.getInt(this, "dbversion", 0);
        IMClient.getInstance().initDB(String.valueOf(user.getUserId()),1,version);
        SharePrefUtil.saveInt(this, "dbversion", 1);
        //3、存入内存
        AccountManager.getInstance().setLogin(true);
        AccountManager.getInstance().saveLoginAccount(this, user);
        AccountManager.setCurrentUserId(String.valueOf(user.getUserId()));
//        // 进入主页面
//        runOnUiThread(new Runnable() {
//            public void run() {
//                DialogManager.getInstance().dissMissLoadingDialog();
//                startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                finish();
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//            }
//        });
    }

}
