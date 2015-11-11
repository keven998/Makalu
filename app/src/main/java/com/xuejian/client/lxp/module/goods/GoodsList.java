package com.xuejian.client.lxp.module.goods;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.common.widget.TagView.Tag;
import com.xuejian.client.lxp.common.widget.TagView.TagListView;
import com.xuejian.client.lxp.module.dest.adapter.StringSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yibiao.qin on 2015/10/17.
 */
public class GoodsList extends PeachBaseActivity {

    @InjectView(R.id.tv_title_back)
    TextView tvTitleBack;
    @InjectView(R.id.type_spinner)
    Spinner typeSpinner;
    @InjectView(R.id.sort_spinner)
    Spinner sortSpinner;
    @InjectView(R.id.lv_poi_list)
    PullToRefreshListView goodsList;
    @InjectView(R.id.iv_toTop)
    ImageView toTop;
    GoodsListAdapter adapter;
    private int[] lebelColors = new int[]{
            R.drawable.all_light_green_label,
            R.drawable.all_light_red_label,
            R.drawable.all_light_perple_label,
            R.drawable.all_light_blue_label,
            R.drawable.all_light_yellow_label
    };
    private final List<Tag> mTags = new ArrayList<Tag>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_list);
        ButterKnife.inject(this);
        goodsList.setPullLoadEnabled(false);
        goodsList.setPullRefreshEnabled(false);
        goodsList.setScrollLoadEnabled(true);
        goodsList.setHasMoreData(false);
        adapter = new GoodsListAdapter(mContext);
        goodsList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            }
        });
        tvTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ArrayList<String> TypeList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TypeList.add("类型 " + i);
        }
        ArrayList<String> SortList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            SortList.add("排序 " + i);
        }
        StringSpinnerAdapter mTypeListAdapter = new StringSpinnerAdapter(mContext, TypeList);
        typeSpinner.setAdapter(mTypeListAdapter);
        typeSpinner.setSelection(0, true);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        StringSpinnerAdapter mSortListAdapter = new StringSpinnerAdapter(mContext, SortList);
        sortSpinner.setAdapter(mSortListAdapter);
        sortSpinner.setSelection(0, true);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        initData();
        goodsList.getRefreshableView().setAdapter(adapter);
        toTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodsList.getRefreshableView().setSelection(0);
            }
        });
    }

    private void initData() {
        int lastColor = new Random().nextInt(4);
        for (int i = 0; i < 3; i++) {
            Tag tag = new Tag();
            tag.setTitle("服务" + i);
            tag.setId(i);
            tag.setBackgroundResId(lebelColors[lastColor]);
            //   tag.setBackgroundResId(R.drawable.all_whitesolid_greenline);
            tag.setTextColor(R.color.white);
            mTags.add(tag);
            lastColor = getNextColor(lastColor);
        }
    }

    public int getNextColor(int currentcolor) {
        Random random = new Random();
        int nextValue = random.nextInt(4);
        if (nextValue == 0) {
            nextValue++;
        }
        return (nextValue + currentcolor) % 5;
    }

    private class GoodsListAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<PoiDetailBean> mDataList;

        private DisplayImageOptions picOptions;

        public GoodsListAdapter(Context context) {
            mContext = context;
            mDataList = new ArrayList<PoiDetailBean>();
            picOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                    .resetViewBeforeLoading(true)
                    .showImageOnFail(R.drawable.ic_default_picture)
                    .showImageOnLoading(R.drawable.ic_default_picture)
                    .showImageForEmptyUri(R.drawable.ic_default_picture)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        }

        public ArrayList<PoiDetailBean> getDataList() {
            return mDataList;
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_goods_list, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ImageLoader.getInstance().displayImage("http://images.taozilvxing.com/097b5bade72c60272634a5127f1e8152?imageView2/2/w/960", holder.ivGoods, picOptions);
            holder.tvGoodsPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvGoodsPrice.getPaint().setAntiAlias(true);
            holder.tvGoodsService.removeAllViews();
            holder.tvGoodsService.setmTagViewResId(R.layout.goods_tag);
            holder.tvGoodsService.addTags(mTags);
            return convertView;
        }

        /**
         * This class contains all butterknife-injected Views & Layouts from layout file 'item_goods_list.xml'
         * for easy to all layout elements.
         *
         * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
         */
    }

    static class ViewHolder {
        @InjectView(R.id.iv_poi_img)
        ImageView ivGoods;
        @InjectView(R.id.tv_goods_name)
        TextView tvGoodsName;
        @InjectView(R.id.tv_goods_detail)
        TextView tvGoodsDetail;
        @InjectView(R.id.tv_goods_service)
        TagListView tvGoodsService;
        @InjectView(R.id.tv_goods_comment)
        TextView tvGoodsComment;
        @InjectView(R.id.tv_goods_sales)
        TextView tvGoodsSales;
        @InjectView(R.id.tv_goods_current_price)
        TextView tvGoodsCurrentPrice;
        @InjectView(R.id.tv_goods_price)
        TextView tvGoodsPrice;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
