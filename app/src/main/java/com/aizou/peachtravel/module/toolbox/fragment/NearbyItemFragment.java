package com.aizou.peachtravel.module.toolbox.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.bean.SearchAllBean;
import com.aizou.peachtravel.common.api.BaseApi;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.aizou.peachtravel.module.dest.adapter.PoiAdapter;
import com.aizou.peachtravel.module.toolbox.NearbyActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/24.
 */
public class NearbyItemFragment extends PeachBaseFragment implements NearbyActivity.OnLocationChangeListener {
    @InjectView(R.id.fragment_mainTab_item_progressBar)
    ProgressBar mFragmentMainTabItemProgressBar;
    private int tabIndex;
    public String type;
    public double mLat, mLng;
    private PullToRefreshListView mListView;
    private int mPage = 0;
    private PoiAdapter poiAdapter;
    private List<PoiDetailBean> mPoiList;
    private View rootView;

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tabmain_item, container, false);
        ButterKnife.inject(this, rootView);
        type = getArguments().getString("type");
        mListView = (PullToRefreshListView) rootView.findViewById(R.id.nearby_lv);
        mListView.setPullLoadEnabled(false);
        mListView.setPullRefreshEnabled(true);
        mListView.setScrollLoadEnabled(true);
        poiAdapter = new PoiAdapter(getActivity(), false);
        mListView.getRefreshableView().setAdapter(poiAdapter);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPage = 0;
                getPoiListByLoc(mPage, mLat, mLng);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getPoiListByLoc(mPage, mLat, mLng);
            }
        });

        if(mPoiList==null){
            LogUtil.d(type+"----poiList=null");
            mLat = getArguments().getDouble("lat");
            mLng = getArguments().getDouble("lng");
            mPage=0;
            mPoiList = new ArrayList<>();
            getPoiListByLoc(mPage,mLat,mLng);
        }else{
            LogUtil.d(type+"----poiList!=null");
            poiAdapter.getDataList().clear();
            poiAdapter.getDataList().addAll(mPoiList);
            poiAdapter.notifyDataSetChanged();
        }


        return rootView;
    }

    private void getPoiListByLoc(int page, double lat, double lng) {
        TravelApi.getNearbyPoi(lat, lng, page, type, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<SearchAllBean> allResult = CommonJson.fromJson(result, SearchAllBean.class);
                if (allResult.code == 0) {
                    bindView(allResult.result);
                    mPage++;
                }
                mListView.onPullUpRefreshComplete();
                mListView.onPullDownRefreshComplete();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                mListView.onPullUpRefreshComplete();
                mListView.onPullDownRefreshComplete();
            }
        });


    }

    private void bindView(SearchAllBean result) {
        ArrayList<PoiDetailBean> poiList = null;
        if (type.equals(TravelApi.PoiType.SPOT)) {
            poiList = result.vs;
        } else if (type.equals(TravelApi.PoiType.RESTAURANTS)) {
            poiList = result.restaurant;
        } else if (type.equals(TravelApi.PoiType.SHOPPING)) {
            poiList = result.shopping;
        } else if (type.equals(TravelApi.PoiType.HOTEL)) {
            poiList = result.hotel;
        }
        if (mPage == 0) {
            poiAdapter = new PoiAdapter(getActivity(), false);
            mPoiList.clear();
            mListView.getRefreshableView().setAdapter(poiAdapter);
        }

        for(PoiDetailBean poi:poiList){
            if(poi.location!=null){
                String distance = CommonUtils.getDistanceStr(mLat,mLng,poi.location.coordinates[1],poi.location.coordinates[0]);
                poi.distance = distance;
            }
        }
        mPoiList.addAll(poiList);
        poiAdapter.getDataList().addAll(poiList);
        poiAdapter.notifyDataSetChanged();
        if (poiList == null || poiList.size() < BaseApi.PAGE_SIZE) {
            mListView.setHasMoreData(false);
            // ptrLv.setScrollLoadEnabled(false);
        } else {
            mListView.setHasMoreData(true);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onLocationChange(double lat, double lng) {
        mPage = 0;
        mLat =lat;
        mLng = lng;
        getPoiListByLoc(mPage, lat, lng);
    }
}
