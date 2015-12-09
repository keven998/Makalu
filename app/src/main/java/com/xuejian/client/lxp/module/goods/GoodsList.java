package com.xuejian.client.lxp.module.goods;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.SimpleCommodityBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.XDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.widget.TagView.Tag;
import com.xuejian.client.lxp.common.widget.TagView.TagListView;
import com.xuejian.client.lxp.module.RNView.ReactMainPage;

import java.util.ArrayList;
import java.util.Arrays;
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
    CheckedTextView typeSpinner;
    @InjectView(R.id.sort_spinner)
    CheckedTextView sortSpinner;
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

    private String[] sortType = new String[]{"价格","销量","好评率"};
    private String[] sortValue = new String[]{"salesVolume","price","price"};

    private String currentType;
    private static final int PAGE_SIZE = 15;
    private static int COUNT =15 ;
    private static int START ;
    String locId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_list);
        ButterKnife.inject(this);
        locId = getIntent().getStringExtra("id");

        goodsList.setPullLoadEnabled(false);
        goodsList.setPullRefreshEnabled(false);
        goodsList.setScrollLoadEnabled(true);
        goodsList.setHasMoreData(false);
        adapter = new GoodsListAdapter(mContext);
        goodsList.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(GoodsList.this, ReactMainPage.class);
                intent.putExtra("commodityId",id);
                startActivity(intent);
            }
        });

        goodsList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData(null, locId, currentType, null,START,COUNT,false);
            }
        });
        tvTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        getCategory(locId);
        getData(null,locId,null,null,0,15,true);
    }

    private void getCategory(String id) {
        TravelApi.getCategoryList(id, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson<CategoryBean> list = CommonJson.fromJson(result, GoodsList.CategoryBean.class);
                initCategoryData(list.result);

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

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

    public void getData(String sellerId,String localityId,String category,String sortBy, final int start,int count, final boolean fresh) {

        TravelApi.getCommodityList(sellerId, localityId, category, sortBy, null,String.valueOf(start) ,String.valueOf(count),new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<SimpleCommodityBean> list = CommonJson4List.fromJson(result, SimpleCommodityBean.class);

                if (fresh){
                    adapter.getDataList().clear();
                }
                START = list.result.size();
                adapter.getDataList().addAll(list.result);
                adapter.notifyDataSetChanged();
                goodsList.onPullUpRefreshComplete();
                 if (list.result.size()>= COUNT){
                     goodsList.setHasMoreData(true);
                 }else {
                     goodsList.setHasMoreData(false);
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

    private void initCategoryData(final CategoryBean bean) {
        ArrayList<String> tempList = new ArrayList<>();
        tempList.addAll(Arrays.asList(sortType));
        final ArrayList<String> SortList = tempList;

        final XDialog categoryDialog = new XDialog(GoodsList.this, R.layout.dialog_type_spinner, R.style.LocSelectDialog);
        final CategoryAdapter adapter = new CategoryAdapter(bean.category, GoodsList.this);
        typeSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeSpinner.setChecked(true);
                WindowManager.LayoutParams wlmp = categoryDialog.getWindow().getAttributes();
                wlmp.gravity = Gravity.TOP | Gravity.LEFT;
                final ListView lv = categoryDialog.getListView();
                lv.setAdapter(adapter);
                categoryDialog.show();
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //    lv.setSelection(headerPos.get(position));
                        currentType = bean.category.get(position);
                        typeSpinner.setText(currentType);
                        getData(null, locId, currentType, null, 0, 15, true);
                        typeSpinner.setChecked(false);
                        if (categoryDialog != null) categoryDialog.dismiss();
                    }
                });
            }
        });





        final XDialog sortDialog = new XDialog(GoodsList.this, R.layout.dialog_sort_spinner, R.style.LocSelectDialog);
        final CategoryAdapter sortAdapter = new CategoryAdapter(SortList, GoodsList.this);
        sortSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortSpinner.setChecked(true);
                WindowManager.LayoutParams wlmp = sortDialog.getWindow().getAttributes();
                wlmp.gravity = Gravity.TOP | Gravity.RIGHT;
                final ListView lv = sortDialog.getListView();
                lv.setAdapter(sortAdapter);
                sortDialog.show();
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //    lv.setSelection(headerPos.get(position));
                        sortSpinner.setText(SortList.get(position));
                        getData(null, locId, currentType, sortValue[position], 0, 15, true);
                        sortSpinner.setChecked(false);
                        if (sortDialog != null) sortDialog.dismiss();
                    }
                });
            }
        });
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
        private ArrayList<SimpleCommodityBean> mDataList;

        private DisplayImageOptions picOptions;

        public GoodsListAdapter(Context context) {
            mContext = context;
            mDataList = new ArrayList<SimpleCommodityBean>();
            picOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                    .resetViewBeforeLoading(true)
                    .showImageOnFail(R.drawable.ic_default_picture)
                    .showImageOnLoading(R.drawable.ic_default_picture)
                    .showImageForEmptyUri(R.drawable.ic_default_picture)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        }

        public ArrayList<SimpleCommodityBean> getDataList() {
            return mDataList;
        }

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mDataList.get(position).getCommodityId();
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
            SimpleCommodityBean bean = (SimpleCommodityBean) getItem(position);
            ImageLoader.getInstance().displayImage(bean.getCover().getUrl(), holder.ivGoods, picOptions);
            holder.tvGoodsName.setText(bean.getTitle());
            holder.tvGoodsCurrentPrice.setText("¥" + String.valueOf((double) Math.round(bean.getPrice() * 10 / 10)));
            holder.tvGoodsPrice.setText("¥" + String.valueOf((double) Math.round(bean.getMarketPrice() * 10 / 10)));
            holder.tvGoodsPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvGoodsPrice.getPaint().setAntiAlias(true);
            holder.tvGoodsSales.setText("销量:" + String.valueOf(bean.getSalesVolume()));
            holder.tvGoodsComment.setText(bean.getRating() * 100 + "%满意");
            holder.tvStoreName.setText(bean.getSeller().getName());
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
        @InjectView(R.id.tv_store_name)
        TextView tvStoreName;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public static class CategoryBean {
        public ArrayList<String> category;
    }

    public class CategoryAdapter extends BaseAdapter {
        private ArrayList<String> list;
        private Context mContext;

        public CategoryAdapter(ArrayList<String> list, Context context) {
            this.list = list;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.loc_cell, null);
            }
            TextView days_title = (TextView) convertView.findViewById(R.id.days_title);
            if (typeSpinner.getText().toString().equals(getItem(position).toString()))
                days_title.setTextColor(getResources().getColor(R.color.app_theme_color));
            else days_title.setTextColor(getResources().getColor(R.color.base_color_white));
            days_title.setText(getItem(position).toString());
            //selected=(ImageView) convertView.findViewById(R.id.map_days_selected);
            //if(position==(whichDay-1)){selected.setVisibility(View.VISIBLE);}else{selected.setVisibility(View.GONE);}
            return convertView;
        }
    }
}
