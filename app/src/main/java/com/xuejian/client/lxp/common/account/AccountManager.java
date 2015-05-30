package com.xuejian.client.lxp.common.account;

import android.content.Context;
import android.text.TextUtils;

import com.aizou.core.utils.GsonTools;
import com.aizou.core.utils.SharePrefUtil;
import com.easemob.EMCallBack;
import com.xuejian.client.lxp.bean.PeachUser;
import com.xuejian.client.lxp.config.hxconfig.PeachHXSDKHelper;
import com.xuejian.client.lxp.db.IMUser;
import com.xuejian.client.lxp.db.respository.IMUserRepository;
import com.xuejian.client.lxp.db.respository.InviteMsgRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountManager {
    public static final String ACCOUNT_LOGOUT_ACTION = "com.aizou.peathtravel.ACTION_LOGOUT";
    public static final String LOGIN_USER_PREF = "login_user";
    public static PeachUser user;
    private Map<String, IMUser> contactList;

    /**
     * 当前用户nickname,为了苹果推送不是userid而是昵称
     */
    public static String currentUserNick = "";
    private static AccountManager instance;

    public static AccountManager getInstance() {
        if (instance == null) {
            instance = new AccountManager();
        }
        return instance;

    }


    public PeachUser getLoginAccount(Context context) {
        String userJson = SharePrefUtil.getString(context, LOGIN_USER_PREF, "");
        if (TextUtils.isEmpty(userJson)) {
            return null;
        }
        if (user == null) {
            user = GsonTools.parseJsonToBean(userJson,
                    PeachUser.class);
        }
        return user;
    }

    public void logout(final Context context, final boolean isConflict, final EMCallBack callBack) {
        PeachHXSDKHelper.getInstance().logout(new EMCallBack() {
            @Override
            public void onSuccess() {
                SharePrefUtil.saveString(context, AccountManager.LOGIN_USER_PREF, "");
                AccountManager.getInstance().setContactList(null);
                IMUserRepository.clearAllContact(context);
                InviteMsgRepository.clearAllInviteMsg(context);
                if (callBack != null) {
                    callBack.onSuccess();
                }
                /*MyFragment my=new MyFragment();
                my.refresh();*/
               /* Looper.prepare();
                final PeachMessageDialog dialog=new PeachMessageDialog(context);
                dialog.setMessage("下线");
                dialog.setPositiveButton("确定",new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
                Looper.loop();*/

            }

            @Override
            public void onError(int i, String s) {
                if (callBack != null) {
                    callBack.onError(i, s);
                }
            }

            @Override
            public void onProgress(int i, String s) {
                if (callBack != null) {
                    callBack.onProgress(i, s);
                }
            }
        });
        // reset password to null

    }

    public void saveLoginAccount(Context context, PeachUser user) {
        this.user = user;
        SharePrefUtil.saveString(context, LOGIN_USER_PREF, GsonTools.createGsonString(user));
    }

    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, IMUser> getContactList(Context context) {
        // 获取本地好友user list到内存,方便以后获取好友list
        List<IMUser> userList = IMUserRepository.getContactList(context);
        contactList = new HashMap<String, IMUser>();
        for (IMUser user : userList) {
            contactList.put(user.getUsername(), user);
        }
        return contactList;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, IMUser> contactList) {
        this.contactList = contactList;
    }


}
