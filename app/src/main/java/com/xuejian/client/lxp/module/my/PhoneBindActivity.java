package com.xuejian.client.lxp.module.my;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.http.HttpManager;
import com.aizou.core.utils.RegexUtils;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CheckValidationBean;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.bean.ValidationBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.db.User;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Rjm on 2014/10/11.
 */
public class PhoneBindActivity extends PeachBaseActivity implements View.OnClickListener {
    @Bind(R.id.et_phone)
    EditText phoneEt;
    @Bind(R.id.et_sms)
    EditText smsEt;
    @Bind(R.id.tv_confirm)
    TextView tv_confirm;
    @Bind(R.id.tv_cancel)
    TextView tv_cancel;
    @Bind(R.id.tv_title_bar_title)
    TextView tv_title_bar_title;
    @Bind(R.id.btn_time_down)
    TextView downTimeBtn;
    private CountDownTimer countDownTimer;
    private int countDown;
    private User user;
    private String sendSuccessPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        ButterKnife.bind(this);
        downTimeBtn.setOnClickListener(this);
        user = AccountManager.getInstance().getLoginAccount(this);
        tv_confirm.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        tv_title_bar_title.setText("安全设置");
        tv_confirm.setText("提交");

        if (!TextUtils.isEmpty(user.getTel())) {
            TextView tv = (TextView) findViewById(R.id.bind_hint);
            tv.setText(String.format("已绑定手机：%s", user.getTel()));
        }
    }

    private void startCountDownTime() {
        downTimeBtn.setEnabled(false);
        countDownTimer = new CountDownTimer(countDown * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                downTimeBtn.setText((millisUntilFinished / 1000) + "s");
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
            case R.id.btn_time_down:
                if (!RegexUtils.isMobileNO(phoneEt.getText().toString().trim())) {
                    ToastUtil.getInstance(this).showToast("请正确输入11位手机号");
                    return;
                }

                if (!CommonUtils.isNetWorkConnected(mContext)) {
                    ToastUtil.getInstance(this).showToast(getResources().getString(R.string.request_network_failed));
                    return;
                }
                try {
                    DialogManager.getInstance().showLoadingDialog(PhoneBindActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String uid = null;
                if (user != null) {
                    uid = String.valueOf(user.getUserId());
                }
                UserApi.sendValidation(phoneEt.getText().toString().trim(), UserApi.ValidationCode.BIND_PHONE, uid, new HttpCallBack<String>() {
                    @Override
                    public void doSuccess(String result, String method) {
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
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method, int code) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        if (code == HttpManager.PERMISSION_ERROR) {
                            if (!isFinishing())
                                ToastUtil.getInstance(PhoneBindActivity.this).showToast("发送短信过于频繁！");
                        } else if (code==HttpManager.PWD_ERROR){
                            if (!isFinishing())
                                ToastUtil.getInstance(PhoneBindActivity.this).showToast("验证码错误！");
                        }
                        else if (!isFinishing())
                            ToastUtil.getInstance(PhoneBindActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                    }
                });

                break;
            case R.id.tv_confirm:
                if (!RegexUtils.isMobileNO(phoneEt.getText().toString().trim())) {
                    ToastUtil.getInstance(this).showToast("请正确输入11位手机号");
                    return;
                }

                String confirmCode = smsEt.getText().toString();
                if(TextUtils.isEmpty(confirmCode) || confirmCode.trim().length()==0){
                    ToastUtil.getInstance(this).showToast("请输入正确的验证码");
                    return;
                }
                if (!CommonUtils.isNetWorkConnected(mContext)) {
                    ToastUtil.getInstance(this).showToast(getResources().getString(R.string.request_network_failed));
                    return;
                }
                try {
                    DialogManager.getInstance().showLoadingDialog(PhoneBindActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                UserApi.checkValidation(phoneEt.getText().toString().trim(), smsEt.getText().toString(), UserApi.ValidationCode.BIND_PHONE, user.getUserId(), new HttpCallBack<String>() {
                    @Override
                    public void doSuccess(String result, String method) {
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
                                    public void doSuccess(String result, String method) {
                                        DialogManager.getInstance().dissMissLoadingDialog();
                                        CommonJson<ModifyResult> bindResult = CommonJson.fromJson(result, ModifyResult.class);
                                        if (bindResult.code == 0) {
                                            user.setTel(sendSuccessPhone);
                                            User user = AccountManager.getInstance().getLoginAccount(PhoneBindActivity.this);
                                            user.setTel(sendSuccessPhone);
                                            AccountManager.getInstance().saveLoginAccount(mContext, user);
                                            Intent intent = new Intent();
                                            intent.putExtra("bindphone", sendSuccessPhone);
                                            setResult(RESULT_OK, intent);
                                            ToastUtil.getInstance(mContext).showToast("OK~绑定成功");
                                            finish();
                                        } else {
                                            ToastUtil.getInstance(mContext).showToast(bindResult.err.message);
                                        }

                                    }

                                    @Override
                                    public void doFailure(Exception error, String msg, String method) {
                                        DialogManager.getInstance().dissMissLoadingDialog();
                                    }

                                    @Override
                                    public void doFailure(Exception error, String msg, String method, int code) {

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

                    @Override
                    public void doFailure(Exception error, String msg, String method, int code) {

                    }
                });
                break;
            case R.id.tv_cancel:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

}
