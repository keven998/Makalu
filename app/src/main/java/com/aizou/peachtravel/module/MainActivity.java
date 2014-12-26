package com.aizou.peachtravel.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.AssetUtils;
import com.aizou.core.widget.FragmentTabHost;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.TestBean;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.dialog.PeachMessageDialog;
import com.aizou.peachtravel.module.dest.RecDestFragment;
import com.aizou.peachtravel.module.my.MyFragment;
import com.aizou.peachtravel.module.toolbox.ToolboxFragment;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMNotifier;
import com.google.gson.Gson;

import java.util.List;


public class MainActivity extends PeachBaseActivity {

    //定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    //定义一个布局
    private LayoutInflater layoutInflater;

    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {ToolboxFragment.class, RecDestFragment.class, MyFragment.class,};

   // 定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.tab_tao_selector,R.drawable.tab_loc_selector,R.drawable.tab_my_selector,
            };

    //Tab选项卡的文字
//    private String mTextviewArray[] = {"首页", "想去", "我"};
    //Tab选项Tag
    private String mTagArray[] = {"Home", "Loc", "My"};
    private NewMessageBroadcastReceiver msgReceiver;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        List<String> blacklist = null;
        // 注册一个接收消息的BroadcastReceiver
        msgReceiver = new NewMessageBroadcastReceiver();

    }

    /**
     * 初始化组件
     */
    private void initView(){
        //实例化布局对象
        layoutInflater = LayoutInflater.from(this);

        //实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        //得到fragment的个数
        int count = fragmentArray.length;

        for(int i = 0; i < count; i++){
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTagArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
        }
    }


    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index){
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

//        TextView textView = (TextView) view.findViewById(R.id.textview);
//        textView.setText(mTextviewArray[index]);
        return view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(msgReceiver);
    }

    @Override
    protected void showConflictDialog(){
        if(isFinishing())
            return;
        MyFragment myFragment = (MyFragment) getSupportFragmentManager().findFragmentByTag("My");
        if(myFragment!=null){
            myFragment.refresh();
        }
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

    /**
     * 新消息广播接收者
     */
    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看

            // 消息id
            String msgId = intent.getStringExtra("msgid");
            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
            // EMMessage message =
            // EMChatManager.getInstance().getMessage(msgId);

            ToolboxFragment toolboxFragment = (ToolboxFragment) getSupportFragmentManager().findFragmentByTag("Home");
            if(toolboxFragment!=null){
                toolboxFragment.updateUnreadLabel();
            }
            // 提示有新消息
            EMNotifier.getInstance(mContext).notifyOnNewMsg();
            // 注销广播，否则在ChatActivity中会收到这个广播
            abortBroadcast();
        }
    }



    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    public void onDrivingLogout() {
        MyFragment myFragment = (MyFragment) getSupportFragmentManager().findFragmentByTag("My");
        if(myFragment!=null){
            myFragment.refresh();
        }
    }
}
