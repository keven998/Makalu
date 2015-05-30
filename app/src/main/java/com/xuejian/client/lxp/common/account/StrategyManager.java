package com.xuejian.client.lxp.common.account;

import android.content.Context;

import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.PeachUser;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.StrategyBean;

import org.json.JSONArray;
import org.json.JSONException;
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

    public void cacheFirstPageStrategy() {

    }

    public static String putItineraryJson(Context context,JSONObject rootJson, StrategyBean strategy, ArrayList<ArrayList<PoiDetailBean>> routeDayMap) {
        try {
            int i = 0;
            JSONArray itineraryArray = new JSONArray();
            for (ArrayList<PoiDetailBean> dayList : routeDayMap) {
                JSONObject indexPoiObject;
                JSONObject poiObject;
                for (PoiDetailBean poiDetailBean : dayList) {
                    indexPoiObject = new JSONObject();
                    indexPoiObject.put("dayIndex", i);
                    poiObject = new JSONObject();
                    poiObject.put("type", poiDetailBean.type);
                    poiObject.put("id", poiDetailBean.id);
                    indexPoiObject.put("poi", poiObject);
                    itineraryArray.put(indexPoiObject);
                }
                i++;
            }
            rootJson.put("itineraryDays", strategy.itineraryDays);
            rootJson.put("itinerary", itineraryArray);
            return rootJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String putRestaurantJson(Context context,JSONObject rootJson, StrategyBean strategy) {
        try {
            JSONArray restaurantArray = new JSONArray();
            JSONObject poiObject;
            for (PoiDetailBean poiDetailBean : strategy.restaurant) {
                poiObject = new JSONObject();
                poiObject.put("type", poiDetailBean.type);
                poiObject.put("id", poiDetailBean.id);
                restaurantArray.put(poiObject);
            }
            rootJson.put("restaurant", restaurantArray);
            return rootJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }
    public static String putShoppingJson(Context context,JSONObject rootJson, StrategyBean strategy) {
        try {
            JSONArray shoppingArray = new JSONArray();
            JSONObject poiObject;
            for (PoiDetailBean poiDetailBean : strategy.shopping) {
                poiObject = new JSONObject();
                poiObject.put("type", poiDetailBean.type);
                poiObject.put("id", poiDetailBean.id);
                shoppingArray.put(poiObject);
            }
            rootJson.put("shopping", shoppingArray);
            return rootJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void putSaveGuideBaseInfo(JSONObject rootJson, Context context, StrategyBean strategy){

        try {
            rootJson.put("id", strategy.id);
            rootJson.put("title", strategy.title);
            PeachUser user = AccountManager.getInstance().getLoginAccount(context);
            rootJson.put("userId", user.userId);
            rootJson.put("itineraryDays", strategy.itineraryDays);
            JSONArray locArray = new JSONArray();
            JSONObject locObject;
            for (LocBean loc : strategy.localities) {
                locObject = new JSONObject();
                locObject.put("id", loc.id);
                locObject.put("zhName", loc.zhName);
                locObject.put("enName", loc.enName);
                locArray.put(locObject);
            }
            rootJson.put("localities", locArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
