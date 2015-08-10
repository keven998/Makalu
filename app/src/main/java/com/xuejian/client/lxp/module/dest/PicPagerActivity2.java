package com.xuejian.client.lxp.module.dest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.aizou.core.widget.HackyViewPager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.widget.photoview.PhotoView;
import com.xuejian.client.lxp.common.widget.photoview.PhotoViewAttacher;

import java.util.ArrayList;


public class PicPagerActivity2 extends PeachBaseActivity {


    private HackyViewPager mViewPager;
    private ArrayList<String> imageUrls;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        imageUrls = getIntent().getStringArrayListExtra("imageStringUrlList");
        int pos = getIntent().getIntExtra("pos", 0);
        mViewPager.setAdapter(new ImagePagerAdapter());
        mViewPager.setCurrentItem(pos);
        if (savedInstanceState != null) {

        }
    }

    @Override
    public void onBackPressed() {
        PicPagerActivity2.this.finishWithNoAnim();
        overridePendingTransition(0, R.anim.fade_out);
    }

    class ImagePagerAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
//			ImageView photoView = new ImageView(container.getContext());
////			photoView.setScaleType(ScaleType.FIT_CENTER);
//			PhotoViewAttacher mAttacher=new PhotoViewAttacher(photoView);
            // Now just add PhotoView to ViewPager and return it
            View contentView = View.inflate(mContext, R.layout.item_view_pic, null);

            PhotoView photeView = (PhotoView) contentView.findViewById(R.id.pv_view);
            final ProgressBar loadingPb = (ProgressBar) contentView.findViewById(R.id.pb_loading);
            ImageLoader.getInstance().displayImage(imageUrls.get(position), photeView, UILUtils.getDefaultOption(), new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    loadingPb.setVisibility(View.VISIBLE);

                }

                @Override
                public void onLoadingFailed(String imageUri, View view,
                                            FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    loadingPb.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    // TODO Auto-generated method stub

                }
            });
            container.addView(contentView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            photeView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {

                @Override
                public void onViewTap(View view, float x, float y) {
                    PicPagerActivity2.this.finishWithNoAnim();
                    overridePendingTransition(0, R.anim.fade_out);

                }
            });
            return contentView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }


}
