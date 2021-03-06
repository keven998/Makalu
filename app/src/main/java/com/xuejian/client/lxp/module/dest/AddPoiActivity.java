package com.xuejian.client.lxp.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.common.api.BaseApi;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.widget.FlowLayout;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.module.dest.adapter.PoiAdapter;
import com.xuejian.client.lxp.module.dest.adapter.StringSpinnerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Rjm on 2014/11/27.
 */
public class AddPoiActivity extends PeachBaseActivity {
    public final static int REQUEST_CODE_SEARCH_POI = 101;
    @Bind(R.id.lv_poi_list)
    PullToRefreshListView mLvPoiList;
    @Bind(R.id.loc_spinner)
    Spinner loc_spinner;
    @Bind(R.id.type_spinner)
    Spinner type_spinner;
    @Bind(R.id.tv_back)
    TextView tv_back;
    @Bind(R.id.iv_location)
    ImageView iv_map;
    @Bind(R.id.iv_search)
    ImageView iv_search;
    @Bind(R.id.add_poi_bottom_panel)
    FrameLayout bottomFrame;
    @Bind(R.id.add_poi_scroll_panel)
    HorizontalScrollView hsView;
    @Bind(R.id.poi_add_ll)
    LinearLayout hsViewLL;
    private String mType;
    private List<LocBean> locList;
    private ArrayList<PoiDetailBean> allLoadLocList = new ArrayList<PoiDetailBean>();
    private ArrayList<PoiDetailBean> hasAddList;
    private int dayIndex;
    private String[] poiTypeArray, poiTypeValueArray, poiTypeValueArrays;

    private int curPage = 0;
    private LocBean curLoc;
    private PoiAdapter mPoiAdapter;
    private StringSpinnerAdapter mLocSpinnerAdapter, mTypeSpinnerAdapter;
    private CityAdapter cityAdapter;
    private PopupWindow mPop;
    private ImageView selected;
    private int globalFlag = 0;
    private static final int SEARCH_CODE = 105;
    private List<PoiDetailBean> curPoiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAccountAbout(true);
        initView();
        initData();

    }

    private void initView() {
        setContentView(R.layout.activity_add_poi);
        ButterKnife.bind(this);
        mLvPoiList.setPullLoadEnabled(false);
        mLvPoiList.setPullRefreshEnabled(false);
        mLvPoiList.setScrollLoadEnabled(true);
        mLvPoiList.setHasMoreData(false);
        mPoiAdapter = new PoiAdapter(mContext, true);
        mPoiAdapter.setOnPoiActionListener(new PoiAdapter.OnPoiActionListener() {
            @Override
            public void onPoiAdded(final PoiDetailBean poi) {
                hasAddList.add(poi);
                //mTilteView.setText(String.format("第%d天(%d安排)", dayIndex+1, hasAddList.size()));
                View view = View.inflate(AddPoiActivity.this, R.layout.poi_bottom_cell_with_del, null);
                FrameLayout del_fl = (FrameLayout) view.findViewById(R.id.poi_del_fl);
                TextView location = (TextView) view.findViewById(R.id.names);
                location.setText(poi.zhName);
                del_fl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onPoiRemoved(poi);
                        poi.hasAdded = false;
                        mPoiAdapter.notifyDataSetChanged();
                    }
                });
                hsViewLL.addView(view);
                if (hasAddList.size() > 0) {
                    bottomFrame.setVisibility(View.VISIBLE);
                }
                autoScrollPanel();
            }

            @Override
            public void onPoiRemoved(PoiDetailBean poi) {
                int index = hasAddList.indexOf(poi);
                hsViewLL.removeViewAt(index);
                hasAddList.remove(poi);
                // mTilteView.setText(String.format("第%d天(%d安排)", dayIndex+1, hasAddList.size()));
                if (hasAddList.size() == 0) {
                    bottomFrame.setVisibility(View.GONE);
                }
                autoScrollPanel();
            }

            @Override
            public void onPoiNavi(PoiDetailBean poi) {

            }
        });
        mLvPoiList.getRefreshableView().setAdapter(mPoiAdapter);
        mLvPoiList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getPoiListByLoc(mType, curLoc.id, 0);

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getPoiListByLoc(mType, curLoc.id, curPage + 1);

            }
        });
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("poiList", hasAddList);
                intent.putExtra("dayIndex", dayIndex);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sear_intent = new Intent(AddPoiActivity.this, SearchForPoi.class);
                sear_intent.putExtra("type", mType);
                sear_intent.putExtra("loc", curLoc);
                sear_intent.putExtra("isCanAdd", true);
                startActivityForResult(sear_intent, SEARCH_CODE);
            }
        });
    }


    private void autoScrollPanel() {
        hsView.postDelayed(new Runnable() {
            @Override
            public void run() {
                hsView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 100);
    }

    private void showFilterPage(final List<String> cityNames) {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//自定义布局
        ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
                R.layout.poi_filter_page, null, true);
        TitleHeaderBar titleHeaderBar = (TitleHeaderBar) menuView.findViewById(R.id.poi_filter_title);
        final CheckedTextView food = (CheckedTextView) menuView.findViewById(R.id.poi_filter_food);
        final CheckedTextView shop = (CheckedTextView) menuView.findViewById(R.id.poi_filter_shop);
        final CheckedTextView spot = (CheckedTextView) menuView.findViewById(R.id.poi_filter_spot);
        final CheckedTextView hotel = (CheckedTextView) menuView.findViewById(R.id.poi_filter_hotel);
        if (mType == poiTypeValueArray[0]) {
            setCheckAction(spot, food, shop, hotel);
        } else if (mType == poiTypeValueArray[1]) {
            setCheckAction(hotel, food, shop, spot);
        } else if (mType == poiTypeValueArray[2]) {
            setCheckAction(food, spot, shop, hotel);
        } else if (mType == poiTypeValueArray[3]) {
            setCheckAction(shop, food, spot, hotel);
        }
        spot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCheckAction(spot, food, shop, hotel);
            }
        });
        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCheckAction(food, spot, shop, hotel);
            }
        });
        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCheckAction(shop, food, spot, hotel);
            }
        });
        hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCheckAction(hotel, food, shop, spot);
            }
        });
        final ListView lv = (ListView) menuView.findViewById(R.id.poi_city_list);

        cityAdapter = new CityAdapter(cityNames, globalFlag);
        lv.setAdapter(cityAdapter);
        mPop = new PopupWindow(menuView, FlowLayout.LayoutParams.MATCH_PARENT,
                FlowLayout.LayoutParams.MATCH_PARENT, true);
        mPop.setContentView(menuView);//设置包含视图
        mPop.setWidth(FlowLayout.LayoutParams.MATCH_PARENT);
        mPop.setHeight(FlowLayout.LayoutParams.MATCH_PARENT);
        mPop.setAnimationStyle(R.style.PopAnimation);
        mPop.showAtLocation(findViewById(R.id.filter_parent), Gravity.BOTTOM, 0, 0);
        titleHeaderBar.findViewById(R.id.ly_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPop.dismiss();
            }
        });
        titleHeaderBar.getRightTextView().setText("确定");
        titleHeaderBar.getRightTextView().setTextColor(getResources().getColor(R.color.app_theme_color));
        titleHeaderBar.findViewById(R.id.ly_title_bar_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPop.dismiss();
                if (food.isChecked()) {
                    mType = poiTypeValueArray[2];
                } else if (shop.isChecked()) {
                    mType = poiTypeValueArray[3];
                } else if (spot.isChecked()) {
                    mType = poiTypeValueArray[0];
                } else if (hotel.isChecked()) {
                    mType = poiTypeValueArray[1];
                }
                curLoc = locList.get(globalFlag);
                mPoiAdapter.getDataList().clear();
                mPoiAdapter.notifyDataSetChanged();
                mLvPoiList.onPullUpRefreshComplete();
                mLvPoiList.onPullDownRefreshComplete();
                mLvPoiList.doPullRefreshing(true, 500);
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                globalFlag = position;
                cityAdapter = new CityAdapter(cityNames, globalFlag);
                lv.setAdapter(cityAdapter);
            }
        });
    }

    public void setCheckAction(CheckedTextView v1, CheckedTextView v2, CheckedTextView v3, CheckedTextView v4) {
        v1.setChecked(true);
        v2.setChecked(false);
        v3.setChecked(false);
        v4.setChecked(false);
    }


    public class CityAdapter extends BaseAdapter {

        private List<String> citys;
        private int pos;

        public CityAdapter(List<String> citys, int pos) {
            this.citys = citys;
            this.pos = pos;
        }

        @Override
        public int getCount() {
            return citys.size();
        }

        @Override
        public Object getItem(int position) {
            return citys.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(AddPoiActivity.this, R.layout.maps_days_cell, null);

            }
            TextView days_title = (TextView) convertView.findViewById(R.id.days_title);
            days_title.setText(citys.get(position));
            selected = (ImageView) convertView.findViewById(R.id.map_days_selected);
            if (position == pos) {
                selected.setVisibility(View.VISIBLE);
            } else {
                selected.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        if (mPop != null && mPop.isShowing()) {
            mPop.dismiss();
        } else {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("poiList", hasAddList);
            intent.putExtra("dayIndex", dayIndex);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void initData() {
        mType = getIntent().getStringExtra("type");
        dayIndex = getIntent().getIntExtra("dayIndex", -1);
        hasAddList = getIntent().getParcelableArrayListExtra("poiList");
        if (hasAddList.size() > 0) {
            bottomFrame.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < hasAddList.size(); i++) {
            final int pos = i;
            View view = View.inflate(AddPoiActivity.this, R.layout.poi_bottom_cell_with_del, null);
            final FrameLayout del_fl = (FrameLayout) view.findViewById(R.id.poi_del_fl);
            final TextView location = (TextView) view.findViewById(R.id.names);
            location.setText(hasAddList.get(i).zhName);
            del_fl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int pos = getPos(location.getText().toString());
                    int listPos = getPosOfList(location.getText().toString());
                    if (listPos>=0)mPoiAdapter.cancleAdd(listPos);
                    hasAddList.remove(pos);
                    if (hasAddList.size() == 0) {
                        bottomFrame.setVisibility(View.GONE);
                    }
                    // getPos(location.getText().toString());
                    hsViewLL.removeView(del_fl);
                    mPoiAdapter.notifyDataSetChanged();
                    autoScrollPanel();
                }
            });
            hsViewLL.addView(view);
        }
        autoScrollPanel();
        poiTypeArray = getResources().getStringArray(R.array.poi_type);
        poiTypeValueArray = getResources().getStringArray(R.array.poi_type_value);
        poiTypeValueArrays = getResources().getStringArray(R.array.poi_type_values);
        locList = getIntent().getParcelableArrayListExtra("locList");
        final List<String> cityStrList = new ArrayList<String>();
        for (LocBean locBean : locList) {
            cityStrList.add(locBean.zhName);
        }
        curLoc = locList.get(0);
        if (TextUtils.isEmpty(mType))mType = poiTypeValueArray[0];
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cityStrList);
        adapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
//        filter_icon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showFilterPage(cityStrList);
//            }
//        });
        mLocSpinnerAdapter = new StringSpinnerAdapter(mContext, cityStrList);
        loc_spinner.setAdapter(mLocSpinnerAdapter);
        loc_spinner.setSelection(0, true);
        loc_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!((CheckedTextView) view).isChecked())
                    ((CheckedTextView) view).setChecked(true);
                curLoc = locList.get(position);
                mPoiAdapter.getDataList().clear();
                mPoiAdapter.notifyDataSetChanged();
                mLvPoiList.onPullUpRefreshComplete();
                mLvPoiList.onPullDownRefreshComplete();
                mLvPoiList.doPullRefreshing(true, 500);
                getPoiListByLoc(mType, curLoc.id, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int pos =0;
        for (int i = 0; i < poiTypeValueArray.length; i++) {
            if (poiTypeValueArray[i].equals(mType)){
                pos=i;
                break;
            }
        }
        mTypeSpinnerAdapter = new StringSpinnerAdapter(mContext, Arrays.asList(getResources().getStringArray(R.array.poi_type_values_name)));
        type_spinner.setAdapter(mTypeSpinnerAdapter);
        type_spinner.setSelection(pos, true);
        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!((CheckedTextView) view).isChecked())
                    ((CheckedTextView) view).setChecked(true);
                mType = poiTypeValueArrays[position];
                mPoiAdapter.getDataList().clear();
                mPoiAdapter.notifyDataSetChanged();
                mLvPoiList.onPullUpRefreshComplete();
                mLvPoiList.onPullDownRefreshComplete();
                mLvPoiList.doPullRefreshing(true, 500);
                getPoiListByLoc(mType, curLoc.id, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //   initSpinnerListener();
        mLvPoiList.doPullRefreshing(true, 500);

        iv_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPoiActivity.this, StrategyDomesticMapActivity.class);
                intent.putParcelableArrayListExtra("allLoadLocList", allLoadLocList);
                intent.putExtra("title", curLoc.zhName + resizeTypeName(mType));
                intent.putExtra("isAllPoiLoc", true);
                startActivity(intent);
            }
        });
//        getPoiListByLoc(mType, curLoc.id, 0);

//        mTilteView.setText(String.format("第%d天(%d安排)", dayIndex+1, hasAddList.size()));
    }

    private int getPosOfList(String zhName) {
        for (int i = 0; i < curPoiList.size(); i++) {
            if (curPoiList.get(i).zhName.equals(zhName)) {
                return i;
            }
        }
        return -1;
    }

    private int getPos(String name) {
        for (int i = 0; i < hasAddList.size(); i++) {
            if (hasAddList.get(i).zhName.equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public String resizeTypeName(String type) {
        if ("hotel".equals(type)) {
            return "酒店";
        } else if ("restaurant".equals(type)) {
            return "美食";
        } else if ("shopping".equals(type)) {
            return "购物";
        } else if ("vs".equals(type)) {
            return "景点";
        } else {
            return "";
        }
    }

    private void getPoiListByLoc(final String type, final String cityId, final int page) {

        TravelApi.getPoiListByLoc(type, cityId, page, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                if (!(mType.equals(type) && curLoc.id.equals(cityId))) {
                    return;
                }
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson4List<PoiDetailBean> poiListResult = CommonJson4List.fromJson(result, PoiDetailBean.class);
                if (poiListResult.code == 0) {
                    curPage = page;
                    bindView(poiListResult.result);
                    collectAllLocs(poiListResult.result);
                } else {
                    if (!isFinishing()) {
                        ToastUtil.getInstance(AddPoiActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                    }
                }
//                if (curPage == 0) {
//                    mLvPoiList.onPullUpRefreshComplete();
//                    mLvPoiList.onPullDownRefreshComplete();
//                } else {
//                    mLvPoiList.onPullDownRefreshComplete();
//                }
                mLvPoiList.onPullUpRefreshComplete();
                mLvPoiList.onPullDownRefreshComplete();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing()) {
                    ToastUtil.getInstance(AddPoiActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void collectAllLocs(List<PoiDetailBean> result) {
        allLoadLocList.addAll(result);
        /*while(result.iterator().hasNext()){
            allLoadLocList.add(result.iterator().next());
        }*/
    }

    private void bindView(List<PoiDetailBean> result) {
        if (curPage == 0) {
            mPoiAdapter.getDataList().clear();
            mLvPoiList.onPullUpRefreshComplete();
            mLvPoiList.onPullDownRefreshComplete();
            mPoiAdapter.notifyDataSetChanged();
        }
        if (result == null
                || result.size() < BaseApi.PAGE_SIZE) {
            mLvPoiList.setHasMoreData(false);
            return;
        } else {
            mLvPoiList.setHasMoreData(true);
            mLvPoiList.onPullUpRefreshComplete();
        }
        for (PoiDetailBean detailBean : result) {
            detailBean.hasAdded = hasAddList.contains(detailBean);
        }
        curPoiList = result;
        mPoiAdapter.getDataList().addAll(result);
        mPoiAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SEARCH_CODE) {
                ArrayList<PoiDetailBean> list = new ArrayList<>();
                list = data.getParcelableArrayListExtra("newPoi");
                if (list.size() > 0) {
                    if (hasAddList.size() > 0) {
                        for (PoiDetailBean bean : hasAddList) {
                            if (list.contains(bean)) {
                                list.remove(bean);
                            }
                        }
                    }
                    for (PoiDetailBean bean : mPoiAdapter.getDataList()) {
                        if (list.contains(bean)) {
                            bean.hasAdded = true;
                        }
                    }
                    hasAddList.addAll(list);
                    mPoiAdapter.notifyDataSetChanged();
                    for (PoiDetailBean bean : list) {
                        View view = View.inflate(AddPoiActivity.this, R.layout.poi_bottom_cell_with_del, null);
                        FrameLayout del_fl = (FrameLayout) view.findViewById(R.id.poi_del_fl);
                        TextView location = (TextView) view.findViewById(R.id.names);
                        location.setText(bean.zhName);
                        hsViewLL.addView(view);
                        autoScrollPanel();
                    }
                }


            }
        }
    }
}
