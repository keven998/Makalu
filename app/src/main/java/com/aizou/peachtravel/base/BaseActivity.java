package com.aizou.peachtravel.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.peachtravel.common.dialog.PeachMessageDialog;
import com.aizou.peachtravel.config.hxconfig.PeachHXSDKHelper;
import com.easemob.EMCallBack;
import com.umeng.analytics.MobclickAgent;

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
        mLogoutRecevier = new LogoutRecevier();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AccountManager.ACCOUNT_LOGOUT_ACTION);
        registerReceiver(mLogoutRecevier, filter);
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFroground=true;
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFroground=false;
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mLogoutRecevier);
        super.onDestroy();
    }

    public void finishWithNoAnim() {
        super.finish();
    }

    public void startActivityWithNoAnim(Intent intent) {
        super.startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    public void setAccountAbout(boolean isAccountAbout){
        this.isAccountAbout = isAccountAbout;
    }

    protected PeachMessageDialog conflictDialog;
    /**
     * 显示帐号在别处登录dialog
     */
    protected void showConflictDialog() {
        if(isFinishing())
            return;
        try {
            if (conflictDialog == null){
                conflictDialog= new PeachMessageDialog(mContext);
                conflictDialog.setTitle("下线通知");
                conflictDialog.setTitleIcon(R.drawable.ic_dialog_tip);
                conflictDialog.setMessage(getResources().getText(R.string.connect_conflict).toString());
                conflictDialog.setPositiveButton("确定",new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        conflictDialog.dismiss();
                        conflictDialog = null;
                        if(isAccountAbout){
                            finish();
                        }
                    }
                });
                conflictDialog.show();
                conflictDialog.setCancelable(false);
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
