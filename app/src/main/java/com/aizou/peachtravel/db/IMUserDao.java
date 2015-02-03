package com.aizou.peachtravel.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table IMUSER.
*/
public class IMUserDao extends AbstractDao<IMUser, String> {

    public static final String TABLENAME = "IMUSER";

    /**
     * Properties of entity IMUser.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property UserId = new Property(0, Long.class, "userId", false, "USER_ID");
        public final static Property Username = new Property(1, String.class, "username", true, "USERNAME");
        public final static Property Nick = new Property(2, String.class, "nick", false, "NICK");
        public final static Property Avatar = new Property(3, String.class, "avatar", false, "AVATAR");
        public final static Property AvatarSmall = new Property(4, String.class, "avatarSmall", false, "AVATAR_SMALL");
        public final static Property Gender = new Property(5, String.class, "gender", false, "GENDER");
        public final static Property Signature = new Property(6, String.class, "signature", false, "SIGNATURE");
        public final static Property Tel = new Property(7, String.class, "tel", false, "TEL");
        public final static Property Memo = new Property(8, String.class, "memo", false, "MEMO");
        public final static Property UnreadMsgCount = new Property(9, Integer.class, "unreadMsgCount", false, "UNREAD_MSG_COUNT");
        public final static Property Header = new Property(10, String.class, "header", false, "HEADER");
        public final static Property IsMyFriends = new Property(11, boolean.class, "isMyFriends", false, "IS_MY_FRIENDS");
    };


    public IMUserDao(DaoConfig config) {
        super(config);
    }
    
    public IMUserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'IMUSER' (" + //
                "'USER_ID' INTEGER," + // 0: userId
                "'USERNAME' TEXT PRIMARY KEY NOT NULL ," + // 1: username
                "'NICK' TEXT," + // 2: nick
                "'AVATAR' TEXT," + // 3: avatar
                "'AVATAR_SMALL' TEXT," + // 4: avatarSmall
                "'GENDER' TEXT," + // 5: gender
                "'SIGNATURE' TEXT," + // 6: signature
                "'TEL' TEXT," + // 7: tel
                "'MEMO' TEXT," + // 8: memo
                "'UNREAD_MSG_COUNT' INTEGER," + // 9: unreadMsgCount
                "'HEADER' TEXT," + // 10: header
                "'IS_MY_FRIENDS' INTEGER NOT NULL );"); // 11: isMyFriends
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'IMUSER'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, IMUser entity) {
        stmt.clearBindings();
 
        Long userId = entity.getUserId();
        if (userId != null) {
            stmt.bindLong(1, userId);
        }
 
        String username = entity.getUsername();
        if (username != null) {
            stmt.bindString(2, username);
        }
 
        String nick = entity.getNick();
        if (nick != null) {
            stmt.bindString(3, nick);
        }
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(4, avatar);
        }
 
        String avatarSmall = entity.getAvatarSmall();
        if (avatarSmall != null) {
            stmt.bindString(5, avatarSmall);
        }
 
        String gender = entity.getGender();
        if (gender != null) {
            stmt.bindString(6, gender);
        }
 
        String signature = entity.getSignature();
        if (signature != null) {
            stmt.bindString(7, signature);
        }
 
        String tel = entity.getTel();
        if (tel != null) {
            stmt.bindString(8, tel);
        }
 
        String memo = entity.getMemo();
        if (memo != null) {
            stmt.bindString(9, memo);
        }
 
        Integer unreadMsgCount = entity.getUnreadMsgCount();
        if (unreadMsgCount != null) {
            stmt.bindLong(10, unreadMsgCount);
        }
 
        String header = entity.getHeader();
        if (header != null) {
            stmt.bindString(11, header);
        }
        stmt.bindLong(12, entity.getIsMyFriends() ? 1l: 0l);
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1);
    }    

    /** @inheritdoc */
    @Override
    public IMUser readEntity(Cursor cursor, int offset) {
        IMUser entity = new IMUser( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // userId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // username
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // nick
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // avatar
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // avatarSmall
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // gender
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // signature
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // tel
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // memo
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9), // unreadMsgCount
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // header
            cursor.getShort(offset + 11) != 0 // isMyFriends
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, IMUser entity, int offset) {
        entity.setUserId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUsername(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setNick(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAvatar(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setAvatarSmall(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setGender(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSignature(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setTel(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setMemo(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setUnreadMsgCount(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setHeader(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setIsMyFriends(cursor.getShort(offset + 11) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(IMUser entity, long rowId) {
        return entity.getUsername();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(IMUser entity) {
        if(entity != null) {
            return entity.getUsername();
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
