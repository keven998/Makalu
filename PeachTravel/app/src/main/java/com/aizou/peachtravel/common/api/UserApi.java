package com.aizou.peachtravel.common.api;

import android.text.TextUtils;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.http.HttpManager;
import com.aizou.core.http.entity.PTHeader;
import com.aizou.core.http.entity.PTRequest;
import com.aizou.core.http.entity.PTRequestData;
import com.aizou.core.http.entity.PTRequestHandler;
import com.aizou.core.log.LogUtil;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.config.SystemConfig;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Rjm on 2014/10/11.
 */
public class UserApi extends BaseApi{
    public static class ValidationCode{
//        1：注册
//        2：找回密码
//        3：绑定手机号
        public final static String REG_CODE="1";
        public final static String FIND_PWD="2";
        public final static String BIND_PHONE="3";
    }
    public final static String SEND_VALIDATION="/users/send-validation";
    public final static String AUTH_SIGNUP = "/users/auth-signup";
    public final static String SIGNUP = "/users/signup";
    public final static String USERINFO = "/users/";

    public static PTRequestHandler authSignUp(String code,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + AUTH_SIGNUP);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE,"application/json");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code",code);
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

    public static PTRequestHandler sendValidation
            (String phone,String actionCode,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + SEND_VALIDATION);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE,"application/json");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tel",phone);
            jsonObject.put("actionCode",actionCode);
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
    public static PTRequestHandler signUp(String phone,String pwd,String captcha,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + SIGNUP);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE,"application/json");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tel",phone);
            jsonObject.put("pwd",pwd);
            jsonObject.put("captcha",captcha);
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

    public static PTRequestHandler getUserInfo(PeachUser user,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + USERINFO+user.userId);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE,"application/json");
        return HttpManager.request(request, callback);
    }
    public static PTRequestHandler editUserAvatar(PeachUser user,String avatar,HttpCallBack callBack){
       return editUserInfo(user,avatar,null,null,null,callBack);
    }
    public static PTRequestHandler editUserNickName(PeachUser user,String nickname,HttpCallBack callBack){
        return editUserInfo(user,null,nickname,null,null,callBack);
    }
    public static PTRequestHandler editUserSignature(PeachUser user,String signature,HttpCallBack callBack){
        return editUserInfo(user,null,null,signature,null,callBack);
    }
    public static PTRequestHandler editUserGender(PeachUser user,String gender,HttpCallBack callBack){
        return editUserInfo(user,null,null,null,gender,callBack);
    }


    public static PTRequestHandler editUserInfo(PeachUser user,String avatar,String nickName,String signature,String gender,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + USERINFO+user.userId);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE,"application/json");
        JSONObject jsonObject = new JSONObject();
        try {
            if(!TextUtils.isEmpty(avatar)){
                jsonObject.put("avatar",avatar);
            }
            if(!TextUtils.isEmpty(nickName)){
                jsonObject.put("nickName",nickName);
            }
            if(!TextUtils.isEmpty(signature)){
                jsonObject.put("signature",signature);
            }
            if(!TextUtils.isEmpty(gender)){
                jsonObject.put("gender",gender);
            }

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
