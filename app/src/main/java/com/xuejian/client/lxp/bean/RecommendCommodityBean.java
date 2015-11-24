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

    private ArrayList<CommoditiesEntity> commodities;

    public void setTopicType(String topicType) {
        this.topicType = topicType;
    }

    public void setCommodities(ArrayList<CommoditiesEntity> commodities) {
        this.commodities = commodities;
    }

    public String getTopicType() {
        return topicType;
    }

    public ArrayList<CommoditiesEntity> getCommodities() {
        return commodities;
    }

    public static class CommoditiesEntity {
        private String title;
        private double marketPrice;
        private int price;
        private double rating;
        private int salesVolume;

        private ArrayList<ImageBean> images;

        public void setTitle(String title) {
            this.title = title;
        }

        public void setMarketPrice(double marketPrice) {
            this.marketPrice = marketPrice;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        public void setSalesVolume(int salesVolume) {
            this.salesVolume = salesVolume;
        }

        public void setImages(ArrayList<ImageBean> images) {
            this.images = images;
        }

        public String getTitle() {
            return title;
        }

        public double getMarketPrice() {
            return marketPrice;
        }

        public int getPrice() {
            return price;
        }

        public double getRating() {
            return rating;
        }

        public int getSalesVolume() {
            return salesVolume;
        }

        public ArrayList<ImageBean> getImages() {
            return images;
        }
    }
}
