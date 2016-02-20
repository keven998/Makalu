package com.xuejian.client.lxp.module.goods;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CommentDetailBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson4List;

import java.util.ArrayList;
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

        initData(100000);
    }

    private void initData(long commodityId) {
        TravelApi.getCommentList(commodityId, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<CommentDetailBean> list = CommonJson4List.fromJson(result, CommentDetailBean.class);
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

    private void bindView(List<CommentDetailBean> result) {
        lvList.setLayoutManager(new LinearLayoutManager(this));
//        lvList.addItemDecoration(new DividerItemDecoration(this,
//                DividerItemDecoration.VERTICAL_LIST));
        GoodsListAdapter adapter = new GoodsListAdapter(this,true);
        adapter.getDataList().addAll(result);
        lvList.setAdapter(adapter);

    }

    private class GoodsListAdapter extends RecyclerView.Adapter<ViewHolder> {
        private Activity mContext;
        private ArrayList<CommentDetailBean> mDataList;
        private boolean isCheckable;
        public GoodsListAdapter(Activity context,boolean isCheckable) {
            mContext = context;
            this.isCheckable = isCheckable ;
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
                    .inflate(R.layout.item_coupon_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            ViewCompat.setElevation(holder.ll_container,10);
            holder.ll_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.ctv.setChecked(!holder.ctv.isChecked());
                }
            });
        }

        @Override
        public int getItemCount() {
            return 10;
          //  return mDataList.size();
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
