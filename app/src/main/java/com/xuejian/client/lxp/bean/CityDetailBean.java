package com.xuejian.client.lxp.bean;

import java.util.List;

/**
 * Created by yibiao.qin on 2016/5/14.
 */
public class CityDetailBean {

    /**
     * id : 546f2da8b8ce0440eddb28e0
     * zhName : 东京
     * enName : Tokyo
     * desc : 东京（Tokyo）是日本国的首都，是亚洲第一大城市，世界第二大城市。全球最大的经济中心之一。
     东京的著名观光景点有东京铁塔、皇居、国会议事堂、浅草寺、浜离宫、上野公园与动物园、葛西临海公园、台场与彩虹大桥、东京迪士尼乐园、代代木公园、日比谷公园、新宿御苑、幕张奥特莱斯（outlets）、奥多摩湖、Hello kitty 乐园、明治神宫、忍野八海、池袋、上野公园、东映动漫Gallery、涩谷、升仙峡、丰田汽车会馆、筑地市场、千鸟之渊、秋叶原、二重桥、隅田公园、滨离宫庭园 Tsukiji鱼市等。
     比较有特色的比赛有棒球和相扑，看棒球可以到后乐园站的东京球场，那里是东京巨人队的主场。看相扑可以到秋叶原附近的两国去，那里既有两国国技馆，还有许多相扑选手所属的部屋（俱乐部），每个部屋都有自己的名号与标志。江户东京博物馆也值得一看。
     山手线上的几个大站的附近地区，也是游玩和观光的好地方，比如池袋附近的太阳城（Sunshine City)里集中了许多水族馆、美术馆、博物馆，新宿附近的都厅大厦、歌舞伎町、购物区，涉谷与原宿则是日本年轻人时装、音乐、化妆、随身物品、发式、甚至生活方式的信息源。
     上野附近集中了如日本的传统剧场、东京国立美术馆、国立科学博物馆、国立西洋美术馆、东京都美术馆等。
     在银座和新宿的主街道，到了星期天，禁止车辆通行，那里就成了步行者的天堂，卖艺或公演的街头艺术家、出售各种手工制品的各国小贩、在街上玩耍的年轻人，各色人等、五花八门。
     * travelMonth : 基本上全年都适宜旅行，东京属于海洋性气候，夏季高温多湿、常有台风，冬季气候干燥、多为晴天。
     3-5月是春季。早春时早晚温差比较大，但全天基本比较舒适，风和日丽，是绝好的出游时机。
     6--8月是一年里最热的季节。特别是梅雨刚过去的7月--8月的气温超过30℃，湿度很高，每天都是盛夏的闷热天气。
     9-11月是秋季。9月还会有白天温度30℃以上的盛夏天气，到了10月就会有台风。
     12-2月是冬季，气温偏低。虽然偶尔会下雪，但是市中心不会积雪。
     东京的四月份正值樱花盛开的时节，漫山遍野的樱花铺满了东京的大街小巷，是去东京赏樱花的最佳季节。
     * images : [{"url":"http://images.taozilvxing.com/d42dfcd90bcbbb1ebb0598031eda45fc"},{"url":"http://images.taozilvxing.com/14ac3e08bea20b0bc351ed84803157cb"},{"url":"http://images.taozilvxing.com/62995280160caaef87fadad6a065f53b"},{"url":"http://images.taozilvxing.com/6b267aa6d7818ec19486a0e60e07ba86"},{"url":"http://images.taozilvxing.com/9e313fe72a66db77b70cbabe5de1ac6b"},{"url":"http://images.taozilvxing.com/e3d0823a9534b16453b9bcf47eab2b11"}]
     * remarks : [{"title":"玩家精选","url":"http://www.baidu.com","cover":{"url":"http://7sbm17.com1.z0.glb.clouddn.com/commodity/images/9f3eaf3cad6a15ad1bebfa3a9a349dd0"},"images":null}]
     * imageCnt : 6
     * playGuide : http://h5.taozilvxing.com/city/items.php?tid=546f2da8b8ce0440eddb28e0
     * trafficInfoUrl : http://h5.taozilvxing.com/city/traff-list.php?tid=546f2da8b8ce0440eddb28e0
     * commoditiesCnt : 0
     */

    private String id;
    private String zhName;
    private String enName;
    private String desc;
    private String travelMonth;
    private int imageCnt;
    private String playGuide;
    private String trafficInfoUrl;
    private int commoditiesCnt;
    /**
     * url : http://images.taozilvxing.com/d42dfcd90bcbbb1ebb0598031eda45fc
     */

    private List<ImagesEntity> images;
    /**
     * title : 玩家精选
     * url : http://www.baidu.com
     * cover : {"url":"http://7sbm17.com1.z0.glb.clouddn.com/commodity/images/9f3eaf3cad6a15ad1bebfa3a9a349dd0"}
     * images : null
     */

    private List<Remarks> remarks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZhName() {
        return zhName;
    }

    public void setZhName(String zhName) {
        this.zhName = zhName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTravelMonth() {
        return travelMonth;
    }

    public void setTravelMonth(String travelMonth) {
        this.travelMonth = travelMonth;
    }

    public int getImageCnt() {
        return imageCnt;
    }

    public void setImageCnt(int imageCnt) {
        this.imageCnt = imageCnt;
    }

    public String getPlayGuide() {
        return playGuide;
    }

    public void setPlayGuide(String playGuide) {
        this.playGuide = playGuide;
    }

    public String getTrafficInfoUrl() {
        return trafficInfoUrl;
    }

    public void setTrafficInfoUrl(String trafficInfoUrl) {
        this.trafficInfoUrl = trafficInfoUrl;
    }

    public int getCommoditiesCnt() {
        return commoditiesCnt;
    }

    public void setCommoditiesCnt(int commoditiesCnt) {
        this.commoditiesCnt = commoditiesCnt;
    }

    public List<ImagesEntity> getImages() {
        return images;
    }

    public void setImages(List<ImagesEntity> images) {
        this.images = images;
    }

    public List<Remarks> getRemarks() {
        return remarks;
    }

    public void setRemarks(List<Remarks> remarks) {
        this.remarks = remarks;
    }

    public static class ImagesEntity {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class RemarksEntity {
        private String title;
        private String url;
        /**
         * url : http://7sbm17.com1.z0.glb.clouddn.com/commodity/images/9f3eaf3cad6a15ad1bebfa3a9a349dd0
         */

        private CoverEntity cover;
        private Object images;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public CoverEntity getCover() {
            return cover;
        }

        public void setCover(CoverEntity cover) {
            this.cover = cover;
        }

        public Object getImages() {
            return images;
        }

        public void setImages(Object images) {
            this.images = images;
        }

        public static class CoverEntity {
            private String url;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
