package com.aizou.peachtravel.module.dest;

import android.content.Intent;
import android.net.Uri;
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
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.bean.PoiGuideBean;
import com.aizou.peachtravel.common.api.BaseApi;
import com.aizou.peachtravel.common.api.H5Url;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.aizou.peachtravel.module.PeachWebViewActivity;
import com.aizou.peachtravel.module.dest.adapter.PoiAdapter;
import com.aizou.peachtravel.module.dest.adapter.StringSpinnerAdapter;

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
    @InjectView(R.id.et_search)
    EditText mEtSearch;
    @InjectView(R.id.btn_search)
    Button mBtnSearch;
    @InjectView(R.id.tv_title_bar_title)
    TextView mTitle;
    @InjectView(R.id.tv_poi_want_type)
    TextView mTvPoiWantType;
    private PullToRefreshListView mPoiListLv;
    private View headerView;
    private String type;
    private boolean canAdd;
    private List<LocBean> locList;
    private ArrayList<PoiDetailBean> hasAddList;
    private int curPage = 0;
    private LocBean curLoc;
    private String mKeyWord;


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

//        if (canAdd) {
//            mBtnOk.setVisibility(View.VISIBLE);
//            mBtnOk.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent();
//                    intent.putParcelableArrayListExtra("poiList", hasAddList);
//                    setResult(RESULT_OK, intent);
//                    finish();
//                }
//            });
//        } else {
//            mBtnOk.setVisibility(View.INVISIBLE);
//        }
        if (locList.size() > 1) {
            mLocSpinner.setVisibility(View.VISIBLE);
        } else {
            mLocSpinner.setVisibility(View.GONE);
        }
        if (canAdd) {
            mTvTitleBarLeft.setText("完成");
            mTvTitleBarLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra("poiList", hasAddList);
                    setResult(RESULT_OK, intent);
                    finish();
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
                if(poi.location!=null&&poi.location.coordinates!=null){
                    Uri mUri = Uri.parse("geo:"+poi.location.coordinates[1]+","+poi.location.coordinates[0]+"?q="+poi.zhName);
                    Intent mIntent = new Intent(Intent.ACTION_VIEW,mUri);
                    if (CommonUtils.checkIntent(PoiListActivity.this, mIntent)){
                        startActivity(mIntent);
                    }else{
                        ToastUtil.getInstance(PoiListActivity.this).showToast("手机里没有地图软件哦");
                    }

                }
//                MapUtils.showSelectMapDialog(getActivity(),mLat,mLng,"我的位置",poi.location.coordinates[1],poi.location.coordinates[0],poi.zhName);
            }
        });
        mPoiListLv.getRefreshableView().setAdapter(mPoiAdapter);
        List<String> cityStrList = new ArrayList<String>();
        for (LocBean locBean : locList) {
            cityStrList.add(locBean.zhName);
        }
        mLocSpinnerAdapter = new StringSpinnerAdapter(mContext, cityStrList);
        curLoc = locList.get(0);

        mLocSpinner.setAdapter(mLocSpinnerAdapter);
        mLocSpinner.setSelection(0, true);
        mLocSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long itemId) {
                curLoc = locList.get(position);
                mPoiListLv.doPullRefreshing(true, 200);

                if (type.equals(TravelApi.PeachType.RESTAURANTS)) {
                    mTitle.setText(String.format("吃在%s", curLoc.zhName));
                    mTvPoiWantType.setText(String.format("%s吃什么?", curLoc.zhName));
                } else if (type.equals(TravelApi.PeachType.SHOPPING)) {
                    mTitle.setText(String.format("买在%s", curLoc.zhName));
                    mTvPoiWantType.setText(String.format("%s买什么?", curLoc.zhName));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyWord = mEtSearch.getText().toString().trim();
                Intent intent = new Intent(mContext, SearchPoiActivity.class);
                intent.putExtra("keyword", mKeyWord);
                intent.putExtra("type", type);
                intent.putParcelableArrayListExtra("poiList", hasAddList);
                intent.putExtra("loc", curLoc);
                startActivityForResult(intent, AddPoiActivity.REQUEST_CODE_SEARCH_POI);
            }
        });

        mPoiListLv.doPullRefreshing(true, 200);

        if (type.equals(TravelApi.PeachType.RESTAURANTS)) {
            mTitle.setText(String.format("吃在%s", curLoc.zhName));
            mTvPoiWantType.setText(String.format("%s吃什么?", curLoc.zhName));
        } else if (type.equals(TravelApi.PeachType.SHOPPING)) {
            mTitle.setText(String.format("买在%s", curLoc.zhName));
            mTvPoiWantType.setText(String.format("%s买什么?", curLoc.zhName));
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
                getPoiGuide(type, curLoc.id);
                getPoiListData(type, curLoc.id, 0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getPoiListData(type, curLoc.id, curPage + 1);
            }
        });
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
                if (!isFinishing()) {
                    ToastUtil.getInstance(PoiListActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
            }
        });
    }

    private void bindGuideView(final PoiGuideBean result) {
        if (TextUtils.isEmpty(result.desc)) {
            headerView.setVisibility(View.GONE);
        } else {
            mTvCityPoiDesc.setText(result.desc);
        }
        findViewById(R.id.header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                if (type.equals(TravelApi.PeachType.RESTAURANTS)) {
                    intent.putExtra("url", result.detailUrl);
                    intent.putExtra("title", String.format("%s吃什么", curLoc.zhName));
                } else if (type.equals(TravelApi.PeachType.SHOPPING)) {
                    intent.putExtra("url", result.detailUrl);
                    intent.putExtra("title", String.format("%s买什么", curLoc.zhName));
                }
                startActivity(intent);

            }
        });

    }

    private void getPoiListData(String type, String cityId, final int page) {
        TravelApi.getPoiListByLoc(type, cityId, page, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson4List<PoiDetailBean> poiListResult = CommonJson4List.fromJson(result, PoiDetailBean.class);
                if (poiListResult.code == 0) {
                    curPage = page;
                    bindView(poiListResult.result);
                }
                if (curPage == 0) {
                    mPoiListLv.onPullDownRefreshComplete();
                    mPoiListLv.onPullUpRefreshComplete();
                } else {
                    mPoiListLv.onPullUpRefreshComplete();
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                if (!isFinishing()) {
                    ToastUtil.getInstance(PoiListActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
            }
        });

        mEtSearch.clearFocus();
    }

    private void bindView(List<PoiDetailBean> result) {
        if (curPage == 0) {
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
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == AddPoiActivity.REQUEST_CODE_SEARCH_POI) {
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
