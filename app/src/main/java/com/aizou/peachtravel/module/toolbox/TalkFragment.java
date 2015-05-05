package com.aizou.peachtravel.module.toolbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.log.LogUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.PeachConversation;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.peachtravel.common.dialog.MoreDialog;
import com.aizou.peachtravel.common.dialog.PeachMessageDialog;
import com.aizou.peachtravel.config.Constant;
import com.aizou.peachtravel.config.PeachApplication;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.aizou.peachtravel.db.respository.InviteMsgRepository;
import com.aizou.peachtravel.module.MainActivity;
import com.aizou.peachtravel.module.my.LoginActivity;
import com.aizou.peachtravel.module.toolbox.im.AddContactActivity;
import com.aizou.peachtravel.module.toolbox.im.ChatActivity;
import com.aizou.peachtravel.module.toolbox.im.ContactActivity;
import com.aizou.peachtravel.module.toolbox.im.PickContactsWithCheckboxActivity;
import com.aizou.peachtravel.module.toolbox.im.adapter.ChatAllHistoryAdapter;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by rjm on 2015/3/16.
 */
public class TalkFragment extends PeachBaseFragment {
    public static final int NEW_CHAT_REQUEST_CODE = 101;
    @InjectView(R.id.tv_title_add)
    TextView tvTitleAdd;
    @InjectView(R.id.tv_title_bar_title)
    TextView tvTitleBarTitle;
    @InjectView(R.id.btn_address_list)
    ImageView btnAddressList;
    @InjectView(R.id.unread_address_number)
    TextView unreadAddressNumber;
    @InjectView(R.id.btn_container_address_list)
    RelativeLayout btnContainerAddressList;
    @InjectView(R.id.list)
    ListView list;
    @InjectView(R.id.start_chat)
    ImageView startChat;
    @InjectView(R.id.empty_view)
    LinearLayout emptyView;


    private InputMethodManager inputMethodManager;
    private ListView listView;
    private Map<String, IMUser> contactList;
    private ChatAllHistoryAdapter adapter;
    private List<PeachConversation> conversationList = new ArrayList<PeachConversation>();
    private boolean hidden;
    private List<EMGroup> groups;
    private int del_unread_item=0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_talk, null);
        ButterKnife.inject(this, rootView);
        return rootView;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(((MainActivity)getActivity()).isConflict){
            outState.putBoolean("isConflict", true);
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (ListView) getView().findViewById(R.id.list);
        adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList);
        // 设置adapter
        listView.setAdapter(adapter);
        tvTitleAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showActionDialog();
            }
        });
//        // 搜索框
//        query = (EditText) getView().findViewById(R.id.query);
//        // 搜索框中清除button
//        clearSearch = (ImageButton) getView().findViewById(R.id.search_clear);
//        query.addTextChangedListener(new TextWatcher() {
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                adapter.getFilter().filter(s);
//                if (s.length() > 0) {
//                    clearSearch.setVisibility(View.VISIBLE);
//                } else {
//                    clearSearch.setVisibility(View.INVISIBLE);
//                }
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            public void afterTextChanged(Editable s) {
//            }
//        });
//        clearSearch.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                query.getText().clear();
//
//            }
//        });
        btnContainerAddressList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ContactActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_bottom_in,0);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = adapter.getItem(position).emConversation;
                String username = conversation.getUserName();
                if (username.equals(AccountManager.getInstance().getLoginAccount(getActivity()).easemobUser))
//                    Toast.makeText(getActivity(), "不能和自己聊天", Toast.LENGTH_SHORT).show();
                    ToastUtil.getInstance(getActivity()).showToast("我们还不支持跟自己聊啦");
                else {
                    // 进入聊天页面
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    if (conversation.getIsGroup()) {
//                        EMGroup group = EMGroupManager.getInstance().getGroup(username);
                        // it is group chat
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                        intent.putExtra("groupId", username);
                    } else {
                        // it is single chat
                        intent.putExtra("userId", username);
                    }
                    startActivity(intent);
                }
            }
        });
        // 注册上下文菜单
        registerForContextMenu(listView);

        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        listView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getActivity().getCurrentFocus() != null)
                        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });

        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;

        if(!EMChat.getInstance().isLoggedIn()){
            return;
        }
//        errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
//        errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
        // contact list
        contactList = AccountManager.getInstance().getContactList(getActivity());
//        if(EMGroupManager.getInstance().getAllGroups()==null){
        EMGroupManager.getInstance().loadAllGroups();
//        }
        loadConversationsWithRecentChat();
        updateGroupsInfo();
    }

    private void showActionDialog() {
        String[] names={"新建Talk","加好友"};
        final MoreDialog dialog=new MoreDialog(getActivity());
        dialog.setMoreStyle(false,2,names);
        dialog.getTv3().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent( getActivity(),"event_create_new_talk");
                startActivityForResult(new Intent( getActivity(), PickContactsWithCheckboxActivity.class).putExtra("request", NEW_CHAT_REQUEST_CODE), NEW_CHAT_REQUEST_CODE);
                getActivity().overridePendingTransition(R.anim.push_bottom_in,0);
                dialog.dismiss();
            }
        });
        dialog.getTv4().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent( getActivity(),"event_add_new_friend");
                startActivity(new Intent( getActivity(), AddContactActivity.class));
                getActivity().overridePendingTransition(R.anim.push_bottom_in,0);
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void setEmptyView() {
//        listView.setEmptyView();
        if (listView.getEmptyView() == null) {
            View emptyView = getActivity().findViewById(R.id.empty_view);
            listView.setEmptyView(emptyView);
            getActivity().findViewById(R.id.start_chat).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().startActivityForResult(new Intent(getActivity(), PickContactsWithCheckboxActivity.class).putExtra("request", NEW_CHAT_REQUEST_CODE), NEW_CHAT_REQUEST_CODE);
                }
            });
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
        getActivity().getMenuInflater().inflate(R.menu.delete_message, menu);
        // }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_message) {
            MobclickAgent.onEvent(getActivity(), "event_delete_talk_item");
            PeachConversation peachConversation = adapter.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
            EMConversation tobeDeleteCons = peachConversation.emConversation;
            // 删除此会话
            EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup());
            InviteMsgRepository.deleteInviteMsg(getActivity(), tobeDeleteCons.getUserName());

            refresh();

            // 更新消息未读数
			((MainActivity) getActivity()).updateUnreadMsgCount();

            if (adapter.getCount() <= 0) {
                setEmptyView();
            }

            return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        loadConversationsWithRecentChat();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        updateUnreadAddressLable();

    }

    public void updateUnreadAddressLable(){
                int count = getUnreadAddressCountTotal();
                if (count > 0) {
                    unreadAddressNumber.setText(String.valueOf(count));
                    unreadAddressNumber.setVisibility(View.VISIBLE);
                } else {
                    unreadAddressNumber.setVisibility(View.GONE);
                }
    }
    /**
     * 获取未读申请与通知消息
     *
     * @return
     */
    public int getUnreadAddressCountTotal() {
        int unreadAddressCountTotal = 0;
        unreadAddressCountTotal = (int) InviteMsgRepository.getUnAcceptMsgCount(PeachApplication.getContext());
        if (AccountManager.getInstance().getContactList(PeachApplication.getContext()).get(Constant.NEW_FRIENDS_USERNAME) != null) {
            IMUser imUser = AccountManager.getInstance().getContactList(PeachApplication.getContext()).get(Constant.NEW_FRIENDS_USERNAME);
            imUser.setUnreadMsgCount(unreadAddressCountTotal);
            IMUserRepository.saveContact(PeachApplication.getContext(), imUser);
        }
        return unreadAddressCountTotal;
    }

    /**
     * 获取所有会话
     *
     * @return
     */
    private List<PeachConversation> loadConversationsWithRecentChat() {
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        conversationList.clear();
        //过滤掉messages seize为0的conversation
        final Iterator<EMConversation> conversationIt = conversations.values().iterator();

        while (conversationIt.hasNext()) {
            final EMConversation conversation = conversationIt.next();
            if (conversation.getIsGroup()) {

                EMGroup group = EMGroupManager.getInstance().getGroup(conversation.getUserName());
                if (group == null) {

                } else {
                    PeachConversation peachConversation = new PeachConversation();
                    peachConversation.emConversation = conversation;
                    conversationList.add(peachConversation);
                }


            } else if (!TextUtils.isEmpty(conversation.getUserName())) {
                IMUser user = IMUserRepository.getMyFriendByUserName(getActivity(), conversation.getUserName());
                if (user != null) {
                    PeachConversation peachConversation = new PeachConversation();
                    peachConversation.emConversation = conversation;
                    peachConversation.imUser = user;
                    conversationList.add(peachConversation);
                } else {
                }
            }
        }

        // 排序
        sortConversationByLastChatTime(conversationList);
        return conversationList;
    }

    public void updateGroupsInfo() {
        final ArrayList<String> groupIdList = new ArrayList<>();
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        for(Map.Entry<String,EMConversation> entry:conversations.entrySet()){
            EMConversation conversation= entry.getValue();
            if(conversation.getIsGroup()){
                groupIdList.add(conversation.getUserName());
            }

        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Iterator<String> it = groupIdList.iterator();
                while (it.hasNext()) {
                    String groupId = it.next();
                    try {
                        EMGroup emGroup = EMGroupManager.getInstance().getGroupFromServer(groupId);
                        if (emGroup != null) {
                            if (emGroup.getMembers().contains(AccountManager.getInstance().getLoginAccount(PeachApplication.getContext()).easemobUser)) {
                                EMGroupManager.getInstance().createOrUpdateLocalGroup(emGroup);

                            } else {
                                EMChatManager.getInstance().deleteConversation(groupId, true);
                            }
                        } else {
                            EMChatManager.getInstance().deleteConversation(groupId, true);
                        }
                    } catch (EaseMobException e) {
                        LogUtil.d("errcode=" + e.getErrorCode());
                        e.printStackTrace();
                    }

                }
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refresh();
                        }
                    });
                }



            }
        }).start();
    }

    /**
     * 根据最后一条消息的时间排序
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<PeachConversation> conversationList) {
        Collections.sort(conversationList, new Comparator<PeachConversation>() {
            @Override
            public int compare(final PeachConversation con1, final PeachConversation con2) {

                EMMessage con2LastMessage = con2.emConversation.getLastMessage();
                EMMessage con1LastMessage = con1.emConversation.getLastMessage();
                if (con1LastMessage == null || con2LastMessage == null) {
                    return -1;
                }
                if (con2LastMessage.getMsgTime() == con1LastMessage.getMsgTime()) {
                    return 0;
                } else if (con2LastMessage.getMsgTime() > con1LastMessage.getMsgTime()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //返回页面的动画样式
        //getActivity().overridePendingTransition(R.anim.push_bottom_out,R.anim.push_bottom_in);
        MobclickAgent.onPageStart("page_talk_lists");
        if (!hidden) {
            refresh();
        }
        if(EMChat.getInstance().isLoggedIn()) {
            if (listView.getAdapter().getCount() <= 0) {
                setEmptyView();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("page_talk_lists");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case NEW_CHAT_REQUEST_CODE:
                    int chatType = data.getIntExtra("chatType", 0);
                    String toId = data.getStringExtra("toId");
                    if (chatType == ChatActivity.CHATTYPE_GROUP) {
                        //进入群聊
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        // it is group chat
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                        intent.putExtra("groupId", toId);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        // it is single chat
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
                        intent.putExtra("userId", toId);
                        startActivity(intent);
                    }


            }
        }

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


}
