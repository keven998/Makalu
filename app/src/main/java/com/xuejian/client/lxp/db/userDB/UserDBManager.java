package com.xuejian.client.lxp.db.userDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lidroid.xutils.exception.DbException;

/**
 * Created by yibiao.qin on 2015/5/30.
 */
public class UserDBManager extends SQLiteOpenHelper {
    SQLiteDatabase db;
    public UserDBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db=SQLiteDatabase.create(null);
        this.db=db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
