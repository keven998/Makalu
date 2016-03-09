package com.xuejian.client.lxp.module.goods;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.OrderBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.thirdpart.weixin.WeixinApi;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.module.pay.PaymentActivity;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2016/2/1.
 */
public class RefundActivity extends PeachBaseActivity {

    @Bind(R.id.tv_title_back)
    TextView tvTitleBack;
    @Bind(R.id.tv_list_title)
    TextView tvListTitle;
    @Bind(R.id.rl_normal_bar)
    RelativeLayout rlNormalBar;
    @Bind(R.id.lv_poi_list)
    XRecyclerView lvOrderList;
    OrderListAdapter adapter;
    private static int COUNT = 5;
    Handler handler;
    @Bind(R.id.empty_view)
    LinearLayout emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);
        ButterKnife.bind(this);
        tvListTitle.setText("退款");
        tvTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        emptyView.setVisibility(View.VISIBLE);
        handler = new Handler();
        setupRecyclerView(lvOrderList);
        lvOrderList.setRefreshProgressStyle(ProgressStyle.SysProgress);
        lvOrderList.setLoadingMoreEnabled(false);
        lvOrderList.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                lvOrderList.setLoadingMoreEnabled(true);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getOrder("refundApplied,refunded", 0, COUNT, true);
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getOrder("refundApplied,refunded", adapter.getItemCount(), COUNT, false);
                    }
                }, 1000);
            }
        });
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);
        adapter = new OrderListAdapter(this, 5);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OrderListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                Intent intent = new Intent(RefundActivity.this, OrderDetailActivity.class);
                intent.putExtra("type", "orderDetail");
                intent.putExtra("orderId", id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        lvOrderList.setLoadingMoreEnabled(true);
        getOrder("refundApplied,refunded", 0, COUNT, true);
    }

    public void getOrder(String status, int start, int count, final boolean refresh) {
        long userId = AccountManager.getInstance().getLoginAccount(this).getUserId();
        TravelApi.getOrderList(userId, status, String.valueOf(start), String.valueOf(count),false, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<OrderBean> list = CommonJson4List.fromJson(result, OrderBean.class);
                if (refresh) adapter.getDataList().clear();
                adapter.getDataList().addAll(list.result);
                adapter.notifyDataSetChanged();
                if (list.result.size() > 0) emptyView.setVisibility(View.GONE);
                lvOrderList.setVisibility(View.VISIBLE);
                //        if (mSwipeRefreshWidget.isRefreshing()) mSwipeRefreshWidget.setRefreshing(false);
                lvOrderList.refreshComplete();
                lvOrderList.loadMoreComplete();

                if (list.result.size() >= COUNT) {

                } else {
//                    goodsList.noMoreLoading();
                    lvOrderList.setLoadingMoreEnabled(false);
                }
                DialogManager.getInstance().dissMissModelessLoadingDialog();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
//                if (mSwipeRefreshWidget.isRefreshing()){
//                    mSwipeRefreshWidget.setRefreshing(false);
//                }
                lvOrderList.refreshComplete();
                lvOrderList.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                DialogManager.getInstance().dissMissModelessLoadingDialog();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
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
                    holder.tvDrawBackState.setText("退款申请中");
                    break;
                case "pending":
                    long time = bean.getExpireTime() - System.currentTimeMillis();
                    if (time > 0) {
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
                                showPayActionDialog((Activity) mContext, bean.getOrderId());
                            }
                        });
                    } else {
                        holder.rlProcess.setVisibility(View.VISIBLE);
                        holder.tvDrawBackState.setText("订单已超过支付期限,请重新下单");
                    }

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
                    holder.rlDrawBack.setVisibility(View.VISIBLE);
                    holder.tvDrawBackState.setText("已退款");
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
          //  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            holder.tvDate.setText(String.format("出行日期:%s", bean.getRendezvousTime()));
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
                    if (!WeixinApi.getInstance().isWXinstalled(act)) {
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
