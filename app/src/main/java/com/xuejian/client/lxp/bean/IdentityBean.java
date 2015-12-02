package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2015/12/2.
 */
public class IdentityBean implements Parcelable {
    private String number;
    private String idType;

    public void setNumber(String number) {
        this.number = number;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getNumber() {
        return number;
    }

    public String getIdType() {
        return idType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.number);
        dest.writeString(this.idType);
    }

    public IdentityBean() {
    }

    protected IdentityBean(Parcel in) {
        this.number = in.readString();
        this.idType = in.readString();
    }

    public static final Parcelable.Creator<IdentityBean> CREATOR = new Parcelable.Creator<IdentityBean>() {
        public IdentityBean createFromParcel(Parcel source) {
            return new IdentityBean(source);
        }

        public IdentityBean[] newArray(int size) {
            return new IdentityBean[size];
        }
    };
}
