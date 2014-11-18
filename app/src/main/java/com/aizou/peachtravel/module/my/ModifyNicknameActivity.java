package com.aizou.peachtravel.module.my;

import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.aizou.peachtravel.common.utils.InputCheckUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by Rjm on 2014/10/11.
 */
public class ModifyNicknameActivity extends PeachBaseActivity {
    @ViewInject(R.id.et_nickname)
    private EditText nickEt;

    @ViewInject(R.id.title_bar)
    private TitleHeaderBar titleHeaderBar;

    private PeachUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_nickname);
        ViewUtils.inject(this);

        TitleHeaderBar titleBar = (TitleHeaderBar)findViewById(R.id.title_bar);
        titleBar.getTitleTextView().setText("修改昵称");

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!RegexUtils.checkNickName(nickEt.getText().toString().trim())){
                    ToastUtil.getInstance(mContext).showToast("请输入正确格式昵称");
                    return;
                }else if(InputCheckUtils.checkNickNameIsNumber(nickEt.getText().toString().trim())){
                    ToastUtil.getInstance(mContext).showToast("昵称不能为连续超过6位数字");
                    return;
                }
                if(!CommonUtils.isNetWorkConnected(mContext)){
                    ToastUtil.getInstance(mContext).showToast("无网络连接，请检查网络");
                    return;
                }
                DialogManager.getInstance().showProgressDialog(mContext,"请稍后");
                UserApi.editUserNickName(user, nickEt.getText().toString().trim(), new HttpCallBack<String>() {


                    @Override
                    public void doSucess(String result, String method) {
                        DialogManager.getInstance().dissMissProgressDialog();
                        CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result, ModifyResult.class);
                        if (modifyResult.code == 0) {
                            user.nickName = nickEt.getText().toString().trim();
                            AccountManager.getInstance().saveLoginAccount(mContext, user);
                            finish();
                        }else{
                            ToastUtil.getInstance(mContext).showToast(modifyResult.err.message);
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
            }
        });

        initData();

    }

    private void initData() {
        user = AccountManager.getInstance().getLoginAccount(this);
        nickEt.setText(user.nickName);
        CharSequence text = nickEt.getText();
        //Debug.asserts(text instanceof Spannable);
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }

}
