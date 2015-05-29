package com.xuejian.client.lxp.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rjm on 2014/10/16.
 */
public class InputCheckUtils {
    public static boolean checkNickNameIsNumber(String nickName){
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(nickName);
        return m.matches();
    }
}
