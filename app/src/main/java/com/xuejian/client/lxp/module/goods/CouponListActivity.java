package com.xuejian.client.lxp.module.goods;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CouponBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.db.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2016/2/18.
 */
public class CouponListActivity extends PeachBaseActivity {

    @Bind(R.id.tv_title_back)
    TextView tvTitleBack;
    @Bind(R.id.tv_list_title)
    TextView tvListTitle;
    @Bind(R.id.lv_poi_list)
    XRecyclerView lvList;
    @Bind(R.id.empty_view)
    LinearLayout emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_list);
        ButterKnife.bind(this);
        tvTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lvList.setPullRefreshEnabled(false);
        User user = AccountManager.getInstance().getLoginAccount(this);
        initData(user.getUserId());
    }

    private void initData(long userId) {
        TravelApi.getCouponList(userId, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<CouponBean> list = CommonJson4List.fromJson(result, CouponBean.class);
                if (list.result.size() > 0) {
                    bindView(list.result);
                } else {
                    emptyView.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                emptyView.setVisibility(View.VISIBLE);
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

    }

    private void bindView(List<CouponBean> result) {
        lvList.setLayoutManager(new LinearLayoutManager(this));
//        lvList.addItemDecoration(new DividerItemDecoration(this,
//                DividerItemDecoration.VERTICAL_LIST));


        ArrayList<CouponBean> availableList = new ArrayList<>();
        boolean createOrder = getIntent().getBooleanExtra("createOrder", false);
        double price = getIntent().getDoubleExtra("price", 0);
        String id = getIntent().getStringExtra("id");
        GoodsListAdapter adapter = new GoodsListAdapter(this, createOrder,id);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        if (createOrder) {
            for (CouponBean bean : result) {
                Date d = null;
                try {
                    d= simpleDateFormat.parse(bean.getExpire());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (price>=bean.getThreshold()&&d!=null&&(d.after(date)||SampleDecorator.checkDate(d, date))){
                    availableList.add(bean);
                }
            }

            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position, CouponBean id) {
                    Intent intent = new Intent();
                    intent.putExtra("coupon", id);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        } else {
            for (CouponBean bean : result) {
                Date d = null;
                try {
                    d= simpleDateFormat.parse(bean.getExpire());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (d!=null&&(d.after(date)||SampleDecorator.checkDate(d,date))){
                    availableList.add(bean);
                }
            }
        }
        adapter.getDataList().addAll(availableList);
        lvList.setAdapter(adapter);
        if (availableList.size()==0)emptyView.setVisibility(View.VISIBLE);
    }

    interface OnItemClickListener {
        void onItemClick(View view, int position, CouponBean id);
    }

    private class GoodsListAdapter extends RecyclerView.Adapter<ViewHolder> {
        private Activity mContext;
        private ArrayList<CouponBean> mDataList;
        private boolean isCheckable;
        private OnItemClickListener listener;
        private String selectedId;
        public GoodsListAdapter(Activity context, boolean isCheckable,String selectedId) {
            mContext = context;
            this.isCheckable = isCheckable;
            mDataList = new ArrayList<CouponBean>();
            this.selectedId = selectedId;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        public ArrayList<CouponBean> getDataList() {
            return mDataList;
        }


        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_coupon_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final CouponBean bean = (CouponBean) getItem(position);
            ViewCompat.setElevation(holder.ll_container, 10);
            holder.ll_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onItemClick(v, position, bean);
                }
            });
            holder.tvCouponTitle.setText(bean.title);
            holder.tvCouponType.setText(bean.getDesc());
            if (bean.getThreshold() > 0) {
                holder.bg.setBackgroundResource(R.drawable.icon_voupon);
                holder.tvCouponCondition.setText(String.format("满%s元可用", CommonUtils.getPriceString(bean.getThreshold())));
                holder.title.setText("代金券");
            } else {
                holder.bg.setBackgroundResource(R.drawable.icon_voupon_yellow);
                holder.tvCouponCondition.setText("无条件使用");
                holder.title.setText("现金券");
            }
            SpannableString string = new SpannableString("¥");
            string.setSpan(new AbsoluteSizeSpan(12, true), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            stringBuilder.append(string).append(CommonUtils.getPriceString(bean.getDiscount()));
            holder.tvCouponPrice.setText(stringBuilder);
            holder.tvCouponTimestamp.setText("有效期至: " + bean.getExpire());
            if (this.isCheckable){
                holder.ctv.setVisibility(View.VISIBLE);
                if (bean.getId().equals(selectedId)){
                    holder.ctv.setChecked(true);
                }
            }

        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

    }

    public static  class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_coupon_price)
        TextView tvCouponPrice;
        @Bind(R.id.tv_coupon_title)
        TextView tvCouponTitle;
        @Bind(R.id.tv_coupon_type)
        TextView tvCouponType;
        @Bind(R.id.tv_coupon_condition)
        TextView tvCouponCondition;
        @Bind(R.id.tv_coupon_timestamp)
        TextView tvCouponTimestamp;
        @Bind(R.id.ll_container)
        LinearLayout ll_container;
        @Bind(R.id.ctv_1)
        CheckedTextView ctv;
        @Bind(R.id.rl_bg)
        RelativeLayout bg;
        @Bind(R.id.tv_title)
        TextView title;
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
