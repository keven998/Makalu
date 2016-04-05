package com.xuejian.client.lxp.module.my;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.StartCity;
import com.xuejian.client.lxp.common.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by rjm on 2015/3/26.
 */
public class SelectResidentActivity extends PeachBaseActivity {
    private TextView loc_name;
    private ExpandableListView elvCity;
    private LocationManagerProxy mLocationManagerProxy;
    private ArrayList<StartCity> startCitys = new ArrayList<StartCity>();
    private ArrayList<StartCity> allCitys;
    private CityListAdapter aAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_resident);
        TextView titleView = (TextView) findViewById(R.id.tv_title_bar_title);
        titleView.setText("选择现住地");
        findViewById(R.id.tv_confirm).setVisibility(View.GONE);
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        elvCity = (ExpandableListView) findViewById(R.id.elv_city);
        loc_name = (TextView) findViewById(R.id.tv_loc_name);
        mLocationManagerProxy = LocationManagerProxy.getInstance(SelectResidentActivity.this);
        mLocationManagerProxy.setGpsEnable(false);
        requestLocation();
        initData();
    }

    private void requestLocation() {
        if (mLocationManagerProxy == null) {
            mLocationManagerProxy = LocationManagerProxy.getInstance(SelectResidentActivity.this);
            mLocationManagerProxy.setGpsEnable(false);
        }
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, -1, 15, new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(final AMapLocation aMapLocation) {
                        if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
                            //获取位置信息
                            loc_name.setText(aMapLocation.getCity());
                            loc_name.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.putExtra("result", aMapLocation.getCity());
                                    StartCity city = searchCity(aMapLocation.getCity());
                                    intent.putExtra("resultId", city.id);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            });
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
                });
    }

    private void initData() {
        new Thread(new Runnable() {

            @Override
            public void run() {

                allCitys = CommonUtils.parserStartCityJson(mContext);
                startCitys.addAll(allCitys);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        aAdapter = new CityListAdapter();
                        elvCity.setAdapter(aAdapter);
                    }
                });

            }
        }).start();

    }

    private StartCity searchCity(String keyword) {
        if (allCitys == null) {
            allCitys = CommonUtils.parserStartCityJson(mContext);
        }
        for (StartCity city : allCitys) {
            if (city.name.contains(keyword) || city.pinyin.contains(keyword)) {

                return city;
            } else {
                for (StartCity childCity : city.childs) {
                    if (childCity.name.contains(keyword)
                            || childCity.pinyin.contains(keyword)) {
                        return childCity;
                    }
                }
            }
        }
        return null;
    }

    private class CityListAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return startCitys.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (startCitys.get(groupPosition).childs == null) {
                return 0;
            } else {
                return startCitys.get(groupPosition).childs.size();
            }

        }

        @Override
        public Object getGroup(int groupPosition) {
            return startCitys.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return startCitys.get(groupPosition).childs.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(final int groupPosition,
                                 final boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_city_parent,
                        null);
            }
            TextView nameTv = (TextView) convertView
                    .findViewById(R.id.tv_city_parent_name);
            ImageView arrIv = (ImageView) convertView
                    .findViewById(R.id.iv_parent_arr);
            final StartCity city = startCitys.get(groupPosition);
            nameTv.setText(city.name);

            if (city.childs != null && city.childs.size() > 0) {
                if (isExpanded) {
                    arrIv.setBackgroundResource(R.drawable.common_icon_up);
                } else {
                    arrIv.setBackgroundResource(R.drawable.common_icon_down);
                }
                convertView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (isExpanded) {
                            elvCity.collapseGroup(groupPosition);
                        } else {
                            elvCity.expandGroup(groupPosition);
                        }

                    }
                });
            } else {
                convertView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("result", city.name);
                        intent.putExtra("resultId", city.id);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
                arrIv.setBackgroundResource(0);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_city_child,
                        null);
            }
            TextView nameTv = (TextView) convertView
                    .findViewById(R.id.tv_city_child_name);
            final StartCity city = startCitys.get(groupPosition).childs
                    .get(childPosition);
            nameTv.setText(city.name);

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("result", city.name);
                    intent.putExtra("resultId", city.id);
                    setResult(RESULT_OK, intent);
                    finish();

                }
            });
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

    }

}
