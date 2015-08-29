package com.lv.net;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lv.Listener.FetchListener;
import com.lv.Listener.HttpCallback;
import com.lv.bean.Message;
import com.lv.bean.SendMessageBean;
import com.lv.im.IMClient;
import com.lv.im.LazyQueue;
import com.lv.utils.Config;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by q on 2015/4/27.
 */
public class HttpUtils {
    static ExecutorService exec = Executors.newCachedThreadPool();
    public static final OkHttpClient client = new OkHttpClient();

    static {
        client.setConnectTimeout(10, TimeUnit.SECONDS);
    }

    public static final MediaType json
            = MediaType.parse("application/json; charset=utf-8");

    public static Response HttpRequest_Post(String url, String postBody) throws Exception {
        RequestBody body = RequestBody.create(json, postBody);
        Request request = new Request.Builder()
                .addHeader("UserId", IMClient.getInstance().getCurrentUserId())
                .addHeader("Accept", "application/vnd.hedylogos.v1+json")
                .url(url)
                .post(body)
                .build();
        if (Config.isDebug){
            Log.d(Config.TAG,"请求内容： "+postBody);
            Log.d(Config.TAG,"请求接口： "+url);
        }
        return client.newCall(request).execute();

    }

    public static Response HttpRequest_Put(String url, String postBody) throws Exception {
        RequestBody body = RequestBody.create(json, postBody);
        Request request = new Request.Builder()
                .addHeader("UserId", IMClient.getInstance().getCurrentUserId())
                .addHeader("Accept", "application/vnd.hedylogos.v1+json")
                .url(url)
                .put(body)
                .build();
        return client.newCall(request).execute();

    }

    public static Response HttpRequest_Get(String url) throws Exception {
        Request request = new Request.Builder()
                .addHeader("UserId", IMClient.getInstance().getCurrentUserId())
                .addHeader("Accept", "application/vnd.hedylogos.v1+json")
                .url(url)
                .get()
                .build();
        if (Config.isDebug){
            Log.d(Config.TAG,"请求接口： "+url);
        }
        return client.newCall(request).execute();
    }

    public static Response HttpRequest_Patch(String url, String patchBody) throws Exception {
        RequestBody body = RequestBody.create(json, patchBody);
        Request request = new Request.Builder()
                .addHeader("UserId", IMClient.getInstance().getCurrentUserId())
                .addHeader("Accept", "application/vnd.hedylogos.v1+json")
                .url(url)
                .patch(body)
                .build();
        if (Config.isDebug){
            Log.d(Config.TAG,"请求内容： "+patchBody);
            Log.d(Config.TAG,"请求接口： "+url);
        }
        return client.newCall(request).execute();
    }

    public static void muteConversation(String conversation, boolean value, HttpCallback callback) {
        final String url = Config.HOST + "/users/" + IMClient.getInstance().getCurrentUserId() + "/conversations/" + conversation;
        final JSONObject object = new JSONObject();
        try {
            object.put("mute", value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        exec.execute(() -> {
            try {
                Response response = HttpRequest_Patch(url, object.toString());
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else callback.onFailed(response.code());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void postAck(final long time, FetchListener listener) {
        final String url = Config.ACK_URL + IMClient.getInstance().getCurrentUserId() + "/messages";
        final JSONObject obj = new JSONObject();
        try {
            obj.put("purgeBefore", time);
            IMClient.getInstance().clearackList();
            if (Config.isDebug) {
                Log.i(Config.TAG, url + " ack : " + obj.toString());
            }
            IMClient.getInstance().clearackList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        exec.execute(() -> {
            try {
                Response response = HttpRequest_Post(url, obj.toString());
                if (response.isSuccessful()) {
                    String s = response.body().string();
                    JSONObject object = new JSONObject(s);
                    JSONArray resultArray = object.getJSONArray("result");
                    List<Message> list = new ArrayList<>();
                    if (Config.isDebug) {
                        Log.i(Config.TAG, "ack Result : " + s);
                    }
                    if (resultArray.length()>0){
                        IMClient.lastSuccessFetch = object.getLong("timestamp");
                    }
                    for (int j = 0; j < resultArray.length(); j++) {
                        Message msg = JSON.parseObject(resultArray.getJSONObject(j).toString(), Message.class);
                        list.add(msg);
                    }
                    if (list.size() > 0) {
                        if (Config.isDebug) {
                            Log.i(Config.TAG, "msg list : " + list.toString());
                        }
                        listener.OnMsgArrive(list);
                    }else {
                        listener.OnMsgArrive(new ArrayList<Message>());
                    }
                } else {
                    listener.OnMsgArrive(new ArrayList<Message>());
                    IMClient.getInstance().setBLOCK(false);
                    LazyQueue.getInstance().TempDequeue();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void sendMessage(final String conversation, final String currentFri, final SendMessageBean msg, final long localId, final HttpCallback listen, final String chatType) {
        if (IMClient.taskMap.containsKey(currentFri)) {
            if (IMClient.taskMap.get(currentFri).contains(localId)) return;
            else IMClient.taskMap.get(currentFri).add(localId);
        } else {
            IMClient.taskMap.put(currentFri, new ArrayList<Long>());
            IMClient.taskMap.get(currentFri).add(localId);
        }
        JSONObject object = new JSONObject();
        try {
            object.put("chatType", chatType);
            object.put("sender", msg.getSender());
//            if (conversation != null && !"".equals(conversation)) {
//                object.put("conversation", conversation);
//            }
            object.put("receiver", Long.parseLong(currentFri));
            object.put("msgType", msg.getMsgType());
            object.put("contents", msg.getContents());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String str = object.toString();
        if (Config.isDebug) {
            Log.i(Config.TAG, "send_message:" + str);
        }
        exec.execute(() -> {
            try {
                Response response = HttpRequest_Post(Config.SEND_URL, str);
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    IMClient.taskMap.get(currentFri).remove(localId);
                    if (Config.isDebug) {
                        Log.i(Config.TAG, result);
                    }
                    JSONObject object1 = new JSONObject(result);
                    JSONObject obj = object1.getJSONObject("result");
                    String conversation1 = obj.get("conversation").toString();
                    String msgId = obj.get("msgId").toString();
                    Long timestamp = Long.parseLong(obj.get("timestamp").toString());
                    IMClient.getInstance().setLastMsg(conversation1, Integer.parseInt(msgId));
                    IMClient.getInstance().updateMessage(currentFri, localId, msgId, conversation1, timestamp, Config.STATUS_SUCCESS, null, msg.getMsgType());
                    if (Config.isDebug) {
                        Log.i(Config.TAG, "发送成功，消息更新！");
                    }
                    listen.onSuccess();
                } else {
                    int code = response.code();
                    IMClient.taskMap.get(currentFri).remove(localId);
                    if (Config.isDebug) {
                        Log.i(Config.TAG, "发送失败：code " + code);
                    }
                    IMClient.getInstance().updateMessage(currentFri, localId, null, null, 0, Config.STATUS_FAILED, null, msg.getMsgType());
                    listen.onFailed(code);
                }
            } catch (Exception e) {
                e.printStackTrace();
                IMClient.taskMap.get(currentFri).remove(localId);
                IMClient.getInstance().updateMessage(currentFri, localId, null, null, 0, Config.STATUS_FAILED, null, msg.getMsgType());
                listen.onFailed(-1);
            }
        });

    }

    public static void login(final String username, final HttpCallback callback) {
        JSONObject obj = new JSONObject();
        try {
            exec.execute(() -> {
                try {
                    String cid = null;
                    while (true) {
                        if (IMClient.getInstance().getCid() != null) {
                            cid = IMClient.getInstance().getCid();
                            break;
                        }
                    }
                    obj.put("userId", Long.parseLong(username));
                    obj.put("regId", cid);
                    if (Config.isDebug) {
                        Log.i(Config.TAG, "login:" + obj.toString());
                    }
                    Response response = HttpRequest_Post(Config.LOGIN_URL, obj.toString());
                    if (Config.isDebug) {
                        Log.i(Config.TAG, "login code:" + response.code());
                    }
                    if (response.isSuccessful()) {
                        callback.onSuccess();
                    } else callback.onFailed(response.code());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void NetSpeedTest() {
        List<String> urlList = new ArrayList<>();
        urlList.add("202.108.22.5");
        urlList.add("www.sina.com.cn");
        urlList.add("60.5.254.21");
        final Process[] process = {null};
        final String[] result = {""};
        final String[] fastest = {""};
        final long[] temp = {9999};
        for (String url : urlList) {
            exec.execute(() -> {
                long start = 0;
                long end = 0;
                try {
                    start = System.currentTimeMillis();
                    process[0] = Runtime.getRuntime().exec("/system/bin/ping -c 1 " + url);
                    int status = process[0].waitFor();
                    if (status == 0) {
                        result[0] = "success";
                    } else {
                        result[0] = Integer.toString(status);
                    }
                    end = System.currentTimeMillis();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                long time = end - start;
                if (Config.isDebug) {
                    Log.i(Config.TAG, "result " + result[0]);
                    Log.i(Config.TAG, url + " ping time:" + time);
                }
                if (time < temp[0]) {
                    temp[0] = time;
                    fastest[0] = url;
                }
            });
            if (Config.isDebug) {
                Log.i(Config.TAG, "fastest url " + fastest[0]);
            }
        }


    }

    public interface tokenGet {
        public void OnSuccess(String key, String token);

        public void OnFailed();
    }

    public static void getToken(final int msgType, final tokenGet listener) {
        exec.execute(() -> {
            JSONObject object = new JSONObject();
            try {
                object.put("msgType", msgType);
                Response response = HttpRequest_Post(Config.HOST + "/upload/token-generator", object.toString());
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    JSONObject res = new JSONObject(result);
                    JSONObject obj = res.getJSONObject("result");
                    String key = obj.getString("key");
                    String token = obj.getString("token");
                    listener.OnSuccess(key, token);
                } else if (Config.isDebug) {
                    Log.i(Config.TAG, "TokenGet Error :" + response.code());
                    listener.OnFailed();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void getConversationAttrs(String userId, List<String> chatIds, final HttpCallback callback) {
        exec.execute(() -> {
            if (chatIds.size()==0)return;
            String cids = "";
            for (int i = 0; i < chatIds.size(); i++) {
                if (i != 0) cids += ",";
                cids += chatIds.get(i);
            }
            String url = Config.HOST + String.format(Config.CONS_URL, userId) + cids;
            try {
                Response response = HttpRequest_Get(url);
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    callback.onSuccess();
                    callback.onSuccess(result);
                } else callback.onFailed(response.code());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public static void getConversationAttr(String userId,String friendId, final HttpCallback callback) {
        exec.execute(() -> {
            String url = Config.HOST + String.format(Config.CON_URL, userId) + friendId;
            try {
                Response response = HttpRequest_Get(url);
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    callback.onSuccess();
                    callback.onSuccess(result);
                } else callback.onFailed(response.code());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
