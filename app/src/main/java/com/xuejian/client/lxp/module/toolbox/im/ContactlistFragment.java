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
package com.xuejian.client.lxp.module.toolbox.im;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.widget.SideBar;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.config.Constant;
import com.xuejian.client.lxp.db.IMUser;
import com.xuejian.client.lxp.db.userDB.User;
import com.xuejian.client.lxp.db.userDB.UserDBManager;
import com.xuejian.client.lxp.module.toolbox.HisMainPageActivity;
import com.xuejian.client.lxp.module.toolbox.im.adapter.ContactAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 联系人列表页
 * 
 */
public class ContactlistFragment extends Fragment {
	private ContactAdapter adapter;
	private List<User> contactList;
	private ListView listView;
    private SideBar indexBar;
    private TextView indexDialogTv;
	private boolean hidden;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_contact_list, container, false);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if(savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
//		inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		listView = (ListView) getView().findViewById(R.id.list);
        indexBar = (SideBar) getView().findViewById(R.id.sb_index);
        indexDialogTv = (TextView) getView().findViewById(R.id.dialog);
        //search = (EditText) getView().findViewById(R.id.contact_search_tv);
        /*InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);*/
        indexBar.setTextView(indexDialogTv);
        indexBar.setTextColor(getResources().getColor(R.color.app_theme_color_secondary));
        contactList = new ArrayList<User>();
		// 获取设置contactlist
		getContactList();
		// 设置adapter
		adapter = new ContactAdapter(getActivity(), R.layout.row_contact, contactList);
        listView.setAdapter(adapter);
        indexBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForIndex(s);
                if (position != -1) {
                    listView.setSelection(position+1);
                }
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String username = adapter.getItem(position).getNickName();
				if (Constant.NEW_FRIENDS_USERNAME.equals(username)) {
					// 进入申请与通知页面
					IMUser user = AccountManager.getInstance().getContactList(getActivity()).get(Constant.NEW_FRIENDS_USERNAME);
					user.setUnreadMsgCount(0);
					startActivity(new Intent(getActivity(), NewFriendsMsgActivity.class));
				} else if (Constant.GROUP_USERNAME.equals(username)) {
					// 进入群聊列表页面
					startActivity(new Intent(getActivity(), GroupsActivity.class));
				} else {
					// demo中直接进入聊天页面，实际一般是进入用户详情页
					startActivity(new Intent(getActivity(), HisMainPageActivity.class).putExtra("userId", adapter.getItem(position).getUserId().intValue()));
				}
			}
		});


//		listView.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// 隐藏软键盘
//				if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
//					if (getActivity().getCurrentFocus() != null)
//						inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
//								InputMethodManager.HIDE_NOT_ALWAYS);
//				}
//				return false;
//			}
//		});

//		registerForContextMenu(listView);

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
//        MobclickAgent.onPageStart("page_friends_lists");
		if (!hidden) {
			refresh();
		}


	}

    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_friends_lists");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



	/**
	 * 把user移入到黑名单
	 */
	private void moveToBlacklist(final String username){
		final ProgressDialog pd = new ProgressDialog(getActivity());
		pd.setMessage("正在移入黑名单...");
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					//加入到黑名单
					EMContactManager.getInstance().addUserToBlackList(username,true);
                    if (isAdded())
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
//							Toast.makeText(getActivity(), "移入黑名单成功", Toast.LENGTH_SHORT).show();
                            ToastUtil.getInstance(getActivity()).showToast("成功移除她");
						}
					});
				} catch (EaseMobException e) {
					e.printStackTrace();
                    if (isAdded())
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
//							Toast.makeText(getActivity(), "移入黑名单失败", Toast.LENGTH_SHORT).show();
                            ToastUtil.getInstance(getActivity()).showToast("呃~好像找不到网络");
						}
					});
				}
			}
		}).start();
		
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
		Map<String, IMUser> users = AccountManager.getInstance().getContactList(getActivity());
        if(users==null){
            return;
        }
        contactList.clear();
//		Iterator<Map.Entry<String, IMUser>> iterator = users.entrySet().iterator();
//		while (iterator.hasNext()) {
//			Map.Entry<String, IMUser> entry = iterator.next();
//			if (!entry.getKey().equals(Constant.NEW_FRIENDS_USERNAME) && !entry.getKey().equals(Constant.GROUP_USERNAME)) {
//                contactList.add(entry.getValue());
//            }
//		}
        contactList= UserDBManager.getInstance().getContactListWithoutGroup();
		// 排序
		Collections.sort(contactList, new Comparator<User>() {

			@Override
			public int compare(User lhs, User rhs) {
				return lhs.getHeader().compareTo(rhs.getHeader());
			}
		});
//		// 加入"申请与通知"和"群聊"
//		contactList.add(0, users.get(Constant.GROUP_USERNAME));
		// 把"申请与通知"添加到首位
//        IMUser user = users.get(Constant.NEW_FRIENDS_USERNAME);
//        if(user!=null){
//            contactList.add(0, users.get(Constant.NEW_FRIENDS_USERNAME));
//        }


	}
}
