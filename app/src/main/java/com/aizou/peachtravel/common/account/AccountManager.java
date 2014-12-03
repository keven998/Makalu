package com.aizou.peachtravel.common.account;

import android.content.Context;
import android.text.TextUtils;

import com.aizou.core.utils.GsonTools;
import com.aizou.core.utils.SharePrefUtil;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.config.PeachApplication;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.easemob.chat.EMChatManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountManager {
	public static final String LOGIN_USER_PREF = "login_user";
    public static PeachUser user;
    private Map<String, IMUser> contactList;
    /**
     * 当前用户nickname,为了苹果推送不是userid而是昵称
     */
    public static String currentUserNick = "";
    private static AccountManager instance;
    public static AccountManager getInstance(){
        if(instance==null){
            instance=new AccountManager();
        }
        return instance;

    }


	public  PeachUser getLoginAccount(Context context) {
		String userJson = SharePrefUtil.getString(context, LOGIN_USER_PREF, "");
		if (TextUtils.isEmpty(userJson)) {
			return null;
		}
        if(user==null){
            user = GsonTools.parseJsonToBean(userJson,
                    PeachUser.class);
        }
		return user;
	}

	public  void logout(Context context) {
		SharePrefUtil.saveString(context, LOGIN_USER_PREF, "");
        // 先调用sdk logout，在清理app中自己的数据
        EMChatManager.getInstance().logout();

        // reset password to null
        setContactList(null);
	}
	
    public  void saveLoginAccount(Context context,PeachUser user) {
        this.user =user;
        SharePrefUtil.saveString(context, LOGIN_USER_PREF, GsonTools.createGsonString(user));
    }

    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, IMUser> getContactList(Context context) {
        if (user != null&&user.easemobUser!=null && contactList == null) {
            // 获取本地好友user list到内存,方便以后获取好友list
            List<IMUser> userList= IMUserRepository.getContactList(context);
            contactList = new HashMap<String, IMUser>();
            for(IMUser user :userList){
                contactList.put(user.getUsername(),user);
            }
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
