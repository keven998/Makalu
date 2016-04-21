package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2016/4/14.
 */
public class BountyItemBean implements Parcelable {

    /**
     * itemId : 1459418291170
     * desc : haha
     * price : 1000
     * seller : [{"sellerId":211568,"name":"青海容途旅游公司曹茜"}]
     * createTime : 1459418291170
     * updateTime : 1459418291170
     */

    private long itemId;
    private String desc;
    private double price;
    public long bountyId;
    public String status;
    private long createTime;
    private long updateTime;
    /**
     * sellerId : 211568
     * name : 青海容途旅游公司曹茜
     */

    public SellerBean seller;

    public StrategyBean guide;

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public SellerBean getSeller() {
        return seller;
    }

    public void setSeller(SellerBean seller) {
        this.seller = seller;
    }

    public BountyItemBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.itemId);
        dest.writeString(this.desc);
        dest.writeDouble(this.price);
        dest.writeLong(this.bountyId);
        dest.writeString(this.status);
        dest.writeLong(this.createTime);
        dest.writeLong(this.updateTime);
        dest.writeParcelable(this.seller, flags);
        dest.writeParcelable(this.guide, flags);
    }

    protected BountyItemBean(Parcel in) {
        this.itemId = in.readLong();
        this.desc = in.readString();
        this.price = in.readDouble();
        this.bountyId = in.readLong();
        this.status = in.readString();
        this.createTime = in.readLong();
        this.updateTime = in.readLong();
        this.seller = in.readParcelable(SellerBean.class.getClassLoader());
        this.guide = in.readParcelable(StrategyBean.class.getClassLoader());
    }

    public static final Creator<BountyItemBean> CREATOR = new Creator<BountyItemBean>() {
        @Override
        public BountyItemBean createFromParcel(Parcel source) {
            return new BountyItemBean(source);
        }

        @Override
        public BountyItemBean[] newArray(int size) {
            return new BountyItemBean[size];
        }
    };
}
