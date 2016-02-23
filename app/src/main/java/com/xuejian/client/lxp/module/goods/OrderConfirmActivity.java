package com.xuejian.client.lxp.module.goods;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import android.widget.Toast;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.alibaba.fastjson.JSON;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.OrderBean;
import com.xuejian.client.lxp.bean.PlanBean;
import com.xuejian.client.lxp.bean.TravellerBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.thirdpart.weixin.WeixinApi;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.module.pay.PaymentActivity;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2015/12/23.
 */
public class OrderConfirmActivity extends PeachBaseActivity {
    @Bind(R.id.iv_nav_back)
    ImageView ivNavBack;
    @Bind(R.id.tv_title_bar_title)
    TextView tvTitleBarTitle;
    @Bind(R.id.tv_pay_state)
    TextView tvState;
    @Bind(R.id.tv_pay_feedback)
    TextView tvFeedback;
    @Bind(R.id.tv_goods_name)
    TextView tvGoodsName;
    @Bind(R.id.tv_order_id)
    TextView tvOrderId;
    @Bind(R.id.tv_order_package)
    TextView tvOrderPackage;
    @Bind(R.id.tv_order_date)
    TextView tvOrderDate;
    @Bind(R.id.tv_order_num)
    TextView tvOrderNum;
    @Bind(R.id.tv_order_price)
    TextView tvOrderPrice;
    @Bind(R.id.iv_goods)
    ImageView ivGoods;
    @Bind(R.id.tv_order_store_name)
    TextView tvOrderStoreName;
    @Bind(R.id.userinfo)
    TextView userinfo;
    @Bind(R.id.tv_order_traveller_count)
    TextView tvOrderTravellerCount;
    @Bind(R.id.user_info)
    RelativeLayout userInfo;
    @Bind(R.id.tv_order_contact_name)
    TextView tvOrderContactName;
    @Bind(R.id.tv_order_contact_tel)
    TextView tvOrderContactTel;
    @Bind(R.id.tv_order_message)
    TextView tvOrderMessage;
    @Bind(R.id.tv_pay)
    TextView tvPay;
    @Bind(R.id.ll_trade_action0)
    LinearLayout llTradeAction0;
    @Bind(R.id.tv_talk)
    TextView tvTalk;
    @Bind(R.id.ll_message)
    LinearLayout llMessage;
    @Bind(R.id.tv_coupon_price)
    TextView tv_coupon_price;
    long orderId;
    CountDownTimer countDownTimer;
    private ArrayList<TravellerBean> passengerList = new ArrayList<>();
    ArrayList<TravellerBean> list;
    Handler handler = new Handler();
    private HashMap<String, String> idType = new HashMap<>();
    private OrderBean currentBean;
    private boolean orderCreated;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        ButterKnife.bind(this);
        ivNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        OrderBean bean = getIntent().getParcelableExtra("order");
        list = getIntent().getParcelableArrayListExtra("passengerList");
        for (TravellerBean travellerBean : list) {
            passengerList.add(new TravellerBean(travellerBean.getTraveller()));
        }
        idType.put("chineseID", "身份证");
        idType.put("passport", "护照");
        idType.put("HMPermit", "港澳通行证");
        idType.put("TWPermit", "台湾通行证");
        bindView(bean);
    }

    private void bindView(final OrderBean bean) {
        CommonAdapter memberAdapter = new CommonAdapter(mContext, R.layout.item_order_users, false, null);
        ListView memberList = (ListView) findViewById(R.id.lv_members);
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
                tvState.setText("退款申请中");
                break;
            case "pending":
                tvState.setText(String.format("待付款 ¥%s", CommonUtils.getPriceString(bean.getTotalPrice()-bean.getDiscount())));
//                long time = bean.getExpireTime() - System.currentTimeMillis();
//                if (time > 0) {
//                    countDownTimer = new CountDownTimer(time, 1000) {
//                        @Override
//                        public void onTick(long millisUntilFinished) {
//                            tvFeedback.setText(String.format("请在%s内完成支付", CommonUtils.formatDuring(millisUntilFinished)));
//                        }
//
//                        @Override
//                        public void onFinish() {
//                            tvFeedback.setText("订单已超过支付期限,请重新下单");
//                            tvPay.setText("再次预定");
//                        }
//                    }.start();
//                } else {
//                    tvFeedback.setText("订单已超过支付期限,请重新下单");
//                    tvPay.setText("再次预定");
//                }
                tvPay.setText("确认订单");
                tvPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //       if (tvPay.getText().toString().equals("立即支付")){
                        if (orderCreated){
                            showPayActionDialog(currentBean);
                        }else {
                            DialogManager.getInstance().showLoadingDialog(mContext, "订单创建中");
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    createOrder(bean);
                                }
                            }, 1000);
                        }

//                        }else if (tvPay.getText().toString().equals("再次预定")){
//                            intent.setClass(OrderConfirmActivity.this, ReactMainPage.class);
//                            intent.putExtra("commodityId", bean.getCommodity().getCommodityId());
//                            startActivity(intent);
//                        }
                    }
                });
                break;
            case "finished":
                tvState.setText("交易已结束");
                llTradeAction0.setVisibility(View.VISIBLE);
                tvPay.setText("再次预定");
                tvPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.setClass(OrderConfirmActivity.this, CommodityDetailActivity.class);
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
                        intent.setClass(OrderConfirmActivity.this, CommodityDetailActivity.class);
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
                        intent.setClass(OrderConfirmActivity.this, CommodityDetailActivity.class);
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
                        intent.setClass(OrderConfirmActivity.this, CommodityDetailActivity.class);
                        intent.putExtra("commodityId", bean.getCommodity().getCommodityId());
                        startActivity(intent);
                    }
                });
                break;
            default:
                break;
        }

//        tvOrderStoreName.setText(bean.getCommodity().getSeller().getName());
        tvGoodsName.setText(bean.getCommodity().getTitle());
//        tvGoodsName.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
//        tvGoodsName.getPaint().setAntiAlias(true);//抗锯齿
//        tvGoodsName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(OrderConfirmActivity.this, ReactMainPage.class);
//                intent.putExtra("commodityId", bean.getCommodity().getCommodityId());
//                startActivity(intent);
//            }
//        });
        //     tvOrderId.setText(String.valueOf(bean.getOrderId()));
        tvOrderPackage.setText(bean.getCommodity().getPlans().get(0).getTitle());
        tvOrderDate.setText(bean.getRendezvousTime());
        tvOrderNum.setText(String.valueOf(bean.getQuantity()));
        tvOrderPrice.setText("¥" + CommonUtils.getPriceString(bean.getTotalPrice()));
        if (bean.couponBean!=null){
            tv_coupon_price.setText("¥" + CommonUtils.getPriceString(bean.couponBean.getDiscount()));
        }else {
            tv_coupon_price.setText("¥" + CommonUtils.getPriceString(0.0f));
        }

        // tvOrderTravellerCount.setText(String.valueOf(list.size()));

        tvOrderContactName.setText(bean.getContact().getSurname() + " " + bean.getContact().getGivenName());
        tvOrderContactTel.setText("+" + bean.getContact().getTel().getDialCode() + "-" + bean.getContact().getTel().getNumber());
        if (TextUtils.isEmpty(bean.getComment())) {
            llMessage.setVisibility(View.GONE);
        } else {
            tvOrderMessage.setText(bean.getComment());
        }
        if (bean.getCommodity().getSeller() != null) {
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

//        tvPay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPayActionDialog(bean);
//            }
//        });

    }

    public void createOrder(final OrderBean bean) {
        JSONObject object = null;
        try {
            object = new JSONObject(JSON.toJSON(bean.getContact()).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String couponId ="";
        if (bean.couponBean!=null){
            couponId = bean.couponBean.getId();
        }

        TravelApi.createOrder(bean.getCommodity().getCommodityId(), bean.getPlanId(), bean.getRendezvousTime(), bean.getQuantity(), object,
                bean.getComment(), list,couponId ,new HttpCallBack<String>() {
                    @Override
                    public void doSuccess(String result, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        CommonJson<OrderBean> bean = CommonJson.fromJson(result, OrderBean.class);
                        currentBean = bean.result;
                        setResult(RESULT_OK);
                        showPayActionDialog(bean.result);
                        orderCreated = true ;
//                        Intent intent = new Intent(OrderConfirmActivity.this, OrderConfirmActivity.class);
//                        intent.putExtra("type", "pendingOrder");
//                        intent.putExtra("order", bean.result);
//                        intent.putExtra("orderId", bean.result.getOrderId());
//                        startActivity(intent);
//                        finish();
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        Toast.makeText(mContext, "订单创建失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method, int code) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void showPayActionDialog(final OrderBean currentOrder) {
        final Activity act = this;
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        dialog.setCanceledOnTouchOutside(false);
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
             //   finish();
            }
        });
        weixinpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (!WeixinApi.getInstance().isWXinstalled(OrderConfirmActivity.this)) {
                    ToastUtil.getInstance(mContext).showToast("你还没有安装微信");
                    return;
                }
                Intent tv_pay = new Intent(OrderConfirmActivity.this, PaymentActivity.class);
                if (currentOrder != null) {
                    tv_pay.putExtra("orderId", currentOrder.getOrderId());
                }
                tv_pay.putExtra("type", "weixinpay");
                startActivity(tv_pay);
            //    finish();
            }
        });
        contentView.findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notice(currentOrder.getOrderId());
             //   dialog.dismiss();
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

    private void notice(final long orderId) {
        final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
        dialog.setTitle("提示");
        dialog.setMessage("确认取消支付？");
        dialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(OrderConfirmActivity.this, OrderDetailActivity.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("type","orderDetail");
                startActivity(intent);
                finish();
            }
        });
        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

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
            } else if (ResId == R.layout.item_order_users) {
                return passengerList.size();
            }
            return 0;
        }


        @Override
        public Object getItem(int position) {
            if (ResId == R.layout.item_package_info) {
                return packageList.get(position);
            } else if (ResId == R.layout.item_order_users) {
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
            } else if (ResId == R.layout.item_order_users) {
                TravellerBean bean = (TravellerBean) getItem(position);
                if (convertView == null) {
                    convertView = View.inflate(mContext, ResId, null);

                    holder = new ViewHolder();
                    holder.username = (TextView) convertView.findViewById(R.id.tv_name);
                    holder.id = (TextView) convertView.findViewById(R.id.tv_id);
                    holder.tel = (TextView) convertView.findViewById(R.id.tv_tel);
                    holder.title = (TextView) convertView.findViewById(R.id.tv_num);
                    convertView.setTag(holder);

                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.title.setText(String.format("旅客%d:", position + 1));
                holder.username.setText(bean.getTraveller().getSurname() + bean.getTraveller().getGivenName());
                holder.tel.setText(bean.getTraveller().getTel().getDialCode() + "-" + bean.getTraveller().getTel().getNumber());
                if (bean.getTraveller().getIdentities().size() > 0) {
                    holder.id.setText(idType.get(bean.getTraveller().getIdentities().get(0).getIdType()) + " " + bean.getTraveller().getIdentities().get(0).getNumber());
                }
            }
            return convertView;
        }

        class ViewHolder {
            private TextView username;
            private TextView tel;
            private TextView id;
            private TextView title;
        }

        class ViewHolder1 {
            private TextView packageName;
            private TextView packagePrice;
            private LinearLayout bg;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

}
