package com.aizou.peachtravel.bean;

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
    public LocationBean location;
    public ArrayList<ImageBean> images=new ArrayList<>();
    public String openTime;
    public String timeCostDesc;
    public String address;
    public String trafficInfoUrl;
    public String guideUrl;
    public String kengdieUrl;
    public String tipsUrl;

    @Override
    public ShareDialogBean createShareBean() {
        ExtMessageBean extMessageBean = new ExtMessageBean();
        extMessageBean.id = id;
        extMessageBean.type = TravelApi.PeachType.SPOT;
        extMessageBean.image = images.size()>0?images.get(0).url:"";
        extMessageBean.name = zhName;
        extMessageBean.desc = desc;
        extMessageBean.timeCost = timeCostDesc;
        return new ShareDialogBean(extMessageBean);
    }
}
