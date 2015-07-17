package com.xuejian.client.lxp.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class PeachUser implements Serializable {
    public static final String M = "M";
    public static final String F = "F";
    public long userId;
    public String nickName = "";
    public String avatar = "";
    public String avatarSmall = "";
    public String gender = "";
    public String signature = "";
    public String tel;
    public String secToken;
    public String countryCode;
    public String email;
    public String easemobUser;
    public String easemobPwd;
    public String memo = "";
    public String travelStatus;
    public String residence;
    public String level;
    public String zodiac;
    public String birthday;
    public Map<String, ArrayList<LocBean>> tracks;
    public int guideCnt;

    public static String getF() {
        return F;
    }

    public static String getM() {
        return M;
    }

    public long getUserId() {
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

    public String getEasemobUser() {
        return easemobUser;
    }

    public void setEasemobUser(String easemobUser) {
        this.easemobUser = easemobUser;
    }

    public String getEasemobPwd() {
        return easemobPwd;
    }

    public void setEasemobPwd(String easemobPwd) {
        this.easemobPwd = easemobPwd;
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

    public Map<String, ArrayList<LocBean>> getTracks() {
        return tracks;
    }

    public void setTracks(Map<String, ArrayList<LocBean>> tracks) {
        this.tracks = tracks;
    }

    public int getGuideCnt() {
        return guideCnt;
    }

    public void setGuideCnt(int guideCnt) {
        this.guideCnt = guideCnt;
    }

    public String getGenderDesc() {
        if ("F".equals(gender)) {
            return "美女";
        } else if ("M".equals(gender)) {
            return "帅锅";
        } else if ("U".equals(gender)) {
            return "不告诉你";
        }
        return "";
    }
}
