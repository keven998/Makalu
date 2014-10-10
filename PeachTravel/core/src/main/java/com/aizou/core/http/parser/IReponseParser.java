package com.aizou.core.http.parser;

/**
 * Created by Rjm on 2014/10/8.
 */
public interface IReponseParser<T> {
   public  T parse(String result,Class<T> clazz);
}
