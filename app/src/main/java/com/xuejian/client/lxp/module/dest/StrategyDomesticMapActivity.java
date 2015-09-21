package com.xuejian.client.lxp.module.dest;

import android.os.Bundle;
import android.view.View;

import com.aizou.core.utils.SharePrefUtil;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.MapsInitializer;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yibiao.qin on 2015/9/17.
 */
public class StrategyDomesticMapActivity extends PeachBaseActivity implements AMap.OnMarkerClickListener,
        AMap.OnInfoWindowClickListener, AMap.OnMapLoadedListener,
        View.OnClickListener{
    MapView mapView;
    AMap aMap;
    private static final String OSM_URL = "http://a.tile.openstreetmap.org/%d/%d/%d.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.domestic_map);
        if (SharePrefUtil.getBoolean(mContext, "isAbroad", false))MapsInitializer.replaceURL(OSM_URL, "OSM");
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setupMap();
        }
    }

    private void setupMap() {
        aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
        addMarkersToMap();// 往地图上添加marker
        drawLine();
    }

    private void drawLine() {
        List<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(39.983456, 116.3154950));
        latLngs.add(new LatLng(34.341568, 108.940174));
        latLngs.add(new LatLng(30.679879, 104.064855));
        latLngs.add(new LatLng(53.001000, 103.480001));
        aMap.addPolyline(new PolylineOptions().addAll(latLngs).color(getResources().getColor(R.color.app_theme_color)));
    }

    private void addMarkersToMap() {
        aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .position(new LatLng(39.983456, 116.3154950)).title("北京市")
                .snippet("北京市:30.679879, 104.064855").draggable(true));

        MarkerOptions options = new MarkerOptions();
        options.title("成都市").snippet("成都市经纬度");
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_boy));
        options.position(new LatLng(30.679879, 104.064855));
        aMap.addMarker(options);

        aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .position(new LatLng(34.341568, 108.940174)).title("西安市")
                .snippet("西安市经纬度").draggable(true));

        aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .position(new LatLng(53.001001, 103.480001)).title("安市")
                .snippet("经纬度").draggable(true));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapLoaded() {
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(new LatLng(30.679879, 104.064855)).include(new LatLng(39.983456, 116.3154950))
                .include(new LatLng(34.341568, 108.940174)).include(new LatLng(31.238068, 121.501654)).build();
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker.getPosition()));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        return false;
    }
}
