package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2016/4/20.
 */
public class Consumer implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.userId);
        dest.writeString(this.nickname);
        dest.writeParcelable(this.avatar, flags);
    }

    public Consumer() {
    }

    protected Consumer(Parcel in) {
        this.userId = in.readLong();
        this.nickname = in.readString();
        this.avatar = in.readParcelable(AvatarBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<Consumer> CREATOR = new Parcelable.Creator<Consumer>() {
        @Override
        public Consumer createFromParcel(Parcel source) {
            return new Consumer(source);
        }

        @Override
        public Consumer[] newArray(int size) {
            return new Consumer[size];
        }
    };
}
