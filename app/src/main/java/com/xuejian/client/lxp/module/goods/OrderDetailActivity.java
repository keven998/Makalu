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
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.utils.CommonUtils;

/**
 * Created by yibiao.qin on 2015/11/13.
 */
public class OrderDetailActivity extends PeachBaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        findViewById(R.id.iv_nav_back).setOnClickListener(this);
        TextView tvGoodsName = (TextView) findViewById(R.id.tv_goods_name);
        findViewById(R.id.user_info).setOnClickListener(this);
        findViewById(R.id.tv_pay).setOnClickListener(this);
        tvGoodsName.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvGoodsName.getPaint().setAntiAlias(true);//抗锯齿
        final TextView feedBack = (TextView) findViewById(R.id.tv_pay_feedback);
        CountDownTimer countDownTimer = new CountDownTimer(1000000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                feedBack.setText(String.format("请在%s内完成支付", CommonUtils.formatDuring(millisUntilFinished)));
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_info:
                Intent intent = new Intent(OrderDetailActivity.this,CommonUserInfoActivity.class);
                intent.putExtra("ListType",2);
                startActivity(intent);
                break;
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
                Intent tv_pay = new Intent(OrderDetailActivity.this,OrderListActivity.class);
                startActivity(tv_pay);
            }
        });
        weixinpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent tv_pay = new Intent(OrderDetailActivity.this,OrderListActivity.class);
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
