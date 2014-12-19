package com.aizou.peachtravel.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.bean.PoiGuideBean;
import com.aizou.peachtravel.common.api.BaseApi;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.module.dest.adapter.PoiAdapter;
import com.aizou.peachtravel.module.dest.adapter.StringSpinnerAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/24.
 */
public class PoiListActivity extends PeachBaseActivity {
    @InjectView(R.id.loc_spinner)
    Spinner mLocSpinner;
    @InjectView(R.id.btn_ok)
    TextView mBtnOk;
    @InjectView(R.id.iv_city_poi)
    ImageView mIvCityPoi;
    @InjectView(R.id.tv_city_name)
    TextView mTvCityName;
    @InjectView(R.id.tv_city_poi_desc)
    TextView mTvCityPoiDesc;
    PoiAdapter mPoiAdapter;
    StringSpinnerAdapter mLocSpinnerAdapter;
    @InjectView(R.id.tv_title_bar_left)
    TextView mTvTitleBarLeft;
    private PullToRefreshListView mPoiListLv;
    private String type;
    private boolean canAdd;
    private List<LocBean> locList;
    private ArrayList<PoiDetailBean> hasAddList;
    private int page = 0;
    private LocBean curLoc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        type = getIntent().getStringExtra("type");
        canAdd = getIntent().getBooleanExtra("canAdd", false);
        locList = getIntent().getParcelableArrayListExtra("locList");
        hasAddList = getIntent().getParcelableArrayListExtra("poiList");
        mPoiAdapter = new PoiAdapter(this, canAdd);
        mPoiAdapter.setOnPoiActionListener(new PoiAdapter.OnPoiActionListener() {
            @Override
            public void onPoiAdded(PoiDetailBean poi) {
                hasAddList.add(poi);
            }

            @Override
            public void onPoiRemoved(PoiDetailBean poi) {
                hasAddList.remove(poi);
            }
        });
        mPoiListLv.getRefreshableView().setAdapter(mPoiAdapter);
        List<String> cityStrList = new ArrayList<String>();
        for (LocBean locBean : locList) {
            cityStrList.add(locBean.zhName);
        }
        mLocSpinnerAdapter = new StringSpinnerAdapter(mContext, cityStrList);
        mLocSpinner.setAdapter(mLocSpinnerAdapter);
        mLocSpinner.setSelection(0, true);
        curLoc = locList.get(0);
        mLocSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long itemId) {
                curLoc = locList.get(position);
                page = 0;
                getPoiListData(type, curLoc.id);
                getPoiGuide(type, curLoc.id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
//        ImageLoader.getInstance().displayImage(result.images.get(0).url, mIvCityPoi, UILUtils.getDefaultOption());
//        mTvCityName.setText(result.zhName);
//        mTvCityPoiDesc.setText(result.desc);
        getPoiGuide(type, curLoc.id);
        getPoiListData(type, curLoc.id);

    }

    private void initView() {
        setContentView(R.layout.activity_poi_list);
        mPoiListLv = (PullToRefreshListView) findViewById(R.id.lv_poi_list);
        View headerView = View.inflate(mContext, R.layout.view_poi_list_header, null);
        mPoiListLv.getRefreshableView().addHeaderView(headerView);
        mPoiListLv.setPullLoadEnabled(false);
        mPoiListLv.setPullRefreshEnabled(false);
        mPoiListLv.setScrollLoadEnabled(true);
        ButterKnife.inject(this);
        mTvTitleBarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPoiListLv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 0;
                getPoiListData(type, curLoc.id);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getPoiListData(type, curLoc.id);
            }
        });
        if (canAdd) {
            mBtnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra("poiList", hasAddList);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        } else {
            mBtnOk.setVisibility(View.INVISIBLE);
        }


    }

    private void getPoiGuide(final String type, String cityId) {
        TravelApi.getDestPoiGuide(cityId, type, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<PoiGuideBean> poiGuideResult = CommonJson.fromJson(result, PoiGuideBean.class);
                if (poiGuideResult.code == 0) {
                    bindGuideView(poiGuideResult.result);
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }
        });
    }

    private void bindGuideView(PoiGuideBean result) {
        mTvCityName.setText(curLoc.zhName);
        mTvCityPoiDesc.setText(result.desc);
        if (result.images != null && result.images.size() > 0) {
            ImageLoader.getInstance().displayImage(result.images.get(0).url, mIvCityPoi, UILUtils.getDefaultOption());
        }
    }

    private void getPoiListData(String type, String cityId) {
        TravelApi.getPoiListByLoc(type, cityId, page, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson4List<PoiDetailBean> poiListResult = CommonJson4List.fromJson(result, PoiDetailBean.class);
                if (poiListResult.code == 0) {
                    bindView(poiListResult.result);
                    page++;
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }
        });
    }

    private void bindView(List<PoiDetailBean> result) {
        if (page == 0) {
            mPoiAdapter.getDataList().clear();
        }
        if (canAdd) {
            for (PoiDetailBean detailBean : result) {
                if (hasAddList.contains(detailBean)) {
                    detailBean.hasAdded = true;
                } else {
                    detailBean.hasAdded = false;
                }
            }
        }

        mPoiAdapter.getDataList().addAll(result);
        mPoiAdapter.notifyDataSetChanged();
        if (result == null
                || result.size() < BaseApi.PAGE_SIZE) {
            mPoiListLv.setHasMoreData(false);
            // ptrLv.setScrollLoadEnabled(false);
        } else {
            mPoiListLv.setHasMoreData(true);
            mPoiListLv.onPullUpRefreshComplete();
        }
        if (page == 0) {
            mPoiListLv.onPullUpRefreshComplete();
            mPoiListLv.onPullDownRefreshComplete();
        }
    }


}
