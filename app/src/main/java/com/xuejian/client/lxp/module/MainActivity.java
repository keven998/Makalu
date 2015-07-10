package com.xuejian.client.lxp.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TabHost;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.core.widget.FragmentTabHost;
import com.lv.bean.MessageBean;
import com.lv.im.HandleImMessage;
import com.lv.im.IMClient;
import com.lv.user.LoginSuccessListener;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ContactListBean;
import com.xuejian.client.lxp.bean.CountryBean;
import com.xuejian.client.lxp.bean.GroupLocBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.PreferenceUtils;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.dest.TripFragment;
import com.xuejian.client.lxp.module.my.LoginActivity;
import com.xuejian.client.lxp.module.my.MyFragment;
import com.xuejian.client.lxp.module.toolbox.TalkFragment;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends PeachBaseActivity implements HandleImMessage.MessageHandler {
    //    public final static int CODE_IM_LOGIN = 101;
//    public static final int NEW_CHAT_REQUEST_CODE = 102;
    // 账号在别处登录
    public boolean isConflict = false;
    //定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    //定义一个布局
    private LayoutInflater layoutInflater;

    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {TalkFragment.class, TripFragment.class, MyFragment.class};

    // 定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.checker_tab_home, R.drawable.checker_tab_home_destination, R.drawable.checker_tab_home_user};
//    private String[] tabTitle = {"Talk", "旅游", "我"};

    private TextView unreadMsg;

    //Tab选项Tag
    private String mTagArray[] = {"Talk", "Travel", "My"};
    private boolean FromBounce;
    private Vibrator vibrator;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        FromBounce = getIntent().getBooleanExtra("FromBounce", false);
        setContentView(R.layout.activity_main);
        initView();
        if (getIntent().getBooleanExtra("conflict", false)) {
            showConflictDialog(MainActivity.this);
        }
        //断网提示
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionReceiver, intentFilter);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (!FromBounce) {
            com.lv.user.User.login(AccountManager.getCurrentUserId(), new LoginSuccessListener() {
                @Override
                public void OnSuccess() {
                    IMClient.getInstance().initAckAndFetch();
                }

                @Override
                public void OnFailed(int code) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.getInstance(MainActivity.this).showToast("登录失败");
                        }
                    });
                }
            });

            TalkFragment talkFragment = (TalkFragment) getSupportFragmentManager().findFragmentByTag("Talk");
            if (talkFragment != null) {
                talkFragment.loadConversation();
            }
            initData();
            UserApi.getUserInfo(AccountManager.getCurrentUserId(), new HttpCallBack() {
                @Override
                public void doSuccess(Object result, String method) {
                    CommonJson<User> Info = CommonJson.fromJson(result.toString(), User.class);
                    if (Info.code == 0) {
                        AccountManager.getInstance().setLoginAccountInfo(Info.result);
                        MyFragment myFragment = (MyFragment) getSupportFragmentManager().findFragmentByTag("My");
                        if (myFragment != null) {
                            myFragment.refreshLoginStatus();
                        }
                    }
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {

                }
            });
            getInLocList();
            getOutCountryList();
//            GroupApi.editGroupName("123123", new HttpCallBack() {
//                @Override
//                public void doSuccess(Object result, String method) {
//
//                }
//
//                @Override
//                public void doFailure(Exception error, String msg, String method) {
//
//                }
//            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("conflict", false)) {
            showConflictDialog(MainActivity.this);
        }
    }

    private void initData() {
        //网络更新好友列表
        getContactFromServer();
    }

    private void getContactFromServer() {
        UserApi.getContact(new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson<ContactListBean> contactResult = CommonJson.fromJson(result, ContactListBean.class);
                if (contactResult.code == 0) {
                    AccountManager.getInstance().setContactList(null);
                    Map<Long, User> userlist = new HashMap<Long, User>();
                    // 存入内存
                    for (User myUser : contactResult.result.contacts) {
                        myUser.setType(1);
                        userlist.put(myUser.getUserId(), myUser);
                    }
                    // 存入db
                    List<User> users = new ArrayList<User>(userlist.values());
                    UserDBManager.getInstance().saveContactList(users);
                    AccountManager.getInstance().setContactList(userlist);
                    refreshChatHistoryFragment();
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                ToastUtil.getInstance(MainActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }

    private void refreshChatHistoryFragment() {
        TalkFragment talkFragment = (TalkFragment) getSupportFragmentManager().findFragmentByTag("Talk");
        if (talkFragment != null) {
            talkFragment.refresh();
        }
    }

    private void updateUnreadAddressLable() {
        TalkFragment talkFragment = (TalkFragment) getSupportFragmentManager().findFragmentByTag("Talk");
        if (talkFragment != null) {
            talkFragment.updateUnreadAddressLable();
        }
    }

    /**
     * 初始化组件
     */
    private void initView() {
        //实例化布局对象
        layoutInflater = LayoutInflater.from(this);

        //实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);

        //得到fragment的个数
        int count = fragmentArray.length;
        for (int i = 0; i < count; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTagArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);

        }
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                if (s.equals(mTagArray[0])) {
                    if (!AccountManager.getInstance().isLogin()) {
                        mTabHost.setCurrentTab(1);
                        Intent logIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityWithNoAnim(logIntent);
                        overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
                    }
                } else if (s.equals(mTagArray[1])) {
                }
            }
        });
        if (AccountManager.getInstance().isLogin()) {
            mTabHost.setCurrentTab(0);
        } else {
            mTabHost.setCurrentTab(1);
        }
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
        if (index == 0) {
            unreadMsg = (TextView) view.findViewById(R.id.unread_msg_notify);
        }
        CheckedTextView imageView = (CheckedTextView) view.findViewById(R.id.imageview);
        imageView.setCompoundDrawablesWithIntrinsicBounds(mImageViewArray[index], 0, 0, 0);
        //imageView.setText(tabTitle[index]);
        return view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AccountManager.getInstance().isLogin()) {
            HandleImMessage.getInstance().registerMessageListener(this);
            //  if (!isConflict){
            TalkFragment talkFragment = (TalkFragment) getSupportFragmentManager().findFragmentByTag("Talk");
            if (talkFragment != null) {
                talkFragment.loadConversation();
                talkFragment.updateUnreadAddressLable();
            }
            updateUnreadMsgCount();
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
        AccountManager.getInstance().logout(context);
        int i = mTabHost.getCurrentTab();
        if (i == 2) {
            MyFragment my = (MyFragment) getSupportFragmentManager().findFragmentByTag("My");
            my.onResume();
        }
        if (isFinishing())
            return;
        try {
            if (conflictDialog == null) {
                conflictDialog = new PeachMessageDialog(context);
                conflictDialog.setTitle("下线通知");
                //conflictDialog.setTitleIcon(R.drawable.ic_dialog_tip);
                conflictDialog.setMessage(getResources().getText(R.string.connect_conflict).toString());
                conflictDialog.setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        conflictDialog.dismiss();
                        conflictDialog = null;
                        mTabHost.setCurrentTab(1);
                        if (isAccountAbout) {
                            finish();
                        }
                    }
                });
                conflictDialog.show();
                conflictDialog.isCancle(false);
            }
            conflictDialog.show();
            isConflict = true;
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
//            //获取cmd message对象
//            String msgId = intent.getStringExtra("msgid");
//            EMMessage message = intent.getParcelableExtra("message");
//            //获取消息body
//            CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
//            String aciton = cmdMsgBody.action;//获取自定义action
//            //获取扩展属性
//            try {
//                int cmdType = message.getIntAttribute("CMDType");
//                String content = message.getStringAttribute("content");
//                //接受到好友请求
//                if (cmdType == 1) {
//                    // 刷新bottom bar消息未读数
//                    updateUnreadAddressLable();
//
//                }
//                //对方同意了加好友请求(好友添加)
//                else if (cmdType == 2) {
//                    // updateUnreadMsgCount();
//                    refreshChatHistoryFragment();
//
//
//                }
//                //删除好友
//                else if (cmdType == 3) {
//                    // 刷新ui
//                    refreshChatHistoryFragment();
//                }
//
//            } catch (EaseMobException e) {
//                e.printStackTrace();
//            }
        }
    };


    @Override
    public void onMsgArrive(MessageBean m, String groupId) {
        TalkFragment talkFragment = (TalkFragment) getSupportFragmentManager().findFragmentByTag("Talk");
        if (talkFragment != null) {
            talkFragment.loadConversation();
        }
        updateUnreadMsgCount();
        vibrator.vibrate(500);
        //  notifyNewMessage(m);
    }

    @Override
    public void onCMDMessageArrive(MessageBean m) {
        try {
            if (m.getType()==100) {
                String cmd = m.getMessage();
                JSONObject object = new JSONObject(cmd);
                String action = object.getString("action");
                switch (action) {
                    case "F_AGREE":
                        long userId = object.getLong("userId");
                        String avatar = object.getString("avatar");
                        String nickName = object.getString("nickName");
                        User user = new User();
                        user.setUserId(userId);
                        user.setNickName(nickName);
                        user.setAvatar(avatar);
                        user.setType(1);
                        UserDBManager.getInstance().saveContact(user);
                        AccountManager.getInstance().getContactList(MainActivity.this).put(user.getUserId(), user);
                        break;
                    case "F_ADD":
                        updateUnreadMsgCount();
                        updateUnreadAddressLable();
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // IMUtils.HandleCMDInfoFromMessage(m);
    }

    /**
     * 保存提示新消息
     */
    private void notifyNewInviteMessage() {

        // 刷新bottom bar消息未读数
        updateUnreadAddressLable();
    }

    /**
     * 网络状态广播
     */
    private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                TalkFragment talkFragment = (TalkFragment) getSupportFragmentManager().findFragmentByTag("Talk");
                if (talkFragment != null) {
                    talkFragment.netStateChange("(未连接)");
                }
            } else {
                TalkFragment talkFragment = (TalkFragment) getSupportFragmentManager().findFragmentByTag("Talk");
                if (talkFragment != null) {
                    talkFragment.netStateChange("");
                }
            }
        }
    };


    public void updateUnreadMsgCount() {
        int unreadMsgCountTotal = 0;
        // unreadMsgCountTotal = IMClient.getInstance().getUnReadCount()+
        unreadMsgCountTotal = IMClient.getInstance().getUnReadCount() + IMClient.getInstance().getUnAcceptMsg();
        if (unreadMsgCountTotal > 0) {
            unreadMsg.setVisibility(View.VISIBLE);
            if (unreadMsgCountTotal < 100) {
                unreadMsg.setText(String.valueOf(unreadMsgCountTotal));
            } else {
                unreadMsg.setText("...");
            }
        } else {
            unreadMsg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onDrivingLogout() {
        MyFragment myFragment = (MyFragment) getSupportFragmentManager().findFragmentByTag("My");
        if (myFragment != null) {
            myFragment.refreshLoginStatus();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        HandleImMessage.getInstance().unregisterMessageListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectionReceiver != null) {
            unregisterReceiver(connectionReceiver);
            connectionReceiver = null;
        }
    }

    private void getInLocList() {
        //这个地方也需要判断一下做出接口读取的选择
        String lastModify = PreferenceUtils.getCacheData(MainActivity.this, "indest_group_last_modify");
        TravelApi.getInDestListByGroup(lastModify, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {

            }

            @Override
            public void doSuccess(String result, String method, Header[] headers) {
                CommonJson4List<GroupLocBean> locListResult = CommonJson4List.fromJson(result, GroupLocBean.class);
                if (locListResult.code == 0) {
                    PreferenceUtils.cacheData(MainActivity.this, "destination_indest_group", result);
                    PreferenceUtils.cacheData(MainActivity.this, "indest_group_last_modify", CommonUtils.getLastModifyForHeader(headers));
                    LogUtil.d("last_modify", CommonUtils.getLastModifyForHeader(headers));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
//                if (isAdded()) {
//                    ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
//                }
            }
        });
    }

    private void getOutCountryList() {
        String lastModify = PreferenceUtils.getCacheData(MainActivity.this, "outcountry_last_modify");
        TravelApi.getOutDestList(lastModify, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
            }

            @Override
            public void doSuccess(String result, String method, Header[] headers) {
                CommonJson4List<CountryBean> countryListResult = CommonJson4List.fromJson(result, CountryBean.class);
                if (countryListResult.code == 0) {
                    PreferenceUtils.cacheData(MainActivity.this, "destination_outcountry", result);
                    PreferenceUtils.cacheData(MainActivity.this, "outcountry_last_modify", CommonUtils.getLastModifyForHeader(headers));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
//                if (isAdded())
//                ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }
}
