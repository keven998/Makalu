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

import com.airbnb.android.airmapview.AirMapInterface;
import com.airbnb.android.airmapview.AirMapMarker;
import com.airbnb.android.airmapview.AirMapPolyline;
import com.airbnb.android.airmapview.AirMapView;
import com.airbnb.android.airmapview.AirMapViewTypes;
import com.airbnb.android.airmapview.DefaultAirMapViewBuilder;
import com.airbnb.android.airmapview.GoogleChinaMapType;
import com.airbnb.android.airmapview.listeners.OnMapInitializedListener;
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
public class StrategyMapActivity extends PeachBaseActivity implements OnMapInitializedListener {
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
        mapViewBuilder = new DefaultAirMapViewBuilder(this);
        airMapInterface = mapViewBuilder.builder(AirMapViewTypes.WEB).withOptions(new GoogleChinaMapType()).build();
        mapView = (AirMapView) findViewById(R.id.strategy_map);
        mapView.setOnMapInitializedListener(this);
        mapView.initialize(getSupportFragmentManager(), airMapInterface);
        mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        //mapView.onCreate(savedInstanceState);
        //initMapView();
        all_locations = (HorizontalScrollView) findViewById(R.id.map_days_name_list);
        layout=new LinearLayout(getApplicationContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LocalDisplay.dp2px(50)));
        layout.setGravity(Gravity.CENTER_VERTICAL);
        initData(0);
    }

    private void initData(int pos){
  //      aMap.clear();
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
        setUpMap(names,coordinates, mLatLngsArr);
    }



    private void setUpMap(ArrayList<String> names,ArrayList<double[]> coor,com.amap.api.maps2d.model.LatLng... latLngs){
      //  aMap.moveCamera(CameraUpdateFactory.zoomTo(4));
        if(coor.size()>0) {
            List<com.google.android.gms.maps.model.LatLng> points=new List<com.google.android.gms.maps.model.LatLng>() {
                @Override
                public void add(int location, com.google.android.gms.maps.model.LatLng object) {

                }

                @Override
                public boolean add(com.google.android.gms.maps.model.LatLng object) {
                    return false;
                }

                @Override
                public boolean addAll(int location, Collection<? extends com.google.android.gms.maps.model.LatLng> collection) {
                    return false;
                }

                @Override
                public boolean addAll(Collection<? extends com.google.android.gms.maps.model.LatLng> collection) {
                    return false;
                }

                @Override
                public void clear() {

                }

                @Override
                public boolean contains(Object object) {
                    return false;
                }

                @Override
                public boolean containsAll(Collection<?> collection) {
                    return false;
                }

                @Override
                public com.google.android.gms.maps.model.LatLng get(int location) {
                    return null;
                }

                @Override
                public int indexOf(Object object) {
                    return 0;
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @NonNull
                @Override
                public Iterator<com.google.android.gms.maps.model.LatLng> iterator() {
                    return null;
                }

                @Override
                public int lastIndexOf(Object object) {
                    return 0;
                }

                @NonNull
                @Override
                public ListIterator<com.google.android.gms.maps.model.LatLng> listIterator() {
                    return null;
                }

                @NonNull
                @Override
                public ListIterator<com.google.android.gms.maps.model.LatLng> listIterator(int location) {
                    return null;
                }

                @Override
                public com.google.android.gms.maps.model.LatLng remove(int location) {
                    return null;
                }

                @Override
                public boolean remove(Object object) {
                    return false;
                }

                @Override
                public boolean removeAll(Collection<?> collection) {
                    return false;
                }

                @Override
                public boolean retainAll(Collection<?> collection) {
                    return false;
                }

                @Override
                public com.google.android.gms.maps.model.LatLng set(int location, com.google.android.gms.maps.model.LatLng object) {
                    return null;
                }

                @Override
                public int size() {
                    return 0;
                }

                @NonNull
                @Override
                public List<com.google.android.gms.maps.model.LatLng> subList(int start, int end) {
                    return null;
                }

                @NonNull
                @Override
                public Object[] toArray() {
                    return new Object[0];
                }

                @NonNull
                @Override
                public <T> T[] toArray(T[] array) {
                    return null;
                }
            };
            //aMap.addPolyline((new PolylineOptions()).add(latLngs).width(5).color(Color.RED));

            for(int k=0;k<names.size();k++){
               /* aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                        .position(new LatLng(coor.get(k)[1], coor.get(k)[0])).title(names.get(k))
                        .draggable(true));*/
                mapView.addMarker(new AirMapMarker(new com.google.android.gms.maps.model.LatLng(coor.get(k)[1], coor.get(k)[0]),k).setTitle(names.get(k)));
                points.add(new com.google.android.gms.maps.model.LatLng(coor.get(k)[1], coor.get(k)[0]));
            }
            mapView.addPolyline(new AirMapPolyline(points,5));

            com.amap.api.maps2d.model.LatLngBounds.Builder llBound=new com.amap.api.maps2d.model.LatLngBounds.Builder();

            for(com.amap.api.maps2d.model.LatLng ll:latLngs){
                llBound.include(ll);
            }
            bounds=llBound.build();
        }
      //  aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
    }


    private int DealWithDays(String days){
        String[] deal1=days.split("第");
        String[] deal2=deal1[1].split("天");
        int value=Integer.valueOf(deal2[0]);
        return value;
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
    public void onMapInitialized() {
        final LatLng airbnbLatLng = new LatLng(37.771883, -122.405224);
        addMarker("Airbnb HQ", airbnbLatLng);
        addMarker("Performance Bikes", new LatLng(37.773975,-122.40205));
        addMarker("REI", new LatLng(37.772127, -122.404411));
        mapView.animateCenterZoom(airbnbLatLng, 10);
    }

    private void addMarker(String title, LatLng latLng) {
        mapView.addMarker(new AirMapMarker(latLng, 1)
                .setTitle(title));
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
