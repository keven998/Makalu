package com.xuejian.client.lxp.module.goods;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.http.HttpCallBack;
import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.FavBean;
import com.xuejian.client.lxp.bean.SimpleCommodityBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.niceSpinner.NiceSpinner;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.RNView.ReactMainPage;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2015/10/17.
 */
public class GoodsList extends PeachBaseActivity {

    @Bind(R.id.tv_title_back)
    TextView tvTitleBack;
    @Bind(R.id.type_spinner)
    NiceSpinner typeSpinner;
    @Bind(R.id.sort_spinner)
    NiceSpinner sortSpinner;
    @Bind(R.id.lv_poi_list)
    XRecyclerView goodsList;
    @Bind(R.id.iv_toTop)
    FloatingActionButton toTop;
    @Bind(R.id.tv_list_title)
    TextView tvTitle;
    @Bind(R.id.ll_spinner)
    LinearLayout ll_spinner;
    @Bind(R.id.iv_banner)
    ImageView iv_banner;
    GoodsListAdapter adapter;
//    private int[] lebelColors = new int[]{
//            R.drawable.all_light_green_label,
//            R.drawable.all_light_red_label,
//            R.drawable.all_light_perple_label,
//            R.drawable.all_light_blue_label,
//            R.drawable.all_light_yellow_label
//    };
    //   private final List<Tag> mTags = new ArrayList<Tag>();

    private String[] sortType = new String[]{"推荐排序", "销量最高", "价格最低", "价格最高"};
    private String[] sortValue = new String[]{"", "salesVolume", "price", "price"};
    private String[] sort = new String[]{"", "desc", "asc", "desc"};
    private String currentSortValue;
    private String currentsort;
    private String currentType;
    private static final int PAGE_SIZE = 15;
    private static int COUNT = 15;
    private static int START;
    String locId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_list);
        ButterKnife.bind(this);
        final Handler handler = new Handler();
        locId = getIntent().getStringExtra("id");
        String title = getIntent().getStringExtra("title");
        boolean collection = getIntent().getBooleanExtra("collection", false);
        if (!TextUtils.isEmpty(title)) tvTitle.setText(title);

        adapter = new GoodsListAdapter(this);
        goodsList.setPullRefreshEnabled(false);
        goodsList.setLoadingMoreEnabled(true);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(GoodsList.this, ReactMainPage.class);
                intent.putExtra("commodityId", id);
                startActivity(intent);
            }
        });
        if (collection) {
            goodsList.setLoadingMoreEnabled(false);
            ll_spinner.setVisibility(View.GONE);
            toTop.setVisibility(View.GONE);
        }
        goodsList.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData(null, locId, currentType, currentSortValue, currentsort, adapter.getItemCount(), COUNT, false);
                    }
                }, 1000);
            }
        });
        tvTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        goodsList.setLayoutManager(new LinearLayoutManager(this));
        goodsList.setAdapter(adapter);
        toTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodsList.smoothScrollToPosition(0);
            }
        });
        if (collection) {
            final User user = AccountManager.getInstance().getLoginAccount(mContext);
            if (user != null) {
                getCollectionList(user.getUserId());
                adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
                    @Override
                    public void onItemLongClick(View view, int position, String id) {
                        notice(user.getUserId(), id);
                    }
                });
            }
        } else {
            getCategory(locId);
            getData(null, locId, null, null, null, 0, 15, true);
        }


    }

    public void deleteFav(final long userId, String id) {
        UserApi.delFav(String.valueOf(userId), id, "commodity", new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                Toast.makeText(mContext, "收藏已取消", Toast.LENGTH_SHORT).show();
                getCollectionList(userId);
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                Toast.makeText(mContext, "收藏取消失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void getCollectionList(Long userId) {
        UserApi.getFav(String.valueOf(userId), "", "commodity", new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson<FavBean> list = CommonJson.fromJson(result, FavBean.class);
                adapter.getDataList().clear();
                adapter.getDataList().addAll(list.result.commodities);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
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

//    private void initData() {
//        int lastColor = new Random().nextInt(4);
//        for (int i = 0; i < 3; i++) {
//            Tag tag = new Tag();
//            tag.setTitle("服务" + i);
//            tag.setId(i);
//            tag.setBackgroundResId(lebelColors[lastColor]);
//            //   tag.setBackgroundResId(R.drawable.all_whitesolid_greenline);
//            tag.setTextColor(R.color.white);
//            mTags.add(tag);
//            lastColor = getNextColor(lastColor);
//        }
//    }


    public void getData(String sellerId, String localityId, String category, String sortBy, String sort, final int start, int count, final boolean fresh) {

        TravelApi.getCommodityList(sellerId, localityId, category, sortBy, sort, String.valueOf(start), String.valueOf(count), new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<SimpleCommodityBean> list = CommonJson4List.fromJson(result, SimpleCommodityBean.class);

                if (fresh) {
                    adapter.getDataList().clear();
                }
                START = list.result.size();
                adapter.getDataList().addAll(list.result);
                adapter.notifyDataSetChanged();
                goodsList.loadMoreComplete();
                if (list.result.size() >= COUNT) {
                    // goodsList.setHasMoreData(true);
                } else {
                    goodsList.noMoreLoading();
                    goodsList.setLoadingMoreEnabled(false);
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
        if (bean.category.size() > 0) {
            bean.category.add(0, "全部类型");
            typeSpinner.attachDataSource(bean.category);
        }

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentType = bean.category.get(position);
                if (currentType.equals("全部类型")) {
                    currentType = "";
                }
                getData(null, locId, currentType, null, null, 0, 15, true);
                typeSpinner.dismissDropDown();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        final XDialog categoryDialog = new XDialog(GoodsList.this, R.layout.dialog_type_spinner, R.style.TypeSelectDialog);
//        final CategoryAdapter adapter = new CategoryAdapter(bean.category, GoodsList.this);
//        typeSpinner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                typeSpinner.setChecked(true);
//                WindowManager.LayoutParams wlmp = categoryDialog.getWindow().getAttributes();
//                wlmp.gravity = Gravity.TOP ;
//                wlmp.windowAnimations = android.R.anim.slide_in_left;
//                final ListView lv = categoryDialog.getListView();
//                lv.setAdapter(adapter);
//                categoryDialog.show();
//                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        //    lv.setSelection(headerPos.get(position));
//                        currentType = bean.category.get(position);
//                        typeSpinner.setText(currentType);
//                        getData(null, locId, currentType, null, 0, 15, true);
//                        typeSpinner.setChecked(false);
//                        if (categoryDialog != null) categoryDialog.dismiss();
//                    }
//                });
//            }
//        });


        sortSpinner.attachDataSource(Arrays.asList(sortType));
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortValue = sortValue[position];
                currentsort = sort[position];
                getData(null, locId, currentType, sortValue[position], sort[position], 0, 15, true);
                sortSpinner.dismissDropDown();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        final XDialog sortDialog = new XDialog(GoodsList.this, R.layout.dialog_sort_spinner, R.style.LocSelectDialog);
//        final CategoryAdapter sortAdapter = new CategoryAdapter(SortList, GoodsList.this);
//        sortSpinner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sortSpinner.setChecked(true);
//                WindowManager.LayoutParams wlmp = sortDialog.getWindow().getAttributes();
//                wlmp.gravity = Gravity.TOP | Gravity.RIGHT;
//                final ListView lv = sortDialog.getListView();
//                lv.setAdapter(sortAdapter);
//                sortDialog.show();
//                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        //    lv.setSelection(headerPos.get(position));
//                        sortSpinner.setText(SortList.get(position));
//                        getData(null, locId, currentType, sortValue[position], 0, 15, true);
//                        sortSpinner.setChecked(false);
//                        if (sortDialog != null) sortDialog.dismiss();
//                    }
//                });
//            }
//        });
    }


//    public int getNextColor(int currentcolor) {
//        Random random = new Random();
//        int nextValue = random.nextInt(4);
//        if (nextValue == 0) {
//            nextValue++;
//        }
//        return (nextValue + currentcolor) % 5;
//    }

    private class GoodsListAdapter extends RecyclerView.Adapter<ViewHolder> {
        private Activity mContext;
        private ArrayList<SimpleCommodityBean> mDataList;
        private DisplayImageOptions picOptions;
        private OnItemClickListener listener;
        private OnItemLongClickListener longClickListener;

        public GoodsListAdapter(Activity context) {
            mContext = context;
            mDataList = new ArrayList<SimpleCommodityBean>();
//            picOptions = new DisplayImageOptions.Builder()
//                    .cacheInMemory(true)
//                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
//                    .resetViewBeforeLoading(true)
//                    .showImageOnFail(R.drawable.ic_default_picture)
//                    .showImageOnLoading(R.drawable.ic_default_picture)
//                    .showImageForEmptyUri(R.drawable.ic_default_picture)
//                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        }


        public ArrayList<SimpleCommodityBean> getDataList() {
            return mDataList;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        public void setOnItemLongClickListener(OnItemLongClickListener listener) {
            this.longClickListener = listener;
        }

        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_goods_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final SimpleCommodityBean bean = (SimpleCommodityBean) getItem(position);
            Glide.with(mContext)
                    .load(bean.getCover().getUrl())
                    .placeholder(R.drawable.ic_default_picture)
                    .error(R.drawable.ic_default_picture)
                    .centerCrop()
                    .into(holder.ivGoods);

            //  ImageLoader.getInstance().displayImage(bean.getCover().getUrl(), holder.ivGoods, picOptions);
            holder.tvGoodsName.setText(bean.getTitle());

            SpannableString string = new SpannableString("起");
            string.setSpan(new AbsoluteSizeSpan(12, true), 0, 1,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            SpannableStringBuilder spb = new SpannableStringBuilder();
            spb.append("¥" + CommonUtils.getPriceString(bean.getPrice())).append(string);
            holder.tvGoodsCurrentPrice.setText(spb);

            holder.tvGoodsPrice.setText("¥" + CommonUtils.getPriceString(bean.getMarketPrice()));
            holder.tvGoodsPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvGoodsPrice.getPaint().setAntiAlias(true);
            holder.tvGoodsSales.setText(String.valueOf(bean.getSalesVolume()) + "已售");
            holder.tvGoodsComment.setText(bean.getRating() * 100 + "%满意");
            if (bean.getSeller() != null) {
                holder.tvStoreName.setText(bean.getSeller().getName());
            }
            holder.llContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(v, position, bean.getCommodityId());
                    }
                }
            });
            holder.llContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (longClickListener != null) {
                        longClickListener.onItemLongClick(v, position, bean.id);
                    }
                    return true;
                }
            });
        }

//        @Override
//        public long getItemId(int position) {
//            return mDataList.get(position).getCommodityId();
//        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }


    }

    interface OnItemClickListener {
        void onItemClick(View view, int position, long id);
    }

    interface OnItemLongClickListener {
        void onItemLongClick(View view, int position, String id);
    }

    private void notice(final long userId, final String id) {
        final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
        dialog.setTitle("提示");
        dialog.setMessage("确认取消收藏？");
        dialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFav(userId, id);
                dialog.dismiss();

            }
        });
        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView ivGoods;
        public final TextView tvGoodsName;
        public final TextView tvGoodsComment;
        public final TextView tvGoodsSales;
        public final TextView tvGoodsCurrentPrice;
        public final TextView tvGoodsPrice;
        public final TextView tvStoreName;
        public final LinearLayout llContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            tvStoreName = (TextView) itemView.findViewById(R.id.tv_store_name);
            tvGoodsPrice = (TextView) itemView.findViewById(R.id.tv_goods_price);
            tvGoodsCurrentPrice = (TextView) itemView.findViewById(R.id.tv_goods_current_price);
            tvGoodsSales = (TextView) itemView.findViewById(R.id.tv_goods_sales);
            tvGoodsComment = (TextView) itemView.findViewById(R.id.tv_goods_comment);
            tvGoodsName = (TextView) itemView.findViewById(R.id.tv_goods_name);
            ivGoods = (ImageView) itemView.findViewById(R.id.iv_poi_img);
            llContainer = (LinearLayout) itemView.findViewById(R.id.ll_container);
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
            else days_title.setTextColor(getResources().getColor(R.color.color_text_ii));
            days_title.setText(getItem(position).toString());
            //selected=(ImageView) convertView.findViewById(R.id.map_days_selected);
            //if(position==(whichDay-1)){selected.setVisibility(View.VISIBLE);}else{selected.setVisibility(View.GONE);}
            return convertView;
        }
    }
}
