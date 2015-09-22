package com.xuejian.client.lxp.module.dest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
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
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.my.MyFootPrinterActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yibiao.qin on 2015/9/17.
 */
public class StrategyDomesticMapActivity extends PeachBaseActivity implements AMap.OnMarkerClickListener,
        AMap.OnInfoWindowClickListener, AMap.OnMapLoadedListener, AMapLocationListener {
    @InjectView(R.id.map_title_back)
    ImageView mapTitleBack;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.tv_subtitle)
    TextView tvSubtitle;
    @InjectView(R.id.map_more)
    ImageView mapMore;
    @InjectView(R.id.map_title_bar)
    RelativeLayout mapTitleBar;
    @InjectView(R.id.map_days_name_list)
    HorizontalScrollView mapDaysNameList;
    @InjectView(R.id.strategy_map_locations)
    RelativeLayout strategyMapLocations;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.iv_plan_index)
    ImageView plan_index;
    @InjectView(R.id.tv_plan_index_title)
    TextView tvPlanIndexTitle;
    @InjectView(R.id.plan_index)
    ListView planIndex;
    @InjectView(R.id.iv_my_location)
    ImageView myLocation;
    private ArrayList<StrategyBean> allBeans;
    private MarkerOptions markerOption;
    private LinearLayout layout;
    private long POLY_LINE = 1;
    boolean isShow = false;
    private boolean isExpertFootPrint, isMyFootPrint, isAllPoiLoc;
    private ArrayList<LocBean> all_print_print;
    private List<LocBean> my_footprint = new ArrayList<LocBean>();
    private ArrayList<PoiDetailBean> all_place_loc = new ArrayList<PoiDetailBean>();
    private ArrayList<double[]> coords = new ArrayList<double[]>();
    private String allDesString;
    private int SET_FOOTPRINT = 200;
    MapView mapView;
    AMap aMap;
    Marker currentMarker;
    LocationManagerProxy mLocationManagerProxy;
    private static final String OSM_URL = "http://a.tile.openstreetmap.org/%d/%d/%d.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.domestic_map);
        ButterKnife.inject(this);
        if (SharePrefUtil.getBoolean(mContext, "isAbroad", false))
            MapsInitializer.replaceURL(OSM_URL, "OSM");
        allBeans = getIntent().getParcelableArrayListExtra("strategy");
        isExpertFootPrint = getIntent().getBooleanExtra("isExpertFootPrint", false);
        isMyFootPrint = getIntent().getBooleanExtra("isMyFootPrint", false);
        isAllPoiLoc = getIntent().getBooleanExtra("isAllPoiLoc", false);
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMyFootPrint) {
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra("footprint", all_print_print);
                    setResult(RESULT_OK, intent);
                }
                finish();
                overridePendingTransition(0, R.anim.slide_out_to_right);
            }
        });
        plan_index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);//关闭抽屉
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);//打开抽屉
                }
            }
        });
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location();
            }
        });
        init();
        initData();

    }

    private void location() {
        mLocationManagerProxy = LocationManagerProxy.getInstance(mContext);
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, -1, 15, this);
        mLocationManagerProxy.setGpsEnable(false);
    }


    private void initData() {
        if (isMyFootPrint) {
            mapMore.setVisibility(View.VISIBLE);
            plan_index.setVisibility(View.GONE);
            tvTitle.setText(getIntent().getStringExtra("title"));
            tvSubtitle.setText("足迹");
            mapMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tracks_intent = new Intent(mContext, MyFootPrinterActivity.class);
                    tracks_intent.putExtra("myfootprint", (Serializable) my_footprint);
                    tracks_intent.putExtra("title", tvTitle.getText().toString());
                    tracks_intent.putExtra("isOwner", true);
                    startActivityForResult(tracks_intent, SET_FOOTPRINT);
                }
            });
            layout = new LinearLayout(mContext);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LocalDisplay.dp2px(50)));
            layout.setGravity(Gravity.CENTER_VERTICAL);
            initMyPrint(AccountManager.getCurrentUserId());
        } else if (isExpertFootPrint) {
            mapMore.setVisibility(View.GONE);
            plan_index.setVisibility(View.GONE);
            tvTitle.setText(getIntent().getStringExtra("title"));
            tvSubtitle.setText("足迹");
            layout = new LinearLayout(mContext);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LocalDisplay.dp2px(50)));
            layout.setGravity(Gravity.CENTER_VERTICAL);
            String id = getIntent().getStringExtra("id");
            initMyPrint(id);
        } else if (isAllPoiLoc) {
            mapMore.setVisibility(View.GONE);
            plan_index.setVisibility(View.GONE);
            tvTitle.setText(getIntent().getStringExtra("title"));
            tvSubtitle.setText("位置");
            all_place_loc = getIntent().getParcelableArrayListExtra("allLoadLocList");
            layout = new LinearLayout(mContext);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LocalDisplay.dp2px(50)));
            layout.setGravity(Gravity.CENTER_VERTICAL);
            loadPlaceLoc(all_place_loc);
        } else {
            mapMore.setVisibility(View.GONE);
            plan_index.setVisibility(View.VISIBLE);
            tvTitle.setText(getIntent().getStringExtra("title"));
            tvSubtitle.setText("位置");
            layout = new LinearLayout(mContext);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LocalDisplay.dp2px(50)));
            layout.setGravity(Gravity.CENTER_VERTICAL);
            loadPlanData(0);
        }
    }

    private void loadPlanData(int pos) {
        List<LatLng> latLngs = new ArrayList<>();
        mapDaysNameList.removeAllViews();
        layout.removeAllViews();
        aMap.clear();
        int flag = 1;
        allDesString = "";
        if (allBeans.get(0).itinerary.size() == 0) {
            allDesString = "无";
        }
        planIndex.setAdapter(new DrawAdapter(mContext, allBeans.get(0).itineraryDays));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        boolean show = true;
        for (int i = 0; i < allBeans.get(0).itinerary.size(); i++) {
            if (allBeans.get(0).itinerary.get(i).dayIndex == pos) {
                View view = View.inflate(mContext, R.layout.strategy_map_locations_item, null);
                TextView location = (TextView) view.findViewById(R.id.map_places);
                ImageView r_arrow = (ImageView) view.findViewById(R.id.iv_arrow);
                if (!show) {
                    r_arrow.setVisibility(View.VISIBLE);
                } else r_arrow.setVisibility(View.GONE);
                show = false;
                location.setText(flag + "." + allBeans.get(0).itinerary.get(i).poi.zhName);
                final int position = i;
                LatLng latLng = new LatLng(allBeans.get(0).itinerary.get(i).poi.location.coordinates[1], allBeans.get(0).itinerary.get(i).poi.location.coordinates[0]);
                latLngs.add(latLng);
                builder.include(latLng);
                aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                        .position(latLng).title(allBeans.get(0).itinerary.get(i).poi.zhName)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker)).draggable(true));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(allBeans.get(0).itinerary.get(position).poi.location.coordinates[1], allBeans.get(0).itinerary.get(position).poi.location.coordinates[0]), 14.1f));
                    }
                });
                layout.addView(view);
                if (flag == 1) {
                    allDesString = allBeans.get(0).itinerary.get(0).poi.zhName;
                } else {
                    allDesString = String.format("%s>%s", allDesString, allBeans.get(0).itinerary.get(i).poi.zhName);
                }
                flag++;
            }
        }
        tvTitle.setText(String.format("第%d天", pos + 1));
        tvSubtitle.setText(allDesString);
        mapDaysNameList.addView(layout);
        aMap.addPolyline(new PolylineOptions().addAll(latLngs).color(getResources().getColor(R.color.app_theme_color)));
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));
    }

    private void loadPlaceLoc(final ArrayList<PoiDetailBean> beans) {
        mapDaysNameList.removeAllViews();
        layout.removeAllViews();
        coords.clear();
        aMap.clear();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int k = 0; k < beans.size(); k++) {
            builder.include(new LatLng(beans.get(k).location.coordinates[1], beans.get(k).location.coordinates[0]));
            aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .position(new LatLng(beans.get(k).location.coordinates[1], beans.get(k).location.coordinates[0])).title(beans.get(k).zhName)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker)).draggable(true));
            View view = View.inflate(mContext, R.layout.strategy_map_locations_item, null);
            CheckedTextView location = (CheckedTextView) view.findViewById(R.id.map_places);
            location.setText(beans.get(k).zhName);
            final int pos = k;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(beans.get(pos).location.coordinates[1], beans.get(pos).location.coordinates[0]), 10.1f));
                }
            });
            layout.addView(view);
            coords.add(beans.get(k).location.coordinates);
        }
        mapDaysNameList.addView(layout);
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));
    }

    private void initMyPrint(String id) {
        try {
            DialogManager.getInstance().showLoadingDialog(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UserApi.getUserFootPrint(id, new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                CommonJson4List<LocBean> locs = CommonJson4List.fromJson(result.toString(), LocBean.class);
                if (locs.code == 0) {
                    DialogManager.getInstance().dissMissLoadingDialog();
                    my_footprint.addAll(locs.result);
                    loadExpertFootPrintMap(my_footprint);
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                ToastUtil.getInstance(mContext).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {
                DialogManager.getInstance().dissMissLoadingDialog();
                ToastUtil.getInstance(mContext).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }

    private void loadExpertFootPrintMap(final List<LocBean> my_footprint) {
        mapDaysNameList.removeAllViews();
        layout.removeAllViews();
        coords.clear();
        aMap.clear();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int k = 0; k < my_footprint.size(); k++) {
            builder.include(new LatLng(my_footprint.get(k).location.coordinates[1], my_footprint.get(k).location.coordinates[0]));
            aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .position(new LatLng(my_footprint.get(k).location.coordinates[1], my_footprint.get(k).location.coordinates[0])).title(my_footprint.get(k).zhName)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker)).draggable(true));
            View view = View.inflate(mContext, R.layout.strategy_map_locations_item, null);
            CheckedTextView location = (CheckedTextView) view.findViewById(R.id.map_places);
            location.setText(my_footprint.get(k).zhName);
            final int pos = k;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(my_footprint.get(pos).location.coordinates[1], my_footprint.get(pos).location.coordinates[0]), 10.1f));
                }
            });
            layout.addView(view);
            coords.add(my_footprint.get(k).location.coordinates);
        }
        mapDaysNameList.addView(layout);
        if (my_footprint.size() > 0)
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(my_footprint.get(0).location.coordinates[1], my_footprint.get(0).location.coordinates[0]), 1f));
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
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
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapLoaded() {
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker.getPosition()));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14.1f));
        return false;
    }

    public void updateUserinfo() {
        UserApi.getUserInfo(AccountManager.getCurrentUserId(), new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                CommonJson<User> Info = CommonJson.fromJson(result.toString(), User.class);
                if (Info.code == 0) {
                    AccountManager.getInstance().setLoginAccountInfo(Info.result);
                    tvTitle.setText(Info.result.getCountryCnt() + "国" + Info.result.getTrackCnt() + "城市");
                   /* MyFragment myFragment = (MyFragment) getSupportFragmentManager().findFragmentByTag("My");
                    if (myFragment != null) {
                        myFragment.refreshLoginStatus();
                    }*/
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SET_FOOTPRINT) {
                //ArrayList<LocBean> result=new ArrayList<LocBean>();
                all_print_print = data.getParcelableArrayListExtra("footprint");
                updateUserinfo();
                loadExpertFootPrintMap(all_print_print);
            }
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
            if (currentMarker != null) currentMarker.remove();
            currentMarker = aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_my_location))
                    .position(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())).snippet(aMapLocation.getAddress()));
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()), 17.5f));
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

    private class DrawAdapter extends BaseAdapter {
        private TextView place;
        private Context drawContext;
        private int count;

        public DrawAdapter(Context context, int count) {
            drawContext = context;
            this.count = count;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(drawContext, R.layout.strategy_draw_list_cell, null);
            }
            place = (TextView) convertView.findViewById(R.id.user_been_place);
            place.setText(String.format("DAY%d", position + 1));
            place.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                    new Handler() {
                        public void handleMessage(Message msg) {
                            loadPlanData(position);
                        }
                    }.sendEmptyMessageDelayed(0, 300);
                }
            });
            return convertView;
        }
    }
}
