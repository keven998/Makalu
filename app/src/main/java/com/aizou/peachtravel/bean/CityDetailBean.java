package com.aizou.peachtravel.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Rjm on 2014/11/14.
 */
public class CityDetailBean implements Parcelable {
    public String _id;
    public String zhName;
    public String enName;
    public double lat;
    public double lng;
    public String desc;
    public float timeCost;
    public String travelMonth;
    public String cover;
    public int imageCount;
    public List<TravelNoteBean> travelNote;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.zhName);
        dest.writeString(this.enName);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeString(this.desc);
        dest.writeFloat(this.timeCost);
        dest.writeString(this.travelMonth);
        dest.writeString(this.cover);
        dest.writeInt(this.imageCount);
        dest.writeTypedList(travelNote);
    }

    public CityDetailBean() {
    }

    private CityDetailBean(Parcel in) {
        this._id = in.readString();
        this.zhName = in.readString();
        this.enName = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.desc = in.readString();
        this.timeCost = in.readFloat();
        this.travelMonth = in.readString();
        this.cover = in.readString();
        this.imageCount = in.readInt();
        in.readTypedList(travelNote, TravelNoteBean.CREATOR);
    }

    public static final Parcelable.Creator<CityDetailBean> CREATOR = new Parcelable.Creator<CityDetailBean>() {
        public CityDetailBean createFromParcel(Parcel source) {
            return new CityDetailBean(source);
        }

        public CityDetailBean[] newArray(int size) {
            return new CityDetailBean[size];
        }
    };
}
