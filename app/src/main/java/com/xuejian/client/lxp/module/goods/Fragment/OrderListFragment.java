package com.xuejian.client.lxp.module.goods.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.module.goods.OrderDetailActivity;
import com.xuejian.client.lxp.module.pay.PaymentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yibiao.qin on 2015/11/11.
 */
public class OrderListFragment extends PeachBaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout mSwipeRefreshWidget;
    private int type;
    public static final int ALL =1;
    public static final int NEED_PAY =2;
    public static final int PROCESS =3;
    public static final int AVAILABLE =4;
    public static final int DRAWBACK =5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(
                R.layout.fragment_order_list, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerview);
        TextView empty = (TextView) view.findViewById(R.id.empty_view);
        mSwipeRefreshWidget = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshWidget.setOnRefreshListener(this);
        mSwipeRefreshWidget.setColorSchemeResources(R.color.app_theme_color);
        setupRecyclerView(rv);
//        rv.setVisibility(View.GONE);
//        empty.setVisibility(View.VISIBLE);
//        empty.setText("您没有处理中的订单");
        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            list.add("" + i);
        }
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        OrderListAdapter adapter = new OrderListAdapter(getActivity(),
                list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OrderListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshWidget.setRefreshing(false);
            }
        }, 2000);
    }

    static class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {
        public interface OnItemClickListener{
            void onItemClick(View view, int position);
        }
        private OnItemClickListener listener;
        private List<String> mValues;
        private Context mContext;
        public class ViewHolder extends RecyclerView.ViewHolder {
            public final ImageView mImageView;
            public final TextView tvGoodsName;
            public final TextView tvPay;
            public final TextView tvCancel;
            public ViewHolder(View view) {
                super(view);
                mImageView = (ImageView) view.findViewById(R.id.iv_goods_img);
                tvGoodsName = (TextView) view.findViewById(R.id.tv_goods_name);
                tvPay = (TextView) view.findViewById(R.id.tv_pay);
                tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
            }
        }

        public OrderListAdapter(Context context, List<String> items) {
            mContext = context;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            ImageLoader.getInstance().displayImage("http://taozi-uploads.qiniudn.com/avt_100004_1443601212983.jpg", holder.mImageView, UILUtils.getDefaultOption());
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(holder.itemView, position);
                    }
                }
            });
            holder.tvPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PaymentActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }

    }
}
