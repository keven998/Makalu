package com.aizou.peachtravel.module.my;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aizou.core.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.RegexUtils;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.CheckValidationBean;
import com.aizou.peachtravel.bean.ModifyResult;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.bean.ValidationBean;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by Rjm on 2014/10/11.
 */
public class PhoneBindActivity extends PeachBaseActivity implements View.OnClickListener {
    @ViewInject(R.id.et_phone)
    private EditText phoneEt;
    @ViewInject(R.id.et_sms)
    private EditText smsEt;
    @ViewInject(R.id.btn_next)
    private Button nextBtn;
    @ViewInject(R.id.btn_time_down)
    private Button downTimeBtn;
    private CountDownTimer countDownTimer;
    private int countDown;
    private PeachUser user;
    private String sendSuccessPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        ViewUtils.inject(this);
        nextBtn.setOnClickListener(this);
        downTimeBtn.setOnClickListener(this);
        user = AccountManager.getInstance().getLoginAccount(this);

        TitleHeaderBar titleBar = (TitleHeaderBar)findViewById(R.id.ly_header_bar_title_wrap);
        titleBar.getTitleTextView().setText("绑定手机");
        titleBar.enableBackKey(true);
    }

    private void startCountDownTime(){
        downTimeBtn.setClickable(false);
        countDownTimer= new CountDownTimer(countDown*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                downTimeBtn.setText((millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                downTimeBtn.setText("重新获取");
                downTimeBtn.setClickable(true);
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
                DialogManager.getInstance().showProgressDialog(PhoneBindActivity.this);

                String uid=null ;
                if(user!=null){
                    uid = user.userId+"";
                }
                UserApi.sendValidation(phoneEt.getText().toString().trim(), UserApi.ValidationCode.BIND_PHONE,uid, new HttpCallBack<String>() {
                    @Override
                    public void doSucess(String result, String method) {
                        DialogManager.getInstance().dissMissProgressDialog();
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
                        DialogManager.getInstance().dissMissProgressDialog();
                    }
                });

                break;
            case R.id.btn_next:
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
                DialogManager.getInstance().showProgressDialog(PhoneBindActivity.this);
                UserApi.checkValidation(phoneEt.getText().toString().trim(),smsEt.getText().toString(),UserApi.ValidationCode.BIND_PHONE,user.userId+"",new HttpCallBack<String>() {
                    @Override
                    public void doSucess(String result, String method) {
                        CommonJson<CheckValidationBean> chechResult = CommonJson.fromJson(result, CheckValidationBean.class);
                        if(chechResult.code==0){
                            if(TextUtils.isEmpty(user.tel)){
                                DialogManager.getInstance().dissMissProgressDialog();
                                Intent intent = new Intent(mContext,SetPwdActivity.class);
                                intent.putExtra("token",chechResult.result.token);
                                intent.putExtra("phone",phoneEt.getText().toString().trim());
                                startActivity(intent);

                            }else{
                                UserApi.bindPhone(phoneEt.getText().toString().trim(), user.userId+"", null, chechResult.result.token, new HttpCallBack<String>() {
                                    @Override
                                    public void doSucess(String result, String method) {
                                        DialogManager.getInstance().dissMissProgressDialog();
                                        CommonJson<ModifyResult> bindResult = CommonJson.fromJson(result, ModifyResult.class);
                                        if (bindResult.code == 0) {
                                            user.tel = sendSuccessPhone;
                                            AccountManager.getInstance().saveLoginAccount(mContext,user);
                                            finish();

                                        } else {
                                            ToastUtil.getInstance(mContext).showToast(bindResult.err.message);
                                        }

                                    }

                                    @Override
                                    public void doFailure(Exception error, String msg, String method) {
                                        DialogManager.getInstance().dissMissProgressDialog();
                                    }
                                });
                            }
                        } else {
                            DialogManager.getInstance().dissMissProgressDialog();
                            ToastUtil.getInstance(mContext).showToast(chechResult.err.message);
                        }

                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                        DialogManager.getInstance().dissMissProgressDialog();

                    }
                });



                break;
        }
    }
}
