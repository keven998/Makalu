package com.xuejian.client.lxp.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table INVITE_MESSAGE.
*/
public class InviteMessageDao extends AbstractDao<InviteMessage, String> {

    public static final String TABLENAME = "INVITE_MESSAGE";

    /**
     * Properties of entity InviteMessage.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Nickname = new Property(0, String.class, "nickname", false, "NICKNAME");
        public final static Property UserId = new Property(1, Long.class, "userId", false, "USER_ID");
        public final static Property Avatar = new Property(2, String.class, "avatar", false, "AVATAR");
        public final static Property Gender = new Property(3, String.class, "gender", false, "GENDER");
        public final static Property From = new Property(4, String.class, "from", true, "FROM");
        public final static Property Reason = new Property(5, String.class, "reason", false, "REASON");
        public final static Property Time = new Property(6, Long.class, "time", false, "TIME");
        public final static Property Status = new Property(7, Integer.class, "status", false, "STATUS");
        public final static Property GroupId = new Property(8, String.class, "groupId", false, "GROUP_ID");
        public final static Property GroupName = new Property(9, String.class, "groupName", false, "GROUP_NAME");
    };


    public InviteMessageDao(DaoConfig config) {
        super(config);
    }

    public InviteMessageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'INVITE_MESSAGE' (" + //
                "'NICKNAME' TEXT," + // 0: nickname
                "'USER_ID' INTEGER," + // 1: userId
                "'AVATAR' TEXT," + // 2: avatar
                "'GENDER' TEXT," + // 3: gender
                "'FROM' TEXT PRIMARY KEY NOT NULL ," + // 4: from
                "'REASON' TEXT," + // 5: reason
                "'TIME' INTEGER," + // 6: time
                "'STATUS' INTEGER," + // 7: status
                "'GROUP_ID' TEXT," + // 8: groupId
                "'GROUP_NAME' TEXT);"); // 9: groupName
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'INVITE_MESSAGE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, InviteMessage entity) {
        stmt.clearBindings();

        String nickname = entity.getNickname();
        if (nickname != null) {
            stmt.bindString(1, nickname);
        }

        Long userId = entity.getUserId();
        if (userId != null) {
            stmt.bindLong(2, userId);
        }

        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(3, avatar);
        }

        String gender = entity.getGender();
        if (gender != null) {
            stmt.bindString(4, gender);
        }

        String from = entity.getFrom();
        if (from != null) {
            stmt.bindString(5, from);
        }

        String reason = entity.getReason();
        if (reason != null) {
            stmt.bindString(6, reason);
        }

        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(7, time);
        }

        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(8, status);
        }

        String groupId = entity.getGroupId();
        if (groupId != null) {
            stmt.bindString(9, groupId);
        }

        String groupName = entity.getGroupName();
        if (groupName != null) {
            stmt.bindString(10, groupName);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4);
    }

    /** @inheritdoc */
    @Override
    public InviteMessage readEntity(Cursor cursor, int offset) {
        InviteMessage entity = new InviteMessage( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // nickname
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // userId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // avatar
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // gender
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // from
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // reason
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // time
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // status
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // groupId
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // groupName
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, InviteMessage entity, int offset) {
        entity.setNickname(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setUserId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setAvatar(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setGender(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setFrom(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setReason(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setTime(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setStatus(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setGroupId(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setGroupName(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }

    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(InviteMessage entity, long rowId) {
        return entity.getFrom();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(InviteMessage entity) {
        if(entity != null) {
            return entity.getFrom();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}