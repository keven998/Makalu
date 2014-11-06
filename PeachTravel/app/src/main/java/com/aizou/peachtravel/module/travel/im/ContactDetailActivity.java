package com.aizou.peachtravel.module.travel.im;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.BaseChatActivity;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.db.IMUser;
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

    private IMUser imUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        ViewUtils.inject(this);
        imUser = (IMUser) getIntent().getSerializableExtra("user");
        ImageLoader.getInstance().displayImage(imUser.getAvatar(),avatarIv, UILUtils.getDefaultOption());
        nickNameTv.setText(imUser.getNick());
        idTv.setText(imUser.getUserId()+"");
        signTv.setText(imUser.getSignature());


    }
}
