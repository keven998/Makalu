package com.xuejian.client.lxp.module.my;

import android.content.Intent;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
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
import com.xuejian.client.lxp.common.utils.InputCheckUtils;
import com.xuejian.client.lxp.db.User;

/**
 * Created by Rjm on 2014/10/11.
 */
public class ModifyNicknameActivity extends PeachBaseActivity {
    @ViewInject(R.id.et_modify_content)
    private EditText nickEt;
    @ViewInject(R.id.tv_confirm)
    private TextView tv_confirm;
    @ViewInject(R.id.tv_cancel)
    private TextView tv_cancel;
    @ViewInject(R.id.tv_title_bar_title)
    private TextView tv_title_bar_title;
    private boolean isEditMemo;
    private User user;
    private String nickname;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_content_editor);
        ViewUtils.inject(this);
        isEditMemo=getIntent().getBooleanExtra("isEditMemo",false);
        if(isEditMemo){
            userId=getIntent().getStringExtra("userId");
            tv_title_bar_title.setText("修改备注");
            nickname=getIntent().getStringExtra("nickname");
            tv_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    UserApi.editMemo(userId, nickEt.getText().toString(), new HttpCallBack() {
                        @Override
                        public void doSuccess(Object result, String method) {
                            Intent intent = new Intent();
                            intent.putExtra("memo", nickEt.getText().toString());
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
                            Intent intent = new Intent();
                            intent.putExtra("memo", "");
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method, int code) {

                        }
                    });
                }
            });
        }else{
            tv_title_bar_title.setText("姓名设置");
            tv_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!RegexUtils.checkNickName(nickEt.getText().toString().trim())) {
                        ToastUtil.getInstance(ModifyNicknameActivity.this).showToast("请输入1-12位中英文昵称");
                        return;
                    } else if (InputCheckUtils.checkNickNameIsNumber(nickEt.getText().toString().trim())) {
                        ToastUtil.getInstance(ModifyNicknameActivity.this).showToast("昵称不能为纯数字");
                        return;
                    }
                    if (!CommonUtils.isNetWorkConnected(ModifyNicknameActivity.this)) {
                        ToastUtil.getInstance(ModifyNicknameActivity.this).showToast("无网络连接，请检查网络");
                        return;
                    }

                    DialogManager.getInstance().showLoadingDialog(ModifyNicknameActivity.this, "请稍后");
                    UserApi.editUserNickName(user, nickEt.getText().toString().trim(), new HttpCallBack<String>() {
                        @Override
                        public void doSuccess(String result, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result, ModifyResult.class);
                            if (modifyResult.code == 0) {
                                user.setNickName(nickEt.getText().toString().trim());
                                AccountManager.getInstance().saveLoginAccount(mContext, user);
                                Intent intent = new Intent();
                                intent.putExtra("nickname", nickEt.getText().toString().trim());
                                setResult(RESULT_OK, intent);
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
                                ToastUtil.getInstance(ModifyNicknameActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method, int code) {

                        }

                        @Override
                        public void onStart() {

                        }
                    });
                }
            });
        }
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
    }

    private void initData() {
        if (isEditMemo) {
            nickEt.setText(nickname);
        } else {
            user = AccountManager.getInstance().getLoginAccount(this);
            nickEt.setText(user.getNickName());
            CharSequence text = nickEt.getText();
            if (text instanceof Spannable) {
                Spannable spanText = (Spannable) text;
                Selection.setSelection(spanText, text.length());
            }
        }
    }

}
