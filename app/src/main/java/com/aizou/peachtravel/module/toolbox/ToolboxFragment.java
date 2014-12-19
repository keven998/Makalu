package com.aizou.peachtravel.module.toolbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.core.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.DateUtil;
import com.aizou.core.widget.DotView;
import com.aizou.core.widget.autoscrollviewpager.AutoScrollViewPager;
import com.aizou.core.widget.pagerIndicator.viewpager.RecyclingPagerAdapter;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.OperateBean;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.OtherApi;
import com.aizou.peachtravel.common.gson.CommonJson4List;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/10/9.
 */
public class ToolboxFragment extends PeachBaseFragment implements View.OnClickListener {
    public final static int CODE_IM_LOGIN = 101;
    public final static int CODE_FAVORITE = 102;
    public final static int CODE_PLAN = 103;


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
    @InjectView(R.id.ll_weather)
    LinearLayout mLlWeather;
    @InjectView(R.id.dot_view)
    DotView mDotView;
    private PeachUser user;
    private String[] weatherArray;
    private String weatherStr;
    private String city;
    private String street;
    private String address;
    private Double geoLat;
    private Double geoLng;

    private LocationManagerProxy mLocationManagerProxy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_travel, null);
        ButterKnife.inject(this,rootView);
        mLyHeaderBarTitleWrap.getTitleTextView().setText("桃子旅行");
        mLyHeaderBarTitleWrap.enableBackKey(false);

        mBtnLxq.setOnClickListener(this);
        mTvNearby.setOnClickListener(this);
        mTvMyGuide.setOnClickListener(this);
        rootView.findViewById(R.id.tv_fav).setOnClickListener(this);
        user = AccountManager.getInstance().getLoginAccount(getActivity());
        weatherArray = getResources().getStringArray(R.array.weather);
        mLocationManagerProxy = LocationManagerProxy.getInstance(getActivity());
        mLocationManagerProxy.setGpsEnable(false);
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, -1, 15, new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation aMapLocation) {
                        //获取位置信息
                        geoLat = aMapLocation.getLatitude();
                        geoLng = aMapLocation.getLongitude();
                        city = aMapLocation.getCity();
                        address = aMapLocation.getAddress();
                        street = aMapLocation.getStreet();
                        getYahooWeather(geoLat, geoLng);
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
                });
        getOperateData();
        return rootView;
    }

    private void getOperateData() {
        OtherApi.getOperate(new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson4List<OperateBean> operateResult = CommonJson4List.fromJson(result, OperateBean.class);
                if (operateResult.code == 0) {
                    bindOperateView(operateResult.result);
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }
        });

    }

    private void bindOperateView(final List<OperateBean> result) {
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(getActivity(), result);
        imagePagerAdapter.setInfiniteLoop(true);
        mVpTravel.setAdapter(imagePagerAdapter);
        mVpTravel.setStopScrollWhenTouch(true);
        mDotView.setNum(result.size());
        mVpTravel.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mDotView.setSelected(position%result.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getYahooWeather(double lat, double lon) {
        YahooWeather.getInstance().queryYahooWeatherByLatLon(getActivity(), lat + "", lon + "", new YahooWeatherInfoListener() {
            @Override
            public void gotWeatherInfo(WeatherInfo weatherInfo) {
                if (weatherInfo == null) {
                    return;
                }
                weatherStr = DateUtil.getCurrentMonthDay() + "   " + city + "   " + weatherArray[weatherInfo.getCurrentCode()];
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
                    startActivityForResult(intent, CODE_IM_LOGIN);
                }
                break;

            case R.id.tv_nearby:
                if (geoLat == null) {
                    DialogManager.getInstance().showProgressDialog(getActivity(), "正在定位");
                    mLocationManagerProxy.requestLocationData(
                            LocationProviderProxy.AMapNetwork, -1, 15, new AMapLocationListener() {
                                @Override
                                public void onLocationChanged(AMapLocation aMapLocation) {
                                    DialogManager.getInstance().dissMissProgressDialog();
                                    if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
                                        //获取位置信息
                                        geoLat = aMapLocation.getLatitude();
                                        geoLng = aMapLocation.getLongitude();
                                        city = aMapLocation.getCity();
                                        street = aMapLocation.getStreet();
                                        address = aMapLocation.getAddress();
                                        Intent intent = new Intent(getActivity(), NearbyActivity.class);
                                        intent.putExtra("lat", geoLat);
                                        intent.putExtra("lng", geoLng);
                                        intent.putExtra("street", street);
                                        intent.putExtra("address", address);
                                        intent.putExtra("city", city);
                                        startActivity(intent);
                                    } else {
                                        ToastUtil.getInstance(getActivity()).showToast("定位失败，请稍后重试");
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
                            });
                } else {
                    Intent intent = new Intent(getActivity(), NearbyActivity.class);
                    intent.putExtra("lat", geoLat);
                    intent.putExtra("lng", geoLng);
                    intent.putExtra("street", street);
                    intent.putExtra("address", address);
                    intent.putExtra("city", city);
                    startActivity(intent);
                }

                break;

            case R.id.tv_my_guide:
                if (user != null && !TextUtils.isEmpty(user.easemobUser)) {
                    Intent strategyIntent = new Intent(getActivity(), StrategyListActivity.class);
                    startActivity(strategyIntent);
                } else {
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(loginIntent, CODE_PLAN);
                }
                break;

            case R.id.tv_fav:
                if (user != null && !TextUtils.isEmpty(user.easemobUser)) {
                    Intent fIntent = new Intent(getActivity(), FavListActivity.class);
                    startActivity(fIntent);
                } else {
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(loginIntent, CODE_FAVORITE);
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CODE_IM_LOGIN:
                    startActivity(new Intent(getActivity(), IMMainActivity.class));
                    break;

                case CODE_FAVORITE:
                    startActivity(new Intent(getActivity(), FavListActivity.class));
                    break;

                case CODE_PLAN:
                    startActivity(new Intent(getActivity(), StrategyListActivity.class));
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        user = AccountManager.getInstance().getLoginAccount(getActivity());
    }

    public class ImagePagerAdapter extends RecyclingPagerAdapter {

        private Context context;
        private List<OperateBean> operateBeans;

        private int size;
        private boolean isInfiniteLoop;

        public ImagePagerAdapter(Context context, List<OperateBean> operateBeans) {
            this.context = context;
            this.operateBeans = operateBeans;
            this.size = operateBeans.size();
            isInfiniteLoop = false;
        }

        @Override
        public int getCount() {
            // Infinite loop
            return isInfiniteLoop ? Integer.MAX_VALUE : operateBeans.size();
        }

        /**
         * get really position
         *
         * @param position
         * @return
         */
        private int getPosition(int position) {
            return isInfiniteLoop ? position % size : position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup container) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                }
            });

            ImageLoader.getInstance().displayImage(
                    operateBeans.get(getPosition(position)).cover, imageView,
                    UILUtils.getDefaultOption());
            imageView.setTag(position);
            return imageView;
        }


        /**
         * @return the isInfiniteLoop
         */
        public boolean isInfiniteLoop() {
            return isInfiniteLoop;
        }

        /**
         * @param isInfiniteLoop the isInfiniteLoop to set
         */
        public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
            this.isInfiniteLoop = isInfiniteLoop;
            return this;
        }


    }

}
