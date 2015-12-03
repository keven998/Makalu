package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yibiao.qin on 2015/12/3.
 */
public class PricingEntity implements Parcelable {
    private int price;
    private List<Long> timeRange;

    public void setPrice(int price) {
        this.price = price;
    }

    public void setTimeRange(List<Long> timeRange) {
        this.timeRange = timeRange;
    }

    public int getPrice() {
        return price;
    }

    public List<Long> getTimeRange() {
        return timeRange;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.price);
        dest.writeList(this.timeRange);
    }

    public PricingEntity() {
    }

    protected PricingEntity(Parcel in) {
        this.price = in.readInt();
        this.timeRange = new ArrayList<Long>();
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