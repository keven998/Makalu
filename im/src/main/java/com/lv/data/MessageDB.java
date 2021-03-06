package com.lv.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.lv.bean.ConversationBean;
import com.lv.bean.InventMessage;
import com.lv.bean.MessageBean;
import com.lv.im.IMClient;
import com.lv.im.LazyQueue;
import com.lv.utils.Config;
import com.lv.utils.CryptUtils;
import com.lv.utils.TimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by q on 2015/4/21.
 */

public class MessageDB {
    private String con_table_name;
    private String fri_table_name;
    private SQLiteDatabase db;
    private static MessageDB instance;
    private String databaseFilename;
    private static final int CONVERSATION_INDEX_lastTime = 1;
    private static final int CONVERSATION_INDEX_Friend_Id = 0;
    private static final int CONVERSATION_INDEX_HASH = 2;
    private static final int CONVERSATION_INDEX_last_rec_msgId = 3;
    private static final int CONVERSATION_INDEX_isRead = 5;

    private static final int MESSAGE_INDEX_LocalId = 0;
    private static final int MESSAGE_INDEX_ServerId = 1;
    private static final int MESSAGE_INDEX_Status = 2;
    private static final int MESSAGE_INDEX_Type = 3;
    private static final int MESSAGE_INDEX_Message = 4;
    private static final int MESSAGE_INDEX_CreateTime = 5;
    private static final int MESSAGE_INDEX_SendType = 6;
    private static final int MESSAGE_INDEX_Metadata = 7;
    private static final int MESSAGE_INDEX_SenderId = 8;
    private static final String CON_SCHEMA = " (Friend_Id INTEGER PRIMARY KEY,lastTime INTEGER," +
            "HASH TEXT,last_rec_msgId INTEGER, IsRead INTEGER,conversation TEXT,chatType TEXT)";
    private static final String MSG_SCHEMA = " (LocalId INTEGER PRIMARY KEY AUTOINCREMENT,ServerId INTEGER,Status INTEGER," +
            "Type INTEGER, Message TEXT,CreateTime INTEGER, SendType INTEGER, Metadata TEXT," +
            "SenderId INTEGER)";
    private static final String REQUEST_SCHEMA = " (Id INTEGER PRIMARY KEY AUTOINCREMENT ,UserId INTEGER," +
            "nickName TEXT,avatarSmall TEXT,requestMsg TEXT,requestId TEXT,status INTEGER,time INTEGER,isRead INTEGER)";
    public static final int TIPS_TYPE = 200;
    private String request_msg_table_name;
    private AtomicInteger mOpenCounter = new AtomicInteger();
    private SQLiteDatabase mdb;
    private static String userId;
    private static final int AGREE = 1;
    private static final int DEFAULT = 0;
//    ExecutorService dbThread = Executors.newFixedThreadPool(1);

    private MessageDB(String User_Id) {
        String path = CryptUtils.getMD5String(User_Id);
        con_table_name = "con_" + path;
        fri_table_name = "fri_" + path;
        request_msg_table_name = "request_" + path;
        String DATABASE_PATH = Config.DB_PATH + path;
        databaseFilename = DATABASE_PATH + "/" + Config.MSG_DBNAME;
        File dir = new File(DATABASE_PATH);
        if (!dir.exists())
            dir.mkdir();
        db = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
    }

    public String getFilename(){
        return databaseFilename;
    }
    public static MessageDB getInstance() {
        if (instance == null) {
            instance = new MessageDB(userId);
        }
        return instance;
    }

    public static void initDB(String _userId) {
        userId = _userId;
    }

    public static void disconnectDB() {
        instance = null;
    }

    public synchronized SQLiteDatabase getDB() {
        if (mOpenCounter.incrementAndGet() == 1) {
            db = SQLiteDatabase.openDatabase(databaseFilename, null, SQLiteDatabase.OPEN_READWRITE);
        }
        return db;
    }

    public synchronized void closeDB() {
        if (mOpenCounter.decrementAndGet() == 0) {
            db.close();
        }
        mdb = null;
    }

    public void init(int version,int currentVersion) {
        mdb = getDB();
        mdb.execSQL("CREATE table IF NOT EXISTS "
                + con_table_name
                + CON_SCHEMA);
        mdb.execSQL("create index if not exists index_Con_Friend_Id on " + con_table_name + "(Friend_Id)");
        if (currentVersion<version&&version==1){
            mdb.execSQL("DROP TABLE IF EXISTS "+request_msg_table_name);
            mdb.execSQL("CREATE table IF NOT EXISTS "
                    + request_msg_table_name
                    + REQUEST_SCHEMA);
        }else {
            mdb.execSQL("CREATE table IF NOT EXISTS "
                    + request_msg_table_name
                    + REQUEST_SCHEMA);
        }
    }

    public synchronized long saveMsg(final String Friend_Id, final MessageBean entity, String chatType) {

        String table_name = "chat_" + CryptUtils.getMD5String(Friend_Id);
        mdb = getDB();
        mdb.execSQL("CREATE table IF NOT EXISTS "
                + table_name
                + MSG_SCHEMA);
        //db.execSQL("create index if not exists index_Msg_Friend_Id on " + fri_table_name + "(Friend_Id)");
        ContentValues values = new ContentValues();
        values.put("ServerId", entity.getServerId());
        values.put("Status", entity.getStatus());
        values.put("Type", entity.getType());
        values.put("Message", entity.getMessage());
        values.put("CreateTime", entity.getCreateTime());
        values.put("SendType", entity.getSendType());
        values.put("Metadata", entity.getMetadata());
        values.put("SenderId", entity.getSenderId());
        long localid = mdb.insert(table_name, null, values);
        add2Conversion(Integer.parseInt(Friend_Id), entity.getCreateTime(), table_name, entity.getServerId(), null, chatType);
        closeDB();
        return localid;
    }

    public synchronized int saveReceiveMsg(String Friend_Id, MessageBean entity, String conversation, long groupId, String chatType) {
        mdb = getDB();
        String table_name = null;
        String chater = null;
        if (Config.isDebug) {
            System.out.println("Friend_Id " + Friend_Id + " conversation " + conversation);
        }
        /**
         * CMD消息
         */
        if (entity.getType() == 100) {
            closeDB();
            return handleCMD(entity);
        }
//        else if (entity.getType() == 200) {
//            closeDB();
//            return handleTips(entity);
//        }
        /**
         * 单聊
         */
        else if ("single".equals(chatType)) {
            if (String.valueOf(entity.getSenderId()).equals(IMClient.getInstance().getCurrentUserId())) {
                table_name = "chat_" + CryptUtils.getMD5String(entity.getReceiverId() + "");
                chater = entity.getReceiverId() + "";
            } else {
                table_name = "chat_" + CryptUtils.getMD5String(entity.getSenderId() + "");
                chater = entity.getSenderId() + "";
            }
        }
        /**
         * 群聊
         */
        else if ("group".equals(chatType)) {
            table_name = "chat_" + CryptUtils.getMD5String(groupId + "");
            chater = groupId + "";
        } else {
            if (Config.isDebug) {
                Log.e(Config.TAG, "chatType is null");
            }
            if (IMClient.getInstance().isBLOCK()) {
                IMClient.getInstance().setBLOCK(false);
                LazyQueue.getInstance().TempDequeue();
            }
            closeDB();
            return 1;
        }
        /**
         * 屏蔽欢迎回来
         */
        if (entity.getSenderId() == 0) {
            if (IMClient.getInstance().isBLOCK()) {
                IMClient.getInstance().setBLOCK(false);
                LazyQueue.getInstance().TempDequeue();
            }
            closeDB();
            return 1;
        }
        switch (entity.getType()) {
            case Config.AUDIO_MSG:
                try {
                    JSONObject object = new JSONObject(entity.getMessage());
                    String url = object.getString("url");
                    String path = Config.DownLoadAudio_path + CryptUtils.getMD5String(IMClient.getInstance().getCurrentUserId()) + "/" + CryptUtils.getMD5String(url) + ".amr";
                    object.put("path", path);
                    object.put("isRead", false);
                    entity.setMessage(object.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Config.IMAGE_MSG:
                try {
                    JSONObject object = new JSONObject(entity.getMessage());
                    String url = object.getString("thumb");
                    String path = Config.DownLoadImage_path + CryptUtils.getMD5String(IMClient.getInstance().getCurrentUserId()) + "/" + CryptUtils.getMD5String(url) + ".jpeg";
                    String full = object.getString("full");
                    String localPath = Config.DownLoadImage_path + CryptUtils.getMD5String(IMClient.getInstance().getCurrentUserId()) + "/" + CryptUtils.getMD5String(full) + ".jpeg";
                    object.put("localPath", localPath);
                    object.put("thumbPath", path);
                    entity.setMessage(object.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Config.LOC_MSG:
                try {
                    JSONObject object = new JSONObject(entity.getMessage());
                    String snapShot = object.getString("snapshot");
                    String path = Config.DownLoadMap_path + CryptUtils.getMD5String(IMClient.getInstance().getCurrentUserId()) + "/" + CryptUtils.getMD5String(snapShot) + ".png";
                    object.put("path", path);
                    entity.setMessage(object.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        if (Config.isDebug) {
            Log.i(Config.TAG, "table_name " + table_name);
        }
        mdb.execSQL("CREATE table IF NOT EXISTS "
                + table_name
                + MSG_SCHEMA);
        // db.execSQL("create UNIQUE index if not exists index_Msg_Id on " + table_name + "(ServerId)");

        Cursor cursor = mdb.rawQuery("select * from " + table_name + " where ServerId=?", new String[]{entity.getServerId() + ""});
        int count = cursor.getCount();

        if (count > 0) {
            cursor.close();
            closeDB();
            return 1;
        }
        if (entity.getType() == 200) {
            cursor.close();
            closeDB();
            return handleTips(entity);
        }
        cursor.close();
        ContentValues values = new ContentValues();
        values.put("ServerId", entity.getServerId());
        values.put("Status", entity.getStatus());
        values.put("Type", entity.getType());
        values.put("Message", entity.getMessage());
        values.put("CreateTime", entity.getCreateTime());
        values.put("SendType", entity.getSendType());
        values.put("Metadata", entity.getMetadata());
        values.put("SenderId", entity.getSenderId());
        mdb.insert(table_name, null, values);
        IMClient.getInstance().setLastMsg(conversation, entity.getServerId());
        add2Conversion(Long.parseLong(chater), entity.getCreateTime(), table_name, entity.getServerId(), conversation, chatType);
        closeDB();
        return 0;
    }

    private int handleTips(MessageBean entity) {
        try {
            String cmd = entity.getMessage();
            JSONObject tips = new JSONObject(cmd);
            String groupId = tips.getString("chatGroupId");
            int tipsType = tips.getInt("tipType");
            JSONObject operator = tips.getJSONObject("operator");
            JSONArray targets = tips.getJSONArray("targets");
            StringBuilder tag = new StringBuilder();
            for (int i = 0; i < targets.length(); i++) {
                if (i > 0) tag.append("、");
                if (targets.getJSONObject(i).getInt("userId") == Integer.parseInt(IMClient.getInstance().getCurrentUserId())) {
                    tag.append("你");
                } else {
                    tag.append(targets.getJSONObject(i).getString("nickName"));
                }
            }
            if (tipsType == 2001) {
                addTips(groupId, operator.getString("nickName") + "邀请" + tag.toString() + "加入讨论组", "group",entity.getServerId(),entity.getSenderId());
            } else if (tipsType == 2002) {
                if (TextUtils.isEmpty(tag.toString())) {
                    addTips(groupId, operator.getString("nickName") + "退出了讨论组", "group",entity.getServerId(),entity.getSenderId());
                } else {
                    addTips(groupId, operator.getString("nickName") + "将" + tag.toString() + "移出了讨论组", "group",entity.getServerId(),entity.getSenderId());
                }

            } else return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int handleCMD(MessageBean entity) {
        mdb = getDB();
        try {
            String cmd = entity.getMessage();
            JSONObject object = new JSONObject(cmd);
            String action = object.getString("action");
//            switch (action) {
//            case "D_INVITE":
//                    long chatId = object.getLong("groupId");
//                    long inviteId = object.getLong("userId");
//                    String nickName = object.getString("nickName");
//                    String groupName = object.getString("groupName");
//                    addTips(String.valueOf(chatId),(nickName + "邀请你加入" + groupName + "讨论组"),"group");
//                    MessageBean m = new MessageBean();
//                    m.setMessage(nickName + "邀请你加入" + groupName + "讨论组");
//                    m.setCreateTime(TimeUtils.getTimestamp());
//                    m.setType(TIPS_TYPE);
//                    saveMsg(String.valueOf(chatId), m, "group");
//                    return 1;
//                    break;
            if ("F_ADD".equals(action)) {
                String message = object.getString("message");
                long userId = object.getLong("userId");
                String avatar = object.getString("avatar");
                String nickName = object.getString("nickName");
                String requestId = object.getString("requestId");
                Cursor cursor = mdb.rawQuery("select status from " + request_msg_table_name + " where requestId=?", new String[]{requestId});
                if (cursor.getCount() == 0) {
                    saveInventMessage(new InventMessage(userId, nickName, avatar, message, requestId, DEFAULT, TimeUtils.getTimestamp(),0));
                    cursor.close();
                    closeDB();
                    return 0;
                }
                cursor.moveToLast();
                int status = cursor.getInt(0);
                if (status == DEFAULT) {
                    cursor.close();
                    closeDB();
                    return 1;
                } else {
                    ContentValues values = new ContentValues();
                    values.put("status", DEFAULT);
                    mdb.update(request_msg_table_name, values, "requestId=?", new String[]{requestId});
                    cursor.close();
                    closeDB();
                    return 0;
                }
            } else if ("F_AGREE".equals(action)) {
                long userId = object.getLong("userId");
                if (String.valueOf(userId).equals(IMClient.getInstance().getCurrentUserId())) {
                    closeDB();
                    return 1;
                }
                String nickName = object.getString("nickName");
                addTips(String.valueOf(userId), nickName + "已同意你的添加请求，现在可以开始聊天", "single");
                closeDB();
                return 0;
            } else {
                closeDB();
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            closeDB();
            return 1;
        }
//        String table_name = "cmd_" + CryptUtils.getMD5String(100 + "");
//        mdb.execSQL("CREATE table IF NOT EXISTS "
//                + table_name
//                + MSG_SCHEMA);
//        Cursor cursor = mdb.rawQuery("select * from " + table_name + " where ServerId=?", new String[]{entity.getServerId() + ""});
//        int count = cursor.getCount();
//        if (count > 0) return 1;
//        cursor.close();
//        ContentValues values = new ContentValues();
//        values.put("ServerId", entity.getServerId());
//        values.put("Status", entity.getStatus());
//        values.put("Type", entity.getType());
//        values.put("Message", entity.getMessage());
//        values.put("CreateTime", entity.getCreateTime());
//        values.put("SendType", entity.getSendType());
//        values.put("Metadata", entity.getMetadata());
//        values.put("SenderId", entity.getSenderId());
//        mdb.insert(table_name, null, values);
//        String cmd = entity.getMessage();
//        try {
//            JSONObject object = new JSONObject(cmd);
//            String action = object.getString("action");
//            switch (action) {
//                case "D_INVITE":
//                    long chatId = object.getLong("groupId");
//                    long inviteId = object.getLong("userId");
//                    String nickName = object.getString("nickName");
//                    String groupName = object.getString("groupName");
//                    addTips(String.valueOf(chatId),(nickName + "邀请你加入" + groupName + "讨论组"),"group");
////                    MessageBean m = new MessageBean();
////                    m.setMessage(nickName + "邀请你加入" + groupName + "讨论组");
////                    m.setCreateTime(TimeUtils.getTimestamp());
////                    m.setType(TIPS_TYPE);
////                    saveMsg(String.valueOf(chatId), m, "group");
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        closeDB();
//        return 0;
    }

    public int getUnAcceptMsg() {
        mdb = getDB();
        int num = 0;
        try {
            Cursor cursor = mdb.rawQuery("select isRead from " + request_msg_table_name, null);
            while (cursor.moveToNext()) {
                int c = cursor.getInt(0);
                if (c == 0) num++;
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        closeDB();
        return num;
    }

    public synchronized void saveInventMessage(InventMessage inventMessage) {
        mdb = getDB();
        ContentValues values = new ContentValues();
        values.put("UserId", inventMessage.getUserId());
        values.put("nickName", inventMessage.getNickName());
        values.put("avatarSmall", inventMessage.getAvatarSmall());
        values.put("requestMsg", inventMessage.getRequestMsg());
        values.put("requestId", inventMessage.getRequestId());
        values.put("status", inventMessage.getStatus());
        values.put("time", inventMessage.getTime());
        values.put("isRead", 0);
        mdb.insert(request_msg_table_name, null, values);
        closeDB();

    }

    public synchronized void deleteInventMessage(String UserId) {
        mdb = getDB();
        mdb.delete(request_msg_table_name, "UserId=?", new String[]{UserId});
        closeDB();
    }

    public List<InventMessage> getInventMessages() {
        mdb = getDB();
        List<InventMessage> list = new ArrayList<InventMessage>();
        Cursor cursor = mdb.rawQuery("select * from " + request_msg_table_name, null);
        while (cursor.moveToNext()) {
            int Id = cursor.getInt(0);
            long userId = cursor.getLong(1);
            String nickName = cursor.getString(2);
            String avatarSmall = cursor.getString(3);
            String requestMsg = cursor.getString(4);
            String requestId = cursor.getString(5);
            int status = cursor.getInt(6);
            long time = cursor.getLong(7);
            int isRead = cursor.getInt(8);
            list.add(new InventMessage(Id, userId, nickName, avatarSmall, requestMsg, requestId, status, time,isRead));
        }
        cursor.close();
        closeDB();
        return list;
    }

    public synchronized void updateInventMsgReadStatus(int isRead) {
        mdb = getDB();
        ContentValues values = new ContentValues();
        values.put("isRead", isRead);
        mdb.update(request_msg_table_name, values, null, null);
        closeDB();
    }

    public synchronized void updateInventMessageStatus(long UserId, int status) {
        mdb = getDB();
        Cursor cursor = mdb.rawQuery("select * from " + request_msg_table_name + " where UserId=?", new String[]{String.valueOf(UserId)});
        if (cursor.getCount() > 0) {
            ContentValues values = new ContentValues();
            values.put("Status", status);
            mdb.update(request_msg_table_name, values, "UserId=?", new String[]{String.valueOf(UserId)});
        }
        cursor.close();
        closeDB();
    }

    public List<MessageBean> getAllMsg(String Friend_Id, int pager) {
        mdb = getDB();
        String table_name = "chat_" + CryptUtils.getMD5String(Friend_Id);
        List<MessageBean> list = new LinkedList<MessageBean>();
        // 滚动到顶端自动加载数据
        int from = 0;
        int to = (pager + 1) * 20 - 1;
        mdb.execSQL("CREATE table IF NOT EXISTS "
                + table_name
                + MSG_SCHEMA);
        Cursor c = mdb.rawQuery("SELECT * from " + table_name
                + " ORDER BY LocalId DESC LIMIT " + from + "," + to, null);
        if (c.getCount() == 0) {
            c.close();
            closeDB();
            return list;
        }
        c.moveToLast();
        if (c.getCount() > 0) {
            list.add(Curson2Message(c));
        }
        while (c.moveToPrevious()) {
            list.add(Curson2Message(c));
        }
        // updateReadStatus(Friend_Id);
        c.close();
        closeDB();
        return list;
    }

    public synchronized void updateReadStatus(String conversation, int num) {
        mdb = getDB();
        ContentValues values = new ContentValues();
        if (num == 0) {
            values.put("IsRead", num);
        }
        if (num == 1) {
            Cursor c = mdb.rawQuery("select IsRead from " + con_table_name + " where conversation='" + conversation + "'", null);
            c.moveToLast();
            int n = c.getInt(0);
            if (Config.isDebug) {
                Log.i(Config.TAG, "未读数量： " + n);
            }
            values.put("IsRead", n + 1);
            c.close();
        }
        mdb.update(con_table_name, values, " conversation=?", new String[]{conversation});
        closeDB();
    }

    public MessageBean Curson2Message(Cursor c) {
        int LocalId = c.getInt(MESSAGE_INDEX_LocalId);
        int ServerId = c.getInt(MESSAGE_INDEX_ServerId);
        int Status = c.getInt(MESSAGE_INDEX_Status);
        int Type = c.getInt(MESSAGE_INDEX_Type);
        String Message = c.getString(MESSAGE_INDEX_Message);
        long CreateTime = c.getLong(MESSAGE_INDEX_CreateTime);
        int SendType = c.getInt(MESSAGE_INDEX_SendType);
        String Metadata = c.getString(MESSAGE_INDEX_Metadata);
        int SenderId = c.getInt(MESSAGE_INDEX_SenderId);
        return new MessageBean(LocalId, ServerId, Status, Type, Message, CreateTime, SendType, Metadata, SenderId);
    }

    public synchronized void add2Conversion(long Friend_Id, long lastTime, String hash, int last_rec_msgId, String conversation, String chatType) {
        mdb = getDB();
        mdb.execSQL("CREATE table IF NOT EXISTS "
                + hash
                + MSG_SCHEMA);
        ContentValues values = new ContentValues();
        values.put("Friend_Id", Friend_Id);
        values.put("lastTime", lastTime);
        values.put("HASH", hash);
        values.put("last_rec_msgId", last_rec_msgId);
        if (conversation != null) {
            values.put("conversation", conversation);
        }
        values.put("chatType", chatType);
        Cursor cursor = mdb.rawQuery("select Friend_Id from " + con_table_name + " where Friend_Id=" + Friend_Id + "", null);
        if (cursor.getCount() == 0) {
            mdb.insert(con_table_name, null, values);
        } else mdb.update(con_table_name, values, "Friend_Id=?", new String[]{Friend_Id + ""});
        cursor.close();
        closeDB();
    }

    public synchronized List<String> getConversationIds() {
        mdb = getDB();
        List<String> list = new ArrayList<String>();
        Cursor c = mdb.rawQuery("SELECT Friend_Id FROM " + con_table_name, null);
        while (c.moveToNext()) {
            int friend_id = c.getInt(0);
            list.add(String.valueOf(friend_id));
        }
        c.close();
        closeDB();
        return list;
    }

    public synchronized List<ConversationBean> getConversationList() {
        mdb = getDB();
        List<ConversationBean> list = new ArrayList<ConversationBean>();
        Cursor c = mdb.rawQuery("SELECT * FROM " + con_table_name, null);
        while (c.moveToNext()) {
            long time = c.getLong(CONVERSATION_INDEX_lastTime);
            int friend_id = c.getInt(CONVERSATION_INDEX_Friend_Id);
            String table = c.getString(CONVERSATION_INDEX_HASH);
            int lastmsgId = c.getInt(CONVERSATION_INDEX_last_rec_msgId);
            int isRead = c.getInt(c.getColumnIndex("IsRead"));
            String conversation = c.getString(c.getColumnIndex("conversation"));
            String chatType = c.getString(c.getColumnIndex("chatType"));
            if (conversation == null) conversation = "0";
            IMClient.getInstance().setLastMsg(conversation, lastmsgId);
            /**
             * 取最后消息
             */
            //Cursor cursor = mdb.rawQuery("SELECT Message,Type,Status,SendType FROM " + table + " order by ServerId desc limit 1", null);
            Cursor cursor = mdb.rawQuery("SELECT Message,Type,Status,SendType FROM " + table + " order by LocalId desc limit 1", null);
            String lastMessage = null;
            int messageType = 0;
            int status = 0;
            int sendType = 0;
            if (cursor.getCount() > 0) {
                cursor.moveToLast();
                lastMessage = cursor.getString(0);
                messageType = cursor.getInt(1);
                status = cursor.getInt(2);
                sendType = cursor.getInt(3);
            }
            list.add(new ConversationBean(friend_id, time, table, lastmsgId, isRead, conversation, lastMessage, chatType, messageType, status, sendType));
            cursor.close();
        }
        c.close();
        closeDB();
        return list;
    }

    public synchronized void deleteConversation(String friendId) {
        mdb = getDB();
        mdb.delete(con_table_name, "Friend_Id=?", new String[]{friendId});
        closeDB();
    }

    public synchronized void deleteMessage(String friendId) {
        mdb = getDB();
        String table_name = "chat_" + CryptUtils.getMD5String(friendId);
        mdb.delete(table_name, null, null);
        closeDB();
    }

    public synchronized void updateMsg(String fri_ID, long LocalId, String msgId, String conversation, long timestamp, int status, String message, int Type) {
        mdb = getDB();
        String table_name = "chat_" + CryptUtils.getMD5String(fri_ID);
        ContentValues values = new ContentValues();
        if (msgId != null)
            values.put("ServerId", msgId);
        //values.put("conversation",conversation);
        if (message != null) {
            values.put("Message", message);
        }
        if (timestamp != 0) values.put("CreateTime", timestamp);
        values.put("Status", status);
        values.put("Type", Type);
        mdb.update(table_name, values, "LocalId=?", new String[]{LocalId + ""});
        int id;
        if (msgId == null) id = -1;
        else id = Integer.parseInt(msgId);
        updateConversation(fri_ID, conversation, id);
        closeDB();
    }

    public synchronized void updateConversation(String fri_ID, String conversation, int last_msgId) {
        mdb = getDB();
        ContentValues values = new ContentValues();
        if (conversation != null) values.put("conversation", conversation);
        if (last_msgId != -1) values.put("last_rec_msgId", last_msgId);
        if (values.size() != 0)
            mdb.update(con_table_name, values, "Friend_Id=?", new String[]{fri_ID});
        closeDB();
    }

    public synchronized void deleteSingleMessage(String fri_ID, long msgId) {
        mdb = getDB();
        String table_name = "chat_" + CryptUtils.getMD5String(fri_ID);
        mdb.delete(table_name, "LocalId=?", new String[]{String.valueOf(msgId)});
        closeDB();
    }

    public synchronized void changeMessagestatus(String fri_ID, long msgId, int status) {
        mdb = getDB();
        String table_name = "chat_" + CryptUtils.getMD5String(fri_ID);
        ContentValues values = new ContentValues();
        values.put("Status", status);
        mdb.update(table_name, values, "LocalId=?", new String[]{String.valueOf(msgId)});
        closeDB();
    }

    public synchronized void updateReadStatus(String fri_ID, long msgId, boolean status) {
        mdb = getDB();
        String table_name = "chat_" + CryptUtils.getMD5String(fri_ID);
        Cursor cursor = mdb.rawQuery("select Message from " + table_name + " where LocalId=?", new String[]{String.valueOf(msgId)});
        cursor.moveToLast();
        String result = cursor.getString(0);
        try {
            JSONObject object = new JSONObject(result);
            object.put("isRead", status);
            ContentValues values = new ContentValues();
            values.put("Message", object.toString());
            mdb.update(table_name, values, "LocalId=?", new String[]{String.valueOf(msgId)});
        } catch (Exception e) {
            e.printStackTrace();
            cursor.close();
            closeDB();
        }
        cursor.close();
        closeDB();
    }

    public int getUnReadCount() {
        if (con_table_name == null) return 0;
        mdb = getDB();
        Cursor cursor = mdb.rawQuery("select sum(isRead) from " + con_table_name, null);
        cursor.moveToLast();
        int count = cursor.getInt(0);
        cursor.close();
        closeDB();
        return count;
    }

    public synchronized void addTips(String chatId, String tips, String chatType) {
        MessageBean m = new MessageBean();
        m.setMessage(tips);
        m.setCreateTime(TimeUtils.getTimestamp());
        m.setType(TIPS_TYPE);
        saveMsg(String.valueOf(chatId), m, chatType);
    }
    public synchronized void addTips(String chatId, String tips, String chatType,long serverId, long senderId) {
        MessageBean m = new MessageBean();
        m.setMessage(tips);
        m.setCreateTime(TimeUtils.getTimestamp());
        m.setSenderId(serverId);
        m.setSenderId(senderId);
        m.setType(TIPS_TYPE);
        saveMsg(String.valueOf(chatId), m, chatType);
    }
    public ArrayList<String> getAllPics(String chatId){
        ArrayList<String> pics = new ArrayList<>();
        if (TextUtils.isEmpty(chatId)) return pics;
        String table_name = "chat_" + CryptUtils.getMD5String(chatId);
        mdb = getDB();
        Cursor cursor = mdb.rawQuery("select Message,SendType from " + table_name + " where Type=?", new String[]{String.valueOf(2)});
        while (cursor.moveToNext()) {
            String message = cursor.getString(0);
            int sendType = cursor.getInt(1);
            if (sendType == 1) {
                try {
                    JSONObject object = new JSONObject(message);
                    String url = object.getString("full");
                    pics.add(url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    JSONObject object = new JSONObject(message);
                    String url = object.getString("localPath");
                    url = "file://" + url;
                    pics.add(url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        cursor.close();
        closeDB();
        return pics;
    }
}
