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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.utils.SharedPreferencesUtil;
import com.aizou.core.widget.SideBar;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.config.Constant;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.my.ModifyNicknameActivity;
import com.xuejian.client.lxp.module.toolbox.HisMainPageActivity;
import com.xuejian.client.lxp.module.toolbox.im.adapter.ContactAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


/**
 * 联系人列表页
 */
public class ContactlistFragment extends Fragment {
    private ContactAdapter adapter;
    private List<User> contactList;
    private ListView listView;
    private SideBar indexBar;
    private TextView indexDialogTv;
    private boolean hidden;
    private boolean isAddFriend;
    CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAddFriend = getArguments().getBoolean("isAddFriend");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            return;
        }
        listView = (ListView) getView().findViewById(R.id.id_stickynavlayout_innerscrollview);
        View footView = new View(getActivity());
        AbsListView.LayoutParams abp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        abp.height = 400;
        footView.setLayoutParams(abp);
        listView.addFooterView(footView);
        indexBar = (SideBar) getView().findViewById(R.id.sb_index);
        indexDialogTv = (TextView) getView().findViewById(R.id.dialog);
        indexBar.setTextView(indexDialogTv);
        indexBar.setTextColor(getResources().getColor(R.color.app_theme_color));
        contactList = new ArrayList<User>();
        // 获取设置contactlist
        getContactList();
        // 设置adapter
//        adapter = new ContactAdapter(getActivity(), R.layout.row_contact, contactList);
//        listView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
        indexBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForIndex(s);
                if (position != -1) {
                    listView.setSelection(position);
                }
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getCount() <= position) {
                    return;
                }
                try {
                    String username = adapter.getItem(position).getNickName();
                    if (Constant.NEW_FRIENDS_USERNAME.equals(username)) {
                        startActivity(new Intent(getActivity(), AddContactActivity.class));
                        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    } else if ("item_friends_request".equals(username)) {
                        //MobclickAgent.onEvent(getActivity(), "cell_item_new_friends_request");
                        startActivity(new Intent(getActivity(), NewFriendsMsgActivity.class));
                        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    } else if (Constant.GROUP_USERNAME.equals(username)) {
                        // 进入群聊列表页面
                        //           startActivity(new Intent(getActivity(), GroupsActivity.class));
                        //            getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    } else {
                        startActivity(new Intent(getActivity(), HisMainPageActivity.class).putExtra("userId", adapter.getItem(position).getUserId()));
                        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(android.view.ContextMenu menu, View v, android.view.ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (((AdapterView.AdapterContextMenuInfo) menuInfo).position > 0) {
            getActivity().getMenuInflater().inflate(R.menu.edit_memo, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_message) {
            int pos = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
            User user = adapter.getItem(pos);
            Intent intent = new Intent(getActivity(), ModifyNicknameActivity.class);
            intent.putExtra("isEditMemo", true);
            intent.putExtra("nickname", user.getNickName());
            intent.putExtra("userId", String.valueOf(user.getUserId()));
            startActivity(intent);
            return true;
        }
        return super.onContextItemSelected(item);
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
        //MobclickAgent.onPageStart("page_friends_lists");
        boolean needfresh = SharedPreferencesUtil.getBooleanValue(getActivity(), "contactNeedRefresh", false);
        if (!hidden || needfresh) {
            SharedPreferencesUtil.saveValue(getActivity(), "contactNeedRefresh", false);
            refresh();
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        //MobclickAgent.onPageEnd("page_friends_lists");
    }

    @Override
    public void onDestroy() {
        if (this.compositeSubscription.hasSubscriptions()) this.compositeSubscription.clear();
        super.onDestroy();
    }


    // 刷新ui
    public void refresh() {
        try {
            // 可能会在子线程中调到这方法
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    getContactList();
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getContactList() {
        Map<Long, User> users = AccountManager.getInstance().getContactList(getActivity());

        if (users == null) {
            return;
        }
      //  DialogManager.getInstance().showModelessLoadingDialog(getActivity());
        compositeSubscription.add(
                Observable.create(new Observable.OnSubscribe<List<User>>() {
                    @Override
                    public void call(Subscriber<? super List<User>> subscriber) {
                        try {
                            subscriber.onNext(UserDBManager.getInstance().getContactListWithoutGroup());
                        }catch (Exception e){
                            subscriber.onError(e);
                        }

                        subscriber.onCompleted();
                    }
                })
                        .flatMap(new Func1<List<User>, Observable<User>>() {
                            @Override
                            public Observable<User> call(List<User> conversationBeans) {
                                return Observable.from(conversationBeans);
                            }
                        })
                        .filter(new Func1<User, Boolean>() {
                            @Override
                            public Boolean call(User user) {
                                return !(user.getUserId() == 10001 || user.getUserId() == 10002 || user.getUserId() == 10003);
                            }
                        })
                        .toList()
                        .map(new Func1<List<User>, List<User>>() {
                            @Override
                            public List<User> call(List<User> contacts) {

                                Collections.sort(contacts, new Comparator<User>() {

                                    @Override
                                    public int compare(User lhs, User rhs) {
                                        return lhs.getHeader().compareTo(rhs.getHeader());
                                    }
                                });
                                return contacts;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<User>>() {
                            @Override
                            public void call(List<User> contacts) {
                                contactList.clear();
                                contactList.addAll(contacts);
                                User friendRequest = new User();
                                friendRequest.setUserId(3);
                                friendRequest.setNickName("item_friends_request");
                                friendRequest.setType(1);
                                UserDBManager.getInstance().saveContact(friendRequest);
                                contactList.add(0, friendRequest);
                                if (isAddFriend) {
                                    User newFriends = new User();
                                    newFriends.setUserId(2);
                                    newFriends.setNickName("item_new_friends");
                                    newFriends.setType(1);
                                    UserDBManager.getInstance().saveContact(newFriends);
                                    contactList.add(1, newFriends);
                                }
                                adapter = new ContactAdapter(getActivity(), R.layout.row_contact, contactList);
                                listView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                //   DialogManager.getInstance().dissMissModelessLoadingDialog();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        }));


//        contactList.clear();
//        contactList.addAll(UserDBManager.getInstance().getContactListWithoutGroup());
//        List<User> del = new ArrayList<>();
//        for (User user : contactList) {
//            if (user.getUserId() == 10000 || user.getUserId() == 10001|| user.getUserId() == 10002|| user.getUserId() == 10003) del.add(user);
//        }
//        contactList.removeAll(del);
//        // 排序
//        Collections.sort(contactList, new Comparator<User>() {
//
//            @Override
//            public int compare(User lhs, User rhs) {
//                return lhs.getHeader().compareTo(rhs.getHeader());
//            }
//        });
////		// 加入"申请与通知"和"群聊"
//        // 把"申请与通知"添加到首位
//        User friendRequest = new User();
//        friendRequest.setUserId(3);
//        friendRequest.setNickName("item_friends_request");
//        friendRequest.setType(1);
//        UserDBManager.getInstance().saveContact(friendRequest);
//        contactList.add(0, friendRequest);
//        if (isAddFriend) {
//            User newFriends = new User();
//            newFriends.setUserId(2);
//            newFriends.setNickName("item_new_friends");
//            newFriends.setType(1);
//            UserDBManager.getInstance().saveContact(newFriends);
//            contactList.add(1, newFriends);
//        }

    }

}
