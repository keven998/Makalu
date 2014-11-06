package com.aizou.peachtravel.common.account;

import android.text.TextUtils;

import com.aizou.peachtravel.config.Constant;
import com.aizou.peachtravel.db.IMUser;
import com.easemob.util.HanziToPinyin;

/**
 * Created by Rjm on 2014/11/3.
 */
public class UserUtils {
    /**
     * set head
     * @return
     */
    public static IMUser setUserHead(IMUser user) {
        String username=user.getUsername();
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(
                    0, 1).toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
        return user;
    }
}
