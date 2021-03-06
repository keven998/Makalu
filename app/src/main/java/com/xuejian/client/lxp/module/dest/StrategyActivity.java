package com.xuejian.client.lxp.module.dest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.GsonTools;
import com.aizou.core.utils.SharePrefUtil;
import com.aizou.core.widget.pagerIndicator.indicator.FixedIndicatorView;
import com.aizou.core.widget.pagerIndicator.indicator.IndicatorViewPager;
import com.aizou.core.widget.pagerIndicator.viewpager.FixedViewPager;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CopyStrategyBean;
import com.xuejian.client.lxp.bean.IndexPoi;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.account.StrategyManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.ComfirmDialog;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.dialog.PeachEditDialog;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.GuideViewUtils;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.PreferenceUtils;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.dest.fragment.CollectionFragment;
import com.xuejian.client.lxp.module.dest.fragment.PlanScheduleFragment;
import com.xuejian.client.lxp.module.toolbox.StrategyListActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Rjm on 2014/11/24.
 */
public class StrategyActivity extends PeachBaseActivity {
    public final static int EDIT_LOC_REQUEST_CODE = 110;
    @Bind(R.id.strategy_title)
    TextView topTitle;
    @Bind(R.id.iv_more)
    ImageView mIvMore;
    @Bind(R.id.tv_copy_guide)
    TextView mTvCopyGuide;
    @Bind(R.id.strategy_drawer_layout)
    DrawerLayout drawerLayout;
    private IndicatorViewPager indicatorViewPager;
    @Bind(R.id.strategy_viewpager)
    FixedViewPager mStrategyViewpager;
    @Bind(R.id.strategy_indicator)
    FixedIndicatorView mStrategyIndicator;
    @Bind(R.id.iv_location)
    ImageView Iv_location;
    @Bind(R.id.iv_edit)
    ImageView ivEdit;
    @Bind(R.id.day_pannel)
    LinearLayout llAddDay;
    private String id;
    private StrategyBean strategy;
    private List<String> cityIdList;
    private ArrayList<LocBean> destinations;
    private int curIndex = 0;
    PlanScheduleFragment routeDayFragment;
    CollectionFragment collectionFragment;
    private ListView draw_list;
    private DrawAdapter adapter;
    private String userId;
    private boolean isOwner;
    private int count;
    private boolean newCreate;
    private String newId;
    private String locId;
    private boolean recomment;
    private ArrayList<String> recommendCityList;
    boolean auto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        userId = getIntent().getStringExtra("userId");
        locId = getIntent().getStringExtra("locId");
        recomment = getIntent().getBooleanExtra("recommend", false);
        destinations = getIntent().getParcelableArrayListExtra("destinations");
        auto = getIntent().getBooleanExtra("auto",true);
        initView();
        initData(savedInstanceState);
        llAddDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resizeData(strategy.itinerary);
                routeDayMap.add(new ArrayList<PoiDetailBean>());
                strategy.itineraryDays++;
                saveStrategy();
                if (routeDayFragment!=null){
                    routeDayFragment.addDay();
                }
            }
        });
    }
    private void saveStrategy() {

        final JSONObject jsonObject = new JSONObject();
        StrategyManager.putSaveGuideBaseInfo(jsonObject, StrategyActivity.this, strategy);
        StrategyManager.putItineraryJson(StrategyActivity.this, jsonObject, strategy, routeDayMap);

        ArrayList<LocBean> locs=new ArrayList<LocBean>();
        locs.addAll(strategy.localities);
        TravelApi.saveGuide(strategy.id, jsonObject.toString(), new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {
            }
        });
    }
    private List<Integer> sectionlist ;
    ArrayList<ArrayList<PoiDetailBean>> routeDayMap;
    private void resizeData(ArrayList<IndexPoi> itinerary) {
        sectionlist = new ArrayList<>();
        StrategyBean strategyBean = strategy;
        routeDayMap = new ArrayList<ArrayList<PoiDetailBean>>();
        for (int i = 0; i < strategyBean.itineraryDays; i++) {
            routeDayMap.add(new ArrayList<PoiDetailBean>());
        }

        for (IndexPoi indexPoi : itinerary) {
            if (routeDayMap.size() > indexPoi.dayIndex) {
                routeDayMap.get(indexPoi.dayIndex).add(indexPoi.poi);
            }
        }
        int temp = 0;
        for (ArrayList<PoiDetailBean> list : routeDayMap) {
            sectionlist.add(temp += list.size());
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("strategy", strategy);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (!SharePrefUtil.getBoolean(this, "plan_guide1", false)) {
                GuideViewUtils.getInstance().initGuide(this, "plan_guide1", "点击这里修改计划", (int) getResources().getDimension(R.dimen.title_bar_height), -1, -1);
            }
        }
    }

    private void initView() {
        setContentView(R.layout.activity_strategy);
        draw_list = (ListView) findViewById(R.id.strategy_user_been_place_list);

        findViewById(R.id.tv_add_plan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MobclickAgent.onEvent(StrategyActivity.this, "cell_item_plan_change_select_city");
                Intent intent = new Intent(mContext, SelectDestActivity.class);
                intent.putExtra("locList", destinations);
                intent.putExtra("guide_id", strategy.id);
                intent.putExtra("request_code", EDIT_LOC_REQUEST_CODE);
                startActivityForResult(intent, EDIT_LOC_REQUEST_CODE);
            }
        });

        ButterKnife.bind(this);
        mStrategyViewpager.setCanScroll(false);
        mStrategyViewpager.setOffscreenPageLimit(3);
        // 默认是1,，自动预加载左右两边的界面。设置viewpager预加载数为0。只加载加载当前界面。
        mStrategyViewpager.setPrepareNumber(2);
        mStrategyIndicator.setDividerDrawable(getResources().getDrawable(R.color.color_line));
        indicatorViewPager = new IndicatorViewPager(mStrategyIndicator, mStrategyViewpager);
        Iv_location.setEnabled(false);
        Iv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MobclickAgent.onEvent(StrategyActivity.this, "navigation_item_lxp_plan_mapview");
                Intent intent = new Intent(StrategyActivity.this, StrategyDomesticMapActivity.class);
                ArrayList<StrategyBean> list = new ArrayList<StrategyBean>() {
                };
                list.add(getSaveStrategy());
                intent.putParcelableArrayListExtra("strategy", list);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_stay);
            }
        });
        findViewById(R.id.tv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("strategy", getSaveStrategy());
                // intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                // ;
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        findViewById(R.id.strategy_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MobclickAgent.onEvent(StrategyActivity.this, "cell_item_plan_lxp_share");
                drawerLayout.closeDrawer(GravityCompat.END);
                final Handler handler = new Handler() {
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case 1:
                                IMUtils.onClickImShare(StrategyActivity.this);
                        }
                        super.handleMessage(msg);
                    }
                };
                Message message = handler.obtainMessage(1);
                handler.sendMessageDelayed(message, 300);
            }
        });

        findViewById(R.id.tv_edit_plan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MobclickAgent.onEvent(StrategyActivity.this, "cell_item_plan_edit_plan");
                drawerLayout.closeDrawer(GravityCompat.END);
                new Handler() {
                    public void handleMessage(Message msg) {
                        Intent intent = new Intent(StrategyActivity.this, ActivityPlanEditor.class);
                        intent.putExtra("strategy", strategy);
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
                    }
                }.sendEmptyMessageDelayed(0, 300);
            }
        });
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StrategyActivity.this, ActivityPlanEditor.class);
                intent.putExtra("strategy", strategy);
                startActivity(intent);
                overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStrategyViewpager = null;
        mStrategyIndicator = null;
        routeDayFragment = null;
        collectionFragment = null;
    }

    public StrategyBean getStrategy() {
        return strategy;
    }

    public ArrayList<LocBean> getDestinations() {
        return destinations;
    }

    private void initData(Bundle savedInstanceState) {
        id = getIntent().getStringExtra("id");
        if (recomment) {
            getRecommentPlan();
        } else if (id == null) {
            cityIdList = new ArrayList<String>();
            for (LocBean loc : destinations) {
                cityIdList.add(loc.id);
            }
            createStrategyByCityIds(cityIdList, auto);

        } else {
            if (savedInstanceState != null) {
                strategy = savedInstanceState.getParcelable("strategy");
                bindView(strategy);
            } else if (getIntent().getParcelableExtra("strategy")!=null){
                strategy = getIntent().getParcelableExtra("strategy");
                bindView(strategy);
            }else {
                boolean hasCache = setupViewFromCache(id);
                if (!hasCache) {
                    try {
                       // DialogManager.getInstance().showLoadingDialog(this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                getStrategyDataById(id);
            }
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
        TravelApi.getGuideDetail(userId, itemId, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<StrategyBean> strategyResult = CommonJson.fromJson(result, StrategyBean.class);
                if (strategyResult.code == 0) {
                    bindView(strategyResult.result);
                } else {
                    ToastUtil.getInstance(StrategyActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                Iv_location.setVisibility(View.GONE);
                if (!isFinishing())
                    DialogManager.getInstance().dissMissLoadingDialog();
                ToastUtil.getInstance(StrategyActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    public void getRecommentPlan() {
        TravelApi.getRecommendPlan(locId, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<StrategyBean> bean = CommonJson4List.fromJson(result, StrategyBean.class);
                if (bean.code == 0) {
                    if (bean.result.size() > 0) {
                        bindView(bean.result.get(0));
                    }
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

    public void createStrategyByCityIds(List<String> cityIds, final boolean recommend) {
        try {
            DialogManager.getInstance().showLoadingDialog(mContext, "请稍后");
        } catch (Exception e) {
            DialogManager.getInstance().dissMissLoadingDialog();
        }

        TravelApi.createGuide("create", cityIds, recommend, null,new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<StrategyBean> strategyResult = CommonJson.fromJson(result, StrategyBean.class);
                if (strategyResult.code == 0) {
                    bindView(strategyResult.result);
                    newCreate = true;
                    newId = strategyResult.result.id;
                    SharePrefUtil.saveBoolean(mContext, "newPlan", true);
                    SharePrefUtil.saveString(mContext, "newPlanId", newId);
                    if (recommend) {
                        final ComfirmDialog cdialog = new ComfirmDialog(StrategyActivity.this);
                        cdialog.findViewById(R.id.tv_dialog_title).setVisibility(View.VISIBLE);
                        cdialog.findViewById(R.id.btn_cancle).setVisibility(View.GONE);
                        cdialog.setTitle("提示");
                        cdialog.setMessage(String.format("已为你创建%d天行程", strategyResult.result.itineraryDays));
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
                        handler.sendMessageDelayed(message, 1000);
                    } else {
                        new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                ToastUtil.getInstance(StrategyActivity.this).showToast("已保存到我的计划");
                            }
                        }.sendEmptyMessageDelayed(0, 1000);
                    }
                    int cnt = AccountManager.getInstance().getLoginAccountInfo().getGuideCnt();
                    AccountManager.getInstance().getLoginAccountInfo().setGuideCnt(cnt + 1);
                } else {
                    Iv_location.setVisibility(View.GONE);
                    if (!isFinishing())
                        ToastUtil.getInstance(StrategyActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                Iv_location.setVisibility(View.GONE);
                if (!isFinishing())
                    ToastUtil.getInstance(StrategyActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void bindView(final StrategyBean result) {
        Iv_location.setEnabled(true);
        destinations = result.localities;
        if (recomment && destinations != null) {
            recommendCityList = new ArrayList<>();
            for (LocBean bean : destinations) {
                recommendCityList.add(bean.id);
            }
        }
        strategy = result;
        TextView dtv = (TextView) findViewById(R.id.jh_title);
//        dtv.setText(result.title);
        adapter = new DrawAdapter(StrategyActivity.this);
        draw_list.setAdapter(adapter);
        final User user = AccountManager.getInstance().getLoginAccount(this);
        topTitle.setText(result.title);

        if (user == null) {
            mIvMore.setVisibility(View.GONE);
            ivEdit.setVisibility(View.GONE);
            Iv_location.setVisibility(View.VISIBLE);
            mTvCopyGuide.setVisibility(View.GONE);
        } else {
            isOwner = (user.getUserId() == result.userId);
            if (isOwner){
                llAddDay.setVisibility(View.VISIBLE);
            }
            if (!isOwner) {
                mIvMore.setVisibility(View.GONE);
                ivEdit.setVisibility(View.GONE);
                Iv_location.setVisibility(View.GONE);
                mTvCopyGuide.setVisibility(View.VISIBLE);
                mTvCopyGuide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //MobclickAgent.onEvent(StrategyActivity.this, "navigation_item_copy_plan");
                        final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
                        dialog.setTitle("提示");
                        dialog.setMessage(String.format("复制\"%s\"到我的旅行计划", result.title));
                        dialog.setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                try {
                                //    DialogManager.getInstance().showLoadingDialog(mContext);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (recomment) {
                                    TravelApi.createGuide("create", recommendCityList, true,null, new HttpCallBack<String>() {
                                        @Override
                                        public void doSuccess(String result, String method) {
                                            DialogManager.getInstance().dissMissLoadingDialog();
                                            CommonJson<CopyStrategyBean> modifyResult = CommonJson.fromJson(result, CopyStrategyBean.class);
                                            if (modifyResult.code == 0) {
                                                ToastUtil.getInstance(mContext).showToast("已复制到你的旅行计划");
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
                                } else {
                                    TravelApi.copyStrategy(result.userId, result.id, new HttpCallBack<String>() {
                                        @Override
                                        public void doSuccess(String resultStr, String method) {
                                            DialogManager.getInstance().dissMissLoadingDialog();
                                            CommonJson<CopyStrategyBean> modifyResult = CommonJson.fromJson(resultStr, CopyStrategyBean.class);
                                            if (modifyResult.code == 0) {
                                                User info = AccountManager.getInstance().getLoginAccountInfo();
                                                info.setGuideCnt(info.getGuideCnt() + 1);
                                                Intent intent = new Intent(StrategyActivity.this, StrategyListActivity.class);
                                                User user = AccountManager.getInstance().getLoginAccount(StrategyActivity.this);
                                                intent.putExtra("userId", String.valueOf(user.getUserId()));
                                                intent.putExtra("copyId", modifyResult.result.id);
                                                intent.putExtra("new_copy", true);
                                                startActivity(intent);
                                            } else {
                                                ToastUtil.getInstance(StrategyActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                                            }
                                        }

                                        @Override
                                        public void doFailure(Exception error, String msg, String method) {

                                        }

                                        @Override
                                        public void doFailure(Exception error, String msg, String method, int code) {
                                            DialogManager.getInstance().dissMissLoadingDialog();
                                            if (code == 404) {
                                                if (!isFinishing()) {
                                                    ToastUtil.getInstance(StrategyActivity.this).showToast("资源不存在");
                                                }
                                            } else if (!isFinishing()) {
                                                ToastUtil.getInstance(StrategyActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                                            }
                                        }
                                    });
                                }

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
                });
            } else {
                mIvMore.setVisibility(View.VISIBLE);
                ivEdit.setVisibility(View.VISIBLE);
                Iv_location.setVisibility(View.VISIBLE);
                mTvCopyGuide.setVisibility(View.GONE);
                dtv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //MobclickAgent.onEvent(StrategyActivity.this, "cell_item_plan_change_name");
                        final PeachEditDialog editDialog = new PeachEditDialog(mContext);
                        editDialog.setTitle("修改计划名");
                        editDialog.setMessage(strategy.title);
                        editDialog.setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final String editTitle = editDialog.getMessage();
                                if (TextUtils.isEmpty(editTitle) || editTitle.trim().length() == 0) {
                                    return;
                                }
                                editDialog.dismiss();
                                try {
                                 //   DialogManager.getInstance().showLoadingDialog(mContext);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                TravelApi.modifyGuideTitle(result.id, editTitle, new HttpCallBack<String>() {
                                    @Override
                                    public void doSuccess(String result, String method) {
                                        DialogManager.getInstance().dissMissLoadingDialog();
                                        CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result, ModifyResult.class);
                                        if (modifyResult.code == 0) {
//                                            dtv.setText(editDialog.getMessage());
                                            strategy.title = editTitle;
                                        } else {
                                            if (!isFinishing()) {
                                                ToastUtil.getInstance(StrategyActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                                            }
                                        }
                                    }

                                    @Override
                                    public void doFailure(Exception error, String msg, String method) {
                                        DialogManager.getInstance().dissMissLoadingDialog();
                                        if (!isFinishing()) {
                                            ToastUtil.getInstance(StrategyActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                                        }
                                    }

                                    @Override
                                    public void doFailure(Exception error, String msg, String method, int code) {

                                    }
                                });
                            }
                        });
                        editDialog.show();
                    }
                });
                mIvMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //这个与群聊chat里面的侧边栏内容一样，后期实现请参考ChatActivity
                        if (drawerLayout.isDrawerVisible(GravityCompat.END)) {
                            drawerLayout.closeDrawer(GravityCompat.END);//关闭抽屉
                        } else {
                            //MobclickAgent.onEvent(StrategyActivity.this, "navigiation_item_lxp_plan_setting");
                            drawerLayout.openDrawer(GravityCompat.END);//打开抽屉
                        }
                    }
                });
            }
        }
        indicatorViewPager.setAdapter(new StrategyAdapter(getSupportFragmentManager(), result));
        indicatorViewPager.setOnIndicatorPageChangeListener(new IndicatorViewPager.OnIndicatorPageChangeListener() {
            @Override
            public void onIndicatorPageChange(int preItem, int currentItem) {
                if (currentItem == 1 && count++ == 1) {
                    if (!SharePrefUtil.getBoolean(StrategyActivity.this, "plan_guide2", false)) {
                        GuideViewUtils.getInstance().initGuide(StrategyActivity.this, "plan_guide2", "添加备选心愿到收藏", (int) getResources().getDimension(R.dimen.title_bar_height) + 65, CommonUtils.getScreenWidth(StrategyActivity.this) / 2 - 100, -1);
                    }
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
    }

    private StrategyBean getSaveStrategy() {
        return strategy;
    }


    private class StrategyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        private String[] tabNames = {"行程", "收集"};
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
                convertView = inflater.inflate(R.layout.tab_select_dest, container, false);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.tv_title);
            textView.setText(tabNames[position]);
            if (position == 0) {
                textView.setBackgroundResource(R.drawable.in_out_indicator_textbg);
            } else if (position == 1) {
                textView.setBackgroundResource(R.drawable.in_out_indicator_textbg_01);
            }
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            if (position == 0) {
                //MobclickAgent.onEvent(StrategyActivity.this, "tab_item_trip_detail");
                if (routeDayFragment == null) {
                    routeDayFragment = new PlanScheduleFragment();
                }

                return routeDayFragment;
            } else {
                //MobclickAgent.onEvent(StrategyActivity.this, "tab_item_trip_favorite");
                if (collectionFragment == null) {
                    collectionFragment = new CollectionFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isOwner", isOwner);
                    collectionFragment.setArguments(bundle);
                }
                return collectionFragment;
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
            if (requestCode == EDIT_LOC_REQUEST_CODE) {
                destinations = data.getParcelableArrayListExtra("destinations");
                strategy.localities = destinations;
                adapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            Intent intent = getIntent();
            intent.putExtra("strategy", getSaveStrategy());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private class DrawAdapter extends BaseAdapter {
        private TextView place;
        private Context drawContext;

        public DrawAdapter(Context context) {
            drawContext = context;
        }

        @Override
        public int getCount() {
            return destinations.size();
        }

        @Override
        public Object getItem(int position) {
            return destinations.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(drawContext, R.layout.strategy_menu_draw_list_cell, null);
            }
            place = (TextView) convertView.findViewById(R.id.user_been_place);
            place.setText(destinations.get(position).zhName);
            place.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                    new Handler() {
                        public void handleMessage(Message msg) {
                            Intent intent = new Intent(mContext, CityInfoActivity.class);
                            intent.putExtra("id", destinations.get(position).id);
                            intent.putExtra("isFromStrategy", true);
                            startActivity(intent);
                        }
                    }.sendEmptyMessageDelayed(0, 300);
                }
            });
            return convertView;
        }
    }
}


