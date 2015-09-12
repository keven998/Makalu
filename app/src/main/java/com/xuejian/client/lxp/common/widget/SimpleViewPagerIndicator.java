package com.xuejian.client.lxp.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import com.xuejian.client.lxp.R;

public class SimpleViewPagerIndicator extends LinearLayout {

    private static final int COLOR_TEXT_NORMAL = 0xFF646464;
    private static final int COLOR_INDICATOR_COLOR =0xFF99CC66 ;
    //Color.GREEN;

    private String[] mTitles;
    private CheckedTextView []views = new CheckedTextView[2];
    private int mTabCount;
    private int mIndicatorColor = COLOR_INDICATOR_COLOR;
    private float mTranslationX;
    private Paint mPaint = new Paint();
    private int mTabWidth;
    OnIndicatorChangeListenr listenr;
    public SimpleViewPagerIndicator(Context context) {
        this(context, null);
    }

    public SimpleViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint.setColor(mIndicatorColor);
        mPaint.setStrokeWidth(9.0F);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTabWidth = w / mTabCount;
    }

    public void setTitles(String[] titles) {
        mTitles = titles;
        mTabCount = titles.length;
        generateTitleView();

    }

    public void setIndicatorColor(int indicatorColor) {
        this.mIndicatorColor = indicatorColor;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();
        canvas.translate(mTranslationX, getHeight() - 2);
        canvas.drawLine(0, 0, mTabWidth, 0, mPaint);
        canvas.restore();
    }

    public void scroll(int position, float offset) {
        /**
         * <pre>
         *  0-1:position=0 ;1-0:postion=0;
         * </pre>
         */
        mTranslationX = getWidth() / mTabCount * (position + offset);
        invalidate();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    private void generateTitleView() {
        if (getChildCount() > 0)
            this.removeAllViews();
        int count = mTitles.length;

        setWeightSum(count);
        for (int i = 0; i < count; i++) {
            final int pos = i;
            final CheckedTextView tv = new CheckedTextView(getContext());
            LayoutParams lp = new LayoutParams(0,
                    LayoutParams.MATCH_PARENT);
            lp.weight = 1;
            lp.rightMargin=20;
            lp.leftMargin=20;
            if (i==0){
                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.selecter_btn_plan,0,0,0);
                tv.setCompoundDrawablePadding(10);
                tv.setChecked(true);
            }
            if (i==1){
                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.selecter_btn_contact,0,0,0);
                tv.setCompoundDrawablePadding(10);
            }
            tv.setPadding(20,0,20,0);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(COLOR_TEXT_NORMAL);
            tv.setText(mTitles[i]);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv.setLayoutParams(lp);
            views[i] =tv;
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    views[pos].setChecked(true);
                    views[pos^1].setChecked(false);
                    if (listenr != null) {
                        listenr.OnIndicatorChange(pos);
                    }

                }
            });
            addView(tv);
        }
    }
    public void setOnIndicatorChangeListenr(OnIndicatorChangeListenr listener){
        this.listenr = listener;
    }
    public interface OnIndicatorChangeListenr {
        public void OnIndicatorChange(int postion);
    }
}
