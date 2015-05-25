package com.aizou.peachtravel.module.dest;

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
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.OperateBean;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.OtherApi;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.widget.DynamicBox;
import com.aizou.peachtravel.common.yweathergetter4a.WeatherInfo;
import com.aizou.peachtravel.common.yweathergetter4a.YahooWeather;
import com.aizou.peachtravel.common.yweathergetter4a.YahooWeatherInfoListener;
import com.aizou.peachtravel.module.MainActivity;
import com.aizou.peachtravel.module.PeachWebViewActivity;
import com.aizou.peachtravel.module.my.LoginActivity;
import com.aizou.peachtravel.module.toolbox.NearbyActivity;
import com.aizou.peachtravel.module.toolbox.StrategyListActivity;
import com.aizou.peachtravel.module.toolbox.im.ExpertListActivity;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * Created by lxp_dqm07 on 2015/4/11.
 */
public class TripFragment extends PeachBaseFragment implements View.OnClickListener {

    private DynamicBox box;
    private TextView lx_guide_favour;
    private TextView lx_trip_plan;
    private TextView lx_around;
    private TextView lx_des;
    private EditText search_all;
    private String[] weatherArray;
    private String weatherStr;
    private String city;
    private String street;
    private String address;
    private double geoLat = -1;
    private double geoLng = -1;
    private AutoScrollViewPager mVpTravel;
    private DotView mDotView;
    private boolean isScrollPicLoad;

    private LocationManagerProxy mLocationManagerProxy;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_trip,null);

        mVpTravel=(AutoScrollViewPager)rootView.findViewById(R.id.vp_travel);
        mDotView=(DotView)rootView.findViewById(R.id.dot_view);
        lx_guide_favour=(TextView)rootView.findViewById(R.id.lx_guide_favour);
        lx_trip_plan=(TextView)rootView.findViewById(R.id.lx_trip_plan);
        lx_around=(TextView)rootView.findViewById(R.id.lx_around);
        lx_des=(TextView)rootView.findViewById(R.id.lx_des);
        search_all=(EditText)rootView.findViewById(R.id.search_all_et);
        lx_guide_favour.setOnClickListener(this);
        lx_trip_plan.setOnClickListener(this);
        lx_around.setOnClickListener(this);
        lx_des.setOnClickListener(this);
        search_all.setOnClickListener(this);
        weatherArray = getResources().getStringArray(R.array.weather);
        mLocationManagerProxy = LocationManagerProxy.getInstance(getActivity());
        mLocationManagerProxy.setGpsEnable(false);

        getOperateData();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(((MainActivity)getActivity()).isConflict){
            outState.putBoolean("isConflict", true);
        }
    }

    private void getOperateData() {
        OtherApi.getOperate(new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson4List<OperateBean> operateResult = CommonJson4List.fromJson(result, OperateBean.class);
                if (operateResult.code == 0) {
                    isScrollPicLoad=true;
                    bindOperateView(operateResult.result);
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
               isScrollPicLoad=false;
               ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
            }
        });

    }

    private void bindOperateView(final List<OperateBean> result) {
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(getActivity(), result);
        imagePagerAdapter.setInfiniteLoop(true);
        mVpTravel.setAdapter(imagePagerAdapter);
        mVpTravel.setStopScrollWhenTouch(true);
        mVpTravel.setAutoScrollDurationFactor(6000);
        mDotView.setNum(result.size());
        mVpTravel.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mDotView.setSelected(position % result.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        scrollHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                int nextItem = mVpTravel.getCurrentItem() + 1;
//                nextItem = (nextItem >= mVpTravel.getAdapter().getCount() ? 0 : nextItem);
//                mVpTravel.setCurrentItem(nextItem);
//                scrollHandler.sendEmptyMessageDelayed(0, 6000);
//            }
//        };
//        scrollHandler.sendEmptyMessageDelayed(0, 6000);
    }

    public class ImagePagerAdapter extends RecyclingPagerAdapter {

        private Context context;
        private List<OperateBean> operateBeans;

        private int size;
        private boolean isInfiniteLoop;

        private DisplayImageOptions options;

        public ImagePagerAdapter(Context context, List<OperateBean> operateBeans) {
            this.context = context;
            this.operateBeans = operateBeans;
            this.size = operateBeans.size();
            isInfiniteLoop = false;

            options = UILUtils.getDefaultOption();
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
            ImageView imageView = (ImageView) view;
            if (imageView == null) {
                imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            ImageLoader.getInstance().displayImage(
                    operateBeans.get(getPosition(position)).cover, imageView, options
            );
            imageView.setTag(position);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(getActivity(), "event_click_opertion_page");
                    Intent intent = new Intent(getActivity(), PeachWebViewActivity.class);
                    intent.putExtra("url",operateBeans.get(getPosition(position)).link);
                    startActivity(intent);
                }
            });
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

    @Override
    public void onClick(View v) {
        PeachUser user = AccountManager.getInstance().getLoginAccount(getActivity());
        switch (v.getId()) {
            case R.id.lx_guide_favour:
                Intent ExpertIntent=new Intent(getActivity(), ExpertListActivity.class);
                startActivity(ExpertIntent);
                break;

            case R.id.lx_trip_plan:
                if(user!=null&&!TextUtils.isEmpty(user.easemobUser)){
                    Intent intent=new Intent(getActivity(), StrategyListActivity.class);
                    intent.putExtra("userId",user.userId+"");
                    startActivity(intent);
                }else{
                    Intent LoginIntent=new Intent(getActivity(), LoginActivity.class);
                    startActivity(LoginIntent);
                    getActivity().overridePendingTransition(R.anim.push_bottom_in,0);
                    ToastUtil.getInstance(getActivity()).showToast(" 请先登录");
                }
                break;

            case R.id.lx_around:
                Intent AroundIntent=new Intent(getActivity(), NearbyActivity.class);
                AroundIntent.putExtra("lat", geoLat);
                AroundIntent.putExtra("lng", geoLng);
                AroundIntent.putExtra("street", street);
                AroundIntent.putExtra("address", address);
                AroundIntent.putExtra("city", city);
                startActivity(AroundIntent);
                break;

            case R.id.lx_des:
                Intent intent=new Intent();
                intent.setClass(getActivity(), RecDestActivity.class);
                startActivity(intent);
                break;

            case R.id.search_all_et:
                Intent sear_intent = new Intent(getActivity(),SearchAllActivity.class);
                startActivity(sear_intent);
                getActivity().overridePendingTransition(R.anim.push_bottom_in,0);
                break;

            default:
                break;
        }
    }

    public void reloadDataAndPics() {
        if (TextUtils.isEmpty(weatherStr)) {
            requestWeather();
        }
        if(!isScrollPicLoad){
            getOperateData();
        }
    }

    private void requestWeather() {
        if(mLocationManagerProxy == null){
            mLocationManagerProxy = LocationManagerProxy.getInstance(getActivity());
            mLocationManagerProxy.setGpsEnable(false);
        }
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, -1, 15, new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation aMapLocation) {
                        if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
                            //获取位置信息
                            geoLat = aMapLocation.getLatitude();
                            geoLng = aMapLocation.getLongitude();
                            city = aMapLocation.getCity();
                            address = aMapLocation.getAddress();
                            street = aMapLocation.getStreet();
                            getYahooWeather(geoLat, geoLng);
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
    }

    private void getYahooWeather(double lat, double lon) {
        YahooWeather.getInstance().queryYahooWeatherByLatLon(getActivity(), lat + "", lon + "", new YahooWeatherInfoListener() {
            @Override
            public void gotWeatherInfo(WeatherInfo weatherInfo) {
                try {
                    if (weatherInfo == null) {
                        return;
                    }
                    weatherStr = DateUtil.getCurrentMonthDay() + "   " + city;
//                ImageLoader.getInstance().displayImage(weatherInfo.getCurrentConditionIconURL(), mIvWeather, UILUtils.getDefaultOption());
//                    mTvWeather.setText(weatherArray[weatherInfo.getCurrentCode()]);
//                    mTvCity.setText(weatherStr);

                    mLocationManagerProxy.destroy();
                    mLocationManagerProxy = null;
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadDataAndPics();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        reloadDataAndPics();
    }
}
