package com.xuejian.client.lxp.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.lv.utils.Config;
import com.lv.utils.CryptUtils;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.utils.HanziToPinyin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yibiao.qin on 2015/5/30.
 */
public class UserDBManager {
    private SQLiteDatabase db;
    private String fri_table_name;
    private String databaseFilename;
    private static UserDBManager instance;
    private SQLiteDatabase mdb;
    private AtomicInteger mOpenCounter = new AtomicInteger();
    private static int FRIEND = 1;
    private static int GROUP = 8;
    private static int QUIT_GROUP = 16;

    private UserDBManager() {
    }

    public void initDB(String User_Id) {
        String path = CryptUtils.getMD5String(User_Id);
        fri_table_name = "FRI_" + path;
        String DATABASE_PATH = Config.DB_PATH + path;
        databaseFilename = DATABASE_PATH + "/" + "lxp.db";
        File dir = new File(DATABASE_PATH);
        if (!dir.exists())
            dir.mkdir();
        db = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);

        db.execSQL("CREATE table IF NOT EXISTS "
                + fri_table_name
                + " (userId INTEGER PRIMARY KEY,nickName TEXT,avatar TEXT,avatarSmall TEXT,gender TEXT,signature TEXT,tel TEXT,secToken TEXT,countryCode TEXT,email TEXT,memo TEXT,travelStatus TEXT,residence TEXT,level TEXT,zodiac TEXT,birthday TEXT," +
                "tracks TEXT,guideCnt INTEGER,Type INTEGER,ext TEXT,header TEXT)");
    }

    public static UserDBManager getInstance() {
        if (instance == null) {
            instance = new UserDBManager();
        }
        return instance;
    }

    public void disconnectDB() {
        //db.close();
        db = null;
        instance = null;
    }

    public synchronized SQLiteDatabase getDB() {
        if (mOpenCounter.incrementAndGet() == 1) {
            if (TextUtils.isEmpty(databaseFilename)){
                String path = CryptUtils.getMD5String(AccountManager.getCurrentUserId());
                fri_table_name = "FRI_" + path;
                String DATABASE_PATH = Config.DB_PATH + path;
                databaseFilename = DATABASE_PATH + "/" + "lxp.db";
            }
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

    public long getGroupCreater(long groupId) {
        mdb = getDB();
        Cursor cursor = mdb.rawQuery("select ext from " + fri_table_name + " where userId=?", new String[]{String.valueOf(groupId)});
        String data = null;
        while (cursor.moveToNext()) {
            data = cursor.getString(0);
        }
        closeDB();
        try {
            JSONObject object = new JSONObject(data);
            return object.getLong("creator");
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<Long> getGroupMemberId(long groupId) {
        mdb = getDB();
        List<Long> list = new ArrayList<>();
        Cursor cursor = mdb.rawQuery("select ext from " + fri_table_name + " where userId=?", new String[]{String.valueOf(groupId)});
        String data = null;
        while (cursor.moveToNext()) {
            data = cursor.getString(0);
        }

        try {
            JSONArray array = (JSONArray) new JSONObject(data).get("GroupMember");
            for (int i = 0; i < array.length(); i++) {
                list.add(array.getLong(i));
            }
            cursor.close();
            closeDB();
            return list;
        } catch (JSONException e) {
            cursor.close();
            e.printStackTrace();
            closeDB();
            return null;
        }
    }

    public List<User> getGroupMember(long groupId) {
        mdb = getDB();
        List<User> list = new ArrayList<>();
        Cursor cursor = mdb.rawQuery("select ext from " + fri_table_name + " where userId=?", new String[]{String.valueOf(groupId)});
        String data = null;
        while (cursor.moveToNext()) {
            data = cursor.getString(0);
        }
        if (data != null && !"".equals(data)) {
            try {
                JSONArray userlist = new JSONArray((new JSONObject(data).get("GroupMember")).toString());
                for (int i = 0; i < userlist.length(); i++) {
                    User user = getContactByUserId(userlist.getLong(i));
                    if (user == null) return list;
                    list.add(user);
                }
                cursor.close();
                closeDB();
                return list;
            } catch (JSONException e) {
                e.printStackTrace();
                cursor.close();
                closeDB();
                return list;
            }
        }
        cursor.close();
        closeDB();
        return list;
    }

    public synchronized void deleteContact(long userId) {
        mdb = getDB();
        mdb.delete(fri_table_name, "userId=?", new String[]{String.valueOf(userId)});
        closeDB();
    }

    public synchronized void updateUserType(long userId, int type, String action) {

    }

    public User getContactByUserId(long _id) {
        mdb = getDB();
        Cursor cursor = mdb.rawQuery("select * from " + fri_table_name + " where userId=?", new String[]{String.valueOf(_id)});
        if (cursor.getCount() == 0) {
            closeDB();
            return null;
        }
        cursor.moveToLast();
        long userId = cursor.getLong(0);
        String nickName = cursor.getString(1);
        String avatar = cursor.getString(2);
        String avatarSmall = cursor.getString(3);
        String gender = cursor.getString(4);
        String signature = cursor.getString(5);
        String tel = cursor.getString(6);
        String secToken = cursor.getString(7);
        String countryCode = cursor.getString(8);
        String email = cursor.getString(9);
        String memo = cursor.getString(10);
        String travelStatus = cursor.getString(11);
        String residence = cursor.getString(12);
        String level = cursor.getString(13);
        String zodiac = cursor.getString(14);
        String birthday = cursor.getString(15);
        String tracks = cursor.getString(16);
        int guideCnt = cursor.getInt(17);
        int Type = cursor.getInt(18);
        String ext = cursor.getString(19);
        String header = cursor.getString(20);
        cursor.close();
        closeDB();
        return new User(userId, nickName, avatar, avatarSmall, gender, signature, tel, secToken, countryCode,
                email, memo, travelStatus, residence, level, zodiac, birthday, guideCnt, Type, ext, header);
    }


    public boolean isMyFriend(long userId) {
        mdb = getDB();
        Cursor cursor = mdb.rawQuery("select Type from " + fri_table_name + " where userId=?", new String[]{String.valueOf(userId)});
        if (cursor.getCount() == 0) {
            closeDB();
            return false;
        }
        cursor.moveToLast();
        int type = cursor.getInt(0);
        cursor.close();
        closeDB();
        return (type & 1) == 1;
    }

    public List<User> getContactListWithoutGroup() {
        mdb = getDB();
        List<User> list = new ArrayList<User>();
        Cursor cursor = mdb.rawQuery("select * from " + fri_table_name, null);
        while (cursor.moveToNext()) {
            long userId = cursor.getLong(0);
            String nickName = cursor.getString(1);
            String avatar = cursor.getString(2);
            String avatarSmall = cursor.getString(3);
            String gender = cursor.getString(4);
            String signature = cursor.getString(5);
            String tel = cursor.getString(6);
            String secToken = cursor.getString(7);
            String countryCode = cursor.getString(8);
            String email = cursor.getString(9);
            String memo = cursor.getString(10);
            String travelStatus = cursor.getString(11);
            String residence = cursor.getString(12);
            String level = cursor.getString(13);
            String zodiac = cursor.getString(14);
            String birthday = cursor.getString(15);
            String tracks = cursor.getString(16);
            int guideCnt = cursor.getInt(17);
            int Type = cursor.getInt(18);
            String ext = cursor.getString(19);
            String header = cursor.getString(20);
            if (((Type & 1) == 1) && ((Type & 8) != 8) && userId != 2 && userId != 3) {
                list.add(new User(userId, nickName, avatar, avatarSmall, gender, signature, tel, secToken, countryCode,
                        email, memo, travelStatus, residence, level, zodiac, birthday, guideCnt, Type, ext, header));
            }
        }
        cursor.close();
        closeDB();
        return list;
    }

    public boolean isGroup(long userId) {
        mdb = getDB();
        Cursor cursor = mdb.rawQuery("select Type from " + fri_table_name + " where userId=?", new String[]{String.valueOf(userId)});
        int type = cursor.getInt(0);
        cursor.close();
        closeDB();
        return (type & 8) == 8;
    }

    public boolean isMaster(long userId) {
        mdb = getDB();
        Cursor cursor = mdb.rawQuery("select Type from " + fri_table_name + " where userId=?", new String[]{String.valueOf(userId)});
        int type = cursor.getInt(0);
        cursor.close();
        closeDB();
        return (type & 2) == 2;
    }

    public synchronized void quiteGroup(String groupId) {
        mdb = getDB();
        Cursor cursor = mdb.rawQuery("select Type from " + fri_table_name + " where userId=?", new String[]{String.valueOf(groupId)});
        cursor.moveToLast();
        int type = cursor.getInt(0);
        cursor.close();
        type = type | 16;
        ContentValues values = new ContentValues();
        values.put("Type", type);
        mdb.update(fri_table_name, values, "userId=?", new String[]{String.valueOf(groupId)});
        closeDB();
    }

    public synchronized boolean isGroupMember(String groupId) {
        try {
            mdb = getDB();
            Cursor cursor = mdb.rawQuery("select Type from " + fri_table_name + " where userId=?", new String[]{String.valueOf(groupId)});
            cursor.moveToLast();
            int type = cursor.getInt(0);
            cursor.close();
            closeDB();
            return (type & QUIT_GROUP) == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public synchronized void saveContact(User user) {
        if (user == null) {
            return;
        }
        mdb = getDB();
        if (user.getNickName() == null || "".equals(user.getNickName())) {
            user.setHeader("#");
        } else if (" ".equals(user.getNickName().substring(0, 1))) {
            user.setHeader("#");
        } else {
            String headerName = user.getNickName();
            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(
                    0, 1).toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
        Cursor cursor = mdb.rawQuery("select * from " + fri_table_name + " where userId=?", new String[]{String.valueOf(user.getUserId())});
        if (cursor.getCount() > 0) {
            ContentValues values = new ContentValues();
            if (user.getUserId() != null) values.put("userId", user.getUserId());
            if (user.getNickName() != null) values.put("nickName", user.getNickName());
            if (user.getAvatar() != null) values.put("avatar", user.getAvatar());
            if (user.getAvatarSmall() != null) values.put("avatarSmall", user.getAvatarSmall());
            if (user.getGender() != null) values.put("gender", user.getGender());
            if (user.getSignature() != null) values.put("signature", user.getSignature());
            if (user.getTel() != null) values.put("tel", user.getTel());
            if (user.getSecToken() != null) values.put("secToken", user.getSecToken());
            if (user.getCountryCode() != null) values.put("countryCode", user.getCountryCode());
            if (user.getEmail() != null) values.put("email", user.getEmail());
            if (user.getMemo() != null) values.put("memo", user.getMemo());
            if (user.getTravelStatus() != null) values.put("travelStatus", user.getTravelStatus());
            if (user.getResidence() != null) values.put("residence", user.getResidence());
            if (user.getLevel() != null) values.put("level", user.getLevel());
            if (user.getZodiac() != null) values.put("zodiac", user.getZodiac());
            if (user.getBirthday() != null) values.put("birthday", user.getBirthday());
//            values.put("tracks",tracksToString(user.getTracks()));
            if (user.getGuideCnt() != 0) values.put("guideCnt", user.getGuideCnt());
            if (user.getType() != null) values.put("Type", user.getType());
            if (user.getExt() != null) values.put("ext", user.getExt());
            if (user.getHeader() != null) values.put("header", user.getHeader());
            mdb.update(fri_table_name, values, "userId=?", new String[]{String.valueOf(user.getUserId())});
            cursor.close();
        } else {
            ContentValues values = new ContentValues();
            values.put("userId", user.getUserId());
            values.put("nickName", user.getNickName());
            values.put("avatar", user.getAvatar());
            values.put("avatarSmall", user.getAvatarSmall());
            values.put("gender", user.getGender());
            values.put("signature", user.getSignature());
            values.put("tel", user.getTel());
            values.put("secToken", user.getSecToken());
            values.put("countryCode", user.getCountryCode());
            values.put("email", user.getEmail());
            values.put("memo", user.getMemo());
            values.put("travelStatus", user.getTravelStatus());
            values.put("residence", user.getResidence());
            values.put("level", user.getLevel());
            values.put("zodiac", user.getZodiac());
            values.put("birthday", user.getBirthday());
//            values.put("tracks",tracksToString(user.getTracks()));
            values.put("guideCnt", user.getGuideCnt());
            values.put("Type", user.getType());
            values.put("ext", user.getExt());
            values.put("header", user.getHeader());
            mdb.insert(fri_table_name, null, values);
            cursor.close();
        }
        closeDB();
    }

    public synchronized void saveContactList(List<User> list) {
        if (list == null) return;
        mdb = getDB();
        ContentValues values1 = new ContentValues();
        values1.put("Type", 0);
        mdb.update(fri_table_name, values1, null, null);
        if (list.size() > 0) {
            mdb.beginTransaction();
            for (User user : list) {
                if (user.getNickName() == null || "".equals(user.getNickName())) {
                    user.setHeader("#");
                } else if (" ".equals(user.getNickName().substring(0, 1))) {
                    user.setHeader("#");
                } else {
                    String headerName = user.getNickName();
                    user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(
                            0, 1).toUpperCase());
                    char header = user.getHeader().toLowerCase().charAt(0);
                    if (header < 'a' || header > 'z') {
                        user.setHeader("#");
                    }
                }
                Cursor cursor = mdb.rawQuery("select * from " + fri_table_name + " where userId=?", new String[]{String.valueOf(user.getUserId())});
                if (cursor.getCount() == 0) {
                    ContentValues values = new ContentValues();
                    values.put("userId", user.getUserId());
                    values.put("nickName", user.getNickName());
                    values.put("avatar", user.getAvatar());
                    values.put("avatarSmall", user.getAvatarSmall());
                    values.put("gender", user.getGender());
                    values.put("signature", user.getSignature());
                    values.put("tel", user.getTel());
                    values.put("secToken", user.getSecToken());
                    values.put("countryCode", user.getCountryCode());
                    values.put("email", user.getEmail());
                    values.put("memo", user.getMemo());
                    values.put("travelStatus", user.getTravelStatus());
                    values.put("residence", user.getResidence());
                    values.put("level", user.getLevel());
                    values.put("zodiac", user.getZodiac());
                    values.put("birthday", user.getBirthday());
                    //   values.put("tracks",tracksToString(user.getTracks()));
                    values.put("guideCnt", user.getGuideCnt());
                    values.put("Type", user.getType());
                    values.put("ext", user.getExt());
                    values.put("header", user.getHeader());
                    mdb.insert(fri_table_name, null, values);
                } else {
                    ContentValues values = new ContentValues();
                    if (user.getUserId() != null) values.put("userId", user.getUserId());
                    if (user.getNickName() != null) values.put("nickName", user.getNickName());
                    if (user.getAvatar() != null) values.put("avatar", user.getAvatar());
                    if (user.getAvatarSmall() != null)
                        values.put("avatarSmall", user.getAvatarSmall());
                    if (user.getGender() != null) values.put("gender", user.getGender());
                    if (user.getSignature() != null) values.put("signature", user.getSignature());
                    if (user.getTel() != null) values.put("tel", user.getTel());
                    if (user.getSecToken() != null) values.put("secToken", user.getSecToken());
                    if (user.getCountryCode() != null)
                        values.put("countryCode", user.getCountryCode());
                    if (user.getEmail() != null) values.put("email", user.getEmail());
                    if (user.getMemo() != null) values.put("memo", user.getMemo());
                    if (user.getTravelStatus() != null)
                        values.put("travelStatus", user.getTravelStatus());
                    if (user.getResidence() != null) values.put("residence", user.getResidence());
                    if (user.getLevel() != null) values.put("level", user.getLevel());
                    if (user.getZodiac() != null) values.put("zodiac", user.getZodiac());
                    if (user.getBirthday() != null) values.put("birthday", user.getBirthday());
//            values.put("tracks",tracksToString(user.getTracks()));
                    if (user.getGuideCnt() != 0) values.put("guideCnt", user.getGuideCnt());
                    if (user.getType() != null) values.put("Type", user.getType());
                    if (user.getExt() != null) values.put("ext", user.getExt());
                    if (user.getHeader() != null) values.put("header", user.getHeader());
                    mdb.update(fri_table_name, values, "userId=?", new String[]{String.valueOf(user.getUserId())});
                }
                cursor.close();
            }
            mdb.setTransactionSuccessful();
            mdb.endTransaction();
        }
        closeDB();
    }

    public synchronized void updateGroupInfo(User user, String groupId) {
        if (user == null) {
            return;
        }
        mdb = getDB();
        Cursor cursor = mdb.rawQuery("select ext from " + fri_table_name + " where userId=?", new String[]{String.valueOf(groupId)});
        if (cursor.getCount() == 0) {
            closeDB();
            return;
        }
        cursor.moveToLast();
        String ext = cursor.getString(0);
        cursor.close();
        try {
            JSONObject o1 = new JSONObject(ext);
            JSONObject o2 = new JSONObject(user.getExt());
            Iterator it = o2.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                o1.put(key, o2.get(key));
            }
            user.setExt(o1.toString());
            user.setUserId(Long.parseLong(groupId));
            saveContact(user);

        } catch (JSONException e) {
            e.printStackTrace();
            closeDB();
        }
        closeDB();
    }

    public synchronized void updateGroupMemberInfo(List<User> list, String groupId) {
        if (list == null) {
            return;
        }
        JSONArray array = new JSONArray();
        for (User user : list) {
            array.put(user.getUserId());
            saveContact(user);
        }
        User user = new User();
        user.setType(8);
        user.setUserId(Long.parseLong(groupId));
        try {
            user.setExt(new JSONObject().put("GroupMember", array.toString()).toString());
            saveContact(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
