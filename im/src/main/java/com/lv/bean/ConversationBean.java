package com.lv.bean;

/**
 * private int FriendId;
 * private long LastChatTime;
 * private String HASH;
 * private int last_rev_msgId;
 * private int isRead; //0:已读  num：数量
 * private String conversation;
 * private String lastMessage;
 */
public class ConversationBean {
    private int FriendId;
    private long LastChatTime;
    private String HASH;
    private int last_rev_msgId;
    private int isRead; //0:有未读  1：已读
    private String conversation;

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getSendType() {
        return sendType;
    }

    public void setSendType(int sendType) {
        this.sendType = sendType;
    }

    private int Status;
    private int sendType;

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    private int Type;
    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    private String chatType;

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    private String lastMessage;
    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public ConversationBean(int friendId, int isRead  ,String chatType) {
        FriendId = friendId;
        this.isRead = isRead;
        this.chatType=chatType;
    }

    public ConversationBean(int friendId, long lastChatTime, String HASH, int Last_rev_msgId, int isRead,String conversation,String lastMessage,String chatType,int type,int status,int sendType) {
        LastChatTime = lastChatTime;
        this.HASH = HASH;
        FriendId = friendId;
        last_rev_msgId = Last_rev_msgId;
        this.isRead = isRead;
        this.conversation=conversation;
        this.lastMessage=lastMessage;
        this.chatType=chatType;
        this.Type=type;
        this.Status=status;
        this.sendType=sendType;
    }

    public int getFriendId() {
        return FriendId;
    }

    public void setFriendId(int friendId) {
        FriendId = friendId;
    }

    public long getLastChatTime() {
        return LastChatTime;
    }

    public void setLastChatTime(long lastChatTime) {
        LastChatTime = lastChatTime;
    }

    public String getHASH() {
        return HASH;
    }

    public void setHASH(String HASH) {
        this.HASH = HASH;
    }

    public int getLast_rev_msgId() {
        return last_rev_msgId;
    }

    public void setLast_rev_msgId(int last_rev_msgId) {
        this.last_rev_msgId = last_rev_msgId;
    }
    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }
}
