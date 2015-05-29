package com.xuejian.client.lxp.common.gson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class CommonJson4List<T> implements Serializable {

	/**
     *
     */
	private static final long serialVersionUID = -369558847578246550L;

	/**
	 * 是否成功
	 */
	public int code = 1;
	public ErrBean err;

	public class ErrBean {
		public String message;
		public String extro;
	}

	/**
	 * 数据
	 */
	 Class<T> type;
	 
	
	public CommonJson4List() {
		super();
        this.type = (Class<T>) getClass();
	}

	public List<T> result;

	public List<T> getData() {
		return result;
	}

	public void setData(List<T> data) {
		this.result = result;
	}

	public static CommonJson4List fromJson(String json, Class clazz) {
		Gson gson = new Gson();
		Type objectType = type(CommonJson4List.class, clazz);
		return gson.fromJson(json, objectType);
	}
	
	public CommonJson4List<T> fromJsonList(String json, TypeToken<T> token) {
		Gson gson = new Gson();
        return gson.fromJson(json, token.getType());  
	}

	public String toJson(Class<T> clazz) {
		Gson gson = new Gson();
		Type objectType = type(CommonJson4List.class, clazz);
		return gson.toJson(this, objectType);
	}

	static ParameterizedType type(final Class raw, final Type... args) {
		return new ParameterizedType() {
			public Type getRawType() {
				return raw;
			}

			public Type[] getActualTypeArguments() {
				return args;
			}

			public Type getOwnerType() {
				return null;
			}
		};
	}
}