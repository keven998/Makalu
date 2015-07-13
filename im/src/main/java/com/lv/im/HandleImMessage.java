package com.lv.im;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lv.Listener.DequeueListener;
import com.lv.Listener.MsgListener;
import com.lv.Utils.Config;
import com.lv.Utils.TimeUtils;
import com.lv.bean.Message;
import com.lv.bean.MessageBean;
import com.lv.service.DownloadService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HandleImMessage {
    private static HandleImMessage instance;
    LazyQueue queue = LazyQueue.getInstance();
    private Context c;
    private long lastTime;
    private static HashMap<MessageHandler, String> openStateMap = new HashMap<>();
    public static MyHandler handler = new MyHandler();

    private HandleImMessage() {
        queue.setDequeueListener(dequeueListener);
    }

    public static HandleImMessage getInstance() {
        if (instance == null) {
            instance = new HandleImMessage();
        }
        return instance;
    }

    private static ArrayList<MessageHandler> ehList = new ArrayList<>();

    public static abstract interface MessageHandler {
        /**
         * @param m 收到的消息
         */
        public void onMsgArrive(MessageBean m, String groupId);

        public void onCMDMessageArrive(MessageBean m);
    }

    /**
     * Activity注册消息listener
     *
     * @param listener listener
     */
    public void registerMessageListener(MessageHandler listener) {
        if (!ehList.contains(listener)) ehList.add(listener);

    }

    /**
     * 解除注册
     *
     * @param listener listener
     */
    public void unregisterMessageListener(MessageHandler listener) {
        ehList.remove(listener);
    }

    public void registerMessageListener(MessageHandler listener, String conversation) {
        if (!ehList.contains(listener)) {
            ehList.add(listener);
        }
        openStateMap.put(listener, conversation);
        IMClient.getInstance().updateReadStatus(conversation);
    }

    public void unregisterMessageListener(MessageHandler listener, String conversation) {
        ehList.remove(listener);
        openStateMap.clear();
    }

    public static class MyHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message message) {
            super.handleMessage(message);
            switch (message.what) {
                case Config.CMD_MSG:
                case Config.TIP_MSG:
                    Message newCMDMessage = (Message) message.obj;
                    for (MessageHandler handler : ehList) {
                        handler.onCMDMessageArrive(Msg2Bean(newCMDMessage));
                    }
                    break;
                case Config.TEXT_MSG:
                    Message newMessage = (Message) message.obj;
                    if (Config.isDebug) {
                        System.out.println(ehList.size() + "  handlerMessage " + newMessage.getContents());
                    }
                    for (MessageHandler handler : ehList) {
                        handler.onMsgArrive(Msg2Bean(newMessage), String.valueOf(newMessage.getGroupId()));
                    }
                    break;
//                case Config.LOC_MSG:
//                    Message newLocMessage = (Message) message.obj;
//                    for (MessageHandler handler : ehList) {
//                        handler.onMsgArrive(Msg2Bean(newLocMessage), String.valueOf(newLocMessage.getGroupId()));
//                    }
//                    break;
                case Config.DOWNLOAD_SUCCESS:
                case Config.DOWNLOAD_FILED:
                    Message newMediaMessage = (Message) message.obj;
                    for (MessageHandler handler : ehList) {
                        handler.onMsgArrive(Msg2Bean(newMediaMessage), String.valueOf(newMediaMessage.getGroupId()));
                    }
                    break;
                default:
                    Message extMessage = (Message) message.obj;
                    for (MessageHandler handler : ehList) {
                        handler.onMsgArrive(Msg2Bean(extMessage), String.valueOf(extMessage.getGroupId()));
                    }
                    break;
            }
        }
    }

    public MsgListener listener = new MsgListener() {
        @Override
        public void OnMessage(Context context, String message) {
            c = context;
            Message newmsg = JSON.parseObject(message, Message.class);
            newmsg.setSendType(1);
            /**
             * 处理消息重组、丢失
             */
            if (!IMClient.getInstance().isLogin()) {
                if (Config.isDebug) {
                    Log.i(Config.TAG, "not login ");
                }
                return;
            }
            queue.addMsg(newmsg.getConversation(), newmsg);
        }
    };
    /**
     * 消息处理完成，出队
     */
    DequeueListener dequeueListener = new DequeueListener() {
        @Override
        public void onDequeueMsg(Message messageBean) {
            if (Config.isDebug) {
                Log.i(Config.TAG, "onDequeueMsg ");
            }

            if (lastTime == 0) {
                lastTime = messageBean.getTimestamp();
            } else if (messageBean.getTimestamp() < lastTime) {
                messageBean.setTimestamp(TimeUtils.getTimestamp());
            }
            messageBean.setSendType(1);
            int result = IMClient.getInstance().saveReceiveMsg(messageBean);
            if (Config.isDebug) {
                Log.i(Config.TAG, "result :" + result);
            }
            if (result == 0) {
                if (messageBean.getMsgType() == 100) {
                    android.os.Message cmd_msg = android.os.Message.obtain();
                    cmd_msg.obj = messageBean;
                    cmd_msg.what = Config.CMD_MSG;
                    handler.sendMessage(cmd_msg);
                    return;
                }
                if (Config.isDebug) {
                    System.out.println("ehList size: " + ehList.size());
                }
                //  for (MessageHandler handler : ehList) {
                if (ehList.size() > 0) {
                    if (openStateMap.containsKey(ehList.get(0))) {
                        if (messageBean.getConversation().equals(openStateMap.get(ehList.get(0))))
                            IMClient.getInstance().updateReadStatus(openStateMap.get(ehList.get(0)));
                        else IMClient.getInstance().increaseUnRead(messageBean.getConversation());
                    } else IMClient.getInstance().increaseUnRead(messageBean.getConversation());
                } else {
                    notifyMsg(c, messageBean);
                    IMClient.getInstance().increaseUnRead(messageBean.getConversation());
                }

                String content = messageBean.getContents();
                JSONObject object = null;
                try {
                    switch (messageBean.getMsgType()) {
                        case Config.TEXT_MSG:
                            android.os.Message handlermsg = android.os.Message.obtain();
                            handlermsg.obj = messageBean;
                            handlermsg.what = Config.TEXT_MSG;
                            handler.sendMessage(handlermsg);
                            break;
                        /**
                         * {"snapshot":"http://7xirnn.com1.z0.glb.clouddn.com/70b4c860-60e1-4631-b3a0-d7fdf595a3dd!thumb?e=1435989212&token=jU6KkDZdGYODmrPVh5sbBIkJX65y-Cea991uWpWZ:lq1V2KaxGaTtZrlZHzWi8YlvFog=",
                         * "lat":39.99049,"lng":116.313248,"address":"北京市海淀区海淀西大街36号205号"}
                         */
                        case Config.LOC_MSG:
                            try {
                                object = new JSONObject(content);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String Murl = null;
                            try {
                                Murl = object.getString("snapshot");
                                if (Config.isDebug) {
                                    Log.i(Config.TAG, "snapshot " + Murl);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            messageBean.setUrl(Murl);
                            Intent dlM_intent = new Intent(c, DownloadService.class);
                            dlM_intent.putExtra("msg", messageBean);
                            c.startService(dlM_intent);
                            break;

                        case Config.AUDIO_MSG:
                            try {
                                object = new JSONObject(content);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String aurl = null;
                            try {
                                aurl = object.getString("url");
                                if (Config.isDebug) {
                                    Log.i(Config.TAG, "url " + aurl);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            messageBean.setUrl(aurl);
                            Intent dlA_intent = new Intent(c, DownloadService.class);
                            dlA_intent.putExtra("msg", messageBean);
                            c.startService(dlA_intent);
                            break;
                        case Config.IMAGE_MSG:
                            try {
                                object = new JSONObject(content);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String iurl = null;
                            try {
                                if (object != null) {
                                    iurl = object.getString("thumb");
                                }
                                if (Config.isDebug) {
                                    Log.i(Config.TAG, "url " + iurl);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            messageBean.setUrl(iurl);
                            Intent dlI_intent = new Intent(c, DownloadService.class);
                            dlI_intent.putExtra("msg", messageBean);
                            c.startService(dlI_intent);
                            break;
                        default:
                            android.os.Message ext_msg = android.os.Message.obtain();
                            ext_msg.obj = messageBean;
                            ext_msg.what = 10;
                            handler.sendMessage(ext_msg);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private static MessageBean Msg2Bean(Message msg) {
        return new MessageBean(msg.getMsgId(), msg.getStatus(), msg.getMsgType(), msg.getContents(), msg.getTimestamp(), msg.getSendType(), null, msg.getSenderId(),msg.getAbbrev());
    }

    public void notifyMsg(Context c, Message message) {
        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c)
                .setSmallIcon(c.getApplicationInfo().icon)
                .setWhen(System.currentTimeMillis()).setAutoCancel(true);

        // String ticker = IMUtils.getMessageDigest(message, this);
        // if(message.getType() == EMMessage.Type.TXT)
        //    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
        //设置状态栏提示
        mBuilder.setTicker(message.getAbbrev());
        mBuilder.setContentTitle("旅行派");
        mBuilder.setContentText(message.getAbbrev());
        //必须设置pendingintent，否则在2.3的机器上会有bug
        Intent intent = new Intent("android.intent.action.notify");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 11, intent, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pendingIntent);

        Notification notification = mBuilder.build();
        notificationManager.notify(11, notification);
        //   notificationManager.cancel(11);
    }

    public boolean isAppRunningForeground(Context var0) {
        ActivityManager var1 = (ActivityManager) var0.getSystemService(Context.ACTIVITY_SERVICE);
        List var2 = var1.getRunningTasks(1);
        return var0.getPackageName().equalsIgnoreCase(((ActivityManager.RunningTaskInfo) var2.get(0)).baseActivity.getPackageName());
    }
}