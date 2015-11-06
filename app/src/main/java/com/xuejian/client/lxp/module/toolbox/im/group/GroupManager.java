package com.xuejian.client.lxp.module.toolbox.im.group;

import com.aizou.core.http.HttpCallBack;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.GroupApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class GroupManager {
    private static GroupManager groupManager;

    private GroupManager() {
    }

    public static GroupManager getGroupManager() {
        if (groupManager == null) {
            groupManager = new GroupManager();
        }
        return groupManager;
    }

    public void createGroup(String groupName, String avatar, String desc, JSONArray groupMember, HttpCallBack callBack) {
        final JSONObject obj = new JSONObject();
        try {
            obj.put("name", groupName);
            obj.put("avatar", avatar);
            obj.put("desc", desc);
            obj.put("members", groupMember);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GroupApi.createGroup(obj.toString(), callBack);
        // HttpManager.createGroup1(groupName, groupType, isPublic, null, groupMember, 0, listener);
    }


    public void addMembers(String groupId, List<Long> members, boolean isPublic, CallBack callBack) {
     //   HttpManager.addMembers(groupId, members, isPublic, callBack);

    }

    public void joinGroup(String groupId, String message) {

    }

    public void removeMembers(String groupId, List<Long> members, boolean isPublic, CallBack callBack) {
     //   HttpManager.removeMembers(groupId, members, isPublic, callBack);
    }

    public void silenceMembers(String groupId, List<Long> members, boolean isPublic, CallBack callBack) {
     //   HttpManager.silenceMembers(groupId, members, isPublic, callBack);
    }

    public void quitGroup(String groupId, HttpCallBack callBack) {
        GroupApi.deleteGroupMember(groupId, Long.parseLong(AccountManager.getCurrentUserId()), callBack);
    }

    public void getGroupInformation(String groupId, CallBack callBack) {
    //    HttpManager.getGroupInformation(groupId, callBack);
    }

    public void getGroupMembers(String groupId, CallBack callBack) {
     //   HttpManager.getGroupMembers(groupId, callBack);
    }

    public void getUserGroupInfo(String userId) {
     //   HttpManager.getUserGroupInfo(userId);
    }

    public void searchGroup(String tag, String value) {
      //  HttpManager.searchGroup("groupId", "900052");
    }

    public void editGroupName(String GroupId, String groupName, CallBack callBack) {
      //  HttpManager.editGroup(GroupId, groupName, callBack);
    }
}
