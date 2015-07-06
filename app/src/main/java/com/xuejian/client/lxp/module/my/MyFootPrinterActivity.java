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
import com.xuejian.client.lxp.bean.CountryBean;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.PreferenceUtils;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.dest.OnDestActionListener;
import com.xuejian.client.lxp.module.dest.fragment.InDestFragment;
import com.xuejian.client.lxp.module.dest.fragment.OutCountryFragment;

import java.util.ArrayList;
import java.util.HashMap;
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
    public void onDestAdded(LocBean locBean, boolean isEdit, String type) {
        if (allAddCityList.contains(locBean)) {
            return;
        }
        allAddCityList.add(locBean);
        refreshMapView(allAddCityList);
        if (isEdit) {
            updataUserFootPrint("add", locBean, type);
        }
    }

    @Override
    public void onDestRemoved(LocBean locBean, String type) {
        allAddCityList.remove(locBean);
        refreshMapView(allAddCityList);
        updataUserFootPrint("del", locBean, type);
    }

    private void updataUserFootPrint(final String type, final LocBean locBean, final String dest) {
        String[] ids = new String[1];
        ids[0] = locBean.id;
        User user = AccountManager.getInstance().getLoginAccount(this);

        UserApi.updateUserFootPrint(user.getUserId() + "", type, ids, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                /*CommonJson4List<GroupLocBean> addResult= CommonJson4List.fromJson(result, GroupLocBean.class);
                if(addResult.code==0){*/
//                if ("add".equals(type)) {
//                    if (dest.equals("out")) {
//                        String outcountry = PreferenceUtils.getCacheData(MyFootPrinterActivity.this, "destination_outcountry");
//                        CommonJson4List<CountryBean> countryListResult = CommonJson4List.fromJson(outcountry, CountryBean.class);
//                        for (CountryBean countryBean : countryListResult.result) {
//                            for (LocBean kLocBean : countryBean.destinations) {
//                                if (locBean.equals(kLocBean)) {
//                                    if (AccountManager.getInstance().getLoginAccountInfo().getTracks().containsKey(countryBean.zhName)) {
//                                        AccountManager.getInstance().getLoginAccountInfo().getTracks().get(countryBean.zhName).add(locBean);
//                                        System.out.println("addout " + countryBean.zhName);
//                                    } else {
//                                        AccountManager.getInstance().getLoginAccountInfo().getTracks().put(countryBean.zhName, new ArrayList<LocBean>());
//                                        AccountManager.getInstance().getLoginAccountInfo().getTracks().get(countryBean.zhName).add(locBean);
//                                        System.out.println("addout1 " + countryBean.zhName);
//                                    }
//                                }
//                            }
//                        }
//                    } else if (dest.equals("in")) {
//                        String inCountry = PreferenceUtils.getCacheData(MyFootPrinterActivity.this, "destination_indest_group");
//                        CommonJson4List<CountryBean> groupListResult = CommonJson4List.fromJson(inCountry, CountryBean.class);
//                        for (CountryBean countryBean : groupListResult.result) {
//                            for (LocBean kLocBean : countryBean.destinations) {
//                                if (locBean.equals(kLocBean)) {
//                                    if (AccountManager.getInstance().getLoginAccountInfo().getTracks().containsKey(countryBean.zhName)) {
//                                        AccountManager.getInstance().getLoginAccountInfo().getTracks().get("中国").add(locBean);
//                                    } else {
//                                        AccountManager.getInstance().getLoginAccountInfo().getTracks().put("中国", new ArrayList<LocBean>());
//                                        AccountManager.getInstance().getLoginAccountInfo().getTracks().get("中国").add(locBean);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                } else if ("del".equals(type)) {
//                    if (dest.equals("out")) {
//                        String outcountry = PreferenceUtils.getCacheData(MyFootPrinterActivity.this, "destination_outcountry");
//                        CommonJson4List<CountryBean> countryListResult = CommonJson4List.fromJson(outcountry, CountryBean.class);
//                        for (CountryBean countryBean : countryListResult.result) {
//                            for (LocBean kLocBean : countryBean.destinations) {
//                                if (locBean.equals(kLocBean)) {
//                                    if (AccountManager.getInstance().getLoginAccountInfo().getTracks().containsKey(countryBean.zhName)) {
//                                        AccountManager.getInstance().getLoginAccountInfo().getTracks().get(countryBean.zhName).remove(locBean);
//                                        if (AccountManager.getInstance().getLoginAccountInfo().getTracks().get(countryBean.zhName).size() == 0)
//                                            AccountManager.getInstance().getLoginAccountInfo().getTracks().remove(countryBean.zhName);
//                                    }
//                                }
//                            }
//                        }
//                    } else if (dest.equals("in")) {
//                        String inCountry = PreferenceUtils.getCacheData(MyFootPrinterActivity.this, "destination_indest_group");
//                        System.out.println("inCountry " + inCountry);
//                        CommonJson4List<CountryBean> groupListResult = CommonJson4List.fromJson(inCountry, CountryBean.class);
//                        for (CountryBean countryBean : groupListResult.result) {
//                            for (LocBean kLocBean : countryBean.destinations) {
//                                if (locBean.equals(kLocBean)) {
//                                    if (AccountManager.getInstance().getLoginAccountInfo().getTracks().containsKey(countryBean.zhName)) {
//                                        AccountManager.getInstance().getLoginAccountInfo().getTracks().get("中国").remove(locBean);
//                                        if (AccountManager.getInstance().getLoginAccountInfo().getTracks().get("中国").size() == 0)
//                                            AccountManager.getInstance().getLoginAccountInfo().getTracks().remove("中国");
//                                    }
//                                }
//                            }
//                        }
//                    }
//                } else System.out.println("unCaught ============");
//                ToastUtil.getInstance(MyFootPrinterActivity.this).showToast("修改成功");
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                ToastUtil.getInstance(MyFootPrinterActivity.this).showToast("更改足迹失败");
            }
        });

    }

    public ArrayList<LocBean> getAllSelectedLoc() {
        return allAddCityList;
    }

    private void refreshMapView(final ArrayList<LocBean> bean) {
        if (mapView != null) {
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
        View rootView = View.inflate(mContext, R.layout.activity_my_footprinter, null);
        setContentView(rootView);
        mapView = (AirMapView) rootView.findViewById(R.id.my_footprinter_map);
        hasSelectLoc = getIntent().getParcelableArrayListExtra("myfootprint");
        inOutIndicator = (FixedIndicatorView) rootView.findViewById(R.id.my_footprinter_in_out_indicator);
        mSelectDestVp = (FixedViewPager) rootView.findViewById(R.id.my_footprinter_select_dest_viewPager);
        indicatorViewPager = new IndicatorViewPager(inOutIndicator, mSelectDestVp);
        indicatorViewPager.setAdapter(new InOutFragmentAdapter(getSupportFragmentManager()));
        mSelectDestVp.setCanScroll(false);
        // 设置viewpager保留界面不重新加载的页面数量
        mSelectDestVp.setOffscreenPageLimit(2);
        // 默认是1,，自动预加载左右两边的界面。设置viewpager预加载数为0。只加载加载当前界面。
        mSelectDestVp.setPrepareNumber(0);
        indicatorViewPager.setOnIndicatorPageChangeListener(new IndicatorViewPager.OnIndicatorPageChangeListener() {
            @Override
            public void onIndicatorPageChange(int preItem, int currentItem) {
                if (currentItem == 1) {
                    MobclickAgent.onEvent(mContext, "event_go_aboard");
                }
            }
        });

        titleBack = (TextView) rootView.findViewById(R.id.my_footprinter_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFootPrint(allAddCityList);
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("footprint", allAddCityList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        if (hasSelectLoc != null && hasSelectLoc.size() > 0) {
            for (LocBean locBean : hasSelectLoc) {
                onDestAdded(locBean, false, null);
                for (OnDestActionListener onDestActionListener : mOnDestActionListeners) {
                    onDestActionListener.onDestAdded(locBean, false, null);
                }
            }
        } else {
            refreshMapView(allAddCityList);
        }
    }

    private void updateFootPrint(ArrayList<LocBean> allAddCityList) {
        HashMap<String,ArrayList<LocBean>> track=new HashMap<>();
        String outcountry = PreferenceUtils.getCacheData(MyFootPrinterActivity.this, "destination_outcountry");
        String inCountry = PreferenceUtils.getCacheData(MyFootPrinterActivity.this, "destination_indest_group");
        CommonJson4List<CountryBean> countryListResult = CommonJson4List.fromJson(outcountry, CountryBean.class);
        CommonJson4List<CountryBean> groupListResult = CommonJson4List.fromJson(inCountry, CountryBean.class);
        for (LocBean locBean:allAddCityList){
                for (CountryBean countryBean : countryListResult.result) {
                    for (LocBean kLocBean : countryBean.destinations) {
                        if (locBean.equals(kLocBean)) {
                            if (track.containsKey(countryBean.zhName)) {
                                track.get(countryBean.zhName).add(locBean);
                            } else {
                                track.put(countryBean.zhName, new ArrayList<LocBean>());
                                track.get(countryBean.zhName).add(locBean);
                            }
                        }
                    }
                }

                for (CountryBean incountryBean : groupListResult.result) {
                    for (LocBean kLocBean : incountryBean.destinations) {
                        if (locBean.equals(kLocBean)) {
                            if (track.containsKey("中国")) {
                                track.get("中国").add(locBean);
                            } else {
                                track.put("中国", new ArrayList<LocBean>());
                                track.get("中国").add(locBean);
                            }
                        }
                    }
                }
            }
        AccountManager.getInstance().getLoginAccountInfo().setTracks(track);
    }

    private class InOutFragmentAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        private String[] tabNames = {"国内", "国外"};
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
            if (position == 0) {
                textView.setBackgroundResource(R.drawable.in_out_indicator_textbg);
            } else if (position == 1) {
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
