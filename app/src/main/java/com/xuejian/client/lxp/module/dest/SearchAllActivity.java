package com.xuejian.client.lxp.module.dest;

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
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.SharePrefUtil;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.KeywordBean;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.PoiGuideBean;
import com.xuejian.client.lxp.bean.SearchAllBean;
import com.xuejian.client.lxp.bean.SearchTypeBean;
import com.xuejian.client.lxp.bean.TravelNoteBean;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.IntentUtils;
import com.xuejian.client.lxp.common.widget.TagView.Tag;
import com.xuejian.client.lxp.common.widget.TagView.TagListView;
import com.xuejian.client.lxp.common.widget.TagView.TagView;
import com.xuejian.client.lxp.module.PeachWebViewActivity;
import com.xuejian.client.lxp.module.dest.adapter.SearchResultAdapter;
import com.xuejian.client.lxp.module.dest.adapter.TravelNoteViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Rjm on 2014/12/9.
 */
public class SearchAllActivity extends PeachBaseActivity {
    @Bind(R.id.et_search)
    EditText mEtSearch;
    @Bind(R.id.btn_search)
    TextView mBtnSearch;
    @Bind(R.id.search_all_lv)
    ListView mSearchAllLv;
    @Bind(R.id.cleanHistory)
    TextView cleanHistory;
    @Bind(R.id.history_pannel)
    FrameLayout history_pannel;
    @Bind(R.id.recomend_tag)
    TagListView recomend_tag;
    @Bind(R.id.history_lebel)
    TextView historyLebel;
    @Bind(R.id.history_tag)
    TagListView history_tag;
    String toId;
    String chatType;
    Object temp;
    String currentType;
    String conversation;
    String type;
    private final List<Tag> mTags = new ArrayList<Tag>();
    private final List<Tag> mKeyTags = new ArrayList<Tag>();
    private String[] keys;
    View headerView;
    LinearLayout header;
    TravelNoteBean noteBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_all);
        toId = getIntent().getStringExtra("toId");
        chatType = getIntent().getStringExtra("chatType");
        conversation = getIntent().getStringExtra("conversation");
        type = getIntent().getStringExtra("type");
        ButterKnife.bind(this);
        cleanHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                history_tag.cleanTags();
                history_pannel.setVisibility(View.GONE);
                SharePrefUtil.saveHistory(mContext, String.format("%s_his", type), "");
            }
        });
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBtnSearch.getText().toString().trim().equals("取消")) {
                    finishWithNoAnim();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    if (TextUtils.isEmpty(mEtSearch.getText())) {
                        return;
                    } else {
                        searchAll(mEtSearch.getText().toString().trim());
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    mBtnSearch.setText("取消");
                }
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
                mBtnSearch.setText("取消");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mSearchAllLv.setAdapter(null);
                    mBtnSearch.setText("取消");
                }
                if (s.length() > 0) {
                    mBtnSearch.setText("搜索");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        View emptyView = findViewById(R.id.empty_text);


        mSearchAllLv.setEmptyView(emptyView);
        mSearchAllLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mEtSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEtSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 600);
     //   history_tag = (TagListView) findViewById(R.id.history_tag);
        setUpData();
        history_tag.setOnTagClickListener(new TagListView.OnTagClickListener() {
            @Override
            public void onTagClick(TagView tagView, Tag tag) {
                if (keys != null && keys.length > 0) {
                    mEtSearch.setText(keys[tag.getId()]);
                }

            }
        });
    }

    public void getAncillaryInfo(final String keyword) {
        TravelApi.getAncillaryInfo(type, keyword, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson<PoiGuideBean> poiGuideResult = CommonJson.fromJson(result, PoiGuideBean.class);
                if (poiGuideResult.code == 0 && poiGuideResult.result.desc != null) {
                    bindGuideView(keyword, poiGuideResult.result);
                } else {
                    if (headerView != null) mSearchAllLv.removeHeaderView(headerView);
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void bindGuideView(String keyword ,final PoiGuideBean bean) {
        String typeString="";
        if ("restaurant".equals(type)){
            typeString = "美食";
        }else if ("shopping".equals(type)){
            typeString = "购物";
        }
        if (headerView != null) mSearchAllLv.removeHeaderView(headerView);
        headerView = View.inflate(mContext, R.layout.view_poi_list_header, null);
        header = (LinearLayout) headerView.findViewById(R.id.header);
        TextView title= (TextView) headerView.findViewById(R.id.title);
        title.setText(String.format("全部%s攻略>",keyword+typeString));
        mSearchAllLv.addHeaderView(headerView);
        header.setVisibility(View.VISIBLE);
        TextView textView = (TextView) headerView.findViewById(R.id.tv_city_poi_desc);
        textView.setText(bean.desc);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                if (type.equals(TravelApi.PeachType.RESTAURANTS)) {
                    intent.putExtra("url", bean.detailUrl);
                    intent.putExtra("title", "美食攻略");
                } else if (type.equals(TravelApi.PeachType.SHOPPING)) {
                    intent.putExtra("url", bean.detailUrl);
                    intent.putExtra("title", "购物攻略");
                }
                startActivity(intent);
            }
        });
    }

    private void setUpData() {

        //historyLebel
        int tagBackground=0;
        int tagcolor=0;
        if("restaurant".equals(type)){
            historyLebel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.delecious_history,0,0,0);
            tagBackground=R.drawable.all_whitesolid_yellowline;
            tagcolor =R.color.tag_color_yellow;
        }else if("shopping".equals(type)){
            historyLebel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shopping_history,0,0,0);
            tagBackground=R.drawable.all_whitesolid_pinkline;
            tagcolor = R.color.tag_color_pink;
        }else if("travelNote".equals(type)){
            historyLebel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.note_history,0,0,0);
            tagBackground=R.drawable.all_whitesolid_greenline;
            tagcolor = R.color.app_theme_color;
        }else if("vs".equals(type)){
            historyLebel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.place_history,0,0,0);
            tagBackground=R.drawable.all_whitesolid_blueline;
            tagcolor = R.color.tag_color_blue;
        }else{
            historyLebel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.home_history,0,0,0);
            tagBackground=R.drawable.all_whitesolid_greenline;
            tagcolor = R.color.app_theme_color;
        }
        keys = null;
        keys = getSearchHistory();
        mTags.clear();
        if (keys.length > 0 && !TextUtils.isEmpty(keys[0])) {
            int count = 0;
            for (int i = keys.length - 1; i >= 0; i--) {
                Tag tag = new Tag();
                tag.setId(i);
                tag.setChecked(true);
                tag.setTitle(keys[i]);
                tag.setBackgroundResId(tagBackground);
                tag.setTextColor(tagcolor);
                mTags.add(tag);
                count++;
                if (count == 9) break;
            }
        } else {
            history_pannel.setVisibility(View.GONE);
        }
        history_tag.cleanTags();
        history_tag.removeAllViews();
        history_tag.setTags(mTags);
        final int tagBgRes = tagBackground;
        final int tagTxRes = tagcolor;
        TravelApi.getRecommendKeywords(type, new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                CommonJson4List<KeywordBean> keyList = CommonJson4List.fromJson(result.toString(), KeywordBean.class);
                if (keyList.code == 0) {
                    for (int i = 0; i < keyList.result.size(); i++) {
                        Tag tag = new Tag();
                        tag.setId(i);
                        tag.setChecked(true);
                        tag.setTitle(keyList.result.get(i).zhName);
                        tag.setBackgroundResId(tagBgRes);
                        tag.setTextColor(tagTxRes);
                        mKeyTags.add(tag);
                    }
                    recomend_tag.setTags(mKeyTags);
                    recomend_tag.setOnTagClickListener(new TagListView.OnTagClickListener() {
                        @Override
                        public void onTagClick(TagView tagView, Tag tag) {
                            if (mKeyTags != null && mKeyTags.size() > 0) {
                                mEtSearch.setText(mKeyTags.get(tag.getId()).getTitle());
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(tagView.getWindowToken(), 0);
                                searchAll(mKeyTags.get(tag.getId()).getTitle());
                            }
                        }
                    });
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finishWithNoAnim();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //MobclickAgent.onPageStart("page_lxp_search");
        //MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //MobclickAgent.onPageEnd("page_lxp_search");
        //MobclickAgent.onPause(this);
    }

    private void searchAll(final String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            ToastUtil.getInstance(this).showToast("请输入关键词");
            return;
        }
        saveHistory(keyword);
        try {
            DialogManager.getInstance().showLoadingDialog(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if ("restaurant".equals(type) || "shopping".equals(type)) {
            getAncillaryInfo(keyword);
        }


        if ("travelNote".equals(type)) {
            OtherApi.getTravelNoteByKeyword(keyword, 0, 30, new HttpCallBack<String>() {

                @Override
                public void doSuccess(String result, String method) {
                    DialogManager.getInstance().dissMissLoadingDialog();
                    CommonJson4List<TravelNoteBean> detailResult = CommonJson4List.fromJson(result, TravelNoteBean.class);
                    if (detailResult.code == 0) {
                        if (detailResult.result.size() == 0) {
                            ToastUtil.getInstance(SearchAllActivity.this).showToast(String.format("没有找到“%s”的相关结果", keyword));
                        } else bindNoteView(detailResult.result);
                    }
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {
                    DialogManager.getInstance().dissMissLoadingDialog();
                }

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });
        } else if ("vs".equals(type)) {
            TravelApi.searchAll(keyword, "false", "true", "false", "false", "false", new HttpCallBack<String>() {
                @Override
                public void doSuccess(String result, String method) {
                    DialogManager.getInstance().dissMissLoadingDialog();
                    CommonJson<SearchAllBean> searchAllResult = CommonJson.fromJson(result, SearchAllBean.class);
                    if (searchAllResult.code == 0) {
                        //          if (searchAllResult.result.s)
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

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });
        } else if ("restaurant".equals(type)) {
            TravelApi.searchAll(keyword, "false", "false", "false", "true", "false", new HttpCallBack<String>() {
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

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });
        } else if ("shopping".equals(type)) {
            TravelApi.searchAll(keyword, "false", "false", "false", "false", "true", new HttpCallBack<String>() {
                @Override
                public void doSuccess(String result, String method) {
                    DialogManager.getInstance().dissMissLoadingDialog();
                    CommonJson<SearchAllBean> searchAllResult = CommonJson.fromJson(result, SearchAllBean.class);
                    if (searchAllResult.code == 0) {
                        //          if (searchAllResult.result.s)
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

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });
        }else if ("loc".equals(type)){
            TravelApi.searchAll(keyword, "true", "false", "false", "false", "false", new HttpCallBack<String>() {
                @Override
                public void doSuccess(String result, String method) {
                    DialogManager.getInstance().dissMissLoadingDialog();
                    CommonJson<SearchAllBean> searchAllResult = CommonJson.fromJson(result, SearchAllBean.class);
                    if (searchAllResult.code == 0) {
                        //          if (searchAllResult.result.s)
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

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });
        }
    }

    private void bindNoteView(final List<TravelNoteBean> beans) {
        ListViewDataAdapter mTravelNoteAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                TravelNoteViewHolder viewHolder = new TravelNoteViewHolder(SearchAllActivity.this, true, false);
                viewHolder.setOnSendClickListener(new TravelNoteViewHolder.OnSendClickListener() {
                    @Override
                    public void onSendClick(View view, TravelNoteBean itemData) {
                        noteBean = itemData;
                        IMUtils.showSendDialog(mContext, noteBean, chatType, toId, conversation, null);
                        // IMUtils.onClickImShare(mContext);
                    }
                });
                return viewHolder;
            }
        });

        mSearchAllLv.setAdapter(mTravelNoteAdapter);
        mTravelNoteAdapter.getDataList().addAll(beans);
        mTravelNoteAdapter.notifyDataSetChanged();
    }

    private String[] getSearchHistory() {
        String save_Str = SharePrefUtil.getHistory(this, String.format("%s_his", type));
        return save_Str.split(",");
    }

    private void saveHistory(String keyword) {

        String save_Str = SharePrefUtil.getHistory(this, String.format("%s_his", type));
        String[] hisArrays = save_Str.split(",");
        for (String s : hisArrays) {
            if (s.equals(keyword)) {
                return;
            }
        }
        StringBuilder sb = new StringBuilder(save_Str);
        sb.append(keyword + ",");
        SharePrefUtil.saveHistory(this, String.format("%s_his", type), sb.toString());

        setUpData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if ("note".equals(type)) {
                IMUtils.onShareResult(mContext, noteBean, requestCode, resultCode, data, null);
            } else if ("locality".equals(type)) {
                IMUtils.onShareResult(mContext, (LocBean) temp, requestCode, resultCode, data, null);
            } else
                IMUtils.onShareResult(mContext, (PoiDetailBean) temp, requestCode, resultCode, data, null);

        }
    }

    private void bindView(final String keyword, SearchAllBean result) {
        ArrayList<SearchTypeBean> typeBeans = new ArrayList<SearchTypeBean>();
        if (result.locality != null && result.locality.size() > 0) {
            SearchTypeBean searchTypeBean = new SearchTypeBean();
            searchTypeBean.type = "loc";
            searchTypeBean.resultList = result.locality;
            typeBeans.add(searchTypeBean);
        }
        if (result.vs != null && result.vs.size() > 0) {
            SearchTypeBean searchTypeBean = new SearchTypeBean();
            searchTypeBean.type = "vs";
            searchTypeBean.resultList = result.vs;
            typeBeans.add(searchTypeBean);
        }
        if (result.hotel != null && result.hotel.size() > 0) {
            SearchTypeBean searchTypeBean = new SearchTypeBean();
            searchTypeBean.type = "hotel";
            searchTypeBean.resultList = result.hotel;
            typeBeans.add(searchTypeBean);
        }
        if (result.restaurant != null && result.restaurant.size() > 0) {
            SearchTypeBean searchTypeBean = new SearchTypeBean();
            searchTypeBean.type = "restaurant";
            searchTypeBean.resultList = result.restaurant;
            typeBeans.add(searchTypeBean);
        }
        if (result.shopping != null && result.shopping.size() > 0) {
            SearchTypeBean searchTypeBean = new SearchTypeBean();
            searchTypeBean.type = "shopping";
            searchTypeBean.resultList = result.shopping;
            typeBeans.add(searchTypeBean);
        }
        if (typeBeans.size() == 0) {
            ToastUtil.getInstance(SearchAllActivity.this).showToast(String.format("没有找到“%s”的相关结果", keyword));
            return;
        }
        boolean isSend;
        isSend = !TextUtils.isEmpty(toId);
        SearchResultAdapter searchAllAdapter = new SearchResultAdapter(this, typeBeans, true, isSend);
        searchAllAdapter.setOnSearchResultClickListener(new SearchResultAdapter.OnSearchResultClickListener() {
            @Override
            public void onMoreResultClick(String type) {
                //MobclickAgent.onEvent(SearchAllActivity.this, "button_item_all_search_result");
                Intent intent = new Intent(mContext, SearchTypeActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("keyWord", keyword);
                intent.putExtra("chatType", chatType);
                intent.putExtra("fromChat",true);
                intent.putExtra("toId", toId);
                intent.putExtra("conversation", conversation);
                startActivity(intent);
            }

            @Override
            public void onItemOnClick(String type, String id, Object object) {
                IntentUtils.intentToDetail(SearchAllActivity.this, type, id);
            }

            @Override
            public void onSendClick(String type, String id, Object object) {
                //MobclickAgent.onEvent(SearchAllActivity.this, "button_item_lxp_send_search_result");
                currentType = type;
                temp = object;
                //IMUtils.onClickImShare(mContext);
                if ("locality".equals(currentType)) {
                    IMUtils.showSendDialog(mContext, (LocBean) temp, chatType, toId, conversation, null);
                } else
                    IMUtils.showSendDialog(mContext, (PoiDetailBean) temp, chatType, toId, conversation, null);
//                IMUtils.showImShareDialog(mContext, (ICreateShareDialog) object, new IMUtils.OnDialogShareCallBack() {
//                            @Override
//                            public void onDialogShareOk(Dialog dialog, int type, String content, String leave_msg) {
//                                DialogManager.getInstance().showLoadingDialog(mContext);
//                                IMClient.getInstance().sendExtMessage(AccountManager.getCurrentUserId(), toId, chatType, content, type, new HttpCallback() {
//                                    @Override
//                                    public void onSuccess() {
//                                        DialogManager.getInstance().dissMissLoadingDialog();
//                                        runOnUiThread(new Runnable() {
//                                            public void run() {
//                                                ToastUtil.getInstance(mContext).showToast("已发送~");
//                                            }
//                                        });
//                                    }
//
//                                    @Override
//                                    public void onFailed(int code) {
//                                        DialogManager.getInstance().dissMissLoadingDialog();
//                                        runOnUiThread(new Runnable() {
//                                            public void run() {
//                                                ToastUtil.getInstance(mContext).showToast("好像发送失败了");
//
//                                            }
//                                        });
//
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onDialogShareCancle(Dialog dialog, int type, String content) {
//                            }
//                        }

//                );
            }
        });
        mSearchAllLv.setAdapter(searchAllAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogManager.getInstance().dissMissLoadingDialog();
        CommonUtils.fixInputMethodManagerLeak(this);
    }
}
