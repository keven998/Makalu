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
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ValidationBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.db.userDB.User;

/**
 * Created by Rjm on 2014/10/13.
 */
public class VerifyPhoneActivity extends PeachBaseActivity implements View.OnClickListener {
    @ViewInject(R.id.et_sms)
    private EditText smsEt;
    @ViewInject(R.id.btn_time_down)
    private Button downTimeBtn;
    private CountDownTimer countDownTimer;
    private String tel,pwd,actionCode;
    private int countDown;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        ViewUtils.inject(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        downTimeBtn.setOnClickListener(this);
        initData();
        startCountDownTime();

        TitleHeaderBar titleBar = (TitleHeaderBar)findViewById(R.id.ly_header_bar_title_wrap);
        titleBar.getTitleTextView().setText("验证");
        titleBar.enableBackKey(true);

        TextView tips = (TextView)findViewById(R.id.tips);
        tips.setText(String.format("已发送短信验证码至 %s\n网络有延迟,请稍后", tel));
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
    private void initData(){
        tel = getIntent().getStringExtra("tel");
        pwd = getIntent().getStringExtra("pwd");
        countDown = getIntent().getIntExtra("countDown",60);
        actionCode = getIntent().getStringExtra("actionCode");
        user = AccountManager.getInstance().getLoginAccount(this);
        if(user!=null){
            setAccountAbout(true);
        }
    }

    private void startCountDownTime(){
        downTimeBtn.setEnabled(false);
        countDownTimer= new CountDownTimer(countDown*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                downTimeBtn.setText("(" + (millisUntilFinished / 1000) + ")");
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
        switch (v.getId()){
            case R.id.btn_next:
                if(TextUtils.isEmpty(smsEt.getText().toString())){
                    ToastUtil.getInstance(mContext).showToast("请输入验证码");
                }else{
                    if(!CommonUtils.isNetWorkConnected(mContext)){
                        ToastUtil.getInstance(this).showToast("无网络，请检查网络连接");
                        return;
                    }
                    if(actionCode.equals(UserApi.ValidationCode.REG_CODE)){
                        DialogManager.getInstance().showLoadingDialog(VerifyPhoneActivity.this);
                        UserApi.signUp(tel, pwd, smsEt.getText().toString().trim(), new HttpCallBack<String>() {
                            @Override
                            public void doSuccess(String result, String method) {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                CommonJson<User> userResult = CommonJson.fromJson(result, User.class);
                                if (userResult.code == 0) {
//                                    AccountManager.getInstance().saveLoginAccount(mContext, userResult.result);
                                    ToastUtil.getInstance(mContext).showToast("注册成功");
                                    Intent intent = new Intent();
                                    intent.putExtra("user", userResult.result);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    ToastUtil.getInstance(mContext).showToast(userResult.err.message);
                                }

                            }

                            @Override
                            public void doFailure(Exception error, String msg, String method) {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                ToastUtil.getInstance(VerifyPhoneActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                            }
                        });
                    }else if(actionCode.equals(UserApi.ValidationCode.FIND_PWD)){

                    }

                }
                break;
            case R.id.btn_time_down:
                if(!CommonUtils.isNetWorkConnected(mContext)){
                    ToastUtil.getInstance(this).showToast("无网络，请检查网络连接");
                    return;
                }
                DialogManager.getInstance().showLoadingDialog(VerifyPhoneActivity.this);

                String uid=null ;
                if(user!=null){
                    uid = user.getUserId()+"";
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
                        DialogManager.getInstance().dissMissLoadingDialog();
                        if (!isFinishing())
                            ToastUtil.getInstance(VerifyPhoneActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                    }
                });

                break;
        }
    }
}
