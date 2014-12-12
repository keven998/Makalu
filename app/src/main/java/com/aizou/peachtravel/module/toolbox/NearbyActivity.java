package com.aizou.peachtravel.module.toolbox;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.pagerIndicator.indicator.IndicatorViewPager;
import com.aizou.core.widget.pagerIndicator.indicator.ScrollIndicatorView;
import com.aizou.core.widget.pagerIndicator.indicator.slidebar.ColorBar;
import com.aizou.core.widget.pagerIndicator.viewpager.FixedViewPager;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.toolbox.fragment.NearbyItemFragment;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/24.
 */
public class NearbyActivity extends PeachBaseActivity implements AMapLocationListener {
    @InjectView(R.id.title_bar)
    TitleHeaderBar mTitleBar;
    @InjectView(R.id.nearby_indicator)
    ScrollIndicatorView mNearbyIndicator;
    @InjectView(R.id.nearby_viewPager)
    FixedViewPager mNearbyViewPager;
    @InjectView(R.id.tv_address)
    TextView mTvAddress;
    @InjectView(R.id.btn_refresh)
    Button mBtnRefresh;
    @InjectView(R.id.pb_location)
    ProgressBar mPbLocation;
    private IndicatorViewPager indicatorViewPager;
    private double lat;
    private double lng;
    private String city;
    private String street;
    private String address;

    private String[] tabTitles;
    private String[] tabTypes = {"vs", "restaurant", "shopping", "hotel"};
    private int[] tabRes = {R.drawable.checker_tab_nearby_ic_spot, R.drawable.checker_tab_nearby_ic_delicacy, R.drawable.checker_tab_nearby_ic_shopping, R.drawable.checker_tab_nearby_ic_stay};
    private LocationManagerProxy mLocationManagerProxy;
    private ArrayList<OnLocationChangeListener> onLocationChangeListenerList = new ArrayList<OnLocationChangeListener>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabTitles = getResources().getStringArray(R.array.local_type_title);
        lat = getIntent().getDoubleExtra("lat", 0);
        lng = getIntent().getDoubleExtra("lng", 0);
        city = getIntent().getStringExtra("city");
        street = getIntent().getStringExtra("street");
        address = getIntent().getStringExtra("address");
        initView();
        mLocationManagerProxy = LocationManagerProxy.getInstance(this);
        mLocationManagerProxy.setGpsEnable(false);
    }

    private void initView() {
        setContentView(R.layout.activity_nearby);
        ButterKnife.inject(this);
        mTvAddress.setText(address);
        ColorBar colorBar = new ColorBar(mContext, getResources().getColor(R.color.app_theme_color), 5);
        colorBar.setWidth(LocalDisplay.dp2px(50));
        mNearbyIndicator.setScrollBar(colorBar);

        indicatorViewPager = new IndicatorViewPager(mNearbyIndicator, mNearbyViewPager);
        indicatorViewPager.setPageOffscreenLimit(2);

        indicatorViewPager.setAdapter(new NearbyAdapter(getSupportFragmentManager()));
        mTitleBar.getTitleTextView().setText("我身边");
        mTitleBar.enableBackKey(true);
        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocation();
            }
        });
    }

    private void startLocation() {
        mPbLocation.setVisibility(View.VISIBLE);
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, -1, 15, this);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        try {
            OnLocationChangeListener listener = (OnLocationChangeListener) fragment;
            onLocationChangeListenerList.add(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onAttachFragment(fragment);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        mPbLocation.setVisibility(View.GONE);
        if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
            //获取位置信息
            lat = aMapLocation.getLatitude();
            lng = aMapLocation.getLongitude();
            city = aMapLocation.getCity();
            street = aMapLocation.getStreet();
            address = aMapLocation.getAddress();
            mTvAddress.setText(address);
            for (OnLocationChangeListener onLocationChangeListener : onLocationChangeListenerList) {
                if (onLocationChangeListener != null) {
                    onLocationChangeListener.onLocationChange(lat, lng);
                }
            }
        } else {
            ToastUtil.getInstance(this).showToast("定位失败，请稍后重试");
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

    private class NearbyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        List<NearbyItemFragment> fragmentList;

        public NearbyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            fragmentList =new ArrayList<>();
            for(String type :tabTypes){
                NearbyItemFragment fragment = new NearbyItemFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", type);
                bundle.putDouble("lat", lat);
                bundle.putDouble("lng", lng);
                fragment.setArguments(bundle);
                fragmentList.add(fragment);
            }

        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.tab_top, null);
                int width = LocalDisplay.SCREEN_WIDTH_PIXELS / 4;
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                convertView.setLayoutParams(params);
            }
            CheckedTextView textView = (CheckedTextView) convertView;
            textView.setText(tabTitles[position]);
            textView.setCompoundDrawablesWithIntrinsicBounds(0, tabRes[position], 0, 0);
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            return fragmentList.get(position);
        }

    }

    public interface OnLocationChangeListener {
        void onLocationChange(double lat, double lng);
    }

}
