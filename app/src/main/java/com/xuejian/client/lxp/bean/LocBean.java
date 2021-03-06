package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.share.ICreateShareDialog;
import com.xuejian.client.lxp.common.share.ShareDialogBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/11/14.
 */
public class LocBean implements Parcelable, ICreateShareDialog {
    public boolean isAdded;
    public String header;
    public String id;
    public String zhName;
    public String enName;
    public String pinyin;
    public LocationBean location;
    public String desc;
    public float timeCost;
    public String timeCostDesc;
    public String travelMonth;
    public List<ImageBean> images = new ArrayList<ImageBean>();
    public int imageCnt;
    public boolean isFavorite;
    public String playGuide;
    public String diningTitles;
    public String shoppingTitles;
    public String destCountry;
    public boolean isChecked;
    public boolean isVote;
    public boolean traveled;
    public ArrayList<String> style=new ArrayList<String>();
    public float rating;

    public LocBean() {
    }
    public float getRating() {
        if (rating > 1) {
            return rating;
        }
        return rating * 5;
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof LocBean) {
            LocBean bean = (LocBean) o;
            return bean.id.equals(this.id);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {
        if (!TextUtils.isEmpty(id)) {
            return id.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "LocBean{" +
                "id='" + id + '\'' +
                ", zhName='" + zhName + '\'' +
                '}';
    }

    @Override
    public ShareDialogBean createShareBean() {
        ExtMessageBean extMessageBean = new ExtMessageBean();
        extMessageBean.name = zhName;
        extMessageBean.timeCost = timeCostDesc;
        extMessageBean.type = TravelApi.PeachType.LOC;
        extMessageBean.id = id;
        extMessageBean.desc = (!TextUtils.isEmpty(desc)) ? desc.substring(0, 50) : "";
        if (images != null && images.size() > 0)
            extMessageBean.image = images.get(0).url;
        return new ShareDialogBean(extMessageBean);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(isAdded ? (byte) 1 : (byte) 0);
        dest.writeString(this.header);
        dest.writeString(this.id);
        dest.writeString(this.zhName);
        dest.writeString(this.enName);
        dest.writeString(this.pinyin);
        dest.writeParcelable(this.location, 0);
        dest.writeString(this.desc);
        dest.writeFloat(this.timeCost);
        dest.writeString(this.timeCostDesc);
        dest.writeString(this.travelMonth);
        dest.writeTypedList(images);
        dest.writeInt(this.imageCnt);
        dest.writeByte(isFavorite ? (byte) 1 : (byte) 0);
        dest.writeString(this.playGuide);
        dest.writeString(this.destCountry);
       dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isVote ? (byte) 1 : (byte) 0);
        dest.writeByte(this.traveled ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.style);
        dest.writeFloat(this.rating);
    }

    private LocBean(Parcel in) {
        this.isAdded = in.readByte() != 0;
        this.header = in.readString();
        this.id = in.readString();
        this.zhName = in.readString();
        this.enName = in.readString();
        this.pinyin = in.readString();
        this.location = in.readParcelable(LocationBean.class.getClassLoader());
        this.desc = in.readString();
        this.timeCost = in.readFloat();
        this.timeCostDesc = in.readString();
        this.travelMonth = in.readString();
        in.readTypedList(images, ImageBean.CREATOR);
        this.imageCnt = in.readInt();
        this.isFavorite = in.readByte() != 0;
        this.playGuide = in.readString();
        this.destCountry = in.readString();
        this.isChecked = (in.readByte() != 0);
        this.isVote = (in.readByte() != 0);
        this.traveled = (in.readByte() != 0);
        this.style = (ArrayList<String>) in.readSerializable();
        this.rating = in.readFloat();
    }

    public static final Creator<LocBean> CREATOR = new Creator<LocBean>() {
        public LocBean createFromParcel(Parcel source) {
            return new LocBean(source);
        }

        public LocBean[] newArray(int size) {
            return new LocBean[size];
        }
    };

}
