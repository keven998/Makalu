package com.aizou.peachtravel.module.dest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.SearchAllBean;
import com.aizou.peachtravel.common.api.BaseApi;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/18.
 */
public class SearchDestActivity extends PeachBaseActivity {

    @InjectView(R.id.ly_header_bar_title_wrap)
    TitleHeaderBar mTitleBar;
    @InjectView(R.id.et_search)
    EditText mEtSearch;
    @InjectView(R.id.btn_search)
    Button mBtnSearch;
    @InjectView(R.id.search_result_lv)
    PullToRefreshListView mSearchResultLv;
    private ListViewDataAdapter mSearchResultAdapter;
    private int curPage;
    private String mKeyWord ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_search_dest);
        ButterKnife.inject(this);
        mTitleBar.getTitleTextView().setText("搜索想去的城市");
        mTitleBar.enableBackKey(true);
        mSearchResultLv.setPullLoadEnabled(false);
        mSearchResultLv.setPullRefreshEnabled(false);
        mSearchResultLv.setScrollLoadEnabled(true);
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
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyWord = mEtSearch.getText().toString().trim();
                if (TextUtils.isEmpty(mKeyWord)) {
                    return;
                }
                DialogManager.getInstance().showLoadingDialog(SearchDestActivity.this);
                searchSearchLocData(mKeyWord, 0);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                mKeyWord = mEtSearch.getText().toString().trim();
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    if (TextUtils.isEmpty(mKeyWord)) {
//                        ToastUtil.getInstance(mContext).showToast("你要找什么");
                        return true;
                    } else {
                        DialogManager.getInstance().showLoadingDialog(SearchDestActivity.this);
                        searchSearchLocData(mKeyWord, 0);
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
                return new SearchResultViewHolder();
            }
        });
        mSearchResultLv.getRefreshableView().setAdapter(mSearchResultAdapter);
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
                    ToastUtil.getInstance(SearchDestActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
            }
        });
    }

    private void bindView(List<LocBean> result) {

        if (curPage == 0) {
            mSearchResultAdapter.getDataList().clear();
        }
        mSearchResultAdapter.getDataList().addAll(result);

        if (mSearchResultAdapter.getCount() >= BaseApi.PAGE_SIZE) {
            mSearchResultLv.setScrollLoadEnabled(true);
        }
        if (result == null || result.size() == 0) {
            mSearchResultLv.setHasMoreData(false);
            if (curPage == 0) {
                if (!isFinishing()) {
                    ToastUtil.getInstance(this).showToast("什么都没找到");
                }
            } else {
                if (!isFinishing()) {
                    ToastUtil.getInstance(this).showToast("已列出全部了");
                }
            }
        } else {
            mSearchResultLv.setHasMoreData(true);
        }
        mSearchResultAdapter.notifyDataSetChanged();
    }

    private class SearchResultViewHolder extends ViewHolderBase<LocBean> {
        private ImageView destIv;
        private TextView destNameTv;
        private View contentView;

        @Override
        public View createView(LayoutInflater layoutInflater) {
            contentView = layoutInflater.inflate(R.layout.row_dest_search, null);
            destIv = (ImageView) contentView.findViewById(R.id.iv_dest);
            destNameTv = (TextView) contentView.findViewById(R.id.tv_dest_name);
            return contentView;
        }

        @Override
        public void showData(int position, final LocBean itemData) {
            destNameTv.setText(itemData.zhName);
            if(itemData.images != null && itemData.images.size() > 0){
                ImageLoader.getInstance().displayImage(itemData.images.get(0).url, destIv, UILUtils.getRadiusOption(LocalDisplay.dp2px(2)));
            }
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
