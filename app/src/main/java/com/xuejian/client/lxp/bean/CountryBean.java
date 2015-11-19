package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Rjm on 2014/12/3.
 */
public class CountryBean implements Parcelable {
    public String id;
    public String code;
    public String zhName;
    public String enName;
    public String desc;
    public ArrayList<ImageBean> images = new ArrayList<ImageBean>();
    public ArrayList<LocBean> destinations = new ArrayList<LocBean>();
    public boolean isOpened;
    public boolean isSelect;
    public int rank;
    public int commodityCnt;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.code);
        dest.writeString(this.zhName);
        dest.writeString(this.enName);
        dest.writeString(this.desc);
        dest.writeSerializable(this.images);
        dest.writeSerializable(this.destinations);
        dest.writeInt(this.rank);
        dest.writeInt(this.commodityCnt);
    }

    public CountryBean() {
    }

    private CountryBean(Parcel in) {
        this.id = in.readString();
        this.code = in.readString();
        this.zhName = in.readString();
        this.enName = in.readString();
        this.desc = in.readString();
        this.images = (ArrayList<ImageBean>) in.readSerializable();
        this.destinations = (ArrayList<LocBean>) in.readSerializable();
        this.rank = in.readInt();
        this.commodityCnt = in.readInt();
    }

    public static final Creator<CountryBean> CREATOR = new Creator<CountryBean>() {
        public CountryBean createFromParcel(Parcel source) {
            return new CountryBean(source);
        }

        public CountryBean[] newArray(int size) {
            return new CountryBean[size];
        }
    };
}
