package com.aizou.peachtravel.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.aizou.peachtravel.common.share.ICreateShareDialog;
import com.aizou.peachtravel.common.share.ShareDialogBean;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/11/22.
 */
public class PoiDetailBean implements Parcelable,ICreateShareDialog{
    public final static String RESTAURANT="restaurant";
    public final static String SHOPPING="shopping";
    public String distance="";
    public boolean hasAdded=false;
    public boolean isFavorite;
    public String type;
    public String id;
    public String zhName;
    public String enName;
    public String priceDesc="";
    public String desc;
    public String timeCostDesc;
    public float rating;
    public int commentCnt;
    public LocationBean location;
    public List<ImageBean> images = new ArrayList<ImageBean>();
    public List<LocBean> locList = new ArrayList<LocBean>();
    public LocBean locality;
    public String address="";
    public ArrayList<String> tel=new ArrayList<>();
    public List<RecommendBean> recommends = new ArrayList<RecommendBean>();
    public List<CommentBean> comments= new ArrayList<CommentBean>();
    public String moreCommentsUrl;

    public PoiDetailBean() {
    }

    public float getRating() {
        if(rating>1){
            return rating;
        }
        return rating * 5;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof PoiDetailBean){
            return id.equals(((PoiDetailBean)o).id);
        }else{
            return false;
        }

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public ShareDialogBean createShareBean() {
        ExtMessageBean extMessageBean = new ExtMessageBean();
        extMessageBean.id = id;
        extMessageBean.type = type;
        extMessageBean.image = images.size()>0?images.get(0).url:"";
        extMessageBean.name = zhName;
        DecimalFormat df= new   java.text.DecimalFormat("#.#");
        extMessageBean.rating=df.format(getRating());
        extMessageBean.price = priceDesc;
        extMessageBean.address = address;
        extMessageBean.timeCost = timeCostDesc;
        extMessageBean.desc=desc;
        return new ShareDialogBean(extMessageBean);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.distance);
        dest.writeByte(hasAdded ? (byte) 1 : (byte) 0);
        dest.writeByte(isFavorite ? (byte) 1 : (byte) 0);
        dest.writeString(this.type);
        dest.writeString(this.id);
        dest.writeString(this.zhName);
        dest.writeString(this.enName);
        dest.writeString(this.priceDesc);
        dest.writeString(this.desc);
        dest.writeString(this.timeCostDesc);
        dest.writeFloat(this.rating);
        dest.writeInt(this.commentCnt);
        dest.writeParcelable(this.location, 0);
        dest.writeTypedList(images);
        dest.writeTypedList(locList);
        dest.writeParcelable(this.locality, 0);
        dest.writeString(this.address);
        dest.writeSerializable(this.tel);
        dest.writeTypedList(recommends);
        dest.writeTypedList(comments);
        dest.writeString(this.moreCommentsUrl);
    }

    private PoiDetailBean(Parcel in) {
        this.distance = in.readString();
        this.hasAdded = in.readByte() != 0;
        this.isFavorite = in.readByte() != 0;
        this.type = in.readString();
        this.id = in.readString();
        this.zhName = in.readString();
        this.enName = in.readString();
        this.priceDesc = in.readString();
        this.desc = in.readString();
        this.timeCostDesc = in.readString();
        this.rating = in.readFloat();
        this.commentCnt = in.readInt();
        this.location = in.readParcelable(LocationBean.class.getClassLoader());
        in.readTypedList(images, ImageBean.CREATOR);
        in.readTypedList(locList, LocBean.CREATOR);
        this.locality = in.readParcelable(LocBean.class.getClassLoader());
        this.address = in.readString();
        this.tel = (ArrayList<String>) in.readSerializable();
        in.readTypedList(recommends, RecommendBean.CREATOR);
        in.readTypedList(comments, CommentBean.CREATOR);
        this.moreCommentsUrl = in.readString();
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
