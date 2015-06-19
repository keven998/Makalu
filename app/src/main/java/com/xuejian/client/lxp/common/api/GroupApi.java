package com.xuejian.client.lxp.common.api;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.http.HttpManager;
import com.aizou.core.http.entity.PTHeader;
import com.aizou.core.http.entity.PTRequest;
import com.aizou.core.http.entity.PTRequestHandler;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.config.PeachApplication;
import com.xuejian.client.lxp.config.SystemConfig;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

/**
 * Created by yibiao.qin on 2015/6/19.
 */
public class GroupApi {
    public static final String HOST = "http://hedy.zephyre.me";

    public static PTRequestHandler createGroup(String requestBody,HttpCallBack callback){
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + "/groups");
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        StringEntity entity = null;
        try {
            entity = new StringEntity(requestBody, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        request.setBodyEntity(entity);
        return HttpManager.request(request, callback);
    }

    public static void setDefaultParams(PTRequest request){
        if(AccountManager.getCurrentUserId()!=null){
            request.setHeader("UserId", AccountManager.getCurrentUserId());
        }
        request.setHeader("Platform", "Android "+android.os.Build.VERSION.RELEASE);
        request.setHeader("Version", PeachApplication.APP_VERSION_NAME);
    }
}