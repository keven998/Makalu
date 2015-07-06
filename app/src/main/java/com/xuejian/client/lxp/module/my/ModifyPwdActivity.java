package com.xuejian.client.lxp.module.my;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.RegexUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.db.User;

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
    @ViewInject(R.id.tv_confirm)
    private TextView tv_confirm;
    @ViewInject(R.id.tv_cancel)
    private TextView tv_cancel;
    @ViewInject(R.id.tv_title_bar_title)
    private TextView tv_title_bar_title;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        ViewUtils.inject(this);
        user = AccountManager.getInstance().getLoginAccount(this);
        tv_confirm.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        tv_title_bar_title.setText("修改密码");
        tv_confirm.setText("保存");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                if (!RegexUtils.isPwdOk(oldPwdEt.getText().toString().trim())) {
                    ToastUtil.getInstance(this).showToast("请输入你的当前密码");
                } else if (!RegexUtils.isPwdOk(newPwdEt.getText().toString().trim())) {
                    ToastUtil.getInstance(this).showToast("请正确输入6-12位新密码");
                } else if (!RegexUtils.isPwdOk(rePwdEt.getText().toString().trim())) {
                    ToastUtil.getInstance(this).showToast("请正确输入6-12位确认密码");
                } else if (!newPwdEt.getText().toString().trim().equals(rePwdEt.getText().toString().trim())) {
                    ToastUtil.getInstance(this).showToast("新密码不一致");
                } else {
                    if (!CommonUtils.isNetWorkConnected(mContext)) {
//                        ToastUtil.getInstance(this).showToast("无网络，请检查网络连接");
                        ToastUtil.getInstance(ModifyPwdActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                        return;
                    }
                    DialogManager.getInstance().showLoadingDialog(ModifyPwdActivity.this);
                    UserApi.modifyPwd(oldPwdEt.getText().toString().trim(), newPwdEt.getText().toString().trim(), user.getUserId() + "", new HttpCallBack<String>() {
                        @Override
                        public void doSuccess(String result, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result, ModifyResult.class);
                            if (modifyResult.code == 0) {
                                ToastUtil.getInstance(mContext).showToast("修改成功");
                                finish();
                            } else {
                                ToastUtil.getInstance(mContext).showToast(modifyResult.err.message);
                            }
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            if (!isFinishing())
                                ToastUtil.getInstance(ModifyPwdActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                        }
                    });
                }
                break;
            case R.id.tv_cancel:
                finish();
                break;
        }
    }
}
