package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by yibiao.qin on 2016/4/1.
 */
public class BountiesBean implements Parcelable {

    /**
     * itemId : 1459332961004
     * consumerId : 211216
     * destination : [{"id":"5473ccd7b8ce043a64108c46","zhName":"北京","enName":""}]
     * contact : [{"surname":"霍","givenName":"青桐","gender":"m","email":"lvxingpai@gmail.com","birthday":0,"tel":{"dialCode":86,"number":13099880088},"identities":[]}]
     * bountyPrice : 1
     * departure : [{"id":"5473ccd7b8ce043a64108c46","zhName":"北京","enName":""}]
     * departureDate : 2016-04-05
     * timeCost : 2
     * participants : ["children"]
     * participantCnt : 0
     * budget : 100
     * memo : 备注
     * service : 服务
     * topic : 主题
     * takers : []
     * paid : false
     */

    public int takersCnt;
    public  long createTime;
    public  long updateTime;
    private long itemId;
    private long consumerId;
    private double bountyPrice;
    private String departureDate;
    private int timeCost;
    private int participantCnt;
    private double budget;
    private String memo;
    private String service;
    private String topic;
    private boolean paid;
    /**
     * id : 5473ccd7b8ce043a64108c46
     * zhName : 北京
     * enName :
     */

    private List<LocBean> destination;
    /**
     * surname : 霍
     * givenName : 青桐
     * gender : m
     * email : lvxingpai@gmail.com
     * birthday : 0
     * tel : {"dialCode":86,"number":13099880088}
     * identities : []
     */

    private List<ContactBean> contact;
    /**
     * id : 5473ccd7b8ce043a64108c46
     * zhName : 北京
     * enName :
     */

    private List<LocBean> departure;
    private List<String> participants;
  //  private List<String> takers;

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(long consumerId) {
        this.consumerId = consumerId;
    }

    public double getBountyPrice() {
        return bountyPrice;
    }

    public void setBountyPrice(double bountyPrice) {
        this.bountyPrice = bountyPrice;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public int getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(int timeCost) {
        this.timeCost = timeCost;
    }

    public int getParticipantCnt() {
        return participantCnt;
    }

    public void setParticipantCnt(int participantCnt) {
        this.participantCnt = participantCnt;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public List<LocBean> getDestination() {
        return destination;
    }

    public void setDestination(List<LocBean> destination) {
        this.destination = destination;
    }

    public List<ContactBean> getContact() {
        return contact;
    }

    public void setContact(List<ContactBean> contact) {
        this.contact = contact;
    }

    public List<LocBean> getDeparture() {
        return departure;
    }

    public void setDeparture(List<LocBean> departure) {
        this.departure = departure;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }


    public BountiesBean() {
    }

    @Override
    public String toString() {
        return "BountiesBean{" +
                "itemId=" + itemId +
                ", consumerId=" + consumerId +
                ", bountyPrice=" + bountyPrice +
                ", departureDate='" + departureDate + '\'' +
                ", timeCost=" + timeCost +
                ", participantCnt=" + participantCnt +
                ", budget=" + budget +
                ", memo='" + memo + '\'' +
                ", service='" + service + '\'' +
                ", topic='" + topic + '\'' +
                ", paid=" + paid +
                ", destination=" + destination +
                ", contact=" + contact +
                ", departure=" + departure +
                ", participants=" + participants +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.takersCnt);
        dest.writeLong(this.createTime);
        dest.writeLong(this.updateTime);
        dest.writeLong(this.itemId);
        dest.writeLong(this.consumerId);
        dest.writeDouble(this.bountyPrice);
        dest.writeString(this.departureDate);
        dest.writeInt(this.timeCost);
        dest.writeInt(this.participantCnt);
        dest.writeDouble(this.budget);
        dest.writeString(this.memo);
        dest.writeString(this.service);
        dest.writeString(this.topic);
        dest.writeByte(paid ? (byte) 1 : (byte) 0);
        dest.writeTypedList(destination);
        dest.writeTypedList(contact);
        dest.writeTypedList(departure);
        dest.writeStringList(this.participants);
    }

    protected BountiesBean(Parcel in) {
        this.takersCnt = in.readInt();
        this.createTime = in.readLong();
        this.updateTime = in.readLong();
        this.itemId = in.readLong();
        this.consumerId = in.readLong();
        this.bountyPrice = in.readDouble();
        this.departureDate = in.readString();
        this.timeCost = in.readInt();
        this.participantCnt = in.readInt();
        this.budget = in.readDouble();
        this.memo = in.readString();
        this.service = in.readString();
        this.topic = in.readString();
        this.paid = in.readByte() != 0;
        this.destination = in.createTypedArrayList(LocBean.CREATOR);
        this.contact = in.createTypedArrayList(ContactBean.CREATOR);
        this.departure = in.createTypedArrayList(LocBean.CREATOR);
        this.participants = in.createStringArrayList();
    }

    public static final Creator<BountiesBean> CREATOR = new Creator<BountiesBean>() {
        @Override
        public BountiesBean createFromParcel(Parcel source) {
            return new BountiesBean(source);
        }

        @Override
        public BountiesBean[] newArray(int size) {
            return new BountiesBean[size];
        }
    };
}
