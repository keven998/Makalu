package com.aizou.peachtravel.base;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.config.hxconfig.PeachHXSDKHelper;
import com.aizou.peachtravel.module.MainActivity;
import com.aizou.peachtravel.module.my.LoginActivity;
import com.easemob.EMCallBack;
import com.easemob.chat.EMMessage;
import com.easemob.chat.NotificationCompat;
import com.easemob.util.EasyUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Rjm on 2014/12/4.
 */
public class BaseActivity extends FragmentActivity {
    protected Context mContext;
    protected boolean isFroground;
    protected boolean isAccountAbout;
    protected boolean isConflict;
    private static final int notifiId = 11;
    protected NotificationManager notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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



    public void onDrivingLogout() {

    }

    /**
     * 当应用在前台时，如果当前消息不是属于当前会话，在状态栏提示一下
     * 如果不需要，注释掉即可
     * @param message
     */
    protected void notifyNewMessage(EMMessage message) {
        //如果是设置了不提醒只显示数目的群组(这个是app里保存这个数据的，demo里不做判断)
        //以及设置了setShowNotificationInbackgroup:false(设为false后，后台时sdk也发送广播)
        if(!EasyUtils.isAppRunningForeground(this)){
            return;
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getApplicationInfo().icon)
                .setWhen(System.currentTimeMillis()).setAutoCancel(true);

        String ticker = IMUtils.getMessageDigest(message, this);
        if(message.getType() == EMMessage.Type.TXT)
            ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
        //设置状态栏提示
        mBuilder.setTicker(message.getFrom()+": " + ticker);

        //必须设置pendingintent，否则在2.3的机器上会有bug
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notifiId, intent, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pendingIntent);

        Notification notification = mBuilder.build();
        notificationManager.notify(notifiId, notification);
        notificationManager.cancel(notifiId);
    }
}
