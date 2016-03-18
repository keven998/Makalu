package com.xuejian.client.lxp.module.my;


import android.content.Intent;
import android.os.Bundle;
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
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.db.User;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Rjm on 2014/10/13.
 */
public class SetPwdActivity extends PeachBaseActivity implements View.OnClickListener {
    @Bind(R.id.et_pwd)
    EditText pwdEt;
    @Bind(R.id.btn_ok)
    Button okBtn;
    String mToken;
    String mPhone;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pwd);
        ButterKnife.bind(this);
        okBtn.setOnClickListener(this);
        mToken = getIntent().getStringExtra("token");
        mPhone = getIntent().getStringExtra("phone");
        user = AccountManager.getInstance().getLoginAccount(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                if (TextUtils.isEmpty(pwdEt.getText())) {
                    if (!RegexUtils.isPwdOk(pwdEt.getText().toString().trim())) {
                        ToastUtil.getInstance(this).showToast("请正确输入6-12位密码");
                        return;
                    }
                    return;
                }
                if (!CommonUtils.isNetWorkConnected(mContext)) {
                    ToastUtil.getInstance(this).showToast(getResources().getString(R.string.request_network_failed));
                    return;
                }
                UserApi.bindPhone(mPhone, user.getUserId() + "", pwdEt.getText().toString().trim(), mToken, new HttpCallBack<String>() {
                    @Override
                    public void doSuccess(String result, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        CommonJson<ModifyResult> bindResult = CommonJson.fromJson(result, ModifyResult.class);
                        if (bindResult.code == 0) {
                            user.setTel(mPhone);
                            User user = AccountManager.getInstance().getLoginAccount(SetPwdActivity.this);
                            user.setTel(mPhone);
                            AccountManager.getInstance().saveLoginAccount(mContext, user);
                            Intent intent = new Intent(mContext, AccountActvity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        } else {
                            ToastUtil.getInstance(mContext).showToast(bindResult.err.message);
                        }

                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {

                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method, int code) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        if (code == HttpManager.PERMISSION_ERROR){
                            ToastUtil.getInstance(SetPwdActivity.this).showToast("验证码失效");
                        }
                        else {
                           if (!isFinishing()) ToastUtil.getInstance(SetPwdActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                        }

                    }
                });
                break;
        }
    }
}
