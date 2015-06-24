package com.xuejian.client.lxp.module.my;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.RegexUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.db.userDB.User;

/**
 * Created by Rjm on 2014/10/13.
 */
public class ResetPwdActivity extends PeachBaseActivity implements View.OnClickListener {
    @ViewInject(R.id.et_new_password)
    private EditText newPwdEt;

    String mToken;
    String mPhone;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        ViewUtils.inject(this);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        mToken = getIntent().getStringExtra("token");
        mPhone = getIntent().getStringExtra("phone");
        TitleHeaderBar titleBar = (TitleHeaderBar)findViewById(R.id.ly_header_bar_title_wrap);
        titleBar.getTitleTextView().setText("重置密码");
        titleBar.enableBackKey(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                if(!RegexUtils.isPwdOk(newPwdEt.getText().toString().trim())) {
                    ToastUtil.getInstance(this).showToast("请正确输入6-16位新密码");
                } else {
                    DialogManager.getInstance().showLoadingDialog(this);
                    UserApi.resetPwd(mPhone, newPwdEt.getText().toString().trim(), mToken, new HttpCallBack<String>() {
                        @Override
                        public void doSuccess(String result, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            CommonJson<User> resetResult = CommonJson.fromJson(result, User.class);
                            if (resetResult.code == 0) {
//                                AccountManager.getInstance().saveLoginAccount(mContext, user);
                                ToastUtil.getInstance(mContext).showToast("设置成功");
                                Intent intent = new Intent();
                                intent.putExtra("user", resetResult.result);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                ToastUtil.getInstance(mContext).showToast(resetResult.err.message);
                            }
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            if (!isFinishing())
                                ToastUtil.getInstance(ResetPwdActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                        }
                    });
                }
                break;
        }
    }
}
