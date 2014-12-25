package com.aizou.peachtravel.module.my;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by Rjm on 2014/10/11.
 */
public class ModifyPwdActivity extends PeachBaseActivity implements View.OnClickListener {
    @ViewInject(R.id.et_old_pwd)
    private EditText oldPwdEt;
    @ViewInject(R.id.et_new_password)
    private EditText newPwdEt;
    @ViewInject(R.id.et_re_password)
    private EditText rePwdEt;
    @ViewInject(R.id.btn_ok)
    private Button okBtn;
    private PeachUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        ViewUtils.inject(this);
        okBtn.setOnClickListener(this);
        user = AccountManager.getInstance().getLoginAccount(this);

        TitleHeaderBar titleBar = (TitleHeaderBar)findViewById(R.id.ly_header_bar_title_wrap);
        titleBar.getTitleTextView().setText("修改密码");
        titleBar.enableBackKey(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                if(!RegexUtils.isPwdOk(oldPwdEt.getText().toString().trim())){
                    ToastUtil.getInstance(this).showToast("请输入你的当前密码");
                }else if(!RegexUtils.isPwdOk(newPwdEt.getText().toString().trim())){
                    ToastUtil.getInstance(this).showToast("请正确输入6-12位新密码");
                }else if(!RegexUtils.isPwdOk(rePwdEt.getText().toString().trim())){
                    ToastUtil.getInstance(this).showToast("请正确输入6-12位确认密码");
                }else if(!newPwdEt.getText().toString().trim().equals(rePwdEt.getText().toString().trim())){
                    ToastUtil.getInstance(this).showToast("新密码不一致");
                }else{
                    if(!CommonUtils.isNetWorkConnected(mContext)){
//                        ToastUtil.getInstance(this).showToast("无网络，请检查网络连接");
                        ToastUtil.getInstance(ModifyPwdActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                        return;
                    }
                    DialogManager.getInstance().showProgressDialog(ModifyPwdActivity.this);
                    UserApi.modifyPwd(oldPwdEt.getText().toString().trim(),newPwdEt.getText().toString().trim(),user.userId+"",new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {
                            DialogManager.getInstance().dissMissProgressDialog();
                            CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result,ModifyResult.class);
                            if (modifyResult.code == 0) {
                                ToastUtil.getInstance(mContext).showToast("OK~修改成功");
                                finish();
                            } else {
                                ToastUtil.getInstance(mContext).showToast(modifyResult.err.message);
                            }
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
                            DialogManager.getInstance().dissMissProgressDialog();
                            ToastUtil.getInstance(ModifyPwdActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                        }
                    });
                }
            break;
        }
    }
}
