package com.aizou.peachtravel.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.config.hxconfig.PeachHXSDKHelper;

/**
 * Created by Rjm on 2014/12/4.
 */
public class BaseActivity extends FragmentActivity {
    protected Context mContext;
    protected LogoutRecevier mLogoutRecevier;
    protected boolean isFroground;
    protected boolean isAccountAbout;
    protected boolean isConflict;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null &&savedInstanceState.getBoolean("isConflict")&&isAccountAbout) {
            // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            finish();
            return;
        }
        super.onCreate(savedInstanceState);
        mLogoutRecevier =new LogoutRecevier();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AccountManager.ACCOUNT_LOGOUT_ACTION);
        registerReceiver(mLogoutRecevier,filter);
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFroground=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFroground=false;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mLogoutRecevier);
        super.onDestroy();
    }

    public void setAccountAbout(boolean isAccountAbout){
        this.isAccountAbout = isAccountAbout;
    }

    protected MaterialDialog.Builder conflictBuilder;
    protected MaterialDialog conflictDialog;
    /**
     * 显示帐号在别处登录dialog
     */
    protected void showConflictDialog() {
        if(isFinishing())
            return;
        try {
            if (conflictDialog == null){
                conflictBuilder = new MaterialDialog.Builder(this);
                conflictBuilder.title("下线通知");
                conflictBuilder.content(R.string.connect_conflict);
                conflictBuilder.positiveText(R.string.ok);
                conflictBuilder.callback(new MaterialDialog.Callback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {


                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                        conflictBuilder = null;
                        if(isAccountAbout){
                            finish();
                        }
                    }
                });
                conflictBuilder.cancelable(false);
                conflictDialog= conflictBuilder.build();
            }
            conflictDialog.show();
            isConflict=true;
        } catch (Exception e) {
            Log.e("###", "---------color conflictBuilder error" + e.getMessage());
        }


    }

    public class LogoutRecevier extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == AccountManager.ACCOUNT_LOGOUT_ACTION) {
                boolean isConflict = intent.getBooleanExtra("isConflict",false);
                if(isFroground){
                    if(isConflict){
                        showConflictDialog();
                    }else{
                        onDrivingLogout();
                    }

                }else{
                    if(isAccountAbout){
                        finish();
                    }else{

                    }
                }
            }
        }
    }

    public void onDrivingLogout() {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isConflict",isConflict);
        super.onSaveInstanceState(outState);
    }
}
