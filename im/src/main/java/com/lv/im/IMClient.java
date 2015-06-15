package com.lv.im;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiConfiguration;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.igexin.sdk.PushManager;
import com.lv.Listener.FetchListener;
import com.lv.Listener.SendMsgListener;
import com.lv.Listener.UploadListener;
import com.lv.Utils.Config;
import com.lv.Utils.CryptUtils;
import com.lv.Utils.PictureUtil;
import com.lv.Utils.TimeUtils;
import com.lv.bean.Conversation;
import com.lv.bean.ConversationBean;
import com.lv.bean.IMessage;
import com.lv.bean.Message;
import com.lv.bean.MessageBean;
import com.lv.data.MessageDB;
import com.lv.net.HttpUtils;
import com.lv.net.UploadUtils;
import com.lv.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Created by q on 2015/4/21.
 */
public class IMClient {
    private boolean isBLOCK;
    private JSONArray acklist;
    private HashMap<String, Integer> lastMsgMap;
    private volatile HashMap<String, String> cidMap;
    private static IMClient client;
    private MessageDB db;
    private static List<Conversation> conList;
    private static List<ConversationBean> convercationList= new ArrayList<>();
    //private static HashMap<String,MessageBean> messageMap;
    private int count;

    private IMClient() {
        cidMap = new HashMap<>();
        lastMsgMap = new HashMap<>();
        acklist = new JSONArray();
        conList = new ArrayList<>();
    }

    public static IMClient getInstance() {
        if (client == null) {
            client = new IMClient();
        }
        return client;
    }

    public void initDB() {
        db = MessageDB.getInstance();
        MessageDB.getInstance().init();
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
        System.out.println("ack list size:" + acklist.length());
        if (acklist.length() > 10) {
//            HttpUtils.FetchNewMsg(User.getUser().getCurrentUser(), (list) -> {
//                for (Message msg : list) {
//                    LazyQueue.getInstance().add2Temp(msg.getConversation(), msg);
//                }
//                LazyQueue.getInstance().TempDequeue();
//            });
        }
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
        convercationList = db.getConversationList();
        count = 0;
        for (ConversationBean c : convercationList) {
            count += c.getIsRead();
        }
        return convercationList;
    }

    public int getUnReadCount() {
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
        System.out.println("updateReadStatus");
    }

    public void increaseUnRead(String conversation) {
        db.updateReadStatus(conversation, 1);
        System.out.println("increaseUnRead");
    }

    public List<MessageBean> getMessages(String friendId, int page) {
        return db.getAllMsg(friendId, page);
    }


    public void sendTextMessage(MessageBean message, String conversation, SendMsgListener listen, String chatType) {
        if ("0".equals(conversation)) conversation = null;
        IMessage imessage = new IMessage(Integer.parseInt(User.getUser().getCurrentUser()), String.valueOf(message.getSenderId()), Config.TEXT_MSG, message.getMessage());
        SendMsgAsyncTask.sendMessage(conversation, String.valueOf(message.getSenderId()), imessage, message.getLocalId(), listen, chatType);
    }

    public MessageBean createTextMessage(String text, String friendId, String chatType) {
        if (TextUtils.isEmpty(text)) return null;
        IMessage message = new IMessage(Integer.parseInt(User.getUser().getCurrentUser()), friendId, Config.TEXT_MSG, text);
        MessageBean messageBean = imessage2Bean(message);
        long localId = db.saveMsg(friendId, messageBean, chatType);
        MessageBean m = new MessageBean(0, 1, 0, text, TimeUtils.getTimestamp(), 0, null, Long.parseLong(friendId));
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
    public void sendAudioMessage(MessageBean message, String path, String friendId, UploadListener listener, String chatTpe) {
        UploadUtils.getInstance().upload(path, User.getUser().getCurrentUser(), friendId, Config.AUDIO_MSG, message.getLocalId(), listener, chatTpe);
    }

    public MessageBean createAudioMessage(String path, String friendId, String durtime, String chatTpe) {
        JSONObject object = new JSONObject();
        try {
            object.put("isRead", false);
            object.put("path", path);
            object.put("duration", durtime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IMessage message = new IMessage(Integer.parseInt(User.getUser().getCurrentUser()), friendId, Config.AUDIO_MSG, object.toString());
        MessageBean messageBean = imessage2Bean(message);
        long localId = db.saveMsg(friendId, messageBean, chatTpe);
        MessageBean m = new MessageBean(0, Config.STATUS_SENDING, Config.AUDIO_MSG, messageBean.getMessage(), TimeUtils.getTimestamp(), 0, null, Long.parseLong(friendId));
        m.setLocalId((int) localId);
        return m;
    }

    public MessageBean CreateImageMessage(String path, String friendId, String chatTpe) {

        String sdkPath = PictureUtil.reSizeImage(path);
        String thumbnailPath = PictureUtil.getThumbImagePath(sdkPath, 160, 160);
        JSONObject object = new JSONObject();
        try {
            object.put("localPath", sdkPath);
            object.put("thumbPath", thumbnailPath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IMessage message = new IMessage(Integer.parseInt(User.getUser().getCurrentUser()), friendId, Config.IMAGE_MSG, object.toString());
        MessageBean messageBean = imessage2Bean(message);
        System.out.println("message " + messageBean.getMessage());
        long localId = db.saveMsg(friendId, messageBean, chatTpe);
        MessageBean m = new MessageBean(0, Config.STATUS_SENDING, Config.IMAGE_MSG, messageBean.getMessage(), TimeUtils.getTimestamp(), 0, null, Long.parseLong(friendId));
        m.setLocalId((int) localId);
        System.out.println("localId-----------"+localId);
        return m;
    }

    public void sendImageMessage(MessageBean messageBean, String friendId, UploadListener listener, String chatTpe) {
        UploadUtils.getInstance().uploadImage(messageBean, User.getUser().getCurrentUser(), friendId, Config.IMAGE_MSG, messageBean.getLocalId(), listener, chatTpe);
    }

    public void sendImageMessageByUrl(String path, Bitmap bitmap, String friendId, UploadListener listener, String chatTpe) {

    }

    /**
     * 发送位置信息
     *
     * @param message
     * @param conversation
     * @param listen
     * @param chatType
     */
    public void sendLocationMessage(MessageBean message, String conversation, SendMsgListener listen, String chatType) {
        if ("0".equals(conversation)) conversation = null;
        IMessage imessage = new IMessage(Integer.parseInt(User.getUser().getCurrentUser()), String.valueOf(message.getSenderId()), Config.LOC_MSG, message.getMessage());
        SendMsgAsyncTask.sendMessage(conversation, String.valueOf(message.getSenderId()), imessage, message.getLocalId(), listen, chatType);
    }

    public MessageBean CreateLocationMessage(String name, String conversation, String friendId, String chatType, double latitude, double longitude, String locationAddress) {
        if ("0".equals(conversation)) conversation = null;
        JSONObject object = new JSONObject();
        try {
            object.put("name", name);
            object.put("lng", latitude);
            object.put("lat", longitude);
            object.put("desc", locationAddress);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IMessage message = new IMessage(Integer.parseInt(User.getUser().getCurrentUser()), friendId, Config.TEXT_MSG, object.toString());
        MessageBean messageBean = imessage2Bean(message);
        long localId = db.saveMsg(friendId, messageBean, chatType);
        MessageBean m = new MessageBean(0, Config.STATUS_SENDING, Config.LOC_MSG, messageBean.getMessage(), TimeUtils.getTimestamp(), 0, null, Long.parseLong(friendId));
        m.setLocalId((int) localId);
        return m;
    }
    public void sendExtMessage(String friendId,String chatType,String contentJson,int type,SendMsgListener listen){
        if (TextUtils.isEmpty(contentJson)) return ;
        IMessage message = new IMessage(Integer.parseInt(User.getUser().getCurrentUser()), friendId, type+9, contentJson);
        MessageBean messageBean = imessage2Bean(message);
        long localId = db.saveMsg(friendId, messageBean, chatType);
        MessageBean m = new MessageBean(0, 1, 0, contentJson, TimeUtils.getTimestamp(), Config.TYPE_SEND, null, Long.parseLong(friendId));
        m.setLocalId((int) localId);
        //return m;
        //if ("0".equals(conversation)) conversation = null;
        IMessage imessage = new IMessage(Integer.parseInt(User.getUser().getCurrentUser()), String.valueOf(m.getSenderId()), type+9, m.getMessage());
        SendMsgAsyncTask.sendMessage(null, String.valueOf(m.getSenderId()), imessage, m.getLocalId(), listen, chatType);
    }

//    private int getExtType(int type) {
//        switch (type){
//            case 1:
//            break;
//            case 2:
//                break;
//            case 3:
//                break;
//            case 5:
//                break;
//            case 1:
//                break;
//            case 1:
//                break;
//            case 1:
//                break;
//        }
//        return 0;
//    }

    public void updateMessage(String fri_ID, long LocalId, String msgId, String conversation, long timestamp, int status, String message, int Type) {
        db.updateMsg(fri_ID, LocalId, msgId, conversation, timestamp, status, message, Type);
    }

    private MessageBean imessage2Bean(IMessage message) {

        return new MessageBean(0, Config.STATUS_SENDING, message.getMsgType(), message.getContents(), TimeUtils.getTimestamp(), Config.TYPE_SEND, null, Long.parseLong(User.getUser().getCurrentUser()));
    }

    public void saveMessage(Message message, String chatTpe) {
        MessageBean newMsg = Msg2Bean(message);
        db.saveMsg(newMsg.getSenderId() + "", newMsg, chatTpe);
        lastMsgMap.put(newMsg.getSenderId() + "", newMsg.getServerId());
        add2ackList(message.getId());
    }

    private MessageBean Msg2Bean(Message msg) {
        return new MessageBean(msg.getMsgId(), Config.STATUS_SUCCESS, msg.getMsgType(), msg.getContents(), msg.getTimestamp(), msg.getSendType(), null, msg.getSenderId());
    }

    public void ackAndFetch(FetchListener listener) {
        HttpUtils.postack(acklist, listener);
    }

    /**
     * 初始化Fetch
     */
    public void initAckAndFetch() {
        HttpUtils.postack(acklist, (list) -> {
            for (Message msg : list) {
                LazyQueue.getInstance().add2Temp(msg.getConversation(), msg);
            }
            LazyQueue.getInstance().TempDequeue();
        });
    }

    public int saveReceiveMsg(Message message) {
        int result = db.saveReceiveMsg(message.getSenderId() + "", Msg2Bean(message), message.getConversation(), message.getGroupId(), message.getChatType());
        if (result == 0) {
            setLastMsg(message.getConversation(), message.getMsgId());
        }
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
    public void cleanMessageHistory(String chatId){
        db.deleteMessage(chatId);
    }
    public void deleteSingleMessage(String chatId,long MessageId){
        db.deleteSingleMessage(chatId, MessageId);
    }
    public void changeMessageStatus(String chatId,long MessageId,int Status){
        db.changeMessagestatus(chatId, MessageId, Status);
    }
    public void updateReadStatus(String chatId,long messageId,boolean isRead){
        db.updateReadStatus(chatId,messageId,isRead);
    }
}

