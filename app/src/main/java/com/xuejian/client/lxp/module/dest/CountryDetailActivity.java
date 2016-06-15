package com.xuejian.client.lxp.module.dest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.autoscrollviewpager.AutoScrollViewPager;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CityDetailBean;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.Remarks;
import com.xuejian.client.lxp.bean.SellerBean;
import com.xuejian.client.lxp.bean.SellerWrap;
import com.xuejian.client.lxp.bean.SimpleCommodityBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.FixedGridLayoutManager;
import com.xuejian.client.lxp.common.widget.ListViewForScrollView;
import com.xuejian.client.lxp.common.widget.glide.GlideCircleTransform;
import com.xuejian.client.lxp.module.PeachWebViewActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2016/5/14.
 */
public class CountryDetailActivity extends PeachBaseActivity {

    @Bind(R.id.iv_nav_back)
    ImageView mIvNavBack;
    @Bind(R.id.tv_title_bar_title)
    TextView mTvTitleBarTitle;
    @Bind(R.id.iv_menu)
    ImageView mIvMenu;
    @Bind(R.id.vp_pic)
    AutoScrollViewPager mVpPic;
    @Bind(R.id.tv_city_name)
    TextView mTvCityName;
    @Bind(R.id.tv_city_name_en)
    TextView mTvCityNameEn;
    @Bind(R.id.tv_country_pic_num)
    TextView mTvCountryPicNum;
    @Bind(R.id.fl_city_img)
    FrameLayout mFlCityImg;
    @Bind(R.id.lv_player)
    ListViewForScrollView mLvPlayer;
    @Bind(R.id.rv_sellers)
    RecyclerView mRvSellers;
    @Bind(R.id.lv_city_detail)
    RecyclerView mLvCityDetail;
    @Bind(R.id.tv_show_all)
    TextView mTvShowAll;
    @Bind(R.id.footView)
    LinearLayout mFootView;
    @Bind(R.id.lv_city)
    ListViewForScrollView mLvCity;
    @Bind(R.id.hs_seller)
    HorizontalScrollView hsSeller;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_info);
        ButterKnife.bind(this);
        id = getIntent().getStringExtra("id");
        getData();
    }

    private void getData() {
        TravelApi.getCountryDetail(id, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson<CityDetailBean> beanCommonJson = CommonJson.fromJson(result, CityDetailBean.class);
                bindInfoView(beanCommonJson.result);
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

        TravelApi.getSellerInCountryDetail(id, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson<SellerWrap> list = CommonJson.fromJson(result, SellerWrap.class);
                bindSellerView(list.result.sellers);
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
        TravelApi.getCommodityList(null,null, id, null, null, null, "0", "4", false,new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<SimpleCommodityBean> list = CommonJson4List.fromJson(result, SimpleCommodityBean.class);
                bindCommodityView(list.result);
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void bindCommodityView(List<SimpleCommodityBean> result) {
        mLvCityDetail.setLayoutManager(new FixedGridLayoutManager(this, 2));
        mLvCityDetail.setAdapter(new Adapter(this,result));
    }

    private void bindSellerView(List<SellerBean> result) {
        initScrollView(result);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        mRvSellers.setLayoutManager(linearLayoutManager);
//        mRvSellers.setAdapter(new SellerAdapter(result));
    }

    private void initScrollView(List<SellerBean> list) {
        hsSeller.removeAllViews();
        LinearLayout llPics = new LinearLayout(this);
        llPics.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        llPics.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            View view = View.inflate(this, R.layout.goods_main_pic_cell, null);
            final ImageView my_pics_cell = (ImageView) view.findViewById(R.id.my_pics_cell);
            final int index = i;
            if (list.get(index).user.getAvatar()!=null) {
                Glide.with(this)
                        .load(list.get(index).user.getAvatar().getUrl())
                        .placeholder(R.drawable.ic_default_picture)
                        .error(R.drawable.ic_default_picture)
                        .centerCrop()
                        .transform(new GlideCircleTransform(this))
                        .into(my_pics_cell);
            } else {
                Glide.with(this)
                        .load("")
                        .placeholder(R.drawable.ic_default_picture)
                        .error(R.drawable.ic_default_picture)
                        .centerCrop()
                        .transform(new GlideCircleTransform(this))
                        .into(my_pics_cell);
            }
            llPics.addView(view);
        }
        hsSeller.addView(llPics);
    }
    private void bindInfoView(final CityDetailBean result) {
        mTvTitleBarTitle.setText(result.getZhName());
        mVpPic.setAdapter(new GoodsPageAdapter(this, result.getImages(), result.getId(), result.getZhName()));
        mLvPlayer.setAdapter(new PlayAdapter(result.getRemarks()));
        mIvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPanel(result);
            }
        });

    }

    public void showPanel(final CityDetailBean bean) {
        final ArrayList<LocBean> locList = new ArrayList<LocBean>();
        final LocBean locBean = new LocBean();
        locBean.id = bean.getId();
        locBean.zhName = bean.getZhName();
        View view = View.inflate(this, R.layout.dialog_city_panel, null);
        view.findViewById(R.id.btn_travel_notice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                intent.putExtra("url", bean.getPlayGuide());
                intent.putExtra("title", "城市指南");
                startActivity(intent);
            }
        });
        view.findViewById(R.id.btn_traffic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                intent.putExtra("url", bean.getTrafficInfoUrl());
                intent.putExtra("title", "交通信息");
                startActivity(intent);
            }
        });
        view.findViewById(R.id.btn_viewspot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SpotListActivity.class);
                locList.add(locBean);
                intent.putParcelableArrayListExtra("locList", locList);
                intent.putExtra("type", TravelApi.PeachType.SPOT);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.btn_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MoreTravelNoteActivity.class);
                intent.putExtra("keyword", bean.getZhName());
                intent.putExtra("id", bean.getId());
                startActivity(intent);
            }
        });
        view.findViewById(R.id.btn_food).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PoiListActivity.class);
                locList.clear();
                locList.add(locBean);
                intent.putParcelableArrayListExtra("locList", locList);
                intent.putExtra("type", TravelApi.PeachType.RESTAURANTS);
                intent.putExtra("isFromCityDetail", true);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.btn_shopping).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PoiListActivity.class);
                locList.clear();
                locList.add(locBean);
                intent.putParcelableArrayListExtra("locList", locList);
                intent.putExtra("type", TravelApi.PeachType.SHOPPING);
                intent.putExtra("isFromCityDetail", true);
                startActivity(intent);
            }
        });
        final PopupWindow popupWindow = new PopupWindow(view);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setWidth(CommonUtils.getScreenWidth(this));
        popupWindow.setHeight(CommonUtils.getScreenHeight(this) - mTvTitleBarTitle.getHeight() - 50);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        int[] location = new int[2];
        mTvTitleBarTitle.getLocationOnScreen(location);
        popupWindow.setAnimationStyle(R.style.PopAnimation1);
        final int[] f = location;

        findViewById(R.id.iv_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAtLocation(mTvTitleBarTitle, Gravity.NO_GRAVITY, CommonUtils.getScreenWidth(CountryDetailActivity.this) / 2, mTvTitleBarTitle.getHeight() + 50);
            }
        });
    }

    class GoodsPageAdapter extends PagerAdapter {

        private Context mContext;
        private List<CityDetailBean.ImagesEntity> mDatas;
        private String id;
        private String zhName;

        public GoodsPageAdapter(Context context, List<CityDetailBean.ImagesEntity> datas, String id, String zhName) {
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
            CityDetailBean.ImagesEntity ib = mDatas.get(position);
            ImageLoader.getInstance().displayImage(ib.getUrl(), imageView, UILUtils.getDefaultOption());
            container.addView(imageView, 0);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //MobclickAgent.onEvent(CityInfoActivity.this, "card_item_city_pictures");
                    Intent intent = new Intent(CountryDetailActivity.this, CityPictureActivity.class);
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

    class RemarkAdapter extends RecyclerView.Adapter<RemarkAdapter.ViewHolder> {

        List<Remarks> list;

        public RemarkAdapter(List<Remarks> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_remark, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Remarks remarks = list.get(position);
            holder.mTvContent.setText(remarks.title);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.iv_icon)
            ImageView mIvIcon;
            @Bind(R.id.tv_content)
            TextView mTvContent;
            @Bind(R.id.tv_city_arrow)
            TextView mTvCityArrow;
            @Bind(R.id.container)
            RelativeLayout mContainer;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }

    private class PlayAdapter extends BaseAdapter {
        List<Remarks> list;

        public PlayAdapter(List<Remarks> remarks) {
            list = remarks;
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
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.item_remark, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Remarks remarks = (Remarks) getItem(position);
            viewHolder.mTvContent.setText(remarks.title);
            return convertView;
        }
    }

    class ViewHolder {
        @Bind(R.id.iv_icon)
        ImageView mIvIcon;
        @Bind(R.id.tv_content)
        TextView mTvContent;
        @Bind(R.id.tv_city_arrow)
        TextView mTvCityArrow;
        @Bind(R.id.container)
        RelativeLayout mContainer;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private class SellerAdapter extends RecyclerView.Adapter<SellerViewHolder> {

        List<SellerBean> list;
        Context mContext;
        public SellerAdapter(List<SellerBean> result) {
            list = result;
        }

        @Override
        public SellerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_seller, parent, false);
            return new SellerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SellerViewHolder holder, int position) {
            SellerBean bean = list.get(position);
            if (bean.user.getAvatar()!=null){
                Glide.with(mContext).
                        load(bean.user.getAvatar().getUrl()).
                        into( holder.mIvAvatar);
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class SellerViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_avatar)
        ImageView mIvAvatar;
        SellerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position, long id);
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private List<SimpleCommodityBean> list = new ArrayList<>();
        private Activity mContext;
        private int w;
        private OnItemClickListener listener;
        public Adapter(Activity context,List<SimpleCommodityBean> list) {
            this.mContext = context;
            this.list.addAll(list);
            w = CommonUtils.getScreenWidth((Activity) mContext);
        }

        public List<SimpleCommodityBean> getList() {
            return list;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final TextView tvCurrentPrice;
            public final TextView tvPrice;
            public final TextView tvSales;
            public final ImageView mImageView;
            public final TextView tvGoodsName;
            public final LinearLayout llContainer;
            public ViewHolder(View itemView) {
                super(itemView);
                tvGoodsName = (TextView) itemView.findViewById(R.id.tv_goods_name);
                tvCurrentPrice = (TextView) itemView.findViewById(R.id.tv_goods_current_price);
                tvPrice = (TextView) itemView.findViewById(R.id.tv_goods_price);
                tvSales = (TextView) itemView.findViewById(R.id.tv_goods_sales);
                mImageView = (ImageView) itemView.findViewById(R.id.iv_goods_img);
                llContainer = (LinearLayout) itemView.findViewById(R.id.ll_container);
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
        public void onBindViewHolder(ViewHolder holder, final int position) {

            final SimpleCommodityBean bean = (SimpleCommodityBean) getItem(position);
            ViewGroup.LayoutParams layoutParams = holder.mImageView.getLayoutParams();
            int w1 = w / 2 - 20;
            int h1 = w1 * 2 / 3;
            layoutParams.width = w1;
            layoutParams.height = h1;
            if (bean.getCover()!=null){

                Glide.with(mContext)
                        .load(bean.getCover().getUrl())
                        .placeholder(R.drawable.ic_default_picture)
                        .error(R.drawable.ic_default_picture)
                        .centerCrop()
                        .into(holder.mImageView);
                //     ImageLoader.getInstance().displayImage(bean.getCover().getUrl(), holder.mImageView);
            }else {
                Glide.with(mContext)
                        .load(bean.getCover().getUrl())
                        .placeholder(R.drawable.ic_default_picture)
                        .error(R.drawable.ic_default_picture)
                        .centerCrop()
                        .into(holder.mImageView);
                //       ImageLoader.getInstance().displayImage("", holder.mImageView);
            }
            holder.mImageView.setLayoutParams(layoutParams);
            holder.tvSales.setText( bean.getSalesVolume()+"已售");
            holder.tvPrice.setText(String.format("¥%s", CommonUtils.getPriceString(bean.getMarketPrice())));
            holder.tvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvPrice.getPaint().setAntiAlias(true);
            holder.tvCurrentPrice.setText(String.format("¥%s", CommonUtils.getPriceString(bean.getPrice())));
            holder.tvGoodsName.setText(bean.getTitle());
            holder.llContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        listener.onItemClick(v,position,bean.getCommodityId());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
    }
}
