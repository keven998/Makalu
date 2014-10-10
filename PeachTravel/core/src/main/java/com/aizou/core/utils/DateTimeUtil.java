package com.aizou.core.utils;

import android.content.Context;


import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {

	private Context mContext;
	private String title;
	private OnDateTimeChanged onDateTimeChanged;
	private String dateStr = "",timeStr = "";
	private Calendar mCalendar;
	public DateTimeUtil(Context context,
			final String title,OnDateTimeChanged onDateTimeChanged){
		mContext = context;
		this.title =title;
		this.onDateTimeChanged = onDateTimeChanged;
	}
	

	
	
	public interface OnDateTimeChanged {
		public void onDateTimeListenner(String date);
	}

}
