package com.aizou.peachtravel.common.account;

import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.bean.StrategyBean;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Rjm on 2014/12/20.
 */
public class StrategyManager {
    private static StrategyManager instance;
    public static StrategyManager getInstance() {
        if (instance == null) {
            instance = new StrategyManager();
        }
        return instance;
    }

    public void cacheFirstPageStrategy(){

    }

    public void getSaveGuideJson(ArrayList<ArrayList<PoiDetailBean>> routeDayMap){
        JSONObject rootJson = new JSONObject();
        for(ArrayList<PoiDetailBean> dayList:routeDayMap){

        }

    }


}
