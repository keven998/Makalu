package com.aizou.peachtravel.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/11/13.
 */
public class RecDestBean {
    public String title;
    public List<RecDestItem> contentsList=new ArrayList<>();

    public class RecDestItem{
        public String id;
        public String zhName;
        public String enName;
        public int linkType;
        public String linkUrl;
        public String cover;
        public String desc;
    }
}
