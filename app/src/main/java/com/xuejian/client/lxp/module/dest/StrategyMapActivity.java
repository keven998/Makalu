package com.xuejian.client.lxp.module.dest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.airbnb.android.airmapview.AirMapInterface;
import com.airbnb.android.airmapview.AirMapMarker;
import com.airbnb.android.airmapview.AirMapPolyline;
import com.airbnb.android.airmapview.AirMapView;
import com.airbnb.android.airmapview.AirMapViewTypes;
import com.airbnb.android.airmapview.DefaultAirMapViewBuilder;
import com.airbnb.android.airmapview.GoogleChinaMapType;
import com.airbnb.android.airmapview.listeners.OnMapInitializedListener;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.dialog.XDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.my.MyFootPrinterActivity;
import com.xuejian.client.lxp.module.my.MyFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxp_dqm07 on 2015/4/27.
 */
public class StrategyMapActivity extends PeachBaseActivity implements OnMapInitializedListener {
    private AirMapView mapView;
    // private AMap aMap;

    // private TitleHeaderBar titleHeaderBar;
    private HorizontalScrollView all_locations;
    private ArrayList<StrategyBean> allBeans;
    private MarkerOptions markerOption;
    private MapsDayAdapter adapter;
    private ImageView selected;
    private PopupWindow mPop;
    private LinearLayout layout;
    private LatLngBounds bounds;
    com.amap.api.maps2d.model.LatLng latlng;
    private DefaultAirMapViewBuilder mapViewBuilder;
    private AirMapInterface airMapInterface;
    private AirMapPolyline airMapPolyline;
    private long POLY_LINE = 1;
    boolean isShow = false;
    private TextView tv_title, tv_subTitle, tv_select_day;
    private ImageView title_back, map_more;
    private boolean isExpertFootPrint, isMyFootPrint, isAllPoiLoc;
    private ArrayList<LocBean> all_print_print;
    private List<LocBean> my_footprint = new ArrayList<LocBean>();
    private ArrayList<PoiDetailBean> all_place_loc = new ArrayList<PoiDetailBean>();
    private ArrayList<double[]> coords = new ArrayList<double[]>();
    private LinearLayout day_select;
    private String allDesString;
    private int SET_FOOTPRINT = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strategy_map);
        allBeans = getIntent().getParcelableArrayListExtra("strategy");
        isExpertFootPrint = getIntent().getBooleanExtra("isExpertFootPrint", false);
        isMyFootPrint = getIntent().getBooleanExtra("isMyFootPrint", false);
        isAllPoiLoc = getIntent().getBooleanExtra("isAllPoiLoc", false);
        title_back = (ImageView) findViewById(R.id.map_title_back);
        map_more = (ImageView) findViewById(R.id.map_more);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_subTitle = (TextView) findViewById(R.id.tv_subtitle);
        tv_select_day = (TextView) findViewById(R.id.tv_select_day);
        day_select = (LinearLayout) findViewById(R.id.day_select);

        title_back.setOnClickListener(new View.OnClickListener() {
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

        //titleHeaderBar.getTitleTextView().setText("地图");
        if (!isExpertFootPrint && !isMyFootPrint && !isAllPoiLoc) {
            if (allBeans.size() > 0) {
                final int day_sums = allBeans.get(0).itinerary.get(allBeans.get(0).itinerary.size() - 1).dayIndex + 1;
                tv_title.setText("第1天");
                tv_select_day.setText("01");
                day_select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//自定义布局
                        final XDialog xDialog = new XDialog(StrategyMapActivity.this);
                        WindowManager.LayoutParams wlmp = xDialog.getWindow().getAttributes();
                        wlmp.gravity = Gravity.TOP | Gravity.RIGHT;
                        ListView lv = xDialog.getListView();
                        adapter = new MapsDayAdapter(day_sums, DealWithDays(tv_title.getText().toString()));
                        lv.setAdapter(adapter);
                        xDialog.show();
/*
                        ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
                                R.layout.map_day_select, null, true);
                        //TextView pop_dismiss = (TextView) menuView.findViewById(R.id.pop_dismiss);

                        ListView lv = (ListView) menuView.findViewById(R.id.map_days_list);
                        adapter = new MapsDayAdapter(day_sums, DealWithDays(tv_title.getText().toString()));
                        lv.setAdapter(adapter);
                        mPop = new PopupWindow(menuView,FlowLayout.LayoutParams.MATCH_PARENT,
                                FlowLayout.LayoutParams.WRAP_CONTENT, true);
                        mPop.setContentView(menuView);//设置包含视图
                        mPop.setWidth(FlowLayout.LayoutParams.MATCH_PARENT);
                        mPop.setHeight(FlowLayout.LayoutParams.WRAP_CONTENT);
                        mPop.setAnimationStyle(android.R.style.Animation_Dialog);
                        //mPop.showAsDropDown(day_select,-30,20);
                        mPop.setOutsideTouchable(true);
                        mPop.showAtLocation(findViewById(R.id.parent), Gravity.CENTER, 0, 0);*/
                       /* WindowManager.LayoutParams params=StrategyMapActivity.this.getWindow().getAttributes();
                        params.alpha=0.7f;
                        StrategyMapActivity.this.getWindow().setAttributes(params);*/
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                tv_title.setText("第" + (position + 1) + "天");
                                tv_select_day.setText(normalizeNumber(position + 1));
                                initData(position);
                                xDialog.dismiss();
                            }
                        });
                    }
                });
            }
        } else {
            if (isAllPoiLoc) {
                tv_subTitle.setText("位置");
            } else {
                tv_subTitle.setText("足迹");
            }
            tv_title.setText(getIntent().getStringExtra("title"));
        }
        mapView = (AirMapView) findViewById(R.id.strategy_map);
        //mapView.onCreate(savedInstanceState);
        //initMapView();
        all_locations = (HorizontalScrollView) findViewById(R.id.map_days_name_list);
        layout = new LinearLayout(getApplicationContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LocalDisplay.dp2px(50)));
        layout.setGravity(Gravity.CENTER_VERTICAL);
        if (isExpertFootPrint) {
            day_select.setVisibility(View.GONE);
            String id = getIntent().getStringExtra("id");
            initMyPrint(id);
            //loadExpertFootPrintMap(all_print_print);
        } else if (isMyFootPrint) {
            day_select.setVisibility(View.GONE);
            all_print_print = getIntent().getParcelableArrayListExtra("myfootprint");
            initMyPrint(AccountManager.getCurrentUserId());
            map_more.setVisibility(View.VISIBLE);
            map_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent tracks_intent = new Intent(StrategyMapActivity.this, MyFootPrinterActivity.class);
                    tracks_intent.putExtra("myfootprint", (Serializable) my_footprint);
                    tracks_intent.putExtra("title", tv_title.getText().toString());
                    tracks_intent.putExtra("isOwner", true);
                    startActivityForResult(tracks_intent, SET_FOOTPRINT);
                }
            });
        } else if (isAllPoiLoc) {
            day_select.setVisibility(View.GONE);
            all_place_loc = getIntent().getParcelableArrayListExtra("allLoadLocList");
            loadAllPlaceFootPrintMap(all_place_loc);
        } else {
            if (allBeans.size() > 0) {
                initData(0);
            }
        }
    }

    private void initMyPrint(String id) {
        DialogManager.getInstance().showLoadingDialog(this);
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
                ToastUtil.getInstance(StrategyMapActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                loadExpertFootPrintMap(my_footprint);
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {
                DialogManager.getInstance().dissMissLoadingDialog();
                ToastUtil.getInstance(StrategyMapActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                loadExpertFootPrintMap(my_footprint);
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

    public void updateUserinfo() {
        UserApi.getUserInfo(AccountManager.getCurrentUserId(), new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                CommonJson<User> Info = CommonJson.fromJson(result.toString(), User.class);
                if (Info.code == 0) {
                    AccountManager.getInstance().setLoginAccountInfo(Info.result);
                    MyFragment myFragment = (MyFragment) getSupportFragmentManager().findFragmentByTag("My");
                    if (myFragment != null) {
                        myFragment.refreshLoginStatus();
                    }
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

    private String normalizeNumber(int pos) {
        String value;
        if (pos < 10) {
            value = "0" + pos;
        } else if (pos < 100) {
            value = pos + "";
        } else {
            value = "99+";
        }
        return value;
    }

    private void loadAllPlaceFootPrintMap(final ArrayList<PoiDetailBean> footPrint) {
        all_locations.removeAllViews();
        layout.removeAllViews();
        coords.clear();
        for (int k = 0; k < footPrint.size(); k++) {
            View view = View.inflate(StrategyMapActivity.this, R.layout.strategy_map_locations_item, null);
            TextView location = (TextView) view.findViewById(R.id.map_places);
            TextView r_arrow = (TextView) view.findViewById(R.id.right_arrow);
            r_arrow.setVisibility(View.GONE);
            location.setText(footPrint.get(k).zhName);
            final int pos = k;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mapView.animateCenterZoom(new LatLng(footPrint.get(pos).location.coordinates[1]
                            , footPrint.get(pos).location.coordinates[0]), 8);
                }
            });
            layout.addView(view);
            coords.add(footPrint.get(k).location.coordinates);
        }
        all_locations.addView(layout);
        setUpAllPlacePrintMap(coords, footPrint);
    }

    private void setUpAllPlacePrintMap(final ArrayList<double[]> mCoords, final ArrayList<PoiDetailBean> prints) {
        if (mCoords.size() > 0) {
            mapViewBuilder = new DefaultAirMapViewBuilder(this);
            airMapInterface = mapViewBuilder.builder(AirMapViewTypes.WEB).withOptions(new GoogleChinaMapType()).build();
            mapView.setOnMapInitializedListener(new OnMapInitializedListener() {
                @Override
                public void onMapInitialized() {
                    for (int k = 0; k < mCoords.size(); k++) {
                        mapView.addMarker(new AirMapMarker(new LatLng(mCoords.get(k)[1], mCoords.get(k)[0]), k + 1)
                                .setTitle(prints.get(k).zhName));
                    }
                    mapView.animateCenterZoom(new LatLng(mCoords.get(0)[1], mCoords.get(0)[0]), 12);
                }
            });
            mapView.initialize(getSupportFragmentManager(), airMapInterface);
            //mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } else {
            refreshNullMap();
        }
    }


    private void loadExpertFootPrintMap(final List<LocBean> footPrint) {
        all_locations.removeAllViews();
        layout.removeAllViews();
        coords.clear();
        for (int k = 0; k < footPrint.size(); k++) {
            View view = View.inflate(StrategyMapActivity.this, R.layout.strategy_map_locations_item, null);
            TextView location = (TextView) view.findViewById(R.id.map_places);
            TextView r_arrow = (TextView) view.findViewById(R.id.right_arrow);
            r_arrow.setVisibility(View.GONE);
            location.setText(footPrint.get(k).zhName);
            final int pos = k;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mapView.animateCenterZoom(new LatLng(footPrint.get(pos).location.coordinates[1]
                            , footPrint.get(pos).location.coordinates[0]), 8);
                }
            });
            layout.addView(view);
            coords.add(footPrint.get(k).location.coordinates);
        }
        all_locations.addView(layout);
        setUpExpertFootPrintMap(coords, footPrint);
    }


    private void setUpExpertFootPrintMap(final ArrayList<double[]> mCoords, final List<LocBean> prints) {
        if (mCoords.size() > 0) {
            mapViewBuilder = new DefaultAirMapViewBuilder(this);
            airMapInterface = mapViewBuilder.builder(AirMapViewTypes.WEB).withOptions(new GoogleChinaMapType()).build();
            mapView.setOnMapInitializedListener(new OnMapInitializedListener() {
                @Override
                public void onMapInitialized() {
                    for (int k = 0; k < mCoords.size(); k++) {
                        mapView.addMarker(new AirMapMarker(new LatLng(mCoords.get(k)[1], mCoords.get(k)[0]), k + 1)
                                .setTitle(prints.get(k).zhName));
                    }
                    mapView.animateCenterZoom(new LatLng(mCoords.get(0)[1], mCoords.get(0)[0]), 2);
                }
            });
            mapView.initialize(getSupportFragmentManager(), airMapInterface);
            //mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } else {
            refreshNullMap();
        }
    }

    private void initData(int pos) {
        if (mapView != null) {
            mapView.removeAllViews();
        }
        ArrayList<double[]> coordinates = new ArrayList<double[]>();
        ArrayList<String> names = new ArrayList<String>();
        all_locations.removeAllViews();
        layout.removeAllViews();
        int flag = 1;
        allDesString = "";
        if (allBeans.get(0).itinerary.size() == 0) {
            allDesString = "无";
        }
        for (int i = 0; i < allBeans.get(0).itinerary.size(); i++) {
            if (allBeans.get(0).itinerary.get(i).dayIndex == pos) {
                View view = View.inflate(StrategyMapActivity.this, R.layout.strategy_map_locations_item, null);
                TextView location = (TextView) view.findViewById(R.id.map_places);
                TextView r_arrow = (TextView) view.findViewById(R.id.right_arrow);
                location.setText(flag + " " + allBeans.get(0).itinerary.get(i).poi.zhName);
                final int position = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mapView.animateCenterZoom(new LatLng(allBeans.get(0).itinerary.get(position).poi.location.coordinates[1]
                                , allBeans.get(0).itinerary.get(position).poi.location.coordinates[0]), 15);
                    }
                });
                layout.addView(view);
                coordinates.add(allBeans.get(0).itinerary.get(i).poi.location.coordinates);
                names.add(allBeans.get(0).itinerary.get(i).poi.zhName);
                if (flag == 1) {
                    allDesString = allBeans.get(0).itinerary.get(0).poi.zhName;
                    r_arrow.setVisibility(View.GONE);
                } else {
                    allDesString = String.format("%s>%s", allDesString, allBeans.get(0).itinerary.get(i).poi.zhName);
                }
                flag++;
            }
        }
        tv_subTitle.setText(allDesString);
        all_locations.addView(layout);
        ArrayList<com.amap.api.maps2d.model.LatLng> mLatLngs = new ArrayList<>();
        for (int j = 0; j < coordinates.size(); j++) {
            mLatLngs.add(new com.amap.api.maps2d.model.LatLng(coordinates.get(j)[1], coordinates.get(j)[0]));
        }
        int size = mLatLngs.size();
        com.amap.api.maps2d.model.LatLng[] mLatLngsArr = new com.amap.api.maps2d.model.LatLng[size];
        for (int j = 0; j < size; j++) {
            mLatLngsArr[j] = mLatLngs.get(j);
        }
        setUpMap(names, coordinates, pos);
    }


    private void setUpMap(final ArrayList<String> names, final ArrayList<double[]> coor, int pos) {
        if (coor.size() > 0) {
            final List<LatLng> points = new ArrayList<LatLng>();

            mapViewBuilder = new DefaultAirMapViewBuilder(this);
            airMapInterface = mapViewBuilder.builder(AirMapViewTypes.WEB).withOptions(new GoogleChinaMapType()).build();
            // airMapInterface = mapViewBuilder.builder(AirMapViewTypes.WEB).build();
            mapView.setOnMapInitializedListener(new OnMapInitializedListener() {
                @Override
                public void onMapInitialized() {
                    for (int k = 0; k < names.size(); k++) {
                        mapView.addMarker(new AirMapMarker(new LatLng(coor.get(k)[1], coor.get(k)[0]), k + 1)
                                .setTitle(names.get(k)));
                        points.add(new LatLng(coor.get(k)[1], coor.get(k)[0]));
                    }
                    airMapPolyline = new AirMapPolyline(points, POLY_LINE);
                    mapView.addPolyline(airMapPolyline);
                    mapView.animateCenterZoom(new LatLng(coor.get(0)[1], coor.get(0)[0]), 13);
                }
            });
            mapView.initialize(getSupportFragmentManager(), airMapInterface);
            //mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            //aMap.addPolyline((new PolylineOptions()).add(latLngs).width(5).color(Color.RED));

/* aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                        .position(new LatLng(coor.get(k)[1], coor.get(k)[0])).title(names.get(k))
                        .draggable(true));*//*

            */
/*com.amap.api.maps2d.model.LatLngBounds.Builder llBound=new com.amap.api.maps2d.model.LatLngBounds.Builder();
            for(com.amap.api.maps2d.model.LatLng ll:latLngs){
                llBound.include(ll);
            }
            bounds=llBound.build();*/

        } else {
            refreshNullMap();
        }
        //  aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
    }


    private void refreshNullMap() {
        mapViewBuilder = new DefaultAirMapViewBuilder(this);
        airMapInterface = mapViewBuilder.builder(AirMapViewTypes.WEB).withOptions(new GoogleChinaMapType()).build();
        mapView.setOnMapInitializedListener(new OnMapInitializedListener() {
            @Override
            public void onMapInitialized() {
                mapView.animateCenterZoom(new LatLng(39.969654, 116.393525), 10);
            }
        });
        mapView.initialize(getSupportFragmentManager(), airMapInterface);
        // mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }


    private int DealWithDays(String days) {
        String[] deal1 = days.split("第");
        String[] deal2 = deal1[1].split("天");
        int value = Integer.valueOf(deal2[0]);
        return value;
    }


    @Override
    public void onMapInitialized() {
    }

    private void addMarker(String title, LatLng latLng) {
        mapView.addMarker(new AirMapMarker(latLng, 1)
                .setTitle(title));
    }




/* private void initMapView() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
    }*/


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("page_profile_tracks");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageStart("page_profile_tracks");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // mapView.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
    }


    public class MapsDayAdapter extends BaseAdapter {

        int days, whichDay;


        public MapsDayAdapter(int days, int whichDay) {
            super();
            this.days = days;
            this.whichDay = whichDay;
        }


        @Override
        public int getCount() {
            return days;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = View.inflate(StrategyMapActivity.this, R.layout.maps_days_cell, null);

            }
            TextView days_title = (TextView) convertView.findViewById(R.id.days_title);
            days_title.setText(normalizeNumber(position + 1) + ".Day");
            //selected=(ImageView) convertView.findViewById(R.id.map_days_selected);
            //if(position==(whichDay-1)){selected.setVisibility(View.VISIBLE);}else{selected.setVisibility(View.GONE);}
            return convertView;
        }
    }
}
