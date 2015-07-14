package com.xuejian.client.lxp.module.toolbox;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.lv.Listener.HttpCallback;
import com.lv.im.IMClient;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
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
import com.xuejian.client.lxp.common.dialog.PeachEditDialog;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.PreferenceUtils;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.dest.SelectDestActivity;
import com.xuejian.client.lxp.module.dest.StrategyActivity;

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

    ListViewDataAdapter mStrategyListAdapter;
    boolean isShare;
    int mCurrentPage = 0;
    String chatType;
    String toId;
    private String userId;
    private boolean isOwner;
    private User user;
    private int mContentType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        resetMemberValue(getIntent());
        toId = getIntent().getStringExtra("toId");
        chatType = getIntent().getStringExtra("chatType");
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
        isOwner = (AccountManager.getInstance().getLoginAccount(this) != null) && userId.equals(AccountManager.getCurrentUserId());
        user = UserDBManager.getInstance().getContactByUserId(Long.parseLong(userId));
    }

    @Override
    protected void onResume() {
        super.onResume();
         mMyStrategyLv.doPullRefreshing(true, 0);
//        MobclickAgent.onPageStart("page_plan_lists");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_plan_lists");
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
                        startActivityForResult(intent, RESULT_PLAN_DETAIL);
                    }
                }

        );
        mEditBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(mContext, "event_create_new_trip_plan_mine");
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
                finish();
            }
        });

        findViewById(R.id.ivb_content_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogPlus.Builder(StrategyListActivity.this)
                        .setAdapter(new MenuAdapter())
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                dialog.dismiss();
                                mContentType = position;
                                mMyStrategyLv.onPullDownRefreshComplete();
                                mMyStrategyLv.onPullUpRefreshComplete();
                                mMyStrategyLv.doPullRefreshing(true, 0);
                            }
                        })
                        .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                        .setGravity(Gravity.CENTER)
                        .setOutAnimation(R.anim.fade_out)
                        .create()
                        .show();
            }
        });
    }

    private void setupViewFromCache() {
        if (isOwner) {
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
        ImageView mModify;
        RelativeLayout rl_plan;
        ImageView mCheckStatus;
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
            mDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
            mCheck = (ImageView) convertView.findViewById(R.id.iv_check);
            mModify = (ImageView) convertView.findViewById(R.id.iv_modify_name);
            mCheckStatus = (ImageView) convertView.findViewById(R.id.iv_check_status);
            rl_plan = (RelativeLayout) convertView.findViewById(R.id.rl_plan);
            return convertView;
        }

        @Override
        public void showData(final int position, final StrategyBean itemData) {
            tv_tian.setText(String.valueOf(itemData.dayCnt));
            mCitysTv.setText(itemData.summary);
            mNameTv.setText(itemData.title);
            mTimeTv.setText("创建：" + new SimpleDateFormat("yyyy-MM-dd").format(new Date(itemData.updateTime)));
            if (itemData.status.equals("traveled")) {
                rl_plan.setBackgroundResource(R.drawable.plan_bg_gray);
                mCheckStatus.setVisibility(View.VISIBLE);
            } else {
                rl_plan.setBackgroundResource(R.drawable.selector_plan_item);
                mCheckStatus.setVisibility(View.GONE);
            }

            if (!isOwner) {
                mModify.setVisibility(View.INVISIBLE);
                mDelete.setVisibility(View.INVISIBLE);
                mCheck.setVisibility(View.INVISIBLE);
            }

            mModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PeachEditDialog editDialog = new PeachEditDialog(mContext);
                    editDialog.setTitle("修改计划名");
                    editDialog.setMessage(itemData.title);
                    editDialog.setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editDialog.dismiss();
                            DialogManager.getInstance().showLoadingDialog(mContext);
                            TravelApi.modifyGuideTitle(itemData.id, editDialog.getMessage(), new HttpCallBack<String>() {
                                @Override
                                public void doSuccess(String result, String method) {
                                    DialogManager.getInstance().dissMissLoadingDialog();
                                    CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result, ModifyResult.class);
                                    if (modifyResult.code == 0) {
                                        itemData.title = editDialog.getMessage();
                                        mStrategyListAdapter.notifyDataSetChanged();
                                        cachePage();
                                    } else {
                                        if (!isFinishing()) {
                                            ToastUtil.getInstance(StrategyListActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                                        }
                                    }
                                }

                                @Override
                                public void doFailure(Exception error, String msg, String method) {
                                    DialogManager.getInstance().dissMissLoadingDialog();
                                    if (!isFinishing()) {
                                        ToastUtil.getInstance(StrategyListActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                                    }
                                }
                            });
                        }
                    });
                    editDialog.show();
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
                    if (itemData.status.equals("planned")) {
                        haveBeenVisited(itemData);
                        mStrategyListAdapter.notifyDataSetChanged();
                        final ComfirmDialog cdialog = new ComfirmDialog(StrategyListActivity.this);
                        cdialog.findViewById(R.id.tv_dialog_title).setVisibility(View.VISIBLE);
                        cdialog.findViewById(R.id.btn_cancle).setVisibility(View.GONE);
                        cdialog.setTitle("完成签到");
                        cdialog.setMessage("旅历＋1");
                        cdialog.setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cdialog.dismiss();
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
                        final ComfirmDialog cdialog = new ComfirmDialog(StrategyListActivity.this);
                        cdialog.findViewById(R.id.tv_dialog_title).setVisibility(View.VISIBLE);
                        cdialog.findViewById(R.id.btn_cancle).setVisibility(View.GONE);
                        cdialog.setTitle("取消签到");
                        cdialog.setMessage("旅历-1");
                        cdialog.setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cdialog.dismiss();
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
                    }
                }
            });
        }
    }

    private void deleteItem(final StrategyBean itemData) {
        MobclickAgent.onEvent(mContext, "event_delete_trip_plan");
        final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
        dialog.setTitle("提示");
        dialog.setTitleIcon(R.drawable.ic_dialog_tip);
        dialog.setMessage("删除确认");
        dialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DialogManager.getInstance().showLoadingDialog(mContext);
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
                            DialogManager.getInstance().showLoadingDialog(mContext);
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
        DialogManager.getInstance().showLoadingDialog(mContext);
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
                    DialogManager.getInstance().showLoadingDialog(mContext);
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
        });
    }

    private void addToTop(final StrategyBean topBean) {
        mStrategyListAdapter.getDataList().add(0, topBean);
        mStrategyListAdapter.notifyDataSetChanged();
    }

    private void haveBeenVisited(final StrategyBean beenBean) {
        DialogManager.getInstance().showLoadingDialog(mContext);
        String visited = "traveled";
        TravelApi.modifyGuideVisited(beenBean.id, visited, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> visitedResult = CommonJson.fromJson(result, ModifyResult.class);
                if (visitedResult.code == 0) {
                    deleteThisItem(beenBean);
                } else {
                    DialogManager.getInstance().showLoadingDialog(mContext);
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
        });
    }

    private void cancleVisited(final StrategyBean beenBean) {
        DialogManager.getInstance().showLoadingDialog(mContext);
        String planned = "planned";
        TravelApi.modifyGuideVisited(beenBean.id, planned, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> visitedResult = CommonJson.fromJson(result, ModifyResult.class);
                if (visitedResult.code == 0) {
                    deleteThisItem(beenBean);
                } else {
                    DialogManager.getInstance().showLoadingDialog(mContext);
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
        });
    }

    private class MenuAdapter extends BaseAdapter {
        private final String[] CONTENT_TYPE = {"全部", "只看计划", "只看已签到"};

        @Override
        public int getCount() {
            return CONTENT_TYPE.length;
        }

        @Override
        public Object getItem(int position) {
            return CONTENT_TYPE[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.spinner_item_dropdown, null);
            }
            TextView ttv = (TextView) convertView.findViewById(R.id.tv_title);
            ttv.setText(CONTENT_TYPE[position]);
            return convertView;
        }
    }

}
