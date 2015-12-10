package com.xuejian.client.lxp.common.alipay;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by xuyongchen on 15/10/21.
 */
public class PayUtils {

    // 商户PID
    public static final String PARTNER = "2088021950613142";
    // 商户收款账号
    public static final String SELLER = "xjpay@xuejianinc.com";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKGaAyDXCtls1k+xY8l8ZnOqRG/NL+cgTzpqp5Asr67bt4qQR8dn9PmZt1aZovuJ1/4ptSMbNtYYCkonz/JV8knXtze83Xymhaabkji0pN9utTWzxOYuhK4rE7rYCM+LeOirIMa/DiJDbXN8X1ezaY2kwnPk9UfIocAQefpTbyHzAgMBAAECgYBaLpWCVR9T2L38eTUDsu2FrMZGUg5igBBWvEBIX/emsCzMIdo8uJHoXNUIefKPRwyHZQUV01EVxxKu8WrFhRsRIUY/R2LuzEF4zu4flj0juePJQHmE8NcF+YsYkPmptIB/Ih1ySNdAeU/8uGS9sNvxYyKKN15dA/moVjWMwhc7cQJBAM+IYfnk2AHE6pT3IRNQT9AytSAkqlD9K8SjIrPFXnBqnYTH4zdijFe/Yg5i4Zkn2qHkQGw/03RAFpNtPq+zKjkCQQDHV5ON0/wDMqsKrh0mgi2k6TGgDxljqjG17YuQDNp4k9C8Ggrt4941pkjAqdRM4YrsYqzG5fzV23KZB0CfVN2LAkB+AeD7Seedq2KDcEm04F6VmzQ+gASpiA67JhI79OqSdymNJZDgYAW2gY2YDpHL3FbzVROIpnhNbVxHx1z4cWCBAkBnYV/AuiOEdQ6HZ2zuv6x0TgvSeoIsUeOd+ifa+Q+EYQRryQXKAdcH88jfQjQ7+uxVU4dPT48kSb7Q5hX5i8WpAkEAoUBYL8/SkKOcn9KUQsmOcIFATPvEPZDqXmUQj7GZt4acSLbxZnrhFYg7jO76OAiOuYjEC/n4GGsa85SoIhGIFw==";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";
    private static final int SDK_PAY_FLAG = 1;

    /**
     * create the order info. 创建订单信息
     *
     */
    public static String getOrderInfo(String subject, String body, String price) {

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
        return orderInfo;
    }


    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     *
     */
    public static String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
                Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);

        return key;
    }


    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content
     *            待签名订单信息
     */
    public static String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }


    /**
     * get the sign type we use. 获取签名方式
     *
     */
    public static String getSignType() {
        return "sign_type=\"RSA\"";
    }
}
