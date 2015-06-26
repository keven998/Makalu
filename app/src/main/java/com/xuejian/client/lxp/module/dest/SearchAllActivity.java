package com.xuejian.client.lxp.module.dest;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.lv.Listener.SendMsgListener;
import com.lv.im.IMClient;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.SearchAllBean;
import com.xuejian.client.lxp.bean.SearchTypeBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.share.ICreateShareDialog;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.IntentUtils;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.module.dest.adapter.SearchAllAdapter;

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
    String toId;
    String chatType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_all);
        toId = getIntent().getStringExtra("toId");
        chatType = getIntent().getStringExtra("chatType");
        ButterKnife.inject(this);
        if (!TextUtils.isEmpty(toId)) {
            mTitleBar.getTitleTextView().setText("发送攻略");
        } else {
            mTitleBar.getTitleTextView().setText("旅行搜搜");
        }

        mTitleBar.findViewById(R.id.ly_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithNoAnim();
                overridePendingTransition(0, android.R.anim.fade_out);
            }
        });
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEtSearch.getText())) {
//                    ToastUtil.getInstance(mContext).showToast("你要找什么");
                    return;
                } else {
                    searchAll(mEtSearch.getText().toString().trim());
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    if (TextUtils.isEmpty(mEtSearch.getText())) {
//                        ToastUtil.getInstance(mContext).showToast("你要找什么");
                        return true;
                    } else {
                        searchAll(mEtSearch.getText().toString().trim());
                    }
                }
                return false;
            }
        });

        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mSearchAllLv.setAdapter(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        View emptyView = findViewById(R.id.empty_text);
        mSearchAllLv.setEmptyView(emptyView);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mEtSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEtSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 600);
    }


    @Override
    public void onBackPressed() {
        finishWithNoAnim();
        overridePendingTransition(0, android.R.anim.fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("page_search_destination");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_search_destination");
    }

    private void searchAll(final String keyword){
        DialogManager.getInstance().showLoadingDialog(this);
        TravelApi.searchAll(keyword, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<SearchAllBean> searchAllResult = CommonJson.fromJson(result, SearchAllBean.class);
                if (searchAllResult.code == 0) {
                    bindView(keyword, searchAllResult.result);
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing()) {
                    ToastUtil.getInstance(SearchAllActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
            }
        });

    }

    private void bindView(final String keyword,SearchAllBean result) {
        ArrayList<SearchTypeBean> typeBeans = new ArrayList<SearchTypeBean>();
        if(result.locality!=null&&result.locality.size()>0){
            SearchTypeBean searchTypeBean = new SearchTypeBean();
            searchTypeBean.type = "loc";
            searchTypeBean.resultList = result.locality;
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
        boolean isSend;
        isSend = !TextUtils.isEmpty(toId);
        SearchAllAdapter searchAllAdapter = new SearchAllAdapter(mContext, typeBeans, true,isSend);
        searchAllAdapter.setOnSearchResultClickListener(new SearchAllAdapter.OnSearchResultClickListener() {
            @Override
            public void onMoreResultClick(String type) {
                MobclickAgent.onEvent(mContext, "event_click_more_search_result");
                Intent intent = new Intent(mContext, SearchTypeActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("keyWord", keyword);
                intent.putExtra("chatType", chatType);
                intent.putExtra("toId", toId);
                startActivity(intent);
            }

            @Override
            public void onItemOnClick(String type, String id, Object object) {
                MobclickAgent.onEvent(mContext, "event_click_search_result_item");
                IntentUtils.intentToDetail(SearchAllActivity.this, type, id);
            }

            @Override
            public void onSendClick(String type, String id, Object object) {
                IMUtils.showImShareDialog(mContext, (ICreateShareDialog) object, new IMUtils.OnDialogShareCallBack() {
                    @Override
                    public void onDialogShareOk(Dialog dialog, int type, String content) {
                        DialogManager.getInstance().showLoadingDialog(mContext);
                        IMClient.getInstance().sendExtMessage(AccountManager.getCurrentUserId(),toId, chatType, content, type, new SendMsgListener() {
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
                            public void onFailed(int code) {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        ToastUtil.getInstance(mContext).showToast("好像发送失败了");

                                    }
                                });

                            }
                        });
                }

                @Override
                public void onDialogShareCancle (Dialog dialog,int type, String content){
                }
            }

            );
        }
    });
        mSearchAllLv.setAdapter(searchAllAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonUtils.fixInputMethodManagerLeak(this);
    }
}
