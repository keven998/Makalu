package com.aizou.peachtravel.module.toolbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.utils.DateUtil;
import com.aizou.core.widget.autoscrollviewpager.AutoScrollViewPager;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.common.yweathergetter4a.WeatherInfo;
import com.aizou.peachtravel.common.yweathergetter4a.YahooWeather;
import com.aizou.peachtravel.common.yweathergetter4a.YahooWeatherInfoListener;
import com.aizou.peachtravel.module.my.LoginActivity;
import com.aizou.peachtravel.module.toolbox.im.IMMainActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/10/9.
 */
public class TravelFragment extends PeachBaseFragment implements View.OnClickListener {
    public final static int IM_LOGIN = 100;
    @InjectView(R.id.ly_header_bar_title_wrap)
    TitleHeaderBar mLyHeaderBarTitleWrap;
    @InjectView(R.id.iv_weather)
    ImageView mIvWeather;
    @InjectView(R.id.tv_weather)
    TextView mTvWeather;
    @InjectView(R.id.vp_travel)
    AutoScrollViewPager mVpTravel;
    @InjectView(R.id.tv_my_guide)
    TextView mTvMyGuide;
    @InjectView(R.id.tv_fav)
    TextView mTvFav;
    @InjectView(R.id.tv_nearby)
    TextView mTvNearby;
    @InjectView(R.id.btn_lxq)
    Button mBtnLxq;
    private PeachUser user;
    private String[] weatherArray;
    private String weatherStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_travel, null);
        ButterKnife.inject(this,rootView);
        mBtnLxq.setOnClickListener(this);
        mTvNearby.setOnClickListener(this);
        mTvMyGuide.setOnClickListener(this);
        rootView.findViewById(R.id.tv_fav).setOnClickListener(this);
        user = AccountManager.getInstance().getLoginAccount(getActivity());
        weatherArray = getResources().getStringArray(R.array.weather);
        getWeather(116.402544, 39.93242);
        return rootView;
    }

    private void getWeather(double lat, double lon) {
        YahooWeather.getInstance().queryYahooWeatherByLatLon(getActivity(), lat + "", lon + "", new YahooWeatherInfoListener() {
            @Override
            public void gotWeatherInfo(WeatherInfo weatherInfo) {

                if (weatherInfo == null) {
                    return;
                }
                weatherStr = DateUtil.getCurrentMonthDay() + " 北京 " + weatherArray[weatherInfo.getCurrentCode()];
                ImageLoader.getInstance().displayImage(weatherInfo.getCurrentConditionIconURL(), mIvWeather, UILUtils.getDefaultOption());
                mTvWeather.setText(weatherStr);

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_lxq:
                if (user != null && !TextUtils.isEmpty(user.easemobUser)) {
                    Intent intent = new Intent(getActivity(), IMMainActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, IM_LOGIN);
                }
                break;

            case R.id.tv_nearby:
                Intent intent = new Intent(getActivity(), NearbyActivity.class);
                startActivity(intent);
                break;

            case R.id.tv_my_guide:
                Intent strategyIntent = new Intent(getActivity(), StrategyListActivity.class);
                startActivity(strategyIntent);
                break;

            case R.id.tv_fav:
                Intent fIntent = new Intent(getActivity(), FavListActivity.class);
                startActivity(fIntent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IM_LOGIN:
                    Intent intent = new Intent(getActivity(), IMMainActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        user = AccountManager.getInstance().getLoginAccount(getActivity());
    }
}
