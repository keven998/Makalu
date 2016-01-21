package com.xuejian.client.lxp.module.pay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.common.alipay.PayResult;
import com.xuejian.client.lxp.common.alipay.PayUtils;
import com.xuejian.client.lxp.common.weixinpay.Constants;
import com.xuejian.client.lxp.common.weixinpay.MMPayUtil;
import com.xuejian.client.lxp.common.weixinpay.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuyongchen on 15/10/21.
 */
public class PayDetailAcitivity extends Activity{
    private Button confirm_topay;
    private int payType=-1;
    private final int ALI_SDK_PAY=0x823;
    private final int MM_SDK_PREID=0x824;
    PayReq req;
    IWXAPI msgApi;

    /*****************************************************************
     * mMode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
     *****************************************************************/
    private final String mMode = "01";
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ALI_SDK_PAY:
                    PayResult payResult = new PayResult((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(PayDetailAcitivity.this, "支付成功",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(PayDetailAcitivity.this, "支付结果确认中",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(PayDetailAcitivity.this, "支付失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case MM_SDK_PREID:
                    Map<String,String> resultunifiedorder =(Map<String,String>) msg.obj;
                    genPayReq(resultunifiedorder);
                    sendPayReq();
                    break;
                }

            }
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.pay_detail_activity);
        payType = getIntent().getIntExtra("payType",-1);
        initPay(payType);
        confirm_topay = (Button)findViewById(R.id.confirm_topay);
        confirm_topay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startToPay(payType,"0.01");
            }
        });
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


    }
    private void initPay(int type){
        if(type==2){
            msgApi = WXAPIFactory.createWXAPI(this, null);
            req = new PayReq();
            msgApi.registerApp(Constants.APP_ID);
        }
    }
    private void startToPay(int type,String fees){
        switch (type){
            case 1:
                startAlipay(fees);
                break;
            case 2:
                startWeixinpay(fees);
                break;
            default:
                Toast.makeText(PayDetailAcitivity.this,"请选择支付方式！",Toast.LENGTH_SHORT).show();
        }
    }

    private void startAlipay(String fees){
            // 订单
            String orderInfo = PayUtils.getOrderInfo("1", "我是测试数据", "0.02");

            // 对订单做RSA 签名
            String sign = PayUtils.sign(orderInfo);
            try {
                // 仅需对sign 做URL编码
                sign = URLEncoder.encode(sign, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 完整的符合支付宝参数规范的订单信息
            final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                    + PayUtils.getSignType();

            Runnable payRunnable = new Runnable() {

                @Override
                public void run() {
                    // 构造PayTask 对象
                    PayTask alipay = new PayTask(PayDetailAcitivity.this);
                    // 调用支付接口，获取支付结果

                    String result = alipay.pay(payInfo);
                    Message msg = new Message();
                    msg.what = ALI_SDK_PAY;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            };

            // 必须异步调用
            Thread payThread = new Thread(payRunnable);
            payThread.start();

    }



    private void startWeixinpay(String fees){
        GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
        getPrepayId.execute();
    }


    private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String,String>> {

        private ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(PayDetailAcitivity.this,"提示","正在获取预支付订单...");
        }

        @Override
        protected void onPostExecute(Map<String,String> result) {
            if (dialog != null) {
                dialog.dismiss();
            }
           // sb.append("prepay_id\n"+result.get("prepay_id")+"\n\n");
           // show.setText(sb.toString());
            Message message = new Message();
            message.what=MM_SDK_PREID;
            message.obj=result;
            mHandler.sendMessage(message);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Map<String,String>  doInBackground(Void... params) {
            String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
            String entity = MMPayUtil.genProductArgs();
            byte[] buf = Util.httpPost(url, entity);

            String content = new String(buf);
            Map<String,String> xml=MMPayUtil.decodeXml(content);

            return xml;
        }
    }

    private void genPayReq(Map<String,String> result) {

        req.appId = Constants.APP_ID;
        req.partnerId = Constants.MCH_ID;
        req.prepayId = result.get("prepay_id");
        req.packageValue = "Sign=WXPay";
        req.nonceStr = MMPayUtil.genNonceStr();
        req.timeStamp = String.valueOf(genTimeStamp());


        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

        req.sign = MMPayUtil.genAppSign(signParams);

    }

    private void sendPayReq() {
        msgApi.registerApp(Constants.APP_ID);
        msgApi.sendReq(req);
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }




}
