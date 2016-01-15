package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2015/12/8.
 */
public class ShareCommodityBean implements Parcelable {

    public long commodityId;
    public String title;
    public double price;
    public String image;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.commodityId);
        dest.writeString(this.title);
        dest.writeDouble(this.price);
        dest.writeString(this.image);
    }

    public ShareCommodityBean() {
    }

    protected ShareCommodityBean(Parcel in) {
        this.commodityId = in.readLong();
        this.title = in.readString();
        this.price = in.readDouble();
        this.image = in.readString();
    }

    public static final Parcelable.Creator<ShareCommodityBean> CREATOR = new Parcelable.Creator<ShareCommodityBean>() {
        public ShareCommodityBean createFromParcel(Parcel source) {
            return new ShareCommodityBean(source);
        }

        public ShareCommodityBean[] newArray(int size) {
            return new ShareCommodityBean[size];
        }
    };
}
