package com.aizou.core.constant;

/**
 * @ClassName: SystemConfig
 * @Description: 系统配置文件
 * @author luql
 * @date 2014-1-8 下午03:48:27
 */
public class SystemConfig {

	/**
	 * SD卡数据库存储名称
	 */
	public static String DB_NAME = "peath_travel.db";

	public static String TOKEN_CODE = "TOKEN_CODE";
	public static String SERVER_DATE = "SERVER_DATE";
	public static String SERVER_TIME = "SERVER_TIME";
	/**
	 * 小胖地址
	 */
	public static String XP_URL = "http://192.168.11.132:8080/kqService/";

	/**
	 * 英华地址
	 */
	public static String YH_URL = "http://192.168.1.124:8080/kqService/";
	
	/**
	 * 投产地址设置
	 **/
	
	public static String BASE_SERVICE_URL = "http://app.eshangke.com";

	/**
	 * 是否为debug模式，投产需要改为false
	 */
	public static final boolean DEBUG = true;

	/**
	 * 打印日志的开关,生产版本时改为false
	 */
	public static boolean LOGFLAG = true;

	/**
	 * 是否读取SD卡数据
	 */
	public static boolean READ_SDCARD = false;

	/**
	 * 是否保存到SD卡
	 */
	public static boolean LOGSAVE_SDCARD = false;

	/**
	 * 客户端版本
	 */
	public static final String APP_VERSION = "1.0";

	/**
	 * 签到失败，不在签到范围
	 */
	public static final String SIGN_FAILED_CODE = "000000";

	/**
	 * 终端操作系统
	 */
	public static final String APP_OS = "ANDROID";
}
