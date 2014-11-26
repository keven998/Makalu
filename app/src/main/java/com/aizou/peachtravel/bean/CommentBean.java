package com.aizou.peachtravel.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rjm on 2014/11/22.
 */
public class CommentBean implements Parcelable {
    public String avatar;
    public String nickName;
    public float rating;
    public String commentDetails;
    public String commentTime;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatar);
        dest.writeString(this.nickName);
        dest.writeFloat(this.rating);
        dest.writeString(this.commentDetails);
        dest.writeString(this.commentTime);
    }

    public CommentBean() {
    }

    private CommentBean(Parcel in) {
        this.avatar = in.readString();
        this.nickName = in.readString();
        this.rating = in.readFloat();
        this.commentDetails = in.readString();
        this.commentTime = in.readString();
    }

    public static final Parcelable.Creator<CommentBean> CREATOR = new Parcelable.Creator<CommentBean>() {
        public CommentBean createFromParcel(Parcel source) {
            return new CommentBean(source);
        }

        public CommentBean[] newArray(int size) {
            return new CommentBean[size];
        }
    };
}
