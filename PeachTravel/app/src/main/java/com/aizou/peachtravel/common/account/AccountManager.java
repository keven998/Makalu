package com.aizou.peachtravel.common.account;

import android.content.Context;
import android.text.TextUtils;

import com.aizou.core.utils.GsonTools;
import com.aizou.core.utils.SharePrefUtil;
import com.aizou.peachtravel.bean.PeachUser;

public class AccountManager {
	public static final String LOGIN_USER_PREF = "login_user";
    public static PeachUser user;
    private static AccountManager instance;
    public static AccountManager getInstance(){
        if(instance==null){
            instance=new AccountManager();
        }
        return instance;

    }


	public  PeachUser getLoginAccountFromPref(Context context) {
		String userJson = SharePrefUtil.getString(context, LOGIN_USER_PREF, "");
		if (TextUtils.isEmpty(userJson)) {
			return null;
		}
		PeachUser userResult = GsonTools.parseJsonToBean(userJson,
                PeachUser.class);
		return userResult;
	}

	public  void logout(Context context) {
		SharePrefUtil.saveString(context, LOGIN_USER_PREF, "");
	}
	
    public  void saveLoginAccount(Context context,PeachUser user) {
        SharePrefUtil.saveString(context, LOGIN_USER_PREF, GsonTools.createGsonString(user));
    }


}
