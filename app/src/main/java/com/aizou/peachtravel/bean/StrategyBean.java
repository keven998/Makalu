package com.aizou.peachtravel.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.share.ICreateShareDialog;
import com.aizou.peachtravel.common.share.ShareDialogBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/12/26.
 */
public class StrategyBean implements ICreateShareDialog{
    public String id;
    public String title;
    public String summary;
    public long updateTime;
    public List<ImageBean> images=new ArrayList<>();
    public ArrayList<LocBean> localities;
    public ArrayList<IndexPoi> itinerary;
    public ArrayList<PoiDetailBean> shopping;
    public ArrayList<PoiDetailBean> restaurant;
    public String userId;
    public Integer dayCnt;
    public Integer itineraryDays;

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

        extMessageBean.desc = summary;
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

        public static final Parcelable.Creator<IndexPoi> CREATOR = new Parcelable.Creator<IndexPoi>() {
            public IndexPoi createFromParcel(Parcel source) {
                return new IndexPoi(source);
            }

            public IndexPoi[] newArray(int size) {
                return new IndexPoi[size];
            }
        };
    }

}
