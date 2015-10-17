package com.xuejian.client.lxp.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

import com.xuejian.client.lxp.R;

/**
 * Created by yibiao.qin on 2015/10/17.
 */
public class RotateTextview extends TextView {
    private int degrees;

    public RotateTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RotateTextview);
        degrees = a.getInt(R.styleable.RotateTextview_rotate, 0);
        a.recycle();
       // degrees = attrs.getAttributeIntValue(NAMESPACE, ATTR_ROTATE, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(degrees, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        super.onDraw(canvas);
    }
}
