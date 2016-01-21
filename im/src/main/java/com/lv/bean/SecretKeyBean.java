package com.lv.bean;

/**
 * Created by yibiao.qin on 2016/1/11.
 */
public class SecretKeyBean {

    /**
     * key : ORcj29+6DuPwEQHeiWi4HgaQFMw=
     * timestamp : 1452495873463
     * expire : null
     */

    private String key;
    private long timestamp;
    private long expire;

    public void setKey(String key) {
        this.key = key;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public String getKey() {
        return key;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getExpire() {
        return expire;
    }
}
