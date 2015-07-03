package com.xuejian.client.lxp.db;

/**
 * Created by yibiao.qin on 2015/7/3.
 */
public class InventMessage {

    public InventMessage(long userId, String nickName, String avatarSmall, String requestMsg, String requestId, int status, long time) {
        UserId = userId;
        this.nickName = nickName;
        this.avatarSmall = avatarSmall;
        this.requestMsg = requestMsg;
        this.requestId = requestId;
        this.status = status;
        this.time = time;
    }
    public InventMessage(int Id, long userId, String nickName, String avatarSmall, String requestMsg, String requestId, int status, long time) {
        this.Id=Id;
        UserId = userId;
        this.nickName = nickName;
        this.avatarSmall = avatarSmall;
        this.requestMsg = requestMsg;
        this.requestId = requestId;
        this.status = status;
        this.time = time;
    }

    private int Id;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public long getUserId() {
        return UserId;
    }

    public void setUserId(long userId) {
        UserId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatarSmall() {
        return avatarSmall;
    }

    public void setAvatarSmall(String avatarSmall) {
        this.avatarSmall = avatarSmall;
    }

    public String getRequestMsg() {
        return requestMsg;
    }

    public void setRequestMsg(String requestMsg) {
        this.requestMsg = requestMsg;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    private long UserId;
    private String nickName;
    private String avatarSmall;
    private String requestMsg;
    private String requestId;
    private int status;
    private long time;
}
