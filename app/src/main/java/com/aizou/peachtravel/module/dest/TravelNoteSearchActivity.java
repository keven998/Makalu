package com.aizou.peachtravel.module.dest;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.peachtravel.common.dialog.DialogManager;
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
                return new TravelNoteViewHolder(TravelNoteSearchActivity.this, true, false);
            }
        });
        mSearchTravelNoteLv.getRefreshableView().setAdapter(mTravelNoteAdapter);
        mSearchTravelNoteLv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                searchTravelNote(mKeyWord, 0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                searchTravelNote(mKeyWord, mPage+1);
            }
        });
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyWord = mEtSearch.getText().toString();
                if (TextUtils.isEmpty(mKeyWord) || TextUtils.isEmpty(mKeyWord.trim())) {
                    return;
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                DialogManager.getInstance().showLoadingDialog(TravelNoteSearchActivity.this);
                searchTravelNote(mKeyWord, 0);
            }
        });

        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    mKeyWord = mEtSearch.getText().toString();
                    if (TextUtils.isEmpty(mKeyWord) || TextUtils.isEmpty(mKeyWord.trim())) {
                        return true;
                    } else {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                        DialogManager.getInstance().showLoadingDialog(TravelNoteSearchActivity.this);
                        searchTravelNote(mKeyWord, 0);
                    }
                }
                return false;
            }
        });
    }

    private void searchTravelNote(String keyWord, final int page) {
        OtherApi.getTravelNoteByKeyword(keyWord,page,new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson4List<TravelNoteBean> detailResult = CommonJson4List.fromJson(result, TravelNoteBean.class);
                if (detailResult.code == 0) {
                    bindView(detailResult.result);
                    mPage = page;
                }
                mSearchTravelNoteLv.onPullUpRefreshComplete();
                mSearchTravelNoteLv.onPullDownRefreshComplete();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                mSearchTravelNoteLv.onPullUpRefreshComplete();
                mSearchTravelNoteLv.onPullDownRefreshComplete();
                if (!isFinishing())
                ToastUtil.getInstance(TravelNoteSearchActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });

    }

    private void bindView(List<TravelNoteBean> result) {
        if (mPage == 0) {
            mTravelNoteAdapter.getDataList().clear();
        }
        mTravelNoteAdapter.getDataList().addAll(result);

        if (mTravelNoteAdapter.getCount() >= BaseApi.PAGE_SIZE) {
            mSearchTravelNoteLv.setScrollLoadEnabled(true);
        }
        if (result == null || result.size() == 0) {
            mSearchTravelNoteLv.setHasMoreData(false);
            if (mPage == 0) {
                ToastUtil.getInstance(this).showToast("没找到相关游记");
            } else {
                ToastUtil.getInstance(this).showToast("已列出全部");
            }
        } else {
            mSearchTravelNoteLv.setHasMoreData(true);
        }
    }
}
