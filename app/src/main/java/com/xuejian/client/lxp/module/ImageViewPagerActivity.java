package com.xuejian.client.lxp.module;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import com.aizou.core.widget.HackyViewPager;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ImageBean;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.widget.photoview.PhotoView;
import com.xuejian.client.lxp.common.widget.photoview.PhotoViewAttacher;

import java.util.ArrayList;


public class ImageViewPagerActivity extends PeachBaseActivity {


    private HackyViewPager mViewPager;
    private ArrayList<ImageBean> imageUrls;

    ColorDrawable mBackground;
    private int mShortAnimationDuration = 300;
    float startScale;
    View contentView;
    Rect startBounds;
    Rect fromBounds;
    float startScaleFinal;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = View.inflate(this, R.layout.activity_view_pager, null);
        setContentView(contentView);
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        imageUrls = getIntent().getParcelableArrayListExtra("imageUrlList");
        int pos = getIntent().getIntExtra("pos", 0);
        mViewPager.setAdapter(new ImagePagerAdapter());
        mViewPager.setCurrentItem(pos);

        if (savedInstanceState != null) {
        }
        mBackground = new ColorDrawable(Color.BLACK);
        contentView.setBackgroundDrawable(mBackground);
        fromBounds = getIntent().getParcelableExtra("rect");
        ViewTreeObserver observer = contentView.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                contentView.getViewTreeObserver().removeOnPreDrawListener(this);
                zoomEnterAnimation();
                return true; // To change body of implemented methods use File |
                // Settings | File Templates.
            }
        });
    }

    private void zoomEnterAnimation() {
        startBounds = fromBounds;
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();
        mViewPager.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);
        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the
        // "center crop" technique. This prevents undesirable stretching during
        // the animation.
        // Also calculate the start scaling factor (the end scaling factor is
        // always 1.0).

        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds
                .width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(1);
        animSet.play(ObjectAnimator.ofFloat(mViewPager, "pivotX", 0f))
                .with(ObjectAnimator.ofFloat(mViewPager, "pivotY", 0f))
                .with(ObjectAnimator.ofFloat(mViewPager, "alpha", 1.0f));
//                .with(ObjectAnimator.ofFloat(mBackground,"alpha"));
        animSet.start();


        // Construct and run the parallel animation of the four translation and
        // scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(startBounds, "alpha", 1.0f, 0.f);
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(mViewPager, "x", startBounds.left, finalBounds.left);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(mViewPager, "y", startBounds.top, finalBounds.top);
        ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(mViewPager, "scaleX", startScale, 1f);
        ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(mViewPager, "scaleY", startScale, 1f);

        set.play(alphaAnimator).with(animatorX).with(animatorY).with(animatorScaleX).with(animatorScaleY);
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {

            public void onAnimationEnd(Animator animation) {

            }

            public void onAnimationCancel(Animator animation) {
            }
        });
        set.start();

        // Upon clicking the zoomed-in image, it should zoom back down to the
        // original bounds
        // and show the thumbnail instead of the expanded image.
        startScaleFinal = startScale;
    }

    public boolean getScaleFinalBounds() {
        startBounds = fromBounds;
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        mViewPager.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds
                .width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }
        startScaleFinal = startScale;
        return true;
    }

    private void zoomExitAnimation(final Runnable listener) {
        mBackground.setAlpha(0);
        AnimatorSet as = new AnimatorSet();
        ObjectAnimator containAlphaAnimator = ObjectAnimator.ofFloat(mBackground, "alpha", 1.f, 0.f);
        boolean scaleResult = getScaleFinalBounds();
        if (scaleResult) {
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(mViewPager, "x", startBounds.left);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(mViewPager, "y", startBounds.top);
            ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(mViewPager, "scaleX", startScaleFinal);
            ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(mViewPager, "scaleY", startScaleFinal);

            as.play(containAlphaAnimator).with(animatorX).with(animatorY).with(animatorScaleX).with(animatorScaleY);
        } else {
            //the selected photoview is beyond the mobile screen display
            //so it just fade out
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mViewPager, "alpha", 0.1f);
            as.play(alphaAnimator).with(containAlphaAnimator);
        }
        as.setDuration(mShortAnimationDuration);
        as.setInterpolator(new DecelerateInterpolator());
        as.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
//	            viewPager.clearAnimation();
//				viewPager.setVisibility(View.GONE);
                listener.run();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
//	            viewPager.clearAnimation();
//				viewPager.setVisibility(View.GONE);
            }
        });
        as.start();


    }


    @Override
    public void onBackPressed() {
//		super.onBackPressed();
        zoomExitAnimation(new Runnable() {
            @Override
            public void run() {
                finishWithNoAnim();
            }
        });
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
            container.addView(contentView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            photeView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {

                @Override
                public void onViewTap(View view, float x, float y) {
                    zoomExitAnimation(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });

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
