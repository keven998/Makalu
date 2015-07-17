package com.xuejian.client.lxp.common.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Rjm on 2015/1/22.
 */
public class PhotoViewPager extends ViewPager {
    public PhotoViewPager(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public PhotoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        try {
            return super.onInterceptTouchEvent(arg0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        try {
            return super.onTouchEvent(arg0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

}
