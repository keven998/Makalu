package com.xuejian.client.lxp.common.api;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.http.HttpManager;
import com.aizou.core.http.entity.PTHeader;
import com.aizou.core.http.entity.PTRequest;
import com.aizou.core.http.entity.PTRequestHandler;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.config.PeachApplication;
import com.xuejian.client.lxp.config.SystemConfig;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by yibiao.qin on 2015/6/19.
 */
public class GroupApi {
    public static final String CreateGroup = "/chatgroups";
    public static final String EditGroup = "/chatgroups/";
    public static final String GetGroupInfo = "/chatgroups/";
    public static final String GetGroupMember1 = "/chatgroups/";
    public static final String GetGroupMember2 = "/members";
    public static final String AddGroupMember1 = "/chatgroups/";
    public static final String AddGroupMember2 = "/members";


    public static PTRequestHandler createGroup(String requestBody,HttpCallBack callback){
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + CreateGroup);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        StringEntity entity = null;
        try {
            entity = new StringEntity(requestBody, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        request.setBodyEntity(entity);
        return HttpManager.request(request, callback);
    }
    public static void editGroupDesc(String desc,HttpCallBack callBack){
        JSONObject object=new JSONObject();
        try {
            object.put("desc",desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editGroup(object.toString(), callBack);
    }
    public static void editGroupName(String name,HttpCallBack callBack){
        JSONObject object=new JSONObject();
        try {
            object.put("name",name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editGroup(object.toString(), callBack);
    }
    public static void editGroupAvatar(String avatar,HttpCallBack callBack){
        JSONObject object=new JSONObject();
        try {
            object.put("avatar",avatar);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editGroup(object.toString(), callBack);
    }

    public static PTRequestHandler editGroup(String requestBody,HttpCallBack callback){
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.TRACE);
        request.setUrl(SystemConfig.DEV_URL + "/chatgroups/200010");
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        StringEntity entity = null;
        try {
            entity = new StringEntity(requestBody, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        request.setBodyEntity(entity);
        return HttpManager.request(request, callback);
    }

    public static PTRequestHandler getGroupInfo(String groupId,HttpCallBack callback){
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + "/chatgroups/" + groupId);
                request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }
    public static PTRequestHandler getGroupMemberInfo(String groupId,HttpCallBack callback){
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + "/chatgroups/" + groupId + "/members");
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }
    public static PTRequestHandler addGroupMember(String groupId,long userId,HttpCallBack callback){
        JSONObject object=new JSONObject();
        try {
            object.put("member",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + "/chatgroups/" + groupId+"/members");
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        StringEntity entity = null;
        try {
            entity = new StringEntity(object.toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        request.setBodyEntity(entity);
        return HttpManager.request(request, callback);
    }

    public static PTRequestHandler deleteGroupMember(String groupId,long userId,HttpCallBack callback){
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.DELETE);
        request.setUrl(SystemConfig.DEV_URL + "/chatgroups/" + groupId+"/members/"+userId);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        return HttpManager.request(request, callback);
    }
    public static PTRequestHandler editGroupMembers(String groupId ,List<Long>members,int action,long userId,HttpCallBack callback){
        JSONObject object=new JSONObject();
        JSONArray array=new JSONArray();
        for (long id:members){
            array.put(id);
        }
        try {
            object.put("member",array);
            object.put("action",action);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PTRequest request = new PTRequest();
        if (action==1){
            request.setHttpMethod(PTRequest.POST);
        }else {
            request.setHttpMethod(PTRequest.DELETE);
        }
        request.setUrl(SystemConfig.DEV_URL + "/chatgroups/" + groupId+"/members");
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        setDefaultParams(request);
        StringEntity entity = null;
        try {
            entity = new StringEntity(object.toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        request.setBodyEntity(entity);
        return HttpManager.request(request, callback);
    }

    public static void setDefaultParams(PTRequest request){
        if(AccountManager.getCurrentUserId()!=null){
            request.setHeader("UserId", AccountManager.getCurrentUserId());
        }
        request.setHeader("Accept","application/vnd.lvxingpai.v1+json");
        request.setHeader("Platform", "Android "+android.os.Build.VERSION.RELEASE);
        request.setHeader("Version", PeachApplication.APP_VERSION_NAME);
    }
}