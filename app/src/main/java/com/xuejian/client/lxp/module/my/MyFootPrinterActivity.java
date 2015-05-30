package com.xuejian.client.lxp.module.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.android.airmapview.AirMapInterface;
import com.airbnb.android.airmapview.AirMapMarker;
import com.airbnb.android.airmapview.AirMapView;
import com.airbnb.android.airmapview.AirMapViewTypes;
import com.airbnb.android.airmapview.DefaultAirMapViewBuilder;
import com.airbnb.android.airmapview.GoogleChinaMapType;
import com.airbnb.android.airmapview.listeners.OnMapInitializedListener;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.pagerIndicator.indicator.FixedIndicatorView;
import com.aizou.core.widget.pagerIndicator.indicator.IndicatorViewPager;
import com.aizou.core.widget.pagerIndicator.viewpager.FixedViewPager;
import com.amap.api.maps2d.AMap;
import com.google.android.gms.maps.model.LatLng;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.PeachUser;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.module.dest.OnDestActionListener;
import com.xuejian.client.lxp.module.dest.fragment.InDestFragment;
import com.xuejian.client.lxp.module.dest.fragment.OutCountryFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
    private long MARKER = 1;
    private ArrayList<LocBean> allAddCityList = new ArrayList<LocBean>();
    private ArrayList<LocBean> hasSelectLoc;
    private Set<OnDestActionListener> mOnDestActionListeners = new HashSet<OnDestActionListener>();

    @Override
    public void onDestAdded(LocBean locBean,boolean isEdit) {
        if(allAddCityList.contains(locBean)){
            return;
        }
        allAddCityList.add(locBean);
        refreshMapView(allAddCityList);
        if(isEdit) {
            updataUserFootPrint("add", locBean.id);
        }
    }

    @Override
    public void onDestRemoved(LocBean locBean) {
        allAddCityList.remove(locBean);
        refreshMapView(allAddCityList);
        updataUserFootPrint("del", locBean.id);
    }

    private void updataUserFootPrint(String type,String id){
        String[] ids=new String[1];
        ids[0]=id;
        PeachUser user= AccountManager.getInstance().getLoginAccount(this);

        UserApi.updateUserFootPrint(user.userId + "", type, ids, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                /*CommonJson4List<GroupLocBean> addResult= CommonJson4List.fromJson(result, GroupLocBean.class);
                if(addResult.code==0){*/
                ToastUtil.getInstance(MyFootPrinterActivity.this).showToast("修改成功");
                /*}else{
                    ToastUtil.getInstance(MyFootPrinterActivity.this).showToast("更改足迹失败");
                }*/
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                ToastUtil.getInstance(MyFootPrinterActivity.this).showToast("更改足迹失败");
            }
        });

    }

    public ArrayList<LocBean> getAllSelectedLoc(){
        return allAddCityList;
    }

    private void refreshMapView(final ArrayList<LocBean> bean) {
        if(mapView!=null){
            mapView.removeAllViews();
        }
        if (bean.size() > 0) {
            mapViewBuilder = new DefaultAirMapViewBuilder(this);
            airMapInterface = mapViewBuilder.builder(AirMapViewTypes.WEB).withOptions(new GoogleChinaMapType()).build();
            mapView.setOnMapInitializedListener(new OnMapInitializedListener() {
                @Override
                public void onMapInitialized() {
                    for (int j = 0; j < bean.size(); j++) {
                        mapView.addMarker(new AirMapMarker(new LatLng(bean.get(j).location.coordinates[1], bean.get(j).location.coordinates[0]), MARKER));
                    }
                    mapView.animateCenterZoom(new LatLng(bean.get(0).location.coordinates[1], bean.get(0).location.coordinates[0]), 2);
                }
                });
        } else {
            mapViewBuilder = new DefaultAirMapViewBuilder(this);
            airMapInterface = mapViewBuilder.builder(AirMapViewTypes.WEB).withOptions(new GoogleChinaMapType()).build();
            mapView.setOnMapInitializedListener(new OnMapInitializedListener() {
                @Override
                public void onMapInitialized() {
                    mapView.animateCenterZoom(new LatLng(39.969654, 116.393525), 2);
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
        hasSelectLoc=getIntent().getParcelableArrayListExtra("myfootprint");
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
                Intent intent=new Intent();
                intent.putParcelableArrayListExtra("footprint",allAddCityList);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        if(hasSelectLoc!=null&&hasSelectLoc.size()>0){
            for(LocBean locBean:hasSelectLoc){
                onDestAdded(locBean,false);
                for(OnDestActionListener onDestActionListener:mOnDestActionListeners){
                    onDestActionListener.onDestAdded(locBean,false);
                }
            }
        }else{
            refreshMapView(allAddCityList);
        }
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
