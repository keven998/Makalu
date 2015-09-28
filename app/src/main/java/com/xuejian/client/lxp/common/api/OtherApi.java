package com.xuejian.client.lxp.common.api;

import android.util.Log;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.http.OkHttpClientManager;
import com.aizou.core.http.entity.PTHeader;
import com.aizou.core.http.entity.PTRequest;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.LocalDisplay;
import com.xuejian.client.lxp.config.SystemConfig;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Rjm on 2014/11/10.
 */
public class OtherApi extends BaseApi {

    public static class Scenario {
        public final static String PORTRAIT = "portrait";
        public final static String ALBUM = "album";
    }

    //封面图片
    public final static String COVER_STORY = "/misc/cover-stories";
    //获取上传token;
     //public final static String UPLOAD_TOKEN = "/misc/put-policy/";
    public final static String UPLOAD_TOKEN = "/misc/upload-tokens/";
    //游记搜索
    public final static String TRAVEL_NOTES = "/travel-notes/search";
 //   public final static String TRAVEL_NOTES = "/travelnotes";
 public final static String TRAVEL_NOTES_Key = "/travelnotes";
    //收藏
    public final static String FAV = "/misc/favorites";
    //运营
    public final static String OPERATE = "/columns";
    //反馈
    public final static String FEEDBACK = "/misc/feedback";
    //检查更新
    public final static String UPDATE = "/misc/updates";

    /**
     * 获取封面故事
     *
     * @param callback
     * @return
     */
    public static void getCoverStory(HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + COVER_STORY);
        request.putUrlParams("width", LocalDisplay.SCREEN_WIDTH_PIXELS + "");
        request.putUrlParams("height", LocalDisplay.SCREEN_HEIGHT_PIXELS + "");
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, "", callback);
   //     return HttpManager.request(request, callback);
    }

    public static void getOperate(HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + OPERATE);
        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, "", callback);
     //   return HttpManager.request(request, callback);
    }

    /**
     * 获取上传图片token
     *
     * @param callback
     * @param scenario
     * @return
     */
    public static void getUploadToken(String scenario,String info,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);

        StringBuffer sb = new StringBuffer();
        sb.append(SystemConfig.DEV_URL + UPLOAD_TOKEN + scenario);
        if(info!=null){
            sb.append("?caption="+info);
        }
        request.setUrl(sb.toString());
     //   request.setUrl(SystemConfig.DEV_URL + UPLOAD_TOKEN );

//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("scenario", scenario);
//            jsonObject.put("userId", AccountManager.getCurrentUserId());
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        try {
//            StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");
//            request.setBodyEntity(entity);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        LogUtil.d(jsonObject.toString());

        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, "", callback);
     //   return HttpManager.request(request, callback);
    }

    /**
     * 获取头像上传图片token
     *
     * @param callback
     * @return
     */
    public static void getAvatarUploadToken(HttpCallBack callback) {
         getUploadToken(Scenario.PORTRAIT,null, callback);
    }


    public static void getAvatarAlbumUploadToken(HttpCallBack callback) {
         getUploadToken(Scenario.ALBUM,null, callback);
    }

    public static void getAvatarAlbumUploadToken(HttpCallBack callback,String info) {
        getUploadToken(Scenario.ALBUM,info,callback);
    }
    /**
     * 根据城市获取游记
     *
     * @param callback
     * @return
     */
    public static void getTravelNoteByLocId(String locId, int page, int pageSize, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + TRAVEL_NOTES);
        request.putUrlParams("locId", locId);
        request.putUrlParams("page", String.valueOf(page));
        request.putUrlParams("pageSize", String.valueOf(pageSize));
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(100) + "");
        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, "", callback);
     //   return HttpManager.request(request, callback);
    }

    /**
     * 根据关键字获取游记
     *
     * @param callback
     * @return
     */
    public static void getTravelNoteByKeyword(String keyword, int page, int pageSize, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + TRAVEL_NOTES_Key);
        request.putUrlParams("query", keyword);
        request.putUrlParams("sortby", "posttime");
        request.putUrlParams("sort", "desc");
        request.putUrlParams("page", String.valueOf(page));
        request.putUrlParams("pageSize", String.valueOf(pageSize));
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(100) + "");
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, "", callback);
     //   return HttpManager.request(request, callback);
    }

    /**
     * 获取收藏列表
     *
     * @param page
     * @param callback
     * @return
     */
    public static void getFavist(String type, int page, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + FAV);
        request.putUrlParams("faType", type);
        request.putUrlParams("page", String.valueOf(page));
        request.putUrlParams("pageSize", String.valueOf(PAGE_SIZE));
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(100) + "");
        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, "", callback);
       // return HttpManager.request(request, callback);
    }

    /**
     * 删除收藏
     *
     * @param id
     * @param callBack
     * @return
     */
    public static void deleteFav(String id, HttpCallBack callBack) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.DELETE);
        request.setUrl(SystemConfig.BASE_URL + FAV + "/" + id);
        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, "", callBack);
      //  return HttpManager.request(request, callBack);
    }

    /**
     * 添加收藏
     *
     * @param id
     * @param callBack
     * @return
     */
    public static void addFav(String id, String type, HttpCallBack callBack) {
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
            StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");
            request.setBodyEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LogUtil.d(jsonObject.toString());
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callBack);
      //  return HttpManager.request(request, callBack);
    }

    /**
     * 反馈信息
     *
     * @param content
     * @param callBack
     * @return
     */
    public static void feedback(String content, HttpCallBack callBack) {
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
            StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");
            request.setBodyEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LogUtil.d(jsonObject.toString());
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callBack);
     //   return HttpManager.request(request, callBack);
    }

    /**
     * 检查更新
     *
     * @param callBack
     * @return
     */
    public static void checkUpdate(HttpCallBack callBack) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + UPDATE);
        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, "", callBack);
      //  return HttpManager.request(request, callBack);
    }


}
