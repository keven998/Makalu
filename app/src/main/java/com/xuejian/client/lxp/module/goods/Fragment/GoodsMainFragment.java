package com.xuejian.client.lxp.module.goods.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.DotView;
import com.aizou.core.widget.autoscrollviewpager.AutoScrollViewPager;
import com.aizou.core.widget.section.BaseSectionAdapter;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.ColumnBean;
import com.xuejian.client.lxp.bean.EventLogin;
import com.xuejian.client.lxp.bean.EventLogout;
import com.xuejian.client.lxp.bean.RecommendCommodityBean;
import com.xuejian.client.lxp.bean.SimpleCommodityBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.PeachWebViewActivity;
import com.xuejian.client.lxp.module.goods.CommodityDetailActivity;
import com.xuejian.client.lxp.module.goods.GoodsList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2015/10/23.
 */
public class GoodsMainFragment extends PeachBaseFragment {
    ListView listView;
    DotView dotView;
    HorizontalScrollView hsPic;
    AutoScrollViewPager viewPager;
    LinearLayout llPics;
    ArrayList<ColumnBean.ColumnsEntity> picList = new ArrayList<>();
    DisplayImageOptions options;
    ArrayList<ArrayList<String>> data = new ArrayList<>();
    RelativeLayout rlError;
    TextView tvRetry;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goods_main, null);
        listView = (ListView) v.findViewById(R.id.lv_recommend_goods);
        View headView = View.inflate(getActivity(), R.layout.fragment_goods_main_head, null);
        hsPic = (HorizontalScrollView) headView.findViewById(R.id.hs_pic);
        dotView = (DotView) headView.findViewById(R.id.dot_view);
        viewPager = (AutoScrollViewPager) headView.findViewById(R.id.vp_pic);
        rlError = (RelativeLayout) v.findViewById(R.id.rl_error);
        tvRetry = (TextView) v.findViewById(R.id.tv_retry);
        listView.addHeaderView(headView);
        getData();
        getListData();
        tvRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlError.setVisibility(View.GONE);
                retry();
            }
        });
        ((TextView)v.findViewById(R.id.tv_search_content)).setText("商品搜索");
        v.findViewById(R.id.ll_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GoodsList.class);
                intent.putExtra("search", true);
                startActivity(intent);
            }
        });
        return v;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogigEvent(EventLogin eventLogin){
        getData();
        getListData();
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onLogigEvent(EventLogout eventLogout){
        getData();
        getListData();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initScrollView() {
        hsPic.removeAllViews();
        llPics = new LinearLayout(getActivity());
        llPics.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        llPics.removeAllViews();
        for (int i = 0; i < picList.size(); i++) {
            View view = View.inflate(getActivity(), R.layout.goods_main_pic_cell, null);
            final ImageView my_pics_cell = (ImageView) view.findViewById(R.id.my_pics_cell);
            final int index = i;
            if (picList.get(index).getImages().size() > 0) {
                Glide.with(this)
                        .load(picList.get(index).getImages().get(0).url)
                        .placeholder(R.drawable.ic_default_picture)
                        .error(R.drawable.ic_default_picture)
                        .centerCrop()
                        .into(my_pics_cell);
                //   ImageLoader.getInstance().displayImage(picList.get(i).getImages().get(0).url, my_pics_cell, options);
            } else {
                Glide.with(this)
                        .load("")
                        .placeholder(R.drawable.ic_default_picture)
                        .error(R.drawable.ic_default_picture)
                        .centerCrop()
                        .into(my_pics_cell);
                //     ImageLoader.getInstance().displayImage("", my_pics_cell, options);
            }
            final String url = picList.get(index).getLink();
            my_pics_cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (url.startsWith("lvxingpai")) {
                        Intent in = new Intent();
                        in.setAction("android.intent.action.route");
                        in.addCategory(Intent.CATEGORY_DEFAULT);
                        in.setData(Uri.parse(url));
                        if (CommonUtils.checkIntent(getActivity(), in)) startActivity(in);
                    } else if (url.startsWith("http://")) {
                        Intent intent = new Intent(getActivity(), PeachWebViewActivity.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
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
                DialogManager.getInstance().dissMissModelessLoadingDialog();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                rlError.setVisibility(View.VISIBLE);

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
            }
        });
    }

    private void retry() {
        getData();
        getListData();
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

            ArrayList<SimpleCommodityBean> delList = new ArrayList<>();
            ArrayList<SimpleCommodityBean> addList = result.get(i).getCommodities();
            for (SimpleCommodityBean bean : addList) {
                if (bean == null) {
                    delList.add(bean);
                }
            }
            // addList.removeAll(Collections.singleton(null));
            addList.removeAll(delList);
            data.add(i, addList);
            delList = null;
            addList = null;
            //  data.add(i, result.get(i).getCommodities());
        }
        bindListView(data, sectionName);
    }

    private void bindListView(ArrayList<ArrayList<SimpleCommodityBean>> data, ArrayList<String> sectionName) {
        listView.setAdapter(new RecommendGoodsAdapter(getActivity(), data, sectionName, this));
    }


    public void getData() {
        long userId = -1;
        User user = AccountManager.getInstance().getLoginAccount(getActivity());
        if (user!=null){
            userId = user.getUserId();
        }
        TravelApi.getMainPageColumns(userId,new HttpCallBack<String>() {

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
                viewPager.setInterval(4000);
                viewPager.setAutoScrollDurationFactor(2.0);
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
                picList.addAll(result.get(1).getColumns());
                initScrollView();
            }
        }

    }


    class GoodsPageAdapter extends PagerAdapter {

        private Context mContext;
        private ArrayList<ColumnBean.ColumnsEntity> mDatas;
        private SparseArray<View> mViews;

        public GoodsPageAdapter(Context context, ArrayList<ColumnBean.ColumnsEntity> datas) {
            mDatas = datas;
            mContext = context;
            mViews = new SparseArray<View>(datas.size());
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

        private View getViews(int position) {
            return mViews.get(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imageView = (ImageView) getViews(position);
            if (imageView == null) {
                imageView = new ImageView(mContext);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.spot_detail_picture_height));
                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setBackgroundColor(getResources().getColor(R.color.color_gray_light));

                if (mDatas.get(position).getImages().size() > 0) {
                    Glide.with(mContext)
                            .load(mDatas.get(position).getImages().get(0).url)
                            .placeholder(R.drawable.ic_default_picture)
                            .error(R.drawable.ic_default_picture)
                            .centerCrop()
                            .into(imageView);
                    //         ImageLoader.getInstance().displayImage(mDatas.get(position).getImages().get(0).url, imageView, options);
                } else {
                    Glide.with(mContext)
                            .load("")
                            .placeholder(R.drawable.ic_default_picture)
                            .error(R.drawable.ic_default_picture)
                            .centerCrop()
                            .into(imageView);
                    //    ImageLoader.getInstance().displayImage("", imageView, options);
                }

                final String url = mDatas.get(position).getLink();
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (url.startsWith("lvxingpai")) {
                            Intent in = new Intent();
                            in.setAction("android.intent.action.route");
                            in.addCategory(Intent.CATEGORY_DEFAULT);
                            in.setData(Uri.parse(url));
                            if (CommonUtils.checkIntent(getActivity(), in)) startActivity(in);
                        } else if (url.startsWith("http://")) {
                            Intent intent = new Intent(getActivity(), PeachWebViewActivity.class);
                            intent.putExtra("url", url);
                            intent.putExtra("share",true);
                            startActivity(intent);
                        }
                    }
                });
                mViews.put(position, imageView);
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
        private Fragment mFragment;

        public RecommendGoodsAdapter(Context c, ArrayList<ArrayList<SimpleCommodityBean>> data, ArrayList<String> sectionName, Fragment fragment) {
            mContex = c;
            this.data = data;
            this.sectionName = sectionName;
            mFragment = fragment;
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
                viewHolder.tvGoodsPrice.setText(String.format("¥%s", CommonUtils.getPriceString(bean.getMarketPrice())));
                viewHolder.tvGoodsPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                viewHolder.tvGoodsPrice.getPaint().setAntiAlias(true);
                viewHolder.tvGoodsCurrentPrice.setText(String.format("¥%s", CommonUtils.getPriceString(bean.getPrice())));
                Glide.with(mFragment)
                        .load(bean.getCover().getUrl())
                        .placeholder(R.drawable.ic_default_picture)
                        .error(R.drawable.ic_default_picture)
                        .centerCrop()
                        .into(viewHolder.ivGoodsImg);
                //  ImageLoader.getInstance().displayImage(bean.getCover().getUrl(), viewHolder.ivGoodsImg, options);
                viewHolder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent settingIntent = new Intent(getActivity(), CommodityDetailActivity.class);
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
            switch (sectionName.get(section)) {
                case "特价折扣":
                    tv.setTextColor(getResources().getColor(R.color.price_color));
          //          tv.setCompoundDrawables(getResources().getDrawable(R.drawable.icon_goods_discount), null, null, null);
                    tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_goods_discount,0,0,0);
                    break;
                case "小编推荐":
                    tv.setTextColor(getResources().getColor(R.color.app_theme_color));
           //         tv.setCompoundDrawables(getResources().getDrawable(R.drawable.icon_goods_recommend), null, null, null);
                    tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_goods_recommend, 0, 0, 0);
                    break;
                case "热门玩乐":
                    tv.setTextColor(getResources().getColor(R.color.price_color));
           //         tv.setCompoundDrawables(getResources().getDrawable(R.drawable.icon_good_section_hot), null, null, null);
                    tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_good_section_hot, 0, 0, 0);
                    break;
                default:
                    tv.setTextColor(getResources().getColor(R.color.price_color));
           //         tv.setCompoundDrawables(getResources().getDrawable(R.drawable.icon_good_section_hot), null, null, null);
                    tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_good_section_hot, 0, 0, 0);
                    break;
            }
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
            @Bind(R.id.iv_goods_img)
            ImageView ivGoodsImg;
            @Bind(R.id.tv_goods_name)
            TextView tvGoodsName;
            @Bind(R.id.tv_goods_loc)
            TextView tvGoodsLoc;
            @Bind(R.id.tv_goods_price)
            TextView tvGoodsPrice;
            @Bind(R.id.tv_goods_current_price)
            TextView tvGoodsCurrentPrice;
            @Bind(R.id.tv_shop_name)
            TextView tvShopName;
            @Bind(R.id.fl_container)
            FrameLayout container;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
