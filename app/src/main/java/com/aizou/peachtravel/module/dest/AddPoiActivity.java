package com.aizou.peachtravel.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.common.api.BaseApi;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.module.dest.adapter.PoiAdapter;
import com.aizou.peachtravel.module.dest.adapter.StringSpinnerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/27.
 */
public class AddPoiActivity extends PeachBaseActivity {
    public final static int REQUEST_CODE_SEARCH_POI=101;
    @InjectView(R.id.lv_poi_list)
    PullToRefreshListView mLvPoiList;
    @InjectView(R.id.loc_spinner)
    Spinner mLocSpinner;
//    @InjectView(R.id.btn_ok)
//    Button mBtnOk;
    @InjectView(R.id.type_spinner)
    Spinner mTypeSpinner;
    @InjectView(R.id.et_search)
    EditText mEtSearch;
    @InjectView(R.id.btn_search)
    Button mBtnSearch;
    @InjectView(R.id.tv_title_bar_title)
    TextView mTilteView;
    private String mType;
    private List<LocBean> locList;
    private ArrayList<PoiDetailBean> hasAddList;
    private int dayIndex;
    private String[] poiTypeArray, poiTypeValueArray;

    private int curPage = 0;
    private LocBean curLoc;
    private PoiAdapter mPoiAdapter;
    private StringSpinnerAdapter mLocSpinnerAdapter, mTypeSpinnerAdapter;
    private String mKeyWord="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAccountAbout(true);
        initView();
        initData();

    }

    private void initView() {
        setContentView(R.layout.activity_add_poi);
        ButterKnife.inject(this);
        mLvPoiList.setPullLoadEnabled(false);
        mLvPoiList.setPullRefreshEnabled(false);
        mLvPoiList.setScrollLoadEnabled(true);
        mLvPoiList.setHasMoreData(false);
        mPoiAdapter = new PoiAdapter(mContext, true);
        mPoiAdapter.setOnPoiActionListener(new PoiAdapter.OnPoiActionListener() {
            @Override
            public void onPoiAdded(PoiDetailBean poi) {
                hasAddList.add(poi);
                mTilteView.setText(String.format("第%d天(%d安排)", dayIndex+1, hasAddList.size()));
            }

            @Override
            public void onPoiRemoved(PoiDetailBean poi) {
                hasAddList.remove(poi);
                mTilteView.setText(String.format("第%d天(%d安排)", dayIndex+1, hasAddList.size()));
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
        findViewById(R.id.lyt_btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("poiList", hasAddList);
                intent.putExtra("dayIndex", dayIndex);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyWord = mEtSearch.getText().toString().trim();
                if(TextUtils.isEmpty(mKeyWord)){
                    ToastUtil.getInstance(mContext).showToast("请输入关键字");
                    return;
                }
                Intent intent = new Intent(mContext,SearchPoiActivity.class);
                intent.putExtra("keyword",mKeyWord);
                intent.putExtra("type",mType);
                intent.putExtra("dayIndex",dayIndex);
                intent.putParcelableArrayListExtra("poiList", hasAddList);
                intent.putExtra("loc",curLoc);
                startActivityForResult(intent,REQUEST_CODE_SEARCH_POI);
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("poiList", hasAddList);
        intent.putExtra("dayIndex", dayIndex);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initSpinnerListener() {
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mType = poiTypeValueArray[position];
                mPoiAdapter.getDataList().clear();
                mPoiAdapter.notifyDataSetChanged();
                mLvPoiList.onPullUpRefreshComplete();
                mLvPoiList.onPullDownRefreshComplete();
                mLvPoiList.doPullRefreshing(true,500);
//                getPoiListByLoc(mType, curLoc.id, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mLocSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long itemId) {
                curLoc = locList.get(position);
                mPoiAdapter.getDataList().clear();
                mPoiAdapter.notifyDataSetChanged();
                mLvPoiList.onPullUpRefreshComplete();
                mLvPoiList.onPullDownRefreshComplete();
                mLvPoiList.doPullRefreshing(true,500);
//                getPoiListByLoc(mType, curLoc.id, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }


    private void initData() {
        dayIndex = getIntent().getIntExtra("dayIndex", -1);
        hasAddList = getIntent().getParcelableArrayListExtra("poiList");
        poiTypeArray = getResources().getStringArray(R.array.poi_type);
        poiTypeValueArray = getResources().getStringArray(R.array.poi_type_value);
        mTypeSpinnerAdapter = new StringSpinnerAdapter(mContext, Arrays.asList(poiTypeArray));
        mTypeSpinner.setAdapter(mTypeSpinnerAdapter);
        locList = getIntent().getParcelableArrayListExtra("locList");
        List<String> cityStrList = new ArrayList<String>();
        for (LocBean locBean : locList) {
            cityStrList.add(locBean.zhName);
        }
        mLocSpinnerAdapter = new StringSpinnerAdapter(mContext, cityStrList);
        mLocSpinner.setAdapter(mLocSpinnerAdapter);
        mTypeSpinner.setSelection(0, true);
        mLocSpinner.setSelection(0, true);
        initSpinnerListener();
        curLoc = locList.get(0);
        mType = poiTypeValueArray[0];
        mLvPoiList.doPullRefreshing(true,500);
//        getPoiListByLoc(mType, curLoc.id, 0);

        mTilteView.setText(String.format("第%d天(%d安排)", dayIndex+1, hasAddList.size()));
    }

    private void getPoiListByLoc(final String type, final String cityId, final int page) {

        TravelApi.getPoiListByLoc(type, cityId, page, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                if(!(mType.equals(type)&&curLoc.id.equals(cityId))){
                    return;
                }
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson4List<PoiDetailBean> poiListResult = CommonJson4List.fromJson(result, PoiDetailBean.class);
                if (poiListResult.code == 0) {
                    curPage = page;
                    bindView(poiListResult.result);
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
        });
    }

    private void bindView(List<PoiDetailBean> result) {
        if (curPage == 0) {
            mPoiAdapter.getDataList().clear();
        }
        for (PoiDetailBean detailBean : result) {
            if (hasAddList.contains(detailBean)) {
                detailBean.hasAdded = true;
            } else {
                detailBean.hasAdded = false;
            }
        }
        mPoiAdapter.getDataList().addAll(result);
        mPoiAdapter.notifyDataSetChanged();
        if (result == null
                || result.size() < BaseApi.PAGE_SIZE) {
            mLvPoiList.setHasMoreData(false);
            // ptrLv.setScrollLoadEnabled(false);
        } else {
            mLvPoiList.setHasMoreData(true);
            mLvPoiList.onPullUpRefreshComplete();
        }
        if (curPage == 0) {
            mLvPoiList.onPullUpRefreshComplete();
            mLvPoiList.onPullDownRefreshComplete();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==REQUEST_CODE_SEARCH_POI){
                hasAddList = data.getParcelableArrayListExtra("poiList");
                for (PoiDetailBean detailBean : mPoiAdapter.getDataList()) {
                    if (hasAddList.contains(detailBean)) {
                        detailBean.hasAdded = true;
                    } else {
                        detailBean.hasAdded = false;
                    }
                }
                mPoiAdapter.notifyDataSetChanged();
            }
        }
    }
}
