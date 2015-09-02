package com.xuejian.client.lxp.common.api;

import android.text.TextUtils;

import com.aizou.core.http.GzipCompressingEntity;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.http.OkHttpClientManager;
import com.aizou.core.http.entity.PTHeader;
import com.aizou.core.http.entity.PTRequest;
import com.aizou.core.log.LogUtil;
import com.xuejian.client.lxp.bean.AddressBookbean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.config.SystemConfig;
import com.xuejian.client.lxp.db.User;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
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

    // 发送验证码
//    public final static String SEND_VALIDATION = "/users/send-validation";
    public final static String SEND_VALIDATION = "/users/validation-codes";

    //验证验证码换取token
    // public final static String CHECK_VALIDATION = "/users/check-validation";
    public final static String CHECK_VALIDATION = "/users/tokens";


    //第三方登录
    //public final static String AUTH_SIGNUP = "/users/auth-signup";
    // public final static String AUTH_SIGNUP = "/users/auth-signup";

    //  注册
    // public final static String SIGNUP = "/users/signup";
    public final static String SIGNUP = "/users";
    //登录
    public final static String SIGNIN = "/users/signin";
    //获取个人信息
    public final static String USERINFO = "/users/";
    //绑定手机号
    public final static String BIND_PHONE = "/users/%s/tel";
    //密码
    public final static String MODIFY_PWD = "/users/%s/password";
    //重设密码
    public final static String RESET_PWD = "/users/reset-pwd";


    //联系人
//    public final static String CONTACTS = "/users/contacts";
    public final static String CONTACTS = "/users/%s/contacts";
    //请求添加好友
    public final static String REQUEST_ADD_CONTACTS = "/users/request-contacts";
    //搜索联系人
    public final static String SEACH_CONTACT = "/users/search";
    public final static String SEACH_CONTACT_BY_TEL = "/users?query=";
    public final static String SEACH_CONTACT_BY_NICKNAME = "/users?nickName=";
    // 获取相册
    public final static String ALBUMS = "/users/%s/albums";
    //搜索达人足迹
    public final static String SEARCH_EXPERT_FOOTPRINT = "/users/expert/tracks";
    //通讯录匹配
    //  public final static String SEARCH_BY_ADDRESSBOOK = "/users/search-by-address-book";
    public final static String SEARCH_BY_ADDRESSBOOK = "/users/%s/match";
    //根据足迹获取达人
    public final static String EXPERT_BY_TRACK = "/geo/countries/%s/expert";
    public final static String LOGOUT = "/users/logout";
    //    //根据足迹获取达人
//    public final static String EXPERT_BY_TRACK = "/users/expert/tracks/users";
//    /app/users/:id/memo
    //消息免打扰
    public final static String MUTE_CONVERSATION = "/users/%s/conversations/";
    //获取足迹
    public final static String TRACKS = "/users/%s/footprints";
    //用户喜欢
    public final static String LIKE = "/users/%s/likes";
    public final static String VOTE = "/geo/localities/%s/votes";

    public final static String EXPERT_REQUEST = "/users/%s/expert-requests";
    public final static String BLOCK = "/users/%s/blacklist";

    public static void authSignUp(String code, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + SIGNIN);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("authCode", code);
            jsonObject.put("provider", "weixin");
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
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        //   return HttpManager.request(request, callback);
    }

    public static void sendValidation
            (String phone, String actionCode, String uid, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + SEND_VALIDATION);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tel", phone);
            jsonObject.put("dialCode", 86);
            jsonObject.put("action", Integer.parseInt(actionCode));
            if (!TextUtils.isEmpty(uid)) {
                jsonObject.put("userId", Integer.parseInt(uid));
            }

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
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        //   return HttpManager.request(request, callback);
    }

    public static void checkValidation
            (String phone, String captcha, String actionCode, long uid, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + CHECK_VALIDATION);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tel", phone);
            jsonObject.put("validationCode", captcha);
            jsonObject.put("action", Integer.parseInt(actionCode));
            //  if (!TextUtils.isEmpty(uid)) {
            if (uid != 0)
                jsonObject.put("userId", uid);
            //    }
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
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        //   return HttpManager.request(request, callback);
    }

    public static void signUp(String phone, String pwd, String captcha, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + SIGNUP);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tel", phone);
            jsonObject.put("password", pwd);
            jsonObject.put("validationCode", captcha);
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
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        //    return HttpManager.request(request, callback);
    }

    public static void bindPhone(String phone, String uid, String pwd, String token, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.PUT);
        request.setUrl(SystemConfig.DEV_URL + String.format(MODIFY_PWD, uid));
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tel", phone);
            jsonObject.put("token", token);
            if (!TextUtils.isEmpty(pwd)) {
                jsonObject.put("newPassword", pwd);
            }
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
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        //   return HttpManager.request(request, callback);
    }

    public static void resetPwd(String tel, String pwd, String token, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.PUT);
        request.setUrl(SystemConfig.DEV_URL + String.format(MODIFY_PWD, "_"));
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tel", tel);
            jsonObject.put("token", token);
            jsonObject.put("newPassword", pwd);
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
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        //  return HttpManager.request(request, callback);
    }

    public static void modifyPwd(String oldPwd, String newPwd, String uid, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.PUT);
        request.setUrl(SystemConfig.DEV_URL + String.format(MODIFY_PWD, uid));
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("oldPassword", oldPwd);
            jsonObject.put("newPassword", newPwd);
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
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        // return HttpManager.request(request, callback);
    }

    public static void signIn(String loginName, String pwd, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + SIGNIN);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("loginName", loginName);
            jsonObject.put("password", pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");
            request.setBodyEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        LogUtil.d(jsonObject.toString());
        // return null;
        //   return HttpManager.request(request, callback);
    }

    public static void updateUserFootPrint(String userId, String type, String[] id, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + String.format(TRACKS, AccountManager.getCurrentUserId()));
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", type);
            JSONArray jsonArray = new JSONArray();
            for (String sid : id) {
                jsonArray.put(sid);
            }
            jsonObject.put("tracks", jsonArray);
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
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        //  return HttpManager.request(request, callback);
    }

    public static void getUserInfo(String userId, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + USERINFO + userId);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, "", callback);
        // return HttpManager.request(request, callback);
    }


    public static void getUserFootPrint(String userId, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + String.format(TRACKS, userId));
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, "", callback);
        //  return HttpManager.request(request, callback);
    }


    public static void editUserAvatar(User user, String avatar, HttpCallBack callBack) {
        editUserInfo(user, avatar, null, null, null, null, null, null, callBack);
    }

    public static void editUserNickName(User user, String nickname, HttpCallBack callBack) {
        editUserInfo(user, null, nickname, null, null, null, null, null, callBack);
    }

    public static void editUserSignature(User user, String signature, HttpCallBack callBack) {
        editUserInfo(user, null, null, signature, null, null, null, null, callBack);
    }

    public static void editUserGender(User user, String gender, HttpCallBack callBack) {
        editUserInfo(user, null, null, null, gender, null, null, null, callBack);
    }

    public static void editUserResidence(User user, String residence, HttpCallBack callBack) {
        editUserInfo(user, null, null, null, null, residence, null, null, callBack);
    }

    public static void editUserBirthday(User user, String birthday, HttpCallBack callBack) {
        editUserInfo(user, null, null, null, null, null, birthday, null, callBack);
    }

    public static void editUserStatus(User user, String status, HttpCallBack callBack) {
        editUserInfo(user, null, null, null, null, null, null, status, callBack);
    }


    public static void editUserInfo(User user, String avatar, String nickName, String signature, String gender, String residence,
                                    String birthday, String status, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.PATCH);
        request.setUrl(SystemConfig.DEV_URL + USERINFO + user.getUserId());
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
//        request.setHeader(PTHeader.HEADER_CHARSET, "utf-8");
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
                if (gender.equals("U")) {
                    jsonObject.put("gender", null);
                } else jsonObject.put("gender", gender);
            }
            if (!TextUtils.isEmpty(residence)) {
                jsonObject.put("residence", residence);
            }
            if (!TextUtils.isEmpty(status)) {
                jsonObject.put("travelStatus", status);
            }
            if (!TextUtils.isEmpty(birthday)) {
                jsonObject.put("birthday", birthday);
            }

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
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        //    return HttpManager.request(request, callback);
    }

    /**
     * 获取好友列表
     *
     * @param callback
     * @return
     */
    public static void getContact(HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + String.format(CONTACTS, AccountManager.getCurrentUserId()));
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, "", callback);
        //  return HttpManager.request(request, callback);
    }
    /**
     * 同步获取好友列表
     *
     * @param
     * @return
     */
    /*public static String getAsynContact() {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + CONTACTS);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        return HttpManager.aysnRequest(request);
    }*/

    /**
     * 请求添加好友
     *
     * @param uid
     * @param callback
     * @return
     */
    public static void requestAddContact(String uid, String message, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        // request.setUrl(SystemConfig.BASE_URL + REQUEST_ADD_CONTACTS);
        request.setUrl(SystemConfig.DEV_URL + "/users/" + AccountManager.getCurrentUserId() + "/contact-requests");
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request, uid);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("contactId", Long.parseLong(uid));
            jsonObject.put("message", message);
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
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        //  return HttpManager.request(request, callback);
    }

    /**
     * 添加好友
     *
     * @param requestId
     * @param callback
     * @return
     */
    public static void addContact(String requestId, String message, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.PATCH);
        request.setUrl(SystemConfig.DEV_URL + "/users/" + AccountManager.getCurrentUserId() + "/contact-requests/" + requestId);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", 1);
            jsonObject.put("message", message);
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
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        // return HttpManager.request(request, callback);
    }

    /**
     * 删除好友
     *
     * @param uid
     * @param callback
     * @return
     */
    public static void deleteContact(String uid, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.DELETE);
        request.setUrl(SystemConfig.DEV_URL + String.format(CONTACTS, AccountManager.getCurrentUserId()) + "/" + uid);
//        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, "", callback);
        //   return HttpManager.request(request, callback);
    }

    /**
     * 搜索联系人
     *
     * @param key
     * @param callback
     * @return
     */

    public static void seachContact(String key, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        //     if ( RegexUtils.isMobileNO(key)) {
        request.setUrl(SystemConfig.DEV_URL + SEACH_CONTACT_BY_TEL + key);
        //    }else {
        //        request.setUrl(SystemConfig.DEV_URL + SEACH_CONTACT_BY_NICKNAME+key);
        //    }
        //    request.setUrl(SystemConfig.DEV_URL + CONTACTS);
        //    request.putUrlParams("Keyword", key);
        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, "", callback);
        //   return HttpManager.request(request, callback);
    }

    public static void logout(long userId, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + LOGOUT);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");
            request.setBodyEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        //   return HttpManager.request(request, callback);
    }


    public static void experRequest(String phoneNumber, long userId, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + String.format(EXPERT_REQUEST, AccountManager.getCurrentUserId()));
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tel", phoneNumber);
            if (userId != -1) {
                jsonObject.put("userId", userId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");
            request.setBodyEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        //   return HttpManager.request(request, callback);
    }


    /**
     * 达人信息列表
     *
     * @param key
     * @param callback
     * @return
     */

    public static void searchExpertContact(String key, String field, int page, int pageSize, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + SEACH_CONTACT);
        request.putUrlParams("keyword", key);
        request.putUrlParams("field", field);
        request.putUrlParams("page", page + "");
        request.putUrlParams("pageSize", pageSize + "");
        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, "", callback);
        //  return HttpManager.request(request, callback);
    }


    /**
     * 达人足迹列表
     *
     * @param abroad
     * @param callback
     * @return
     */

    public static void searchExpertFootPrint(boolean abroad, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + SEARCH_EXPERT_FOOTPRINT);
        request.putUrlParams("abroad", String.valueOf(abroad));
        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, "", callback);
        //   return HttpManager.request(request, callback);
    }


    /**
     * 获取相册
     *
     * @param userId
     * @param callback
     * @return
     */

    public static void getUserPicAlbumn(String userId, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + String.format(ALBUMS, userId));
        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, "", callback);
        //   return HttpManager.request(request, callback);
    }


    /**
     * 删除相册中的图片
     *
     * @param userId
     * @param picId
     * @param callback
     * @return
     */

    public static void delUserAlbumPic(String userId, String picId, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.DELETE);
        request.setUrl(SystemConfig.DEV_URL + String.format(ALBUMS, userId) + "/" + picId);
        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, "", callback);
        //return HttpManager.request(request, callback);
    }


    /**
     * 根据达人目的地id获取达人列表
     *
     * @param locId
     * @param callback
     * @return
     */

    public static void getExpertById(String[] locId, int page, int pageSize, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + String.format(EXPERT_BY_TRACK, locId[0]));
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        request.putUrlParams("page", String.valueOf(page));
        request.putUrlParams("pageSize", String.valueOf(pageSize));
        setDefaultParams(request);
//        JSONObject jsonObject = new JSONObject();
//        try {
//            JSONArray jsonArray = new JSONArray();
//            for (String id : locId) {
//                jsonArray.put(id);
//            }
//            jsonObject.put("locId", jsonArray);
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
        OkHttpClientManager.getInstance().request(request, "", callback);
        //   return HttpManager.request(request, callback);
    }

    /**
     * 根据通讯录获取联系人
     *
     * @param bookList
     * @param callback
     * @return
     */

    public static void searchByAddressBook(List<AddressBookbean> bookList, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + String.format(SEARCH_BY_ADDRESSBOOK, AccountManager.getCurrentUserId()));
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        request.setHeader("Content-Encoding", "gzip");
        setDefaultParams(request);
        JSONObject rootObject = new JSONObject();
        try {

            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject;
            for (AddressBookbean addressBookbean : bookList) {
                jsonObject = new JSONObject();
                jsonObject.put("entryId", addressBookbean.entryId);
                jsonObject.put("sourceId", addressBookbean.sourceId);
                jsonObject.put("tel", addressBookbean.tel.trim());
                jsonObject.put("name", addressBookbean.name);
                jsonArray.put(jsonObject);
            }
            rootObject.put("contacts", jsonArray);
            rootObject.put("action", "addressbook");
            try {
                StringEntity entity = new StringEntity(rootObject.toString(), "utf-8");
//                request.setBodyEntity( entity);
                request.setBodyEntity(new GzipCompressingEntity(entity));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            LogUtil.d(rootObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClientManager.getInstance().request(request, rootObject.toString(), callback);
        //   return HttpManager.request(request, callback);
    }

    public static void editMemo
            (String userId, String memo, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.PUT);
        request.setUrl(SystemConfig.DEV_URL + "/users/" + AccountManager.getCurrentUserId() + "/contacts/" + userId + "/memo");
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("memo", memo);
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
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        //  return HttpManager.request(request, callback);
    }

    public static void muteConversation
            (String userId, String conversation, boolean value, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.PATCH);
        request.setUrl(SystemConfig.DEV_URL + String.format(MUTE_CONVERSATION, userId) + conversation);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mute", value);
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
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        //  return HttpManager.request(request, callback);
    }

    //用户喜欢的地点

    public static void vote(String id, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + String.format(VOTE, id));
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", Long.parseLong(AccountManager.getCurrentUserId()));

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
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        //  return HttpManager.request(request, callback);
    }

    public static void unVote(String id, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.DELETE);
        request.setUrl(SystemConfig.DEV_URL + String.format(VOTE, id) + "/" + AccountManager.getCurrentUserId());
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        OkHttpClientManager.getInstance().request(request, "", callback);
        // return HttpManager.request(request, callback);
    }


    //屏蔽用户
    public static void addToBlackList(String userId, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + String.format(BLOCK, AccountManager.getCurrentUserId()));
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", Long.parseLong(userId));
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
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        //  return HttpManager.request(request, callback);
    }

    public static void removeFromBlackList(String userId, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.DELETE);
        request.setUrl(SystemConfig.DEV_URL + String.format(BLOCK, AccountManager.getCurrentUserId()) + "/" + userId);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        //    return HttpManager.request(request, callback);
        OkHttpClientManager.getInstance().request(request, "", callback);
    }
}
