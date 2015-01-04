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

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.ChatBaseActivity;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.easemob.EMCallBack;
import com.easemob.analytics.EMMessageCollector;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;

import java.util.ArrayList;
import java.util.List;

public class GroupDetailsActivity extends ChatBaseActivity implements OnClickListener {
	private static final String TAG = "GroupDetailsActivity";
	private static final int REQUEST_CODE_ADD_USER = 0;
	private static final int REQUEST_CODE_EXIT = 1;
	private static final int REQUEST_CODE_EXIT_DELETE = 2;
	private static final int REQUEST_CODE_CLEAR_ALL_HISTORY=3;
    private static final int REQUEST_CODE_MODIFY_GROUP_NAME = 4;
	
//	private ExpandGridView userGridview;
	private String groupId;
	private Button exitBtn;
	private Button deleteBtn;
	private EMGroup group;
    private TitleHeaderBar titleHeaderBar;
//	private GridAdapter adapter;
	private int referenceWidth;
	private int referenceHeight;

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
    private EMChatOptions options ;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_details);
		instance = this;
        titleHeaderBar = (TitleHeaderBar) findViewById(R.id.title_bar);
		clearAllHistory=(RelativeLayout) findViewById(R.id.clear_all_history);
//		userGridview = (ExpandGridView) findViewById(R.id.gridview);
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
        options = EMChatManager.getInstance().getChatOptions();
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
                new MaterialDialog.Builder(GroupDetailsActivity.this)

                        .title(null)
                        .content("确定清空此群的聊天记录吗？")
                        .positiveText("确定")
                        .negativeText("取消")
                        .autoDismiss(false)
                        .positiveColor(getResources().getColor(R.color.app_theme_color))
                        .negativeColor(getResources().getColor(R.color.app_theme_color))
                        .callback(new MaterialDialog.Callback() {
                            @Override
                            public void onPositive(final MaterialDialog dialog) {
                                View progressView = View.inflate(mContext,R.layout.view_progressbar,null);
                                dialog.setContentView(progressView);
                                clearGroupHistory(dialog);

                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                dialog.dismiss();
                            }
                        })
                        .show();
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
        new MaterialDialog.Builder(this)

                .title(null)
                .content("退出后，将不再接收此群聊消息")
                .positiveText("退出")
                .negativeText("取消")
                .autoDismiss(false)
                .positiveColor(getResources().getColor(R.color.app_theme_color))
                .negativeColor(getResources().getColor(R.color.app_theme_color))
                .callback(new MaterialDialog.Callback() {
                    @Override
                    public void onPositive(final MaterialDialog dialog) {
                        View progressView = View.inflate(mContext,R.layout.view_progressbar,null);
                        dialog.setContentView(progressView);
                        exitGroup(dialog);

                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .show();

	}

	/**
	 * 点击解散群组按钮
	 * 
	 * @param view
	 */
	public void exitDeleteGroup(View view) {
        new MaterialDialog.Builder(this)

                .title(null)
                .content(getString(R.string.dissolution_group_hint))
                .positiveText("退出")
                .negativeText("取消")
                .autoDismiss(false)
                .positiveColor(getResources().getColor(R.color.app_theme_color))
                .negativeColor(getResources().getColor(R.color.app_theme_color))
                .callback(new MaterialDialog.Callback() {
                    @Override
                    public void onPositive(final MaterialDialog dialog) {
                        View progressView = View.inflate(mContext,R.layout.view_progressbar,null);
                        dialog.setContentView(progressView);
                        deleteGrop(dialog);

                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .show();

	}

	
	
	
	/**
	 * 清空群聊天记录
	 */
	public void clearGroupHistory(MaterialDialog dialog){
		
		
		EMChatManager.getInstance().clearConversation(group.getGroupId());
        dialog.dismiss();
//		adapter.refresh(EMChatManager.getInstance().getConversation(toChatUsername));
		
		
		
	}
	
	
	/**
	 * 退出群组
	 * 
	 */
	public void exitGroup(final MaterialDialog dialog) {
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
                                    dialog.dismiss();
                                    setResult(RESULT_OK);
                                    finish();
                                    ChatActivity.activityInstance.finish();
                                }
                            });
                        }

                        @Override
                        public void onError(int i, String s) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });

				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
                            dialog.dismiss();
//							Toast.makeText(getApplicationContext(), "退出群聊失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            ToastUtil.getInstance(getApplicationContext()).showToast("呃~网络有些问题");
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
	private void deleteGrop(final MaterialDialog dialog) {
		new Thread(new Runnable() {
			public void run() {
				try {
					EMGroupManager.getInstance().exitAndDeleteGroup(groupId);
					runOnUiThread(new Runnable() {
						public void run() {
                            dialog.dismiss();
							setResult(RESULT_OK);
							finish();
							ChatActivity.activityInstance.finish();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
                            dialog.dismiss();
//							Toast.makeText(getApplicationContext(), "解散群聊失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            ToastUtil.getInstance(getApplicationContext()).showToast("呃~网络有些问题");
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
                            DialogManager.getInstance().dissMissLoadingDialog();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
                            DialogManager.getInstance().dissMissLoadingDialog();
//							Toast.makeText(getApplicationContext(), "添加群成员失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            ToastUtil.getInstance(getApplicationContext()).showToast("呃~网络有些问题");
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
        }else{
            findViewById(R.id.iv_arr).setVisibility(View.GONE);
        }
        titleHeaderBar.getTitleTextView().setText("群聊设置");
        titleHeaderBar.enableBackKey(true);
        groupNameTv.setText(group.getGroupName());
        //update block
        System.out.println("group msg is blocked:" + group.getMsgBlocked());
        List<String> notReceiveNotifyGroups =options.getReceiveNoNotifyGroup();
        if(notReceiveNotifyGroups==null||!notReceiveNotifyGroups.contains(groupId)){
            iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
            iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);
        }else if(notReceiveNotifyGroups.contains(groupId)){
            iv_switch_block_groupmsg.setVisibility(View.VISIBLE);
            iv_switch_unblock_groupmsg.setVisibility(View.INVISIBLE);
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
                            bindView();

						}
					});

				} catch (Exception e) {
                    e.printStackTrace();
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
//				    EMGroupManager.getInstance().unblockGroupMessage(groupId);
                    List<String> notReceiveNotifyGroups =options.getReceiveNoNotifyGroup();
                    if(notReceiveNotifyGroups==null){
                        notReceiveNotifyGroups=new ArrayList<String>();
                    }
                    notReceiveNotifyGroups.remove(groupId);
                    options.setReceiveNotNoifyGroup(notReceiveNotifyGroups);
				    iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
					iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					e.printStackTrace();
					//todo: 显示错误给用户
				}
			} else {
				System.out.println("change to block group msg");
				try {
//				    EMGroupManager.getInstance().blockGroupMessage(groupId);
                    List<String> notReceiveNotifyGroups =options.getReceiveNoNotifyGroup();
                    if(notReceiveNotifyGroups==null){
                        notReceiveNotifyGroups=new ArrayList<String>();
                    }
                    notReceiveNotifyGroups.add(groupId);
                    options.setReceiveNotNoifyGroup(notReceiveNotifyGroups);
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
