
package com.aizou.peachtravel.wxapi;

import com.aizou.peachtravel.common.thirdpart.weixin.WeixinApi;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

public class WXEntryActivity extends WXCallbackActivity {


    @Override
    public void onResp(BaseResp resp) {
        if(resp instanceof SendAuth.Resp){
            WeixinApi.WeixinAuthListener authListener= WeixinApi.getInstance().getAuthListener();
            if (resp.errCode != 0)
            {
                if (resp.errCode == -2)
                    if(authListener!=null){
                        authListener.onCancel();
                    }
                else {
                        if(authListener!=null){
                            authListener.onError(resp.errCode);
                        }

                }
            }else{
                SendAuth.Resp sendResp = (SendAuth.Resp)resp;
                String code = sendResp.code;
                if(authListener!=null){
                    authListener.onComplete(code);
                }

            }

            finish();
            return;
        }
        super.onResp(resp);
    }
}
