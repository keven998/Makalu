package com.aizou.peachtravel.module.toolbox;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aizou.core.dialog.DialogManager;
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
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.utils.PreferenceUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.dest.SelectDestActivity;
import com.aizou.peachtravel.module.dest.StrategyActivity;
import com.easemob.EMCallBack;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/1.
 */
public class StrategyListActivity extends PeachBaseActivity {

    @InjectView(R.id.title_bar)
    TitleHeaderBar mTitleBar;
    @InjectView(R.id.my_strategy_lv)
    PullToRefreshListView mMyStrategyLv;
    @InjectView(R.id.edit_btn)
    CheckedTextView mEditBtn;
    ListViewDataAdapter mStrategyListAdapter;
    public boolean isEditableMode;
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

    private void initView() {
        setContentView(R.layout.activity_strategy_list);
        ButterKnife.inject(this);

        PullToRefreshListView listView = mMyStrategyLv;
        listView.setPullLoadEnabled(false);
        listView.setPullRefreshEnabled(true);
        listView.setScrollLoadEnabled(true);
        mStrategyListAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                return new StrategyListViewHolder();
            }
        });

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

        listView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StrategyBean bean = (StrategyBean) mStrategyListAdapter.getDataList().get(position);
                if(isShare){
                    IMUtils.showImShareDialog(mContext, bean, new IMUtils.OnDialogShareCallBack() {
                        @Override
                        public void onDialogShareOk(Dialog dialog, int type, String content) {
                            DialogManager.getInstance().showProgressDialog(mContext);
                            IMUtils.sendExtMessage(mContext, type, content, chatType, toId, new EMCallBack() {
                                @Override
                                public void onSuccess() {
                                    DialogManager.getInstance().dissMissProgressDialog();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            ToastUtil.getInstance(mContext).showToast("发送成功");

                                        }
                                    });

                                }

                                @Override
                                public void onError(int i, String s) {
                                    DialogManager.getInstance().dissMissProgressDialog();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            ToastUtil.getInstance(mContext).showToast("发送失败");

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
                }else{
                    Intent intent = new Intent(mContext, StrategyActivity.class);
                    intent.putExtra("id", bean.id);
                    startActivity(intent);
                }

            }
        });

        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditableMode = !isEditableMode;
                if (isEditableMode) {
                    mEditBtn.setChecked(true);
                } else {
                    mEditBtn.setChecked(false);
                }
                mStrategyListAdapter.notifyDataSetChanged();
            }
        });

//        mTitleBar.enableBackKey(true);
        mTitleBar.getTitleTextView().setText("旅行Memo");
        mTitleBar.getLeftTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTitleBar.getRightTextView().setText("新建");
        mTitleBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StrategyListActivity.this, SelectDestActivity.class);
                startActivity(intent);
            }
        });


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
    public void finish() {
//        String action = getIntent().getAction();
//        if ("plan.flow".equals(action)) {
//            Intent intent = new Intent(StrategyListActivity.this, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            overridePendingTransition(R.anim.slide_stay, R.anim.slide_out_to_right);
//            return;
//        }
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initData() {
        isShare=getIntent().getBooleanExtra("isShare",false);
        toId = getIntent().getStringExtra("toId");
        chatType = getIntent().getIntExtra("chatType",0);
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
                    if (page == 0) {
                        cachePage();
                    }
                }
                mMyStrategyLv.onPullUpRefreshComplete();
                mMyStrategyLv.onPullDownRefreshComplete();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                mMyStrategyLv.onPullUpRefreshComplete();
                mMyStrategyLv.onPullDownRefreshComplete();
                ToastUtil.getInstance(StrategyListActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });

    }

    private void bindView(List<StrategyBean> result) {
        ListViewDataAdapter adapter = mStrategyListAdapter;
        if (mCurrentPage == 0) {
            adapter.getDataList().clear();
        }
        adapter.getDataList().addAll(result);
        adapter.notifyDataSetChanged();
        if (result == null || result.size() < BaseApi.PAGE_SIZE) {
            mMyStrategyLv.setHasMoreData(false);
            // ptrLv.setScrollLoadEnabled(false);
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
                findViewById(R.id.start_create).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(StrategyListActivity.this, SelectDestActivity.class);
                        startActivity(intent);
//                        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_stay);
                    }
                });
            } else {
                Toast.makeText(this, "已加载全部", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    public class StrategyListViewHolder extends ViewHolderBase<StrategyBean> {

        @InjectView(R.id.delete_iv)
        ImageView mDeleteIv;
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

        DisplayImageOptions poptions;

        public StrategyListViewHolder() {
            poptions = UILUtils.getDefaultOption();
        }

        @Override
        public View createView(LayoutInflater layoutInflater) {
            View view = layoutInflater.inflate(R.layout.row_my_strategy, mMyStrategyLv.getRefreshableView(), false);
            ButterKnife.inject(this, view);
//            int width = (LocalDisplay.SCREEN_WIDTH_PIXELS - LocalDisplay.dp2px(20));
//            int height = width * 480 / 150;
//            mStrategyIv.setLayoutParams(new RelativeLayout.LayoutParams(width,height));
            return view;
        }

        @Override
        public void showData(int position, final StrategyBean itemData) {
            if (itemData.images != null && itemData.images.size() > 0) {
                ImageLoader.getInstance().displayImage(itemData.images.get(0).url, mStrategyIv, poptions);
            } else {
                mStrategyIv.setImageResource(R.drawable.guide_1);
            }
            mDayTv.setText(itemData.dayCnt + "天");
//            String city = "";
//            int size = itemData.localities.size();
//            for (int i = 0; i < size; ++i) {
//                LocBean loc = itemData.localities.get(i);
//                if (i > 0) {
//                    city += "、" + loc.zhName;
//                } else {
//                    city += loc.zhName;
//                }
//            }
            mCitysTv.setText(itemData.summary);
            mNameTv.setText(itemData.title);
            mTimeTv.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(itemData.updateTime)));
            if (isEditableMode) {
                mDeleteIv.setVisibility(View.VISIBLE);
                mNameTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_title_edit, 0, 0, 0);
                mNameTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //todo:修改攻略名称
                    }
                });
                mDeleteIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteItem(itemData);
                    }
                });
            } else {
                mDeleteIv.setVisibility(View.GONE);
                mNameTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        }

        private void deleteItem(final StrategyBean itemData) {
            new MaterialDialog.Builder(StrategyListActivity.this)
                    .title(null)
                    .content("删除后就找不到了")
                    .positiveText("删除")
                    .negativeText("取消")
                    .autoDismiss(false)
                    .positiveColor(getResources().getColor(R.color.app_theme_color))
                    .negativeColor(getResources().getColor(R.color.app_theme_color))
                    .callback(new MaterialDialog.Callback() {
                        @Override
                        public void onPositive(final MaterialDialog dialog) {
                            View progressView = View.inflate(mContext, R.layout.view_progressbar, null);
                            dialog.setContentView(progressView);

                            TravelApi.deleteStrategy(itemData.id, new HttpCallBack<String>() {
                                @Override
                                public void doSucess(String result, String method) {
                                    dialog.dismiss();
                                    CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                                    if (deleteResult.code == 0) {
                                        int index = mStrategyListAdapter.getDataList().indexOf(itemData);
                                        mStrategyListAdapter.getDataList().remove(index);
                                        mStrategyListAdapter.notifyDataSetChanged();
                                        if (mStrategyListAdapter.getCount() == 0) {
                                            mMyStrategyLv.getRefreshableView().setEmptyView(findViewById(R.id.empty_view));
                                        }
//                                        ToastUtil.getInstance(mContext).showToast("删除成功");
                                        ToastUtil.getInstance(StrategyListActivity.this).showToast("OK!成功删除");
                                        if (index <= OtherApi.PAGE_SIZE) {
                                            cachePage();
                                        }
                                    } else {
//                                        ToastUtil.getInstance(mContext).showToast("删除失败");
                                        ToastUtil.getInstance(StrategyListActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                                    }
                                }

                                @Override
                                public void doFailure(Exception error, String msg, String method) {
//                                    ToastUtil.getInstance(mContext).showToast("删除失败");
                                    ToastUtil.getInstance(StrategyListActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                                }
                            });

                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

    }
}
