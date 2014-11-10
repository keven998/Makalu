package com.aizou.core.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * @author xyl
 *	把滑动时间丢给子View处理
 */
public class CustomerScroView extends ScrollView {

	public CustomerScroView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return false;
	}

}
