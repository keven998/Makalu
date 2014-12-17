package com.aizou.peachtravel.common.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aizou.core.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.utils.GsonTools;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.bean.ExtFromUser;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.bean.SpotDetailBean;
import com.aizou.peachtravel.bean.StrategyBean;
import com.aizou.peachtravel.bean.TravelNoteBean;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.config.Constant;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.aizou.peachtravel.module.my.LoginActivity;
import com.aizou.peachtravel.module.toolbox.im.ChatActivity;
import com.aizou.peachtravel.module.toolbox.im.IMShareActivity;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.HanziToPinyin;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rjm on 2014/11/5.
 */
public class IMUtils {
    public final static int IM_SHARE_REQUEST_CODE=200;
    public final static int IM_LOGIN_REQUEST_CODE=300;

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

    public static void onClickImShare(Context context){
        PeachUser user = AccountManager.getInstance().getLoginAccount(context);
        if (user != null && !TextUtils.isEmpty(user.easemobUser)) {
            Intent intent = new Intent(context, IMShareActivity.class);
            ((Activity)context).startActivityForResult(intent, IM_SHARE_REQUEST_CODE);
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            ((Activity)context).startActivityForResult(intent, IM_LOGIN_REQUEST_CODE);
        }
    }

    public static void onShareResult(final Context context,Object detailBean,int requestCode, int resultCode, Intent data, final OnDialogShareCallBack callback){
        if(resultCode==Activity.RESULT_OK){
            if(requestCode==IM_LOGIN_REQUEST_CODE){
                Intent intent = new Intent(context, IMShareActivity.class);
                ((Activity)context).startActivityForResult(intent, IM_SHARE_REQUEST_CODE);
            }else if(requestCode ==IM_SHARE_REQUEST_CODE){
                final int chatType = data.getIntExtra("chatType", 0);
                final String toId = data.getStringExtra("toId");
                showImShareDialog(context,detailBean,new OnDialogShareCallBack() {
                    @Override
                    public void onDialogShareOk(Dialog dialog, int type, String content) {
                        DialogManager.getInstance().showProgressDialog(context);
                        sendExtMessage(context,type,content,chatType,toId,new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                DialogManager.getInstance().dissMissProgressDialog();
                                ((Activity)context).runOnUiThread(new Runnable() {
                                    public void run() {
                                        ToastUtil.getInstance(context).showToast("发送成功");

                                    }
                                });

                            }

                            @Override
                            public void onError(int i, String s) {
                                DialogManager.getInstance().dissMissProgressDialog();
                                ((Activity)context).runOnUiThread(new Runnable() {
                                    public void run() {
                                        ToastUtil.getInstance(context).showToast("发送失败");

                                    }
                                });
                            }

                            @Override
                            public void onProgress(int i, String s) {

                            }
                        });
                        if(callback!=null){
                            callback.onDialogShareOk(dialog,type,content);
                        }
                    }

                    @Override
                    public void onDialogShareCancle(Dialog dialog, int type, String content) {
                        if(callback!=null){
                            callback.onDialogShareCancle(dialog, type, content);
                        }
                    }
                });

            }
        }

    }



    public static void showImShareDialog(Context context, Object detailBean, final OnDialogShareCallBack callback){
        int extType = 0;
        String contentJson = "";
        String type = "";
        String id="";
        String zhName="";
        String attr="";
        String desc="";
        String image="";
        if(detailBean instanceof PoiDetailBean){
            PoiDetailBean detail = (PoiDetailBean)detailBean;
            type =detail.type;
            id =detail.id;
            zhName =detail.zhName;
            attr = detail.rating+"  "+detail.priceDesc;
            desc =detail.address;
            if(detail.images!=null&&detail.images.size()>0){
                image = detail.images.get(0).url;
            }
            if(type.equals(TravelApi.PoiType.RESTAURANTS)){
                extType =5;
            }else if(type.equals(TravelApi.PoiType.SHOPPING)){
                extType =6;
            }else if(type.equals(TravelApi.PoiType.HOTEL)){
                extType =7;
            }else if(type.equals(TravelApi.PoiType.SPOT)){
                extType =4;
            }
            contentJson = createExtMessageContentForPoi(detail);

        }else if(detailBean instanceof SpotDetailBean){
            SpotDetailBean detail = (SpotDetailBean)detailBean;
            type =detail.type;
            id =detail.id;
            zhName =detail.zhName;
            attr = detail.timeCostStr;
            desc = detail.desc;
            if(detail.images!=null&&detail.images.size()>0){
                image = detail.images.get(0).url;
            }
            extType =4;
            contentJson = createExtMessageContentForSpot(detail);
        }else if(detailBean instanceof LocBean){
            LocBean detail = (LocBean)detailBean;
            type ="locality";
            id =detail.id;
            zhName =detail.zhName;
            attr = detail.timeCostDesc;
            desc = detail.desc;
            if(detail.images!=null&&detail.images.size()>0){
                image = detail.images.get(0).url;
            }
            extType =2;
            contentJson = createExtMessageContentForLoc(detail);
        }else if(detailBean instanceof TravelNoteBean){
            TravelNoteBean detail = (TravelNoteBean)detailBean;
            type ="travelNote";
            id =detail.id;
            zhName =detail.title;
            attr = detail.authorName;

            String[] strArray=detail.summary.split("\n");
            String maxLengthStr=strArray[0];
            for(String str:strArray){
                if(str.length()>maxLengthStr.length()){
                    maxLengthStr=str;
                }
            }
            desc = maxLengthStr;
            if(detail.cover!=null){
                image = detail.cover;
            }
            extType =3;
            contentJson = createExtMessageContentForNote(detail);
        }else if(detailBean instanceof StrategyBean){
            StrategyBean detail = (StrategyBean)detailBean;
            type ="guide";
            id =detail.id;
            zhName =detail.title;
            attr = detail.itineraryDays+"天";
            desc = detail.summary;
            if(detail.images!=null&&detail.images.size()>0){
                image = detail.images.get(0).url;
            }
            extType =1;
            contentJson = createExtMessageContentForGuide(detail);
        }
        final Dialog dialog  = new Dialog(context,R.style.TransparentDialog);
        View contentView = View.inflate(context, R.layout.dialog_im_share,null);
        TextView titleTv = (TextView) contentView.findViewById(R.id.title_tv);
        ImageView vsIv = (ImageView) contentView.findViewById(R.id.image_iv);
        TextView nameTv = (TextView) contentView.findViewById(R.id.name_tv);
        TextView attrTv = (TextView) contentView.findViewById(R.id.attr_tv);
        TextView descTv = (TextView) contentView.findViewById(R.id.desc_tv);
        Button okBtn = (Button) contentView.findViewById(R.id.btn_ok);
        Button cancleBtn = (Button) contentView.findViewById(R.id.btn_cancle);
        if(type.equals(TravelApi.PoiType.RESTAURANTS)){
            titleTv.setText("美食");
        }else if(type.equals(TravelApi.PoiType.SHOPPING)){
            titleTv.setText("购物");
        }else if(type.equals(TravelApi.PoiType.HOTEL)){
            titleTv.setText("酒店");
        }else if(type.equals("locality")){
            titleTv.setText("城市");
        }else if(type.equals("travelNote")){
            titleTv.setText("游记");
        }else if(type.equals("vs")){
            titleTv.setText("景点");
        }else if(type.equals("guide")){
            titleTv.setText("攻略");
        }
        nameTv.setText(zhName);
        attrTv.setText(attr);
        descTv.setText(desc);
        ImageLoader.getInstance().displayImage(image, vsIv, UILUtils.getRadiusOption(LocalDisplay.dp2px(2)));
        dialog.setContentView(contentView);
        final int finalExtType = extType;
        final String finalContentJson = contentJson;
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.onDialogShareOk(dialog, finalExtType, finalContentJson);
            }
        });
        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.onDialogShareCancle(dialog, finalExtType, finalContentJson);
            }
        });
        dialog.show();
    }


    public static String createExtMessageContentForSpot(SpotDetailBean detailBean){

        JSONObject contentJson = new JSONObject();
        try {
            contentJson.put("id",detailBean.id);
            contentJson.put("image",detailBean.images.size()>0?detailBean.images.get(0).url:"");
            contentJson.put("name",detailBean.zhName);
            contentJson.put("desc", detailBean.desc);
            contentJson.put("timeCost",detailBean.timeCostStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  contentJson.toString();
    }
    public static String createExtMessageContentForPoi(PoiDetailBean detailBean){

        JSONObject contentJson = new JSONObject();
        try {
            contentJson.put("id",detailBean.id);
            contentJson.put("image",detailBean.images.size()>0?detailBean.images.get(0).url:"");
            contentJson.put("name",detailBean.zhName);
            contentJson.put("desc",detailBean.desc);
            contentJson.put("rating",detailBean.rating+"");
            contentJson.put("price",detailBean.priceDesc);
            contentJson.put("address",detailBean.address);
            contentJson.put("timeCost",detailBean.timeCostDesc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contentJson.toString();
    }
    public static String createExtMessageContentForLoc(LocBean detailBean){

        JSONObject contentJson = new JSONObject();
        try {
            contentJson.put("id",detailBean.id);
            contentJson.put("image",detailBean.images.size()>0?detailBean.images.get(0).url:"");
            contentJson.put("name",detailBean.zhName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contentJson.toString();
    }

    public static String createExtMessageContentForNote(TravelNoteBean noteBean){
        JSONObject contentJson = new JSONObject();
        try {
            contentJson.put("id",noteBean.id);
            contentJson.put("image",noteBean.cover);
            contentJson.put("name",noteBean.title);
            String[] strArray=noteBean.summary.split("\n");
            String maxLengthStr=strArray[0];
            for(String str:strArray){
                if(str.length()>maxLengthStr.length()){
                    maxLengthStr=str;
                }
            }
            contentJson.put("desc",maxLengthStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contentJson.toString();
    }
    public static String createExtMessageContentForGuide(StrategyBean detailBean){

        JSONObject contentJson = new JSONObject();
        try {
            contentJson.put("id",detailBean.id);
            contentJson.put("image",detailBean.images.size()>0?detailBean.images.get(0).url:"");
            contentJson.put("name",detailBean.title);
            contentJson.put("timeCost",detailBean.itineraryDays+"天");
            contentJson.put("desc",detailBean.summary);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contentJson.toString();
    }

    public static void sendExtMessage(Context context,int type,String contentJson,int chatType,String to,EMCallBack callBack){
        EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.TXT);
        if(chatType== ChatActivity.CHATTYPE_GROUP){
            msg.setChatType(EMMessage.ChatType.GroupChat);
        }else{
            msg.setChatType(EMMessage.ChatType.Chat);
        }
        msg.setReceipt(to);
        IMUtils.setMessageWithTaoziUserInfo(context, msg);
        msg.setAttribute("tzType",type);
        msg.setAttribute("content", contentJson);
        msg.addBody(new TextMessageBody("[链接]"));
        IMUtils.setMessageWithTaoziUserInfo(context,msg);
        EMChatManager.getInstance().sendMessage(msg,callBack);
    }


    public interface OnDialogShareCallBack{
        void onDialogShareOk(Dialog dialog,int type,String content);
        void onDialogShareCancle(Dialog dialog,int type,String content);
    }



}
