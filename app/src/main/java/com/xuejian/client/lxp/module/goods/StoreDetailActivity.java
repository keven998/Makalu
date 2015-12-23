package com.xuejian.client.lxp.module.goods;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.TagView.Tag;
import com.xuejian.client.lxp.common.widget.TagView.TagListView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        String sellerId = getIntent().getStringExtra("sellerId");
        System.out.println("sellerId "+sellerId);
        findViewById(R.id.tv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final XRecyclerView recyclerView = (XRecyclerView) findViewById(R.id.ul_recyclerView);
        Adapter adapter = new Adapter(this);
        for (int i = 0; i < 20; i++) {
            adapter.getList().add("a");
        }
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
        recyclerView.addHeaderView(view);
        recyclerView.setAdapter(adapter);
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

        private ArrayList<String> list;
        private Context mContext;
        private int w;

        public Adapter(Context context) {
            this.mContext = context;
            list = new ArrayList<>();
            w = CommonUtils.getScreenWidth((Activity) mContext);
        }

        public ArrayList<String> getList() {
            return list;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final TextView tvCurrentPrice;
            public final TextView tvPrice;
            public final TextView tvSales;
            public final ImageView mImageView;
            public final TextView tvGoodsName;

            public ViewHolder(View itemView) {
                super(itemView);
                tvGoodsName = (TextView) itemView.findViewById(R.id.tv_goods_name);
                tvCurrentPrice = (TextView) itemView.findViewById(R.id.tv_goods_current_price);
                tvPrice = (TextView) itemView.findViewById(R.id.tv_goods_price);
                tvSales = (TextView) itemView.findViewById(R.id.tv_goods_sales);
                mImageView = (ImageView) itemView.findViewById(R.id.iv_goods_img);
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
        public void onBindViewHolder(ViewHolder holder, int position) {

            Object o = getItem(position);
            ViewGroup.LayoutParams layoutParams = holder.mImageView.getLayoutParams();
            int w1 = w / 2 - 20;
            int h1 = w1 * 2 / 3;
            layoutParams.width = w1;
            layoutParams.height = h1;
            ImageLoader.getInstance().displayImage("http://7sbm17.com1.z0.glb.clouddn.com/commodity/images/50d1b9ad960899382bc982e9818b688b", holder.mImageView);
            holder.mImageView.setLayoutParams(layoutParams);
            holder.tvSales.setText(String.format("销量:%d件", 1000));
            holder.tvPrice.setText(String.format("¥%s", String.valueOf((float) (Math.round(3453.87 * 10) / 10))));
            holder.tvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvPrice.getPaint().setAntiAlias(true);
            holder.tvCurrentPrice.setText(String.format("¥%s", String.valueOf((float) (Math.round(155.34 * 10) / 10))));
            holder.tvGoodsName.setText("港澳台7日游");
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
