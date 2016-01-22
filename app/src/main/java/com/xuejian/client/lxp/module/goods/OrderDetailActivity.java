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
import android.view.LayoutInflater;
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
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.OrderBean;
import com.xuejian.client.lxp.bean.TradeActivityBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.thirdpart.weixin.WeixinApi;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.pay.PaymentActivity;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2015/11/13.
 */
public class OrderDetailActivity extends PeachBaseActivity implements View.OnClickListener {

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
    @Bind(R.id.tv_cancel_action)
    TextView tvCancel;
    @Bind(R.id.ll_trade_action0)
    LinearLayout llTradeAction0;
    @Bind(R.id.tv_action0)
    TextView tvAction0;
    @Bind(R.id.ll_trade_action1)
    LinearLayout llTradeAction1;
    @Bind(R.id.tv_talk)
    TextView tvTalk;
    @Bind(R.id.ll_message)
    LinearLayout llMessage;
    @Bind(R.id.lv_activity)
    ListView listView;
    long orderId;
    OrderBean currentOrder;
    CountDownTimer countDownTimer;
    private static final int REFRESH = 107;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
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
        tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.tv_pay:
//                if (tvPay.getText().toString().equals("立即支付")){
//                    showPayActionDialog();
//                }else if (tvPay.getText().toString().equals("再次预定")){
//
//                }
//
//                break;
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
                tvState.setText("等待卖家确认");
                llTradeAction0.setVisibility(View.VISIBLE);
                tvAction0.setText("申请退款");
                tvAction0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.setClass(OrderDetailActivity.this, DrawbackActivity.class);
                        intent.putExtra("orderId", bean.getOrderId());
                        startActivityForResult(intent, REFRESH);
                    }
                });
                break;
            case "committed":
                tvState.setText("可使用");
                if (checkState(bean.activities)){
                    llTradeAction0.setVisibility(View.GONE);
                }else {
                    llTradeAction0.setVisibility(View.VISIBLE);
                }
                tvAction0.setText("申请退款");
                tvAction0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.setClass(OrderDetailActivity.this, DrawbackActivity.class);
                        intent.putExtra("amount",bean.getTotalPrice());
                        intent.putExtra("orderId", bean.getOrderId());
                        startActivityForResult(intent,REFRESH);
                    }
                });
                break;
            case "refundApplied":
                tvState.setText("退款申请中");
                break;
            case "pending":
                tvState.setText(String.format("待付款¥%s",CommonUtils.getPriceString(bean.getTotalPrice())));
                long time = bean.getExpireTime() - System.currentTimeMillis();
                if (time > 0) {
                    llTradeAction1.setVisibility(View.VISIBLE);
                    countDownTimer = new CountDownTimer(time, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            tvFeedback.setText(String.format("请在%s内完成支付", CommonUtils.formatDuring(millisUntilFinished)));
                        }

                        @Override
                        public void onFinish() {
                            tvFeedback.setText("订单已超过支付期限,请重新下单");
                            tvPay.setText("再次预定");
                        }
                    }.start();
                } else {
                    tvFeedback.setText("订单已超过支付期限,请重新下单");
                    llTradeAction1.setVisibility(View.GONE);
                    llTradeAction0.setVisibility(View.VISIBLE);
                    tvAction0.setText("再次预定");
                    tvAction0.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            intent.setClass(OrderDetailActivity.this, CommodityDetailActivity.class);
                            intent.putExtra("commodityId", bean.getCommodity().getCommodityId());
                            startActivity(intent);
                        }
                    });
                  //  tvPay.setText("再次预定");
                }
                tvPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tvPay.getText().toString().equals("立即支付")) {
                            showPayActionDialog();
                        } else if (tvPay.getText().toString().equals("再次预定")) {
                            intent.setClass(OrderDetailActivity.this, CommodityDetailActivity.class);
                            intent.putExtra("commodityId", bean.getCommodity().getCommodityId());
                            startActivity(intent);
                        }
                    }
                });

                break;
            case "finished":
                tvState.setText("已完成");
                llTradeAction0.setVisibility(View.VISIBLE);
                tvAction0.setText("再次预定");
                tvAction0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.setClass(OrderDetailActivity.this, CommodityDetailActivity.class);
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
                        intent.setClass(OrderDetailActivity.this, CommodityDetailActivity.class);
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
                        intent.setClass(OrderDetailActivity.this, CommodityDetailActivity.class);
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
                        intent.setClass(OrderDetailActivity.this, CommodityDetailActivity.class);
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
                Intent intent = new Intent(OrderDetailActivity.this, CommodityDetailActivity.class);
                intent.putExtra("commodityId", bean.getCommodity().getCommodityId());
                intent.putExtra("snapshots",true);
                intent.putExtra("version",bean.getCommodity().version);
                startActivity(intent);
            }
        });
        tvOrderId.setText(String.valueOf(bean.getOrderId()));
        tvOrderPackage.setText(bean.getCommodity().getPlans().get(0).getTitle());
        tvOrderDate.setText(bean.getRendezvousTime());
        tvOrderNum.setText(String.valueOf(bean.getQuantity()));
        tvOrderPrice.setText("¥" + CommonUtils.getPriceString(bean.getTotalPrice()));

        tvOrderTravellerCount.setText(String.valueOf(bean.getTravellers().size()));

        tvOrderContactName.setText(bean.getContact().getSurname() + " " + bean.getContact().getGivenName());
        tvOrderContactTel.setText("+" + bean.getContact().getTel().getDialCode() + "-" + bean.getContact().getTel().getNumber());
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
        if (bean.getCommodity().getSeller()!=null){
            tvTalk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent talkIntent = new Intent(mContext, ChatActivity.class);
                    talkIntent.putExtra("friend_id", bean.getCommodity().getSeller().getSellerId() + "");
                    talkIntent.putExtra("chatType", "single");
               //     talkIntent.putExtra("shareCommodityBean", bean.creteShareBean());
              //      talkIntent.putExtra("fromTrade", true);
                    startActivity(talkIntent);
                }
            });
        }
        ActivityAdapter activityAdapter = new ActivityAdapter(mContext);
        listView.setAdapter(activityAdapter);
        activityAdapter.getData().addAll(bean.activities);
        CommonUtils.setListViewHeightBasedOnChildren(listView);
    }

    public boolean checkState(ArrayList<TradeActivityBean> list){
        for (TradeActivityBean activity : list) {
            if ("refundDeny".equals(activity.action)){
                return true;
            }
        }
        return false;
    }

    public class ActivityAdapter extends BaseAdapter{

        private ArrayList<TradeActivityBean> data;
        private Context mContext;
        private HashMap<String,String> stateMap;
        public ActivityAdapter(Context context ){
            this.mContext = context;
            this.data = new ArrayList<>();
            stateMap = new HashMap<>();
            stateMap.put("create","买家提交订单");
           // stateMap.put("cancel","订单已取消");
            stateMap.put("pay","买家付款");
            stateMap.put("refundApply","买家申请退款");
            stateMap.put("refundApprove","卖家同意退款");
            stateMap.put("refundDeny","卖家拒绝退款申请");
            stateMap.put("refundAuto","系统自动退款");
            stateMap.put("commit","卖家已发货");
            stateMap.put("finish","订单完成");

        }

        public ArrayList<TradeActivityBean> getData() {
            return data;
        }

        private String getState(TradeActivityBean bean){

            if (bean.action.equals("expire")){
                if (bean.prevStatus.equals("paid")){
                    return "卖家未确认订单,系统自动退款";
                }else if (bean.prevStatus.equals("pending")){
                    return "买家未支付,订单过期";
                } else return "";
            }else if (bean.action.equals("cancel")){
                if (AccountManager.getCurrentUserId().equals(bean.data.userId)){
                    return "买家取消订单";
                }else {
                    return "卖家取消订单";
                }
            } else {
                return stateMap.get(bean.action);
            }
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_activities,null);
                viewHolder = new ViewHolder();
                viewHolder.timestamp = (TextView) convertView.findViewById(R.id.tv_timestamp);
                viewHolder.activity = (TextView) convertView.findViewById(R.id.tv_activity);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            TradeActivityBean bean = (TradeActivityBean) getItem(position);
            viewHolder.timestamp.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(bean.timestamp)));
            viewHolder.activity.setText(getState(bean));
            return convertView;
        }
        class ViewHolder{
            private TextView timestamp;
            private TextView activity;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent tv_title_back = new Intent(OrderDetailActivity.this, OrderListActivity.class);
//        startActivity(tv_title_back);
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
        JSONObject object = new JSONObject();
        User user = AccountManager.getInstance().getLoginAccount(this);
        try {
            object.put("userId",user.getUserId());
            object.put("memo","");
            object.put("reason","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TravelApi.editOrderStatus(orderId, "cancel", object, new HttpCallBack<String>() {

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
                finish();
            }
        });
        weixinpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (!WeixinApi.getInstance().isWXinstalled(OrderDetailActivity.this)){
                    ToastUtil.getInstance(mContext).showToast("你还没有安装微信");
                    return;
                }
                Intent tv_pay = new Intent(OrderDetailActivity.this, PaymentActivity.class);
                if (currentOrder != null) {
                    tv_pay.putExtra("orderId", currentOrder.getOrderId());
                }
                tv_pay.putExtra("type", "weixinpay");
                startActivity(tv_pay);
                finish();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode ==RESULT_OK){
            if (requestCode == REFRESH){
                llTradeAction0.setVisibility(View.GONE);
                llTradeAction1.setVisibility(View.GONE);
                getData(orderId);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer!=null){
            countDownTimer.cancel();
            countDownTimer=null;
        }
    }
}
