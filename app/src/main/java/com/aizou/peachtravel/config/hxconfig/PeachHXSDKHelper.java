/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aizou.peachtravel.config.hxconfig;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.aizou.core.utils.GsonTools;
import com.aizou.core.utils.SharePrefUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.bean.CmdAgreeBean;
import com.aizou.peachtravel.bean.CmdDeleteBean;
import com.aizou.peachtravel.bean.CmdInvateBean;
import com.aizou.peachtravel.bean.ExtFromUser;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.hxsdk.controller.HXSDKHelper;
import com.aizou.peachtravel.common.hxsdk.model.HXSDKModel;
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.utils.PreferenceUtils;
import com.aizou.peachtravel.config.Constant;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.InviteMessage;
import com.aizou.peachtravel.db.InviteStatus;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.aizou.peachtravel.db.respository.InviteMsgRepository;
import com.aizou.peachtravel.module.SplashActivity;
import com.aizou.peachtravel.module.toolbox.im.ChatActivity;
import com.aizou.peachtravel.module.toolbox.im.IMMainActivity;
import com.aizou.peachtravel.module.toolbox.im.VoiceCallReceiver;
import com.easemob.EMCallBack;

import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMNotifier;
import com.easemob.chat.NotificationCompat;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.chat.OnNotificationClickListener;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EasyUtils;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.UUID;

/**
 * Demo UI HX SDK helper class which subclass HXSDKHelper
 * @author easemob
 *
 */
public class PeachHXSDKHelper extends HXSDKHelper {

    @Override
    protected void initHXOptions(){
        super.initHXOptions();
        // you can also get EMChatOptions to set related SDK options
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        PeachUser user = AccountManager.getInstance().getLoginAccount(appContext);
        if(user!=null){
            String data= PreferenceUtils.getCacheData(appContext, String.format("%s_not_notify",user.userId ));
            if(!TextUtils.isEmpty(data)){
                List<String> notNotifyList = GsonTools.parseJsonToBean(data,new TypeToken<List<String>>(){});
                options.setReceiveNotNoifyGroup(notNotifyList);
            }
        }

    }

    @Override
    protected OnMessageNotifyListener getMessageNotifyListener(){
        // 取消注释，app在后台，有新消息来时，状态栏的消息提示换成自己写的
//        return null;
      return new OnMessageNotifyListener() {

          @Override
          public String onNewMessageNotify(EMMessage message) {
              // 设置状态栏的消息提示，可以根据message的类型做相应提示
//              String ticker = IMUtils.getMessageDigest(message, appContext);
//              if(message.getType() == EMMessage.Type.TXT)
//                  ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
              return "收到一条新消息";
          }

          @Override
          public String onLatestMessageNotify(EMMessage message, int fromUsersNum, int messageNum) {
              return null;
//             return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
          }

          @Override
          public String onSetNotificationTitle(EMMessage message) {
              //修改标题,这里使用默认
              return null;
          }

          @Override
          public int onSetSmallIcon(EMMessage message) {
              //设置小图标
              return 0;
          }
      };
    }
    
    @Override
    protected OnNotificationClickListener getNotificationClickListener(){
        return new OnNotificationClickListener() {

            @Override
            public Intent onNotificationClick(EMMessage message) {
                final Intent notificationIntent = new Intent(appContext, SplashActivity.class);
                notificationIntent.setAction(Intent.ACTION_MAIN);
                notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                notificationIntent.putExtra("im_message",message);
//                appContext.startActivity(notificationIntent);
//                Intent intent = new Intent(appContext, ChatActivity.class);
//                ChatType chatType = message.getChatType();
//                if (chatType == ChatType.Chat) { // 单聊信息
//                    intent.putExtra("userId", message.getFrom());
//                    intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
//                } else { // 群聊信息
//                            // message.getTo()为群聊id
//                    intent.putExtra("groupId", message.getTo());
//                    intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
//                }
                return notificationIntent;
            }
        };
    }
    
    @Override
    protected void onConnectionConflict(){
        AccountManager.getInstance().logout(appContext,true,null);
//        Intent intent = new Intent(appContext, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("conflict", true);
//        appContext.startActivity(intent);
    }
    
    @Override
    protected void initListener(){
        super.initListener();
        IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance().getIncomingVoiceCallBroadcastAction());
        appContext.registerReceiver(new VoiceCallReceiver(), callFilter);
        // 注册一个cmd消息的BroadcastReceiver
        IntentFilter cmdIntentFilter = new IntentFilter(EMChatManager.getInstance().getCmdMessageBroadcastAction());
        cmdIntentFilter.setPriority(2);
        appContext.registerReceiver(cmdMessageReceiver, cmdIntentFilter);

        NewMessageBroadcastReceiver msgReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(100);
        appContext.registerReceiver(msgReceiver, intentFilter);

    }

    /**
     * cmd消息BroadcastReceiver
     */
    private BroadcastReceiver cmdMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取cmd message对象
            String msgId = intent.getStringExtra("msgid");
            EMMessage message = intent.getParcelableExtra("message");
            //获取消息body
            CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
            String aciton = cmdMsgBody.action;//获取自定义action
            //获取扩展属性
            try {
                int cmdType=message.getIntAttribute("CMDType");
                String content = message.getStringAttribute("content");
                //接受到好友请求
                if(cmdType==1){
                    CmdInvateBean invateBean = GsonTools.parseJsonToBean(content, CmdInvateBean.class);
                    // 自己封装的javabean
                    InviteMessage msg = new InviteMessage();
                    msg.setFrom(invateBean.easemobUser);
                    msg.setTime(System.currentTimeMillis());
                    msg.setReason(invateBean.attachMsg);
                    // 设置相应status
                    msg.setStatus(InviteStatus.BEINVITEED);
                    msg.setNickname(invateBean.nickName);
                    msg.setAvatar(invateBean.avatar);
                    msg.setUserId(invateBean.userId);
                    msg.setGender(invateBean.gender);
                    // 保存msg
                    InviteMsgRepository.saveMessage(appContext, msg);
                    IMUser user = AccountManager.getInstance().getContactList(appContext).get(Constant.NEW_FRIENDS_USERNAME);
                    // 未读数加1
                    user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
                    IMUserRepository.saveContact(appContext, user);
                    // 提示有新消息
                    EMNotifier.getInstance(appContext).notifyOnNewMsg();

                }
                //对方同意了加好友请求(好友添加)
                else if(cmdType==2){
                    CmdAgreeBean agreeBean = GsonTools.parseJsonToBean(content,CmdAgreeBean.class);
                    IMUser imUser = new IMUser();
                    imUser.setUserId(agreeBean.userId);
                    imUser.setNick(agreeBean.nickName);
                    imUser.setUsername(agreeBean.easemobUser);
                    imUser.setAvatar(agreeBean.avatar);
                    imUser.setIsMyFriends(true);
                    imUser.setGender(agreeBean.gender);
                    IMUtils.setUserHead(imUser);
                    IMUserRepository.saveContact(appContext, imUser);
                    AccountManager.getInstance().getContactList(appContext).put(imUser.getUsername(),imUser);
                    EMConversation conversation = EMChatManager.getInstance().getConversation(imUser.getUsername());
                    if(conversation.getMsgCount()==0){
                        EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                        TextMessageBody body = new TextMessageBody(appContext.getResources().getString(R.string.agree_add_contact));
                        msg.addBody(body);
                        msg.setMsgId(UUID.randomUUID().toString());
                        msg.setFrom(agreeBean.easemobUser);
                        msg.setTo(AccountManager.getInstance().getLoginAccount(context).easemobUser);
                        msg.setMsgTime(System.currentTimeMillis());
                        msg.setUnread(true);
                        EMChatManager.getInstance().saveMessage(msg);
                        // 提示有新消息
                        EMNotifier.getInstance(appContext).notifyOnNewMsg();
                    }



                }
                //删除好友
                else if(cmdType==3){
                    CmdDeleteBean deleteBean = GsonTools.parseJsonToBean(content,CmdDeleteBean.class);
                    final IMUser imUser = IMUserRepository.getContactByUserId(appContext,deleteBean.userId);
                    if(imUser!=null){
                        AccountManager.getInstance().getContactList(appContext).remove(imUser.getUsername());
                        IMUserRepository.deleteContact(appContext, imUser.getUsername());
                        // 删除此会话
                        EMChatManager.getInstance().deleteConversation(imUser.getUsername(),true);
                        InviteMsgRepository.deleteInviteMsg(appContext, imUser.getUsername());
                    }

                }

            } catch (EaseMobException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 新消息广播接收者
     */
    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 消息id
            String username = intent.getStringExtra("from");
            String msgid = intent.getStringExtra("msgid");
            final EMMessage message = EMChatManager.getInstance().getMessage(msgid);
            final String fromUser = message.getStringAttribute(Constant.FROM_USER,"");
            final String finalUsername = username;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(!TextUtils.isEmpty(fromUser)){
                        IMUser imUser = IMUtils.getUserInfoFromMessage(appContext,message);
                        IMUserRepository.saveContact(appContext,imUser);
                    }
                }
            }).start();
//            notifyNewMessage(message);
        }
    }

    /**
     * 当应用在前台时，如果当前消息不是属于当前会话，在状态栏提示一下
     * 如果不需要，注释掉即可
     * @param message
     */
    protected void notifyNewMessage(EMMessage message) {
        //如果是设置了不提醒只显示数目的群组(这个是app里保存这个数据的，demo里不做判断)
        //以及设置了setShowNotificationInbackgroup:false(设为false后，后台时sdk也发送广播)
        if(!EasyUtils.isAppRunningForeground(appContext)){
            return;
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext)
                .setSmallIcon(appContext.getApplicationInfo().icon)
                .setWhen(System.currentTimeMillis()).setAutoCancel(true);

//        String ticker = IMUtils.getMessageDigest(message, appContext);
//        if(message.getType() == EMMessage.Type.TXT)
//            ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
//        //设置状态栏提示
        mBuilder.setTicker("收到一条新消息");

        //必须设置pendingintent，否则在2.3的机器上会有bug
        Intent intent = new Intent(appContext, IMMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        int notifiId=11;
        PendingIntent pendingIntent = PendingIntent.getActivity(appContext, notifiId, intent, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pendingIntent);

        Notification notification = mBuilder.build();
        NotificationManager notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notifiId, notification);
        notificationManager.cancel(notifiId);
    }


    @Override
    protected HXSDKModel createModel() {
        return new PeachHXSDKModel(appContext);
    }
    
    /**
     * get demo HX SDK Model
     */
    public PeachHXSDKModel getModel(){
        return (PeachHXSDKModel) hxModel;
    }
    


    @Override
    public void logout(final EMCallBack callback){
        super.logout(new EMCallBack(){
            @Override
            public void onSuccess() {
                if(callback != null){
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int code, String message) {
                if(callback != null){
                    callback.onError(code,message);
                }
                
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub
                if(callback != null){
                    callback.onProgress(progress, status);
                }
            }
            
        });
    }
}
