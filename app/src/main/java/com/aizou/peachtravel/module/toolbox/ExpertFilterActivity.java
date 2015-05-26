package com.aizou.peachtravel.module.toolbox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aizou.core.widget.pagerIndicator.indicator.FixedIndicatorView;
import com.aizou.core.widget.pagerIndicator.indicator.IndicatorViewPager;
import com.aizou.core.widget.pagerIndicator.viewpager.FixedViewPager;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.dest.fragment.ExpertFragment;
import com.aizou.peachtravel.module.dest.fragment.InDestFragment;
import com.aizou.peachtravel.module.dest.fragment.OutCountryFragment;

/**
 * Created by lxp_dqm07 on 2015/5/18.
 */
public class ExpertFilterActivity extends PeachBaseActivity {

    private FixedIndicatorView inOutIndicator;
    private FixedViewPager mSelectDestVp;
    private IndicatorViewPager indicatorViewPager;
    private TitleHeaderBar titleHeaderBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expertfilter);
        titleHeaderBar = (TitleHeaderBar) findViewById(R.id.expert_filter_title);
        titleHeaderBar.getTitleTextView().setText("根据城市筛选");
        titleHeaderBar.findViewById(R.id.ly_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,R.anim.push_bottom_out);
            }
        });
        inOutIndicator = (FixedIndicatorView) findViewById(R.id.expert_in_out_indicator);
        mSelectDestVp = (FixedViewPager) findViewById(R.id.expert_select_dest_viewPager);
        indicatorViewPager = new IndicatorViewPager(inOutIndicator,mSelectDestVp);
        indicatorViewPager.setAdapter(new InOutFragmentAdapter(getSupportFragmentManager()));
        mSelectDestVp.setCanScroll(false);
        // 设置viewpager保留界面不重新加载的页面数量
        mSelectDestVp.setOffscreenPageLimit(2);
        // 默认是1,，自动预加载左右两边的界面。设置viewpager预加载数为0。只加载加载当前界面。
        mSelectDestVp.setPrepareNumber(0);
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
            if(position==0){
                textView.setBackgroundResource(R.drawable.in_out_indicator_textbg);
            }else if(position==1){
                textView.setBackgroundResource(R.drawable.in_out_indicator_textbg_01);
            }
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            if (position == 0) {
                ExpertFragment inDestFragment = new ExpertFragment(position);
                return inDestFragment;
            } else if (position == 1) {
                ExpertFragment outCountryFragment = new ExpertFragment(position);
                return outCountryFragment;
            }
            return null;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
