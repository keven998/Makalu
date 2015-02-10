package com.aizou.peachtravel.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.share.ICreateShareDialog;
import com.aizou.peachtravel.common.share.ShareDialogBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/11/14.
 */
public class LocBean implements Parcelable,ICreateShareDialog {
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
    public List<ImageBean> images =new ArrayList<ImageBean>();
    public int imageCnt;
    public boolean isFavorite;
    public String playGuide;


    public LocBean() {
    }


    @Override
    public boolean equals(Object o) {
        if(o instanceof LocBean){
            LocBean bean = (LocBean) o;
            return bean.id.equals(this.id);
        }else{
            return false;
        }

    }

    @Override
    public int hashCode() {
        if(!TextUtils.isEmpty(id)){
            return id.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public ShareDialogBean createShareBean() {
        ExtMessageBean extMessageBean = new ExtMessageBean();
        extMessageBean.name = zhName;
        extMessageBean.timeCost = timeCostDesc;
        extMessageBean.type= TravelApi.PeachType.LOC;
        extMessageBean.id = id;
        extMessageBean.desc =  (!TextUtils.isEmpty(desc))?desc.substring(0,50):"";
        if(images!=null&&images.size()>0)
        extMessageBean.image =images.get(0).url;
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
