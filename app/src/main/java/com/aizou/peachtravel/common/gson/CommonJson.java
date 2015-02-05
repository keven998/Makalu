package com.aizou.peachtravel.common.gson;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.gson.Gson;

public class CommonJson<T> implements Serializable {

	/**
     *
     */
	private static final long serialVersionUID = -3440061414071692254L;

	/**
	 * 是否成功
	 */
	public int code = 1;
	public ErrBean err;

	public class ErrBean {
		public String message="";
		public String extro;
	}

	/**
	 * 数据
	 */
	public T result;


	public T getData() {
		return result;
	}

	public void setData(T data) {
		this.result = result;
	}

	public static CommonJson fromJson(String json, Class clazz) {
		Gson gson = new Gson();
		Type objectType = type(CommonJson.class, clazz);
		return gson.fromJson(json, objectType);
	}

	public String toJson(Class<T> clazz) {
		Gson gson = new Gson();
		Type objectType = type(CommonJson.class, clazz);
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
