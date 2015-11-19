package com.xuejian.client.lxp.module.pay;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yibiao.qin on 2015/11/18.
 */
public class PaymentActivity extends PeachBaseActivity implements View.OnClickListener {

    @InjectView(R.id.tv_title_back)
    TextView tvTitleBack;
    @InjectView(R.id.ctv_alipay)
    CheckedTextView ctvAlipay;
    @InjectView(R.id.ctv_weixin)
    CheckedTextView ctvWeixin;
    @InjectView(R.id.tv_price)
    TextView tvPrice;
    @InjectView(R.id.tv_pay)
    TextView tvPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.inject(this);
        ctvWeixin.setOnClickListener(this);
        ctvAlipay.setOnClickListener(this);
        tvTitleBack.setOnClickListener(this);


        SpannableString priceStr = new SpannableString("¥35353");
        priceStr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.price_color)), 0, priceStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        priceStr.setSpan(new AbsoluteSizeSpan(15, true), 0, priceStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder spb = new SpannableStringBuilder();
        spb.append("总价:").append(priceStr);
        tvPrice.setText(spb);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ctv_alipay:
                ctvAlipay.setChecked(true);
                if (ctvWeixin.isChecked()) {
                    ctvWeixin.setChecked(false);
                }
                break;
            case R.id.ctv_weixin:
                ctvWeixin.setChecked(true);
                if (ctvAlipay.isChecked()) {
                    ctvAlipay.setChecked(false);
                }
                break;
            case R.id.tv_title_back:
                finish();
                break;
        }
    }
}
