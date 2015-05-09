package com.aizou.peachtravel.module.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.RegexUtils;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.ValidationBean;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Rjm on 2014/10/13.
 */
public class RegActivity extends PeachBaseActivity implements View.OnClickListener {
    public static final int REQUEST_CODE_CHECH_VALICATION = 201;

    @ViewInject(R.id.et_phone)
    private EditText phoneEt;
    @ViewInject(R.id.et_password)
    private EditText pwdEt;

    @ViewInject(R.id.btn_reg)
    private Button regBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        ViewUtils.inject(this);
        regBtn.setOnClickListener(this);

        TitleHeaderBar titleBar = (TitleHeaderBar)findViewById(R.id.ly_header_bar_title_wrap);
        titleBar.getTitleTextView().setText("注册");
        //titleBar.enableBackKey(true);
        titleBar.findViewById(R.id.ly_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,R.anim.push_bottom_out);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0,R.anim.push_bottom_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("page_register");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_register");
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_reg:
                if(!RegexUtils.isMobileNO(phoneEt.getText().toString().trim())){
                    ToastUtil.getInstance(this).showToast("请正确输入11位手机号");
                    return;
                }
                if(!RegexUtils.isPwdOk(pwdEt.getText().toString().trim())){
                    ToastUtil.getInstance(this).showToast("请正确输入6-12位密码");
                    return;
                }
                if(!CommonUtils.isNetWorkConnected(mContext)){
                    ToastUtil.getInstance(this).showToast("无网络，请检查网络连接");
                    return;
                }
                DialogManager.getInstance().showLoadingDialog(RegActivity.this);
                UserApi.sendValidation(phoneEt.getText().toString().trim(), UserApi.ValidationCode.REG_CODE,null, new HttpCallBack<String>() {
                    @Override
                    public void doSucess(String result, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        CommonJson<ValidationBean> validationResult = CommonJson.fromJson(result, ValidationBean.class);
                        if (validationResult.code == 0) {
                            Intent intent = new Intent(mContext, VerifyPhoneActivity.class);
                            intent.putExtra("tel", phoneEt.getText().toString().trim());
                            intent.putExtra("pwd", pwdEt.getText().toString().trim());
                            intent.putExtra("countDown", validationResult.result.coolDown);
                            intent.putExtra("actionCode", UserApi.ValidationCode.REG_CODE);
                            startActivityForResult(intent, REQUEST_CODE_CHECH_VALICATION);
                        } else {
                            ToastUtil.getInstance(mContext).showToast(validationResult.err.message);
                        }

                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        if (!isFinishing())
                        ToastUtil.getInstance(RegActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                    }
                });

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHECH_VALICATION){
            setResult(RESULT_OK, data);
            finish();
        }
    }

}
