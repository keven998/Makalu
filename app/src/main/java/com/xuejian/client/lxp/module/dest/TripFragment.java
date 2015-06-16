package com.xuejian.client.lxp.module.dest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.DateUtil;
import com.aizou.core.widget.DotView;
import com.aizou.core.widget.autoscrollviewpager.AutoScrollViewPager;
import com.aizou.core.widget.pagerIndicator.viewpager.RecyclingPagerAdapter;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.OperateBean;
import com.xuejian.client.lxp.bean.PeachUser;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.widget.DynamicBox;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.common.yweathergetter4a.WeatherInfo;
import com.xuejian.client.lxp.common.yweathergetter4a.YahooWeather;
import com.xuejian.client.lxp.common.yweathergetter4a.YahooWeatherInfoListener;
import com.xuejian.client.lxp.db.userDB.User;
import com.xuejian.client.lxp.module.MainActivity;
import com.xuejian.client.lxp.module.PeachWebViewActivity;
import com.xuejian.client.lxp.module.my.LoginActivity;
import com.xuejian.client.lxp.module.toolbox.NearbyActivity;
import com.xuejian.client.lxp.module.toolbox.StrategyListActivity;
import com.xuejian.client.lxp.module.toolbox.im.ExpertListActivity;

import java.util.List;

/**
 * Created by lxp_dqm07 on 2015/4/11.
 */
public class TripFragment extends PeachBaseFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trip, null);

        final TitleHeaderBar titleHeaderBar = (TitleHeaderBar)rootView.findViewById(R.id.ly_header_bar_title_wrap);
        titleHeaderBar.getTitleTextView().setText("旅行");

        rootView.findViewById(R.id.lxp_search_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sear_intent = new Intent(getActivity(), SearchAllActivity.class);
                startActivityWithNoAnim(sear_intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, R.anim.slide_stay);
            }
        });

        rootView.findViewById(R.id.lxp_helper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ExpertIntent = new Intent(getActivity(), ExpertListActivity.class);
                startActivity(ExpertIntent);
            }
        });

        rootView.findViewById(R.id.lxp_plan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PeachUser user = AccountManager.getInstance().getLoginAccount(getActivity());
                if (user != null && !TextUtils.isEmpty(user.easemobUser)) {
                    Intent intent = new Intent(getActivity(), StrategyListActivity.class);
                    intent.putExtra("userId", String.valueOf(user.userId));
                    intent.putExtra("isExpertPlan", false);
                    startActivity(intent);
                } else {
                    Intent LoginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(LoginIntent, 1);
                    getActivity().overridePendingTransition(R.anim.push_bottom_in, 0);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                PeachUser user = AccountManager.getInstance().getLoginAccount(getActivity());
                if (user != null && !TextUtils.isEmpty(user.easemobUser)) {
                    Intent intent = new Intent(getActivity(), StrategyListActivity.class);
                    intent.putExtra("userId", String.valueOf(user.userId));
                    intent.putExtra("isExpertPlan", false);
                    startActivity(intent);
                }
            }
        }
    }

}
