package com.xuejian.client.lxp.module.customization;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.BountiesBean;

import java.util.ArrayList;
import java.util.List;

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

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public ProjectAdapter(Context context, int type) {
        mContext = context;
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

        BountiesBean bean = (BountiesBean) getItem(position);
        holder.tvName.setText(bean.getContact().get(0).getSurname());

    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}


