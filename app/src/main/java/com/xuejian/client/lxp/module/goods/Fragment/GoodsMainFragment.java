package com.xuejian.client.lxp.module.goods.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.widget.DotView;
import com.aizou.core.widget.autoscrollviewpager.AutoScrollViewPager;
import com.aizou.core.widget.section.BaseSectionAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.ImageBean;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.widget.TagView.Tag;
import com.xuejian.client.lxp.common.widget.TagView.TagListView;
import com.xuejian.client.lxp.module.RNView.ReactMainPage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yibiao.qin on 2015/10/23.
 */
public class GoodsMainFragment extends PeachBaseFragment {
    ListView listView;
    DotView dotView;
    HorizontalScrollView hsPic;
    AutoScrollViewPager viewPager;
    LinearLayout llPics;
    ArrayList<String> picList = new ArrayList<>();
    DisplayImageOptions options;
    ArrayList<ArrayList<String>> data= new ArrayList<>();
    private final List<Tag> mTags = new ArrayList<Tag>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .resetViewBeforeLoading(true)
                .showImageOnFail(R.drawable.ic_default_picture)
                .showImageOnLoading(R.drawable.ic_default_picture)
                .showImageForEmptyUri(R.drawable.ic_default_picture)
             //   .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(10)))
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goods_main, null);
        listView = (ListView) v.findViewById(R.id.lv_recommend_goods);
        View headView = View.inflate(getActivity(), R.layout.fragment_goods_main_head, null);
        hsPic = (HorizontalScrollView) headView.findViewById(R.id.hs_pic);
        dotView = (DotView) headView.findViewById(R.id.dot_view);
        viewPager = (AutoScrollViewPager) headView.findViewById(R.id.vp_pic);
        initData();
        viewPager.setAdapter(new GoodsPageAdapter(getActivity(), null));
        listView.addHeaderView(headView);
        listView.setAdapter(new RecommendGoodsAdapter(getActivity()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent settingIntent = new Intent(getActivity(), ReactMainPage.class);
                startActivity(settingIntent);
            }
        });
        viewPager.startAutoScroll();
        viewPager.setInterval(2000);
        viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        dotView.setNum(4);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                dotView.setSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        for (int i = 0; i < 6; i++) {
            picList.add("http://images.taozilvxing.com/c8915e680131f7e94358c52d50de9b70?imageView2/2/w/200");
        }
        initScrollView(v);
        return v;
    }

    private void initScrollView(View v) {
        hsPic.removeAllViews();
        llPics = new LinearLayout(getActivity());
        llPics.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        llPics.removeAllViews();
        for (int i = 0; i < picList.size(); i++) {
            View view = View.inflate(getActivity(), R.layout.goods_main_pic_cell, null);
            ImageView my_pics_cell = (ImageView) view.findViewById(R.id.my_pics_cell);
            final String uri = picList.get(i);
            final int index = i;
            ImageLoader.getInstance().displayImage(picList.get(i), my_pics_cell, options);
            my_pics_cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            llPics.addView(view);
        }
        hsPic.addView(llPics);
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


    class RecommendGoodsAdapter extends BaseSectionAdapter {

        private Context mContex;

        public RecommendGoodsAdapter(Context c) {
            mContex = c;
        }

        @Override
        public int getContentItemViewType(int section, int position) {
            return 0;
        }

        @Override
        public int getHeaderItemViewType(int section) {
            return 0;
        }

        @Override
        public int getItemViewTypeCount() {
            return 1;
        }

        @Override
        public int getHeaderViewTypeCount() {
            return 1;
        }

        @Override
        public Object getItem(int section, int position) {
          return data.get(section).get(position);
        }

        @Override
        public long getItemId(int section, int position) {
            return getGlobalPositionForItem(section, position);
        }

        @Override
        public View getItemView(int section, int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(mContex, R.layout.item_recommend_goods_list, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvGoodsPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.tvGoodsPrice.getPaint().setAntiAlias(true);
            ImageLoader.getInstance().displayImage("http://images.taozilvxing.com/af563f2f2e6bea2560857c6026e428a1?imageMogr2/auto-orient/strip/gravity/NorthWest/crop/!998x570a2a2/thumbnail/960", viewHolder.ivGoodsImg, options);
            viewHolder.goodsTag.removeAllViews();
            viewHolder.goodsTag.setmTagViewResId(R.layout.goods_tag);
            viewHolder.goodsTag.setTags(mTags);
            return convertView;
        }

        @Override
        public View getHeaderView(int section, View convertView, ViewGroup parent) {
            convertView = View.inflate(mContex,R.layout.item_goods_section,null);
            TextView tv = (TextView) convertView.findViewById(R.id.tv_goods_section);
            return convertView;
        }

        @Override
        public int getSectionCount() {
            return data.size();
        }

        @Override
        public int getCountInSection(int section) {
            return data.get(section).size();
        }

        @Override
        public boolean doesSectionHaveHeader(int section) {
            return true;
        }

        @Override
        public boolean shouldListHeaderFloat(int headerIndex) {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return !isHeader(position);
        }

        /**
         * This class contains all butterknife-injected Views & Layouts from layout file 'item_recommend_goods_list.xml'
         * for easy to all layout elements.
         *
         * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
         */
         class ViewHolder {
            @InjectView(R.id.iv_goods_img)
            ImageView ivGoodsImg;
            @InjectView(R.id.tv_goods_name)
            TextView tvGoodsName;
            @InjectView(R.id.tv_goods_loc)
            TextView tvGoodsLoc;
            @InjectView(R.id.tv_goods_price)
            TextView tvGoodsPrice;
            @InjectView(R.id.tv_goods_current_price)
            TextView tvGoodsCurrentPrice;
            @InjectView(R.id.tv_shop_name)
            TextView tvShopName;
            @InjectView(R.id.goods_tag)
            TagListView goodsTag;

            ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }
    private void initData() {
        for (int i = 0; i < 3; i++) {
            data.add(new ArrayList<String>());
            for (int j = 0; j < 2; j++) {
                data.get(i).add(String.valueOf(j));
            }
        }

        int lastColor = new Random().nextInt(4);
        for (int i = 0; i < 3; i++) {
            Tag tag = new Tag();
            tag.setTitle("服务" + i);
            tag.setId(i);
          //  tag.setBackgroundResId();
            tag.setBackgroundResId(R.drawable.all_whitesolid_greenline);
            tag.setTextColor(R.color.app_theme_color);
            mTags.add(tag);
        }
    }
}
