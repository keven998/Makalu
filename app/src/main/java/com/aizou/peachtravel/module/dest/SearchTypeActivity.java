package com.aizou.peachtravel.module.dest;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.dialog.DialogManager;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.SearchAllBean;
import com.aizou.peachtravel.bean.SearchTypeBean;
import com.aizou.peachtravel.common.api.BaseApi;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.dest.adapter.SearchAllAdapter;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/9.
 */
public class SearchTypeActivity extends PeachBaseActivity {
    @InjectView(R.id.title_bar)
    TitleHeaderBar mTitleBar;
    @InjectView(R.id.loc_tv)
    TextView mLocTv;
    @InjectView(R.id.ll_loc)
    LinearLayout mLlLoc;
    @InjectView(R.id.search_type_lv)
    PullToRefreshListView mSearchTypeLv;
    int page=0;
    String type;
    String keyWord;
    SearchAllAdapter mAdapter;
    LocBean locBean;
    ArrayList<SearchTypeBean> typeBeans = new ArrayList<SearchTypeBean>();
    SearchTypeBean typeBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_search_type);
        ButterKnife.inject(this);
        mSearchTypeLv.setPullLoadEnabled(false);
        mSearchTypeLv.setPullRefreshEnabled(false);
        mSearchTypeLv.setScrollLoadEnabled(true);
        mSearchTypeLv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 0;
                searchSearchTypeData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                searchSearchTypeData();
            }
        });
        mTitleBar.getTitleTextView().setText("更多结果");
        mTitleBar.enableBackKey(true);
    }

    private void initData() {
        type = getIntent().getStringExtra("type");
        keyWord = getIntent().getStringExtra("keyWord");
        if(type.equals("loc")){
            mLlLoc.setVisibility(View.GONE);
            typeBean = new SearchTypeBean();
            typeBean.type = "loc";
            typeBean.resultList = new ArrayList();
            typeBeans.add(typeBean);
        }else if(type.equals("vs")){
            typeBean = new SearchTypeBean();
            typeBean.type = "vs";
            typeBean.resultList = new ArrayList();
            typeBeans.add(typeBean);
        }
        else if(type.equals("hotel")){
            typeBean = new SearchTypeBean();
            typeBean.type = "hotel";
            typeBean.resultList = new ArrayList();
            typeBeans.add(typeBean);
        }
        else if(type.equals("restaurants")){
            typeBean = new SearchTypeBean();
            typeBean.type = "SearchTypeBean";
            typeBean.resultList = new ArrayList();
            typeBeans.add(typeBean);
        }
        else if(type.equals("shopping")){
            typeBean = new SearchTypeBean();
            typeBean.type = "shopping";
            typeBean.resultList = new ArrayList();
            typeBeans.add(typeBean);
        }
        DialogManager.getInstance().showProgressDialog(this);
        searchSearchTypeData();
    }

    private void searchSearchTypeData(){
        String locId="";
        if(locBean!=null){
            locId=locBean.id;

        }
        TravelApi.searchForType(keyWord,type,locId,page,new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissProgressDialog();
                CommonJson<SearchAllBean> searchAllResult = CommonJson.fromJson(result,SearchAllBean.class);
                if(searchAllResult.code==0){
                    bindView(searchAllResult.result);
                    page++;
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissProgressDialog();
            }
        });

    }

    private void bindView(SearchAllBean result) {
        if (page == 0) {
            typeBean.resultList.clear();
        }
        boolean hasMore =true;
        if(type.equals("loc")){
            typeBean.resultList.addAll(result.loc);
            if(result.loc.size()<BaseApi.PAGE_SIZE){
                hasMore=false;
            }
        }else if(type.equals("vs")){
            typeBean.resultList.addAll(result.vs);
            if(result.vs.size()<BaseApi.PAGE_SIZE){
                hasMore=false;
            }
        }
        else if(type.equals("hotel")){
            typeBean.resultList.addAll(result.hotel);
            if(result.hotel.size()<BaseApi.PAGE_SIZE){
                hasMore=false;
            }
        }
        else if(type.equals("restaurants")){
            typeBean.resultList.addAll(result.restaurant);
            if(result.restaurant.size()<BaseApi.PAGE_SIZE){
                hasMore=false;
            }
        }
        else if(type.equals("shopping")){
            typeBean.resultList.addAll(result.shopping);
            if(result.shopping.size()<BaseApi.PAGE_SIZE){
                hasMore=false;
            }
        }
        if(mAdapter==null){
            mAdapter = new SearchAllAdapter(mContext,typeBeans,false);
            mSearchTypeLv.getRefreshableView().setAdapter(mAdapter);
        }else{
            mAdapter.notifyDataSetChanged();
        }

        if (result == null
                || !hasMore) {
            mSearchTypeLv.setHasMoreData(false);
            // ptrLv.setScrollLoadEnabled(false);
        } else {
            mSearchTypeLv.setHasMoreData(true);
            mSearchTypeLv.onPullUpRefreshComplete();
        }
        if (page == 0) {
            mSearchTypeLv.onPullUpRefreshComplete();
            mSearchTypeLv.onPullDownRefreshComplete();
        }


    }

}
