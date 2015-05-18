package com.aizou.peachtravel.module.my;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.StartCity;
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by rjm on 2015/3/26.
 */
public class SelectResidentActivity extends PeachBaseActivity {
    @InjectView(R.id.ly_header_bar_title_wrap)
    TitleHeaderBar titleHeaderBar;
   /* @InjectView(R.id.et_search)
    EditText etSearch;*/
    @InjectView(R.id.elv_city)
    ExpandableListView elvCity;
    @InjectView(R.id.tv_loc_name)
    TextView loc_name;


    private LocationManagerProxy mLocationManagerProxy;
    private ArrayList<StartCity> startCitys = new ArrayList<StartCity>();
    private ArrayList<StartCity> allCitys;
    private CityListAdapter aAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_resident);
        ButterKnife.inject(this);
        titleHeaderBar.getTitleTextView().setText("现住地");
        titleHeaderBar.findViewById(R.id.ly_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,R.anim.fade_out);
            }
        });
        mLocationManagerProxy = LocationManagerProxy.getInstance(SelectResidentActivity.this);
        mLocationManagerProxy.setGpsEnable(false);
        requestLocation();
        /*etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                    if (TextUtils.isEmpty(s.toString())) {
                        startCitys.clear();
                        startCitys.addAll(allCitys);
                    } else {
                        searchCity(s.toString());
                    }
                    aAdapter.notifyDataSetChanged();
                }


        });*/
        initData();
    }

    private void requestLocation() {
        if(mLocationManagerProxy == null){
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
                                    Intent intent=new Intent();
                                    intent.putExtra("result",aMapLocation.getCity());
                                    setResult(RESULT_OK,intent);
                                    finish();
                                    overridePendingTransition(0,R.anim.fade_out);
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
        // ArrayList<String> allCities = new ArrayList<String>();

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

    private void searchCity(String keyword) {
        if (allCitys == null) {
            allCitys = CommonUtils.parserStartCityJson(mContext);
        }
        startCitys.clear();
        for (StartCity city : allCitys) {
            if (city.name.contains(keyword) || city.pinyin.contains(keyword)) {
                startCitys.add(city);
                continue;
            } else {
                for (StartCity childCity : city.childs) {
                    if (childCity.name.contains(keyword)
                            || childCity.pinyin.contains(keyword)) {
                        startCitys.add(childCity);
                    }
                }
            }
        }
    }
    class CityListAdapter extends BaseExpandableListAdapter {

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
                    arrIv.setBackgroundResource(R.drawable.right_arrow_icon);
                } else {
                    arrIv.setBackgroundResource(R.drawable.right_arrow_icon);
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
                        Intent intent=new Intent();
                        intent.putExtra("result",city.name);
                        setResult(RESULT_OK,intent);
                        finish();
                        overridePendingTransition(0,R.anim.fade_out);
                    }
                });
                arrIv.setBackgroundDrawable(null);
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
            ImageView arrIv = (ImageView) convertView
                    .findViewById(R.id.iv_child_arr);
            //arrIv.setVisibility(View.INVISIBLE);
            final StartCity city = startCitys.get(groupPosition).childs
                    .get(childPosition);
            nameTv.setText(city.name);

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent=new Intent();
                    intent.putExtra("result",city.name);
                    setResult(RESULT_OK,intent);
                    finish();
                    overridePendingTransition(0,R.anim.fade_out);

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
