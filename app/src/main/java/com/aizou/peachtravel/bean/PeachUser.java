package com.aizou.peachtravel.bean;

import java.io.Serializable;

public class PeachUser implements Serializable{
    public static final String M="M";
    public static final String F="F";
	public long userId;
	public String nickName="";
	public String avatar="";
    public String avatarSmall="";
    public String gender="";
    public String signature="";
	public String tel;
    public String secToken;
    public String countryCode;
    public String email;
    public String easemobUser;
    public String easemobPwd;
    public String memo="";

    public String getGenderDesc() {
        if ("F".equals(gender)) {
            return "美女";
        } else if ("M".equals(gender)) {
            return "帅锅";
        } else if ("U".equals(gender)) {
            return "无可奉告";
        }
        return "";
    }
}
