package com.xuejian.client.lxp.bean;

import java.util.List;

/**
 * Created by yibiao.qin on 2015/11/23.
 */
public class SellerBean {

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
    private List<String> lang;
    private List<String> serviceZones;
    private List<String> bankAccounts;
    private List<String> email;
    private List<String> phone;

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

    public void setServiceZones(List<String> serviceZones) {
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

    public List<String> getServiceZones() {
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
}
