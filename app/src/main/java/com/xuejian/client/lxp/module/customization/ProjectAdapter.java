package com.xuejian.client.lxp.module.customization;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.BountiesBean;
import com.xuejian.client.lxp.common.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2016/3/30.
 */
public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(View view, int position, long id, boolean expire);
    }

    private OnItemClickListener listener;
    private List<BountiesBean> mValues;
    private Context mContext;
    private int type;

    static

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_avatar)
        ImageView ivAvatar;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_timestamp)
        TextView tvTimestamp;
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
        @Bind(R.id.tv_state)
        TextView tvState;
        @Bind(R.id.ll_container)
        LinearLayout ll_container;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public ProjectAdapter(Context fragment, int type) {
        mContext = fragment;
        mValues = new ArrayList<>();
        this.type = type;
    }
    public Object getItem(int position) {
        return mValues.get(position);
    }

    public List<BountiesBean> getDataList() {
        return mValues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final BountiesBean bean = (BountiesBean) getItem(position);
        if (bean.consumer!=null){
            holder.tvName.setText(bean.consumer.getNickname());
            Glide.with(mContext)
                    .load(bean.consumer.getAvatar().getUrl())
                    .placeholder(R.drawable.ic_default_picture)
                    .error(R.drawable.ic_default_picture)
                    .centerCrop()
                    .into(holder.ivAvatar);
        }

        holder.tvTimestamp.setText(String.format("在%s发布了需求",CommonUtils.getTimestampString(new Date(bean.createTime))));
        StringBuilder desc = new StringBuilder();
        for (int i = 0; i < bean.getDestination().size(); i++) {
            if (i!=0)desc.append("、");
            desc.append(bean.getDestination().get(i).zhName);
        }
        holder.tvProjectInfo1.setText(String.format("[%s]",desc));
        holder.tvProjectTime.setText(String.format(Locale.CHINA,"%d日游",bean.getTimeCost()));
        holder.tvProjectInfo2.setText(bean.getService());
        holder.tvProjectCount.setText(String.format(Locale.CHINA,"已有%d位商家抢单",bean.takersCnt));


        String budget = String.format("定金%s元",CommonUtils.getPriceString(bean.getBountyPrice()));
        String total = String.format("总预算%s元",CommonUtils.getPriceString(bean.getBudget()));

        SpannableString budgetString = new SpannableString(budget);
        budgetString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.price_color)),2,budget.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString totalString = new SpannableString(total);
        totalString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.price_color)),3,total.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder price = new SpannableStringBuilder();
        price.append(budgetString).append("  ").append(totalString);
        holder.tvProjectPrice.setText(price);

        holder.ll_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    listener.onItemClick(v,position,bean.getItemId(),false);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}


