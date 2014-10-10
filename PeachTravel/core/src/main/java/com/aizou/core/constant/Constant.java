package com.aizou.core.constant;

/**
 * 描述:常量类
 */
public class Constant {
	public static final String DEFAULT_SHAREDPREFERENCES_NAME = "itlsp";
	// 版本管理平台地址（生产）
	public final static String UpdateUrl = "https://mbs.boc.cn/BocMBCGate/MBCExchangeVersionInfo.do";//

	public final static String FIDGET_TABLE_NAME = "fidgettable";
	/**下载应用的数据库表*/
	public final static String DOWNLOAD_TABLE_NAME = "itldownload";
	public final static int DB_VERSION = 1;
	/**应用类别*/
	public final static String FIDGETBEAN_TYPE = "type";
	
	public static final String DEFAULT_ENCORD = "UTF-8";// 默认通讯编码方式
	public static final String HTTP = "http";// 注册通讯访问方式，以及UI工具类处理地址时使用
	public static final String HTTPS = "https";// 注册通讯访问方式，以及UI工具类处理地址时使用
	/**正常的请求码*/
	public static final String SUCESS_STATE_CODE = "01";
	
	
	/**
	 * 程序下载文件夹SD卡中根目录下
	 */
	public static final String DOWNLOADFILENAME = "PeathTravel";
	/**系统将进程杀掉标示*/
	public static final String SYSTEM_KILL_KEY = "system_kill";

	/**
	 * 日夜间模式
	 */
	public static final String DATE_MODE_DAY_NIGHT="date_mode_day_night";

    /**
     * 页个数
     */
	public static final String PAGE_SIZE ="10";
	
}
