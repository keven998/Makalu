package com.xuejian.client.lxp.module.toolbox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.lv.Listener.MsgListener;
import com.lv.utils.Config;
import com.lv.utils.JsonValidator;
import com.lv.im.HandleImMessage;
import com.lv.im.IMClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MessageReceiver extends BroadcastReceiver {

    public static HashMap<String, ArrayList<MsgListener>> routeMap = new HashMap<String, ArrayList<MsgListener>>();

    /**
     * 注册RouteKey
     *
     * @param listener 监听
     * @param routeKey key值
     */
    public static void registerListener(MsgListener listener, String routeKey) {
        if (!routeMap.containsKey(routeKey)) {
            routeMap.put(routeKey, new ArrayList<MsgListener>());
        }
        routeMap.get(routeKey).add(listener);
    }

    static {
        registerListener(HandleImMessage.getInstance().listener, "IM");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt("action")) {
            case PushConsts.GET_MSG_DATA:
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    String data = new String(payload);
                    if (Config.isDebug) {
                        Log.i(Config.TAG, "data:" + data);
                    }
                    JsonValidator jsonValidator = new JsonValidator();
                    if (jsonValidator.validate(data)) {
                        try {
                            JSONObject object = new JSONObject(data);
                            /**
                             * 分发消息
                             */
                            String routeKey = object.getString("routingKey");
                            String message = object.getString("message");
                            for (String key : routeMap.keySet()) {
                                if (key.equals(routeKey)) {
                                    for (MsgListener listener : routeMap.get(routeKey)) {
                                        if (Config.isDebug) {
                                            Log.i(Config.TAG, "patch Message " + routeKey);
                                        }
                                        listener.OnMessage(context, message);
                                    }
                                    break;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (Config.isDebug) {
                            System.out.println("format error");
                        }
                    }
                }
                break;
            case PushConsts.GET_CLIENTID:
                String cid = bundle.getString("clientid");
                IMClient.getInstance().setCid(cid);
                if (Config.isDebug) {
                    Log.i(Config.TAG, IMClient.getInstance().getCid());
                }
                break;
            case PushConsts.THIRDPART_FEEDBACK:
                String appid = bundle.getString("appid");
                String task_id = bundle.getString("taskid");
                String actionid = bundle.getString("actionid");
                String result1 = bundle.getString("result");
                long timestamp = bundle.getLong("timestamp");
                Log.d("GetuiSdkDemo", "appid = " + appid);
                Log.d("GetuiSdkDemo", "taskid = " + task_id);
                Log.d("GetuiSdkDemo", "actionid = " + actionid);
                Log.d("GetuiSdkDemo", "result = " + result1);
                Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                break;
            default:
                break;
        }

    }

}
