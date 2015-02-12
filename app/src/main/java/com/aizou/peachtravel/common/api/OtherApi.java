package com.aizou.peachtravel.common.api;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.http.HttpManager;
import com.aizou.core.http.entity.PTHeader;
import com.aizou.core.http.entity.PTRequest;
import com.aizou.core.http.entity.PTRequestHandler;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.config.SystemConfig;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Rjm on 2014/11/10.
 */
public class OtherApi extends BaseApi {

    public static class Scenario{
         public final static String PORTRAIT="portrait";
    }
    //封面图片
    public final static String COVER_STORY="/misc/cover-stories";
    //获取上传token;
    public final static String UPLOAD_TOKEN="/misc/put-policy/";
    //游记搜索
    public final static String TRAVEL_NOTES="/travel-notes/search";
    //收藏
    public final static String FAV="/misc/favorites";
    //运营
    public final static String OPERATE="/columns";
    //反馈
    public final static String FEEDBACK="/misc/feedback";
    //检查更新
    public final static String UPDATE="/misc/updates";

    /**
     * 获取封面故事
     * @param callback
     * @return
     */
    public static PTRequestHandler getCoverStory(HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + COVER_STORY);
        request.putUrlParams("width", LocalDisplay.SCREEN_WIDTH_PIXELS+"");
        request.putUrlParams("height",LocalDisplay.SCREEN_HEIGHT_PIXELS+"");
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }

    public static PTRequestHandler getOperate(HttpCallBack callback){
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + OPERATE);
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }

    /**
     * 获取上传图片token
     * @param callback
     * @param scenario
     * @return
     */
    public static PTRequestHandler getUploadToken(String scenario,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + UPLOAD_TOKEN+scenario);
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }

    /**
     * 获取头像上传图片token
     * @param callback
     * @return
     */
    public static PTRequestHandler getAvatarUploadToken(HttpCallBack callback) {
       return getUploadToken(Scenario.PORTRAIT,callback);
    }

    /**
     * 根据城市获取游记
     * @param callback
     * @return
     */
    public static PTRequestHandler getTravelNoteByLocId(String locId,int page,int pageSize,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + TRAVEL_NOTES);
        request.putUrlParams("locId", locId);
        request.putUrlParams("page", String.valueOf(page));
        request.putUrlParams("pageSize", String.valueOf(pageSize));
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(100)+"");
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }

    /**
     * 根据关键字获取游记
     * @param callback
     * @return
     */
    public static PTRequestHandler getTravelNoteByKeyword(String keyword,int page,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + TRAVEL_NOTES);
        request.putUrlParams("keyWord", keyword);
        request.putUrlParams("page", String.valueOf(page));
        request.putUrlParams("pageSize", String.valueOf(PAGE_SIZE));
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(100)+"");
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }

    /**
     * 获取收藏列表
     * @param page
     * @param callback
     * @return
     */
    public static PTRequestHandler getFavist(String type,int page, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + FAV);
        request.putUrlParams("faType", type);
        request.putUrlParams("page", String.valueOf(page));
        request.putUrlParams("pageSize", String.valueOf(PAGE_SIZE));
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
    public static PTRequestHandler deleteFav(String id, HttpCallBack callBack){
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.DELETE);
        request.setUrl(SystemConfig.BASE_URL + FAV+"/"+id);
        setDefaultParams(request);
        return HttpManager.request(request, callBack);
    }

    /**
     * 添加收藏
     * @param id
     * @param callBack
     * @return
     */
    public static PTRequestHandler addFav(String id, String type, HttpCallBack callBack){
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + FAV);
        setDefaultParams(request);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("itemId", id);
            jsonObject.put("type", type);

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
        return HttpManager.request(request, callBack);
    }

    /**
     * 反馈信息
     * @param content
     * @param callBack
     * @return
     */
    public static PTRequestHandler feedback(String content,HttpCallBack callBack){
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + FEEDBACK);
        setDefaultParams(request);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("body", content);

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
        return HttpManager.request(request, callBack);
    }

    /**
     * 检查更新
     * @param callBack
     * @return
     */
    public static PTRequestHandler checkUpdate( HttpCallBack callBack){
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + UPDATE);
        setDefaultParams(request);
        return HttpManager.request(request, callBack);
    }


}
