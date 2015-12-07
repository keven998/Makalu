package com.xuejian.client.lxp.bean;

/**
 * Created by yibiao.qin on 2015/12/7.
 */
public class TradeMessageBean {
    /**
     * title : 消息的标题
     * text : 消息的正文
     * commodityName : 商品名称
     * orderId : 2015123412834123
     * uri : lvxingpai://marketplace/orders/2015123412834123
     */

    private String title;
    private String text;
    private String commodityName;
    private long orderId;
    private String uri;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getUri() {
        return uri;
    }
}
