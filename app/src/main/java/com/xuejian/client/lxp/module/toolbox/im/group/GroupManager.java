package com.xuejian.client.lxp.module.toolbox.im.group;

import com.xuejian.client.lxp.module.toolbox.im.group.HttpManager;
import com.lv.user.UserDao;

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

    public void createGroup(String groupName, String groupType, boolean isPublic, List<Long> groupMember,CreateSuccessListener listener) {
      // long row= UserDao.getInstance().addGroup2User(groupName,groupType,isPublic,null,groupMember);
       HttpManager.createGroup(groupName, groupType, isPublic, null, groupMember, 0, listener);
    }


    public void addMembers(String groupId, List<Long> members, boolean isPublic,CallBack callBack) {
        HttpManager.addMembers(groupId, members, isPublic,callBack);

    }

    public void joinGroup(String groupId, String message) {

    }

    public void removeMembers(String groupId, List<Long> members, boolean isPublic,CallBack callBack) {
        HttpManager.removeMembers(groupId, members, isPublic,callBack);
    }

    public void silenceMembers(String groupId, List<Long> members, boolean isPublic,CallBack callBack) {
        HttpManager.silenceMembers(groupId, members, isPublic,callBack);
    }

    public void quitGroup(String groupId) {

    }

    public void getGroupInformation(String groupId,CallBack callBack) {
        HttpManager.getGroupInformation(groupId,callBack);
    }

    public void getGroupMembers(String groupId,CallBack callBack) {
        HttpManager.getGroupMembers(groupId,callBack);
    }
    public void getUserGroupInfo(String userId){
        HttpManager.getUserGroupInfo(userId);
    }
    public void searchGroup(String tag,String value){
        HttpManager.searchGroup("groupId","900052");
    }
}
