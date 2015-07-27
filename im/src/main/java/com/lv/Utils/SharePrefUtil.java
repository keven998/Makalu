package com.lv.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yibiao.qin on 2015/7/24.
 */
public class SharePrefUtil {
    private final static String SP_NAME = "config";
    private static SharedPreferences sp;

    public static void saveBoolean(Context context, String key, boolean value) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        sp.edit().putBoolean(key, value).commit();
    }
    public static boolean getBoolean(Context context, String key,
                                     boolean defValue) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getBoolean(key, defValue);
    }
    public final static String LXQ_PREF = "lxq_pref";
    public final static String AD_PREF = "ad_pref";

    public static boolean getLxqPushSetting(Context context) {
        return SharePrefUtil.getBoolean(context, LXQ_PREF, true);
    }

    public void setLxqPushSetting(Context context, boolean lxqpush) {
        SharePrefUtil.saveBoolean(context, LXQ_PREF, lxqpush);
    }

}
