package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Rjm on 2014/11/22.
 */
public class RecommendBean implements Parcelable {
    public String title;
    public List<ImageBean> images;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeTypedList(images);
    }

    public RecommendBean() {
    }

    private RecommendBean(Parcel in) {
        this.title = in.readString();
        in.readTypedList(images, ImageBean.CREATOR);
    }

    public static final Creator<RecommendBean> CREATOR = new Creator<RecommendBean>() {
        public RecommendBean createFromParcel(Parcel source) {
            return new RecommendBean(source);
        }

        public RecommendBean[] newArray(int size) {
            return new RecommendBean[size];
        }
    };
}
