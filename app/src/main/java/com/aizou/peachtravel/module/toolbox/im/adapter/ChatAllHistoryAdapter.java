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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.GsonTools;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.bean.ExtFromUser;
import com.aizou.peachtravel.bean.PeachConversation;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.utils.SmileUtils;
import com.aizou.peachtravel.common.widget.circluaravatar.JoinBitmaps;
import com.aizou.peachtravel.config.Constant;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 显示所有聊天记录adpater
 */
public class ChatAllHistoryAdapter extends ArrayAdapter<PeachConversation> {

    private LayoutInflater inflater;
    DisplayImageOptions options;
    private Handler handler;
    private ImageSize avatarSize;

    public ChatAllHistoryAdapter(Context context, int textViewResourceId, List<PeachConversation> objects) {
        super(context, textViewResourceId, objects);
        inflater = LayoutInflater.from(context);
        handler = new Handler();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .showImageForEmptyUri(R.drawable.avatar_placeholder)
                .showImageOnFail(R.drawable.avatar_placeholder)
                .cacheOnDisc(true)
                        // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(22.5f))) // 设置成圆角图片
                .build();
        avatarSize = new ImageSize(LocalDisplay.dp2px(45), LocalDisplay.dp2px(45));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_chat_history, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
//            holder.msgState = convertView.findViewById(R.id.msg_state);
//            holder.list_item_layout = (RelativeLayout) convertView.findViewById(R.id.list_item_layout);
            convertView.setTag(holder);
        }
//        if (position % 2 == 0) {
//            holder.list_item_layout.setBackgroundResource(R.drawable.mm_listitem);
//        } else {
//            holder.list_item_layout.setBackgroundResource(R.drawable.mm_listitem_grey);
//        }

        // 获取与此用户/群组的会话
        EMConversation conversation = getItem(position).emConversation;
        IMUser imUser = getItem(position).imUser;
        // 获取用户username或者群组groupid
        String username = conversation.getUserName();

        EMContact contact = null;
        boolean isGroup = conversation.getIsGroup();
        if (isGroup) {
            contact = EMGroupManager.getInstance().getGroup(username);
            if (contact != null) {
                final EMGroup group = (EMGroup) contact;
                List<String> members = group.getMembers();
                if (members == null) {
                    members = new ArrayList<>();
                }
                final List<Bitmap> membersAvatars = new ArrayList<>();
                final int size = Math.min(members.size(), 4);
                // 群聊消息，显示群聊头像
                final ViewHolder finalHolder1 = holder;
                if (size != 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < size; i++) {
                                String username = group.getMembers().get(i);
                                IMUser user = IMUserRepository.getContactByUserName(getContext(), username);
                                if (user != null) {
                                    Bitmap bitmap = ImageLoader.getInstance().loadImageSync(user.getAvatar(), avatarSize, UILUtils.getDefaultOption());

                                    LogUtil.d("load_bitmap", user.getAvatar() + "=" + bitmap);
                                    if (bitmap == null) {
                                        bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.avatar_placeholder);
                                    }
                                    membersAvatars.add(bitmap);
                                } else {
                                    Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.avatar_placeholder);
                                    membersAvatars.add(bitmap);
                                }
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    finalHolder1.avatar.setImageBitmap(JoinBitmaps.createBitmap(LocalDisplay.dp2px(45),
                                            LocalDisplay.dp2px(45), membersAvatars));
                                }
                            });


                        }
                    }).start();
                } else {
                    holder.avatar.setImageResource(R.drawable.group_icon);
                }
            } else {
                holder.avatar.setImageResource(R.drawable.group_icon);
            }
            if (contact != null) {
                holder.name.setText(contact.getNick() != null ? contact.getNick() : username);
            }

        } else {
            if (imUser != null) {
                // 本地或者服务器获取用户详情，以用来显示头像和nick
//                holder.avatar.setBackgroundResource(R.drawable.default_avatar);
                final ViewHolder finalHolder = holder;
                finalHolder.avatar.setTag(imUser.getAvatar());
                ImageLoader.getInstance().loadImage(imUser.getAvatar(), avatarSize, UILUtils.getDefaultOption(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        if (imageUri == null) {
                            return;
                        }
                        if (imageUri.equals(finalHolder.avatar.getTag())) {
                            if (loadedImage == null) {
                                loadedImage = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.avatar_placeholder);
                            }
                            ArrayList<Bitmap> bmps = new ArrayList<Bitmap>();
                            bmps.add(loadedImage);
                            finalHolder.avatar.setImageBitmap(JoinBitmaps.createBitmap(LocalDisplay.dp2px(45),
                                    LocalDisplay.dp2px(45), bmps));
                        }


                    }
                });
//                ImageLoader.getInstance().displayImage(imUser.getAvatar(), holder.avatar, options);
//                if (username.equals(Constant.GROUP_USERNAME)) {
//                    holder.name.setText("群聊");
//
//                } else if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
//                    holder.name.setText("申请与通知");
//                }
                if (TextUtils.isEmpty(imUser.getMemo())) {
                    holder.name.setText(imUser.getNick());
                } else {
                    holder.name.setText(imUser.getMemo());
                }
            }


        }

        if (conversation.getUnreadMsgCount() > 0) {
            // 显示与此用户的消息未读数
//            holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
            holder.unreadLabel.setVisibility(View.VISIBLE);
        } else {
            holder.unreadLabel.setVisibility(View.GONE);
        }

        if (conversation.getMsgCount() != 0) {
            // 把最后一条消息的内容作为item的message内容
            EMMessage lastMessage = conversation.getLastMessage();
            holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct == EMMessage.Direct.SEND && lastMessage.status == EMMessage.Status.FAIL) {
//                holder.msgState.setVisibility(View.VISIBLE);
                Drawable drawable = getContext().getResources().getDrawable(R.drawable.ic_message_send_fail);
                drawable.setBounds(1, 1, LocalDisplay.dp2px(15), LocalDisplay.dp2px(15));
                holder.message.setCompoundDrawables(null, null, drawable, null);
            } else {
//                holder.msgState.setVisibility(View.GONE);
                holder.message.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
            holder.message.setText(SmileUtils.getSmiledText(getContext(), getMessageDigest(lastMessage, (this.getContext()), isGroup)),
                    BufferType.SPANNABLE);
        }

        return convertView;
    }

    /**
     * 根据消息内容和消息类型获取消息内容提示
     *
     * @param message
     * @param context
     * @return
     */
    private String getMessageDigest(EMMessage message, Context context, boolean isGroup) {
        String digest = "";
        int extType = message.getIntAttribute(Constant.EXT_TYPE, 0);
        if (isGroup) {
            if (extType != Constant.ExtType.TIPS) {
                String fromUserJson = message.getStringAttribute("fromUser", "");
                ExtFromUser fromUser = GsonTools.parseJsonToBean(fromUserJson, ExtFromUser.class);
                if (fromUser != null && !TextUtils.isEmpty(fromUser.nickName)) {
                    digest = fromUser.nickName + ":";
                }

            }
        }
        switch (message.getType()) {
            case LOCATION: // 位置消息
                if (message.direct == EMMessage.Direct.RECEIVE) {
                    // 从sdk中提到了ui中，使用更简单不犯错的获取string的方法
                    // digest = EasyUtils.getAppResourceString(context,
                    // "location_recv");
                    digest = getStrng(context, R.string.location_recv);
                    digest = String.format(digest, message.getFrom());
                    return digest;
                } else {
                    // digest = EasyUtils.getAppResourceString(context,
                    // "location_prefix");
                    digest = getStrng(context, R.string.location_prefix);
                }
                break;
            case IMAGE: // 图片消息
                ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
                digest = digest + getStrng(context, R.string.picture) + imageBody.getFileName();
                break;
            case VOICE:// 语音消息
                digest = digest + getStrng(context, R.string.voice);
                break;
            case VIDEO: // 视频消息
                digest = digest + getStrng(context, R.string.video);
                break;
            case TXT: // 文本消息
                if (!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {

                    String content = message.getStringAttribute(Constant.MSG_CONTENT, "");
                    if (extType == 0) {
                        TextMessageBody txtBody = (TextMessageBody) message.getBody();
                        digest = digest + txtBody.getMessage();
                    } else if (extType == Constant.ExtType.TIPS) {
                        digest = content;
                    } else {
                        digest = digest + "[链接]";
                    }

                } else {
                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
                    digest = getStrng(context, R.string.voice_call) + txtBody.getMessage();
                }
                break;
            case FILE: // 普通文件消息
                digest = digest + getStrng(context, R.string.file);
                break;
            default:
                System.err.println("error, unknow type");
                return "";
        }

        return digest;
    }

    private static class ViewHolder {
        /**
         * 和谁的聊天记录
         */
        TextView name;
        /**
         * 消息未读数
         */
        TextView unreadLabel;
        /**
         * 最后一条消息的内容
         */
        TextView message;
        /**
         * 最后一条消息的时间
         */
        TextView time;
        /**
         * 用户头像
         */
        ImageView avatar;
        /**
         * 最后一条消息的发送状态
         */
//        View msgState;
        /**
         * 整个list中每一行总布局
         */
//        RelativeLayout list_item_layout;

    }

    String getStrng(Context context, int resId) {
        return context.getResources().getString(resId);
    }
}
