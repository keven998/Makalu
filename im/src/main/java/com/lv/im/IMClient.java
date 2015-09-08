package com.lv.im;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.igexin.sdk.PushManager;
import com.lv.Listener.FetchListener;
import com.lv.Listener.HttpCallback;
import com.lv.Listener.UploadListener;
import com.lv.utils.Config;
import com.lv.utils.CryptUtils;
import com.lv.utils.PictureUtil;
import com.lv.utils.TimeUtils;
import com.lv.bean.Conversation;
import com.lv.bean.ConversationBean;
import com.lv.bean.InventMessage;
import com.lv.bean.Message;
import com.lv.bean.MessageBean;
import com.lv.bean.SendMessageBean;
import com.lv.data.MessageDB;
import com.lv.net.HttpUtils;
import com.lv.net.UploadUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by q on 2015/4/21.
 */
public class IMClient {
    private boolean isBLOCK;
    private JSONArray acklist;
    private ConcurrentHashMap<String, Integer> lastMsgMap;
    private volatile HashMap<String, String> cidMap;
    private static IMClient client;
    private MessageDB db;
    private static List<Conversation> conList;
    private static List<ConversationBean> convercationList = new ArrayList<>();
    private int count;
    public static HashMap<String, ArrayList<Long>> taskMap = new HashMap<>();
    private static List<String> invokeStatus = new ArrayList<>();
    private static ConcurrentHashMap<String, Integer> prograssMap = new ConcurrentHashMap<>();
    private Timer timer;
    private volatile boolean isRunning;
    public int p;
    public static long lastSuccessFetch;
    private Context mContext;

    public boolean isLogin() {
        return isLogin;
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    private boolean isLogin;
    private CountFrequency countFrequency;

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    private String currentUserId;

    private IMClient() {
        cidMap = new HashMap<>();
        lastMsgMap = new ConcurrentHashMap<>();
        acklist = new JSONArray();
        conList = new ArrayList<>();
        countFrequency = new CountFrequency();
    }

    public static IMClient getInstance() {
        if (client == null) {
            client = new IMClient();
        }
        return client;
    }

    public void initDB(String userId, int version, int currentVersion) {
        MessageDB.initDB(userId);
        db = MessageDB.getInstance();
        MessageDB.getInstance().init(version, currentVersion);
        isLogin = true;
    }

    public void disconnectDB() {
        db = null;
    }

    public static void initIM(Context context) {
        PushManager.getInstance().initialize(context.getApplicationContext());
        if (Config.isDebug) {
            Log.i(Config.TAG, "start initialize");
        }
    }

    public JSONArray getackList() {
        return acklist;
    }

    public int getackListsize() {
        return acklist.length();
    }

    public void add2ackList(String id) {
        acklist.put(id);
//        if (!isRunning) {
//            ack(countFrequency.getFrequency() * 5);
//        }


//        if (acklist.length() > 10) {
//            HttpUtils.FetchNewMsg(User.getUser().getCurrentUser(), (list) -> {
//                for (Message msg : list) {
//                    LazyQueue.getInstance().add2Temp(msg.getConversation(), msg);
//                }
//                LazyQueue.getInstance().TempDequeue();
//            });
//        }

    }

    public void ack(long frequency) {
        if (Config.isDebug) {
            Log.i(Config.TAG, "ACK  频率" + frequency);
        }
        if (frequency == 0) frequency = 60 * 1000;
        if (isRunning) return;
        isRunning = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ackAndFetch(new FetchListener() {
                    @Override
                    public void OnMsgArrive(List<Message> list) {

                        for (Message msg : list) {
                            LazyQueue.getInstance().add2Temp(msg.getConversation(), msg);
                        }
                        if (list.size() > 0) LazyQueue.getInstance().TempDequeue();
                    }
                });
            }
        }, frequency, frequency);


//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                ackAndFetch(new FetchListener() {
//                    @Override
//                    public void OnMsgArrive(List<Message> list) {
//                        /**
//                         *
//                         * 风险
//                         *
//                         */
////                        if (Config.isDebug) {
////                            Log.i(Config.TAG, "ACK  result");
////                        }
////                        for (Message msg : list) {
////                            LazyQueue.getInstance().add2Temp(msg.getConversation(), msg);
////                        }
////                        LazyQueue.getInstance().TempDequeue();
////                        isRunning = false;
//                    }
//                });
//            }
//        }, frequency);
    }

    public void clearackList() {
        acklist = new JSONArray();
    }

    public boolean isBLOCK() {
        return isBLOCK;
    }

    public void setBLOCK(boolean isBLOCK) {
        this.isBLOCK = isBLOCK;
    }

    public int getLastMsg(String conversation) {
        if (lastMsgMap.get(conversation) != null)
            return lastMsgMap.get(conversation);
        else
            return -1;
    }

    public void setLastMsg(String conversation, int msgId) {
        if (!lastMsgMap.containsKey(conversation)) {
            lastMsgMap.put(conversation, -1);
        }
        int temp = lastMsgMap.get(conversation);
        if (temp > msgId) return;
        lastMsgMap.put(conversation, msgId);
    }

    public String getCid() {
        return cidMap.get("cid");
    }

    public void setCid(String cid) {
        cidMap.put("cid", cid);
    }

    /**
     * @return List<ConversationBean>
     */
    public List<ConversationBean> getConversationList() {
        if (db == null) {
            return convercationList;
        }
        convercationList = db.getConversationList();
//        count = 0;
//        for (ConversationBean c : convercationList) {
//            count += c.getIsRead();
//        }
        return convercationList;
    }

    public int getUnReadCount() {
        count = 0;
        if (convercationList != null) {
            for (ConversationBean c : convercationList) {
                count += c.getIsRead();
            }
        }
        return count;
    }

    public void deleteConversation(String friend) {
        db.deleteConversation(friend);
    }

    public void add2ConversationList(ConversationBean conversationBean) {
        boolean flag = true;
        for (int i = 0; i < convercationList.size(); i++) {
            if (convercationList.get(i).getFriendId() == conversationBean.getFriendId()) {
                convercationList.set(i, conversationBean);
                flag = false;
                break;
            }
        }
        if (flag) convercationList.add(conversationBean);
    }

    public void addToConversationList(Conversation conversation) {
        boolean flag = false;
        for (Conversation c : conList) {
            if (c.getFriendId() == conversation.getFriendId()) {
                flag = true;
                c = conversation;
                break;
            }
        }
        if (!flag) conList.add(conversation);
    }

    public void updateReadStatus(String conversation) {
        db.updateReadStatus(conversation, 0);
    }

    public void increaseUnRead(String conversation) {
        db.updateReadStatus(conversation, 1);
    }

    public List<MessageBean> getMessages(String friendId, int page) {
        List<MessageBean> list = db.getAllMsg(friendId, page);
        if (!invokeStatus.contains(friendId)) {
            for (MessageBean m : list) {
                if (m.getStatus() == 1) {
                    m.setStatus(2);
                    updateMessage(friendId, m.getLocalId(), null, null, 0, Config.STATUS_FAILED, null, m.getType());
                }
            }
            invokeStatus.add(friendId);
            return list;
        }
//        System.out.println("invoke !");
//        if (taskMap != null && taskMap.containsKey(friendId)) {
//            List<Long> taskIds = taskMap.get(friendId);
//            for (MessageBean m : list) {
//                if (m.getStatus() == 1 && !taskIds.contains(m.getLocalId())) {
//                    m.setStatus(2);
//                    System.out.println("setFailed " + m.getLocalId());
//                }
//            }
//            return list;
//        } else {
//            for (MessageBean m : list) {
//                if (m.getStatus() == 1){
//                    m.setStatus(2);
//                    System.out.println("setFailed111 " + m.getLocalId());
//                }
//
//            }
//            return list;
//        }
        return list;
    }

    public String getDBFilename() {
        return db.getFilename();
    }

    public void sendTextMessage(MessageBean message, String friendId, String conversation, HttpCallback listen, String chatType) {
        if ("0".equals(conversation)) conversation = null;
        SendMessageBean imessage = new SendMessageBean((int) message.getSenderId(), friendId, Config.TEXT_MSG, message.getMessage());
        if (Config.isDebug) {
            System.out.println("message.getSenderId()  ====" + message.getSenderId());
        }
        HttpUtils.sendMessage(conversation, friendId, imessage, message.getLocalId(), listen, chatType);
    }

    public MessageBean createTextMessage(String UserId, String text, String friendId, String chatType) {
        if (TextUtils.isEmpty(text)) return null;
        SendMessageBean message = new SendMessageBean(Integer.parseInt(UserId), friendId, Config.TEXT_MSG, text);
        MessageBean messageBean = imessage2Bean(message);
        long localId = db.saveMsg(friendId, messageBean, chatType);
        //   MessageBean m = new MessageBean(0, 1, 0, text, TimeUtils.getTimestamp(), 0, null, Long.parseLong(friendId));
        MessageBean m = new MessageBean(0, Config.STATUS_SENDING, 0, text, TimeUtils.getTimestamp(), Config.TYPE_SEND, null, messageBean.getSenderId());
        m.setLocalId((int) localId);
        return m;
    }

    /**
     * 发送语音消息
     *
     * @param path     路径
     * @param friendId friendId
     * @param listener listener
     * @param chatTpe  聊天类型
     */
    public void sendAudioMessage(String userId, MessageBean message, String path, String friendId, UploadListener listener, String chatTpe) {
        if (taskMap.containsKey(friendId)) {
            if (taskMap.get(friendId).contains(message.getLocalId())) return;
            else taskMap.get(friendId).add(message.getLocalId());
        } else {
            taskMap.put(friendId, new ArrayList<Long>());
            taskMap.get(friendId).add(message.getLocalId());
        }
        UploadUtils.getInstance().upload(path, userId, friendId, Config.AUDIO_MSG, message.getLocalId(), listener, chatTpe, 0, 0, null);
    }

    public MessageBean createAudioMessage(String userId, String path, String friendId, String durtime, String chatTpe) {
        JSONObject object = new JSONObject();
        try {
            object.put("isRead", false);
            object.put("path", path);
            object.put("duration", durtime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendMessageBean message = new SendMessageBean(Integer.parseInt(userId), friendId, Config.AUDIO_MSG, object.toString());
        MessageBean messageBean = imessage2Bean(message);
        long localId = db.saveMsg(friendId, messageBean, chatTpe);
        MessageBean m = new MessageBean(0, Config.STATUS_SENDING, Config.AUDIO_MSG, messageBean.getMessage(), TimeUtils.getTimestamp(), 0, null, Long.parseLong(userId));
        m.setLocalId((int) localId);
        return m;
    }

    public MessageBean CreateImageMessage(String userId, String path, String friendId, String chatTpe) {
        try {
            String sdkPath = PictureUtil.reSizeImage(path);
            String thumbnailPath = PictureUtil.getThumbImagePath(sdkPath, 160, 160);
            JSONObject object = new JSONObject();
            try {
                object.put("localPath", sdkPath);
                object.put("thumbPath", thumbnailPath);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SendMessageBean message = new SendMessageBean(Integer.parseInt(userId), friendId, Config.IMAGE_MSG, object.toString());
            MessageBean messageBean = imessage2Bean(message);
            if (Config.isDebug) {
                System.out.println("message " + messageBean.getMessage());
            }
            long localId = db.saveMsg(friendId, messageBean, chatTpe);
            MessageBean m = new MessageBean(0, Config.STATUS_SENDING, Config.IMAGE_MSG, messageBean.getMessage(), TimeUtils.getTimestamp(), 0, null, Long.parseLong(userId));
            m.setLocalId((int) localId);
            return m;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void sendImageMessage(String userId, MessageBean messageBean, String friendId, UploadListener listener, String chatTpe) {
        if (taskMap.containsKey(friendId)) {
            if (taskMap.get(friendId).contains(messageBean.getLocalId())) return;
            else taskMap.get(friendId).add(messageBean.getLocalId());
        } else {
            taskMap.put(friendId, new ArrayList<Long>());
            taskMap.get(friendId).add(messageBean.getLocalId());
        }
        UploadUtils.getInstance().uploadImage(messageBean, userId, friendId, Config.IMAGE_MSG, messageBean.getLocalId(), listener, chatTpe);
    }

    public void sendImageMessageByUrl(String path, Bitmap bitmap, String friendId, UploadListener listener, String chatTpe) {

    }

    /**
     * 发送位置信息
     */
    public void sendLocationMessage(String userId, MessageBean message, String path, String friendId, UploadListener listener, String chatTpe, double lat, double lng, String desc) {
        if (taskMap.containsKey(friendId)) {
            if (taskMap.get(friendId).contains(message.getLocalId())) return;
            else taskMap.get(friendId).add(message.getLocalId());
        } else {
            taskMap.put(friendId, new ArrayList<Long>());
            taskMap.get(friendId).add(message.getLocalId());
        }
        UploadUtils.getInstance().upload(path, userId, friendId, Config.LOC_MSG, message.getLocalId(), listener, chatTpe, lat, lng, desc);
    }

    public MessageBean CreateLocationMessage(String userID, String conversation, String friendId, String chatType, double latitude, double longitude, String locationAddress, String path) {
        if ("0".equals(conversation)) conversation = null;
        JSONObject object = new JSONObject();
        try {
            object.put("lng", longitude);
            object.put("lat", latitude);
            object.put("address", locationAddress);
            object.put("path", path);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendMessageBean message = new SendMessageBean(Integer.parseInt(userID), friendId, Config.LOC_MSG, object.toString());
        MessageBean messageBean = imessage2Bean(message);
        long localId = db.saveMsg(friendId, messageBean, chatType);
        MessageBean m = new MessageBean(0, Config.STATUS_SENDING, Config.LOC_MSG, messageBean.getMessage(), TimeUtils.getTimestamp(), Config.TYPE_SEND, null, Long.parseLong(userID));
        m.setLocalId((int) localId);
        return m;
    }

    public void sendExtMessage(String UserId, String friendId, String chatType, String contentJson, int type, HttpCallback listen) {
        if (TextUtils.isEmpty(contentJson)) return;
        SendMessageBean message = new SendMessageBean(Integer.parseInt(UserId), friendId, type + 9, contentJson);
        MessageBean messageBean = imessage2Bean(message);
        long localId = db.saveMsg(friendId, messageBean, chatType);
        MessageBean m = new MessageBean(0, Config.STATUS_SENDING, 0, contentJson, TimeUtils.getTimestamp(), Config.TYPE_SEND, null, messageBean.getSenderId());
        m.setLocalId((int) localId);
        //return m;
        //if ("0".equals(conversation)) conversation = null;
        SendMessageBean imessage = new SendMessageBean(Integer.parseInt(UserId), friendId, type + 9, m.getMessage());
        HttpUtils.sendMessage(null, friendId, imessage, m.getLocalId(), listen, chatType);
    }


    public void updateMessage(String fri_ID, long LocalId, String msgId, String conversation, long timestamp, int status, String message, int Type) {
        db.updateMsg(fri_ID, LocalId, msgId, conversation, timestamp, status, message, Type);
    }

    private MessageBean imessage2Bean(SendMessageBean message) {

        return new MessageBean(0, Config.STATUS_SENDING, message.getMsgType(), message.getContents(), TimeUtils.getTimestamp(), Config.TYPE_SEND, null, Long.parseLong(IMClient.getInstance().getCurrentUserId()));
    }

    public void saveMessage(Message message, String chatTpe) {
        MessageBean newMsg = Msg2Bean(message);
        db.saveMsg(newMsg.getSenderId() + "", newMsg, chatTpe);
        lastMsgMap.put(newMsg.getSenderId() + "", newMsg.getServerId());
        add2ackList(message.getId());
    }

    public void addToConversation(String chatId, String chatType) {
        db.add2Conversion(Long.parseLong(chatId), TimeUtils.getTimestamp(), "con_" + CryptUtils.getMD5String(chatId), 0, null, chatType);
    }

    private MessageBean Msg2Bean(Message msg) {
        return new MessageBean(msg.getMsgId(), Config.STATUS_SUCCESS, msg.getMsgType(), msg.getContents(), msg.getTimestamp(), msg.getSendType(), null, msg.getSenderId(), msg.getReceiverId());
    }

    public void ackAndFetch(FetchListener listener) {
        HttpUtils.postAck(lastSuccessFetch, listener);
    }

    /**
     * 初始化Fetch
     */
    public void initAckAndFetch() {
        HttpUtils.postAck(lastSuccessFetch, (list) -> {
            for (Message msg : list) {
                LazyQueue.getInstance().add2Temp(msg.getConversation(), msg);
            }
            LazyQueue.getInstance().TempDequeue();
            ack(60 * 1000);
        });
    }

    public int saveReceiveMsg(Message message) {
        countFrequency.addMessage();
        int result = db.saveReceiveMsg(message.getSenderId() + "", Msg2Bean(message), message.getConversation(), message.getGroupId(), message.getChatType());
        if (result == 0 ) {
            setLastMsg(message.getConversation(), message.getMsgId());
        }
        //       IMClient.lastSuccessFetch = message.getTimestamp();
        add2ackList(message.getId());
        return result;
    }

//    public void saveMessages(List<Message> list) {
//        List<MessageBean> list1 = new ArrayList<>();
//        for (Message message : list) {
//            list1.add(Msg2Bean(message));
//            System.out.println(message.getMsgId());
//        }
//        db.saveMsgs(list1);
//    }

    public void addGroup2Conversation(String groupId, String conversation) {
        db.add2Conversion(Long.parseLong(groupId), TimeUtils.getTimestamp(), "chat_" + CryptUtils.getMD5String(groupId), -1, conversation, "group");
    }

    public void cleanMessageHistory(String chatId) {
        db.deleteMessage(chatId);
    }

    public void deleteSingleMessage(String chatId, long MessageId) {
        db.deleteSingleMessage(chatId, MessageId);
    }

    public void changeMessageStatus(String chatId, long MessageId, int Status) {
        db.changeMessagestatus(chatId, MessageId, Status);
    }

    public void updateReadStatus(String chatId, long messageId, boolean isRead) {
        db.updateReadStatus(chatId, messageId, isRead);
    }

    public int getUnReadMsgCount() {
        return db.getUnReadCount();
    }

    public void addTips(String chatId, String tips, String chatType) {
        db.addTips(chatId, tips, chatType);
    }

    public void savePrograss(String id, int prograss) {
        prograssMap.put(id, prograss);
    }

    public int getProgress(String id) {
        if (prograssMap.containsKey(id)) {
            return prograssMap.get(id);
        } else {
            prograssMap.put(id, 0);
            return 0;
        }
    }

    public List<InventMessage> getInventMessages() {
        return db.getInventMessages();
    }

    public void deleteInventMessage(String UserId) {
        db.deleteInventMessage(UserId);
    }

    public int getUnAcceptMsg() {
        return db.getUnAcceptMsg();
    }

    public void updateInventMsgStatus(long userId, int status) {
        db.updateInventMessageStatus(userId, status);
    }

    public void updateInventMsgReadStatus(int isRead) {
        db.updateInventMsgReadStatus(isRead);
    }

    public void muteConversation(String conversation, boolean value, HttpCallback callback) {
        HttpUtils.muteConversation(conversation, value, callback);
    }

    public static void login(String UserId, HttpCallback callback) {
        HttpUtils.login(UserId, callback);
    }

    public void getConversationAttrs(String userId, List<String> chatIds, final HttpCallback callback) {
        HttpUtils.getConversationAttrs(userId, chatIds, callback);
    }

    public List<String> getConversationIds() {
        try {
            return db.getConversationIds();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }

    }

    public void getConversationAttr(String userId, String friendId, HttpCallback callback) {
        HttpUtils.getConversationAttr(userId, friendId, callback);
    }

    public boolean isDbEmpty() {
        return db == null;
    }

    public ArrayList<String> getPics(String chatId) {
        return db.getAllPics(chatId);
    }

    public void logout() {
        isLogin = false;
        db.disconnectDB();
        currentUserId = null;
        client = null;
    }
}

