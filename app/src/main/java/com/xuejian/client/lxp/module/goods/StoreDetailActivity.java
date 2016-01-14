package com.xuejian.client.lxp.module.goods;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.SimpleCommodityBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.TagView.Tag;
import com.xuejian.client.lxp.common.widget.TagView.TagListView;
import com.xuejian.client.lxp.module.RNView.ReactMainPage;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yibiao.qin on 2015/12/18.
 */
public class StoreDetailActivity extends PeachBaseActivity {

    private int[] lebelColors = new int[]{
            R.drawable.all_light_green_label,
            R.drawable.all_light_red_label,
            R.drawable.all_light_perple_label,
            R.drawable.all_light_blue_label,
            R.drawable.all_light_yellow_label
    };
    Adapter adapter;
    XRecyclerView recyclerView;
    RelativeLayout rlTalk;
    private static final int PAGE_SIZE = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        final String sellerId = getIntent().getStringExtra("sellerId");
        findViewById(R.id.tv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rlTalk = (RelativeLayout) findViewById(R.id.ll_trade_action0);
        recyclerView = (XRecyclerView) findViewById(R.id.ul_recyclerView);
        adapter = new Adapter(this);
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        View view = getLayoutInflater().inflate(R.layout.head_store_detail, null);
        TagListView langTag = (TagListView) view.findViewById(R.id.tag_lang);
        langTag.setmTagViewResId(R.layout.expert_tag);
        langTag.removeAllViews();
        langTag.addTags(initTagData());
        LinearLayout service = (LinearLayout) view.findViewById(R.id.ll_service);
        for (int i = 0; i < 3; i++) {
            TextView textView = (TextView) getLayoutInflater().inflate(R.layout.textview_service,null);
            textView.setText("服务");
            textView.setPadding(5,0,5,0);
            service.addView(textView);
        }
        recyclerView.setLoadingMoreEnabled(true);
        recyclerView.addHeaderView(view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                getData(sellerId, adapter.getItemCount(), PAGE_SIZE);
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(StoreDetailActivity.this, ReactMainPage.class);
                intent.putExtra("commodityId", id);
                startActivity(intent);
            }
        });
        getData(sellerId, 0, PAGE_SIZE);

        rlTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent talkIntent = new Intent(mContext, ChatActivity.class);
                talkIntent.putExtra("friend_id", sellerId);
                talkIntent.putExtra("chatType", "single");
                startActivity(talkIntent);
            }
        });

    }

    private void getData(String sellerId ,int start, int count) {
        if (sellerId==null)return;

        TravelApi.getSellerInfo(Long.parseLong(sellerId), new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

        TravelApi.getCommodityList(sellerId, null, null, null, null, String.valueOf(start), String.valueOf(count), new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<SimpleCommodityBean> list = CommonJson4List.fromJson(result,SimpleCommodityBean.class);

               int start = list.result.size();
                adapter.getList().addAll(list.result);
                adapter.notifyDataSetChanged();
                recyclerView.loadMoreComplete();
                if (start >= PAGE_SIZE) {
                    // goodsList.setHasMoreData(true);
                } else {
                    recyclerView.setLoadingMoreEnabled(false);
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private  List<Tag> initTagData() {
        List<Tag> mTags = new ArrayList<Tag>();
        int lastColor = new Random().nextInt(4);
        for (int i = 0; i < 3; i++) {
            Tag tag = new Tag();
            tag.setTitle("语言" + i);
            tag.setId(i);
            tag.setBackgroundResId(lebelColors[lastColor]);
           //    tag.setBackgroundResId(R.drawable.all_whitesolid_greenline);
            tag.setTextColor(R.color.white);
            mTags.add(tag);
            lastColor = getNextColor(lastColor);
        }
        return mTags;
    }


    public int getNextColor(int currentcolor) {
        Random random = new Random();
        int nextValue = random.nextInt(4);
        if (nextValue == 0) {
            nextValue++;
        }
        return (nextValue + currentcolor) % 5;
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private ArrayList<SimpleCommodityBean> list;
        private Context mContext;
        private int w;
        private OnItemClickListener listener;
        public Adapter(Context context) {
            this.mContext = context;
            list = new ArrayList<>();
            w = CommonUtils.getScreenWidth((Activity) mContext);
        }

        public ArrayList<SimpleCommodityBean> getList() {
            return list;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final TextView tvCurrentPrice;
            public final TextView tvPrice;
            public final TextView tvSales;
            public final ImageView mImageView;
            public final TextView tvGoodsName;
            public final LinearLayout llContainer;
            public ViewHolder(View itemView) {
                super(itemView);
                tvGoodsName = (TextView) itemView.findViewById(R.id.tv_goods_name);
                tvCurrentPrice = (TextView) itemView.findViewById(R.id.tv_goods_current_price);
                tvPrice = (TextView) itemView.findViewById(R.id.tv_goods_price);
                tvSales = (TextView) itemView.findViewById(R.id.tv_goods_sales);
                mImageView = (ImageView) itemView.findViewById(R.id.iv_goods_img);
                llContainer = (LinearLayout) itemView.findViewById(R.id.ll_container);
            }
        }

        public Object getItem(int pos) {
            return list.get(pos);
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_goods_grid, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            final SimpleCommodityBean bean = (SimpleCommodityBean) getItem(position);
            ViewGroup.LayoutParams layoutParams = holder.mImageView.getLayoutParams();
            int w1 = w / 2 - 20;
            int h1 = w1 * 2 / 3;
            layoutParams.width = w1;
            layoutParams.height = h1;
            if (bean.getCover()!=null){
                ImageLoader.getInstance().displayImage(bean.getCover().getUrl(), holder.mImageView);
            }else {
                ImageLoader.getInstance().displayImage("", holder.mImageView);
            }
            holder.mImageView.setLayoutParams(layoutParams);
            holder.tvSales.setText( bean.getSalesVolume()+"已售");
            holder.tvPrice.setText(String.format("¥%s", CommonUtils.getPriceString(bean.getMarketPrice())));
            holder.tvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvPrice.getPaint().setAntiAlias(true);
            holder.tvCurrentPrice.setText(String.format("¥%s", CommonUtils.getPriceString(bean.getPrice())));
            holder.tvGoodsName.setText(bean.getTitle());
            holder.llContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        listener.onItemClick(v,position,bean.getCommodityId());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position, long id);
    }
}
