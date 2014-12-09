package com.aizou.peachtravel.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.core.widget.pagerIndicator.indicator.FixedIndicatorView;
import com.aizou.core.widget.pagerIndicator.indicator.IndicatorViewPager;
import com.aizou.core.widget.pagerIndicator.indicator.slidebar.ColorBar;
import com.aizou.core.widget.pagerIndicator.viewpager.FixedViewPager;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.CountryBean;
import com.aizou.peachtravel.bean.InDestBean;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.common.widget.FlowLayout;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.common.widget.TopSectionBar;
import com.aizou.peachtravel.common.widget.expandablelayout.ExpandableLayoutItem;
import com.aizou.peachtravel.common.widget.expandablelayout.ExpandableLayoutListView;
import com.aizou.peachtravel.module.dest.fragment.InDestFragment;
import com.aizou.peachtravel.module.dest.fragment.OutCountryFragment;
import com.easemob.util.HanziToPinyin;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Rjm on 2014/10/9.
 */
public class SelectDestActivity extends PeachBaseActivity implements OnDestActionListener {
    private RadioGroup inOutRg;
    private LinearLayout citysLl;
    private FrameLayout mBottomPanel;
    private TextView startTv;
    private FixedIndicatorView inOutIndicator;
    private FixedViewPager mSelectDestVp;
    private IndicatorViewPager indicatorViewPager;
    private ArrayList<LocBean> allAddCityList = new ArrayList<LocBean>();
    private Set<OnDestActionListener> mOnDestActionListeners= new HashSet<OnDestActionListener>();
    private HorizontalScrollView mScrollPanel;

    @Override
    public void onDestAdded(final LocBean locBean) {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = View.inflate(mContext, R.layout.activity_select_dest, null);
        setContentView(rootView);
        initTitleBar();
        citysLl = (LinearLayout) rootView.findViewById(R.id.ll_citys);
        mScrollPanel = (HorizontalScrollView) rootView.findViewById(R.id.scroll_panel);
        mBottomPanel = (FrameLayout) rootView.findViewById(R.id.bottom_panel);
        startTv = (TextView) rootView.findViewById(R.id.tv_start);
        inOutIndicator = (FixedIndicatorView) rootView.findViewById(R.id.in_out_indicator);
        mSelectDestVp = (FixedViewPager) rootView.findViewById(R.id.select_dest_viewPager);
        startTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, StrategyActivity.class);
                intent.putParcelableArrayListExtra("destinations", allAddCityList);
                startActivity(intent);
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
        initData();
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

    private void initTitleBar(){
        final TitleHeaderBar titleHeaderBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
        titleHeaderBar.setRightViewImageRes(R.drawable.ic_search);
        titleHeaderBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        titleHeaderBar.enableBackKey(true);
        titleHeaderBar.getTitleTextView().setText("选择想去的城市");

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
            if(position==0){
                InDestFragment inDestFragment = new InDestFragment();
                return inDestFragment;
            }else if(position==1){
                OutCountryFragment outCountryFragment = new OutCountryFragment();
                return outCountryFragment;
            }
            return null;

        }
    }

}
