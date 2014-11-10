package com.aizou.core.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class MySelectLinearLayout extends LinearLayout {
	private boolean isCheck;

	public MySelectLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		super.setOnClickListener(l);
	}
	
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
		for(int i = 0;i<getChildCount();i++){
			if(isCheck){
				getChildAt(i).setSelected(isCheck);
			}
		}
		
	}
	
}
