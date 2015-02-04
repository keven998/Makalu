package com.aizou.peachtravel.module.dest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.GsonTools;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.pagerIndicator.indicator.FixedIndicatorView;
import com.aizou.core.widget.pagerIndicator.indicator.IndicatorViewPager;
import com.aizou.core.widget.pagerIndicator.indicator.slidebar.LayoutBar;
import com.aizou.core.widget.pagerIndicator.indicator.slidebar.ScrollBar;
import com.aizou.core.widget.pagerIndicator.viewpager.FixedViewPager;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.CopyStrategyBean;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.ModifyResult;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.bean.StrategyBean;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.account.StrategyManager;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.peachtravel.common.dialog.PeachMessageDialog;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.utils.PreferenceUtils;
import com.aizou.peachtravel.common.utils.ShareUtils;
import com.aizou.peachtravel.common.widget.FlowLayout;
import com.aizou.peachtravel.module.dest.fragment.RestaurantFragment;
import com.aizou.peachtravel.module.dest.fragment.RouteDayFragment;
import com.aizou.peachtravel.module.dest.fragment.ShoppingFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/24.
 */
public class StrategyActivity extends PeachBaseActivity implements OnEditModeChangeListener{
    public final static int EDIT_LOC_REQUEST_CODE=110;
    @InjectView(R.id.tv_title_back)
    TextView mTvTitleBack;
    @InjectView(R.id.iv_edit)
    CheckedTextView mIvEdit;
    @InjectView(R.id.iv_more)
    ImageView mIvMore;
    @InjectView(R.id.tv_copy_guide)
    TextView mTvCopyGuide;
    private IndicatorViewPager indicatorViewPager;
    @InjectView(R.id.strategy_viewpager)
    FixedViewPager mStrategyViewpager;
    @InjectView(R.id.strategy_indicator)
    FixedIndicatorView mStrategyIndicator;
    private String id;
    private StrategyBean strategy;
    private List<String> cityIdList;
    private ArrayList<LocBean> destinations;
    private int curIndex=0;
    private LayoutBar layoutBar;
    private TextView indexTv;
    RouteDayFragment routeDayFragment;
    RestaurantFragment restFragment;
    ShoppingFragment shoppingFragment;
    private Set<OnEditModeChangeListener> mOnEditModeChangeListeners = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        destinations = getIntent().getParcelableArrayListExtra("destinations");
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_strategy);
        ButterKnife.inject(this);
        mStrategyViewpager.setCanScroll(true);
        layoutBar = new LayoutBar(mContext, R.layout.tab_strategy, ScrollBar.Gravity.CENTENT);
        indexTv = (TextView) layoutBar.getSlideView();
        mStrategyIndicator.setScrollBar(layoutBar);
        // 设置viewpager保留界面不重新加载的页面数量
        mStrategyViewpager.setOffscreenPageLimit(3);
        // 默认是1,，自动预加载左右两边的界面。设置viewpager预加载数为0。只加载加载当前界面。
        mStrategyViewpager.setPrepareNumber(2);
        indicatorViewPager = new IndicatorViewPager(mStrategyIndicator, mStrategyViewpager);
        indicatorViewPager.setOnIndicatorPageChangeListener(new IndicatorViewPager.OnIndicatorPageChangeListener() {
            @Override
            public void onIndicatorPageChange(int preItem, int currentItem) {
                curIndex = currentItem;
                if(curIndex==0){
                    indexTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_plan_selected, 0, 0);
                }else if(curIndex==1){
                    indexTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_delicacy_selected, 0, 0);
                }else if(curIndex==2){
                    indexTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_shopping_selected, 0, 0);
                }

            }
        });
        mTvTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIvEdit.isChecked()) {
                    warnCancel();
                } else {
                    Intent intent = getIntent();
                    intent.putExtra("strategy", getSaveStrategy());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStrategyViewpager = null;
        mStrategyIndicator = null;
        routeDayFragment = null;
        restFragment = null;
        shoppingFragment = null;
        layoutBar = null;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        try {
            OnEditModeChangeListener listener = (OnEditModeChangeListener)fragment;
            mOnEditModeChangeListeners.add(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onAttachFragment(fragment);
    }

    private void gotoEditMode(){
        for(OnEditModeChangeListener onEditModeChangeListener:mOnEditModeChangeListeners){
            onEditModeChangeListener.onEditModeChange(true);
        }
    }

    public void onEditModeChange(boolean inEditMode){
//        isInEditMode = inEditMode;
//        if(inEditMode){
//            mTvTitleBack.setVisibility(View.GONE);
//            mTvTitleComplete.setVisibility(View.VISIBLE);
//            mIvEdit.setVisibility(View.GONE);
//        }else{
//            mTvTitleBack.setVisibility(View.VISIBLE);
//            mTvTitleComplete.setVisibility(View.GONE);
//            mIvEdit.setVisibility(View.VISIBLE);
//        }

        mIvEdit.setChecked(inEditMode);
        gotoEditMode();
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
            final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
            dialog.setTitle("提示");
            dialog.setMessage("小桃可为你创建模版，旅程计划更简单");
            dialog.setNegativeButton("不需要", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    createStrategyByCityIds(cityIdList, false);
                }
            });
            dialog.setPositiveButton("创建", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    createStrategyByCityIds(cityIdList, true);
                }
            });
            dialog.show();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    createStrategyByCityIds(cityIdList, false);
                }
            });

        } else {
           boolean hasCache= setupViewFromCache(id);
            if(!hasCache)
                DialogManager.getInstance().showLoadingDialog(mContext);
            getStrategyDataById(id);

        }

    }

    private boolean setupViewFromCache(String itemId) {
        String data = PreferenceUtils.getCacheData(this, "last_strategy");
        if (!TextUtils.isEmpty(data)) {
            StrategyBean item = GsonTools.parseJsonToBean(data, StrategyBean.class);
            if (item.id.equals(itemId)) {
                bindView(item);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void getStrategyDataById(String itemId) {
        TravelApi.getGuideDetail(itemId, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<StrategyBean> strategyResult = CommonJson.fromJson(result, StrategyBean.class);
                if (strategyResult.code == 0) {
                    bindView(strategyResult.result);
//                    StrategyActivity.this.po
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                if (!isFinishing())
                    DialogManager.getInstance().dissMissLoadingDialog();
                    ToastUtil.getInstance(StrategyActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }

    public void createStrategyByCityIds(List<String> cityIds, boolean recommend) {
        DialogManager.getInstance().showLoadingDialog(mContext, "请稍后");
        TravelApi.createGuide(cityIds, recommend, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<StrategyBean> strategyResult = CommonJson.fromJson(result, StrategyBean.class);
                if (strategyResult.code == 0) {
//                    ToastUtil.getInstance(mContext).showToast("已保存到旅行Memo");
                    bindView(strategyResult.result);
                    new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            ToastUtil.getInstance(StrategyActivity.this).showToast("已保存到我的旅程");
                        }
                    }.sendEmptyMessageDelayed(0, 1000);
                } else {
                    if (!isFinishing())
                        ToastUtil.getInstance(StrategyActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(StrategyActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }

    private void copyGuide(){

    }

    private void bindView(final StrategyBean result) {
        destinations = result.localities;
        strategy = result;
//        mTitleBar.getTitleTextView().setText(result.title);
        final PeachUser user = AccountManager.getInstance().getLoginAccount(mContext);

        if (user.userId != result.userId) {
            mIvEdit.setVisibility(View.GONE);
            mIvMore.setVisibility(View.GONE);
            mTvCopyGuide.setVisibility(View.VISIBLE);
            mTvCopyGuide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo:复制路线
                    DialogManager.getInstance().showLoadingDialog(mContext);
                    TravelApi.copyStrategy(result.id, new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String resultStr, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            CommonJson<CopyStrategyBean> modifyResult = CommonJson.fromJson(resultStr, CopyStrategyBean.class);
                            if (modifyResult.code == 0) {
                                strategy.id = modifyResult.result.id;
                                strategy.userId = user.userId;
                                bindView(strategy);
                                if (!isFinishing()) {
                                    ToastUtil.getInstance(StrategyActivity.this).showToast("已保存到我的旅程");
                                }
                            }

                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            if (!isFinishing())
                                ToastUtil.getInstance(StrategyActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                        }
                    });

//                    final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
//                    dialog.setTitle("提示");
//                    dialog.setTitleIcon(R.drawable.ic_dialog_tip);
//                    dialog.setMessage("复制这条攻略到我的攻略里面吗？");
//                    dialog.setPositiveButton("确定", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                            DialogManager.getInstance().showLoadingDialog(mContext);
//                            TravelApi.copyStrategy(result.id,new HttpCallBack<String>() {
//                                @Override
//                                public void doSucess(String result, String method) {
//                                    DialogManager.getInstance().dissMissLoadingDialog();
//                                }
//
//                                @Override
//                                public void doFailure(Exception error, String msg, String method) {
//                                    DialogManager.getInstance().dissMissLoadingDialog();
//                                    ToastUtil.getInstance(StrategyActivity.this).showToast(getResources().getString(R.string.request_network_failed));
//                                }
//                            });
//                        }
//                    });
//                    dialog.setNegativeButton("取消",new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                        }
//                    });
                }
            });
        } else {
            mIvEdit.setVisibility(View.VISIBLE);
            mIvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckedTextView cv = (CheckedTextView) v;
                    if (!cv.isChecked()) {
                        gotoEditMode();
                        cv.setChecked(true);
                    } else {
                        saveStrategy(false);
                    }
                }
            });
            mIvMore.setVisibility(View.VISIBLE);
            mTvCopyGuide.setVisibility(View.GONE);
//            mTitleBar.setRightViewImageRes(R.drawable.ic_share);
//            mTitleBar.getRightTextView().setText("");
//            mTitleBar.setRightOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ShareUtils.showSelectPlatformDialog(StrategyActivity.this, strategy);
//                }
//            });

            mIvMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showActionDialog();
                }
            });
        }
        indicatorViewPager.setAdapter(new StrategyAdapter(getSupportFragmentManager(), result));
        indicatorViewPager.setCurrentItem(curIndex, false);
        if(curIndex==0){
            LogUtil.d("indicator--onDraw setDrawable");
            indexTv.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_tab_plan_selected,0,0);
        }else if(curIndex==1){
            indexTv.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_tab_delicacy_selected,0,0);
        }else if(curIndex==2){
            indexTv.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_tab_shopping_selected,0,0);
        }
//        mStrategyViewpager.postInvalidate();
//
//        mLocListRv.setAdapter(new LocAdapter(mContext, result.localities));
//        setRVVisiable(false);
    }

    @Override
    public void finish() {
        super.finish();
    }

    private StrategyBean getSaveStrategy() {
        if (routeDayFragment != null && routeDayFragment.getStrategy() != null) {
            strategy = routeDayFragment.getStrategy();
        }
        if (restFragment != null && restFragment.getStrategy() != null) {
            strategy.restaurant = restFragment.getStrategy().restaurant;
        }
        if (shoppingFragment != null && shoppingFragment.getStrategy() != null) {
            strategy.shopping = shoppingFragment.getStrategy().shopping;
        }
        return strategy;
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
        private String[] tabNames = {"旅程", "吃收集", "逛收集",};
        private int[] tabIcons = {R.drawable.ic_tab_plan_unselect, R.drawable.ic_tab_delicacy_normal, R.drawable.ic_tab_shopping_normal};
        private LayoutInflater inflater;
        private StrategyBean strategyBean;

        public StrategyAdapter(FragmentManager fragmentManager, StrategyBean strategyBean) {
            super(fragmentManager);
            inflater = LayoutInflater.from(getApplicationContext());
            this.strategyBean = strategyBean;
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
                if (routeDayFragment == null) {
                    routeDayFragment = new RouteDayFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("strategy", strategyBean);
                    bundle.putBoolean("isInEditMode", mIvEdit.isChecked());
                    routeDayFragment.setArguments(bundle);
                }
                return routeDayFragment;
            } else if (position == 1) {
                if (restFragment == null) {
                    restFragment = new RestaurantFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("strategy", strategyBean);
                    bundle.putBoolean("isInEditMode", mIvEdit.isChecked());
                    restFragment.setArguments(bundle);
                }

                return restFragment;
            } else {
                if (shoppingFragment == null) {
                    shoppingFragment = new ShoppingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("strategy", strategyBean);
                    bundle.putBoolean("isInEditMode", mIvEdit.isChecked());
                    shoppingFragment.setArguments(bundle);
                }

                return shoppingFragment;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IMUtils.onShareResult(mContext, strategy, requestCode, resultCode, data, null);
        if (resultCode == RESULT_OK) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
            if(requestCode==EDIT_LOC_REQUEST_CODE){
                destinations = data.getParcelableArrayListExtra("destinations");
                showLocDialog();
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (checkIsEditableMode()) {
            warnCancel();
        } else {
            Intent intent = getIntent();
            intent.putExtra("strategy", getSaveStrategy());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private boolean checkIsEditableMode() {
        if (routeDayFragment != null && routeDayFragment.isEditableMode()) {
            return true;
        } else if (shoppingFragment != null && shoppingFragment.isEditableMode()) {
            return true;
        } else if (restFragment != null && restFragment.isEditableMode()) {
            return true;
        }
        return false;
    }

    private void saveStrategy(final boolean finish) {
        final JSONObject jsonObject = new JSONObject();
        StrategyManager.putSaveGuideBaseInfo(jsonObject, mContext, strategy);
        if (routeDayFragment != null&&routeDayFragment.getStrategy()!=null) {
            StrategyManager.putItineraryJson(mContext, jsonObject, routeDayFragment.getStrategy(), routeDayFragment.getRouteDayMap());
        }
        if (shoppingFragment != null&&shoppingFragment.getStrategy()!=null) {
            StrategyManager.putShoppingJson(mContext, jsonObject, shoppingFragment.getStrategy());
        }
        if (restFragment != null&&restFragment.getStrategy()!=null) {
            StrategyManager.putRestaurantJson(mContext, jsonObject, restFragment.getStrategy());
        }
        DialogManager.getInstance().showLoadingDialog(mContext);
        TravelApi.saveGuide(strategy.id, jsonObject.toString(), new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> saveResult = CommonJson.fromJson(result, ModifyResult.class);
                if (saveResult.code == 0) {
                    mIvEdit.setChecked(false);
                    for(OnEditModeChangeListener onEditModeChangeListener:mOnEditModeChangeListeners){
                        onEditModeChangeListener.onEditModeChange(false);
                    }
                    if (routeDayFragment != null) {
                        routeDayFragment.resumeItinerary();
                    }
                    if (finish) {
                        finish();
                    }
                } else {
                    if (!isFinishing())
                        ToastUtil.getInstance(mContext).showToast(getResources().getString(R.string.request_server_failed));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
//                                ToastUtil.getInstance(getActivity()).showToast("保存失败");
                if (!isFinishing())
                    ToastUtil.getInstance(mContext).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }

    private void warnCancel() {
        final JSONObject jsonObject = new JSONObject();
        StrategyManager.putSaveGuideBaseInfo(jsonObject, mContext, strategy);
        if (routeDayFragment != null && routeDayFragment.isEditableMode()) {
            StrategyManager.putItineraryJson(mContext, jsonObject, routeDayFragment.getStrategy(), routeDayFragment.getRouteDayMap());
        } else if (shoppingFragment != null && shoppingFragment.isEditableMode()) {
            StrategyManager.putShoppingJson(mContext, jsonObject, shoppingFragment.getStrategy());
        } else if (restFragment != null && restFragment.isEditableMode()) {
            StrategyManager.putRestaurantJson(mContext, jsonObject, restFragment.getStrategy());
        }

        final PeachMessageDialog messageDialog = new PeachMessageDialog(mContext);
        messageDialog.setTitle("提示");
        messageDialog.setMessage("计划已编辑，是否保存");
        messageDialog.setPositiveButton("先保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageDialog.dismiss();
                saveStrategy(true);

            }
        });
        messageDialog.setNegativeButton("直接返回", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageDialog.dismiss();
                Intent intent = getIntent();
                intent.putExtra("strategy", strategy);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        messageDialog.show();
    }

    private void showActionDialog() {
        final Activity act = this;
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        View contentView = View.inflate(act, R.layout.dialog_city_detail_action, null);
        Button btn1 = (Button) contentView.findViewById(R.id.btn_go_plan);
        btn1.setText("分享");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.showSelectPlatformDialog(StrategyActivity.this, strategy);
                dialog.dismiss();
            }
        });
        Button btn2 = (Button) contentView.findViewById(R.id.btn_go_share);
        btn2.setText("目的地");
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLocDialog();
                dialog.dismiss();
            }
        });
        contentView.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
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

    private void showLocDialog() {
        final Activity act = this;
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        View contentView = View.inflate(act, R.layout.dialog_strategy_loclist, null);
        FlowLayout locListFl = (FlowLayout) contentView.findViewById(R.id.fl_loc_list);
        TextView cancleTv = (TextView) contentView.findViewById(R.id.tv_cancle);
        for(final LocBean loc :destinations){
            View view = View.inflate(mContext,R.layout.item_strategy_loc,null);
            TextView  nameTv = (TextView) view.findViewById(R.id.tv_cell_name);
            nameTv.setText(loc.zhName);
            nameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,CityDetailActivity.class);
                    intent.putExtra("id",loc.id);
                    startActivity(intent);
                }
            });
            locListFl.addView(view);
        }
        View editView =View.inflate(mContext, R.layout.item_strategy_loc,null);
        TextView  nameTv = (TextView) editView.findViewById(R.id.tv_cell_name);
        ViewGroup.LayoutParams tvLp = nameTv.getLayoutParams();
        tvLp.width = LocalDisplay.dp2px(80);
        tvLp.height =LocalDisplay.dp2px(32);
        nameTv.setLayoutParams(tvLp);
        nameTv.setBackgroundResource(R.drawable.btn_loc_add);
        nameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,SelectDestActivity.class);
                intent.putExtra("locList",destinations);
                intent.putExtra("request_code",EDIT_LOC_REQUEST_CODE);
                startActivityForResult(intent,EDIT_LOC_REQUEST_CODE);
                dialog.dismiss();

            }
        });
        locListFl.addView(editView);
        cancleTv.setOnClickListener(new View.OnClickListener() {
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

}


