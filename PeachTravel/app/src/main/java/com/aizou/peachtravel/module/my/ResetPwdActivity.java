package com.aizou.peachtravel.module.my;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.aizou.core.dialog.DialogManager;
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
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by Rjm on 2014/10/13.
 */
public class ResetPwdActivity extends PeachBaseActivity implements View.OnClickListener {
    @ViewInject(R.id.et_new_password)
    private EditText newPwdEt;
    @ViewInject(R.id.et_re_password)
    private EditText rePwdEt;

    String mToken;
    String mPhone;
    PeachUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        ViewUtils.inject(this);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        mToken = getIntent().getStringExtra("token");
        mPhone = getIntent().getStringExtra("phone");
        user = AccountManager.getInstance().getLoginAccount(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                if(!RegexUtils.isPwdOk(newPwdEt.getText().toString().trim())){
                    ToastUtil.getInstance(this).showToast("请正确输入6-12位新密码");
                }else if(!RegexUtils.isPwdOk(rePwdEt.getText().toString().trim())){
                    ToastUtil.getInstance(this).showToast("请正确输入6-12位确认密码");
                }else if(!newPwdEt.getText().toString().trim().equals(rePwdEt.getText().toString().trim())){
                    ToastUtil.getInstance(this).showToast("确认密码不一致");
                }else{
                    DialogManager.getInstance().showProgressDialog(this);
                    UserApi.resetPwd(mPhone,newPwdEt.getText().toString().trim(),mToken,new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {
                            DialogManager.getInstance().dissMissProgressDialog();
                            CommonJson<ModifyResult> resetResult = CommonJson.fromJson(result,ModifyResult.class);
                            if(resetResult.code==0){
                                AccountManager.getInstance().saveLoginAccount(mContext,user);
                                Intent intent = new Intent(mContext,LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }else {
                                ToastUtil.getInstance(mContext).showToast(resetResult.err.message);
                            }
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
                            DialogManager.getInstance().dissMissProgressDialog();
                        }
                    });
                }
                break;
        }
    }
}
