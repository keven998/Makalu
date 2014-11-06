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

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.BaseChatActivity;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.widget.ExpandGridView;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.NetUtils;

public class GroupDetailsActivity extends BaseChatActivity implements OnClickListener {
	private static final String TAG = "GroupDetailsActivity";
	private static final int REQUEST_CODE_ADD_USER = 0;
	private static final int REQUEST_CODE_EXIT = 1;
	private static final int REQUEST_CODE_EXIT_DELETE = 2;
	private static final int REQUEST_CODE_CLEAR_ALL_HISTORY=3;
    private static final int REQUEST_CODE_MODIFY_GROUP_NAME = 4;
	
//	private ExpandGridView userGridview;
	private String groupId;
	private ProgressBar loadingPB;
	private Button exitBtn;
	private Button deleteBtn;
	private EMGroup group;
//	private GridAdapter adapter;
	private int referenceWidth;
	private int referenceHeight;
	private ProgressDialog progressDialog;
	
	private RelativeLayout rl_switch_block_groupmsg;
    private LinearLayout rl_groupName;
    private TextView groupNameTv;
	/**
	 * 屏蔽群消息imageView
	 */
	private ImageView iv_switch_block_groupmsg;
	/**
	 * 关闭屏蔽群消息imageview
	 */
	private ImageView iv_switch_unblock_groupmsg;
	
	public static GroupDetailsActivity instance;
	
	//清空所有聊天记录
	private RelativeLayout clearAllHistory;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_details);
		instance = this;
		clearAllHistory=(RelativeLayout) findViewById(R.id.clear_all_history);
//		userGridview = (ExpandGridView) findViewById(R.id.gridview);
		loadingPB = (ProgressBar) findViewById(R.id.progressBar);
		exitBtn = (Button) findViewById(R.id.btn_exit_grp);
		deleteBtn = (Button) findViewById(R.id.btn_exitdel_grp);

        rl_groupName = (LinearLayout) findViewById(R.id.ll_group_name);
        groupNameTv = (TextView) findViewById(R.id.tv_groupName);
		
		rl_switch_block_groupmsg = (RelativeLayout)findViewById(R.id.rl_switch_block_groupmsg);
		iv_switch_block_groupmsg = (ImageView) findViewById(R.id.iv_switch_block_groupmsg);
		iv_switch_unblock_groupmsg = (ImageView) findViewById(R.id.iv_switch_unblock_groupmsg);
		
		rl_switch_block_groupmsg.setOnClickListener(this);

		Drawable referenceDrawable = getResources().getDrawable(R.drawable.smiley_add_btn);
		referenceWidth = referenceDrawable.getIntrinsicWidth();
		referenceHeight = referenceDrawable.getIntrinsicHeight();

		// 获取传过来的groupid
		groupId = getIntent().getStringExtra("groupId");
		group = EMGroupManager.getInstance().getGroup(groupId);
        bindView();


//		adapter = new GridAdapter(this, R.layout.grid, group.getMembers());
//		userGridview.setAdapter(adapter);

		// 保证每次进详情看到的都是最新的group
		updateGroup();

		// 设置OnTouchListener
//		userGridview.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				switch (event.getAction()) {
//				case MotionEvent.ACTION_DOWN:
//					if (adapter.isInDeleteMode) {
//						adapter.isInDeleteMode = false;
//						adapter.notifyDataSetChanged();
//						return true;
//					}
//					break;
//				default:
//					break;
//				}
//				return false;
//			}
//		});
		
		clearAllHistory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(GroupDetailsActivity.this, AlertDialog.class);
				intent.putExtra("cancel",true);
				intent.putExtra("titleIsCancel", true);
				intent.putExtra("msg","确定清空此群的聊天记录吗？");
				startActivityForResult(intent, REQUEST_CODE_CLEAR_ALL_HISTORY);
			}
		});
		
		
	}

	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_ADD_USER:// 添加群成员
				final String[] newmembers = data.getStringArrayExtra("newmembers");
				addMembersToGroup(newmembers);

				break;
			case REQUEST_CODE_EXIT: // 退出群
				progressDialog.setMessage("正在退出群聊...");
				exitGrop();
				break;
			case REQUEST_CODE_EXIT_DELETE: // 解散群
				progressDialog.setMessage("正在解散群聊...");
				deleteGrop();
				break;
			case REQUEST_CODE_CLEAR_ALL_HISTORY:
				//清空此群聊的聊天记录
				progressDialog.setMessage("正在清空群消息...");
				clearGroupHistory();
				break;
                case REQUEST_CODE_MODIFY_GROUP_NAME: // 修改群名称
                group = EMGroupManager.getInstance().getGroup(groupId);
                groupNameTv.setText(group.getGroupName());
                break;
			default:
				break;
			}
		}
	}

	/**
	 * 点击退出群组按钮
	 * 
	 * @param view
	 */
	public void exitGroup(View view) {
		startActivityForResult(new Intent(this, ExitGroupDialog.class), REQUEST_CODE_EXIT);

	}

	/**
	 * 点击解散群组按钮
	 * 
	 * @param view
	 */
	public void exitDeleteGroup(View view) {
		startActivityForResult(new Intent(this, ExitGroupDialog.class).putExtra("deleteToast", getString(R.string.dissolution_group_hint)),
				REQUEST_CODE_EXIT_DELETE);

	}

	
	
	
	/**
	 * 清空群聊天记录
	 */
	public void clearGroupHistory(){
		
		
		EMChatManager.getInstance().clearConversation(group.getGroupId());
		progressDialog.dismiss();
//		adapter.refresh(EMChatManager.getInstance().getConversation(toChatUsername));
		
		
		
	}
	
	
	/**
	 * 退出群组
	 * 
	 */
	private void exitGrop() {
		new Thread(new Runnable() {
			public void run() {
				try {
                    EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                    msg.setChatType(EMMessage.ChatType.GroupChat);
                    msg.setFrom(AccountManager.getInstance().getLoginAccount(mContext).easemobUser);
                    msg.setReceipt(group.getGroupId());
                    IMUtils.setMessageWithTaoziUserInfo(mContext, msg);
                    String myNickname = AccountManager.getInstance().getLoginAccount(mContext).nickName;
                    String content = myNickname+" 退出了群聊";
                    IMUtils.setMessageWithExtTips(mContext,msg,content);
                    msg.addBody(new TextMessageBody(content));
                    EMChatManager.getInstance().sendGroupMessage(msg,new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            try {
                                EMGroupManager.getInstance().exitFromGroup(groupId);
                            } catch (EaseMobException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    setResult(RESULT_OK);
                                    finish();
                                    ChatActivity.activityInstance.finish();
                                }
                            });
                        }

                        @Override
                        public void onError(int i, String s) {

                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });

				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), "退出群聊失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 解散群组
	 * 
	 */
	private void deleteGrop() {
		new Thread(new Runnable() {
			public void run() {
				try {
					EMGroupManager.getInstance().exitAndDeleteGroup(groupId);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							setResult(RESULT_OK);
							finish();
							ChatActivity.activityInstance.finish();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), "解散群聊失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 增加群成员
	 * 
	 * @param newmembers
	 */
	private void addMembersToGroup(final String[] newmembers) {
		new Thread(new Runnable() {

			public void run() {
				try {
					//创建者调用add方法
					if(EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())){
						EMGroupManager.getInstance().addUsersToGroup(groupId, newmembers);
					}else{
						//一般成员调用invite方法
						EMGroupManager.getInstance().inviteUser(groupId, newmembers, null);
					}
					runOnUiThread(new Runnable() {
						public void run() {
//							adapter.notifyDataSetChanged();
							((TextView) findViewById(R.id.group_name)).setText(group.getGroupName()+"("+group.getAffiliationsCount()+"人)");
							progressDialog.dismiss();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), "添加群成员失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();
	}


    private void bindView(){
// 如果自己是群主，显示解散按钮
        if(group.getOwner() == null || "".equals(group.getOwner())){
            exitBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
        }
        if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
            exitBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.VISIBLE);
            rl_groupName.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,ModifyGroupNameActivity.class);
                    intent.putExtra("groupId",groupId);
                    startActivityForResult(intent,REQUEST_CODE_MODIFY_GROUP_NAME);
                }
            });
        }
        ((TextView) findViewById(R.id.group_name)).setText("聊天信息"+"("+group.getAffiliationsCount()+"人)");
        groupNameTv.setText(group.getGroupName());
        //update block
        System.out.println("group msg is blocked:" + group.getMsgBlocked());
        if (group.getMsgBlocked()) {
            iv_switch_block_groupmsg.setVisibility(View.VISIBLE);
            iv_switch_unblock_groupmsg.setVisibility(View.INVISIBLE);
        } else {
            iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
            iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);
        }
    }
	
	protected void updateGroup() {
		new Thread(new Runnable() {
			public void run() {
				try {
					group = EMGroupManager.getInstance().getGroupFromServer(groupId);
					//更新本地数据
					EMGroupManager.getInstance().createOrUpdateLocalGroup(group);
					
					runOnUiThread(new Runnable() {
						public void run() {
							((TextView) findViewById(R.id.group_name)).setText(group.getGroupName()+"("+group.getAffiliationsCount()+"人)");
							loadingPB.setVisibility(View.INVISIBLE);
                            bindView();

						}
					});

				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							loadingPB.setVisibility(View.INVISIBLE);
						}
					});
				}
			}
		}).start();
	}

	public void back(View view) {
		setResult(RESULT_OK);
		finish();
	}



	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_switch_block_groupmsg:
			if (iv_switch_block_groupmsg.getVisibility() == View.VISIBLE) {
				System.out.println("change to unblock group msg");
				try {
				    EMGroupManager.getInstance().unblockGroupMessage(groupId);
				    iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
					iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					e.printStackTrace();
					//todo: 显示错误给用户
				}
			} else {
				System.out.println("change to block group msg");
				try {
				    EMGroupManager.getInstance().blockGroupMessage(groupId);
				    iv_switch_block_groupmsg.setVisibility(View.VISIBLE);
					iv_switch_unblock_groupmsg.setVisibility(View.INVISIBLE);
				} catch (Exception e) {
					e.printStackTrace();
					//todo: 显示错误给用户
				}
			}
			break;
			default:
		}
		
	}
	
	
	
	
}
