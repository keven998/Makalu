package com.xuejian.client.lxp.module.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.xuejian.client.lxp.R;

/**
 * Created by xuyongchen on 15/10/21.
 */
public class ChoosePayWayActivity extends Activity implements View.OnClickListener{

    private Button alipay_button;
    private Button weixinpay_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.pay_way_activity);
        alipay_button = (Button)findViewById(R.id.alipay_button);
        weixinpay_button = (Button)findViewById(R.id.weixinpay_button);

        alipay_button.setOnClickListener(this);
        weixinpay_button.setOnClickListener(this);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.alipay_button:
                Intent alipayintent = new Intent(ChoosePayWayActivity.this,PayDetailAcitivity.class);
                alipayintent.putExtra("payType",1);
                ChoosePayWayActivity.this.startActivity(alipayintent);
                break;
            case R.id.weixinpay_button:
                Intent weixinpayintent = new Intent(ChoosePayWayActivity.this,PayDetailAcitivity.class);
                weixinpayintent.putExtra("payType",2);
                ChoosePayWayActivity.this.startActivity(weixinpayintent);
                break;

        }
    }
}
