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
package com.aizou.peachtravel.module.toolbox.im;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.aizou.core.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.bean.ModifyResult;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.widget.TopSectionBar;
import com.aizou.peachtravel.config.Constant;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.aizou.peachtravel.db.respository.InviteMsgRepository;
import com.aizou.peachtravel.module.toolbox.im.adapter.ContactAdapter;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;

/**
 * 联系人列表页
 * 
 */
public class ContactlistFragment extends Fragment {
	private ContactAdapter adapter;
	private List<IMUser> contactList;
	private ListView listView;
	private boolean hidden;
	private TopSectionBar sectionBar;
	private InputMethodManager inputMethodManager;
    private View emptyView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_contact_list, container, false);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		listView = (ListView) getView().findViewById(R.id.list);
        sectionBar = (TopSectionBar) getView().findViewById(R.id.section_bar);
		contactList = new ArrayList<IMUser>();
		// 获取设置contactlist
		getContactList();
		// 设置adapter
		adapter = new ContactAdapter(getActivity(), R.layout.row_contact, contactList);
        listView.setAdapter(adapter);
        sectionBar.setListView(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String username = adapter.getItem(position).getUsername();
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
					startActivity(new Intent(getActivity(), ContactDetailActivity.class).putExtra("userId", adapter.getItem(position).getUserId()));
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
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// 长按前两个不弹menu
//		if (((AdapterContextMenuInfo) menuInfo).position > 0) {
//			getActivity().getMenuInflater().inflate(R.menu.context_contact_list, menu);
//		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_contact) {
			IMUser tobeDeleteUser = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
			// 删除此联系人
			deleteContact(tobeDeleteUser);
			// 删除相关的邀请消息
            InviteMsgRepository.deleteInviteMsg(getActivity(),tobeDeleteUser.getUsername());
			return true;
		}else if(item.getItemId() == R.id.add_to_blacklist){
			IMUser user = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
			moveToBlacklist(user.getUsername());
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
		if (!hidden) {
			refresh();
		}
        if (contactList.size() <= 1 && emptyView == null) {
            emptyView = getActivity().findViewById(R.id.empty_view);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.findViewById(R.id.add_friend).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //todo
                }
            });
        } else if (contactList.size() > 1) {
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
                emptyView = null;
            }
        }
	}

	/**
	 * 删除联系人
	 * 
	 * @param tobeDeleteUser
	 */
	public void deleteContact(final IMUser tobeDeleteUser) {
//		final ProgressDialog pd = new ProgressDialog(getActivity());
//		pd.setMessage("正在删除...");
//		pd.setCanceledOnTouchOutside(false);
//		pd.show();
        DialogManager.getInstance().showProgressDialog(getActivity(),"正在删除...");
        UserApi.deleteContact(tobeDeleteUser.getUserId()+"",new HttpCallBack() {
            @Override
            public void doSucess(Object result, String method) {
                DialogManager.getInstance().dissMissProgressDialog();
                CommonJson<ModifyResult> deleteResult = CommonJson.fromJson((String) result,ModifyResult.class);
                if(deleteResult.code==0){
                    IMUserRepository.deleteContact(getActivity(),tobeDeleteUser.getUsername());
                    // 删除此会话
                    EMChatManager.getInstance().deleteConversation(tobeDeleteUser.getUsername());
                    AccountManager.getInstance().getContactList(getActivity()).remove(tobeDeleteUser.getUsername());
                    adapter.remove(tobeDeleteUser);
                    adapter.notifyDataSetChanged();
                    if(((IMMainActivity)getActivity())!=null){
                        ((IMMainActivity)getActivity()).refreshChatHistoryFragment();
                    }
                }else if(!TextUtils.isEmpty(deleteResult.err.message)){
                    ToastUtil.getInstance(getActivity()).showToast(deleteResult.err.message);
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissProgressDialog();
                Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
            }
        });
//        new Thread(new Runnable() {
//			public void run() {
//				try {
//					EMContactManager.getInstance().deleteContact(tobeDeleteUser.getUsername());
//					// 删除db和内存中此用户的数据
//					UserDao dao = new UserDao(getActivity());
//					dao.deleteContact(tobeDeleteUser.getUsername());
//					AccountManager.getInstance().getContactList(getActivity()).remove(tobeDeleteUser.getUsername());
//					getActivity().runOnUiThread(new Runnable() {
//						public void run() {
//							pd.dismiss();
//							adapter.remove(tobeDeleteUser);
//							adapter.notifyDataSetChanged();
//
//						}
//					});
//				} catch (final Exception e) {
//					getActivity().runOnUiThread(new Runnable() {
//						public void run() {
//							pd.dismiss();
//							Toast.makeText(getActivity(), "删除失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//						}
//					});
//
//				}
//
//			}
//		}).start();

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
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getActivity(), "移入黑名单成功", Toast.LENGTH_SHORT).show();
						}
					});
				} catch (EaseMobException e) {
					e.printStackTrace();
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getActivity(), "移入黑名单失败", Toast.LENGTH_SHORT).show();
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
    private String[] sections = new String[]{"#","A","B","C","D","E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	private void getContactList() {
		contactList.clear();
		Map<String, IMUser> users = AccountManager.getInstance().getContactList(getActivity());
		Iterator<Map.Entry<String, IMUser>> iterator = users.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, IMUser> entry = iterator.next();
			if (!entry.getKey().equals(Constant.NEW_FRIENDS_USERNAME) && !entry.getKey().equals(Constant.GROUP_USERNAME)) {
                contactList.add(entry.getValue());
            }
		}
//        for(int i=0;i<24;i++){
//            IMUser user = new IMUser();
//            user.setAvatar("");
//            user.setNick(sections[i]+"阮金明");
//            user.setUsername("rjm");
//            user.setUserId("1111");
//            if (Character.isDigit(user.getNick().charAt(0))) {
//                user.setHeader("#");
//            } else {
//                user.setHeader(HanziToPinyin.getInstance().get(user.getNick().substring(0, 1))
//                        .get(0).target.substring(0, 1).toUpperCase());
//                char header = user.getHeader().toLowerCase().charAt(0);
//                if (header < 'a' || header > 'z') {
//                    user.setHeader("#");
//                }
//            }
//            contactList.add(user);
//            contactList.add(user);
//            contactList.add(user);
//            contactList.add(user);
//        }

		// 排序
		Collections.sort(contactList, new Comparator<IMUser>() {

			@Override
			public int compare(IMUser lhs, IMUser rhs) {
				return lhs.getHeader().compareTo(rhs.getHeader());
			}
		});

//		// 加入"申请与通知"和"群聊"
//		contactList.add(0, users.get(Constant.GROUP_USERNAME));
		// 把"申请与通知"添加到首位
		contactList.add(0, users.get(Constant.NEW_FRIENDS_USERNAME));

	}
}
