package com.aizou.peachtravel.module.dest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aizou.peachtravel.bean.ModifyResult;
import com.aizou.peachtravel.common.account.StrategyManager;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.pagerIndicator.indicator.FixedIndicatorView;
import com.aizou.core.widget.pagerIndicator.indicator.IndicatorViewPager;
import com.aizou.core.widget.pagerIndicator.viewpager.FixedViewPager;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.bean.StrategyBean;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.dialog.PeachMessageDialog;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.ShareUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.dest.fragment.RestaurantFragment;
import com.aizou.peachtravel.module.dest.fragment.RouteDayFragment;
import com.aizou.peachtravel.module.dest.fragment.ShoppingFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/24.
 */
public class StrategyActivity extends PeachBaseActivity {
    @InjectView(R.id.loc_list_rv)
    RecyclerView mLocListRv;
    @InjectView(R.id.title_bar)
    TitleHeaderBar mTitleBar;
    private IndicatorViewPager indicatorViewPager;
    @InjectView(R.id.strategy_viewpager)
    FixedViewPager mStrategyViewpager;
    @InjectView(R.id.strategy_indicator)
    FixedIndicatorView mStrategyIndicator;
    private String id;
    private StrategyBean strategy;
    private List<String> cityIdList;
    private ArrayList<LocBean> destinations;
    private boolean canEdit;
    RouteDayFragment routeDayFragment;
    RestaurantFragment restFragment;
    ShoppingFragment shoppingFragment;

    private boolean isAniming = false, isRVVisable = true;
    private Animation inAnim, outAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        destinations = getIntent().getParcelableArrayListExtra("destinations");
        initView();
        initData();

        inAnim = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_top);
        outAnim = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_top);
    }

    private void initView() {
        setContentView(R.layout.activity_strategy);
        ButterKnife.inject(this);
        // 禁止viewpager的滑动事件
        mStrategyViewpager.setCanScroll(false);
        // 设置viewpager保留界面不重新加载的页面数量
        mStrategyViewpager.setOffscreenPageLimit(3);
        // 默认是1,，自动预加载左右两边的界面。设置viewpager预加载数为0。只加载加载当前界面。
        mStrategyViewpager.setPrepareNumber(0);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mLocListRv.setLayoutManager(linearLayoutManager);
//        mTitleBar.enableBackKey(true);
        mTitleBar.getLeftTextView().setText(" 完成");
        mTitleBar.getLeftTextView().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        mTitleBar.getLeftTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkIsEditableMode()){
                    warnCancel();
                }else {
                    finish();
            }
            }
        });

        mTitleBar.setRightViewImageRes(R.drawable.ic_share);
        mTitleBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.showSelectPlatformDialog(StrategyActivity.this);
            }
        });
    }

    private void initData() {
        id = getIntent().getStringExtra("id");
        if (id == null) {
            cityIdList = new ArrayList<String>();
            for (LocBean loc : destinations) {
            cityIdList.add(loc.id);
            }
            //test
//            cityIdList.add("5473ccd7b8ce043a64108c46");
//            cityIdList.add("5473ccddb8ce043a64108d22");
            createStrategyByCityIds(cityIdList);
        } else {
            getStrategyDataById();
        }

    }

    public void getStrategyDataById() {
        TravelApi.getGuideDetail(id, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<StrategyBean> strategyResult = CommonJson.fromJson(result, StrategyBean.class);
                if (strategyResult.code == 0) {
                    bindView(strategyResult.result);
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                ToastUtil.getInstance(StrategyActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }

    public void createStrategyByCityIds(List<String> cityIds) {
        TravelApi.createGuide(cityIds, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<StrategyBean> strategyResult = CommonJson.fromJson(result, StrategyBean.class);
                if (strategyResult.code == 0) {
//                    ToastUtil.getInstance(mContext).showToast("已保存到旅行Memo");
                    bindView(strategyResult.result);
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                ToastUtil.getInstance(StrategyActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }

    private void bindView(final StrategyBean result) {
        strategy = result;
        mTitleBar.getTitleTextView().setText(result.title);
        PeachUser user = AccountManager.getInstance().getLoginAccount(mContext);
        if(user.userId!=result.userId){
            mTitleBar.getRightTextView().setText("复制路线");
            canEdit = false;
            mTitleBar.setRightOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo:复制路线
                    final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
                    dialog.setTitle("提示");
                    dialog.setTitleIcon(R.drawable.ic_dialog_tip);
                    dialog.setMessage("复制这条攻略到我的攻略里面吗？");
                    dialog.setPositiveButton("确定",new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            DialogManager.getInstance().showLoadingDialog(mContext);
                            TravelApi.copyStrategy(result.id,new HttpCallBack<String>() {
                                @Override
                                public void doSucess(String result, String method) {
                                    DialogManager.getInstance().dissMissLoadingDialog();
                                }

                                @Override
                                public void doFailure(Exception error, String msg, String method) {
                                    DialogManager.getInstance().dissMissLoadingDialog();
                                    ToastUtil.getInstance(StrategyActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                                }
                            });
                        }
                    });
                    dialog.setNegativeButton("取消",new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                }
            });
        } else {
            canEdit = true;
        }
        indicatorViewPager = new IndicatorViewPager(mStrategyIndicator, mStrategyViewpager);
        indicatorViewPager.setAdapter(new StrategyAdapter(getSupportFragmentManager(), result,canEdit));
        mLocListRv.setAdapter(new LocAdapter(mContext, result.localities));
//        setRVVisiable(false);
    }

    public void setRVVisiable(boolean visiable) {
        Log.d("test", "visiable = " + visiable + ", isRVVisable = " + isRVVisable + ", isAniming = " + isAniming);
        if (isAniming) {
            return;
        }

        if (visiable && !isRVVisable) {
            isAniming = true;
            mLocListRv.startAnimation(inAnim);
            inAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isAniming = false;
                    isRVVisable = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else if (!visiable && isRVVisable) {
            isAniming = true;
            mLocListRv.startAnimation(outAnim);
            outAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isAniming = false;
                    isRVVisable = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }



    public class LocAdapter extends RecyclerView.Adapter<LocAdapter.ViewHolder> {

        private LayoutInflater mInflater;
        private List<LocBean> mDatas;

        public LocAdapter(Context context, List<LocBean> datas) {
            mInflater = LayoutInflater.from(context);
            mDatas = datas;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View arg0) {
                super(arg0);
            }

            ImageView mImg;
            TextView mTxt;
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        /**
         * 创建ViewHolder
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
            View view = mInflater.inflate(R.layout.item_guide_loc,
                    viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.mTxt = (TextView) view
                    .findViewById(R.id.tv_name);
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });

            return viewHolder;
        }


        /**
         * 设置值
         */
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            LocBean locBean = mDatas.get(i);
            viewHolder.mTxt.setText(locBean.zhName);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CityDetailActivity.class);
                    intent.putExtra("id", mDatas.get(i).id);
                    startActivity(intent);
                }
            });
        }
    }

    private class StrategyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        private String[] tabNames = {"玩安排", "吃清单", "买清单",};
        private int[] tabIcons = {R.drawable.checker_tab_plan_list, R.drawable.checker_tab_delicacy_list, R.drawable.checker_tab_shopping_list};
        private LayoutInflater inflater;
        private StrategyBean strategyBean;
        private boolean canEdit;

        public StrategyAdapter(FragmentManager fragmentManager, StrategyBean strategyBean,boolean canEdit) {
            super(fragmentManager);
            inflater = LayoutInflater.from(getApplicationContext());
            this.strategyBean = strategyBean;
            this.canEdit = canEdit;
        }

        @Override
        public int getCount() {
            return tabNames.length;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.tab_strategy, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(tabNames[position]);
            textView.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[position], 0, 0);
            return textView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            if (position == 0) {
                if(routeDayFragment==null){
                    routeDayFragment = new RouteDayFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("strategy", strategyBean);
                    bundle.putBoolean("canEdit",canEdit);
                    routeDayFragment.setArguments(bundle);
                }
                return routeDayFragment;
            } else if (position == 1) {
                if(restFragment==null){
                    restFragment = new RestaurantFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("strategy", strategyBean);
                    bundle.putBoolean("canEdit",canEdit);
                    restFragment.setArguments(bundle);
                }

                return restFragment;
            } else {
                if(shoppingFragment==null){
                    shoppingFragment = new ShoppingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("strategy", strategyBean);
                    bundle.putBoolean("canEdit",canEdit);
                    shoppingFragment.setArguments(bundle);
                }

                return shoppingFragment;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }

            }
        }

    }

    @Override
    public void onBackPressed() {
        if(checkIsEditableMode()){
            warnCancel();
        }else {
            super.onBackPressed();
        }

    }

    private boolean checkIsEditableMode(){
        if(routeDayFragment!=null&&routeDayFragment.isEditableMode()){
            return true;
        }else if(shoppingFragment!=null&&shoppingFragment.isEditableMode()){
            return true;
        }else if(restFragment!=null&&restFragment.isEditableMode()){
            return true;
        }
        return  false;
    }

    private void warnCancel() {
        final JSONObject jsonObject = new JSONObject();
        StrategyManager.putSaveGuideBaseInfo(jsonObject, mContext,strategy);
        if(routeDayFragment!=null&&routeDayFragment.isEditableMode()){
            StrategyManager.putItineraryJson(mContext,jsonObject,routeDayFragment.getStrategy(),routeDayFragment.getRouteDayMap());
        }else if(shoppingFragment!=null&&shoppingFragment.isEditableMode()){
            StrategyManager.putShoppingJson(mContext,jsonObject,shoppingFragment.getStrategy());
        }else if(restFragment!=null&&restFragment.isEditableMode()){
            StrategyManager.putRestaurantJson(mContext, jsonObject, restFragment.getStrategy());
        }

        final PeachMessageDialog messageDialog = new PeachMessageDialog(mContext);
        messageDialog.setTitle("提示");
        messageDialog.setMessage("是否先保存已完成的清单");
        messageDialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageDialog.dismiss();
                DialogManager.getInstance().showLoadingDialog(mContext);
                TravelApi.saveGuide(strategy.id, jsonObject.toString(),new HttpCallBack<String>() {
                    @Override
                    public void doSucess(String result, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        CommonJson<ModifyResult> saveResult = CommonJson.fromJson(result,ModifyResult.class);
                        if (saveResult.code == 0) {
                            ToastUtil.getInstance(StrategyActivity.this).showToast("已保存到 \"旅行Memo\"");
                            finish();
                        }
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
//                                ToastUtil.getInstance(getActivity()).showToast("保存失败");
                        ToastUtil.getInstance(mContext).showToast(getResources().getString(R.string.request_network_failed));
                    }
                });
            }
        });
        messageDialog.setNegativeButton("取消",new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageDialog.dismiss();
            }
        });
        messageDialog.show();
    }
}


