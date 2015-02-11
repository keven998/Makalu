package com.aizou.peachtravel.module.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.H5Url;
import com.aizou.peachtravel.common.utils.ShareUtils;
import com.aizou.peachtravel.module.PeachWebViewActivity;
import com.aizou.peachtravel.module.toolbox.FavListActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by Rjm on 2014/10/9.
 */
public class MyFragment extends PeachBaseFragment implements View.OnClickListener {
    public final static int CODE_FAVORITE = 102;

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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my,null);
        ViewUtils.inject(this, rootView);
        rootView.findViewById(R.id.ll_share_account).setOnClickListener(this);
        rootView.findViewById(R.id.ll_about).setOnClickListener(this);
        rootView.findViewById(R.id.ll_setting).setOnClickListener(this);
        rootView.findViewById(R.id.btn_login).setOnClickListener(this);
        rootView.findViewById(R.id.btn_reg).setOnClickListener(this);
        rootView.findViewById(R.id.ll_message_center).setOnClickListener(this);
        rootView.findViewById(R.id.ll_push_friends).setOnClickListener(this);
        rootView.findViewById(R.id.rl_user_info).setOnClickListener(this);

        return rootView;
    }

    public void refresh(){
        PeachUser user = AccountManager.getInstance().getLoginAccount(getActivity());
        if(user == null) {
            idTv.setVisibility(View.GONE);
            signTv.setVisibility(View.GONE);
            genderIv.setVisibility(View.GONE);
            View view = getView();
            view.findViewById(R.id.indicator).setVisibility(View.GONE);
            view.findViewById(R.id.btn_login).setVisibility(View.VISIBLE);
            view.findViewById(R.id.btn_reg).setVisibility(View.VISIBLE);
            avatarIv.setImageResource(R.drawable.avatar_placeholder);
            nickNameTv.setText("未登录");
        } else {
            idTv.setVisibility(View.VISIBLE);
            signTv.setVisibility(View.VISIBLE);
            genderIv.setVisibility(View.VISIBLE);
            View view = getView();
            view.findViewById(R.id.indicator).setVisibility(View.VISIBLE);
            view.findViewById(R.id.btn_login).setVisibility(View.GONE);
            view.findViewById(R.id.btn_reg).setVisibility(View.GONE);
            if (user.gender.equalsIgnoreCase("M")) {
                genderIv.setImageResource(R.drawable.ic_gender_man);
            } else if (user.gender.equalsIgnoreCase("F")) {
                genderIv.setImageResource(R.drawable.ic_gender_lady);
            } else {
                genderIv.setImageDrawable(null);
            }
            nickNameTv.setText(user.nickName);
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .showImageForEmptyUri(R.drawable.avatar_placeholder_round)
                    .showImageOnFail(R.drawable.avatar_placeholder_round)
                    .cacheOnDisc(true)
                            // 设置下载的图片是否缓存在SD卡中
                    .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(62))) // 设置成圆角图片
                    .build();
            ImageLoader.getInstance().displayImage(user.avatar, avatarIv, options);
            idTv.setText("ID: " + user.userId);
            if (TextUtils.isEmpty(user.signature)) {
                signTv.setText("no签名");
            } else {
                signTv.setText(user.signature);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.rl_user_info:
                PeachUser user = AccountManager.getInstance().getLoginAccount(getActivity());
                if (user == null) return;
                Intent accountIntent = new Intent(getActivity(), AccountActvity.class);
                startActivity(accountIntent);
                break;

            case R.id.ll_share_account:
                Intent intent = new Intent(getActivity(), ShareAccountActivity.class);
                startActivity(intent);
                break;

            case R.id.ll_about:
                Intent aboutIntent = new Intent(getActivity(), PeachWebViewActivity.class);
                aboutIntent.putExtra("url", String.format("%s?version=%s", H5Url.ABOUT, getResources().getString(R.string.app_version)));
                aboutIntent.putExtra("title", "关于桃子旅行");
                startActivity(aboutIntent);
                break;

            case R.id.ll_setting:
                Intent settingIntent = new Intent(getActivity(), SettingActivity.class);
                startActivity(settingIntent);
                break;

            case R.id.btn_login:
                Intent loginintent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginintent);
                break;

            case R.id.btn_reg:
                Intent regintent = new Intent(getActivity(), RegActivity.class);
                startActivityForResult(regintent, LoginActivity.REQUEST_CODE_REG);
                break;

            case R.id.ll_message_center:
//                Intent msgIntent = new Intent(getActivity(), MessageContents.class);
//                startActivity(msgIntent);
                PeachUser user1 = AccountManager.getInstance().getLoginAccount(getActivity());
                if (user1 != null && !TextUtils.isEmpty(user1.easemobUser)) {
                    Intent fIntent = new Intent(getActivity(), FavListActivity.class);
                    startActivity(fIntent);
                } else {
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(loginIntent, CODE_FAVORITE);
                    ToastUtil.getInstance(getActivity()).showToast("请先登录");
                }
                break;

            case R.id.ll_push_friends:
                ShareUtils.shareAppToWx(getActivity(),null);

                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LoginActivity.REQUEST_CODE_REG) {

            } else if (requestCode == CODE_FAVORITE) {
                startActivity(new Intent(getActivity(), FavListActivity.class));
            }
        }
    }
}
