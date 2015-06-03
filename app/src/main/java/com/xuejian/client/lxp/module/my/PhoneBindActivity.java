package com.xuejian.client.lxp.module.my;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.RegexUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CheckValidationBean;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.bean.PeachUser;
import com.xuejian.client.lxp.bean.ValidationBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.db.userDB.User;

/**
 * Created by Rjm on 2014/10/11.
 */
public class PhoneBindActivity extends PeachBaseActivity implements View.OnClickListener {
    @ViewInject(R.id.et_phone)
    private EditText phoneEt;
    @ViewInject(R.id.et_sms)
    private EditText smsEt;
  /*  @ViewInject(R.id.btn_next)
    private Button nextBtn;*/
    @ViewInject(R.id.btn_time_down)
    private Button downTimeBtn;
    private CountDownTimer countDownTimer;
    private int countDown;
    private User user;
    private String sendSuccessPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        ViewUtils.inject(this);
        //nextBtn.setOnClickListener(this);
        downTimeBtn.setOnClickListener(this);
        user = AccountManager.getInstance().getLoginAccount(this);

        TitleHeaderBar titleBar = (TitleHeaderBar)findViewById(R.id.ly_header_bar_title_wrap);
        titleBar.getTitleTextView().setText("绑定手机");
        titleBar.getRightTextView().setText("提交");
        titleBar.getRightTextView().setTextColor(getResources().getColor(R.color.app_theme_color));
        titleBar.findViewById(R.id.ly_title_bar_right).setOnClickListener(this);
        titleBar.enableBackKey(true);
    }

    private void startCountDownTime(){
        downTimeBtn.setEnabled(false);
        countDownTimer= new CountDownTimer(countDown*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                downTimeBtn.setText((millisUntilFinished / 1000) + "s");
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
            case R.id.btn_time_down:
                if(!RegexUtils.isMobileNO(phoneEt.getText().toString().trim())){
                    ToastUtil.getInstance(this).showToast("请正确输入11位手机号");
                    return;
                }
                if(!CommonUtils.isNetWorkConnected(mContext)){
                    ToastUtil.getInstance(this).showToast("无网络，请检查网络连接");
                    return;
                }
                DialogManager.getInstance().showLoadingDialog(PhoneBindActivity.this);

                String uid=null ;
                if(user!=null){
                    uid = user.getUserId()+"";
                }
                UserApi.sendValidation(phoneEt.getText().toString().trim(), UserApi.ValidationCode.BIND_PHONE, uid, new HttpCallBack<String>() {
                    @Override
                    public void doSucess(String result, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        CommonJson<ValidationBean> validationResult = CommonJson.fromJson(result, ValidationBean.class);
                        if (validationResult.code == 0) {
                            countDown = validationResult.result.coolDown;
                            sendSuccessPhone = phoneEt.getText().toString().trim();
                            startCountDownTime();
                        } else {
                            ToastUtil.getInstance(mContext).showToast(validationResult.err.message);
                        }
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        ToastUtil.getInstance(PhoneBindActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                    }
                });

                break;
            case R.id.ly_title_bar_right:
                if(!RegexUtils.isMobileNO(phoneEt.getText().toString().trim())){
                    ToastUtil.getInstance(this).showToast("请正确输入11位手机号");
                    return;
                }
                if(TextUtils.isEmpty(smsEt.getText().toString())){
                    ToastUtil.getInstance(mContext).showToast("请输入验证码");
                }
                if(!CommonUtils.isNetWorkConnected(mContext)){
                    ToastUtil.getInstance(this).showToast("无网络，请检查网络连接");
                    return;
                }
                DialogManager.getInstance().showLoadingDialog(PhoneBindActivity.this);
                UserApi.checkValidation(phoneEt.getText().toString().trim(), smsEt.getText().toString(), UserApi.ValidationCode.BIND_PHONE, user.getUserId() + "", new HttpCallBack<String>() {
                    @Override
                    public void doSucess(String result, String method) {
                        CommonJson<CheckValidationBean> chechResult = CommonJson.fromJson(result, CheckValidationBean.class);
                        if (chechResult.code == 0) {
                            if (TextUtils.isEmpty(user.getTel())) {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                Intent intent = new Intent(mContext, SetPwdActivity.class);
                                intent.putExtra("token", chechResult.result.token);
                                intent.putExtra("phone", phoneEt.getText().toString().trim());
                                startActivity(intent);

                            } else {
                                UserApi.bindPhone(phoneEt.getText().toString().trim(), user.getUserId() + "", null, chechResult.result.token, new HttpCallBack<String>() {
                                    @Override
                                    public void doSucess(String result, String method) {
                                        DialogManager.getInstance().dissMissLoadingDialog();
                                        CommonJson<ModifyResult> bindResult = CommonJson.fromJson(result, ModifyResult.class);
                                        if (bindResult.code == 0) {
                                            user.setTel(sendSuccessPhone);
                                            AccountManager.getInstance().saveLoginAccount(mContext, user);
                                            finish();

                                        } else {
                                            ToastUtil.getInstance(mContext).showToast(bindResult.err.message);
                                        }

                                    }

                                    @Override
                                    public void doFailure(Exception error, String msg, String method) {
                                        DialogManager.getInstance().dissMissLoadingDialog();
                                    }
                                });
                            }
                        } else {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            ToastUtil.getInstance(mContext).showToast(chechResult.err.message);
                        }

                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        if (!isFinishing())
                            ToastUtil.getInstance(PhoneBindActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                    }
                });



                break;
        }
    }
}
