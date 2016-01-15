package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yibiao.qin on 2015/12/3.
 */
public class PricingEntity implements Parcelable {
    private double price;
    private List<String> timeRange;

    public void setPrice(int price) {
        this.price = price;
    }

    public void setTimeRange(List<String> timeRange) {
        this.timeRange = timeRange;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getTimeRange() {
        return timeRange;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.price);
        dest.writeList(this.timeRange);
    }

    public PricingEntity() {
    }

    protected PricingEntity(Parcel in) {
        this.price = in.readDouble();
        this.timeRange = new ArrayList<String>();
        in.readList(this.timeRange, List.class.getClassLoader());
    }

    public static final Parcelable.Creator<PricingEntity> CREATOR = new Parcelable.Creator<PricingEntity>() {
        public PricingEntity createFromParcel(Parcel source) {
            return new PricingEntity(source);
        }

        public PricingEntity[] newArray(int size) {
            return new PricingEntity[size];
        }
    };
}