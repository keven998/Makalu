package com.xuejian.client.lxp.module.dest;

import android.os.Bundle;
import android.widget.ListView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.TravelNoteBean;
import com.xuejian.client.lxp.common.api.BaseApi;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.module.dest.adapter.TravelNoteViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Rjm on 2014/12/11.
 */
public class MoreTravelNoteActivity extends PeachBaseActivity {
    @Bind(R.id.title_bar)
    TitleHeaderBar mTitleBar;
    @Bind(R.id.more_travel_note_lv)
    PullToRefreshListView mMoreTravelNoteLv;
    ListViewDataAdapter mTravelNoteAdapter;
    int mPage = 0;
    String locId;
    String keyword;
    boolean isExpert;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_travelnote);
        ButterKnife.bind(this);
        isExpert = getIntent().getBooleanExtra("isExpert", false);
        if (isExpert) {
            locId = getIntent().getStringExtra("id");
            mTitleBar.getTitleTextView().setText(getIntent().getStringExtra("title"));
            mTitleBar.getTitleTextView().setTextColor(getResources().getColor(R.color.color_text_ii));
        } else {
            locId = getIntent().getStringExtra("id");
            keyword = getIntent().getStringExtra("keyword");
            mTitleBar.getTitleTextView().setText("全部游记");
        }

        mTitleBar.enableBackKey(true);

        mMoreTravelNoteLv.setPullLoadEnabled(false);
        mMoreTravelNoteLv.setPullRefreshEnabled(false);
        mMoreTravelNoteLv.setScrollLoadEnabled(true);
        mMoreTravelNoteLv.setHasMoreData(false);
        mTravelNoteAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                TravelNoteViewHolder viewHolder = new TravelNoteViewHolder(MoreTravelNoteActivity.this, false, false);
                return viewHolder;
            }
        });
        mMoreTravelNoteLv.getRefreshableView().setAdapter(mTravelNoteAdapter);
        mMoreTravelNoteLv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //  getTravelNoteList(0);
                if (isExpert) {
                    getExpertNote(locId,0);
                } else getTravelNoteListByKeyword(0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // getTravelNoteList(mPage + 1);
                if (isExpert) {

                } else getTravelNoteListByKeyword(mPage + 1);
            }
        });

        mMoreTravelNoteLv.doPullRefreshing(true, 100);

    }

    private void getExpertNote(String userId,int page) {
        UserApi.getUserTravelNote(userId, new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                try {
                    DialogManager.getInstance().dissMissLoadingDialog();
                }catch (Exception e){
                    e.printStackTrace();
                }
                CommonJson4List<TravelNoteBean> detailResult = CommonJson4List.fromJson(result.toString(), TravelNoteBean.class);
                if (detailResult.code == 0) {
              //      mPage = page;
                    bindView(detailResult.result);
                } else {
//                  ToastUtil.getInstance(MoreTravelNoteActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                }
                mMoreTravelNoteLv.onPullUpRefreshComplete();
                mMoreTravelNoteLv.onPullDownRefreshComplete();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                mMoreTravelNoteLv.onPullUpRefreshComplete();
                mMoreTravelNoteLv.onPullDownRefreshComplete();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //MobclickAgent.onPageStart("page_travel_notes_lists");
        //.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //MobclickAgent.onPageEnd("page_travel_notes_lists");
        // MobclickAgent.onPause(this);
    }

    private void getTravelNoteListByKeyword(final int page) {
        OtherApi.getTravelNoteByKeyword(keyword, page, BaseApi.PAGE_SIZE, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                try {
                    DialogManager.getInstance().dissMissLoadingDialog();
                }catch (Exception e){
                    e.printStackTrace();
                }
                CommonJson4List<TravelNoteBean> detailResult = CommonJson4List.fromJson(result, TravelNoteBean.class);
                if (detailResult.code == 0) {
                    mPage = page;
                    bindView(detailResult.result);
                } else {
//                  ToastUtil.getInstance(MoreTravelNoteActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                }
                mMoreTravelNoteLv.onPullUpRefreshComplete();
                mMoreTravelNoteLv.onPullDownRefreshComplete();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing()) {
                    mMoreTravelNoteLv.onPullUpRefreshComplete();
                    mMoreTravelNoteLv.onPullDownRefreshComplete();
                    ToastUtil.getInstance(MoreTravelNoteActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

    }

    private void getTravelNoteList(final int page) {
        OtherApi.getTravelNoteByLocId(locId, page, BaseApi.PAGE_SIZE, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson4List<TravelNoteBean> detailResult = CommonJson4List.fromJson(result, TravelNoteBean.class);
                if (detailResult.code == 0) {
                    mPage = page;
                    bindView(detailResult.result);
                } else {
//                  ToastUtil.getInstance(MoreTravelNoteActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                }
                mMoreTravelNoteLv.onPullUpRefreshComplete();
                mMoreTravelNoteLv.onPullDownRefreshComplete();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing()) {
                    mMoreTravelNoteLv.onPullUpRefreshComplete();
                    mMoreTravelNoteLv.onPullDownRefreshComplete();
                    ToastUtil.getInstance(MoreTravelNoteActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

    }

    private void bindView(List<TravelNoteBean> result) {
        if (result == null || result.size() == 0) {
            mMoreTravelNoteLv.setHasMoreData(false);
//            if (mPage == 0) {
//                if (!isFinishing()) {
//                    ToastUtil.getInstance(this).showToast("好像没找到收藏");
//                }
//            } else {
//                if (!isFinishing()) {
//                    ToastUtil.getInstance(this).showToast("已取完全部收藏啦");
//                }
//            }
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
