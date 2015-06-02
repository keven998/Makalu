package com.lv.im;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.lv.Listener.DequeueListener;
import com.lv.Listener.MsgListener;
import com.lv.Utils.Config;
import com.lv.Utils.TimeUtils;
import com.lv.bean.Message;
import com.lv.bean.MessageBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class HandleImMessage {
    private static HandleImMessage instance;
    LazyQueue queue = LazyQueue.getInstance();
    private Context c;
    private long lastTime;
    private static HashMap<MessagerHandler, String> openStateMap = new HashMap<>();
//    static {
//        Looper.prepare();
//    }
    private HandleImMessage() {
      //  MessageReceiver.registerListener(listener, "IM");
        queue.setDequeueListener(dequeueListener);
       // Looper.prepare();
    }

    public static HandleImMessage getInstance() {
        if (instance == null) {
            instance = new HandleImMessage();
        }
        return instance;
    }

    private static ArrayList<MessagerHandler> ehList = new ArrayList<>();

    public static abstract interface MessagerHandler {
        /**
         *
         * @param m 收到的消息
         */
        public void onMsgArrive(MessageBean m);
    }

    /**
     * Activity注册消息listener
     * @param listener listener
     */
    public  void registerMessageListener(MessagerHandler listener) {
        ehList.add(listener);
    }

    /**
     * 解除注册
     * @param listener listener
     */
    public  void unregisterMessageListener(MessagerHandler listener) {
        ehList.remove(listener);
    }

    public void registerMessageListener(MessagerHandler listener, String conversation) {
        ehList.add(listener);
        openStateMap.put(listener, conversation);
        IMClient.getInstance().updateReadStatus(conversation);

    }

    public  void unregisterMessageListener(MessagerHandler listener, String conversation) {
        ehList.remove(listener);
        openStateMap.clear();
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message message) {
            System.out.println("new Message");
            switch (message.what) {
                case Config.TEXT_MSG:
                    Message newMessage = (Message) message.obj;
                    System.out.println(ehList.size()+"  handlerMessage "+newMessage.getContents());
                    for (MessagerHandler handler : ehList) {
                        handler.onMsgArrive(Msg2Bean(newMessage));
                    }
                    break;
                case Config.LOC_MSG:
                    Message newLocMessage = (Message) message.obj;
                    for (MessagerHandler handler : ehList) {
                        handler.onMsgArrive(Msg2Bean(newLocMessage));
                    }
                    break;
                case Config.DOWNLOAD_SUCCESS:
                case Config.DOWNLOAD_FILED:
                    Message newMediaMessage = (Message) message.obj;
                    for (MessagerHandler handler : ehList) {
                        handler.onMsgArrive(Msg2Bean(newMediaMessage));
                    }
                    break;
            }
        }
    };

    public MsgListener listener = new MsgListener() {
        @Override
        public void OnMessage(Context context, Message message) {
            c = context;
            /**
             * 处理消息重组、丢失
             */
            queue.addMsg(message.getConversation(), message);
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

            if (lastTime==0){
                lastTime=messageBean.getTimestamp();
            }
            else if (messageBean.getTimestamp()<lastTime){
                messageBean.setTimestamp(TimeUtils.getTimestamp());
            }
            messageBean.setSendType(1);
            int result = IMClient.getInstance().saveReceiveMsg(messageBean);
            if (Config.isDebug) {
                Log.i(Config.TAG, "result :" + result);
            }
            if (result == 0) {
                System.out.println("ehList size: "+ehList.size());
              //  for (MessagerHandler handler : ehList) {
                    if (openStateMap.containsKey(ehList.get(0))) {
                         IMClient.getInstance().updateReadStatus(openStateMap.get(ehList.get(0)));
                    }
                    else IMClient.getInstance().increaseUnRead(messageBean.getConversation());
             //   }
                String content = messageBean.getContents();
                JSONObject object = null;
                switch (messageBean.getMsgType()) {
                    case Config.TEXT_MSG:
                        android.os.Message handlermsg = android.os.Message.obtain();
                        handlermsg.obj = messageBean;
                        handlermsg.what = Config.TEXT_MSG;
                        handler.sendMessage(handlermsg);
                        break;
                    case Config.LOC_MSG:
                        android.os.Message loc_msg = android.os.Message.obtain();
                        loc_msg.obj = messageBean;
                        loc_msg.what = Config.LOC_MSG;
                        handler.sendMessage(loc_msg);
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
                            if (Config.isDebug){
                                Log.i(Config.TAG,"url " + aurl);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        messageBean.setUrl(aurl);
                        Intent dlA_intent = new Intent("ACTION.IMSDK.STARTDOWNLOAD");
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
                        Intent dlI_intent = new Intent("ACTION.IMSDK.STARTDOWNLOAD");
                        dlI_intent.putExtra("msg", messageBean);
                        c.startService(dlI_intent);
                        break;
                }
            }
        }
    };
    private static MessageBean Msg2Bean(Message msg) {
        return new MessageBean(msg.getMsgId(), msg.getStatus(), msg.getMsgType(), msg.getContents(), msg.getTimestamp(), msg.getSendType(), null, msg.getSenderId());
    }
}