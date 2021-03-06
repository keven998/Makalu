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
package com.xuejian.client.lxp.module.toolbox.im.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.utils.SharedPreferencesUtil;
import com.lv.bean.InventMessage;
import com.lv.im.IMClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.toolbox.HisMainPageActivity;

import java.util.List;

public class NewFriendsMsgAdapter extends ArrayAdapter<InventMessage> {

    private Context context;
    DisplayImageOptions options;
    private ImageLoader imageLoader;

    public NewFriendsMsgAdapter(Context context, int textViewResourceId, List<InventMessage> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .showImageForEmptyUri(R.drawable.ic_home_more_avatar_unknown_round)
                .showImageOnFail(R.drawable.ic_home_more_avatar_unknown_round)
                .cacheOnDisk(true)
                        // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(19))) // 设置成圆角图片
                .build();
        imageLoader = ImageLoader.getInstance();
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

        final InventMessage msg = getItem(position);

        holder.avator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HisMainPageActivity.class);
                intent.putExtra("userId", msg.getUserId());
                context.startActivity(intent);
            }
        });
        if (msg.getStatus() == 0) {
//            if(msg.getGroupId() != null){ // 显示群聊提示
//                holder.groupContainer.setVisibility(View.VISIBLE);
//                holder.groupname.setText(msg.getGroupName());
//            } else{
            holder.groupContainer.setVisibility(View.GONE);
            //         }
            if (TextUtils.isEmpty(msg.getRequestMsg())) {
                // 如果没写理由
                holder.reason.setText("请求加你为朋友");
            } else {
                holder.reason.setText(msg.getRequestMsg());
            }
            imageLoader.displayImage(msg.getAvatarSmall(), holder.avator, options);
            holder.name.setText(msg.getNickName());

            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText("同意");
            // 设置点击事件
            holder.status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 同意别人发的好友请求
                    acceptInvitation(holder.status, msg);
                }
            });
            holder.status.setEnabled(true);
        } else if (msg.getStatus() == 1) {
            holder.name.setText(msg.getNickName());
            if (TextUtils.isEmpty(msg.getRequestMsg())) {
                // 如果没写理由
                holder.reason.setText("请求加你为朋友");
            } else {
                holder.reason.setText(msg.getRequestMsg());
            }
            holder.status.setEnabled(false);
            holder.status.setText("已添加");
            holder.status.setBackgroundResource(0);
            imageLoader.displayImage(msg.getAvatarSmall(), holder.avator, options);

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
    private void acceptInvitation(final Button button, final InventMessage msg) {
        try {
            DialogManager.getInstance().showLoadingDialog(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UserApi.addContact(msg.getRequestId(), null, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                button.setText("已添加");
                button.setTextColor(getContext().getResources().getColor(R.color.app_theme_color));
                //   msg.setStatus(InviteStatus.AGREED);
                //   InviteMsgRepository.getInviteMsgDao(context).update(msg);
                button.setBackgroundDrawable(null);
                button.setEnabled(false);
                User imUser = new User();
                imUser.setUserId(msg.getUserId());
                imUser.setNickName(msg.getNickName());
                imUser.setAvatarSmall(msg.getAvatarSmall());
                imUser.setType(1);
                UserDBManager.getInstance().saveContact(imUser);
                AccountManager.getInstance().getContactList(context).put(imUser.getUserId(), imUser);
                msg.setStatus(1);
                SharedPreferencesUtil.saveValue(context,"contactNeedRefresh",true);
                IMClient.getInstance().addTips(String.valueOf(imUser.getUserId()), "你已添加" + imUser.getNickName() + "为朋友，现在可以开始聊天了", "single");
                IMClient.getInstance().updateInventMsgStatus(imUser.getUserId(), 1);

                //   (context).startActivity(new Intent(context, HisMainPageActivity.class).putExtra("userId", msg.getUserId().intValue()));
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!((Activity) context).isFinishing())
                    ToastUtil.getInstance(getContext()).showToast(getContext().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
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
