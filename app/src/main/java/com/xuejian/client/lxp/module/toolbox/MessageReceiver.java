package com.xuejian.client.lxp.module.toolbox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.lv.Listener.MsgListener;
import com.lv.bean.Message;
import com.lv.im.HandleImMessage;
import com.lv.im.IMClient;
import com.lv.utils.Config;
import com.lv.utils.JsonValidator;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.db.UserDBManager;

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
    public void onReceive(final Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt("action")) {
            case PushConsts.GET_MSG_DATA:

//                Observable.just(bundle.getByteArray("payload"))
//                        .filter(new Func1<byte[], Boolean>() {
//                            @Override
//                            public Boolean call(byte[] bytes) {
//                                return bytes != null;
//                            }
//                        })
//                        .map(new Func1<byte[], String>() {
//                            @Override
//                            public String call(byte[] bytes) {
//                                return new String(bytes);
//                            }
//                        })
//                        .filter(new Func1<String, Boolean>() {
//                            @Override
//                            public Boolean call(String s) {
//                                JsonValidator jsonValidator = new JsonValidator();
//                                return jsonValidator.validate(s);
//                            }
//                        })
//                        .groupBy(new Func1<String, String>() {
//                            @Override
//                            public String call(String s) {
//                                try {
//                                    return new JSONObject(s).get("routingKey").toString();
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                    return null;
//                                }
//                            }
//                        })
//                        .subscribe(new Action1<GroupedObservable<String, String>>() {
//                            @Override
//                            public void call(GroupedObservable<String, String> stringStringGroupedObservable) {
//                                if ("IM".equals(stringStringGroupedObservable.getKey())) {
//                                    stringStringGroupedObservable
//                                            .filter(new Func1<String, Boolean>() {
//                                                @Override
//                                                public Boolean call(String s) {
//                                                    return AccountManager.getInstance().getLoginAccount(context) != null;
//                                                }
//                                            })
//                                            .subscribe(new Action1<String>() {
//                                                @Override
//                                                public void call(String s) {
//                                                    //listener.OnMessage(context, s);
//                                                }
//                                            });
//                                } else {
//
//                                }
//                            }
//                        });


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
                                    if ("IM".equals(key)) {
                                        if (AccountManager.getInstance().getLoginAccount(context) == null)
                                            return;
                                        try {
                                            CommonJson<Message> m = CommonJson.fromJson(message, Message.class);
                                            if ("group".equals(m.result.getChatType()) &&
                                                    !UserDBManager.getInstance().isGroupMember(String.valueOf(m.result.getGroupId())))
                                                return;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
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
