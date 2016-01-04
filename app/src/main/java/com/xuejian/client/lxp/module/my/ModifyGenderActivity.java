package com.xuejian.client.lxp.module.my;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;

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
public class ModifyGenderActivity extends PeachBaseActivity {
    @Bind(R.id.rg_gender)
    RadioGroup genderRg;
    private User user;
    private String gender;

    @Bind(R.id.title_bar)
    TitleHeaderBar titleHeaderBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_gender);
        ButterKnife.bind(this);
        genderRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_men:
                        gender = User.M;
                        break;
                    case R.id.rb_women:
                        gender = User.F;
                        break;
                }
            }
        });
        titleHeaderBar.getRightTextView().setText("保存");
        titleHeaderBar.enableBackKey(true);
        titleHeaderBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonUtils.isNetWorkConnected(mContext)) {
                    ToastUtil.getInstance(mContext).showToast("无网络连接，请检查网络");
                    return;
                }
                try {
                   DialogManager.getInstance().showLoadingDialog(mContext, "请稍后");
                }catch (Exception e){
                    DialogManager.getInstance().dissMissLoadingDialog();
                }
                UserApi.editUserGender(user, gender, new HttpCallBack<String>() {


                    @Override
                    public void doSuccess(String result, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result, ModifyResult.class);
                        if (modifyResult.code == 0) {
                            user.setGender(gender);
                            AccountManager.getInstance().saveLoginAccount(mContext, user);
                        } else {
                            if (modifyResult.err != null && !TextUtils.isEmpty(modifyResult.err.message))
                                ToastUtil.getInstance(mContext).showToast(modifyResult.err.message);
                        }
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        if (!isFinishing())
                            ToastUtil.getInstance(ModifyGenderActivity.this).showToast(getResources().getString(R.string.request_network_failed));
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
        initData();

    }

    private void initData() {
        user = AccountManager.getInstance().getLoginAccount(this);
        gender = user.getGenderDesc();
        if (gender.equals(User.M)) {
            genderRg.check(R.id.rb_men);
        } else {
            genderRg.check(R.id.rb_women);
        }


    }
}
