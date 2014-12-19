package com.aizou.peachtravel.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.share.ICreateShareDialog;
import com.aizou.peachtravel.common.share.ShareDialogBean;

/**
 * Created by Rjm on 2014/11/14.
 */
public class TravelNoteBean implements Parcelable,ICreateShareDialog {
    public String id;
    public String title;
    public String summary;
    public String cover;
    public String authorName;
    public String authorAvatar;
    public String source;
    public String sourceUrl;
    public long publishDate;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.summary);
        dest.writeString(this.cover);
        dest.writeString(this.authorName);
        dest.writeString(this.authorAvatar);
        dest.writeString(this.source);
        dest.writeString(this.sourceUrl);
        dest.writeLong(this.publishDate);
    }

    public TravelNoteBean() {
    }

    private TravelNoteBean(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.summary = in.readString();
        this.cover = in.readString();
        this.authorName = in.readString();
        this.authorAvatar = in.readString();
        this.source = in.readString();
        this.sourceUrl = in.readString();
        this.publishDate = in.readLong();
    }

    public static final Creator<TravelNoteBean> CREATOR = new Creator<TravelNoteBean>() {
        public TravelNoteBean createFromParcel(Parcel source) {
            return new TravelNoteBean(source);
        }

        public TravelNoteBean[] newArray(int size) {
            return new TravelNoteBean[size];
        }
    };

    @Override
    public ShareDialogBean createShareBean() {
        ExtMessageBean extMessageBean = new ExtMessageBean();
        extMessageBean.type = TravelApi.PeachType.NOTE;
        extMessageBean.id = id;
        extMessageBean.image= cover;
        extMessageBean.name = title;
        if(TextUtils.isEmpty(summary)){
            String[] strArray=summary.split("\n");
            String maxLengthStr=strArray[0];
            for(String str:strArray){
                if(str.length()>maxLengthStr.length()){
                    maxLengthStr=str;
                }
            }
            extMessageBean.desc = maxLengthStr;
        }
        return new ShareDialogBean(extMessageBean);
    }
}
