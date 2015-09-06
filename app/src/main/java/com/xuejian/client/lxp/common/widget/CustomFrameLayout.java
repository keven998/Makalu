package com.xuejian.client.lxp.common.widget;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

/**
 * Created by xuyongchen on 15/9/5.
 */
public class CustomFrameLayout extends FrameLayout{
    private Boolean canInterTitleUp=true;
    private Boolean canInterTitleDown=false;
    private Float startY=0.0f;
    private OnInterDispatchListener onInterDispatchListener;
    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float paramAnonymousFloat) {
            float f = paramAnonymousFloat - 1.0F;
            return 1.0F + f * (f * (f * (f * f)));
        }
    };
    public CustomFrameLayout(Context context){
        super(context);
    }
    public CustomFrameLayout(Context context,AttributeSet attrs){
        super(context,attrs);
    }
    public CustomFrameLayout(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY=ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                if(canInterTitleUp || canInterTitleDown){
                    if((startY-ev.getY())>0){
                        if(canInterTitleUp){
                            if(onInterDispatchListener!=null){
                                onInterDispatchListener.onInterEvent(1);
                            }
                            canInterTitleUp=false;
                            canInterTitleDown=true;
                            return false;
                        }
                    }else{
                        if(canInterTitleDown){
                            if(onInterDispatchListener!=null){
                                onInterDispatchListener.onInterEvent(2);
                            }
                            canInterTitleDown=false;
                            canInterTitleUp=true;
                            return false;
                        }
                    }
                }

                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setCanInterTitleDown(Boolean canInterTitleDown) {
        this.canInterTitleDown = canInterTitleDown;
    }

    public void setCanInterTitleUp(Boolean canInterTitleUp) {
        this.canInterTitleUp = canInterTitleUp;
    }

    public interface OnInterDispatchListener{
        public void onInterEvent(int upordown);
    }

    public void setOnInterDispatchListener(OnInterDispatchListener onInterDispatchListener) {
        this.onInterDispatchListener = onInterDispatchListener;
    }



}
