package com.aizou.core.exception;

import android.util.Log;

/**
 * @ClassName: CLog
 * @Description: Crash日志
 * @author luql
 * @date 2014-1-8 上午11:14:01
 */
class CLog {
	public static final String COMMCATCH = "catch";
	/**
	 * 打印日志的开�?
	 */
	public static boolean LOGFLAG = true;

	/**
	 * 根据打印日志标识，判断是否打印日�? </p>
	 * 
	 * 级别：verbose
	 * 
	 * @param TAG
	 * @param msg
	 * @param t
	 */
	public static void v(String TAG, String msg, Throwable t) {
		if (msg != null) {
			if (LOGFLAG) {
				Log.v(TAG, msg, t);
			}
		}
	}

	/**
	 * 根据打印日志标识，判断是否打印日�?</p>
	 * 
	 * 级别：verbose
	 * 
	 * @param TAG
	 * @param msg
	 */
	public static void v(String TAG, String msg) {
		if (msg != null) {
			if (LOGFLAG) {
				Log.v(TAG, msg);
			}
		}
	}

	/**
	 * 根据打印日志标识，判断是否打印日�? </p>
	 * 
	 * 级别：debug
	 * 
	 * @param TAG
	 * @param msg
	 * @param t
	 */
	public static void d(String TAG, String msg, Throwable t) {
		if (msg != null) {
			if (LOGFLAG) {
				Log.d(TAG, msg, t);
			}
		}
	}

	/**
	 * 根据打印日志标识，判断是否打印日�?</p>
	 * 
	 * 级别：debug
	 * 
	 * @param TAG
	 * @param msg
	 */
	public static void d(String TAG, String msg) {
		if (msg != null) {
			if (LOGFLAG) {
				Log.d(TAG, msg);
			}
		}
	}

	/**
	 * 根据打印日志标识，判断是否打印日�?</p>
	 * 
	 * 级别：info
	 * 
	 * @param TAG
	 * @param msg
	 */
	public static void i(String TAG, String msg) {
		if (msg != null) {
			if (LOGFLAG) {
				Log.i(TAG, msg);
			}
		}
	}

	/**
	 * 根据打印日志标识，判断是否打印日�?</p>
	 * 
	 * 级别：info
	 * 
	 * @param TAG
	 * @param msg
	 * @param t
	 */
	public static void i(String TAG, String msg, Throwable t) {
		if (msg != null) {
			if (LOGFLAG) {
				Log.i(TAG, msg, t);
			}
		}
	}

	/**
	 * 根据打印日志标识，判断是否打印日�?</p>
	 * 
	 * 级别：warn
	 * 
	 * @param TAG
	 * @param msg
	 */
	public static void w(String TAG, String msg) {
		if (msg != null) {
			if (LOGFLAG) {
				Log.w(TAG, msg);
			}
		}
	}

	/**
	 * 根据打印日志标识，判断是否打印日�?</p>
	 * 
	 * 级别：warn
	 * 
	 * @param TAG
	 * @param msg
	 * @param t
	 */
	public static void w(String TAG, String msg, Throwable t) {
		if (msg != null) {
			if (LOGFLAG) {
				Log.w(TAG, msg, t);
			}
		}
	}

	/**
	 * 根据打印日志标识，判断是否打印日�?</p>
	 * 
	 * 级别：error
	 * 
	 * @param TAG
	 * @param msg
	 */
	public static void e(String TAG, String msg) {
		if (msg != null) {
			if (LOGFLAG) {
				Log.e(TAG, msg);
			}
		}
	}

	/**
	 * 根据打印日志标识，判断是否打印日�?</p>
	 * 
	 * 级别：error
	 * 
	 * @param TAG
	 * @param msg
	 * @param t
	 */
	public static void e(String TAG, String msg, Throwable t) {
		if (msg != null) {
			if (LOGFLAG) {
				Log.e(TAG, msg, t);
			}
		}
	}

}
