package com.aizou.peachtravel.base;

import com.easemob.chat.EMChatManager;

/**
 * Created by Rjm on 2014/10/21.
 */
public class BaseChatActivity extends PeachBaseActivity {
    @Override
    protected void onResume() {
        super.onResume();
        //onresume时，取消notification显示
        EMChatManager.getInstance().activityResumed();
    }
}
