package com.xuejian.client.lxp.bean;

import java.util.List;

/**
 * Created by yibiao.qin on 2015/11/23.
 */
public class CommodityBean {

    /**
     * id : 0ac698d9d4a91ae449576033
     * commodityId : 78668897366
     * title : adfadf
     * rating : 0.99
     * salesVolume : 100
     * marketPrice : 100.0999984741211
     * price : 123
     * category : ["特色活动"]
     * images : [{"caption":null,"key":null,"bucket":null,"width":0,"height":0,"url":"http://7sbm17.com1.z0.glb.clouddn.com/avatar/df09a4b34ef187f129cb9842c8d55c41"}]
     * seller : {"sellerId":100012,"lang":[],"serviceZones":[],"bankAccounts":[],"name":"天意小馆","email":[],"phone":[],"favorCnt":0}
     * desc : {"title":"商品介绍","summary":"dsf","body":"dsf"}
     * notice : [{"title":"费用包含","summary":"adsf","body":"adsf"},{"title":"费用不含","summary":"asdf","body":"asdf"},{"title":"使用方法","summary":"asdf","body":"asdf"},{"title":"注意事项","summary":"adsf","body":"adsf"}]
     * refundPolicy : [{"title":"预定流程","summary":"adsf","body":"adsf"},{"title":"退改流程","summary":"adsf","body":"adsf"}]
     * trafficInfo : [{"title":"交通提示","summary":"adsf","body":"adsf"}]
     */

    private String id;
    private long commodityId;
    private String title;
    private double rating;
    private int salesVolume;
    private double marketPrice;
    private int price;
    /**
     * sellerId : 100012
     * lang : []
     * serviceZones : []
     * bankAccounts : []
     * name : 天意小馆
     * email : []
     * phone : []
     * favorCnt : 0
     */

    private SellerBean seller;
    /**
     * title : 商品介绍
     * summary : dsf
     * body : dsf
     */

    private DescEntity desc;
    private List<String> category;
    /**
     * caption : null
     * key : null
     * bucket : null
     * width : 0
     * height : 0
     * url : http://7sbm17.com1.z0.glb.clouddn.com/avatar/df09a4b34ef187f129cb9842c8d55c41
     */

    private List<ImagesEntity> images;
    /**
     * title : 费用包含
     * summary : adsf
     * body : adsf
     */

    private List<NoticeEntity> notice;
    /**
     * title : 预定流程
     * summary : adsf
     * body : adsf
     */

    private List<RefundPolicyEntity> refundPolicy;
    /**
     * title : 交通提示
     * summary : adsf
     * body : adsf
     */

    private List<TrafficInfoEntity> trafficInfo;

    public void setId(String id) {
        this.id = id;
    }

    public void setCommodityId(long commodityId) {
        this.commodityId = commodityId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setSalesVolume(int salesVolume) {
        this.salesVolume = salesVolume;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public void setPrice(int price) {
        this.price = price;
    }


    public void setDesc(DescEntity desc) {
        this.desc = desc;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public void setImages(List<ImagesEntity> images) {
        this.images = images;
    }

    public void setNotice(List<NoticeEntity> notice) {
        this.notice = notice;
    }

    public void setRefundPolicy(List<RefundPolicyEntity> refundPolicy) {
        this.refundPolicy = refundPolicy;
    }

    public void setTrafficInfo(List<TrafficInfoEntity> trafficInfo) {
        this.trafficInfo = trafficInfo;
    }

    public String getId() {
        return id;
    }

    public long getCommodityId() {
        return commodityId;
    }

    public String getTitle() {
        return title;
    }

    public double getRating() {
        return rating;
    }

    public int getSalesVolume() {
        return salesVolume;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public int getPrice() {
        return price;
    }


    public DescEntity getDesc() {
        return desc;
    }

    public List<String> getCategory() {
        return category;
    }

    public List<ImagesEntity> getImages() {
        return images;
    }

    public List<NoticeEntity> getNotice() {
        return notice;
    }

    public List<RefundPolicyEntity> getRefundPolicy() {
        return refundPolicy;
    }

    public List<TrafficInfoEntity> getTrafficInfo() {
        return trafficInfo;
    }

    public static class DescEntity {
        private String title;
        private String summary;
        private String body;

        public void setTitle(String title) {
            this.title = title;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getTitle() {
            return title;
        }

        public String getSummary() {
            return summary;
        }

        public String getBody() {
            return body;
        }
    }

    public static class ImagesEntity {
        private Object caption;
        private Object key;
        private Object bucket;
        private int width;
        private int height;
        private String url;

        public void setCaption(Object caption) {
            this.caption = caption;
        }

        public void setKey(Object key) {
            this.key = key;
        }

        public void setBucket(Object bucket) {
            this.bucket = bucket;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Object getCaption() {
            return caption;
        }

        public Object getKey() {
            return key;
        }

        public Object getBucket() {
            return bucket;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public String getUrl() {
            return url;
        }
    }

    public static class NoticeEntity {
        private String title;
        private String summary;
        private String body;

        public void setTitle(String title) {
            this.title = title;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getTitle() {
            return title;
        }

        public String getSummary() {
            return summary;
        }

        public String getBody() {
            return body;
        }
    }

    public static class RefundPolicyEntity {
        private String title;
        private String summary;
        private String body;

        public void setTitle(String title) {
            this.title = title;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getTitle() {
            return title;
        }

        public String getSummary() {
            return summary;
        }

        public String getBody() {
            return body;
        }
    }

    public static class TrafficInfoEntity {
        private String title;
        private String summary;
        private String body;

        public void setTitle(String title) {
            this.title = title;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getTitle() {
            return title;
        }

        public String getSummary() {
            return summary;
        }

        public String getBody() {
            return body;
        }
    }
}
