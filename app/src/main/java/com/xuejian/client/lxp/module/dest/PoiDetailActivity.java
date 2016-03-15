package com.xuejian.client.lxp.module.dest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CommentBean;
import com.xuejian.client.lxp.bean.ImageBean;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.RecommendBean;
import com.xuejian.client.lxp.bean.TipsBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.IntentUtils;
import com.xuejian.client.lxp.common.widget.GridViewForListView;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.PeachWebViewActivity;
import com.xuejian.client.lxp.module.my.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.techery.properratingbar.ProperRatingBar;

/**
 * Created by Rjm on 2014/11/22.
 */
public class PoiDetailActivity extends PeachBaseActivity {
    ListView mCommentsList;
    View headerView;
    private String id;
    PoiDetailBean poiDetailBean;
    private String type;
    ListViewDataAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.spot_detail_list);
        mCommentsList = (ListView) findViewById(R.id.spot_detail_list);
        headerView = View.inflate(this, R.layout.view_poi_detail_header, null);
        mCommentsList.addHeaderView(headerView);
        commentAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                return new CommentViewHolder(PoiDetailActivity.this);
            }
        });

        findViewById(R.id.poi_det_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCommentsList.setAdapter(commentAdapter);
    }

    private void initData() {
        id = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");
        getDetailData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //MobclickAgent.onPageStart("page_poi_detai");
        //MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //MobclickAgent.onPageEnd("page_poi_detai");
        //MobclickAgent.onPause(this);
    }


    private void getDetailData() {
        try {
            DialogManager.getInstance().showModelessLoadingDialog(mContext);
        } catch (Exception e) {
            DialogManager.getInstance().dissMissModelessLoadingDialog();
        }
        TravelApi.getPoiDetail(type, id, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                CommonJson<PoiDetailBean> detailBean = CommonJson.fromJson(result, PoiDetailBean.class);
                if (detailBean.code == 0) {
                    poiDetailBean = detailBean.result;
                    bindView(poiDetailBean);
                } else {
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                if (!isFinishing()) {
                    DialogManager.getInstance().dissMissModelessLoadingDialog();
                    ToastUtil.getInstance(PoiDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

    }

    private void bindView(final PoiDetailBean bean) {
        //标题
        final TextView titleView = (TextView) findViewById(R.id.poi_det_title);
        titleView.setText(bean.zhName);

        findViewById(R.id.iv_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = AccountManager.getInstance().getLoginAccount(PoiDetailActivity.this);
                if (user != null) {
                    IMUtils.onClickImShare(PoiDetailActivity.this);

                } else {
                    ToastUtil.getInstance(PoiDetailActivity.this).showToast("请先登录");
                    Intent intent = new Intent(PoiDetailActivity.this, LoginActivity.class);
                    intent.putExtra("isFromTalkShare", true);
                    startActivity(intent);
                }

                //MobclickAgent.onEvent(mContext, "navigation_item_poi_lxp_share");
           //     IMUtils.onClickImShare(PoiDetailActivity.this);
            }
        });
        final TextView pic_num = (TextView) headerView.findViewById(R.id.tv_commodity_pic_num);

        pic_num.setText("0/0");
        //图片
        ViewPager vp = (ViewPager) headerView.findViewById(R.id.vp_pic);
        ViewGroup.LayoutParams params = vp.getLayoutParams();
        params.width = CommonUtils.getScreenWidth(this);
        params.height = CommonUtils.getScreenWidth(this)*2/3;
        vp.setLayoutParams(params);
        if (bean.images != null) {

            //  final DotView dotview = (DotView) findViewById(R.id.dot_view);
            final int pc = bean.images.size();
            if (pc > 1) {
                pic_num.setText("1/" + pc);
                //     dotview.setNum(bean.images.size());
                vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        pic_num.setText((position + 1) + "/" + pc);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
            } else {
                pic_num.setText("1/1");
            }
            if (bean.images.size() == 0) {
                pic_num.setText("0/0");
                ImageBean imageBean = new ImageBean();
                imageBean.url = "";
                bean.images.add(imageBean);
                vp.setAdapter(new POIImageVPAdapter(this, bean.images));
            } else {
                vp.setAdapter(new POIImageVPAdapter(this, bean.images));
            }

        }

        //评分、类型、排名
        ProperRatingBar rb = (ProperRatingBar) headerView.findViewById(R.id.rb_poi);
        rb.setRating((int) bean.getRating());
        TextView styleTV = (TextView) headerView.findViewById(R.id.tv_poi);
        if ("vs".equals(bean.type)){
           styleTV.setBackgroundResource(0);
            styleTV.setTextColor(getResources().getColor(R.color.color_text_ii));
            if (TextUtils.isEmpty(bean.timeCostDesc)){
                styleTV.setVisibility(View.INVISIBLE);
            }else {
                styleTV.setText("推荐游玩："+bean.timeCostDesc);
            }

        }else {
           styleTV.setBackgroundResource(R.drawable.bg_common_theme_color_solid);
           styleTV.setTextColor(getResources().getColor(R.color.base_color_white));
            if (bean.style.size() > 0) {
                styleTV.setText(bean.style.get(0));
            } else {
                styleTV.setVisibility(View.INVISIBLE);
            }
        }


        //费用
        TextView ptv = (TextView) headerView.findViewById(R.id.tv_cost);
        if (TextUtils.isEmpty(bean.priceDesc)) {
            ptv.setText("费用  未知");
        } else {
            ptv.setText("费用  " + bean.priceDesc);
        }

        //开放时间
        TextView rttv = (TextView) headerView.findViewById(R.id.tv_open_time);
        if (TextUtils.isEmpty(bean.openTime)) {
            rttv.setText("开放  全天");
        } else {
            rttv.setText("开放  " + bean.openTime);
        }

        //简介
        final TextView descView = (TextView) headerView.findViewById(R.id.tv_info_1);
        final String desc = bean.desc;
        if (TextUtils.isEmpty(desc)) {
            headerView.findViewById(R.id.rl_info).setVisibility(View.GONE);
            headerView.findViewById(R.id.ll_info).setVisibility(View.GONE);
        } else {
            descView.setText(desc);
        }
        headerView.findViewById(R.id.rl_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("content", desc);
                intent.putExtra("title", bean.zhName);
                intent.setClass(PoiDetailActivity.this, ReadMoreActivity.class);
                startActivityWithNoAnim(intent);
            }
        });

        //交通
        String address;
        if (TextUtils.isEmpty(bean.address)) {
            address = bean.zhName;
        } else {
            address = bean.address;
        }

        TextView addrT = (TextView) headerView.findViewById(R.id.tv_traffic_1);
        addrT.setText("地址："+address);
        TextView addrT1 = (TextView) headerView.findViewById(R.id.tv_traffic_2);
        addrT1.setText("乘车方案："+bean.trafficInfo);
        headerView.findViewById(R.id.tv_traffic_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.location != null && bean.location.coordinates != null) {
                    Uri mUri = Uri.parse("geo:" + bean.location.coordinates[1] + "," + bean.location.coordinates[0] + "?q=" + bean.zhName);
                    Intent mIntent = new Intent(Intent.ACTION_VIEW, mUri);
                    try {
                        if (CommonUtils.checkIntent(mContext, mIntent)) {
                            startActivity(mIntent);
                        } else {
                            ToastUtil.getInstance(mContext).showToast("没有安装地图应用");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.getInstance(mContext).showToast("没有安装地图应用");
                    }
                }
            }
        });
//        TextView tp = (TextView) findViewById(R.id.tv_phone);
//        //电话
//        if (bean.tel != null && bean.tel.size() > 0) {
//            final String phoneNum = bean.tel.get(0);
//
//            tp.setText(phoneNum);
//            findViewById(R.id.rl_phone).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_DIAL);
//                    Uri data = Uri.parse("tel:" + phoneNum);
//                    intent.setData(data);
//                    startActivity(intent);
//                }
//            });
//        } else {
//            tp.setText("未知");
//        }


//        findViewById(R.id.rl_run_time).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!TextUtils.isEmpty(bean.descUrl)) {
//                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
//                    intent.putExtra("enable_bottom_bar", false);
//                    intent.putExtra("url", bean.descUrl);
//                    intent.putExtra("title", bean.zhName);
//                    startActivity(intent);
//                }
//            }
//        });


//        if (!TextUtils.isEmpty(bean.lyPoiUrl)) {
//            ptv.setChecked(true);
//            findViewById(R.id.rl_ticket).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
//                    intent.putExtra("enable_bottom_bar", true);
//                    intent.putExtra("url", bean.lyPoiUrl);
//                    intent.putExtra("title", bean.zhName);
//                    startActivity(intent);
//                }
//            });
//        } else {
//            ptv.setChecked(false);
//            findViewById(R.id.rl_ticket).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
//        }

        if (TextUtils.isEmpty(bean.visitGuide)) {
            findViewById(R.id.ll_plan).setVisibility(View.GONE);
            findViewById(R.id.rl_plan).setVisibility(View.GONE);
        } else {
            TextView guideView = (TextView) headerView.findViewById(R.id.tv_plan_1);
            guideView.setText(bean.visitGuide);
        }
        final StringBuilder sb = new StringBuilder();
        if (bean.tips!=null&&bean.tips.size()>0){

            for (TipsBean tip : bean.tips) {
                sb.append(tip.title).append("\n");
                sb.append(tip.desc).append("\n");
            }
            TextView tvTip = (TextView) headerView.findViewById(R.id.tv_tips_1);
            tvTip.setText(Html.fromHtml(sb.toString()));
        }else {
            findViewById(R.id.rl_tips).setVisibility(View.GONE);
            findViewById(R.id.ll_tips).setVisibility(View.GONE);
        }


        // 操作
        //    if ("vs".equals(bean.type)) {
        if (!TextUtils.isEmpty(bean.visitGuideUrl)) {
            headerView.findViewById(R.id.rl_plan).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //MobclickAgent.onEvent(PoiDetailActivity.this, "button_item_poi_travel_notes");
                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                    intent.putExtra("enable_bottom_bar", false);
                    intent.putExtra("url", bean.visitGuideUrl);
                    intent.putExtra("title", bean.zhName);
                    startActivity(intent);
                }
            });
        } else {
            headerView.findViewById(R.id.iv_plan).setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(bean.tipsUrl)) {
            headerView.findViewById(R.id.rl_tips).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //MobclickAgent.onEvent(PoiDetailActivity.this, "button_item_poi_travel_tips");
                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                    intent.putExtra("enable_bottom_bar", false);
                    intent.putExtra("url", bean.tipsUrl);
                    intent.putExtra("title", bean.zhName);
                    startActivity(intent);
                }
            });
        } else {
            if (bean.tips!=null&&bean.tips.size()>0){
                headerView.findViewById(R.id.rl_tips).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("content", sb.toString());
                        intent.putExtra("title", "贴士");
                        intent.setClass(PoiDetailActivity.this, ReadMoreActivity.class);
                        startActivityWithNoAnim(intent);
                    }
                });
            }else headerView.findViewById(R.id.iv_tips).setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(bean.trafficInfoUrl)) {
            headerView.findViewById(R.id.rl_traffic).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //MobclickAgent.onEvent(PoiDetailActivity.this, "button_item_poi_travel_traffic");
                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                    intent.putExtra("enable_bottom_bar", false);
                    intent.putExtra("url", bean.trafficInfoUrl);
                    intent.putExtra("title", bean.zhName);
                    startActivity(intent);
                }
            });
        } else {
            headerView.findViewById(R.id.iv_traffic).setVisibility(View.GONE);
        }
//        } else {
//         //   findViewById(R.id.ll_actions).setVisibility(View.GONE);
//        }

        //点评

        if (bean.comments.size() > 0) {
            findViewById(R.id.rl_comment).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_comment).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //MobclickAgent.onEvent(PoiDetailActivity.this, "cell_item_poi_all_comments");
                    Intent intent = new Intent(mContext, MoreCommentActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("poi", poiDetailBean);
                    startActivity(intent);
                }
            });
        }
        commentAdapter.getDataList().addAll(bean.comments);
//        if (bean.comments != null && bean.comments.size() > 1) {
//            View footerView = View.inflate(this, R.layout.activity_poi_foot, null);
//            mCommentsList.addFooterView(footerView);
//            footerView.findViewById(R.id.all_evaluation).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    MobclickAgent.onEvent(PoiDetailActivity.this,"cell_item_poi_all_comments");
//                    Intent intent = new Intent(mContext, MoreCommentActivity.class);
//                    intent.putExtra("id", id);
//                    intent.putExtra("poi", poiDetailBean);
//                    startActivity(intent);
//                }
//            });
//        }

        commentAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            IMUtils.onShareResult(mContext, poiDetailBean, requestCode, resultCode, data, null);
        }
    }

    public static class CommentViewHolder extends ViewHolderBase<CommentBean> {
        //        @Bind(R.id.tv_commenter_property)
//        TextView mTvCommentProperty;
//        @Bind(R.id.tv_comment_content)
//        TextView mTvComment;
//        @Bind(R.id.iv_commenter_avatar)
//        ImageView mCommeterAvatar;
//        @Bind(R.id.rb_comment_rating)
//        ProperRatingBar starbar;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
        private DisplayImageOptions options;
        private Context mContext;

        public CommentViewHolder(Activity context) {
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .showImageForEmptyUri(R.drawable.ic_home_more_avatar_unknown_round)
                    .showImageOnFail(R.drawable.ic_home_more_avatar_unknown_round)
                    .resetViewBeforeLoading(true)
                    .cacheOnDisk(true)
                    .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(29))) // 设置成圆角图片
                    .build();
            mContext = context;
        }

        @Override
        public View createView(LayoutInflater layoutInflater) {
            View view = View.inflate(mContext, R.layout.item_comment, null);
            ButterKnife.bind(this, view);
            return view;
        }

        @Override
        public void showData(int position, final CommentBean itemData) {
            tvTimestamp.setText(String.format("%s | %s", itemData.authorName, dateFormat.format(new Date(itemData.publishTime))));
            tvComment.setText(Html.fromHtml(itemData.contents));
            rbComment.setRating((int) itemData.getRating());
            ImageLoader.getInstance().displayImage(itemData.authorAvatar, ivAvatar, options);
            gvCommentPic.setVisibility(View.GONE);
            tvPackage.setVisibility(View.GONE);
        }
    }

    public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

        private LayoutInflater mInflater;
        private List<RecommendBean> mDatas;

        public GalleryAdapter(Context context, List<RecommendBean> datats) {
            mInflater = LayoutInflater.from(context);
            mDatas = datats;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View arg0) {
                super(arg0);
            }

            ImageView mImg;
            TextView mTxt;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.row_rec_some,
                    viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);

            viewHolder.mImg = (ImageView) view
                    .findViewById(R.id.rec_some_iv);
            viewHolder.mTxt = (TextView) view
                    .findViewById(R.id.rec_name_tv);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            RecommendBean bean = mDatas.get(i);
            ImageLoader.getInstance().displayImage(bean.images.get(0).url, viewHolder.mImg, UILUtils.getDefaultOption());
            viewHolder.mTxt.setText(bean.title);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }
    }


    public static class POIImageVPAdapter extends PagerAdapter {
        private ArrayList<ImageBean> mDatas;
        private Context mContext;
        private DisplayImageOptions diop;

        public POIImageVPAdapter(Context context, ArrayList<ImageBean> datas) {
            mDatas = datas;
            mContext = context;
            diop = UILUtils.getDefaultOption();
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
        public Object instantiateItem(ViewGroup container, final int position) {
            Context cxt = mContext;
            ImageView imageView = new ImageView(cxt);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mContext.getResources().getDimensionPixelSize(R.dimen.spot_detail_picture_height));
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBackgroundColor(mContext.getResources().getColor(R.color.color_gray_light));
            ImageBean ib = mDatas.get(position);
            ImageLoader.getInstance().displayImage(ib.url, imageView, diop);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtils.intentToPicGallery((Activity) mContext, mDatas, position);
                }
            });
            container.addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }
    }

}
