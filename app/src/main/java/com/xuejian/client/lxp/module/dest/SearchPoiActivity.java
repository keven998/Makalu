package com.xuejian.client.lxp.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.SearchAllBean;
import com.xuejian.client.lxp.common.api.BaseApi;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.module.dest.adapter.PoiAdapter;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/27.
 */
public class SearchPoiActivity extends PeachBaseActivity {
    @InjectView(R.id.ly_header_bar_title_wrap)
    TitleHeaderBar titleHeaderBar;
    @InjectView(R.id.lv_poi_list)
    PullToRefreshListView mLvPoiList;
    private String mType;
    private ArrayList<PoiDetailBean> hasAddList;
    private int dayIndex;

    private int curPage = 0;
    private LocBean curLoc;
    private PoiAdapter mPoiAdapter;
    private String mKeyWord = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAccountAbout(true);
        initView();
        initData();

    }

    private void initView() {
        setContentView(R.layout.activity_search_poi);
        ButterKnife.inject(this);
        mLvPoiList.setPullLoadEnabled(false);
        mLvPoiList.setPullRefreshEnabled(false);
        mLvPoiList.setScrollLoadEnabled(true);
        mLvPoiList.setHasMoreData(false);
        mLvPoiList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                searchSearchTypeData(mKeyWord, mType, curLoc.id, 0);


            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                searchSearchTypeData(mKeyWord, mType, curLoc.id, curPage + 1);

            }
        });
        titleHeaderBar.getTitleTextView().setText("搜索结果");
        titleHeaderBar.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasAddList==null){
                    finish();
                }else{
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra("poiList", hasAddList);
                    intent.putExtra("dayIndex", dayIndex);
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });
    }


    private void initData() {
        dayIndex = getIntent().getIntExtra("dayIndex", -1);
        hasAddList = getIntent().getParcelableArrayListExtra("poiList");
        curLoc = getIntent().getParcelableExtra("loc");
        mType = getIntent().getStringExtra("type");
        mKeyWord = getIntent().getStringExtra("keyword");
        mLvPoiList.doPullRefreshing(true, 500);
        if(hasAddList==null){
            mPoiAdapter = new PoiAdapter(mContext, false);
        }else{
            mPoiAdapter = new PoiAdapter(mContext, true);
        }
        mPoiAdapter.setOnPoiActionListener(new PoiAdapter.OnPoiActionListener() {
            @Override
            public void onPoiAdded(PoiDetailBean poi) {
                hasAddList.add(poi);
            }

            @Override
            public void onPoiRemoved(PoiDetailBean poi) {
                hasAddList.remove(poi);

            }

            @Override
            public void onPoiNavi(PoiDetailBean poi) {

            }
        });
        mLvPoiList.getRefreshableView().setAdapter(mPoiAdapter);
//        getPoiListByLoc(mType, curLoc.id, 0);

    }


    private void searchSearchTypeData(String keyWord, final String type, String locId, final int page) {
        TravelApi.searchForType(keyWord, type, locId, page, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<SearchAllBean> searchAllResult = CommonJson.fromJson(result, SearchAllBean.class);
                if (searchAllResult.code == 0) {
                    curPage = page;
                    bindSearchView(type, searchAllResult.result);
                }
                if (curPage == 0) {
                    mLvPoiList.onPullDownRefreshComplete();
                } else {
                    mLvPoiList.onPullDownRefreshComplete();
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                mLvPoiList.onPullUpRefreshComplete();
                mLvPoiList.onPullDownRefreshComplete();
            }
        });
    }

    private void bindSearchView(String type, SearchAllBean result) {
        if (curPage == 0) {
            mPoiAdapter.getDataList().clear();
        }
        boolean hasMore = true;
        if (type.equals("vs")) {
            if(hasAddList!=null){
                for (PoiDetailBean detailBean : result.vs) {
                    if (hasAddList.contains(detailBean)) {
                        detailBean.hasAdded = true;
                    } else {
                        detailBean.hasAdded = false;
                    }
                }
            }
            mPoiAdapter.getDataList().addAll(result.vs);
            if (result.vs.size() < BaseApi.PAGE_SIZE) {
                hasMore = false;
            }
        } else if (type.equals("hotel")) {
            if(hasAddList!=null) {
                for (PoiDetailBean detailBean : result.hotel) {
                    if (hasAddList.contains(detailBean)) {
                        detailBean.hasAdded = true;
                    } else {
                        detailBean.hasAdded = false;
                    }
                }
            }
            mPoiAdapter.getDataList().addAll(result.hotel);
            if (result.hotel.size() < BaseApi.PAGE_SIZE) {
                hasMore = false;
            }
        } else if (type.equals("restaurant")) {
            if(hasAddList!=null) {
                for (PoiDetailBean detailBean : result.restaurant) {
                    if (hasAddList.contains(detailBean)) {
                        detailBean.hasAdded = true;
                    } else {
                        detailBean.hasAdded = false;
                    }
                }
            }
            mPoiAdapter.getDataList().addAll(result.restaurant);
            if (result.restaurant.size() < BaseApi.PAGE_SIZE) {
                hasMore = false;
            }
        } else if (type.equals("shopping")) {
            if(hasAddList!=null) {
                for (PoiDetailBean detailBean : result.shopping) {
                    if (hasAddList.contains(detailBean)) {
                        detailBean.hasAdded = true;
                    } else {
                        detailBean.hasAdded = false;
                    }
                }
            }
            mPoiAdapter.getDataList().addAll(result.shopping);
            if (result.shopping.size() < BaseApi.PAGE_SIZE) {
                hasMore = false;
            }
        }
        if (result == null
                || !hasMore) {
            mLvPoiList.setHasMoreData(false);
            // ptrLv.setScrollLoadEnabled(false);
        } else {
            mLvPoiList.setHasMoreData(true);
            mLvPoiList.onPullUpRefreshComplete();
        }
        mPoiAdapter.notifyDataSetChanged();
    }

}
