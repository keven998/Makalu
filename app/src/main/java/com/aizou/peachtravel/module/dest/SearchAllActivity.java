package com.aizou.peachtravel.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.aizou.core.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.SearchAllBean;
import com.aizou.peachtravel.bean.SearchTypeBean;
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
public class SearchAllActivity extends PeachBaseActivity {
    @InjectView(R.id.title_bar)
    TitleHeaderBar mTitleBar;
    @InjectView(R.id.et_search)
    EditText mEtSearch;
    @InjectView(R.id.btn_search)
    Button mBtnSearch;
    @InjectView(R.id.search_all_lv)
    ListView mSearchAllLv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_all);
        ButterKnife.inject(this);
        mTitleBar.getTitleTextView().setText("发送地点");
        mTitleBar.enableBackKey(true);
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mEtSearch.getText())){
                    ToastUtil.getInstance(mContext).showToast("请输入关键字");
                }else{
                    searchAll(mEtSearch.getText().toString().trim());
                }
            }
        });
    }


    private void searchAll(final String keyword){
        DialogManager.getInstance().showProgressDialog(this);
        TravelApi.searchAll(keyword,new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissProgressDialog();
                CommonJson<SearchAllBean> searchAllResult = CommonJson.fromJson(result,SearchAllBean.class);
                if(searchAllResult.code==0){
                    bindView(keyword,searchAllResult.result);
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissProgressDialog();
            }
        });

    }

    private void bindView(final String keyword,SearchAllBean result) {
        ArrayList<SearchTypeBean> typeBeans = new ArrayList<SearchTypeBean>();
        if(result.loc!=null&&result.loc.size()>0){
            SearchTypeBean searchTypeBean = new SearchTypeBean();
            searchTypeBean.type = "loc";
            searchTypeBean.resultList = result.loc;
            typeBeans.add(searchTypeBean);
        }
        if(result.vs!=null&&result.vs.size()>0){
            SearchTypeBean searchTypeBean = new SearchTypeBean();
            searchTypeBean.type = "vs";
            searchTypeBean.resultList = result.vs;
            typeBeans.add(searchTypeBean);
        }
        if(result.hotel!=null&&result.hotel.size()>0){
            SearchTypeBean searchTypeBean = new SearchTypeBean();
            searchTypeBean.type = "hotel";
            searchTypeBean.resultList = result.hotel;
            typeBeans.add(searchTypeBean);
        }
        if(result.restaurant!=null&&result.restaurant.size()>0){
            SearchTypeBean searchTypeBean = new SearchTypeBean();
            searchTypeBean.type = "restaurant";
            searchTypeBean.resultList = result.restaurant;
            typeBeans.add(searchTypeBean);
        }
        if(result.shopping!=null&&result.shopping.size()>0){
            SearchTypeBean searchTypeBean = new SearchTypeBean();
            searchTypeBean.type = "shopping";
            searchTypeBean.resultList = result.shopping;
            typeBeans.add(searchTypeBean);
        }
        SearchAllAdapter searchAllAdapter = new SearchAllAdapter(mContext,typeBeans,true);
        searchAllAdapter.setOnMoreResultClickListener(new SearchAllAdapter.OnMoreResultClickListener() {
            @Override
            public void onMoreResultClick(String type) {
                Intent intent = new Intent(mContext,SearchTypeActivity.class);
                intent.putExtra("type",type);
                intent.putExtra("keyWord",keyword);
                startActivity(intent);
            }
        });
        mSearchAllLv.setAdapter(searchAllAdapter);

    }

}
