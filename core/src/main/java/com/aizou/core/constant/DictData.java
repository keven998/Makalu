package com.aizou.core.constant;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * @author xbybaoying
 */
public class DictData {

	protected LinkedHashMap<String, String> SDATA_MAP ;

	/**
	 * @Title: getSexName
	 * @Description: 获取性别显示名称
	 * @param key
	 * @return
	 */
	public String getValue(String key) {
		if (SDATA_MAP.isEmpty()) {
			initMap();
		}
		if (SDATA_MAP.containsKey(key)) {
			return SDATA_MAP.get(key);
		}
		return "";
	}

	/**
	 * @Title: getSexName
	 * @Description: 获取性别代号
	 * @param value
	 *            姓名名称
	 * @return
	 */
	public String getkey(String value) {
		if (SDATA_MAP.isEmpty()) {
			initMap();
		}
		for (Entry<String, String> map : SDATA_MAP.entrySet()) {
			if (map.getValue().equals(value)) {
				return map.getKey();
			}
		}
		return "";
	}

	/**
	 * @Title: initSexMap
	 * @Description: 初始化性别集合
	 */
	public void initMap() {
		if(SDATA_MAP ==null){
			SDATA_MAP = new LinkedHashMap<String, String>();
		}
	}
	
	public void put(String key,String value){
		initMap();
		SDATA_MAP.put(key, value);
	}
	
	/**
	 * 获取值集合
	 * @return
	 */
	public ArrayList<String> getValues(){
		ArrayList<String> lists = new ArrayList<String>();
		for (Entry<String, String> map : SDATA_MAP.entrySet()) {
			if (map.getValue().equals(SDATA_MAP)) {
				lists.add(map.getValue());
			}
		}
		return lists;
	}
	
	

}
