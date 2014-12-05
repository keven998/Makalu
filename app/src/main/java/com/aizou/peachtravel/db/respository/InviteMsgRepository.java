package com.aizou.peachtravel.db.respository;

import android.content.Context;

import com.aizou.peachtravel.config.PeachApplication;
import com.aizou.peachtravel.db.InviteMessage;
import com.aizou.peachtravel.db.InviteMessageDao;

import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

/**
 * Created by Rjm on 2014/11/1.
 */
public class InviteMsgRepository {
    public static InviteMessageDao getInviteMsgDao(Context c) {
        return ((PeachApplication) c.getApplicationContext()).getDaoSession().getInviteMessageDao();
    }

    public static void deleteInviteMsg(Context c,String username){
        QueryBuilder<InviteMessage> qb =  getInviteMsgDao(c).queryBuilder();
        DeleteQuery<InviteMessage> bd = qb.where(InviteMessageDao.Properties.From.eq(username)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }
    public static void saveMessage(Context c,InviteMessage message){
        getInviteMsgDao(c).insertOrReplace(message);
    }

    public static List<InviteMessage> getMessagesList(Context c){
        return getInviteMsgDao(c).loadAll();
    }

    public static void clearAllInviteMsg(Context c){
        getInviteMsgDao(c).deleteAll();
    }
}
