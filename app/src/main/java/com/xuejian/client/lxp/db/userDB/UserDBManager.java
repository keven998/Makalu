package com.xuejian.client.lxp.db.userDB;

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

    private UserDBManager(String User_Id) {
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
            instance = new UserDBManager(AccountManager.getInstance().getCurrentUserId());
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
                "gender TEXT，signature TEXT，tel TEXT，secToken TEXT，countryCode TEXT，email TEXT，" +
                "memo TEXT，travelStatus TEXT，residence TEXT，level TEXT，zodiac TEXT，birthday TEXT," +
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
        return new User(userId,nickName,avatar,avatarSmall,gender,signature,tel,secToken,countryCode,
                email,memo,travelStatus,residence,level,zodiac,birthday,tracks,guideCnt,Type,ext);
    }
}
