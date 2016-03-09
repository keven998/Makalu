package com.xuejian.client.lxp.module.trade;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yibiao.qin on 2015/11/11.
 */
public class TradeOrderListActivity extends PeachBaseActivity {

    public static final int ORDER = 1;
    public static final int GOODS = 2;
    private int type ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        type = getIntent().getIntExtra("type",0);
        TextView title = (TextView) findViewById(R.id.strategy_title);
        if (type == ORDER){
            title.setText("订单");
        }else if (type == GOODS){
            title.setText("商品");
        }

        int page = getIntent().getIntExtra("page", 0);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(getResources().getColor(R.color.color_text_ii), getResources().getColor(R.color.price_color));
        viewPager.setCurrentItem(page);
        findViewById(R.id.tv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());

        if (type==ORDER){
            adapter.addFragment(new TradeOrderListFragment(), "待发货",TradeOrderListFragment.TO_COMMIT);
            adapter.addFragment(new TradeOrderListFragment(), "待退款",TradeOrderListFragment.TO_DRAWBACK);
            adapter.addFragment(new TradeOrderListFragment(), "全部",TradeOrderListFragment.ALL);
        }else if (type==GOODS){
            adapter.addFragment(new TradeGoodsListFragment(), "已发布",TradeGoodsListFragment.PUB);
            adapter.addFragment(new TradeGoodsListFragment(), "审核中",TradeGoodsListFragment.REVIEW);
            adapter.addFragment(new TradeGoodsListFragment(), "已下架",TradeGoodsListFragment.DISABLE);
        }

        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title, int type) {
            Bundle bundle = new Bundle();
            bundle.putInt("type", type);
            fragment.setArguments(bundle);
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
