package com.aizou.peachtravel.module.toolbox;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.pagerIndicator.indicator.IndicatorViewPager;
import com.aizou.core.widget.pagerIndicator.indicator.ScrollIndicatorView;
import com.aizou.core.widget.pagerIndicator.indicator.slidebar.ColorBar;
import com.aizou.core.widget.pagerIndicator.viewpager.FixedViewPager;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.common.widget.FlowLayout;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.toolbox.fragment.NearbyItemFragment;
import com.umeng.socialize.utils.Log;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/24.
 */
public class NearbyActivity extends PeachBaseActivity {
    @InjectView(R.id.title_bar)
    TitleHeaderBar mTitleBar;
    @InjectView(R.id.nearby_indicator)
    ScrollIndicatorView mNearbyIndicator;
    @InjectView(R.id.nearby_viewPager)
    FixedViewPager mNearbyViewPager;
    private IndicatorViewPager indicatorViewPager;

    private String[] tabTitles;
    private int[] tabRes = {R.drawable.checker_tab_nearby_ic_spot, R.drawable.checker_tab_nearby_ic_delicacy, R.drawable.checker_tab_nearby_ic_shopping, R.drawable.checker_tab_nearby_ic_stay};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tabTitles = getResources().getStringArray(R.array.local_type_title);

        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_nearby);
        ButterKnife.inject(this);
        mNearbyIndicator.setScrollBar(new ColorBar(mContext, getResources().getColor(R.color.app_theme_color), 5));
        mNearbyViewPager.setOffscreenPageLimit(2);
        indicatorViewPager = new IndicatorViewPager(mNearbyIndicator, mNearbyViewPager);
        indicatorViewPager.setAdapter(new NearbyAdapter(getSupportFragmentManager()));

        mTitleBar.getTitleTextView().setText("我身边");
        mTitleBar.enableBackKey(true);
    }

    private class NearbyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

        public NearbyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.tab_top, null);
                int width = LocalDisplay.SCREEN_WIDTH_PIXELS/4;
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                convertView.setLayoutParams(params);
            }
            CheckedTextView textView = (CheckedTextView) convertView;
            textView.setText(tabTitles[position]);
            textView.setCompoundDrawablesWithIntrinsicBounds(0, tabRes[position], 0, 0);
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            NearbyItemFragment fragment = new NearbyItemFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("intent_int_index", position);
            fragment.setArguments(bundle);
            return fragment;
        }

    };


}
