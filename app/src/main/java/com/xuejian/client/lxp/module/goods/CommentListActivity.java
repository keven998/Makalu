package com.xuejian.client.lxp.module.goods;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CommentDetailBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.GridViewForListView;
import com.xuejian.client.lxp.common.widget.glide.GlideCircleTransform;
import com.xuejian.client.lxp.common.widget.twowayview.layout.DividerItemDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.techery.properratingbar.ProperRatingBar;

/**
 * Created by yibiao.qin on 2016/1/30.
 */
public class CommentListActivity extends PeachBaseActivity {

    @Bind(R.id.tv_title_back)
    TextView tvTitleBack;
    @Bind(R.id.tv_list_title)
    TextView tvListTitle;
    @Bind(R.id.lv_poi_list)
    XRecyclerView lvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);
        ButterKnife.bind(this);
        tvTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvListTitle.setText("全部评价");
        long commodityId = getIntent().getLongExtra("commodityId",-1);
        lvList.setPullRefreshEnabled(false);

        initData(commodityId);
    }

    private void initData(long commodityId) {
        if (commodityId<0)return;
        TravelApi.getCommentList(commodityId, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<CommentDetailBean> list = CommonJson4List.fromJson(result, CommentDetailBean.class);

                bindView(list.result);

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

    }

    private void bindView(List<CommentDetailBean> result) {
        lvList.setLayoutManager(new LinearLayoutManager(this));
        lvList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        GoodsListAdapter adapter = new GoodsListAdapter(this);
        adapter.getDataList().addAll(result);
        lvList.setAdapter(adapter);

    }

    private class GoodsListAdapter extends RecyclerView.Adapter<ViewHolder> {
        private Activity mContext;
        private ArrayList<CommentDetailBean> mDataList;

        public GoodsListAdapter(Activity context) {
            mContext = context;
            mDataList = new ArrayList<CommentDetailBean>();
        }


        public ArrayList<CommentDetailBean> getDataList() {
            return mDataList;
        }


        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_comment, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            CommentDetailBean bean = (CommentDetailBean) getItem(position);
            holder.rbComment.setRating((int)bean.getRating());
            holder.tvComment.setText(bean.getContents());
            holder.tvTimestamp.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(bean.getCreateTime())));
            if (bean.images.size()>0){
                holder.gvCommentPic.setAdapter(new CommentPicAdapter(CommentListActivity.this));
            }
            if (bean.getUser()!=null){
                if (bean.getUser().getAvatar()!=null){
                    Glide.with(mContext)
                            .load(bean.getUser().getAvatar().url)
                            .placeholder(R.drawable.ic_home_more_avatar_unknown_round)
                            .error(R.drawable.ic_home_more_avatar_unknown_round)
                            .centerCrop()
                            .transform(new GlideCircleTransform(mContext))
                            .into(holder.ivAvatar);
                }

                holder.tvName.setText(bean.getUser().getNickname());
            }

        }


        @Override
        public int getItemCount() {
            return mDataList.size();
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_avatar)
        ImageView ivAvatar;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_timestamp)
        TextView tvTimestamp;
        @Bind(R.id.rb_comment)
        ProperRatingBar rbComment;
        @Bind(R.id.tv_comment)
        TextView tvComment;
        @Bind(R.id.gv_comment_pic)
        GridViewForListView gvCommentPic;
        @Bind(R.id.tv_package)
        TextView tvPackage;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public  class CommentPicAdapter extends BaseAdapter {


        Activity activity;

        public CommentPicAdapter(Activity activity) {
            this.activity = activity;
        }

        @Override
        public int getCount() {
                return 8;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(activity).inflate(R.layout.all_pics_cell, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ViewGroup.LayoutParams layoutParams = holder.allPicsCellId.getLayoutParams();
            layoutParams.width = (CommonUtils.getScreenWidth(activity)-75) / 6;
            layoutParams.height = (CommonUtils.getScreenWidth(activity)-75) / 6;
            holder.allPicsCellId.setLayoutParams(layoutParams);
            Glide.with(mContext)
                    .load("http://7sbm17.com1.z0.glb.clouddn.com/commodity/images/f074adb29e1d39a184a02320a3aff555")
                    .placeholder(R.drawable.ic_default_picture)
                    .error(R.drawable.ic_default_picture)
                    .centerCrop()
                    .into(holder.allPicsCellId);
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.all_pics_cell_id)
            ImageView allPicsCellId;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

}
