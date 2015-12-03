package com.xuejian.client.lxp.bean;

import java.util.ArrayList;

/**
 * Created by yibiao.qin on 2015/11/24.
 */
public class RecommendCommodityBean {


    /**
     * topicType : 分类标题
     * commodities : 商品列表
     */

    private String topicType;
    /**
     * title : 标题
     * marketPrice : 现价
     * price : 原价
     * rating : 分数
     * salesVolume : 好评度
     * images : 图片
     */

    private ArrayList<SimpleCommodityBean> commodities;

    public void setTopicType(String topicType) {
        this.topicType = topicType;
    }

    public void setCommodities(ArrayList<SimpleCommodityBean> commodities) {
        this.commodities = commodities;
    }

    public String getTopicType() {
        return topicType;
    }

    public ArrayList<SimpleCommodityBean> getCommodities() {
        return commodities;
    }

}
