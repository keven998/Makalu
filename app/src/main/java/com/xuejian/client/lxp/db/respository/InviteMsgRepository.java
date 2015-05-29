package com.xuejian.client.lxp.db.respository;

import android.content.Context;

import com.xuejian.client.lxp.config.PeachApplication;
import com.xuejian.client.lxp.db.InviteMessage;
import com.xuejian.client.lxp.db.InviteMessageDao;
import com.xuejian.client.lxp.db.InviteStatus;

import java.util.List;

import de.greenrobot.dao.query.CountQuery;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

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
    public static long getUnAcceptMsgCount(Context c){
        CountQuery<InviteMessage> cq= getInviteMsgDao(c).queryBuilder().where(InviteMessageDao.Properties.Status.eq(InviteStatus.BEINVITEED)).buildCount();
        return cq.count();
    }

    public static void clearAllInviteMsg(Context c){
        getInviteMsgDao(c).deleteAll();
    }
}
