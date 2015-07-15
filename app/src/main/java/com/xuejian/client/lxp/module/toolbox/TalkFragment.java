package com.xuejian.client.lxp.module.toolbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.lv.bean.ConversationBean;
import com.lv.im.IMClient;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.dialog.MoreDialog;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.module.MainActivity;
import com.xuejian.client.lxp.module.toolbox.im.AddContactActivity;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;
import com.xuejian.client.lxp.module.toolbox.im.ContactActivity;
import com.xuejian.client.lxp.module.toolbox.im.PickContactsWithCheckboxActivity;
import com.xuejian.client.lxp.module.toolbox.im.adapter.ChatAllHistoryAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class TalkFragment extends PeachBaseFragment {
    public static final int NEW_CHAT_REQUEST_CODE = 101;
    @InjectView(R.id.tv_title_add)
    TextView tvTitleAdd;
    @InjectView(R.id.unread_address_number)
    TextView unreadAddressNumber;
    @InjectView(R.id.btn_container_address_list)
    RelativeLayout btnContainerAddressList;
    @InjectView(R.id.tv_title_bar_title)
    TextView title_bar_title;
    private InputMethodManager inputMethodManager;
    private ListView listView;
    private ChatAllHistoryAdapter adapter;
    private boolean hidden;
    private List<ConversationBean> conversations = new ArrayList<>();
    private List<ConversationBean> tempConversations = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_talk, null);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (((MainActivity) getActivity()).isConflict) {
            outState.putBoolean("isConflict", true);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (ListView) getView().findViewById(R.id.list);
        adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversations);
        // 设置adapter
        listView.setAdapter(adapter);
        tvTitleAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionDialog();
            }
        });
        btnContainerAddressList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ContactActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ConversationBean conversation = adapter.getItem(position);
//                String username = conversation.getFriendId() + "";
                if (String.valueOf(conversation.getFriendId()).equals(AccountManager.getCurrentUserId()))
                    //  if (username.equals(AccountManager.getInstance().getLoginAccount(getActivity()).easemobUser))
//                    Toast.makeText(getActivity(), "不能和自己聊天", Toast.LENGTH_SHORT).show();
                    ToastUtil.getInstance(getActivity()).showToast("还不支持自己聊");
                else {
                    // 进入聊天页面
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("friend_id", conversation.getFriendId() + "");
                    intent.putExtra("chatType", conversation.getChatType());
                    if (conversation.getConversation() != null) {
                        intent.putExtra("conversation", conversation.getConversation());
                    }
                    if (conversation.getHASH() != null) {
                        intent.putExtra("name", conversation.getHASH());
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
        if (!AccountManager.getInstance().isLogin()) {
            return;
        }
        loadConversation();
    }

    private void showActionDialog() {
        String[] names = {"新建聊天", "添加好友", "取消"};
        final MoreDialog dialog = new MoreDialog(getActivity());
        dialog.setMoreStyle(false, 3, names);
        dialog.getTv1().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "event_create_new_talk");
                startActivityForResult(new Intent(getActivity(), PickContactsWithCheckboxActivity.class).putExtra("request", NEW_CHAT_REQUEST_CODE), NEW_CHAT_REQUEST_CODE);
                getActivity().overridePendingTransition(R.anim.push_bottom_in, 0);
                dialog.dismiss();
            }
        });
        dialog.getTv3().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "event_add_new_friend");
                startActivity(new Intent(getActivity(), AddContactActivity.class));
                getActivity().overridePendingTransition(R.anim.push_bottom_in, 0);
                dialog.dismiss();
            }
        });
        dialog.getTv4().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void netStateChange(String state) {
        title_bar_title.setText("消息" + state);
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
            int pos = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
            ConversationBean Conversation = adapter.getItem(pos);
            IMClient.getInstance().deleteConversation(Conversation.getFriendId() + "");
            conversations.remove(pos);
            refresh();
            // 更新消息未读数
            ((MainActivity) getActivity()).updateUnreadMsgCount();

            if (adapter.getCount() <= 0) {
                //setEmptyView();
            }

            return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        if (!AccountManager.getInstance().isLogin())return;
        // loadConversationsWithRecentChat();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        updateUnreadAddressLable();
        ((MainActivity) getActivity()).updateUnreadMsgCount();
    }

    public void loadConversation() {
        List<ConversationBean>del=new ArrayList<>();
        conversations.clear();
        tempConversations.clear();
        tempConversations.addAll(IMClient.getInstance().getConversationList());
        for (ConversationBean bean:tempConversations){
            if (bean.getFriendId()==10000){
                conversations.add(0,bean);
                del.add(bean);
            }
        }
        tempConversations.removeAll(del);
        sortConversationByLastChatTime(tempConversations);
        if (conversations.size()==0){
            conversations.add(0,new ConversationBean(10000,0,"single"));
        }
        conversations.addAll(tempConversations);
        refresh();
    }

    public void updateUnreadAddressLable() {
        int count = getUnreadAddressCountTotal();
        //  int count=IMClient.getInstance().getUnReadCount();
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
        return IMClient.getInstance().getUnAcceptMsg();
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

                long LastTime2 = con2.getLastChatTime();
                long LastTime1 = con1.getLastChatTime();
                if (LastTime1 == 0 || LastTime2 == 0) {
                    return -1;
                }
                if (LastTime2 == LastTime1) {
                    return 0;
                } else if (LastTime2 > LastTime1) {
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
        if (AccountManager.getInstance().isLogin())
            loadConversation();
        if (AccountManager.getInstance().isLogin()) {
            if (!hidden) {
                refresh();
            }
            loadConversation();
            if (listView.getAdapter().getCount() <= 0) {
                //setEmptyView();
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
                    String chatType = data.getStringExtra("chatType");
                    String toName = data.getStringExtra("toName");
                    long id = data.getLongExtra("toId", 0);
                    if (chatType != null) {
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        // it is group chat
                        intent.putExtra("chatType", chatType);
                        intent.putExtra("friend_id", String.valueOf(id));
                        intent.putExtra("Name", toName);
                        startActivity(intent);
                    }
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        CommonUtils.fixInputMethodManagerLeak(getActivity());
    }


}
