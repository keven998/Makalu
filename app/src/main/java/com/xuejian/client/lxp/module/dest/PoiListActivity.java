package com.xuejian.client.lxp.module.dest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.PoiGuideBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.account.StrategyManager;
import com.xuejian.client.lxp.common.api.BaseApi;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.module.PeachWebViewActivity;
import com.xuejian.client.lxp.module.dest.adapter.PoiAdapter;
import com.xuejian.client.lxp.module.dest.adapter.StringSpinnerAdapter;

import org.json.JSONObject;

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
    @InjectView(R.id.tv_city_poi_desc)
    TextView mTvCityPoiDesc;
    PoiAdapter mPoiAdapter;
    StringSpinnerAdapter mLocSpinnerAdapter;
    @InjectView(R.id.tv_title_bar_left)
    TextView mTvTitleBarLeft;
//    @InjectView(R.id.et_search)
//    EditText mEtSearch;
//    @InjectView(R.id.btn_search)
//    Button mBtnSearch;
    @InjectView(R.id.tv_search)
    TextView tv_search;
    @InjectView(R.id.tv_title_bar_title)
    TextView mTitle;
    private PullToRefreshListView mPoiListLv;
    private View headerView;
    private String type;
    private boolean canAdd;
    private List<LocBean> locList;
    private ArrayList<PoiDetailBean> hasAddList;
    private ArrayList<PoiDetailBean> originAddList=new ArrayList<PoiDetailBean>();
    private StrategyBean strategy;
    private int curPage = 0;
    private LocBean curLoc;
    private String mKeyWord;
    private boolean isFromCityDetail;
    private String value;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }
    @Override
    protected void onResume() {
        super.onResume();
//        if (type.equals(TravelApi.PeachType.RESTAURANTS)) {
//           MobclickAgent.onPageStart("page_delicacy_lists");
//        } else if (type.equals(TravelApi.PeachType.SHOPPING)) {
//           MobclickAgent.onPageStart("page_shopping_lists");
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (type.equals(TravelApi.PeachType.RESTAURANTS)) {
//            MobclickAgent.onPageEnd("page_delicacy_lists");
//        } else if (type.equals(TravelApi.PeachType.SHOPPING)) {
//            MobclickAgent.onPageEnd("page_shopping_lists");
//        }
    }

    private void initData() {
        type = getIntent().getStringExtra("type");
        canAdd = getIntent().getBooleanExtra("canAdd", false);
        strategy = getIntent().getParcelableExtra("strategy");
        if (!canAdd){
            locList=getIntent().getParcelableArrayListExtra("locList");
        }else {
            locList = strategy.localities;
            if(type.equals(TravelApi.PeachType.SHOPPING)){
                hasAddList = strategy.shopping;
            }else if(type.equals(TravelApi.PeachType.RESTAURANTS)){
                hasAddList = strategy.restaurant;
            }
            originAddList.addAll(hasAddList);
        }
        isFromCityDetail = getIntent().getBooleanExtra("isFromCityDetail",false);
        value = getIntent().getStringExtra("value");

//        if (locList.size() > 1) {
//            mLocSpinner.setVisibility(View.VISIBLE);
//        } else {
//            mLocSpinner.setVisibility(View.GONE);
//        }
        if (canAdd) {
            mTvTitleBarLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkAddDiff()){
                        savePoiStrategy();
                    }else {
                        Intent intent = new Intent();
                        intent.putParcelableArrayListExtra("poiList", hasAddList);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            });
        } else {
            mTvTitleBarLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        mPoiAdapter = new PoiAdapter(this, canAdd);
        mPoiAdapter.setAddStr("收集");
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
                if (poi.location != null && poi.location.coordinates != null) {
                    Uri mUri = Uri.parse("geo:" + poi.location.coordinates[1] + "," + poi.location.coordinates[0] + "?q=" + poi.zhName);
                    Intent mIntent = new Intent(Intent.ACTION_VIEW, mUri);
                    if (CommonUtils.checkIntent(PoiListActivity.this, mIntent)) {
                        startActivity(mIntent);
                    } else {
                        ToastUtil.getInstance(PoiListActivity.this).showToast("没有找到地图应用");
                    }

                }
            }
        });
        mPoiListLv.getRefreshableView().setAdapter(mPoiAdapter);
        List<String> cityStrList = new ArrayList<String>();
        for (LocBean locBean : locList) {
            cityStrList.add(locBean.zhName);
        }
        mLocSpinnerAdapter = new StringSpinnerAdapter(mContext, cityStrList);
        curLoc = locList.get(0);
//        mLocSpinner.setAdapter(mLocSpinnerAdapter);
//        mLocSpinner.setSelection(0, true);
//        mLocSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> spinner, View view, int position, long itemId) {
//                curLoc = locList.get(position);
//                mPoiListLv.onPullUpRefreshComplete();
//                mPoiListLv.onPullDownRefreshComplete();
////                mPoiListLv.doPullRefreshing(true, 200);
//                if (type.equals(TravelApi.PeachType.RESTAURANTS)) {
//                    mTitle.setText(String.format("吃在%s", curLoc.zhName));
//                    mTvPoiWantType.setImageResource(R.drawable.jingdian_food_eat);
//                } else if (type.equals(TravelApi.PeachType.SHOPPING)) {
//                    mTitle.setText(String.format("%s购物", curLoc.zhName));
//                    mTvPoiWantType.setImageResource(R.drawable.jingdian_shopping);
//                }
//                loadPageData();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        mBtnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mKeyWord = mEtSearch.getText().toString().trim();
//                if(TextUtils.isEmpty(mKeyWord)){
//                    ToastUtil.getInstance(mContext).showToast("请输入关键字");
//                    return;
//                }
//                Intent intent = new Intent(mContext, SearchPoiActivity.class);
//                intent.putExtra("keyword", mKeyWord);
//                intent.putExtra("type", type);
//                intent.putParcelableArrayListExtra("poiList", hasAddList);
//                intent.putExtra("loc", curLoc);
//                startActivityForResult(intent, AddPoiActivity.REQUEST_CODE_SEARCH_POI);
//            }
//        });

        if (type.equals(TravelApi.PeachType.RESTAURANTS)) {
            mTitle.setText(String.format("吃在%s", curLoc.zhName));
          //  mTvPoiWantType.setImageResource(R.drawable.jingdian_food_eat);
        } else if (type.equals(TravelApi.PeachType.SHOPPING)) {
            mTitle.setText(String.format("%s购物", curLoc.zhName));
         //   mTvPoiWantType.setImageResource(R.drawable.jingdian_shopping);
        }

        loadPageData();
    }

    @Override
    public void onBackPressed() {
        if(checkAddDiff()){
            savePoiStrategy();
        }else {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("poiList", hasAddList);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void savePoiStrategy(){
        final JSONObject jsonObject = new JSONObject();
        StrategyManager.putSaveGuideBaseInfo(jsonObject, PoiListActivity.this, strategy);
        StrategyManager.putRestaurantJson(PoiListActivity.this, jsonObject, strategy);
        StrategyManager.putShoppingJson(PoiListActivity.this, jsonObject ,strategy);

        DialogManager.getInstance().showLoadingDialog(PoiListActivity.this);
        TravelApi.saveGuide(strategy.id, jsonObject.toString(), new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                CommonJson<ModifyResult> saveResult = CommonJson.fromJson(result.toString(), ModifyResult.class);
                if (saveResult.code == 0) {
                    DialogManager.getInstance().dissMissLoadingDialog();
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra("poiList", hasAddList);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private boolean checkAddDiff(){
        boolean flag=false;
        if(originAddList.size()==hasAddList.size()){
            for(int i=0;i<originAddList.size();i++){
                if(!originAddList.get(i).id.equals(hasAddList.get(i).id)){
                    flag=true;
                }
            }
            return flag;
        }else{
            flag=true;
            return flag;
        }
    }

    private void initView() {
        setContentView(R.layout.activity_poi_list);
        PullToRefreshListView listView = (PullToRefreshListView) findViewById(R.id.lv_poi_list);
        mPoiListLv = listView;
        headerView = View.inflate(mContext, R.layout.view_poi_list_header, null);
        listView.getRefreshableView().addHeaderView(headerView);
        listView.setPullLoadEnabled(false);
        listView.setPullRefreshEnabled(false);
        listView.setScrollLoadEnabled(true);
        mPoiListLv.setHasMoreData(false);
        ButterKnife.inject(this);

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getPoiListData(type, curLoc.id, curPage + 1);
            }
        });
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sear_intent = new Intent(PoiListActivity.this, SearchAllActivity.class);
                startActivityWithNoAnim(sear_intent);
            }
        });
    }

    private void loadPageData() {
        DialogManager.getInstance().showModelessLoadingDialog(mContext);
        getPoiGuide(type, curLoc.id);
        getPoiListData(type, curLoc.id, 0);
    }

    private void getPoiGuide(final String type, String cityId) {
        TravelApi.getDestPoiGuide(cityId, type, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson<PoiGuideBean> poiGuideResult = CommonJson.fromJson(result, PoiGuideBean.class);
                if (poiGuideResult.code == 0) {
                    bindGuideView(poiGuideResult.result,value);
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                if (!isFinishing()) {
                    ToastUtil.getInstance(PoiListActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void bindGuideView(final PoiGuideBean result,String value) {

            if (TextUtils.isEmpty(result.desc)) {
                headerView.setVisibility(View.GONE);
            } else {
                headerView.setVisibility(View.VISIBLE);
//                if(isFromCityDetail) {
//                    mTvCityPoiDesc.setText(value);
//                }else{
                    mTvCityPoiDesc.setText(result.desc);
       //         }
            }
            findViewById(R.id.header).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                    if (type.equals(TravelApi.PeachType.RESTAURANTS)) {
                        MobclickAgent.onEvent(mContext, "event_delicacy_strategy");
                        intent.putExtra("url", result.detailUrl);
//                    intent.putExtra("title", String.format("%s吃什么", curLoc.zhName));
                        intent.putExtra("title", "美食攻略");
                    } else if (type.equals(TravelApi.PeachType.SHOPPING)) {
                        MobclickAgent.onEvent(mContext, "event_shopping_strategy");
                        intent.putExtra("url", result.detailUrl);
//                    intent.putExtra("title", String.format("%s买什么", curLoc.zhName));
                        intent.putExtra("title", "购物攻略");
                    }
                    startActivity(intent);

                }
            });
        }

    private void getPoiListData(final String type, final String cityId, final int page) {
        TravelApi.getPoiListByLoc(type, cityId, page, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                if (!curLoc.id.equals(cityId)) {
                    return;
                }
                CommonJson4List<PoiDetailBean> poiListResult = CommonJson4List.fromJson(result, PoiDetailBean.class);
                if (poiListResult.code == 0) {
                    curPage = page;
                    bindView(poiListResult.result);
                }
                if (curPage == 0) {
//                    mPoiListLv.onPullDownRefreshComplete();
                    mPoiListLv.onPullUpRefreshComplete();
                } else {
                    mPoiListLv.onPullUpRefreshComplete();
                }
                DialogManager.getInstance().dissMissModelessLoadingDialog();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                if (!isFinishing()) {
                    ToastUtil.getInstance(PoiListActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
                DialogManager.getInstance().dissMissModelessLoadingDialog();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

//        mEtSearch.clearFocus();
    }

    private void bindView(List<PoiDetailBean> result) {
        if (curPage == 0) {
            mPoiAdapter.getDataList().clear();
        }
        if (canAdd) {
            for (PoiDetailBean detailBean : result) {
                detailBean.hasAdded = hasAddList.contains(detailBean);
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
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == AddPoiActivity.REQUEST_CODE_SEARCH_POI) {
                hasAddList = data.getParcelableArrayListExtra("poiList");
                for (PoiDetailBean detailBean : mPoiAdapter.getDataList()) {
                    detailBean.hasAdded = hasAddList.contains(detailBean);
                }
                mPoiAdapter.notifyDataSetChanged();
            }
        }
    }


}
