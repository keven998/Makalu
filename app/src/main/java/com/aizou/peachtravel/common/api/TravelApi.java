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
        public final static String RESTAURANTS="restaurant";
        public final static String SHOPPING="shopping";
        public final static String HOTEL="hotel";
    }

    //目的地推荐
    public final static String REC_DEST="/recommend";
    public final static String CITY_DETAIL="/geo/localities/";
    //目的地美食、购物介绍
    public final static String LOC_POI_GUIDE="/guides/destination/%1$s/%2$s";


    //poi相关
    //景点
    public final static String SPOT_DETAIL="/poi/vs/";
    //POI详情
    public final static String POI_DETAIL="/poi/%1$s/";
    //POI列表
    public final static String POI_LIST_BY_LOC="/poi/%1$s/localities/";
    //根据ID获取攻略
    public final static String GUIDEBYID="/guides/locality/";
    //根据目的地ID创建攻略
    public final static String CREATE_GUIDE="/create-guide";
    //攻略
    public final static String GUIDE="/guides";




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
     * 获取目的地美食，购物介绍
     * @param callback
     * @return
     */
    public static PTRequestHandler getDestPoiGuide(String locId,String type,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL +String.format(LOC_POI_GUIDE,locId,type) );
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

    /**
     * 新建攻略
     * @param locList
     * @param callback
     * @return
     */
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

    /**
     * 保存攻略
     * @param guideJson
     * @param callback
     * @return
     */
    public static PTRequestHandler saveGUide
            (String guideJson, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.PUT);
        request.setUrl(SystemConfig.BASE_URL + GUIDE);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        try {
            StringEntity entity = new StringEntity(guideJson);
            request.setBodyEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LogUtil.d(guideJson);
        return HttpManager.request(request, callback);
    }

    /**
     * 获取攻略列表
     * @param callback
     * @return
     */
    public static PTRequestHandler getStrategyList(int page,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + GUIDE);
        request.putUrlParams("page",page+"");
        request.putUrlParams("pageSize",PAGE_SIZE+"");
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }

    public static PTRequestHandler deleteStrategy(String id,HttpCallBack callBack){
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.DELETE);
        request.setUrl(SystemConfig.BASE_URL + GUIDE+"/"+id);
        setDefaultParams(request);
        return HttpManager.request(request, callBack);
    }

}
