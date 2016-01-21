package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2015/12/1.
 */
public class TelBean implements Parcelable {
    private int dialCode;
    private long number;

    public void setDialCode(int dialCode) {
        this.dialCode = dialCode;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public int getDialCode() {
        return dialCode;
    }

    public long getNumber() {
        return number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.dialCode);
        dest.writeLong(this.number);
    }

    public TelBean() {
    }

    protected TelBean(Parcel in) {
        this.dialCode = in.readInt();
        this.number = in.readLong();
    }

    public static final Parcelable.Creator<TelBean> CREATOR = new Parcelable.Creator<TelBean>() {
        public TelBean createFromParcel(Parcel source) {
            return new TelBean(source);
        }

        public TelBean[] newArray(int size) {
            return new TelBean[size];
        }
    };
}
