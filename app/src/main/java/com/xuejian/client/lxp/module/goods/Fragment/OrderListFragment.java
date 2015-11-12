package com.xuejian.client.lxp.module.goods.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.common.imageloader.UILUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yibiao.qin on 2015/11/11.
 */
public class OrderListFragment extends PeachBaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout mSwipeRefreshWidget;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(
                R.layout.fragment_order_list, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerview);
        mSwipeRefreshWidget = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshWidget.setOnRefreshListener(this);
        mSwipeRefreshWidget.setColorSchemeResources(R.color.app_theme_color);
        setupRecyclerView(rv);
        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            list.add("" + i);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new OrderListAdapter(getActivity(),
                list));
    }

    @Override
    public void onRefresh() {
        Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshWidget.setRefreshing(false);
            }
        }, 2000);
    }

    class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

        private List<String> mValues;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final ImageView mImageView;

            public ViewHolder(View view) {
                super(view);
                mImageView = (ImageView) view.findViewById(R.id.iv_goods_img);
            }
        }

        public OrderListAdapter(Context context, List<String> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            ImageLoader.getInstance().displayImage("http://taozi-uploads.qiniudn.com/avt_100004_1443601212983.jpg", holder.mImageView, UILUtils.getDefaultOption());
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}
