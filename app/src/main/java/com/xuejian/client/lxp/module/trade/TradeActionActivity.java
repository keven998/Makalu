package com.xuejian.client.lxp.module.trade;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.http.HttpCallBack;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.OrderBean;
import com.xuejian.client.lxp.bean.TradeActivityBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2016/3/10.
 */
public class TradeActionActivity extends PeachBaseActivity {

    @Bind(R.id.iv_nav_back)
    ImageView ivNavBack;
    @Bind(R.id.tv_title_bar_title)
    TextView tvTitleBarTitle;
    @Bind(R.id.tv_state)
    TextView tvState;
    @Bind(R.id.tv_pay_feedback)
    TextView tvPayFeedback;
    @Bind(R.id.tv_type_title)
    TextView tvTypeTitle;
    @Bind(R.id.tv_notice_content)
    TextView tvNoticeContent;
    @Bind(R.id.ll_notice)
    LinearLayout llNotice;
    @Bind(R.id.tv_goods_name)
    TextView tvGoodsName;
    @Bind(R.id.tv_package)
    TextView tvPackage;
    @Bind(R.id.tv_goods_num)
    TextView tvGoodsNum;
    @Bind(R.id.tv_total_date)
    TextView tvTotalDate;
    @Bind(R.id.tv_goods_order_id)
    TextView tvGoodsOrderId;
    @Bind(R.id.ll_goods_info)
    LinearLayout llGoodsInfo;
    @Bind(R.id.tv_customer_name)
    TextView tvCustomerName;
    @Bind(R.id.tv_real_price)
    TextView tvRealPrice;
    @Bind(R.id.tv_drawback_time)
    TextView tvDrawbackTime;
    @Bind(R.id.tv_drawback_reason)
    TextView tvDrawbackReason;
    @Bind(R.id.tv_drawback_message)
    TextView tvDrawbackMessage;
    @Bind(R.id.ll_drawback_user_info)
    LinearLayout llDrawbackUserInfo;
    @Bind(R.id.tv_order_user_name)
    TextView tvOrderUserName;
    @Bind(R.id.tv_order_tel)
    TextView tvOrderTel;
    @Bind(R.id.tv_deliver_order_date)
    TextView tvDeliverOrderDate;
    @Bind(R.id.tv_order_message)
    TextView tvOrderMessage;
    @Bind(R.id.ll_deliver_user_info)
    LinearLayout llDeliverUserInfo;
    @Bind(R.id.tv_price)
    TextView tvPrice;
    @Bind(R.id.et_price)
    EditText etPrice;
    @Bind(R.id.tv_sign)
    TextView tvSign;
    @Bind(R.id.ll_drawback_price_container)
    LinearLayout llDrawbackPriceContainer;
    @Bind(R.id.tv_leave_message)
    TextView tvLeaveMessage;
    @Bind(R.id.ll_leave_message)
    LinearLayout llLeaveMessage;
    @Bind(R.id.tv_drawback_info)
    TextView tvDrawbackInfo;
    @Bind(R.id.ll_drawback_info)
    LinearLayout llDrawbackInfo;
    @Bind(R.id.et_uneditable)
    EditText etUneditable;
    @Bind(R.id.et_editable)
    EditText etEditable;
    @Bind(R.id.ll_drawback_action_container)
    LinearLayout llDrawbackActionContainer;
    @Bind(R.id.tv_action0)
    TextView tvAction0;
    @Bind(R.id.ll_trade_action0)
    LinearLayout llTradeAction0;
    @Bind(R.id.ll_state)
    LinearLayout llState;
    @Bind(R.id.tv_drawback_info_title)
    TextView tv_drawback_info_title;
    private int type;
    private static final int DRAWBACK_OUT_OF_STOCK = 1;
    private static final int DELIVER = 2;
    private static final int DRAWBACK_BEFORE_DELIVER = 3;
    private static final int DRAWBACK_AFTER_DELIVER = 4;
    private static final int DRAWBACK_DENY = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_action);
        ButterKnife.bind(this);
        type = getIntent().getIntExtra("type", -1);
        long orderId = getIntent().getLongExtra("orderId", -1);
        getData(orderId);
        ivNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getData(long orderId) {
        TravelApi.getOrderDetail(orderId, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson<OrderBean> bean = CommonJson.fromJson(result, OrderBean.class);
                bindView(bean.result);

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                bindView(null);
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void bindView(final OrderBean bean) {
        final long userId = AccountManager.getInstance().getLoginAccount(this).getUserId();
        switch (type) {
            case DRAWBACK_OUT_OF_STOCK:
                tvTitleBarTitle.setText("缺货退款");

                tvTypeTitle.setVisibility(View.VISIBLE);
                tvTypeTitle.setText("提示");
                llNotice.setVisibility(View.VISIBLE);
                tvNoticeContent.setText("1.买家已经付款，您还未做任何处理。\n2.如果您想取消交易，可退款给买家。\n3.您还可以发货。");

                llDrawbackUserInfo.setVisibility(View.VISIBLE);

                llDrawbackActionContainer.setVisibility(View.VISIBLE);
                llDrawbackPriceContainer.setVisibility(View.VISIBLE);
                tvPrice.setVisibility(View.VISIBLE);
                tvPrice.setText("123");
                llLeaveMessage.setVisibility(View.VISIBLE);
                etEditable.setVisibility(View.VISIBLE);

                tvAction0.setText("退款");
                tvAction0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawback(bean.getOrderId(),userId,bean.getTotalPrice(),etEditable.getText().toString());
                    }
                });
                break;
            case DELIVER:
                tvTitleBarTitle.setText("发货");

                tvTypeTitle.setVisibility(View.VISIBLE);
                tvTypeTitle.setText("商品订单信息");
                llGoodsInfo.setVisibility(View.VISIBLE);

                llDeliverUserInfo.setVisibility(View.VISIBLE);

                tvAction0.setText("确认发货");
                tvAction0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bean.getStatus().equals("paid")){
                            commit(bean.getOrderId(),userId);
                        }else {
                            commitAndDeny(bean.getOrderId(),userId);
                        }
                    }
                });
                break;
            case DRAWBACK_BEFORE_DELIVER:
                tvTitleBarTitle.setText("同意退款");
                llState.setVisibility(View.VISIBLE);

                tvTypeTitle.setVisibility(View.VISIBLE);
                tvTypeTitle.setText("提示");
                llNotice.setVisibility(View.VISIBLE);
                tvNoticeContent.setText("1.买家已经付款，您对订单还未做任何处理。\n2.如果您在买家申请退款后48小时内未做处理，系统将自动退款给买家。\n3.如果您拒绝退款，您可以选择发货。");

                llDrawbackUserInfo.setVisibility(View.VISIBLE);
                llDrawbackActionContainer.setVisibility(View.VISIBLE);
                llDrawbackPriceContainer.setVisibility(View.VISIBLE);
                tvPrice.setVisibility(View.VISIBLE);
                tvPrice.setText("234");
                llLeaveMessage.setVisibility(View.VISIBLE);
                etEditable.setVisibility(View.VISIBLE);
                tvAction0.setText("退款");
                tvAction0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawback(bean.getOrderId(),userId,bean.getTotalPrice(),etEditable.getText().toString());
                    }
                });
                break;
            case DRAWBACK_AFTER_DELIVER:
                tvTitleBarTitle.setText("同意退款");
                llState.setVisibility(View.VISIBLE);

                tvTypeTitle.setVisibility(View.VISIBLE);
                tvTypeTitle.setText("提示");
                llNotice.setVisibility(View.VISIBLE);
                tvNoticeContent.setText("1.您已发货，买家申请了退款。\n2.如果您同意退款，系统审核后，将钱款退还给买家。\n3.如果您拒绝退款，请输入拒绝原因，避免与买家发生交易冲突。\n4.如果您在买家申请退款后48小时内未做处理，系统将自动退款给买家");

                llDrawbackUserInfo.setVisibility(View.VISIBLE);
                llDrawbackActionContainer.setVisibility(View.VISIBLE);
                llDrawbackPriceContainer.setVisibility(View.VISIBLE);
                etPrice.setVisibility(View.VISIBLE);
                llDrawbackInfo.setVisibility(View.VISIBLE);
                etEditable.setVisibility(View.VISIBLE);
                tvAction0.setText("退款");
                tvAction0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isDigitsOnly(etPrice.getText().toString())){
                            Toast.makeText(mContext,"请输入合法的金额",Toast.LENGTH_LONG).show();
                            return;
                        }
                        drawback(bean.getOrderId(), userId, Double.parseDouble(etPrice.getText().toString()), etEditable.getText().toString());
                    }
                });
                break;
            case DRAWBACK_DENY:
                tvTitleBarTitle.setText("拒绝退款");
                llState.setVisibility(View.VISIBLE);

                tvTypeTitle.setVisibility(View.VISIBLE);
                tvTypeTitle.setText("提示");
                llNotice.setVisibility(View.VISIBLE);
                tvNoticeContent.setText("1.您已发货，买家申请了退款。\n2.如果您同意退款，系统审核后，将钱款退还给买家。\n3.如果您拒绝退款，请输入拒绝原因，避免与买家发生交易冲突。\n4.如果您在买家申请退款后48小时内未做处理，系统将自动退款给买家");

                llDrawbackUserInfo.setVisibility(View.VISIBLE);
                llDrawbackActionContainer.setVisibility(View.VISIBLE);

                llDrawbackInfo.setVisibility(View.VISIBLE);
                tv_drawback_info_title.setText("拒绝原因");
                etEditable.setVisibility(View.VISIBLE);
                tvAction0.setText("确定");
                tvAction0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawBackDeny(bean.getOrderId(),userId,etEditable.getText().toString());
                    }
                });
                break;
        }

        bindCustomerData(bean);
    }



    public void drawback(long orderId, long userId, double amount,String memo) {

        JSONObject data = new JSONObject();
        try {
            data.put("userId", userId);
            data.put("memo", memo);
            data.put("reason", "");
            data.put("amount",amount);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TravelApi.editOrderStatus(orderId, "refundApprove", data, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    public void commit(long orderId, long userId){
        JSONObject data = new JSONObject();
        try {
            data.put("userId", userId);
            data.put("memo", "");
            data.put("reason", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        TravelApi.editOrderStatus(orderId, "commit", data, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }


    public void commitAndDeny(final long orderId, long userId){
        final JSONObject data = new JSONObject();
        try {
            data.put("userId", userId);
            data.put("memo", "");
            data.put("reason", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        TravelApi.editOrderStatus(orderId, "refundDeny", data, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {

                TravelApi.editOrderStatus(orderId, "commit", data, new HttpCallBack<String>() {
                    @Override
                    public void doSuccess(String result, String method) {

                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {

                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method, int code) {

                    }
                });
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

    }

    public void drawBackDeny(long orderId,long userId,String memo){
        JSONObject data = new JSONObject();
        try {
            data.put("userId", userId);
            data.put("memo", memo);
            data.put("reason", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        TravelApi.editOrderStatus(orderId, "refundDeny", data, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }


    private void bindCustomerData(final OrderBean bean) {
//        tvOrderUserName.setText(bean.getContact().getGivenName()+bean.getContact().getSurname());
//        tvRealPrice.setText(String.format("¥%s", CommonUtils.getPriceString(bean.getTotalPrice() - bean.getDiscount())));
        final long userId = AccountManager.getInstance().getLoginAccount(this).getUserId();
        if (type == DELIVER) {
            tvGoodsName.setText(bean.getCommodity().getTitle());
            tvPackage.setText(bean.getCommodity().getPlans().get(0).getTitle());
            tvGoodsNum.setText(String.valueOf(bean.getQuantity()));
            tvTotalDate.setText("¥"+ CommonUtils.getPriceString(bean.getTotalPrice()));
            tvGoodsOrderId.setText(String.valueOf(bean.getOrderId()));

            tvOrderUserName.setText(bean.getContact().getSurname()+bean.getContact().getGivenName());
            tvOrderTel.setText(bean.getContact().getTel().toString());
            tvDeliverOrderDate.setText(bean.getRendezvousTime());
            tvOrderMessage.setText(bean.getComment());
        } else {
            tvCustomerName.setText(bean.getContact().getSurname()+bean.getContact().getGivenName());
            tvRealPrice.setText("¥" + CommonUtils.getPriceString(bean.getTotalPrice() - bean.getDiscount()));
            TradeActivityBean activityBean = getDrawBackData(bean.activities);
            if (activityBean!=null){
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                tvDrawbackTime.setText(format.format(new Date(activityBean.timestamp)));
                if (activityBean.data!=null){
                    tvDrawbackReason.setText(activityBean.data.reason);
                    tvDrawbackMessage.setText(activityBean.data.memo);
                }

            }
            tvPrice.setText(CommonUtils.getPriceString(bean.getTotalPrice()));
        }

        tvAction0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog(TradeActionActivity.this, bean.getOrderId(), userId);
            }
        });
    }


    public TradeActivityBean getDrawBackData(ArrayList<TradeActivityBean> activityBeans){
        for (TradeActivityBean activityBean : activityBeans) {
            if ("refundApply".equals(activityBean.action)){
                return activityBean;
            }
        }
        return null;
    }

    private void showConfirmDialog(final Activity act, final long orderId, final long userId) {
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        View contentView = View.inflate(act, R.layout.dialog_confirm_password, null);
        EditText et = (EditText) contentView.findViewById(R.id.et_password);
        TextView tvConfirm = (TextView) contentView.findViewById(R.id.tv_confirm);
        TextView tvCancle = (TextView) contentView.findViewById(R.id.tv_cancel);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit(orderId,userId);
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
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }
}
