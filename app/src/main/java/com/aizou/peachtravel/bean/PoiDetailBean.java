package com.aizou.peachtravel.bean;

import java.util.List;

/**
 * Created by Rjm on 2014/11/22.
 */
public class PoiDetailBean {
    public final static String RESTAURANT="restaurant";
    public final static String SHOPPING="shopping";
    public String type;
    public String id;
    public String zhName;
    public String enName;
    public String priceDesc;
    public String desc;
    public float rating;
    public int commentCnt;
    public LocationBean loction;
    public List<ImageBean> images;
    public String address;
    public String telephone;
    public List<RecommendBean> recommends;
    public List<CommentBean> comments;
}
