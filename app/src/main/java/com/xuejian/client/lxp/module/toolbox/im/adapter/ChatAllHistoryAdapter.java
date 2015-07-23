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

import com.aizou.core.utils.LocalDisplay;
import com.lv.bean.ConversationBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.SmileUtils;
import com.xuejian.client.lxp.common.widget.circluaravatar.JoinBitmaps;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 显示所有聊天记录adpater
 */
public class ChatAllHistoryAdapter extends ArrayAdapter<ConversationBean> {
    private static final int TEXT_MSG = 0;
    private static final int VOICE_MSG = 1;
    private static final int IMAGE_MSG = 2;
    private static final int LOC_MSG = 3;
    private static final int POI_MSG = 5;
    private static final int VIDEO_MSG = 6;
    private static final int FILE_MSG = 7;
    private static final int TYPE_SEND = 0;
    private static final int TYPE_REV = 1;

    private static final int GUIDE_MSG = 10;
    private static final int CITY_MSG = 11;
    private static final int TRAVEL_MSG = 12;
    private static final int SPOT_MSG = 13;
    private static final int FOOD_MSG = 14;
    private static final int SHOP_MSG = 15;
    private static final int HOTEL_MSG = 16;
    private static final int TIPS_MSG = 200;


    private LayoutInflater inflater;
    DisplayImageOptions options;
    private Handler handler;
    private ImageSize avatarSize;
    private Context mContext;

    public ChatAllHistoryAdapter(Context context, int textViewResourceId, List<ConversationBean> objects) {
        super(context, textViewResourceId, objects);
        mContext = context;
        inflater = LayoutInflater.from(context);
        handler = new Handler();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                        //  .showImageOnLoading()
                .showImageOnFail(R.drawable.messages_bg_useravatar)
                .cacheOnDisc(true)
                        // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(28))) // 设置成圆角图片
                .build();
        avatarSize = new ImageSize(LocalDisplay.dp2px(56), LocalDisplay.dp2px(56));
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

        // 获取与此用户/群组的会话
        ConversationBean conversation = getItem(position);
        User user = null;
        if (AccountManager.getInstance().getLoginAccount(mContext) != null) {
            user = UserDBManager.getInstance().getContactByUserId(Long.parseLong(conversation.getFriendId() + ""));
        }
        // 获取用户username或者群组groupid
        String username = String.valueOf(conversation.getFriendId());
        boolean isGroup = "group".equals(conversation.getChatType());
        if (isGroup) {
            final List<User> members = UserDBManager.getInstance().getGroupMember(Long.parseLong(username));
            final List<Bitmap> membersAvatars = new ArrayList<>();
            int membersize;
            if (members != null) {
                membersize = members.size();
            } else membersize = 0;
            final int size = Math.min(membersize, 4);
//                // 群聊消息，显示群聊头像
            final ViewHolder finalHolder1 = holder;
            if (size != 0) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < size; i++) {
                            User user = members.get(i);
                            if (user != null) {
                                Bitmap bitmap = ImageLoader.getInstance().loadImageSync(user.getAvatarSmall(), avatarSize, UILUtils.getDefaultOption());
                                if (bitmap == null) {
                                    bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.messages_bg_useravatar);
                                }
                                membersAvatars.add(bitmap);
                            } else {
                                Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.messages_bg_useravatar);
                                membersAvatars.add(bitmap);
                            }
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                finalHolder1.avatar.setImageBitmap(JoinBitmaps.createBitmap(LocalDisplay.dp2px(56),
                                        LocalDisplay.dp2px(56), membersAvatars));
                            }
                        });


                    }
                }).start();
            } else {
                holder.avatar.setImageResource(R.drawable.messages_bg_useravatar);
            }
            if (user != null) {
                if (user.getNickName() != null) holder.name.setText(user.getNickName());
                else holder.name.setText(user.getUserId() + "");
            } else holder.name.setText(conversation.getFriendId() + "");
        } else {
            if (user != null) {
                // 本地或者服务器获取用户详情，以用来显示头像和nick
//                holder.avatar.setBackgroundResource(R.drawable.default_avatar);
                final ViewHolder finalHolder = holder;
                if (user.getUserId() == 10001) {
                    finalHolder.avatar.setImageResource(R.drawable.icon_avatar_wenwen);
                } else if (user.getUserId() == 10000) {
                    finalHolder.avatar.setImageResource(R.drawable.icon_avatar_paipai);
                } else {
                    finalHolder.avatar.setTag(user.getAvatarSmall());
                    ImageLoader.getInstance().displayImage(user.getAvatarSmall(), finalHolder.avatar, options);
                }
//                ImageLoader.getInstance().loadImage(imUser.getAvatar(), avatarSize, UILUtils.getDefaultOption(), new SimpleImageLoadingListener() {
//                    @Override
//                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                        super.onLoadingComplete(imageUri, view, loadedImage);
//                        if (imageUri == null) {
//                            return;
//                        }
//                        if (imageUri.equals(finalHolder.avatar.getTag())) {
//                            if (loadedImage == null) {
//                                loadedImage = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.avatar_placeholder);
//                            }
//                            ArrayList<Bitmap> bmps = new ArrayList<Bitmap>();
//                            bmps.add(loadedImage);
//                            finalHolder.avatar.setImageBitmap(JoinBitmaps.createBitmap(LocalDisplay.dp2px(56),
//                                    LocalDisplay.dp2px(56), bmps));
//                        }
//
//
//                    }
//                });
//                ImageLoader.getInstance().displayImage(imUser.getAvatar(), holder.avatar, options);
//                if (username.equals(Constant.GROUP_USERNAME)) {
//                    holder.name.setText("群聊");
//
//                } else if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
//                    holder.name.setText("申请与通知");
//                }
                if (TextUtils.isEmpty(user.getMemo())) {
                    finalHolder.name.setText(user.getNickName());
                } else {
                    finalHolder.name.setText(user.getMemo() + "(" + user.getNickName() + ")");
                }
            } else {
                holder.name.setText("");
            }

        }

        if (conversation.getIsRead() != 0) {
            // 显示与此用户的消息未读数
            holder.unreadLabel.setText(String.valueOf(conversation.getIsRead()));
            holder.unreadLabel.setVisibility(View.VISIBLE);
        } else {
            holder.unreadLabel.setVisibility(View.GONE);
        }

        if (conversation.getLastMessage() != null) {
            // 把最后一条消息的内容作为item的message内容
            String lastMessage = conversation.getLastMessage();
            holder.time.setText(CommonUtils.getTimestampString(new Date(conversation.getLastChatTime())));
            if (conversation.getSendType() == TYPE_SEND && conversation.getStatus() == 2) {
//                holder.msgState.setVisibility(View.VISIBLE);
                Drawable drawable = getContext().getResources().getDrawable(R.drawable.ic_message_send_fail);
                drawable.setBounds(1, 1, LocalDisplay.dp2px(15), LocalDisplay.dp2px(15));
                holder.message.setCompoundDrawables(drawable, null, null, null);
            } else if (conversation.getSendType() == TYPE_SEND && conversation.getStatus() == 1) {
                Drawable drawable = getContext().getResources().getDrawable(R.drawable.ic_message_inprogress);
                drawable.setBounds(1, 1, LocalDisplay.dp2px(15), LocalDisplay.dp2px(15));
                holder.message.setCompoundDrawables(drawable, null, null, null);
            } else {
//                holder.msgState.setVisibility(View.GONE);
                holder.message.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
            holder.message.setText(SmileUtils.getSmiledText(getContext(), getMessageDigest(conversation, (this.getContext()), isGroup)),
                    BufferType.SPANNABLE);
        } else {
            holder.message.setText("");
            holder.time.setText("");
        }

        return convertView;
    }

    /**
     * 根据消息内容和消息类型获取消息内容提示
     *
     * @param conversationBean
     * @param context
     * @return
     */
    private String getMessageDigest(ConversationBean conversationBean, Context context, boolean isGroup) {
        String digest = "";
        //       int extType = message.getIntAttribute(Constant.EXT_TYPE, 0);
        if (isGroup) {
//            switch (conversationBean.getType()) {
//                case LOC_MSG: // 位置消息
//                    if (conversationBean.getSendType() == TYPE_REV) {
//                        // 从sdk中提到了ui中，使用更简单不犯错的获取string的方法
//                        // digest = EasyUtils.getAppResourceString(context,
//                        // "location_recv");
//                        User user = UserDBManager.getInstance().getContactByUserId(conversationBean.getFriendId());
//                        digest = getString(context, R.string.location_recv);
//                        if (user != null) digest = String.format(digest, user.getNickName());
//                        else digest = String.format(digest, conversationBean.getFriendId());
//                        return digest;
//                    } else {
//                        // digest = EasyUtils.getAppResourceString(context,
//                        // "location_prefix");
//                        digest = getString(context, R.string.location_prefix);
//                    }
//                    break;
//            }
        }
        switch (conversationBean.getType()) {
            case LOC_MSG: // 位置消息
                if (conversationBean.getSendType() == TYPE_REV) {
                    // 从sdk中提到了ui中，使用更简单不犯错的获取string的方法
                    // digest = EasyUtils.getAppResourceString(context,
                    // "location_recv");
                    User user = UserDBManager.getInstance().getContactByUserId(conversationBean.getFriendId());
                    digest = getString(context, R.string.location_recv);
                    if (user != null) digest = String.format(digest, user.getNickName());
                    else digest = String.format(digest, conversationBean.getFriendId());
                    return digest;
                } else {
                    // digest = EasyUtils.getAppResourceString(context,
                    // "location_prefix");
                    digest = getString(context, R.string.location_prefix);
                }
                break;
            case IMAGE_MSG: // 图片消息
                //ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
                digest = digest + getString(context, R.string.picture);
                break;
            case VOICE_MSG:// 语音消息
                digest = digest + getString(context, R.string.voice);
                break;
            case VIDEO_MSG: // 视频消息
                digest = digest + getString(context, R.string.video);
                break;
            case TEXT_MSG: // 文本消息
                //         if (!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {

                digest = digest + conversationBean.getLastMessage();
//                    if (extType == 0) {
//                        TextMessageBody txtBody = (TextMessageBody) message.getBody();
//                        digest = digest + txtBody.getMessage();
//                    } else if (extType == Constant.ExtType.TIPS) {
//                        digest = content;
//                    } else {
//                        digest = digest + "[链接]";
//                    }

//                } else {
//                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
//                    digest = getString(context, R.string.voice_call) ;
//                }
                break;
//            case FILE: // 普通文件消息
//                digest = digest + getString(context, R.string.file);
//                break;
            case GUIDE_MSG:
            case CITY_MSG:
            case TRAVEL_MSG:
            case SPOT_MSG:
            case FOOD_MSG:
            case SHOP_MSG:
            case HOTEL_MSG:
                try {
                    JSONObject jsonObject = new JSONObject(conversationBean.getLastMessage());
                    digest = digest + "[链接] " + jsonObject.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case TIPS_MSG:
                digest = "[系统消息]";
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

    String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }
}
