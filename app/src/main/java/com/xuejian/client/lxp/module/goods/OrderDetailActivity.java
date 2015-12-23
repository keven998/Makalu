package com.xuejian.client.lxp.module.goods;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.http.HttpCallBack;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.OrderBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.module.RNView.ReactMainPage;
import com.xuejian.client.lxp.module.pay.PaymentActivity;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yibiao.qin on 2015/11/13.
 */
public class OrderDetailActivity extends PeachBaseActivity implements View.OnClickListener {

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
    @InjectView(R.id.tv_cancel_action)
    TextView tvCancel;
    @InjectView(R.id.ll_trade_action0)
    LinearLayout llTradeAction0;
    @InjectView(R.id.tv_action0)
    TextView tvAction0;
    @InjectView(R.id.ll_trade_action1)
    LinearLayout llTradeAction1;
    @InjectView(R.id.tv_talk)
    TextView tvTalk;
    @InjectView(R.id.ll_message)
    LinearLayout llMessage;
    long orderId;
    OrderBean currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.inject(this);
        orderId = getIntent().getLongExtra("orderId", -1);
        String type = getIntent().getStringExtra("type");
        switch (type) {
            case "pendingOrder":
                OrderBean bean = getIntent().getParcelableExtra("order");
                bindView(bean);
                break;
            case "orderDetail":
                getData(orderId);
                break;
            default:
                break;
        }

        ivNavBack.setOnClickListener(this);
        tvPay.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pay:
                showPayActionDialog();
                break;
            case R.id.iv_nav_back:
//                Intent tv_title_back = new Intent(OrderDetailActivity.this, OrderListActivity.class);
//                startActivity(tv_title_back);
                finish();
                break;
            case R.id.tv_cancel_action:
                cancelOrderDialog();
                break;
            default:
                break;
        }
    }

    public void getData(long orderId) {
        if (orderId <= 0) return;
        TravelApi.getOrderDetail(orderId, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson<OrderBean> bean = CommonJson.fromJson(result, OrderBean.class);
                bindView(bean.result);
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void bindView(final OrderBean bean) {
        currentOrder = bean;
        final Intent intent = new Intent();
        switch (bean.getStatus()) {
            case "paid":
                tvState.setText("已支付");
                llTradeAction0.setVisibility(View.VISIBLE);
                tvAction0.setText("申请退款");
                tvAction0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.setClass(OrderDetailActivity.this, DrawbackActivity.class);
                        intent.putExtra("orderId", bean.getOrderId());
                        startActivity(intent);
                    }
                });
                break;
            case "committed":
                tvState.setText("可使用");
                llTradeAction0.setVisibility(View.VISIBLE);
                tvAction0.setText("申请退款");
                tvAction0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.setClass(OrderDetailActivity.this, DrawbackActivity.class);
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
                            tvFeedback.setText("订单已过期");
                        }
                    }.start();
                } else {
                    tvFeedback.setText("订单已过期");
                }
                llTradeAction1.setVisibility(View.VISIBLE);
                break;
            case "finished":
                tvState.setText("交易已结束");
                llTradeAction0.setVisibility(View.VISIBLE);
                tvAction0.setText("再次预定");
                tvAction0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.setClass(OrderDetailActivity.this, ReactMainPage.class);
                        intent.putExtra("commodityId", bean.getCommodity().getCommodityId());
                        startActivity(intent);
                    }
                });
                break;
            case "canceled":
                tvState.setText("已取消");
                llTradeAction0.setVisibility(View.VISIBLE);
                tvAction0.setText("再次预定");
                tvAction0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.setClass(OrderDetailActivity.this, ReactMainPage.class);
                        intent.putExtra("commodityId", bean.getCommodity().getCommodityId());
                        startActivity(intent);
                    }
                });
                break;
            case "expired":
                tvState.setText("已过期");
                llTradeAction0.setVisibility(View.VISIBLE);
                tvAction0.setText("再次预定");
                tvAction0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.setClass(OrderDetailActivity.this, ReactMainPage.class);
                        intent.putExtra("commodityId", bean.getCommodity().getCommodityId());
                        startActivity(intent);
                    }
                });
                break;
            case "refunded":
                tvState.setText("已退款");
                llTradeAction0.setVisibility(View.VISIBLE);
                tvAction0.setText("再次预定");
                tvAction0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.setClass(OrderDetailActivity.this, ReactMainPage.class);
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
                Intent intent = new Intent(OrderDetailActivity.this, ReactMainPage.class);
                intent.putExtra("commodityId", bean.getCommodity().getCommodityId());
                startActivity(intent);
            }
        });
        tvOrderId.setText(String.valueOf(bean.getOrderId()));
        tvOrderPackage.setText(bean.getCommodity().getPlans().get(0).getTitle());
        tvOrderDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(bean.getRendezvousTime())));
        tvOrderNum.setText(String.valueOf(bean.getQuantity()));
        tvOrderPrice.setText("¥" + String.valueOf((double) Math.round(bean.getTotalPrice() * 10 / 10)));

        tvOrderTravellerCount.setText(String.valueOf(bean.getTravellers().size()));

        tvOrderContactName.setText(bean.getContact().getGivenName() + " " + bean.getContact().getSurname());
        tvOrderContactTel.setText(bean.getContact().getTel().getDialCode() + "-" + bean.getContact().getTel().getNumber());
        if (TextUtils.isEmpty(bean.getComment())){
            llMessage.setVisibility(View.GONE);
        }else {
            tvOrderMessage.setText(bean.getComment());
        }


        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this, CommonUserInfoActivity.class);
                intent.putExtra("ListType", 3);
                intent.putParcelableArrayListExtra("passengerList", bean.getTravellers());
                startActivity(intent);
            }
        });
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

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent tv_title_back = new Intent(OrderDetailActivity.this, OrderListActivity.class);
        startActivity(tv_title_back);
        finish();
    }

    private void cancelOrderDialog() {
        final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
        dialog.setTitle("提示");
        dialog.setMessage("确定取消订单吗？");
        dialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                cancelOrder();

            }
        });
        dialog.setNegativeButton("不取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public void cancelOrder() {
        TravelApi.editOrderStatus(orderId, "cancel", "", new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                Toast.makeText(OrderDetailActivity.this, "订单已取消", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(OrderDetailActivity.this, OrderListActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                Toast.makeText(OrderDetailActivity.this, "取消失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void showPayActionDialog() {
        final Activity act = this;
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        View contentView = View.inflate(act, R.layout.dialog_select_payment, null);
        CheckedTextView alipay = (CheckedTextView) contentView.findViewById(R.id.ctv_alipay);
        CheckedTextView weixinpay = (CheckedTextView) contentView.findViewById(R.id.ctv_weixin);
        alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent tv_pay = new Intent(OrderDetailActivity.this, PaymentActivity.class);
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
                Intent tv_pay = new Intent(OrderDetailActivity.this, PaymentActivity.class);
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

}
