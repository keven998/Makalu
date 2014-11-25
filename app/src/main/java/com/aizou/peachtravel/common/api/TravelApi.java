package com.aizou.peachtravel.common.api;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.http.HttpManager;
import com.aizou.core.http.entity.PTRequest;
import com.aizou.core.http.entity.PTRequestHandler;
import com.aizou.peachtravel.config.SystemConfig;

/**
 * Created by Rjm on 2014/11/13.
 */
public class TravelApi extends BaseApi{
    //目的地推荐
    public final static String REC_DEST="/recommend";
    public final static String CITY_DETAIL="/geo/localities/";
    public final static String SPOT_DETAIL="/poi/vs/";
    public final static String REST_LIST="/poi/restaurants/localities/";
    public final static String REST_DETAIL="/poi/restaurants/";
    public final static String SHOPPING_LIST="/poi/shopping/localities/";
    public final static String SHOPPING_DETAIL="/poi/shopping/";

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
     * 获取城市详情
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
     * 获取美食列表
     * @param callback
     * @return
     */
    public static PTRequestHandler getRESTList(String id,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + REST_LIST+id);
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
}
