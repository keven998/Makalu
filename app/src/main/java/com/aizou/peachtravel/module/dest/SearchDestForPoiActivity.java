package com.aizou.peachtravel.module.dest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.SearchAllBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/18.
 */
public class SearchDestForPoiActivity extends PeachBaseActivity {

    @InjectView(R.id.ly_header_bar_title_wrap)
    TitleHeaderBar mTitleBar;
    @InjectView(R.id.et_search)
    EditText mEtSearch;
    @InjectView(R.id.search_result_lv)
    PullToRefreshListView mSearchResultLv;
    @InjectView(R.id.ll_loading)
    ProgressBar mProgressBar;
    private ListViewDataAdapter mSearchResultAdapter;
    private int curPage;
    private String mKeyWord = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_search_dest_for_poi);
        ButterKnife.inject(this);
        mTitleBar.getTitleTextView().setText("选择所在城市");
        mTitleBar.enableBackKey(true);
        mSearchResultLv.setPullLoadEnabled(false);
        mSearchResultLv.setPullRefreshEnabled(false);
        mSearchResultLv.setScrollLoadEnabled(false);
        mSearchResultLv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                searchSearchLocData(mKeyWord, 0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                searchSearchLocData(mKeyWord, curPage + 1);
            }
        });
//        mBtnSearch.setVisibility(View.GONE);
//        mBtnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mKeyWord = mEtSearch.getText().toString().trim();
//                if (TextUtils.isEmpty(mKeyWord)) {
//                    return;
//                }
//                DialogManager.getInstance().showLoadingDialog(SearchDestForPoiActivity.this);
//                searchSearchLocData(mKeyWord, 0);
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//            }
//        });

        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                mKeyWord = mEtSearch.getText().toString().trim();
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    if (TextUtils.isEmpty(mKeyWord)) {
//                        ToastUtil.getInstance(mContext).showToast("你要找什么");
                        mSearchResultAdapter.getDataList().clear();
                        mSearchResultAdapter.notifyDataSetChanged();
                        return true;
                    } else {
                        DialogManager.getInstance().showLoadingDialog(SearchDestForPoiActivity.this);
                        searchSearchLocData(mKeyWord, 0);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });

    }

    private void initData() {
        mSearchResultAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                return new SearchResultForPoiViewHolder();
            }
        });
        mSearchResultLv.getRefreshableView().setAdapter(mSearchResultAdapter);
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    mSearchResultAdapter.getDataList().clear();
                    mSearchResultAdapter.notifyDataSetChanged();
                }else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    suggestSearchLocData(s.toString().trim());
                }
            }
        });
    }

    private void suggestSearchLocData(String keyWord){
        TravelApi.suggestLoc(keyWord, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<SearchAllBean> searchAllResult = CommonJson.fromJson(result, SearchAllBean.class);
                if (searchAllResult.code == 0) {
                    bindView(searchAllResult.result.locality);
                }

                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                if (!isFinishing()) {
                    ToastUtil.getInstance(SearchDestForPoiActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void searchSearchLocData(String keyWord, final int page){
        TravelApi.searchLoc(keyWord, page, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<SearchAllBean> searchAllResult = CommonJson.fromJson(result, SearchAllBean.class);
                if (searchAllResult.code == 0) {
                    curPage = page;
                    bindView(searchAllResult.result.locality);
                }
                mSearchResultLv.onPullUpRefreshComplete();
                mSearchResultLv.onPullDownRefreshComplete();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing()) {
                    mSearchResultLv.onPullUpRefreshComplete();
                    mSearchResultLv.onPullDownRefreshComplete();
                    ToastUtil.getInstance(SearchDestForPoiActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
            }
        });

    }
    private void bindView(List<LocBean> result) {
        mSearchResultAdapter.getDataList().clear();
        mSearchResultAdapter.getDataList().addAll(result);
        mSearchResultAdapter.notifyDataSetChanged();
    }

    private class SearchResultForPoiViewHolder extends ViewHolderBase<LocBean> {
//        private ImageView destIv;
        private TextView destNameTv;
        private View contentView;

        @Override
        public View createView(LayoutInflater layoutInflater) {
            contentView = layoutInflater.inflate(R.layout.row_dest_search_for_type, null);
//            destIv = (ImageView) contentView.findViewById(R.id.iv_dest);
            destNameTv = (TextView) contentView.findViewById(R.id.tv_dest_name);
            return contentView;
        }

        @Override
        public void showData(int position, final LocBean itemData) {
            destNameTv.setText(itemData.zhName);
//            if(itemData.images!=null&&itemData.images.size()>0){
//                ImageLoader.getInstance().displayImage(itemData.images.get(0).url, destIv, UILUtils.getRadiusOption(LocalDisplay.dp2px(2)));
//            }
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("loc",itemData);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            });
        }
    }
}
