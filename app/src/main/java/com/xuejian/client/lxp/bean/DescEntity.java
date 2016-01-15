package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2016/1/15.
 */
public class DescEntity implements Parcelable {
    private String title;
    private String summary;
    private String body;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getBody() {
        return body;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.summary);
        dest.writeString(this.body);
    }

    public DescEntity() {
    }

    protected DescEntity(Parcel in) {
        this.title = in.readString();
        this.summary = in.readString();
        this.body = in.readString();
    }

    public static final Parcelable.Creator<DescEntity> CREATOR = new Parcelable.Creator<DescEntity>() {
        public DescEntity createFromParcel(Parcel source) {
            return new DescEntity(source);
        }

        public DescEntity[] newArray(int size) {
            return new DescEntity[size];
        }
    };
}
