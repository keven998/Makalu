package com.xuejian.client.lxp.module.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.utils.GsonTools;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.utils.ConstellationUtil;
import com.xuejian.client.lxp.common.utils.PreferenceUtils;
import com.xuejian.client.lxp.common.widget.CustemViewPager;
import com.xuejian.client.lxp.common.widget.RoundImageBoarderView;
import com.xuejian.client.lxp.common.widget.SimpleViewPagerIndicator;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.MainActivity;
import com.xuejian.client.lxp.module.dest.SelectDestActivity;
import com.xuejian.client.lxp.module.dest.fragment.StrategyFragment;
import com.xuejian.client.lxp.module.toolbox.im.ContactlistFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yibiao.qin on 2015/9/10.
 */
public class UserFragment extends PeachBaseFragment {
    @InjectView(R.id.user_avatar)
    RoundImageBoarderView userAvatar;
    @InjectView(R.id.nameAndId)
    TextView nameAndId;
    @InjectView(R.id.other_infos)
    TextView otherInfos;
    @InjectView(R.id.user_info_pannel)
    LinearLayout userInfoPannel;
    @InjectView(R.id.id_stickynavlayout_topview)
    RelativeLayout mTopview;
    @InjectView(R.id.id_stickynavlayout_indicator)
    SimpleViewPagerIndicator mIndicator;
    @InjectView(R.id.id_stickynavlayout_viewpager)
    CustemViewPager mViewpager;
    @InjectView(R.id.setting_btn)
    TextView tv_setting;
    @InjectView(R.id.my_panpan_frame)
    RelativeLayout rl_myInfo;
    @InjectView(R.id.add_plan_icon)
    ImageView iv_createPlan;
    private String[] mTitles = new String[]{"旅行计划", "联系人"};
    private FragmentPagerAdapter mAdapter;
    private Fragment[] mFragments = new Fragment[2];
    User user;
    DisplayImageOptions options;
    private static final int RESULT_PLAN_DETAIL = 0x222;
    public static final int REQUEST_CODE_NEW_PLAN = 0x22;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_fragment, container, false);
        ButterKnife.inject(this, rootView);
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_home_more_avatar_unknown_round)
                .showImageOnFail(R.drawable.ic_home_more_avatar_unknown_round)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中// 设置成圆角图片
                .build();
        user = AccountManager.getInstance().getLoginAccount(getActivity());
        initHeadTitleView(user);
        tv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingIntent = new Intent(getActivity(), SettingActivity.class);
                startActivity(settingIntent);
            }
        });
        mTopview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyProfileActivity.class);
                startActivity(intent);
            }
        });
        iv_createPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectDestActivity.class);
                startActivityForResult(intent, REQUEST_CODE_NEW_PLAN);
            }
        });
        initDatas();
        initEvents();
        return rootView;
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

    public void initHeadTitleView(User user) {
        if (user != null) {
            ImageLoader.getInstance().displayImage(user.getAvatar(), userAvatar, options);
            StringBuffer nameSb = new StringBuffer();
            int nameLenth = 0;
            if (user.getNickName() != null) {
                nameSb.append(user.getNickName());
                nameLenth = user.getNickName().length();
            }
            int idLenght=0;
            if (user.getUserId() != null) {
                nameSb.append("  " + user.getUserId());
                idLenght=(user.getUserId()+"").length();
            }
            SpannableString spannableString = new SpannableString(nameSb.toString());
            if(idLenght>0){
                spannableString.setSpan(new AbsoluteSizeSpan(13,true),nameLenth+2,nameLenth+2+idLenght,0);
                nameAndId.setText(spannableString);
            }else{
                nameAndId.setText(nameSb.toString());
            }
            StringBuffer otherSb = new StringBuffer();
            if (user.getGender() != null) {
                if (user.getGender().equalsIgnoreCase("M")) {
                    otherSb.append("男");
                } else if (user.getGender().equalsIgnoreCase("F")) {
                    otherSb.append("女");
                } else if (user.getGender().equalsIgnoreCase("S")) {
                    otherSb.append("保密");
                } else {
                    otherSb.append("一言难尽");
                }
            }

            if (user.getBirthday() != null) {
                otherSb.append("  " + ConstellationUtil.calculateConstellationZHname(user.getBirthday()));
            }
            if (user.getLevel() != null) {
                otherSb.append("  " + "LV" + user.getLevel());
            }

            otherInfos.setText(otherSb.toString());
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        User user = AccountManager.getInstance().getLoginAccount(getActivity());
    }
    public ContactlistFragment createContactlistFragment(){
        ContactlistFragment fragment = new ContactlistFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isAddFriend",true);
        fragment.setArguments(bundle);
        return fragment;
    }
    private void initDatas() {
        mViewpager.setScanScroll(false);
        mIndicator.setTitles(mTitles);
        for (int i = 0; i < mTitles.length; i++) {
            if (i == 0) mFragments[i] = (StrategyFragment) new StrategyFragment();
            if (i == 1) mFragments[i] = (ContactlistFragment) createContactlistFragment();
        }

        mAdapter = new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTitles.length;
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

        };
        mViewpager.setAdapter(mAdapter);
        mViewpager.setCurrentItem(0);
    }

    private void initEvents() {
        mIndicator.setOnIndicatorChangeListenr(new SimpleViewPagerIndicator.OnIndicatorChangeListenr() {
            @Override
            public void OnIndicatorChange(int postion) {
                if (postion==0){
                    iv_createPlan.setVisibility(View.VISIBLE);
                }else {
                    iv_createPlan.setVisibility(View.GONE);
                }
                mIndicator.scroll(0, postion);
                mViewpager.setCurrentItem(postion, true);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == RESULT_PLAN_DETAIL) {
                StrategyBean sb = data.getParcelableExtra("strategy");
            }else if(requestCode == REQUEST_CODE_NEW_PLAN){
                StrategyBean sb = data.getParcelableExtra("strategy");
                if (sb != null) {
                    if (getActivity()!=null)PreferenceUtils.cacheData(getActivity(), "last_strategy", GsonTools.createGsonString(sb));
                }
           //     getStrategyListData(user);

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
