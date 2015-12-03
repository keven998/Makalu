package com.xuejian.client.lxp.module.my.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.widget.RoundImageBoarderView;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.MainActivity;
import com.xuejian.client.lxp.module.goods.CommonUserInfoActivity;
import com.xuejian.client.lxp.module.goods.OrderListActivity;
import com.xuejian.client.lxp.module.my.SettingActivity;
import com.xuejian.client.lxp.module.toolbox.StrategyListActivity;
import com.xuejian.client.lxp.module.toolbox.im.ContactActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yibiao.qin on 2015/11/21.
 */
public class MyInfoFragment extends PeachBaseFragment implements View.OnClickListener{
    DisplayImageOptions options;
    @InjectView(R.id.setting_btn)
    ImageView settingBtn;
    @InjectView(R.id.user_avatar)
    RoundImageBoarderView userAvatar;
    @InjectView(R.id.tv_nickname)
    TextView tvNickname;
    @InjectView(R.id.tv_all_order)
    TextView tvAllOrder;
    @InjectView(R.id.tv_need_pay)
    TextView tvNeedPay;
    @InjectView(R.id.tv_process)
    TextView tvProcess;
    @InjectView(R.id.tv_available)
    TextView tvAvailable;
    @InjectView(R.id.tv_drawback)
    TextView tvDrawback;
    @InjectView(R.id.rl_my_collection)
    RelativeLayout rlMyCollection;
    @InjectView(R.id.rl_my_plan)
    RelativeLayout rlMyPlan;
    @InjectView(R.id.rl_my_contact)
    RelativeLayout rlMyContact;
    @InjectView(R.id.rl_my_common_user)
    RelativeLayout rlMyCommonUser;
    User user;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_home_more_avatar_unknown_round)
                .showImageOnFail(R.drawable.ic_home_more_avatar_unknown_round)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中// 设置成圆角图片
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_info, null);
        ButterKnife.inject(this, view);

        settingBtn.setOnClickListener(this);
        tvAllOrder.setOnClickListener(this);
        tvNeedPay.setOnClickListener(this);
        tvProcess.setOnClickListener(this);
        tvAvailable.setOnClickListener(this);
        tvDrawback.setOnClickListener(this);
        rlMyCollection.setOnClickListener(this);
        rlMyCommonUser.setOnClickListener(this);
        rlMyContact.setOnClickListener(this);
        rlMyPlan.setOnClickListener(this);
        user = AccountManager.getInstance().getLoginAccount(getActivity());
        initHeadTitleView(user);
        return view;
    }

    public void initHeadTitleView(User user) {
        if (user!=null){
            tvNickname.setText(user.getNickName());
            ImageLoader.getInstance().displayImage(user.getAvatar(), userAvatar, options);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        User user = AccountManager.getInstance().getLoginAccount(getActivity());
        if (user != null) {
            initHeadTitleView(user);
        } else {
            MainActivity activity = (MainActivity) getActivity();
            activity.setTabForLogout();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_btn:
                Intent setting_btn = new Intent(getActivity(), SettingActivity.class);
                startActivity(setting_btn);
                break;
            case R.id.tv_all_order:
                Intent tv_all_order = new Intent(getActivity(), OrderListActivity.class);
                startActivity(tv_all_order);
                break;
            case R.id.tv_need_pay:
                Intent tv_need_pay = new Intent(getActivity(), OrderListActivity.class);
                tv_need_pay.putExtra("page",1);
                startActivity(tv_need_pay);
                break;
            case R.id.tv_process:
                Intent tv_process = new Intent(getActivity(), OrderListActivity.class);
                tv_process.putExtra("page",2);
                startActivity(tv_process);
                break;
            case R.id.tv_available:
                Intent tv_available = new Intent(getActivity(), OrderListActivity.class);
                tv_available.putExtra("page",3);
                startActivity(tv_available);
                break;
            case R.id.tv_drawback:
                Intent tv_drawback = new Intent(getActivity(), OrderListActivity.class);
                tv_drawback.putExtra("page",4);
                startActivity(tv_drawback);
                break;
            case R.id.rl_my_collection:
                break;
            case R.id.rl_my_plan:
                Intent planIntent = new Intent();
                planIntent.setClass(getActivity(), StrategyListActivity.class);
                planIntent.putExtra("isShare", false);
                planIntent.putExtra("isOwner",true);
                if (user!=null)planIntent.putExtra("userId",String.valueOf(user.getUserId()));
                startActivity(planIntent);
                break;
            case R.id.rl_my_contact:
                Intent contactIntent = new Intent();
                contactIntent.setClass(getActivity(), ContactActivity.class);
                startActivity(contactIntent);
                break;
            case R.id.rl_my_common_user:
                Intent userIntent = new Intent();
                userIntent.setClass(getActivity(), CommonUserInfoActivity.class);
                userIntent.putExtra("ListType",2);
                startActivity(userIntent);
                break;
            default:
                break;
        }
    }
}
