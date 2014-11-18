package com.aizou.peachtravel.module.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by Rjm on 2014/10/9.
 */
public class MyFragment extends PeachBaseFragment implements View.OnClickListener {
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
    @ViewInject(R.id.rl_user_info)
    private RelativeLayout userInfoRl;
    @ViewInject(R.id.account_lyt)
    private RelativeLayout loginRl;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my,null);
        ViewUtils.inject(this, rootView);
        rootView.findViewById(R.id.ll_share_account).setOnClickListener(this);
        rootView.findViewById(R.id.ll_about).setOnClickListener(this);
        rootView.findViewById(R.id.ll_setting).setOnClickListener(this);
        rootView.findViewById(R.id.btn_login).setOnClickListener(this);
        rootView.findViewById(R.id.btn_reg).setOnClickListener(this);
        userInfoRl.setOnClickListener(this);

        TitleHeaderBar thb = (TitleHeaderBar)rootView.findViewById(R.id.ly_header_bar_title_wrap);
        thb.getTitleTextView().setText("我");

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        PeachUser user = AccountManager.getInstance().getLoginAccount(getActivity());
        if(user==null){
            userInfoRl.setVisibility(View.GONE);
            loginRl.setVisibility(View.VISIBLE);
        } else {
            userInfoRl.setVisibility(View.VISIBLE);
            loginRl.setVisibility(View.GONE);
            nickNameTv.setText(user.nickName);
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .cacheOnDisc(true)
                            // 设置下载的图片是否缓存在SD卡中
                    .displayer(
                            new RoundedBitmapDisplayer(LocalDisplay.dp2px(
                                    40))) // 设置成圆角图片
                    .build();
            ImageLoader.getInstance().displayImage(user.avatar, avatarIv,
                    options);
            idTv.setText(user.userId+"");
            signTv.setText(user.signature);
        }

    }

    private void initData() {

    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.rl_user_info:
                Intent accountIntent = new Intent(getActivity(),AccountActvity.class);
                startActivity(accountIntent);
                break;

            case R.id.ll_share_account:
                Intent intent = new Intent(getActivity(),ShareAccountActivity.class);
                startActivity(intent);
                break;

            case R.id.ll_about:
                Intent aboutIntent = new Intent(getActivity(),AboutActivity.class);
                startActivity(aboutIntent);
                break;

            case R.id.ll_setting:
                Intent settingIntent = new Intent(getActivity(),SettingActivity.class);
                startActivity(settingIntent);
                break;

            case R.id.btn_login:
                Intent loginintent = new Intent(getActivity(),LoginActivity.class);
                startActivity(loginintent);
                break;

            case R.id.btn_reg:
                Intent regintent = new Intent(getActivity(),RegActivity.class);
                startActivity(regintent);
                break;
        }
    }
}
