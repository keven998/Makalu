package com.aizou.peachtravel.bean;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rjm on 2014/11/17.
 */
public class ImageBean implements Parcelable {
    public String url;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
    }

    public ImageBean() {
    }

    private ImageBean(Parcel in) {
        this.url = in.readString();
    }

    public static final Parcelable.Creator<ImageBean> CREATOR = new Parcelable.Creator<ImageBean>() {
        public ImageBean createFromParcel(Parcel source) {
            return new ImageBean(source);
        }

        public ImageBean[] newArray(int size) {
            return new ImageBean[size];
        }
    };
}
