package com.xuejian.client.lxp.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
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
import com.xuejian.client.lxp.module.dest.adapter.PoiAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yibiao.qin on 2015/7/21.
 */
public class SearchForPoi extends PeachBaseActivity {
    PoiAdapter mPoiAdapter;
    @InjectView(R.id.tv_title_bar_left)
    TextView mTvTitleBarLeft;
    @InjectView(R.id.et_search)
    EditText mEtSearch;
    @InjectView(R.id.btn_search)
    Button mBtnSearch;
    @InjectView(R.id.tv_title_bar_title)
    TextView mTitle;
    private PullToRefreshListView mPoiListLv;
    private String mType;
    private boolean canAdd;
    private List<LocBean> locList;
    private ArrayList<PoiDetailBean> AddList;
    private int curPage = 0;
    private LocBean curLoc;
    private String mKeyWord;
    private boolean isCanAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_poi);
        initView();
        initData();
    }

    private void initData() {
        AddList = new ArrayList<>();
        curLoc = getIntent().getParcelableExtra("loc");
        mType = getIntent().getStringExtra("type");
        isCanAdd = getIntent().getBooleanExtra("isCanAdd", false);
        if ("restaurant".equals(mType)) {
            mTitle.setText("美食搜索");
        }else if ("shopping".equals(mType)) {
            mTitle.setText("购物搜索");
        }else if ("vs".equals(mType)) {
            mTitle.setText("景点搜索");
        }
    }

    private void initView() {
        ButterKnife.inject(this);
        PullToRefreshListView listView = (PullToRefreshListView) findViewById(R.id.lv_poi_list);
        mPoiListLv = listView;
        mPoiAdapter = new PoiAdapter(SearchForPoi.this, isCanAdd);
        listView.setPullLoadEnabled(false);
        listView.setPullRefreshEnabled(false);
        listView.setScrollLoadEnabled(true);
        mPoiListLv.setHasMoreData(false);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                searchSearchTypeData(mKeyWord, mType, curLoc.id, 0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                searchSearchTypeData(mKeyWord, mType, curLoc.id, curPage + 1);
            }
        });
        listView.getRefreshableView().setAdapter(mPoiAdapter);
        mPoiAdapter.setOnPoiActionListener(new PoiAdapter.OnPoiActionListener() {
            @Override
            public void onPoiAdded(PoiDetailBean poi) {
                AddList.add(poi);
            }

            @Override
            public void onPoiRemoved(PoiDetailBean poi) {
                AddList.remove(poi);
            }

            @Override
            public void onPoiNavi(PoiDetailBean poi) {

            }
        });
        mTvTitleBarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("newPoi", AddList);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyWord = mEtSearch.getText().toString().trim();
                if (TextUtils.isEmpty(mKeyWord)) {
                    ToastUtil.getInstance(mContext).showToast("请输入关键字");
                    return;
                }
                searchSearchTypeData(mKeyWord, mType, curLoc.id, curPage);
//                Intent intent = new Intent(mContext, SearchPoiActivity.class);
//                intent.putExtra("keyword", mKeyWord);
//                intent.putExtra("type", type);
//                intent.putParcelableArrayListExtra("poiList", hasAddList);
//                intent.putExtra("loc", curLoc);
//                startActivityForResult(intent, AddPoiActivity.REQUEST_CODE_SEARCH_POI);
            }
        });
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
                    mPoiListLv.onPullDownRefreshComplete();
                } else {
                    mPoiListLv.onPullDownRefreshComplete();
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                mPoiListLv.onPullUpRefreshComplete();
                mPoiListLv.onPullDownRefreshComplete();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void bindSearchView(String type, SearchAllBean result) {
        if (curPage == 0) {
            mPoiAdapter.getDataList().clear();
        }
        boolean hasMore = true;
        if (type.equals("restaurant")) {
//            if (hasAddList != null) {
//                for (PoiDetailBean detailBean : result.restaurant) {
//                    detailBean.hasAdded = hasAddList.contains(detailBean);
//                }
//            }
            mPoiAdapter.getDataList().addAll(result.restaurant);
            mPoiAdapter.notifyDataSetChanged();
            if (result.restaurant.size() < BaseApi.PAGE_SIZE) {
                hasMore = false;
            }
        } else if (type.equals("shopping")) {
//            if (hasAddList != null) {
//                for (PoiDetailBean detailBean : result.shopping) {
//                    detailBean.hasAdded = hasAddList.contains(detailBean);
//                }
//            }
            mPoiAdapter.getDataList().addAll(result.shopping);
            mPoiAdapter.notifyDataSetChanged();
            if (result.shopping.size() < BaseApi.PAGE_SIZE) {
                hasMore = false;
            }
        }else if (type.equals("vs")) {
//            if (hasAddList != null) {
//                for (PoiDetailBean detailBean : result.shopping) {
//                    detailBean.hasAdded = hasAddList.contains(detailBean);
//                }
//            }
            mPoiAdapter.getDataList().addAll(result.vs);
            mPoiAdapter.notifyDataSetChanged();
            if (result.vs.size() < BaseApi.PAGE_SIZE) {
                hasMore = false;
            }
        }
        if (result == null
                || !hasMore) {
            mPoiListLv.setHasMoreData(false);
            // ptrLv.setScrollLoadEnabled(false);
        } else {
            mPoiListLv.setHasMoreData(true);
            mPoiListLv.onPullUpRefreshComplete();
        }

    }
}
