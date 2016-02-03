package com.xuejian.client.lxp.bean;

import java.util.ArrayList;

/**
 * Created by yibiao.qin on 2016/1/30.
 */
public class CommentDetailBean {
    /**
     * id : 56ab53fa3d7ed12c8453ec1f
     * contents : 好评pppb
     * rating : 0
     * user : {"userId":211216,"nickname":"topy","avatar":{"url":"http://wx.qlogo.cn/mmopen/PiajxSqBRaEK9jgOU4NscPQD5r7JaLVjlWuiatp1bDDCWcoRzZJsDEgZyB7G6MwJvC2yc2kgAeIODjEDHicqfaqrQ/0"}}
     * reply : {}
     * createTime : 1454068730557
     * updateTime : 1454068730557
     */
    public OrderBean order;
    public boolean anonymous;
    private String id;
    private String contents;
    private float rating;
    /**
     * userId : 211216
     * nickname : topy
     * avatar : {"url":"http://wx.qlogo.cn/mmopen/PiajxSqBRaEK9jgOU4NscPQD5r7JaLVjlWuiatp1bDDCWcoRzZJsDEgZyB7G6MwJvC2yc2kgAeIODjEDHicqfaqrQ/0"}
     */

    private UserEntity user;
    private long createTime;
    private long updateTime;

    public ArrayList<ImageBean> images;
    public void setId(String id) {
        this.id = id;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getId() {
        return id;
    }

    public String getContents() {
        return contents;
    }

    public float getRating() {
        return rating;
    }

    public UserEntity getUser() {
        return user;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public static class UserEntity {
        private long userId;
        private String nickname;
        /**
         * url : http://wx.qlogo.cn/mmopen/PiajxSqBRaEK9jgOU4NscPQD5r7JaLVjlWuiatp1bDDCWcoRzZJsDEgZyB7G6MwJvC2yc2kgAeIODjEDHicqfaqrQ/0
         */

        private Avatar avatar;

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setAvatar(Avatar avatar) {
            this.avatar = avatar;
        }

        public long getUserId() {
            return userId;
        }

        public String getNickname() {
            return nickname;
        }

        public Avatar getAvatar() {
            return avatar;
        }
        public static class  Avatar{
            public String url;
        }


    }
}
