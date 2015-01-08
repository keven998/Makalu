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
import android.view.*;
import android.view.ContextMenu;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.ChatBaseActivity;
import com.aizou.peachtravel.bean.PeachConversation;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.config.Constant;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.InviteMessage;
import com.aizou.peachtravel.db.InviteStatus;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.aizou.peachtravel.db.respository.InviteMsgRepository;
import com.aizou.peachtravel.module.toolbox.im.adapter.NewFriendsMsgAdapter;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;

/**
 * 申请与通知
 *
 */
public class NewFriendsMsgActivity extends ChatBaseActivity {
	private ListView listView;
    private List<InviteMessage> msgs;
    private NewFriendsMsgAdapter adapter;


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
	    msgs = InviteMsgRepository.getMessagesList(mContext);
        for (InviteMessage msg : msgs) {
            if (msg.getStatus() == InviteStatus.BEINVITEED&&IMUserRepository.isMyFriend(mContext,msg.getFrom())) {
                msg.setStatus(InviteStatus.AGREED);
                InviteMsgRepository.saveMessage(mContext,msg);
            }
        }
		//设置adapter
	    adapter = new NewFriendsMsgAdapter(this, 1, msgs);
		listView.setAdapter(adapter);
        registerForContextMenu(listView);

	}

    @Override
    public void onCreateContextMenu(android.view.ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
       getMenuInflater().inflate(R.menu.delete_request, menu);
        // }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_request) {
            InviteMessage message = msgs.get(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
            InviteMsgRepository.deleteInviteMsg(mContext,message.getFrom());
            msgs.remove(message);
            adapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }

	public void back(View view) {
		finish();
	}
	
	
}
