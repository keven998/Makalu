package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2015/11/20.
 */
public class PassengerBean implements Parcelable {
    public String firstName;
    public String lastName;
    public String tel;
    public String id;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.tel);
        dest.writeString(this.id);
    }

    public PassengerBean() {
    }

    protected PassengerBean(Parcel in) {
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.tel = in.readString();
        this.id = in.readString();
    }

    public static final Parcelable.Creator<PassengerBean> CREATOR = new Parcelable.Creator<PassengerBean>() {
        public PassengerBean createFromParcel(Parcel source) {
            return new PassengerBean(source);
        }

        public PassengerBean[] newArray(int size) {
            return new PassengerBean[size];
        }
    };
}
