package com.aizou.peachtravel.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.aizou.core.widget.HackyViewPager;
import com.aizou.core.widget.RecyclingPagerAdapter;
import com.aizou.core.widget.autoscrollviewpager.AutoScrollViewPager;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.bean.ImageBean;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.common.widget.SmoothPhotoView;
import com.aizou.peachtravel.common.widget.photoview.PhotoViewAttacher;
import com.aizou.peachtravel.module.GuideActivity;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/11/19.
 */
public class ImageZoomAnimator2 {
    private Context context;
    private View containView;
    private View zoomContainer;
    private ViewGroup fromViewGroup;
    private ViewPager zoomViewPager;
    private List<ImageBean> imageUrls;
    private View currentView;
    private ImagePagerAdapter mImagePagerAdpater;

    public ImageZoomAnimator2(Context context, ViewGroup fromViewGroup, View zoomContainer, List<ImageBean> imageUrls){
        this.context =context;
        this.fromViewGroup = fromViewGroup;
        this.zoomContainer = zoomContainer;
        this.imageUrls = imageUrls;
        this.zoomViewPager = (HackyViewPager) zoomContainer.findViewById(R.id.vp_zoom_pic);
        mImagePagerAdpater=new ImagePagerAdapter();
        zoomViewPager.setAdapter(mImagePagerAdpater);


    }


    public void transformIn(int position) {
        mImagePagerAdpater.notifyDataSetChanged();
        zoomContainer.setVisibility(View.VISIBLE);
        zoomViewPager.setCurrentItem(position, false);
        SmoothPhotoView photeView =(SmoothPhotoView) zoomViewPager.findViewWithTag(position);
        View view = fromViewGroup.findViewWithTag(position);
        if(view!=null){
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            photeView.setOriginalInfo(view.getWidth(), view.getHeight(), location[0], location[1]);
        }

        photeView.transformIn();



    }

    public void transformOut(int position){
        if(fromViewGroup instanceof AutoScrollViewPager){
            ((AutoScrollViewPager) fromViewGroup).setCurrentItem(position,false);
        }
        SmoothPhotoView photeView =(SmoothPhotoView) zoomViewPager.findViewWithTag(position);
        View view = fromViewGroup.findViewWithTag(position);
        if(view!=null){
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            photeView.setOriginalInfo(view.getWidth(), view.getHeight(), location[0], location[1]);
        }
        photeView.setOnTransformListener(new SmoothPhotoView.TransformListener() {
            @Override
            public void onTransformComplete(int mode) {
                if (mode == 2) {
                    zoomContainer.setVisibility(View.INVISIBLE);
                }
            }
        });
        photeView.transformOut();
    }

    public class ImagePagerAdapter extends RecyclingPagerAdapter {


        private int size;

        @Override
        public int getCount() {
            // Infinite loop
            return imageUrls.size();
        }

        /**
         * get really position
         *
         * @param position
         * @return
         */
        private int getPosition(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup container) {
            View contentView = View.inflate(context, R.layout.item_view_pic, null);
            final SmoothPhotoView photeView =(SmoothPhotoView) contentView.findViewById(R.id.pv_view);
            photeView.setTag(position);
            final ProgressBar loadingPb = (ProgressBar) contentView.findViewById(R.id.pb_loading);
            ImageLoader.getInstance().displayImage(imageUrls.get(position).url, photeView, UILUtils.getDefaultOption(), new ImageLoadingListener() {

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

            photeView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {

                @Override
                public void onViewTap(View view, float x, float y) {
//                    photeView.clearZoom();
                    transformOut(position);



                }
            });
            return contentView;
        }


        /**
         * @return the isInfiniteLoop
         */

        /**
         * @param isInfiniteLoop
         *            the isInfiniteLoop to set
         */



    }


//    class ImagePagerAdapter extends PagerAdapter {
//
//
//        @Override
//        public int getCount() {
//            return imageUrls.size();
//        }
//
//        @Override
//        public View instantiateItem(final ViewGroup container, final int position) {
////			ImageView photoView = new ImageView(container.getContext());
//////			photoView.setScaleType(ScaleType.FIT_CENTER);
////			PhotoViewAttacher mAttacher=new PhotoViewAttacher(photoView);
//            // Now just add PhotoView to ViewPager and return it
//            View contentView = View.inflate(context, R.layout.item_view_pic, null);
//            final SmoothPhotoView photeView =(SmoothPhotoView) contentView.findViewById(R.id.pv_view);
//            photeView.setTag(position);
//            final ProgressBar loadingPb = (ProgressBar) contentView.findViewById(R.id.pb_loading);
//            ImageLoader.getInstance().displayImage(imageUrls.get(position).url, photeView, UILUtils.getDefaultOption(),new ImageLoadingListener() {
//
//                @Override
//                public void onLoadingStarted(String imageUri, View view) {
//                    loadingPb.setVisibility(View.VISIBLE);
//
//                }
//
//                @Override
//                public void onLoadingFailed(String imageUri, View view,
//                                            FailReason failReason) {
//
//                }
//
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    loadingPb.setVisibility(View.GONE);
//                }
//
//                @Override
//                public void onLoadingCancelled(String imageUri, View view) {
//                    // TODO Auto-generated method stub
//
//                }
//            });
//            container.addView(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//
//            photeView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
//
//                @Override
//                public void onViewTap(View view, float x, float y) {
////                    photeView.clearZoom();
//                    transformOut(position);
//
//
//
//                }
//            });
//            return contentView;
//        }
//
//        @Override
//        public void setPrimaryItem(ViewGroup container, int position, Object object) {
//            super.setPrimaryItem(container, position, object);
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((View) object);
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//
//
//
//    }


}
