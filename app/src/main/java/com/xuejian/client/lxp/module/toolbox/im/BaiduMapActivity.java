/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

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
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
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
	MyLocationListenner myListener=new MyLocationListenner();
	// 定位相关
	LocationClient mLocClient;
	Button sendButton = null;
	static BDLocation lastLocation = null;
	private MyLocationConfiguration.LocationMode mCurrentMode;
	public static BaiduMapActivity instance = null;
	private String mapPath;
	ProgressDialog progressDialog;
	private boolean isFirstLoc=true;
	public static final String strKey = "3AB1810EBAAE0175EB41A744CF3B2D6497407B87";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplication());
		setContentView(R.layout.activity_baidumap);
		mMapView = (MapView) findViewById(R.id.bmapView);
		//mMapController = mMapView.getController();
		sendButton = (Button) findViewById(R.id.btn_location_send);
	//	initMapView();

	//	mMapView.getController().setZoom(17);
	//	mMapView.getController().enableClick(true);
	//	mMapView.setBuiltInZoomControls(true);
		initMapView();
		Intent intent = getIntent();
		double latitude = intent.getDoubleExtra("latitude", 0);
		if (latitude == 0) {
			location();
		} else {
			double longtitude = intent.getDoubleExtra("longitude", 0);
			showMap(latitude,longtitude,null);
		}
	}
	public void location(){
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
		Toast.makeText(BaiduMapActivity.this,
				"发送中。。。。",
				Toast.LENGTH_SHORT).show();
		mBaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
			public void onSnapshotReady(Bitmap snapshot) {
				String path= Config.mapPath;
				File path1=new File(path);
				if (!path1.exists())path1.mkdirs();
				mapPath=path+"map_"+System.currentTimeMillis()+".png";
				File file = new File(mapPath);
				FileOutputStream out;
				try {
					out = new FileOutputStream(file);
					if (snapshot.compress(
							Bitmap.CompressFormat.PNG, 50, out)) {
						out.flush();
						out.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		Intent intent = this.getIntent();
		intent.putExtra("latitude", lastLocation.getLatitude());
		intent.putExtra("longitude", lastLocation.getLongitude());
		intent.putExtra("address", lastLocation.getAddrStr());
		intent.putExtra("path", mapPath);
		this.setResult(RESULT_OK, intent);
		finish();
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
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,17);
				mBaiduMap.animateMapStatus(u);

			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	public void showMap(double latitude,double longtitude,String address){
		if (mBaiduMap==null)mBaiduMap=mMapView.getMap();
		LatLng ll = new LatLng(latitude,
				longtitude);
		System.out.println(ll.toString());
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_marka);
		OverlayOptions option = new MarkerOptions()
				.position(ll)
				//	.title(address)
				.icon(bitmap);
		mBaiduMap.addOverlay(option);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
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
		if (mLocClient!=null)mLocClient.stop();
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

	/**
	 * 监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
	 */
//	public class MyLocationListenner implements BDLocationListener {
//		@Override
//		public void onReceiveLocation(BDLocation location) {
//			if (location == null) {
//				return;
//			}
//			sendButton.setEnabled(true);
//			if (progressDialog != null) {
//				progressDialog.dismiss();
//			}
//
//			if (lastLocation != null) {
//				if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude()) {
//					Log.d("map", "same location, skip refresh");
//					// mMapView.refresh(); //need this refresh?
//					return;
//				}
//			}
//
//			lastLocation = location;
//
//			GeoPoint gcj02Point = new GeoPoint((int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6));
//			Log.d(TAG, "GCJ-02 loc:" + gcj02Point);
//			GeoPoint point = CoordinateConvert.fromGcjToBaidu(gcj02Point);
//			Log.d(TAG, "converted BD-09 loc:" + point);
//
//			// GeoPoint p1 = gcjToBaidu(location.getLatitude(),
//			// location.getLongitude());
//			// System.err.println("johnson change to baidu:" + p1);
//			// GeoPoint p2 = baiduToGcj(location.getLatitude(),
//			// location.getLongitude());
//			// System.err.println("johnson change to gcj:" + p2);
//
//			OverlayItem addrItem = new OverlayItem(point, "title", location.getAddrStr());
//			mAddrOverlay.removeAll();
//			mAddrOverlay.addItem(addrItem);
//		//	mMapView.getController().setZoom(17);
//		//	mMapView.refresh();
//			mMapController.animateTo(point);
//		}
//
//		public void onReceivePoi(BDLocation poiLocation) {
//			if (poiLocation == null) {
//				return;
//			}
//		}
//	}
//
//	public class NotifyLister extends BDNotifyListener {
//		public void onNotify(BDLocation mlocation, float distance) {
//		}
//	}


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
