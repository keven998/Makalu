package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by yibiao.qin on 2015/9/9.
 */
public class ExpertInfo implements Parcelable {

    public String profile;
    public ArrayList<String> zone;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public ArrayList<String> getZone() {
        return zone;
    }

    public void setZone(ArrayList<String> zone) {
        this.zone = zone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.profile);
        dest.writeStringList(this.zone);
    }

    public ExpertInfo() {
    }

    protected ExpertInfo(Parcel in) {
        this.profile = in.readString();
        this.zone = in.createStringArrayList();
    }

    public static final Parcelable.Creator<ExpertInfo> CREATOR = new Parcelable.Creator<ExpertInfo>() {
        public ExpertInfo createFromParcel(Parcel source) {
            return new ExpertInfo(source);
        }

        public ExpertInfo[] newArray(int size) {
            return new ExpertInfo[size];
        }
    };
}
