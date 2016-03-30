package com.xuejian.client.lxp.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by yibiao.qin on 2016/3/30.
 */
public class FixedScrollView extends ScrollView {


    public FixedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FixedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedScrollView(Context context) {
        super(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (listener!=null){
            listener.onScrollChanged(l, t, oldl, oldt);
        }
    }
    public void setOnScrollChangedListener(OnScrollChangedListener listener){
        this.listener = listener;
    }

    private OnScrollChangedListener listener;
    public interface OnScrollChangedListener{
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }
}
