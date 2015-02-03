package com.aizou.peachtravel.db.respository;

import android.content.Context;

import com.aizou.peachtravel.config.PeachApplication;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.IMUserDao;

import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;


/**
 * Created by surecase on 19/03/14.
 */
public class IMUserRepository {


    private static IMUserDao getIMUserDao(Context c) {
        return ((PeachApplication) c.getApplicationContext()).getDaoSession().getIMUserDao();
    }

    public static void deleteContact(Context c,String username){
        QueryBuilder<IMUser> qb =  getIMUserDao(c).queryBuilder();
        DeleteQuery<IMUser> bd = qb.where(IMUserDao.Properties.Username.eq(username)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }
    public static IMUser getContactByUserName(Context c, String username){
       return getIMUserDao(c).queryBuilder().where(IMUserDao.Properties.Username.eq(username)).build().unique();
    }
    public static IMUser getMyFriendByUserName(Context c, String username){
        return getIMUserDao(c).queryBuilder().where(IMUserDao.Properties.Username.eq(username),IMUserDao.Properties.IsMyFriends.eq(true)).build().unique();
    }
    public static IMUser getContactByUserId(Context c, long userId){
        return getIMUserDao(c).queryBuilder().where(IMUserDao.Properties.UserId.eq(userId)).build().unique();
    }

    public static List<IMUser> getContactList(Context c){
       return getIMUserDao(c).queryBuilder().where(IMUserDao.Properties.IsMyFriends.eq(true)).build().list();
    }

    public static boolean isMyFriend(Context c,String username){
        IMUser user = getContactByUserName(c, username);
        if(user==null){
            return false;
        }else{
            return user.getIsMyFriends();
        }
    }
    public static void saveContact(Context c,IMUser user){
        if(user==null){
            return;
        }
        getIMUserDao(c).insertOrReplace(user);
    }
    public static void saveContactList(Context c,List<IMUser> user){
        getIMUserDao(c).insertOrReplaceInTx(user);
    }

    public static void clearAllContact(Context c){
        getIMUserDao(c).deleteAll();
    }
    public static void clearMyFriendsContact(Context c){

        getIMUserDao(c).queryBuilder().where(IMUserDao.Properties.IsMyFriends.eq(true)).buildDelete().executeDeleteWithoutDetachingEntities();
    }
}
