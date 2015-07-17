package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/11/22.
 */
public class CommentBean implements Parcelable {
    public String userId;
    public String authorAvatar;
    public String authorName;
    public float rating;
    public String contents;
    public long publishTime;
    public List<ImageBean> images = new ArrayList<ImageBean>();


    public float getRating() {
        if (rating > 1) {
            return rating;
        }
        return rating * 5;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.authorAvatar);
        dest.writeString(this.authorName);
        dest.writeFloat(this.rating);
        dest.writeString(this.contents);
        dest.writeLong(this.publishTime);
        dest.writeTypedList(images);
    }

    public CommentBean() {
    }

    private CommentBean(Parcel in) {
        this.userId = in.readString();
        this.authorAvatar = in.readString();
        this.authorName = in.readString();
        this.rating = in.readFloat();
        this.contents = in.readString();
        this.publishTime = in.readLong();
        in.readTypedList(images, ImageBean.CREATOR);
    }

    public static final Creator<CommentBean> CREATOR = new Creator<CommentBean>() {
        public CommentBean createFromParcel(Parcel source) {
            return new CommentBean(source);
        }

        public CommentBean[] newArray(int size) {
            return new CommentBean[size];
        }
    };
}
