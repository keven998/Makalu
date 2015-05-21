package com.aizou.peachtravel.module.dest;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.android.airmapview.AirMapInterface;
import com.airbnb.android.airmapview.AirMapMarker;
import com.airbnb.android.airmapview.AirMapPolyline;
import com.airbnb.android.airmapview.AirMapView;
import com.airbnb.android.airmapview.AirMapViewTypes;
import com.airbnb.android.airmapview.DefaultAirMapViewBuilder;
import com.airbnb.android.airmapview.GoogleChinaMapType;
import com.airbnb.android.airmapview.listeners.OnInfoWindowClickListener;
import com.airbnb.android.airmapview.listeners.OnMapInitializedListener;
import com.airbnb.android.airmapview.listeners.OnMapMarkerClickListener;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.StrategyBean;
import com.aizou.peachtravel.common.widget.FlowLayout;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.toolbox.im.ImageGridActivity;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import butterknife.InjectView;

/**
 * Created by lxp_dqm07 on 2015/4/27.
 */
public class StrategyMapActivity extends PeachBaseActivity implements OnMapInitializedListener,OnMapMarkerClickListener
           ,OnInfoWindowClickListener{
    private AirMapView mapView;
   // private AMap aMap;

    private TitleHeaderBar titleHeaderBar;
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
    boolean isShow=false;
    private TextView spinnet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strategy_map);
        allBeans=getIntent().getParcelableArrayListExtra("strategy");
        final int day_sums=allBeans.get(0).itinerary.get(allBeans.get(0).itinerary.size()-1).dayIndex+1;
        titleHeaderBar = (TitleHeaderBar) findViewById(R.id.map_title_bar);
        titleHeaderBar.getRightTextView().setText("确定");
        titleHeaderBar.getLeftTextView().setText("第1天");
        titleHeaderBar.getLeftTextView().setTextColor(getResources().getColor(R.color.app_theme_color));
        titleHeaderBar.getTitleTextView().setText("地图");
        titleHeaderBar.setLeftDrawableToNull();
        titleHeaderBar.enableBackKey(true);
        titleHeaderBar.findViewById(R.id.ly_title_bar_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, R.anim.slide_out_to_right);
            }
        });
        titleHeaderBar.findViewById(R.id.ly_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//自定义布局
                ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
                        R.layout.map_day_select, null, true);
                TextView pop_dismiss=(TextView)menuView.findViewById(R.id.pop_dismiss);

                ListView lv=(ListView)menuView.findViewById(R.id.map_days_list);
                adapter=new MapsDayAdapter(day_sums,DealWithDays(titleHeaderBar.getLeftTextView().getText().toString()));
                lv.setAdapter(adapter);
                mPop = new PopupWindow(menuView, FlowLayout.LayoutParams.MATCH_PARENT,
                        FlowLayout.LayoutParams.MATCH_PARENT, true);
                mPop.setContentView(menuView );//设置包含视图
                mPop.setWidth(FlowLayout.LayoutParams.MATCH_PARENT);
                mPop.setHeight(FlowLayout.LayoutParams.MATCH_PARENT);
                mPop.setAnimationStyle(R.style.PopAnimation);
                mPop.showAtLocation(findViewById(R.id.parent), Gravity.BOTTOM,0,0);
                pop_dismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPop.dismiss();
                    }
                });
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        titleHeaderBar.getLeftTextView().setText("第"+(position+1)+"天");
                        initData(position);
                        mPop.dismiss();
                    }
                });
            }
        });
        mapView = (AirMapView) findViewById(R.id.strategy_map);
        spinnet = (TextView) findViewById(R.id.spinnet);
        //mapView.onCreate(savedInstanceState);
        //initMapView();
        all_locations = (HorizontalScrollView) findViewById(R.id.map_days_name_list);
        layout=new LinearLayout(getApplicationContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LocalDisplay.dp2px(50)));
        layout.setGravity(Gravity.CENTER_VERTICAL);
        initData(0);
    }

    private void initData(int pos){
        if(mapView!=null){
            mapView.removeAllViews();
        }
        ArrayList<double[]> coordinates=new ArrayList<double[]>();
        ArrayList<String> names=new ArrayList<String>();
        all_locations.removeAllViews();
        layout.removeAllViews();
        int flag=1;
        for(int i=0;i<allBeans.get(0).itinerary.size();i++){
            if(allBeans.get(0).itinerary.get(i).dayIndex==pos){
                View view=View.inflate(StrategyMapActivity.this,R.layout.strategy_map_locations_item,null);
                TextView location=(TextView)view.findViewById(R.id.map_places);
                location.setText(flag+" "+allBeans.get(0).itinerary.get(i).poi.zhName);
                layout.addView(view);
                coordinates.add(allBeans.get(0).itinerary.get(i).poi.location.coordinates);
                names.add(allBeans.get(0).itinerary.get(i).poi.zhName);
                flag++;
            }
        }
        all_locations.addView(layout);
        ArrayList<com.amap.api.maps2d.model.LatLng> mLatLngs =new ArrayList<>();
        for(int j=0;j<coordinates.size();j++){
            mLatLngs.add(new com.amap.api.maps2d.model.LatLng(coordinates.get(j)[1],coordinates.get(j)[0]));
        }
        int size=mLatLngs.size();
        com.amap.api.maps2d.model.LatLng[] mLatLngsArr = new  com.amap.api.maps2d.model.LatLng[size];
        for(int j=0;j<size;j++){
            mLatLngsArr[j]=mLatLngs.get(j);
        }
        setUpMap(names,coordinates, pos);
    }



    private void setUpMap(final ArrayList<String> names, final ArrayList<double[]> coor,int pos){
        if(coor.size()>0) {
            final List<LatLng> points=new ArrayList<LatLng>();

            mapViewBuilder = new DefaultAirMapViewBuilder(this);
            airMapInterface = mapViewBuilder.builder(AirMapViewTypes.WEB).withOptions(new GoogleChinaMapType()).build();
            mapView.setOnMapInitializedListener(new OnMapInitializedListener() {
                @Override
                public void onMapInitialized() {
                    for(int k=0;k<names.size();k++){
                        mapView.addMarker(new AirMapMarker(new LatLng(coor.get(k)[1], coor.get(k)[0]), k+1)
                                .setTitle(names.get(k)));
                        points.add(new LatLng(coor.get(k)[1], coor.get(k)[0]));
                    }
                    airMapPolyline=new AirMapPolyline(points,POLY_LINE);
                    mapView.addPolyline(airMapPolyline);
                    mapView.animateCenterZoom(new LatLng(coor.get(0)[1], coor.get(0)[0]), 10);
                }
            });
            mapView.initialize(getSupportFragmentManager(), airMapInterface);
            mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            //aMap.addPolyline((new PolylineOptions()).add(latLngs).width(5).color(Color.RED));
            /* aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                        .position(new LatLng(coor.get(k)[1], coor.get(k)[0])).title(names.get(k))
                        .draggable(true));*/
            /*com.amap.api.maps2d.model.LatLngBounds.Builder llBound=new com.amap.api.maps2d.model.LatLngBounds.Builder();
            for(com.amap.api.maps2d.model.LatLng ll:latLngs){
                llBound.include(ll);
            }
            bounds=llBound.build();*/
        }else{
            refreshNullMap();
        }
      //  aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
    }


    private void refreshNullMap(){
        mapViewBuilder = new DefaultAirMapViewBuilder(this);
        airMapInterface = mapViewBuilder.builder(AirMapViewTypes.WEB).withOptions(new GoogleChinaMapType()).build();
        mapView.setOnMapInitializedListener(new OnMapInitializedListener() {
            @Override
            public void onMapInitialized() {
                mapView.animateCenterZoom(new LatLng(20,20), 20);
            }
        });
        mapView.initialize(getSupportFragmentManager(), airMapInterface);
        mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }


    private int DealWithDays(String days){
        String[] deal1=days.split("第");
        String[] deal2=deal1[1].split("天");
        int value=Integer.valueOf(deal2[0]);
        return value;
    }


    @Override public void onMapInitialized() {
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
       // mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
       // mapView.onPause();
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

    @Override
    public void onMapMarkerClick(com.google.android.gms.maps.model.Marker marker) {
        if(!isShow){
            ToastUtil.getInstance(this).showToast(marker.getTitle());
            spinnet.setText(marker.getTitle());
            spinnet.setVisibility(View.VISIBLE);
            isShow=true;
        }else{
            isShow=false;
            spinnet.setText("");
            spinnet.setVisibility(View.GONE);
        }
    }

    @Override
    public void onInfoWindowClick(long l) {

    }

    @Override
    public void onInfoWindowClick(com.google.android.gms.maps.model.Marker marker) {
        ToastUtil.getInstance(this).showToast("123");
    }

    @Override
    public void onMapMarkerClick(long l) {
        if(!isShow){
            ToastUtil.getInstance(this).showToast("hfhd");

            isShow=true;
        }else{
            isShow=false;

        }
    }




    public class MapsDayAdapter extends BaseAdapter {

        int days,whichDay;


        public MapsDayAdapter(int days,int whichDay){
            super();
            this.days=days;
            this.whichDay=whichDay;
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

            if(convertView==null){
                 convertView=View.inflate(StrategyMapActivity.this,R.layout.maps_days_cell,null);

            }
            TextView days_title=(TextView)convertView.findViewById(R.id.days_title);
            days_title.setText("第"+(position+1)+"天");
            selected=(ImageView) convertView.findViewById(R.id.map_days_selected);
            if(position==(whichDay-1)){selected.setVisibility(View.VISIBLE);}else{selected.setVisibility(View.GONE);}
            return convertView;
        }
    }
}
