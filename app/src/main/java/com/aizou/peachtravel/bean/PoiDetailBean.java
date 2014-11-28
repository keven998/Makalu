package com.aizou.peachtravel.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Rjm on 2014/11/22.
 */
public class PoiDetailBean implements Parcelable {
    public final static String RESTAURANT="restaurant";
    public final static String SHOPPING="shopping";
    public boolean hasAdded=false;
    public String type;
    public String id;
    public String zhName;
    public String enName;
    public String priceDesc;
    public String desc;
    public String timeCostDesc;
    public float rating;
    public int commentCnt;
    public LocationBean loction;
    public List<ImageBean> images;
    public List<LocBean> locList;
    public String address;
    public String telephone;
    public List<RecommendBean> recommends;
    public List<CommentBean> comments;

    public PoiDetailBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.id);
        dest.writeString(this.zhName);
        dest.writeString(this.enName);
        dest.writeString(this.priceDesc);
        dest.writeString(this.desc);
        dest.writeString(this.timeCostDesc);
        dest.writeFloat(this.rating);
        dest.writeInt(this.commentCnt);
        dest.writeParcelable(this.loction, 0);
        dest.writeTypedList(images);
        dest.writeTypedList(locList);
        dest.writeString(this.address);
        dest.writeString(this.telephone);
        dest.writeTypedList(recommends);
        dest.writeTypedList(comments);
    }

    private PoiDetailBean(Parcel in) {
        this.type = in.readString();
        this.id = in.readString();
        this.zhName = in.readString();
        this.enName = in.readString();
        this.priceDesc = in.readString();
        this.desc = in.readString();
        this.timeCostDesc = in.readString();
        this.rating = in.readFloat();
        this.commentCnt = in.readInt();
        this.loction = in.readParcelable(LocationBean.class.getClassLoader());
        in.readTypedList(images, ImageBean.CREATOR);
        in.readTypedList(locList, LocBean.CREATOR);
        this.address = in.readString();
        this.telephone = in.readString();
        in.readTypedList(recommends, RecommendBean.CREATOR);
        in.readTypedList(comments, CommentBean.CREATOR);
    }

    public static final Creator<PoiDetailBean> CREATOR = new Creator<PoiDetailBean>() {
        public PoiDetailBean createFromParcel(Parcel source) {
            return new PoiDetailBean(source);
        }

        public PoiDetailBean[] newArray(int size) {
            return new PoiDetailBean[size];
        }
    };
}
