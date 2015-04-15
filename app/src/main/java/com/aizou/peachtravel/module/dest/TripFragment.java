package com.aizou.peachtravel.module.dest;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.utils.DateUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.utils.IntentUtils;
import com.aizou.peachtravel.common.utils.video.Utils;
import com.aizou.peachtravel.common.widget.DynamicBox;
import com.aizou.peachtravel.common.yweathergetter4a.WeatherInfo;
import com.aizou.peachtravel.common.yweathergetter4a.YahooWeather;
import com.aizou.peachtravel.common.yweathergetter4a.YahooWeatherInfoListener;
import com.aizou.peachtravel.module.my.LoginActivity;
import com.aizou.peachtravel.module.toolbox.NearbyActivity;
import com.aizou.peachtravel.module.toolbox.StrategyListActivity;
import com.aizou.peachtravel.module.toolbox.im.ExpertListActivity;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

/**
 * Created by lxp_dqm07 on 2015/4/11.
 */
public class TripFragment extends PeachBaseFragment implements View.OnClickListener {

    private DynamicBox box;
    private TextView lx_guide_favour;
    private TextView lx_trip_plan;
    private TextView lx_around;
    private TextView lx_des;
    private String[] weatherArray;
    private String weatherStr;
    private String city;
    private String street;
    private String address;
    private double geoLat = -1;
    private double geoLng = -1;

    private LocationManagerProxy mLocationManagerProxy;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_trip,null);

        lx_guide_favour=(TextView)rootView.findViewById(R.id.lx_guide_favour);
        lx_trip_plan=(TextView)rootView.findViewById(R.id.lx_trip_plan);
        lx_around=(TextView)rootView.findViewById(R.id.lx_around);
        lx_des=(TextView)rootView.findViewById(R.id.lx_des);
        lx_guide_favour.setOnClickListener(this);
        lx_trip_plan.setOnClickListener(this);
        lx_around.setOnClickListener(this);
        lx_des.setOnClickListener(this);

        weatherArray = getResources().getStringArray(R.array.weather);
        mLocationManagerProxy = LocationManagerProxy.getInstance(getActivity());
        mLocationManagerProxy.setGpsEnable(false);

        return rootView;
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
                    startActivity(intent);
                }else{
                    Intent LoginIntent=new Intent(getActivity(), LoginActivity.class);
                    startActivity(LoginIntent);
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

            default:
                break;
        }
    }

    public void reloadData() {
        if (TextUtils.isEmpty(weatherStr)) {
            requestWeather();
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
        reloadData();
    }
}
