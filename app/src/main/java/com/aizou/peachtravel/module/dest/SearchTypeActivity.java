package com.aizou.peachtravel.module.dest;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
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
import com.aizou.peachtravel.common.share.ICreateShareDialog;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.utils.IntentUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.dest.adapter.SearchAllAdapter;
import com.easemob.EMCallBack;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/9.
 */
public class SearchTypeActivity extends PeachBaseActivity {
    public final static int REQUEST_CODE_SEARCH_LOC=100;
    @InjectView(R.id.title_bar)
    TitleHeaderBar mTitleBar;
    @InjectView(R.id.loc_tv)
    TextView mLocTv;
    @InjectView(R.id.ll_loc)
    LinearLayout mLlLoc;
    @InjectView(R.id.search_type_lv)
    PullToRefreshListView mSearchTypeLv;
    int curPage=0;
    String type;
    String keyWord;
    SearchAllAdapter mAdapter;
    LocBean mLocBean;
    ArrayList<SearchTypeBean> typeBeans = new ArrayList<SearchTypeBean>();
    SearchTypeBean typeBean;
    String toId;
    int chatType;

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
        mSearchTypeLv.setHasMoreData(false);
        mSearchTypeLv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                searchSearchTypeData(0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                searchSearchTypeData(curPage+1);
            }
        });
        mTitleBar.getTitleTextView().setText("更多结果");
        mTitleBar.enableBackKey(true);
        mLlLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,SearchDestForPoiActivity.class);
                startActivityForResult(intent,REQUEST_CODE_SEARCH_LOC);
            }
        });
    }

    private void initData() {
        type = getIntent().getStringExtra("type");
        keyWord = getIntent().getStringExtra("keyWord");
        toId = getIntent().getStringExtra("toId");
        chatType = getIntent().getIntExtra("chatType",0);
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
        else if(type.equals("restaurant")){
            typeBean = new SearchTypeBean();
            typeBean.type = "restaurant";
            typeBean.resultList = new ArrayList();
            typeBeans.add(typeBean);
        }
        else if(type.equals("shopping")){
            typeBean = new SearchTypeBean();
            typeBean.type = "shopping";
            typeBean.resultList = new ArrayList();
            typeBeans.add(typeBean);
        }
        DialogManager.getInstance().showLoadingDialog(this);
        searchSearchTypeData(0);
    }
    private void setLoc(LocBean locBean){
        mLocBean = locBean;
        mLocTv.setText(locBean.zhName);
    }

    private void searchSearchTypeData(final int page){
        String locId = "";
        if(mLocBean != null) {
            locId = mLocBean.id;

        }
        TravelApi.searchForType(keyWord, type, locId, page, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<SearchAllBean> searchAllResult = CommonJson.fromJson(result,SearchAllBean.class);
                if(searchAllResult.code==0){
                    curPage = page;
                    bindView(searchAllResult.result);
                }
                if (curPage == 0) {
                    mSearchTypeLv.onPullUpRefreshComplete();
                    mSearchTypeLv.onPullDownRefreshComplete();
                }else{
                    mSearchTypeLv.onPullUpRefreshComplete();
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                    if (!isFinishing()) {
                        mSearchTypeLv.onPullUpRefreshComplete();
                        mSearchTypeLv.onPullDownRefreshComplete();
                        ToastUtil.getInstance(SearchTypeActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                    }
            }
        });

    }

    private void bindView(SearchAllBean result) {
        if (curPage == 0) {
            typeBean.resultList.clear();
        }
        boolean hasMore =true;
        if(type.equals("loc")){
            typeBean.resultList.addAll(result.locality);
            if(result.locality.size()<BaseApi.PAGE_SIZE){
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
        else if(type.equals("restaurant")){
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
            mAdapter.setOnSearchResultClickListener(new SearchAllAdapter.OnSearchResultClickListener() {
                @Override
                public void onMoreResultClick(String type) {

                }

                @Override
                public void onItemOnClick(String type,String id,Object object) {
                    if(!TextUtils.isEmpty(toId)){
                        IMUtils.showImShareDialog(mContext, (ICreateShareDialog)object, new IMUtils.OnDialogShareCallBack() {
                            @Override
                            public void onDialogShareOk(Dialog dialog, int type, String content) {
                                DialogManager.getInstance().showLoadingDialog(mContext);
                                IMUtils.sendExtMessage(mContext, type, content, chatType, toId, new EMCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        DialogManager.getInstance().dissMissLoadingDialog();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                ToastUtil.getInstance(mContext).showToast("已发送~");

                                            }
                                        });

                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        DialogManager.getInstance().dissMissLoadingDialog();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                ToastUtil.getInstance(mContext).showToast("好像发送失败了");

                                            }
                                        });

                                    }

                                    @Override
                                    public void onProgress(int i, String s) {

                                    }
                                });
                            }

                            @Override
                            public void onDialogShareCancle(Dialog dialog, int type, String content) {
                            }
                        });
                    }else{
                        IntentUtils.intentToDetail(SearchTypeActivity.this, type, id);
                    }

                }
            });
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


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==REQUEST_CODE_SEARCH_LOC){
                LocBean locBean =data.getParcelableExtra("loc");
                setLoc(locBean);
                searchSearchTypeData(0);
            }
        }
    }
}
