package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2016/1/5.
 */
public class TradeActivityBean implements Parcelable {

    /**
     * action : create
     * prevStatus :
     * data : {"userId":"100004"}
     * timestamp : 1451962463546
     */

    public String action;
    public String prevStatus;
    /**
     * userId : 100004
     */

    public ActivityDataBean data;
    public long timestamp;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.action);
        dest.writeString(this.prevStatus);
        dest.writeParcelable(this.data, 0);
        dest.writeLong(this.timestamp);
    }

    public TradeActivityBean() {
    }

    protected TradeActivityBean(Parcel in) {
        this.action = in.readString();
        this.prevStatus = in.readString();
        this.data = in.readParcelable(ActivityDataBean.class.getClassLoader());
        this.timestamp = in.readLong();
    }

    public static final Parcelable.Creator<TradeActivityBean> CREATOR = new Parcelable.Creator<TradeActivityBean>() {
        public TradeActivityBean createFromParcel(Parcel source) {
            return new TradeActivityBean(source);
        }

        public TradeActivityBean[] newArray(int size) {
            return new TradeActivityBean[size];
        }
    };
}
