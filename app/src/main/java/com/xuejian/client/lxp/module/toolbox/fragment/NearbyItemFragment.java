package com.xuejian.client.lxp.module.toolbox.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.SearchAllBean;
import com.xuejian.client.lxp.common.api.BaseApi;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.MapUtils;
import com.xuejian.client.lxp.module.dest.adapter.PoiAdapter;
import com.xuejian.client.lxp.module.toolbox.NearbyActivity;

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
//        mLat = getArguments().getDouble("lat");
//        mLng = getArguments().getDouble("lng");

        PullToRefreshListView listView = (PullToRefreshListView) rootView.findViewById(R.id.nearby_lv);
        mListView = listView;
        listView.setPullLoadEnabled(false);
        listView.setPullRefreshEnabled(false);
        listView.setScrollLoadEnabled(false);
        poiAdapter = new PoiAdapter(getActivity(), false);
        poiAdapter.setOnPoiActionListener(new PoiAdapter.OnPoiActionListener() {
            @Override
            public void onPoiAdded(PoiDetailBean poi) {

            }

            @Override
            public void onPoiRemoved(PoiDetailBean poi) {

            }

            @Override
            public void onPoiNavi(PoiDetailBean poi) {
                if(poi.location!=null&&poi.location.coordinates!=null){
                    MobclickAgent.onEvent(getActivity(),"event_go_navigation");
                    Uri mUri = Uri.parse("geo:"+poi.location.coordinates[1]+","+poi.location.coordinates[0]+"?q="+poi.zhName);
                    Intent mIntent = new Intent(Intent.ACTION_VIEW,mUri);
                    if (CommonUtils.checkIntent(getActivity(), mIntent)){
                        startActivity(mIntent);
                    }else{
                        ToastUtil.getInstance(getActivity()).showToast("没有找到地图应用");
                    }

                }
//                MapUtils.showSelectMapDialog(getActivity(),mLat,mLng,"我的位置",poi.location.coordinates[1],poi.location.coordinates[0],poi.zhName);
            }
        });
        listView.getRefreshableView().setAdapter(poiAdapter);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getPoiListByLoc(0, mLat, mLng);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getPoiListByLoc(mPage + 1, mLat, mLng);
            }
        });

        if (mPoiList == null) {
            mPage = 0;
            mPoiList = new ArrayList<>();
        } else {
            poiAdapter.getDataList().clear();
            poiAdapter.getDataList().addAll(mPoiList);
            poiAdapter.notifyDataSetChanged();
        }

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void updateLocation(double lat, double lng) {
        mLat = lat;
        mLng = lng;
        mPage = 0;
        if(mPoiList!=null&&poiAdapter!=null){
            mPoiList.clear();
            poiAdapter.reset();
        }

    }

    public void requestDataUpdate() {
        mListView.doPullRefreshing(true,0);
    }

    public void requestDataForInit() {
        if (mPoiList.size() == 0) {
            if (mLat != -1 && mLng != -1) {
                mListView.doPullRefreshing(true,0);
            }
        }
    }

    private void getPoiListByLoc(final int page, double lat, double lng) {
        TravelApi.getNearbyPoi(lat, lng, page, type, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson<SearchAllBean> allResult = CommonJson.fromJson(result, SearchAllBean.class);
                if (allResult.code == 0) {
                    mPage = page;
                    bindView(allResult.result);
                }
                mListView.onPullUpRefreshComplete();
                mListView.onPullDownRefreshComplete();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                if (isAdded()) {
                    mListView.onPullUpRefreshComplete();
                    mListView.onPullDownRefreshComplete();
                    ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));

                }
            }
        });


    }

    private void bindView(SearchAllBean result) {
        ArrayList<PoiDetailBean> poiList = null;
        if (type.equals(TravelApi.PeachType.SPOT)) {
            poiList = result.vs;
        } else if (type.equals(TravelApi.PeachType.RESTAURANTS)) {
            poiList = result.restaurant;
        } else if (type.equals(TravelApi.PeachType.SHOPPING)) {
            poiList = result.shopping;
        } else if (type.equals(TravelApi.PeachType.HOTEL)) {
            poiList = result.hotel;
        } else {
            poiList = new ArrayList<>();
        }
        if (mPage == 0) {
            poiAdapter = new PoiAdapter(getActivity(), false);
            poiAdapter.setOnPoiActionListener(new PoiAdapter.OnPoiActionListener() {
                @Override
                public void onPoiAdded(PoiDetailBean poi) {

                }

                @Override
                public void onPoiRemoved(PoiDetailBean poi) {

                }

                @Override
                public void onPoiNavi(PoiDetailBean poi) {
                    MapUtils.showSelectMapDialog(getActivity(), mLat, mLng, "我的位置", poi.location.coordinates[1], poi.location.coordinates[0], poi.zhName);
                }
            });
            mPoiList.clear();
            mListView.getRefreshableView().setAdapter(poiAdapter);
        }

        for (PoiDetailBean poi : poiList) {
            if(poi.location!=null){
                String distance = CommonUtils.getNearbyDistance(mLat, mLng, poi.location.coordinates[1], poi.location.coordinates[0]);
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

        if (poiAdapter.getCount() >= TravelApi.PAGE_SIZE) {
            mListView.setScrollLoadEnabled(true);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onLocationChange(double lat, double lng) {
        mLat = lat;
        mLng = lng;
        mPoiList.clear();
        mPage = 0;
        poiAdapter.reset();
    }
}
