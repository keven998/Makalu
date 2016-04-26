package com.xuejian.client.lxp.module.customization;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.bumptech.glide.Glide;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.BountyItemBean;
import com.xuejian.client.lxp.bean.Consumer;
import com.xuejian.client.lxp.bean.ProjectDetailBean;
import com.xuejian.client.lxp.bean.ProjectEvent;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.ListViewForScrollView;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

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
    @Bind(R.id.tv_action6)
    TextView tvAction0;
    @Bind(R.id.ll_trade_action0)
    LinearLayout llTradeAction0;
    @Bind(R.id.tv_cancel_action1)
    TextView tvCancelAction;
    @Bind(R.id.tv_pay1)
    TextView tvPay;
    @Bind(R.id.ll_trade_action1)
    LinearLayout llTradeAction1;
    @Bind(R.id.lv_plan)
    ListViewForScrollView mLvPlan;
    @Bind(R.id.tv_contact)
    TextView tv_contact;
    @Bind(R.id.ll_contact_container)
    LinearLayout ll_contact_container;
    @Bind(R.id.tv_message_title)
    TextView tv_message_title;
    long id;
    boolean isOwner;
    long userId;
    private PlanAdapter mPlanAdapter;
    private boolean isTakerOrder;
    private boolean isCreatePlan;

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

        getData(id);
        //   getBountyList(id);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnProjectEvent(ProjectEvent event) {
        if ("refresh".equals(event.status)) {
            getData(id);
        }
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


        if (bean.consumer != null) {
            tvName.setText(bean.consumer.getNickname());
            Glide.with(mContext)
                    .load(bean.consumer.getAvatar().getUrl())
                    .placeholder(R.drawable.ic_default_picture)
                    .error(R.drawable.ic_default_picture)
                    .centerCrop()
                    .into(ivAvatar);
        }

        tvTimestamp.setText(String.format("在%s发布了需求", CommonUtils.getTimestampString(new Date(bean.createTime))));
        StringBuilder desc = new StringBuilder();
        if (bean.getDestination() != null && bean.getDestination().size() > 0) {
            for (int i = 0; i < bean.getDestination().size(); i++) {
                if (i != 0) desc.append("、");
                desc.append(bean.getDestination().get(i).zhName);
            }
        }
        tvProjectInfo1.setText(String.format("[%s]", desc));
        tvProjectTime.setText(String.format(Locale.CHINA, "%d日游", bean.getTimeCost()));
        tvProjectInfo2.setText(bean.getService());
        tvProjectCount.setText(String.format(Locale.CHINA, "已有%d位商家抢单", bean.takers.size()));


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
        tvTotalPrice.setText(String.format("%s元", CommonUtils.getPriceString(bean.getBudget())));

        StringBuilder target = new StringBuilder();
        for (int i = 0; i < bean.getDestination().size(); i++) {
            if (i != 0) target.append("、");
            target.append(bean.getDestination().get(i).zhName);
        }
        tvTargetCity.setText(target);
        tvService.setText(bean.getService());
        tvTheme.setText(bean.getTopic());

        if (TextUtils.isEmpty(bean.getMemo())) {
            tvMessage.setVisibility(View.GONE);
            tv_message_title.setVisibility(View.GONE);
        }
        tvMessage.setText(bean.getMemo());

        final User user = AccountManager.getInstance().getLoginAccount(this);
        if (user != null) {
            if (user.getUserId() == bean.getConsumerId()) {
                if ("refundApplied".equals(bean.status)) {

                } else if (bean.isBountyPaid() && !bean.isSchedulePaid()) {
                    llState.setVisibility(View.VISIBLE);
                    tvPayState.setText("已支付定金");
                    llTradeAction0.setVisibility(View.VISIBLE);
                    tvAction0.setText("申请退款");
                    llTradeAction0.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawback(bean.getItemId(), "bounty", bean.getBountyPrice());
                        }
                    });

                } else if (bean.isBountyPaid() && bean.isSchedulePaid()) {
                    llState.setVisibility(View.VISIBLE);
                    tvPayState.setText("已支付方案");
                    llTradeAction0.setVisibility(View.VISIBLE);
                    tvAction0.setText("申请退款");
                    llTradeAction0.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawback(bean.getItemId(), "schedule", bean.getBudget());
                        }
                    });

                } else if (!bean.isBountyPaid() && bean.isSchedulePaid()) {
                    llState.setVisibility(View.VISIBLE);
                    tvPayState.setText("已支付方案");
                    llTradeAction0.setVisibility(View.VISIBLE);
                    tvAction0.setText("申请退款");
                    llTradeAction0.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawback(bean.getItemId(), "schedule", bean.getBudget());
                        }
                    });

                } else {
                    llTradeAction0.setVisibility(View.GONE);
                }

                tv_contact.setVisibility(View.VISIBLE);
                ll_contact_container.setVisibility(View.VISIBLE);

                mPlanAdapter = new PlanAdapter(bean.schedules, bean.takers, false, true, bean.isSchedulePaid());
                mLvPlan.setAdapter(mPlanAdapter);
            } else {

                if (AccountManager.getInstance().isSeller()) {


                    /**
                     * 判断是否接单
                     */
                    for (Consumer taker : bean.takers) {
                        if (taker.getUserId() == user.getUserId()) {
                            isTakerOrder = true;
                            break;
                        }
                    }
                    isCreatePlan(bean.schedules, user.getUserId());
                    if ("paid".equals(bean.status) && bean.scheduled != null && bean.scheduled.seller != null && (bean.scheduled.seller.getSellerId() == user.getUserId())) {
                        llState.setVisibility(View.VISIBLE);
                        tvPayState.setText("已购买方案");
                        tv_contact.setVisibility(View.VISIBLE);
                        ll_contact_container.setVisibility(View.VISIBLE);
                        llTradeAction0.setVisibility(View.VISIBLE);
                        tvAction0.setText("联系买家");
                        tvAction0.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent talkIntent = new Intent(mContext, ChatActivity.class);
                                talkIntent.putExtra("friend_id", bean.getConsumerId() + "");
                                talkIntent.putExtra("chatType", "single");
                                startActivity(talkIntent);
                            }
                        });
                        mPlanAdapter = new PlanAdapter(bean.schedules, bean.takers, true, true, bean.isSchedulePaid());
                        mLvPlan.setAdapter(mPlanAdapter);
                    } else if ("refundApplied".equals(bean.status) && bean.scheduled != null && bean.scheduled.seller != null && (bean.scheduled.seller.getSellerId() == user.getUserId())) {
                        llState.setVisibility(View.VISIBLE);
                        tvPayState.setText("已申请退款");
                        tv_contact.setVisibility(View.VISIBLE);
                        ll_contact_container.setVisibility(View.VISIBLE);
                        mPlanAdapter = new PlanAdapter(bean.schedules, bean.takers, true, true, bean.isSchedulePaid());
                        mLvPlan.setAdapter(mPlanAdapter);
                        llTradeAction0.setVisibility(View.GONE);
                        llTradeAction1.setVisibility(View.VISIBLE);
                        tvPay.setText("联系买家");
                        tvCancelAction.setText("待退款");
                        tvCancelAction.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (bean.scheduled != null) {
                                    showRefundDialog(bean.getItemId(), user.getUserId(), bean.scheduled.getPrice(), "schedule");
                                }
                            }
                        });
                        tvPay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent talkIntent = new Intent(mContext, ChatActivity.class);
                                talkIntent.putExtra("friend_id", bean.getConsumerId() + "");
                                talkIntent.putExtra("chatType", "single");
                                startActivity(talkIntent);
                            }
                        });
                        mPlanAdapter = new PlanAdapter(bean.schedules, bean.takers, true, true, bean.isSchedulePaid());
                        mLvPlan.setAdapter(mPlanAdapter);
                    } else if (bean.isSchedulePaid()) {
                        tv_contact.setVisibility(View.VISIBLE);
                        ll_contact_container.setVisibility(View.VISIBLE);
                        llTradeAction0.setVisibility(View.VISIBLE);
                        tvAction0.setText("联系买家");
                        tvAction0.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent talkIntent = new Intent(mContext, ChatActivity.class);
                                talkIntent.putExtra("friend_id", bean.getConsumerId() + "");
                                talkIntent.putExtra("chatType", "single");
                                startActivity(talkIntent);
                            }
                        });
                        mPlanAdapter = new PlanAdapter(bean.schedules, bean.takers, true, true, bean.isSchedulePaid());
                        mLvPlan.setAdapter(mPlanAdapter);
                    } else if (isTakerOrder && !isCreatePlan) {
                        tv_contact.setVisibility(View.VISIBLE);
                        ll_contact_container.setVisibility(View.VISIBLE);
                        takeOrderSuccess(bean.getConsumerId());
                        mPlanAdapter = new PlanAdapter(bean.schedules, bean.takers, false, false, bean.isSchedulePaid());
                        mLvPlan.setAdapter(mPlanAdapter);
                    } else if (isTakerOrder && isCreatePlan) {
                        takeOrderSuccess(bean.getConsumerId());
                        mPlanAdapter = new PlanAdapter(bean.schedules, bean.takers, true, true, bean.isSchedulePaid());
                        mLvPlan.setAdapter(mPlanAdapter);
                    } else {
                        mPlanAdapter = new PlanAdapter(bean.schedules, bean.takers, false, false, bean.isSchedulePaid());
                        mLvPlan.setAdapter(mPlanAdapter);
                        llTradeAction0.setVisibility(View.VISIBLE);
                        tvAction0.setText("接单");
                        llTradeAction0.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                takeOrder(bean.getConsumerId());
                            }
                        });
                    }

                } else {
                    mPlanAdapter = new PlanAdapter(bean.schedules, bean.takers, false, false, bean.isSchedulePaid());
                    mLvPlan.setAdapter(mPlanAdapter);
                }
            }

        } else {
            mPlanAdapter = new PlanAdapter(bean.schedules, bean.takers, false, false, bean.isSchedulePaid());
            mLvPlan.setAdapter(mPlanAdapter);
        }


    }

    private boolean isCreatePlan(ArrayList<BountyItemBean> schedules, long userId) {
        for (BountyItemBean schedule : schedules) {

            if (schedule.seller.getSellerId() == userId) {
                isCreatePlan = true;
                return true;
            }
        }
        return false;
    }


    private void drawback(final long bountyId, final String target, final double amount) {
        final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
        dialog.setTitle("提示");
        dialog.setMessage("确定申请退款？");
        dialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                refundCus(bountyId, target, amount);

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

    /**
     * 申请退款
     *
     * @param bountyId bountyId
     * @param target   target
     * @param amount   amount
     */
    private void refundCus(long bountyId, String target, double amount) {
        JSONObject object = new JSONObject();
        User user = AccountManager.getInstance().getLoginAccount(this);

        long userId = 0;
        if (user != null) {
            userId = user.getUserId();
        }
        try {
            object.put("userId", userId);
            object.put("memo", "");
            object.put("reason", "");
            object.put("amount", amount);
            //    object.put("type", "apply");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TravelApi.drawbackCus(bountyId, "refundApply", target, object, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                Toast.makeText(ProjectDetailActivity.this, "退款申请已提交", Toast.LENGTH_LONG).show();
                llTradeAction0.setVisibility(View.GONE);
                EventBus.getDefault().post(new ProjectEvent("refresh"));
//                setResult(RESULT_OK);
//                finish();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                Toast.makeText(ProjectDetailActivity.this, "退款申请提交失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
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
        tvPay.setText("联系买家");
        tvCancelAction.setText("制作方案");
        tvCancelAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCreatePlan(consumerId);
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

    private void toCreatePlan(long consumerId) {
        Intent intent = new Intent(this, CreatePlanActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("targetUserId", consumerId);
        startActivity(intent);
    }


    private void showRefundDialog(final long bountyId, final long userId, final double amount, final String target) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View contentView = View.inflate(this, R.layout.dialog_confirm_refund, null);
        TextView tvNotice = (TextView) contentView.findViewById(R.id.tv_notice);
        final EditText et = (EditText) contentView.findViewById(R.id.et_password);
        TextView tvConfirm = (TextView) contentView.findViewById(R.id.tv_confirm);
        TextView tvCancle = (TextView) contentView.findViewById(R.id.tv_cancel);
        tvNotice.setText(String.format("买家购买了方案，共支付%s元。\n买家申请退款，等待退款处理。", CommonUtils.getPriceString(amount)));
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Double a = Double.parseDouble(et.getText().toString().trim());
                    showConfirmDialog(bountyId, userId, a, target);
                    dialog.dismiss();
                } catch (Exception e) {
                    ToastUtil.getInstance(ProjectDetailActivity.this).showToast("请输入正确的金额");
                }

            }
        });
        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.setView(new EditText(ProjectDetailActivity.this));
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Window window = dialog.getWindow();
        window.setContentView(contentView);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = display.getWidth(); // 设置宽度
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }


    private void showConfirmDialog(final long bountyId, final long userId, final double amount, final String target) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View contentView = View.inflate(this, R.layout.dialog_confirm_password, null);
        final EditText et = (EditText) contentView.findViewById(R.id.et_password);
        TextView tvConfirm = (TextView) contentView.findViewById(R.id.tv_confirm);
        TextView tvCancle = (TextView) contentView.findViewById(R.id.tv_cancel);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserApi.veryfyPassword(et.getText().toString(), new HttpCallBack() {
                    @Override
                    public void doSuccess(Object result, String method) {
                        drawbackConfirm(bountyId, userId, amount, target);
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                        Toast.makeText(ProjectDetailActivity.this, "密码输入错误", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method, int code) {

                    }
                });

                dialog.dismiss();
            }
        });
        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.setView(new EditText(ProjectDetailActivity.this));
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Window window = dialog.getWindow();
        window.setContentView(contentView);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = display.getWidth(); // 设置宽度
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }


    public void drawbackConfirm(long orderId, long userId, double amount, String target) {

        JSONObject data = new JSONObject();
        try {
            data.put("userId", userId);
            data.put("memo", "");
            data.put("reason", "");
            data.put("amount", amount);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TravelApi.drawbackCus(orderId, "refundApprove", target, data, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                Toast.makeText(ProjectDetailActivity.this, "退款成功", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                Toast.makeText(ProjectDetailActivity.this, "退款失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    public class PlanAdapter extends BaseAdapter {

        ArrayList<BountyItemBean> list;
        ArrayList<Consumer> data;

        public ArrayList<Consumer> getList() {
            return data;
        }

        boolean isSeller;
        boolean clickable;
        ArrayList<Object> dataList;
        boolean isPaid;

        public PlanAdapter(ArrayList<BountyItemBean> Bountylist, ArrayList<Consumer> Consumerdata, boolean isSeller, boolean clickable, boolean isPaid) {
            this.list = Bountylist;
            this.data = Consumerdata;
            this.isSeller = isSeller;
            this.clickable = clickable;
            this.isPaid = isPaid;
            dataList = new ArrayList<>();
            initData();
        }

        private void initData() {
            dataList.addAll(list);

            for (Consumer consumer : data) {
                if (!contain(consumer)) dataList.add(consumer);
            }

        }

        private boolean contain(Consumer consumer) {
            for (BountyItemBean bean : list) {
                if (bean.getSeller().getSellerId() == consumer.getUserId()) return true;
            }
            return false;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public BountyItemBean isSub(long userId) {
            if (list == null) return null;
            long time = 0;
            BountyItemBean bountyItemBean = null;
            for (BountyItemBean bean : list) {
                if (bean.seller.getSellerId() == userId) {
                    if (bean.getCreateTime() > time) {
                        time = bean.getCreateTime();
                        bountyItemBean = bean;
                    }
                }
            }
            return bountyItemBean;
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
            Object o = getItem(position);

            if (o instanceof BountyItemBean) {


                final BountyItemBean bountyItemBean = (BountyItemBean) o;

                final Consumer bean = getConsumer(bountyItemBean);

                if (bountyItemBean.getSeller().user != null) {
                    holder.mTvName.setText(bountyItemBean.getSeller().user.getNickname());
                    Glide.with(mContext)
                            .load(bountyItemBean.getSeller().user.getAvatar().getUrl())
                            .placeholder(R.drawable.ic_default_picture)
                            .error(R.drawable.ic_default_picture)
                            .centerCrop()
                            .into(holder.mIvAvatar);
                }

                holder.mTvTimestamp.setText(String.format("在%s提交了方案", CommonUtils.getTimestampString(new Date(bountyItemBean.getCreateTime()))));
                holder.mIvState.setVisibility(View.VISIBLE);
                //  holder.mTvTimestamp.setText(DateFormat.format("yyyy-MM-dd", bountyItemBean.getUpdateTime()));
                if (isSeller) {
                    if (bountyItemBean.seller.getSellerId() == AccountManager.getInstance().getLoginAccount(mContext).getUserId()) {
                        holder.ll_container.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, CreatePlanActivity.class);
                                intent.putExtra("isDetail", true);
                                intent.putExtra("item", bountyItemBean);
                                intent.putExtra("id", bountyItemBean.getItemId());
                                if (bean != null) intent.putExtra("Consumer", bean);
                                startActivity(intent);
                            }
                        });
                    }

                } else {
                    holder.ll_container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, CreatePlanActivity.class);
                            intent.putExtra("isDetail", true);
                            intent.putExtra("isConsume", true);
                            intent.putExtra("isPaid", isPaid);
                            intent.putExtra("item", bountyItemBean);
                            intent.putExtra("id", bountyItemBean.getItemId());
                            if (bean != null) intent.putExtra("Consumer", bean);
                            startActivity(intent);
                        }
                    });
                }


            } else {
                final Consumer consumer = (Consumer) o;
                holder.mTvName.setText(consumer.getNickname());
                Glide.with(mContext)
                        .load(consumer.getAvatar().getUrl())
                        .placeholder(R.drawable.ic_default_picture)
                        .error(R.drawable.ic_default_picture)
                        .centerCrop()
                        .into(holder.mIvAvatar);

                holder.mTvTimestamp.setText("已接单");
                holder.mIvTalk.setVisibility(View.VISIBLE);
                holder.ll_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent talkIntent = new Intent(mContext, ChatActivity.class);
                        talkIntent.putExtra("friend_id", consumer.getUserId() + "");
                        talkIntent.putExtra("chatType", "single");
                        startActivity(talkIntent);
                    }
                });

            }


//
//            final Consumer bean = (Consumer) getItem(position);
//
//                holder.mTvName.setText(bean.getNickname());
//                Glide.with(mContext)
//                        .load(bean.getAvatar().getUrl())
//                        .placeholder(R.drawable.ic_default_picture)
//                        .error(R.drawable.ic_default_picture)
//                        .centerCrop()
//                        .into(holder.mIvAvatar);
//
//            final BountyItemBean bountyItemBean = isSub(bean.getUserId());
//            if (bountyItemBean!=null){
//
//                holder.mTvTimestamp.setText(String.format("在%s提交了方案",CommonUtils.getTimestampString(new Date(bountyItemBean.getCreateTime()))));
//                holder.mIvState.setVisibility(View.VISIBLE);
//                holder.mTvTimestamp.setText(DateFormat.format("yyyy-MM-dd", bountyItemBean.getUpdateTime()));
//                if (isSeller){
//                    if (bountyItemBean.seller.getSellerId()==AccountManager.getInstance().getLoginAccount(mContext).getUserId()){
//                        holder.ll_container.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(mContext, CreatePlanActivity.class);
//                                intent.putExtra("isDetail",true);
//                                intent.putExtra("item",bountyItemBean);
//                                intent.putExtra("id", bountyItemBean.getItemId());
//                                if (bean!=null)intent.putExtra("Consumer",bean);
//                                startActivity(intent);
//                            }
//                        });
//                    }
//
//                }else {
//                    holder.ll_container.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(mContext, CreatePlanActivity.class);
//                            intent.putExtra("isDetail",true);
//                            intent.putExtra("isConsume",true);
//                            intent.putExtra("item",bountyItemBean);
//                            intent.putExtra("id", bountyItemBean.getItemId());
//                            if (bean!=null)intent.putExtra("Consumer",bean);
//                            startActivity(intent);
//                        }
//                    });
//                }
//
//
//            }else {
//                holder.mTvTimestamp.setText("已接单");
//                holder.mIvTalk.setVisibility(View.VISIBLE);
//                holder.ll_container.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent talkIntent = new Intent(mContext, ChatActivity.class);
//                        talkIntent.putExtra("friend_id", bean.getUserId()+"");
//                        talkIntent.putExtra("chatType", "single");
//                        startActivity(talkIntent);
//                    }
//                });
//            }

            if (!clickable) holder.ll_container.setClickable(false);
            return convertView;
        }

        private Consumer getConsumer(BountyItemBean bean) {

            for (Consumer consumer : data) {

                if (consumer.getUserId() == bean.getSeller().getSellerId()) return consumer;
            }
            return null;
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
