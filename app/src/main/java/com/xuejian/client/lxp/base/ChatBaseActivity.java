package com.xuejian.client.lxp.base;

import android.os.Bundle;

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
