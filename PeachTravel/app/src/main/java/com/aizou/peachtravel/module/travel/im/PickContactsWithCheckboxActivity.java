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
package com.aizou.peachtravel.module.travel.im;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.BaseChatActivity;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.common.widget.Sidebar;
import com.aizou.peachtravel.common.widget.TopSectionBar;
import com.aizou.peachtravel.config.Constant;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.module.travel.im.adapter.ContactAdapter;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PickContactsWithCheckboxActivity extends BaseChatActivity {
    private ListView listView;
    private RecyclerView toBeAddContactsRv;
    private List<IMUser> toBeAddContacts;
    /**
     * 是否为一个新建的群组
     */
    protected boolean isCreatingNewGroup;
    /**
     * 是否为单选
     */
    private boolean isSignleChecked;
    private PickContactAdapter contactAdapter;
    private ToBeAddContactsAdapter toBeAddAdapter;
    /**
     * group中一开始就有的成员
     */
    private List<String> exitingMembers;
    // 好友列表
    private List<IMUser> alluserList;
    private TopSectionBar sectionBar;
    private int request;
    private String groupId;
    private EMGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_pick_contacts);

        // String groupName = getIntent().getStringExtra("groupName");
        request = getIntent().getIntExtra("request", 0);
        String groupId = getIntent().getStringExtra("groupId");
        if (groupId == null) {// 创建群组
            isCreatingNewGroup = true;
        } else {
            // 获取此群组的成员列表
            group = EMGroupManager.getInstance().getGroup(groupId);
            exitingMembers = group.getMembers();
        }
        if (exitingMembers == null)
            exitingMembers = new ArrayList<String>();
        // 获取好友列表
        alluserList = new ArrayList<IMUser>();
        for (IMUser user : AccountManager.getInstance().getContactList(this).values()) {
            if (!user.getUsername().equals(Constant.NEW_FRIENDS_USERNAME) & !user.getUsername().equals(Constant.GROUP_USERNAME))
                alluserList.add(user);
        }
        // 对list进行排序
        Collections.sort(alluserList, new Comparator<IMUser>() {
            @Override
            public int compare(IMUser lhs, IMUser rhs) {
                return (lhs.getUsername().compareTo(rhs.getUsername()));

            }
        });
        listView = (ListView) findViewById(R.id.list);
        sectionBar = (TopSectionBar) findViewById(R.id.section_bar);
        toBeAddContactsRv = (RecyclerView) findViewById(R.id.rv_add_contacts);
        toBeAddContacts = new ArrayList<IMUser>();
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        toBeAddContactsRv.setLayoutManager(linearLayoutManager);
        //设置适配器
        toBeAddAdapter = new ToBeAddContactsAdapter(mContext, toBeAddContacts);
        toBeAddContactsRv.setAdapter(toBeAddAdapter);

        contactAdapter = new PickContactAdapter(this, R.layout.row_contact_with_checkbox, alluserList);
        listView.setAdapter(contactAdapter);
        sectionBar.setListView(listView);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                checkBox.toggle();

            }
        });
    }

    /**
     * 确认选择的members
     *
     * @param v
     */
    public void save(View v) {
        if(toBeAddContacts.size()==0) {
            ToastUtil.getInstance(mContext).showToast("请选择联系人");
            return;
        }
        final StringBuffer groupName = new StringBuffer();
        final StringBuffer membersStr = new StringBuffer();
        for (int i = 0; i < toBeAddContacts.size(); i++) {
            if (i < 3) {
                groupName.append(toBeAddContacts.get(i).getNick());
                if (i!=toBeAddContacts.size()-1&&i != 2) {
                    groupName.append("、");
                }
            }
            membersStr.append(toBeAddContacts.get(i).getNick());
            if (i != toBeAddContacts.size()-1) {
                membersStr.append("、");
            }
        }
        if (toBeAddContacts.size() > 3) {
            groupName.append("...");
        }
        if (request == IMMainActivity.NEW_CHAT_REQUEST_CODE) {
            //新建群组
            if (toBeAddContacts.size() > 1) {
                DialogManager.getInstance().showProgressDialog(mContext, "正在新建群组");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 调用sdk创建群组方法


                        String desc = "";
                        ArrayList<String> members = new ArrayList<String>();
                        for (IMUser imUser : toBeAddContacts) {
                            members.add(imUser.getUsername());
                        }
                        try {
                            //创建不公开群
                            final EMGroup group = EMGroupManager.getInstance().createPrivateGroup(groupName.toString(), desc, members.toArray(new String[0]), true,50);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    DialogManager.getInstance().dissMissProgressDialog();
                                    // 被邀请
                                    EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                                    msg.setChatType(EMMessage.ChatType.GroupChat);
                                    msg.setFrom(AccountManager.getInstance().getLoginAccount(mContext).easemobUser);
                                    msg.setReceipt(group.getGroupId());
                                    IMUtils.setMessageWithTaoziUserInfo(mContext, msg);
                                    String myNickmae = AccountManager.getInstance().getLoginAccount(mContext).nickName;
                                    String content = String.format(mContext.getResources().getString(R.string.invate_to_group),myNickmae,membersStr.toString());
                                    IMUtils.setMessageWithExtTips(mContext,msg,content);
                                    msg.addBody(new TextMessageBody(content));
                                    EMChatManager.getInstance().sendGroupMessage(msg,new EMCallBack() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(int i, String s) {

                                        }

                                        @Override
                                        public void onProgress(int i, String s) {

                                        }
                                    });
                                    //进入群聊
                                    Intent intent = new Intent(mContext, ChatActivity.class);
                                    // it is group chat
                                    intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                                    intent.putExtra("groupId", group.getGroupId());
                                    startActivity(intent);
                                    finish();

                                }
                            });
                        } catch (final Exception e) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    DialogManager.getInstance().dissMissProgressDialog();
                                }
                            });
                        }

                    }
                }).start();
            } else if (toBeAddContacts.size() == 1) {
                startActivity(new Intent(mContext, ChatActivity.class).putExtra("userId", toBeAddContacts.get(0).getUsername()));
                finish();
            }


        } else if(groupId!=null){
            DialogManager.getInstance().showProgressDialog(mContext);
            new Thread(new Runnable() {

                public void run() {
                    ArrayList<String> members = new ArrayList<String>();
                    for (IMUser imUser : toBeAddContacts) {
                        members.add(imUser.getUsername());
                    }
                    try {
                        //创建者调用add方法
                        if(EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())){
                            EMGroupManager.getInstance().addUsersToGroup(groupId,members.toArray(new String[0]));
                        }else{
                            //一般成员调用invite方法
                            EMGroupManager.getInstance().inviteUser(groupId, members.toArray(new String[0]), null);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogManager.getInstance().dissMissProgressDialog();
                                // 被邀请
                                EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                                msg.setChatType(EMMessage.ChatType.GroupChat);
                                msg.setFrom(AccountManager.getInstance().getLoginAccount(mContext).easemobUser);
                                msg.setReceipt(group.getGroupId());
                                IMUtils.setMessageWithTaoziUserInfo(mContext, msg);
                                String myNickname = AccountManager.getInstance().getLoginAccount(mContext).nickName;
                                String content = String.format(mContext.getResources().getString(R.string.invate_to_group),myNickname,membersStr.toString());
                                IMUtils.setMessageWithExtTips(mContext,msg,content);
                                msg.addBody(new TextMessageBody(content));
                                EMChatManager.getInstance().sendGroupMessage(msg,new EMCallBack() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(int i, String s) {

                                    }

                                    @Override
                                    public void onProgress(int i, String s) {

                                    }
                                });
                                setResult(RESULT_OK);
                                finish();
                            }
                        });

                    } catch (final Exception e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                DialogManager.getInstance().dissMissProgressDialog();
                                Toast.makeText(getApplicationContext(), "添加群成员失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();
        }

    }

    /**
     * 获取要被添加的成员
     *
     * @return
     */
    private List<IMUser> getToBeAddMembers() {
        return toBeAddContacts;
    }

    public class ToBeAddContactsAdapter extends
            RecyclerView.Adapter<ToBeAddContactsAdapter.ViewHolder> {

        private LayoutInflater mInflater;
        private List<IMUser> mDatas;

        public ToBeAddContactsAdapter(Context context, List<IMUser> datas) {
            mInflater = LayoutInflater.from(context);
            mDatas = datas;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View arg0) {
                super(arg0);
            }

            ImageView mImg;
            TextView mTxt;
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        /**
         * 创建ViewHolder
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.item_select_contact,
                    viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);

            viewHolder.mImg = (ImageView) view
                    .findViewById(R.id.iv_avatar);
            viewHolder.mTxt = (TextView) view
                    .findViewById(R.id.tv_nickname);

            return viewHolder;
        }

        /**
         * 设置值
         */
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            IMUser user = mDatas.get(i);
            ImageLoader.getInstance().displayImage(mDatas.get(i).getAvatar(), viewHolder.mImg, UILUtils.getDefaultOption());
            viewHolder.mTxt.setText(mDatas.get(i).getNick());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = alluserList.indexOf(mDatas.get(i));
                    contactAdapter.isCheckedArray[index] = false;
                    contactAdapter.notifyDataSetChanged();
                }
            });
        }

    }

    /**
     * adapter
     */
    private class PickContactAdapter extends ContactAdapter {

        public boolean[] isCheckedArray;

        public PickContactAdapter(Context context, int resource, List<IMUser> users) {
            super(context, resource, users);
            isCheckedArray = new boolean[users.size()];
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            final String username = getItem(position).getUsername();
            final IMUser user = AccountManager.getInstance().getContactList(mContext).get(username);
            // 选择框checkbox
            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);

            if (checkBox != null) {
                // checkBox.setOnCheckedChangeListener(null);
                if (exitingMembers != null && exitingMembers.contains(username)) {
                    checkBox.setButtonDrawable(R.drawable.checkbox_bg_gray_selector);
                } else {
                    checkBox.setButtonDrawable(R.drawable.checkbox_bg_selector);
                }
                checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // 群组中原来的成员一直设为选中状态
                        if (exitingMembers.contains(username)) {
                            isChecked = true;
                            checkBox.setChecked(true);
                        } else {
                            if (isChecked) {
                                toBeAddContacts.add(user);
                            } else {
                                toBeAddContacts.remove(user);
                            }
                            toBeAddAdapter.notifyDataSetChanged();
                        }
                        isCheckedArray[position] = isChecked;
                        //如果是单选模式
                        if (isSignleChecked && isChecked) {
                            for (int i = 0; i < isCheckedArray.length; i++) {
                                if (i != position) {
                                    isCheckedArray[i] = false;
                                }
                            }
                            contactAdapter.notifyDataSetChanged();
                        }
                    }
                });
                // 群组中原来的成员一直设为选中状态
                if (exitingMembers.contains(username)) {
                    checkBox.setChecked(true);
                    isCheckedArray[position] = true;
                } else {
                    checkBox.setChecked(isCheckedArray[position]);
                }
            }
            return view;
        }
    }

    public void back(View view) {
        finish();
    }

}
