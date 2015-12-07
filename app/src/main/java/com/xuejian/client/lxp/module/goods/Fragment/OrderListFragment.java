package com.xuejian.client.lxp.module.goods.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.aizou.core.http.HttpCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.OrderBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.module.goods.OrderDetailActivity;
import com.xuejian.client.lxp.module.pay.PaymentActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yibiao.qin on 2015/11/11.
 */
public class OrderListFragment extends PeachBaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout mSwipeRefreshWidget;
    private int type;
    public static final int ALL = 1;
    public static final int NEED_PAY = 2;
    public static final int PROCESS = 3;
    public static final int AVAILABLE = 4;
    public static final int DRAWBACK = 5;
    OrderListAdapter adapter;
    RecyclerView recyclerView;
    TextView empty;
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
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        empty = (TextView) view.findViewById(R.id.empty_view);
        mSwipeRefreshWidget = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshWidget.setOnRefreshListener(this);
        mSwipeRefreshWidget.setColorSchemeResources(R.color.app_theme_color);
        setupRecyclerView(recyclerView);
       switch (type){
           case ALL:
               getOrder("");
               empty.setText("您近期没有出行订单");
               break;
           case NEED_PAY:
               getOrder("pending");
               empty.setText("您没有待付款的订单");
               break;
           case PROCESS:
               empty.setText("您没有处理中的订单");
               break;
           case AVAILABLE:
               empty.setText("您没有可使用的订单");
               break;
           case DRAWBACK:
               empty.setText("您没有退款单");
               break;
           default:
               break;
       }

        return view;
    }

    public void getOrder(String status) {
        long userId = AccountManager.getInstance().getLoginAccount(getActivity()).getUserId();
        TravelApi.getOrderList(userId, status, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<OrderBean> list = CommonJson4List.fromJson(result, OrderBean.class);
                adapter.getDataList().clear();
                adapter.getDataList().addAll(list.result);
                adapter.notifyDataSetChanged();
                empty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (mSwipeRefreshWidget.isRefreshing())mSwipeRefreshWidget.setRefreshing(false);
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                if (mSwipeRefreshWidget.isRefreshing())mSwipeRefreshWidget.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new OrderListAdapter(getActivity(), type);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OrderListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position , long id) {
                Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                intent.putExtra("type", "orderDetail");
                intent.putExtra("orderId", id);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        switch (type){
            case ALL:
                getOrder("");
                break;
            case NEED_PAY:
                getOrder("pending");
                break;
            default:
                if (mSwipeRefreshWidget.isRefreshing())mSwipeRefreshWidget.setRefreshing(false);
                break;
        }


    }

    static class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {
        public interface OnItemClickListener {
            void onItemClick(View view, int position,long id);
        }

        private OnItemClickListener listener;
        private List<OrderBean> mValues;
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
            public final TextView tvPackageName;
            public final TextView tvDate;
            public ViewHolder(View view) {
                super(view);

                rlNeedPay = (RelativeLayout) view.findViewById(R.id.rl_need_pay);
                tvNeedPayState = (TextView) view.findViewById(R.id.tv_need_pay_state);
                tvNeedPayTalk = (TextView) view.findViewById(R.id.tv_need_pay_talk);
                tvNeedPayPay = (TextView) view.findViewById(R.id.tv_pay);

                rlProcess = (RelativeLayout) view.findViewById(R.id.rl_process);
                tvProcessState = (TextView) view.findViewById(R.id.tv_process_state);
                tvProcessTalk = (TextView) view.findViewById(R.id.tv_process_talk);

                rlAvailable = (RelativeLayout) view.findViewById(R.id.rl_available);
                tvAvailableState = (TextView) view.findViewById(R.id.tv_available_state);
                tvAvailableTalk = (TextView) view.findViewById(R.id.tv_available_talk);
                tvAvailableMessage = (TextView) view.findViewById(R.id.tv_available_message);

                rlDrawBack = (RelativeLayout) view.findViewById(R.id.rl_drawback);
                tvDrawBackState = (TextView) view.findViewById(R.id.tv_drawback_state);
                tvDrawBackTalk = (TextView) view.findViewById(R.id.tv_drawback_talk);

                mImageView = (ImageView) view.findViewById(R.id.iv_goods_img);
                tvGoodsName = (TextView) view.findViewById(R.id.tv_goods_name);
                tvDate = (TextView) view.findViewById(R.id.tv_goods_time);
                tvPackageName = (TextView) view.findViewById(R.id.tv_goods_package);
            }
        }

        public OrderListAdapter(Context context, int type) {
            mContext = context;
            mValues = new ArrayList<>();
            this.type = type;
        }

        public Object getItem(int position) {
            return mValues.get(position);
        }

        public List<OrderBean> getDataList() {
            return mValues;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final OrderBean bean = (OrderBean) getItem(position);

            switch (bean.getStatus()) {
                case "paid":
                    holder.rlProcess.setVisibility(View.VISIBLE);
                    break;
                case "committed":
                    holder.rlAvailable.setVisibility(View.VISIBLE);
                    break;
                case "refundApplied":
                    holder.rlDrawBack.setVisibility(View.VISIBLE);
                    break;
                case "pending":
                    holder.rlNeedPay.setVisibility(View.VISIBLE);

                    SpannableString priceStr = new SpannableString("¥"+String.valueOf((double) Math.round(bean.getTotalPrice() * 10 / 10)));
                    priceStr.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.price_color)), 0, priceStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    priceStr.setSpan(new AbsoluteSizeSpan(13, true), 0, priceStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder spb = new SpannableStringBuilder();
                    spb.append("待付款:").append(priceStr);
                    holder.tvNeedPayState.setText(spb);
                    break;
                default:
                    holder.rlNeedPay.setVisibility(View.VISIBLE);
                    holder.rlNeedPay.setVisibility(View.VISIBLE);
                    SpannableString str = new SpannableString("¥35353");
                    str.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.price_color)), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    str.setSpan(new AbsoluteSizeSpan(13, true), 0, str.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder spb1 = new SpannableStringBuilder();
                    spb1.append("待付款:").append(str);
                    holder.tvNeedPayState.setText(spb1);
                    break;
            }

            holder.tvGoodsName.setText(bean.getCommodity().getTitle());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            holder.tvDate.setText(String.format("出行日期:%s",format.format(new Date(bean.getRendezvousTime()))));
            holder.tvPackageName.setText(bean.getCommodity().getPlans().get(0).getTitle()+" x"+bean.getQuantity());
            if (bean.getCommodity().getImages()!=null&&bean.getCommodity().getImages().size()>0){
                ImageLoader.getInstance().displayImage(bean.getCommodity().getImages().get(0).url, holder.mImageView, UILUtils.getDefaultOption());
            }else {
                ImageLoader.getInstance().displayImage("", holder.mImageView, UILUtils.getDefaultOption());
            }
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(holder.itemView, position,bean.getOrderId());
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

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

    }
}
