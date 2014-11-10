package com.aizou.peachtravel.module.travel.im;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.BaseChatActivity;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Rjm on 2014/10/29.
 */
public class ContactDetailActivity extends BaseChatActivity {
    @ViewInject(R.id.iv_avatar)
    private ImageView avatarIv;
    @ViewInject(R.id.iv_gender)
    private ImageView genderIv;

    @ViewInject(R.id.tv_nickname)
    private TextView nickNameTv;
    @ViewInject(R.id.tv_id)
    private TextView idTv;
    @ViewInject(R.id.tv_sign)
    private TextView signTv;
    private long userId;
    private IMUser imUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        ViewUtils.inject(this);
        userId = getIntent().getLongExtra("userId", 0);
        imUser = IMUserRepository.getContactByUserId(mContext, userId);
        if (imUser != null) {
            bindView();
        }

        UserApi.getUserInfo(userId + "", new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<PeachUser> userResult = CommonJson.fromJson(result, PeachUser.class);
                if (userResult.code == 0) {
                    PeachUser user = userResult.result;
                    imUser = AccountManager.getInstance().getContactList(mContext).get(user.easemobUser);
                    if(imUser!=null){
                        imUser.setNick(user.nickName);
                        imUser.setAvatar(user.avatar);
                        imUser.setSignature(user.signature);
                        imUser.setMemo(user.memo);
                        imUser.setGender(user.gender);
                        IMUtils.setUserHead(imUser);
                        AccountManager.getInstance().getContactList(mContext).put(imUser.getUsername(), imUser);
                        IMUserRepository.saveContact(mContext, imUser);
                        bindView();
                    }


                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }
        });


    }

    private void bindView() {
        ImageLoader.getInstance().displayImage(imUser.getAvatar(), avatarIv, UILUtils.getDefaultOption());
        nickNameTv.setText(imUser.getNick());
        idTv.setText(imUser.getUserId() + "");
        signTv.setText(imUser.getSignature());
    }

    public void startChat(View view) {
        startActivity(new Intent(mContext, ChatActivity.class).putExtra("userId", imUser.getUsername()));

    }
}
