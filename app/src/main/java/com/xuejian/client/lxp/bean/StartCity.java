package com.xuejian.client.lxp.bean;

import java.util.ArrayList;

public class StartCity {
	public String name;
	public String id;
	public double lat;
	public double lng;
	public String bdId;
	public String pinyin;
	public ArrayList<StartCity> childs = new ArrayList<StartCity>();
}
