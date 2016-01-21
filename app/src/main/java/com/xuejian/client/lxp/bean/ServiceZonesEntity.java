package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2016/1/15.
 */
public class ServiceZonesEntity implements Parcelable {
    private String id;
    private String zhName;
    private String enName;

    public void setId(String id) {
        this.id = id;
    }

    public void setZhName(String zhName) {
        this.zhName = zhName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getId() {
        return id;
    }

    public String getZhName() {
        return zhName;
    }

    public String getEnName() {
        return enName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.zhName);
        dest.writeString(this.enName);
    }

    public ServiceZonesEntity() {
    }

    protected ServiceZonesEntity(Parcel in) {
        this.id = in.readString();
        this.zhName = in.readString();
        this.enName = in.readString();
    }

    public static final Parcelable.Creator<ServiceZonesEntity> CREATOR = new Parcelable.Creator<ServiceZonesEntity>() {
        public ServiceZonesEntity createFromParcel(Parcel source) {
            return new ServiceZonesEntity(source);
        }

        public ServiceZonesEntity[] newArray(int size) {
            return new ServiceZonesEntity[size];
        }
    };
}
