package com.xuejian.client.lxp.module.dest.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.SharePrefUtil;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.KeywordBean;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.SearchAllBean;
import com.xuejian.client.lxp.bean.SearchTypeBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.IntentUtils;
import com.xuejian.client.lxp.common.widget.TagView.Tag;
import com.xuejian.client.lxp.common.widget.TagView.TagListView;
import com.xuejian.client.lxp.common.widget.TagView.TagView;
import com.xuejian.client.lxp.module.dest.SearchTypeActivity;
import com.xuejian.client.lxp.module.dest.adapter.SearchAllAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuyongchen on 15/8/26.
 */
public class SearchAllFragment extends PeachBaseFragment {

    ImageView iv_clean;
    EditText mEtSearch;
    TextView mBtnSearch;
    ListView mSearchAllLv;
    TextView cleanHistory;
    FrameLayout history_pannel;
    TagListView recomend_tag;
    String toId;
    String chatType;
    Object temp;
    String currentType;
    String conversation;

    private final List<Tag> mTags = new ArrayList<Tag>();
    private final List<Tag> mKeyTags = new ArrayList<Tag>();
    private TagListView history_tag;
    private String[] keys;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //toId = getIntent().getStringExtra("toId");
        //chatType = getIntent().getStringExtra("chatType");
        //conversation = getIntent().getStringExtra("conversation");
        View rootView = inflater.inflate(R.layout.activity_search_all, container, false);
        mEtSearch = (EditText) rootView.findViewById(R.id.et_search);
        mBtnSearch = (TextView) rootView.findViewById(R.id.btn_search);
        mSearchAllLv = (ListView) rootView.findViewById(R.id.search_all_lv);
        cleanHistory = (TextView) rootView.findViewById(R.id.cleanHistory);
        history_pannel = (FrameLayout) rootView.findViewById(R.id.history_pannel);
        recomend_tag = (TagListView) rootView.findViewById(R.id.recomend_tag);
        iv_clean = (ImageView) rootView.findViewById(R.id.iv_clean);
        iv_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtSearch.setText("");
            }
        });
        cleanHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                history_tag.cleanTags();
                history_pannel.setVisibility(View.GONE);
                SharePrefUtil.saveHistory(getActivity(), "");
            }
        });
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEtSearch.getText())) {
                    return;
                } else {
                    searchAll(mEtSearch.getText().toString().trim());
                }
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        mEtSearch.setHint("城市/景点/美食/购物");
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
                    iv_clean.setVisibility(View.INVISIBLE);
                }
                if (s.length() > 0) {
                    iv_clean.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        View emptyView = rootView.findViewById(R.id.empty_text);


        mSearchAllLv.setEmptyView(emptyView);
        mSearchAllLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mEtSearch.requestFocus();
//                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(mEtSearch, InputMethodManager.SHOW_IMPLICIT);
//            }
//        }, 600);
        history_tag = (TagListView) rootView.findViewById(R.id.history_tag);
        setUpData();
        history_tag.setTags(mTags);
        history_tag.setOnTagClickListener(new TagListView.OnTagClickListener() {
            @Override
            public void onTagClick(TagView tagView, Tag tag) {
                if (keys != null && keys.length > 0) {
                    mEtSearch.setText(keys[tag.getId()]);
                }
            }
        });

        return rootView;
    }

    private String[] getSearchHistory() {
        String save_Str = SharePrefUtil.getHistory(getActivity());
        return save_Str.split(",");
    }

    private void setUpData() {
        keys = getSearchHistory();
        if (keys.length > 0 && !TextUtils.isEmpty(keys[0])) {
            int count = 0;
            for (int i = keys.length - 1; i >= 0; i--) {
                Tag tag = new Tag();
                tag.setId(i);
                tag.setChecked(true);
                tag.setTitle(keys[i]);
                mTags.add(tag);
                count++;
                if (count==9)break;
            }
        } else {
            history_pannel.setVisibility(View.GONE);
        }
        TravelApi.getRecommendKeywords(new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                CommonJson4List<KeywordBean> keyList = CommonJson4List.fromJson(result.toString(), KeywordBean.class);
                for (int i = 0; i < keyList.result.size(); i++) {
                    Tag tag = new Tag();
                    tag.setId(i);
                    tag.setChecked(true);
                    tag.setTitle(keyList.result.get(i).zhName);
                    mKeyTags.add(tag);
                }
                recomend_tag.setmTagViewResId(R.layout.tag);
                recomend_tag.setTagViewBackgroundRes(R.drawable.all_whitesolid_greenline);
                recomend_tag.setTags(mKeyTags);
                recomend_tag.setOnTagClickListener(new TagListView.OnTagClickListener() {
                    @Override
                    public void onTagClick(TagView tagView, Tag tag) {
                        if (mKeyTags != null && mKeyTags.size() > 0) {
                            mEtSearch.setText(mKeyTags.get(tag.getId()).getTitle());
                        }

                    }
                });

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void saveHistory(String keyword) {
        String save_Str = SharePrefUtil.getHistory(getActivity());
        String[] hisArrays = save_Str.split(",");
        for (String s : hisArrays) {
            if (s.equals(keyword)) {
                return;
            }
        }
        StringBuilder sb = new StringBuilder(save_Str);
        sb.append(keyword + ",");
        SharePrefUtil.saveHistory(getActivity(), sb.toString());
    }

    private void searchAll(final String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            ToastUtil.getInstance(getActivity()).showToast("请输入关键词");
            return;
        }
        saveHistory(keyword);
        try {
            DialogManager.getInstance().showLoadingDialog(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        TravelApi.searchAll(keyword, new HttpCallBack<String>() {
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
                ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

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
            ToastUtil.getInstance(getActivity()).showToast(String.format("没有找到“%s”的相关结果", keyword));
            return;
        }
        boolean isSend;
        isSend = !TextUtils.isEmpty(toId);
        SearchAllAdapter searchAllAdapter = new SearchAllAdapter(getActivity(), typeBeans, true, isSend);
        searchAllAdapter.setOnSearchResultClickListener(new SearchAllAdapter.OnSearchResultClickListener() {
            @Override
            public void onMoreResultClick(String type) {
                Intent intent = new Intent(getActivity(), SearchTypeActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("keyWord", keyword);
                intent.putExtra("chatType", chatType);
                intent.putExtra("toId", toId);
                intent.putExtra("conversation", conversation);
                startActivity(intent);
            }

            @Override
            public void onItemOnClick(String type, String id, Object object) {
                IntentUtils.intentToDetail(getActivity(), type, id);
            }

            @Override
            public void onSendClick(String type, String id, Object object) {

                currentType = type;
                temp = object;
                //IMUtils.onClickImShare(mContext);
                if ("locality".equals(currentType)) {
                    IMUtils.showSendDialog(getActivity(), (LocBean) temp, chatType, toId, conversation, null);
                } else
                    IMUtils.showSendDialog(getActivity(), (PoiDetailBean) temp, chatType, toId, conversation, null);
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
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
