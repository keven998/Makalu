package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2016/6/15.
 */
public class TrafficBean implements Parcelable {
    /**
     * arrTime : 2016-3-05
     * category : train
     * dayIndex : 0
     * depTime : 2016-03-05
     * end : 厦门
     * start : 北京
     */

    public String arrTime;
    public String category;
    public int dayIndex;
    public String depTime;
    public String end;
    public String start;
    public String desc;

    public TrafficBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.arrTime);
        dest.writeString(this.category);
        dest.writeInt(this.dayIndex);
        dest.writeString(this.depTime);
        dest.writeString(this.end);
        dest.writeString(this.start);
        dest.writeString(this.desc);
    }

    protected TrafficBean(Parcel in) {
        this.arrTime = in.readString();
        this.category = in.readString();
        this.dayIndex = in.readInt();
        this.depTime = in.readString();
        this.end = in.readString();
        this.start = in.readString();
        this.desc = in.readString();
    }

    public static final Creator<TrafficBean> CREATOR = new Creator<TrafficBean>() {
        @Override
        public TrafficBean createFromParcel(Parcel source) {
            return new TrafficBean(source);
        }

        @Override
        public TrafficBean[] newArray(int size) {
            return new TrafficBean[size];
        }
    };
}
