package com.xuejian.client.lxp.module.trade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.StoreBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2016/3/17.
 */
public class TradeMainActivity extends PeachBaseActivity {

    @Bind(R.id.setting_head_back)
    ImageView ivBack;
    @Bind(R.id.tv_sales)
    TextView tvSales;
    @Bind(R.id.tv_orders)
    TextView tvOrders;
    @Bind(R.id.rl_order)
    RelativeLayout rlOrder;
    @Bind(R.id.rl_commodity)
    RelativeLayout rlCommodity;
    @Bind(R.id.tv_pending_order)
    TextView tv_pending_order;
    @Bind(R.id.rl_service)
    RelativeLayout rlService;
    @Bind(R.id.rl_sub)
    RelativeLayout rl_sub;
    long userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_main);
        ButterKnife.bind(this);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        userId = AccountManager.getInstance().getLoginAccount(this).getUserId();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userId>0){
            getShopDetail(userId);
        }
    }

    private void getShopDetail(long userId) {
        TravelApi.getSellerInfo(userId, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson<StoreBean> commonJson = CommonJson.fromJson(result, StoreBean.class);
                if (commonJson.code == 0) {
                    bindView(commonJson.result);
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void bindView(StoreBean bean) {

        tvOrders.setText(String.format("总订单量\n%d单",bean.totalOrderCnt));
        tvSales.setText(String.format("总销售额\n%s元", CommonUtils.getPriceString(bean.totalSales)));
        tv_pending_order.setText(String.format("订单(%d单待处理)",bean.pendingOrderCnt));
        rlCommodity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TradeMainActivity.this, TradeOrderListActivity.class);
                intent.putExtra("type", TradeOrderListActivity.GOODS);
                startActivity(intent);
            }
        });
        rlOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(TradeMainActivity.this, TradeOrderListActivity.class);
                intent1.putExtra("type", TradeOrderListActivity.ORDER);
                startActivity(intent1);
            }
        });
        rlService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(TradeMainActivity.this, ServiceListActivity.class);
                startActivity(intent1);
            }
        });
        rl_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(TradeMainActivity.this, SubscribeLocActivity.class);
                startActivity(intent1);
            }
        });
    }
}
