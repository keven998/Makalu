package com.xuejian.client.lxp.module.customization;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.bumptech.glide.Glide;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.BountyItemBean;
import com.xuejian.client.lxp.bean.ProjectDetailBean;
import com.xuejian.client.lxp.bean.ProjectEvent;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.ListViewForScrollView;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
    @Bind(R.id.tv_contact)
    TextView tv_contact;
    @Bind(R.id.ll_contact_container)
    LinearLayout  ll_contact_container;
    @Bind(R.id.tv_message_title)
    TextView tv_message_title;
    long id;
    boolean isOwner;
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
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnProjectEvent(ProjectEvent event){
        if ("refresh".equals(event.status)){
             getData(id);
        }
    }
    private void getBountyList(long id) {
        TravelApi.getBOUNTYLIST(id, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<BountyItemBean> list = CommonJson4List.fromJson(result, BountyItemBean.class);
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



    private void getData(long id) {

        TravelApi.getPROJECT_DETAIL(id, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson<ProjectDetailBean> beanCommonJson = CommonJson.fromJson(result, ProjectDetailBean.class);
                bindView(beanCommonJson.result);
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void bindView(final ProjectDetailBean bean) {


        if (bean.consumer!=null){
            tvName.setText(bean.consumer.getNickname());
            Glide.with(mContext)
                    .load(bean.consumer.getAvatar().getUrl())
                    .placeholder(R.drawable.ic_default_picture)
                    .error(R.drawable.ic_default_picture)
                    .centerCrop()
                    .into(ivAvatar);
        }

        tvTimestamp.setText(String.format("在%s发布了需求", CommonUtils.getTimestampString(new Date())));
        StringBuilder desc = new StringBuilder();
        if (bean.getDestination()!=null&&bean.getDestination().size()>0){
            for (int i = 0; i < bean.getDestination().size(); i++) {
                if (i != 0) desc.append("、");
                desc.append(bean.getDestination().get(i).zhName);
            }
        }
        tvProjectInfo1.setText(String.format("[%s]", desc));
        tvProjectTime.setText(String.format(Locale.CHINA, "%d日游", bean.getTimeCost()));
        tvProjectInfo2.setText(bean.getService());
        tvProjectCount.setText(String.format(Locale.CHINA, "已有%d位商家抢单", bean.getTakers().size()));


        String budget = String.format("定金%s元", CommonUtils.getPriceString(bean.getBountyPrice()));
        String total = String.format("总预算%s元", CommonUtils.getPriceString(bean.getBudget()));

        SpannableString budgetString = new SpannableString(budget);
        budgetString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.price_color)), 2, budget.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString totalString = new SpannableString(total);
        totalString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.price_color)), 3, total.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder price = new SpannableStringBuilder();
        price.append(budgetString).append("  ").append(totalString);
        tvProjectPrice.setText(price);

        if (bean.getContact() != null && bean.getContact().size() > 0) {
            tvContactName.setText(bean.getContact().get(0).getSurname() + bean.getContact().get(0).getGivenName());
            tvContactTel.setText(bean.getContact().get(0).getTel().getDialCode() + "-" + bean.getContact().get(0).getTel().getNumber());
        }


        if (bean.getDeparture() != null && bean.getDeparture().size() > 0) {
            tvDepartureCity.setText(bean.getDeparture().get(0).zhName);
        }

        tvDepartureDate.setText(bean.getDepartureDate());
        tvDepartureCnt.setText(String.format("%d天左右", bean.getTimeCost()));
        StringBuilder departurePeople = new StringBuilder();
        departurePeople.append(String.format("%d人左右", bean.getParticipantCnt()));
        if (bean.getParticipants().size() > 0) {
            if (bean.getParticipants().contains("children")) departurePeople.append(" 含儿童 ");
            if (bean.getParticipants().contains("oldman")) departurePeople.append(" 含老人 ");
        }
        tvDeparturePeople.setText(departurePeople);
        tvTotalPrice.setText(String.format("总预算%s元", CommonUtils.getPriceString(bean.getBudget())));

        StringBuilder target = new StringBuilder();
        for (int i = 0; i < bean.getDestination().size(); i++) {
            if (i != 0) target.append("、");
            target.append(bean.getDestination().get(i).zhName);
        }
        tvTargetCity.setText(target);
        tvService.setText(bean.getService());
        tvTheme.setText(bean.getTopic());

        if (TextUtils.isEmpty(bean.getMemo())){
            tvMessage.setVisibility(View.GONE);
            tv_message_title.setVisibility(View.GONE);
        }
        tvMessage.setText(bean.getMemo());

        User user = AccountManager.getInstance().getLoginAccount(this);
        if (user != null) {
            if (user.getUserId() == bean.getConsumerId()) {
                if (bean.isBountyPaid()) {
                    llState.setVisibility(View.VISIBLE);
                    tvState.setText("已支付");
                    llTradeAction0.setVisibility(View.VISIBLE);
                    tvAction0.setText("申请退款");
                    llTradeAction0.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawback();
                        }
                    });

                } else {

                }

                tv_contact.setVisibility(View.VISIBLE);
                ll_contact_container.setVisibility(View.VISIBLE);
            }else {

                if (AccountManager.getInstance().isSeller()){

                    llTradeAction0.setVisibility(View.VISIBLE);
                    llTradeAction0.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            takeOrder(bean.getConsumerId());
                        }
                    });


                }
            }

        }



    }


    private void drawback(){
        final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
        dialog.setTitle("提示");
        dialog.setMessage("确定申请退款？");
        dialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void takeOrder(final long consumerId) {

        TravelApi.takeOrder(id, new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                ToastUtil.getInstance(mContext).showToast("您已接单成功，快去制定旅游方案吧~");
                takeOrderSuccess(consumerId);

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

    private void takeOrderSuccess(final long consumerId) {
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
                talkIntent.putExtra("friend_id", consumerId + "");
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
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bounty, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final BountyItemBean bean = (BountyItemBean) getItem(position);
            if (bean.getSeller() != null && bean.getSeller().size() > 0) {
                holder.mTvName.setText(bean.getSeller().get(0).getName());
//                Glide.with(mContext)
//                        .load(bean.getSeller().get(0).)
//                        .placeholder(R.drawable.ic_default_picture)
//                        .error(R.drawable.ic_default_picture)
//                        .centerCrop()
//                        .into(holder.mIvAvatar);
            }

            holder.mTvTimestamp.setText(DateFormat.format("yyyy-MM-dd", bean.getCreateTime()));

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
                    Intent intent = new Intent(mContext, CreatePlanActivity.class);
                    intent.putExtra("isDetail",true);
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
