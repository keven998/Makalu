package com.aizou.peachtravel.db;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import com.easemob.chat.EMContact;

import java.io.Serializable;

/**
 * Entity mapped to table IMUSER.
 */
public class IMUser extends EMContact implements Serializable {

    private Long userId=0L;
    private String username;
    private String nick;
    private String avatar="";
    private String gender;
    private String signature;
    private String tel;
    private String memo;
    private Integer unreadMsgCount=0;
    private String header="";
    private boolean isMyFriends=false;

    public IMUser() {
    }

    public IMUser(String username) {
        this.username = username;
    }

    public IMUser(Long userId, String username, String nick, String avatar, String gender, String signature, String tel, String memo, Integer unreadMsgCount, String header, boolean isMyFriends) {
        this.userId = userId;
        this.username = username;
        this.nick = nick;
        this.avatar = avatar;
        this.gender = gender;
        this.signature = signature;
        this.tel = tel;
        this.memo = memo;
        this.unreadMsgCount = unreadMsgCount;
        this.header = header;
        this.isMyFriends = isMyFriends;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(Integer unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public boolean getIsMyFriends() {
        return isMyFriends;
    }

    public void setIsMyFriends(boolean isMyFriends) {
        this.isMyFriends = isMyFriends;
    }



    public boolean equals(Object other){       //重写equals方法，后面最好重写hashCode方法

        if(this == other)                                      //先检查是否其自反性，后比较other是否为空。这样效率高
            return true;
        if(other == null)
            return false;
        if( !(other instanceof IMUser))
            return false;

        final IMUser user = (IMUser)other;
        if( !getUsername().equals(user.getUsername()))
            return false;
        return true;
    }

    public int hashCode(){                 //hashCode主要是用来提高hash系统的查询效率。当hashCode中不进行任何操作时，可以直接让其返回 一常数，或者不进行重写。
        int result = getUsername().hashCode();
        return result;
        //return 0;
    }

}
