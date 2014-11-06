package com.aizou.peachtravel.config;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.BuildConfig;
import android.util.Log;

import com.aizou.core.base.BaseApplication;
import com.aizou.core.http.HttpCallBack;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.BaseApi;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.PreferenceUtils;
import com.aizou.peachtravel.db.DaoMaster;
import com.aizou.peachtravel.db.DaoSession;
import com.aizou.peachtravel.module.MainActivity;
import com.aizou.peachtravel.module.travel.im.ChatActivity;
import com.aizou.peachtravel.module.travel.im.VoiceCallReceiver;
import com.easemob.chat.ConnectionListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.OnNotificationClickListener;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Rjm on 2014/10/9.
 */
public class PeachApplication extends BaseApplication {
    private static PeachApplication instance;
    public DaoSession daoSession;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initPeachConfig();
        initImageLoader();
//        refreshUserInfo();
        BaseApi.testHttps();
        setupDatabase();
        initIM();

    }
    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "peachtravel-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
    public static PeachApplication getInstance() {
        return instance;
    }

    private void initIM(){
        int pid = android.os.Process.myPid();

        // 初始化环信SDK,一定要先调用init()
        EMChat.getInstance().init(this);
        EMChat.getInstance().setDebugMode(true);
        Log.d("EMChat Demo", "initialize EMChat SDK");
        // debugmode设为true后，就能看到sdk打印的log了

        // 获取到EMChatOptions对象
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // 默认环信是不维护好友关系列表的，如果app依赖环信的好友关系，把这个属性设置为true
        options.setUseRoster(false);
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 设置收到消息是否有新消息通知，默认为true
        options.setNotifyBySoundAndVibrate(PreferenceUtils.getInstance(this).getSettingMsgNotification());
        // 设置收到消息是否有声音提示，默认为true
        options.setNoticeBySound(PreferenceUtils.getInstance(this).getSettingMsgSound());
        // 设置收到消息是否震动 默认为true
        options.setNoticedByVibrate(PreferenceUtils.getInstance(this).getSettingMsgVibrate());
        // 设置语音消息播放是否设置为扬声器播放 默认为true
        options.setUseSpeaker(PreferenceUtils.getInstance(this).getSettingMsgSpeaker());
        // 设置notification消息点击时，跳转的intent为自定义的intent
        options.setOnNotificationClickListener(new OnNotificationClickListener() {

            @Override
            public Intent onNotificationClick(EMMessage message) {
                Intent intent = new Intent(context, ChatActivity.class);
                EMMessage.ChatType chatType = message.getChatType();
                if (chatType == EMMessage.ChatType.Chat) { // 单聊信息
                    intent.putExtra("userId", message.getFrom());
                    intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
                } else { // 群聊信息
                    // message.getTo()为群聊id
                    intent.putExtra("groupId", message.getTo());
                    intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                }
                return intent;
            }
        });
        // 设置一个connectionlistener监听账户重复登陆
        EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
//		// 取消注释，app在后台，有新消息来时，状态栏的消息提示换成自己写的
//		options.setNotifyText(new OnMessageNotifyListener() {
//
//			@Override
//			public String onNewMessageNotify(EMMessage message) {
//				// 可以根据message的类型提示不同文字(可参考微信或qq)，demo简单的覆盖了原来的提示
//				return "你的好基友" + message.getFrom() + "发来了一条消息哦";
//			}
//
//			@Override
//			public String onLatestMessageNotify(EMMessage message, int fromUsersNum, int messageNum) {
//				return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
//			}
//
//			@Override
//			public String onSetNotificationTitle(EMMessage message) {
//				//修改标题
//				return "环信notification";
//			}
//
//
//		});

        //注册一个语言电话的广播接收者
        IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance().getIncomingVoiceCallBroadcastAction());
        registerReceiver(new VoiceCallReceiver(), callFilter);
    }




    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    class MyConnectionListener implements ConnectionListener {
        @Override
        public void onReConnecting() {
        }

        @Override
        public void onReConnected() {
        }

        @Override
        public void onDisConnected(String errorString) {
            if (errorString != null && errorString.contains("conflict")) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("conflict", true);
                startActivity(intent);
            }

        }

        @Override
        public void onConnecting(String progress) {

        }

        @Override
        public void onConnected() {
        }
    }

    private void initImageLoader() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(this,SystemConfig.NET_IMAGE_CACHE_DIR);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2).threadPoolSize(3)
                .memoryCacheSizePercentage(20)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .diskCacheSize(40 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .denyCacheImageMultipleSizesInMemory()
                        .writeDebugLogs() // Remove for release app
                .build();


        ImageLoader.getInstance().init(config);
    }

    private void initPeachConfig(){
        if(BuildConfig.DEBUG){
            SystemConfig.BASE_URL = SystemConfig.DEBUG_BASE_URL;
        }else{
            SystemConfig.BASE_URL = SystemConfig.RELEASE_BASE_URL;
        }

    }

    private void refreshUserInfo(){
        final PeachUser user = AccountManager.getInstance().getLoginAccount(this);
        if(user!=null){
            UserApi.getUserInfo(user,new HttpCallBack<String>() {
                @Override
                public void doSucess(String result, String method) {
                    CommonJson<PeachUser> userResult = CommonJson.fromJson(result,PeachUser.class);
                    if(userResult.code==0){
                        AccountManager.getInstance().saveLoginAccount(context,userResult.result);
                    }

                }

                @Override
                public void doFailure(Exception error, String msg, String method) {

                }
            });
        }
    }

}
