package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2016/1/5.
 */
public class ActivityDataBean implements Parcelable {

    public String userId;
    public String reason;
    public String memo;

    public ActivityDataBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.reason);
        dest.writeString(this.memo);
    }

    protected ActivityDataBean(Parcel in) {
        this.userId = in.readString();
        this.reason = in.readString();
        this.memo = in.readString();
    }

    public static final Creator<ActivityDataBean> CREATOR = new Creator<ActivityDataBean>() {
        public ActivityDataBean createFromParcel(Parcel source) {
            return new ActivityDataBean(source);
        }

        public ActivityDataBean[] newArray(int size) {
            return new ActivityDataBean[size];
        }
    };
}
