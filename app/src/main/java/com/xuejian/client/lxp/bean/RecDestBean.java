package com.xuejian.client.lxp.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/11/13.
 */
public class RecDestBean {
    public String title;
    public List<RecDestItem> contents = new ArrayList<>();

    public class RecDestItem {
        public String id;
        public String itemType;
        public String itemId;
        public String title;
        public String linkType;
        public String linkUrl;
        public String cover;
        public String desc;
    }
}
