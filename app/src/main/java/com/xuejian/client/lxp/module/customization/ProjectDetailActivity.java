package com.xuejian.client.lxp.module.customization;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.api.TravelApi;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2016/4/6.
 */
public class ProjectDetailActivity extends PeachBaseActivity {


    @Bind(R.id.iv_nav_back)
    ImageView ivNavBack;
    @Bind(R.id.tv_title_bar_title)
    TextView tvTitleBarTitle;
    @Bind(R.id.tv_pay_state)
    TextView tvPayState;
    @Bind(R.id.tv_pay_feedback)
    TextView tvPayFeedback;
    @Bind(R.id.ll_state)
    LinearLayout llState;
    @Bind(R.id.iv_avatar)
    ImageView ivAvatar;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_timestamp)
    TextView tvTimestamp;
    @Bind(R.id.tv_state)
    TextView tvState;
    @Bind(R.id.tv_project_info1)
    TextView tvProjectInfo1;
    @Bind(R.id.tv_project_time)
    TextView tvProjectTime;
    @Bind(R.id.tv_project_info2)
    TextView tvProjectInfo2;
    @Bind(R.id.tv_project_count)
    TextView tvProjectCount;
    @Bind(R.id.tv_project_price)
    TextView tvProjectPrice;
    @Bind(R.id.ll_container)
    LinearLayout llContainer;
    @Bind(R.id.tv_contact_name)
    TextView tvContactName;
    @Bind(R.id.tv_contact_tel)
    TextView tvContactTel;
    @Bind(R.id.tv_company_name)
    TextView tvCompanyName;
    @Bind(R.id.tv_departure_city)
    TextView tvDepartureCity;
    @Bind(R.id.tv_departure_date)
    TextView tvDepartureDate;
    @Bind(R.id.tv_departure_cnt)
    TextView tvDepartureCnt;
    @Bind(R.id.tv_departure_people)
    TextView tvDeparturePeople;
    @Bind(R.id.tv_total_price)
    TextView tvTotalPrice;
    @Bind(R.id.tv_target_city)
    TextView tvTargetCity;
    @Bind(R.id.tv_service)
    TextView tvService;
    @Bind(R.id.tv_theme)
    TextView tvTheme;
    @Bind(R.id.tv_message)
    TextView tvMessage;
    @Bind(R.id.tv_append_message)
    TextView tvAppendMessage;
    @Bind(R.id.tv_action0)
    TextView tvAction0;
    @Bind(R.id.ll_trade_action0)
    LinearLayout llTradeAction0;
    @Bind(R.id.tv_cancel_action)
    TextView tvCancelAction;
    @Bind(R.id.tv_pay)
    TextView tvPay;
    @Bind(R.id.ll_trade_action1)
    LinearLayout llTradeAction1;


    long id;
    boolean isOwner;
    boolean isSeller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        ButterKnife.bind(this);
        id = getIntent().getLongExtra("id", -1);

        ivNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getData(id);
    }

    private void getData(long id) {

        bindView();
    }

    private void bindView() {

        llTradeAction0.setVisibility(View.VISIBLE);
        tvAction0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeOrder();
            }
        });
    }

    private void takeOrder() {
        TravelApi.takeOrder(id, new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                Log.d("ProjectDetailActivity", "result:" + result);
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }
}
