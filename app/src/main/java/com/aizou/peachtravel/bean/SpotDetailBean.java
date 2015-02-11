package com.aizou.peachtravel.bean;

import android.text.TextUtils;

import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.share.ICreateShareDialog;
import com.aizou.peachtravel.common.share.ShareDialogBean;

import java.util.ArrayList;

/**
 * Created by Rjm on 2014/11/17.
 */
public class SpotDetailBean implements ICreateShareDialog{
    public boolean isFavorite;
    public String type;
    public String id;
    public String zhName;
    public String enName;
    public String desc;
    public String priceDesc;
    public String travelMonth;
    public float rating;
    public LocationBean location;
    public ArrayList<ImageBean> images=new ArrayList<>();
    public String openTime;
    public String timeCostDesc;
    public String address;
    public String trafficInfoUrl;
    public String visitGuideUrl;
    public String tipsUrl;
    private int rank;
    public float getRating() {
        if(rating>1){
            return rating;
        }
        return rating * 5;
    }
    public String getRank() {
        if(rank>100){
            return "大于100";
        }
        return rank+"";
    }
    @Override
    public ShareDialogBean createShareBean() {
        ExtMessageBean extMessageBean = new ExtMessageBean();
        extMessageBean.id = id;
        extMessageBean.type = TravelApi.PeachType.SPOT;
        extMessageBean.image = images.size()>0?images.get(0).url:"";
        extMessageBean.name = zhName;
        extMessageBean.desc =  (!TextUtils.isEmpty(desc))?desc.substring(0,50):"";
        extMessageBean.timeCost = timeCostDesc;
        return new ShareDialogBean(extMessageBean);
    }
}
