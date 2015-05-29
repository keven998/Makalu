package com.xuejian.client.lxp.module.my;

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
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.bean.PeachUser;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;

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
   /* @ViewInject(R.id.btn_ok)
    private Button okBtn;*/
    private PeachUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        ViewUtils.inject(this);
       // okBtn.setOnClickListener(this);
        user = AccountManager.getInstance().getLoginAccount(this);

        TitleHeaderBar titleBar = (TitleHeaderBar)findViewById(R.id.ly_header_bar_title_wrap);
        titleBar.getTitleTextView().setText("密码修改");
        titleBar.getRightTextView().setText("确定");
        titleBar.getRightTextView().setTextColor(getResources().getColor(R.color.app_theme_color));
        titleBar.findViewById(R.id.ly_title_bar_right).setOnClickListener(this);
        titleBar.enableBackKey(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ly_title_bar_right:
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
                    DialogManager.getInstance().showLoadingDialog(ModifyPwdActivity.this);
                    UserApi.modifyPwd(oldPwdEt.getText().toString().trim(), newPwdEt.getText().toString().trim(), user.userId + "", new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result, ModifyResult.class);
                            if (modifyResult.code == 0) {
                                ToastUtil.getInstance(mContext).showToast("OK~修改成功");
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
        }
    }
}