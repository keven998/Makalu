package com.xuejian.client.lxp.config;

import android.content.Context;

import com.aizou.core.utils.SharePrefUtil;

/**
 * Created by Rjm on 2014/10/15.
 */
public class SettingConfig {
    private  static SettingConfig instance;
    public static SettingConfig getInstance(){
        if(instance==null){
            instance = new SettingConfig();
        }
        return  instance;
    }
    public final static String LXQ_PREF="lxq_pref";
    public final static String AD_PREF="ad_pref";

    public boolean getLxqPushSetting(Context context){
        return SharePrefUtil.getBoolean(context,LXQ_PREF,true);
    }

    public void setLxqPushSetting(Context context,boolean lxqpush){
         SharePrefUtil.saveBoolean(context,LXQ_PREF,lxqpush);
    }

    public boolean getAdPushSetting(Context context){
        return SharePrefUtil.getBoolean(context,AD_PREF,true);
    }

    public void setAdPushSetting(Context context,boolean adpush){
        SharePrefUtil.saveBoolean(context,AD_PREF,adpush);
    }
}
