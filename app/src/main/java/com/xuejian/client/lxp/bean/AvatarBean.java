package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2016/4/20.
 */
public class AvatarBean implements Parcelable {
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
    }

    public AvatarBean() {
    }

    protected AvatarBean(Parcel in) {
        this.url = in.readString();
    }

    public static final Parcelable.Creator<AvatarBean> CREATOR = new Parcelable.Creator<AvatarBean>() {
        @Override
        public AvatarBean createFromParcel(Parcel source) {
            return new AvatarBean(source);
        }

        @Override
        public AvatarBean[] newArray(int size) {
            return new AvatarBean[size];
        }
    };
}
