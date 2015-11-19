package com.xuejian.client.lxp.module.goods;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;

/**
 * Created by yibiao.qin on 2015/11/13.
 */
public class OrderDetailActivity extends PeachBaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        TextView tvGoodsName = (TextView) findViewById(R.id.tv_goods_name);
        findViewById(R.id.userinfo).setOnClickListener(this);
        tvGoodsName.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvGoodsName.getPaint().setAntiAlias(true);//抗锯齿
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.userinfo:
                Intent intent = new Intent(OrderDetailActivity.this,CommonUserInfoActivity.class);
                intent.putExtra("ListType",2);
                startActivity(intent);
                break;
        }
    }


}
