package com.xuejian.client.lxp.db;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import com.xuejian.client.lxp.bean.ExpertInfo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Entity mapped to table USER.
 */
public class User implements Serializable {
    public static final String M = "M";
    public static final String F = "F";
    public static final String S = "S";
    public static final String U = "U";
    private long userId;
    private String nickName;
    private String avatar;
    private String avatarSmall;
    private String gender;
    private String signature;
    private String tel;
    private String secToken;
    private String countryCode;
    private String email;
    private String memo;
    private String travelStatus;
    private String residence;
    private String level;
    private String zodiac;
    private String birthday;
    private int guideCnt;
    public boolean isBlocked;
    public ExpertInfo expertInfo;
    private ArrayList<String> tags;
    public int getTrackCnt() {
        return trackCnt;
    }

    public void setTrackCnt(int trackCnt) {
        this.trackCnt = trackCnt;
    }

    public int getCountryCnt() {
        return countryCnt;
    }

    public void setCountryCnt(int countryCnt) {
        this.countryCnt = countryCnt;
    }

    public int getTravelNoteCnt() {
        return travelNoteCnt;
    }

    public void setTravelNoteCnt(int travelNoteCnt) {
        this.travelNoteCnt = travelNoteCnt;
    }

    public int getAlbumCnt() {
        return albumCnt;
    }

    public void setAlbumCnt(int albumCnt) {
        this.albumCnt = albumCnt;
    }

    private int trackCnt;
    private int countryCnt;
    private int travelNoteCnt;
    private int albumCnt;
    private Integer Type;
    private String Ext;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    private String header;

    public User() {
    }

    public User(Long userId, String nickName, String ext, Integer type, String avatar) {
        this.userId = userId;
        this.nickName = nickName;
        Ext = ext;
        Type = type;
        this.avatar = avatar;
    }

    public User(Long userId, String nickName, String avatar, String avatarSmall, String gender, String signature, String tel, String secToken, String countryCode, String email, String memo, String travelStatus, String residence, String level, String zodiac, String birthday, int guideCnt, Integer Type, String Ext, String header) {
        this.userId = userId;
        this.nickName = nickName;
        this.avatar = avatar;
        this.avatarSmall = avatarSmall;
        this.gender = gender;
        this.signature = signature;
        this.tel = tel;
        this.secToken = secToken;
        this.countryCode = countryCode;
        this.email = email;
        this.memo = memo;
        this.travelStatus = travelStatus;
        this.residence = residence;
        this.level = level;
        this.zodiac = zodiac;
        this.birthday = birthday;
        this.guideCnt = guideCnt;
        this.Type = Type;
        this.Ext = Ext;
        this.header = header;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatarSmall() {
        return avatarSmall;
    }

    public void setAvatarSmall(String avatarSmall) {
        this.avatarSmall = avatarSmall;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getSecToken() {
        return secToken;
    }

    public void setSecToken(String secToken) {
        this.secToken = secToken;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getTravelStatus() {
        return travelStatus;
    }

    public void setTravelStatus(String travelStatus) {
        this.travelStatus = travelStatus;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getZodiac() {
        return zodiac;
    }

    public void setZodiac(String zodiac) {
        this.zodiac = zodiac;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getGuideCnt() {
        return guideCnt;
    }

    public void setGuideCnt(int guideCnt) {
        this.guideCnt = guideCnt;
    }

    public Integer getType() {
        return Type;
    }

    public void setType(Integer Type) {
        this.Type = Type;
    }

    public String getExt() {
        return Ext;
    }

    public void setExt(String Ext) {
        this.Ext = Ext;
    }


    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getGenderDesc() {
        if ("F".equals(gender)) {
            return "美女";
        } else if ("M".equals(gender)) {
            return "帅锅";
        } else if ("U".equals(gender)) {
            return "不告诉你";
        } else if ("S".equals(gender)) {
            return "保密";
        }
        return "";
    }

}
