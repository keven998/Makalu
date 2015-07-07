package com.xuejian.client.lxp.module.toolbox.im.group;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.lv.Utils.Config;
import com.lv.im.IMClient;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by q on 2015/5/11.
 */
public class HttpManager {
    private static SyncHttpClient client = new SyncHttpClient();
    static ExecutorService exec = Executors.newFixedThreadPool(5);

    public static void createGroup(final String name, String groupType, final boolean isPublic, String avatar, final List<Long> participants, final long row, final CreateSuccessListener listener) {
        final JSONObject obj = new JSONObject();
        final JSONArray array = new JSONArray();
        try {

            for (long member : participants) {
                array.put(member);
            }
            obj.put("name", name);
            obj.put("groupType", groupType);
            obj.put("isPublic", isPublic);
            obj.put("avatar", avatar);
            obj.put("participants", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        exec.execute(new Runnable() {
            @Override
            public void run() {
//                GroupApi.createGroup(obj.toString(), new HttpCallBack() {
//                    @Override
//                    public void doSuccess(Object result, String method) {
//                        try {
//                            String r = result.toString();
//                            if (Config.isDebug) {
//                                Log.i(Config.TAG, "create group Result : " + result);
//                            }
//                            JSONObject object = new JSONObject(r);
//                            JSONObject jsonObject = object.getJSONObject("result");
//                            String groupId = jsonObject.getString("groupId");
//                            String conversation = jsonObject.getString("conversation");
//                            String groupType = jsonObject.getString("groupType");
//                            long creator = jsonObject.getLong("creator");
//                            IMClient.getInstance().addGroup2Conversation(groupId, conversation);
//                            JSONObject o = new JSONObject();
//                            o.put("GroupMember", array);
//                            o.put("groupType", groupType);
//                            o.put("isPublic", isPublic);
//                            o.put("creator", creator);
//                            UserDBManager.getInstance().saveContact(new com.xuejian.client.lxp.db.User(Long.parseLong(groupId), name, o.toString(), 8));
//                            if (Config.isDebug) {
//                                Log.i(Config.TAG, "群组更新成功");
//                            }
//                            listener.OnSuccess(groupId, conversation);
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void doFailure(Exception error, String msg, String method) {
//                            Log.i("GroupApi", "create group Error : " + msg);
//                    }
//                });
                HttpPost post = new HttpPost(Config.HOST + "/groups");
                post.addHeader("UserId", AccountManager.getCurrentUserId());
                HttpResponse httpResponse = null;
                try {
                    StringEntity entity = new StringEntity(obj.toString(),
                            HTTP.UTF_8);
                    entity.setContentType("application/json");
                    post.setEntity(entity);
                    httpResponse = new DefaultHttpClient().execute(post);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity res = httpResponse.getEntity();
                        String result = EntityUtils.toString(res);
                        if (Config.isDebug) {
                            Log.i(Config.TAG, "create group Result : " + result);
                        }
                        JSONObject object = new JSONObject(result);
                        JSONObject jsonObject = object.getJSONObject("result");
                        String groupId = jsonObject.getString("groupId");
                        String conversation = jsonObject.getString("conversation");
                        String groupType = jsonObject.getString("groupType");
                        long creator = jsonObject.getLong("creator");
                        IMClient.getInstance().addGroup2Conversation(groupId, conversation);
                        JSONObject o = new JSONObject();
                        o.put("GroupMember", array);
                        o.put("groupType", groupType);
                        o.put("isPublic", isPublic);
                        o.put("creator", creator);
                    //    UserDBManager.getInstance().saveContact(new User(Long.parseLong(groupId), name, o.toString(), 8));
                        if (Config.isDebug) {
                            Log.i(Config.TAG, "群组更新成功");
                        }
                        listener.OnSuccess(groupId, conversation);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void addMembers(String groupId, List<Long> members, boolean isPublic, CallBack callBack) {
        final JSONObject obj = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            array.put(members.get(0));
            //array.put(3);
            obj.put("action", "addMembers");
            obj.put("isPublic", isPublic);
            obj.put("participants", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editGroupMembers(groupId, obj, callBack);
    }

    public static void removeMembers(String groupId, List<Long> members, boolean isPublic, CallBack callBack) {
        final JSONObject obj = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            for (long id : members) {
                array.put(id);
            }
            obj.put("action", "delMembers");
            obj.put("isPublic", isPublic);
            obj.put("participants", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editGroupMembers(groupId, obj, callBack);
    }

    public static void silenceMembers(String groupId, List<Long> members, boolean isPublic, CallBack callBack) {
        final JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            for (long id : members) {
                array.put(id);
            }
            obj.put("action", "silence");
            obj.put("isPublic", isPublic);
            obj.put("participants", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editGroupMembers(groupId, obj, callBack);
    }

    public static void editGroupMembers(final String GroupId, final JSONObject obj, final CallBack callBack) {

        exec.execute(new Runnable() {
            @Override
            public void run() {
                HttpPost post = new HttpPost(Config.HOST + "/groups/" + GroupId);
                post.addHeader("UserId", AccountManager.getCurrentUserId());
                HttpResponse httpResponse = null;
                try {
                    StringEntity entity = new StringEntity(obj.toString(),
                            HTTP.UTF_8);
                    entity.setContentType("application/json");
                    post.setEntity(entity);
                    httpResponse = new DefaultHttpClient().execute(post);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity res = httpResponse.getEntity();
                        String result = EntityUtils.toString(res);
                        if (Config.isDebug) {
                            Log.i(Config.TAG, "edit member Result : " + result);
                        }
                        JSONObject object = new JSONObject(result);
                        callBack.onSuccess();
                        //JSONObject jsonObject = object.getJSONObject("result");
                    } else callBack.onFailed();
                } catch (Exception e) {
                    e.printStackTrace();
                    callBack.onFailed();
                }
            }
        });
    }

    public static void editGroup(final String groupId, final String groupName, final CallBack callBack) {
        final String url = Config.GET_GROUP + groupId;
        exec.execute(new Runnable() {
            @Override
            public void run() {
                HttpPut httpPut = new HttpPut(url);
                httpPut.addHeader("UserId", AccountManager.getCurrentUserId());
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("groupId", Long.parseLong(groupId));
                    obj.put("name", groupName);
                    StringEntity entity = new StringEntity(obj.toString(),
                            HTTP.UTF_8);
                    entity.setContentType("application/json");
                    httpPut.setEntity(entity);
                    HttpResponse httpResponse = new DefaultHttpClient().execute(httpPut);
                    HttpEntity res = httpResponse.getEntity();
                    int code = httpResponse.getStatusLine().getStatusCode();
                    String result = EntityUtils.toString(res);
                    if (code == 200) {
                        if (Config.isDebug) {
                            Log.i(Config.TAG, "edit group : " + result);
                        }
                        callBack.onSuccess();
                    } else callBack.onFailed();
                } catch (Exception e) {
                    e.printStackTrace();
                    callBack.onFailed();
                }
            }
        });
    }

    public static void getGroupMembers(String groupId, CallBack callBack) {
        String url = Config.GET_GROUP + groupId + "/users";
        getInformations(url, "member", groupId, callBack);
    }

    public static void getGroupInformation(String groupId, CallBack callBack) {
        final String url = Config.GET_GROUP + groupId;
        getInformations(url, "info", groupId, callBack);
    }

    private static void getInformations(final String url, final String type, final String groupId, final CallBack callBack) {
        exec.execute(new Runnable() {
            @Override
            public void run() {
                HttpGet get = new HttpGet(url);
                get.addHeader("UserId", AccountManager.getCurrentUserId());
                try {
                    HttpResponse httpResponse = new DefaultHttpClient().execute(get);
                    HttpEntity res = httpResponse.getEntity();
                    int code = httpResponse.getStatusLine().getStatusCode();
                    String result = EntityUtils.toString(res);
                    if (code == 200) {
                        if ("member".equals(type)) {
                            try {
                                if (Config.isDebug) {
                                    Log.i(Config.TAG, "group member : " + result);
                                }
                                JSONObject object = new JSONObject(result);
                                JSONArray userList = object.getJSONArray("result");
                                List<User> list = new ArrayList<User>();
                                for (int i = 0; i < userList.length(); i++) {
                                    String str = userList.get(i).toString();
                                    User user = JSON.parseObject(str, User.class);
                                    list.add(user);
                                }
                                UserDBManager.getInstance().updateGroupMemberInfo(list, groupId);
                                callBack.onSuccess();
                            } catch (Exception e) {
                                e.printStackTrace();
                                callBack.onFailed();
                            }

                        } else if ("info".equals(type)) {
                            JSONObject object = null;
                            try {
                                if (Config.isDebug) {
                                    Log.i(Config.TAG, "group info : " + result);
                                }
                                object = new JSONObject(result);
                                JSONObject o = object.getJSONObject("result");
                                User user = new User();
                                user.setNickName(o.get("name").toString() == null ? " " : o.get("name").toString());
                                o.remove("name");
                                user.setExt(o.toString());
                                user.setType(8);
                                UserDBManager.getInstance().updateGroupInfo(user, groupId);
                                //"groupType":"common","createTime":1433316405290,"desc":"群主什么也没说","visible":true,"updateTime":1433316405290,"isPublic":true,"
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    } else {
                        if (Config.isDebug) {
                            Log.i(Config.TAG, "group info error code: " + code + " " + result);
                        }
                        callBack.onFailed();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getUserGroupInfo(String userId) {
        final String url = Config.HOST + "/users/" + AccountManager.getCurrentUserId() + "/groups";
        exec.execute(new Runnable() {
            @Override
            public void run() {
                HttpGet get = new HttpGet(url);
                get.addHeader("UserId", AccountManager.getCurrentUserId());
                try {
                    HttpResponse httpResponse = new DefaultHttpClient().execute(get);
                    HttpEntity res = httpResponse.getEntity();
                    if (Config.isDebug) {
                        Log.i(Config.TAG, "User-Group Info : " + EntityUtils.toString(res));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void searchGroup(final String tag, final String value) {
        final String url = Config.HOST + "/groups/search";

        exec.execute(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams();
                params.put(tag, value);
                // params.add("UserId",100001+"");
                client.addHeader("UserId", AccountManager.getCurrentUserId());
                client.get(url, params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        if (Config.isDebug) {
                            throwable.printStackTrace();
                            Log.i(Config.TAG, i + " fail  " + s);
                        }
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        if (Config.isDebug) {
                            Log.i(Config.TAG, i + "  " + s);
                        }
                    }
                });
            }
        });
    }

    public static void createGroup1(final String name, String groupType, final boolean isPublic, String avatar, final List<Long> participants, final long row, final CreateSuccessListener listener) {
        final JSONObject obj = new JSONObject();
        final JSONArray array = new JSONArray();
        try {

            for (long member : participants) {
                array.put(member);
            }
           // array.put(Long.parseLong(AccountManager.getCurrentUserId()));
            obj.put("name", name);
            obj.put("avatar", "");
            obj.put("desc", "");
            obj.put("members", array);
            System.out.println(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        exec.execute(new Runnable() {
            @Override
            public void run() {
//                GroupApi.createGroup(obj.toString(), new HttpCallBack() {
//                    @Override
//                    public void doSuccess(Object result, String method) {
//                        try {
//                            String r = result.toString();
//                            if (Config.isDebug) {
//                                Log.i(Config.TAG, "create group Result : " + result);
//                            }
//                            JSONObject object = new JSONObject(r);
//                            JSONObject jsonObject = object.getJSONObject("result");
//                            String groupId = jsonObject.getString("groupId");
//                            String conversation = jsonObject.getString("conversation");
//                            String groupType = jsonObject.getString("groupType");
//                            long creator = jsonObject.getLong("creator");
//                            IMClient.getInstance().addGroup2Conversation(groupId, conversation);
//                            JSONObject o = new JSONObject();
//                            o.put("GroupMember", array);
//                            o.put("groupType", groupType);
//                            o.put("isPublic", isPublic);
//                            o.put("creator", creator);
//                            UserDBManager.getInstance().saveContact(new com.xuejian.client.lxp.db.User(Long.parseLong(groupId), name, o.toString(), 8));
//                            if (Config.isDebug) {
//                                Log.i(Config.TAG, "群组更新成功");
//                            }
//                            listener.OnSuccess(groupId, conversation);
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void doFailure(Exception error, String msg, String method) {
//                            Log.i("GroupApi", "create group Error : " + msg);
//                    }
//                });
                HttpPost post = new HttpPost("http://api-dev.lvxingpai.com/app/chatgroups");
               // post.addHeader("Accept","application/vnd.lvxingpai.v1+json");
                post.addHeader("UserId","100014");
                HttpResponse httpResponse = null;
                try {
                    StringEntity entity = new StringEntity(obj.toString(),
                            HTTP.UTF_8);
                    entity.setContentType("application/json");
                    post.setEntity(entity);
                    httpResponse = new DefaultHttpClient().execute(post);
                    System.out.println("create status code:" + httpResponse.getStatusLine().getStatusCode());
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity res = httpResponse.getEntity();
                        String result = EntityUtils.toString(res);
                        if (Config.isDebug) {
                            Log.i(Config.TAG, "create group Result : " + result);
                        }
//                        JSONObject object = new JSONObject(result);
//                        JSONObject jsonObject = object.getJSONObject("result");
//                        String groupId = jsonObject.getString("groupId");
//                        String conversation = jsonObject.getString("conversation");
//                        String groupType = jsonObject.getString("groupType");
//                        long creator = jsonObject.getLong("creator");
//                        IMClient.getInstance().addGroup2Conversation(groupId, conversation);
//                        JSONObject o = new JSONObject();
//                        o.put("GroupMember", array);
//                        o.put("groupType", groupType);
//                        o.put("isPublic", isPublic);
//                        o.put("creator", creator);
//                        UserDBManager.getInstance().saveContact(new User(Long.parseLong(groupId), name, o.toString(), 8));
//                        if (Config.isDebug) {
//                            Log.i(Config.TAG, "群组更新成功");
//                        }
                        listener.OnSuccess("0", null);
                    }
                    else listener.OnFailed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}

