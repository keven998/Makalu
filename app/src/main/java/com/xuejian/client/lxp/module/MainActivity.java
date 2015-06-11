package com.xuejian.client.lxp.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.FragmentTabHost;
import com.easemob.EMCallBack;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMNotifier;
import com.easemob.chat.GroupChangeListener;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.lv.bean.MessageBean;
import com.lv.im.HandleImMessage;
import com.lv.im.IMClient;
import com.lv.user.LoginSuccessListener;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ContactListBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.config.Constant;
import com.xuejian.client.lxp.db.IMUser;
import com.xuejian.client.lxp.db.InviteMessage;
import com.xuejian.client.lxp.db.InviteStatus;
import com.xuejian.client.lxp.db.respository.IMUserRepository;
import com.xuejian.client.lxp.db.userDB.User;
import com.xuejian.client.lxp.db.userDB.UserDBManager;
import com.xuejian.client.lxp.module.dest.TripFragment;
import com.xuejian.client.lxp.module.my.LoginActivity;
import com.xuejian.client.lxp.module.my.MyFragment;
import com.xuejian.client.lxp.module.toolbox.TalkFragment;
import com.xuejian.client.lxp.module.toolbox.im.GroupsActivity;
import com.xuejian.client.lxp.module.toolbox.im.IMMainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class MainActivity extends PeachBaseActivity implements HandleImMessage.MessageHandler {
    public final static int CODE_IM_LOGIN = 101;
    public static final int NEW_CHAT_REQUEST_CODE = 102;
    // 账号在别处登录
    public boolean isConflict = false;
    //定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    //定义一个布局
    private LayoutInflater layoutInflater;

    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {TalkFragment.class, TripFragment.class, MyFragment.class,};

   // 定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.checker_tab_home, R.drawable.checker_tab_home_destination, R.drawable.checker_tab_home_user,
            };
    private String[] tabTitle = {"Talk", "旅游", "我"};

    private TextView unreadMsg;

    private Long NEWFRIEND=2l;
    //Tab选项Tag
    private String mTagArray[] = {"Talk", "Travel", "My"};
    //private NewMessageBroadcastReceiver msgReceiver;
    private MyGroupChangeListener groupChangeListener;
    private boolean FromBounce;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        //IMClient.initIM(getApplicationContext());
        FromBounce=getIntent().getBooleanExtra("FromBounce",false);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        if (getIntent().getBooleanExtra("conflict", false)){
            showConflictDialog(MainActivity.this);
        }
        if(!FromBounce) {
        com.lv.user.User.login(AccountManager.getInstance().getCurrentUserId(),new LoginSuccessListener() {
            @Override
            public void OnSuccess() {
                System.out.println("登陆成功");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TalkFragment talkFragment = (TalkFragment) getSupportFragmentManager().findFragmentByTag("Talk");
                        if (talkFragment != null) {
                            talkFragment.loadConversation();
                        }
                    }
                });
            }


                @Override
                public void OnFailed(int code) {
                    ToastUtil.getInstance(MainActivity.this).showToast("个推登录失败");
                }
            });
        }
      }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("conflict", false) ){
            showConflictDialog(MainActivity.this);
        }
    }

    private void initData(){
        //网络更新好友列表
        getContactFromServer();
    }

    private void getContactFromServer() {
        UserApi.getContact(new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                System.out.println(result);
                CommonJson<ContactListBean> contactResult = CommonJson.fromJson(result, ContactListBean.class);
                if (contactResult.code == 0) {
                  //  IMUserRepository.clearMyFriendsContact(mContext);
                    AccountManager.getInstance().setContactList(null);
                    Map<Long, User> userlist = new HashMap<Long,User>();

                    /*//添加默认服务号
                    IMUser paiServerUser = new IMUser();
                    paiServerUser.setUserId((long)10000);
                    paiServerUser.setUsername(serverName);
                    paiServerUser.setNick("旅行派团队");
                    paiServerUser.setHeader("");
                    paiServerUser.setAvatar(getResources().getResourceName(R.drawable.ic_news));
                    paiServerUser.setAvatarSmall(getResources().getResourceName(R.drawable.ic_news));
                    paiServerUser.setIsMyFriends(true);
                    userlist.put(serverName, paiServerUser);
*/
//                    // 添加"群聊"
//                    IMUser groupUser = new IMUser();
//                    groupUser.setUsername(Constant.GROUP_USERNAME);
//                    groupUser.setNick("群聊");
//                    groupUser.setHeader("");
//                    groupUser.setUnreadMsgCount(0);
//                    userlist.put(Constant.GROUP_USERNAME, groupUser);
                    // 存入内存
                    for (User myUser : contactResult.result.contacts) {
                        myUser.setType(1);
                        userlist.put(myUser.getUserId(), myUser);
                    }
                    // 存入db
                    List<User> users = new ArrayList<User>(userlist.values());
                    UserDBManager.getInstance().saveContactList(users);
                    AccountManager.getInstance().setContactList(userlist);
                    //给服务号发送消息
                   /* EMMessage contentMsg = EMMessage.createSendMessage(EMMessage.Type.TXT);
                    TextMessageBody body = new TextMessageBody("");
                    contentMsg.setMsgId(UUID.randomUUID().toString());
                    contentMsg.addBody(body);
                    contentMsg.setTo(serverName);
                    contentMsg.setFrom(AccountManager.getInstance().getLoginAccount(MainActivity.this).easemobUser);
                    contentMsg.setMsgTime(System.currentTimeMillis());
                    contentMsg.setAttribute(Constant.EXT_TYPE, Constant.ExtType.TIPS);
                    contentMsg.setUnread(false);
                    contentMsg.setAttribute(Constant.MSG_CONTENT,String.format("欢迎回来",paiServerUser.getNick()));
                    EMChatManager.getInstance().saveMessage(contentMsg);*/


                    refreshChatHistoryFragment();


                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
//                ToastUtil.getInstance(IMMainActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }

    private void refreshChatHistoryFragment(){
        TalkFragment talkFragment = (TalkFragment) getSupportFragmentManager().findFragmentByTag("Talk");
        if(talkFragment != null){
                talkFragment.refresh();
        }
    }

    private void updateUnreadAddressLable(){
        TalkFragment talkFragment = (TalkFragment) getSupportFragmentManager().findFragmentByTag("Talk");
        if(talkFragment != null){
            talkFragment.updateUnreadAddressLable();
        }
    }


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
     * 初始化组件
     */
    private void initView(){
        //实例化布局对象
        layoutInflater = LayoutInflater.from(this);

        //实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);

        //得到fragment的个数
        int count = fragmentArray.length;
        for(int i = 0; i < count; i++){
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTagArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);

        }
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                if (s.equals(mTagArray[0])) {
                    if (!com.lv.user.User.getUser().isLogin()){
                        Toast.makeText(MainActivity.this,"正在登陆",Toast.LENGTH_LONG).show();
                    }
                    /**
                     * 注释掉登陆
                     */
//                    if(!EMChat.getInstance().isLoggedIn()){
//                        mTabHost.setCurrentTab(1);
//                        Intent logIntent=new Intent(MainActivity.this,LoginActivity.class);
//                        startActivity(logIntent);
//                        overridePendingTransition(R.anim.push_bottom_in,0);
//                    }

                } else if (s.equals(mTagArray[1])) {
                  /*  RecDestFragment fg = (RecDestFragment)getSupportFragmentManager().findFragmentByTag(s);
                    if (fg != null) {
                        fg.reloadData();
                    }*/
                }
            }
        });
//        if (AccountManager.getInstance().isLogin()) {
//            mTabHost.setCurrentTab(0);
//        } else {
            if (com.lv.user.User.getUser().isLogin()) {
                mTabHost.setCurrentTab(0);
            } else {
            mTabHost.setCurrentTab(1);
        }
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index){
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
        if(index==0){
            unreadMsg = (TextView) view.findViewById(R.id.unread_msg_notify);
        }
        CheckedTextView imageView = (CheckedTextView) view.findViewById(R.id.imageview);
        imageView.setCompoundDrawablesWithIntrinsicBounds(0, mImageViewArray[index], 0, 0);
        //imageView.setText(tabTitle[index]);
        return view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("MainActivity resume");
        HandleImMessage.getInstance().registerMessageListener(this);
        if (!isConflict){
            TalkFragment talkFragment = (TalkFragment) getSupportFragmentManager().findFragmentByTag("Talk");
            if(talkFragment != null){
                talkFragment.loadConversation();
            }
            updateUnreadMsgCount();
           // EMChatManager.getInstance().activityResumed();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isConflict", isConflict);
        super.onSaveInstanceState(outState);
    }

    protected PeachMessageDialog conflictDialog;
    /**
     * 显示帐号在别处登录dialog
     */
    public void showConflictDialog(Context context) {
        AccountManager.getInstance().logout(context,isConflict,new EMCallBack() {
            @Override
            public void onSuccess() {
                int i=mTabHost.getCurrentTab();
                if(i==2){
                    MyFragment my=(MyFragment)getSupportFragmentManager().findFragmentByTag("My");
                    my.onResume();
                }
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
        if(isFinishing())
            return;
        try {
            if (conflictDialog == null){
                conflictDialog= new PeachMessageDialog(context);
                conflictDialog.setTitle("下线通知");
                //conflictDialog.setTitleIcon(R.drawable.ic_dialog_tip);
                conflictDialog.setMessage(getResources().getText(R.string.connect_conflict).toString());
                conflictDialog.setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        conflictDialog.dismiss();
                        conflictDialog = null;
                        mTabHost.setCurrentTab(1);
                        if(isAccountAbout){
                            finish();
                        }
                    }
                });
                conflictDialog.show();
                conflictDialog.isCancle(false);
            }
            conflictDialog.show();
            isConflict=true;
        } catch (Exception e) {
            Log.e("###", "---------color conflictBuilder error" + e.getMessage());
        }


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

                }
                //对方同意了加好友请求(好友添加)
                else if (cmdType == 2) {
                   // updateUnreadMsgCount();
                    refreshChatHistoryFragment();


                }
                //删除好友
                else if (cmdType == 3) {
                    // 刷新ui
                    refreshChatHistoryFragment();
                }

            } catch (EaseMobException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public void onMsgArrive(MessageBean m) {
        System.out.println("message :"+m.getMessage());
        TalkFragment talkFragment = (TalkFragment) getSupportFragmentManager().findFragmentByTag("Talk");
        if(talkFragment != null){
            talkFragment.loadConversation();
        }
        updateUnreadMsgCount();
      //  notifyNewMessage(m);
    }

    @Override
    public void onCMDMessageArrive(MessageBean m) {
        System.out.println("onCMDMessageArrive");
            IMUtils.HandleCMDInfoFromMessage(m);
    }


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
            updateUnreadMsgCount();
            refreshChatHistoryFragment();
           // notifyNewMessage(message);

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
     * 保存提示新消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg) {

        // 刷新bottom bar消息未读数
        updateUnreadAddressLable();
    }


    /**
     * MyGroupChangeListener
     */
    private class MyGroupChangeListener implements GroupChangeListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            refreshChatHistoryFragment();

        }

        @Override
        public void onInvitationAccpted(String groupId, String inviter, String reason) {
            refreshChatHistoryFragment();
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
                        updateUnreadMsgCount();
                        refreshChatHistoryFragment();
                        if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
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
                    updateUnreadMsgCount();
                    refreshChatHistoryFragment();
                    if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
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
            EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            msg.setChatType(EMMessage.ChatType.GroupChat);
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
                    updateUnreadMsgCount();
                    // 刷新ui
                    refreshChatHistoryFragment();
                    if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
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



    public void updateUnreadMsgCount(){
        int unreadMsgCountTotal = 0;
       // unreadMsgCountTotal = IMClient.getInstance().getUnReadCount()+getUnreadAddressCountTotal();
        unreadMsgCountTotal = IMClient.getInstance().getUnReadCount();
        if (unreadMsgCountTotal > 0) {
            unreadMsg.setVisibility(View.VISIBLE);
            unreadMsg.setText(unreadMsgCountTotal+"");
        } else {
            unreadMsg.setVisibility(View.GONE);
        }
    }

    /**
     * 获取未读申请与通知消息
     *
     * @return
     */
   /* public int getUnreadAddressCountTotal() {
        int unreadAddressCountTotal = 0;
       // unreadAddressCountTotal = (int) InviteMsgRepository.getUnAcceptMsgCount(this);
        if (AccountManager.getInstance().getContactList(this).get(Constant.NEW_FRIENDS_USERNAME) != null) {

            IMUser imUser = AccountManager.getInstance().getContactList(this).get(Constant.NEW_FRIENDS_USERNAME);
            imUser.setUnreadMsgCount(unreadAddressCountTotal);
            IMUserRepository.saveContact(this, imUser);
        }
        return unreadAddressCountTotal;
    }
*/


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onDrivingLogout() {
        MyFragment myFragment = (MyFragment) getSupportFragmentManager().findFragmentByTag("My");
        if(myFragment != null){
            myFragment.refresh();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CODE_IM_LOGIN) {
                startActivityWithNoAnim(new Intent(this, IMMainActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("MainActivity pause");
        HandleImMessage.getInstance().unregisterMessageListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("MainActivity stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播接收者
        System.out.println("MainActivity destroy");
       /* try {
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

        }*/

    }

}
