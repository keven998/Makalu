package com.xuejian.client.lxp.module.toolbox.im;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.aizou.core.utils.SharePrefUtil;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.MapsInitializer;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.lv.utils.Config;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by yibiao.qin on 2015/9/17.
 */
public class MapActivity extends PeachBaseActivity implements AMapLocationListener ,AMap.OnMapScreenShotListener{
    MapView mapView;
    AMap aMap;
    Button sendButton;
    LocationManagerProxy mLocationManagerProxy;
    ProgressDialog progressDialog;
    String mapPath;
    double lat;
    double lng;
    String addr;
    private static final String OSM_URL = "http://a.tile.openstreetmap.org/%d/%d/%d.png";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (SharePrefUtil.getBoolean(mContext,"isAbroad",false))MapsInitializer.replaceURL(OSM_URL, "OSM");
        mapView = (MapView) findViewById(R.id.bmapView);
        sendButton = (Button) findViewById(R.id.btn_location_send);
        sendButton.setEnabled(false);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLocation();
            }
        });
        mapView.onCreate(savedInstanceState);
        init();
        mapPath = Config.mapPath + "map_" + System.currentTimeMillis() + ".png";
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);
        if (latitude == 0) {
            location();
        } else {
            sendButton.setVisibility(View.INVISIBLE);
            double longtitude = intent.getDoubleExtra("longitude", 0);
            String address = intent.getStringExtra("address");
            showMap(longtitude, latitude, address);
        }
    }

    private void showMap(double longtitude, double latitude, String addr) {
        aMap.clear();
        aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka))
                .position(new LatLng(latitude,longtitude)).snippet(addr));
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longtitude), 19));

    }

    public void back(View v) {
        finish();
    }

    private void location() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在确定你的位置...");

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            public void onCancel(DialogInterface arg0) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                finish();
            }
        });

        progressDialog.show();
        mLocationManagerProxy = LocationManagerProxy.getInstance(this);
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, 30 * 1000, 15, this);
        mLocationManagerProxy.setGpsEnable(false);
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
    }
    public void getMapScreenShot() {
        // 设置截屏监听接口，截取地图可视区域
        aMap.getMapScreenShot(this);
    }

    /**
     * 截屏回调方法
     */
    @Override
    public void onMapScreenShot(Bitmap bitmap) {
        String path = Config.mapPath;
        File path1 = new File(path);
        if (!path1.exists()) path1.mkdirs();
        File file = new File(mapPath);
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(
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
                intent.putExtra("latitude", lat);
                intent.putExtra("longitude", lng);
                intent.putExtra("address", addr);
                intent.putExtra("path", mapPath);
                progressDialog.dismiss();
                MapActivity.this.setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    public void sendLocation() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("发送中...");
        getMapScreenShot();
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
        if (mLocationManagerProxy!=null){
            mLocationManagerProxy.removeUpdates(this);
            mLocationManagerProxy.destroy();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
            if (lat!=aMapLocation.getLatitude()&&lng!=aMapLocation.getLongitude()){
                if (progressDialog!=null)progressDialog.dismiss();
                lat=aMapLocation.getLatitude();
                lng=aMapLocation.getLongitude();
                addr = aMapLocation.getAddress();
                aMap.clear();
                aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka))
                        .position(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())).snippet(aMapLocation.getAddress()));
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()), 18.5f));
                sendButton.setEnabled(true);
            }

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
}
