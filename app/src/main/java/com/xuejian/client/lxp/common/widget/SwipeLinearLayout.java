package com.xuejian.client.lxp.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.OverScroller;
import android.widget.ScrollView;

/**
 * Created by xuyongchen on 15/9/16.
 */
public class SwipeLinearLayout extends LinearLayout{
    private boolean isControl=false;
    private boolean isTopHidden=false;
    private boolean mDragging=false;
    private float mLastY=0;
    private int mTouchSlop;
    private ViewGroup scrollView;
    private VelocityTracker velocityTracker;

    private OverScroller  overScroller;
    private int mTopViewHeight;
    private int mMaximumVelocity,mMinmumVelocity;

    public SwipeLinearLayout(Context context){
        super(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mMinmumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        overScroller = new OverScroller(context);
    }

    public SwipeLinearLayout(Context context,AttributeSet attributeSet){
        super(context,attributeSet);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        overScroller = new OverScroller(context);
        mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mMinmumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    public SwipeLinearLayout(Context context,AttributeSet attributeSet,int defStyle){
        super(context,attributeSet,defStyle);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        overScroller = new OverScroller(context);
        mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mMinmumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                getInerScrollView();
                float detY=y-mLastY;
                if(scrollView!=null && scrollView instanceof ScrollView){
                    if(!isTopHidden && scrollView.getScrollY()==0 && detY>0 && !isControl){
                        isControl=true;
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        MotionEvent ev2 = MotionEvent.obtain(ev);
                        dispatchTouchEvent(ev);
                        ev2.setAction(MotionEvent.ACTION_DOWN);
                        return dispatchTouchEvent(ev2);
                    }
                }else if(scrollView!=null && scrollView instanceof ListView){
                    View view = scrollView.getChildAt(((ListView) scrollView).getFirstVisiblePosition());
                    if(!isTopHidden && view!=null && view.getTop()==0 && !isControl && detY>0){
                        isControl=true;
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        MotionEvent ev2 = MotionEvent.obtain(ev);
                        dispatchTouchEvent(ev);
                        ev2.setAction(MotionEvent.ACTION_DOWN);
                        return dispatchTouchEvent(ev2);
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float detY=y-mLastY;
                getInerScrollView();
                if(Math.abs(detY)>mTouchSlop){
                    if(scrollView !=null && scrollView instanceof ScrollView){
                        if(!isTopHidden || (scrollView.getScrollY()==0 && isTopHidden && detY>0)){
                            initVelocityTrackerIfnotExits();
                            velocityTracker.addMovement(ev);
                            mLastY = y;
                            return true;
                        }
                    }else if(scrollView !=null && scrollView instanceof ListView){
                        View view = scrollView.getChildAt(((ListView) scrollView).getFirstVisiblePosition());
                        if(!isTopHidden || (view!=null && view.getTop()==0 && isTopHidden && detY>0)){
                            initVelocityTrackerIfnotExits();
                            velocityTracker.addMovement(ev);
                            mLastY = y;
                            return true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mDragging=false;
                recycleVelocityTracker();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVelocityTrackerIfnotExits();
        velocityTracker.addMovement(event);
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(!overScroller.isFinished()){
                    overScroller.abortAnimation();
                }
                mLastY=y;
                return true;
            case MotionEvent.ACTION_MOVE:
                float dy = y-mLastY;
                if(!mDragging && Math.abs(dy)>mTouchSlop){
                    mDragging=true;
                }

                if(mDragging){
                    scrollBy(0,(int)-dy);
                    if(getScrollY() == mTopViewHeight && dy<0){
                        event.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchEvent(event);
                        isControl=false;
                    }
                }
                mLastY=y;
                break;
            case MotionEvent.ACTION_CANCEL:
                mDragging=false;
                recycleVelocityTracker();
                if(!overScroller.isFinished()){
                    overScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_UP:
                mDragging=false;
                velocityTracker.computeCurrentVelocity(1000,mMaximumVelocity);
                int velocityY = (int)velocityTracker.getYVelocity();
                if(Math.abs(velocityY)>mMinmumVelocity){
                    fling(-velocityY);
                }
                recycleVelocityTracker();
                break;

        }
        return super.onTouchEvent(event);
    }


    @Override
    public void scrollTo(int x, int y) {
        if(y<0){
            y=0;
        }

        if(y>mTopViewHeight){
            y=mTopViewHeight;
        }

        if(y!=getScrollY()){
            super.scrollTo(x, y);
        }

        isTopHidden=getScaleY()==mTopViewHeight;

    }

    @Override
    public void computeScroll() {
        if(overScroller.computeScrollOffset()){
            scrollTo(0,overScroller.getCurrY());
            invalidate();
        }
        super.computeScroll();
    }

    public void fling(int velocityY){
        overScroller.fling(0,getScrollY(),0,velocityY,0,0,0,mTopViewHeight);
        invalidate();
    }


    public ViewGroup getInerScrollView() {
        return scrollView;
    }

    private void initVelocityTrackerIfnotExits(){
        if(velocityTracker == null){
            velocityTracker = VelocityTracker.obtain();
        }
    }


    private void recycleVelocityTracker(){
        if(velocityTracker != null){
            velocityTracker.recycle();
            velocityTracker=null;
        }
    }

    public int getmTopViewHeight() {
        return mTopViewHeight;
    }

    public void setmTopViewHeight(int mTopViewHeight) {
        this.mTopViewHeight = mTopViewHeight;
    }
}
