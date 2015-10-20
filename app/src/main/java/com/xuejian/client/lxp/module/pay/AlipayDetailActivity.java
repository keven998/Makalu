package com.xuejian.client.lxp.module.pay;

import android.app.Activity;

/**
 * Created by xuyongchen on 15/10/15.
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.alipay.sdk.app.PayTask;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.common.alipay.PayResult;
import com.xuejian.client.lxp.common.alipay.SignUtils;


public class AlipayDetailActivity extends Activity {

    // 商户PID
    public static final String PARTNER = "2088021950613142";
    // 商户收款账号
    public static final String SELLER = "xjpay@xuejianinc.com";
    // 商户私钥，pkcs8格式
   /* public static final String RSA_PRIVATE ="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOsl2Ht4UT7IoCr7" +
            "TX2jNdUNA8wk38VLVCCxTL9NBds80QBj2cATD2o6Iv0Tk1RElZ/ZqZ4FFFylPZY6" +
            "h3EW9NTwifG37Rod4o/87DteirKUWOn3k5qsMv1h2GdmUsvj6wAxznQKuXX6/szZ" +
            "ioKtYU2Bu57D6b2cmKyDAfffpZD7AgMBAAECgYEAhT2xl+zKJIgr8Y7qDsm/RBSW" +
            "1UMDpNiVN90e/rrXGa4xQ0ZQ6TwjnO6KStqfvb0LB0Ofj9GTAxgCeLB7dIIsgbIp" +
            "KE9Jdlglc0HIYdBTiyMxGvPe9F0dBLQjFfYap5NUtSDohh+Y8dcS9+HqS0BcwC7K" +
            "kbLFHuupnQk+wRIipIECQQD2LKO9AnZCWgFIahbSlsqgybp2259iOBzHl9y1xi4H" +
            "fMrmWUTJyCeYdqWMtBJQ+LNVkZA4ziPXrrIqpz6clmi7AkEA9IiKAEgPXlgT5BzA" +
            "hSjxiRqm/9/0+UyzYqC1iHi5M6tN8U90bMXPdrDHXgeMiAE4NVdy6g6REh0Tmtih" +
            "FhYUwQJAF3Fe0mQGoQDzOFMqAznq5+7t+92kSvUvAG7czM6nmXzv0YnUvCZ8Zs99" +
            "qm2HhQB6C38GLfKrhSGUw+0TUFQeewJBAM0EFL97qvbsXXDo8jgyRZ8va0gn2lmR" +
            "huxv1QMgRBYfz287sF7p0bCvMdeR+K6mZxvBv6SgpdxL6H3wlc3QoYECQGTnkaLy" +
            "jZkfvDyIw6mva+nYsNTBPONKB+D4mU1e4GRVAg2C49+e6nCCy3uV4cNcWbMHwoYJ" +
            "IDxY74AGlgJcV0I=";*/

    public static final String RSA_PRIVATE="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKGaAyDXCtls1k+xY8l8ZnOqRG/NL+cgTzpqp5Asr67bt4qQR8dn9PmZt1aZovuJ1/4ptSMbNtYYCkonz/JV8knXtze83Xymhaabkji0pN9utTWzxOYuhK4rE7rYCM+LeOirIMa/DiJDbXN8X1ezaY2kwnPk9UfIocAQefpTbyHzAgMBAAECgYBaLpWCVR9T2L38eTUDsu2FrMZGUg5igBBWvEBIX/emsCzMIdo8uJHoXNUIefKPRwyHZQUV01EVxxKu8WrFhRsRIUY/R2LuzEF4zu4flj0juePJQHmE8NcF+YsYkPmptIB/Ih1ySNdAeU/8uGS9sNvxYyKKN15dA/moVjWMwhc7cQJBAM+IYfnk2AHE6pT3IRNQT9AytSAkqlD9K8SjIrPFXnBqnYTH4zdijFe/Yg5i4Zkn2qHkQGw/03RAFpNtPq+zKjkCQQDHV5ON0/wDMqsKrh0mgi2k6TGgDxljqjG17YuQDNp4k9C8Ggrt4941pkjAqdRM4YrsYqzG5fzV23KZB0CfVN2LAkB+AeD7Seedq2KDcEm04F6VmzQ+gASpiA67JhI79OqSdymNJZDgYAW2gY2YDpHL3FbzVROIpnhNbVxHx1z4cWCBAkBnYV/AuiOEdQ6HZ2zuv6x0TgvSeoIsUeOd+ifa+Q+EYQRryQXKAdcH88jfQjQ7+uxVU4dPT48kSb7Q5hX5i8WpAkEAoUBYL8/SkKOcn9KUQsmOcIFATPvEPZDqXmUQj7GZt4acSLbxZnrhFYg7jO76OAiOuYjEC/n4GGsa85SoIhGIFw==";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";
    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    Log.e("payresult",payResult+"----------------------");
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(AlipayDetailActivity.this, "支付成功",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(AlipayDetailActivity.this, "支付结果确认中",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(AlipayDetailActivity.this, "支付失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(AlipayDetailActivity.this, "检查结果为：" + msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alipay_detal_activity);
    }

    /**
     * call alipay sdk pay. 调用SDK支付
     *
     */
    public void pay(View v) {
        if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE)
                || TextUtils.isEmpty(SELLER)) {
            new AlertDialog.Builder(this)
                    .setTitle("警告")
                    .setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface, int i) {
                                    //
                                    finish();
                                }
                            }).show();
            return;
        }
        // 订单
        String orderInfo = getOrderInfo("1", "我是测试数据", "0.02");

        // 对订单做RSA 签名
        String sign = sign(orderInfo);
        Log.e("sign",sign+"--------------------");
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (Exception e) {
            Log.e("error",e.getMessage()+e.getCause()+"------------------");
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + getSignType();
        Log.e("payInfo",payInfo+"---------------------");
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(AlipayDetailActivity.this);
                // 调用支付接口，获取支付结果

                String result = alipay.pay(payInfo);
                Log.e("result",result+"-----------------------");
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     *
     */
    public void check(View v) {
        Runnable checkRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(AlipayDetailActivity.this);
                // 调用查询接口，获取查询结果
                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();

    }

    /**
     * get the sdk version. 获取SDK版本号
     *
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * create the order info. 创建订单信息
     *
     */
    public String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://www.xxx.com"
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
      //  orderInfo += "&return_url=\"m.alipay.com\"";
        orderInfo += "&show_url=\"m.alipay.com\"";
        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";
        Log.e("orderInfo",orderInfo+"---------------------");
        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     *
     */
    public String getOutTradeNo() {
        /*SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
                Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);*/
        String key ="UT77IMUC8V61SX3";
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content
     *            待签名订单信息
     */
    public String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }

}
