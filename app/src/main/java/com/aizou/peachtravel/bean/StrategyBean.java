package com.aizou.peachtravel.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Rjm on 2014/12/26.
 */
public class StrategyBean {
    public String id;
    public String title;
    public ArrayList<LocBean> destinations;
    public ArrayList<IndexPoi> itinerary;
    public ArrayList<PoiDetailBean> shopping;
    public ArrayList<PoiDetailBean> restaurants;
    public String userId;
    public int itineraryDays;




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
