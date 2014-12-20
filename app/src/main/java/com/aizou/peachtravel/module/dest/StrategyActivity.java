package com.aizou.peachtravel.module.dest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.pagerIndicator.indicator.FixedIndicatorView;
import com.aizou.core.widget.pagerIndicator.indicator.IndicatorViewPager;
import com.aizou.core.widget.pagerIndicator.viewpager.FixedViewPager;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.StrategyBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.share.ShareDialogBean;
import com.aizou.peachtravel.common.utils.ShareUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.dest.fragment.RestaurantFragment;
import com.aizou.peachtravel.module.dest.fragment.RouteDayFragment;
import com.aizou.peachtravel.module.dest.fragment.ShoppingFragment;

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
    private List<String> cityIdList;

    private ArrayList<LocBean> destinations;

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
        mTitleBar.enableBackKey(true);
        mTitleBar.setRightViewImageRes(R.drawable.ic_share);
        mTitleBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.showSelectPlatformDialog(StrategyActivity.this);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
    }

    private void initData() {
        id = getIntent().getStringExtra("id");
        if (id == null) {
            cityIdList = new ArrayList<String>();
            for (LocBean loc : destinations) {
//            cityIdList.add(loc.id);
            }
            //test
            cityIdList.add("5473ccd7b8ce043a64108c46");
            cityIdList.add("5473ccddb8ce043a64108d22");
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

            }
        });
    }

    public void createStrategyByCityIds(List<String> cityIds) {
        TravelApi.createGuide(cityIds, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<StrategyBean> strategyResult = CommonJson.fromJson(result, StrategyBean.class);
                if (strategyResult.code == 0) {
                    ToastUtil.getInstance(mContext).showToast("已保存到旅行Memo");
                    bindView(strategyResult.result);
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }
        });
    }

    private void bindView(StrategyBean result) {
        mTitleBar.getTitleTextView().setText(result.title);
        indicatorViewPager = new IndicatorViewPager(mStrategyIndicator, mStrategyViewpager);
        indicatorViewPager.setAdapter(new StrategyAdapter(getSupportFragmentManager(), result));
        mLocListRv.setAdapter(new LocAdapter(mContext, result.localities));

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
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CityDetailActivity.class);
                    intent.putExtra("id", mDatas.get(i).id);
                    startActivity(intent);
                }
            });

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

                }
            });
        }
    }

    private class StrategyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        private String[] tabNames = {"玩安排", "吃清单", "买清单",};
        private int[] tabIcons = {R.drawable.checker_tab_plan_list, R.drawable.checker_tab_delicacy_list, R.drawable.checker_tab_shopping_list};
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
                RouteDayFragment routeDayFragment = new RouteDayFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", strategyBean.id);
                bundle.putString("title", strategyBean.title);
                bundle.putParcelableArrayList("itinerary", strategyBean.itinerary);
                bundle.putInt("day", strategyBean.itineraryDays);
                bundle.putParcelableArrayList("locList", strategyBean.localities);
                routeDayFragment.setArguments(bundle);
                return routeDayFragment;
            } else if (position == 1) {
                RestaurantFragment restFragment = new RestaurantFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", strategyBean.id);
                bundle.putString("title", strategyBean.title);
                bundle.putParcelableArrayList("restaurant", strategyBean.restaurant);
                bundle.putParcelableArrayList("locList", strategyBean.localities);
                restFragment.setArguments(bundle);
                return restFragment;
            } else {
                ShoppingFragment shoppingFragment = new ShoppingFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", strategyBean.id);
                bundle.putString("title", strategyBean.title);
                bundle.putParcelableArrayList("shopping", strategyBean.shopping);
                bundle.putParcelableArrayList("locList", strategyBean.localities);
                shoppingFragment.setArguments(bundle);
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
        warnCancel();
    }

    private void warnCancel() {
        new MaterialDialog.Builder(this)
                .title("提示")
                .content("是否先保存已完成的清单")
                .positiveText("保存")
                .negativeText("直接返回")
                .callback(new MaterialDialog.Callback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        finish();
                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {
//                        Intent intent = new Intent(StrategyActivity.this, StrategyListActivity.class);
//                        intent.setAction("plan.flow"); //magic number in stand of being start in plan flow
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_stay);
                        Toast.makeText(StrategyActivity.this, "已保存到\"旅行Memo\"", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .show();
    }
}
