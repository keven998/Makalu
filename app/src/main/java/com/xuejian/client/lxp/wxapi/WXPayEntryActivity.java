package com.xuejian.client.lxp.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xuejian.client.lxp.common.utils.ShareUtils;
import com.xuejian.client.lxp.module.goods.OrderConfirmActivity;
import com.xuejian.client.lxp.module.pay.PaymentActivity;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "WXPayEntryActivity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   //     setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, ShareUtils.PlatfromSetting.WX_APPID);

        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			if (resp.errCode == 0){
			//	Toast.makeText(WXPayEntryActivity.this,"支付成功",Toast.LENGTH_SHORT).show();
				Intent intent1 = new Intent();
                intent1.setClass(WXPayEntryActivity.this, PaymentActivity.class);
                intent1.putExtra("wxSuccess", true);
                startActivity(intent1);
			}else if (resp.errCode == -2){
				Toast.makeText(WXPayEntryActivity.this, "支付取消 " , Toast.LENGTH_SHORT).show();
				Intent intent1 = new Intent();
				intent1.setClass(WXPayEntryActivity.this, OrderConfirmActivity.class);
				intent1.putExtra("cancel", true);
				startActivity(intent1);
				finish();
               // startActivity(intent);
			}else {
				Toast.makeText(WXPayEntryActivity.this,"支付失败",Toast.LENGTH_SHORT).show();
				Intent intent1 = new Intent();
				intent1.setClass(WXPayEntryActivity.this, OrderConfirmActivity.class);
				intent1.putExtra("cancel", true);
				startActivity(intent1);
				finish();
              //  startActivity(intent);
			}

			finish();
		}
	}
}