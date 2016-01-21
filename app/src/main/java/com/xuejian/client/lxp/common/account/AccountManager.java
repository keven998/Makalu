package com.xuejian.client.lxp.common.account;

import android.content.Context;
import android.text.TextUtils;

import com.aizou.core.utils.GsonTools;
import com.aizou.core.utils.SharePrefUtil;
import com.lv.im.IMClient;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountManager {
    public static final String ACCOUNT_LOGOUT_ACTION = "com.aizou.peathtravel.ACTION_LOGOUT";
    public static final String LOGIN_USER_PREF = "login_user";
    public static User user;
    public static String CurrentUserId;
    private static ConcurrentHashMap<Long, User> contactList;
    private boolean isLogin;
    private User userInfo;
    /**
     * 当前用户nickname,为了苹果推送不是userid而是昵称
     */
    public static String currentUserNick = "";
    private static AccountManager instance;

    public static String getChannelId() {
        return ChannelId;
    }

    public static void setChannelId(String channelId) {
        ChannelId = channelId;
    }

    public static String ChannelId;
    public static AccountManager getInstance() {
        if (instance == null) {
            instance = new AccountManager();
        }
        return instance;

    }

    public void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public static String getCurrentUserId() {
        if(user!=null){
            if (TextUtils.isEmpty(CurrentUserId)){
                CurrentUserId=String.valueOf(user.getUserId());
            }
        }
        return CurrentUserId;
    }

    public static void setCurrentUserId(String currentUserId) {
        CurrentUserId = currentUserId;
    }

    public User getLoginAccount(Context context) {

        if (user == null) {
            String userJson = SharePrefUtil.getString(context, LOGIN_USER_PREF, "");
            if (TextUtils.isEmpty(userJson)) {
                return null;
            }
            user = GsonTools.parseJsonToBean(userJson,
                    User.class);
        }
        return user;
    }

    public void logout(final Context context) {
        SharePrefUtil.saveString(context, AccountManager.LOGIN_USER_PREF, "");
        AccountManager.getInstance().setContactList(null);
        setLoginAccountInfo(null);
        this.isLogin = false;
        user=null;
        IMClient.getInstance().logout();
        UserDBManager.getInstance().disconnectDB();
    }
//    public void logout(final Context context, final boolean isConflict, final EMCallBack callBack) {
//        PeachHXSDKHelper.getInstance().logout(new EMCallBack() {
//            @Override
//            public void onSuccess() {
//                SharePrefUtil.saveString(context, AccountManager.LOGIN_USER_PREF, "");
//                AccountManager.getInstance().setContactList(null);
//                //处理一下用户名密码表，下次登录的时候重新建立用户名密码表，表里同时只能存在一个用户
//                UserDBManager.getInstance().disconnectDB();
//                IMClient.getInstance().disconnectDB();
//                /*IMUserRepository.clearAllContact(context);
//                InviteMsgRepository.clearAllInviteMsg(context);
//                if (callBack != null) {
//                    callBack.onSuccess();
//                }*/
//                /*MyFragment my=new MyFragment();
//                my.refresh();*/
//               /* Looper.prepare();
//                final PeachMessageDialog dialog=new PeachMessageDialog(context);
//                dialog.setMessage("下线");
//                dialog.setPositiveButton("确定",new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//                dialog.setCancelable(false);
//                dialog.show();
//                Looper.loop();*/
//
//            }
//
//            @Override
//            public void onError(int i, String s) {
//                if (callBack != null) {
//                    callBack.onError(i, s);
//                }
//            }
//
//            @Override
//            public void onProgress(int i, String s) {
//                if (callBack != null) {
//                    callBack.onProgress(i, s);
//                }
//            }
//        });
//        // reset password to null
//
//    }

    public void saveLoginAccount(Context context, User user) {
        AccountManager.user = user;
        SharePrefUtil.saveString(context, LOGIN_USER_PREF, GsonTools.createGsonString(user));
    }

    public User getLoginAccountInfo() {
        if (userInfo==null){
            userInfo=user;
        }
        return userInfo;
    }

    public void setLoginAccountInfo(User user) {
        this.userInfo = user;
    }

    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<Long, User> getContactList(Context context) {
        // 获取本地好友user list到内存,方便以后获取好友list
        if (contactList == null) contactList = new ConcurrentHashMap<>();
        if (contactList.size() == 0) {
            List<User> userList = UserDBManager.getInstance().getContactListWithoutGroup();
            if (userList != null) {
                contactList.clear();
                for (User user : userList) {
                    contactList.put(user.getUserId(), user);
                }
            }
        }
        return contactList;
    }

    /**
     * 设置好友user list到内存中
     */
    public void setContactList(ConcurrentHashMap<Long, User> _contactList) {
        if (contactList == null) contactList = new ConcurrentHashMap<>();
        contactList = _contactList;
    }

    public Map<Long, User> getCacheContactList() {
        return contactList;
    }
}
