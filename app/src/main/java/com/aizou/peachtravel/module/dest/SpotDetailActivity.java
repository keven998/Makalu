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
import com.aizou.peachtravel.common.utils.ImageZoomAnimator2;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Rjm on 2014/11/17.
 */
public class SpotDetailActivity extends PeachBaseActivity {
    private String mSpotId;
    private TitleHeaderBar mTitleBar;
    private AutoScrollViewPager mSpotImagesVp;
    private HackyViewPager mZoomImagesVp;
    private View zoomContainer;
    private ExpandableTextView mSpotIntroTv;
    private TextView mSpotNameTv,mPriceDescTv,mBestMonthTv,mOpenTimeTv,mTimeCostTv,mAddressTv;
    private LinearLayout mOtherLl;
    private View containView;
    private ImageZoomAnimator2 zoomAnimator;
    private DotView mDotView;
    private ImageView favIv;
    private SpotDetailBean spotDetailBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView(){
        setContentView(R.layout.activity_spot_detail);
        mTitleBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
        mDotView = (DotView) findViewById(R.id.dot_view);
        favIv = (ImageView) findViewById(R.id.iv_fav);
        mTitleBar.enableBackKey(true);
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
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(mContext,result.images);
        mDotView.setNum(result.images.size());
        mTitleBar.getTitleTextView().setText(result.zhName);
        mSpotImagesVp.setAdapter(imagePagerAdapter);
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
                    startActivity(mIntent);
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
        mSpotImagesVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mDotView.setSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mZoomImagesVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSpotImagesVp.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mOtherLl.removeAllViews();
        if(!TextUtils.isEmpty(result.guideUrl)){
            View view = View.inflate(mContext,R.layout.item_spot_detail_othor,null);
            ImageView otherIv = (ImageView) view.findViewById(R.id.iv_other);
            TextView otherTv = (TextView) view.findViewById(R.id.tv_other);
            otherIv.setImageResource(R.drawable.spot_guide_btn_normal);
            otherTv.setText("游玩贴士");
            mOtherLl.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        if(!TextUtils.isEmpty(result.kengdieUrl)){
            View view = View.inflate(mContext,R.layout.item_spot_detail_othor,null);
            ImageView otherIv = (ImageView) view.findViewById(R.id.iv_other);
            TextView otherTv = (TextView) view.findViewById(R.id.tv_other);
            otherIv.setImageResource(R.drawable.spot_fangken_btn_normal);
            otherTv.setText("注意事项");
            mOtherLl.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        if(!TextUtils.isEmpty(result.trafficInfoUrl)){
            View view = View.inflate(mContext,R.layout.item_spot_detail_othor,null);
            ImageView otherIv = (ImageView) view.findViewById(R.id.iv_other);
            TextView otherTv = (TextView) view.findViewById(R.id.tv_other);
            otherIv.setImageResource(R.drawable.spot_traffic_btn_normal);
            otherTv.setText("交通");
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
