package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2015/12/3.
 */
public class localityBean implements Parcelable {
    /**
     * zhName : 普吉岛
     * enName :
     */

    private String zhName;
    private String enName;

    public void setZhName(String zhName) {
        this.zhName = zhName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
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
        dest.writeString(this.zhName);
        dest.writeString(this.enName);
    }

    public localityBean() {
    }

    protected localityBean(Parcel in) {
        this.zhName = in.readString();
        this.enName = in.readString();
    }

    public static final Parcelable.Creator<localityBean> CREATOR = new Parcelable.Creator<localityBean>() {
        public localityBean createFromParcel(Parcel source) {
            return new localityBean(source);
        }

        public localityBean[] newArray(int size) {
            return new localityBean[size];
        }
    };
}
