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
    public long createTime;
    public ArrayList<ImageBean> images = new ArrayList<>();
    public ArrayList<LocBean> localities = new ArrayList<>();
    public ArrayList<IndexPoi> itinerary = new ArrayList<>();
    public ArrayList<PoiDetailBean> shopping = new ArrayList<>();
    public ArrayList<PoiDetailBean> restaurant = new ArrayList<>();
    public ArrayList<DemoBean> demoItems = new ArrayList<>();
    public ArrayList<TrafficBean> trafficItems = new ArrayList<>();

    public long userId;
    public Integer dayCnt;
    public Integer itineraryDays;
    public String detailUrl;
    public String status;

    @Override
    public ShareDialogBean createShareBean() {
        ExtMessageBean extMessageBean = new ExtMessageBean();
        extMessageBean.type = TravelApi.PeachType.GUIDE;
        extMessageBean.id = id;
        extMessageBean.name = title;
        extMessageBean.image = images.size() > 0 ? images.get(0).url : "";
        if (dayCnt == null) {
            extMessageBean.timeCost = itineraryDays + "天";
        } else {
            extMessageBean.timeCost = dayCnt + "天";
        }
        if (TextUtils.isEmpty(summary)) {
            StringBuilder sb = new StringBuilder();
            for (LocBean loc : localities) {
                sb.append(loc.zhName + " ");
            }
            extMessageBean.desc = sb.toString();
        } else {
            extMessageBean.desc = summary;
        }

        return new ShareDialogBean(extMessageBean);
    }

    public StrategyBean() {
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof StrategyBean){
            return id.equals(((StrategyBean) o).id);
        }
        return super.equals(o);
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
        dest.writeLong(this.createTime);
        dest.writeTypedList(images);
        dest.writeTypedList(localities);
        dest.writeTypedList(itinerary);
        dest.writeTypedList(shopping);
        dest.writeTypedList(restaurant);
        dest.writeTypedList(demoItems);
        dest.writeTypedList(trafficItems);
        dest.writeLong(this.userId);
        dest.writeValue(this.dayCnt);
        dest.writeValue(this.itineraryDays);
        dest.writeString(this.detailUrl);
        dest.writeString(this.status);
    }

    protected StrategyBean(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.summary = in.readString();
        this.updateTime = in.readLong();
        this.createTime = in.readLong();
        this.images = in.createTypedArrayList(ImageBean.CREATOR);
        this.localities = in.createTypedArrayList(LocBean.CREATOR);
        this.itinerary = in.createTypedArrayList(IndexPoi.CREATOR);
        this.shopping = in.createTypedArrayList(PoiDetailBean.CREATOR);
        this.restaurant = in.createTypedArrayList(PoiDetailBean.CREATOR);
        this.demoItems = in.createTypedArrayList(DemoBean.CREATOR);
        this.trafficItems = in.createTypedArrayList(TrafficBean.CREATOR);
        this.userId = in.readLong();
        this.dayCnt = (Integer) in.readValue(Integer.class.getClassLoader());
        this.itineraryDays = (Integer) in.readValue(Integer.class.getClassLoader());
        this.detailUrl = in.readString();
        this.status = in.readString();
    }

    public static final Creator<StrategyBean> CREATOR = new Creator<StrategyBean>() {
        @Override
        public StrategyBean createFromParcel(Parcel source) {
            return new StrategyBean(source);
        }

        @Override
        public StrategyBean[] newArray(int size) {
            return new StrategyBean[size];
        }
    };
}
