package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by yibiao.qin on 2015/12/1.
 */
public class OrderBean implements Parcelable {

    /**
     * orderId : 订单ID
     * consumerId : 顾客ID
     * planId : 商品计划ID
     * commodity : 商品
     * travellers : 旅客信息
     * contact : 联系人信息
     * rendezvousTime : 旅游预约时间
     * commodityTimeRange : 旅游时段
     * totalPrice : 总价
     * discount : 折扣
     * quantity : 数量
     * comment : 留言
     * status : 状态
     * createTime : 订单创建时间
     * updateTime : 订单更新时间
     * expireTime : 订单失效时间
     */

    private long orderId;
    private int consumerId;
    private String planId;
    private SimpleCommodityBean commodity;
    private ContactBean contact;
    private String rendezvousTime;
    private int totalPrice;
    private int discount;
    private int quantity;
    private String comment;
    private String status;
    private List<String> travellers;
    private List<String> commodityTimeRange;
    private String createTime;
    private String updateTime;
    private String expireTime;


    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public void setConsumerId(int consumerId) {
        this.consumerId = consumerId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public void setCommodity(SimpleCommodityBean commodity) {
        this.commodity = commodity;
    }

    public void setContact(ContactBean contact) {
        this.contact = contact;
    }

    public void setRendezvousTime(String rendezvousTime) {
        this.rendezvousTime = rendezvousTime;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTravellers(List<String > travellers) {
        this.travellers = travellers;
    }

    public void setCommodityTimeRange(List<String > commodityTimeRange) {
        this.commodityTimeRange = commodityTimeRange;
    }

    public long getOrderId() {
        return orderId;
    }

    public int getConsumerId() {
        return consumerId;
    }

    public String getPlanId() {
        return planId;
    }

    public SimpleCommodityBean getCommodity() {
        return commodity;
    }

    public ContactBean getContact() {
        return contact;
    }

    public String getRendezvousTime() {
        return rendezvousTime;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public int getDiscount() {
        return discount;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getComment() {
        return comment;
    }

    public String getStatus() {
        return status;
    }

    public List<?> getTravellers() {
        return travellers;
    }

    public List<?> getCommodityTimeRange() {
        return commodityTimeRange;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.orderId);
        dest.writeInt(this.consumerId);
        dest.writeString(this.planId);
        dest.writeParcelable(this.commodity, 0);
        dest.writeParcelable(this.contact, 0);
        dest.writeString(this.rendezvousTime);
        dest.writeInt(this.totalPrice);
        dest.writeInt(this.discount);
        dest.writeInt(this.quantity);
        dest.writeString(this.comment);
        dest.writeString(this.status);
        dest.writeStringList(this.travellers);
        dest.writeStringList(this.commodityTimeRange);
        dest.writeString(this.createTime);
        dest.writeString(this.updateTime);
        dest.writeString(this.expireTime);
    }

    public OrderBean() {
    }

    protected OrderBean(Parcel in) {
        this.orderId = in.readLong();
        this.consumerId = in.readInt();
        this.planId = in.readString();
        this.commodity = in.readParcelable(SimpleCommodityBean.class.getClassLoader());
        this.contact = in.readParcelable(ContactBean.class.getClassLoader());
        this.rendezvousTime = in.readString();
        this.totalPrice = in.readInt();
        this.discount = in.readInt();
        this.quantity = in.readInt();
        this.comment = in.readString();
        this.status = in.readString();
        this.travellers = in.createStringArrayList();
        this.commodityTimeRange = in.createStringArrayList();
        this.createTime = in.readString();
        this.updateTime = in.readString();
        this.expireTime = in.readString();
    }

    public static final Parcelable.Creator<OrderBean> CREATOR = new Parcelable.Creator<OrderBean>() {
        public OrderBean createFromParcel(Parcel source) {
            return new OrderBean(source);
        }

        public OrderBean[] newArray(int size) {
            return new OrderBean[size];
        }
    };
}
