package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yibiao.qin on 2016/1/15.
 */
public class StoreBean implements Parcelable {
    /**
     * id : dc8a2050e7b9d7166129d42f
     * sellerId : 210067
     * name : 会会的小店^__^
     * desc : {"title":"","summary":"","body":"<p>fdsafdsafasfsafdsafdsafdsafsdaffdsafdsaf<\/p>"}
     * lang : ["zh"]
     * qualifications : ["24小时响应","认证商家","如实描述"]
     * services : ["语言帮助","行程规划","当地咨询"]
     * serviceZones : [{"id":"5434d70d10114e684bb1b4e9","zhName":"日本","enName":""},{"id":"5434d70e10114e684bb1b4ee","zhName":"中国","enName":""},{"id":"5434d6c710114e684bb1b43a","zhName":"阿尔巴尼亚","enName":""},{"id":"5434d70d10114e684bb1b4e6","zhName":"柬埔寨","enName":""},{"id":"5434d70e10114e684bb1b4ea","zhName":"泰国","enName":""},{"id":"5434d70d10114e684bb1b4e5","zhName":"韩国","enName":""},{"id":"546f2da7b8ce0440eddb2855","zhName":"福冈","enName":""},{"id":"546f2dadb8ce0440eddb2dbd","zhName":"思茅","enName":""},{"id":"54782a57b8ce042b8e26df45","zhName":"发罗拉","enName":""},{"id":"546f2da8b8ce0440eddb28e0","zhName":"东京","enName":""},{"id":"54782a56b8ce042b8e26de0f","zhName":"贡布","enName":""},{"id":"546f2da8b8ce0440eddb28cc","zhName":"普吉岛","enName":""},{"id":"546f2da8b8ce0440eddb2870","zhName":"济州岛","enName":""}]
     * address :
     * favorCnt : 0
     * rating : 0
     * cover : {}
     * images : []
     */

    private String id;
    private int sellerId;
    private String name;
    /**
     * title :
     * summary :
     * body : <p>fdsafdsafasfsafdsafdsafdsafsdaffdsafdsaf</p>
     */

    private DescEntity desc;
    private String address;
    private int favorCnt;
    private int rating;
    private List<String> lang;
    private List<String> qualifications;
    private List<String> services;
    /**
     * id : 5434d70d10114e684bb1b4e9
     * zhName : 日本
     * enName :
     */

    private List<ServiceZonesEntity> serviceZones;
    private List<String> images;

    public void setId(String id) {
        this.id = id;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(DescEntity desc) {
        this.desc = desc;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFavorCnt(int favorCnt) {
        this.favorCnt = favorCnt;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setLang(List<String> lang) {
        this.lang = lang;
    }

    public void setQualifications(List<String> qualifications) {
        this.qualifications = qualifications;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public void setServiceZones(List<ServiceZonesEntity> serviceZones) {
        this.serviceZones = serviceZones;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public int getSellerId() {
        return sellerId;
    }

    public String getName() {
        return name;
    }

    public DescEntity getDesc() {
        return desc;
    }

    public String getAddress() {
        return address;
    }

    public int getFavorCnt() {
        return favorCnt;
    }

    public int getRating() {
        return rating;
    }

    public List<String> getLang() {
        return lang;
    }

    public List<String> getQualifications() {
        return qualifications;
    }

    public List<String> getServices() {
        return services;
    }

    public List<ServiceZonesEntity> getServiceZones() {
        return serviceZones;
    }

    public List<?> getImages() {
        return images;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.sellerId);
        dest.writeString(this.name);
        dest.writeParcelable(this.desc, 0);
        dest.writeString(this.address);
        dest.writeInt(this.favorCnt);
        dest.writeInt(this.rating);
        dest.writeStringList(this.lang);
        dest.writeStringList(this.qualifications);
        dest.writeStringList(this.services);
        dest.writeTypedList(serviceZones);
        dest.writeStringList(this.images);
    }

    public StoreBean() {
    }

    protected StoreBean(Parcel in) {
        this.id = in.readString();
        this.sellerId = in.readInt();
        this.name = in.readString();
        this.desc = in.readParcelable(DescEntity.class.getClassLoader());
        this.address = in.readString();
        this.favorCnt = in.readInt();
        this.rating = in.readInt();
        this.lang = in.createStringArrayList();
        this.qualifications = in.createStringArrayList();
        this.services = in.createStringArrayList();
        this.serviceZones = in.createTypedArrayList(ServiceZonesEntity.CREATOR);
        this.images = new ArrayList<String>();
        in.readList(this.images, List.class.getClassLoader());
    }

    public static final Parcelable.Creator<StoreBean> CREATOR = new Parcelable.Creator<StoreBean>() {
        public StoreBean createFromParcel(Parcel source) {
            return new StoreBean(source);
        }

        public StoreBean[] newArray(int size) {
            return new StoreBean[size];
        }
    };
}
