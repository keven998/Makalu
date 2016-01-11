package com.xuejian.client.lxp.common.api;

import android.text.TextUtils;

import com.aizou.core.http.entity.PTRequest;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.security.SecurityUtil;
import com.xuejian.client.lxp.config.PeachApplication;
import com.xuejian.client.lxp.db.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Rjm on 2014/10/16.
 */
public class BaseApi {
    public final static int PAGE_SIZE = 15;


    public static void setDefaultParams(PTRequest request) {
        User user = AccountManager.getInstance().getLoginAccount(PeachApplication.getContext());
        if (user != null) {
            request.setHeader("UserId", String.valueOf(user.getUserId()));
        //    request.setHeader("X-Lvxingpai-Id", String.valueOf(user.getUserId()));
        }
        if (!TextUtils.isEmpty(PeachApplication.ChannelId)) {
            request.setHeader("ChannelId", PeachApplication.ChannelId);
        }
        request.setHeader("Accept", "application/vnd.lvxingpai.v1+json");
        request.setHeader("Platform", "Android " + android.os.Build.VERSION.RELEASE);
        request.setHeader("Version", PeachApplication.APP_VERSION_NAME);
    }


    public static void setDefaultParams(PTRequest request, String body) {
        if (!TextUtils.isEmpty(PeachApplication.ChannelId)) {
            request.setHeader("ChannelId", PeachApplication.ChannelId);
        }
        String date = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss 'GMT'", Locale.ENGLISH).format(new Date());
        request.setHeader("Accept", "application/vnd.lvxingpai.v1+json");
        request.setHeader("Platform", "Android " + android.os.Build.VERSION.RELEASE);
        request.setHeader("Version", PeachApplication.APP_VERSION_NAME);
        request.setHeader("Date", date);
        User user = AccountManager.getInstance().getLoginAccount(PeachApplication.getContext());
        if (user != null) {
            request.setHeader("UserId", String.valueOf(user.getUserId()));
            request.setHeader("X-Lvxingpai-Id", String.valueOf(user.getUserId()));
            try {
                SecurityUtil.getAuthBody(user.getSecretKey(),request,body,date, String.valueOf(user.getUserId()));
            } catch (Exception e) {
                e.printStackTrace();
            }
         //   request.setHeader("Authorization", "LVXINGPAI-v1-HMAC-SHA256 ");
        }
    }
}
