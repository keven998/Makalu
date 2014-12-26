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
package com.aizou.peachtravel.module.toolbox.im.adapter;

import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.config.Constant;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.InviteMessage;
import com.aizou.peachtravel.db.InviteStatus;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.aizou.peachtravel.db.respository.InviteMsgRepository;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class NewFriendsMsgAdapter extends ArrayAdapter<InviteMessage> {

	private Context context;
    DisplayImageOptions options;

	public NewFriendsMsgAdapter(Context context, int textViewResourceId, List<InviteMessage> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .showImageForEmptyUri(R.drawable.avatar_placeholder)
                .showImageOnFail(R.drawable.avatar_placeholder)
                .cacheOnDisc(true)
                        // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(39))) // 设置成圆角图片
                .build();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.row_invite_msg, null);
			holder.avator = (ImageView) convertView.findViewById(R.id.avatar);
			holder.reason = (TextView) convertView.findViewById(R.id.message);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.status = (Button) convertView.findViewById(R.id.user_state);
			holder.groupContainer = (LinearLayout) convertView.findViewById(R.id.ll_group);
			holder.groupname = (TextView) convertView.findViewById(R.id.tv_groupName);
			// holder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final InviteMessage msg = getItem(position);
		if (msg != null) {
			if(msg.getGroupId() != null){ // 显示群聊提示
				holder.groupContainer.setVisibility(View.VISIBLE);
				holder.groupname.setText(msg.getGroupName());
			} else{
				holder.groupContainer.setVisibility(View.GONE);
			}
			
			holder.reason.setText(msg.getReason());
			holder.name.setText(msg.getNickname());
			// holder.time.setText(DateUtils.getTimestampString(new
			// Date(msg.getTime())));
			if (msg.getStatus() == InviteStatus.BEAGREED) {
				holder.status.setVisibility(View.GONE);
				holder.reason.setText("已同意你的桃友请求");
			} else if (msg.getStatus() == InviteStatus.BEINVITEED || msg.getStatus() == InviteStatus.BEAPPLYED) {
				holder.status.setVisibility(View.VISIBLE);
				holder.status.setText("同意");
				if(msg.getStatus() == InviteStatus.BEINVITEED){
					if (msg.getReason() == null) {
						// 如果没写理由
						holder.reason.setText("请求加你为桃友");
					}
				}else{ //入群申请
					if (TextUtils.isEmpty(msg.getReason())) {
						holder.reason.setText("申请加入群：" + msg.getGroupName());
					}
				}
				// 设置点击事件
				holder.status.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 同意别人发的好友请求
						acceptInvitation(holder.status, msg);
					}
				});
			} else if (msg.getStatus() ==  InviteStatus.AGREED) {
				holder.status.setText("已添加");
                holder.status.setTextColor(getContext().getResources().getColor(R.color.app_theme_color));
				holder.status.setBackgroundResource(0);
				holder.status.setEnabled(false);
			} else if(msg.getStatus() ==  InviteStatus.REFUSED) {
				holder.status.setText("已拒绝");
                holder.status.setTextColor(getContext().getResources().getColor(R.color.app_theme_color));
				holder.status.setBackgroundResource(0);
				holder.status.setEnabled(false);
			}
            ImageLoader.getInstance().displayImage(msg.getAvatar(),holder.avator, options);

			// 设置用户头像
		}

		return convertView;
	}

	/**
	 * 同意好友请求或者群申请
	 * 
	 * @param button
	 * @param msg
	 */
	private void acceptInvitation(final Button button, final InviteMessage msg) {
        DialogManager.getInstance().showLoadingDialog((Activity) context);
        UserApi.addContact(msg.getUserId()+"",new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                button.setText("已同意");
                button.setTextColor(getContext().getResources().getColor(R.color.app_theme_color));
                msg.setStatus(InviteStatus.AGREED);
                InviteMsgRepository.getInviteMsgDao(context).update(msg);
                button.setBackgroundDrawable(null);
                button.setEnabled(false);
                IMUser imUser = new IMUser();
                imUser.setUserId(msg.getUserId());
                imUser.setNick(msg.getNickname());
                imUser.setUsername(msg.getFrom());
                imUser.setAvatar(msg.getAvatar());
                imUser.setIsMyFriends(true);
                imUser.setGender(msg.getGender());
                IMUtils.setUserHead(imUser);
                IMUserRepository.saveContact(context, imUser);
                AccountManager.getInstance().getContactList(context).put(imUser.getUsername(),imUser);
                EMMessage contentMsg = EMMessage.createSendMessage(EMMessage.Type.TXT);
                TextMessageBody body = new TextMessageBody("");
                contentMsg.setMsgId(UUID.randomUUID().toString());
                contentMsg.addBody(body);
                contentMsg.setTo(msg.getFrom());
                contentMsg.setFrom(AccountManager.getInstance().getLoginAccount(context).easemobUser);
                contentMsg.setMsgTime(System.currentTimeMillis());
                contentMsg.setAttribute(Constant.EXT_TYPE, Constant.ExtType.TIPS);
                contentMsg.setUnread(false);
                contentMsg.setAttribute(Constant.MSG_CONTENT,String.format(context.getResources().getString(R.string.has_add_contact),imUser.getNick()));
                EMChatManager.getInstance().saveMessage(contentMsg);

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                ToastUtil.getInstance(getContext()).showToast(getContext().getString(R.string.request_network_failed));
            }
        });

//		new Thread(new Runnable() {
//			public void run() {
//				// 调用sdk的同意方法
//				try {
//					if(msg.getGroupId() == null) //同意好友请求
//						EMChatManager.getInstance().acceptInvitation(msg.getFrom());
//					else //同意加群申请
//						EMGroupManager.getInstance().acceptApplication(msg.getFrom(), msg.getGroupId());
//				} catch (final Exception e) {
//					((Activity) context).runOnUiThread(new Runnable() {
//
//						@Override
//						public void run() {
//							pd.dismiss();
//							Toast.makeText(context, "同意失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//						}
//					});
//
//				}
//			}
//		}).start();
	}
	private static class ViewHolder {
		ImageView avator;
		TextView name;
		TextView reason;
		Button status;
		LinearLayout groupContainer;
		TextView groupname;
		// TextView time;
	}

}
