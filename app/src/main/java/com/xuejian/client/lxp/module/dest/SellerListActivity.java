package com.xuejian.client.lxp.module.dest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.SellerBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.widget.glide.GlideCircleTransform;
import com.xuejian.client.lxp.common.widget.twowayview.layout.DividerItemDecoration;
import com.xuejian.client.lxp.module.goods.StoreDetailActivityV2;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.techery.properratingbar.ProperRatingBar;

/**
 * Created by yibiao.qin on 2016/2/18.
 */
public class SellerListActivity extends PeachBaseActivity {

    @Bind(R.id.tv_title_back)
    TextView tvTitleBack;
    @Bind(R.id.tv_list_title)
    TextView tvListTitle;
    @Bind(R.id.lv_poi_list)
    XRecyclerView lvList;
    @Bind(R.id.empty_view)
    LinearLayout emptyView;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_list);
        ButterKnife.bind(this);
        tvTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lvList.setPullRefreshEnabled(false);
        id = getIntent().getStringExtra("id");
        initData(id);
    }

    private void initData(String id) {
        TravelApi.getSellerInCityDetail(id, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<SellerBean> list = CommonJson4List.fromJson(result, SellerBean.class);
                bindView(list.result);
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void bindView(List<SellerBean> result) {
        lvList.setLayoutManager(new LinearLayoutManager(this));
        lvList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        GoodsListAdapter adapter = new GoodsListAdapter();
        adapter.getDataList().addAll(result);
        lvList.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                Intent intent = new Intent(SellerListActivity.this, StoreDetailActivityV2.class);
                intent.putExtra("sellerId",id+"");
                startActivity(intent);
            }
        });
    }

    interface OnItemClickListener {
        void onItemClick(View view, int position, long id);
    }

    private class GoodsListAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<SellerBean> mDataList;
        private OnItemClickListener listener;
        private Context mContext;
        public GoodsListAdapter() {
            mDataList = new ArrayList<SellerBean>();
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        public ArrayList<SellerBean> getDataList() {
            return mDataList;
        }


        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_deller_list, parent, false);
            mContext = parent.getContext();
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder( ViewHolder holder, final int position) {
            final SellerBean bean = (SellerBean) getItem(position);
            if (bean.user.getAvatar()!=null){
                Glide.with(mContext)
                        .load(bean.user.getAvatar().getUrl())
                        .placeholder(R.drawable.ic_default_picture)
                        .error(R.drawable.ic_default_picture)
                        .centerCrop()
                        .transform(new GlideCircleTransform(mContext))
                        .into(holder.mIvAvatar);
            }
            holder.mTvGoodsSales.setText(String.format("交易量:%d单",bean.lastSalesVolume));
            holder.mRbGoods.setRating(4);
            holder.tv_name.setText(bean.getName());
            StringBuilder builder = new StringBuilder();
            builder.append("服务:");
            if (bean.services!=null&&bean.services.size() > 0) {

                for (String s : bean.services) {
                    builder.append(s).append(" ");
                }
                holder.mTvService.setText(builder.toString());
            }
            holder.mLlContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        listener.onItemClick(v,position,bean.getSellerId());
                    }
                }
            });
            holder.tv_level.setText(String.format("V%d",bean.level));
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_avatar)
        ImageView mIvAvatar;
        @Bind(R.id.rb_goods)
        ProperRatingBar mRbGoods;
        @Bind(R.id.tv_goods_sales)
        TextView mTvGoodsSales;
        @Bind(R.id.tv_service)
        TextView mTvService;
        @Bind(R.id.tv_name)
        TextView tv_name;
        @Bind(R.id.tv_level)
        TextView tv_level;
        @Bind(R.id.ll_container)
        LinearLayout mLlContainer;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
