package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2015/12/2.
 */
public class TravellerBean implements Parcelable {
    /**
     * key : 565e026c9bdf11000149c74a
     * traveller : {"surname":"余","givenName":"鱼同","gender":"male","email":"","tel":{"dialCode":86,"number":13099887766},"identities":[{"number":"1201011999....","idType":"chineseID"}]}
     */

    private String key;
    /**
     * surname : 余
     * givenName : 鱼同
     * gender : male
     * email :
     * tel : {"dialCode":86,"number":13099887766}
     * identities : [{"number":"1201011999....","idType":"chineseID"}]
     */

    private TravellerEntity traveller;

    public void setKey(String key) {
        this.key = key;
    }

    public void setTraveller(TravellerEntity traveller) {
        this.traveller = traveller;
    }

    public String getKey() {
        return key;
    }

    public TravellerEntity getTraveller() {
        return traveller;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeParcelable(this.traveller, 0);
    }

    public TravellerBean() {
    }
    public TravellerBean(TravellerEntity entity) {
        traveller = entity;
    }
    protected TravellerBean(Parcel in) {
        this.key = in.readString();
        this.traveller = in.readParcelable(TravellerEntity.class.getClassLoader());
    }

    public static final Parcelable.Creator<TravellerBean> CREATOR = new Parcelable.Creator<TravellerBean>() {
        public TravellerBean createFromParcel(Parcel source) {
            return new TravellerBean(source);
        }

        public TravellerBean[] newArray(int size) {
            return new TravellerBean[size];
        }
    };
}
