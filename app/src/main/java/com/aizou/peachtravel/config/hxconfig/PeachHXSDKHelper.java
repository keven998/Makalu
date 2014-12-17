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


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.aizou.core.utils.GsonTools;
import com.aizou.core.utils.SharePrefUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.bean.CmdAgreeBean;
import com.aizou.peachtravel.bean.CmdDeleteBean;
import com.aizou.peachtravel.bean.CmdInvateBean;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.hxsdk.controller.HXSDKHelper;
import com.aizou.peachtravel.common.hxsdk.model.HXSDKModel;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.config.Constant;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.InviteMessage;
import com.aizou.peachtravel.db.InviteStatus;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.aizou.peachtravel.db.respository.InviteMsgRepository;
import com.aizou.peachtravel.module.toolbox.im.ChatActivity;
import com.aizou.peachtravel.module.toolbox.im.VoiceCallReceiver;
import com.easemob.EMCallBack;

import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMNotifier;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.chat.OnNotificationClickListener;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;

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
        // EMChatOptions options = EMChatManager.getInstance().getChatOptions();
    }

    @Override
    protected OnMessageNotifyListener getMessageNotifyListener(){
        // 取消注释，app在后台，有新消息来时，状态栏的消息提示换成自己写的
        return null;
//      return new OnMessageNotifyListener() {
//
//          @Override
//          public String onNewMessageNotify(EMMessage message) {
//              // 设置状态栏的消息提示，可以根据message的类型做相应提示
//              String ticker = IMUtils.getMessageDigest(message, appContext);
//              if(message.getType() == Type.TXT)
//                  ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
//              return message.getFrom() + ": " + ticker;
//          }
//
//          @Override
//          public String onLatestMessageNotify(EMMessage message, int fromUsersNum, int messageNum) {
//              return null;
//             // return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
//          }
//
//          @Override
//          public String onSetNotificationTitle(EMMessage message) {
//              //修改标题,这里使用默认
//              return null;
//          }
//
//          @Override
//          public int onSetSmallIcon(EMMessage message) {
//              //设置小图标
//              return 0;
//          }
//      };
    }
    
    @Override
    protected OnNotificationClickListener getNotificationClickListener(){
        return new OnNotificationClickListener() {

            @Override
            public Intent onNotificationClick(EMMessage message) {
                Intent intent = new Intent(appContext, ChatActivity.class);
                ChatType chatType = message.getChatType();
                if (chatType == ChatType.Chat) { // 单聊信息
                    intent.putExtra("userId", message.getFrom());
                    intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
                } else { // 群聊信息
                            // message.getTo()为群聊id
                    intent.putExtra("groupId", message.getTo());
                    intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                }
                return intent;
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
        cmdIntentFilter.setPriority(3);
        appContext.registerReceiver(cmdMessageReceiver, cmdIntentFilter);

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
