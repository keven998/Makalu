package com.aizou.peachtravel.base;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.config.hxconfig.PeachHXSDKHelper;
import com.easemob.chat.EMChatManager;

/**
 * Created by Rjm on 2014/10/21.
 */
public class ChatBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //onresume时，取消notification显示
        EMChatManager.getInstance().activityResumed();
    }





}
