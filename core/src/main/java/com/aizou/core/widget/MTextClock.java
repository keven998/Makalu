package com.aizou.core.widget;

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RemoteViews.RemoteView;
import android.widget.TextView;


import com.aizou.core.base.BaseApplication;

import java.util.Calendar;


@RemoteView
public class MTextClock extends TextView {
	
	private boolean mAttached;
	private Calendar mTime;
	private final Runnable mTicker = new Runnable() {
		public void run() {
			onTimeChanged();
			getHandler().postDelayed(mTicker, 60*1000);
			mTime.add(Calendar.MINUTE, 1);
		}
	};

	/**
	 * Creates a new clock using the default patterns
	 * respectively for the 24-hour and 12-hour modes.
	 * 
	 * @param context
	 *            The Context the view is running in, through which it can
	 *            access the current theme, resources, etc.
	 */
	public MTextClock(Context context) {
		super(context);
		init();
	}

	/**
	 * Creates a new clock inflated from XML. This object's properties are
	 * intialized from the attributes specified in XML.
	 * 
	 * This constructor uses a default style of 0, so the only attribute values
	 * applied are those in the Context's Theme and the given AttributeSet.
	 * 
	 * @param context
	 *            The Context the view is running in, through which it can
	 *            access the current theme, resources, etc.
	 * @param attrs
	 *            The attributes of the XML tag that is inflating the view
	 */
	public MTextClock(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * Creates a new clock inflated from XML. This object's properties are
	 * intialized from the attributes specified in XML.
	 * 
	 * @param context
	 *            The Context the view is running in, through which it can
	 *            access the current theme, resources, etc.
	 * @param attrs
	 *            The attributes of the XML tag that is inflating the view
	 * @param defStyle
	 *            The default style to apply to this view. If 0, no style will
	 *            be applied (beyond what is included in the theme). This may
	 *            either be an attribute resource, whose value will be retrieved
	 *            from the current theme, or an explicit style resource
	 */
	public MTextClock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		createTime();
	}

	private void createTime() {
			mTime =Calendar.getInstance();
	}

	public void setTime(long milliseconds){
		mTime.setTimeInMillis(milliseconds);
		onTimeChanged();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (!mAttached) {
			mAttached = true;
			createTime();
            mTicker.run();
		}
	}
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mAttached) {
			getHandler().removeCallbacks(mTicker);
			mAttached = false;
		}
	}

	private void onTimeChanged() {
		int hourInt = mTime.get(Calendar.HOUR_OF_DAY);
		String hour = ""+hourInt;
		if(hourInt<10){
			hour =""+"0"+hourInt;
		}
		int minuteInt = mTime.get(Calendar.MINUTE);
		String minute = ""+minuteInt;
		if(minuteInt<10){
			minute = ""+"0"+minuteInt;
		}
		String currentTime = hour 	+":"+minute;
		setText(currentTime);
	}

}

