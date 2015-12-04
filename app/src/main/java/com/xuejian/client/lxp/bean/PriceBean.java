package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2015/12/4.
 */
public class PriceBean implements Parcelable {

    public String date;
    public int price;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeInt(this.price);
    }

    public PriceBean() {
    }

    protected PriceBean(Parcel in) {
        this.date = in.readString();
        this.price = in.readInt();
    }

    public static final Parcelable.Creator<PriceBean> CREATOR = new Parcelable.Creator<PriceBean>() {
        public PriceBean createFromParcel(Parcel source) {
            return new PriceBean(source);
        }

        public PriceBean[] newArray(int size) {
            return new PriceBean[size];
        }
    };

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
