package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2015/12/2.
 */
public class PlanBean implements Parcelable {

    /**
     * planId :
     * title :
     * desc :
     */

    private String planId;
    private String title;
    private String desc;

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPlanId() {
        return planId;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.planId);
        dest.writeString(this.title);
        dest.writeString(this.desc);
    }

    public PlanBean() {
    }

    protected PlanBean(Parcel in) {
        this.planId = in.readString();
        this.title = in.readString();
        this.desc = in.readString();
    }

    public static final Parcelable.Creator<PlanBean> CREATOR = new Parcelable.Creator<PlanBean>() {
        public PlanBean createFromParcel(Parcel source) {
            return new PlanBean(source);
        }

        public PlanBean[] newArray(int size) {
            return new PlanBean[size];
        }
    };
}
