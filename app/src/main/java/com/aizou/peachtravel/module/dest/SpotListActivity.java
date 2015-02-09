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
public class SpotListActivity extends PeachBaseActivity {
    @InjectView(R.id.loc_spinner)
    Spinner mLocSpinner;
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
    private PullToRefreshListView mPoiListLv;
    //    private View headerView;
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
                if (poi.location != null && poi.location.coordinates != null) {
                    Uri mUri = Uri.parse("geo:" + poi.location.coordinates[1] + "," + poi.location.coordinates[0] + "?q=" + poi.zhName);
                    Intent mIntent = new Intent(Intent.ACTION_VIEW, mUri);
                    if (CommonUtils.checkIntent(SpotListActivity.this, mIntent)) {
                        startActivity(mIntent);
                    } else {
                        ToastUtil.getInstance(SpotListActivity.this).showToast("没有找到地图应用");
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
                mTitle.setText(String.format("%s景点", curLoc.zhName));
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
//        ImageLoader.getInstance().displayImage(result.images.get(0).url, mIvCityPoi, UILUtils.getDefaultOption());
//        mTvCityName.setText(result.zhName);
//        mTvCityPoiDesc.setText(result.desc);
        mPoiListLv.doPullRefreshing(true, 200);
        mTitle.setText(String.format("%s景点", curLoc.zhName));
    }

    private void initView() {
        setContentView(R.layout.activity_poi_list);
        PullToRefreshListView listView = (PullToRefreshListView) findViewById(R.id.lv_poi_list);
        mPoiListLv = listView;
//        headerView = View.inflate(mContext, R.layout.view_poi_list_header, null);
//        listView.getRefreshableView().addHeaderView(headerView);
        listView.setPullLoadEnabled(false);
        listView.setPullRefreshEnabled(false);
        listView.setScrollLoadEnabled(true);
        mPoiListLv.setHasMoreData(false);
        ButterKnife.inject(this);

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getPoiListData(type, curLoc.id, 0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getPoiListData(type, curLoc.id, curPage + 1);
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
                    ToastUtil.getInstance(SpotListActivity.this).showToast(getResources().getString(R.string.request_network_failed));
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
