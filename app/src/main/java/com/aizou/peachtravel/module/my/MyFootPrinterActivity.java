package com.aizou.peachtravel.module.my;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.android.airmapview.AirMapInterface;
import com.airbnb.android.airmapview.AirMapMarker;
import com.airbnb.android.airmapview.AirMapPolyline;
import com.airbnb.android.airmapview.AirMapView;
import com.airbnb.android.airmapview.AirMapViewTypes;
import com.airbnb.android.airmapview.DefaultAirMapViewBuilder;
import com.airbnb.android.airmapview.GoogleChinaMapType;
import com.airbnb.android.airmapview.listeners.OnMapInitializedListener;
import com.aizou.core.log.LogUtil;
import com.aizou.core.widget.pagerIndicator.indicator.FixedIndicatorView;
import com.aizou.core.widget.pagerIndicator.indicator.IndicatorViewPager;
import com.aizou.core.widget.pagerIndicator.viewpager.FixedViewPager;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.module.dest.OnDestActionListener;
import com.aizou.peachtravel.module.dest.fragment.InDestFragment;
import com.aizou.peachtravel.module.dest.fragment.OutCountryFragment;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxp_dqm07 on 2015/5/12.
 */
public class MyFootPrinterActivity extends PeachBaseActivity implements OnDestActionListener {

    private TextView titleBack;
    private FixedIndicatorView inOutIndicator;
    private FixedViewPager mSelectDestVp;
    private IndicatorViewPager indicatorViewPager;
    private AirMapView mapView;
    private AMap aMap;
    private DefaultAirMapViewBuilder mapViewBuilder;
    private AirMapInterface airMapInterface;
    private AirMapPolyline airMapPolyline;
    private long MARKER = 1;
    private ArrayList<LocBean> allAddCityList = new ArrayList<LocBean>();

    @Override
    public void onDestAdded(LocBean locBean) {
        allAddCityList.add(locBean);
        refreshMapView(allAddCityList);
    }

    @Override
    public void onDestRemoved(LocBean locBean) {
        allAddCityList.remove(locBean);
        refreshMapView(allAddCityList);
    }

    private void refreshMapView(final ArrayList<LocBean> bean){
        if(bean.size()>0){
            mapViewBuilder = new DefaultAirMapViewBuilder(this);
            airMapInterface = mapViewBuilder.builder(AirMapViewTypes.WEB).withOptions(new GoogleChinaMapType()).build();
            mapView.setOnMapInitializedListener(new OnMapInitializedListener() {
                @Override
                public void onMapInitialized() {
                    for (int j = 0; j < bean.size();j++) {
                        mapView.addMarker(new AirMapMarker(new LatLng(bean.get(j).location.coordinates[1], bean.get(j).location.coordinates[0]), j + 1));
                    }
                    mapView.animateCenterZoom(new LatLng(bean.get(0).location.coordinates[1], bean.get(0).location.coordinates[0]), 2);
                }
            });
        }else{
            mapViewBuilder = new DefaultAirMapViewBuilder(this);
            airMapInterface = mapViewBuilder.builder(AirMapViewTypes.WEB).withOptions(new GoogleChinaMapType()).build();
            mapView.setOnMapInitializedListener(new OnMapInitializedListener() {
                @Override
                public void onMapInitialized() {
                    mapView.animateCenterZoom(new LatLng(0, 0), 2);
                }
            });
        }
        mapView.initialize(getSupportFragmentManager(), airMapInterface);
        mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView= View.inflate(mContext, R.layout.activity_my_footprinter,null);
        setContentView(rootView);
        mapView = (AirMapView)rootView.findViewById(R.id.my_footprinter_map);
       /* mapView.onCreate(savedInstanceState);
        initMapView();*/
        inOutIndicator = (FixedIndicatorView) rootView.findViewById(R.id.my_footprinter_in_out_indicator);
        mSelectDestVp = (FixedViewPager) rootView.findViewById(R.id.my_footprinter_select_dest_viewPager);
        indicatorViewPager = new IndicatorViewPager(inOutIndicator,mSelectDestVp);
        indicatorViewPager.setAdapter(new InOutFragmentAdapter(getSupportFragmentManager()));
        mSelectDestVp.setCanScroll(false);
        // 设置viewpager保留界面不重新加载的页面数量
        mSelectDestVp.setOffscreenPageLimit(2);
        // 默认是1,，自动预加载左右两边的界面。设置viewpager预加载数为0。只加载加载当前界面。
        mSelectDestVp.setPrepareNumber(0);
        indicatorViewPager.setOnIndicatorPageChangeListener(new IndicatorViewPager.OnIndicatorPageChangeListener() {
            @Override
            public void onIndicatorPageChange(int preItem, int currentItem) {
                if(currentItem==1){
                    MobclickAgent.onEvent(mContext, "event_go_aboard");
                }
            }
        });

        titleBack=(TextView)rootView.findViewById(R.id.my_footprinter_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        refreshMapView(allAddCityList);
    }

    private class InOutFragmentAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        private String[] tabNames = { "国内", "国外"};
        private LayoutInflater inflater;

        public InOutFragmentAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            inflater = LayoutInflater.from(getApplicationContext());
        }

        @Override
        public int getCount() {
            return tabNames.length;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.tab_select_dest, container, false);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.tv_title);
            textView.setText(tabNames[position]);
            if(position==0){
                textView.setBackgroundResource(R.drawable.in_out_indicator_textbg);
            }else if(position==1){
                textView.setBackgroundResource(R.drawable.in_out_indicator_textbg_01);
            }
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            if (position == 0) {
                InDestFragment inDestFragment = new InDestFragment(false);
                return inDestFragment;
            } else if (position == 1) {
                OutCountryFragment outCountryFragment = new OutCountryFragment(false);
                return outCountryFragment;
            }
            return null;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
