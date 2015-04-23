package com.aizou.peachtravel.module.toolbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.ModifyResult;
import com.aizou.peachtravel.bean.StrategyBean;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.BaseApi;
import com.aizou.peachtravel.common.api.OtherApi;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.peachtravel.common.dialog.PeachEditDialog;
import com.aizou.peachtravel.common.dialog.PeachMessageDialog;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.utils.PreferenceUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.common.widget.swipelistview.SwipeLayout;
import com.aizou.peachtravel.common.widget.swipelistview.adapters.BaseSwipeAdapter;
import com.aizou.peachtravel.module.dest.SelectDestActivity;
import com.aizou.peachtravel.module.dest.StrategyActivity;
import com.aizou.peachtravel.module.toolbox.im.AddContactActivity;
import com.aizou.peachtravel.module.toolbox.im.PickContactsWithCheckboxActivity;
import com.easemob.EMCallBack;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/1.
 */
public class StrategyListActivity extends PeachBaseActivity {

    public static final int RESULT_PLAN_DETAIL = 1;
    public static final int REQUEST_CODE_NEW_PLAN = 2;

    @InjectView(R.id.title_bar)
    TitleHeaderBar mTitleBar;
    @InjectView(R.id.my_strategy_lv)
    PullToRefreshListView mMyStrategyLv;
    @InjectView(R.id.edit_btn)
    ImageButton mEditBtn;
    StrategyAdapter mStrategyListAdapter;
    boolean isShare;
    int mCurrentPage = 0;
    int chatType;
    String toId;

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
//        MobclickAgent.onPageStart("page_my_trip_plans");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_my_trip_plans");
    }

    private void initView() {
        setContentView(R.layout.activity_strategy_list);
        ButterKnife.inject(this);

        PullToRefreshListView listView = mMyStrategyLv;
        listView.setPullLoadEnabled(false);
        listView.setPullRefreshEnabled(true);
        listView.setScrollLoadEnabled(false);
        listView.setHasMoreData(false);
        isShare = getIntent().getBooleanExtra("isShare", false);
        mStrategyListAdapter = new StrategyAdapter(isShare);
        if (isShare) {
            mEditBtn.setVisibility(View.GONE);
        }

        listView.getRefreshableView().setAdapter(mStrategyListAdapter);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getStrategyListData(0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getStrategyListData(mCurrentPage + 1);
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

//        mTitleBar.enableBackKey(true);

        String action = getIntent().getAction();
        TitleHeaderBar tbar = mTitleBar;
        tbar.enableBackKey(true);
        tbar.getTitleTextView().

                setText("我的旅程");
    }

    private void setupViewFromCache() {
        AccountManager account = AccountManager.getInstance();
        String data = PreferenceUtils.getCacheData(this, String.format("%s_plans", account.user.userId));
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
            getStrategyListData(0);
        } else {
            mMyStrategyLv.doPullRefreshing(true, 0);
        }
    }

    private void cachePage() {
        AccountManager account = AccountManager.getInstance();
        int size = mStrategyListAdapter.getCount();
        if (size > OtherApi.PAGE_SIZE) {
            size = OtherApi.PAGE_SIZE;
        }
        List<StrategyBean> cd = mStrategyListAdapter.getDataList().subList(0, size);
        PreferenceUtils.cacheData(StrategyListActivity.this, String.format("%s_plans", account.user.userId), GsonTools.createGsonString(cd));
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
        toId = getIntent().getStringExtra("toId");
        chatType = getIntent().getIntExtra("chatType", 0);
//        getStrategyListData(0);
//        mMyStrategyLv.doPullRefreshing(true, 100);
        setupViewFromCache();
    }

    private void getStrategyListData(final int page) {
        TravelApi.getStrategyList(page, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
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
        StrategyAdapter adapter = mStrategyListAdapter;
        if (mCurrentPage == 0) {
            adapter.getDataList().clear();
        }
        adapter.getDataList().addAll(result);
        adapter.notifyDataSetChanged();
        if (result == null || result.size() < BaseApi.PAGE_SIZE) {
            mMyStrategyLv.setHasMoreData(false);
        } else {
            mMyStrategyLv.setHasMoreData(true);
        }
//        if (adapter.getCount() >= BaseApi.PAGE_SIZE) {
//            mMyStrategyLv.setScrollLoadEnabled(true);
//        }else{
//            mMyStrategyLv.setScrollLoadEnabled(false);
//        }
        if (result.size() == 0) {
            if (mCurrentPage == 0) {
                mMyStrategyLv.getRefreshableView().setEmptyView(findViewById(R.id.empty_view));
            } else {
                ToastUtil.getInstance(this).showToast("已取完所有内容");
            }
            return;
        }
    }

    public class StrategyAdapter extends BaseSwipeAdapter {
        protected ArrayList<StrategyBean> mItemDataList = new ArrayList<StrategyBean>();
        DisplayImageOptions poptions;
        boolean isSend;

        public StrategyAdapter(boolean isSend) {
            poptions = UILUtils.getDefaultOption();
            this.isSend = isSend;
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe;
        }

        public ArrayList<StrategyBean> getDataList() {
            return mItemDataList;
        }

        @Override
        public View generateView(int position, ViewGroup parent) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.row_my_strategy, null);
            return v;
        }

        @Override
        public void fillValues(int position, View convertView) {
            ImageView mStrategyIv = (ImageView) convertView.findViewById(R.id.strategy_iv);
            TextView mDayTv = (TextView) convertView.findViewById(R.id.day_tv);
            TextView mCitysTv = (TextView) convertView.findViewById(R.id.citys_tv);
            TextView mNameTv = (TextView) convertView.findViewById(R.id.name_tv);
            TextView mTimeTv = (TextView) convertView.findViewById(R.id.time_tv);
            ImageButton mMore = (ImageButton) convertView.findViewById(R.id.edit_more);
            ImageButton mDeleteItem = (ImageButton) convertView.findViewById(R.id.delete_item);


            final StrategyBean itemData = (StrategyBean) getItem(position);
            TextView mBtnSend = (TextView) convertView.findViewById(R.id.btn_send);
            RelativeLayout mRlSend = (RelativeLayout) convertView.findViewById(R.id.rl_send);
            if (itemData.images != null && itemData.images.size() > 0) {
                ImageLoader.getInstance().displayImage(itemData.images.get(0).url, mStrategyIv, poptions);
            } else {
                mStrategyIv.setImageDrawable(null);
            }
            mDayTv.setText(String.valueOf(itemData.dayCnt));
            mCitysTv.setText(itemData.summary);
            mNameTv.setText(itemData.title);
            mTimeTv.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(itemData.updateTime)));
            if (isSend) {
                mRlSend.setVisibility(View.VISIBLE);
                mBtnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IMUtils.showImShareDialog(mContext, itemData, new IMUtils.OnDialogShareCallBack() {
                            @Override
                            public void onDialogShareOk(Dialog dialog, int type, String content) {
                                DialogManager.getInstance().showLoadingDialog(mContext);
                                IMUtils.sendExtMessage(mContext, type, content, chatType, toId, new EMCallBack() {
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
                                    public void onError(int i, String s) {
                                        DialogManager.getInstance().dissMissLoadingDialog();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                ToastUtil.getInstance(mContext).showToast("好像发送失败了");

                                            }
                                        });

                                    }

                                    @Override
                                    public void onProgress(int i, String s) {

                                    }
                                });
                            }

                            @Override
                            public void onDialogShareCancle(Dialog dialog, int type, String content) {
                            }
                        });
                    }
                });
            } else {
                mRlSend.setVisibility(View.GONE);
            }

            mMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                       showMoreDialog(itemData);
                }
            });

            mDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteItem(itemData);
                }
            });
        }

        public void showMoreDialog(final StrategyBean strBean){
            final Activity act = StrategyListActivity.this;
            final AlertDialog dialog = new AlertDialog.Builder(act).create();
            View contentView = View.inflate(act, R.layout.dialog_strategy_more_view, null);
            TextView title = (TextView) contentView.findViewById(R.id.strategy_more_title);
            Button edit_title = (Button) contentView.findViewById(R.id.strategy_edit_title);
            Button goto_top = (Button) contentView.findViewById(R.id.strategy_goto_top);
            Button been = (Button) contentView.findViewById(R.id.strategy_been);

            title.setText(strBean.title);
            edit_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    final PeachEditDialog editDialog = new PeachEditDialog(mContext);
                    editDialog.setTitle("修改标题");
                    editDialog.setMessage(strBean.title);
                    editDialog.setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editDialog.dismiss();
                            DialogManager.getInstance().showLoadingDialog(mContext);
                            TravelApi.modifyGuideTitle(strBean.id, editDialog.getMessage(), new HttpCallBack<String>() {
                                @Override
                                public void doSucess(String result, String method) {
                                    DialogManager.getInstance().dissMissLoadingDialog();
                                    CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result, ModifyResult.class);
                                    if (modifyResult.code == 0) {
                                        strBean.title = editDialog.getMessage();
                                        mStrategyListAdapter.notifyDataSetChanged();
                                        cachePage();
                                    } else {
                                        if (!isFinishing())
                                            ToastUtil.getInstance(StrategyListActivity.this).showToast(getResources().getString(R.string.request_network_failed));
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
                    editDialog.show();
                }
            });
            contentView.findViewById(R.id.strategy_btn_cancle).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            WindowManager windowManager = act.getWindowManager();
            Window window = dialog.getWindow();
            window.setContentView(contentView);
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = (int) (display.getWidth()); // 设置宽度
            window.setAttributes(lp);
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
        }

        @Override
        public int getCount() {
            return mItemDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mItemDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    public class StrategyListViewHolder extends ViewHolderBase<StrategyBean> {
        View rootView;
        @InjectView(R.id.strategy_iv)
        ImageView mStrategyIv;
        @InjectView(R.id.day_tv)
        TextView mDayTv;
        @InjectView(R.id.citys_tv)
        TextView mCitysTv;
        @InjectView(R.id.name_tv)
        TextView mNameTv;
        @InjectView(R.id.time_tv)
        TextView mTimeTv;


        ImageButton mEditTtitle;
        @InjectView(R.id.delete_item)
        ImageButton mDeleteItem;


        DisplayImageOptions poptions;
        boolean isSend;
        @InjectView(R.id.btn_send)
        TextView mBtnSend;
        @InjectView(R.id.rl_send)
        RelativeLayout mRlSend;
        @InjectView(R.id.swipe)
        SwipeLayout swipe;


        public StrategyListViewHolder(boolean isSend) {
            poptions = UILUtils.getDefaultOption();
            this.isSend = isSend;
        }

        @Override
        public View createView(LayoutInflater layoutInflater) {
            rootView = layoutInflater.inflate(R.layout.row_my_strategy, mMyStrategyLv.getRefreshableView(), false);
            ButterKnife.inject(this, rootView);
//            int width = (LocalDisplay.SCREEN_WIDTH_PIXELS - LocalDisplay.dp2px(20));
//            int height = width * 480 / 150;
//            mStrategyIv.setLayoutParams(new RelativeLayout.LayoutParams(width,height));
            return rootView;
        }

        @Override
        public void showData(int position, final StrategyBean itemData) {
            if (itemData.images != null && itemData.images.size() > 0) {
                ImageLoader.getInstance().displayImage(itemData.images.get(0).url, mStrategyIv, poptions);
            } else {
                mStrategyIv.setImageDrawable(null);
            }
            mDayTv.setText(String.valueOf(itemData.dayCnt));
            mCitysTv.setText(itemData.summary);
            mNameTv.setText(itemData.title);
            mTimeTv.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(itemData.updateTime)));
            if (isSend) {
                mRlSend.setVisibility(View.VISIBLE);
                mBtnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IMUtils.showImShareDialog(mContext, itemData, new IMUtils.OnDialogShareCallBack() {
                            @Override
                            public void onDialogShareOk(Dialog dialog, int type, String content) {
                                DialogManager.getInstance().showLoadingDialog(mContext);
                                IMUtils.sendExtMessage(mContext, type, content, chatType, toId, new EMCallBack() {
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
                                    public void onError(int i, String s) {
                                        DialogManager.getInstance().dissMissLoadingDialog();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                ToastUtil.getInstance(mContext).showToast("好像发送失败了");

                                            }
                                        });

                                    }

                                    @Override
                                    public void onProgress(int i, String s) {

                                    }
                                });
                            }

                            @Override
                            public void onDialogShareCancle(Dialog dialog, int type, String content) {
                            }
                        });
                    }
                });
            } else {
                mRlSend.setVisibility(View.GONE);
            }


            mEditTtitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MobclickAgent.onEvent(mContext, "event_edit_trip_title");
                    final PeachEditDialog editDialog = new PeachEditDialog(mContext);
                    editDialog.setTitle("修改标题");
                    editDialog.setMessage(itemData.title);
                    editDialog.setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editDialog.dismiss();
                            DialogManager.getInstance().showLoadingDialog(mContext);
                            TravelApi.modifyGuideTitle(itemData.id, editDialog.getMessage(), new HttpCallBack<String>() {
                                @Override
                                public void doSucess(String result, String method) {
                                    DialogManager.getInstance().dissMissLoadingDialog();
                                    CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result, ModifyResult.class);
                                    if (modifyResult.code == 0) {
                                        itemData.title = editDialog.getMessage();
                                        mStrategyListAdapter.notifyDataSetChanged();
                                        cachePage();
                                    } else {
                                        if (!isFinishing())
                                            ToastUtil.getInstance(StrategyListActivity.this).showToast(getResources().getString(R.string.request_network_failed));
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
                    editDialog.show();
                }
            });

            mDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteItem(itemData);
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
                    public void doSucess(String result, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                        if (deleteResult.code == 0) {
                            int index = mStrategyListAdapter.getDataList().indexOf(itemData);
                            mStrategyListAdapter.closeItem(index);
                            mStrategyListAdapter.getDataList().remove(index);
                            mStrategyListAdapter.notifyDataSetChanged();
                            if (mStrategyListAdapter.getCount() == 0) {
                                mMyStrategyLv.doPullRefreshing(true, 0);
                            } else if (index <= OtherApi.PAGE_SIZE) {
                                cachePage();
                            }
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
}
