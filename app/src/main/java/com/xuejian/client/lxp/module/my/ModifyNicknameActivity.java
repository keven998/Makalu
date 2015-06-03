package com.xuejian.client.lxp.module.my;

import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.RegexUtils;
import com.easemob.chat.EMChatManager;
import com.easemob.util.EMLog;
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
import com.xuejian.client.lxp.common.utils.InputCheckUtils;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.db.userDB.User;

/**
 * Created by Rjm on 2014/10/11.
 */
public class ModifyNicknameActivity extends PeachBaseActivity {
    @ViewInject(R.id.et_nickname)
    private EditText nickEt;

    @ViewInject(R.id.title_bar)
    private TitleHeaderBar titleHeaderBar;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_nickname);
        ViewUtils.inject(this);

        titleHeaderBar.getTitleTextView().setText("修改昵称");
        titleHeaderBar.getRightTextView().setText("保存");
        titleHeaderBar.enableBackKey(true);

        findViewById(R.id.ly_title_bar_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!RegexUtils.checkNickName(nickEt.getText().toString().trim())) {
                    ToastUtil.getInstance(mContext).showToast("请输入1-12位中英文昵称");
                    return;
                } else if (InputCheckUtils.checkNickNameIsNumber(nickEt.getText().toString().trim())) {
                    ToastUtil.getInstance(mContext).showToast("昵称不能为纯数字");
                    return;
                }
                if (!CommonUtils.isNetWorkConnected(mContext)) {
                    ToastUtil.getInstance(mContext).showToast("无网络连接，请检查网络");
                    return;
                }

                DialogManager.getInstance().showLoadingDialog(mContext, "请稍后");
                UserApi.editUserNickName(user, nickEt.getText().toString().trim(), new HttpCallBack<String>() {
                    @Override
                    public void doSucess(String result, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result, ModifyResult.class);
                        if (modifyResult.code == 0) {
                            user.setNickName(nickEt.getText().toString().trim());
                            AccountManager.getInstance().saveLoginAccount(mContext, user);
                            boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(user.getNickName());
                            if (!updatenick) {
                                EMLog.e("ModifyNicknameActivity", "update current user nick fail");
                            }
                            ToastUtil.getInstance(mContext).showToast("OK~成功修改");
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
                    public void onStart() {

                    }
                });
            }
        });

        initData();

    }

    private void initData() {
        user = AccountManager.getInstance().getLoginAccount(this);
        nickEt.setText(user.getNickName());
        CharSequence text = nickEt.getText();
        //Debug.asserts(text instanceof Spannable);
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }

}
