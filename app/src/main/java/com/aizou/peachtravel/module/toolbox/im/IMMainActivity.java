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
package com.aizou.peachtravel.module.toolbox.im;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.GsonTools;
import com.aizou.core.widget.popupmenu.PopupMenuCompat;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.BaseChatActivity;
import com.aizou.peachtravel.bean.CmdAgreeBean;
import com.aizou.peachtravel.bean.CmdDeleteBean;
import com.aizou.peachtravel.bean.CmdInvateBean;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.widget.BlurDialogMenu.BlurDialogFragment;
import com.aizou.peachtravel.common.widget.BlurDialogMenu.SupportBlurDialogFragment;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.config.Constant;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.InviteMessage;
import com.aizou.peachtravel.db.InviteStatus;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.aizou.peachtravel.db.respository.InviteMsgRepository;
import com.aizou.peachtravel.module.my.LoginActivity;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.ConnectionListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.EMNotifier;
import com.easemob.chat.GroupChangeListener;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.NetUtils;

public class IMMainActivity extends BaseChatActivity {
    public static final int NEW_CHAT_REQUEST_CODE=101;

	protected static final String TAG = "MainActivity";
	// 未读消息textview
	private TextView unreadLabel;
	// 未读通讯录textview
	private TextView unreadAddressLable;

	private Button[] mTabs;
	private ContactlistFragment contactListFragment;
//	private ChatHistoryFragment chatHistoryFragment;
	private ChatAllHistoryFragment chatHistoryFragment;
	private SettingsFragment settingFragment;
	private Fragment[] fragments;
	private int index;
	private RelativeLayout[] tab_containers;
	// 当前fragment的index
	private int currentTabIndex;
	private NewMessageBroadcastReceiver msgReceiver;
	// 账号在别处登录
	private boolean isConflict = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_im_main);
		initView();

		//这个fragment只显示好友和群组的聊天记录
//		chatHistoryFragment = new ChatHistoryFragment();
		//显示所有人消息记录的fragment
		chatHistoryFragment = new ChatAllHistoryFragment();
		contactListFragment = new ContactlistFragment();
		settingFragment = new SettingsFragment();
		fragments = new Fragment[] { chatHistoryFragment, contactListFragment, settingFragment };
		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, chatHistoryFragment)
				.add(R.id.fragment_container, contactListFragment).hide(contactListFragment).show(chatHistoryFragment)
				.commit();
        // 注册一个cmd消息的BroadcastReceiver
        IntentFilter cmdIntentFilter = new IntentFilter(EMChatManager.getInstance().getCmdMessageBroadcastAction());
        cmdIntentFilter.setPriority(3);
        mContext.registerReceiver(cmdMessageReceiver, cmdIntentFilter);


		// 注册一个接收消息的BroadcastReceiver
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);

		// 注册一个ack回执消息的BroadcastReceiver
		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance()
				.getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(3);
		registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

		// 注册一个离线消息的BroadcastReceiver
//		IntentFilter offlineMessageIntentFilter = new IntentFilter(EMChatManager.getInstance()
//				.getOfflineMessageBroadcastAction());
//		registerReceiver(offlineMessageReceiver, offlineMessageIntentFilter);
		
//		// setContactListener监听联系人的变化等
//		EMContactManager.getInstance().setContactListener(new MyContactListener());
		// 注册一个监听连接状态的listener
		EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
		// 注册群聊相关的listener
		EMGroupManager.getInstance().addGroupChangeListener(new MyGroupChangeListener());
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
	}

    public void refreshChatHistoryFragment(){
        if(chatHistoryFragment!=null){
            chatHistoryFragment.refresh();
        }
    }

    public void refreshContactListFragment(){
        if(contactListFragment!=null){
            contactListFragment.refresh();
        }
    }

	/**
	 * 初始化组件
	 */
	private void initView() {
        initTitleBar();
		unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
		unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);
		mTabs = new Button[3];
		mTabs[0] = (Button) findViewById(R.id.btn_conversation);
		mTabs[1] = (Button) findViewById(R.id.btn_address_list);
//		mTabs[2] = (Button) findViewById(R.id.btn_setting);
		// 把第一个tab设为选中状态
		mTabs[0].setSelected(true);

	}

    private void initTitleBar(){
        final TitleHeaderBar titleHeaderBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
        titleHeaderBar.setRightViewImageRes(R.drawable.add);
        titleHeaderBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showMoreMenu(titleHeaderBar.getRightTextView());
                BlurMenu fragment = new BlurMenu();
                Bundle args = new Bundle();
                args.putInt(
                        SupportBlurDialogFragment.BUNDLE_KEY_BLUR_RADIUS,
                        4
                );
                args.putFloat(
                        SupportBlurDialogFragment.BUNDLE_KEY_DOWN_SCALE_FACTOR,
                        5
                );

                fragment.setArguments(args);
                fragment.show(getSupportFragmentManager(), "blur_menu");
            }
        });

        TitleHeaderBar thbar = (TitleHeaderBar)findViewById(R.id.ly_header_bar_title_wrap);
        thbar.getTitleTextView().setText("旅行圈");

    }

    private void showMoreMenu(View view){
        PopupMenuCompat menu = new PopupMenuCompat(this,view);
        menu.inflate(R.menu.menu_im_add_more);
        menu.setOnMenuItemClickListener(new PopupMenuCompat.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_add_friends:
                        startActivity(new Intent(mContext, AddContactActivity.class));
                        break;
                    case R.id.menu_new_message:
                        startActivity(new Intent(mContext, PickContactsWithCheckboxActivity.class).putExtra("request",NEW_CHAT_REQUEST_CODE));
                        break;
                }
                return false;
            }
        });
        menu.show();
    }

	/**
	 * button点击事件
	 * 
	 * @param view
	 */
	public void onTabClicked(View view) {
		switch (view.getId()) {
		case R.id.btn_conversation:
			index = 0;
			break;
		case R.id.btn_address_list:
			index = 1;
			break;
//		case R.id.btn_setting:
//			index = 2;
//			break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 注销广播接收者
		try {
			unregisterReceiver(msgReceiver);
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(ackMessageReceiver);
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(cmdMessageReceiver);
		} catch (Exception e) {
		}

		if (conflictBuilder != null) {
			conflictBuilder.create().dismiss();
			conflictBuilder = null;
		}

	}

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			unreadLabel.setText(String.valueOf(count));
			unreadLabel.setVisibility(View.VISIBLE);
		} else {
			unreadLabel.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 刷新申请与通知消息数
	 */
	public void updateUnreadAddressLable() {
		runOnUiThread(new Runnable() {
			public void run() {
				int count = getUnreadAddressCountTotal();
				if (count > 0) {
					unreadAddressLable.setText(String.valueOf(count));
					unreadAddressLable.setVisibility(View.VISIBLE);
				} else {
					unreadAddressLable.setVisibility(View.INVISIBLE);
				}
			}
		});

	}

	/**
	 * 获取未读申请与通知消息
	 * 
	 * @return
	 */
	public int getUnreadAddressCountTotal() {
		int unreadAddressCountTotal = 0;
		if (AccountManager.getInstance().getContactList(this).get(Constant.NEW_FRIENDS_USERNAME) != null)
			unreadAddressCountTotal = AccountManager.getInstance().getContactList(this).get(Constant.NEW_FRIENDS_USERNAME)
					.getUnreadMsgCount();
		return unreadAddressCountTotal;
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		return unreadMsgCountTotal;
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
                    CmdInvateBean invateBean = GsonTools.parseJsonToBean(content,CmdInvateBean.class);
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
                    notifyNewIviteMessage(msg);

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
                    IMUserRepository.saveContact(mContext, imUser);
                    AccountManager.getInstance().getContactList(mContext).put(imUser.getUsername(),imUser);
                    EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                    TextMessageBody body = new TextMessageBody(getResources().getString(R.string.agree_add_contact));
                    msg.addBody(body);
                    msg.setMsgId(UUID.randomUUID().toString());
                    msg.setFrom(agreeBean.easemobUser);
                    msg.setTo(AccountManager.getInstance().getLoginAccount(context).easemobUser);
                    msg.setMsgTime(System.currentTimeMillis());
                    msg.setUnread(true);
                    EMChatManager.getInstance().saveMessage(msg);
                    // 提示有新消息
                    EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();
                    updateUnreadLabel();
                    if(currentTabIndex==0)
                        chatHistoryFragment.refresh();
                    if (currentTabIndex == 1)
                        contactListFragment.refresh();


                }
                //删除好友
                else if(cmdType==3){
                    CmdDeleteBean deleteBean = GsonTools.parseJsonToBean(content,CmdDeleteBean.class);
                    final IMUser imUser = IMUserRepository.getContactByUserId(mContext,deleteBean.userId);
                    if(imUser!=null){
                        AccountManager.getInstance().getContactList(mContext).remove(imUser.getUsername());
                        IMUserRepository.deleteContact(mContext, imUser.getUsername());
                        // 删除此会话
                        EMChatManager.getInstance().deleteConversation(imUser.getUsername());
                        InviteMsgRepository.deleteInviteMsg(mContext, imUser.getUsername());
                        runOnUiThread(new Runnable() {
                            public void run() {
                                //如果正在与此用户的聊天页面
                                if (ChatActivity.activityInstance != null && imUser.getUsername().equals(ChatActivity.activityInstance.getToChatUsername())) {
                                    Toast.makeText(IMMainActivity.this, ChatActivity.activityInstance.getToChatUsername() + "已把你从他好友列表里移除", Toast.LENGTH_SHORT).show();
                                    ChatActivity.activityInstance.finish();
                                }
                                updateUnreadLabel();
                            }
                        });
                        // 刷新ui
                        if (currentTabIndex == 1)
                            contactListFragment.refresh();
                    }

                }

            } catch (EaseMobException e) {
                e.printStackTrace();
            }
        }
    };

	/**
	 * 新消息广播接收者
	 * 
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			//主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看
			
			// 消息id
			String msgId = intent.getStringExtra("msgid");
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			// EMMessage message =
			// EMChatManager.getInstance().getMessage(msgId);

			// 刷新bottom bar消息未读数
			updateUnreadLabel();
			if (currentTabIndex == 0) {
				// 当前页面如果为聊天历史页面，刷新此页面
				if (chatHistoryFragment != null) {
					chatHistoryFragment.refresh();
				}
			}
			// 注销广播，否则在ChatActivity中会收到这个广播
			abortBroadcast();
		}
	}

	/**
	 * 消息回执BroadcastReceiver
	 */
	private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					msg.isAcked = true;
				}
			}
			abortBroadcast();
		}
	};

	/**
	 * 离线消息BroadcastReceiver
	 * sdk 登录后，服务器会推送离线消息到client，这个receiver，是通知UI 有哪些人发来了离线消息
	 * UI 可以做相应的操作，比如下载用户信息
	 */
//	private BroadcastReceiver offlineMessageReceiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String[] users = intent.getStringArrayExtra("fromuser");
//			String[] groups = intent.getStringArrayExtra("fromgroup");
//			if (users != null) {
//				for (String user : users) {
//					System.out.println("收到user离线消息：" + user);
//				}
//			}
//			if (groups != null) {
//				for (String group : groups) {
//					System.out.println("收到group离线消息：" + group);
//				}
//			}
//			abortBroadcast();
//		}
//	};
	

	/***
	 * 好友变化listener
	 * 
	 */
	private class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(List<String> usernameList) {



		}

		
		@Override
		public void onContactDeleted(final List<String> usernameList) {
			// 被删除
			Map<String, IMUser> localUsers = AccountManager.getInstance().getContactList(IMMainActivity.this);
			for (String username : usernameList) {
				localUsers.remove(username);
				IMUserRepository.deleteContact(mContext, username);
				InviteMsgRepository.deleteInviteMsg(mContext, username);
			}
			runOnUiThread(new Runnable() {
				public void run() {
					//如果正在与此用户的聊天页面
					if (ChatActivity.activityInstance != null && usernameList.contains(ChatActivity.activityInstance.getToChatUsername())) {
						Toast.makeText(IMMainActivity.this, ChatActivity.activityInstance.getToChatUsername()+"已把你从他好友列表里移除", Toast.LENGTH_SHORT).show();
						ChatActivity.activityInstance.finish();
					}
					updateUnreadLabel();
				}
			});
			// 刷新ui
			if (currentTabIndex == 1)
				contactListFragment.refresh();

		}

		@Override
		public void onContactInvited(final String username, final String reason) {
			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不要重复提醒
			List<InviteMessage> msgs = InviteMsgRepository.getMessagesList(mContext);
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
				    InviteMsgRepository.deleteInviteMsg(mContext,username);
				}
			}

            UserApi.seachContact(username,new HttpCallBack<String>() {
                @Override
                public void doSucess(String result, String method) {
                    CommonJson4List<PeachUser> seachResult = CommonJson4List.fromJson(result,PeachUser.class);
                    if(seachResult.code==0){
                        if(seachResult.result.size()>0){
                            PeachUser user = seachResult.result.get(0);
                            // 自己封装的javabean
                            final InviteMessage msg = new InviteMessage();
                            msg.setFrom(username);
                            msg.setTime(System.currentTimeMillis());
                            msg.setReason(reason);
                            Log.d(TAG, username + "请求加你为好友,reason: " + reason);
                            // 设置相应status
                            msg.setStatus(InviteStatus.BEINVITEED);
                            msg.setNickname(user.nickName);
                            msg.setUserId(user.userId);
                            notifyNewIviteMessage(msg);

                        }
                    }
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {

                }
            });



		}

		@Override
		public void onContactAgreed(String username) {
//			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
//			for (InviteMessage inviteMessage : msgs) {
//				if (inviteMessage.getFrom().equals(username)) {
//					return;
//				}
//			}
//			// 自己封装的javabean
//			InviteMessage msg = new InviteMessage();
//			msg.setFrom(username);
//			msg.setTime(System.currentTimeMillis());
//			Log.d(TAG, username + "同意了你的好友请求");
//			msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
//			notifyNewIviteMessage(msg);

		}

		@Override
		public void onContactRefused(String username) {
			// 参考同意，被邀请实现此功能,demo未实现

		}

	}

	/**
	 * 保存提示新消息
	 * 
	 * @param msg
	 */
	private void notifyNewIviteMessage(InviteMessage msg) {
        saveInviteMsg(msg);
		// 提示有新消息
		EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();
		// 刷新bottom bar消息未读数
		updateUnreadAddressLable();
		// 刷新好友页面ui
		if (currentTabIndex == 1)
			contactListFragment.refresh();
	}

	/**
	 * 保存邀请等msg
	 * @param msg
	 */
	private void saveInviteMsg(InviteMessage msg) {
		// 保存msg
		InviteMsgRepository.saveMessage(mContext, msg);
        IMUser user = AccountManager.getInstance().getContactList(IMMainActivity.this).get(Constant.NEW_FRIENDS_USERNAME);
        // 未读数加1
        user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
        IMUserRepository.saveContact(mContext,user);

	}


	/**
	 * 连接监听listener
	 * 
	 */
	private class MyConnectionListener implements ConnectionListener {

		@Override
		public void onConnected() {
//			chatHistoryFragment.errorItem.setVisibility(View.GONE);
		}

		@Override
		public void onDisConnected(String errorString) {
			if (errorString != null && errorString.contains("conflict")) {
				// 显示帐号在其他设备登陆dialog
				showConflictDialog();
			} else {
//				chatHistoryFragment.errorItem.setVisibility(View.VISIBLE);
//				if(NetUtils.hasNetwork(IMMainActivity.this))
//					chatHistoryFragment.errorText.setText("连接不到聊天服务器");
//				else
//					chatHistoryFragment.errorText.setText("当前网络不可用，请检查网络设置");
					
			}
		}

		@Override
		public void onReConnected() {
//			chatHistoryFragment.errorItem.setVisibility(View.GONE);
		}

		@Override
		public void onReConnecting() {
		}

		@Override
		public void onConnecting(String progress) {
		}

	}

	/**
	 * MyGroupChangeListener
	 */
	private class MyGroupChangeListener implements GroupChangeListener {

		@Override
		public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
//			boolean hasGroup = false;
//			for(EMGroup group : EMGroupManager.getInstance().getAllGroups()){
//				if(group.getGroupId().equals(groupId)){
//					hasGroup = true;
//					break;
//				}
//			}
//			if(!hasGroup)
//				return;
//
//			// 被邀请
//			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
//			msg.setChatType(ChatType.GroupChat);
//			msg.setFrom(inviter);
//			msg.setTo(groupId);
//			msg.setMsgId(UUID.randomUUID().toString());
//			msg.addBody(new TextMessageBody(inviter + "邀请你加入了群聊"));
//			// 保存邀请消息
//			EMChatManager.getInstance().saveMessage(msg);
//			// 提醒新消息
//			EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();
//
//			runOnUiThread(new Runnable() {
//				public void run() {
//					updateUnreadLabel();
//					// 刷新ui
//					if (currentTabIndex == 0)
//						chatHistoryFragment.refresh();
//					if (CommonUtils.getTopActivity(IMMainActivity.this).equals(GroupsActivity.class.getName())) {
//						GroupsActivity.instance.onResume();
//					}
//				}
//			});

		}

		@Override
		public void onInvitationAccpted(String groupId, String inviter, String reason) {

		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee, String reason) {

		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {
			// 提示用户被T了，demo省略此步骤
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						updateUnreadLabel();
						if (currentTabIndex == 0)
							chatHistoryFragment.refresh();
						if (CommonUtils.getTopActivity(IMMainActivity.this).equals(GroupsActivity.class.getName())) {
							GroupsActivity.instance.onResume();
						}
					} catch (Exception e) {
						Log.e("###", "refresh exception " + e.getMessage());
					}

				}
			});
		}

		@Override
		public void onGroupDestroy(String groupId, String groupName) {
			// 群被解散
			// 提示用户群被解散,demo省略
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					if (currentTabIndex == 0)
						chatHistoryFragment.refresh();
					if (CommonUtils.getTopActivity(IMMainActivity.this).equals(GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			});

		}

		@Override
		public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {
			// 用户申请加入群聊
			InviteMessage msg = new InviteMessage();
			msg.setFrom(applyer);
			msg.setTime(System.currentTimeMillis());
			msg.setGroupId(groupId);
			msg.setGroupName(groupName);
			msg.setReason(reason);
			Log.d(TAG, applyer + " 申请加入群聊：" + groupName);
			msg.setStatus(InviteStatus.BEAPPLYED);
			notifyNewIviteMessage(msg);
		}

		@Override
		public void onApplicationAccept(String groupId, String groupName, String accepter) {
			//加群申请被同意
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(accepter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new TextMessageBody(accepter + "同意了你的群聊申请"));
			// 保存同意消息
			EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();
			
			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					// 刷新ui
					if (currentTabIndex == 0)
						chatHistoryFragment.refresh();
					if (CommonUtils.getTopActivity(IMMainActivity.this).equals(GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			});
		}

		@Override
		public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
			//加群申请被拒绝，demo未实现
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isConflict) {
			updateUnreadLabel();
			updateUnreadAddressLable();
			EMChatManager.getInstance().activityResumed();
		}

	}


	private android.app.AlertDialog.Builder conflictBuilder;
	private boolean isConflictDialogShow;

	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
        AccountManager.getInstance().logout(this);

		if (!IMMainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(IMMainActivity.this);
				    conflictBuilder.setTitle("下线通知");
				    conflictBuilder.setMessage(R.string.connect_conflict);
				    conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						conflictBuilder = null;
						finish();
						startActivity(new Intent(IMMainActivity.this, LoginActivity.class));
					}
				});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				Log.e("###", "---------color conflictBuilder error" + e.getMessage());
			}

		}

	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow)
			showConflictDialog();
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case NEW_CHAT_REQUEST_CODE:

            }
        }

    }

    public static class BlurMenu extends BlurDialogFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog connectionDialog = new Dialog(getActivity(), R.style.TransparentDialog);
            View customView = getActivity().getLayoutInflater().inflate(R.layout.im_main_dialog_menu, null);
            connectionDialog.setContentView(customView);
//            customView.findViewById(R.id.dialog_frame).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dismiss();
//                }
//            });
            customView.findViewById(R.id.new_talk).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), PickContactsWithCheckboxActivity.class).putExtra("request",NEW_CHAT_REQUEST_CODE));
                    dismiss();
                }
            });

            customView.findViewById(R.id.add_friend).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), AddContactActivity.class));
                    dismiss();
                }
            });
            return connectionDialog;
        }
    }

}