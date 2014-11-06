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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rjm on 2014/11/5.
 */
public class IMUtils {
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
