package com.xuejian.client.lxp.module.dest.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aizou.core.utils.SharePrefUtil;
import com.aizou.core.widget.pagerIndicator.indicator.FixedIndicatorView;
import com.aizou.core.widget.pagerIndicator.indicator.IndicatorViewPager;
import com.aizou.core.widget.pagerIndicator.viewpager.FixedViewPager;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.GuideViewUtils;
import com.xuejian.client.lxp.module.dest.SelectCityActivity;

/**
 * Created by xuyongchen on 15/9/23.
 */
public class DestinationFragment extends PeachBaseFragment {
    private FixedIndicatorView densty_indicator;
    private FixedViewPager densty_viewpager;
    private IndicatorViewPager indicatorViewPager;
    private HomeTownFragment homeTownFragment;
    private OverSeasFragment overSeasFragment;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.destination_fragment,container,false);
        densty_indicator = (FixedIndicatorView) view.findViewById(R.id.densty_indicator);
        densty_viewpager = (FixedViewPager)view.findViewById(R.id.densty_viewpager);
        densty_viewpager.setCanScroll(false);
        densty_viewpager.setOffscreenPageLimit(3);
        // 默认是1,，自动预加载左右两边的界面。设置viewpager预加载数为0。只加载加载当前界面。
        densty_viewpager.setPrepareNumber(2);
        //densty_indicator.setDividerDrawable(getResources().getDrawable(R.color.color_line));
        indicatorViewPager = new IndicatorViewPager(densty_indicator, densty_viewpager);
        indicatorViewPager.setAdapter(new DenstyAdapter(getActivity().getSupportFragmentManager()));
        indicatorViewPager.setOnIndicatorPageChangeListener(new IndicatorViewPager.OnIndicatorPageChangeListener() {
            @Override
            public void onIndicatorPageChange(int preItem, int currentItem) {

            }
        });
        (view.findViewById(R.id.desty_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent = new Intent(getActivity(), SelectCityActivity.class);
                 startActivity(intent);
            }
        });
        return view;
    }


    private class DenstyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        private String[] tabNames = {"国内", "国外"};
        private LayoutInflater inflater;

        public DenstyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            inflater = LayoutInflater.from(getActivity());
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

                if (homeTownFragment == null) {
                    homeTownFragment = new HomeTownFragment();
                }
                return homeTownFragment;
            } else {

                if (overSeasFragment == null) {
                    overSeasFragment = new OverSeasFragment();
                }

                return overSeasFragment;
            }
        }
    }
}
