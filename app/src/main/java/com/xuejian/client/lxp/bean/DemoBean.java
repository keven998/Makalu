package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2016/6/15.
 */
public class DemoBean implements Parcelable {
    /**
     * dayIndex : 0
     * desc : haha
     */

    public int dayIndex;
    public String desc;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.dayIndex);
        dest.writeString(this.desc);
    }

    public DemoBean() {
    }

    protected DemoBean(Parcel in) {
        this.dayIndex = in.readInt();
        this.desc = in.readString();
    }

    public static final Parcelable.Creator<DemoBean> CREATOR = new Parcelable.Creator<DemoBean>() {
        @Override
        public DemoBean createFromParcel(Parcel source) {
            return new DemoBean(source);
        }

        @Override
        public DemoBean[] newArray(int size) {
            return new DemoBean[size];
        }
    };
}
