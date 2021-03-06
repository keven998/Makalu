package com.aizou.core.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;

/**
 * ScrollView反弹效果的实现 
 * 
 */
public class SpaceHorizontalScrollView extends HorizontalScrollView
{
    private View inner;

    private float x;

    private Rect normal = new Rect();;

    public SpaceHorizontalScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

    }

    @Override
    protected void onFinishInflate()
    {
        if (getChildCount() > 0)
        {
            inner = getChildAt(0);

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
    	// TODO Auto-generated method stub\
    	
    	if (inner == null)
        {
            return super.dispatchTouchEvent(ev);
        }
        else
        {
            commOnTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void commOnTouchEvent(MotionEvent ev)
    {
        int action = ev.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                x = ev.getX();
                break;
            case MotionEvent.ACTION_UP:
                if (isNeedAnimation())
                {
                    animation();
                }

                break;
            case MotionEvent.ACTION_MOVE:
                final float preX = x;
                float nowX = ev.getX();
                int deltaX = (int) (preX - nowX);
                // 滚动
                scrollBy(deltaX, 0);

                x = nowX;
                // 当滚动到最上或者最下时就不会再滚动，这时移动布局
                if (isNeedMove())
                {
                    if (normal.isEmpty())
                    {
                        // 保存正常的布局位置
                        normal.set(inner.getLeft(), inner.getTop(), inner.getRight(), inner.getBottom());

                    }
                    // 移动布局
                    inner.layout(inner.getLeft() - deltaX, inner.getTop(), inner.getRight()- deltaX, inner.getBottom() );
                }
                break;

            default:
                break;
        }
    }

    // 开启动画移动

    public void animation()
    {
        // 开启移动动画
        TranslateAnimation ta = new TranslateAnimation( inner.getLeft(), normal.left,0,0);
        ta.setDuration(200);
        inner.startAnimation(ta);
        // 设置回到正常的布局位置
        inner.layout(normal.left, normal.top, normal.right, normal.bottom);

        normal.setEmpty();

    }

    // 是否需要开启动画
    public boolean isNeedAnimation()
    {
        return !normal.isEmpty();
    }

    // 是否需要移动布局
    public boolean isNeedMove()
    {

        int offset = inner.getMeasuredWidth() - getWidth();
        int scrollX = getScrollX();
        if (scrollX == 0 || scrollX == offset)
        {
            return true;
        }
        return false;
    }

}
