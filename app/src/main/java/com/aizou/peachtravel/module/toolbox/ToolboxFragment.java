package com.aizou.peachtravel.module.toolbox;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
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
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/10/9.
 */
public class ToolboxFragment extends PeachBaseFragment implements View.OnClickListener, AMapLocationListener {
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
    private String city;
    private Double geoLat;
    private Double geoLng;

    private LocationManagerProxy mLocationManagerProxy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_travel, null);
        ButterKnife.inject(this,rootView);
        mLyHeaderBarTitleWrap.getTitleTextView().setText("我的攻略");
        mLyHeaderBarTitleWrap.enableBackKey(false);

        mBtnLxq.setOnClickListener(this);
        mTvNearby.setOnClickListener(this);
        mTvMyGuide.setOnClickListener(this);
        rootView.findViewById(R.id.tv_fav).setOnClickListener(this);
        user = AccountManager.getInstance().getLoginAccount(getActivity());
        weatherArray = getResources().getStringArray(R.array.weather);
        mLocationManagerProxy = LocationManagerProxy.getInstance(getActivity());
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, -1, 15, this);
        mLocationManagerProxy.setGpsEnable(false);
        return rootView;
    }

    private void getYahooWeather(double lat, double lon) {
        YahooWeather.getInstance().queryYahooWeatherByLatLon(getActivity(), lat + "", lon + "", new YahooWeatherInfoListener() {
            @Override
            public void gotWeatherInfo(WeatherInfo weatherInfo) {

                if (weatherInfo == null) {
                    return;
                }
                weatherStr = DateUtil.getCurrentMonthDay() + "   "+city+"   " + weatherArray[weatherInfo.getCurrentCode()];
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
                if (user != null && !TextUtils.isEmpty(user.easemobUser)) {
                    Intent strategyIntent = new Intent(getActivity(), StrategyListActivity.class);
                    startActivity(strategyIntent);
                } else {
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(loginIntent, IM_LOGIN);
                }
                break;

            case R.id.tv_fav:
                if (user != null && !TextUtils.isEmpty(user.easemobUser)) {
                    Intent fIntent = new Intent(getActivity(), FavListActivity.class);
                    startActivity(fIntent);
                } else {
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(loginIntent, IM_LOGIN);
                }

                break;

            default:
                break;
        }
        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_stay);
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

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0){
            //获取位置信息
             geoLat = aMapLocation.getLatitude();
             geoLng = aMapLocation.getLongitude();
             city=aMapLocation.getCity();
            getYahooWeather(geoLat,geoLng);
        }

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
