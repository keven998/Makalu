package com.xuejian.client.lxp.module.dest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.autoscrollviewpager.AutoScrollViewPager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ImageBean;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.widget.TagView.Tag;
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
    ArrayList<String> picList = new ArrayList<>();
    private final List<Tag> mTags = new ArrayList<Tag>();
    TextView tvCountryName;
    TextView tvCountryNameEn;
    TextView tvCountryPicNum;
    TextView tvRecommendTime;
    TextView tvStoreNum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_info);
        listView = (ListView) findViewById(R.id.lv_city_detail);
        View headView = View.inflate(this, R.layout.activity_city_info_header, null);
        View footView = View.inflate(this,R.layout.footer_show_all,null);
        TextView showMore = (TextView) footView.findViewById(R.id.tv_show_all);
        showMore.setText("查看全部玩乐");
        ImageView back = (ImageView) findViewById(R.id.iv_nav_back);
        tvStoreNum = (TextView) headView.findViewById(R.id.tv_store_num);
        tvRecommendTime = (TextView) headView.findViewById(R.id.tv_recommend_time);
        tvCountryPicNum = (TextView) headView.findViewById(R.id.tv_country_pic_num);
        tvCountryName = (TextView) headView.findViewById(R.id.tv_country_name);
        tvCountryNameEn = (TextView) headView.findViewById(R.id.tv_country_name_en);
        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CityInfoActivity.this, GoodsList.class);
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
        viewPager.setAdapter(new GoodsPageAdapter(this, null));
        listView.addHeaderView(headView);
        listView.addFooterView(footView);
        listView.setAdapter(new RecommendGoodsAdapter(this));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvCountryPicNum.setText(String.format("%d/4",position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        for (int i = 0; i < 6; i++) {
            picList.add("http://images.taozilvxing.com/c8915e680131f7e94358c52d50de9b70?imageView2/2/w/200");
        }

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
            return 4;
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
            //      ImageBean ib = mDatas.get(position);
            ImageLoader.getInstance().displayImage("http://images.taozilvxing.com/d42dfcd90bcbbb1ebb0598031eda45fc?imageMogr2/auto-orient/strip/gravity/NorthWest/crop/!1982x1238a54a80/thumbnail/480", imageView, UILUtils.getDefaultOption());
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
        private Context mContex;

        public RecommendGoodsAdapter(Context c) {
            mContex = c;
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                    .resetViewBeforeLoading(true)
                    .showImageOnFail(R.drawable.ic_default_picture)
                    .showImageOnLoading(R.drawable.ic_default_picture)
                    .showImageForEmptyUri(R.drawable.ic_default_picture)
                    .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(60)))
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(mContex, R.layout.item_city_info_goods, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvGoodsPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.tvGoodsPrice.getPaint().setAntiAlias(true);
            ImageLoader.getInstance().displayImage("http://images.taozilvxing.com/af563f2f2e6bea2560857c6026e428a1?imageMogr2/auto-orient/strip/gravity/NorthWest/crop/!998x570a2a2/thumbnail/960", viewHolder.ivGoodsImg, UILUtils.getDefaultOption());
            ImageLoader.getInstance().displayImage("http://images.taozilvxing.com/a5c320585c1a9667facb10bd60d0f881?imageView2/2/w/1200", viewHolder.ivAvatar, options);
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
