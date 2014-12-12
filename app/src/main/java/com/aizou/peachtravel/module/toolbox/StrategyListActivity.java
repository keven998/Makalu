package com.aizou.peachtravel.module.toolbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.aizou.peachtravel.bean.ModifyResult;
import com.aizou.peachtravel.bean.StrategyBean;
import com.aizou.peachtravel.common.api.BaseApi;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.MainActivity;
import com.aizou.peachtravel.module.dest.SelectDestActivity;
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

    int page = 0;

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
        mMyStrategyLv.setPullLoadEnabled(false);
        mMyStrategyLv.setPullRefreshEnabled(false);
        mMyStrategyLv.setScrollLoadEnabled(true);
        mStrategyListAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                return new StrategyListViewHolder();
            }
        });
        mMyStrategyLv.getRefreshableView().setAdapter(mStrategyListAdapter);
        mMyStrategyLv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 0;
                getStrategyListData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getStrategyListData();
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
        mTitleBar.getTitleTextView().setText("我的攻略");
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
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_stay);
            }
        });
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
        overridePendingTransition(R.anim.slide_stay, R.anim.slide_out_to_right);
    }

    private void initData() {
        getStrategyListData();
    }

    private void getStrategyListData() {
        TravelApi.getStrategyList(page, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson4List<StrategyBean> strategyListResult = CommonJson4List.fromJson(result, StrategyBean.class);
                if (strategyListResult.code == 0) {
                    bindView(strategyListResult.result);
                    page++;
                }
                mMyStrategyLv.onPullUpRefreshComplete();
                mMyStrategyLv.onPullDownRefreshComplete();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                mMyStrategyLv.onPullUpRefreshComplete();
                mMyStrategyLv.onPullDownRefreshComplete();
            }
        });

    }

    private void bindView(List<StrategyBean> result) {
        if (page == 0) {
            mStrategyListAdapter.getDataList().clear();
        }
        mStrategyListAdapter.getDataList().addAll(result);
        mStrategyListAdapter.notifyDataSetChanged();
        if (mStrategyListAdapter.getCount() == 0) {
            mMyStrategyLv.getRefreshableView().setEmptyView(findViewById(R.id.empty_view));
            findViewById(R.id.start_create).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(StrategyListActivity.this, SelectDestActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_stay);
                }
            });
        }
        if (result == null || result.size() < BaseApi.PAGE_SIZE) {
            mMyStrategyLv.setHasMoreData(false);
            // ptrLv.setScrollLoadEnabled(false);
        } else {
            mMyStrategyLv.setHasMoreData(true);
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
            mCitysTv.setText(itemData.summary);
            mNameTv.setText(itemData.title);
            mTimeTv.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(itemData.updateTime)));
            if (isEditableMode) {
                mDeleteIv.setVisibility(View.VISIBLE);
                mNameTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_line_edit_delete, 0);
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
                    .content("确定删除吗？")
                    .positiveText("确定")
                    .negativeText("取消")
                    .autoDismiss(false)
                    .positiveColor(getResources().getColor(R.color.app_theme_color))
                    .negativeColor(getResources().getColor(R.color.app_theme_color))
                    .callback(new MaterialDialog.Callback() {
                        @Override
                        public void onPositive(final MaterialDialog dialog) {
                            View progressView = View.inflate(mContext,R.layout.view_progressbar,null);
                            dialog.setContentView(progressView);

                            TravelApi.deleteStrategy(itemData.id, new HttpCallBack<String>() {
                                @Override
                                public void doSucess(String result, String method) {
                                    dialog.dismiss();
                                    CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result,ModifyResult.class);
                                    if(deleteResult.code==0){
                                        mStrategyListAdapter.getDataList().remove(itemData);
                                        mStrategyListAdapter.notifyDataSetChanged();
                                        if (mStrategyListAdapter.getCount() == 0) {
                                            mMyStrategyLv.getRefreshableView().setEmptyView(findViewById(R.id.empty_view));
                                        }
                                        ToastUtil.getInstance(mContext).showToast("删除成功");
                                    }else{
                                        ToastUtil.getInstance(mContext).showToast("删除失败");
                                    }
                                }

                                @Override
                                public void doFailure(Exception error, String msg, String method) {
                                    ToastUtil.getInstance(mContext).showToast("删除失败");
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
