package com.aizou.core.utils;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;


import com.aizou.core.log.LogGloble;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期的处理工具类
 * @author xyl
 *
 */
public class DateUtil {
	private static final String TAG = DateUtil.class.getSimpleName();
	public static SimpleDateFormat sdf1 = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	public static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	public static SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");
	public static SimpleDateFormat sdf4= new SimpleDateFormat("HH-mm-ss");
	/**
	 * 获取当前时间并格式化成 yyyy-mm-dd hh-mm-ss格式
	 * 
	 * @return
	 */
	public static final String getCurrentFormateTime() {
		return sdf1.format(new Date());
	}

	/**
	 * 获取当前时间并格式化成 yyyy-mm-dd 格式
	 * 
	 * @return
	 */
	public static final String getCurrentFormateTime2() {
		return sdf2.format(new Date());
	}
	
	/**
	 * 获取当前时间并格式化成 HH:mm:ss 格式
	 * 
	 * @return
	 */
	public static final String getCurrentFormateTime3() {
		return sdf3.format(new Date());
	}

	/**
	 * 格式化成 yyyy-mm-dd hh-mm格式
	 * 
	 * @return
	 */
	public static final String getFormateTime1(Date date) {
		return sdf1.format(date);
	}

	public static final Date parsDate2(String dateStr) {
		try {
			return sdf2.parse(dateStr.substring(0, 10));
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date parsDate1(String dateStr) {
		try {
			return sdf1.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 只限制年和月的日期选择框
	 * 
	 * @param dateStr
	 * @param title
	 *            标题
	 * @param textchangeListenner
	 * @param flag
	 *            是否第一次显示提示框 如果是第一次 则是开始时间，如果不是则是结束时间
	 * @return
	 */
	@SuppressLint("NewApi") public static DatePickerDialog getDataWithYYYYMM(Context context,
			String dateStr, final String title,
			final OnDateSelected textchangeListenner, boolean flag) {
		// TODO 这个时间是当前时间
		Calendar c = getCalendarWithDate(new Date());
		DatePickerDialog dialog = new DatePickerDialog(context,
				new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						/** 选择的开始日期 */
						StringBuffer date = new StringBuffer();
						date.append(String.valueOf(year));
						// date.append("-");
						int month = monthOfYear + 1;
						date.append(((month < 10) ? ("0" + month)
								: (month + "")));
						// date.append("-");
						// date.append(((dayOfMonth < 10) ? ("0" + dayOfMonth)
						// : (dayOfMonth + "")));
						if (textchangeListenner != null) {
							textchangeListenner.selectedListenner(date
									.toString());
						}
					}
				}, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH));
		dialog.setTitle(title);
		DatePicker dp = dialog.getDatePicker();
		if (dp.getCalendarView() != null) {
			dp.getCalendarView().setVisibility(View.GONE);
		}
		((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0))
				.getChildAt(2).setVisibility(View.GONE);
		return dialog;
	}

	/**
	 * 日期弹窗
	 * 
	 * @param title
	 *            标题
	 * @param textchangeListenner
	 *            是否第一次显示提示框 如果是第一次 则是开始时间，如果不是则是结束时间
	 * @return
	 */
	public static DatePickerDialog getDataDialog(Context context,
			final String title, final OnDateSelected textchangeListenner) {
		// TODO 这个时间是当前时间
		Calendar c = getCalendarWithDate(new Date());
		DatePickerDialog dialog = new DatePickerDialog(context,
				new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						/** 选择的开始日期 */
						StringBuffer date = new StringBuffer();
						date.append(String.valueOf(year));
						date.append("-");
						int month = monthOfYear + 1;
						date.append(((month < 10) ? ("0" + month)
								: (month + "")));
						date.append("-");
						date.append(((dayOfMonth < 10) ? ("0" + dayOfMonth)
								: (dayOfMonth + "")));
						if (textchangeListenner != null) {
							textchangeListenner.selectedListenner(date
									.toString());
						}
					}
				}, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH));
		dialog.setTitle(title);
		return dialog;
	}


	/**
	 * 日期弹窗（不能早于今天）
	 * 
	 * @param title
	 *            标题
	 * @param textchangeListenner
	 * @return
	 */
	public static DatePickerDialog getDataDialogAfterToday(Context context,
			final String title, final onDateSelected2 textchangeListenner) {
		// TODO 这个时间是当前时间
		Calendar c = getCalendarWithDate(new Date());
		DatePickerDialog dialog = new DatePickerDialog(context,
				new OnDateSetListener() {

					@SuppressWarnings("deprecation")
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						/** 选择的开始日期 */
						StringBuffer date = new StringBuffer();
						date.append(String.valueOf(year));
						date.append("-");
						int month = monthOfYear + 1;
						date.append(((month < 10) ? ("0" + month)
								: (month + "")));
						date.append("-");
						date.append(((dayOfMonth < 10) ? ("0" + dayOfMonth)
								: (dayOfMonth + "")));

						if (textchangeListenner != null) {
							textchangeListenner.selectedListener(new Date(year,
									monthOfYear, dayOfMonth));
						}
					}
				}, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH));
		dialog.setTitle(title);
		return dialog;
	}

	/**
	 * 时间弹窗
	 * 
	 * @author renxiaotian
	 * @param dateStr
	 * @param title
	 *            标题
	 * @param textchangeListenner
	 * @return
	 */
	public static TimePickerDialog getTimeDialog(Context context,
			String dateStr, final String title,
			final OnDateSelected textchangeListenner, boolean flag) {
		// TODO 这个时间是当前时间
		Calendar c = getCalendarWithDate(new Date());
		TimePickerDialog timePickerDialog = new TimePickerDialog(context,
				new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						// TODO Auto-generated method stub
						StringBuffer time = new StringBuffer();
						time.append(((hourOfDay < 10) ? ("0" + hourOfDay)
								: (hourOfDay + "")));
						time.append(":");
						time.append(((minute < 10) ? ("0" + minute)
								: (minute + "")));
						time.append(":00");
						if (textchangeListenner != null) {
							textchangeListenner.selectedListenner(time
									.toString());
						}
					}
				}, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
		timePickerDialog.setTitle(title);
		return timePickerDialog;
	}

	/**
	 * 初始化 Calendar 字符串 必须为 yyyy-MM-dd
	 * 
	 * @param dateStr
	 * @return Calendar
	 */
	public static Calendar getCalendarWithDate(String dateStr) {
		try {
			return getCalendarWithDate(sdf.parse(dateStr.substring(0, 10)));
		} catch (Exception e) {
			LogGloble.e(TAG, "checkDate1(dateStr,c) 格式转换错误");
			return Calendar.getInstance();
		}
	}

	/**
	 * 初始化 Calendar
	 * 
	 * @param date
	 * @return
	 */
	public static Calendar getCalendarWithDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(date.getTime());
		return c;
	}

	public interface OnDateSelected {
		public void selectedListenner(String date);
	}

	public interface OnTimeSelected {
		public void selectedListenner(String date);
	}

	public interface onDateSelected2 {
		public void selectedListener(Date date);
	}

	public static StringBuffer formatDateToYYMMDD(String date) {
		String[] newDate = date.split("-");
		int year = Integer.valueOf(newDate[0]);
		int monthOfYear = Integer.valueOf(newDate[1]);
		int dayOfMonth = Integer.valueOf(newDate[2]);
		StringBuffer dateBuffer = new StringBuffer();
		dateBuffer.append(String.valueOf(year));
		dateBuffer.append("-");
		dateBuffer.append(((monthOfYear < 10) ? ("0" + monthOfYear)
				: (monthOfYear + "")));
		dateBuffer.append("-");
		dateBuffer.append(((dayOfMonth < 10) ? ("0" + dayOfMonth)
				: (dayOfMonth + "")));
		return dateBuffer;
	}
}
