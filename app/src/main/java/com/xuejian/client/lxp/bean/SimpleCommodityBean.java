package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by yibiao.qin on 2015/11/26.
 */
public class SimpleCommodityBean implements Parcelable {

    /**
     * commodityId : 12477930216
     * title : 拉斯维加斯《蓝人秀Blue Man Group》
     * marketPrice : 100.0999984741211
     * price : 0.0
     * rating : 0.99
     * salesVolume : 100
     * seller : {"sellerId":100012,"name":"天意小馆","user":{"userId":100012,"nickname":"","avatar":null},"qualifications":[],"rating":0,"cover":null}
     * locality : null
     * images : [{"url":"http://7sbm17.com1.z0.glb.clouddn.com/avatar/436074aa3952b00f2e6757b4f3ae81fc"}]
     * cover : {"url":"http://7sbm17.com1.z0.glb.clouddn.com/avatar/436074aa3952b00f2e6757b4f3ae81fc"}
     */
    public String shareUrl;
    public long version;
    private long commodityId;
    private String title;
    private double marketPrice;
    private double price;
    private double rating;
    private int salesVolume;
    private SellerBean seller;
    private localityBean locality;
    private CoverBean cover;
    private List<ImageBean> images;
    private List<PlanBean> plans;
    public String id;
    public boolean isFavorite;
    public ShareCommodityBean creteShareBean(){
        ShareCommodityBean bean = new ShareCommodityBean();
        bean.title = this.title;
        bean.commodityId = this.commodityId;
        bean.price = this.price;
        bean.image = cover.url;
        return bean;
    }

    public List<PlanBean> getPlans() {
        return plans;
    }

    public void setPlans(List<PlanBean> plans) {
        this.plans = plans;
    }

    public void setCommodityId(long commodityId) {
        this.commodityId = commodityId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setSalesVolume(int salesVolume) {
        this.salesVolume = salesVolume;
    }

    public void setSeller(SellerBean seller) {
        this.seller = seller;
    }

    public void setLocality(localityBean locality) {
        this.locality = locality;
    }

    public void setCover(CoverBean cover) {
        this.cover = cover;
    }

    public void setImages(List<ImageBean> images) {
        this.images = images;
    }

    public long getCommodityId() {
        return commodityId;
    }

    public String getTitle() {
        return title;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public double getPrice() {
        return price;
    }

    public double getRating() {
        return rating;
    }

    public int getSalesVolume() {
        return salesVolume;
    }

    public SellerBean getSeller() {
        return seller;
    }

    public localityBean getLocality() {
        return locality;
    }

    public CoverBean getCover() {
        return cover;
    }

    public List<ImageBean> getImages() {
        return images;
    }


    public SimpleCommodityBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.shareUrl);
        dest.writeLong(this.version);
        dest.writeLong(this.commodityId);
        dest.writeString(this.title);
        dest.writeDouble(this.marketPrice);
        dest.writeDouble(this.price);
        dest.writeDouble(this.rating);
        dest.writeInt(this.salesVolume);
        dest.writeParcelable(this.seller, 0);
        dest.writeParcelable(this.locality, 0);
        dest.writeParcelable(this.cover, 0);
        dest.writeTypedList(images);
        dest.writeTypedList(plans);
        dest.writeString(this.id);
        dest.writeByte(isFavorite ? (byte) 1 : (byte) 0);
    }

    protected SimpleCommodityBean(Parcel in) {
        this.shareUrl = in.readString();
        this.version = in.readLong();
        this.commodityId = in.readLong();
        this.title = in.readString();
        this.marketPrice = in.readDouble();
        this.price = in.readDouble();
        this.rating = in.readDouble();
        this.salesVolume = in.readInt();
        this.seller = in.readParcelable(SellerBean.class.getClassLoader());
        this.locality = in.readParcelable(localityBean.class.getClassLoader());
        this.cover = in.readParcelable(CoverBean.class.getClassLoader());
        this.images = in.createTypedArrayList(ImageBean.CREATOR);
        this.plans = in.createTypedArrayList(PlanBean.CREATOR);
        this.id = in.readString();
        this.isFavorite = in.readByte() != 0;
    }

    public static final Creator<SimpleCommodityBean> CREATOR = new Creator<SimpleCommodityBean>() {
        public SimpleCommodityBean createFromParcel(Parcel source) {
            return new SimpleCommodityBean(source);
        }

        public SimpleCommodityBean[] newArray(int size) {
            return new SimpleCommodityBean[size];
        }
    };
}
