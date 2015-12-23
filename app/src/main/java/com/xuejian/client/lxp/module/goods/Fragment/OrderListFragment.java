package com.xuejian.client.lxp.module.goods.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckedTextView;
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
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.module.goods.OrderDetailActivity;
import com.xuejian.client.lxp.module.pay.PaymentActivity;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;

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
     //   DialogManager.getInstance().showModelessLoadingDialog(getActivity());
        View view = (View) inflater.inflate(
                R.layout.fragment_order_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        empty = (TextView) view.findViewById(R.id.empty_view);
        mSwipeRefreshWidget = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshWidget.setOnRefreshListener(this);
        mSwipeRefreshWidget.setColorSchemeResources(R.color.app_theme_color);
        setupRecyclerView(recyclerView);
        mSwipeRefreshWidget.setRefreshing(true);
        switch (type) {
            case ALL:
                empty.setText("您近期没有出行订单");
                getOrder("");
                break;
            case NEED_PAY:
                empty.setText("您没有待付款的订单");
                getOrder("pending");
                break;
            case PROCESS:
                empty.setText("您没有处理中的订单");
                getOrder("paid");
                break;
            case AVAILABLE:
                empty.setText("您没有可使用的订单");
                getOrder("committed");
                break;
            case DRAWBACK:
                empty.setText("您没有退款中订单");
                getOrder("refundApplied");
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
                if (list.result.size() > 0) empty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (mSwipeRefreshWidget.isRefreshing()) mSwipeRefreshWidget.setRefreshing(false);
                DialogManager.getInstance().dissMissModelessLoadingDialog();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                if (mSwipeRefreshWidget.isRefreshing()){
                    mSwipeRefreshWidget.setRefreshing(false);
                }
                recyclerView.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
             //   DialogManager.getInstance().dissMissModelessLoadingDialog();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {
             //   DialogManager.getInstance().dissMissModelessLoadingDialog();
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
            public void onItemClick(View view, int position, long id) {
                Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                intent.putExtra("type", "orderDetail");
                intent.putExtra("orderId", id);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        switch (type) {
            case ALL:
                getOrder("");
                break;
            case NEED_PAY:
                getOrder("pending");
                break;
            case PROCESS:
                getOrder("paid");
                break;
            case AVAILABLE:
                getOrder("committed");
                break;
            case DRAWBACK:
                getOrder("refundApplied");
                break;
            default:
                if (mSwipeRefreshWidget.isRefreshing()) mSwipeRefreshWidget.setRefreshing(false);
                break;
        }


    }

    static class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {
        public interface OnItemClickListener {
            void onItemClick(View view, int position, long id);
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
            public final RelativeLayout rlItem;

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
                rlItem = (RelativeLayout) view.findViewById(R.id.rl_item);
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

            holder.rlDrawBack.setVisibility(View.GONE);
            holder.rlAvailable.setVisibility(View.GONE);
            holder.rlProcess.setVisibility(View.GONE);
            holder.rlNeedPay.setVisibility(View.GONE);
            switch (bean.getStatus()) {
                case "paid":
                    holder.rlProcess.setVisibility(View.VISIBLE);
                    holder.tvProcessState.setText("待卖家确认");
                    break;
                case "committed":
                    holder.rlAvailable.setVisibility(View.VISIBLE);
                    holder.tvAvailableState.setText("可使用");
                    break;
                case "refundApplied":
                    holder.rlDrawBack.setVisibility(View.VISIBLE);
                    holder.tvDrawBackState.setText("已申请退款");
                    break;
                case "pending":
                    holder.rlNeedPay.setVisibility(View.VISIBLE);
                    SpannableString priceStr = new SpannableString("¥" + CommonUtils.getPriceString(bean.getTotalPrice()));
                    priceStr.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.price_color)), 0, priceStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    priceStr.setSpan(new AbsoluteSizeSpan(13, true), 0, priceStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder spb = new SpannableStringBuilder();
                    spb.append("待付款:").append(priceStr);
                    holder.tvNeedPayState.setText(spb);
                    holder.tvNeedPayPay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPayActionDialog((Activity)mContext,bean.getOrderId());
                        }
                    });
                    break;
                case "finished":
                    holder.rlProcess.setVisibility(View.VISIBLE);
                    holder.tvDrawBackState.setText("已完成");
                    break;
                case "canceled":
                    holder.rlProcess.setVisibility(View.VISIBLE);
                    holder.tvProcessState.setText("订单已取消");
                    break;
                case "expired":
                    holder.rlProcess.setVisibility(View.VISIBLE);
                    holder.tvDrawBackState.setText("已过期");
                    break;
                case "refunded":
                    break;
                default:
//                    holder.rlNeedPay.setVisibility(View.VISIBLE);
//                    holder.rlNeedPay.setVisibility(View.VISIBLE);
//                    SpannableString str = new SpannableString("¥35353");
//                    str.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.price_color)), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    str.setSpan(new AbsoluteSizeSpan(13, true), 0, str.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//                    SpannableStringBuilder spb1 = new SpannableStringBuilder();
//                    spb1.append("待付款:").append(str);
//                    holder.tvNeedPayState.setText(spb1);
                    break;
            }
            holder.tvGoodsName.setText(bean.getCommodity().getTitle());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            holder.tvDate.setText(String.format("出行日期:%s", format.format(new Date(bean.getRendezvousTime()))));
            if (bean.getCommodity().getPlans().size() > 0) {
                holder.tvPackageName.setText(bean.getCommodity().getPlans().get(0).getTitle() + " x" + bean.getQuantity());
            }
            if (bean.getCommodity().getCover() != null) {
                ImageLoader.getInstance().displayImage(bean.getCommodity().getCover().getUrl(), holder.mImageView, UILUtils.getDefaultOption());
            } else {
                ImageLoader.getInstance().displayImage("", holder.mImageView, UILUtils.getDefaultOption());
            }
            Intent talkIntent = new Intent(mContext, ChatActivity.class);
            talkIntent.putExtra("friend_id", bean.getCommodity().getSeller().getSellerId() + "");
            talkIntent.putExtra("chatType", "single");
            talkIntent.putExtra("shareCommodityBean", bean.getCommodity().creteShareBean());
            talkIntent.putExtra("fromTrade", true);
            final Intent intent = talkIntent;
            holder.rlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(holder.itemView, position, bean.getOrderId());
                    }
                }
            });
            holder.tvAvailableTalk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(intent);
                }
            });
            holder.tvNeedPayTalk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(intent);
                }
            });
            holder.tvDrawBackTalk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(intent);
                }
            });
            holder.tvProcessTalk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(intent);
                }
            });

        }

        private void showPayActionDialog(final Activity act, final long orderId) {
            final AlertDialog dialog = new AlertDialog.Builder(act).create();
            View contentView = View.inflate(act, R.layout.dialog_select_payment, null);
            CheckedTextView alipay = (CheckedTextView) contentView.findViewById(R.id.ctv_alipay);
            CheckedTextView weixinpay = (CheckedTextView) contentView.findViewById(R.id.ctv_weixin);
            alipay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Intent tv_pay = new Intent(act, PaymentActivity.class);
                    tv_pay.putExtra("orderId", orderId);
                    tv_pay.putExtra("type", "alipay");
                    act.startActivity(tv_pay);
                }
            });
            weixinpay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Intent tv_pay = new Intent(act, PaymentActivity.class);
                    tv_pay.putExtra("orderId", orderId);
                    tv_pay.putExtra("type", "weixinpay");
                    act.startActivity(tv_pay);
                }
            });
            contentView.findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            WindowManager windowManager = act.getWindowManager();
            Window window = dialog.getWindow();
            window.setContentView(contentView);
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = display.getWidth(); // 设置宽度
            window.setAttributes(lp);
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
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
