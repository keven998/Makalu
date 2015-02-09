package com.aizou.peachtravel.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.share.ICreateShareDialog;
import com.aizou.peachtravel.common.share.ShareDialogBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/11/14.
 */
public class TravelNoteBean implements Parcelable,ICreateShareDialog {
    public String id;
    public String title;
    public String summary;
//    public String cover;
    public List<ImageBean> images= new ArrayList<>();
    public String authorName;
    public String authorAvatar;
    public String source;
    public String sourceUrl;
    public long publishTime;
    public String detailUrl;

    public TravelNoteBean() {
    }

    public String getNoteImage(){
        if(images!=null&&images.size()>0){
           return images.get(0).url;
        }
        return "";
    }

    public void setFieldFromExtMessageBean(ExtMessageBean messageBean){
        this.id=messageBean.id;
        ArrayList<ImageBean> imageBeanList = new ArrayList<ImageBean>();
        ImageBean imageBean = new ImageBean();
        imageBean.url = messageBean.image;
        imageBeanList.add(imageBean);
        this.images = imageBeanList;
        this.title = messageBean.name;
        this.summary = messageBean.desc;
        this.detailUrl = messageBean.detailUrl;
    }
    public void setFieldFromRecBean(RecDestBean.RecDestItem recBean){
        this.id=recBean.itemId;
        ArrayList<ImageBean> imageBeanList = new ArrayList<ImageBean>();
        ImageBean imageBean = new ImageBean();
        imageBean.url = recBean.cover;
        imageBeanList.add(imageBean);
        this.images = imageBeanList;
        this.title = recBean.title;
        this.summary = recBean.desc;
        this.detailUrl = recBean.linkUrl;
    }
    public void setFieldFromFavBean(FavoritesBean favBean){
        this.id=favBean.itemId;
        this.images = favBean.images;
        this.title = favBean.zhName;
        this.summary = favBean.desc;
    }

    @Override
    public ShareDialogBean createShareBean() {
        ExtMessageBean extMessageBean = new ExtMessageBean();
        extMessageBean.type = TravelApi.PeachType.NOTE;
        extMessageBean.id = id;
        if(images.size()>0){
            extMessageBean.image= images.get(0).url;
        }
        extMessageBean.name = title;
        if(!TextUtils.isEmpty(summary)){
            String[] strArray=summary.split("\n");
            String maxLengthStr=strArray[0];
            for(String str:strArray){
                if(str.length()>maxLengthStr.length()){
                    maxLengthStr=str;
                }
            }
            extMessageBean.desc = maxLengthStr;
        }
        extMessageBean.detailUrl = detailUrl;
        return new ShareDialogBean(extMessageBean);
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
        dest.writeTypedList(images);
        dest.writeString(this.authorName);
        dest.writeString(this.authorAvatar);
        dest.writeString(this.source);
        dest.writeString(this.sourceUrl);
        dest.writeLong(this.publishTime);
        dest.writeString(this.detailUrl);
    }

    private TravelNoteBean(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.summary = in.readString();
        in.readTypedList(images, ImageBean.CREATOR);
        this.authorName = in.readString();
        this.authorAvatar = in.readString();
        this.source = in.readString();
        this.sourceUrl = in.readString();
        this.publishTime = in.readLong();
        this.detailUrl = in.readString();
    }

    public static final Creator<TravelNoteBean> CREATOR = new Creator<TravelNoteBean>() {
        public TravelNoteBean createFromParcel(Parcel source) {
            return new TravelNoteBean(source);
        }

        public TravelNoteBean[] newArray(int size) {
            return new TravelNoteBean[size];
        }
    };
}
