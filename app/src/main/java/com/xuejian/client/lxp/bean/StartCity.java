package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class StartCity implements Parcelable {
    public String name;
    public String id;
    public double lat;
    public double lng;
    public String bdId;
    public String pinyin;
    public ArrayList<StartCity> childs = new ArrayList<StartCity>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.id);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeString(this.bdId);
        dest.writeString(this.pinyin);
        dest.writeList(this.childs);
    }

    public StartCity() {
    }

    protected StartCity(Parcel in) {
        this.name = in.readString();
        this.id = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.bdId = in.readString();
        this.pinyin = in.readString();
        this.childs = new ArrayList<StartCity>();
        in.readList(this.childs, StartCity.class.getClassLoader());
    }

    public static final Parcelable.Creator<StartCity> CREATOR = new Parcelable.Creator<StartCity>() {
        @Override
        public StartCity createFromParcel(Parcel source) {
            return new StartCity(source);
        }

        @Override
        public StartCity[] newArray(int size) {
            return new StartCity[size];
        }
    };
}
