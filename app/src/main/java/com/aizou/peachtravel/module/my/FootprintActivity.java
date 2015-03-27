package com.aizou.peachtravel.module.my;

import android.os.Bundle;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by rjm on 2015/3/26.
 */
public class FootprintActivity extends PeachBaseActivity {
    @InjectView(R.id.footprint_map)
    MapView footprintMap;
    private AMap aMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_footprint);
        ButterKnife.inject(this);
        footprintMap.onCreate(savedInstanceState);
        init();
    }
    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = footprintMap.getMap();
        }
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(34.341568, 108.940174));
        markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
        markerOption.draggable(true);
        markerOption.icon(BitmapDescriptorFactory.defaultMarker());
        aMap.addMarker(markerOption);
        // 添加带有系统默认icon的marker
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        footprintMap.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        footprintMap.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        footprintMap.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        footprintMap.onDestroy();
    }
}
