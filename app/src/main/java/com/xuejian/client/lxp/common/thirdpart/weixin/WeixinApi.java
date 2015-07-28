package com.xuejian.client.lxp.common.thirdpart.weixin;

import android.app.Activity;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xuejian.client.lxp.common.thirdpart.SnsAccountsUtils;

/**
 * Created by Rjm on 2014/10/15.
 */
public class WeixinApi {
    private WeixinAuthListener mListener;
    private static WeixinApi instance;

    public static WeixinApi getInstance() {
        if (instance == null) {
            instance = new WeixinApi();
        }
        return instance;
    }

    public WeixinAuthListener getAuthListener() {
        return mListener;
    }

    public boolean isWXinstalled(Activity activity){
         IWXAPI wxAPi = WXAPIFactory.createWXAPI(activity, SnsAccountsUtils.WeiXinConstants.APP_ID);
        return wxAPi.isWXAppInstalled();
    }
    public void auth(Activity activity, WeixinAuthListener listener) {
        mListener = listener;
        IWXAPI wxAPi = WXAPIFactory.createWXAPI(activity, SnsAccountsUtils.WeiXinConstants.APP_ID);
        // send oauth request
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        wxAPi.sendReq(req);

    }

    public interface WeixinAuthListener {
        void onComplete(String code);

        void onError(int errCode);

        void onCancel();
    }

}
