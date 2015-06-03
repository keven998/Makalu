package com.xuejian.client.lxp.db.userDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lidroid.xutils.exception.DbException;
import com.lv.Utils.Config;
import com.lv.Utils.CryptUtils;
import com.xuejian.client.lxp.bean.PeachUser;
import com.xuejian.client.lxp.common.account.AccountManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yibiao.qin on 2015/5/30.
 */
public class UserDBManager  {
    private SQLiteDatabase db;
    private String fri_table_name;
    private String databaseFilename;
    private static UserDBManager instance;
    private SQLiteDatabase mdb;
    private AtomicInteger mOpenCounter = new AtomicInteger();

    private UserDBManager() {
    }
    public void initDB(String User_Id){
        String path = CryptUtils.getMD5String(User_Id);
        fri_table_name = "FRI_" + path;
        String DATABASE_PATH = Config.DB_PATH + path;
        databaseFilename = DATABASE_PATH + "/" + "lxp-db";
        File dir = new File(DATABASE_PATH);
        if (!dir.exists())
            dir.mkdir();
        db = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
    }
    public static UserDBManager getInstance() {
        if (instance == null) {
            instance = new UserDBManager();
        }
        return instance;
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
        mdb=null;
    }
    public void init(){
        mdb=getDB();
        mdb.execSQL("CREATE table IF NOT EXISTS "
                + fri_table_name
                + " (userId INTEGER PRIMARY KEY,nickName TEXT,avatar TEXT,avatarSmall TEXT," +
                "gender TEXT,signature TEXT,tel TEXT,secToken TEXT,countryCode TEXT,email TEXT," +
                "memo TEXT,travelStatus TEXT,residence TEXT,level TEXT,zodiac TEXT,birthday TEXT," +
                "tracks TEXT,guideCnt INTEGER,Type INTEGER)");
        //mdb.execSQL("create index if not exists index_Con_Friend_Id on " + con_table_name + "(Friend_Id)");
    }
    public User getMyFriendByUserId(String UserId){
        mdb=getDB();
        Cursor cursor= mdb.rawQuery("select * from " + fri_table_name + " where userId=?", new String[]{UserId});
        long userId=cursor.getLong(0);
        String nickName= cursor.getString(1);
        String avatar= cursor.getString(2);
        String avatarSmall= cursor.getString(3);
        String gender= cursor.getString(4);
        String signature= cursor.getString(5);
        String tel= cursor.getString(6);
        String secToken= cursor.getString(7);
        String countryCode= cursor.getString(8);
        String email= cursor.getString(9);
        String memo= cursor.getString(10);
        String travelStatus= cursor.getString(11);
        String residence= cursor.getString(12);
        String level= cursor.getString(13);
        String zodiac= cursor.getString(14);
        String birthday= cursor.getString(15);
        String tracks= cursor.getString(16);
        int guideCnt= cursor.getInt(17);
        int Type= cursor.getInt(18);
        String ext=cursor.getString(19);
        cursor.close();
        closeDB();
        return new User(userId,nickName,avatar,avatarSmall,gender,signature,tel,secToken,countryCode,
                email,memo,travelStatus,residence,level,zodiac,birthday,tracks,guideCnt,Type,ext);
    }
    public synchronized void deleteContact(long userId){
        mdb=getDB();
        mdb.delete(fri_table_name,"where userId=?",new String[]{String.valueOf(userId)});
        closeDB();
    }
    public User getContactByUserName(String _nickName){
        mdb=getDB();
        Cursor cursor= mdb.rawQuery("select * from " + fri_table_name + " where userId=?", new String[]{_nickName});
        long userId=cursor.getLong(0);
        String nickName= cursor.getString(1);
        String avatar= cursor.getString(2);
        String avatarSmall= cursor.getString(3);
        String gender= cursor.getString(4);
        String signature= cursor.getString(5);
        String tel= cursor.getString(6);
        String secToken= cursor.getString(7);
        String countryCode= cursor.getString(8);
        String email= cursor.getString(9);
        String memo= cursor.getString(10);
        String travelStatus= cursor.getString(11);
        String residence= cursor.getString(12);
        String level= cursor.getString(13);
        String zodiac= cursor.getString(14);
        String birthday= cursor.getString(15);
        String tracks= cursor.getString(16);
        int guideCnt= cursor.getInt(17);
        int Type= cursor.getInt(18);
        String ext=cursor.getString(19);
        cursor.close();
        closeDB();
        return new User(userId,nickName,avatar,avatarSmall,gender,signature,tel,secToken,countryCode,
                email,memo,travelStatus,residence,level,zodiac,birthday,tracks,guideCnt,Type,ext);
    }
    public boolean isMyFriend(long userId){
        mdb=getDB();
        Cursor cursor= mdb.rawQuery("select Type from " + fri_table_name + " where userId=?", new String[]{String.valueOf(userId)});
        int type=cursor.getInt(0);
        cursor.close();
        closeDB();
        return (type&1)==1;
    }
    public List<User> getContactListWithoutGroup(){
        mdb=getDB();
        List<User> list=new ArrayList<>();
        Cursor cursor= mdb.rawQuery("select * from " + fri_table_name ,null);
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
            if (isMyFriend(userId))
            list.add(new User(userId,nickName,avatar,avatarSmall,gender,signature,tel,secToken,countryCode,
                    email,memo,travelStatus,residence,level,zodiac,birthday,tracks,guideCnt,Type,ext));
        }
        cursor.close();
        closeDB();
        return list;
    }

    public void saveContact(User user){
        mdb=getDB();
        Cursor cursor= mdb.rawQuery("select * from " + fri_table_name + " where userId=?", new String[]{String.valueOf(user.getUserId())});
        if (cursor.getCount()==0){
            ContentValues values=new ContentValues();
            values.put("userId",user.getUserId());
            values.put("nickName",user.getNickName());
            values.put("avatar",user.getAvatar());
            values.put("avatarSmall",user.getAvatarSmall());
            values.put("gender",user.getGender());
            values.put("signature",user.getSignature());
            values.put("tel",user.getTel());
            values.put("secToken",user.getSecToken());
            values.put("countryCode",user.getCountryCode());
            values.put("email",user.getEmail());
            values.put("memo",user.getMemo());
            values.put("travelStatus",user.getTravelStatus());
            values.put("residence",user.getResidence());
            values.put("level",user.getLevel());
            values.put("zodiac",user.getZodiac());
            values.put("birthday",user.getBirthday());
            values.put("tracks",user.getTracks());
            values.put("guideCnt",user.getGuideCnt());
            values.put("Type",user.getType());
            mdb.insert(fri_table_name,null,values);
        }
        cursor.close();
        closeDB();
    }
    public void saveContactList(List<User> list){
        mdb=getDB();
        mdb.beginTransaction();
        for (User user:list){
        Cursor cursor= mdb.rawQuery("select * from " + fri_table_name + " where userId=?", new String[]{String.valueOf(user.getUserId())});
        if (cursor.getCount()==0){
            ContentValues values=new ContentValues();
            values.put("userId",user.getUserId());
            values.put("nickName",user.getNickName());
            values.put("avatar",user.getAvatar());
            values.put("avatarSmall",user.getAvatarSmall());
            values.put("gender",user.getGender());
            values.put("signature",user.getSignature());
            values.put("tel",user.getTel());
            values.put("secToken",user.getSecToken());
            values.put("countryCode",user.getCountryCode());
            values.put("email",user.getEmail());
            values.put("memo",user.getMemo());
            values.put("travelStatus",user.getTravelStatus());
            values.put("residence",user.getResidence());
            values.put("level",user.getLevel());
            values.put("zodiac",user.getZodiac());
            values.put("birthday",user.getBirthday());
            values.put("tracks",user.getTracks());
            values.put("guideCnt",user.getGuideCnt());
            values.put("Type",user.getType());
            mdb.insert(fri_table_name,null,values);
        }
        cursor.close();
        }
        mdb.endTransaction();
        closeDB();
    }
}
