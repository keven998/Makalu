package com.aizou.peachtravel.module.dest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.aizou.peachtravel.common.utils.ImageZoomAnimator2;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.PeachWebViewActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Rjm on 2014/11/17.
 */
public class SpotDetailActivity extends PeachBaseActivity {
    private String mSpotId;
    private ImageView spotIv;
    private TextView picNumTv;
    private ExpandableTextView mSpotIntroTv;
    private TextView mSpotNameTv,mPriceDescTv,mOpenTimeTv,mTimeCostTv,mAddressTv;
    private ExpandableTextView mBestMonthTv;
    private TextView tipsTv,travelGuideTv,trafficGuideTv;
    private ImageView favIv;
    private SpotDetailBean spotDetailBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    public void onBackPressed() {
        finishWithNoAnim();
        overridePendingTransition(0,R.anim.fade_out);
    }

    private void initView(){
        setContentView(R.layout.activity_spot_detail);
        spotIv = (ImageView) findViewById(R.id.iv_spot);
        favIv = (ImageView) findViewById(R.id.iv_fav);
        picNumTv = (TextView) findViewById(R.id.tv_pic_num);
        mSpotIntroTv = (ExpandableTextView) findViewById(R.id.expand_text_view);
        mSpotNameTv = (TextView) findViewById(R.id.tv_spot_name);
        mPriceDescTv = (TextView) findViewById(R.id.tv_price_desc);
        mBestMonthTv = (ExpandableTextView) findViewById(R.id.tv_best_month);
        mOpenTimeTv = (TextView) findViewById(R.id.tv_open_time);
        mTimeCostTv = (TextView) findViewById(R.id.tv_time_cost);
        mAddressTv = (TextView) findViewById(R.id.tv_addr);
        tipsTv = (TextView) findViewById(R.id.tv_tips);
        travelGuideTv = (TextView) findViewById(R.id.tv_travel_guide);
        trafficGuideTv = (TextView) findViewById(R.id.tv_traffic_guide);


    }
    private void initData(){
        mSpotId = getIntent().getStringExtra("id");
//        mSpotId = "54814af98b5f77f8306decf4";
        getSpotDetailData();
    }

    private void getSpotDetailData(){
        TravelApi.getSpotDetail(mSpotId,new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<SpotDetailBean> detailResult = CommonJson.fromJson(result,SpotDetailBean.class);
                if(detailResult.code==0){
                    spotDetailBean=detailResult.result;
                    bindView(detailResult.result);
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                if (!isFinishing())
                ToastUtil.getInstance(SpotDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }
    private void refreshFav(SpotDetailBean detailBean){
        if(detailBean.isFavorite){
            favIv.setImageResource(R.drawable.ic_fav);
        }else{
            favIv.setImageResource(R.drawable.ic_unfav);
        }
    }
    private void bindView(final SpotDetailBean result) {
        ImageLoader.getInstance().displayImage(result.images.size()>0?result.images.get(0).url:"",spotIv,UILUtils.getRadiusOption());
        picNumTv.setText(result.images.size()+"");
        mSpotNameTv.setText(result.zhName);
        mSpotIntroTv.setText(result.desc);
        mPriceDescTv.setText(result.priceDesc);
        mBestMonthTv.setText(result.travelMonth);
        mOpenTimeTv.setText(result.openTime);
        mTimeCostTv.setText(result.timeCostDesc);
        mAddressTv.setText(result.address);
        mAddressTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(result.location!=null&&result.location.coordinates!=null){
                    Uri mUri = Uri.parse("geo:"+result.location.coordinates[1]+","+result.location.coordinates[0]+"?q="+result.zhName);
                    Intent mIntent = new Intent(Intent.ACTION_VIEW,mUri);
                    if (CommonUtils.checkIntent(mContext, mIntent)){
                        startActivity(mIntent);
                    }else{
                        ToastUtil.getInstance(mContext).showToast("手机里没有地图软件哦");
                    }

                }

            }
        });
        refreshFav(spotDetailBean);
        favIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DialogManager.getInstance().showLoadingDialog(SpotDetailActivity.this);
                if(result.isFavorite){
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
                            CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result,ModifyResult.class);
                            if(deleteResult.code == 0){
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
        if(!TextUtils.isEmpty(result.tipsUrl)){
            tipsTv.setVisibility(View.VISIBLE);
            tipsTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                    intent.putExtra("title","温馨贴士");
                    intent.putExtra("url",result.tipsUrl);
                    startActivity(intent);

                }
            });
        }
        if(!TextUtils.isEmpty(result.trafficInfoUrl)){
            trafficGuideTv.setVisibility(View.VISIBLE);
            trafficGuideTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                    intent.putExtra("title","交通指南");
                    intent.putExtra("url",result.trafficInfoUrl);
                    startActivity(intent);
                }
            });
        }
        if(!TextUtils.isEmpty(result.guideUrl)){
            travelGuideTv.setVisibility(View.VISIBLE);
            travelGuideTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                    intent.putExtra("title","游玩指南");
                    intent.putExtra("url",result.guideUrl);
                    startActivity(intent);
                }
            });
        }


    }



}
