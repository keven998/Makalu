package com.xuejian.client.lxp.common.api;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.http.OkHttpClientManager;
import com.aizou.core.http.entity.PTHeader;
import com.aizou.core.http.entity.PTRequest;
import com.xuejian.client.lxp.config.SystemConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yibiao.qin on 2015/6/19.
 */
public class GroupApi extends BaseApi{
    public static final String CreateGroup = "/chatgroups";
    public static final String EditGroup = "/chatgroups/";
    public static final String GetGroupInfo = "/chatgroups/";
    public static final String GetGroupMember1 = "/chatgroups/";
    public static final String GetGroupMember2 = "/members";
    public static final String AddGroupMember1 = "/chatgroups/";
    public static final String AddGroupMember2 = "/members";
    public final static int ADD_MEMBER = 1;
    public final static int DELETE_MEMBER = 2;

    public static void createGroup(String requestBody, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + CreateGroup);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request,requestBody);
        OkHttpClientManager.getInstance().request(request, requestBody, callback);
      //  return HttpManager.request(request, callback);
    }

    public static void editGroupDesc(String desc, String GroupId, HttpCallBack callBack) {
        JSONObject object = new JSONObject();
        try {
            object.put("desc", desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editGroup(object.toString(), GroupId, callBack);
    }

    public static void editGroupName(String name, String GroupId, HttpCallBack callBack) {
        JSONObject object = new JSONObject();
        try {
            object.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editGroup(object.toString(), GroupId, callBack);
    }

    public static void editGroupAvatar(String avatar, String GroupId, HttpCallBack callBack) {
        JSONObject object = new JSONObject();
        try {
            object.put("avatar", avatar);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editGroup(object.toString(), GroupId, callBack);
    }

    public static void editGroup(String requestBody, String GroupId, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.PATCH);
        request.setUrl(SystemConfig.DEV_URL + "/chatgroups/" + GroupId);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request,requestBody);
        OkHttpClientManager.getInstance().request(request, requestBody, callback);
    //    return HttpManager.request(request, callback);
    }

    public static void getGroupInfo(String groupId, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + "/chatgroups/" + groupId);
      //  request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
      //  return HttpManager.request(request, callback);
    }

    public static void getGroupMemberInfo(String groupId, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + "/chatgroups/" + groupId + "/members");
      //  request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
      //  return HttpManager.request(request, callback);
    }

    public static void addGroupMember(String groupId, long userId, HttpCallBack callback) {
        JSONObject object = new JSONObject();
        try {
            object.put("member", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + "/chatgroups/" + groupId + "/members");
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request,object.toString());
        OkHttpClientManager.getInstance().request(request, object.toString(), callback);
     //   return HttpManager.request(request, callback);
    }

    public static void deleteGroupMember(String groupId, long userId, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.DELETE);
        request.setUrl(SystemConfig.DEV_URL + "/chatgroups/" + groupId + "/members/" + userId);
     //   request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
       // return HttpManager.request(request, callback);
    }

    public static void editGroupMembers(String groupId, JSONArray members, int action, HttpCallBack callback) {
        JSONObject object = new JSONObject();
        try {
            object.put("members", members);
            object.put("action", action);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PTRequest request = new PTRequest();
        if (action == ADD_MEMBER) {
            request.setHttpMethod(PTRequest.PATCH);
        } else {
            request.setHttpMethod(PTRequest.DELETE);
        }
        request.setUrl(SystemConfig.DEV_URL + "/chatgroups/" + groupId + "/members");
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request, object.toString());
        OkHttpClientManager.getInstance().request(request, object.toString(), callback);
   //     return HttpManager.request(request, callback);
    }
}