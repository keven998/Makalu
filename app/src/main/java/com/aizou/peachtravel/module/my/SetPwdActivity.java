package com.aizou.peachtravel.module.my;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.RegexUtils;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.ModifyResult;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by Rjm on 2014/10/13.
 */
public class SetPwdActivity extends PeachBaseActivity implements View.OnClickListener {
    @ViewInject(R.id.et_pwd)
    private EditText pwdEt;
    @ViewInject(R.id.btn_ok)
    private Button okBtn;
    String mToken;
    String mPhone;
    PeachUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pwd);
        ViewUtils.inject(this);
        okBtn.setOnClickListener(this);
        mToken = getIntent().getStringExtra("token");
        mPhone = getIntent().getStringExtra("phone");
        user = AccountManager.getInstance().getLoginAccount(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                if(TextUtils.isEmpty(pwdEt.getText())){
                    if(!RegexUtils.isPwdOk(pwdEt.getText().toString().trim())){
                        ToastUtil.getInstance(this).showToast("请正确输入6-12位密码");
                        return;
                    }
                    return;
                }
                if(!CommonUtils.isNetWorkConnected(mContext)){
                    ToastUtil.getInstance(this).showToast("无网络，请检查网络连接");
                    return;
                }
                UserApi.bindPhone(mPhone, user.userId+"", pwdEt.getText().toString().trim(), mToken, new HttpCallBack<String>() {
                    @Override
                    public void doSucess(String result, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        CommonJson<ModifyResult> bindResult = CommonJson.fromJson(result, ModifyResult.class);
                        if (bindResult.code == 0) {
                            user.tel = mPhone;
                            AccountManager.getInstance().saveLoginAccount(mContext,user);
                            Intent intent = new Intent(mContext,AccountActvity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        } else {
                            ToastUtil.getInstance(mContext).showToast(bindResult.err.message);
                        }

                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        ToastUtil.getInstance(SetPwdActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                    }
                });
                break;
        }
    }
}
