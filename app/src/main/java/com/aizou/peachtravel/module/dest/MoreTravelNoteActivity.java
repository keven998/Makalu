package com.aizou.peachtravel.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
import com.aizou.peachtravel.common.api.BaseApi;
import com.aizou.peachtravel.common.api.OtherApi;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.dest.adapter.TravelNoteViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/11.
 */
public class MoreTravelNoteActivity extends PeachBaseActivity {
    @InjectView(R.id.title_bar)
    TitleHeaderBar mTitleBar;
    @InjectView(R.id.more_travel_note_lv)
    PullToRefreshListView mMoreTravelNoteLv;
    ListViewDataAdapter mTravelNoteAdapter;
    int mPage=0;
    String locId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_travelnote);
        ButterKnife.inject(this);
        locId = getIntent().getStringExtra("id");

        mTitleBar.getTitleTextView().setText("更多游记");
        mTitleBar.enableBackKey(true);
        mMoreTravelNoteLv.setPullLoadEnabled(false);
        mMoreTravelNoteLv.setPullRefreshEnabled(false);
        mMoreTravelNoteLv.setScrollLoadEnabled(true);
        mTravelNoteAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                TravelNoteViewHolder viewHolder =  new TravelNoteViewHolder(false,false);
                return viewHolder;
            }
        });
        mMoreTravelNoteLv.getRefreshableView().setAdapter(mTravelNoteAdapter);
        mMoreTravelNoteLv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getTravelNoteList(0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getTravelNoteList(mPage + 1);
            }
        });
        mMoreTravelNoteLv.doPullRefreshing(true, 100);

    }



    private void getTravelNoteList(final int page){
        OtherApi.getTravelNoteByLocId(locId, page, BaseApi.PAGE_SIZE,new HttpCallBack < String > () {
            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissProgressDialog();
                CommonJson4List<TravelNoteBean> detailResult = CommonJson4List.fromJson(result, TravelNoteBean.class);
                if (detailResult.code == 0) {
                    mPage = page;
                    bindView(detailResult.result);

                }
                mMoreTravelNoteLv.onPullUpRefreshComplete();
                mMoreTravelNoteLv.onPullDownRefreshComplete();

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissProgressDialog();
                mMoreTravelNoteLv.onPullUpRefreshComplete();
                mMoreTravelNoteLv.onPullDownRefreshComplete();
            }
        });

    }

    private void bindView(List<TravelNoteBean> result) {
        if (result == null || result.size() == 0) {
            mMoreTravelNoteLv.setHasMoreData(false);
            if (mPage == 0) {
                Toast.makeText(MoreTravelNoteActivity.this, "没有任何收藏", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MoreTravelNoteActivity.this, "已列出全部收藏", Toast.LENGTH_SHORT).show();
            }
            return;
        } else {
            mMoreTravelNoteLv.setHasMoreData(true);
        }
        if (mPage == 0) {
            mTravelNoteAdapter.getDataList().clear();
        }
        mTravelNoteAdapter.getDataList().addAll(result);

        if (mTravelNoteAdapter.getCount() >= BaseApi.PAGE_SIZE) {
            mMoreTravelNoteLv.setScrollLoadEnabled(true);
        }
        mTravelNoteAdapter.notifyDataSetChanged();
    }
}
