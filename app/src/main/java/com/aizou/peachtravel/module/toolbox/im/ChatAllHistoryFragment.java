package com.aizou.peachtravel.module.toolbox.im;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.bean.PeachConversation;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.aizou.peachtravel.db.respository.InviteMsgRepository;
import com.aizou.peachtravel.module.toolbox.im.adapter.ChatAllHistoryAdapter;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;

/**
 * 显示所有会话记录，比较简单的实现，更好的可能是把陌生人存入本地，这样取到的聊天记录是可控的
 */
public class ChatAllHistoryFragment extends Fragment {
    public static final int NEW_CHAT_REQUEST_CODE = 101;

    private InputMethodManager inputMethodManager;
    private ListView listView;
    private Map<String, IMUser> contactList;
    private ChatAllHistoryAdapter adapter;
    private List<PeachConversation> conversationList=new ArrayList<PeachConversation>();
//    private EditText query;
//    private ImageButton clearSearch;
//    public RelativeLayout errorItem;
//    public TextView errorText;
    private boolean hidden;
    private List<EMGroup> groups;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation_history, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
//        errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
        // contact list
        contactList = AccountManager.getInstance().getContactList(getActivity());
//        if(EMGroupManager.getInstance().getAllGroups()==null){
            EMGroupManager.getInstance().loadAllGroups();
//        }
        listView = (ListView) getView().findViewById(R.id.list);
        loadConversationsWithRecentChat();
        adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList);
        // 设置adapter
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

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

        listView.setOnTouchListener(new OnTouchListener() {

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

    }

    private void setEmptyView() {
//        listView.setEmptyView();
        if (listView.getEmptyView() == null) {
            View emptyView = getActivity().findViewById(R.id.empty_view);
            listView.setEmptyView(emptyView);
            getActivity().findViewById(R.id.start_chat).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().startActivityForResult(new Intent(getActivity(), PickContactsWithCheckboxActivity.class).putExtra("request", NEW_CHAT_REQUEST_CODE), NEW_CHAT_REQUEST_CODE);
                }
            });
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
        getActivity().getMenuInflater().inflate(R.menu.delete_message, menu);
        // }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_message) {
            PeachConversation peachConversation = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
            EMConversation tobeDeleteCons = peachConversation.emConversation;
            // 删除此会话
            EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup());
            InviteMsgRepository.deleteInviteMsg(getActivity(), tobeDeleteCons.getUserName());
            refresh();

            // 更新消息未读数
//			((MainActivity) getActivity()).updateUnreadLabel();
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
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }

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
        Iterator<EMConversation> conversationIt = conversations.values().iterator();
        while (conversationIt.hasNext()){
            final EMConversation conversation = conversationIt.next();
            if(conversation.getIsGroup()){
                PeachConversation peachConversation = new PeachConversation();
                peachConversation.emConversation = conversation;
                conversationList.add(peachConversation);
                EMGroup group=EMGroupManager.getInstance().getGroup(conversation.getUserName());
                if(group==null){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMGroup emGroup =EMGroupManager.getInstance().getGroupFromServer(conversation.getUserName());
                                EMGroupManager.getInstance().createOrUpdateLocalGroup(emGroup);
                                if(isAdded()&&adapter!=null){
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });

                                }
                            } catch (EaseMobException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

            }else if(!TextUtils.isEmpty(conversation.getUserName())){
                IMUser user = IMUserRepository.getMyFriendByUserName(getActivity(), conversation.getUserName());
                if(user!=null){
                    PeachConversation peachConversation = new PeachConversation();
                    peachConversation.emConversation = conversation;
                    peachConversation.imUser = user;
                    conversationList.add(peachConversation);
                }else{
                    conversationIt.remove();
                }
            }
        }
        // 排序
        sortConversationByLastChatTime(conversationList);
        return conversationList;
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
                if(con1LastMessage==null||con2LastMessage==null){
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
        if (!hidden) {
            refresh();
        }
        if (listView.getAdapter().getCount() <= 0) {
            setEmptyView();
        }
    }

}
