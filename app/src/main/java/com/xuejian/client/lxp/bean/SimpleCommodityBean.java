package com.xuejian.client.lxp.bean;

import java.util.List;

/**
 * Created by yibiao.qin on 2015/11/26.
 */
public class SimpleCommodityBean {

    /**
     * commodityId : 12477930216
     * title : 拉斯维加斯《蓝人秀Blue Man Group》
     * marketPrice : 100.0999984741211
     * price : 0.0
     * rating : 0.99
     * salesVolume : 100
     * seller : {"sellerId":100012,"name":"天意小馆","user":{"userId":100012,"nickname":"","avatar":null},"qualifications":[],"rating":0,"cover":null}
     * locality : null
     * images : [{"url":"http://7sbm17.com1.z0.glb.clouddn.com/avatar/436074aa3952b00f2e6757b4f3ae81fc"}]
     * cover : {"url":"http://7sbm17.com1.z0.glb.clouddn.com/avatar/436074aa3952b00f2e6757b4f3ae81fc"}
     */

    private long commodityId;
    private String title;
    private double marketPrice;
    private double price;
    private double rating;
    private int salesVolume;
    /**
     * sellerId : 100012
     * name : 天意小馆
     * user : {"userId":100012,"nickname":"","avatar":null}
     * qualifications : []
     * rating : 0.0
     * cover : null
     */

    private SellerEntity seller;
    private Object locality;
    /**
     * url : http://7sbm17.com1.z0.glb.clouddn.com/avatar/436074aa3952b00f2e6757b4f3ae81fc
     */

    private CoverEntity cover;
    /**
     * url : http://7sbm17.com1.z0.glb.clouddn.com/avatar/436074aa3952b00f2e6757b4f3ae81fc
     */

    private List<ImagesEntity> images;

    public void setCommodityId(long commodityId) {
        this.commodityId = commodityId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setSalesVolume(int salesVolume) {
        this.salesVolume = salesVolume;
    }

    public void setSeller(SellerEntity seller) {
        this.seller = seller;
    }

    public void setLocality(Object locality) {
        this.locality = locality;
    }

    public void setCover(CoverEntity cover) {
        this.cover = cover;
    }

    public void setImages(List<ImagesEntity> images) {
        this.images = images;
    }

    public long getCommodityId() {
        return commodityId;
    }

    public String getTitle() {
        return title;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public double getPrice() {
        return price;
    }

    public double getRating() {
        return rating;
    }

    public int getSalesVolume() {
        return salesVolume;
    }

    public SellerEntity getSeller() {
        return seller;
    }

    public Object getLocality() {
        return locality;
    }

    public CoverEntity getCover() {
        return cover;
    }

    public List<ImagesEntity> getImages() {
        return images;
    }

    public static class SellerEntity {
        private int sellerId;
        private String name;
        /**
         * userId : 100012
         * nickname :
         * avatar : null
         */

        private UserEntity user;
        private double rating;
        private Object cover;
        private List<?> qualifications;

        public void setSellerId(int sellerId) {
            this.sellerId = sellerId;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setUser(UserEntity user) {
            this.user = user;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        public void setCover(Object cover) {
            this.cover = cover;
        }

        public void setQualifications(List<?> qualifications) {
            this.qualifications = qualifications;
        }

        public int getSellerId() {
            return sellerId;
        }

        public String getName() {
            return name;
        }

        public UserEntity getUser() {
            return user;
        }

        public double getRating() {
            return rating;
        }

        public Object getCover() {
            return cover;
        }

        public List<?> getQualifications() {
            return qualifications;
        }

        public static class UserEntity {
            private int userId;
            private String nickname;
            private Object avatar;

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public void setAvatar(Object avatar) {
                this.avatar = avatar;
            }

            public int getUserId() {
                return userId;
            }

            public String getNickname() {
                return nickname;
            }

            public Object getAvatar() {
                return avatar;
            }
        }
    }

    public static class CoverEntity {
        private String url;

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

    public static class ImagesEntity {
        private String url;

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }
}
