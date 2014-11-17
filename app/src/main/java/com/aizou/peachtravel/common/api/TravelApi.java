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
}
