package com.aizou.peachtravel.module.dest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.DotView;
import com.aizou.core.widget.HackyViewPager;
import com.aizou.core.widget.autoscrollviewpager.AutoScrollViewPager;
import com.aizou.core.widget.expandabletextview.ExpandableTextView;
import com.aizou.core.widget.pagerIndicator.viewpager.RecyclingPagerAdapter;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.ImageBean;
import com.aizou.peachtravel.bean.ModifyResult;
import com.aizou.peachtravel.bean.SpotDetailBean;
import com.aizou.peachtravel.common.api.OtherApi;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.utils.ImageZoomAnimator2;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.utils.IntentUtils;
import com.aizou.peachtravel.common.utils.ShareUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.PeachWebViewActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Rjm on 2014/11/17.
 */
public class SpotDetailActivity extends PeachBaseActivity {
    private String mSpotId;
    private ImageView closeIv;
    private ImageView spotIv;
    private TextView picNumTv;
    private TextView mSpotIntroTv;
    private LinearLayout cardLl;
    private TextView mSpotNameTv, mPriceDescTv, mAddressTv, mCostTimeTv, mOpenTimeTv;
    //    private ExpandableTextView mBestMonthTv;
    private TextView tipsTv, travelGuideTv, trafficGuideTv;
    private ImageView favIv,shareIv;
    private SpotDetailBean spotDetailBean;
    private RatingBar ratingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    public void onBackPressed() {
        finishWithNoAnim();
        overridePendingTransition(0, android.R.anim.fade_out);
    }

    private void initView() {
        setContentView(R.layout.activity_spot_detail);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.y = LocalDisplay.dp2px(5);
        p.height = (int) (d.getHeight() - LocalDisplay.dp2px(80));
        p.width = (int) (d.getWidth() - LocalDisplay.dp2px(30));
//        p.alpha = 1.0f;      //设置本身透明度
//        p.dimAmount = 0.0f;      //设置黑暗度
//        getWindow().setAttributes(p);
        spotIv = (ImageView) findViewById(R.id.iv_spot);
        closeIv = (ImageView) findViewById(R.id.iv_close);
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithNoAnim();
                SpotDetailActivity.this.overridePendingTransition(0, android.R.anim.fade_out);
            }
        });
        favIv = (ImageView) findViewById(R.id.iv_fav);
        shareIv = (ImageView) findViewById(R.id.iv_share);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar_spot);
        picNumTv = (TextView) findViewById(R.id.tv_pic_num);
        mSpotIntroTv = (TextView) findViewById(R.id.tv_intro);
        cardLl = (LinearLayout) findViewById(R.id.ll_card);
        mSpotNameTv = (TextView) findViewById(R.id.tv_spot_name);
        mPriceDescTv = (TextView) findViewById(R.id.tv_price_desc);
        mCostTimeTv = (TextView) findViewById(R.id.tv_cost_time);
        mOpenTimeTv = (TextView) findViewById(R.id.tv_open_time);
//        mBestMonthTv = (ExpandableTextView) findViewById(R.id.tv_best_month);
//        mOpenTimeTv = (TextView) findViewById(R.id.tv_open_time);
//        mTimeCostTv = (TextView) findViewById(R.id.tv_time_cost);
        mAddressTv = (TextView) findViewById(R.id.tv_addr);
        tipsTv = (TextView) findViewById(R.id.tv_tips);
        travelGuideTv = (TextView) findViewById(R.id.tv_travel_guide);
        trafficGuideTv = (TextView) findViewById(R.id.tv_traffic_guide);

    }

    private void initData() {
        mSpotId = getIntent().getStringExtra("id");
        DialogManager.getInstance().showModelessLoadingDialog(mContext);
        getSpotDetailData();
    }

    private void getSpotDetailData() {
        TravelApi.getSpotDetail(mSpotId, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                CommonJson<SpotDetailBean> detailResult = CommonJson.fromJson(result, SpotDetailBean.class);
                if (detailResult.code == 0) {
                    spotDetailBean = detailResult.result;
                    bindView(detailResult.result);
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                if (!isFinishing()){
                    DialogManager.getInstance().dissMissLoadingDialog();
                    ToastUtil.getInstance(SpotDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }

            }
        });
    }

    private void refreshFav(SpotDetailBean detailBean) {
        if (detailBean.isFavorite) {
            favIv.setImageResource(R.drawable.ic_poi_fav_selected);
        } else {
            favIv.setImageResource(R.drawable.ic_poi_fav_normal);
        }
    }

    private void bindView(final SpotDetailBean result) {
        ImageLoader.getInstance().displayImage(result.images.size() > 0 ? result.images.get(0).url : "", spotIv, UILUtils.getDefaultOption());
        spotIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.intentToPicGallery(SpotDetailActivity.this, result.images, 0);
            }
        });
        ratingBar.setRating(result.getRating());
        mSpotNameTv.setText(result.zhName);
//        mSpotIntroTv.setText(result.desc);
        mPriceDescTv.setText("门票 "+result.priceDesc);
        mCostTimeTv.setText("推荐游玩时间 " + result.timeCostDesc);
        mOpenTimeTv.setText("开放时间 " + result.openTime);
//        mBestMonthTv.setText(result.travelMonth);
//        mOpenTimeTv.setText(result.openTime);
//        mTimeCostTv.setText(result.timeCostDesc);
        mAddressTv.setText(result.address);
        mAddressTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (result.location != null && result.location.coordinates != null) {
                    Uri mUri = Uri.parse("geo:" + result.location.coordinates[1] + "," + result.location.coordinates[0] + "?q=" + result.zhName);
                    Intent mIntent = new Intent(Intent.ACTION_VIEW, mUri);
                    if (CommonUtils.checkIntent(mContext, mIntent)) {
                        startActivity(mIntent);
                    } else {
                        ToastUtil.getInstance(mContext).showToast("没找到地图");
                    }

                }

            }
        });
        shareIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMUtils.onClickImShare(SpotDetailActivity.this);
            }
        });
        refreshFav(spotDetailBean);
        favIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DialogManager.getInstance().showLoadingDialog(SpotDetailActivity.this);
                if (result.isFavorite) {
                    OtherApi.deleteFav(spotDetailBean.id, new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {
//                            DialogManager.getInstance().dissMissLoadingDialog();
                            CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                            if (deleteResult.code == 0) {
                                spotDetailBean.isFavorite = false;
                                refreshFav(spotDetailBean);
                            }
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
//                            DialogManager.getInstance().dissMissLoadingDialog();
                            if (!isFinishing())
                                ToastUtil.getInstance(SpotDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                        }
                    });
                } else {
                    OtherApi.addFav(spotDetailBean.id, "vs", new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {
//                            DialogManager.getInstance().dissMissLoadingDialog();
                            CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                            if (deleteResult.code == 0) {
                                spotDetailBean.isFavorite = true;
                                refreshFav(spotDetailBean);
                            }
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
//                            DialogManager.getInstance().dissMissLoadingDialog();
                        }
                    });
                }
            }
        });
        cardLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SpotIntroActivity.class);
                intent.putExtra("content", spotDetailBean.desc);
                intent.putExtra("spot", spotDetailBean.zhName);
                startActivity(intent);
            }
        });
        mSpotIntroTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SpotIntroActivity.class);
                intent.putExtra("content", spotDetailBean.desc);
                intent.putExtra("spot", spotDetailBean.zhName);
                startActivity(intent);
            }
        });
        if (!TextUtils.isEmpty(result.tipsUrl)) {
            tipsTv.setEnabled(true);
            tipsTv.setVisibility(View.VISIBLE);
            tipsTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                    intent.putExtra("title", "实用信息");
                    intent.putExtra("url", result.tipsUrl);
                    startActivity(intent);

                }
            });
        }else{
            tipsTv.setEnabled(false);
        }
        if (!TextUtils.isEmpty(result.trafficInfoUrl)) {
            trafficGuideTv.setEnabled(true);
            trafficGuideTv.setVisibility(View.VISIBLE);
            trafficGuideTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                    intent.putExtra("title", "交通指南");
                    intent.putExtra("url", result.trafficInfoUrl);
                    startActivity(intent);
                }
            });
        }else{
            trafficGuideTv.setEnabled(false);
        }
        if (!TextUtils.isEmpty(result.visitGuideUrl)) {
            travelGuideTv.setEnabled(true);
            travelGuideTv.setVisibility(View.VISIBLE);
            travelGuideTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                    intent.putExtra("title", "亮点体验");
                    intent.putExtra("url", result.visitGuideUrl);
                    startActivity(intent);
                }
            });
        }else{
            travelGuideTv.setEnabled(false);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IMUtils.onShareResult(mContext,spotDetailBean,requestCode,resultCode,data,null);
    }
}
