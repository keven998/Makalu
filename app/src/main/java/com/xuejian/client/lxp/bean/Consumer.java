package com.xuejian.client.lxp.bean;

/**
 * Created by yibiao.qin on 2016/4/20.
 */
public class Consumer {

    /**
     * userId : 202847
     * nickname : 小马哥
     * avatar : {"url":"http://taozi-uploads.qiniudn.com/avt_202847_1457338351658.jpg"}
     */

    private long userId;
    private String nickname;
    /**
     * url : http://taozi-uploads.qiniudn.com/avt_202847_1457338351658.jpg
     */

    private AvatarBean avatar;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public AvatarBean getAvatar() {
        return avatar;
    }

    public void setAvatar(AvatarBean avatar) {
        this.avatar = avatar;
    }

}
