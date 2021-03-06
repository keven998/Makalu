/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xuejian.client.lxp.module.toolbox.im;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lv.bean.InventMessage;
import com.lv.im.IMClient;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.ChatBaseActivity;
import com.xuejian.client.lxp.module.toolbox.im.adapter.NewFriendsMsgAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 申请与通知
 *
 */
public class NewFriendsMsgActivity extends ChatBaseActivity {
    private ListView listView;
    private NewFriendsMsgAdapter adapter;
    private List<InventMessage> invents;

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
        invents = new ArrayList<>();
        invents = IMClient.getInstance().getInventMessages();
        listView = (ListView) findViewById(R.id.list);
        //设置adapter
        adapter = new NewFriendsMsgAdapter(this, 1, invents);
        listView.setAdapter(adapter);
         registerForContextMenu(listView);
        IMClient.getInstance().updateInventMsgReadStatus(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("page_ask_for_friend");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_ask_for_friend");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
     //   super.onCreateContextMenu(menu, v, menuInfo);
    //   if(((AdapterView.AdapterContextMenuInfo)menuInfo).position > 0){
        getMenuInflater().inflate(R.menu.delete_request, menu);
    //    }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_request) {
            InventMessage message = invents.get(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
            IMClient.getInstance().deleteInventMessage(String.valueOf(message.getUserId()));
            invents.remove(message);
            adapter.notifyDataSetChanged();
            return false;
        }
        return super.onContextItemSelected(item);
    }

    public void back(View view) {
        finish();
    }


}
