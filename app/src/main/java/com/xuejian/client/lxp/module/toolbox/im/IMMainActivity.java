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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.pagerIndicator.indicator.FixedIndicatorView;
import com.aizou.core.widget.pagerIndicator.indicator.IndicatorViewPager;
import com.aizou.core.widget.pagerIndicator.indicator.slidebar.ColorBar;
import com.aizou.core.widget.pagerIndicator.viewpager.FixedViewPager;
import com.aizou.core.widget.popupmenu.PopupMenuCompat;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.ChatBaseActivity;
import com.xuejian.client.lxp.bean.ContactListBean;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.module.MainActivity;

public class IMMainActivity extends ChatBaseActivity {
    public static final int NEW_CHAT_REQUEST_CODE = 101;

    protected static final String TAG = "MainActivity";
    // 未读消息textview
    private TextView unreadLabel;
    // 未读通讯录textview
    private TextView unreadAddressLable;
    FixedIndicatorView mIMIndicator;
    FixedViewPager mIMViewPager;
    private IndicatorViewPager indicatorViewPager;
    private IMMainAdapter mIMdapter;
    private ContactlistFragment contactListFragment;
    //	private ChatHistoryFragment chatHistoryFragment;
    private ChatAllHistoryFragment chatHistoryFragment;
    private Fragment[] fragments;
    // 当前fragment的index
    private int currentTabIndex;
    // 账号在别处登录
    private boolean isConflict = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im_main);
        initView();
//        ToastUtil.getInstance(this).showToast("IMMainActivity");
        //这个fragment只显示好友和群组的聊天记录
//		chatHistoryFragment = new ChatHistoryFragment();
        //显示所有人消息记录的fragment
        chatHistoryFragment = new ChatAllHistoryFragment();
        contactListFragment = new ContactlistFragment();
        fragments = new Fragment[]{chatHistoryFragment, contactListFragment};
        if (savedInstanceState == null) {
        } else {
            currentTabIndex = savedInstanceState.getInt("currentTabIndex");
        }
        indicatorViewPager = new IndicatorViewPager(mIMIndicator, mIMViewPager);
        indicatorViewPager.setPageOffscreenLimit(2);
        mIMViewPager.setPrepareNumber(2);
        indicatorViewPager.setAdapter(mIMdapter = new IMMainAdapter(getSupportFragmentManager()));
        indicatorViewPager.setOnIndicatorPageChangeListener(new IndicatorViewPager.OnIndicatorPageChangeListener() {
            @Override
            public void onIndicatorPageChange(int preItem, int currentItem) {
                currentTabIndex = currentItem;
            }
        });

        indicatorViewPager.setCurrentItem(currentTabIndex, false);

//        EMGroupManager.getInstance().asyncGetGroupsFromServer(new EMValueCallBack<List<EMGroup>>() {
//            @Override
//            public void onSuccess(List<EMGroup> emGroups) {
//                if (chatHistoryFragment != null) {
//                    chatHistoryFragment.refresh();
//                }
//            }
//
//            @Override
//            public void onError(int i, String s) {
//
//            }
//        });
        //网络更新好友列表
        //getContactFromServer();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        indicatorViewPager.setCurrentItem(0, false);
    }

    public void refreshChatHistoryFragment() {
        if (chatHistoryFragment != null) {
            chatHistoryFragment.refresh();
        }
    }

    public void refreshContactListFragment() {
        if (contactListFragment != null) {
            contactListFragment.refresh();
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        initTitleBar();
        mIMIndicator = (FixedIndicatorView) findViewById(R.id.im_indicator);
        mIMViewPager = (FixedViewPager) findViewById(R.id.im_viewpager);
        ColorBar colorBar = new ColorBar(this, getResources().getColor(R.color.app_theme_color), LocalDisplay.dp2px(5));
        colorBar.setWidth(LocalDisplay.dp2px(45));
        mIMIndicator.setScrollBar(colorBar);

    }

    private void initTitleBar() {
        findViewById(R.id.ly_title_bar_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                BlurMenu fragment = new BlurMenu();
//                Bundle args = new Bundle();
//                args.putInt(SupportBlurDialogFragment.BUNDLE_KEY_BLUR_RADIUS, 5);
//                args.putFloat(SupportBlurDialogFragment.BUNDLE_KEY_DOWN_SCALE_FACTOR, 6);
//                fragment.setArguments(args);
//                fragment.show(getSupportFragmentManager(), "blur_menu");
                showActionDialog();
            }
        });

        findViewById(R.id.ly_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finishWithNoAnim();
                overridePendingTransition(0, android.R.anim.fade_out);
            }
        });
    }

    private void showActionDialog() {
        final Activity act = this;
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        View contentView = View.inflate(act, R.layout.dialog_city_detail_action, null);
        Button btn = (Button) contentView.findViewById(R.id.btn_go_plan);
        btn.setText("新建Talk");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(mContext, "event_create_new_talk");
                startActivityForResult(new Intent(IMMainActivity.this, PickContactsWithCheckboxActivity.class).putExtra("request", NEW_CHAT_REQUEST_CODE), NEW_CHAT_REQUEST_CODE);
                dialog.dismiss();
            }
        });
        Button btn1 = (Button) contentView.findViewById(R.id.btn_go_share);
        btn1.setText("加朋友");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(mContext, "event_add_new_friend");
                startActivity(new Intent(IMMainActivity.this, AddContactActivity.class));
                dialog.dismiss();
            }
        });
        contentView.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        WindowManager windowManager = act.getWindowManager();
        Window window = dialog.getWindow();
        window.setContentView(contentView);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = display.getWidth(); // 设置宽度
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finishWithNoAnim();
        overridePendingTransition(0, R.anim.fade_out);
    }

    private void showMoreMenu(View view) {
        PopupMenuCompat menu = new PopupMenuCompat(this, view);
        menu.inflate(R.menu.menu_im_add_more);
        menu.setOnMenuItemClickListener(new PopupMenuCompat.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_add_friends:
                        startActivity(new Intent(mContext, AddContactActivity.class));
                        break;

                    case R.id.menu_new_message:
                        startActivity(new Intent(mContext, PickContactsWithCheckboxActivity.class).putExtra("request", NEW_CHAT_REQUEST_CODE));
                        break;
                }
                return false;
            }
        });
        menu.show();
    }

    private void getContactFromServer() {

        UserApi.getContact(new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson<ContactListBean> contactResult = CommonJson.fromJson(result, ContactListBean.class);

                if (contactResult.code == 0) {
                   /* IMUserRepository.clearMyFriendsContact(mContext);
                    AccountManager.getInstance().setContactList(null);
                    Map<String, IMUser> userlist = new HashMap<String, IMUser>();
                    // 添加user"申请与通知"
                    IMUser newFriends = new IMUser();
                    newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
                    newFriends.setNick("好友请求");
                    newFriends.setHeader("");
                    newFriends.setIsMyFriends(true);
                    newFriends.setUnreadMsgCount((int) InviteMsgRepository.getUnAcceptMsgCount(mContext));
                    userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
//                    // 添加"群聊"
//                    IMUser groupUser = new IMUser();
//                    groupUser.setUsername(Constant.GROUP_USERNAME);
//                    groupUser.setNick("群聊");
//                    groupUser.setHeader("");
//                    groupUser.setUnreadMsgCount(0);
//                    userlist.put(Constant.GROUP_USERNAME, groupUser);
                    // 存入内存
                    for (PeachUser peachUser : contactResult.result.contacts) {
                        IMUser user = new IMUser();
                        user.setUserId(peachUser.userId);
                        user.setMemo(peachUser.memo);
                        user.setNick(peachUser.nickName);
                        user.setUsername(peachUser.easemobUser);
                        user.setUnreadMsgCount(0);
                        user.setAvatar(peachUser.avatar);
                        user.setAvatarSmall(peachUser.avatarSmall);
                        user.setSignature(peachUser.signature);
                        user.setIsMyFriends(true);
                        user.setGender(peachUser.gender);
                        IMUtils.setUserHead(user);
                        userlist.put(peachUser.easemobUser, user);
                    }
                    // 存入db
                    List<IMUser> users = new ArrayList<IMUser>(userlist.values());
                    IMUserRepository.saveContactList(mContext, users);
                    AccountManager.getInstance().setContactList(userlist);
                    refreshContactListFragment();
                    refreshChatHistoryFragment();*/

                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
//                ToastUtil.getInstance(IMMainActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }


    private class IMMainAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

        public IMMainAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            View view = convertView;
            if (view == null) {
                if (position == 0) {
                    view = View.inflate(mContext, R.layout.tab_im_conversation, null);
                } else {
                    view = View.inflate(mContext, R.layout.tab_im_contact, null);
                }
            }

            if (position == 0) {
                unreadLabel = (TextView) view.findViewById(R.id.unread_msg_notify);
            } else {
                unreadAddressLable = (TextView) view.findViewById(R.id.unread_address_number);
            }

            return view;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            return fragments[position];
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 刷新未读消息数
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
        if (count > 0) {
            unreadLabel.setVisibility(View.VISIBLE);
            unreadLabel.setText(count + "");
        } else {
            unreadLabel.setVisibility(View.GONE);
        }
    }

    /**
     * 刷新申请与通知消息数
     */
    public void updateUnreadAddressLable() {
        runOnUiThread(new Runnable() {
            public void run() {
                int count = getUnreadAddressCountTotal();
                if (count > 0) {
                    unreadAddressLable.setText(String.valueOf(count));
                    unreadAddressLable.setVisibility(View.VISIBLE);
                } else {
                    unreadAddressLable.setVisibility(View.GONE);
                }
            }
        });

    }

    /**
     * 获取未读申请与通知消息
     *
     * @return
     */
    public int getUnreadAddressCountTotal() {
        /*int unreadAddressCountTotal = 0;
        unreadAddressCountTotal = (int) InviteMsgRepository.getUnAcceptMsgCount(this);
        if (AccountManager.getInstance().getContactList(this).get(Constant.NEW_FRIENDS_USERNAME) != null) {

            IMUser imUser = AccountManager.getInstance().getContactList(this).get(Constant.NEW_FRIENDS_USERNAME);
            imUser.setUnreadMsgCount(unreadAddressCountTotal);
            IMUserRepository.saveContact(this, imUser);
        }
        return unreadAddressCountTotal;*/
        return 0;
    }

    /**
     * 获取未读消息数
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        return unreadMsgCountTotal;
    }


//	private BroadcastReceiver offlineMessageReceiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String[] users = intent.getStringArrayExtra("fromuser");
//			String[] groups = intent.getStringArrayExtra("fromgroup");
//			if (users != null) {
//				for (String user : users) {
//					System.out.println("收到user离线消息：" + user);
//				}
//			}
//			if (groups != null) {
//				for (String group : groups) {
//					System.out.println("收到group离线消息：" + group);
//				}
//			}
//			abortBroadcast();
//		}
//	};


    /**
     * 保存提示新消息
     */
    private void notifyNewIviteMessage() {

        // 刷新bottom bar消息未读数
        updateUnreadAddressLable();
        // 刷新好友页面ui
        if (currentTabIndex == 1)
            contactListFragment.refresh();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("currentTabIndex", currentTabIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isConflict) {
            updateUnreadLabel();
            updateUnreadAddressLable();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case NEW_CHAT_REQUEST_CODE:
                    int chatType = data.getIntExtra("chatType", 0);
                    String toId = data.getStringExtra("toId");
                    if (chatType == ChatActivity.CHATTYPE_GROUP) {
                        //进入群聊
                        Intent intent = new Intent(mContext, ChatActivity.class);
                        // it is group chat
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                        intent.putExtra("groupId", toId);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, ChatActivity.class);
                        // it is single chat
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
                        intent.putExtra("userId", toId);
                        startActivity(intent);
                    }


            }
        }

    }

}