package com.aizou.peachtravel.common.api;

import android.text.TextUtils;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.http.HttpManager;
import com.aizou.core.http.entity.PTHeader;
import com.aizou.core.http.entity.PTRequest;
import com.aizou.core.http.entity.PTRequestData;
import com.aizou.core.http.entity.PTRequestHandler;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.GsonTools;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.config.SystemConfig;
import com.google.gson.Gson;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Rjm on 2014/10/11.
 */
public class UserApi extends BaseApi {
    public static class ValidationCode {
        //        1：注册
//        2：找回密码
//        3：绑定手机号
        public final static String REG_CODE = "1";
        public final static String FIND_PWD = "2";
        public final static String BIND_PHONE = "3";
    }

    //发送验证码
    public final static String SEND_VALIDATION = "/users/send-validation";
    //验证验证码换取token
    public final static String CHECK_VALIDATION = "/users/check-validation";
    //第三方登录
    public final static String AUTH_SIGNUP = "/users/auth-signup";
    //注册
    public final static String SIGNUP = "/users/signup";
    //登录
    public final static String SIGNIN = "/users/signin";
    //获取个人信息
    public final static String USERINFO = "/users/";
    //绑定手机号
    public final static String BIND_PHONE = "/users/bind";
    //密码
    public final static String MODIFY_PWD = "/users/pwd";
    //重设密码
    public final static String RESET_PWD = "/users/reset-pwd";


    //联系人
    public final static String CONTACTS = "/users/contacts";
    //搜索联系人
    public final static String SEACH_CONTACT="/users/search";

    public final static String GET_CONTACT_BY_HX="/users/easemob";

    public static PTRequestHandler authSignUp(String code, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + AUTH_SIGNUP);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
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
            (String phone, String actionCode, String uid, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + SEND_VALIDATION);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tel", phone);
            jsonObject.put("actionCode", actionCode);
            if (!TextUtils.isEmpty(uid)) {
                jsonObject.put("userId", uid);
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

    public static PTRequestHandler checkValidation
            (String phone, String captcha, String actionCode, String uid, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + CHECK_VALIDATION);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tel", phone);
            jsonObject.put("captcha", captcha);
            jsonObject.put("actionCode", actionCode);
            if (!TextUtils.isEmpty(uid)) {
                jsonObject.put("userId", uid);
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

    public static PTRequestHandler signUp(String phone, String pwd, String captcha, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + SIGNUP);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tel", phone);
            jsonObject.put("pwd", pwd);
            jsonObject.put("captcha", captcha);
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

    public static PTRequestHandler bindPhone(String phone, String uid, String pwd, String token, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + BIND_PHONE);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tel", phone);
            jsonObject.put("userId", uid);
            jsonObject.put("token", token);
            if (!TextUtils.isEmpty(pwd)) {
                jsonObject.put("pwd", pwd);
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

    public static PTRequestHandler resetPwd(String tel, String pwd, String token, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + RESET_PWD);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tel", tel);
            jsonObject.put("token", token);
            jsonObject.put("pwd", pwd);
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

    public static PTRequestHandler modifyPwd(String oldPwd, String newPwd, String uid, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + MODIFY_PWD);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("oldPwd", oldPwd);
            jsonObject.put("newPwd", newPwd);
            jsonObject.put("userId", uid);
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

    public static PTRequestHandler signIn(String loginName, String pwd, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + SIGNIN);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("loginName", loginName);
            jsonObject.put("pwd", pwd);
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

    public static PTRequestHandler getUserInfo(PeachUser user, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + USERINFO + user.userId);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }

    public static PTRequestHandler editUserAvatar(PeachUser user, String avatar, HttpCallBack callBack) {
        return editUserInfo(user, avatar, null, null, null, callBack);
    }

    public static PTRequestHandler editUserNickName(PeachUser user, String nickname, HttpCallBack callBack) {
        return editUserInfo(user, null, nickname, null, null, callBack);
    }

    public static PTRequestHandler editUserSignature(PeachUser user, String signature, HttpCallBack callBack) {
        return editUserInfo(user, null, null, signature, null, callBack);
    }

    public static PTRequestHandler editUserGender(PeachUser user, String gender, HttpCallBack callBack) {
        return editUserInfo(user, null, null, null, gender, callBack);
    }


    public static PTRequestHandler editUserInfo(PeachUser user, String avatar, String nickName, String signature, String gender, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + USERINFO + user.userId);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(avatar)) {
                jsonObject.put("avatar", avatar);
            }
            if (!TextUtils.isEmpty(nickName)) {
                jsonObject.put("nickName", nickName);
            }
            if (!TextUtils.isEmpty(signature)) {
                jsonObject.put("signature", signature);
            }
            if (!TextUtils.isEmpty(gender)) {
                jsonObject.put("gender", gender);
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

    /**
     * 获取好友列表
     * @param callback
     * @return
     */
    public static PTRequestHandler getContact(HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + CONTACTS);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }

    /**
     * 添加好友
     * @param uid
     * @param callback
     * @return
     */
    public static PTRequestHandler addContact(String uid, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + CONTACTS);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", uid);
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
     * 删除好友
     * @param uid
     * @param callback
     * @return
     */
    public static PTRequestHandler deleteContact(String uid,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.DELETE);
        request.setUrl(SystemConfig.BASE_URL + CONTACTS+"/"+uid);
//        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }

    /**
     * 搜索联系人
     * @param key
     * @param callback
     * @return
     */

    public static PTRequestHandler seachContact(String key,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + SEACH_CONTACT);
        request.putUrlParams("keyword",key);
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }

    /**
     * 根据环信ID获取联系人
     * @param users
     * @param callback
     * @return
     */

    public static PTRequestHandler getContactByHx(List<String> users,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.BASE_URL + GET_CONTACT_BY_HX);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("easemob", GsonTools.createGsonString(users));
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
