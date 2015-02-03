package com.aizou.peachtravel.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.widget.pagerIndicator.indicator.FixedIndicatorView;
import com.aizou.core.widget.pagerIndicator.indicator.IndicatorViewPager;
import com.aizou.core.widget.pagerIndicator.viewpager.FixedViewPager;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.dest.fragment.InDestFragment;
import com.aizou.peachtravel.module.dest.fragment.OutCountryFragment;
import com.aizou.peachtravel.module.my.LoginActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Rjm on 2014/10/9.
 */
public class SelectDestActivity extends PeachBaseActivity implements OnDestActionListener {
    public final static int REQUEST_CODE_SEARCH_LOC = 101;
    public final static int REQUEST_CODE_LOGIN = 102;
    public final static int REQUEST_CODE_NEW_PLAN=103;

    //    private RadioGroup inOutRg;
    private LinearLayout citysLl;
    private FrameLayout mBottomPanel;
    private FixedIndicatorView inOutIndicator;
    private FixedViewPager mSelectDestVp;
    private IndicatorViewPager indicatorViewPager;
    private ArrayList<LocBean> allAddCityList = new ArrayList<LocBean>();
    private LocBean hasSelectLoc;
    private Set<OnDestActionListener> mOnDestActionListeners = new HashSet<OnDestActionListener>();
    private HorizontalScrollView mScrollPanel;

    @Override
    public void onDestAdded(final LocBean locBean) {
        if(allAddCityList.contains(locBean)){
            ToastUtil.getInstance(mContext).showToast("已添加");
            return;
        }
        View cityView = View.inflate(mContext, R.layout.dest_add_item, null);
        citysLl.addView(cityView);
        allAddCityList.add(locBean);
        TextView cityNameTv = (TextView) cityView.findViewById(R.id.tv_city_name);
        cityNameTv.setText(locBean.zhName);
        cityNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = allAddCityList.indexOf(locBean);
                citysLl.removeViewAt(index);
                allAddCityList.remove(locBean);
                if (allAddCityList.size() == 0) {
                    mBottomPanel.setVisibility(View.GONE);
                }
                for(OnDestActionListener onDestActionListener:mOnDestActionListeners){
                    onDestActionListener.onDestRemoved(locBean);
                }
                autoScrollPanel();
            }
        });

        if (allAddCityList.size() > 0) {
            mBottomPanel.setVisibility(View.VISIBLE);
        }

        autoScrollPanel();
    }

    @Override
    public void onDestRemoved(LocBean locBean) {
        int index = allAddCityList.indexOf(locBean);
        citysLl.removeViewAt(index);
        allAddCityList.remove(locBean);
        if (allAddCityList.size() == 0) {
            mBottomPanel.setVisibility(View.GONE);
        }
        autoScrollPanel();
    }

    private void autoScrollPanel() {
        mScrollPanel.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScrollPanel.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 100);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = View.inflate(mContext, R.layout.activity_select_dest, null);
        setContentView(rootView);
        initTitleBar();
        citysLl = (LinearLayout) rootView.findViewById(R.id.ll_citys);
        mScrollPanel = (HorizontalScrollView) rootView.findViewById(R.id.scroll_panel);
        mBottomPanel = (FrameLayout) rootView.findViewById(R.id.bottom_panel);
        inOutIndicator = (FixedIndicatorView) rootView.findViewById(R.id.in_out_indicator);
        mSelectDestVp = (FixedViewPager) rootView.findViewById(R.id.select_dest_viewPager);
        rootView.findViewById(R.id.tv_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PeachUser user = AccountManager.getInstance().getLoginAccount(mContext);
                if (user != null) {
                    Intent intent = new Intent(mContext, StrategyActivity.class);
                    intent.putParcelableArrayListExtra("destinations", allAddCityList);
                    startActivityForResult(intent,REQUEST_CODE_NEW_PLAN);
                } else {
                    ToastUtil.getInstance(mContext).showToast("请先登录");
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_LOGIN);
                }
            }
        });
        indicatorViewPager = new IndicatorViewPager(inOutIndicator,mSelectDestVp);
        indicatorViewPager.setAdapter(new InOutFragmentAdapter(getSupportFragmentManager()));
//        indicatorViewPager.setIndicatorScrollBar(new ColorBar(mContext, getResources().getColor(R.color.app_theme_color), 5));
        mSelectDestVp.setCanScroll(false);
        // 设置viewpager保留界面不重新加载的页面数量
        mSelectDestVp.setOffscreenPageLimit(2);
        // 默认是1,，自动预加载左右两边的界面。设置viewpager预加载数为0。只加载加载当前界面。
        mSelectDestVp.setPrepareNumber(0);
        hasSelectLoc = getIntent().getParcelableExtra("loc");
        if(hasSelectLoc!=null){
            onDestAdded(hasSelectLoc);
            for(OnDestActionListener onDestActionListener:mOnDestActionListeners){
                onDestActionListener.onDestAdded(hasSelectLoc);
            }
        }
//        initData();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        try {
            OnDestActionListener listener = (OnDestActionListener)fragment;
            mOnDestActionListeners.add(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onAttachFragment(fragment);
    }

    public ArrayList<LocBean> getAllSelectedLoc(){
        return allAddCityList;
    }

    private void initTitleBar(){
        final TitleHeaderBar titleHeaderBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
        //UNUSED
//        titleHeaderBar.setRightViewImageRes(R.drawable.ic_search);
//        titleHeaderBar.setRightOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, SearchDestActivity.class);
//                startActivityForResult(intent, REQUEST_CODE_SEARCH_LOC);
//            }
//        });
        titleHeaderBar.enableBackKey(true);
        titleHeaderBar.getTitleTextView().setText("选择目的地");
    }

    private void initData() {

    }


    private class InOutFragmentAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        private String[] tabNames = { "国内", "国外"};
        private LayoutInflater inflater;

        public InOutFragmentAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            inflater = LayoutInflater.from(getApplicationContext());
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
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            if (position == 0) {
                InDestFragment inDestFragment = new InDestFragment();
                return inDestFragment;
            } else if (position == 1) {
                OutCountryFragment outCountryFragment = new OutCountryFragment();
                return outCountryFragment;
            }
            return null;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SEARCH_LOC) {
                LocBean locBean = data.getParcelableExtra("loc");
                onDestAdded(locBean);
                for(OnDestActionListener onDestActionListener:mOnDestActionListeners){
                    onDestActionListener.onDestAdded(locBean);
                }
            } else if (requestCode == REQUEST_CODE_LOGIN) {
                Intent intent = new Intent(mContext, StrategyActivity.class);
                intent.putParcelableArrayListExtra("destinations", allAddCityList);
                startActivityWithNoAnim(intent);
                finishWithNoAnim();
            }else if(requestCode==REQUEST_CODE_NEW_PLAN){
                setResult(RESULT_OK,data);
                finishWithNoAnim();
            }
        }
    }
}
