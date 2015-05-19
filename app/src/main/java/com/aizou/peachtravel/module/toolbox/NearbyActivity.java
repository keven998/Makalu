package com.aizou.peachtravel.module.toolbox;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.pagerIndicator.indicator.IndicatorViewPager;
import com.aizou.core.widget.pagerIndicator.indicator.ScrollIndicatorView;
import com.aizou.core.widget.pagerIndicator.indicator.slidebar.ColorBar;
import com.aizou.core.widget.pagerIndicator.indicator.slidebar.DrawableBar;
import com.aizou.core.widget.pagerIndicator.indicator.slidebar.ScrollBar;
import com.aizou.core.widget.pagerIndicator.viewpager.FixedViewPager;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.toolbox.fragment.NearbyItemFragment;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/24.
 */
public class NearbyActivity extends PeachBaseActivity {
    @InjectView(R.id.nearby_indicator)
    ScrollIndicatorView mNearbyIndicator;
    @InjectView(R.id.nearby_viewPager)
    FixedViewPager mNearbyViewPager;
    @InjectView(R.id.tv_address)
    TextView mTvAddress;
    @InjectView(R.id.btn_refresh)
    ImageButton mBtnRefresh;
    private IndicatorViewPager indicatorViewPager;
    private NearbyAdapter mNAdapter;

    private double mLat = -1;
    private double mLng = -1;
    private String city;
    private String street;
    private String address;
    private Animation mAnim;

    private String[] tabTitles;
    private String[] tabTypes = {"vs", "restaurant", "shopping", "hotel"};
    private int[] tabRes = {R.drawable.checker_tab_nearby_ic_spot,
            R.drawable.checker_tab_nearby_ic_delicacy,
            R.drawable.checker_tab_nearby_ic_shopping,
            R.drawable.checker_tab_nearby_ic_stay
    };

    private LocationManagerProxy mLocationManagerProxy;
    private ArrayList<OnLocationChangeListener> onLocationChangeListenerList = new ArrayList<OnLocationChangeListener>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabTitles = getResources().getStringArray(R.array.local_type_title);
        mAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        initView();
        mLocationManagerProxy = LocationManagerProxy.getInstance(this);
        mLocationManagerProxy.setGpsEnable(false);
        startLocation();
    }
    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("page_locality");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_locality");
    }

    private void init2PreLocData() {
        mLat = getIntent().getDoubleExtra("lat", -1);
        mLng = getIntent().getDoubleExtra("lng", -1);
        city = getIntent().getStringExtra("city");
        street = getIntent().getStringExtra("street");
        address = getIntent().getStringExtra("address");
    }

    private void initView() {
        setContentView(R.layout.activity_nearby);
        ButterKnife.inject(this);
        mTvAddress.setText("正在定位...");
        ColorBar cBar=new ColorBar(mContext,getResources().getColor(R.color.app_theme_color),LocalDisplay.dp2px(2));
        mNearbyIndicator.setScrollBar(cBar);
        indicatorViewPager = new IndicatorViewPager(mNearbyIndicator, mNearbyViewPager);
        indicatorViewPager.setPageOffscreenLimit(2);
        indicatorViewPager.setAdapter(mNAdapter = new NearbyAdapter(getSupportFragmentManager()));
        indicatorViewPager.setCurrentItem(0,false);
        indicatorViewPager.setOnIndicatorPageChangeListener(new IndicatorViewPager.OnIndicatorPageChangeListener() {
            @Override
            public void onIndicatorPageChange(int preItem, int currentItem) {
                NearbyItemFragment cf = (NearbyItemFragment) mNAdapter.getFragmentForPage(currentItem);
                cf.requestDataForInit();
            }
        });

        findViewById(R.id.tv_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext,"event_refresh_location");
                startLocation();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAnim != null) {
            mAnim.cancel();
            mAnim = null;
        }
        indicatorViewPager = null;
        mNAdapter = null;
        mNearbyViewPager = null;
        mNearbyIndicator = null;
    }

    private void resetLocation() {
        mLng = -1;
        mLat = -1;
        city = null;
        street = null;
        address = null;
    }

    private void startLocation() {
//        mPbLocation.setVisibility(View.VISIBLE);
        resetLocation();
        mTvAddress.setText("正在定位...");
        mBtnRefresh.startAnimation(mAnim);

        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, -1, 15, new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation aMapLocation) {

                        if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
                            //获取位置信息
                            mLat = aMapLocation.getLatitude();
                            mLng = aMapLocation.getLongitude();
                            city = aMapLocation.getCity();
                            street = aMapLocation.getStreet();
                            address = aMapLocation.getAddress();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTvAddress.setText(address);
                                    updateContent();
                                    if (mAnim != null) {
                                        mAnim.cancel();
                                    }
                                }
                            });

                        } else {
                            mBtnRefresh.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mAnim != null) {
                                        mAnim.cancel();
                                    }
                                    mTvAddress.setText("获取位置信息失败");
                                }
                            }, 800);
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
                        mTvAddress.setText("无法获取位置信息");
                    }
                });
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
//        try {
//            OnLocationChangeListener listener = (OnLocationChangeListener) fragment;
//            onLocationChangeListenerList.add(listener);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        super.onAttachFragment(fragment);
    }


    private void updateContent() {
        if (mLat == -1 && mLng == -1) return;
        for(OnLocationChangeListener onLocationChangeListener:onLocationChangeListenerList){
            onLocationChangeListener.onLocationChange(mLat, mLng);
        }
        int item = indicatorViewPager.getCurrentItem();
        NearbyItemFragment cf = (NearbyItemFragment) mNAdapter.getFragmentForPage(item);
        cf.requestDataUpdate();
    }


    private class NearbyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        HashMap<String,NearbyItemFragment> fragmentMap;

        public NearbyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            fragmentMap = new HashMap<>();
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
          //  textView.setCompoundDrawablesWithIntrinsicBounds(0, tabRes[position], 0, 0);
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            NearbyItemFragment fragment = fragmentMap.get(tabTypes[position]);
            if(fragment == null) {
                fragment = new NearbyItemFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", tabTypes[position]);
                fragment.setArguments(bundle);
                fragment.updateLocation(mLat,mLng);
                try {
                    OnLocationChangeListener listener = (OnLocationChangeListener) fragment;
                    onLocationChangeListenerList.add(listener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fragmentMap.put(tabTypes[position], fragment);
            }
            return fragment;
        }

    }

    public interface OnLocationChangeListener {
        void onLocationChange(double lat, double lng);
    }

}
