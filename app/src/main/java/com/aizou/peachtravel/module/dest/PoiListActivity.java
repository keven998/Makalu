package com.aizou.peachtravel.module.dest;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.dest.adapter.PoiAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/24.
 */
public class PoiListActivity extends PeachBaseActivity {
    private PullToRefreshListView mPoiListLv;
    private String type;
    private List<LocBean> cityList;
    private int page = 0;
    private LocBean curCity;
    @InjectView(R.id.ly_header_bar_title_wrap)
    TitleHeaderBar mLyHeaderBarTitleWrap;
    @InjectView(R.id.iv_city_poi)
    ImageView mIvCityPoi;
    @InjectView(R.id.tv_city_name)
    TextView mTvCityName;
    @InjectView(R.id.tv_city_poi_desc)
    TextView mTvCityPoiDesc;
    PoiAdapter mPoiAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        type = getIntent().getStringExtra("type");
        cityList = getIntent().getParcelableArrayListExtra("cityList");
        curCity = cityList.get(0);
//        ImageLoader.getInstance().displayImage(result.images.get(0).url, mIvCityPoi, UILUtils.getDefaultOption());
//        mTvCityName.setText(result.zhName);
//        mTvCityPoiDesc.setText(result.desc);

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
        mPoiAdapter = new PoiAdapter(this, true);
        mPoiListLv.getRefreshableView().setAdapter(mPoiAdapter);
        mPoiListLv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 0;
                getPoiListData(type, curCity.id);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getPoiListData(type, curCity.id);
            }
        });


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
        mPoiAdapter.getDataList().addAll(result);
        mPoiAdapter.notifyDataSetChanged();
    }


}
