package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2015/12/1.
 */
public class CoverBean implements Parcelable {
    private String url;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
    }

    public CoverBean() {
    }

    protected CoverBean(Parcel in) {
        this.url = in.readString();
    }

    public static final Parcelable.Creator<CoverBean> CREATOR = new Parcelable.Creator<CoverBean>() {
        public CoverBean createFromParcel(Parcel source) {
            return new CoverBean(source);
        }

        public CoverBean[] newArray(int size) {
            return new CoverBean[size];
        }
    };
}
