package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

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
    private long createTime;
    private long updateTime;
    /**
     * sellerId : 211568
     * name : 青海容途旅游公司曹茜
     */

    private List<SellerBean> seller;

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

    public List<SellerBean> getSeller() {
        return seller;
    }

    public void setSeller(List<SellerBean> seller) {
        this.seller = seller;
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
        dest.writeLong(this.createTime);
        dest.writeLong(this.updateTime);
        dest.writeTypedList(seller);
    }

    public BountyItemBean() {
    }

    protected BountyItemBean(Parcel in) {
        this.itemId = in.readLong();
        this.desc = in.readString();
        this.price = in.readDouble();
        this.createTime = in.readLong();
        this.updateTime = in.readLong();
        this.seller = in.createTypedArrayList(SellerBean.CREATOR);
    }

    public static final Parcelable.Creator<BountyItemBean> CREATOR = new Parcelable.Creator<BountyItemBean>() {
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
