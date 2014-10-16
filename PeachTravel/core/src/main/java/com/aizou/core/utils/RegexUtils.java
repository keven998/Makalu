package com.aizou.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rjm on 2014/10/15.
 */
public class RegexUtils {
    /**
     * 判断是否是合法手机号
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
//      Pattern p = Pattern
//              .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9])|(18[1,0-1]))\\d{8}$");
        Pattern p = Pattern.compile("^1\\d{10}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isPwdOk(String pwd){
        //密码：6-12数字，英文组合
        Pattern p = Pattern.compile("^[0-9a-zA-Z_]{6,12}$");
        Matcher m = p.matcher(pwd);
        return m.matches();
    }

    /**
     * 验证输入的邮箱格式是否符合
     * @param email
     * @return 是否合法
     */
    public static boolean isEmail(String email) {
        String emailPattern = "[a-zA-Z0-9][a-zA-Z0-9._-]{2,16}[a-zA-Z0-9]@[a-zA-Z0-9]+.[a-zA-Z0-9]+";
        boolean result = Pattern.matches(emailPattern, email);
        return result;
    }
}
