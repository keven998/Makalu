package com.xuejian.client.lxp.bean;

import java.util.ArrayList;

/**
 * Created by yibiao.qin on 2015/7/20.
 */
public class CountryWithExpertsBean {
    public String id;
    public String code;
    public String zhName;
    public String enName;
    public ArrayList<ImageBean> images = new ArrayList<ImageBean>();
    public ArrayList<LocBean> destinations = new ArrayList<LocBean>();
    public ContinentsBean continents;
    public int expertCnt;
    public int rank;

}
