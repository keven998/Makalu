package com.aizou.peachtravel.common.api;

import android.text.TextUtils;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.http.HttpManager;
import com.aizou.core.http.entity.PTHeader;
import com.aizou.core.http.entity.PTRequest;
import com.aizou.core.http.entity.PTRequestHandler;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.GsonTools;
import com.aizou.peachtravel.config.SystemConfig;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Rjm on 2014/11/13.
 */
public class TravelApi extends BaseApi{
    public static class PoiType{
        public final static String SPOT="vs";
        public final static String RESTAURANTS="restaurants";
        public final static String SHOPPING="shopping";
        public final static String HOTEL="hotel";
    }

    //目的地推荐
    public final static String REC_DEST="/recommend";
    public final static String CITY_DETAIL="/geo/localities/";


    //poi相关
    //景点
    public final static String SPOT_DETAIL="/poi/vs/";
    //其他
    public final static String POI_DETAIL="/poi/%1$s/";
    public final static String POI_LIST_BY_LOC="/poi/%1$s/localities/";
    //组装
    public final static String REST_LIST="/poi/restaurants/localities/";
    public final static String REST_DETAIL="/poi/restaurants/";
    public final static String SHOPPING_LIST="/poi/shopping/localities/";
    public final static String SHOPPING_DETAIL="/poi/shopping/";
    //根据ID获取攻略
    public final static String GUIDEBYID="/guides/locality/";

    //根据目的地ID创建攻略
    public final static String CREATE_GUIDE="/create-guide";

    /**
     * 获取目的地推荐
     * @param callback
     * @return
     */
    public static PTRequestHandler getRecDest(HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + REC_DEST);
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }

    /**
     * 获取目的地详情
     * @param callback
     * @return
     */
    public static PTRequestHandler getCityDetail(String id,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + CITY_DETAIL+id);
        request.putUrlParams("noteCnt","3");
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }

    /**
     * 获取景点详情
     * @param callback
     * @return
     */
    public static PTRequestHandler getSpotDetail(String id,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + SPOT_DETAIL+id);
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }
    /**
     * 获取POI列表
     * @param callback
     * @return
     */
    public static PTRequestHandler getPoiListByLoc(String type,String id,int page,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + String.format(POI_LIST_BY_LOC,type)+id);
        request.putUrlParams("page",page+"");
        request.putUrlParams("pageSize",PAGE_SIZE+"");
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }
    /**
     * 获取POI详情
     * @param callback
     * @return
     */
    public static PTRequestHandler getPoiDetail(String type,String id,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + String.format(POI_DETAIL,type)+id);
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }

    /**
     * 获取美食列表
     * @param callback
     * @return
     */
    public static PTRequestHandler getRESTList(String id,int page,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + REST_LIST+id);
        request.putUrlParams("page",page+"");
        request.putUrlParams("pageSize","10");
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }


    /**
     * 获取美食详情
     * @param callback
     * @return
     */
    public static PTRequestHandler getRESTDetail(String id,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + REST_DETAIL+id);
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }
    /**
     * 获取购物列表
     * @param callback
     * @return
     */
    public static PTRequestHandler getShoppingList(String id,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + SHOPPING_LIST+id);
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }
    /**
     * 获取购物详情
     * @param callback
     * @return
     */
    public static PTRequestHandler getShoppingDetail(String id,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + SHOPPING_DETAIL+id);
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }

    /**
     * 根据攻略ID获取攻略详情
     * @param callback
     * @return
     */
    public static PTRequestHandler getGuideDetail(String id,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + GUIDEBYID+id);
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }

    public static PTRequestHandler createGuide
            (List<String> locList, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + CREATE_GUIDE);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            for(String locId:locList){
                jsonArray.put(locId);
            }
            jsonObject.put("locId", jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            StringEntity entity = new StringEntity(jsonObject.toString());
            request.setBodyEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LogUtil.d(jsonObject.toString());

        return HttpManager.request(request, callback);
    }
}
