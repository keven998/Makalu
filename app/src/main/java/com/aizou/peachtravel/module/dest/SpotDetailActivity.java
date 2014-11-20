package com.aizou.peachtravel.module.dest;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.HackyViewPager;
import com.aizou.core.widget.RecyclingPagerAdapter;
import com.aizou.core.widget.autoscrollviewpager.AutoScrollViewPager;
import com.aizou.core.widget.expandabletextview.ExpandableTextView;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.ImageBean;
import com.aizou.peachtravel.bean.SpotDetailBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.ImageZoomAnimator2;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.module.ImageViewPagerActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/11/17.
 */
public class SpotDetailActivity extends PeachBaseActivity {
    private String mSpotId;
    private AutoScrollViewPager mSpotImagesVp;
    private HackyViewPager mZoomImagesVp;
    private View zoomContainer;
    private ExpandableTextView mSpotIntroTv;
    private TextView mSpotNameTv,mPriceDescTv,mBestMonthTv,mOpenTimeTv,mTimeCostTv,mAddressTv;
    private LinearLayout mOtherLl;
    private View containView;
    private ImageZoomAnimator2 zoomAnimator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();

    }

    private void initView(){
        setContentView(R.layout.activity_spot_detail);
        containView = findViewById(R.id.container);
        mSpotImagesVp = (AutoScrollViewPager) findViewById(R.id.vp_spot_images);
        zoomContainer = findViewById(R.id.zoom_container);
        mZoomImagesVp = (HackyViewPager) findViewById(R.id.vp_zoom_pic);
        mSpotIntroTv = (ExpandableTextView) findViewById(R.id.expand_text_view);
        mSpotNameTv = (TextView) findViewById(R.id.tv_spot_name);
        mPriceDescTv = (TextView) findViewById(R.id.tv_price_desc);
        mBestMonthTv = (TextView) findViewById(R.id.tv_best_month);
        mOpenTimeTv = (TextView) findViewById(R.id.tv_open_time);
        mTimeCostTv = (TextView) findViewById(R.id.tv_time_cost);
        mAddressTv = (TextView) findViewById(R.id.tv_addr);
        mOtherLl = (LinearLayout) findViewById(R.id.ll_other);


    }
    private void initData(){
        mSpotId = getIntent().getStringExtra("id");
        mSpotId = "53f30c3f10114e376de5b0fc";
        getSpotDetailData();
    }

    private void getSpotDetailData(){
        TravelApi.getSpotDetail(mSpotId,new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<SpotDetailBean> detailResult = CommonJson.fromJson(result,SpotDetailBean.class);
                if(detailResult.code==0){
                    bindView(detailResult.result);
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }
        });
    }

    private void bindView(SpotDetailBean result) {
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(mContext,result.images);
        mSpotImagesVp.setAdapter(imagePagerAdapter);
        mSpotNameTv.setText(result.zhName);
        mSpotIntroTv.setText(result.desc);
        mPriceDescTv.setText(result.priceDesc);
        mBestMonthTv.setText(result.travelMonth);
        mOpenTimeTv.setText(result.openTime);
        mTimeCostTv.setText(result.timeCostStr);
        mAddressTv.setText(result.address);
        mOtherLl.removeAllViews();
        if(!TextUtils.isEmpty(result.guideUrl)){
            View view = View.inflate(mContext,R.layout.item_spot_detail_othor,null);
            TextView otherTv = (TextView) view.findViewById(R.id.tv_other);
            otherTv.setText("游玩指南");
            mOtherLl.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        if(!TextUtils.isEmpty(result.kengdieUrl)){
            View view = View.inflate(mContext,R.layout.item_spot_detail_othor,null);
            TextView otherTv = (TextView) view.findViewById(R.id.tv_other);
            otherTv.setText("防坑攻略");
            mOtherLl.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        if(!TextUtils.isEmpty(result.trafficInfoUrl)){
            View view = View.inflate(mContext,R.layout.item_spot_detail_othor,null);
            TextView otherTv = (TextView) view.findViewById(R.id.tv_other);
            otherTv.setText("交通指南");
            mOtherLl.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        zoomAnimator = new ImageZoomAnimator2(mContext,mSpotImagesVp,zoomContainer,result.images);


    }

    public class ImagePagerAdapter extends RecyclingPagerAdapter {

        private Context context;
        private ArrayList<ImageBean> imageUrlList;

        private int size;
        private boolean isInfiniteLoop;

        public ImagePagerAdapter(Context context, ArrayList<ImageBean> imageIdList) {
            this.context = context;
            this.imageUrlList = imageIdList;
            this.size = imageIdList.size();
            isInfiniteLoop = false;
        }

        @Override
        public int getCount() {
            // Infinite loop
            return isInfiniteLoop ? Integer.MAX_VALUE : imageUrlList.size();
        }

        /**
         * get really position
         *
         * @param position
         * @return
         */
        private int getPosition(int position) {
            return isInfiniteLoop ? position % size : position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup container) {
                ImageView imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
               imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                      zoomAnimator.transformIn(position);

//                    Intent i = new Intent(mContext, ImageViewPagerActivity.class);
//                    i.putExtra("pos", position);
//                    i.putParcelableArrayListExtra("imageUrlList", imageUrlList);
//
//                    Rect rect = new Rect();
//                    view.getGlobalVisibleRect(rect);
//                    i.putExtra("rect", rect);
//                    startActivity(i);
//                    overridePendingTransition(0,0);
//                    Bundle b = null;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                        //b = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(),
//                        //                                         view.getHeight()).toBundle();
//                        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
//                        b = ActivityOptions.makeThumbnailScaleUpAnimation(view, bitmap, 0, 0).toBundle();
//                    }


                }
            });

            ImageLoader.getInstance().displayImage(
                    imageUrlList.get(position).url, imageView,
                    UILUtils.getDefaultOption());
            imageView.setTag(position);
            return imageView;
        }

        private class ViewHolder {

            ImageView imageView;
        }

        /**
         * @return the isInfiniteLoop
         */
        public boolean isInfiniteLoop() {
            return isInfiniteLoop;
        }

        /**
         * @param isInfiniteLoop
         *            the isInfiniteLoop to set
         */
        public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
            this.isInfiniteLoop = isInfiniteLoop;
            return this;
        }



    }
}
