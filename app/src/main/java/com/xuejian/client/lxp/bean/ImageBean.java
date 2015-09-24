package com.xuejian.client.lxp.bean;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rjm on 2014/11/17.
 */
public class ImageBean implements Parcelable {

    public String url;
    public String originUrl;
    public String caption;
    public String thumb;
    public String full;
    public ImageBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.originUrl);
        dest.writeString(this.caption);
        dest.writeString(this.thumb);
        dest.writeString(this.full);
    }

    private ImageBean(Parcel in) {
        this.url = in.readString();
        this.originUrl = in.readString();
        this.caption = in.readString();
        this.thumb = in.readString();
        this.full = in.readString();
    }

    public static final Creator<ImageBean> CREATOR = new Creator<ImageBean>() {
        public ImageBean createFromParcel(Parcel source) {
            return new ImageBean(source);
        }

        public ImageBean[] newArray(int size) {
            return new ImageBean[size];
        }
    };
}
