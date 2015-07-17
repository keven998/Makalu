package com.xuejian.client.lxp.bean;

import com.xuejian.client.lxp.common.share.ICreateShareDialog;
import com.xuejian.client.lxp.common.share.ShareDialogBean;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoyong on 14/12/11.
 */
public class FavoritesBean implements ICreateShareDialog {
    public final static int CONST_TYPE_SPOT = 1;
    public final static int CONST_TYPE_FOOD = 2;
    public final static int CONST_TYPE_SHOP = 3;
    public final static int CONST_TYPE_STAY = 4;
    public final static int CONST_TYPE_NOTE = 5;
    public final static int CONST_TYPE_CITY = 6;

    public String id;
    public String userId;
    public String itemId;
    public String type;
    public String zhName;
    public String enName;
    public String desc;
    public List<ImageBean> images = new ArrayList<>();
    public LocBean locality;
    public long createTime;
    public String timeCostDesc;
    public float rating;
    public String priceDesc;
    public String address;
    public String telephone;

    public int getType() {
        if ("hotel".equals(type)) {
            return CONST_TYPE_STAY;
        } else if ("restaurant".equals(type)) {
            return CONST_TYPE_FOOD;
        } else if ("shopping".equals(type)) {
            return CONST_TYPE_SHOP;
        } else if ("travelNote".equals(type)) {
            return CONST_TYPE_NOTE;
        } else if ("vs".equals(type)) {
            return CONST_TYPE_SPOT;
        } else {
            return CONST_TYPE_CITY;
        }
    }

    @Override
    public ShareDialogBean createShareBean() {
        ExtMessageBean extMessageBean = new ExtMessageBean();
        extMessageBean.type = type;
        extMessageBean.image = images.size() > 0 ? images.get(0).url : "";
        extMessageBean.desc = desc;
        extMessageBean.id = itemId;
        extMessageBean.timeCost = timeCostDesc;
        extMessageBean.address = address;
        extMessageBean.name = zhName;
        extMessageBean.price = priceDesc;
        DecimalFormat df = new DecimalFormat("#.#");
        extMessageBean.rating = df.format(rating * 5);
        return new ShareDialogBean(extMessageBean);
    }
}
