package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by yibiao.qin on 2015/12/2.
 */
public class PlanBean implements Parcelable {

    /**
     * planId : 2e4fdd2a-f850-4212-98a6-2a1ec7be008e
     * title : 泰国曼谷+芭提雅+沙美岛6日跟团游(4钻)·价低品优+直飞+住沙美岛海滨酒店 特卖汇
     * desc :
     * pricing : [{"price":23,"timeRange":[1449100800000,1450396800000]},{"price":32,"timeRange":[1450483200000,1450915200000]}]
     * marketPrice : 23
     * price : 23
     * stockInfo : []
     */

    private String planId;
    private String title;
    private String desc;
    private int marketPrice;
    private int price;
    /**
     * price : 23
     * timeRange : [1449100800000,1450396800000]
     */

    private List<PricingEntity> pricing;
    private List<String> stockInfo;

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setMarketPrice(int marketPrice) {
        this.marketPrice = marketPrice;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setPricing(List<PricingEntity> pricing) {
        this.pricing = pricing;
    }

    public void setStockInfo(List<String> stockInfo) {
        this.stockInfo = stockInfo;
    }

    public String getPlanId() {
        return planId;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public int getMarketPrice() {
        return marketPrice;
    }

    public int getPrice() {
        return price;
    }

    public List<PricingEntity> getPricing() {
        return pricing;
    }

    public List<String> getStockInfo() {
        return stockInfo;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.planId);
        dest.writeString(this.title);
        dest.writeString(this.desc);
        dest.writeInt(this.marketPrice);
        dest.writeInt(this.price);
        dest.writeTypedList(pricing);
        dest.writeStringList(this.stockInfo);
    }

    public PlanBean() {
    }

    protected PlanBean(Parcel in) {
        this.planId = in.readString();
        this.title = in.readString();
        this.desc = in.readString();
        this.marketPrice = in.readInt();
        this.price = in.readInt();
        this.pricing = in.createTypedArrayList(PricingEntity.CREATOR);
        this.stockInfo = in.createStringArrayList();
    }

    public static final Parcelable.Creator<PlanBean> CREATOR = new Parcelable.Creator<PlanBean>() {
        public PlanBean createFromParcel(Parcel source) {
            return new PlanBean(source);
        }

        public PlanBean[] newArray(int size) {
            return new PlanBean[size];
        }
    };
}
