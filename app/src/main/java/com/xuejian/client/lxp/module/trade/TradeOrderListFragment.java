package com.xuejian.client.lxp.module.trade;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.OrderBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.thirdpart.weixin.WeixinApi;
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
public class TradeOrderListFragment extends PeachBaseFragment {
    // SwipeRefreshLayout mSwipeRefreshWidget;
    private int type;
    public static final int ALL = 1;
    public static final int TO_COMMIT = 2;
    public static final int TO_DRAWBACK = 3;
   // public static final int AVAILABLE = 4;
   // public static final int TO_REVIEW = 5;
    OrderListAdapter adapter;
    XRecyclerView recyclerView;
    TextView empty;
    Handler handler;
    private static int COUNT = 5;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
        if (handler == null)handler = new Handler();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //   DialogManager.getInstance().showModelessLoadingDialog(getActivity());
        View view = (View) inflater.inflate(
                R.layout.fragment_order_list, container, false);
        recyclerView = (XRecyclerView) view.findViewById(R.id.recyclerview);
        empty = (TextView) view.findViewById(R.id.empty_view);
//        mSwipeRefreshWidget = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
//        mSwipeRefreshWidget.setOnRefreshListener(this);
//        mSwipeRefreshWidget.setColorSchemeResources(R.color.app_theme_color);
        setupRecyclerView(recyclerView);
        recyclerView.setRefreshProgressStyle(ProgressStyle.SysProgress);
        recyclerView.setLoadingMoreEnabled(true);
        //   DialogManager.getInstance().showModelessLoadingDialog(getActivity());

        empty.setText("您没有相关订单");

        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                recyclerView.setLoadingMoreEnabled(true);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkState(0, true);
                    }
                },1000);
            }

            @Override
            public void onLoadMore() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkState(adapter.getItemCount(),false);
                    }
                }, 1000);
            }
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        //  DialogManager.getInstance().showModelessLoadingDialog(getActivity());
        //   mSwipeRefreshWidget.setRefreshing(true);
        recyclerView.setLoadingMoreEnabled(true);
        checkState(0,true);
    }

    public void checkState(int start ,boolean refresh){
        switch (type) {
            case ALL:
                getOrder("",start,COUNT,refresh);
                break;
            case TO_COMMIT:
                getOrder("paid",start,COUNT,refresh);
                break;
            case TO_DRAWBACK:
                getOrder("refundApplied",start,COUNT,refresh);
                break;
            default:
                //     if (mSwipeRefreshWidget.isRefreshing()) mSwipeRefreshWidget.setRefreshing(false);
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                break;
        }
    }

    public void getOrder(String status,int start,int count, final boolean refresh) {
        long userId = AccountManager.getInstance().getLoginAccount(getActivity()).getUserId();
        TravelApi.getOrderList(userId, status,String.valueOf(start),String.valueOf(count),true,new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<OrderBean> list = CommonJson4List.fromJson(result, OrderBean.class);
                if (refresh)adapter.getDataList().clear();
                adapter.getDataList().addAll(list.result);
                adapter.notifyDataSetChanged();
                if (list.result.size() > 0) empty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                //        if (mSwipeRefreshWidget.isRefreshing()) mSwipeRefreshWidget.setRefreshing(false);
                recyclerView.refreshComplete();
                recyclerView.loadMoreComplete();

                if (list.result.size() >= COUNT) {

                } else {
//                    goodsList.noMoreLoading();
                    recyclerView.setLoadingMoreEnabled(false);
                }
                DialogManager.getInstance().dissMissModelessLoadingDialog();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
//                if (mSwipeRefreshWidget.isRefreshing()){
//                    mSwipeRefreshWidget.setRefreshing(false);
//                }
                recyclerView.refreshComplete();
                recyclerView.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
                DialogManager.getInstance().dissMissModelessLoadingDialog();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
            }
        });
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);
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


    static class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {
        public interface OnItemClickListener {
            void onItemClick(View view, int position, long id);
        }

        private OnItemClickListener listener;
        private List<OrderBean> mValues;
        private Context mContext;
        private int type;

        public class ViewHolder extends RecyclerView.ViewHolder {

            public final ImageView mImageView;
            public final TextView tvGoodsName;
            public final TextView tvGoodsId;
            public final TextView tvGoodsPrice;
            public final RelativeLayout rlItem;

            public final TextView tvOrderId;
            public final TextView tvOrderTime;
            public final TextView tvCustomer;


            public final TextView tvState;
            public final TextView tvAction1;
            public final TextView tvAction2;
            public final TextView tvTalk;

            public ViewHolder(View view) {
                super(view);

                mImageView = (ImageView) view.findViewById(R.id.iv_goods_img);
                tvGoodsName = (TextView) view.findViewById(R.id.tv_goods_name);
                tvGoodsId = (TextView) view.findViewById(R.id.tv_goods_id);
                tvGoodsPrice = (TextView) view.findViewById(R.id.tv_order_price);
                rlItem = (RelativeLayout) view.findViewById(R.id.rl_item);

                tvOrderId = (TextView) view.findViewById(R.id.tv_order_id);
                tvOrderTime = (TextView) view.findViewById(R.id.tv_order_time);
                tvCustomer = (TextView) view.findViewById(R.id.tv_order_customer);
                tvState = (TextView) view.findViewById(R.id.tv_state);
                tvAction1 = (TextView) view.findViewById(R.id.tv_action1);
                tvAction2 = (TextView) view.findViewById(R.id.tv_action2);
                tvTalk = (TextView) view.findViewById(R.id.tv_talk);
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
                    .inflate(R.layout.item_trade_order_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final OrderBean bean = (OrderBean) getItem(position);
            holder.tvAction2.setVisibility(View.VISIBLE);
            holder.tvAction1.setVisibility(View.VISIBLE);
            switch (bean.getStatus()) {
                case "paid":
                    holder.tvState.setText("待发货");
                    holder.tvAction2.setText("缺货退款");
                    holder.tvAction1.setText("发货");
                    break;
                case "committed":
                    holder.tvState.setText("可使用");
                    holder.tvAction2.setVisibility(View.GONE);
                    holder.tvAction1.setVisibility(View.GONE);
                    break;
                case "refundApplied":
                    holder.tvState.setText("待退款");
                    holder.tvAction2.setText("拒绝退款");
                    holder.tvAction1.setText("同意退款");
                    break;
                case "pending":
                    holder.tvState.setText("待付款");
                    holder.tvAction1.setText("关闭交易");
                    holder.tvAction2.setVisibility(View.GONE);
                    break;
                case "finished":
                    holder.tvState.setText("已完成");
                    holder.tvAction2.setVisibility(View.GONE);
                    holder.tvAction1.setVisibility(View.GONE);
                    break;
                case "canceled":
                    holder.tvState.setText("订单已取消");
                    holder.tvAction2.setVisibility(View.GONE);
                    holder.tvAction1.setVisibility(View.GONE);
                    break;
                case "expired":
                    holder.tvState.setText("已过期");
                    holder.tvAction2.setVisibility(View.GONE);
                    holder.tvAction1.setVisibility(View.GONE);
                    break;
                case "refunded":
                    holder.tvState.setText("已退款");
                    holder.tvAction2.setVisibility(View.GONE);
                    holder.tvAction1.setVisibility(View.GONE);
                    break;
                case "toReview":
                    holder.tvState.setText("待评价");
                    holder.tvAction2.setVisibility(View.GONE);
                    holder.tvAction1.setVisibility(View.GONE);
                    break;
                case "reviewed":
                    holder.tvState.setText("已评价");
                    holder.tvAction2.setVisibility(View.GONE);
                    holder.tvAction1.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
            holder.tvGoodsName.setText(bean.getCommodity().getTitle());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dateString  = format.format(new Date(bean.getCreateTime()));
            holder.tvOrderTime.setText(String.format("下单时间:%s", dateString));
            holder.tvGoodsId.setText(String.format("商品编号:%d", bean.getCommodity().getCommodityId()));
            holder.tvGoodsPrice.setText(String.format("订单总价:%s 共%d件", CommonUtils.getPriceString(bean.getTotalPrice()),bean.getQuantity()));
            holder.tvOrderId.setText(String.format("订单号:%d", bean.getOrderId()));
//            holder.tvCustomer.setText(String.format("联系人:%s %d", bean.getContact().getGivenName() + bean.getContact().getSurname(), bean.getContact().getTel().getNumber()));
            if (bean.getCommodity().getCover() != null) {
                ImageLoader.getInstance().displayImage(bean.getCommodity().getCover().getUrl(), holder.mImageView, UILUtils.getDefaultOption());
            } else {
                ImageLoader.getInstance().displayImage("", holder.mImageView, UILUtils.getDefaultOption());
            }
            Intent talkIntent = new Intent(mContext, ChatActivity.class);
            talkIntent.putExtra("friend_id", bean.getCommodity().getSeller().getSellerId() + "");
            talkIntent.putExtra("chatType", "single");
            //   talkIntent.putExtra("shareCommodityBean", bean.creteShareBean());
            //   talkIntent.putExtra("fromTrade", true);
            final Intent intent = talkIntent;
            holder.rlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(holder.itemView, position, bean.getOrderId());
                    }
                }
            });
            holder.tvTalk.setOnClickListener(new View.OnClickListener() {
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
                    if (!WeixinApi.getInstance().isWXinstalled(act)){
                        ToastUtil.getInstance(mContext).showToast("你还没有安装微信");
                        return;
                    }
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
