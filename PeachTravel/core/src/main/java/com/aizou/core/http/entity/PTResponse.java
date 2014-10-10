package com.aizou.core.http.entity;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 返回主体
 * 
 * @author xby
 * 
 *   例子:</p>
 *   response：  { status:"01", result{
 *   dataId1:"dataValue1",dataId2:"dataValue2",token:""} }
 */
public class PTResponse implements Serializable {

	private static final long serialVersionUID = 6616420914940367247L;

	/** 状态 01代表正常*/
	String status;

	/** 返回结果数据 */
	HashMap<String, Object> result; // 该result,有时是String形式,有时是Map形式或者List形式 data对应的value为业务数据信息
	
	/** 错误信息 */
	String error;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public HashMap<String, Object> getResult() {
		return result;
	}

	public void setResult(HashMap<String, Object> result) {
		this.result = result;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	

}
