package com.xuejian.client.lxp.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.SharePrefUtil;
import com.aizou.core.widget.FragmentTabHost;
import com.alibaba.fastjson.JSON;
import com.lv.Listener.HttpCallback;
import com.lv.bean.MessageBean;
import com.lv.im.HandleImMessage;
import com.lv.im.IMClient;
import com.lv.utils.Config;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ContactListBean;
import com.xuejian.client.lxp.bean.CountryBean;
import com.xuejian.client.lxp.bean.GroupLocBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.GroupApi;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.PreferenceUtils;
import com.xuejian.client.lxp.common.widget.FlowLayout;
import com.xuejian.client.lxp.common.widget.SuperToast.SuperToast;
import com.xuejian.client.lxp.config.SettingConfig;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.dest.fragment.SearchAllFragment;
import com.xuejian.client.lxp.module.dest.fragment.TalentLocFragement;
import com.xuejian.client.lxp.module.my.LoginActivity;
import com.xuejian.client.lxp.module.my.MyFragment;
import com.xuejian.client.lxp.module.toolbox.TalkFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


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
    private Class fragmentArray[] = {TalkFragment.class, TalentLocFragement.class, SearchAllFragment.class, MyFragment.class};

    // 定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.checker_tab_home, R.drawable.checker_tab_home_destination, R.drawable.checker_tab_home_search, R.drawable.checker_tab_home_user};
    private String[] tabTitle = {"消息", "达人", "搜索", "我的"};
    private int[] colors = new int[]{R.color.white, R.color.black_overlay, R.color.white, R.color.black_overlay};
    private TextView unreadMsg;
    private TextView regNotice;
    //Tab选项Tag
    private String mTagArray[] = {"Talk", "Travel", "Soso", "My"};

    private boolean FromBounce;
    private Vibrator vibrator;
    PopupWindow mPop;
    SuperToast superToast;
    private boolean isPause;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        showNotice(this);
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

        if (AccountManager.getInstance().getLoginAccount(this) != null) {
            if (IMClient.getInstance().isDbEmpty()) {
                imLogin(AccountManager.getInstance().getLoginAccount(this));
            }
            initClient();
            //   DialogManager.getInstance().showLoadingDialog(this);
        }
    }

    private void imLogin(final User user) {

        //初始化数据库，方便后面操作
        IMClient.getInstance().setCurrentUserId(String.valueOf(user.getUserId()));
        UserDBManager.getInstance().initDB(user.getUserId() + "");
        UserDBManager.getInstance().saveContact(user);
        int version = SharePrefUtil.getInt(this, "dbversion", 0);
        IMClient.getInstance().initDB(String.valueOf(user.getUserId()), 1, version);
        SharePrefUtil.saveInt(this, "dbversion", 1);
        //3、存入内存
        AccountManager.getInstance().setLogin(true);
        AccountManager.getInstance().saveLoginAccount(this, user);
        AccountManager.setCurrentUserId(String.valueOf(user.getUserId()));
        // 进入主页面
    }

    public void showNotice(Context context) {
        superToast = new SuperToast(context);
        superToast.setDuration(SuperToast.Duration.MEDIUM);
        superToast.setBackground(SuperToast.Background.GRAY);
        superToast.setTextSize(SuperToast.TextSize.SMALL);
        superToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 2);
        superToast.setAnimations(R.style.msg_in);
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setIcon(R.drawable.icon_notice, SuperToast.IconPosition.LEFT);
    }

    public void initClient() {
//        TalkFragment talkFragment = (TalkFragment) getSupportFragmentManager().findFragmentByTag("Talk");
//        if (talkFragment != null) {
//            talkFragment.loadConversation();
//        }
        Handler handler = new Handler();
        IMClient.login(AccountManager.getCurrentUserId(), new HttpCallback() {
            @Override
            public void onSuccess() {
                IMClient.getInstance().initAckAndFetch();
            }

            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onFailed(int code) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtil.getInstance(MainActivity.this).showToast("push服务登录失败");
//                    }
//                });
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                IMClient.getInstance().getConversationAttrs(AccountManager.getCurrentUserId(), IMClient.getInstance().getConversationIds(), new HttpCallback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject res = new JSONObject(result);
                            JSONArray array = res.getJSONArray("result");
                            for (int i = 0; i < array.length(); i++) {
                                SettingConfig.getInstance().setLxpNoticeSetting(MainActivity.this, String.valueOf(array.getJSONObject(i).getInt("targetId")), array.getJSONObject(i).getBoolean("muted"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(int code) {
                    }
                });
            }
        }, 5000);
        initData();
        UserApi.getUserInfo(AccountManager.getCurrentUserId(), new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                CommonJson<User> Info = CommonJson.fromJson(result.toString(), User.class);
                if (Info.code == 0) {
                    AccountManager.getInstance().setLoginAccountInfo(Info.result);
                    /**
                     *
                     *
                     *
                     */
                   /* MyFragment myFragment = (MyFragment) getSupportFragmentManager().findFragmentByTag("My");
                    if (myFragment != null) {
                        myFragment.refreshLoginStatus();
                    }*/
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("conflict", false)) {
            showConflictDialog(MainActivity.this);
        }
        if (intent.getBooleanExtra("reLogin", false)) {
            initClient();
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
                    ConcurrentHashMap<Long, User> userlist = new ConcurrentHashMap<Long, User>();
                    // 存入内存
                    for (User myUser : contactResult.result.contacts) {
                        myUser.setType(1);
                        userlist.put(myUser.getUserId(), myUser);
                    }
                    // 存入db
                    User wenwen = new User();
                    wenwen.setNickName("旅行问问");
                    wenwen.setUserId(10001l);
                    wenwen.setType(1);
                    String drawableUrl = ImageDownloader.Scheme.DRAWABLE.wrap("R.drawable.lvxingwenwen");
                    wenwen.setAvatarSmall("drawable://R.drawable.lvxingwenwen");
                    UserDBManager.getInstance().saveContact(wenwen);
                    User paipai = new User();
                    paipai.setNickName("派派");
                    paipai.setUserId(10000l);
                    paipai.setType(1);
                    UserDBManager.getInstance().saveContact(paipai);
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

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

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
                    if (AccountManager.getInstance().getLoginAccount(MainActivity.this) == null) {
                        mTabHost.setCurrentTab(1);
                        Intent logIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityWithNoAnim(logIntent);
                        overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
                    }
                } else if (s.equals(mTagArray[3])) {
                    if (AccountManager.getInstance().getLoginAccount(MainActivity.this) == null) {
                        mTabHost.setCurrentTab(1);
                        Intent logIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityWithNoAnim(logIntent);
                        overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
                    }
                }
            }
        });
        if (AccountManager.getInstance().getLoginAccount(MainActivity.this) != null) {
            mTabHost.setCurrentTab(0);
        } else {
            mTabHost.setCurrentTab(1);
        }
    }

    public void setTabForLogout() {
        mTabHost.setCurrentTab(1);
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
        //  view.setBackgroundResource(colors[index]);
        if (index == 0) {
            unreadMsg = (TextView) view.findViewById(R.id.unread_msg_notify);
        }
        if (SharePrefUtil.getBoolean(getApplicationContext(), "firstReg", false) && index == 3) {
            regNotice = (TextView) view.findViewById(R.id.unread_msg_notify);
            regNotice.setTextColor(Color.RED);
            regNotice.setVisibility(View.VISIBLE);
        }

        if (index == 3) {
            view.findViewById(R.id.line_inter).setVisibility(View.GONE);
        }
        // Drawable myImage = (Drawable)getResources().getDrawable(mImageViewArray[index], );
        // myImage.setBounds(1, 1, 100, 100);
        CheckedTextView imageView = (CheckedTextView) view.findViewById(R.id.imageview);
        imageView.setCompoundDrawablesWithIntrinsicBounds(0, mImageViewArray[index], 0, 0);
        imageView.setCompoundDrawablePadding(2);

        imageView.setChecked(true);
        imageView.setText(tabTitle[index]);

        return view;
    }

    public void setNoticeInvisiable() {
        if (regNotice != null) {
            regNotice.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        isPause = false;
        if (AccountManager.getInstance().getLoginAccount(MainActivity.this) != null) {
            HandleImMessage.getInstance().registerMessageListener(this);

//            TalkFragment talkFragment = (TalkFragment) getSupportFragmentManager().findFragmentByTag("Talk");
//            if (talkFragment != null) {
//                talkFragment.loadConversation();
//                talkFragment.updateUnreadAddressLable();
//            }
//            updateUnreadMsgCount();

        } else unreadMsg.setVisibility(View.GONE);
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

        }


    }


    @Override
    public void onMsgArrive(MessageBean m, final String groupId) {
        TalkFragment talkFragment = (TalkFragment) getSupportFragmentManager().findFragmentByTag("Talk");
        if (talkFragment != null) {
            talkFragment.loadConversation();
        }
        updateUnreadMsgCount();
        try {
            if (!TextUtils.isEmpty(groupId) && UserDBManager.getInstance().getContactByUserId(Long.parseLong(groupId)) == null) {
                GroupApi.getGroupInfo(groupId, new HttpCallBack() {
                    @Override
                    public void doSuccess(Object result, String method) {
                        JSONObject object = null;
                        try {
                            if (Config.isDebug) {
                                Log.i(Config.TAG, "group info : " + result);
                            }
                            object = new JSONObject(result.toString());
                            JSONObject o = object.getJSONObject("result");
                            User user = new User();
                            user.setNickName(o.get("name").toString() == null ? " " : o.get("name").toString());
                            o.remove("name");
                            user.setAvatar(o.get("avatar").toString());
                            o.remove("avatar");
                            user.setExt(o.toString());
                            user.setType(8);
                            user.setUserId(Long.parseLong(groupId));
                            UserDBManager.getInstance().saveContact(user);
                            TalkFragment fragment = (TalkFragment) getSupportFragmentManager().findFragmentByTag("Talk");
                            if (fragment != null && !isPause) {
                                fragment.loadConversation();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {

                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method, int code) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 震动
         */
        try {
            if (!TextUtils.isEmpty(m.getAbbrev())) {
                superToast.setText("  " + m.getAbbrev());
            } else {
                superToast.setText("  你有一条新消息");
            }
        } catch (Exception e) {
        }

        if (m.getSenderId() != Long.parseLong(AccountManager.getCurrentUserId())) {
            if (SettingConfig.getInstance().getLxqPushSetting(MainActivity.this) && Integer.parseInt(groupId) != 0 && !SettingConfig.getInstance().getLxpNoticeSetting(MainActivity.this, groupId)) {
                //   vibrator.vibrate(500);
                if (HandleImMessage.showNotice(mContext) && isPause && isLongEnough())
                    superToast.show();
            } else if (SettingConfig.getInstance().getLxqPushSetting(MainActivity.this) && Integer.parseInt(groupId) == 0 && !SettingConfig.getInstance().getLxpNoticeSetting(MainActivity.this, m.getSenderId() + "")) {
                //    vibrator.vibrate(500);
                if (HandleImMessage.showNotice(mContext) && isPause && isLongEnough())
                    superToast.show();
            }
        }

        //  notifyNewMessage(m);
    }

    @Override
    public void onCMDMessageArrive(MessageBean m, String groupId) {
        try {
            if (m.getType() == 100) {
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
            } else if (m.getType() == 200) {
                List<Long> targetIds = new ArrayList<>();
                String cmd = m.getMessage();
                boolean beenKicked = false;
                JSONObject tips = new JSONObject(cmd);
                String GroupId = tips.getString("chatGroupId");
                int tipsType = tips.getInt("tipType");
                JSONObject operator = tips.getJSONObject("operator");
                JSONArray targets = tips.getJSONArray("targets");
                StringBuilder tag = new StringBuilder();
                for (int i = 0; i < targets.length(); i++) {
                    if (i > 0) tag.append("、");
                    if (targets.getJSONObject(i).getInt("userId") == Integer.parseInt(IMClient.getInstance().getCurrentUserId())) {
                        tag.append("你");
                        beenKicked = true;
                    } else {
                        tag.append(targets.getJSONObject(i).getString("nickName"));
                        targetIds.add(targets.getJSONObject(i).getLong("userId"));
                    }
                }
                if (tipsType == 2001) {
                    setUpGroupMemeber(GroupId);
                    //   addTips(groupId, operator.getString("nickName") + "邀请" + tag.toString() + "加入讨论组", "group");
                } else if (tipsType == 2002) {
                    if (beenKicked) {
                        ToastUtil.getInstance(getApplicationContext()).showToast("你已被" + operator.getString("nickName") + "移出讨论组");
                        return;
                    }
                    if (TextUtils.isEmpty(tag.toString())) {
                        setUpGroupMemeber(GroupId);
                    } else {
                        setUpGroupMemeber(GroupId);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        TalkFragment fragment = (TalkFragment) getSupportFragmentManager().findFragmentByTag("Talk");
        if (fragment != null) {
            fragment.refresh();
        }
    }

    public void setUpGroupMemeber(final String groupId) {
        //fetch info
        GroupApi.getGroupMemberInfo(groupId, new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                JSONObject object = null;
                JSONArray userList = null;
                List<User> list = new ArrayList<User>();
                try {
                    object = new JSONObject(result.toString());
                    userList = object.getJSONArray("result");
                    for (int i = 0; i < userList.length(); i++) {
                        String str = userList.get(i).toString();
                        User user = JSON.parseObject(str, User.class);
                        list.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                UserDBManager.getInstance().updateGroupMemberInfo(list, groupId);
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
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
                //IMClient.initIM(getApplicationContext());
                IMClient.getInstance().initAckAndFetch();
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


    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
        MobclickAgent.onPause(this);
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
        HandleImMessage.getInstance().unregisterMessageListener(this);
    }

    private void getInLocList() {
        //这个地方也需要判断一下做出接口读取的选择
        String lastModify = PreferenceUtils.getCacheData(MainActivity.this, "indest_group_last_modify");
        TravelApi.getInDestListByGroup(lastModify, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {

            }

            @Override
            public void doSuccess(String result, String method, Map<String, List<String>> headers) {
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

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

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
            public void doSuccess(String result, String method, Map<String, List<String>> headers) {
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

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    public void showAD() {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//自定义布局
        ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
                R.layout.text_diaplay, null, true);
        TextView pop_dismiss = (TextView) menuView.findViewById(R.id.pop_dismiss);

        TextView tv = (TextView) menuView.findViewById(R.id.msg);
        tv.setText("AD");
        mPop = new PopupWindow(menuView, FlowLayout.LayoutParams.MATCH_PARENT,
                FlowLayout.LayoutParams.MATCH_PARENT, true);
        mPop.setContentView(menuView);//设置包含视图
        mPop.setWidth(FlowLayout.LayoutParams.MATCH_PARENT);
        mPop.setHeight(FlowLayout.LayoutParams.MATCH_PARENT);
        mPop.setAnimationStyle(R.style.PopAnimation);
        mPop.showAtLocation(findViewById(R.id.realtabcontent), Gravity.BOTTOM, 0, 0);
        pop_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPop.dismiss();
            }
        });

    }

    private static long mSendTime;

    public static boolean isLongEnough() {
        long currentTime = System.currentTimeMillis();
        long time = currentTime - mSendTime;
        mSendTime = currentTime;
        return !(0 < time && time < 1000);
    }
}
