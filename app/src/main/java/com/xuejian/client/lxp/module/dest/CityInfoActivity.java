package com.xuejian.client.lxp.module.dest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.autoscrollviewpager.AutoScrollViewPager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CityBean;
import com.xuejian.client.lxp.bean.ImageBean;
import com.xuejian.client.lxp.bean.SimpleCommodityBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.widget.TagView.Tag;
import com.xuejian.client.lxp.module.RNView.ReactMainPage;
import com.xuejian.client.lxp.module.goods.GoodsList;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yibiao.qin on 2015/11/4.
 */
public class CityInfoActivity extends PeachBaseActivity implements View.OnClickListener {

    ListView listView;
    AutoScrollViewPager viewPager;
    private final List<Tag> mTags = new ArrayList<Tag>();
    TextView tvCountryName;
    TextView tvCountryNameEn;
    TextView tvCountryPicNum;
    TextView tvRecommendTime;
    TextView tvStoreNum;
    String id;
    RecommendGoodsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_info);
        id = getIntent().getStringExtra("id");
        listView = (ListView) findViewById(R.id.lv_city_detail);
        View headView = View.inflate(this, R.layout.activity_city_info_header, null);
        View footView = View.inflate(this, R.layout.footer_show_all, null);
        TextView showMore = (TextView) footView.findViewById(R.id.tv_show_all);
        showMore.setText("查看全部玩乐");
        ImageView back = (ImageView) findViewById(R.id.iv_nav_back);
        tvStoreNum = (TextView) headView.findViewById(R.id.tv_store_num);
        tvRecommendTime = (TextView) headView.findViewById(R.id.tv_recommend_time);
        tvCountryPicNum = (TextView) headView.findViewById(R.id.tv_country_pic_num);
        tvCountryName = (TextView) headView.findViewById(R.id.tv_city_name);
        tvCountryNameEn = (TextView) headView.findViewById(R.id.tv_city_name_en);
        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CityInfoActivity.this, GoodsList.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewPager = (AutoScrollViewPager) headView.findViewById(R.id.vp_pic);
        listView.addHeaderView(headView);
        listView.addFooterView(footView);
        adapter = new RecommendGoodsAdapter(this, 3, CityInfoActivity.this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(CityInfoActivity.this, ReactMainPage.class);
                intent.putExtra("commodityId", id);
                startActivity(intent);
            }
        });
        initData(id);


    }

    private void initData(String id) {
        TravelApi.getCityInfo(id, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson<CityBean> bean = CommonJson.fromJson(result, CityBean.class);
                bindView(bean.result);
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
        TravelApi.getCityDetail(id, "", new HttpCallBack<String>() {
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

        TravelApi.getCommodityList(null, id, null, null, null, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<SimpleCommodityBean> list = CommonJson4List.fromJson(result, SimpleCommodityBean.class);
                adapter.getDataList().clear();
                adapter.getDataList().addAll(list.result);
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

    private void bindView(final CityBean bean) {
        tvCountryName.setText(bean.zhName);
        tvCountryNameEn.setText(bean.enName);
        tvRecommendTime.setText(String.format("推荐游玩时间:%s", bean.travelMonth));
        tvStoreNum.setText(String.valueOf(bean.commodityCnt));
        System.out.println("size " + bean.images.size());
        tvCountryPicNum.setText(String.format("%d/%d", 1, bean.images.size()));
        viewPager.setAdapter(new GoodsPageAdapter(this, bean.images));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvCountryPicNum.setText(String.format("%d/%d", position + 1, bean.images.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        findViewById(R.id.btn_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MoreTravelNoteActivity.class);
                intent.putExtra("keyword", bean.zhName);
                intent.putExtra("id", bean.id);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    class GoodsPageAdapter extends PagerAdapter {

        private Context mContext;
        private ArrayList<ImageBean> mDatas;

        public GoodsPageAdapter(Context context, ArrayList<ImageBean> datas) {
            mDatas = datas;
            mContext = context;
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(mContext);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.spot_detail_picture_height));
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBackgroundColor(getResources().getColor(R.color.color_gray_light));
            ImageBean ib = mDatas.get(position);
            ImageLoader.getInstance().displayImage(ib.url, imageView, UILUtils.getDefaultOption());
            container.addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }
    }

    class RecommendGoodsAdapter extends BaseAdapter {
        private DisplayImageOptions options;
        private Context mContext;
        private ArrayList<SimpleCommodityBean> data;
        private int maxCount;
        Activity activity;

        public RecommendGoodsAdapter(Context c, int maxCount, Activity activity) {
            this.maxCount = maxCount;
            data = new ArrayList<>();
            mContext = c;
            this.activity = activity;
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                    .resetViewBeforeLoading(true)
                    .showImageOnFail(R.drawable.ic_home_more_avatar_unknown_round)
                    .showImageOnLoading(R.drawable.ic_home_more_avatar_unknown_round)
                    .showImageForEmptyUri(R.drawable.ic_home_more_avatar_unknown_round)
                    .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(60)))
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        public ArrayList<SimpleCommodityBean> getDataList() {
            return data;
        }

        @Override
        public int getCount() {
            if (data.size() > maxCount) {
                return maxCount;
            } else {
                return data.size();
            }
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return data.get(position).getCommodityId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_city_info_goods, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            SimpleCommodityBean bean = (SimpleCommodityBean) getItem(position);

            viewHolder.tvShopName.setText(bean.getTitle());
            viewHolder.tvGoodsCurrentPrice.setText("¥" + String.valueOf((double) Math.round(bean.getPrice() * 10 / 10)));
            viewHolder.tvGoodsPrice.setText("¥" + String.valueOf((double) Math.round(bean.getMarketPrice() * 10 / 10)));
            viewHolder.tvGoodsPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.tvGoodsPrice.getPaint().setAntiAlias(true);
            viewHolder.tvGoodsSales.setText("销量:" + String.valueOf(bean.getSalesVolume()));
            viewHolder.tvGoodsRecommend.setText(bean.getRating() * 100 + "%");

//            ViewGroup.LayoutParams para;
//            para = viewHolder.ivGoodsImg.getLayoutParams();
//            int width = CommonUtils.getScreenWidth(activity);
//            para.height = width * 2 / 5;
//            para.width = width;
//            System.out.println(" para.height " + para.height + "para.width" + para.width);
//
//            viewHolder.ivGoodsImg.setLayoutParams(para);

            if (bean.getImages().size() > 0) {
                ImageLoader.getInstance().displayImage(bean.getImages().get(0).url, viewHolder.ivGoodsImg, UILUtils.getDefaultOption());
            } else {
                ImageLoader.getInstance().displayImage("", viewHolder.ivGoodsImg, UILUtils.getDefaultOption());
            }
            ImageLoader.getInstance().displayImage("", viewHolder.ivAvatar, options);
            return convertView;
        }


        /**
         * This class contains all butterknife-injected Views & Layouts from layout file 'item_city_info_goods.xml'
         * for easy to all layout elements.
         *
         * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
         */
        class ViewHolder {
            @InjectView(R.id.iv_goods_img)
            ImageView ivGoodsImg;
            @InjectView(R.id.tv_shop_name)
            TextView tvShopName;
            @InjectView(R.id.tv_goods_sales)
            TextView tvGoodsSales;
            @InjectView(R.id.tv_goods_recommend)
            TextView tvGoodsRecommend;
            @InjectView(R.id.tv_goods_current_price)
            TextView tvGoodsCurrentPrice;
            @InjectView(R.id.tv_goods_price)
            TextView tvGoodsPrice;
            @InjectView(R.id.iv_avatar)
            ImageView ivAvatar;

            ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }
}
