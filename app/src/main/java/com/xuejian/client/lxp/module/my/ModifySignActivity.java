package com.xuejian.client.lxp.module.my;

import android.content.Intent;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.db.User;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Rjm on 2014/10/11.
 */
public class ModifySignActivity extends PeachBaseActivity {
    @Bind(R.id.et_sign)
    EditText signEt;
    @Bind(R.id.title_bar)
    TitleHeaderBar titleHeaderBar;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_sign);
        ButterKnife.bind(this);
        titleHeaderBar.getTitleTextView().setText("设置签名");
        titleHeaderBar.enableBackKey(true);
        titleHeaderBar.getRightTextView().setText("保存");
        titleHeaderBar.findViewById(R.id.ly_title_bar_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(signEt.getText())) {
                    ToastUtil.getInstance(mContext).showToast("你的性感签名呢");
                    return;
                }
                if (!CommonUtils.isNetWorkConnected(mContext)) {
                    ToastUtil.getInstance(mContext).showToast("呃～好像没找到网络");
                    return;
                }
                try {
                    DialogManager.getInstance().showLoadingDialog(mContext, "请稍后");
                }catch (Exception e){
                    DialogManager.getInstance().dissMissLoadingDialog();
                }
                UserApi.editUserSignature(user, signEt.getText().toString().trim(), new HttpCallBack<String>() {


                    @Override
                    public void doSuccess(String result, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result, ModifyResult.class);
                        if (modifyResult.code == 0) {
                            user.setSignature(signEt.getText().toString().trim());
                            User user = AccountManager.getInstance().getLoginAccount(ModifySignActivity.this);
                            user.setSignature(signEt.getText().toString().trim());
                            AccountManager.getInstance().saveLoginAccount(mContext, user);
                            Intent intent = new Intent();
                            intent.putExtra("signature", signEt.getText().toString().trim());
                            setResult(RESULT_OK, intent);
                            ToastUtil.getInstance(mContext).showToast("OK~成功修改");
//                                                                           Intent intent=new Intent();
//                                                                           intent.putExtra("signature",signEt.getText().toString().trim());
//                                                                           setResult(RESULT_OK,intent);
                            finish();
                        } else {
                            if (modifyResult.err != null && !TextUtils.isEmpty(modifyResult.err.message))
                                ToastUtil.getInstance(mContext).showToast(modifyResult.err.message);
                        }
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        if (!isFinishing())
                            ToastUtil.getInstance(ModifySignActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method, int code) {

                    }

                    @Override
                    public void onStart() {
                    }
                });
//                                                               finish();

            }

        });

        initData();
    }

    private void initData() {
        user = AccountManager.getInstance().getLoginAccount(this);
        signEt.setText(user.getSignature());
        CharSequence text = signEt.getText();
        //Debug.asserts(text instanceof Spannable);
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }

}
