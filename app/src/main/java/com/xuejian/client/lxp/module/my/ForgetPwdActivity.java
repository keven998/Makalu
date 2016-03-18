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
import com.aizou.core.http.HttpManager;
import com.aizou.core.utils.RegexUtils;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CheckValidationBean;
import com.xuejian.client.lxp.bean.ValidationBean;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Rjm on 2014/10/13.
 */
public class ForgetPwdActivity extends PeachBaseActivity implements View.OnClickListener {
    public final static int REQUEST_CODE_RESET_PWD = 300;
    @Bind(R.id.et_phone)
    EditText phoneEt;
    @Bind(R.id.et_sms)
    EditText smsEt;
    @Bind(R.id.btn_next)
    Button nextBtn;
    @Bind(R.id.btn_time_down)
    Button downTimeBtn;
    private TitleHeaderBar titleBar;
    private CountDownTimer countDownTimer;
    private int countDown;
    private String sendSuccessPhone;
    private String tempPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        ButterKnife.bind(this);
        tempPhoneNum =getIntent().getStringExtra("phone");
        titleBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
        titleBar.getTitleTextView().setText("账户验证");
        titleBar.enableBackKey(true);
        nextBtn.setOnClickListener(this);
        downTimeBtn.setOnClickListener(this);
        if (!TextUtils.isEmpty(tempPhoneNum))phoneEt.setText(tempPhoneNum);
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
                    DialogManager.getInstance().showLoadingDialog(ForgetPwdActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UserApi.sendValidation(phoneEt.getText().toString().trim(), UserApi.ValidationCode.FIND_PWD, null, new HttpCallBack<String>() {
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
                                ToastUtil.getInstance(ForgetPwdActivity.this).showToast("发送短信过于频繁！");
                        } else if (code == HttpManager.PARAMETER_ERROR) {
                            if (!isFinishing())
                                ToastUtil.getInstance(ForgetPwdActivity.this).showToast("号码尚未注册！");
                        }else if (!isFinishing())
                            ToastUtil.getInstance(ForgetPwdActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                    }
                });

                break;

            case R.id.btn_next:
                if (!RegexUtils.isMobileNO(phoneEt.getText().toString().trim())) {
                    ToastUtil.getInstance(this).showToast("请正确输入11位手机号");
                    return;
                }
                if (TextUtils.isEmpty(smsEt.getText().toString())) {
                    ToastUtil.getInstance(mContext).showToast("请输入验证码");
                    return;
                }
                if (!CommonUtils.isNetWorkConnected(mContext)) {
                    ToastUtil.getInstance(this).showToast(getResources().getString(R.string.request_network_failed));
                    return;
                }
                try {
                    DialogManager.getInstance().showLoadingDialog(ForgetPwdActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                UserApi.checkValidation(phoneEt.getText().toString().trim(), smsEt.getText().toString(), UserApi.ValidationCode.FIND_PWD, 0, new HttpCallBack<String>() {
                    @Override
                    public void doSuccess(String result, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        CommonJson<CheckValidationBean> chechResult = CommonJson.fromJson(result, CheckValidationBean.class);
                        if (chechResult.code == 0) {

                            Intent intent = new Intent(mContext, ResetPwdActivity.class);
                            intent.putExtra("token", chechResult.result.token);
                            intent.putExtra("phone", phoneEt.getText().toString().trim());
                            startActivityForResult(intent, REQUEST_CODE_RESET_PWD);
                        } else {
                            ToastUtil.getInstance(mContext).showToast(chechResult.err.message);
                        }
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {

                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method, int code) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        if (code == HttpManager.PWD_ERROR) {
                            ToastUtil.getInstance(ForgetPwdActivity.this).showToast("验证码错误");
                        } else if (!isFinishing())
                            ToastUtil.getInstance(ForgetPwdActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                    }
                });

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_RESET_PWD) {
            setResult(RESULT_OK, data);
            finish();
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
