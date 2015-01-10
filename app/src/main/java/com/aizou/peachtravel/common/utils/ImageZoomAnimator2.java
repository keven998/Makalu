package com.aizou.peachtravel.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aizou.core.log.LogUtil;
import com.aizou.core.widget.HackyViewPager;
import com.aizou.core.widget.autoscrollviewpager.AutoScrollViewPager;
import com.aizou.core.widget.pagerIndicator.viewpager.RecyclingPagerAdapter;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.bean.ImageBean;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.widget.SmoothPhotoView;
import com.aizou.peachtravel.common.widget.photoview.PhotoViewAttacher;
import com.nineoldandroids.animation.ValueAnimator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.io.File;
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
    private final int mBgColor = 0xFF000000;
    private int mBgAlpha = 0;

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
        SmoothPhotoView photeView = (SmoothPhotoView) zoomViewPager.findViewWithTag(position);
        if (photeView.getDrawable() == null) {
            File file = ImageLoader.getInstance().getDiskCache().get(imageUrls.get(position).url);
            if((file != null) && file.exists()) {
               Bitmap bitmap = ImageLoader.getInstance().loadImageSync(Uri.fromFile(file).toString());
               photeView.setImageBitmap(bitmap);
            }
        }

        View view = fromViewGroup.findViewWithTag(position);
        if (view != null) {
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
        SmoothPhotoView photeView = (SmoothPhotoView) zoomViewPager.findViewWithTag(position);
        View view = fromViewGroup.findViewWithTag(position);
        if (view != null) {
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            photeView.setOriginalInfo(view.getWidth(), view.getHeight(), location[0], location[1]);
        }
        photeView.transformOut();
    }

    public class ImagePagerAdapter extends RecyclingPagerAdapter {
        private int size;
        private DisplayImageOptions picOptions;

        public ImagePagerAdapter() {
//            Drawable backgroudDrawable = new ColorDrawable(Color.BLACK);
            picOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.all_black_bg)
                    .showImageOnFail(R.drawable.all_black_bg)
                    .showImageOnLoading(R.drawable.all_black_bg)
                    .displayer(new FadeInBitmapDisplayer(300, true, true, false))
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        }

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
            final SmoothPhotoView photeView = (SmoothPhotoView) contentView.findViewById(R.id.pv_view);
            photeView.setTag(position);
            photeView.setOnTransformListener(new SmoothPhotoView.TransformListener() {
                @Override
                public void onTransformComplete(int mode) {
                    if (mode == 2) {
                        mBgAlpha=0;
                        zoomContainer.setVisibility(View.INVISIBLE);
                    }else{
                        mBgAlpha=255;
                    }
                    Drawable backgroudDrawable =  zoomContainer.getBackground();
                    if(backgroudDrawable == null){
                        backgroudDrawable = new ColorDrawable(Color.BLACK);
                        zoomContainer.setBackgroundDrawable(backgroudDrawable);
                    }
                    backgroudDrawable.setAlpha(mBgAlpha);
                    LogUtil.d("Transform","transfromComplete--"+mBgAlpha);
                }

                @Override
                public void onTransformProcess(int mode, int alpha) {
                    mBgAlpha = alpha;
                    Drawable backgroudDrawable =  zoomContainer.getBackground();
                    if(backgroudDrawable==null){
                        backgroudDrawable = new ColorDrawable(Color.BLACK);
                        zoomContainer.setBackgroundDrawable(backgroudDrawable);
                    }
                    backgroudDrawable.setAlpha(mBgAlpha);
                    LogUtil.d("Transform","transfromProcess--"+mBgAlpha);
                }
            });

            final ProgressBar loadingPb = (ProgressBar) contentView.findViewById(R.id.pb_loading);
            final TextView progressText = (TextView) contentView.findViewById(R.id.progress_text);
            if (photeView.getDrawable() == null) {
                ImageLoader.getInstance().displayImage(imageUrls.get(position).url, photeView, picOptions, new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        progressText.setVisibility(View.VISIBLE);
                        loadingPb.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        progressText.setVisibility(View.GONE);
                        loadingPb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        progressText.setVisibility(View.GONE);
                        loadingPb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        progressText.setVisibility(View.GONE);
                        loadingPb.setVisibility(View.GONE);
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                        if (loadingPb.isShown()) {
                            loadingPb.setVisibility(View.GONE);
                        }
                        progressText.setText(String.format("%d%%", current*100/total));
                    }
                });
            }


            photeView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {

                @Override
                public void onViewTap(View view, float x, float y) {
                    photeView.clearZoom();
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

}
