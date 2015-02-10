package com.aizou.peachtravel.module.toolbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.aizou.peachtravel.common.yweathergetter4a.WeatherInfo;
import com.aizou.peachtravel.common.yweathergetter4a.YahooWeather;
import com.aizou.peachtravel.common.yweathergetter4a.YahooWeatherInfoListener;
import com.aizou.peachtravel.module.PeachWebViewActivity;
import com.aizou.peachtravel.module.dest.SelectDestActivity;
import com.aizou.peachtravel.module.my.LoginActivity;
import com.aizou.peachtravel.module.toolbox.im.IMMainActivity;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/10/9.
 */
public class ToolboxFragment extends PeachBaseFragment implements View.OnClickListener {
    public final static int CODE_IM_LOGIN = 101;
    public final static int CODE_PLAN = 103;

    @InjectView(R.id.tv_weather)
    TextView mTvWeather;
    @InjectView(R.id.tv_city)
    TextView mTvCity;
    @InjectView(R.id.vp_travel)
    AutoScrollViewPager mVpTravel;
    @InjectView(R.id.dot_view)
    DotView mDotView;
    @InjectView(R.id.my_guides)
    RelativeLayout mRLMyGuides;
    @InjectView(R.id.my_around)
    RelativeLayout mRLMyAround;
    private String[] weatherArray;
    private String weatherStr;
    private String city;
    private String street;
    private String address;
    private double geoLat = -1;
    private double geoLng = -1;

    private LocationManagerProxy mLocationManagerProxy;

    private Handler scrollHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_travel, null);
        ButterKnife.inject(this, rootView);

        rootView.findViewById(R.id.tv_title_bar_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionDialog();
            }
        });

        weatherArray = getResources().getStringArray(R.array.weather);
        mLocationManagerProxy = LocationManagerProxy.getInstance(getActivity());
        mLocationManagerProxy.setGpsEnable(false);
//        requestWeather();
        getOperateData();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRLMyGuides.setOnClickListener(this);
        mRLMyAround.setOnClickListener(this);
    }

    public void reloadData() {
        if (TextUtils.isEmpty(weatherStr)) {
            requestWeather();
        }
    }

    private void showActionDialog() {
        final Activity act = getActivity();
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        View contentView = View.inflate(act, R.layout.dialog_home_confirm_action, null);
        contentView.findViewById(R.id.btn_go_plan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, SelectDestActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        contentView.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        WindowManager windowManager = act.getWindowManager();
        Window window = dialog.getWindow();
        window.setContentView(contentView);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (display.getWidth()); // 设置宽度
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
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
//                ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
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
                    mTvWeather.setText(weatherArray[weatherInfo.getCurrentCode()]);
                    mTvCity.setText(weatherStr);

                    mLocationManagerProxy.destroy();
                    mLocationManagerProxy = null;
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        PeachUser user = AccountManager.getInstance().getLoginAccount(getActivity());
        switch (v.getId()) {

            case R.id.my_around:
                Intent intent = new Intent(getActivity(), NearbyActivity.class);
                intent.putExtra("lat", geoLat);
                intent.putExtra("lng", geoLng);
                intent.putExtra("street", street);
                intent.putExtra("address", address);
                intent.putExtra("city", city);
                startActivity(intent);

//                if (geoLat == null) {
//                    DialogManager.getInstance().showLoadingDialog(getActivity(), "正在定位");
//                    mLocationManagerProxy.requestLocationData(
//                            LocationProviderProxy.AMapNetwork, -1, 15, new AMapLocationListener() {
//                                @Override
//                                public void onLocationChanged(AMapLocation aMapLocation) {
//                                    DialogManager.getInstance().dissMissLoadingDialog();
//                                    if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
//                                        //获取位置信息
//                                        geoLat = aMapLocation.getLatitude();
//                                        geoLng = aMapLocation.getLongitude();
//                                        city = aMapLocation.getCity();
//                                        street = aMapLocation.getStreet();
//                                        address = aMapLocation.getAddress();
//                                        Intent intent = new Intent(getActivity(), NearbyActivity.class);
//                                        intent.putExtra("lat", geoLat);
//                                        intent.putExtra("lng", geoLng);
//                                        intent.putExtra("street", street);
//                                        intent.putExtra("address", address);
//                                        intent.putExtra("city", city);
//                                        startActivity(intent);
//                                    } else {
//                                        ToastUtil.getInstance(getActivity()).showToast("定位失败，请稍后重试");
//                                    }
//
//                                }
//
//                                @Override
//                                public void onLocationChanged(Location location) {
//
//                                }
//
//                                @Override
//                                public void onStatusChanged(String provider, int status, Bundle extras) {
//
//                                }
//
//                                @Override
//                                public void onProviderEnabled(String provider) {
//
//                                }
//
//                                @Override
//                                public void onProviderDisabled(String provider) {
//
//                                }
//                            });
//                }

                break;

            case R.id.my_guides:
                if (user != null && !TextUtils.isEmpty(user.easemobUser)) {
                    Intent strategyIntent = new Intent(getActivity(), StrategyListActivity.class);
                    startActivity(strategyIntent);
                } else {
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(loginIntent, CODE_PLAN);
                    ToastUtil.getInstance(getActivity()).showToast("请先登录");
                }
                break;

//            case R.id.tv_fav:
//                if (user != null && !TextUtils.isEmpty(user.easemobUser)) {
//                    Intent fIntent = new Intent(getActivity(), FavListActivity.class);
//                    startActivity(fIntent);
//                } else {
//                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
//                    startActivityForResult(loginIntent, CODE_FAVORITE);
//                }
//                break;

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
                    startActivityWithNoAnim(new Intent(getActivity(), IMMainActivity.class));
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
        PeachUser user = AccountManager.getInstance().getLoginAccount(getActivity());
        reloadData();
        if (scrollHandler != null) {
            scrollHandler.removeMessages(0);
            scrollHandler.sendEmptyMessageDelayed(0, 6000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (scrollHandler != null) {
            scrollHandler.removeMessages(0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scrollHandler = null;
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

}
