package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2016/2/20.
 */
public class CouponBean implements Parcelable {
    /**
     * id : 56c6de533d7ed10e58a1b82d
     * userId : 100003
     * desc : 清明节优惠卷
     * discount : 0.01
     * available : true
     * expire : 2016-02-19
     * threshold : 100
     */

    public String title;
    private String id;
    private long userId;
    private String desc;
    private double discount;
    private boolean available;
    private String expire;
    private double threshold;

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public String getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getDesc() {
        return desc;
    }

    public double getDiscount() {
        return discount;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getExpire() {
        return expire;
    }

    public double getThreshold() {
        return threshold;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeLong(this.userId);
        dest.writeString(this.desc);
        dest.writeDouble(this.discount);
        dest.writeByte(available ? (byte) 1 : (byte) 0);
        dest.writeString(this.expire);
        dest.writeDouble(this.threshold);
        dest.writeString(this.title);
    }

    public CouponBean() {
    }

    protected CouponBean(Parcel in) {
        this.id = in.readString();
        this.userId = in.readLong();
        this.desc = in.readString();
        this.discount = in.readDouble();
        this.available = in.readByte() != 0;
        this.expire = in.readString();
        this.threshold = in.readDouble();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<CouponBean> CREATOR = new Parcelable.Creator<CouponBean>() {
        public CouponBean createFromParcel(Parcel source) {
            return new CouponBean(source);
        }

        public CouponBean[] newArray(int size) {
            return new CouponBean[size];
        }
    };
}
