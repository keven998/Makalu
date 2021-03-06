package com.xuejian.client.lxp.module.toolbox.im;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lv.bean.ConversationBean;
import com.lv.im.IMClient;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.toolbox.im.adapter.ChatAllHistoryAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 显示所有会话记录，比较简单的实现，更好的可能是把陌生人存入本地，这样取到的聊天记录是可控的
 */
public class ChatAllHistoryFragment extends Fragment {
    public static final int NEW_CHAT_REQUEST_CODE = 101;

    private InputMethodManager inputMethodManager;
    private ListView listView;
    private Map<Long, User> contactList;
    private ChatAllHistoryAdapter adapter;
    private List<ConversationBean> conversationList = new ArrayList<ConversationBean>();
    //    private EditText query;
//    private ImageButton clearSearch;
//    public RelativeLayout errorItem;
//    public TextView errorText;
    private boolean hidden;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation_history, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
//        errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
        // contact list
        contactList = AccountManager.getInstance().getContactList(getActivity());
        loadConversationsWithRecentChat();
        updateGroupsInfo();
        listView = (ListView) getView().findViewById(R.id.list);
        //  adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList);
        // 设置adapter
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
            //  PeachConversation peachConversation = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
            //  EMConversation tobeDeleteCons = peachConversation.emConversation;
            // 删除此会话
            //   InviteMsgRepository.deleteInviteMsg(getActivity(), tobeDeleteCons.getUserName());
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
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

    }

    /**
     * 获取所有会话
     *
     * @return
     */
    private List<ConversationBean> loadConversationsWithRecentChat() {
        // 获取所有会话，包括陌生人
        conversationList.clear();
        conversationList = IMClient.getInstance().getConversationList();
        //过滤掉messages seize为0的conversation


        // 排序
        sortConversationByLastChatTime(conversationList);
        return conversationList;
    }

    public void updateGroupsInfo() {
    }

    /**
     * 根据最后一条消息的时间排序
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<ConversationBean> conversationList) {
        Collections.sort(conversationList, new Comparator<ConversationBean>() {
            @Override
            public int compare(final ConversationBean con1, final ConversationBean con2) {
                if (con1 == null || con2 == null) {
                    return -1;
                }
                if (con2.getLastChatTime() == con1.getLastChatTime()) {
                    return 0;
                } else if (con2.getLastChatTime() > con1.getLastChatTime()) {
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
//        MobclickAgent.onPageStart("page_talk_lists");
        if (!hidden) {
            refresh();
        }
        if (listView.getAdapter().getCount() <= 0) {
            setEmptyView();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_talk_lists");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtils.fixInputMethodManagerLeak(getActivity());
    }
}
