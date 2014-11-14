package com.aizou.peachtravel.bean;

import java.util.List;

/**
 * Created by Rjm on 2014/11/13.
 */
public class RecDestBean {
    public RecDestType type;
    public List<RecDestItem> localities;
    public class RecDestType{
        public String id;
        public String name;
    }

    public class RecDestItem{
        public String id;
        public String zhName;
        public String enName;
        public int linkType;
        public String linkUrl;
        public String cover;
        public int weight;
        public String desc;
    }
}
