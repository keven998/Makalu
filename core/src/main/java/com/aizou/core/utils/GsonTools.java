package com.aizou.core.utils;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class GsonTools {

	public GsonTools() {
		// TODO Auto-generated constructor stub
	}

	public static String createGsonString(Object object) {
		Gson gson = new Gson();
		String gsonString = gson.toJson(object);
		return gsonString;
	}

	public static <T> T parseJsonToBean(String gsonString, Class<T> cls) {
		Gson gson = new Gson();
		T t = gson.fromJson(gsonString, cls);
		return t;
	}
	
	public static <T> T parseJsonToBean(String gsonString, TypeToken<T> tkon) {
		Gson gson = new Gson();
		T t = gson.fromJson(gsonString, tkon.getType());
		return t;
	}

	

	

}
