package com.xuejian.client.lxp.common.security;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;

import com.aizou.core.http.entity.PTRequest;
import com.xuejian.client.lxp.bean.SecretKeyBean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
/**
 * Created by yibiao.qin on 2016/1/7.
 */
public class SecurityUtil {
    /**
     * 定义加密方式
     * MAC算法可选以下多种算法
     * <pre>
     * HmacMD5
     * HmacSHA1
     * HmacSHA256
     * HmacSHA384
     * HmacSHA512
     * </pre>
     */
    private final static String KEY_MAC = "HmacSHA256";

    /**
     * 全局数组
     */
    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * 构造函数
     */
    public SecurityUtil() {

    }

    /**
     * BASE64 加密
     *
     * @param key 需要加密的字节数组
     * @return 字符串
     * @throws Exception
     */
    public static String encryptBase64(byte[] key) throws Exception {
        return new String(Base64.encode(key, Base64.DEFAULT));
    }

    /**
     * BASE64 解密
     *
     * @param key 需要解密的字符串
     * @return 字节数组
     * @throws Exception
     */
    public static byte[] decryptBase64(String key) throws Exception {
        return Base64.decode(key.getBytes(), Base64.DEFAULT);
    }

    /**
     * 初始化HMAC密钥
     *
     * @return
     */
    public static String init() {
        SecretKey key;
        String str = "key";


        try {
            KeyGenerator generator = KeyGenerator.getInstance(KEY_MAC);
            key = generator.generateKey();
            str = encryptBase64(key.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * HMAC加密
     *
     * @param data 需要加密的字节数组
     * @param key  密钥
     * @return 字节数组
     */
    public static byte[] encryptHMAC(byte[] data, String key) {
        SecretKey secretKey;
        byte[] bytes = null;
        try {
            secretKey = new SecretKeySpec(key.getBytes("UTF-8"), KEY_MAC);
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            bytes = mac.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * HMAC加密
     *
     * @param data 需要加密的字符串
     * @param key  密钥
     * @return 字符串
     */
    public static String encryptHMAC(String data, String key) {
        if (TextUtils.isEmpty(data)) {
            return null;
        }
        byte[] bytes = new byte[0];
        try {
            bytes = encryptHMAC(data.getBytes("UTF-8"), key);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return new String(new org.apache.commons.codec.binary.Base64().encode(bytes));
        // return byteArrayToHexString(bytes);
    }


    /**
     * 将一个字节转化成十六进制形式的字符串
     *
     * @param b 字节数组
     * @return 字符串
     */
    private static String byteToHexString(byte b) {
        int ret = b;
        //System.out.println("ret = " + ret);
        if (ret < 0) {
            ret += 256;
        }
        int m = ret / 16;
        int n = ret % 16;
        return hexDigits[m] + hexDigits[n];
    }

    /**
     * 转换字节数组为十六进制字符串
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(byteToHexString(bytes[i]));
        }

        return sb.toString();
    }

    public static String getAuthBody(SecretKeyBean key,PTRequest request,String body,String date ,String userId) throws Exception{

        String path = Uri.parse(request.readUrl()).getPath();
        StringBuilder header =  new StringBuilder();
        header.append("date=").append(URLEncoder.encode(date,"UTF-8"))
                .append("&")
                .append("X-Lvxingpai-Id=".toLowerCase()).append(URLEncoder.encode(userId, "UTF-8"));
        StringBuilder query =  new StringBuilder();

        ArrayList<String> strings= new ArrayList<>();
        HashMap<String,String> map = new HashMap<>();
        for (int i = 0; i < request.getUrlParams().size(); i++) {
            strings.add(request.getUrlParams().get(i).getName());
            map.put(request.getUrlParams().get(i).getName(), URLEncoder.encode(request.getUrlParams().get(i).getValue()));
        }
        Object[] names = strings.toArray();
        Arrays.sort(names);
        for (int i = 0; i < names.length; i++) {
            query.append(names[i])
                    .append("=")
                    .append(map.get(names[i].toString()));
            if (i<request.getUrlParams().size()-1){
                query.append("&");
            }
        }
        String body_str = new String(new org.apache.commons.codec.binary.Base64().encode(body.getBytes("UTF-8")));

        StringBuilder signatureMessage = new StringBuilder();
        signatureMessage.append("URI=" + path);
        if (!TextUtils.isEmpty(header)){
            signatureMessage.append(",Headers="+header);
        }
        if (!TextUtils.isEmpty(query)){
            signatureMessage.append(",QueryString="+query);
        }
        if (!TextUtils.isEmpty(body)){
            signatureMessage.append(",Body="+body_str);
        }
        String signature = "";
        if (key!=null){
            signature = encryptHMAC(signatureMessage.toString(), key.getKey());

            System.out.println("key str = "+ key.getKey());
            System.out.println("path str = " + path);
            System.out.println("header str = " + header.toString());
            System.out.println("query str = " + query.toString());
            System.out.println("body str = "+ body_str);
            System.out.println("SignatureMessage str = " + signatureMessage);
            System.out.println("Signature = " + signature);
        }

        return signature;
    }

}