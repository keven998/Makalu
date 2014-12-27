/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aizou.peachtravel.module.toolbox.im;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.ChatBaseActivity;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.config.Constant;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.InviteMessage;
import com.aizou.peachtravel.db.InviteStatus;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.aizou.peachtravel.db.respository.InviteMsgRepository;
import com.aizou.peachtravel.module.toolbox.im.adapter.NewFriendsMsgAdapter;

/**
 * 申请与通知
 *
 */
public class NewFriendsMsgActivity extends ChatBaseActivity {
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_friends_msg);

        findViewById(R.id.tv_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

		listView = (ListView) findViewById(R.id.list);
		List<InviteMessage> msgs = InviteMsgRepository.getMessagesList(mContext);
        for (InviteMessage msg : msgs) {
            if (msg.getStatus() == InviteStatus.BEINVITEED&&IMUserRepository.isMyFriend(mContext,msg.getFrom())) {
                msg.setStatus(InviteStatus.AGREED);
                InviteMsgRepository.saveMessage(mContext,msg);
            }
        }
		//设置adapter
		NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs);
		listView.setAdapter(adapter);
        IMUser imUser = AccountManager.getInstance().getContactList(this).get(Constant.NEW_FRIENDS_USERNAME);
        imUser.setUnreadMsgCount(0);
        IMUserRepository.saveContact(this,imUser);
	}

	public void back(View view) {
		finish();
	}
	
	
}
