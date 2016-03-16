package com.xuejian.client.lxp.module.goods;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.http.HttpCallBack;
import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CommentDetailBean;
import com.xuejian.client.lxp.bean.CommodityBean;
import com.xuejian.client.lxp.bean.ImageBean;
import com.xuejian.client.lxp.bean.ShareCommodityBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.ShareUtils;
import com.xuejian.client.lxp.common.widget.GridViewForListView;
import com.xuejian.client.lxp.common.widget.ListViewForScrollView;
import com.xuejian.client.lxp.common.widget.TagView.Tag;
import com.xuejian.client.lxp.common.widget.TagView.TagListView;
import com.xuejian.client.lxp.common.widget.glide.GlideCircleTransform;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.PeachWebViewActivity;
import com.xuejian.client.lxp.module.my.LoginActivity;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.techery.properratingbar.ProperRatingBar;

/**
 * Created by yibiao.qin on 2016/1/20.
 */
public class CommodityDetailActivity extends PeachBaseActivity {
    @Bind(R.id.tv_title_back)
    TextView tvTitleBack;
    @Bind(R.id.tv_commodity_title)
    TextView tvCommodityTitle;
    @Bind(R.id.iv_more)
    ImageView ivShare;
    @Bind(R.id.iv_collection)
    CheckedTextView ivCollection;
    @Bind(R.id.vp_pic)
    ViewPager vpPic;
    @Bind(R.id.tv_commodity_pic_num)
    TextView tvCommodityPicNum;
    @Bind(R.id.fl_commodity_img)
    FrameLayout flCommodityImg;
    @Bind(R.id.tv_commodity_name)
    TextView tvCommodityName;
    @Bind(R.id.tv_goods_comment)
    TextView tvGoodsComment;
    @Bind(R.id.tv_goods_sales)
    TextView tvGoodsSales;
    @Bind(R.id.tv_goods_current_price)
    TextView tvGoodsCurrentPrice;
    @Bind(R.id.tv_goods_price)
    TextView tvGoodsPrice;
    @Bind(R.id.ll_qualification)
    LinearLayout llQualification;
    @Bind(R.id.tv_comm_address)
    TextView tvCommAddress;
    @Bind(R.id.tv_comm_time)
    TextView tvCommTime;
    @Bind(R.id.tv_comm_desc_summery)
    TextView tvCommDescSummery;
    @Bind(R.id.tv_desc_show_all)
    TextView tvDescShowAll;
    @Bind(R.id.tv_book_info_summery)
    TextView tvBookInfoSummery;
    @Bind(R.id.tv_traffic_show_all)
    TextView tvTrafficShowAll;
    @Bind(R.id.tv_commodity_book_summery)
    TextView tvCommodityBookSummery;
    @Bind(R.id.tv_commodity_refund_summery)
    TextView tvCommodityRefundSummery;
    @Bind(R.id.tv_notice_show_all)
    TextView tvNoticeShowAll;
    @Bind(R.id.tv_comm_traffic)
    TextView tvCommTraffic;
    @Bind(R.id.tv_book_show_all)
    TextView tvBookShowAll;
    @Bind(R.id.tv_talk)
    TextView tvTalk;
    @Bind(R.id.tv_store)
    TextView tvStore;
    @Bind(R.id.tv_submit_order)
    TextView tvSubmitOrder;
    @Bind(R.id.tv_retry)
    TextView retry;
    @Bind(R.id.rl_error)
    RelativeLayout rlError;
    @Bind(R.id.empty_view)
    LinearLayout llEmptyView;
    @Bind(R.id.scrollview)
    ScrollView scrollView;
    @Bind(R.id.tv_store_name)
    TextView tvStoreName;
    @Bind(R.id.tv_loc_name)
    TextView tvLocName;
    @Bind(R.id.tag_lang)
    TagListView tagLang;
    @Bind(R.id.ll_service)
    LinearLayout llService;
    @Bind(R.id.ll_snapshot)
    LinearLayout llSnapshot;
    @Bind(R.id.tv_snapshot)
    TextView tvSnapshot;
    @Bind(R.id.ll_service_container)
    LinearLayout ll_service_container;
    @Bind(R.id.ll_action)
    LinearLayout llAction;
    @Bind(R.id.lv_comment)
    ListViewForScrollView lvComment;
    @Bind(R.id.rb_goods)
    ProperRatingBar ratingBar;
    @Bind(R.id.tv_comment_show_all)
    TextView tvCommentShowAll;
    @Bind(R.id.ll_comment)
    LinearLayout ll_comment;
    @Bind(R.id.empty_comment)
    TextView empty_comment;
    @Bind(R.id.tv_comment)
    TextView tv_comment;
    @Bind(R.id.line)
    View line;
    @Bind(R.id.ll_trade_action)
    LinearLayout ll_trade_action;
    @Bind(R.id.tv_trade_state)
    TextView tv_trade_state;
    @Bind(R.id.tv_trade_action)
    TextView tv_trade_action;
    private long commodityId;
    private long userId;
    private boolean isSeller;
    public CommodityBean bean;
    boolean snapshots;
    private int[] lebelColors = new int[]{
            R.drawable.all_light_green_label,
            R.drawable.all_light_red_label,
            R.drawable.all_light_perple_label,
            R.drawable.all_light_blue_label,
            R.drawable.all_light_yellow_label
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_detail);
        ButterKnife.bind(this);
        commodityId = getIntent().getLongExtra("commodityId", -1);
        snapshots = getIntent().getBooleanExtra("snapshots", false);
        isSeller = getIntent().getBooleanExtra("isSeller",false);
        final long version = getIntent().getLongExtra("version", -1);
        Uri uri = getIntent().getData();
        if (uri != null) {
            if (TextUtils.isDigitsOnly(uri.getLastPathSegment())) {
                commodityId = Long.parseLong(uri.getLastPathSegment());
            }
        }
        User user = AccountManager.getInstance().getLoginAccount(mContext);
        if (user != null) {
            userId = user.getUserId();
        }

        final long finalCommodityId = commodityId;
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlError.setVisibility(View.GONE);
                getData(finalCommodityId, version, snapshots);
            }
        });
        if (snapshots) {
            tvCommodityTitle.setText("交易快照");
        }
        getData(commodityId, version, snapshots);

        tvTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getData(final long commodityId, final long version, final boolean snapshots) {
        if (commodityId <= 0) return;
        TravelApi.getCommodity(commodityId, version, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                try {
                    CommonJson<CommodityBean> commodity = CommonJson.fromJson(result, CommodityBean.class);
                    scrollView.setVisibility(View.VISIBLE);
                    bean = commodity.result;
                    bindView(bean);

                } catch (Exception e) {
                    e.printStackTrace();
                    scrollView.setVisibility(View.GONE);
                    llEmptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {
                if (code == 404) {
                    scrollView.setVisibility(View.GONE);
                    llEmptyView.setVisibility(View.VISIBLE);
                } else if (code == -1) {
                    scrollView.setVisibility(View.GONE);
                    rlError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void bindView(final CommodityBean bean) {
        ViewGroup.LayoutParams params = vpPic.getLayoutParams();
        int w = CommonUtils.getScreenWidth(this);
        params.width = w;
        params.height = w / 2;
        vpPic.setLayoutParams(params);
        tvCommodityPicNum.setText("1/" + bean.getImages().size());
        GoodsPageAdapter adapter = new GoodsPageAdapter(this, bean.getImages());
        vpPic.setAdapter(adapter);
        vpPic.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvCommodityPicNum.setText((position + 1) + "/" + bean.getImages().size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        tvCommodityName.setText(bean.getTitle());
        tvGoodsSales.setText(String.valueOf(bean.getSalesVolume()) + "已售");
        tvGoodsComment.setText(String.valueOf((int) (bean.getRating() * 100)) + "%满意");
        ratingBar.setRating((int) (bean.getRating() * 5));
        SpannableString string = new SpannableString("起");
        string.setSpan(new AbsoluteSizeSpan(12, true), 0, 1,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder spb = new SpannableStringBuilder();
        spb.append("¥" + CommonUtils.getPriceString(bean.getPrice())).append(string);
        tvGoodsCurrentPrice.setText(spb);

        tvGoodsPrice.setText("¥" + CommonUtils.getPriceString(bean.getMarketPrice()));
        tvGoodsPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tvGoodsPrice.getPaint().setAntiAlias(true);

        tvCommAddress.setText("详细地址：" + bean.getCountry().zhName + bean.getLocality().zhName + bean.getAddress());
        tvCommTime.setText("游玩时长：" + bean.getTimeCost() + "小时");
        if (!TextUtils.isEmpty(bean.getDesc().getSummary())) {
            tvCommDescSummery.setText(bean.getDesc().getSummary());
        } else {
            tvCommDescSummery.setText("相关内容请咨询卖家");
        }
        tvDescShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("title", "商品介绍");
                intent.putExtra("url", bean.getDescUrl());
                intent.putExtra("showAnim",true);
                intent.setClass(CommodityDetailActivity.this, PeachWebViewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
            }
        });


        if (bean.getNotice().size() > 0 && !TextUtils.isEmpty(bean.getNotice().get(0).getSummary())) {
            tvBookInfoSummery.setText(bean.getNotice().get(0).getSummary());
        } else {
            tvBookInfoSummery.setText("相关内容请咨询卖家");
        }
        tvNoticeShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("title", "购买须知");
                intent.putExtra("url", bean.getNoticeUrl());
                intent.putExtra("showAnim",true);
                intent.setClass(CommodityDetailActivity.this, PeachWebViewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
            }
        });

        if (bean.getRefundPolicy().size() > 0 && !TextUtils.isEmpty(bean.getRefundPolicy().get(0).getSummary())) {
            tvCommodityBookSummery.setText(bean.getRefundPolicy().get(0).getSummary());
        } else {
            tvCommodityBookSummery.setText("相关内容请咨询卖家");
        }
        if (bean.getRefundPolicy().size() > 1 && !TextUtils.isEmpty(bean.getRefundPolicy().get(1).getSummary())) {
            tvCommodityRefundSummery.setText(bean.getRefundPolicy().get(1).getSummary());
        } else {
            tvCommodityRefundSummery.setText("相关内容请咨询卖家");
        }
        tvBookShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("title", "预定及退改");
                intent.putExtra("url", bean.getRefundPolicyUrl());
                intent.putExtra("showAnim",true);
                intent.setClass(CommodityDetailActivity.this, PeachWebViewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
            }
        });


        if (bean.getTrafficInfo().size() > 0 && !TextUtils.isEmpty(bean.getTrafficInfo().get(0).getSummary())) {
            tvCommTraffic.setText(bean.getTrafficInfo().get(0).getSummary());
        } else {
            tvCommTraffic.setText("相关内容请咨询卖家");
        }
        tvTrafficShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("title", "交通提示");
                intent.putExtra("url", bean.getTrafficInfoUrl());
                intent.putExtra("showAnim",true);
                intent.setClass(CommodityDetailActivity.this, PeachWebViewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
            }
        });

        tvTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(CommodityDetailActivity.this,"event_chatWithSeller");
                Intent intent = new Intent();
                if (AccountManager.getInstance().getLoginAccount(CommodityDetailActivity.this) == null) {
                    intent.putExtra("isFromGoods", true);
                    intent.setClass(CommodityDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    ShareCommodityBean shareCommodityBean = bean.creteShareBean();
                    intent.putExtra("friend_id", String.valueOf(bean.getSeller().getSellerId()));
                    intent.putExtra("chatType", "single");
                    intent.putExtra("shareCommodityBean", shareCommodityBean);
                    intent.putExtra("fromTrade", true);
                    intent.setClass(CommodityDetailActivity.this, ChatActivity.class);
                    startActivity(intent);
                }
            }
        });
        tvStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(CommodityDetailActivity.this,"event_gotoStoreDetail");
                Intent intent = new Intent();
                intent.setClass(CommodityDetailActivity.this, StoreDetailActivity.class);
                intent.putExtra("sellerId", String.valueOf(bean.getSeller().getSellerId()));
                startActivity(intent);
            }
        });
        tvSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(CommodityDetailActivity.this,"event_buyGoods");
                Intent intent = new Intent();
                if (AccountManager.getInstance().getLoginAccount(CommodityDetailActivity.this) == null) {
                    intent.putExtra("isFromGoods", true);
                    intent.setClass(CommodityDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    intent.putExtra("planList", bean.getPlans());
                    intent.putExtra("commodityId", String.valueOf(bean.getCommodityId()));
                    intent.putExtra("name", bean.getTitle());
                    intent.setClass(CommodityDetailActivity.this, OrderCreateActivity.class);
                    startActivity(intent);
                }
            }
        });


        if (!snapshots) {
            ivCollection.setVisibility(View.VISIBLE);
            ivShare.setVisibility(View.VISIBLE);
        }
        ivCollection.setChecked(bean.isIsFavorite());
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountManager.getInstance().getLoginAccount(CommodityDetailActivity.this) == null) {
                    Intent intent = new Intent();
                    intent.putExtra("isFromGoods", true);
                    intent.setClass(CommodityDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    ShareUtils.showSelectPlatformDialog(CommodityDetailActivity.this, null, bean.getShareUrl(), bean.creteShareBean());
                }
            }
        });
        ivCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AccountManager.getInstance().getLoginAccount(CommodityDetailActivity.this) == null) {
                    Intent intent = new Intent();
                    intent.putExtra("isFromGoods", true);
                    intent.setClass(CommodityDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    if (userId != -1) {
                        changeCollection(ivCollection.isChecked(), bean.getId());
                    }
                }
            }
        });

        if (bean.getSeller().services.size() > 0) {
            ll_service_container.setVisibility(View.VISIBLE);
            for (String s : bean.getSeller().services) {
                TextView textView = (TextView) getLayoutInflater().inflate(R.layout.textview_service, null);
                textView.setText(s);
                textView.setPadding(5, 0, 5, 0);
                llService.addView(textView);
            }
        } else {
            ll_service_container.setVisibility(View.GONE);
        }


        tagLang.setmTagViewResId(R.layout.expert_tag);
        tagLang.removeAllViews();
        tagLang.addTags(initTagData(bean.getSeller().getLang()));

        tvStoreName.setText(bean.getSeller().getName());
        tvLocName.setText(bean.getLocality().zhName);


        if (bean.getSeller().qualifications.size() > 0) {
            llQualification.setVisibility(View.VISIBLE);
        } else {
            llQualification.setVisibility(View.GONE);
        }

        if (snapshots) {
            llSnapshot.setVisibility(View.VISIBLE);
            SpannableString spannableString = new SpannableString("点击查看最新商品详情");
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableString.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            SpannableStringBuilder spb1 = new SpannableStringBuilder();
            spb1.append("您现在查看的是交易快照,").append(spannableString);
            tvSnapshot.setText(spb1);
            llSnapshot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("commodityId", bean.getCommodityId());
                    intent.setClass(CommodityDetailActivity.this, CommodityDetailActivity.class);
                    startActivity(intent);
                }
            });
            llAction.setVisibility(View.GONE);
        } else {
            llSnapshot.setVisibility(View.GONE);
        }

        if (bean.comments.size()>0){
            line.setVisibility(View.VISIBLE);
            empty_comment.setVisibility(View.GONE);
            tv_comment.setText(String.format("用户评价 (%d人，%d分)",bean.commentCnt,(int)(bean.getRating()*5)));
            lvComment.setAdapter(new CommentAdapter(this,bean.comments));
            tvCommentShowAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(CommodityDetailActivity.this,CommentListActivity.class);
                    intent.putExtra("commodityId",bean.getCommodityId());
                    startActivity(intent);
                }
            });
        }else {
            line.setVisibility(View.GONE);
            tv_comment.setVisibility(View.GONE);
            ll_comment.setVisibility(View.GONE);
            empty_comment.setVisibility(View.VISIBLE);
        }

        if (isSeller){
            llAction.setVisibility(View.GONE);
            ll_trade_action.setVisibility(View.VISIBLE);
            switch (bean.status) {
                case "pub":
                    tv_trade_state.setText("已发布");
                    tv_trade_action.setText("下架");
                    break;
                case "review":
                    tv_trade_state.setText("审核中");
                    tv_trade_action.setVisibility(View.INVISIBLE);
                    break;
                case "disabled":
                    tv_trade_state.setText("已下架");
                    tv_trade_action.setText("上架");
                    break;
                default:
                    break;
            }

            tv_trade_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (tv_trade_action.getText().toString()) {
                        case "下架":
                            editCommodity("disabled",bean.getCommodityId(),"商品已下架");
                            break;
                        case "上架":
                            editCommodity("pub",bean.getCommodityId(),"商品已发布");
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        scrollView.smoothScrollTo(0,0);
    }


    public void editCommodity(String status,long commodityId,final String notice){
        TravelApi.editCommodityStatus(commodityId, status, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                Toast.makeText(mContext,notice,Toast.LENGTH_LONG).show();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("page_goodsDetail");
        MobclickAgent.onResume(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("page_goodsDetail");
        MobclickAgent.onPause(this);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_avatar)
        ImageView ivAvatar;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_timestamp)
        TextView tvTimestamp;
        @Bind(R.id.rb_comment)
        ProperRatingBar rbComment;
        @Bind(R.id.tv_comment)
        TextView tvComment;
        @Bind(R.id.gv_comment_pic)
        GridViewForListView gvCommentPic;
        @Bind(R.id.tv_package)
        TextView tvPackage;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    public class CommentAdapter extends BaseAdapter{
        private ArrayList<CommentDetailBean> list;
        private Context context;
        public CommentAdapter(Context context, ArrayList<CommentDetailBean> list) {
            this.list=list;
            this.context = context;
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
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView==null){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_comment,null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            CommentDetailBean bean = (CommentDetailBean) getItem(position);
            viewHolder.rbComment.setRating((int)(bean.getRating()*5));
            viewHolder.tvComment.setText(bean.getContents());
            viewHolder.tvTimestamp.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(bean.getCreateTime())));
            if (bean.images.size()>0){
                viewHolder.gvCommentPic.setAdapter(new CommentPicAdapter(CommodityDetailActivity.this));
            }
            if (bean.anonymous){
                viewHolder.tvName.setText("*****");
            }else {
                if (bean.getUser()!=null){
                    if (bean.getUser().getAvatar()!=null){
                        Glide.with(mContext)
                                .load(bean.getUser().getAvatar().url)
                                .placeholder(R.drawable.ic_home_more_avatar_unknown_round)
                                .error(R.drawable.ic_home_more_avatar_unknown_round)
                                .centerCrop()
                                .transform(new GlideCircleTransform(mContext))
                                .into(viewHolder.ivAvatar);
                    }
                    viewHolder.tvName.setText(bean.getUser().getNickname());
                }else {
                    viewHolder.tvName.setText("*****");
                }
            }
            if (bean.order.getCommodity()!=null&&bean.order.getCommodity().getPlans().size()>0){
                viewHolder.tvPackage.setText("套餐类型："+bean.order.getCommodity().getPlans().get(0).getTitle());
            }

            return convertView;
        }
    }
    public  class CommentPicAdapter extends BaseAdapter {


        Activity activity;

        public CommentPicAdapter(Activity activity) {
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return 8;
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
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(activity).inflate(R.layout.all_pics_cell, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ViewGroup.LayoutParams layoutParams = holder.allPicsCellId.getLayoutParams();
            layoutParams.width = (CommonUtils.getScreenWidth(activity)-75) / 6;
            layoutParams.height = (CommonUtils.getScreenWidth(activity)-75) / 6;
            holder.allPicsCellId.setLayoutParams(layoutParams);
            Glide.with(mContext)
                    .load("http://7sbm17.com1.z0.glb.clouddn.com/commodity/images/f074adb29e1d39a184a02320a3aff555")
                    .placeholder(R.drawable.ic_default_picture)
                    .error(R.drawable.ic_default_picture)
                    .centerCrop()
                    .into(holder.allPicsCellId);
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.all_pics_cell_id)
            ImageView allPicsCellId;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
    public void changeCollection(boolean isCollection, String id) {
        if (isCollection) {
            UserApi.delFav(String.valueOf(userId), id, "commodity", new HttpCallBack<String>() {

                @Override
                public void doSuccess(String result, String method) {
                    Toast.makeText(mContext, "收藏已取消", Toast.LENGTH_SHORT).show();
                    ivCollection.setChecked(false);
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {

                }

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });
        } else {
            UserApi.addFav(String.valueOf(userId), id, "commodity", new HttpCallBack<String>() {

                @Override
                public void doSuccess(String result, String method) {
                    Toast.makeText(mContext, "收藏成功", Toast.LENGTH_SHORT).show();
                    ivCollection.setChecked(true);
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {

                }

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });
        }


    }


    class GoodsPageAdapter extends PagerAdapter {

        private Context mContext;
        private List<ImageBean> mDatas;
        private SparseArray<View> mViews;

        public GoodsPageAdapter(Context context, List<ImageBean> datas) {
            mDatas = datas;
            mContext = context;
            mViews = new SparseArray<View>(datas.size());
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        public List<ImageBean> getmDatas() {
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
                Glide.with(mContext)
                        .load(mDatas.get(position).url)
                        .placeholder(R.drawable.ic_default_picture)
                        .error(R.drawable.ic_default_picture)
                        .centerCrop()
                        .into(imageView);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IMUtils.onShareResult(mContext, bean.creteShareBean(), requestCode, resultCode, data, null);
    }

    private List<Tag> initTagData(List<String> lang) {
        List<Tag> mTags = new ArrayList<Tag>();
        int lastColor = new Random().nextInt(4);
        for (int i = 0; i < lang.size(); i++) {
            Tag tag = new Tag();
            tag.setTitle(lang.get(i));
            tag.setId(i);
            tag.setBackgroundResId(lebelColors[lastColor]);
            //    tag.setBackgroundResId(R.drawable.all_whitesolid_greenline);
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
}
