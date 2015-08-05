package com.xuejian.client.lxp.module.toolbox;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.GsonTools;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.BaseApi;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.ComfirmDialog;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.dialog.MoreDialog;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.PreferenceUtils;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.dest.SelectDestActivity;
import com.xuejian.client.lxp.module.dest.StrategyActivity;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Rjm on 2014/12/1.
 */
public class StrategyListActivity extends PeachBaseActivity {

    public static final int RESULT_PLAN_DETAIL = 1;
    public static final int REQUEST_CODE_NEW_PLAN = 2;

    PullToRefreshListView mMyStrategyLv;
    ImageButton mEditBtn;
    StrategyBean temp;
    ListViewDataAdapter mStrategyListAdapter;
    boolean isShare;
    int mCurrentPage = 0;
    String chatType;
    String toId;
    private String userId;
    private boolean isOwner;
    private User user;
    private int mContentType = 0;
    private boolean newCopy; //复制补丁
    private String conversation;
    private String copyId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        resetMemberValue(getIntent());
        toId = getIntent().getStringExtra("toId");
        chatType = getIntent().getStringExtra("chatType");
        conversation = getIntent().getStringExtra("conversation");
        newCopy = getIntent().getBooleanExtra("new_copy", false);
        copyId = getIntent().getStringExtra("copyId");
        initView();
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        resetMemberValue(intent);
        mContentType = 0;
        mCurrentPage = 0;
        if (!isOwner) {
            mEditBtn.setVisibility(View.GONE);
        }
        getStrategyListData(0, mContentType);
    }

    private void resetMemberValue(Intent intent) {
        userId = intent.getStringExtra("userId");
        if (AccountManager.getInstance().getLoginAccount(StrategyListActivity.this)!=null){
            isOwner = (AccountManager.getInstance().getLoginAccount(this) != null) && userId.equals(AccountManager.getCurrentUserId());
            user = UserDBManager.getInstance().getContactByUserId(Long.parseLong(userId));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getStrategyListData(0, mContentType);
        MobclickAgent.onPageStart("page_lxp_plan_lists");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
       MobclickAgent.onPageEnd("page_lxp_plan_lists");
        newCopy = false;
        MobclickAgent.onPause(this);
    }

    private void initView() {
        setContentView(R.layout.activity_strategy_list);

        mMyStrategyLv = (PullToRefreshListView) findViewById(R.id.my_strategy_lv);
        mEditBtn = (ImageButton) findViewById(R.id.edit_btn);

        PullToRefreshListView listView = mMyStrategyLv;
        listView.setPullLoadEnabled(false);
        listView.setPullRefreshEnabled(true);
        listView.setScrollLoadEnabled(false);
        listView.setHasMoreData(false);
        isShare = getIntent().getBooleanExtra("isShare", false);
        mStrategyListAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                return new StrategyAdapter(isOwner);
            }
        });
        if (!isOwner) {
            mEditBtn.setVisibility(View.GONE);
        }

        listView.getRefreshableView().setAdapter(mStrategyListAdapter);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getStrategyListData(0, mContentType);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getStrategyListData(mCurrentPage + 1, mContentType);
            }
        });

        listView.getRefreshableView().setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        StrategyBean bean = (StrategyBean) mStrategyListAdapter.getDataList().get(position);
                        Intent intent = new Intent(mContext, StrategyActivity.class);
                        intent.putExtra("id", bean.id);
                        intent.putExtra("userId", userId);
                        startActivityForResult(intent, RESULT_PLAN_DETAIL);
                    }
                }

        );
        mEditBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(StrategyListActivity.this,"navigation_item_plan_create");
                        Intent intent = new Intent(StrategyListActivity.this, SelectDestActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_NEW_PLAN);
                    }
                }
        );

        TextView textView = (TextView) findViewById(R.id.tv_title_bar_title);
        if (isOwner) {
            textView.setText("我的计划");
        } else if (user != null) {
            textView.setText(String.format("%s的计划", user.getNickName()));
        } else {
            textView.setText(String.format("旅行计划"));
        }
        findViewById(R.id.tv_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShare){
                    Intent intent =new Intent();
                    intent .putExtra("friend_id",toId);
                    intent.putExtra("conversation",conversation);
                    intent.putExtra("chatType",chatType);
                    intent.setClass(StrategyListActivity.this, ChatActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });

        findViewById(R.id.ivb_content_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(StrategyListActivity.this,"navigation_item_plans_status_filter");
                String[] names = {"全部", "只看计划", "只看已签到"};
                final MoreDialog dialog = new MoreDialog(StrategyListActivity.this);
                dialog.setMoreStyle(false, 13, names);
                dialog.getTv1().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        mContentType = 0;
                        mMyStrategyLv.onPullDownRefreshComplete();
                        mMyStrategyLv.onPullUpRefreshComplete();
                        mMyStrategyLv.doPullRefreshing(true, 0);
                    }
                });
                dialog.getTv2().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        mContentType = 1;
                        mMyStrategyLv.onPullDownRefreshComplete();
                        mMyStrategyLv.onPullUpRefreshComplete();
                        mMyStrategyLv.doPullRefreshing(true, 0);
                    }
                });
                dialog.getTv3().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        mContentType = 2;
                        mMyStrategyLv.onPullDownRefreshComplete();
                        mMyStrategyLv.onPullUpRefreshComplete();
                        mMyStrategyLv.doPullRefreshing(true, 0);
                    }
                });
                dialog.show();

            }
        });
    }

    private void setupViewFromCache() {
        if (isOwner && !newCopy) {
            String data = PreferenceUtils.getCacheData(this, String.format("%s_plans", userId));
            if (!TextUtils.isEmpty(data)) {
                List<StrategyBean> lists = GsonTools.parseJsonToBean(data,
                        new TypeToken<List<StrategyBean>>() {
                        });
                mStrategyListAdapter.getDataList().addAll(lists);
                mStrategyListAdapter.notifyDataSetChanged();
                if (mStrategyListAdapter.getCount() >= OtherApi.PAGE_SIZE) {
                    mMyStrategyLv.setHasMoreData(true);
                    mMyStrategyLv.setScrollLoadEnabled(true);
                }
                getStrategyListData(0, mContentType);
            } else {
                mMyStrategyLv.doPullRefreshing(true, 0);
            }
        } else {
            getStrategyListData(0, mContentType);
        }
    }

    private void cachePage() {
        if (isOwner && (mContentType == 0)) {
            if (userId != null && !userId.equals(AccountManager.getCurrentUserId())) {
                return;
            }
            int size = mStrategyListAdapter.getCount();
            if (size > OtherApi.PAGE_SIZE) {
                size = OtherApi.PAGE_SIZE;
            }
            List<StrategyBean> cd = mStrategyListAdapter.getDataList().subList(0, size);
            PreferenceUtils.cacheData(StrategyListActivity.this, String.format("%s_plans", AccountManager.getCurrentUserId()), GsonTools.createGsonString(cd));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_PLAN_DETAIL) {
                StrategyBean sb = data.getParcelableExtra("strategy");
                if (sb != null) {
                    PreferenceUtils.cacheData(this, "last_strategy", GsonTools.createGsonString(sb));
                }
            } else if (requestCode == REQUEST_CODE_NEW_PLAN) {
                StrategyBean sb = data.getParcelableExtra("strategy");
                if (sb != null) {
                    PreferenceUtils.cacheData(this, "last_strategy", GsonTools.createGsonString(sb));
                }
                mMyStrategyLv.doPullRefreshing(true, 0);
            }
        }
        //IMUtils.onShareResult(mContext, temp, requestCode, resultCode, data, null);
    }

    private void initData() {
        setupViewFromCache();
    }

    private void getStrategyListData(final int page, int type) {
        String content = null;
        if (type == 1) {
            content = "planned";
        } else if (type == 2) {
            content = "traveled";
        }

        TravelApi.getStrategyPlannedList(userId, page, content, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<StrategyBean> strategyListResult = CommonJson4List.fromJson(result, StrategyBean.class);
                if (strategyListResult.code == 0) {
                    mCurrentPage = page;
                    bindView(strategyListResult.result);
                    if (page == 0 || mStrategyListAdapter.getCount() < OtherApi.PAGE_SIZE * 2) {
                        cachePage();
                    }
                }
                mMyStrategyLv.onPullDownRefreshComplete();
                mMyStrategyLv.onPullUpRefreshComplete();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                mMyStrategyLv.onPullDownRefreshComplete();
                mMyStrategyLv.onPullUpRefreshComplete();
                if (!isFinishing())
                    ToastUtil.getInstance(StrategyListActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

    }

    private void bindView(List<StrategyBean> result) {
        if (mCurrentPage == 0) {
            mStrategyListAdapter.getDataList().clear();
        }
        mStrategyListAdapter.getDataList().addAll(result);
        mStrategyListAdapter.notifyDataSetChanged();
        if (result == null || result.size() < BaseApi.PAGE_SIZE) {
            mMyStrategyLv.setHasMoreData(false);
        } else {
            mMyStrategyLv.setHasMoreData(true);
        }

        if (mStrategyListAdapter.getCount() >= BaseApi.PAGE_SIZE) {
            mMyStrategyLv.setScrollLoadEnabled(true);
        } else {
            mMyStrategyLv.setScrollLoadEnabled(false);
        }
        if (result != null && result.size() == 0) {
            if (mCurrentPage == 0) {
                //mMyStrategyLv.getRefreshableView().setEmptyView(findViewById(R.id.empty_view));
                //mMyStrategyLv.doPullRefreshing(true, 0);
            } else {
                ToastUtil.getInstance(this).showToast("已取完所有内容");
            }
            return;
        }
    }


    private class StrategyAdapter extends ViewHolderBase<StrategyBean> {
        TextView tv_tian;
        TextView tv_day;
        TextView mCitysTv;
        TextView mNameTv;
        TextView mTimeTv;
        ImageView mDelete;
        ImageView mCheck;
        RelativeLayout rl_plan;
        ImageView mCheckStatus;
        RelativeLayout rl_send;
        RelativeLayout rl_action;
        CheckedTextView ctv;
        boolean isOwner;

        public StrategyAdapter(boolean isOwner) {
            this.isOwner = isOwner;
        }

        @Override
        public View createView(LayoutInflater layoutInflater) {
            View convertView = layoutInflater.inflate(R.layout.row_my_strategy, null);
            tv_tian = (TextView) convertView.findViewById(R.id.tian);
            tv_day = (TextView) convertView.findViewById(R.id.day_tv);
            mCitysTv = (TextView) convertView.findViewById(R.id.citys_tv);
            mNameTv = (TextView) convertView.findViewById(R.id.name_tv);
            mTimeTv = (TextView) convertView.findViewById(R.id.time_tv);
            mCheck = (ImageView) convertView.findViewById(R.id.iv_check);
            mDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
            mCheckStatus = (ImageView) convertView.findViewById(R.id.iv_check_status);
            rl_plan = (RelativeLayout) convertView.findViewById(R.id.rl_plan);
            rl_send = (RelativeLayout) convertView.findViewById(R.id.rl_send);
            rl_action = (RelativeLayout) convertView.findViewById(R.id.rl_action);
            ctv = (CheckedTextView) convertView.findViewById(R.id.btn_send);
            return convertView;
        }

        @Override
        public void showData(final int position, final StrategyBean itemData) {
            tv_tian.setText(String.valueOf(itemData.dayCnt));
            mCitysTv.setText(itemData.summary);
            if (newCopy&&itemData.id.equals(copyId)) {
                SpannableString planStr = new SpannableString(String.format("(新复制)%s", itemData.title));
                planStr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_checked)), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mNameTv.setText(planStr);
            } else {
                mNameTv.setText(itemData.title);
            }

            mTimeTv.setText("创建：" + new SimpleDateFormat("yyyy-MM-dd").format(new Date(itemData.updateTime)));
            if (itemData.status.equals("traveled")) {
                rl_plan.setBackgroundResource(R.drawable.plan_bg_gray);
                mCheckStatus.setVisibility(View.VISIBLE);
            } else {
                rl_plan.setBackgroundResource(R.drawable.selector_plan_item);
                mCheckStatus.setVisibility(View.GONE);
            }

            if (!isOwner) {
                mDelete.setVisibility(View.INVISIBLE);
                mCheck.setVisibility(View.INVISIBLE);
            }
            if (isShare) {
                rl_action.setVisibility(View.GONE);
                rl_send.setVisibility(View.VISIBLE);
                if (!itemData.status.equals("traveled")) {
                    ctv.setBackgroundResource(R.color.app_theme_color);
                } else {
                    ctv.setBackgroundResource(R.color.light_grey);
                }
            }
            rl_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    temp = itemData;
                    //IMUtils.onClickImShare(StrategyListActivity.this);
                    IMUtils.showSendDialog(mContext, temp, chatType, toId, conversation, null);


                }
            });
            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(itemData);
                }
            });
            mCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(StrategyListActivity.this,"ell_item_plans_change_status");
                    if (itemData.status.equals("planned")) {
                        haveBeenVisited(itemData);
                        mStrategyListAdapter.notifyDataSetChanged();
                        final ComfirmDialog cdialog = new ComfirmDialog(StrategyListActivity.this);
                        cdialog.findViewById(R.id.tv_dialog_title).setVisibility(View.VISIBLE);
                        cdialog.findViewById(R.id.btn_cancle).setVisibility(View.GONE);
                        cdialog.setTitle("提示");
                        cdialog.setMessage("已去过，旅历＋1");
                        cdialog.setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cdialog.dismiss();
                                mMyStrategyLv.doPullRefreshing(true, 0);
                            }
                        });
                        final Handler handler = new Handler() {
                            public void handleMessage(Message msg) {
                                switch (msg.what) {
                                    case 1:
                                        cdialog.show();
                                }
                                super.handleMessage(msg);
                            }
                        };
                        Message message = handler.obtainMessage(1);
                        handler.sendMessageDelayed(message, 300);
                    } else {
                        cancleVisited(itemData);
                        mStrategyListAdapter.notifyDataSetChanged();
                        mMyStrategyLv.doPullRefreshing(true, 300);
//                        final ComfirmDialog cdialog = new ComfirmDialog(StrategyListActivity.this);
//                        cdialog.findViewById(R.id.tv_dialog_title).setVisibility(View.VISIBLE);
//                        cdialog.findViewById(R.id.btn_cancle).setVisibility(View.GONE);
//                        cdialog.setTitle("取消签到");
//                        cdialog.setMessage("旅历-1");
//                        cdialog.setPositiveButton("确定", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                cdialog.dismiss();
//                                mMyStrategyLv.doPullRefreshing(true, 0);
//                            }
//                        });
//                        final Handler handler = new Handler() {
//                            public void handleMessage(Message msg) {
//                                switch (msg.what) {
//                                    case 1:
//                                        cdialog.show();
//                                }
//                                super.handleMessage(msg);
//                            }
//                        };
//                        Message message = handler.obtainMessage(1);
//                        handler.sendMessageDelayed(message, 300);
                    }
                }
            });
        }
    }




    private void deleteItem(final StrategyBean itemData) {
        MobclickAgent.onEvent(StrategyListActivity.this,"cell_item_plans_delete");
        final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
        dialog.setTitle("提示");
        dialog.setTitleIcon(R.drawable.ic_dialog_tip);
        dialog.setMessage(String.format("删除\"%s\"", itemData.title));
        dialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                try {
                    DialogManager.getInstance().showLoadingDialog(mContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TravelApi.deleteStrategy(itemData.id, new HttpCallBack<String>() {
                    @Override
                    public void doSuccess(String result, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                        if (deleteResult.code == 0) {
                            deleteThisItem(itemData);
                            int cnt = AccountManager.getInstance().getLoginAccountInfo().getGuideCnt();
                            AccountManager.getInstance().getLoginAccountInfo().setGuideCnt(cnt - 1);
                        } else {
                            if (!isFinishing())
                                ToastUtil.getInstance(StrategyListActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                        }
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        if (!isFinishing())
                            ToastUtil.getInstance(StrategyListActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method, int code) {

                    }
                });
            }
        });
        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void deleteThisItem(StrategyBean data) {
        int index = mStrategyListAdapter.getDataList().indexOf(data);
        mStrategyListAdapter.getDataList().remove(index);
        mStrategyListAdapter.notifyDataSetChanged();
        if (mStrategyListAdapter.getCount() == 0) {
            mMyStrategyLv.doPullRefreshing(true, 0);
        } else if (index <= OtherApi.PAGE_SIZE) {
            cachePage();
        }
    }

    private void backToTop(final StrategyBean itemData1) {
        try {
            DialogManager.getInstance().showLoadingDialog(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Long time = System.currentTimeMillis();
        TravelApi.modifyGuideTop(itemData1.id, time, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> topResult = CommonJson.fromJson(result, ModifyResult.class);
                if (topResult.code == 0) {
                    int index = mStrategyListAdapter.getDataList().indexOf(itemData1);
                    mStrategyListAdapter.getDataList().remove(index);
                    mStrategyListAdapter.notifyDataSetChanged();
                    addToTop(itemData1);
                    cachePage();
                } else {
                    if (!isFinishing())
                        ToastUtil.getInstance(StrategyListActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(StrategyListActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void addToTop(final StrategyBean topBean) {
        mStrategyListAdapter.getDataList().add(0, topBean);
        mStrategyListAdapter.notifyDataSetChanged();
    }

    private void haveBeenVisited(final StrategyBean beenBean) {
        try {
            DialogManager.getInstance().showLoadingDialog(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String visited = "traveled";
        TravelApi.modifyGuideVisited(beenBean.id, visited, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> visitedResult = CommonJson.fromJson(result, ModifyResult.class);
                if (visitedResult.code == 0) {
                    deleteThisItem(beenBean);
                } else {
                    if (!isFinishing())
                        ToastUtil.getInstance(StrategyListActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(StrategyListActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void cancleVisited(final StrategyBean beenBean) {
        try {
            DialogManager.getInstance().showLoadingDialog(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String planned = "planned";
        TravelApi.modifyGuideVisited(beenBean.id, planned, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> visitedResult = CommonJson.fromJson(result, ModifyResult.class);
                if (visitedResult.code == 0) {
                    deleteThisItem(beenBean);
                } else {
                    if (!isFinishing())
                        ToastUtil.getInstance(StrategyListActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(StrategyListActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

}
