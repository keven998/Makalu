package com.aizou.peachtravel.common.utils;

import android.content.Context;
import android.text.TextUtils;

import com.aizou.core.utils.GsonTools;
import com.aizou.peachtravel.bean.ExtFromUser;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.config.Constant;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.easemob.chat.EMMessage;
import com.easemob.util.HanziToPinyin;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rjm on 2014/11/5.
 */
public class IMUtils {
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
    public static void setMessageWithTaoziUserInfo(Context context,EMMessage message){
        //组装个人信息json
        PeachUser myUser = AccountManager.getInstance().getLoginAccount(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",myUser.userId);
            jsonObject.put("avatar",myUser.avatar);
            jsonObject.put("nickName",myUser.nickName);
            message.setAttribute("fromUser",jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public static void setMessageWithExtTips(Context context,EMMessage message,String tips){
       message.setAttribute(Constant.EXT_TYPE,Constant.ExtType.TIPS);
       message.setAttribute(Constant.MSG_CONTENT,tips);

    }

    public static IMUser getUserInfoFromMessage(Context context,EMMessage message){
        String fromUser = message.getStringAttribute(Constant.FROM_USER,"");
        if(!TextUtils.isEmpty(fromUser)){
            ExtFromUser user = GsonTools.parseJsonToBean(fromUser, ExtFromUser.class);
            IMUser imUser = IMUserRepository.getContactByUserName(context, message.getFrom());
            if(imUser!=null){
                imUser.setNick(user.nickName);
                imUser.setSignature(user.avatar);
            }else{
                imUser = new IMUser();
                imUser.setUsername(message.getFrom());
                imUser.setNick(user.nickName);
                imUser.setUserId(user.userId);
                imUser.setAvatar(user.avatar);
            }
            IMUserRepository.saveContact(context,imUser);
            return imUser;
        }
        return null;
    }


}
