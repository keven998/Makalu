package com.xuejian.client.lxp.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxp_dqm07 on 2015/7/24.
 */
public class TracksBean {

    public String id;
    public String zhName;
    public String enName;
    public String country;
    public List<ImageBean> images = new ArrayList<ImageBean>();
    public LocationBean location;
}
