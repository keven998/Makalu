package com.aizou.peachtravel.module.my;

import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.aizou.core.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
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
public class ModifySignActivity extends PeachBaseActivity implements View.OnClickListener {
    @ViewInject(R.id.et_sign)
    private EditText signEt;
    @ViewInject(R.id.iv_delete)
    private ImageView deleteIv;
    @ViewInject(R.id.title_bar)
    private TitleHeaderBar titleHeaderBar;
    private PeachUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_sign);
        ViewUtils.inject(this);
        ViewUtils.inject(this);
        deleteIv.setOnClickListener(this);
        titleHeaderBar.getRightTextView().setText("保存");
        titleHeaderBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(signEt.getText())){
                    ToastUtil.getInstance(mContext).showToast("请输入签名");
                    return;
                }
                if(!CommonUtils.isNetWorkConnected(mContext)){
                    ToastUtil.getInstance(mContext).showToast("无网络连接，请检查网络");
                    return;
                }
                DialogManager.getInstance().showProgressDialog(mContext,"请稍后");
                UserApi.editUserSignature(user, signEt.getText().toString().trim(), new HttpCallBack<String>() {


                    @Override
                    public void doSucess(String result, String method) {
                        DialogManager.getInstance().dissMissProgressDialog();
                        CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result, ModifyResult.class);
                        if (modifyResult.code == 0) {
                            user.signature = signEt.getText().toString().trim();
                            AccountManager.getInstance().saveLoginAccount(mContext, user);
                            finish();
                        }
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                        DialogManager.getInstance().dissMissProgressDialog();

                    }

                    @Override
                    public void onStart() {
                    }
                });
                finish();
            }
        });
        initData();
    }

    private void initData() {
        user = AccountManager.getInstance().getLoginAccountFromPref(this);
        signEt.setText(user.signature);
        CharSequence text = signEt.getText();
        //Debug.asserts(text instanceof Spannable);
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_delete:
                signEt.setText("");
                break;
        }
    }
}
