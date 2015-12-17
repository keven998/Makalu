package com.xuejian.client.lxp.bean;

/**
 * Created by yibiao.qin on 2015/12/15.
 */
public class WeixinRespBean {

    /**
     * result : SUCCESS
     * nonce_str : Qnn1hDWF7RvTcgRt
     * prepay_id : wx20151215173952ce063788720568598723
     * trade_type : APP
     * sign : 485404066A96E03D33771038C6BFE33D
     * mch_id : 1278401701
     */

    private String result;
    private String nonce_str;
    private String prepay_id;
    private String trade_type;
    private String sign;
    private String mch_id;
    private String timeStamp;


    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
    public void setResult(String result) {
        this.result = result;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public void setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getResult() {
        return result;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public String getPrepay_id() {
        return prepay_id;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public String getSign() {
        return sign;
    }

    public String getMch_id() {
        return mch_id;
    }

    @Override
    public String toString() {
        return "result "+result+" nonce_str "+nonce_str+" prepay_id "+prepay_id+" trade_type "+trade_type+" sign "+sign+" mch_id "+mch_id;
    }
}
