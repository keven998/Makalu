package com.xuejian.client.lxp.module.dest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.autoscrollviewpager.AutoScrollViewPager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CityBean;
import com.xuejian.client.lxp.bean.ImageBean;
import com.xuejian.client.lxp.bean.SimpleCommodityBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.TagView.Tag;
import com.xuejian.client.lxp.common.widget.TagView.TagListView;
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
    TextView tvStoreNum;
    String id;
    RecommendGoodsAdapter adapter;
    FrameLayout fl_city_img;
    TextView showMore;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_info);
//        Uri uri = getIntent().getData();
//        System.out.println(uri.getHost());
//        System.out.println(uri.getPath());
//        System.out.println(uri.getScheme());
//        System.out.println(uri.getQueryParameter("id"));
//        System.out.println(uri.getLastPathSegment());
//        System.out.println(uri.getPathSegments().toString());

        id = getIntent().getStringExtra("id");
        listView = (ListView) findViewById(R.id.lv_city_detail);
        View headView = View.inflate(this, R.layout.activity_city_info_header, null);
        View footView = View.inflate(this, R.layout.footer_show_all, null);
        showMore = (TextView) footView.findViewById(R.id.tv_show_all);
        showMore.setText("查看全部玩乐");
        fl_city_img = (FrameLayout) headView.findViewById(R.id.fl_city_img);
        ImageView back = (ImageView) findViewById(R.id.iv_nav_back);
        tvStoreNum = (TextView) headView.findViewById(R.id.tv_store_num);
        tvCountryPicNum = (TextView) headView.findViewById(R.id.tv_country_pic_num);
        tvCountryName = (TextView) headView.findViewById(R.id.tv_city_name);
        tvCountryNameEn = (TextView) headView.findViewById(R.id.tv_city_name_en);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewPager = (AutoScrollViewPager) headView.findViewById(R.id.vp_pic);
        listView.addHeaderView(headView);
        listView.addFooterView(footView);
        adapter = new RecommendGoodsAdapter(this, CityInfoActivity.this);
        listView.setAdapter(adapter);
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

        TravelApi.getCommodityList(null, id, null, null, null, "0","3",new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<SimpleCommodityBean> list = CommonJson4List.fromJson(result, SimpleCommodityBean.class);
                adapter.getDataList().clear();
                adapter.getDataList().addAll(list.result);
                adapter.notifyDataSetChanged();
                if (list.result.size()>0){
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent();
                            intent.setClass(CityInfoActivity.this, ReactMainPage.class);
                            intent.putExtra("commodityId", id);
                            startActivity(intent);
                        }
                    });
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

    private void bindView(final CityBean bean) {

        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CityInfoActivity.this, GoodsList.class);
                intent.putExtra("id", id);
                intent.putExtra("title",String.format("%s玩乐",bean.zhName));
                startActivity(intent);
            }
        });
        tvCountryName.setText(bean.zhName);
        tvCountryNameEn.setText(bean.enName);
        tvStoreNum.setText(String.valueOf(bean.commodityCnt));
        viewPager.setAdapter(new GoodsPageAdapter(this, bean.images,bean.id,bean.zhName));


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
        private String id;
        private String zhName;
        public GoodsPageAdapter(Context context, ArrayList<ImageBean> datas,String id,String zhName) {
            mDatas = datas;
            mContext = context;
            this.id = id;
            this.zhName = zhName;
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
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(CityInfoActivity.this, "card_item_city_pictures");
                    Intent intent = new Intent(CityInfoActivity.this, CityPictureActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("title", zhName);
                    startActivityWithNoAnim(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }
    }

    class RecommendGoodsAdapter extends BaseAdapter {
        private DisplayImageOptions picOptions;
        private Context mContext;
        private ArrayList<SimpleCommodityBean> data;
        Activity activity;

        public RecommendGoodsAdapter(Context c, Activity activity) {
            data = new ArrayList<>();
            mContext = c;
            this.activity = activity;
            picOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                    .resetViewBeforeLoading(true)
                    .showImageOnFail(R.drawable.ic_default_picture)
                    .showImageOnLoading(R.drawable.ic_default_picture)
                    .showImageForEmptyUri(R.drawable.ic_default_picture)
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
            return data.size();
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
            holder.tvGoodsCurrentPrice.setText("¥" + CommonUtils.getPriceString(bean.getPrice()));
            holder.tvGoodsPrice.setText("¥" +CommonUtils.getPriceString(bean.getMarketPrice()));
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
         * This class contains all butterknife-injected Views & Layouts from layout file 'item_city_info_goods.xml'
         * for easy to all layout elements.
         *
         * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
         */
        class ViewHolder {
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
    }
}
