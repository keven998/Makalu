package com.xuejian.client.lxp.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yibiao.qin on 2015/11/23.
 */
public class ColumnBean {

    /**
     * columnType : slide   运营位
     * columnType : special  专题
     * columns :  内容
     * */

    private String columnType;

    private ArrayList<ColumnsEntity> columns;

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public void setColumns(ArrayList<ColumnsEntity> columns) {
        this.columns = columns;
    }

    public String getColumnType() {
        return columnType;
    }

    public ArrayList<ColumnsEntity> getColumns() {
        return columns;
    }

    public static class ColumnsEntity {
        private String title;
        private String link;

        private List<ImageBean> images;

        public void setTitle(String title) {
            this.title = title;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public void setImages(List<ImageBean> images) {
            this.images = images;
        }

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }

        public List<ImageBean> getImages() {
            return images;
        }

    }
}
