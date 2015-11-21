package com.xuejian.client.lxp.module.goods.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
    public static int color;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
        color = getResources().getColor(R.color.price_color);
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
                list,type);
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
        private int type;
        public class ViewHolder extends RecyclerView.ViewHolder {
            public final RelativeLayout rlNeedPay;
            public final TextView tvNeedPayState;
            public final TextView tvNeedPayTalk;
            public final TextView tvNeedPayPay;

            public final RelativeLayout rlProcess;
            public final TextView tvProcessState;
            public final TextView tvProcessTalk;

            public final RelativeLayout rlAvailable;
            public final TextView tvAvailableState;
            public final TextView tvAvailableTalk;
            public final TextView tvAvailableMessage;

            public final RelativeLayout rlDrawBack;
            public final TextView tvDrawBackState;
            public final TextView tvDrawBackTalk;

            public final ImageView mImageView;
            public final TextView tvGoodsName;
            public ViewHolder(View view) {
                super(view);

                rlNeedPay  = (RelativeLayout) view.findViewById(R.id.rl_need_pay);
                tvNeedPayState  = (TextView) view.findViewById(R.id.tv_need_pay_state);
                tvNeedPayTalk  = (TextView) view.findViewById(R.id.tv_need_pay_talk);
                tvNeedPayPay  = (TextView) view.findViewById(R.id.tv_pay);

                rlProcess  = (RelativeLayout) view.findViewById(R.id.rl_process);
                tvProcessState  = (TextView) view.findViewById(R.id.tv_process_state);
                tvProcessTalk  = (TextView) view.findViewById(R.id.tv_process_talk);

                rlAvailable  = (RelativeLayout) view.findViewById(R.id.rl_available);
                tvAvailableState  = (TextView) view.findViewById(R.id.tv_available_state);
                tvAvailableTalk  = (TextView) view.findViewById(R.id.tv_available_talk);
                tvAvailableMessage  = (TextView) view.findViewById(R.id.tv_available_message);

                rlDrawBack  = (RelativeLayout) view.findViewById(R.id.rl_drawback);
                tvDrawBackState  = (TextView) view.findViewById(R.id.tv_drawback_state);
                tvDrawBackTalk  = (TextView) view.findViewById(R.id.tv_drawback_talk);

                mImageView = (ImageView) view.findViewById(R.id.iv_goods_img);
                tvGoodsName = (TextView) view.findViewById(R.id.tv_goods_name);
            }
        }

        public OrderListAdapter(Context context, List<String> items,int type) {
            mContext = context;
            mValues = items;
            this.type = type;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            switch (type){
                case PROCESS:
                    holder.rlProcess.setVisibility(View.VISIBLE);
                    break;
                case AVAILABLE:
                    holder.rlAvailable.setVisibility(View.VISIBLE);
                    break;
                case DRAWBACK:
                    holder.rlDrawBack.setVisibility(View.VISIBLE);
                    break;
                case NEED_PAY:
                    holder.rlNeedPay.setVisibility(View.VISIBLE);
                    SpannableString priceStr = new SpannableString("¥35353");
                    priceStr.setSpan(new ForegroundColorSpan(color), 0, priceStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    priceStr.setSpan(new AbsoluteSizeSpan(13, true), 0, priceStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder spb = new SpannableStringBuilder();
                    spb.append("待付款:").append(priceStr);
                    holder.tvNeedPayState.setText(spb);
                    break;
                default:
                    holder.rlNeedPay.setVisibility(View.VISIBLE);
                    holder.rlNeedPay.setVisibility(View.VISIBLE);
                    SpannableString str = new SpannableString("¥35353");
                    str.setSpan(new ForegroundColorSpan(color), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    str.setSpan(new AbsoluteSizeSpan(13, true), 0, str.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder spb1 = new SpannableStringBuilder();
                    spb1.append("待付款:").append(str);
                    holder.tvNeedPayState.setText(spb1);
                    break;
            }


            ImageLoader.getInstance().displayImage("http://taozi-uploads.qiniudn.com/avt_100004_1443601212983.jpg", holder.mImageView, UILUtils.getDefaultOption());
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(holder.itemView, position);
                    }
                }
            });


            holder.tvNeedPayPay.setOnClickListener(new View.OnClickListener() {
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
