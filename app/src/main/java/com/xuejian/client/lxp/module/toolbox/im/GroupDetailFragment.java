package com.xuejian.client.lxp.module.toolbox.im;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.GsonTools;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.PeachUser;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.PreferenceUtils;
import com.xuejian.client.lxp.config.PeachApplication;
import com.xuejian.client.lxp.db.IMUser;
import com.xuejian.client.lxp.db.respository.IMUserRepository;
import com.xuejian.client.lxp.db.userDB.User;
import com.xuejian.client.lxp.db.userDB.UserDBManager;
import com.xuejian.client.lxp.module.toolbox.im.group.CallBack;
import com.xuejian.client.lxp.module.toolbox.im.group.GroupManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rjm on 2015/3/30.
 */
public class GroupDetailFragment extends PeachBaseFragment implements View.OnClickListener {

    private static final int REQUEST_CODE_ADD_USER = 0;
    private static final int REQUEST_CODE_EXIT = 1;
    private static final int REQUEST_CODE_EXIT_DELETE = 2;
    private static final int REQUEST_CODE_CLEAR_ALL_HISTORY = 3;
    private static final int REQUEST_CODE_MODIFY_GROUP_NAME = 4;

    private ListView memberGv;
    private String groupId;
    private String name;
    private EMGroup group;
    private int referenceWidth;
    private int referenceHeight;

    private RelativeLayout rl_switch_block_groupmsg;
    private LinearLayout rl_groupName;
    private TextView groupNameTv;
    private TextView addGroup;
    private CheckedTextView delGroupMember;

    private boolean isInDeleteMode;
    private MemberAdapter memberAdapter;

    /**
     * 屏蔽群消息imageView
     */
    private ImageView iv_switch_block_groupmsg;
    /**
     * 关闭屏蔽群消息imageview
     */
    private ImageView iv_switch_unblock_groupmsg;


    //清空所有聊天记录
    private RelativeLayout clearAllHistory;
    private EMChatOptions options;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_details,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        memberGv = (ListView) getView().findViewById(R.id.gv_members);
        addGroup = (TextView) getView().findViewById(R.id.tv_add_to_group);
        delGroupMember = (CheckedTextView) getView().findViewById(R.id.tv_del_to_group);
        delGroupMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckedTextView view = (CheckedTextView) v;
                if (!isInDeleteMode) {
                    isInDeleteMode = true;
                    view.setText("完成");
                } else {
                    isInDeleteMode = false;
                    view.setText("删除");
                }
                view.setChecked(isInDeleteMode);
                memberAdapter.notifyDataSetChanged();
            }
        });
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 进入选人页面
                startActivityForResult(
                        (new Intent(getActivity(), PickContactsWithCheckboxActivity.class).putExtra("groupId", groupId)), REQUEST_CODE_ADD_USER);
            }
        });
        clearAllHistory = (RelativeLayout) getView().findViewById(R.id.clear_all_history);
        rl_groupName = (LinearLayout) getView().findViewById(R.id.ll_group_name);
        groupNameTv = (TextView) getView().findViewById(R.id.tv_groupName);

        rl_switch_block_groupmsg = (RelativeLayout) getView().findViewById(R.id.rl_switch_block_groupmsg);
        iv_switch_block_groupmsg = (ImageView) getView().findViewById(R.id.iv_switch_block_groupmsg);
        iv_switch_unblock_groupmsg = (ImageView) getView().findViewById(R.id.iv_switch_unblock_groupmsg);

        rl_switch_block_groupmsg.setOnClickListener(this);

        Drawable referenceDrawable = getResources().getDrawable(R.drawable.smiley_add_btn);
        referenceWidth = referenceDrawable.getIntrinsicWidth();
        referenceHeight = referenceDrawable.getIntrinsicHeight();

        // 获取传过来的groupid
        groupId = getArguments().getString("groupId");
        name = getArguments().getString("name");
      //  group = EMGroupManager.getInstance().getGroup(groupId);
      //  options = EMChatManager.getInstance().getChatOptions();
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

        clearAllHistory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final PeachMessageDialog dialog = new PeachMessageDialog(getActivity());
                dialog.setTitle("提示");
                dialog.setMessage("确定清空此群的聊天记录吗？");
                dialog.setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearGroupHistory();
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ADD_USER:// 添加群成员
                    setUpGroupMemeber("update");
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
     * @param
     */
    public void exitGroupTips() {
        final PeachMessageDialog dialog = new PeachMessageDialog(getActivity());
        dialog.setTitle("提示");
        dialog.setMessage("退出后，将不再接收此群聊消息");
        dialog.setPositiveButton("退出", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                exitGroup();
            }
        });
        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 点击解散群组按钮
     *
     * @param
     */
    public void exitDeleteGroupTips() {
        final PeachMessageDialog dialog = new PeachMessageDialog(getActivity());
        dialog.setTitle("提示");
        dialog.setMessage(getString(R.string.dissolution_group_hint));
        dialog.setPositiveButton("解散", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteGrop();
            }
        });
        dialog.setNegativeButton("取消",new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    /**
     * 清空群聊天记录
     */
    public void clearGroupHistory() {


        EMChatManager.getInstance().clearConversation(group.getGroupId());
		//adapter.refresh(EMChatManager.getInstance().getConversation(toChatUsername));


    }


    /**
     * 退出群组
     */
    public void exitGroup() {
        DialogManager.getInstance().showLoadingDialog(getActivity());
        new Thread(new Runnable() {
            public void run() {
                try {

                    EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                    msg.setChatType(EMMessage.ChatType.GroupChat);
                    msg.setFrom(String.valueOf(AccountManager.getInstance().getLoginAccount(PeachApplication.getContext()).getUserId()));
                    msg.setReceipt(group.getGroupId());
                    IMUtils.setMessageWithTaoziUserInfo(PeachApplication.getContext(), msg);
                    String myNickname = AccountManager.getInstance().getLoginAccount(PeachApplication.getContext()).getNickName();
                    String content = myNickname + " 退出了群聊";
                    IMUtils.setMessageWithExtTips(PeachApplication.getContext(), msg, content);
                    msg.addBody(new TextMessageBody(content));
                    EMChatManager.getInstance().sendGroupMessage(msg, new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            try {
                                EMGroupManager.getInstance().exitFromGroup(groupId);
                            } catch (EaseMobException e) {
                                e.printStackTrace();
                            }
                            if (getActivity()!=null&&!getActivity().isFinishing())
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        DialogManager.getInstance().dissMissLoadingDialog();
                                        getActivity().setResult(Activity.RESULT_OK);
                                        getActivity().finish();
//                                        ChatActivity.activityInstance.finish();
                                    }
                                });
                        }

                        @Override
                        public void onError(int i, String s) {
                            if (getActivity()!=null&&!getActivity().isFinishing())
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogManager.getInstance().dissMissLoadingDialog();
                                        ToastUtil.getInstance(PeachApplication.getContext()).showToast("退出群聊失败");
                                    }
                                });
                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });

                } catch (final Exception e) {
                    if (getActivity()!=null&&!getActivity().isFinishing())
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                DialogManager.getInstance().dissMissLoadingDialog();
//							Toast.makeText(getApplicationContext(), "退出群聊失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                ToastUtil.getInstance(PeachApplication.getContext()).showToast("呃~网络有些问题");
                            }
                        });
                }
            }
        }).start();
    }

    /**
     * 解散群组
     */
    private void deleteGrop() {
        DialogManager.getInstance().showLoadingDialog(getActivity());
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMGroupManager.getInstance().exitAndDeleteGroup(groupId);
                    if (getActivity()!=null&&!getActivity().isFinishing())
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {

                                DialogManager.getInstance().dissMissLoadingDialog();
                                getActivity().setResult(Activity.RESULT_OK);
                                ChatActivity.activityInstance.finish();
                            }
                        });
                } catch (final Exception e) {
                    if (getActivity()!=null&&!getActivity().isFinishing())
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                DialogManager.getInstance().dissMissLoadingDialog();
//							Toast.makeText(getApplicationContext(), "解散群聊失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                ToastUtil.getInstance(PeachApplication.getContext()).showToast("呃~网络有些问题");
                            }
                        });
                }
            }
        }).start();
    }



    private void bindView() {
        memberAdapter = new MemberAdapter(new ViewHolderCreator<User>() {
            @Override
            public ViewHolderBase<User> createViewHolder() {
                return new MemberViewHolder();
            }
        });

 //       如果自己是群主，显示解散按钮
//        if (group.getOwner() == null || "".equals(group.getOwner())) {
////            exitBtn.setVisibility(View.GONE);
////            deleteBtn.setVisibility(View.GONE);
//        } else {
//            if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
//                if (memberGv.getFooterViewsCount() == 0) {
//                    View view = getActivity().getLayoutInflater().inflate(R.layout.im_group_option_footer, null);
//                    memberGv.addFooterView(view);
//                    Button btn = (Button) view.findViewById(R.id.footer_btn);
//                    btn.setText("解散该群");
//                    btn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            exitDeleteGroupTips();
//                        }
//                    });
//                }
//
//                delGroupMember.setVisibility(View.VISIBLE);
//                rl_groupName.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(getActivity(), ModifyGroupNameActivity.class);
//                        intent.putExtra("groupId", groupId);
//                        startActivityForResult(intent, REQUEST_CODE_MODIFY_GROUP_NAME);
//                    }
//                });
//            } else {
                getView().findViewById(R.id.iv_arr).setVisibility(View.GONE);
                if (memberGv.getFooterViewsCount() == 0) {
                    View view = getActivity().getLayoutInflater().inflate(R.layout.im_group_option_footer, null);
                    memberGv.addFooterView(view);
                    Button btn = (Button) view.findViewById(R.id.footer_btn);
                    btn.setText("退出此群");
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            exitGroupTips();
                        }
                    });
                }
//            }
 //       }

        memberGv.setAdapter(memberAdapter);
        setUpGroupMemeber("");

        groupNameTv.setText(name);
//        List<String> notReceiveNotifyGroups = options.getReceiveNoNotifyGroup();
//        if (notReceiveNotifyGroups == null || !notReceiveNotifyGroups.contains(groupId)) {
//            iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
//            iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);
//        } else if (notReceiveNotifyGroups.contains(groupId)) {
            iv_switch_block_groupmsg.setVisibility(View.VISIBLE);
            iv_switch_unblock_groupmsg.setVisibility(View.INVISIBLE);
       // }
    }

   /* public class MemberAdapter2 extends BaseAdapter{
        private ImageView avatarIv, removeIv;
        private TextView nicknameTv;
        private DisplayImageOptions picOptions;
        private TextView groupHolderName;
        private  IMUser imUser;

        public MemberAdapter2(IMUser imUser){
            this.imUser=imUser;
            picOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                    .resetViewBeforeLoading(true)
                    .showImageOnFail(R.drawable.avatar_placeholder)
                    .showImageOnLoading(R.drawable.avatar_placeholder)
                    .showImageForEmptyUri(R.drawable.avatar_placeholder)
//				    .decodingOptions(D)
//                  .displayer(new FadeInBitmapDisplayer(150, true, true, false))
                    .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(6)))
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        }

        @Override
        public int getCount() {
            return imUser.;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView=View.inflate(getActivity(),R.layout.group_member_list,null);
            }

            avatarIv = (ImageView) convertView.findViewById(R.id.iv_avatar);
            removeIv = (ImageView) convertView.findViewById(R.id.badge_delete);
            nicknameTv = (TextView) convertView.findViewById(R.id.tv_nickname);
            groupHolderName = (TextView) convertView.findViewById(R.id.group_holder_name);

            return convertView;
        }
    }*/

    private void setUpGroupMemeber(String type){
        final List<User> members=UserDBManager.getInstance().getGroupMember(Long.parseLong(groupId));
        final List<String> unkownMembers= new ArrayList<String>();
        memberAdapter.getDataList().clear();
        if (members==null||"update".equals(type)){
            //fetch info
            System.out.println("fetch info");
            GroupManager.getGroupManager().getGroupMembers(groupId, new CallBack() {
                @Override
                public void onSuccess() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("getGroupMemberSuccess");
                            List<User>  members=UserDBManager.getInstance().getGroupMember(Long.parseLong(groupId));
                            memberAdapter.getDataList().clear();
                            for (User user : members) {
                                memberAdapter.getDataList().add(user);
                                memberAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }

                @Override
                public void onFailed() {

                }
            });
        }
        else {
            for (User user : members) {
                memberAdapter.getDataList().add(user);
            }
        }
        memberAdapter.notifyDataSetChanged();
        if(unkownMembers.size() > 0) {
//            UserApi.getContactByHx(unkownMembers, new HttpCallBack<String>() {
//                @Override
//                public void doSucess(String result, String method) {
//                    CommonJson4List<PeachUser> userResult = CommonJson4List.fromJson(result, PeachUser.class);
//                    if (userResult.code == 0) {
//                        for (PeachUser user : userResult.result) {
//                            IMUser imUser = new IMUser();
//                            imUser.setUserId(user.userId);
//                            imUser.setNick(user.nickName);
//                            imUser.setUsername(user.easemobUser);
//                            imUser.setMemo(user.memo);
//                            imUser.setGender(user.gender);
//                            imUser.setAvatar(user.avatar);
//                            imUser.setAvatarSmall(user.avatarSmall);
//                            imUser.setSignature(user.signature);
//                            IMUserRepository.saveContact(PeachApplication.getContext(), imUser);
//                        }
//                        unkownMembers.clear();
//                        memberAdapter.getDataList().clear();
// //                       for (String username : members) {
////                            IMUser user = IMUserRepository.getContactByUserName(PeachApplication.getContext(), username);
////                            if (user == null) {
////                                unkownMembers.add(username);
////                                user = new IMUser();
////                                user.setUsername(username);
////                            }
////                            if (!user.getUsername().equals(EMChatManager.getInstance().getCurrentUser())) {
////                              //  memberAdapter.getDataList().add(user);
////                            }
////                        }
//                        memberAdapter.notifyDataSetChanged();
//                    }
//                }
//
//                @Override
//                public void doFailure(Exception error, String msg, String method) {
//                    if (getActivity() != null && !getActivity().isFinishing())
//                        ToastUtil.getInstance(PeachApplication.getContext()).showToast(getResources().getString(R.string.request_network_failed));
//                }
//            });
        }

    }

    protected void updateGroup() {
        //更新本地数据
        GroupManager.getGroupManager().getGroupInformation(groupId,new CallBack() {
            @Override
            public void onSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        bindView();
                    }
                });
            }

            @Override
            public void onFailed() {
                System.out.println("更新失败");
            }
        });
    }






    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_switch_block_groupmsg:
                if (iv_switch_block_groupmsg.getVisibility() == View.VISIBLE) {
                    try {
//				    EMGroupManager.getInstance().unblockGroupMessage(groupId);
                        List<String> notReceiveNotifyGroups = options.getReceiveNoNotifyGroup();
                        if (notReceiveNotifyGroups == null) {
                            notReceiveNotifyGroups = new ArrayList<String>();
                        }
                        notReceiveNotifyGroups.remove(groupId);
                        options.setReceiveNotNoifyGroup(notReceiveNotifyGroups);
                        iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
                        iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);
//                    EMChatManager.getInstance().setChatOptions(options);
                        PreferenceUtils.cacheData(getActivity(), String.format("%s_not_notify", AccountManager.getInstance().getLoginAccount(getActivity()).getUserId()), GsonTools.createGsonString(notReceiveNotifyGroups));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //todo: 显示错误给用户
                    }
                } else {
                    try {
//				    EMGroupManager.getInstance().blockGroupMessage(groupId);
                        List<String> notReceiveNotifyGroups = options.getReceiveNoNotifyGroup();
                        if (notReceiveNotifyGroups == null) {
                            notReceiveNotifyGroups = new ArrayList<String>();
                        }
                        notReceiveNotifyGroups.add(groupId);
                        options.setReceiveNotNoifyGroup(notReceiveNotifyGroups);
                        iv_switch_block_groupmsg.setVisibility(View.VISIBLE);
                        iv_switch_unblock_groupmsg.setVisibility(View.INVISIBLE);
//                    EMChatManager.getInstance().setChatOptions(options);
                        PreferenceUtils.cacheData(getActivity(), String.format("%s_not_notify", AccountManager.getInstance().getLoginAccount(getActivity()).getUserId()), GsonTools.createGsonString(notReceiveNotifyGroups));
                    } catch (Exception e) {
                        e.printStackTrace();
                        //todo: 显示错误给用户
                    }
                }
                break;

            default:break;
        }

    }

    private class MemberAdapter extends ListViewDataAdapter<User> {

        /**
         * @param viewHolderCreator The view holder creator will create a View Holder that extends {@link com.aizou.core.widget.listHelper.ViewHolderBase}
         */
        public MemberAdapter(ViewHolderCreator viewHolderCreator) {
            super(viewHolderCreator);
        }

        @Override
        public int getCount() {
            return super.getCount();
        }
    }

    private class MemberViewHolder extends ViewHolderBase<User> {
        private View contentView;
        private ImageView avatarIv, removeIv;
        private TextView nicknameTv,viewHolderName;
        private DisplayImageOptions picOptions;

        public MemberViewHolder() {
            super();
            picOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                    .resetViewBeforeLoading(true)
                    .showImageOnFail(R.drawable.avatar_placeholder_round)
                    .showImageOnLoading(R.drawable.avatar_placeholder_round)
                    .showImageForEmptyUri(R.drawable.avatar_placeholder_round)
//				    .decodingOptions(D)
//                  .displayer(new FadeInBitmapDisplayer(150, true, true, false))
                    .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(6)))
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        }


        @Override
        public View createView(LayoutInflater layoutInflater) {
            contentView = layoutInflater.inflate(R.layout.group_member_list, null);
            avatarIv = (ImageView) contentView.findViewById(R.id.iv_avatar);
            removeIv = (ImageView) contentView.findViewById(R.id.badge_delete);
            nicknameTv = (TextView) contentView.findViewById(R.id.tv_nickname);
            //viewHolderName = (TextView) contentView.findViewById(R.id.group_holder_name);
            return contentView;
        }

        @Override
        public void showData(int position, final User itemData) {
           /* if(position==memberAdapter.getCount()-1){
                avatarIv.setImageResource(R.drawable.smiley_minus_btn);
                nicknameTv.setText("");
                removeIv.setVisibility(View.INVISIBLE);
                if (!group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
                    // if current user is not group admin, hide add/remove btn
                    contentView.setVisibility(View.INVISIBLE);
                }else{
                    if (isInDeleteMode) {
                        // 正处于删除模式下，隐藏删除按钮
                        contentView.setVisibility(View.INVISIBLE);
                    } else {
                        // 正常模式
                        contentView.setVisibility(View.VISIBLE);
                        contentView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isInDeleteMode = true;
                                memberAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }else*/ /*if(position==memberAdapter.getCount()-2){
                avatarIv.setImageResource(R.drawable.smiley_add_btn);
                nicknameTv.setText("");
                removeIv.setVisibility(View.INVISIBLE);
                // 如果不是创建者或者没有相应权限
                if (!group.isAllowInvites() && !group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
                    // if current user is not group admin, hide add/remove btn
                    contentView.setVisibility(View.INVISIBLE);
                } else {
                    // 正处于删除模式下,隐藏添加按钮
                    if (isInDeleteMode) {
                        contentView.setVisibility(View.INVISIBLE);
                    } else {
                        contentView.setVisibility(View.VISIBLE);
                    }
                    contentView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 进入选人页面
                            startActivityForResult(
                                    (new Intent(getActivity(), PickContactsWithCheckboxActivity.class).putExtra("groupId", group.getGroupId())), REQUEST_CODE_ADD_USER);
                        }
                    });
                }
            }else*/
            {
//                avatarIv.setImageResource(R.drawable.avatar_placeholder);
                nicknameTv.setText(itemData.getNickName());
                ImageLoader.getInstance().displayImage(itemData.getAvatar(), avatarIv, picOptions);
                if (isInDeleteMode) {
                    removeIv.setVisibility(View.VISIBLE);
                    /**
                     * 群组踢人功能
                     */
//                    if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
//                        //viewHolderName.setText("群主");
//                        removeIv.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                final PeachMessageDialog dialog = new PeachMessageDialog(getActivity());
//                                dialog.setTitle("提示");
//                                dialog.setMessage("确定要移除该用户吗？");
//                                dialog.setPositiveButton("确定", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        dialog.dismiss();
//                                        deleteMembersFromGroup(itemData);
//
//                                    }
//                                });
//                                dialog.setNegativeButton("取消", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                                dialog.show();
//                            }
//                        });
//                    }
//                }
//            else {
                    removeIv.setVisibility(View.GONE);
                    contentView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //      if (UserDBManager.getInstance().isMyFriend(itemData.getUserId())) {
                            Intent intent = new Intent(getActivity(), ContactDetailActivity.class);
                            intent.putExtra("userId", itemData.getUserId());
                            intent.putExtra("userNick", itemData.getNickName());
                            startActivity(intent);
//                            } else {
//                                PeachUser user = new PeachUser();
//                                user.nickName = itemData.getNick();
//                                user.userId = itemData.getUserId();
//                                user.easemobUser = itemData.getUsername();
//                                user.avatar = itemData.getAvatar();
//                                user.avatarSmall = itemData.getAvatarSmall();
//                                user.signature = itemData.getSignature();
//                                user.gender = itemData.getGender();
//                                user.memo = itemData.getMemo();
//                                Intent intent = new Intent(getActivity(), SeachContactDetailActivity.class);
//                                intent.putExtra("user", user);
//                                startActivity(intent);
//                            }
                        }
                    });
                }

            }
        }
        /**
         * 删除群成员
         * @param imUser
         */
        protected void deleteMembersFromGroup(final IMUser imUser) {
            if(imUser.getUsername().equals(EMChatManager.getInstance().getCurrentUser())){
                ToastUtil.getInstance(getActivity()).showToast("不能删除自己");
                return;
            }
            final ProgressDialog deleteDialog = new ProgressDialog(getActivity());
            deleteDialog.setMessage("正在移除...");
            deleteDialog.setCanceledOnTouchOutside(false);
            deleteDialog.show();
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        // 删除被选中的成员

                        EMGroupManager.getInstance().removeUserFromGroup(group.getGroupId(), imUser.getUsername());
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                deleteDialog.dismiss();
                                memberAdapter.getDataList().remove(imUser);
                                memberAdapter.notifyDataSetChanged();
                                // 被邀请
                                EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                                msg.setChatType(EMMessage.ChatType.GroupChat);
                                msg.setFrom(String.valueOf(AccountManager.getInstance().getLoginAccount(getActivity()).getUserId()));
                                msg.setReceipt(group.getGroupId());
                                IMUtils.setMessageWithTaoziUserInfo(getActivity(), msg);
                                String myNickmae = String.valueOf(AccountManager.getInstance().getLoginAccount(getActivity()).getUserId());
                                String content = String.format(getActivity().getResources().getString(R.string.remove_user_from_group),myNickmae,imUser.getNick());
                                IMUtils.setMessageWithExtTips(getActivity(), msg, content);
                                msg.addBody(new TextMessageBody(content));
                                EMChatManager.getInstance().sendGroupMessage(msg, new EMCallBack() {
                                    @Override
                                    public void onSuccess() {


                                    }

                                    @Override
                                    public void onError(int i, String s) {

                                    }

                                    @Override
                                    public void onProgress(int i, String s) {

                                    }
                                });
                            }
                        });
                    } catch (final Exception e) {
                        deleteDialog.dismiss();
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
//                                Toast.makeText(getApplicationContext(), "删除失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                ToastUtil.getInstance(PeachApplication.getContext()).showToast("请求也是失败了");
                            }
                        });
                    }

                }
            }).start();
        }
    }

}
