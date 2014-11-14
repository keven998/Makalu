package com.aizou.peachtravel.common.api;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.http.HttpManager;
import com.aizou.core.http.entity.PTHeader;
import com.aizou.core.http.entity.PTRequest;
import com.aizou.core.http.entity.PTRequestHandler;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.config.SystemConfig;

/**
 * Created by Rjm on 2014/11/10.
 */
public class OtherApi extends BaseApi {

    public static class Scenario{
         public final static String PORTRAIT="portrait";
    }

    public final static String COVER_STORY="/misc/cover-stories";
    public final static String UPLOAD_TOKEN="/misc/put-policy/";


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


}
