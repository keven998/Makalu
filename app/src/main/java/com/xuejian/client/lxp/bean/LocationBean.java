package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rjm on 2014/11/22.
 */
public class LocationBean implements Parcelable {
    //{lng, lat}
    public double[] coordinates;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDoubleArray(this.coordinates);
    }

    public LocationBean() {
    }

    private LocationBean(Parcel in) {
        this.coordinates = in.createDoubleArray();
    }

    public static final Creator<LocationBean> CREATOR = new Creator<LocationBean>() {
        public LocationBean createFromParcel(Parcel source) {
            return new LocationBean(source);
        }

        public LocationBean[] newArray(int size) {
            return new LocationBean[size];
        }
    };
}
