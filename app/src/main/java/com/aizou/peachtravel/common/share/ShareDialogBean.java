package com.aizou.peachtravel.common.share;

import com.aizou.peachtravel.bean.ExtMessageBean;
import com.aizou.peachtravel.common.api.TravelApi;

/**
 * Created by Rjm on 2014/12/18.
 */
public class ShareDialogBean {
    private String type;
    private String title;
    private String name;
    private String image;
    private String attr;
    private String desc;

    private int extType;
    private ExtMessageBean extMessageBean;

    public ShareDialogBean(ExtMessageBean extMessageBean){
        setExtMessageBean(extMessageBean);
    }
    public int getExtType() {
        return extType;
    }

    public String getType() {
        return type;
    }

    public ExtMessageBean getExtMessageBean() {
        return extMessageBean;
    }

    private void setExtMessageBean(ExtMessageBean extMessageBean){
        this.extMessageBean = extMessageBean;
        this.type = extMessageBean.type;
        this.name = extMessageBean.name;
        this.image = extMessageBean.image;
        if(extMessageBean.type.equals(TravelApi.PeachType.GUIDE)){
            title = "计划";
            attr = extMessageBean.timeCost;
            desc = extMessageBean.desc;
            extType=1;

        }else if(extMessageBean.type.equals(TravelApi.PeachType.RESTAURANTS)){
            title="美食";
            attr=extMessageBean.rating+" "+extMessageBean.price;
            desc =extMessageBean.address;
            extType =5;
        }else if(extMessageBean.type.equals(TravelApi.PeachType.SHOPPING)){
            title="购物";
            attr ="";
            desc =extMessageBean.address;
            extType =6;
        }else if(extMessageBean.type.equals(TravelApi.PeachType.HOTEL)){
            title ="酒店";
            attr=extMessageBean.rating+" "+extMessageBean.price;
            desc =extMessageBean.address;
            extType =7;
        }else if(extMessageBean.type.equals(TravelApi.PeachType.LOC)){
            title ="城市";
            attr=extMessageBean.timeCost;
            desc =extMessageBean.desc;
            extType =2;
        }else if(extMessageBean.type.equals(TravelApi.PeachType.NOTE)){
            title ="游记";
            attr= "";
            desc = extMessageBean.desc;
            extType =3;
        }else if(extMessageBean.type.equals(TravelApi.PeachType.SPOT)){
            title="景点";
            attr=extMessageBean.timeCost;
            desc = extMessageBean.desc;
            extType =4;
        }
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }


    public String getImage() {
        return image;
    }


    public String getAttr() {
        return attr;
    }


    public String getDesc() {
        return desc;
    }

}
