package com.xuejian.client.lxp.module.dest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CouponBean;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.common.widget.NumberPicker;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2016/6/7.
 */
public class ConfirmCityActivity extends PeachBaseActivity {

    @Bind(R.id.tv_title_back)
    TextView mTvTitleBack;
    @Bind(R.id.tv_list_title)
    TextView mTvListTitle;
    @Bind(R.id.rl_normal_bar)
    RelativeLayout mRlNormalBar;
    @Bind(R.id.lv_city_list)
    XRecyclerView mLvCityList;
    @Bind(R.id.tv_save)
    TextView mTvSave;
    ArrayList<LocBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_city_list);
        ButterKnife.bind(this);
        list = getIntent().getParcelableArrayListExtra("loc");


        mTvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StrategyActivity.class);
                intent.putParcelableArrayListExtra("destinations", list);
                intent.putExtra("auto", false);
                startActivity(intent);
                finish();
            }
        });
        bindView();
    }

    private void bindView() {
        mLvCityList.setLayoutManager(new LinearLayoutManager(this));
        GoodsListAdapter adapter = new GoodsListAdapter();
        adapter.getDataList().addAll(list);
        mLvCityList.setAdapter(adapter);
    }

    interface OnItemClickListener {
        void onItemClick(View view, int position, CouponBean id);
    }

    private class GoodsListAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<LocBean> mDataList;
        private OnItemClickListener listener;
        private Context mContext;

        public GoodsListAdapter() {
            mDataList = new ArrayList<LocBean>();
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        public ArrayList<LocBean> getDataList() {
            return mDataList;
        }


        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_confirm_city, parent, false);
            mContext = parent.getContext();
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final LocBean bean = (LocBean) getItem(position);
            holder.mTvCityName.setText(bean.zhName);
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_city_name)
        TextView mTvCityName;
        @Bind(R.id.select_num)
        NumberPicker mSelectNum;
        @Bind(R.id.ll_container)
        LinearLayout mLlContainer;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
