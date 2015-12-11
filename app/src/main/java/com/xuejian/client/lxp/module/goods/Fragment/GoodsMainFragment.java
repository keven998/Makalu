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
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.DotView;
import com.aizou.core.widget.autoscrollviewpager.AutoScrollViewPager;
import com.aizou.core.widget.section.BaseSectionAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.ColumnBean;
import com.xuejian.client.lxp.bean.RecommendCommodityBean;
import com.xuejian.client.lxp.bean.SimpleCommodityBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
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
    ArrayList<ArrayList<String>> data = new ArrayList<>();
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
        listView.addHeaderView(headView);
        initData();
        getData();
        getListData();

        return v;
    }

    private void initScrollView() {
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

    public void getListData() {

        TravelApi.getRecommend(new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<RecommendCommodityBean> list = CommonJson4List.fromJson(result, RecommendCommodityBean.class);

//                Observable.from(list.result)
//                        .map(new Func1<RecommendCommodityBean, Object>() {
//                            @Override
//                            public Object call(RecommendCommodityBean recommendCommodityBean) {
//                                return null;
//                            }
//                        })
//                        .observeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe();

                resizeData(list.result);
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void resizeData(List<RecommendCommodityBean> result) {
//        ArrayList<ArrayList<SimpleCommodityBean>> data = new ArrayList<>();
//        final ArrayList<String> sectionName = new ArrayList<>();
//
//        Observable.from(result)
//                .flatMap(new Func1<RecommendCommodityBean, Observable<Object>>() {
//                    @Override
//                    public Observable<Object> call(final RecommendCommodityBean recommendCommodityBean) {
//
//                        return Observable.create(new Observable.OnSubscribe<Object>() {
//                            @Override
//                            public void call(Subscriber<? super Object> subscriber) {
//                                subscriber.onNext(recommendCommodityBean.getTopicType());
//                                subscriber.onNext(recommendCommodityBean.getCommodities());
//                                subscriber.onCompleted();
//                            }
//                        });
//                    }
//                })
//                .filter(new Func1<Object, Boolean>() {
//                    @Override
//                    public Boolean call(Object o) {
//                        if (o instanceof String) {
//                            sectionName.add(o.toString());
//                            return false;
//                        }else {
//                            return true;
//                        }
//                    }
//                })
//                .map(new Func1<Object, ArrayList<SimpleCommodityBean>>() {
//                    @Override
//                    public ArrayList<SimpleCommodityBean> call(Object o) {
//                        return (ArrayList<SimpleCommodityBean>)o;
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<ArrayList<SimpleCommodityBean>>() {
//                    @Override
//                    public void call(ArrayList<SimpleCommodityBean> simpleCommodityBeans) {
//
//                    }
//                });
        ArrayList<ArrayList<SimpleCommodityBean>> data = new ArrayList<>();
        ArrayList<String> sectionName = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            sectionName.add(i, result.get(i).getTopicType());
            data.add(i, result.get(i).getCommodities());
        }
        bindListView(data, sectionName);
    }

    private void bindListView(ArrayList<ArrayList<SimpleCommodityBean>> data, ArrayList<String> sectionName) {
        listView.setAdapter(new RecommendGoodsAdapter(getActivity(), data, sectionName));
    }


    public void getData() {
        TravelApi.getMainPageColumns(new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<ColumnBean> list = CommonJson4List.fromJson(result, ColumnBean.class);
                bindView(list.result);
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void bindView(List<ColumnBean> result) {
        for (ColumnBean columnBean : result) {
            if ("slide".equals(columnBean.getColumnType())) {
                GoodsPageAdapter adapter = new GoodsPageAdapter(getActivity(), result.get(0).getColumns());
                viewPager.setAdapter(adapter);
                viewPager.startAutoScroll();
                viewPager.setInterval(2000);
                viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_TO_PARENT);
                dotView.setNum(adapter.getCount());
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
            } else if ("special".equals(columnBean.getColumnType())) {
                ArrayList<ColumnBean.ColumnsEntity> list = result.get(1).getColumns();
                for (ColumnBean.ColumnsEntity entity : list) {
                    if (entity.getImages().size() > 0) {
                        picList.add(entity.getImages().get(0).url);
                    }
                }
                initScrollView();
            }
        }

    }


    class GoodsPageAdapter extends PagerAdapter {

        private Context mContext;
        private ArrayList<ColumnBean.ColumnsEntity> mDatas;

        public GoodsPageAdapter(Context context, ArrayList<ColumnBean.ColumnsEntity> datas) {
            mDatas = datas;
            mContext = context;
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        public ArrayList<ColumnBean.ColumnsEntity> getmDatas() {
            return mDatas;
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

            if (mDatas.get(position).getImages().size() > 0) {
                ImageLoader.getInstance().displayImage(mDatas.get(position).getImages().get(0).url, imageView, UILUtils.getDefaultOption());
            } else {
                ImageLoader.getInstance().displayImage("", imageView, UILUtils.getDefaultOption());
            }
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
        private ArrayList<ArrayList<SimpleCommodityBean>> data;
        private ArrayList<String> sectionName;

        public RecommendGoodsAdapter(Context c, ArrayList<ArrayList<SimpleCommodityBean>> data, ArrayList<String> sectionName) {
            mContex = c;
            this.data = data;
            this.sectionName = sectionName;
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
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final SimpleCommodityBean bean = (SimpleCommodityBean) getItem(section, position);
            if (bean != null) {
                viewHolder.tvGoodsName.setText(bean.getTitle());
                viewHolder.tvShopName.setText(bean.getSeller().getName());
                viewHolder.tvGoodsLoc.setText(bean.getLocality().getZhName());
                viewHolder.tvGoodsPrice.setText(String.format("¥%s", String.valueOf((float) (Math.round(bean.getMarketPrice() * 10) / 10))));
                viewHolder.tvGoodsPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                viewHolder.tvGoodsPrice.getPaint().setAntiAlias(true);
                viewHolder.tvGoodsCurrentPrice.setText(String.format("¥%s", String.valueOf((float) (Math.round(bean.getPrice() * 10) / 10))));
                ImageLoader.getInstance().displayImage(bean.getCover().getUrl(), viewHolder.ivGoodsImg, options);
                viewHolder.goodsTag.removeAllViews();
                viewHolder.goodsTag.setmTagViewResId(R.layout.goods_tag);
                viewHolder.goodsTag.setTags(mTags);
                viewHolder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent settingIntent = new Intent(getActivity(), ReactMainPage.class);
                        settingIntent.putExtra("commodityId", bean.getCommodityId());
                        startActivity(settingIntent);
                    }
                });
            }
            return convertView;
        }

        @Override
        public View getHeaderView(int section, View convertView, ViewGroup parent) {
            convertView = View.inflate(mContex, R.layout.item_goods_section, null);
            TextView tv = (TextView) convertView.findViewById(R.id.tv_goods_section);
            tv.setText(sectionName.get(section));
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
            @InjectView(R.id.fl_container)
            FrameLayout container;

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
