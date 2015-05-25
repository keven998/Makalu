package com.aizou.peachtravel.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lxp_dqm07 on 2015/4/21.
 */
public class ExpertBean {
    public String id;
    public String easemobUser;
    public String nickName;
    public String avatar;
    public String avatarSmall;
    public String gender;
    public String signature;
    public int userId;
    public int level;
    public String travelStatus;
    public String residence;
    public String birthday;
    public String zodiac;
    public String memo;
    public String[] roles;
    public Map<String,ArrayList<LocBean>> tracks;


    public String getRolesDescription() {
        if (roles == null || roles.length == 0) return "";
        if ("expert".equals(roles[0])) {
            return "达";
        }
        return "";
    }

    public String getTraceDescription() {
        if (tracks == null) return "";
        int cityCount = 0;
        int coutryCount = 0;
        for (Map.Entry<String, ArrayList<LocBean>> entry : tracks.entrySet()) {
            coutryCount++;
            cityCount += entry.getValue().size();
        }
        return String.format("%d国 %d个城市", coutryCount, cityCount);
    }

}


