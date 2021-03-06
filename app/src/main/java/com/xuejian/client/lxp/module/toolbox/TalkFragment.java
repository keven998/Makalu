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
import android.widget.AbsListView;
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
import com.xuejian.client.lxp.common.dialog.DialogManager;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class TalkFragment extends PeachBaseFragment {
    public static final int NEW_CHAT_REQUEST_CODE = 101;
    private static final int Edit_CHAT_REQUEST_CODE = 102;
    @Bind(R.id.tv_title_add)
    TextView tvTitleAdd;
    @Bind(R.id.unread_address_number)
    TextView unreadAddressNumber;
    @Bind(R.id.btn_container_address_list)
    RelativeLayout btnContainerAddressList;
    @Bind(R.id.tv_title_bar_title)
    TextView title_bar_title;
    private InputMethodManager inputMethodManager;
    private ListView listView;
    private ChatAllHistoryAdapter adapter;
    private boolean hidden;
    private List<ConversationBean> conversations = new ArrayList<>();
    private List<ConversationBean> tempConversations = new ArrayList<>();
    private ConversationBean curconversation;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_talk, null);
        ButterKnife.bind(this, rootView);
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
    public void onDestroy() {
        if (this.compositeSubscription.hasSubscriptions()) this.compositeSubscription.clear();
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (ListView) getView().findViewById(R.id.list);
        adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversations);
        View view = new View(getActivity());
        AbsListView.LayoutParams abp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        abp.height = 200;
        view.setLayoutParams(abp);
        view.setClickable(false);
        listView.addFooterView(view);
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
                //MobclickAgent.onEvent(getActivity(), "navigation_item_my_friends");
                Intent intent = new Intent(getActivity(), ContactActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getCount() <= position) return;
                curconversation = adapter.getItem(position);
                if (String.valueOf(curconversation.getFriendId()).equals(AccountManager.getCurrentUserId()))
                    ToastUtil.getInstance(getActivity()).showToast("还不支持自己聊");
                else {
                    if (curconversation.getFriendId() == 10000) {
                        //MobclickAgent.onEvent(getActivity(), "cell_item_paipai");
                    } else if (curconversation.getFriendId() == 10001) {
                        //MobclickAgent.onEvent(getActivity(), "cell_item_wenwen");
                    }

                    // 进入聊天页面
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("friend_id", curconversation.getFriendId() + "");
                    intent.putExtra("chatType", curconversation.getChatType());
                    if (curconversation.getConversation() != null) {
                        intent.putExtra("conversation", curconversation.getConversation());
                    }
                    if (curconversation.getHASH() != null) {
                        intent.putExtra("name", curconversation.getHASH());
                    }
                    startActivityForResult(intent, Edit_CHAT_REQUEST_CODE);
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
        if (AccountManager.getInstance().getLoginAccount(getActivity()) == null) {
            return;
        }
  //      DialogManager.getInstance().showModelessLoadingDialog(getActivity());
     //   loadConversation();
    }

    private void showActionDialog() {
        //MobclickAgent.onEvent(getActivity(), "navigation_item_talks_menu");
        String[] names = {"新建聊天/群聊", "添加朋友", "取消"};
        final MoreDialog dialog = new MoreDialog(getActivity());
        dialog.setMoreStyle(false, 3, names);
        dialog.getTv1().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), PickContactsWithCheckboxActivity.class).putExtra("request", NEW_CHAT_REQUEST_CODE), NEW_CHAT_REQUEST_CODE);
                getActivity().overridePendingTransition(R.anim.push_bottom_in, 0);
                dialog.dismiss();
            }
        });
        dialog.getTv3().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        if (((AdapterView.AdapterContextMenuInfo) menuInfo).position >= 0) {
            getActivity().getMenuInflater().inflate(R.menu.delete_message, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_message) {
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
        if (AccountManager.getInstance().getLoginAccount(getActivity()) == null) return;
        // loadConversationsWithRecentChat();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        updateUnreadAddressLable();
        ((MainActivity) getActivity()).updateUnreadMsgCount();
    }

    public void loadConversation() {
 //       List<ConversationBean> del = new ArrayList<>();
 //       conversations.clear();
//        tempConversations.clear();
//        try {
//            tempConversations.addAll(IMClient.getInstance().getConversationList());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        conversations.add(0, new ConversationBean(10, 0, "single"));
//        conversations.add(1, new ConversationBean(11, 0, "single"));
//        for (ConversationBean bean : tempConversations) {
//            if (bean.getFriendId() == 10000) {
//                conversations.set(1, bean);
//                del.add(bean);
//            }
//            if (bean.getFriendId() == 10001) {
//                conversations.set(0, bean);
//                del.add(bean);
//            }
//        }
//        tempConversations.removeAll(del);
//        sortConversationByLastChatTime(tempConversations);
//        if (conversations.get(0).getFriendId() == 10) {
//            conversations.set(0, new ConversationBean(10001, 0, "single"));
//        }
//        if (conversations.get(1).getFriendId() == 11) {
//            conversations.set(1, new ConversationBean(10000, 0, "single"));
//        }
//        conversations.addAll(tempConversations);
//        refresh();

        final ConversationBean[] temp = new ConversationBean[2];
        compositeSubscription.add(
                Observable.create(new Observable.OnSubscribe<List<ConversationBean>>() {
                    @Override
                    public void call(Subscriber<? super List<ConversationBean>> subscriber) {
                        subscriber.onNext(IMClient.getInstance().getConversationList());
                        subscriber.onCompleted();
                    }
                })
                        .subscribeOn(Schedulers.io())
//                        .doOnSubscribe(new Action0() {
//                            @Override
//                            public void call() {
//                                System.out.println("doOnSubscribe " + Thread.currentThread().getName());
//                     //           DialogManager.getInstance().showLoadingDialog(getActivity());
//                            }
//                        })
//                        .subscribeOn(AndroidSchedulers.mainThread())
//                        .observeOn(Schedulers.io())
//                        .flatMap(new Func1<List<ConversationBean>, Observable<ConversationBean>>() {
//                            @Override
//                            public Observable<ConversationBean> call(List<ConversationBean> conversationBeans) {
//                                System.out.println("flatMap "+Thread.currentThread().getName()+" "+conversationBeans.size());
//                                return Observable.from(conversationBeans);
//                            }
//                        })
//                        .filter(new Func1<ConversationBean, Boolean>() {
//                            @Override
//                            public Boolean call(ConversationBean conversationBean) {
//                                System.out.println("filter "+Thread.currentThread().getName()+" "+conversationBean.getFriendId());
//                                if (conversationBean.getFriendId() == 10001) {
//                                    temp[0] = conversationBean;
//                                    return false;
//                                } else if (conversationBean.getFriendId() == 10000) {
//                                    temp[1] = conversationBean;
//                                    return false;
//                                } else return true;
//                            }
//                        })
//                        .toList()
//                        .map(new Func1<List<ConversationBean>, List<ConversationBean>>() {
//                            @Override
//                            public List<ConversationBean> call(List<ConversationBean> conversationBeans) {
//                                sortConversationByLastChatTime(conversationBeans);
//                                List<ConversationBean> tempList = new ArrayList<ConversationBean>();
//                                if (temp[0] != null) {
//                                    tempList.add(0, temp[0]);
//                                } else {
//                                    tempList.add(0, new ConversationBean(10001, 0, "single"));
//                                }
//                                if (temp[1] != null) {
//                                    tempList.add(1, temp[1]);
//                                } else {
//                                    tempList.add(1, new ConversationBean(10000, 0, "single"));
//                                }
//                                tempList.addAll(conversationBeans);
//                                System.out.println("map "+Thread.currentThread().getName()+" "+tempList.size());
//                                return tempList;
//                            }
//                        })
                        .map(new Func1<List<ConversationBean>, List<ConversationBean>>() {
                            @Override
                            public List<ConversationBean> call(List<ConversationBean> conversationBeans) {
                                sortConversationByLastChatTime(conversationBeans);
                                return conversationBeans;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<ConversationBean>>() {
                            @Override
                            public void call(List<ConversationBean> conversationBeans) {
                                conversations.clear();
                                conversations.addAll(conversationBeans);
                                refresh();
                                DialogManager.getInstance().dissMissModelessLoadingDialog();
                            }
                        }));
    }

    public void updateUnreadAddressLable() {
        compositeSubscription.add(Observable.just(getUnreadAddressCountTotal())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        if (integer > 0) {
                            unreadAddressNumber.setText(String.valueOf(integer));
                            unreadAddressNumber.setVisibility(View.VISIBLE);
                        } else {
                            unreadAddressNumber.setVisibility(View.GONE);
                        }
                    }
                }));

        //     int count = getUnreadAddressCountTotal();
        //  int count=IMClient.getInstance().getUnReadCount();

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
    private static void sortConversationByLastChatTime(List<ConversationBean> conversationList) {
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
        MobclickAgent.onPageStart("page_chatList");
        MobclickAgent.onResume(getActivity());
        //MobclickAgent.onPageStart("page_home_talk_lists");
        //返回页面的动画样式
        //getActivity().overridePendingTransition(R.anim.push_bottom_out,R.anim.push_bottom_in);
        if (!hidden) {
            refresh();
        }
        if (AccountManager.getInstance().getLoginAccount(getActivity()) != null) {
            loadConversation();
            if (!hidden) {
                refresh();
            }
            if (listView.getAdapter().getCount() <= 0) {
                //setEmptyView();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("page_chatList");
        MobclickAgent.onPause(getActivity());
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
                    break;
                case Edit_CHAT_REQUEST_CODE:
                    if (data != null && curconversation != null) {
                        String groupName = data.getStringExtra("changedTitle");
                        if (groupName != null) {
                            refresh();
                        }
                    }
                    break;
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        CommonUtils.fixInputMethodManagerLeak(getActivity());
    }


}
