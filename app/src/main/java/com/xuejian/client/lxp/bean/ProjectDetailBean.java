package com.xuejian.client.lxp.bean;

import java.util.List;

/**
 * Created by yibiao.qin on 2016/4/19.
 */
public class ProjectDetailBean {

    /**
     * itemId : 1460706241869
     * consumerId : 202847
     * destination : [{"id":"546f2da7b8ce0440eddb2855","zhName":"福冈","enName":""},{"id":"546f2da8b8ce0440eddb2874","zhName":"熊本","enName":""}]
     * contact : [{"surname":"jdjd","givenName":"jsjsj","gender":"m","email":"","birthday":0,"tel":{"dialCode":86,"number":1828},"identities":[]}]
     * departure : [{"id":"5473ccd7b8ce043a64108c46","zhName":"北京市","enName":""}]
     * departureDate : 2016-04-15
     * timeCost : 1
     * participants : ["children"]
     * participantCnt : 0
     * budget : 122
     * memo : 就是觉得家
     * service : 机票酒店,美食门票
     * topic : 蜜月度假,家庭亲子
     * takers : []
     * scheduled : {}
     * schedules : []
     * schedulePaid : false
     * totalPrice : 0.0
     * bountyPaid : false
     * bountyPrice : 0.0
     */

    private long itemId;
    private long consumerId;
    private String departureDate;
    private int timeCost;
    private int participantCnt;
    private double budget;
    private String memo;
    private String service;
    private String topic;
    private boolean schedulePaid;
    private double totalPrice;
    private boolean bountyPaid;
    private double bountyPrice;
    /**
     * id : 546f2da7b8ce0440eddb2855
     * zhName : 福冈
     * enName :
     */

    private List<LocBean> destination;
    /**
     * surname : jdjd
     * givenName : jsjsj
     * gender : m
     * email :
     * birthday : 0
     * tel : {"dialCode":86,"number":1828}
     * identities : []
     */

    private List<ContactBean> contact;
    /**
     * id : 5473ccd7b8ce043a64108c46
     * zhName : 北京市
     * enName :
     */
    public  Consumer consumer;
    private List<LocBean> departure;
    private List<String> participants;
    private List<String> takers;
    private List<BountyItemBean> schedules;

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

    public boolean isSchedulePaid() {
        return schedulePaid;
    }

    public void setSchedulePaid(boolean schedulePaid) {
        this.schedulePaid = schedulePaid;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isBountyPaid() {
        return bountyPaid;
    }

    public void setBountyPaid(boolean bountyPaid) {
        this.bountyPaid = bountyPaid;
    }

    public double getBountyPrice() {
        return bountyPrice;
    }

    public void setBountyPrice(double bountyPrice) {
        this.bountyPrice = bountyPrice;
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

    public List<String> getTakers() {
        return takers;
    }

    public void setTakers(List<String> takers) {
        this.takers = takers;
    }

    public List<?> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<BountyItemBean> schedules) {
        this.schedules = schedules;
    }

}
