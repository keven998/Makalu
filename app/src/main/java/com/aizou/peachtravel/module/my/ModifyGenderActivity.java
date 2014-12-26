package com.aizou.peachtravel.module.my;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

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
public class ModifyGenderActivity extends PeachBaseActivity {
    @ViewInject(R.id.rg_gender)
    private RadioGroup genderRg;
    private PeachUser user;
    private String gender;

    @ViewInject(R.id.title_bar)
    private TitleHeaderBar titleHeaderBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_gender);
        ViewUtils.inject(this);
        genderRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_men:
                        gender = PeachUser.M;
                        break;
                    case R.id.rb_women:
                        gender = PeachUser.F;
                        break;
                }
            }
        });
        titleHeaderBar.getRightTextView().setText("保存");
        titleHeaderBar.enableBackKey(true);
        titleHeaderBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CommonUtils.isNetWorkConnected(mContext)){
                    ToastUtil.getInstance(mContext).showToast("无网络连接，请检查网络");
                    return;
                }
                DialogManager.getInstance().showLoadingDialog(mContext, "请稍后");
                UserApi.editUserGender(user, gender, new HttpCallBack<String>() {


                    @Override
                    public void doSucess(String result, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result,ModifyResult.class);
                        if(modifyResult.code==0){
                            user.gender = gender;
                            AccountManager.getInstance().saveLoginAccount(mContext,user);
                        }
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        ToastUtil.getInstance(ModifyGenderActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                    }

                    @Override
                    public void onStart() {
                    }
                });
            }
        });
        initData();

    }

    private void initData(){
        user = AccountManager.getInstance().getLoginAccount(this);
        gender = user.gender;
        if(gender.equals(PeachUser.M)){
            genderRg.check(R.id.rb_men);
        }else{
            genderRg.check(R.id.rb_women);
        }


    }
}
