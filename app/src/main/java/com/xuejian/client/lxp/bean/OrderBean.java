package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

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
    public boolean committed;
    private long orderId;
    private long consumerId;
    private String planId;
    private SimpleCommodityBean commodity;
    private ContactBean contact;
    private String rendezvousTime;
    private double totalPrice;
    private double discount;
    private int quantity;
    private String comment;
    private String status;
    private ArrayList<TravellerEntity> travellers;
    // private List<String> commodityTimeRange;
    private long createTime;
    private long updateTime;
    private long expireTime;
    public ArrayList<TradeActivityBean> activities;
    public CouponBean couponBean;
    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
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

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setDiscount(double discount) {
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

    public void setTravellers(ArrayList<TravellerEntity > travellers) {
        this.travellers = travellers;
    }

//    public void setCommodityTimeRange(List<String > commodityTimeRange) {
//        this.commodityTimeRange = commodityTimeRange;
//    }

    public long getOrderId() {
        return orderId;
    }

    public long getConsumerId() {
        return consumerId;
    }

    public String getPlanId() {
        return planId;
    }

    public SimpleCommodityBean getCommodity() {
        return commodity;
    }
    public ShareCommodityBean creteShareBean(){
        ShareCommodityBean bean = new ShareCommodityBean();
        bean.title = this.commodity.getTitle();
        bean.commodityId = this.commodity.getCommodityId();
        bean.price = this.totalPrice;
        bean.image = this.commodity.getCover().url;
        return bean;
    }

    public ContactBean getContact() {
        return contact;
    }

    public String getRendezvousTime() {
        return rendezvousTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public double getDiscount() {
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

    public ArrayList<TravellerEntity> getTravellers() {
        return travellers;
    }

//    public List<?> getCommodityTimeRange() {
//        return commodityTimeRange;
//    }

    public OrderBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(committed ? (byte) 1 : (byte) 0);
        dest.writeLong(this.orderId);
        dest.writeLong(this.consumerId);
        dest.writeString(this.planId);
        dest.writeParcelable(this.commodity, 0);
        dest.writeParcelable(this.contact, 0);
        dest.writeString(this.rendezvousTime);
        dest.writeDouble(this.totalPrice);
        dest.writeDouble(this.discount);
        dest.writeInt(this.quantity);
        dest.writeString(this.comment);
        dest.writeString(this.status);
        dest.writeTypedList(travellers);
        dest.writeLong(this.createTime);
        dest.writeLong(this.updateTime);
        dest.writeLong(this.expireTime);
        dest.writeTypedList(activities);
        dest.writeParcelable(this.couponBean, 0);
    }

    protected OrderBean(Parcel in) {
        this.committed = in.readByte() != 0;
        this.orderId = in.readLong();
        this.consumerId = in.readLong();
        this.planId = in.readString();
        this.commodity = in.readParcelable(SimpleCommodityBean.class.getClassLoader());
        this.contact = in.readParcelable(ContactBean.class.getClassLoader());
        this.rendezvousTime = in.readString();
        this.totalPrice = in.readDouble();
        this.discount = in.readDouble();
        this.quantity = in.readInt();
        this.comment = in.readString();
        this.status = in.readString();
        this.travellers = in.createTypedArrayList(TravellerEntity.CREATOR);
        this.createTime = in.readLong();
        this.updateTime = in.readLong();
        this.expireTime = in.readLong();
        this.activities = in.createTypedArrayList(TradeActivityBean.CREATOR);
        this.couponBean = in.readParcelable(CouponBean.class.getClassLoader());
    }

    public static final Creator<OrderBean> CREATOR = new Creator<OrderBean>() {
        public OrderBean createFromParcel(Parcel source) {
            return new OrderBean(source);
        }

        public OrderBean[] newArray(int size) {
            return new OrderBean[size];
        }
    };
}
