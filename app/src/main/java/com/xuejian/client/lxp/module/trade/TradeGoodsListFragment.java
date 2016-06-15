package com.xuejian.client.lxp.module.trade;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.http.HttpCallBack;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.SimpleCommodityBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.module.goods.CommodityDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yibiao.qin on 2015/11/11.
 */
public class TradeGoodsListFragment extends PeachBaseFragment {
    // SwipeRefreshLayout mSwipeRefreshWidget;
    private int type;
    public static final int PUB = 1;
    public static final int REVIEW = 2;
    public static final int DISABLE = 3;
   // public static final int AVAILABLE = 4;
   // public static final int TO_REVIEW = 5;
    OrderListAdapter adapter;
    XRecyclerView recyclerView;
    TextView empty;
    Handler handler;
    private static int COUNT = 5;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
        if (handler == null)handler = new Handler();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //   DialogManager.getInstance().showModelessLoadingDialog(getActivity());
        View view = (View) inflater.inflate(
                R.layout.fragment_order_list, container, false);
        recyclerView = (XRecyclerView) view.findViewById(R.id.recyclerview);
        empty = (TextView) view.findViewById(R.id.empty_view);
//        mSwipeRefreshWidget = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
//        mSwipeRefreshWidget.setOnRefreshListener(this);
//        mSwipeRefreshWidget.setColorSchemeResources(R.color.app_theme_color);
        setupRecyclerView(recyclerView);
        recyclerView.setRefreshProgressStyle(ProgressStyle.SysProgress);
        recyclerView.setLoadingMoreEnabled(true);
        //   DialogManager.getInstance().showModelessLoadingDialog(getActivity());

        empty.setText("暂无数据");

        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                recyclerView.setLoadingMoreEnabled(true);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkState(0, true);
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkState(adapter.getItemCount(), false);
                    }
                }, 1000);
            }
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        //  DialogManager.getInstance().showModelessLoadingDialog(getActivity());
        //   mSwipeRefreshWidget.setRefreshing(true);
        recyclerView.setLoadingMoreEnabled(true);
        checkState(0,true);
    }

    public void checkState(int start ,boolean refresh){
        switch (type) {
            case PUB:
                getCommodities("pub", start, COUNT, refresh);
                break;
            case REVIEW:
                getCommodities("review", start, COUNT, refresh);
                break;
            case DISABLE:
                getCommodities("disabled", start, COUNT, refresh);
                break;
            default:
                //     if (mSwipeRefreshWidget.isRefreshing()) mSwipeRefreshWidget.setRefreshing(false);
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                break;
        }
    }

    public void getCommodities(String status,int start,int count, final boolean refresh) {
        long userId = AccountManager.getInstance().getLoginAccount(getActivity()).getUserId();

        TravelApi.getCommodityList(String.valueOf(userId), status, null, null,"createTime", "desc",  String.valueOf(start), String.valueOf(count),false, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<SimpleCommodityBean> list = CommonJson4List.fromJson(result, SimpleCommodityBean.class);
                if (refresh) adapter.getDataList().clear();
                adapter.getDataList().addAll(list.result);
                adapter.notifyDataSetChanged();
                if (list.result.size() > 0) empty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                //        if (mSwipeRefreshWidget.isRefreshing()) mSwipeRefreshWidget.setRefreshing(false);
                recyclerView.refreshComplete();
                recyclerView.loadMoreComplete();

                if (list.result.size() >= COUNT) {

                } else {
//                    goodsList.noMoreLoading();
                    recyclerView.setLoadingMoreEnabled(false);
                }
                DialogManager.getInstance().dissMissModelessLoadingDialog();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                recyclerView.refreshComplete();
                recyclerView.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
                DialogManager.getInstance().dissMissModelessLoadingDialog();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
            }
        });

    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);
        adapter = new OrderListAdapter(getActivity(), type);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id,boolean expire) {
                    Intent intent = new Intent(getActivity(), CommodityDetailActivity.class);
                    intent.putExtra("isSeller", true);
                    intent.putExtra("commodityId", id);
                    intent.putExtra("expire",expire);
                    startActivity(intent);
            }
        });
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position, long id,boolean expire);
    }

     class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {


        private OnItemClickListener listener;
        private List<SimpleCommodityBean> mValues;
        private Context mContext;
        private int type;

        public class ViewHolder extends RecyclerView.ViewHolder {

            public final ImageView mImageView;
            public final TextView tvGoodsName;
            public final TextView tvGoodsId;
            public final TextView tvGoodsPrice;
            public final RelativeLayout rlItem;

            public final TextView tvAction;

            public ViewHolder(View view) {
                super(view);

                mImageView = (ImageView) view.findViewById(R.id.iv_goods_img);
                tvGoodsName = (TextView) view.findViewById(R.id.tv_goods_name);
                tvGoodsId = (TextView) view.findViewById(R.id.tv_goods_id);
                tvGoodsPrice = (TextView) view.findViewById(R.id.tv_goods_price);
                rlItem = (RelativeLayout) view.findViewById(R.id.rl_item);
                tvAction = (TextView) view.findViewById(R.id.tv_action);
            }
        }

        public OrderListAdapter(Context context, int type) {
            mContext = context;
            mValues = new ArrayList<>();
            this.type = type;
        }

        public Object getItem(int position) {
            return mValues.get(position);
        }

        public List<SimpleCommodityBean> getDataList() {
            return mValues;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_trade_goods_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final SimpleCommodityBean bean = (SimpleCommodityBean) getItem(position);

            switch (bean.status) {
                case "pub":
                    holder.tvAction.setText("下架");
                    break;
                case "review":
                    holder.tvAction.setVisibility(View.GONE);
                    break;
                case "disabled":
                    holder.tvAction.setText("上架");
                    break;
                default:
                    break;
            }
            holder.tvAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (bean.status) {
                        case "pub":
                            editCommodity("disabled",bean.getCommodityId(),"商品已下架","pub");
                            break;
                        case "disabled":
                            if (bean.expire){
                                Toast.makeText(mContext,"商品已超出最大可用日期，请重新编辑后上架",Toast.LENGTH_LONG).show();
                            }else {
                                editCommodity("pub",bean.getCommodityId(),"商品已发布","disabled");
                            }
                            break;
                        default:
                            break;
                    }
                }
            });
            holder.tvGoodsName.setText(bean.getTitle());
            holder.tvGoodsId.setText(String.format("商品编号:%d", bean.getCommodityId()));
            holder.tvGoodsPrice.setText(String.format("价格:¥%s起", CommonUtils.getPriceString(bean.getPrice())));
            if (bean.getCover() != null) {
                ImageLoader.getInstance().displayImage(bean.getCover().getUrl(), holder.mImageView, UILUtils.getDefaultOption());
            } else {
                ImageLoader.getInstance().displayImage("", holder.mImageView, UILUtils.getDefaultOption());
            }

            holder.rlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(holder.itemView, position, bean.getCommodityId(),bean.expire);
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
        public void editCommodity(String status,long commodityId,final String notice,final String refreshType){
            TravelApi.editCommodityStatus(commodityId, status, new HttpCallBack<String>() {

                @Override
                public void doSuccess(String result, String method) {
                    Toast.makeText(mContext, notice, Toast.LENGTH_LONG).show();

                    recyclerView.doRefresh();
                  //  getCommodities(refreshType, 0, COUNT, true);
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {
                    Toast.makeText(mContext, "操作失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });
        }
    }


}
