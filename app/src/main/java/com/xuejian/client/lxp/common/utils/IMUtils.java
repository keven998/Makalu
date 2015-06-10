package com.xuejian.client.lxp.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.utils.GsonTools;
import com.aizou.core.utils.LocalDisplay;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.HanziToPinyin;
import com.lv.Listener.SendMsgListener;
import com.lv.bean.MessageBean;
import com.lv.im.IMClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.ExtFromUser;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.PeachUser;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.SpotDetailBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.bean.TravelNoteBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.share.ICreateShareDialog;
import com.xuejian.client.lxp.common.share.ShareDialogBean;
import com.xuejian.client.lxp.config.Constant;
import com.xuejian.client.lxp.db.IMUser;
import com.xuejian.client.lxp.db.respository.IMUserRepository;
import com.xuejian.client.lxp.db.userDB.User;
import com.xuejian.client.lxp.db.userDB.UserDBManager;
import com.xuejian.client.lxp.module.my.LoginActivity;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;
import com.xuejian.client.lxp.module.toolbox.im.IMShareActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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
    public static User setUserHead(User user) {
//        String username=user.getNickName();
        String headerName = user.getNickName();
//        if (!TextUtils.isEmpty(user.getNick())) {
//            headerName = user.getNick();
//        } else {
//            headerName = user.getUsername();
//        }
//        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
//            user.setHeader("");
//        } else if (Character.isDigit(headerName.charAt(0))) {
//            user.setHeader("#");
//        } else {
            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(
                    0, 1).toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
         //   }
        }
        return user;
    }
    static String getStrng(Context context, int resId){
        return context.getResources().getString(resId);
    }
    /**
     * 根据消息内容和消息类型获取消息内容提示
     *
     * @param message
     * @param context
     * @return
     */

    public static String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
            case LOCATION: // 位置消息
                if (message.direct == EMMessage.Direct.RECEIVE) {
                    //从sdk中提到了ui中，使用更简单不犯错的获取string方法
//              digest = EasyUtils.getAppResourceString(context, "location_recv");
                    digest = getStrng(context, R.string.location_recv);
                    digest = String.format(digest, message.getFrom());
                    return digest;
                } else {
//              digest = EasyUtils.getAppResourceString(context, "location_prefix");
                    digest = getStrng(context, R.string.location_prefix);
                }
                break;
            case IMAGE: // 图片消息
                digest = getStrng(context, R.string.picture);
                break;
            case VOICE:// 语音消息
                digest = getStrng(context, R.string.voice);
                break;
            case VIDEO: // 视频消息
                digest = getStrng(context, R.string.video);
                break;
            case TXT: // 文本消息
                if(!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL,false)){
                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
                    digest = txtBody.getMessage();
                }else {
                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
                    digest = getStrng(context, R.string.voice_call) + txtBody.getMessage();
                }
                break;
            case FILE: //普通文件消息
                digest = getStrng(context, R.string.file);
                break;
            default:
                System.err.println("error, unknow type");
                return "";
        }

        return digest;
    }
    public static void setMessageWithTaoziUserInfo(Context context,EMMessage message){
        //组装个人信息json
        User myUser = AccountManager.getInstance().getLoginAccount(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",myUser.getUserId());
            jsonObject.put("avatar",myUser.getAvatarSmall());
            jsonObject.put("nickName",myUser.getNickName());
            message.setAttribute("fromUser",jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public static void setMessageWithExtTips(Context context,EMMessage message,String tips){
       message.setAttribute(Constant.EXT_TYPE, Constant.ExtType.TIPS);
       message.setAttribute(Constant.MSG_CONTENT,tips);

    }
    public static void HandleCMDInfoFromMessage(MessageBean m){
        String cmd=m.getMessage();
        try {
            JSONObject object=new JSONObject(cmd);
            String action=object.getString("action");
            switch (action){
                case "D_INVITE":
                    long chatId=object.getLong("groupId");
                    long inviteId=object.getLong("userId");
                    String nickName=object.getString("nickName");
                    String groupName=object.getString("groupName");
                    User group=new User(chatId,groupName,"",8);
                    UserDBManager.getInstance().saveContact(group);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public static IMUser getUserInfoFromMessage(Context context,EMMessage message){
        String fromUser = message.getStringAttribute(Constant.FROM_USER,"");
        if(!TextUtils.isEmpty(fromUser)){
            ExtFromUser user = GsonTools.parseJsonToBean(fromUser, ExtFromUser.class);
            IMUser imUser = IMUserRepository.getContactByUserName(context, message.getFrom());
            if(imUser!=null){
                imUser.setNick(user.nickName);
                imUser.setAvatar(user.avatar);
                imUser.setUserId(user.userId);
                imUser.setAvatarSmall(user.avatar);
            }else{
                imUser = new IMUser();
                imUser.setUsername(message.getFrom());
                imUser.setNick(user.nickName);
                imUser.setUserId(user.userId);
                imUser.setAvatar(user.avatar);
                imUser.setAvatarSmall(user.avatar);

            }
            IMUserRepository.saveContact(context, imUser);
            return imUser;
        }
        return null;
    }

    public static void onClickImShare(Context context){
        User user = AccountManager.getInstance().getLoginAccount(context);
        if (user != null) {  //&& !TextUtils.isEmpty(user.easemobUser)
            Intent intent = new Intent(context, IMShareActivity.class);
            ((Activity)context).startActivityForResult(intent, IM_SHARE_REQUEST_CODE);

        } else {
            ToastUtil.getInstance(context).showToast("请先登录");
            Intent intent = new Intent(context, LoginActivity.class);
            ((Activity)context).startActivityForResult(intent, IM_LOGIN_REQUEST_CODE);
        }
    }

    public static void onShareResult(final Context context,ICreateShareDialog iCreateShareDialog,int requestCode, int resultCode, Intent data, final OnDialogShareCallBack callback){
        if(resultCode==Activity.RESULT_OK){
            if(requestCode==IM_LOGIN_REQUEST_CODE){
                Intent intent = new Intent(context, IMShareActivity.class);
                ((Activity)context).startActivityForResult(intent, IM_SHARE_REQUEST_CODE);
            }else if(requestCode ==IM_SHARE_REQUEST_CODE){
                final String chatType = data.getStringExtra("chatType");
                final String toId = data.getStringExtra("toId");
                showImShareDialog(context,iCreateShareDialog,new OnDialogShareCallBack() {
                    @Override
                    public void onDialogShareOk(Dialog dialog, int type, String content) {
                        DialogManager.getInstance().showLoadingDialog(context);
                        IMClient.getInstance().sendExtMessage(toId,chatType,content,type,new SendMsgListener(){

                            @Override
                            public void onSuccess() {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    public void run() {
                                        ToastUtil.getInstance(context).showToast("已发送~");

                                    }
                                });
                            }

                            @Override
                            public void onFailed(int code) {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    public void run() {
                                        ToastUtil.getInstance(context).showToast("好像发送失败了");

                                    }
                                });
                            }
                        });
//                        sendExtMessage(context, type, content, chatType, toId, new EMCallBack() {
//                            @Override
//                            public void onSuccess() {
//                                DialogManager.getInstance().dissMissLoadingDialog();
//                                ((Activity) context).runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        ToastUtil.getInstance(context).showToast("已发送~");
//
//                                    }
//                                });
//
//                            }
//
//                            @Override
//                            public void onError(int i, String s) {
//                                DialogManager.getInstance().dissMissLoadingDialog();
//                                ((Activity) context).runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        ToastUtil.getInstance(context).showToast("好像发送失败了");
//
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onProgress(int i, String s) {
//
//                            }
//                        });
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



    public static void showImShareDialog(Context context, final ICreateShareDialog iCreateShareDialog, final OnDialogShareCallBack callback){
        final ShareDialogBean dialogBean = iCreateShareDialog.createShareBean();
        final Dialog dialog  = new Dialog(context,R.style.ComfirmDialog);
        View contentView = View.inflate(context, R.layout.dialog_im_share,null);
        TextView titleTv = (TextView) contentView.findViewById(R.id.tv_title);
        ImageView vsIv = (ImageView) contentView.findViewById(R.id.iv_image);
        TextView nameTv = (TextView) contentView.findViewById(R.id.tv_name);
        TextView attrTv = (TextView) contentView.findViewById(R.id.tv_attr);
        TextView descTv = (TextView) contentView.findViewById(R.id.tv_desc);
        Button okBtn = (Button) contentView.findViewById(R.id.btn_ok);
        Button cancleBtn = (Button) contentView.findViewById(R.id.btn_cancle);
        titleTv.setText(dialogBean.getTitle());
        nameTv.setText(dialogBean.getName());
        if(TextUtils.isEmpty(dialogBean.getAttr())){
            attrTv.setVisibility(View.GONE);
        }else{
            if(dialogBean.getExtType()== Constant.ExtType.FOOD||dialogBean.getExtType()== Constant.ExtType.HOTEL||dialogBean.getExtType()== Constant.ExtType.SHOPPING){
                attrTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_score_start_small,0,0,0);
            }
            attrTv.setVisibility(View.VISIBLE);
            attrTv.setText(dialogBean.getAttr());
        }

        descTv.setText(dialogBean.getDesc());
        ImageLoader.getInstance().displayImage(dialogBean.getImage(), vsIv, UILUtils.getRadiusOption(LocalDisplay.dp2px(2)));
        dialog.setContentView(contentView);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.onDialogShareOk(dialog, dialogBean.getExtType(), GsonTools.createGsonString(dialogBean.getExtMessageBean()));
            }
        });
        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.onDialogShareCancle(dialog, dialogBean.getExtType(), GsonTools.createGsonString(dialogBean.getExtMessageBean()));
            }
        });
        WindowManager m =((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p =dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() -LocalDisplay.dp2px(40)); // 宽度设置为屏幕的0.65
        dialog.getWindow().setAttributes(p);
        dialog.show();
    }


    public static String createExtMessageContentForSpot(SpotDetailBean detailBean){

        JSONObject contentJson = new JSONObject();
        try {
            contentJson.put("id",detailBean.id);
            contentJson.put("image",detailBean.images.size()>0?detailBean.images.get(0).url:"");
            contentJson.put("name",detailBean.zhName);
            contentJson.put("desc", detailBean.desc);
            contentJson.put("timeCost",detailBean.timeCostDesc);
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
            contentJson.put("image",noteBean.images.get(0).url);
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
        IMUtils.setMessageWithTaoziUserInfo(context, msg);
        EMChatManager.getInstance().sendMessage(msg,callBack);
    }


    public interface OnDialogShareCallBack{
        void onDialogShareOk(Dialog dialog, int type, String content);
        void onDialogShareCancle(Dialog dialog, int type, String content);
    }

    public static boolean isAppRunningForeground(Context var0) {
        ActivityManager var1 = (ActivityManager)var0.getSystemService(Context.ACTIVITY_SERVICE);
        List var2 = var1.getRunningTasks(1);
        return var0.getPackageName().equalsIgnoreCase(((ActivityManager.RunningTaskInfo)var2.get(0)).baseActivity.getPackageName());
    }

}
