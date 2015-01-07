package com.aizou.peachtravel.module.my;

import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.aizou.peachtravel.common.dialog.DialogManager;
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
public class ModifySignActivity extends PeachBaseActivity {
    @ViewInject(R.id.et_sign)
    private EditText signEt;
    @ViewInject(R.id.title_bar)
    private TitleHeaderBar titleHeaderBar;
    private PeachUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_sign);
        ViewUtils.inject(this);
        titleHeaderBar.getTitleTextView().setText("旅行签名");
        titleHeaderBar.enableBackKey(true);
        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View view) {
                                                               if(TextUtils.isEmpty(signEt.getText())){
                                                                   ToastUtil.getInstance(mContext).showToast("你的性感签名呢");
                                                                   return;
                                                               }
                                                               if(!CommonUtils.isNetWorkConnected(mContext)){
                                                                   ToastUtil.getInstance(mContext).showToast("呃～好像没找到网络");
                                                                   return;
                                                               }
                                                               DialogManager.getInstance().showLoadingDialog(mContext, "请稍后");
                                                               UserApi.editUserSignature(user, signEt.getText().toString().trim(), new HttpCallBack<String>() {


                                                                   @Override
                                                                   public void doSucess(String result, String method) {
                                                                       DialogManager.getInstance().dissMissLoadingDialog();
                                                                       CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result, ModifyResult.class);
                                                                       if (modifyResult.code == 0) {
                                                                           user.signature = signEt.getText().toString().trim();
                                                                           AccountManager.getInstance().saveLoginAccount(mContext, user);
                                                                           ToastUtil.getInstance(mContext).showToast("OK~成功修改");
                                                                           finish();
                                                                       }
                                                                   }

                                                                   @Override
                                                                   public void doFailure(Exception error, String msg, String method) {
                                                                       DialogManager.getInstance().dissMissLoadingDialog();
                                                                       if (!isFinishing())
                                                                       ToastUtil.getInstance(ModifySignActivity.this).showToast(getResources().getString(R.string.request_network_failed));
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
        signEt.setText(user.signature);
        CharSequence text = signEt.getText();
        //Debug.asserts(text instanceof Spannable);
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }

}
