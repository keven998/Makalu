package com.aizou.peachtravel.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Rjm on 2014/11/14.
 */
public class LocBean implements Parcelable {
    public String header;
    public String id;
    public String zhName;
    public String enName;
    public double lat;
    public double lng;
    public String desc;
    public float timeCost;
    public String travelMonth;
    public ArrayList<ImageBean> images =new ArrayList<ImageBean>();
    public int imageCount;
    public ArrayList<TravelNoteBean> travelNote = new ArrayList<TravelNoteBean>();


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.zhName);
        dest.writeString(this.enName);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeString(this.desc);
        dest.writeFloat(this.timeCost);
        dest.writeString(this.travelMonth);
        dest.writeSerializable(this.images);
        dest.writeInt(this.imageCount);
        dest.writeSerializable(this.travelNote);
    }

    public LocBean() {
    }

    private LocBean(Parcel in) {
        this.id = in.readString();
        this.zhName = in.readString();
        this.enName = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.desc = in.readString();
        this.timeCost = in.readFloat();
        this.travelMonth = in.readString();
        this.images = (ArrayList<ImageBean>) in.readSerializable();
        this.imageCount = in.readInt();
        this.travelNote = (ArrayList<TravelNoteBean>) in.readSerializable();
    }

    public static final Parcelable.Creator<LocBean> CREATOR = new Parcelable.Creator<LocBean>() {
        public LocBean createFromParcel(Parcel source) {
            return new LocBean(source);
        }

        public LocBean[] newArray(int size) {
            return new LocBean[size];
        }
    };
}
