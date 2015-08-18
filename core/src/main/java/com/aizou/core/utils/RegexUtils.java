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
      Pattern p = Pattern
              .compile("^((10[0-9])|(13[0-9])|(15[[0-9]])|(18[0-9])|(17[0-9])|(14[0-9])|(19[0-9]))\\d{8}$");
//        Pattern p = Pattern.compile("^1\\d{10}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
    public static boolean isVerfyCode(String mobiles) {
        Pattern p = Pattern
                .compile("^([0-9])\\d{5}$");
//        Pattern p = Pattern.compile("^1\\d{10}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
    public static boolean isPwdOk(String pwd){
        //密码：6-12数字，英文组合
        Pattern p = Pattern.compile("^[0-9a-zA-Z_]{6,12}$");
        Matcher m = p.matcher(pwd);
        return m.matches();
    }

    public static boolean checkNickName(String nickname){
//        只支持中英文、数字和下划线
//        用户名唯一
//        12位一下
        Pattern p = Pattern.compile("^[\\u4E00-\\u9FA5|0-9a-zA-Z|_]{1,16}$");
        Matcher m = p.matcher(nickname);
        return m.matches();
    }
    public static boolean checkSignatrue(String sign){
//        150位一下
        Pattern p = Pattern.compile("^{1,150}$");
        Matcher m = p.matcher(sign);
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
