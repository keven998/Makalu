package com.xuejian.client.lxp.module.customization;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.bumptech.glide.Glide;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ProjectEvent;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.widget.ListViewForScrollView;
import com.xuejian.client.lxp.module.toolbox.StrategyListActivity;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2016/4/6.
 */
public class CreatePlanActivity extends PeachBaseActivity {


    private static final int SELECTPLAN = 100;
    @Bind(R.id.et_message)
    EditText mEtMessage;
    @Bind(R.id.unit_price)
    TextView mUnitPrice;
    @Bind(R.id.tv_total_price)
    EditText mTvTotalPrice;
    @Bind(R.id.tv_setOff_city)
    TextView mTvSetOffCity;
    @Bind(R.id.iv1)
    ImageView mIv1;
    @Bind(R.id.rl_plan)
    RelativeLayout mRlPlan;
    @Bind(R.id.iv_nav_back)
    ImageView mIvNavBack;
    @Bind(R.id.tv_title_bar_title)
    TextView mTvTitleBarTitle;
    @Bind(R.id.tv_action0)
    TextView mTvAction0;
    @Bind(R.id.ll_trade_action0)
    LinearLayout mLlTradeAction0;
    @Bind(R.id.tv_cancel_action)
    TextView mTvCancelAction;
    @Bind(R.id.tv_pay)
    TextView mTvPay;
    @Bind(R.id.ll_trade_action1)
    LinearLayout mLlTradeAction1;
    @Bind(R.id.plan_list)
    ListViewForScrollView mPlanList;

    PlanAdapter mPlanAdapter;
    ArrayList<StrategyBean> mSelected = new ArrayList<>();
    long id;
    boolean isDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_create);
        ButterKnife.bind(this);
        id = getIntent().getLongExtra("id",-1);
        isDetail = getIntent().getBooleanExtra("",false);
        if (isDetail){
            initDetailView();
            getData(id);
        }else {
            initView();
        }

    }

    private void getData(long id) {




    }

    public void initDetailView(){
        mLlTradeAction0.setVisibility(View.GONE);
        mLlTradeAction1.setVisibility(View.VISIBLE);
        mEtMessage.setEnabled(false);
        mTvTotalPrice.setEnabled(false);
    }
    private void initView() {
        mRlPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreatePlanActivity.this, StrategyListActivity.class);
                intent.putExtra("ProjectCreate", true);
                intent.putExtra("userId", AccountManager.getInstance().getLoginAccount(CreatePlanActivity.this).getUserId() + "");
                startActivityForResult(intent, SELECTPLAN);
            }
        });
        mTvAction0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPlan();
            }
        });
        mPlanAdapter = new PlanAdapter(this);
        mPlanList.setAdapter(mPlanAdapter);
    }

    private void submitPlan() {
        try {
            Double.parseDouble(mTvTotalPrice.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.getInstance(this).showToast("请输入正确的金额");
            return;
        }
        if (TextUtils.isEmpty(mEtMessage.getText().toString())) {
            ToastUtil.getInstance(this).showToast("请输入方案内容");
            return;
        }

        if (id>0){
            JSONArray array = new JSONArray();

            for (StrategyBean bean : mSelected) {
                array.put(bean.id);
            }
            TravelApi.submitPlan(id, mEtMessage.getText().toString(), Double.parseDouble(mTvTotalPrice.getText().toString().trim()), array, new HttpCallBack<String>() {

                @Override
                public void doSuccess(String result, String method) {
                    ToastUtil.getInstance(mContext).showToast("您的方案已提交成功，请与买家联系~");
                    submitSuccess();
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

    private void submitSuccess() {
        mLlTradeAction0.setVisibility(View.GONE);
        mLlTradeAction1.setVisibility(View.VISIBLE);
        mEtMessage.setEnabled(false);
        mTvTotalPrice.setEnabled(false);
        mTvCancelAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtMessage.setEnabled(true);
                mTvTotalPrice.setEnabled(true);
            }
        });
        mTvPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent talkIntent = new Intent(mContext, ChatActivity.class);
                talkIntent.putExtra("friend_id", 100004+"");
                talkIntent.putExtra("chatType", "single");
                startActivity(talkIntent);
            }
        });
        EventBus.getDefault().post(new ProjectEvent("refresh"));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECTPLAN) {
                ArrayList<StrategyBean>list = data.getParcelableArrayListExtra("selected");
                if (list!=null){
                    mSelected = list;
                    mPlanAdapter.getData().clear();
                    mPlanAdapter.getData().addAll(list);
                    mPlanAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private class PlanAdapter extends BaseAdapter {
        boolean isOwner;
        private ArrayList<StrategyBean> data;
        private Context context;

        public PlanAdapter(Context context) {
            this.context = context;
            data = new ArrayList<>();
        }

        public ArrayList<StrategyBean> getData() {
            return data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_plane_item, null);
                holder = new ViewHolder();
                holder.plane_spans = (TextView) convertView.findViewById(R.id.plane_spans);
                holder.city_hasGone = (TextView) convertView.findViewById(R.id.city_hasGone);
                holder.plane_title = (TextView) convertView.findViewById(R.id.plane_title);
                holder.create_time = (TextView) convertView.findViewById(R.id.create_time);
                holder.mCheck = (TextView) convertView.findViewById(R.id.sign_up);
                holder.mDelete = (TextView) convertView.findViewById(R.id.delete);
                holder.plane_pic = (ImageView) convertView.findViewById(R.id.plane_pic);
                holder.travel_hasGone = (ImageView) convertView.findViewById(R.id.travel_hasGone);
                holder.rl_send = (RelativeLayout) convertView.findViewById(R.id.rl_send);
                holder.btn_send =  (CheckedTextView) convertView.findViewById(R.id.btn_send);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final StrategyBean itemData = (StrategyBean) getItem(position);
            holder.plane_spans.setText(String.format("%s天", String.valueOf(itemData.dayCnt)));
            holder.city_hasGone.setText(itemData.summary);
            holder.plane_title.setText(itemData.title);
            if (itemData.images != null && itemData.images.size() > 0) {
                    Glide.with(mContext)
                            .load(itemData.images.get(0).url)
                            .placeholder(R.drawable.ic_default_picture)
                            .error(R.drawable.ic_default_picture)
                            .centerCrop()
                            .into(holder.plane_pic);

            } else {
                Glide.with(mContext)
                        .load("")
                        .placeholder(R.drawable.ic_default_picture)
                        .error(R.drawable.ic_default_picture)
                        .centerCrop()
                        .into(holder.plane_pic);
            }
            holder.create_time.setText("创建：" + new SimpleDateFormat("yyyy-MM-dd").format(new Date(itemData.updateTime)));
            holder.travel_hasGone.setVisibility(View.GONE);

            holder.mDelete.setVisibility(View.GONE);
            holder.mCheck.setVisibility(View.GONE);
//            if (isShare) {
//                holder.mDelete.setVisibility(View.GONE);
//                holder.mCheck.setVisibility(View.GONE);
//                holder.rl_send.setVisibility(View.VISIBLE);
//            }
            holder.rl_send.setVisibility(View.VISIBLE);
            holder.btn_send.setText("删除");
            holder.rl_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.remove(itemData);
                    notifyDataSetChanged();

                }
            });


            return convertView;
        }

        public class ViewHolder {
            TextView plane_spans;
            TextView tv_day;
            TextView city_hasGone;
            TextView plane_title;
            TextView create_time;
            TextView mDelete;
            TextView mCheck;
            CheckedTextView btn_send;
            ImageView travel_hasGone;
            RelativeLayout rl_send;
            private LinearLayout swipe_ll;
            ImageView plane_pic;
        }

    }


}
