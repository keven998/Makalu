package com.xuejian.client.lxp.module.goods;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.OrderBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;

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
    TextView tvPayState;
    @InjectView(R.id.tv_pay_feedback)
    TextView tvPayFeedback;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.inject(this);
        String type = getIntent().getStringExtra("type");
        switch (type) {
            case "pendingOrder":
                OrderBean bean = getIntent().getParcelableExtra("order");
                bindView(bean);
                break;
            case "orderDetail":
                getData(getIntent().getLongExtra("orderId",-1));
                break;
            default:
                break;
        }

        ivNavBack.setOnClickListener(this);
        userInfo.setOnClickListener(this);
        tvPay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pay:
                showActionDialog();
                break;
            case R.id.iv_nav_back:
                Intent tv_title_back = new Intent(OrderDetailActivity.this, OrderListActivity.class);
                startActivity(tv_title_back);
                finish();
                break;
        }
    }

    public void getData(long orderId) {
        if (orderId<=0)return;
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
        tvGoodsName.setText(bean.getCommodity().getTitle());
        tvGoodsName.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvGoodsName.getPaint().setAntiAlias(true);//抗锯齿
        tvOrderId.setText(String.valueOf(bean.getOrderId()));
        tvOrderPackage.setText(bean.getCommodity().getPlans().get(0).getTitle());
        tvOrderDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(bean.getRendezvousTime())));
        tvOrderNum.setText(String.valueOf(bean.getQuantity()));
        tvOrderPrice.setText("¥"+String.valueOf(bean.getTotalPrice()));

        tvOrderTravellerCount.setText(String.valueOf(bean.getTravellers().size()));

        tvOrderContactName.setText(bean.getContact().getGivenName()+" "+bean.getContact().getSurname());
        tvOrderContactTel.setText(bean.getContact().getTel().getDialCode() + "-" + bean.getContact().getTel().getNumber());
        tvOrderMessage.setText(bean.getComment());

        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this, CommonUserInfoActivity.class);
                intent.putExtra("ListType", 3);
                intent.putParcelableArrayListExtra("passengerList",bean.getTravellers());
                startActivity(intent);
            }
        });



        CountDownTimer countDownTimer = new CountDownTimer(1000000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvPayFeedback.setText(String.format("请在%s内完成支付", CommonUtils.formatDuring(millisUntilFinished)));
            }
            @Override
            public void onFinish() {

            }
        }.start();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent tv_title_back = new Intent(OrderDetailActivity.this, OrderListActivity.class);
        startActivity(tv_title_back);
        finish();
    }

    private void showActionDialog() {
        final Activity act = this;
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        View contentView = View.inflate(act, R.layout.dialog_select_payment, null);
        CheckedTextView alipay = (CheckedTextView) contentView.findViewById(R.id.ctv_alipay);
        CheckedTextView weixinpay = (CheckedTextView) contentView.findViewById(R.id.ctv_weixin);
        alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent tv_pay = new Intent(OrderDetailActivity.this, OrderListActivity.class);
                startActivity(tv_pay);
            }
        });
        weixinpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent tv_pay = new Intent(OrderDetailActivity.this, OrderListActivity.class);
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
