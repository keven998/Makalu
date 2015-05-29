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
package com.xuejian.client.lxp.module.toolbox.im;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.pagerIndicator.indicator.FixedIndicatorView;
import com.aizou.core.widget.pagerIndicator.indicator.IndicatorViewPager;
import com.aizou.core.widget.pagerIndicator.indicator.slidebar.ColorBar;
import com.aizou.core.widget.pagerIndicator.viewpager.FixedViewPager;
import com.aizou.core.widget.popupmenu.PopupMenuCompat;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.EMNotifier;
import com.easemob.chat.GroupChangeListener;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.ChatBaseActivity;
import com.xuejian.client.lxp.bean.ContactListBean;
import com.xuejian.client.lxp.bean.PeachUser;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.config.Constant;
import com.xuejian.client.lxp.db.IMUser;
import com.xuejian.client.lxp.db.InviteMessage;
import com.xuejian.client.lxp.db.InviteStatus;
import com.xuejian.client.lxp.db.respository.IMUserRepository;
import com.xuejian.client.lxp.db.respository.InviteMsgRepository;
import com.xuejian.client.lxp.module.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IMMainActivity extends ChatBaseActivity {
    public static final int NEW_CHAT_REQUEST_CODE = 101;

    protected static final String TAG = "MainActivity";
    // 未读消息textview
    private TextView unreadLabel;
    // 未读通讯录textview
    private TextView unreadAddressLable;
    FixedIndicatorView mIMIndicator;
    FixedViewPager mIMViewPager;
    private IndicatorViewPager indicatorViewPager;
    private IMMainAdapter mIMdapter;
    private ContactlistFragment contactListFragment;
    //	private ChatHistoryFragment chatHistoryFragment;
    private ChatAllHistoryFragment chatHistoryFragment;
    private Fragment[] fragments;
    // 当前fragment的index
    private int currentTabIndex;
    private NewMessageBroadcastReceiver msgReceiver;
    private MyGroupChangeListener groupChangeListener;
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
        fragments = new Fragment[]{chatHistoryFragment, contactListFragment};
        if (savedInstanceState == null) {
        } else {
            currentTabIndex = savedInstanceState.getInt("currentTabIndex");
        }
        indicatorViewPager = new IndicatorViewPager(mIMIndicator, mIMViewPager);
        indicatorViewPager.setPageOffscreenLimit(2);
        mIMViewPager.setPrepareNumber(2);
        indicatorViewPager.setAdapter(mIMdapter = new IMMainAdapter(getSupportFragmentManager()));
        indicatorViewPager.setOnIndicatorPageChangeListener(new IndicatorViewPager.OnIndicatorPageChangeListener() {
            @Override
            public void onIndicatorPageChange(int preItem, int currentItem) {
                currentTabIndex = currentItem;
            }
        });

        indicatorViewPager.setCurrentItem(currentTabIndex,false);

//        EMGroupManager.getInstance().asyncGetGroupsFromServer(new EMValueCallBack<List<EMGroup>>() {
//            @Override
//            public void onSuccess(List<EMGroup> emGroups) {
//                if (chatHistoryFragment != null) {
//                    chatHistoryFragment.refresh();
//                }
//            }
//
//            @Override
//            public void onError(int i, String s) {
//
//            }
//        });
        //网络更新好友列表
        getContactFromServer();
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

        // 注册群聊相关的listener
        groupChangeListener = new MyGroupChangeListener();
        EMGroupManager.getInstance().addGroupChangeListener(groupChangeListener);
        // 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
        EMChat.getInstance().setAppInited();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        indicatorViewPager.setCurrentItem(0, false);
    }

    public void refreshChatHistoryFragment() {
        if (chatHistoryFragment != null) {
            chatHistoryFragment.refresh();
        }
    }

    public void refreshContactListFragment() {
        if (contactListFragment != null) {
            contactListFragment.refresh();
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        initTitleBar();
        mIMIndicator = (FixedIndicatorView) findViewById(R.id.im_indicator);
        mIMViewPager = (FixedViewPager) findViewById(R.id.im_viewpager);
        ColorBar colorBar = new ColorBar(this, getResources().getColor(R.color.app_theme_color_secondary), LocalDisplay.dp2px(5));
        colorBar.setWidth(LocalDisplay.dp2px(45));
        mIMIndicator.setScrollBar(colorBar);

    }

    private void initTitleBar() {
        findViewById(R.id.ly_title_bar_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                BlurMenu fragment = new BlurMenu();
//                Bundle args = new Bundle();
//                args.putInt(SupportBlurDialogFragment.BUNDLE_KEY_BLUR_RADIUS, 5);
//                args.putFloat(SupportBlurDialogFragment.BUNDLE_KEY_DOWN_SCALE_FACTOR, 6);
//                fragment.setArguments(args);
//                fragment.show(getSupportFragmentManager(), "blur_menu");
                showActionDialog();
            }
        });

        findViewById(R.id.ly_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finishWithNoAnim();
                overridePendingTransition(0, android.R.anim.fade_out);
            }
        });
    }

    private void showActionDialog() {
        final Activity act = this;
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        View contentView = View.inflate(act, R.layout.dialog_city_detail_action, null);
        Button btn = (Button) contentView.findViewById(R.id.btn_go_plan);
        btn.setText("新建Talk");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(mContext,"event_create_new_talk");
                startActivityForResult(new Intent(IMMainActivity.this, PickContactsWithCheckboxActivity.class).putExtra("request", NEW_CHAT_REQUEST_CODE), NEW_CHAT_REQUEST_CODE);
                dialog.dismiss();
            }
        });
        Button btn1 = (Button) contentView.findViewById(R.id.btn_go_share);
        btn1.setText("加好友");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(mContext,"event_add_new_friend");
                startActivity(new Intent(IMMainActivity.this, AddContactActivity.class));
                dialog.dismiss();
            }
        });
        contentView.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        WindowManager windowManager = act.getWindowManager();
        Window window = dialog.getWindow();
        window.setContentView(contentView);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (display.getWidth()); // 设置宽度
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finishWithNoAnim();
        overridePendingTransition(0, R.anim.fade_out);
    }

    private void showMoreMenu(View view) {
        PopupMenuCompat menu = new PopupMenuCompat(this, view);
        menu.inflate(R.menu.menu_im_add_more);
        menu.setOnMenuItemClickListener(new PopupMenuCompat.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_add_friends:
                        startActivity(new Intent(mContext, AddContactActivity.class));
                        break;

                    case R.id.menu_new_message:
                        startActivity(new Intent(mContext, PickContactsWithCheckboxActivity.class).putExtra("request", NEW_CHAT_REQUEST_CODE));
                        break;
                }
                return false;
            }
        });
        menu.show();
    }

    private void getContactFromServer() {

        UserApi.getContact(new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<ContactListBean> contactResult = CommonJson.fromJson(result, ContactListBean.class);

                if (contactResult.code == 0) {
                    IMUserRepository.clearMyFriendsContact(mContext);
                    AccountManager.getInstance().setContactList(null);
                    Map<String, IMUser> userlist = new HashMap<String, IMUser>();
                    // 添加user"申请与通知"
                    IMUser newFriends = new IMUser();
                    newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
                    newFriends.setNick("好友请求");
                    newFriends.setHeader("");
                    newFriends.setIsMyFriends(true);
                    newFriends.setUnreadMsgCount((int) InviteMsgRepository.getUnAcceptMsgCount(mContext));
                    userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
//                    // 添加"群聊"
//                    IMUser groupUser = new IMUser();
//                    groupUser.setUsername(Constant.GROUP_USERNAME);
//                    groupUser.setNick("群聊");
//                    groupUser.setHeader("");
//                    groupUser.setUnreadMsgCount(0);
//                    userlist.put(Constant.GROUP_USERNAME, groupUser);
                    // 存入内存
                    for (PeachUser peachUser : contactResult.result.contacts) {
                        IMUser user = new IMUser();
                        user.setUserId(peachUser.userId);
                        user.setMemo(peachUser.memo);
                        user.setNick(peachUser.nickName);
                        user.setUsername(peachUser.easemobUser);
                        user.setUnreadMsgCount(0);
                        user.setAvatar(peachUser.avatar);
                        user.setAvatarSmall(peachUser.avatarSmall);
                        user.setSignature(peachUser.signature);
                        user.setIsMyFriends(true);
                        user.setGender(peachUser.gender);
                        IMUtils.setUserHead(user);
                        userlist.put(peachUser.easemobUser, user);
                    }
                    // 存入db
                    List<IMUser> users = new ArrayList<IMUser>(userlist.values());
                    IMUserRepository.saveContactList(mContext, users);
                    AccountManager.getInstance().setContactList(userlist);
                    refreshContactListFragment();
                    refreshChatHistoryFragment();

                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
//                ToastUtil.getInstance(IMMainActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }

    private class IMMainAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

        public IMMainAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            View view = convertView;
            if (view == null) {
                if (position == 0) {
                    view = View.inflate(mContext, R.layout.tab_im_conversation, null);
                } else {
                    view = View.inflate(mContext, R.layout.tab_im_contact, null);
                }
            }

            if (position == 0) {
                unreadLabel = (TextView) view.findViewById(R.id.unread_msg_notify);
            } else {
                unreadAddressLable = (TextView) view.findViewById(R.id.unread_address_number);
            }

            return view;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            return fragments[position];
        }

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
        try {
            // 注册群聊相关的listener
            EMGroupManager.getInstance().removeGroupChangeListener(groupChangeListener);
        } catch (Exception e) {

        }

    }

    /**
     * 刷新未读消息数
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
        if (count > 0) {
            unreadLabel.setVisibility(View.VISIBLE);
            unreadLabel.setText(count+"");
        } else {
            unreadLabel.setVisibility(View.GONE);
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
                    unreadAddressLable.setVisibility(View.GONE);
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
        unreadAddressCountTotal = (int) InviteMsgRepository.getUnAcceptMsgCount(this);
        if (AccountManager.getInstance().getContactList(this).get(Constant.NEW_FRIENDS_USERNAME) != null) {

            IMUser imUser = AccountManager.getInstance().getContactList(this).get(Constant.NEW_FRIENDS_USERNAME);
            imUser.setUnreadMsgCount(unreadAddressCountTotal);
            IMUserRepository.saveContact(this, imUser);
        }
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
                int cmdType = message.getIntAttribute("CMDType");
                String content = message.getStringAttribute("content");
                //接受到好友请求
                if (cmdType == 1) {
                    // 刷新bottom bar消息未读数
                    updateUnreadAddressLable();
                    // 刷新好友页面ui
                    if (currentTabIndex == 1)
                        contactListFragment.refresh();

                }
                //对方同意了加好友请求(好友添加)
                else if (cmdType == 2) {
                    updateUnreadLabel();
                    if (chatHistoryFragment != null)
                        chatHistoryFragment.refresh();
                    if (contactListFragment != null)
                        contactListFragment.refresh();


                }
                //删除好友
                else if (cmdType == 3) {
                    // 刷新ui
                    if (chatHistoryFragment != null)
                        chatHistoryFragment.refresh();
                    if (contactListFragment != null)
                        contactListFragment.refresh();

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
            //主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看

            // 消息id
            String username = intent.getStringExtra("from");
            String msgid = intent.getStringExtra("msgid");
            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
            final EMMessage message = EMChatManager.getInstance().getMessage(msgid);
            final String fromUser = message.getStringAttribute(Constant.FROM_USER, "");
            final String finalUsername = username;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(fromUser)) {
                       IMUser imUser = IMUtils.getUserInfoFromMessage(mContext, message);
                        IMUserRepository.saveContact(mContext, imUser);
                    }
                }
            }).start();

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


    /**
     * 保存提示新消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg) {

        // 刷新bottom bar消息未读数
        updateUnreadAddressLable();
        // 刷新好友页面ui
        if (currentTabIndex == 1)
            contactListFragment.refresh();
    }


    /**
     * MyGroupChangeListener
     */
    private class MyGroupChangeListener implements GroupChangeListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            if (chatHistoryFragment != null) {
                chatHistoryFragment.refresh();
            }

        }

        @Override
        public void onInvitationAccpted(String groupId, String inviter, String reason) {
            if (chatHistoryFragment != null) {
                chatHistoryFragment.refresh();
            }
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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("currentTabIndex", currentTabIndex);
        super.onSaveInstanceState(outState);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case NEW_CHAT_REQUEST_CODE:
                    int chatType = data.getIntExtra("chatType", 0);
                    String toId = data.getStringExtra("toId");
                    if (chatType == ChatActivity.CHATTYPE_GROUP) {
                        //进入群聊
                        Intent intent = new Intent(mContext, ChatActivity.class);
                        // it is group chat
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                        intent.putExtra("groupId", toId);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, ChatActivity.class);
                        // it is single chat
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
                        intent.putExtra("userId", toId);
                        startActivity(intent);
                    }


            }
        }

    }

}