package com.xuejian.client.lxp.module.my;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.RegexUtils;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.SplashActivity;

/**
 * Created by Rjm on 2014/10/13.
 */
public class ResetPwdActivity extends PeachBaseActivity implements View.OnClickListener {
    private EditText newPwdEt;

    private String mToken;
    private String mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_content_editor);
        mToken = getIntent().getStringExtra("token");
        mPhone = getIntent().getStringExtra("phone");
        TextView titleView = (TextView) findViewById(R.id.tv_title_bar_title);
        titleView.setText("设置新密码");
        findViewById(R.id.tv_confirm).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        newPwdEt = (EditText) findViewById(R.id.et_modify_content);
        newPwdEt.setHint("请设置新密码");
        newPwdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                if (!RegexUtils.isPwdOk(newPwdEt.getText().toString().trim())) {
                    ToastUtil.getInstance(this).showToast("请正确输入6-12位新密码");
                } else {
                    DialogManager.getInstance().showLoadingDialog(this);
                    UserApi.resetPwd(mPhone, newPwdEt.getText().toString().trim(), mToken, new HttpCallBack<String>() {
                        @Override
                        public void doSuccess(String result, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            CommonJson<User> resetResult = CommonJson.fromJson(result, User.class);
                            if (resetResult.code == 0) {
//                                AccountManager.getInstance().saveLoginAccount(mContext, user);
                                Intent intent = new Intent();
                                intent.setClass(ResetPwdActivity.this, SplashActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                ToastUtil.getInstance(mContext).showToast(resetResult.err.message);
                            }
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();

                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method, int code) {
                            if (code== 404){
                                if (!isFinishing()) {
                                    ToastUtil.getInstance(ResetPwdActivity.this).showToast("手机号尚未注册");
                                }
                            }
                            else if (!isFinishing()) {
                                ToastUtil.getInstance(ResetPwdActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                            }
                        }
                    });
                }
                break;

            default:
                break;
        }
    }
}
