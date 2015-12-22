package com.xuejian.client.lxp.module.goods;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.marshalchen.ultimaterecyclerview.uiUtils.BasicGridLayoutManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by yibiao.qin on 2015/12/18.
 */
public class StoreDetailActivity extends PeachBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        findViewById(R.id.tv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        UltimateRecyclerView ultimateRecyclerView = (UltimateRecyclerView) findViewById(R.id.ul_recyclerView);
        ultimateRecyclerView.setHasFixedSize(false);
        Adapter adapter= new Adapter(this);
        for (int i = 0; i < 10; i++) {
            adapter.getList().add("a");
        }
        ultimateRecyclerView.setLayoutManager(new BasicGridLayoutManager(this,2,adapter));
        ultimateRecyclerView.setAdapter(adapter);

        View view = getLayoutInflater().inflate(R.layout.head_store_detail,ultimateRecyclerView,false);
        ultimateRecyclerView.setNormalHeader(view);

    }

    public class Adapter extends UltimateViewAdapter<Adapter.ViewHolder> {

        private ArrayList<String> list;
        private Context mContext;
        private int w;
        public Adapter(Context context) {
            this.mContext = context;
            list = new ArrayList<>();
            w = CommonUtils.getScreenWidth((Activity) mContext);
        }

        public ArrayList<String> getList() {
            return list;
        }

        public class ViewHolder extends UltimateRecyclerviewViewHolder {
            public final TextView tvCurrentPrice;
            public final TextView tvPrice;
            public final TextView tvSales;
            public final ImageView mImageView;
            public final TextView tvGoodsName;

            public ViewHolder(View itemView) {
                super(itemView);
                tvGoodsName = (TextView) itemView.findViewById(R.id.tv_goods_name);
                tvCurrentPrice = (TextView) itemView.findViewById(R.id.tv_goods_current_price);
                tvPrice = (TextView) itemView.findViewById(R.id.tv_goods_price);
                tvSales = (TextView) itemView.findViewById(R.id.tv_goods_sales);
                mImageView = (ImageView) itemView.findViewById(R.id.iv_goods_img);
            }
        }

        @Override
        public ViewHolder getViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_goods_grid, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public int getAdapterItemCount() {
            return list.size();
        }

        @Override
        public long generateHeaderId(int position) {
            return 0;
        }

        public Object getItem(int pos) {
            return list.get(pos);
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            if (VIEW_TYPES.HEADER == getItemViewType(position)) {
                onBindHeaderViewHolder(holder, position);
            } else if (VIEW_TYPES.NORMAL == getItemViewType(position)) {
                Object o = getItem(hasHeaderView() ? position - 1 : position);
                ViewGroup.LayoutParams layoutParams = holder.mImageView.getLayoutParams();
                int w1 = w/2-20;
                int h1 = w1* 2 / 3;
                layoutParams.width = w1;
                layoutParams.height =  h1;
                ImageLoader.getInstance().displayImage("http://7sbm17.com1.z0.glb.clouddn.com/commodity/images/50d1b9ad960899382bc982e9818b688b", holder.mImageView);
                holder.mImageView.setLayoutParams(layoutParams);
                holder.tvSales.setText(String.format("销量:%d件", 1000));
                holder.tvPrice.setText(String.format("¥%s", String.valueOf((float) (Math.round(3453.87 * 10) / 10))));
                holder.tvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tvPrice.getPaint().setAntiAlias(true);
                holder.tvCurrentPrice.setText(String.format("¥%s", String.valueOf((float) (Math.round(155.34 * 10) / 10))));
                holder.tvGoodsName.setText("港澳台7日游");
            }
        }

        @Override
        public ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            return null;
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

    }
}
