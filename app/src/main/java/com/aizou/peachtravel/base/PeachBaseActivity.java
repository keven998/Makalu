package com.aizou.peachtravel.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.config.hxconfig.PeachHXSDKHelper;

/**
 * Created by Rjm on 2014/10/9.
 */
public class PeachBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void finish() {
        super.finish();
    }
}
