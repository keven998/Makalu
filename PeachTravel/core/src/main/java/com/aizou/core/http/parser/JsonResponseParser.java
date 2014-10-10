package com.aizou.core.http.parser;

import com.aizou.core.utils.GsonTools;
import com.google.gson.Gson;

/**
 * Created by Rjm on 2014/10/8.
 */
public class  JsonResponseParser<T> implements IReponseParser<T> {
    Gson gson = new Gson();
    @Override
    public T parse(String result, Class<T> clazz) {
        return GsonTools.parseJsonToBean(result,clazz);
    }
}
