package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.share.ICreateShareDialog;
import com.xuejian.client.lxp.common.share.ShareDialogBean;

import java.util.ArrayList;

/**
 * Created by Rjm on 2014/12/26.
 */
public class StrategyBean implements ICreateShareDialog, Parcelable {
    public String id;
    public String title;
    public String summary;
    public long updateTime;
    public ArrayList<ImageBean> images=new ArrayList<>();
    public ArrayList<LocBean> localities=new ArrayList<>();
    public ArrayList<IndexPoi> itinerary=new ArrayList<>();
    public ArrayList<PoiDetailBean> shopping=new ArrayList<>();
    public ArrayList<PoiDetailBean> restaurant=new ArrayList<>();
    public long userId;
    public Integer dayCnt;
    public Integer itineraryDays;
    public String detailUrl;
    public String status;

    @Override
    public ShareDialogBean createShareBean() {
        ExtMessageBean extMessageBean = new ExtMessageBean();
        extMessageBean.type = TravelApi.PeachType.GUIDE;
        extMessageBean.id=id;
        extMessageBean.name = title;
        extMessageBean.image =images.size()>0?images.get(0).url:"";
        if(dayCnt==null){
            extMessageBean.timeCost = itineraryDays+"天";
        }else{
            extMessageBean.timeCost = dayCnt+"天";
        }
        if(TextUtils.isEmpty(summary)){
            StringBuffer sb=new StringBuffer();
            for(LocBean loc:localities){
                sb.append(loc.zhName+" ");
            }
            extMessageBean.desc = sb.toString();
        }else{
            extMessageBean.desc = summary;
        }

        return new ShareDialogBean(extMessageBean);
    }

    public static class IndexPoi implements Parcelable {
        public int dayIndex;
        public PoiDetailBean poi;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.dayIndex);
            dest.writeParcelable(this.poi, 0);
        }

        public IndexPoi() {
        }

        private IndexPoi(Parcel in) {
            this.dayIndex = in.readInt();
            this.poi = in.readParcelable(PoiDetailBean.class.getClassLoader());
        }

        public static final Creator<IndexPoi> CREATOR = new Creator<IndexPoi>() {
            public IndexPoi createFromParcel(Parcel source) {
                return new IndexPoi(source);
            }

            public IndexPoi[] newArray(int size) {
                return new IndexPoi[size];
            }
        };
    }

    public StrategyBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.summary);
        dest.writeLong(this.updateTime);
        dest.writeTypedList(images);
        dest.writeTypedList(localities);
        dest.writeTypedList(itinerary);
        dest.writeTypedList(shopping);
        dest.writeTypedList(restaurant);
        dest.writeLong(this.userId);
        dest.writeValue(this.dayCnt);
        dest.writeValue(this.itineraryDays);
        dest.writeString(this.detailUrl);
    }

    private StrategyBean(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.summary = in.readString();
        this.updateTime = in.readLong();
        in.readTypedList(images, ImageBean.CREATOR);
        in.readTypedList(localities, LocBean.CREATOR);
        in.readTypedList(itinerary, IndexPoi.CREATOR);
        in.readTypedList(shopping, PoiDetailBean.CREATOR);
        in.readTypedList(restaurant, PoiDetailBean.CREATOR);
        this.userId = in.readLong();
        this.dayCnt = (Integer) in.readValue(Integer.class.getClassLoader());
        this.itineraryDays = (Integer) in.readValue(Integer.class.getClassLoader());
        this.detailUrl = in.readString();
    }

    public static final Creator<StrategyBean> CREATOR = new Creator<StrategyBean>() {
        public StrategyBean createFromParcel(Parcel source) {
            return new StrategyBean(source);
        }

        public StrategyBean[] newArray(int size) {
            return new StrategyBean[size];
        }
    };
}
