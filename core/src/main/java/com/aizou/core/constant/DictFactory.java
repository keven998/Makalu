package com.aizou.core.constant;

import java.util.Map;

public class DictFactory  {
	

	public static Map<String, DictData> map;
	
	public static  DictData getSexDict(){
		DictData d = new DictData();
		d.put("000", "男");
		d.put("001", "女");
		return d;
	}

}
