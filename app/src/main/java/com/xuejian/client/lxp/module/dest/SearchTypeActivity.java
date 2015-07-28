package com.xuejian.client.lxp.module.dest;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.lv.Listener.HttpCallback;
import com.lv.im.IMClient;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.SearchAllBean;
import com.xuejian.client.lxp.bean.SearchTypeBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.BaseApi;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.share.ICreateShareDialog;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.IntentUtils;
import com.xuejian.client.lxp.module.dest.adapter.SearchAllAdapter;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/9.
 */
public class SearchTypeActivity extends PeachBaseActivity {
    public final static int REQUEST_CODE_SEARCH_LOC = 100;
    @InjectView(R.id.tv_title_bar_title)
    TextView titleTv;
    @InjectView(R.id.tv_city_filter)
    TextView cityFilterTv;
    @InjectView(R.id.search_type_lv)
    PullToRefreshListView mSearchTypeLv;
    int curPage = 0;
    String type;
    String keyWord;
    SearchAllAdapter mAdapter;
    LocBean mLocBean;
    ArrayList<SearchTypeBean> typeBeans = new ArrayList<SearchTypeBean>();
    SearchTypeBean typeBean;
    String toId;
    String chatType;
    String conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("page_search_destination_all_result");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_search_destination_all_result");
    }

    private void initView() {
        setContentView(R.layout.activity_search_type);
        ButterKnife.inject(this);
        mSearchTypeLv.setPullLoadEnabled(false);
        mSearchTypeLv.setPullRefreshEnabled(false);
        mSearchTypeLv.setScrollLoadEnabled(true);
        mSearchTypeLv.setHasMoreData(false);
        mSearchTypeLv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                searchSearchTypeData(0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                searchSearchTypeData(curPage + 1);
            }
        });
        findViewById(R.id.tv_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cityFilterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SearchDestForPoiActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SEARCH_LOC);
            }
        });
    }

    private void initData() {
        type = getIntent().getStringExtra("type");
        keyWord = getIntent().getStringExtra("keyWord");
        toId = getIntent().getStringExtra("toId");
        conversation = getIntent().getStringExtra("conversation");
        chatType = getIntent().getStringExtra("chatType");
        if (type.equals("loc")) {
            cityFilterTv.setVisibility(View.GONE);
            typeBean = new SearchTypeBean();
            typeBean.type = "loc";
            typeBean.resultList = new ArrayList();
            typeBeans.add(typeBean);
            titleTv.setText("全部城市");
        } else if (type.equals("vs")) {
           // cityFilterTv.setVisibility(View.VISIBLE);
            typeBean = new SearchTypeBean();
            typeBean.type = "vs";
            typeBean.resultList = new ArrayList();
            typeBeans.add(typeBean);
            titleTv.setText("全部景点");
        } else if (type.equals("hotel")) {
            typeBean = new SearchTypeBean();
            typeBean.type = "hotel";
            typeBean.resultList = new ArrayList();
            typeBeans.add(typeBean);
            titleTv.setText("全部酒店");
        } else if (type.equals("restaurant")) {
            typeBean = new SearchTypeBean();
            typeBean.type = "restaurant";
            typeBean.resultList = new ArrayList();
            typeBeans.add(typeBean);
            titleTv.setText("全部美食");
        } else if (type.equals("shopping")) {
            typeBean = new SearchTypeBean();
            typeBean.type = "shopping";
            typeBean.resultList = new ArrayList();
            typeBeans.add(typeBean);
            titleTv.setText("全部购物");
        }
        DialogManager.getInstance().showLoadingDialog(this);
        searchSearchTypeData(0);
    }

    private void setLoc(LocBean locBean) {
        mLocBean = locBean;
        cityFilterTv.setText(locBean.zhName);
    }

    private void searchSearchTypeData(final int page) {
        String locId = "";
        if (mLocBean != null) {
            locId = mLocBean.id;

        }
        TravelApi.searchForType(keyWord, type, locId, page, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<SearchAllBean> searchAllResult = CommonJson.fromJson(result, SearchAllBean.class);
                if (searchAllResult.code == 0) {
                    curPage = page;
                    bindView(searchAllResult.result);
                }
                if (curPage == 0) {
                    mSearchTypeLv.onPullUpRefreshComplete();
                    mSearchTypeLv.onPullDownRefreshComplete();
                } else {
                    mSearchTypeLv.onPullUpRefreshComplete();
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing()) {
                    mSearchTypeLv.onPullUpRefreshComplete();
                    mSearchTypeLv.onPullDownRefreshComplete();
                    ToastUtil.getInstance(SearchTypeActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

    }

    private void bindView(SearchAllBean result) {
        if (curPage == 0) {
            typeBean.resultList.clear();
        }
        boolean hasMore = true;
        if (type.equals("loc")) {
            typeBean.resultList.addAll(result.locality);
            if (result.locality.size() < BaseApi.PAGE_SIZE) {
                hasMore = false;
            }
        } else if (type.equals("vs")) {
            typeBean.resultList.addAll(result.vs);
            if (result.vs.size() < BaseApi.PAGE_SIZE) {
                hasMore = false;
            }
        } else if (type.equals("hotel")) {
            typeBean.resultList.addAll(result.hotel);
            if (result.hotel.size() < BaseApi.PAGE_SIZE) {
                hasMore = false;
            }
        } else if (type.equals("restaurant")) {
            typeBean.resultList.addAll(result.restaurant);
            if (result.restaurant.size() < BaseApi.PAGE_SIZE) {
                hasMore = false;
            }
        } else if (type.equals("shopping")) {
            typeBean.resultList.addAll(result.shopping);
            if (result.shopping.size() < BaseApi.PAGE_SIZE) {
                hasMore = false;
            }
        }
        if (mAdapter == null) {
            boolean isSend;
            isSend = !TextUtils.isEmpty(toId);
            mAdapter = new SearchAllAdapter(mContext, typeBeans, false, isSend);
            mAdapter.setOnSearchResultClickListener(new SearchAllAdapter.OnSearchResultClickListener() {
                @Override
                public void onMoreResultClick(String type) {

                }

                @Override
                public void onItemOnClick(String type, String id, Object object) {
                    IntentUtils.intentToDetail(SearchTypeActivity.this, type, id);

                }

                @Override
                public void onSendClick(String type, String id, Object object) {
                    IMUtils.showImShareDialog(mContext, (ICreateShareDialog) object, new IMUtils.OnDialogShareCallBack() {
                        @Override
                        public void onDialogShareOk(Dialog dialog, int type, String content, String leave_msg) {
                            DialogManager.getInstance().showLoadingDialog(mContext);
                            IMClient.getInstance().sendExtMessage(AccountManager.getCurrentUserId(), toId, chatType, content, type, new HttpCallback() {
                                @Override
                                public void onSuccess() {
                                    DialogManager.getInstance().dissMissLoadingDialog();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            ToastUtil.getInstance(mContext).showToast("已发送~");
                                            Intent intent=new Intent(mContext, ChatActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            intent.putExtra("conversation",conversation);
                                            intent.putExtra("chatType",chatType);
                                            intent.putExtra("friend_id",toId);
                                            startActivity(intent);
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
                                @Override
                                public void onSuccess(String result) {
                                }
                            });
                        }

                        @Override
                        public void onDialogShareCancle(Dialog dialog, int type, String content) {
                        }
                    });
                }
            });
            mSearchTypeLv.getRefreshableView().setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }


        if (result == null
                || !hasMore) {
            mSearchTypeLv.setHasMoreData(false);
            if (curPage != 0) {
                ToastUtil.getInstance(mContext).showToast("已加载完全部");
            }
            // ptrLv.setScrollLoadEnabled(false);
        } else {
            mSearchTypeLv.setHasMoreData(true);
            mSearchTypeLv.onPullUpRefreshComplete();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SEARCH_LOC) {
                LocBean locBean = data.getParcelableExtra("loc");
                setLoc(locBean);
                mSearchTypeLv.doPullRefreshing(true, 0);
            }
        }
    }
}
