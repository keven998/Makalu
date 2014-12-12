package com.aizou.peachtravel.module.dest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.aizou.core.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.TravelNoteBean;
import com.aizou.peachtravel.common.api.OtherApi;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.dest.adapter.TravelNoteViewHolder;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/11.
 */
public class TravelNoteSearchActivity extends PeachBaseActivity {
    @InjectView(R.id.title_bar)
    TitleHeaderBar mTitleBar;
    @InjectView(R.id.et_search)
    EditText mEtSearch;
    @InjectView(R.id.btn_search)
    Button mBtnSearch;
    @InjectView(R.id.search_travel_note_lv)
    PullToRefreshListView mSearchTravelNoteLv;
    ListViewDataAdapter mTravelNoteAdapter;
    int mPage=0;
    String mKeyWord="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_travelnote);
        ButterKnife.inject(this);
        mTitleBar.getTitleTextView().setText("发送游记");
        mTitleBar.enableBackKey(true);
        mSearchTravelNoteLv.setPullLoadEnabled(false);
        mSearchTravelNoteLv.setPullRefreshEnabled(false);
        mSearchTravelNoteLv.setScrollLoadEnabled(true);
        mTravelNoteAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                return new TravelNoteViewHolder(true,false);
            }
        });
        mSearchTravelNoteLv.getRefreshableView().setAdapter(mTravelNoteAdapter);
        mSearchTravelNoteLv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPage = 0;
                searchTravelNote(mKeyWord,mPage);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                searchTravelNote(mKeyWord,mPage);
            }
        });
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyWord = mEtSearch.getText().toString().trim();
                mPage=0;
                DialogManager.getInstance().showProgressDialog(TravelNoteSearchActivity.this);
                searchTravelNote(mKeyWord, mPage);
            }
        });

    }



    private void searchTravelNote(String keyWord,int page){
        OtherApi.getTravelNoteByKeyword(keyWord,page,new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissProgressDialog();
                CommonJson4List<TravelNoteBean> detailResult = CommonJson4List.fromJson(result, TravelNoteBean.class);
                if (detailResult.code == 0) {
                    mTravelNoteAdapter.getDataList().clear();
                    mTravelNoteAdapter.getDataList().addAll(detailResult.result);
                    mTravelNoteAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissProgressDialog();
                ToastUtil.getInstance(mContext).showToast("搜索失败");
            }
        });

    }
}
