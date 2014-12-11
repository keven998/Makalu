package com.aizou.peachtravel.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/11/22.
 */
public class CommentBean implements Parcelable {
    public String userId;
    public String userAvatar;
    public String userName;
    public float rating;
    public String contents;
    public long cTime;
    public List<ImageBean> images=new ArrayList<ImageBean>();


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.userAvatar);
        dest.writeString(this.userName);
        dest.writeFloat(this.rating);
        dest.writeString(this.contents);
        dest.writeLong(this.cTime);
        dest.writeTypedList(images);
    }

    public CommentBean() {
    }

    private CommentBean(Parcel in) {
        this.userId = in.readString();
        this.userAvatar = in.readString();
        this.userName = in.readString();
        this.rating = in.readFloat();
        this.contents = in.readString();
        this.cTime = in.readLong();
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
