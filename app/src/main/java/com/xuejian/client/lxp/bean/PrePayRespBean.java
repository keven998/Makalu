package com.xuejian.client.lxp.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yibiao.qin on 2015/12/15.
 */
public class PrePayRespBean {

    /**
     * package : Sign=WXPay
     * timestamp : 1450410597
     * prepayid : wx2015121811495793d188c59e0177890074
     * sign : 00EEE9D571327224949A6D91B0CA9075
     * appid : wx86048e56adaf7486
     * noncestr : 5e0a29ea1c674d1094c7c5adc2eb4043
     * partnerid : 1278401701
     */

    @SerializedName("package")
    private String packageX;
    private String timestamp;
    private String prepayid;
    private String sign;
    private String appid;
    private String noncestr;
    private String partnerid;
    private String requestString;

    public String getRequestString() {
        return requestString;
    }

    public void setRequestString(String requestString) {
        this.requestString = requestString;
    }

    public void setPackageX(String packageX) {
        this.packageX = packageX;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPackageX() {
        return packageX;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public String getSign() {
        return sign;
    }

    public String getAppid() {
        return appid;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public String getPartnerid() {
        return partnerid;
    }
}
