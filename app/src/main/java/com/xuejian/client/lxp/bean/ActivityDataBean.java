package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2016/1/5.
 */
public class ActivityDataBean implements Parcelable {

    public String userId;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
    }

    public ActivityDataBean() {
    }

    protected ActivityDataBean(Parcel in) {
        this.userId = in.readString();
    }

    public static final Parcelable.Creator<ActivityDataBean> CREATOR = new Parcelable.Creator<ActivityDataBean>() {
        public ActivityDataBean createFromParcel(Parcel source) {
            return new ActivityDataBean(source);
        }

        public ActivityDataBean[] newArray(int size) {
            return new ActivityDataBean[size];
        }
    };
}
