package com.xuejian.client.lxp.common.thirdpart;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONObject;

public class SnsAccountsUtils {
	
	
	public interface WeiboConstants {
		public static final String APP_KEY		= "1487990731";
//		public static final String APP_SECRET 	= "f590ef1f8a1fd101a325d754b877ca10";
		public static final String REDIRECT_URL = "http://www.lvxingpai.com";
		public static final String SCOPE = "follow_app_official_microblog,friendships_groups_read";
	}
	
	public interface TencentConstants {
		public static final String APP_KEY		= "1102120105";
//		public static final String APP_SECRET 	= "NX41e9rz7NwIxVPy";
		public static final String SCOPE = "get_user_info,get_simple_userinfo";
	}

    public interface WeiXinConstants {
        public static final String APP_ID		= "wx26b58c7173483529";
        //		public static final String APP_SECRET 	= "28daa05c021ebebe6d3cf06645b0c5ac";
    }
	
	public static void cacheAccounts(Context cxt, JSONObject json, String accountType) {
		SharedPreferences sp = cxt.getSharedPreferences("account", Context.MODE_PRIVATE);
		SharedPreferences.Editor ed = sp.edit();
		ed.putString("uid", json.optString("datas"));
		ed.putString("accountType", accountType);
		ed.commit();
	}
	
	public static String getUid(Context cxt) {
		SharedPreferences sp = cxt.getSharedPreferences("account", Context.MODE_PRIVATE);
		return sp.getString("uid", null);
	}
	
	public static String getAccountType(Context cxt) {
		SharedPreferences sp = cxt.getSharedPreferences("account", Context.MODE_PRIVATE);
		return sp.getString("accountType", null);
	}
	
	public static boolean isLogin(Context cxt) {
		SharedPreferences sp = cxt.getSharedPreferences("account", Context.MODE_PRIVATE);
		return !TextUtils.isEmpty(sp.getString("uid", null));
	}
	
}
