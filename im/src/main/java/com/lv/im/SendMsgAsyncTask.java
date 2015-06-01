package com.lv.im;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lv.Listener.SendMsgListener;
import com.lv.Utils.Config;
import com.lv.bean.IMessage;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by q on 2015/4/17.
 */
public class SendMsgAsyncTask {

    Context c;
    sendTask task;

    public SendMsgAsyncTask(Context c) {
        this.c = c;
    }

    public static void sendMessage(final String conversation, final String currentFri, final IMessage msg, final long localId, final SendMsgListener listen,final String chatType) {
        JSONObject object = new JSONObject();
        try {
            object.put("chatType",chatType);
            object.put("sender", msg.getSender());
            if (conversation != null &&! "".equals(conversation)) {
                object.put("conversation", conversation);
            }
            object.put("receiver",Long.parseLong(currentFri));
            object.put("msgType", msg.getMsgType());
            object.put("contents", msg.getContents());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String str = object.toString();
        if (Config.isDebug) {
            Log.i(Config.TAG, "send_message:" + str);
        }
        new Thread(()->{
                HttpPost post = new HttpPost(Config.SEND_URL);
                HttpResponse httpResponse = null;
                try {
                    StringEntity entity = new StringEntity(str,
                            HTTP.UTF_8);
                    entity.setContentType("application/json");
                    post.setEntity(entity);
                    HttpClient httpClient=new DefaultHttpClient();
                    HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 5000);
                    HttpConnectionParams.setSoTimeout(httpClient.getParams(), 5000);
                    httpResponse =httpClient.execute(post);
                    int code = httpResponse.getStatusLine().getStatusCode();
                    if (Config.isDebug) {
                        Log.i(Config.TAG, "send status code:" + code);
                    }
                    if (code == 200) {
                        HttpEntity res = httpResponse.getEntity();
                        String result = EntityUtils.toString(res);
                        if (Config.isDebug) {
                            Log.i(Config.TAG, result);
                        }
                        JSONObject object1 = new JSONObject(result);
                        JSONObject obj = object1.getJSONObject("result");
                        String conversation1 = obj.get("conversation").toString();
                        String msgId = obj.get("msgId").toString();
                        Long timestamp = Long.parseLong(obj.get("timestamp").toString());
                        IMClient.getInstance().setLastMsg(conversation1, Integer.parseInt(msgId));
                        IMClient.getInstance().updateMessage(currentFri, localId, msgId, conversation1, timestamp, Config.STATUS_SUCCESS,null,msg.getMsgType());
                        if (Config.isDebug) {
                            Log.i(Config.TAG, "发送成功，消息更新！");
                        }
                        listen.onSuccess();
                    } else {
                        if (Config.isDebug) {
                            Log.i(Config.TAG, "发送失败：code " + code);
                        }
                        IMClient.getInstance().updateMessage(currentFri, localId, null, null,0, Config.STATUS_FAILED,null,msg.getMsgType());
                        listen.onFailed(code);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }).start();
    }

    public void sendMsg(String correntUser, String currentFri, IMessage msg, long localId, SendMsgListener listen) {
        task = new sendTask();
        task.execute(correntUser, currentFri, msg, localId, listen);
    }

    class sendTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            String correntUser = (String) params[0];
            String currentFri = (String) params[1];
            IMessage msg = (IMessage) params[2];
            long localId = (Long) params[3];
            SendMsgListener listen = (SendMsgListener) params[4];
            String str = JSON.toJSON(msg).toString();
            HttpPost post = new HttpPost(Config.SEND_URL);
            HttpResponse httpResponse = null;
            try {
                StringEntity entity = new StringEntity(str,
                        HTTP.UTF_8);
                entity.setContentType("application/json");
                post.setEntity(entity);
                httpResponse = new DefaultHttpClient().execute(post);
                int code = httpResponse.getStatusLine().getStatusCode();
                if (Config.isDebug) {
                    Log.i(Config.TAG, "send status code:" + code);
                }
                if (code == 200) {
                    HttpEntity res = httpResponse.getEntity();
                    String result = EntityUtils.toString(res);
                    System.out.println(result);
                    JSONObject object = new JSONObject(result);
                    JSONObject obj = object.getJSONObject("result");
                    String conversation = obj.get("conversation").toString();
                    String msgId = obj.get("msgId").toString();
                    Long timestamp = Long.parseLong(obj.get("timestamp").toString());
                    IMClient.getInstance().setLastMsg(currentFri, Integer.parseInt(msgId));
                    IMClient.getInstance().updateMessage(currentFri, localId, msgId, conversation, timestamp, Config.STATUS_SUCCESS,null,msg.getMsgType());
                    if (Config.isDebug) {
                        Log.i(Config.TAG, "发送成功，消息更新！");
                    }
                    listen.onSuccess();
                } else {
                    if (Config.isDebug) {
                        Log.i(Config.TAG, "发送失败：code " + code);
                    }
                    listen.onFailed(code);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}


