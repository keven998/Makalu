package com.xuejian.client.lxp.module.customization;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.BountyItemBean;
import com.xuejian.client.lxp.bean.StoreBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.widget.ListViewForScrollView;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;

import java.util.ArrayList;

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
    @Bind(R.id.lv_plan)
    ListViewForScrollView mLvPlan;

    long id;
    boolean isOwner;
    boolean isSeller;
    long userId;
    private PlanAdapter mPlanAdapter;

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
        if (AccountManager.getInstance().getLoginAccount(this) != null) {
            userId = AccountManager.getInstance().getLoginAccount(this).getUserId();
        }
        mPlanAdapter = new PlanAdapter();
        mLvPlan.setAdapter(mPlanAdapter);
        getData(id);
        getBountyList(id);
    }

    private void getBountyList(long id) {
        TravelApi.getBOUNTYLIST(id, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<BountyItemBean> list = CommonJson4List.fromJson(result,BountyItemBean.class);
                mPlanAdapter.getList().addAll(list.result);
                mPlanAdapter.notifyDataSetChanged();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }


    public void isBusiness(long userId) {
        TravelApi.getSellerInfo(userId, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson<StoreBean> commonJson = CommonJson.fromJson(result, StoreBean.class);
                if (commonJson.code == 0) {
                    isSeller = true;
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {
                isSeller = false;
            }
        });
    }


    private void getData(long id) {

        bindView();
    }

    private void bindView() {

        llTradeAction0.setVisibility(View.VISIBLE);
        llTradeAction0.setOnClickListener(new View.OnClickListener() {
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
                ToastUtil.getInstance(mContext).showToast("您已接单成功，快去制定旅游方案吧~");
                takeOrderSuccess();

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                ToastUtil.getInstance(mContext).showToast("接单失败~");
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void takeOrderSuccess() {
        llTradeAction0.setVisibility(View.GONE);
        llTradeAction1.setVisibility(View.VISIBLE);
        tvCancelAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCreatePlan();
                finish();
            }
        });
        tvPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent talkIntent = new Intent(mContext, ChatActivity.class);
                talkIntent.putExtra("friend_id", 100004+"");
                talkIntent.putExtra("chatType", "single");
                startActivity(talkIntent);
            }
        });
    }

    private void toCreatePlan() {
        Intent intent = new Intent(this, CreatePlanActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }


    public class PlanAdapter extends BaseAdapter {

        ArrayList<BountyItemBean> list;

        public ArrayList<BountyItemBean> getList() {
            return list;
        }

        public PlanAdapter() {
            list = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView==null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bounty, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            final BountyItemBean bean = (BountyItemBean) getItem(position);
            if (bean.getSeller()!=null&&bean.getSeller().size()>0){
                holder.mTvName.setText(bean.getSeller().get(0).getName());
//                Glide.with(mContext)
//                        .load(bean.getSeller().get(0).)
//                        .placeholder(R.drawable.ic_default_picture)
//                        .error(R.drawable.ic_default_picture)
//                        .centerCrop()
//                        .into(holder.mIvAvatar);
            }

            holder.mTvTimestamp.setText( DateFormat.format("yyyy-MM-dd",bean.getCreateTime()));

            holder.mIvTalk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent talkIntent = new Intent(mContext, ChatActivity.class);
                    talkIntent.putExtra("friend_id", bean.getSeller().get(0).getSellerId());
                    talkIntent.putExtra("chatType", "single");
                    startActivity(talkIntent);
                }
            });
            holder.ll_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PlanDetailActivity.class);
                    intent.putExtra("id", bean.getItemId());
                    startActivity(intent);
                }
            });
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.iv_avatar)
            ImageView mIvAvatar;
            @Bind(R.id.tv_name)
            TextView mTvName;
            @Bind(R.id.tv_timestamp)
            TextView mTvTimestamp;
            @Bind(R.id.iv_talk)
            ImageView mIvTalk;
            @Bind(R.id.iv_state)
            ImageView mIvState;
            @Bind(R.id.ll_container)
            LinearLayout ll_container;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

}
