package com.aizou.peachtravel.module.toolbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.StrategyBean;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/1.
 */
public class MyStrategyActivity extends PeachBaseActivity {

    @InjectView(R.id.title_bar)
    TitleHeaderBar mTitleBar;
    @InjectView(R.id.my_strategy_lv)
    PullToRefreshListView mMyStrategyLv;
    @InjectView(R.id.edit_btn)
    Button mEditBtn;
    ListViewDataAdapter mStrategyListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_my_strategy);
        ButterKnife.inject(this);
        mMyStrategyLv.setPullLoadEnabled(false);
        mMyStrategyLv.setPullRefreshEnabled(false);
        mMyStrategyLv.setScrollLoadEnabled(true);
        mStrategyListAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                return new StrategyListViewHolder();
            }
        });
        mMyStrategyLv.getRefreshableView().setAdapter(mStrategyListAdapter);

    }

    private void initData() {
    }

    private void getStrategyListData(){

    }
    private class StrategyListViewHolder extends ViewHolderBase<StrategyBean> {

        @InjectView(R.id.delete_iv)
        ImageView mDeleteIv;
        @InjectView(R.id.strategy_iv)
        ImageView mStrategyIv;
        @InjectView(R.id.day_tv)
        TextView mDayTv;
        @InjectView(R.id.citys_tv)
        TextView mCitysTv;
        @InjectView(R.id.name_tv)
        TextView mNameTv;
        @InjectView(R.id.time_tv)
        TextView mTimeTv;

        @Override
        public View createView(LayoutInflater layoutInflater) {
            View view = layoutInflater.inflate(R.layout.row_my_strategy, mMyStrategyLv.getRefreshableView(), false);
            ButterKnife.inject(this,view);
            return view;
        }

        @Override
        public void showData(int position, StrategyBean itemData) {
            if(itemData.images!=null&&itemData.images.size()>0){
                ImageLoader.getInstance().displayImage(itemData.images.get(0).url,mStrategyIv, UILUtils.getDefaultOption());
            }
            mDayTv.setText(itemData.dayCnt+"天");
            mCitysTv.setText(itemData.summary);
            mNameTv.setText(itemData.title);
            mTimeTv.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(itemData.updateTime)));
            mDeleteIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


        }
    }
}
