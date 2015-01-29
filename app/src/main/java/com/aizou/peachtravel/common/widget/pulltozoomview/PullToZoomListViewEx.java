package com.aizou.peachtravel.common.widget.pulltozoomview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.aizou.peachtravel.common.utils.StickyUtils;

/**
 * Author:    ZhuWenWu
 * Version    V1.0
 * Date:      2014/11/7  18:01.
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2014/11/7        ZhuWenWu            1.0                    1.0
 * Why & What is modified:
 */
public class PullToZoomListViewEx extends PullToZoomBase<ListView> implements AbsListView.OnScrollListener {
    private static final String TAG = PullToZoomListViewEx.class.getSimpleName();
    //    private FrameLayout mHeaderContainer;
    private int mHeaderHeight, zoomViewHeight;
    private ScalingRunnable mScalingRunnable;


    private OnScrollYListener mOnScrollYListener;

    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float paramAnonymousFloat) {
            float f = paramAnonymousFloat - 1.0F;
            return 1.0F + f * (f * (f * (f * f)));
        }
    };

    public PullToZoomListViewEx(Context context) {
        this(context, null);
    }

    public PullToZoomListViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRootView.setOnScrollListener(this);
        mScalingRunnable = new ScalingRunnable();
    }

    public void setOnScrollYListener(OnScrollYListener onScrollYListener) {
        this.mOnScrollYListener = onScrollYListener;
    }

    /**
     * 是否显示headerView
     *
     * @param isHideHeader true: show false: hide
     */
    @Override
    public void setHideHeader(boolean isHideHeader) {
        if (isHideHeader != isHideHeader()) {
            super.setHideHeader(isHideHeader);
            if (isHideHeader) {
                removeHeaderView();
            } else {
                updateHeaderView();
            }
        }
    }

    @Override
    public void setHeaderView(View headerView) {
        if (headerView != null) {
            this.mHeaderView = headerView;
            updateHeaderView();
        }
    }

    @Override
    public void setZoomView(View zoomView) {
        this.mZoomView = zoomView;
        if (mZoomView != null) {
            zoomViewHeight = mZoomView.getMeasuredHeight();
        }
    }

    /**
     * 移除HeaderView
     * 如果要兼容API 9,需要修改此处逻辑，API 11以下不支持动态添加header
     */
    private void removeHeaderView() {
        if (mHeaderView != null) {
            if (mRootView.getHeaderViewsCount() > 0) {
                mRootView.removeHeaderView(mHeaderView);
            }

        }
    }


    /**
     * 更新HeaderView  先移除-->再添加zoomView、HeaderView -->然后添加到listView的head
     * 如果要兼容API 9,需要修改此处逻辑，API 11以下不支持动态添加header
     */
    private void updateHeaderView() {
        if (mHeaderView != null) {
            if (mRootView.getHeaderViewsCount() > 0)
                mRootView.removeHeaderView(mHeaderView);

            mHeaderHeight = mHeaderView.getMeasuredHeight();
            if (mZoomView != null) {
                zoomViewHeight = mZoomView.getMeasuredHeight();
            }
            mRootView.addHeaderView(mHeaderView);
        }
    }

    public void setAdapter(ListAdapter adapter) {
        mRootView.setAdapter(adapter);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mRootView.setOnItemClickListener(listener);
    }

    /**
     * 创建listView 如果要兼容API9,需要修改此处
     *
     * @param context 上下文
     * @param attrs   AttributeSet
     * @return ListView
     */
    @Override
    protected ListView createRootView(Context context, AttributeSet attrs) {
        return new ListView(context, attrs);
    }


    /**
     * 重置动画，自动滑动到顶部
     */
    @Override
    protected void smoothScrollToTop() {
        Log.d(TAG, "smoothScrollToTop --> ");
        mScalingRunnable.startAnimation(200L);
    }

    /**
     * zoomView动画逻辑
     *
     * @param newScrollValue 手指Y轴移动距离值
     */
    @Override
    protected void pullHeaderToZoom(int newScrollValue) {
        Log.d(TAG, "pullHeaderToZoom --> newScrollValue = " + newScrollValue);
        Log.d(TAG, "pullHeaderToZoom --> mHeaderHeight = " + mHeaderHeight);
        Log.d(TAG, "pullHeaderToZoom --> zoomViewHeight = " + zoomViewHeight);
        if (mScalingRunnable != null && !mScalingRunnable.isFinished()) {
            mScalingRunnable.abortAnimation();
        }

        ViewGroup.LayoutParams localLayoutParams = mHeaderView.getLayoutParams();
        ViewGroup.LayoutParams zoomLayoutParams = mZoomView.getLayoutParams();
        zoomLayoutParams.height = Math.abs(newScrollValue) + zoomViewHeight;
        mZoomView.setLayoutParams(zoomLayoutParams);
    }

    @Override
    protected boolean isReadyForPullStart() {
        return isFirstItemVisible();
    }

    private boolean isFirstItemVisible() {
        final Adapter adapter = mRootView.getAdapter();

        if (null == adapter || adapter.isEmpty()) {
            return true;
        } else {
            /**
             * This check should really just be:
             * mRootView.getFirstVisiblePosition() == 0, but PtRListView
             * internally use a HeaderView which messes the positions up. For
             * now we'll just add one to account for it and rely on the inner
             * condition which checks getTop().
             */
            if (mRootView.getFirstVisiblePosition() <= 1) {
                final View firstVisibleChild = mRootView.getChildAt(0);
                if (firstVisibleChild != null) {
                    return firstVisibleChild.getTop() >= mRootView.getTop();
                }
            }
        }

        return false;
    }

    @Override
    public void handleStyledAttributes(TypedArray a) {
        updateHeaderView();
    }

    @Override
    public ListView getRootView() {
        return super.getRootView();
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
                            int paramInt3, int paramInt4) {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        if (mHeaderHeight == 0 && mHeaderView != null) {
            mHeaderHeight = mHeaderView.getHeight();
        }
        if (zoomViewHeight==0&&mZoomView != null) {
            zoomViewHeight = mZoomView.getHeight();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.d(TAG, "onScrollStateChanged --> ");
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mZoomView != null && !isHideHeader() && isPullToZoomEnabled()) {
            float f = StickyUtils.getScrollY(mHeaderView,mRootView);
//                    mHeaderHeight - mHeaderView.getBottom();
            if(mOnScrollYListener!=null){
                mOnScrollYListener.onScrollY(f);
            }
            if (isParallax()) {
                if ((f > 0.0F) && (f < mHeaderHeight)) {
                    int i = (int) (0.65D * f);
                    mHeaderView.scrollTo(0, -i);
                } else if (mHeaderView.getScrollY() != 0) {
                    mHeaderView.scrollTo(0, 0);
                }
            }
        }
    }


    class ScalingRunnable implements Runnable {
        protected long mDuration;
        protected boolean mIsFinished = true;
        protected float mScale;
        protected long mStartTime;

        ScalingRunnable() {
        }

        public void abortAnimation() {
            mIsFinished = true;
        }

        public boolean isFinished() {
            return mIsFinished;
        }

        public void run() {
            if (mZoomView != null) {
                float f2;
                ViewGroup.LayoutParams zoomLayoutParams;
                if ((!mIsFinished) && (mScale > 1.0D)) {
                    float f1 = ((float) SystemClock.currentThreadTimeMillis() - (float) mStartTime) / (float) mDuration;
                    f2 = mScale - (mScale - 1.0F) * PullToZoomListViewEx.sInterpolator.getInterpolation(f1);
                    zoomLayoutParams = mZoomView.getLayoutParams();
                    zoomLayoutParams.height = ((int) (f2 * zoomViewHeight));
//                        mHeaderView.setLayoutParams(localLayoutParams);
                    mZoomView.setLayoutParams(zoomLayoutParams);
                    Log.d(TAG, "ScalingRunnable --> f2 = " + f2+"--zoomViewHeight="+f2 * zoomViewHeight);
                    if (f2 > 1.0F) {
                        post(this);
                        return;
                    }
                    mRootView.requestLayout();
                    mIsFinished = true;

                }
            }
        }

        public void startAnimation(long paramLong) {
            if (mZoomView != null) {
                mStartTime = SystemClock.currentThreadTimeMillis();
                mDuration = paramLong;
                mScale = ((float) (mZoomView.getBottom()-mZoomView.getTop()) / zoomViewHeight);
                Log.d(TAG, "ScalingRunnable --> mZoomView.getBottom() = " + mZoomView.getBottom()+"--zo0mViewHeight="+zoomViewHeight);
                mIsFinished = false;
                post(this);
            }
        }
    }
}
