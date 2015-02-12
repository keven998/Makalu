package com.aizou.peachtravel.common.api;

import android.text.TextUtils;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.http.HttpManager;
import com.aizou.core.http.entity.PTHeader;
import com.aizou.core.http.entity.PTRequest;
import com.aizou.core.http.entity.PTRequestHandler;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.GsonTools;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.bean.LocBean;
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
    public static class PeachType {
        public final static String GUIDE="guide";
        public final static String NOTE="travelNote";
        public final static String LOC="locality";
        public final static String SPOT="vs";
        public final static String RESTAURANTS="restaurant";
        public final static String SHOPPING="shopping";
        public final static String HOTEL="hotel";
    }

    //目的地推荐
    public final static String REC_DEST="/recommend";
    //国内目的地列表
    public final static String IN_DESTINATIONS="/geo/localities/domestic";
    //国外目的地列表
    public final static String OUT_DESTINATIONS="/geo/localities/abroad";
    //目的地详情
    public final static String CITY_DETAIL="/geo/localities/";
    //目的地图集
    public final static String CITY_GALLEY="/geo/localities/%1$s/album";
    //目的地美食、购物介绍
    public final static String LOC_POI_GUIDE="/guides/locality/%1$s/%2$s";
    //poi相关
    //景点
    public final static String SPOT_DETAIL="/poi/vs/";
    //POI详情
    public final static String POI_DETAIL="/poi/%1$s/";
    //POI列表
    public final static String POI_LIST_BY_LOC="/poi/%1$s/localities/";
    //根据ID获取攻略
    public final static String GUIDEBYID="/guides/%1$s";
    //根据目的地ID创建攻略
    public final static String CREATE_GUIDE="/create-guide";
    //复制攻略
    public final static String COPY_GUIDE="/copy-guide/%1$s";
    //攻略
    public final static String GUIDE="/guides";
    //修改攻略标题
    public final static String MODIFY_GUIDE_INFO="/guides/info/%1$s";
    //修改攻略目的地
    public final static String MODIFY_GUIDE_LOC="/guides";
    //收藏
    public final static String FAV="/misc/favorites";
    //搜索
    public final static String SEARCH="/search";
    //搜索联想
    public final static String SUGGEST="/suggestions";
    //周边
    public final static String NEARBY="/poi/nearby";




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
     * 获取国内目的地列表
     * @param callback
     * @return
     */
    public static PTRequestHandler getInDestList(String lastModeify,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + IN_DESTINATIONS);
        setDefaultParams(request);
        if(!TextUtils.isEmpty(lastModeify)){
            request.addHeader("Cache-Control","private");
            request.addHeader("If-Modified-Since",lastModeify);
        }
        return HttpManager.request(request, callback);
    }
    /**
     * 获取国外目的地列表
     * @param callback
     * @return
     */
    public static PTRequestHandler getOutDestList(String lastModeify,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + OUT_DESTINATIONS);
        setDefaultParams(request);
        if(!TextUtils.isEmpty(lastModeify)){
            request.addHeader("Cache-Control","private");
            request.addHeader("If-Modified-Since",lastModeify);
        }
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
    public static PTRequestHandler getCityDetail(String id,int imgWidth,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + CITY_DETAIL+id);
        request.putUrlParams("noteCnt","3");
        request.putUrlParams("imgWidth",imgWidth+"");
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }
    /**
     * 获取目的地图集
     * @param callback
     * @return
     */
    public static PTRequestHandler getCityGalley(String id,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + String.format(CITY_GALLEY,id));
        request.putUrlParams("imgWidth", (int)(LocalDisplay.SCREEN_HEIGHT_PIXELS/3/1.5)+"");
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
        request.putUrlParams("page", String.valueOf(page));
        request.putUrlParams("pageSize", String.valueOf(PAGE_SIZE));
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(100)+"");
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
        request.setUrl(SystemConfig.BASE_URL +String.format(GUIDEBYID,id));
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(100)+"");
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
            (List<String> locList,boolean recommend, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + CREATE_GUIDE);
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(100)+"");
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            for(String locId:locList){
                jsonArray.put(locId);
            }
            jsonObject.put("locId", jsonArray);
            if(recommend){
                jsonObject.put("action","recommend");
            }else{
                jsonObject.put("action","");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            StringEntity entity = new StringEntity(jsonObject.toString(),"utf-8");
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
    public static PTRequestHandler saveGuide
            (String id,String guideJson, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + GUIDE);
        request.putUrlParams("id",id);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        try {
            StringEntity entity = new StringEntity(guideJson,"utf-8");
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
        request.putUrlParams("page", String.valueOf(page));
        request.putUrlParams("pageSize", String.valueOf(PAGE_SIZE));
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }

    /**
     * 复制攻略
     * @param callback
     * @return
     */
    public static PTRequestHandler copyStrategy(String id,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + String.format(COPY_GUIDE,id));
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }

    /**
     * 删除攻略
     * @param id
     * @param callBack
     * @return
     */
    public static PTRequestHandler deleteStrategy(String id,HttpCallBack callBack){
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.DELETE);
        request.setUrl(SystemConfig.BASE_URL + GUIDE+"/"+id);
        setDefaultParams(request);
        return HttpManager.request(request, callBack);
    }

    /**
     * 修改攻略名称
     * @param id
     * @param callback
     * @return
     */
    public static PTRequestHandler modifyGuideTitle
    (String id, String title,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.PUT);
        request.setUrl(SystemConfig.BASE_URL + String.format(MODIFY_GUIDE_INFO,id));
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", title);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            StringEntity entity = new StringEntity(jsonObject.toString(),"utf-8");
            request.setBodyEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LogUtil.d(jsonObject.toString());

        return HttpManager.request(request, callback);
    }

    /**
     * 修改攻略目的地
     * @param id
     * @param callback
     * @return
     */
    public static PTRequestHandler modifyGuideLoc
    (String id, List<LocBean> locList,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + MODIFY_GUIDE_LOC);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            JSONArray locArray= new JSONArray();
            JSONObject locObject;
            for(LocBean loc:locList){
                locObject = new JSONObject();
                locObject.put("id",loc.id);
                locObject.put("zhName",loc.zhName);
                locObject.put("enName",loc.enName);
                locArray.put(locObject);
            }
            jsonObject.put("localities",locArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            StringEntity entity = new StringEntity(jsonObject.toString(),"utf-8");
            request.setBodyEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LogUtil.d(jsonObject.toString());

        return HttpManager.request(request, callback);
    }

    /**
     * 获取收藏列表
     * @param page
     * @param callback
     * @return
     */
    public static PTRequestHandler getFavist(int page,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + FAV);
        request.putUrlParams("page",page+"");
        request.putUrlParams("pageSize",PAGE_SIZE+"");
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(100)+"");
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }

    /**
     * 删除收藏
     * @param id
     * @param callBack
     * @return
     */
    public static PTRequestHandler deleteFav(String id,HttpCallBack callBack){
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.DELETE);
        request.setUrl(SystemConfig.BASE_URL + FAV+"/"+id);
        setDefaultParams(request);
        return HttpManager.request(request, callBack);
    }
    //目的地查询
    public static PTRequestHandler searchLoc(String keyword,int page,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + SEARCH);
        request.putUrlParams("keyWord", keyword);
        request.putUrlParams("loc", "true");
        request.putUrlParams("page", String.valueOf(page));
        request.putUrlParams("pageSize", String.valueOf(PAGE_SIZE));
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }
    //目的地联想
    public static PTRequestHandler suggestLoc(String keyword,HttpCallBack callback) {
        return  suggestForType(keyword,"loc",callback);
    }
    //联合查询
    public static PTRequestHandler searchAll(String keyword,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + SEARCH);
        request.putUrlParams("keyWord",keyword);
        request.putUrlParams("loc","true");
        request.putUrlParams("vs","true");
        request.putUrlParams("hotel","true");
        request.putUrlParams("restaurant","true");
        request.putUrlParams("shopping","true");
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(50)+"");
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }
    //搜索地点
    public static PTRequestHandler searchForType(String keyword,String type,String locId,int page,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + SEARCH);
        request.putUrlParams("keyWord", keyword);
        request.putUrlParams(type, "true");
        request.putUrlParams("locId", locId);
        request.putUrlParams("page", String.valueOf(page));
        request.putUrlParams("pageSize", String.valueOf(PAGE_SIZE));
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(100)+"");
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }
    //联想地点
    public static PTRequestHandler suggestForType(String keyword,String type,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + SUGGEST);
        request.putUrlParams("keyWord", keyword);
        request.putUrlParams(type, "true");
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(50)+"");
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }
    //获取周边
    public static PTRequestHandler getNearbyPoi(double lat ,double lng,int page,String type,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + NEARBY);
        request.putUrlParams("lat", String.valueOf(lat));
        request.putUrlParams("lng", String.valueOf(lng));
        request.putUrlParams(type, "true");
        request.putUrlParams("page", String.valueOf(page));
        request.putUrlParams("pageSize", String.valueOf(PAGE_SIZE));
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(100)+"");
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }
}
