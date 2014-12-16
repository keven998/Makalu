package com.aizou.peachtravel.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/11/14.
 */
public class LocBean implements Parcelable {
    public boolean isAdded;
    public String header;
    public String id;
    public String zhName;
    public String enName;
    public LocationBean location;
    public String desc;
    public float timeCost;
    public String timeCostDesc;
    public String travelMonth;
    public List<ImageBean> images =new ArrayList<ImageBean>();
    public int imageCnt;
    public boolean isFavorite;


    public LocBean() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(isAdded ? (byte) 1 : (byte) 0);
        dest.writeString(this.header);
        dest.writeString(this.id);
        dest.writeString(this.zhName);
        dest.writeString(this.enName);
        dest.writeParcelable(this.location, 0);
        dest.writeString(this.desc);
        dest.writeFloat(this.timeCost);
        dest.writeString(this.timeCostDesc);
        dest.writeString(this.travelMonth);
        dest.writeTypedList(images);
        dest.writeInt(this.imageCnt);
        dest.writeByte(isFavorite ? (byte) 1 : (byte) 0);
    }

    private LocBean(Parcel in) {
        this.isAdded = in.readByte() != 0;
        this.header = in.readString();
        this.id = in.readString();
        this.zhName = in.readString();
        this.enName = in.readString();
        this.location = in.readParcelable(LocationBean.class.getClassLoader());
        this.desc = in.readString();
        this.timeCost = in.readFloat();
        this.timeCostDesc = in.readString();
        this.travelMonth = in.readString();
        in.readTypedList(images, ImageBean.CREATOR);
        this.imageCnt = in.readInt();
        this.isFavorite = in.readByte() != 0;
    }

    public static final Creator<LocBean> CREATOR = new Creator<LocBean>() {
        public LocBean createFromParcel(Parcel source) {
            return new LocBean(source);
        }

        public LocBean[] newArray(int size) {
            return new LocBean[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if(o instanceof LocBean){
            LocBean bean = (LocBean) o;
            return bean.id.equals(this.id);
        }else{
            return false;
        }

    }

    @Override
    public int hashCode() {
        if(!TextUtils.isEmpty(id)){
            return id.hashCode();
        }
        return super.hashCode();
    }
}
