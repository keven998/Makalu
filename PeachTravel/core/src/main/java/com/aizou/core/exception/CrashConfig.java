package com.aizou.core.exception;

/**
 * @ClassName: CrashConfig
 * @Description: 配置信息
 * @author luql
 * @date 2014-1-8 上午09:54:14
 */
public class CrashConfig {

	/**
	 * 配置路径 %path = "/sdcard/chinamworld/crash/"
	 * */
	private String localPath;

	/**
	 * 服务器上传地�?
	 */
	private String remoteUrl;
	/**
	 * 认证用户�?
	 */
	private String name;
	/**
	 * 认证密码
	 */
	private String password;
	/**
	 * 是否�?�?
	 */
	private boolean debug;

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getRemoteUrl() {
		return remoteUrl;
	}

	public void setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
	}

}
