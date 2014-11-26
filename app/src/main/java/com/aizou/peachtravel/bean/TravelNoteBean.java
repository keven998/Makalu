package com.aizou.peachtravel.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rjm on 2014/11/14.
 */
public class TravelNoteBean implements Parcelable {
    public String _id;
    public String title;
    public String desc;
    public String cover;
    public String authorName;
    public String authorAvatar;
    public String source;
    public String sourceUrl;
    public String publishDate;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.title);
        dest.writeString(this.desc);
        dest.writeString(this.cover);
        dest.writeString(this.authorName);
        dest.writeString(this.authorAvatar);
        dest.writeString(this.source);
        dest.writeString(this.sourceUrl);
        dest.writeString(this.publishDate);
    }

    public TravelNoteBean() {
    }

    private TravelNoteBean(Parcel in) {
        this._id = in.readString();
        this.title = in.readString();
        this.desc = in.readString();
        this.cover = in.readString();
        this.authorName = in.readString();
        this.authorAvatar = in.readString();
        this.source = in.readString();
        this.sourceUrl = in.readString();
        this.publishDate = in.readString();
    }

    public static final Parcelable.Creator<TravelNoteBean> CREATOR = new Parcelable.Creator<TravelNoteBean>() {
        public TravelNoteBean createFromParcel(Parcel source) {
            return new TravelNoteBean(source);
        }

        public TravelNoteBean[] newArray(int size) {
            return new TravelNoteBean[size];
        }
    };
}
