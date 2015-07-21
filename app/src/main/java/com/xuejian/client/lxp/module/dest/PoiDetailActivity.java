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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.DotView;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CommentBean;
import com.xuejian.client.lxp.bean.ImageBean;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.RecommendBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.IntentUtils;
import com.xuejian.client.lxp.module.PeachWebViewActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

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
        headerView = View.inflate(this, R.layout.activity_spot_detail, null);
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
//        if (type.equals(TravelApi.PeachType.RESTAURANTS)) {
//            MobclickAgent.onPageStart("page_delicacy_detail");
//        } else if (type.equals(TravelApi.PeachType.SHOPPING)) {
//            MobclickAgent.onPageStart("page_shopping_detail");
//        }else if (type.equals(TravelApi.PeachType.HOTEL)) {
//            MobclickAgent.onPageStart("page_hotel_detail");
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (type.equals(TravelApi.PeachType.RESTAURANTS)) {
//            MobclickAgent.onPageEnd("page_delicacy_detail");
//        } else if (type.equals(TravelApi.PeachType.SHOPPING)) {
//            MobclickAgent.onPageEnd("page_shopping_detail");
//        }else if (type.equals(TravelApi.PeachType.HOTEL)) {
//            MobclickAgent.onPageEnd("page_hotel_detail");
//        }
    }


    private void getDetailData() {
        DialogManager.getInstance().showModelessLoadingDialog(mContext);
        TravelApi.getPoiDetail(type, id, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                CommonJson<PoiDetailBean> detailBean = CommonJson.fromJson(result, PoiDetailBean.class);
                if (detailBean.code == 0) {
                    poiDetailBean = detailBean.result;
                    bindView(poiDetailBean);
                } else {
//                    ToastUtil.getInstance(PoiDetailActivity.this).showToast(getResources().getString(R.string.request_server_failed));
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
        TextView titleView = (TextView) findViewById(R.id.poi_det_title);
        titleView.setText(bean.zhName);
        findViewById(R.id.iv_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MobclickAgent.onEvent(mContext, "event_spot_share_to_talk");
                IMUtils.onClickImShare(PoiDetailActivity.this);
            }
        });

        //图片
        if (bean.images != null) {
            ViewPager vp = (ViewPager) findViewById(R.id.vp_poi);
            vp.setAdapter(new POIImageVPAdapter(this, bean.images));
            final DotView dotview = (DotView) findViewById(R.id.dot_view);
            int pc = bean.images.size();
            if (pc > 1) {
                dotview.setNum(bean.images.size());
                vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        dotview.setSelected(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
            } else {
                dotview.setVisibility(View.GONE);
            }
        }

        //评分、类型、排名
        RatingBar rb = (RatingBar) findViewById(R.id.rb_poi);
        rb.setRating(bean.getRating());
        TextView styleTV = (TextView) findViewById(R.id.tv_poi_style);
        if (bean.style.size()>0){
            styleTV.setText(bean.style.get(0));
        }

        styleTV.setText(bean.zhName);
        TextView levelTv = (TextView) findViewById(R.id.tv_poi_level);
        if (!bean.getFormatRank().equals("0")) {
            levelTv.setText(poiDetailBean.getFormatRank());
        } else {
            levelTv.setText("N");
        }

        //简介
        final TextView descView = (TextView) findViewById(R.id.tv_poi_desc);
        final String desc = bean.desc;
        if (TextUtils.isEmpty(desc)) {
            descView.setVisibility(View.GONE);
        } else {
            descView.setText(desc);
            if (descView.getLineCount() > 2) {
                TextView dv = descView;
                int numChars = dv.getLayout().getLineEnd(2);
                if (dv.getText().length() > numChars) {
                    String text;
                    if (IMUtils.isEnglish(desc)) {
                        text = desc.substring(0, desc.substring(0, numChars - 4).lastIndexOf(" "));
                    } else {
                        text = desc.substring(0, numChars - 4);
                    }
                    SpannableString planStr = new SpannableString("全文");
                    planStr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_theme_color)), 0, planStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder spb = new SpannableStringBuilder();
                    spb.append(String.format("%s... ", text)).append(planStr);
                    dv.setText(spb);
                }
            }
        }
        descView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("content", desc);
                intent.setClass(PoiDetailActivity.this, ReadMoreActivity.class);
                startActivityWithNoAnim(intent);
            }
        });

        //地址
        String address;
        if (TextUtils.isEmpty(bean.address)) {
            address = bean.zhName;
        } else {
            address = bean.address;
        }
        TextView addrT = (TextView) findViewById(R.id.tv_address);
        addrT.setText(address);
        findViewById(R.id.rl_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.location != null && bean.location.coordinates != null) {
                    Uri mUri = Uri.parse("geo:" + bean.location.coordinates[1] + "," + bean.location.coordinates[0] + "?q=" + bean.zhName);
                    Intent mIntent = new Intent(Intent.ACTION_VIEW, mUri);
                    if (CommonUtils.checkIntent(mContext, mIntent)) {
                        startActivity(mIntent);
                    } else {
                        ToastUtil.getInstance(mContext).showToast("没有地图应用");
                    }
                }
            }
        });

        //电话
        if (bean.tel != null && bean.tel.size() > 0) {
            final String phoneNum = bean.tel.get(0);
            TextView tp = (TextView) findViewById(R.id.tv_phone);
            tp.setText(phoneNum);
            findViewById(R.id.rl_phone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Uri data = Uri.parse("tel:" + phoneNum);
                    intent.setData(data);
                    startActivity(intent);
                }
            });
        } else {
            findViewById(R.id.rl_phone).setVisibility(View.GONE);
        }

        //开放时间
        TextView rttv = (TextView) findViewById(R.id.tv_run_time);
        if (TextUtils.isEmpty(bean.openTime)) {
            rttv.setText("全天");
        } else {
            rttv.setText(bean.openTime);
        }
        findViewById(R.id.rl_run_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(bean.descUrl)){
                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                    intent.putExtra("enable_bottom_bar", false);
                    intent.putExtra("url", bean.descUrl);
                    intent.putExtra("title", bean.zhName);
                    startActivity(intent);
                }
            }
        });

        //费用
        CheckedTextView ptv = (CheckedTextView) findViewById(R.id.ctv_ticket);
        if (TextUtils.isEmpty(bean.priceDesc)) {
            ptv.setText("未知");
        } else {
            ptv.setText(bean.priceDesc);
        }
        if (!TextUtils.isEmpty(bean.lyPoiUrl)) {
            ptv.setChecked(true);
            findViewById(R.id.rl_ticket).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(mContext, "event_go_booking_room");
                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                    intent.putExtra("enable_bottom_bar", true);
                    intent.putExtra("url", bean.lyPoiUrl);
                    intent.putExtra("title", bean.zhName);
                    startActivity(intent);
                }
            });
        } else {
            ptv.setChecked(false);
            findViewById(R.id.rl_ticket).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        //操作
        if ("vs".equals(bean.type)) {
            if(!TextUtils.isEmpty(bean.visitGuideUrl)){
                findViewById(R.id.tv_travel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                        intent.putExtra("enable_bottom_bar", false);
                        intent.putExtra("url", bean.visitGuideUrl);
                        intent.putExtra("title", bean.zhName);
                        startActivity(intent);
                    }
                });
            }else{
                findViewById(R.id.tv_travel).setEnabled(false);
            }
            if(!TextUtils.isEmpty(bean.tipsUrl)){
                findViewById(R.id.tv_tips).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                        intent.putExtra("enable_bottom_bar", false);
                        intent.putExtra("url", bean.tipsUrl);
                        intent.putExtra("title", bean.zhName);
                        startActivity(intent);
                    }
                });
            }else{
                findViewById(R.id.tv_tips).setEnabled(false);
            }
            if(!TextUtils.isEmpty(bean.trafficInfoUrl)){
                findViewById(R.id.tv_traffic).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                        intent.putExtra("enable_bottom_bar", false);
                        intent.putExtra("url", bean.trafficInfoUrl);
                        intent.putExtra("title", bean.zhName);
                        startActivity(intent);
                    }
                });
            }else{
                findViewById(R.id.tv_traffic).setEnabled(false);
            }
        } else {
            findViewById(R.id.ll_actions).setVisibility(View.GONE);
        }

        //点评
        commentAdapter.getDataList().addAll(bean.comments);
        if (bean.comments != null && bean.comments.size() > 1) {
            View footerView = View.inflate(this, R.layout.activity_poi_foot, null);
            mCommentsList.addFooterView(footerView);
            footerView.findViewById(R.id.all_evaluation).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MoreCommentActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("poi", poiDetailBean);
                    startActivity(intent);
                }
            });
        }

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
        @InjectView(R.id.tv_commenter_property)
        TextView mTvCommentProperty;
        @InjectView(R.id.tv_comment_content)
        TextView mTvComment;
        @InjectView(R.id.iv_commenter_avatar)
        ImageView mCommeterAvatar;
        @InjectView(R.id.rb_comment_rating)
        RatingBar starbar;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        private DisplayImageOptions options;
        private Context mContext;

        public CommentViewHolder(Activity context) {
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .showImageForEmptyUri(R.drawable.ic_home_talklist_default_avatar)
                    .showImageOnFail(R.drawable.ic_home_talklist_default_avatar)
                    .resetViewBeforeLoading(true)
                    .cacheOnDisk(true)
                    .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(29))) // 设置成圆角图片
                    .build();
            mContext = context;
        }

        @Override
        public View createView(LayoutInflater layoutInflater) {
            View view = View.inflate(mContext, R.layout.row_poi_comment, null);
            ButterKnife.inject(this, view);
            return view;
        }

        @Override
        public void showData(int position, final CommentBean itemData) {
            mTvCommentProperty.setText(String.format("%s | %s", itemData.authorName, dateFormat.format(new Date(itemData.publishTime))));
            mTvComment.setText(Html.fromHtml(itemData.contents));
            starbar.setRating(itemData.getRating());
            Log.d("test", "item ratiing = " + itemData.getRating());
            ImageLoader.getInstance().displayImage(itemData.authorAvatar, mCommeterAvatar, options);
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


    private class POIImageVPAdapter extends PagerAdapter {
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
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.spot_detail_picture_height));
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBackgroundColor(getResources().getColor(R.color.color_gray_light));
            ImageBean ib = mDatas.get(position);
            ImageLoader.getInstance().displayImage(ib.url, imageView, diop);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtils.intentToPicGallery(PoiDetailActivity.this, mDatas, position);
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
