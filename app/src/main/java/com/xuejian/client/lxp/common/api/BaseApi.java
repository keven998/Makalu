package com.xuejian.client.lxp.common.api;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.http.HttpManager;
import com.aizou.core.http.entity.PTHeader;
import com.aizou.core.http.entity.PTRequest;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.config.PeachApplication;
import com.xuejian.client.lxp.db.User;

/**
 * Created by Rjm on 2014/10/16.
 */
public class BaseApi {
    public final static int PAGE_SIZE =15;

    public static void testHttps(){
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl("https://kyfw.12306.cn/otn");
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE,"application/json");
        HttpManager.request(request, new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }
        });
    }

    public static void setDefaultParams(PTRequest request){
        User user = AccountManager.getInstance().getLoginAccount(PeachApplication.getContext());
        if(user!=null){
            request.setHeader("UserId", user.getUserId()+"");
        }
        request.setHeader("Platform", "Android "+android.os.Build.VERSION.RELEASE);
        request.setHeader("Version", PeachApplication.APP_VERSION_NAME);
    }

}
