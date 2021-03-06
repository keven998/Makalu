package com.lv.bean;

import com.lv.utils.TimeUtils;
import com.lv.im.IMClient;

/**
 * Created by q on 2015/4/21.
 */
public class MessageBean {

    private long LocalId;  //本地消息的 ID，pk，自增
    private int ServerId;//服务器消息 id
    private int Status;//消息的发送状态 ：   0：发送成功，   1：发送中，   2：发送失败
    private int Type;//消息类型： 0：普通文本消息， 1：语音消息， 2：图片消息，  3：动画表情消息，  4:位置消息，  5：poi-城市，  6：poi-景点， 7：poi-美食，  8：poi-酒店， 9：poi-游记
    private String Message;//消息体内容
    private long CreateTime;//消息的创建时间，其中发送的消息以创建消息的时间为准，接收的消息以接收到消息的本地时间为准。
    private int SendType;//0：发送，   1：接收
    private String Metadata;   //如果消息不是文本消息，是语音等其他富媒体信息，储存修饰富媒体信息的 json 体。
    private long SenderId;//如果是群聊显示发送者的 id，单聊不显示

    public String getAbbrev() {
        return abbrev;
    }

    public void setAbbrev(String abbrev) {
        this.abbrev = abbrev;
    }

    private String abbrev;

    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    private long receiverId;
    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    private int progress; //进度

    public MessageBean() {
    }

    public MessageBean(String content) {
        this(0, 0, 1, content, TimeUtils.getTimestamp(), 0, null, Long.parseLong(IMClient.getInstance().getCurrentUserId()),null,0);
    }
    public MessageBean(int serverId, int status, int type, String message, long createTime, int sendType, String metadata, long senderId,long _receiverId) {
        ServerId = serverId;
        Status = status;
        Type = type;
        Message = message;
        CreateTime = createTime;
        SendType = sendType;
        Metadata = metadata;
        SenderId = senderId;
        receiverId = _receiverId;
    }

    public MessageBean(int serverId, int status, int type, String message, long createTime, int sendType, String metadata, long senderId) {
        ServerId = serverId;
        Status = status;
        Type = type;
        Message = message;
        CreateTime = createTime;
        SendType = sendType;
        Metadata = metadata;
        SenderId = senderId;
    }

    public MessageBean(int serverId, int status, int type, String message, long createTime, int sendType, String metadata, long senderId, String abbrev,long receiverId) {
        ServerId = serverId;
        Status = status;
        Type = type;
        Message = message;
        CreateTime = createTime;
        SendType = sendType;
        Metadata = metadata;
        SenderId = senderId;
        this.abbrev = abbrev;
        this.receiverId = receiverId;
    }

    public MessageBean(long localId, int serverId, int status, int type, String message, long createTime, int sendType, String metadata, long senderId) {
        LocalId = localId;
        ServerId = serverId;
        Status = status;
        Type = type;
        Message = message;
        CreateTime = createTime;
        SendType = sendType;
        Metadata = metadata;
        SenderId = senderId;

    }

    public long getLocalId() {
        return LocalId;
    }

    public void setLocalId(int localId) {
        LocalId = localId;
    }

    public int getServerId() {
        return ServerId;
    }

    public void setServerId(int serverId) {
        ServerId = serverId;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public long getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(long createTime) {
        CreateTime = createTime;
    }

    public int getSendType() {
        return SendType;
    }

    public void setSendType(int sendType) {
        SendType = sendType;
    }

    public String getMetadata() {
        return Metadata;
    }

    public void setMetadata(String metadata) {
        Metadata = metadata;
    }

    public long getSenderId() {
        return SenderId;
    }

    public void setSenderId(long senderId) {
        SenderId = senderId;
    }
}
