package com.xuejian.client.lxp.module;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TabHost;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.SharePrefUtil;
import com.aizou.core.widget.FragmentTabHost;
import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.lv.Listener.HttpCallback;
import com.lv.bean.MessageBean;
import com.lv.im.HandleImMessage;
import com.lv.im.IMClient;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ContactListBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.GroupApi;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.LocationUtils;
import com.xuejian.client.lxp.common.widget.SuperToast.SuperToast;
import com.xuejian.client.lxp.config.SettingConfig;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.goods.Fragment.DestinationFragment;
import com.xuejian.client.lxp.module.goods.Fragment.GoodsMainFragment;
import com.xuejian.client.lxp.module.my.LoginActivity;
import com.xuejian.client.lxp.module.my.fragment.MyInfoFragment;
import com.xuejian.client.lxp.module.toolbox.TalkFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class MainActivity extends PeachBaseActivity implements HandleImMessage.MessageHandler, AMapLocationListener {
    //    public final static int CODE_IM_LOGIN = 101;
//    public static final int NEW_CHAT_REQUEST_CODE = 102;
    // 账号在别处登录
    public boolean isConflict = false;
    //定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    //定义一个布局
    private LayoutInflater layoutInflater;
    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {GoodsMainFragment.class, DestinationFragment.class, TalkFragment.class, MyInfoFragment.class};
    //TalentLocFragement
    // 定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.checker_tab_home_search, R.drawable.checker_tab_home_destination, R.drawable.checker_tab_home, R.drawable.checker_tab_home_user};
    // private int[] colors = new int[]{R.color.white, R.color.black_overlay, R.color.white, R.color.black_overlay};
    private static String[] tabTitle = {"首页", "目的地", "消息", "我的"};
    private TextView unreadMsg;
    //Tab选项Tag
    private static String mTagArray[] = {"Soso", "Travel", "Talk", "My"};

    private boolean FromBounce, ring, vib;
    private Vibrator vibrator;
    SuperToast superToast;
    private boolean isPause;
    LocationManagerProxy mLocationManagerProxy;
    private SparseBooleanArray infoStatus = new SparseBooleanArray();
    private MediaPlayer mMediaPlayer;
    public CompositeSubscription compositeSubscription = new CompositeSubscription();

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
        prepareJSBundle();
        //断网提示
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        registerReceiver(connectionReceiver, intentFilter);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        getAlarmParams();
        if (AccountManager.getInstance().getLoginAccount(this) != null) {
            if (IMClient.getInstance().isDbEmpty()) {
                imLogin(AccountManager.getInstance().getLoginAccount(this));
            }
            initClient();
        }
        initLocation();

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int heapSize = manager.getLargeMemoryClass();
        System.out.println("heapSize " + heapSize);
        int heapgrowthlimit = manager.getMemoryClass();
        System.out.println("heapgrowthlimit " + heapgrowthlimit);
    }

    private void initLocation() {
        mLocationManagerProxy = LocationManagerProxy.getInstance(this);
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, -1, 100, this);
        mLocationManagerProxy.setGpsEnable(false);
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
                    MyInfoFragment myFragment = (MyInfoFragment) getSupportFragmentManager().findFragmentByTag("My");
                    if (myFragment != null && Info.result != null) {
                        myFragment.initHeadTitleView(Info.result);
                    }

                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                IMClient.getInstance().getConversationList();
//            }
//        }).start();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("reLogin", false)) {
            initClient();
        }
        if (intent.getBooleanExtra("back", false)) {
            mTabHost.setCurrentTab(0);
        }
    }

    private void initData() {
        //网络更新好友列表
        getContactFromServer();

//        ArrayMap
//        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        JobInfo jobInfo = new JobInfo.Builder()
//                .setMinimumLatency()
//                .build();
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

                    User trade = new User();
                    trade.setNickName("交易消息");
                    trade.setUserId(10002l);
                    trade.setType(1);
                    UserDBManager.getInstance().saveContact(trade);

                    User activity = new User();
                    activity.setNickName("活动消息");
                    activity.setUserId(10003l);
                    activity.setType(1);
                    UserDBManager.getInstance().saveContact(activity);

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
        layoutInflater = getLayoutInflater();

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
                if (s.equals(mTagArray[2])) {
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
            mTabHost.setCurrentTab(0);
        }
    }

    public void setTabForLogout() {
        mTabHost.setCurrentTab(0);
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
        //  view.setBackgroundResource(colors[index]);
        if (index == 2) {
            unreadMsg = (TextView) view.findViewById(R.id.unread_msg_notify);
        }
//        if (SharePrefUtil.getBoolean(getApplicationContext(), "firstReg", false) && index == 3) {
//            regNotice = (TextView) view.findViewById(R.id.unread_msg_notify);
//            regNotice.setTextColor(Color.RED);
//            regNotice.setVisibility(View.VISIBLE);
//        }
        // Drawable myImage = (Drawable)getResources().getDrawable(mImageViewArray[index], );
        // myImage.setBounds(1, 1, 100, 100);
        CheckedTextView imageView = (CheckedTextView) view.findViewById(R.id.imageview);
        imageView.setCompoundDrawablesWithIntrinsicBounds(0, mImageViewArray[index], 0, 0);
        imageView.setCompoundDrawablePadding(2);

        imageView.setChecked(true);
        imageView.setText(tabTitle[index]);

        return view;
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
            updateUnreadMsgCount();
        } else unreadMsg.setVisibility(View.GONE);
        try {
            if (!IMClient.isPushTurnOn(mContext)) {
                IMClient.initPushService(mContext);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isConflict", isConflict);
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onMsgArrive(MessageBean m, final String groupId) {
        TalkFragment talkFragment = (TalkFragment) getSupportFragmentManager().findFragmentByTag("Talk");
        if (talkFragment != null) {
            talkFragment.loadConversation();
        }
        updateUnreadMsgCount();
        try {
            if (Integer.parseInt(groupId) != 0 && !infoStatus.get(Integer.parseInt(groupId), false) && UserDBManager.getInstance().getContactByUserId(Long.parseLong(groupId)) == null) {
                infoStatus.put(Integer.parseInt(groupId), true);
                GroupApi.getGroupInfo(groupId, new HttpCallBack() {
                    @Override
                    public void doSuccess(Object result, String method) {
                        JSONObject object = null;
                        try {
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
            } else if (Integer.parseInt(groupId) == 0 && !infoStatus.get((int) m.getSenderId(), false) && UserDBManager.getInstance().getContactByUserId(m.getSenderId()) == null) {
                infoStatus.put((int) m.getSenderId(), true);
                UserApi.getUserInfo(String.valueOf(m.getSenderId()), new HttpCallBack<String>() {
                    @Override
                    public void doSuccess(String result, String method) {
                        CommonJson<User> userInfo = CommonJson.fromJson(result, User.class);
                        if (userInfo.code == 0) {
                            UserDBManager.getInstance().saveContact(userInfo.result);
                            TalkFragment fragment = (TalkFragment) getSupportFragmentManager().findFragmentByTag("Talk");
                            if (fragment != null && !isPause) {
                                fragment.loadConversation();
                            }
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
        if (mMediaPlayer == null) {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(mContext, uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.setLooping(false); //循环播放
        }


        if (m.getSenderId() != Long.parseLong(AccountManager.getCurrentUserId())) {
            if (SettingConfig.getInstance().getLxqPushSetting(MainActivity.this) && Integer.parseInt(groupId) != 0 && !SettingConfig.getInstance().getLxpNoticeSetting(MainActivity.this, groupId)) {
                //   vibrator.vibrate(500);
                if (isLongEnough()) {
                    try {
                        if (ring) mMediaPlayer.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (HandleImMessage.showNotice(mContext) && isPause)
                        superToast.show();
                }
            } else if (SettingConfig.getInstance().getLxqPushSetting(MainActivity.this) && Integer.parseInt(groupId) == 0 && !SettingConfig.getInstance().getLxpNoticeSetting(MainActivity.this, m.getSenderId() + "")) {
                //    vibrator.vibrate(500);
                if (isLongEnough()) {
                    try {
                        if (ring) mMediaPlayer.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (HandleImMessage.showNotice(mContext) && isPause)
                        superToast.show();
                }
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

    public void getAlarmParams() {
        AudioManager volMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        switch (volMgr.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
            case AudioManager.RINGER_MODE_VIBRATE:
                vib = true;
                ring = false;
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                ring = true;
                vib = false;
                break;
            default:
                break;
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
     * 网络状态广播
     */
    private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String acton = "";
            if (intent != null) {
                acton = intent.getAction();
            }
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(acton)) {
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
            } else if (AudioManager.RINGER_MODE_CHANGED_ACTION.equals(acton)) {
                getAlarmParams();
            }
        }
    };


    public void updateUnreadMsgCount() {
        Observable<Integer> UnReadCount = Observable.just(IMClient.getInstance().getUnReadCount()).subscribeOn(Schedulers.io());
        Observable<Integer> UnAccept = Observable.just(IMClient.getInstance().getUnAcceptMsg()).subscribeOn(Schedulers.io());
        compositeSubscription.add(
                Observable.zip(UnReadCount, UnAccept, new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer, Integer integer2) {
                        return integer + integer2;
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                if (integer > 0) {
                                    unreadMsg.setVisibility(View.VISIBLE);
                                    if (integer < 100) {
                                        unreadMsg.setText(String.valueOf(integer));
                                    } else {
                                        unreadMsg.setText("...");
                                    }
                                } else {
                                    unreadMsg.setVisibility(View.GONE);
                                }
                            }
                        })
        );


//        int unreadMsgCountTotal = IMClient.getInstance().getUnReadCount() + IMClient.getInstance().getUnAcceptMsg();
//        if (unreadMsgCountTotal > 0) {
//            unreadMsg.setVisibility(View.VISIBLE);
//            if (unreadMsgCountTotal < 100) {
//                unreadMsg.setText(String.valueOf(unreadMsgCountTotal));
//            } else {
//                unreadMsg.setText("...");
//            }
//        } else {
//            unreadMsg.setVisibility(View.GONE);
//        }
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
        if (this.compositeSubscription.hasSubscriptions()) this.compositeSubscription.clear();
        if (connectionReceiver != null) {
            unregisterReceiver(connectionReceiver);
            connectionReceiver = null;
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
        HandleImMessage.getInstance().unregisterMessageListener(this);
    }

    private static long mSendTime;

    public static boolean isLongEnough() {
        long currentTime = System.currentTimeMillis();
        long time = currentTime - mSendTime;
        mSendTime = currentTime;
        return !(0 < time && time < 1000);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
            //获取位置信息
            Double geoLat = aMapLocation.getLatitude();
            Double geoLng = aMapLocation.getLongitude();
            System.out.println("geoLat " + geoLat + " geoLat " + geoLng);
            LocationUtils utils = new LocationUtils();
            boolean isAbroad = utils.pointInPolygon(new LocationUtils.Point(geoLat, geoLng));
            SharePrefUtil.saveBoolean(mContext, "isAbroad", isAbroad);
            mLocationManagerProxy.removeUpdates(this);
            mLocationManagerProxy.destroy();
        }
    }

    private static void copyFile(InputStream paramInputStream, OutputStream paramOutputStream)
            throws IOException {
        byte[] arrayOfByte = new byte[1024];
        for (; ; ) {
            int i = paramInputStream.read(arrayOfByte);
            if (i == -1) {
                break;
            }
            paramOutputStream.write(arrayOfByte, 0, i);
        }
    }

    private void prepareJSBundle() {
        Object localObject = new File(getFilesDir(), "ReactNativeDevBundle.js");
        if (((File) localObject).exists()) {
            return;
        }
        InputStream localInputStream = null;
        try {
            localObject = new FileOutputStream((File) localObject);
            localInputStream = getAssets().open("ReactNativeDevBundle.js");
            copyFile(localInputStream, (OutputStream) localObject);
            ((OutputStream) localObject).close();
            if (localInputStream != null) localInputStream.close();
            return;
        } catch (FileNotFoundException localFileNotFoundException) {
            localFileNotFoundException.printStackTrace();
            return;
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }
}
