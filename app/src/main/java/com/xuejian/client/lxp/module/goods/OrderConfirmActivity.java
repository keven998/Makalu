package com.xuejian.client.lxp.module.goods;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.OrderBean;
import com.xuejian.client.lxp.bean.PlanBean;
import com.xuejian.client.lxp.bean.TravellerBean;
import com.xuejian.client.lxp.bean.TravellerEntity;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.module.RNView.ReactMainPage;
import com.xuejian.client.lxp.module.pay.PaymentActivity;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yibiao.qin on 2015/12/23.
 */
public class OrderConfirmActivity extends PeachBaseActivity{
    @InjectView(R.id.iv_nav_back)
    ImageView ivNavBack;
    @InjectView(R.id.tv_title_bar_title)
    TextView tvTitleBarTitle;
    @InjectView(R.id.tv_pay_state)
    TextView tvState;
    @InjectView(R.id.tv_pay_feedback)
    TextView tvFeedback;
    @InjectView(R.id.tv_goods_name)
    TextView tvGoodsName;
    @InjectView(R.id.tv_order_id)
    TextView tvOrderId;
    @InjectView(R.id.tv_order_package)
    TextView tvOrderPackage;
    @InjectView(R.id.tv_order_date)
    TextView tvOrderDate;
    @InjectView(R.id.tv_order_num)
    TextView tvOrderNum;
    @InjectView(R.id.tv_order_price)
    TextView tvOrderPrice;
    @InjectView(R.id.iv_goods)
    ImageView ivGoods;
    @InjectView(R.id.tv_order_store_name)
    TextView tvOrderStoreName;
    @InjectView(R.id.userinfo)
    TextView userinfo;
    @InjectView(R.id.tv_order_traveller_count)
    TextView tvOrderTravellerCount;
    @InjectView(R.id.user_info)
    RelativeLayout userInfo;
    @InjectView(R.id.tv_order_contact_name)
    TextView tvOrderContactName;
    @InjectView(R.id.tv_order_contact_tel)
    TextView tvOrderContactTel;
    @InjectView(R.id.tv_order_message)
    TextView tvOrderMessage;
    @InjectView(R.id.tv_pay)
    TextView tvPay;
    @InjectView(R.id.ll_trade_action0)
    LinearLayout llTradeAction0;
    @InjectView(R.id.tv_talk)
    TextView tvTalk;
    @InjectView(R.id.ll_message)
    LinearLayout llMessage;
    long orderId;
    private ArrayList<TravellerBean> passengerList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        ButterKnife.inject(this);
        OrderBean bean = getIntent().getParcelableExtra("order");
        bindView(bean);
    }

    private void bindView(final OrderBean bean) {
        CommonAdapter memberAdapter = new CommonAdapter(mContext, R.layout.item_member_info, false, null);
        ListView memberList = (ListView) findViewById(R.id.lv_members);
        for (TravellerEntity entity : bean.getTravellers()) {
            passengerList.add(new TravellerBean(entity));
        }
        memberList.setAdapter(memberAdapter);
        CommonUtils.setListViewHeightBasedOnChildren(memberList);

        final Intent intent = new Intent();
        switch (bean.getStatus()) {
            case "paid":
                tvState.setText("已支付");
                llTradeAction0.setVisibility(View.VISIBLE);
                tvPay.setText("申请退款");
                tvPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.setClass(OrderConfirmActivity.this, DrawbackActivity.class);
                        intent.putExtra("orderId", bean.getOrderId());
                        startActivity(intent);
                    }
                });
                break;
            case "committed":
                tvState.setText("可使用");
                llTradeAction0.setVisibility(View.VISIBLE);
                tvPay.setText("申请退款");
                tvPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.setClass(OrderConfirmActivity.this, DrawbackActivity.class);
                        intent.putExtra("orderId", bean.getOrderId());
                        startActivity(intent);
                    }
                });
                break;
            case "refundApplied":
                tvState.setText("已申请退款");
                break;
            case "pending":
                tvState.setText(String.format("待付款¥%s", String.valueOf((double) Math.round(bean.getTotalPrice() * 10 / 10))));
                long time = bean.getExpireTime() - System.currentTimeMillis();
                if (time > 0) {
                    CountDownTimer countDownTimer = new CountDownTimer(time, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            tvFeedback.setText(String.format("请在%s内完成支付", CommonUtils.formatDuring(millisUntilFinished)));
                        }

                        @Override
                        public void onFinish() {
                            tvFeedback.setText("订单已超过支付期限,请重新下单");
                        }
                    }.start();
                } else {
                    tvFeedback.setText("订单已超过支付期限,请重新下单");
                }
                break;
            case "finished":
                tvState.setText("交易已结束");
                llTradeAction0.setVisibility(View.VISIBLE);
                tvPay.setText("再次预定");
                tvPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.setClass(OrderConfirmActivity.this, ReactMainPage.class);
                        intent.putExtra("commodityId", bean.getCommodity().getCommodityId());
                        startActivity(intent);
                    }
                });
                break;
            case "canceled":
                tvState.setText("已取消");
                llTradeAction0.setVisibility(View.VISIBLE);
                tvPay.setText("再次预定");
                tvPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.setClass(OrderConfirmActivity.this, ReactMainPage.class);
                        intent.putExtra("commodityId", bean.getCommodity().getCommodityId());
                        startActivity(intent);
                    }
                });
                break;
            case "expired":
                tvState.setText("已过期");
                llTradeAction0.setVisibility(View.VISIBLE);
                tvPay.setText("再次预定");
                tvPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.setClass(OrderConfirmActivity.this, ReactMainPage.class);
                        intent.putExtra("commodityId", bean.getCommodity().getCommodityId());
                        startActivity(intent);
                    }
                });
                break;
            case "refunded":
                tvState.setText("已退款");
                llTradeAction0.setVisibility(View.VISIBLE);
                tvPay.setText("再次预定");
                tvPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.setClass(OrderConfirmActivity.this, ReactMainPage.class);
                        intent.putExtra("commodityId", bean.getCommodity().getCommodityId());
                        startActivity(intent);
                    }
                });
                break;
            default:
                break;
        }

        tvOrderStoreName.setText(bean.getCommodity().getSeller().getName());
        tvGoodsName.setText(bean.getCommodity().getTitle());
        tvGoodsName.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvGoodsName.getPaint().setAntiAlias(true);//抗锯齿
        tvGoodsName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderConfirmActivity.this, ReactMainPage.class);
                intent.putExtra("commodityId", bean.getCommodity().getCommodityId());
                startActivity(intent);
            }
        });
        tvOrderId.setText(String.valueOf(bean.getOrderId()));
        tvOrderPackage.setText(bean.getCommodity().getPlans().get(0).getTitle());
        tvOrderDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(bean.getRendezvousTime())));
        tvOrderNum.setText(String.valueOf(bean.getQuantity()));
        tvOrderPrice.setText("¥" + CommonUtils.getPriceString(bean.getTotalPrice()));

        tvOrderTravellerCount.setText(String.valueOf(bean.getTravellers().size()));

        tvOrderContactName.setText(bean.getContact().getGivenName() + " " + bean.getContact().getSurname());
        tvOrderContactTel.setText(bean.getContact().getTel().getDialCode() + "-" + bean.getContact().getTel().getNumber());
        if (TextUtils.isEmpty(bean.getComment())){
            llMessage.setVisibility(View.GONE);
        }else {
            tvOrderMessage.setText(bean.getComment());
        }

        tvTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent talkIntent = new Intent(mContext, ChatActivity.class);
                talkIntent.putExtra("friend_id", bean.getCommodity().getSeller().getSellerId() + "");
                talkIntent.putExtra("chatType", "single");
                talkIntent.putExtra("shareCommodityBean", bean.getCommodity().creteShareBean());
                talkIntent.putExtra("fromTrade", true);
                startActivity(talkIntent);
            }
        });
        tvPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPayActionDialog(bean);
            }
        });

    }

    private void showPayActionDialog(final OrderBean currentOrder) {
        final Activity act = this;
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        View contentView = View.inflate(act, R.layout.dialog_select_payment, null);
        CheckedTextView alipay = (CheckedTextView) contentView.findViewById(R.id.ctv_alipay);
        CheckedTextView weixinpay = (CheckedTextView) contentView.findViewById(R.id.ctv_weixin);
        alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent tv_pay = new Intent(OrderConfirmActivity.this, PaymentActivity.class);
                if (currentOrder != null) {
                    tv_pay.putExtra("orderId", currentOrder.getOrderId());
                }
                tv_pay.putExtra("type", "alipay");
                startActivity(tv_pay);
            }
        });
        weixinpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent tv_pay = new Intent(OrderConfirmActivity.this, PaymentActivity.class);
                if (currentOrder != null) {
                    tv_pay.putExtra("orderId", currentOrder.getOrderId());
                }
                tv_pay.putExtra("type", "weixinpay");
                startActivity(tv_pay);
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
    public class CommonAdapter extends BaseAdapter {

        private Context mContext;
        private int ResId;
        private int lastId;
        private ArrayList<PlanBean> packageList;

        public CommonAdapter(Context c, int ResId, boolean selected, ArrayList<PlanBean> list) {
            packageList = list;
            mContext = c;
            this.ResId = ResId;

        }

        @Override
        public int getCount() {
            if (ResId == R.layout.item_package_info) {
                return packageList.size();
            } else if (ResId == R.layout.item_member_info) {
                return passengerList.size();
            }
            return 0;
        }


        @Override
        public Object getItem(int position) {
            if (ResId == R.layout.item_package_info) {
                return packageList.get(position);
            } else if (ResId == R.layout.item_member_info) {
                return passengerList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            ViewHolder1 viewHolder1;
            if (ResId == R.layout.item_package_info) {
                if (convertView == null) {
                    convertView = View.inflate(mContext, ResId, null);
                    viewHolder1 = new ViewHolder1();
                    viewHolder1.packageName = (TextView) convertView.findViewById(R.id.tv_package);
                    viewHolder1.packagePrice = (TextView) convertView.findViewById(R.id.tv_package_price);
                    viewHolder1.bg = (LinearLayout) convertView.findViewById(R.id.ll_bg);
                    convertView.setTag(viewHolder1);
                } else {
                    viewHolder1 = (ViewHolder1) convertView.getTag();
                }
                PlanBean bean = (PlanBean) getItem(position);
                viewHolder1.packageName.setText(bean.getTitle());
                viewHolder1.packagePrice.setText(String.format("¥%s起", CommonUtils.getPriceString(bean.getPrice())));
                if (position == lastId) {
                    viewHolder1.bg.setBackgroundResource(R.drawable.icon_package_bg_selected);
                    //  viewHolder1.content.setPadding(10,0,0,0);
                } else {
                    viewHolder1.bg.setBackgroundResource(R.drawable.icon_package_bg_default);
                    //  viewHolder1.content.setPadding(10,0,0,0);
                }
            } else if (ResId == R.layout.item_member_info) {
                TravellerBean bean = (TravellerBean) getItem(position);
                if (convertView == null) {
                    convertView = View.inflate(mContext, ResId, null);

                    holder = new ViewHolder();
                    holder.content = (TextView) convertView.findViewById(R.id.tv_member);
                    convertView.setTag(holder);

                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.content.setText(bean.getTraveller().getSurname() + " " + bean.getTraveller().getGivenName());
            }
            return convertView;
        }

        class ViewHolder {
            private TextView content;
        }

        class ViewHolder1 {
            private TextView packageName;
            private TextView packagePrice;
            private LinearLayout bg;
        }
    }


}
