/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xuejian.client.lxp.module.toolbox.im;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.amap.api.location.core.GeoPoint;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.lv.Utils.Config;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.ChatBaseActivity;

import java.io.File;
import java.io.FileOutputStream;

public class BaiduMapActivity extends ChatBaseActivity {

    private final static String TAG = "map";
    static MapView mMapView = null;
    private BaiduMap mBaiduMap;
    //private MapController mMapController = null;
    //public MKMapViewListener mMapListener = null;
    FrameLayout mMapViewContainer = null;
    MyLocationListenner myListener = new MyLocationListenner();
    // 定位相关
    LocationClient mLocClient;
    Button sendButton = null;
    static BDLocation lastLocation = null;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    public static BaiduMapActivity instance = null;
    private String mapPath;
    ProgressDialog progressDialog;
    private boolean isFirstLoc = true;
    public static final String strKey = "3AB1810EBAAE0175EB41A744CF3B2D6497407B87";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplication());
        setContentView(R.layout.activity_baidumap);
        mMapView = (MapView) findViewById(R.id.bmapView);
        sendButton = (Button) findViewById(R.id.btn_location_send);
        mapPath = Config.mapPath + "map_" + System.currentTimeMillis() + ".png";
        initMapView();
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);
        if (latitude == 0) {
            location();
        } else {
            double longtitude = intent.getDoubleExtra("longitude", 0);
            showMap(latitude, longtitude, null);
        }
    }

    private Rect getScreenSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Rect rect = new Rect();
        rect.set((dm.widthPixels / 4), (dm.heightPixels / 4), (dm.widthPixels / 4 * 3), (dm.heightPixels / 4 * 3));
        return rect;
    }

    public void location() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在确定你的位置...");

        progressDialog.setOnCancelListener(new OnCancelListener() {

            public void onCancel(DialogInterface arg0) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                finish();
            }
        });

        progressDialog.show();

        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_marka);
        mBaiduMap = mMapView.getMap();
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        mBaiduMap
                .setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker));
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setAddrType("all");
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    public void back(View v) {
        finish();
    }

    public void sendLocation(View view) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("发送中...");
        Rect rect = getScreenSize();
        mBaiduMap.snapshotScope(rect, new BaiduMap.SnapshotReadyCallback() {
            public void onSnapshotReady(Bitmap snapshot) {
                String path = Config.mapPath;
                File path1 = new File(path);
                if (!path1.exists()) path1.mkdirs();
                File file = new File(mapPath);
                FileOutputStream out;
                try {
                    out = new FileOutputStream(file);
                    if (snapshot.compress(
                            Bitmap.CompressFormat.PNG, 100, out)) {
                        out.flush();
                        out.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.putExtra("latitude", lastLocation.getLatitude());
                        intent.putExtra("longitude", lastLocation.getLongitude());
                        intent.putExtra("address", lastLocation.getAddrStr());
                        intent.putExtra("path", mapPath);
                        progressDialog.dismiss();
                        BaiduMapActivity.this.setResult(RESULT_OK, intent);
                        finish();
                    }
                });

            }
        });

//		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            progressDialog.dismiss();
            if (lastLocation != null) {
                if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude()) {
                    Log.d("map", "same location, skip refresh");
                    // mMapView.refresh(); //need this refresh?
                    return;
                }
            }
            lastLocation = location;
            sendButton.setEnabled(true);
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(0).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 19);
                mBaiduMap.animateMapStatus(u);

            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    public void showMap(double latitude, double longtitude, String address) {
        sendButton.setVisibility(View.GONE);
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_marka);
        mBaiduMap = mMapView.getMap();
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        mBaiduMap
                .setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker));
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(100)
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(0).latitude(latitude)
                .longitude(longtitude).build();
        mBaiduMap.setMyLocationData(locData);
        LatLng ll = new LatLng(latitude,
                longtitude);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 19);
        mBaiduMap.animateMapStatus(u);
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        if (mLocClient != null) {
            mLocClient.stop();
        }
        super.onPause();
        lastLocation = null;
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        if (mLocClient != null) {
            mLocClient.start();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mLocClient != null) mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    private void initMapView() {
        mMapView.setLongClickable(true);
        mBaiduMap = mMapView.getMap();
//普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
    }

    static final double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

    public static GeoPoint gcjToBaidu(double lat, double lng) {
        double x = lng, y = lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        double bdLng = z * Math.cos(theta) + 0.0065;
        double bdLat = z * Math.sin(theta) + 0.006;
        return new GeoPoint((int) (bdLat * 1e6), (int) (bdLng * 1e6));
    }

    public static GeoPoint baiduToGcj(double lat, double lng) {
        double x = lng - 0.0065, y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        double gcjLng = z * Math.cos(theta);
        double gcjLat = z * Math.sin(theta);
        return new GeoPoint((int) (gcjLat * 1e6), (int) (gcjLng * 1e6));
    }
}
