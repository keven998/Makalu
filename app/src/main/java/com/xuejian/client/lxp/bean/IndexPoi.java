package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2016/6/20.
 */
public class IndexPoi implements Parcelable {
    public int dayIndex;
    public PoiDetailBean poi;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.dayIndex);
        dest.writeParcelable(this.poi, 0);
    }

    public IndexPoi() {
    }

    private IndexPoi(Parcel in) {
        this.dayIndex = in.readInt();
        this.poi = in.readParcelable(PoiDetailBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<IndexPoi> CREATOR = new Parcelable.Creator<IndexPoi>() {
        public IndexPoi createFromParcel(Parcel source) {
            return new IndexPoi(source);
        }

        public IndexPoi[] newArray(int size) {
            return new IndexPoi[size];
        }
    };
}
