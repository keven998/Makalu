package com.aizou.peachtravel.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aizou.core.utils.GsonTools;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.bean.ExtFromUser;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.bean.SpotDetailBean;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.config.Constant;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.aizou.peachtravel.module.my.LoginActivity;
import com.aizou.peachtravel.module.toolbox.im.ChatActivity;
import com.aizou.peachtravel.module.toolbox.im.IMShareActivity;
import com.easemob.chat.EMMessage;
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

    public static void onShareLogin(Context context,int requestCode, int resultCode, Intent data){
        if(resultCode==Activity.RESULT_OK){
            if(requestCode==IM_LOGIN_REQUEST_CODE){
                Intent intent = new Intent(context, IMShareActivity.class);
                ((Activity)context).startActivityForResult(intent, IM_SHARE_REQUEST_CODE);
            }
        }

    }



    public static void showImSharePoiDialog(Context context,PoiDetailBean detailBean,MaterialDialog.Callback callback){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        View contentView = View.inflate(context, R.layout.dialog_im_share,null);
        TextView titleTv = (TextView) contentView.findViewById(R.id.title_tv);
        ImageView vsIv = (ImageView) contentView.findViewById(R.id.image_iv);
        TextView nameTv = (TextView) contentView.findViewById(R.id.name_tv);
        TextView attrTv = (TextView) contentView.findViewById(R.id.attr_tv);
        TextView descTv = (TextView) contentView.findViewById(R.id.desc_tv);
        if(detailBean.type.equals(TravelApi.PoiType.RESTAURANTS)){
            titleTv.setText("美食");
        }else if(detailBean.type.equals(TravelApi.PoiType.SHOPPING)){
            titleTv.setText("购物");
        }else if(detailBean.type.equals(TravelApi.PoiType.HOTEL)){
            titleTv.setText("酒店");
        }

        nameTv.setText(detailBean.zhName);
        attrTv.setText(detailBean.rating+"  "+detailBean.priceDesc);
        descTv.setText(detailBean.address);
        if(detailBean.images!=null&&detailBean.images.size()>0)
            ImageLoader.getInstance().displayImage(detailBean.images.get(0).url,vsIv,UILUtils.getDefaultOption());
        builder.customView(contentView)
                .positiveText("发送")
                .negativeText("取消")
                .positiveColor(context.getResources().getColor(R.color.app_theme_color))
                .negativeColor(context.getResources().getColor(R.color.app_theme_color))
                .callback(callback)
                .show();

    }
    public static void showImShareVsDialog(Context context,SpotDetailBean detailBean,MaterialDialog.Callback callback){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        View contentView = View.inflate(context, R.layout.dialog_im_share,null);
        TextView titleTv = (TextView) contentView.findViewById(R.id.title_tv);
        ImageView vsIv = (ImageView) contentView.findViewById(R.id.image_iv);
        TextView nameTv = (TextView) contentView.findViewById(R.id.name_tv);
        TextView attrTv = (TextView) contentView.findViewById(R.id.attr_tv);
        TextView descTv = (TextView) contentView.findViewById(R.id.desc_tv);
        if(detailBean.type.equals(TravelApi.PoiType.RESTAURANTS)){
            titleTv.setText("美食");
        }else if(detailBean.type.equals(TravelApi.PoiType.SHOPPING)){
            titleTv.setText("购物");
        }else if(detailBean.type.equals(TravelApi.PoiType.HOTEL)){
            titleTv.setText("酒店");
        }

        nameTv.setText(detailBean.zhName);
        attrTv.setText(detailBean.timeCostStr+"");
        descTv.setText(detailBean.desc);
        if(detailBean.images!=null&&detailBean.images.size()>0)
            ImageLoader.getInstance().displayImage(detailBean.images.get(0).url,vsIv,UILUtils.getDefaultOption());
        builder.customView(contentView)
                .positiveText("发送")
                .negativeText("取消")
                .positiveColor(context.getResources().getColor(R.color.app_theme_color))
                .negativeColor(context.getResources().getColor(R.color.app_theme_color))
                .callback(callback)
                .show();

    }


    public static String createExtMessageContentForSpot(SpotDetailBean detailBean){

        JSONObject contentJson = new JSONObject();
        try {
            contentJson.put("id",detailBean.id);
            contentJson.put("image",detailBean.images.size()>0?detailBean.images.get(0).url:"");
            contentJson.put("name",detailBean.zhName);
            contentJson.put("desc", detailBean.desc);
            contentJson.put("costTime",detailBean.timeCostStr);
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
            contentJson.put("rating",detailBean.rating);
            contentJson.put("price",detailBean.priceDesc);
            contentJson.put("address",detailBean.address);
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
            contentJson.put("desc",detailBean.desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contentJson.toString();
    }



}
