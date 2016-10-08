package com.xuejian.client.lxp.module.goods;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.ServiceZonesEntity;
import com.xuejian.client.lxp.bean.SimpleCommodityBean;
import com.xuejian.client.lxp.bean.StoreBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.TagView.Tag;
import com.xuejian.client.lxp.common.widget.TagView.TagListView;
import com.xuejian.client.lxp.common.widget.glide.GlideCircleTransform;
import com.xuejian.client.lxp.module.PeachWebViewActivity;
import com.xuejian.client.lxp.module.my.LoginActivity;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.techery.properratingbar.ProperRatingBar;

public class StoreDetailActivityV2 extends PeachBaseActivity {
    RelativeLayout rlTalk;
    LinearLayout service;
    TagListView langTag;
    LinearLayout qualification;
    TextView tvStoreName;
    TextView tvLocName;
    TextView tvTitle;
    @Bind(R.id.tv_title_back)
    TextView mTvTitleBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.iv_avatar)
    ImageView mIvAvatar;
    @Bind(R.id.tv_level)
    TextView mTvLevel;
    @Bind(R.id.tv_sales_count)
    TextView mTvSalesCount;
    @Bind(R.id.tv_sales)
    TextView mTvSales;
    @Bind(R.id.tv_store_name)
    TextView mTvStoreName;
    @Bind(R.id.tv_loc)
    TextView mTvLocName;
    @Bind(R.id.ll_intro)
    LinearLayout ll_intro;
    private int[] lebelColors = new int[]{
            R.drawable.all_light_green_label,
            R.drawable.all_light_red_label,
            R.drawable.all_light_perple_label,
            R.drawable.all_light_blue_label,
            R.drawable.all_light_yellow_label
    };
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    String sellerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail_activity_v2);
        ButterKnife.bind(this);
        sellerId = getIntent().getStringExtra("sellerId");
        tvTitle = (TextView) findViewById(R.id.tv_title);
        langTag = (TagListView) findViewById(R.id.tag_lang);
        service = (LinearLayout) findViewById(R.id.ll_service);
        qualification = (LinearLayout) findViewById(R.id.ll_qualification);
        tvStoreName = (TextView) findViewById(R.id.tv_store_name);
        tvLocName = (TextView) findViewById(R.id.tv_loc_name);
        rlTalk = (RelativeLayout) findViewById(R.id.ll_trade_action0);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        rlTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountManager.getInstance().getLoginAccount(StoreDetailActivityV2.this) == null) {
                    Intent intent = new Intent();
                    intent.putExtra("isFromGoods", true);
                    intent.setClass(StoreDetailActivityV2.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent talkIntent = new Intent(mContext, ChatActivity.class);
                    talkIntent.putExtra("friend_id", sellerId);
                    talkIntent.putExtra("chatType", "single");
                    startActivity(talkIntent);
                }
            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabTextColors(getResources().getColor(R.color.color_text_ii), getResources().getColor(R.color.app_theme_color));
        getData(sellerId);
    }

    private void getData(String sellerId) {
        if (sellerId == null) return;

        TravelApi.getSellerInfo(Long.parseLong(sellerId), new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson<StoreBean> commonJson = CommonJson.fromJson(result, StoreBean.class);
                if (commonJson.code == 0) {
                    bindView(commonJson.result);
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

    private void bindView(final StoreBean bean) {

        for (String s : bean.getServices()) {
            TextView textView = (TextView) getLayoutInflater().inflate(R.layout.textview_service, null);
            textView.setText(s);
            textView.setPadding(5, 0, 5, 0);
            service.addView(textView);
        }

        langTag.setmTagViewResId(R.layout.expert_tag);
        langTag.removeAllViews();
        langTag.addTags(initTagData(bean.getLang()));

        tvTitle.setText(bean.getName());
        tvStoreName.setText(bean.getName());
        StringBuilder sb = new StringBuilder();
        for (ServiceZonesEntity entity : bean.getServiceZones()) {
            sb.append(entity.getZhName()).append("  ");
        }
        tvLocName.setText(sb);

        if (bean.getQualifications().size() > 0) {
            qualification.setVisibility(View.VISIBLE);
        } else {
            qualification.setVisibility(View.GONE);
        }

        if (mSectionsPagerAdapter.getInfoFragment() != null) {
            mSectionsPagerAdapter.getInfoFragment().bindView(bean);
        }

            Glide.with(mContext)
                    .load("")
                    .placeholder(R.drawable.ic_default_picture)
                    .error(R.drawable.ic_default_picture)
                    .centerCrop()
                    .transform(new GlideCircleTransform(mContext))
                    .into(mIvAvatar);

        mTvLevel.setText("L" + bean.level);
        mTvSales.setText(String.format("销售额：%s", CommonUtils.getPriceString(bean.lastSalesMoney)));
        mTvSalesCount.setText(String.format("销售量：%d", bean.lastSalesVolume));
        if (!TextUtils.isEmpty(bean.getAddress())){
            mTvLocName.setText(String.format("位置：%s", bean.getAddress()));
        }else {
            mTvLocName.setVisibility(View.INVISIBLE);
        }

        ll_intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoreDetailActivityV2.this, PeachWebViewActivity.class);
                intent.putExtra("url",bean.introduceURL);
                intent.putExtra("title",bean.getName());
                startActivity(intent);
            }
        });
    }

    private List<Tag> initTagData(List<String> lang) {
        List<Tag> mTags = new ArrayList<Tag>();
        int lastColor = new Random().nextInt(4);
        for (int i = 0; i < lang.size(); i++) {
            Tag tag = new Tag();
            tag.setTitle(lang.get(i));
            tag.setId(i);
            tag.setBackgroundResId(lebelColors[lastColor]);
            tag.setTextColor(R.color.white);
            mTags.add(tag);
            lastColor = getNextColor(lastColor);
        }
        return mTags;
    }

    public int getNextColor(int currentcolor) {
        Random random = new Random();
        int nextValue = random.nextInt(4);
        if (nextValue == 0) {
            nextValue++;
        }
        return (nextValue + currentcolor) % 5;
    }

    public static class InfoFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        @Bind(R.id.tv_current_drawback)
        TextView mTvCurrentDrawback;
        @Bind(R.id.tv_last_drawback)
        TextView mTvLastDrawback;
        @Bind(R.id.tv_current_argument)
        TextView mTvCurrentArgument;
        @Bind(R.id.tv_last_argument)
        TextView mTvLastArgument;
        @Bind(R.id.tv_current_punch)
        TextView mTvCurrentPunch;
        @Bind(R.id.tv_last_punch)
        TextView mTvLastPunch;
        @Bind(R.id.tv_store_info)
        TextView mTvStoreInfo;
        @Bind(R.id.tv_service)
        TextView mTvService;
        @Bind(R.id.tv_service_city)
        TextView mTvServiceCity;
        @Bind(R.id.rb_taidu)
        ProperRatingBar mRbTaidu;
        @Bind(R.id.rb_chengxin)
        ProperRatingBar mRbChengxin;
        @Bind(R.id.rb_good_rating)
        ProperRatingBar mRbGoodRating;

        public InfoFragment() {
        }

        public static InfoFragment newInstance(int sectionNumber, String id) {
            InfoFragment fragment = new InfoFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION_NUMBER, id);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_seller_detail, container, false);
            ButterKnife.bind(this, rootView);
            if (bean != null) {
                bindView(bean);
            }
            return rootView;
        }

        StoreBean bean;

        public void bindView(StoreBean bean) {
            this.bean = bean;
            if (mTvServiceCity == null) return;
            mTvCurrentDrawback.setText(bean.refundCnt + "");
            mTvLastDrawback.setText(bean.lastRefundCnt + "");
            mTvCurrentArgument.setText(bean.disputeRate + "");
            StringBuilder sb = new StringBuilder();
            for (LocBean entity : bean.subLocalities) {
                sb.append(entity.zhName).append("  ");
            }
            mTvServiceCity.setText(sb);
            mRbGoodRating.setRating((int) (bean.goodRate*5));
            mRbTaidu.setRating((int) (bean.satisfactionRate*5));
            mRbChengxin.setRating((int) (bean.goodRate*5));
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            ButterKnife.unbind(this);
        }
    }

    public static class ProjectFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public ProjectFragment() {
        }

        public static ProjectFragment newInstance(int sectionNumber, String id) {
            ProjectFragment fragment = new ProjectFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION_NUMBER, id);
            fragment.setArguments(args);
            return fragment;
        }
        XRecyclerView recyclerView;
        Adapter adapter;
        ImageView empty;
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_store_detail_activity_v2, container, false);
            recyclerView = (XRecyclerView) rootView.findViewById(R.id.ul_recyclerView);
            empty = (ImageView) rootView.findViewById(R.id.iv_empty);
            adapter = new Adapter(getActivity());
            recyclerView.setPullRefreshEnabled(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            getCommodity(getArguments().getString(ARG_SECTION_NUMBER, ""));
            return rootView;
        }

        private void getCommodity(String id) {
            TravelApi.getCommodityList(id, null, null, null, null, null, String.valueOf("0"), String.valueOf(10),true, new HttpCallBack<String>() {

                @Override
                public void doSuccess(String result, String method) {
                    CommonJson4List<SimpleCommodityBean> list = CommonJson4List.fromJson(result, SimpleCommodityBean.class);
                    if (list.result.size()==0)empty.setVisibility(View.VISIBLE);
                    adapter.getList().addAll(list.result);
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

        public interface OnItemClickListener {
            void onItemClick(View view, int position, long id);
        }

        public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

            private ArrayList<SimpleCommodityBean> list;
            private Activity mContext;
            private int w;
            private OnItemClickListener listener;

            public Adapter(Activity context) {
                this.mContext = context;
                list = new ArrayList<>();
            }

            public ArrayList<SimpleCommodityBean> getList() {
                return list;
            }

            public class ViewHolder extends RecyclerView.ViewHolder {
                public final TextView tvPrice;
                public final TextView tvGoodsName;
                public final ImageView iv;
                public ViewHolder(View itemView) {
                    super(itemView);
                    tvGoodsName = (TextView) itemView.findViewById(R.id.tv_project_name);
                    tvPrice = (TextView) itemView.findViewById(R.id.tv_project_price);
                    iv = (ImageView) itemView.findViewById(R.id.iv_bg);
                }
            }

            public Object getItem(int pos) {
                return list.get(pos);
            }


            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_store_project, parent, false);
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, final int position) {
                SimpleCommodityBean bean = (SimpleCommodityBean) getItem(position);
                if (bean.getCover() != null) {
                    Glide.with(mContext)
                            .load(bean.getCover().getUrl())
                            .placeholder(R.drawable.ic_default_picture)
                            .error(R.drawable.ic_default_picture)
                            .centerCrop()
                            .into(holder.iv);
                } else {
                    Glide.with(mContext)
                            .load("")
                            .placeholder(R.drawable.ic_default_picture)
                            .error(R.drawable.ic_default_picture)
                            .centerCrop()
                            .into(holder.iv);
                }
                holder.tvGoodsName.setText(bean.getTitle());
                holder.tvPrice.setText(String.format("%s元",CommonUtils.getPriceString(bean.getPrice())));
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

    public static class CommodityFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public CommodityFragment() {
        }

        public static CommodityFragment newInstance(int sectionNumber, String id) {
            CommodityFragment fragment = new CommodityFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION_NUMBER, id);
            fragment.setArguments(args);
            return fragment;
        }

        XRecyclerView recyclerView;
        Adapter adapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_store_detail_activity_v2, container, false);
            recyclerView = (XRecyclerView) rootView.findViewById(R.id.ul_recyclerView);
            adapter = new Adapter(getActivity());
            recyclerView.setPullRefreshEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            recyclerView.setAdapter(adapter);
            getCommodity(getArguments().getString(ARG_SECTION_NUMBER, ""));
            return rootView;
        }

        private void getCommodity(String id) {
            TravelApi.getCommodityList(id, null, null, null, null, null, String.valueOf("0"), String.valueOf(4),false, new HttpCallBack<String>() {

                @Override
                public void doSuccess(String result, String method) {
                    CommonJson4List<SimpleCommodityBean> list = CommonJson4List.fromJson(result, SimpleCommodityBean.class);
                    adapter.getList().addAll(list.result);
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

        public interface OnItemClickListener {
            void onItemClick(View view, int position, long id);
        }

        public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

            private ArrayList<SimpleCommodityBean> list;
            private Activity mContext;
            private int w;
            private OnItemClickListener listener;

            public Adapter(Activity context) {
                this.mContext = context;
                list = new ArrayList<>();
                w = CommonUtils.getScreenWidth((Activity) mContext);
            }

            public ArrayList<SimpleCommodityBean> getList() {
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
                if (bean.getCover() != null) {
                    Glide.with(mContext)
                            .load(bean.getCover().getUrl())
                            .placeholder(R.drawable.ic_default_picture)
                            .error(R.drawable.ic_default_picture)
                            .centerCrop()
                            .into(holder.mImageView);
                } else {
                    Glide.with(mContext)
                            .load("")
                            .placeholder(R.drawable.ic_default_picture)
                            .error(R.drawable.ic_default_picture)
                            .centerCrop()
                            .into(holder.mImageView);
                }
                holder.mImageView.setLayoutParams(layoutParams);
                holder.tvSales.setText(bean.getSalesVolume() + "已售");
                holder.tvPrice.setText(String.format("¥%s", CommonUtils.getPriceString(bean.getMarketPrice())));
                holder.tvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tvPrice.getPaint().setAntiAlias(true);
                holder.tvCurrentPrice.setText(String.format("¥%s", CommonUtils.getPriceString(bean.getPrice())));
                holder.tvGoodsName.setText(bean.getTitle());
                holder.llContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onItemClick(v, position, bean.getCommodityId());
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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        CommodityFragment mCommodityFragment;
        ProjectFragment mProjectFragment;
        InfoFragment mInfoFragment;

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return CommodityFragment.newInstance(position + 1, sellerId);
                case 1:
                    return ProjectFragment.newInstance(position + 1, sellerId);
                case 2:
                    if (mInfoFragment == null) {
                        mInfoFragment = InfoFragment.newInstance(position + 1, sellerId);
                    }
                    return mInfoFragment;
            }
            return null;
        }

        public InfoFragment getInfoFragment() {
            if (mInfoFragment == null) {
                mInfoFragment = InfoFragment.newInstance(3, sellerId);
            }
            return mInfoFragment;
        }

        @Override
        public int getCount() {
            return 3;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "在售商品";
                case 1:
                    return "行程方案";
                case 2:
                    return "店铺详情";
            }
            return null;
        }
    }
}
