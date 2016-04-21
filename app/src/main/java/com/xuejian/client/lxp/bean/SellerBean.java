package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by yibiao.qin on 2015/11/23.
 */
public class SellerBean implements Parcelable {

    /**
     * sellerId : 100012
     * lang : []
     * serviceZones : []
     * bankAccounts : []
     * name : 天意小馆
     * email : []
     * phone : []
     * favorCnt : 0
     */

    private long sellerId;
    private String name;
    private int favorCnt;
    public Consumer user;
    private List<ServiceZonesEntity> serviceZones;
    private List<String> bankAccounts;
    private List<String> email;
    private List<String> phone;

    public List<String> lang;
    public List<String> qualifications;
    public List<String> services;
    public void setSellerId(long sellerId) {
        this.sellerId = sellerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFavorCnt(int favorCnt) {
        this.favorCnt = favorCnt;
    }

    public void setLang(List<String> lang) {
        this.lang = lang;
    }

    public void setServiceZones(List<ServiceZonesEntity> serviceZones) {
        this.serviceZones = serviceZones;
    }

    public void setBankAccounts(List<String> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public void setPhone(List<String> phone) {
        this.phone = phone;
    }

    public long getSellerId() {
        return sellerId;
    }

    public String getName() {
        return name;
    }

    public int getFavorCnt() {
        return favorCnt;
    }

    public List<String> getLang() {
        return lang;
    }

    public List<ServiceZonesEntity> getServiceZones() {
        return serviceZones;
    }

    public List<String> getBankAccounts() {
        return bankAccounts;
    }

    public List<String> getEmail() {
        return email;
    }

    public List<String> getPhone() {
        return phone;
    }

    public SellerBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.sellerId);
        dest.writeString(this.name);
        dest.writeInt(this.favorCnt);
        dest.writeParcelable(this.user, flags);
        dest.writeTypedList(serviceZones);
        dest.writeStringList(this.bankAccounts);
        dest.writeStringList(this.email);
        dest.writeStringList(this.phone);
        dest.writeStringList(this.lang);
        dest.writeStringList(this.qualifications);
        dest.writeStringList(this.services);
    }

    protected SellerBean(Parcel in) {
        this.sellerId = in.readLong();
        this.name = in.readString();
        this.favorCnt = in.readInt();
        this.user = in.readParcelable(Consumer.class.getClassLoader());
        this.serviceZones = in.createTypedArrayList(ServiceZonesEntity.CREATOR);
        this.bankAccounts = in.createStringArrayList();
        this.email = in.createStringArrayList();
        this.phone = in.createStringArrayList();
        this.lang = in.createStringArrayList();
        this.qualifications = in.createStringArrayList();
        this.services = in.createStringArrayList();
    }

    public static final Creator<SellerBean> CREATOR = new Creator<SellerBean>() {
        @Override
        public SellerBean createFromParcel(Parcel source) {
            return new SellerBean(source);
        }

        @Override
        public SellerBean[] newArray(int size) {
            return new SellerBean[size];
        }
    };
}
