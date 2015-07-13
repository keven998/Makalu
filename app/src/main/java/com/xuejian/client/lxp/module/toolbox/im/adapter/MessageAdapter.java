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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.utils.GsonTools;
import com.lv.Listener.HttpCallback;
import com.lv.Listener.UploadListener;
import com.lv.Utils.Config;
import com.lv.Utils.CryptUtils;
import com.lv.bean.MessageBean;
import com.lv.im.IMClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.BaseActivity;
import com.xuejian.client.lxp.bean.ExtMessageBean;
import com.xuejian.client.lxp.bean.TravelNoteBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.task.DownloadImage;
import com.xuejian.client.lxp.common.task.DownloadVoice;
import com.xuejian.client.lxp.common.task.LoadImageTask;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.ImageCache;
import com.xuejian.client.lxp.common.utils.IntentUtils;
import com.xuejian.client.lxp.common.utils.SmileUtils;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.dest.CityDetailActivity;
import com.xuejian.client.lxp.module.dest.StrategyActivity;
import com.xuejian.client.lxp.module.toolbox.HisMainPageActivity;
import com.xuejian.client.lxp.module.toolbox.im.BaiduMapActivity;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;
import com.xuejian.client.lxp.module.toolbox.im.ContextMenu;
import com.xuejian.client.lxp.module.toolbox.im.ShowBigImage;
import com.xuejian.client.lxp.module.toolbox.im.VoicePlayClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MessageAdapter extends BaseAdapter {

    private final static String TAG = "msg";

    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
    private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
    private static final int MESSAGE_TYPE_RECV_LOCATION = 4;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
    private static final int MESSAGE_TYPE_SENT_VOICE = 6;
    private static final int MESSAGE_TYPE_RECV_VOICE = 7;
    //    private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
//    private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
//    private static final int MESSAGE_TYPE_SENT_FILE = 10;
//    private static final int MESSAGE_TYPE_RECV_FILE = 11;
//    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 12;
//    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 13;
    private static final int MESSAGE_TYPE_SENT_EXT = 14;
    private static final int MESSAGE_TYPE_RECV_EXT = 15;
    private static final int MESSAGE_TYPE_TIPS = 16;
//    private static final int MESSAGE_TYPE_SENT_UNKOWN = 17;
//    private static final int MESSAGE_TYPE_RECV_UNKOWN = 18;

    public static final String IMAGE_DIR = "chat/image/";
    //    public static final String VOICE_DIR = "chat/audio/";
//    public static final String VIDEO_DIR = "chat/video";
    private static final int TEXT_MSG = 0;
    private static final int VOICE_MSG = 1;
    private static final int IMAGE_MSG = 2;
    private static final int LOC_MSG = 3;
    private static final int POI_MSG = 5;
    private static final int VIDEO_MSG = 6;
    private static final int FILE_MSG = 7;
    private static final int PLAN_MSG = 10;
    private static final int CITY_MSG = 11;
    private static final int TRAVEL_MSG = 12;
    private static final int SPOT_MSG = 13;
    private static final int FOOD_MSG = 14;
    private static final int SHOP_MSG = 15;
    private static final int HOTEL_MSG = 16;

    private static final int TIP_MSG = 99;
    private static final int TYPE_SEND = 0;
    private static final int TYPE_REV = 1;
    public static boolean isRead;
    private String friendId;
    private LayoutInflater inflater;
    private Activity activity;
    private HashMap<Long, User> groupMembers = new HashMap<Long, User>();
    private DisplayImageOptions picOptions;
    private Context context;
    private String chatType;
    private String conversation;
    private HashMap<Long, Timer> timers = new HashMap<>();
    String progress1;

    public MessageAdapter(Context context, String friendId, String chatType, String conversation) {
        this.friendId = friendId;
        this.context = context;
        this.chatType = chatType;
        this.conversation = conversation;
        inflater = LayoutInflater.from(context);
        activity = (Activity) context;
        picOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .resetViewBeforeLoading(true)
                .showImageOnFail(R.drawable.ic_home_talklist_default_avatar)
                .showImageOnLoading(R.drawable.messages_bg_useravatar)
                .showImageForEmptyUri(R.drawable.ic_home_talklist_default_avatar)
                .displayer(new RoundedBitmapDisplayer(context.getResources().getDimensionPixelSize(R.dimen.size_avatar)))
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
    }

    /**
     * 获取item数
     */
    public int getCount() {
        return ChatActivity.messageList.size();
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });

    }

    public MessageBean getItem(int position) {
        return ChatActivity.messageList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * 获取item类型
     */
    public int getItemViewType(int position) {
        MessageBean message = ChatActivity.messageList.get(position);
        switch (message.getType()) {
            case TEXT_MSG:
                return message.getSendType() == 1 ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;

            case VOICE_MSG:
                return message.getSendType() == 1 ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;

            case IMAGE_MSG:
                return message.getSendType() == 1 ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;

            case LOC_MSG:
                return message.getSendType() == 1 ? MESSAGE_TYPE_RECV_LOCATION : MESSAGE_TYPE_SENT_LOCATION;

            case TIP_MSG:
                return MESSAGE_TYPE_TIPS;

            case PLAN_MSG:
            case CITY_MSG:
            case TRAVEL_MSG:
            case SPOT_MSG:
            case FOOD_MSG:
            case SHOP_MSG:
            case HOTEL_MSG:
                return message.getSendType() == 1 ? MESSAGE_TYPE_RECV_EXT : MESSAGE_TYPE_SENT_EXT;
        }
        return -1;// invalid
    }

    public int getViewTypeCount() {
        return 19;
    }

    private View createViewByMessage(MessageBean message, int position) {
        switch (message.getType()) {
            case LOC_MSG:
                return message.getSendType() == 1 ? inflater.inflate(R.layout.row_received_location, null) : inflater.inflate(
                        R.layout.row_sent_location, null);

            case IMAGE_MSG:
                return message.getSendType() == 1 ? inflater.inflate(R.layout.row_received_picture, null) : inflater.inflate(
                        R.layout.row_sent_picture, null);

            case VOICE_MSG:
                return message.getSendType() == 1 ? inflater.inflate(R.layout.row_received_voice, null) : inflater.inflate(
                        R.layout.row_sent_voice, null);

            case VIDEO_MSG:
                return message.getSendType() == 1 ? inflater.inflate(R.layout.row_received_video, null) : inflater.inflate(
                        R.layout.row_sent_video, null);

            case FILE_MSG:
                return message.getSendType() == 1 ? inflater.inflate(R.layout.row_received_file, null) : inflater.inflate(
                        R.layout.row_sent_file, null);

            case TEXT_MSG:
                return message.getSendType() == 1 ? inflater.inflate(R.layout.row_received_message, null) : inflater.inflate(
                        R.layout.row_sent_message, null);

            case TIP_MSG:
                return inflater.inflate(R.layout.row_chat_tips, null);

            case PLAN_MSG:
            case CITY_MSG:
            case TRAVEL_MSG:
            case SPOT_MSG:
            case FOOD_MSG:
            case SHOP_MSG:
            case HOTEL_MSG:
                return message.getSendType() == 1 ? inflater.inflate(R.layout.row_received_ext, null) : inflater.inflate(
                        R.layout.row_sent_ext, null);
            default:
                break;
        }
        return null;
    }

    @SuppressLint("NewApi")
    public View getView(final int position, View convertView, ViewGroup parent) {
        MessageBean message = getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = createViewByMessage(message, position);
            switch (message.getType()) {
                case TEXT_MSG:
                    holder.rl_content = (RelativeLayout) convertView.findViewById(R.id.rl_chatcontent);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
                    holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
                    holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                    holder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                    holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                    holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
                    holder.tv_attr = (TextView) convertView.findViewById(R.id.tv_attr);
                    holder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
                    break;
                case VOICE_MSG:
                    holder.iv = ((ImageView) convertView.findViewById(R.id.iv_voice));
                    holder.rl_voice_content = (RelativeLayout) convertView.findViewById(R.id.rl_voice_content);
                    holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    holder.tv = (TextView) convertView.findViewById(R.id.tv_length);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
                    holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
                    holder.iv_read_status = (ImageView) convertView.findViewById(R.id.iv_unread_voice);
                    break;
                case IMAGE_MSG:
                    holder.iv = ((ImageView) convertView.findViewById(R.id.iv_sendPicture));
                    holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    holder.tv = (TextView) convertView.findViewById(R.id.percentage);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
                    holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
                    break;
                case LOC_MSG:
                    holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    holder.tv = (TextView) convertView.findViewById(R.id.tv_location);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
                    holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
                    break;
                case TIP_MSG:
                    holder.tv_tips = (TextView) convertView.findViewById(R.id.tv_tips);
                    break;
                case PLAN_MSG:
                case CITY_MSG:
                case TRAVEL_MSG:
                case SPOT_MSG:
                case FOOD_MSG:
                case SHOP_MSG:
                case HOTEL_MSG:
                    holder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                    holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                    holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
                    holder.tv_attr = (TextView) convertView.findViewById(R.id.tv_attr);
                    holder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
                    holder.rl_content = (RelativeLayout) convertView.findViewById(R.id.rl_chatcontent);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
                    holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
                    holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                    break;
                default:
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch (message.getType()) {
            // 根据消息type显示item
            case IMAGE_MSG: // 图片
                handleGroupMessage(position, convertView, message, holder);
                handleImageMessage(message, holder, position, convertView);
                handleCommonMessage(position, convertView, message, holder);
                break;
            case TEXT_MSG: // 文本
//                if (!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
//                    int extType = message.getIntAttribute(Constant.EXT_TYPE, 0);
//                    if (extType == 0) {
                handleGroupMessage(position, convertView, message, holder);
                handleTextMessage(message, holder, position);
                handleCommonMessage(position, convertView, message, holder);
//                    } else if (extType < 100) {
//                        handleGroupMessage(position, convertView, message, holder);
//                        handleExtMessage(message, holder, position);
//                        handleCommonMessage(position, convertView, message, holder);
//                    } else if (extType == Constant.ExtType.TIPS) {
//                        handleTipsMessage(message, holder, position);
//                    }
//
//                } else {
//                    // 语音电话
//                    handleGroupMessage(position, convertView, message, holder);
//                    handleVoiceCallMessage(message, holder, position);
//                    handleCommonMessage(position, convertView, message, holder);
                //               }

                break;
            case LOC_MSG: // 位置
                handleGroupMessage(position, convertView, message, holder);
                handleLocationMessage(message, holder, position, convertView);
                handleCommonMessage(position, convertView, message, holder);
                break;
            case VOICE_MSG: // 语音
                handleGroupMessage(position, convertView, message, holder);
                handleVoiceMessage(message, holder, position, convertView);
                handleCommonMessage(position, convertView, message, holder);
                break;
            case TIP_MSG:
                handleTipsMessage(message, holder, position);
                break;
            case PLAN_MSG:
            case CITY_MSG:
            case TRAVEL_MSG:
            case SPOT_MSG:
            case FOOD_MSG:
            case SHOP_MSG:
            case HOTEL_MSG:
                handleGroupMessage(position, convertView, message, holder);
                handleExtMessage(message, holder, position);
                handleCommonMessage(position, convertView, message, holder);
                break;
//            case VIDEO: // 视频
//                handleGroupMessage(position, convertView, message, holder);
//                handleVideoMessage(message, holder, position, convertView);
//                handleCommonMessage(position, convertView, message, holder);
//                break;
//            case FILE: // 一般文件
//                handleGroupMessage(position, convertView, message, holder);
//                handleFileMessage(message, holder, position, convertView);
//                handleCommonMessage(position, convertView, message, holder);
//                break;
            default:
                break;
        }


        TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
        if (position == 0) {
            timestamp.setText(CommonUtils.getTimestampString(new Date(message.getCreateTime())));
            timestamp.setVisibility(View.VISIBLE);
        } else {
            // 两条消息时间离得如果稍长，显示时间
            if (CommonUtils.isCloseEnough(message.getCreateTime(), ChatActivity.messageList.get(position - 1).getCreateTime())) {
                timestamp.setVisibility(View.GONE);
            } else {
                timestamp.setText(CommonUtils.getTimestampString(new Date(message.getCreateTime())));
                timestamp.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    private void handleTipsMessage(MessageBean message, ViewHolder holder, final int position) {
        holder.tv_tips.setText(message.getMessage());

    }

    private void handleGroupMessage(final int position, View convertView, final MessageBean message, ViewHolder holder) {
        // 群聊时，显示接收的消息的发送人的名称
        if (message.getSendType() == TYPE_REV) {
            if ("group".equals(chatType)) {
                // demo用username代替nick
                User user = groupMembers.get(message.getSenderId());
                if (user == null) {
                    user = UserDBManager.getInstance().getContactByUserId(message.getSenderId());
                    if (user == null) {
                        // user = IMUtils.getUserInfoFromMessage(context, message);
                    }
                    groupMembers.put(message.getSenderId(), user);

                }
                if (user != null) {
                    holder.tv_userId.setText(user.getNickName());
                    ImageLoader.getInstance().displayImage(user.getAvatarSmall(), holder.head_iv, picOptions);
                }
            } else {
                // User user = AccountManager.getInstance().getContactList(activity).get(friendId);
                User user = UserDBManager.getInstance().getContactByUserId(message.getSenderId());
                if (user != null) {
                    holder.tv_userId.setText(user.getNickName());
                    ImageLoader.getInstance().displayImage(user.getAvatarSmall(), holder.head_iv, picOptions);
                }
            }
        } else {
            // User user = AccountManager.getInstance().getLoginAccount(context);
            User user = UserDBManager.getInstance().getContactByUserId(Long.parseLong(AccountManager.getCurrentUserId()));
            if (user != null) {
                ImageLoader.getInstance().displayImage(user.getAvatarSmall(), holder.head_iv, picOptions);
            }
        }

        // 如果是发送的消息并且不是群聊消息，显示已读textview
        if (message.getSendType() == 0 && "single".equals(chatType)) {
            holder.tv_ack = (TextView) convertView.findViewById(R.id.tv_ack);
            holder.tv_delivered = (TextView) convertView.findViewById(R.id.tv_delivered);
            if (holder.tv_ack != null) {
                if (message.getStatus() == 0) {
                    if (holder.tv_delivered != null) {
                        holder.tv_delivered.setVisibility(View.INVISIBLE);
                    }
                    holder.tv_ack.setVisibility(View.INVISIBLE);
                } else {
                    holder.tv_ack.setVisibility(View.INVISIBLE);

                    // check and display msg delivered ack status
                    if (holder.tv_delivered != null) {
                        //  if (message.isDelivered) {
                        holder.tv_delivered.setVisibility(View.INVISIBLE);
                        //   } else {
                        //       holder.tv_delivered.setVisibility(View.INVISIBLE);
                        //   }
                    }
                }
            }
        }
//        } else {
//            // 如果是文本或者地图消息并且不是group messgae，显示的时候给对方发送已读回执
//            if ((message.getType() == Type.TXT || message.getType() == Type.LOCATION) && !message.isAcked && chatType != ChatType.GroupChat) {
//                // 不是语音通话记录
//                if (!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
//                    try {
//                        EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
//                        // 发送已读回执
//                        message.isAcked = true;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }

    }

    private void handleCommonMessage(final int position, View convertView, final MessageBean message, ViewHolder holder) {

        if (message.getSendType() == 0) {
            View statusView = convertView.findViewById(R.id.msg_status);
            // 重发按钮点击事件
            statusView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 显示重发消息的自定义alertdialog
                    switch (message.getType()) {
                        case TEXT_MSG:
                        case VOICE_MSG:
                        case IMAGE_MSG:
                        case LOC_MSG:
                        case VIDEO_MSG:
                            //添加扩展类型的跳转
                        case PLAN_MSG:
                        case CITY_MSG:
                        case TRAVEL_MSG:
                        case SPOT_MSG:
                        case FOOD_MSG:
                        case SHOP_MSG:
                        case HOTEL_MSG:
                            ((ChatActivity) activity).resendMessage(position);
                            break;
                    }
                }
            });

        } else {
            // 点击头像进入详情
            holder.head_iv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, HisMainPageActivity.class);
                    User user = UserDBManager.getInstance().getContactByUserId(message.getSenderId());
                    intent.putExtra("userId", user.getUserId());
                    intent.putExtra("userNick", user.getNickName());
                    context.startActivity(intent);
                }
            });
        }
    }

    /**
     * 文本消息
     *
     * @param message
     * @param holder
     * @param position
     */
    private void handleTextMessage(MessageBean message, ViewHolder holder, final int position) {
        Spannable span = SmileUtils.getSmiledText(context, message.getMessage());
        // 设置内容
        holder.tv.setText(SmileUtils.getSmiledText(context, message.getMessage()));
        // 设置长按事件监听
        holder.tv.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult(
                        (new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type",
                                TEXT_MSG), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        if (message.getSendType() == 0) {
            switch (message.getStatus()) {
                case 0: // 发送成功
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                case 1: // 发送中
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.staus_iv.setVisibility(View.GONE);
                    sendMsgInBackground(message, holder);
                    break;
                case 2: // 发送失败
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;

            }
        }
    }

    /**
     * 自定义消息
     *
     * @param message
     * @param holder
     * @param position
     */
    private void handleExtMessage(MessageBean message, final ViewHolder holder, final int position) {
        final int extType = message.getType();
        final String conent = message.getMessage();
        ExtMessageBean bean = null;
        bean = GsonTools.parseJsonToBean(conent, ExtMessageBean.class);
        final ExtMessageBean finalBean = bean;
        holder.tv_attr.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        if (extType == PLAN_MSG) {
            holder.tv_attr.setVisibility(View.VISIBLE);
            holder.tv_name.setText(bean.name);
            holder.tv_desc.setText(bean.desc);
            holder.tv_attr.setText(bean.timeCost);
            ImageLoader.getInstance().displayImage(bean.image, holder.iv_image, UILUtils.getRadiusOption(3));
            holder.tv_type.setText("计划");
            holder.rl_content.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StrategyActivity.class);
                    intent.putExtra("id", finalBean.id);
                    activity.startActivity(intent);
                }
            });
        } else if (extType == CITY_MSG) {
            holder.tv_name.setText(bean.name);
            holder.tv_attr.setVisibility(View.GONE);
            holder.tv_desc.setText(bean.desc);
            holder.tv_type.setText("城市");
            ImageLoader.getInstance().displayImage(bean.image, holder.iv_image, UILUtils.getRadiusOption(8));
            holder.rl_content.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CityDetailActivity.class);
                    intent.putExtra("id", finalBean.id);
                    activity.startActivity(intent);
                }
            });
        } else if (extType == TRAVEL_MSG) {
            holder.tv_name.setText(bean.name);
            holder.tv_attr.setVisibility(View.GONE);
            holder.tv_desc.setText(bean.desc);
            holder.tv_type.setText("游记");
            ImageLoader.getInstance().displayImage(bean.image, holder.iv_image, UILUtils.getRadiusOption(3));
            holder.rl_content.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    TravelNoteBean noteBean = new TravelNoteBean();
                    noteBean.setFieldFromExtMessageBean(finalBean);
                    IntentUtils.intentToNoteDetail(activity, noteBean);
                }
            });

        } else if (extType == SPOT_MSG) {
            if (TextUtils.isEmpty(bean.timeCost)) {
                holder.tv_attr.setVisibility(View.GONE);
            } else {
                holder.tv_attr.setVisibility(View.VISIBLE);
                holder.tv_attr.setText(bean.timeCost);
            }
            holder.tv_name.setText(bean.name);
            holder.tv_desc.setText(bean.desc);

            holder.tv_type.setText("景点");
            ImageLoader.getInstance().displayImage(bean.image, holder.iv_image, UILUtils.getRadiusOption(3));
            holder.rl_content.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtils.intentToDetail(activity, TravelApi.PeachType.SPOT, finalBean.id);
                }
            });
        } else if (extType == FOOD_MSG || extType == HOTEL_MSG || extType == SHOP_MSG) {
            holder.tv_attr.setVisibility(View.VISIBLE);
            switch (extType) {
                case FOOD_MSG:
                    holder.tv_name.setText(bean.name);
                    holder.tv_type.setText("美食");
                    holder.tv_attr.setText(bean.rating + " " + bean.price);
                    break;

                case HOTEL_MSG:
                    holder.tv_name.setText(bean.name);
                    holder.tv_type.setText("酒店");
                    holder.tv_attr.setText(bean.rating + " " + bean.price);
                    break;

                case SHOP_MSG:
                    holder.tv_name.setText(bean.name);
                    holder.tv_type.setText("购物");
                    holder.tv_attr.setText(bean.rating + " ");
                    break;
            }
            if (message.getSendType() == TYPE_REV) {
                holder.tv_attr.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rating_start_highlight, 0, 0, 0);
            } else {
                holder.tv_attr.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rating_start_default, 0, 0, 0);
            }

            holder.tv_desc.setText(bean.address);
            ImageLoader.getInstance().displayImage(bean.image, holder.iv_image, UILUtils.getRadiusOption(3));
            holder.rl_content.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (extType) {
                        case FOOD_MSG:
                            IntentUtils.intentToDetail(activity, TravelApi.PeachType.RESTAURANTS, finalBean.id);
                            break;

                        case HOTEL_MSG:
                            IntentUtils.intentToDetail(activity, TravelApi.PeachType.HOTEL, finalBean.id);
                            break;

                        case SHOP_MSG:
                            IntentUtils.intentToDetail(activity, TravelApi.PeachType.SHOPPING, finalBean.id);
                            break;
                    }

                }
            });
        } else {
            holder.tv.setText("本版本不支持此消息类型，请升级最新版本！");
        }
        holder.rl_content.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult(
                        (new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type",
                                -1), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        if (message.getSendType() == TYPE_SEND) {
            switch (message.getStatus()) {
                case 0: // 发送成功
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                case 2: // 发送失败
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                case 1: // 发送中
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                default:
                    break;
                // 发送消息
                //       sendMsgInBackground(message, holder);
            }
        }


    }

    /**
     * 语音通话记录
     *
     * @param message
     * @param holder
     * @param position
     */
    private void handleVoiceCallMessage(MessageBean message, ViewHolder holder, final int position) {
//        TextMessageBody txtBody = (TextMessageBody) message.getBody();
//        holder.tv.setText(txtBody.getMessage());

    }

    /**
     * 图片消息
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleImageMessage(final MessageBean message, final ViewHolder holder, final int position, View convertView) {
        holder.pb.setTag(position);
        holder.iv.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult(
                        (new Intent(activity, ContextMenu.class)).putExtra("position", position)
                        //   .putExtra("type", EMMessage.Type.IMAGE.ordinal())
                        , ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        // 接收方向的消息
        if (message.getSendType() == TYPE_REV) {
            Bitmap defaultImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_image);
            // "it is receive msg";
            if (message.getStatus() == 1) {
                holder.iv.setImageBitmap(defaultImage);
                showDownloadImageProgress(message, holder);
            } else if (message.getStatus() == 2) {
                return;
            } else if (message.getStatus() == 0) {
                holder.pb.setVisibility(View.GONE);
                holder.tv.setVisibility(View.GONE);
                holder.iv.setImageBitmap(defaultImage);
                String thumbpath = getStringAttr(message, "thumbPath");
                String romotePath = getStringAttr(message, "full");
                String BigImageFilename = Config.DownLoadImage_path + CryptUtils.getMD5String(message.getSenderId() + "") + "/" + CryptUtils.getMD5String(romotePath) + ".jpeg";
                if (thumbpath != null) {
                    showImageView(thumbpath, holder.iv, BigImageFilename, romotePath, message);
                }


//                // "!!!! not back receive, show image directly");
//                holder.pb.setVisibility(View.GONE);
//                holder.tv.setVisibility(View.GONE);
//                holder.iv.setImageBitmap(defaultImage);
//                String url=null;
//                try {
//                    url=new JSONObject(message.getMessage()).getString("url");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                if (url!= null) {
//                    // String filePath = imgBody.getLocalUrl();
//                    String remotePath = imgBody.getRemoteUrl();
//                    String filePath = ImageUtils.getImagePath(remotePath);
//                    String thumbRemoteUrl = imgBody.getThumbnailUrl();
//                    String thumbnailPath = ImageUtils.getThumbnailImagePath(thumbRemoteUrl);
//                    showImageView(thumbnailPath, holder.iv, filePath, imgBody.getRemoteUrl(), message);
//                }
            }
            return;
        }
        // 发送的消息
        String localPath = getStringAttr(message, "localPath");
        String thumbPath = getThumbImagepath(message);
        if (localPath != null && new File(localPath).exists()) {
            showImageView(thumbPath, holder.iv, localPath, null, message);
        } else {
            showImageView(thumbPath, holder.iv, localPath, IMAGE_DIR, message);
        }

        switch (message.getStatus()) {
            case 0:
                holder.pb.setVisibility(View.GONE);
                holder.tv.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.GONE);
                break;
            case 2:
                holder.pb.setVisibility(View.GONE);
                holder.tv.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.VISIBLE);
                break;
            case 1:
                holder.staus_iv.setVisibility(View.GONE);
                holder.pb.setVisibility(View.VISIBLE);
                holder.tv.setVisibility(View.VISIBLE);
//                if (timers.containsKey(message.getLocalId() + "")){
//                    System.out.println(message.getLocalId() + " return ========");
//                    return;
//                }

                // set a timer
                //    if (message.getStatus()==1) sendPictureMessage(message, holder);
                if (timers.containsKey(message.getLocalId())) {
                    if (Config.isDebug) {
                        Log.i(Config.TAG, "already exist time Task");
                    }
                    return;
                }

                sendPictureMessage(message, holder);
                final Timer timer = new Timer();
                timers.put(message.getLocalId(), timer);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                holder.pb.setVisibility(View.VISIBLE);
                                holder.tv.setVisibility(View.VISIBLE);
                                int progress = IMClient.getInstance().getProgress(friendId + message.getLocalId());
                                holder.tv.setText(progress + "%");
                                if (progress == 100) {
                                    holder.pb.setVisibility(View.GONE);
                                    holder.tv.setVisibility(View.GONE);
                                    timer.cancel();
                                } else if (message.getStatus() == 0) {
                                    holder.pb.setVisibility(View.GONE);
                                    holder.tv.setVisibility(View.GONE);
                                    timer.cancel();
                                } else if (message.getStatus() == 2) {
                                    holder.pb.setVisibility(View.GONE);
                                    holder.tv.setVisibility(View.GONE);
                                    // message.setSendingStatus(Message.SENDING_STATUS_FAIL);
                                    // message.setProgress(0);
                                    holder.staus_iv.setVisibility(View.VISIBLE);
//                                    Toast.makeText(activity,
//                                            activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT)
//                                            .show();
                                    if (activity != null && !activity.isFinishing())
                                        ToastUtil.getInstance(activity).showToast("呃~好像没找到网络");
                                    timer.cancel();
                                }

                            }
                        });

                    }
                }, 0, 500);
                break;
            default:
        }
    }

    private double getDoubleAttr(MessageBean message, String name) {
        try {
            JSONObject object = new JSONObject(message.getMessage());
            return object.getDouble(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }

    }

    private String getStringAttr(MessageBean message, String name) {
        try {
            JSONObject object = new JSONObject(message.getMessage());
            return object.getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    private String getThumbImagepath(MessageBean message) {
        try {
            JSONObject object = new JSONObject(message.getMessage());
            return object.getString("thumbPath");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 视频消息
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleVideoMessage(final MessageBean message, final ViewHolder holder, final int position, View convertView) {
//
//        VideoMessageBody videoBody = (VideoMessageBody) message.getBody();
//        // final File image=new File(PathUtil.getInstance().getVideoPath(),
//        // videoBody.getFileName());
//        String localThumb = videoBody.getLocalThumb();
//
//        holder.iv.setOnLongClickListener(new OnLongClickListener() {
//
//            @Override
//            public boolean onLongClick(View v) {
//                activity.startActivityForResult(
//                        new Intent(activity, ContextMenu.class).putExtra("position", position).putExtra("type",
//                                Type.VIDEO.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
//                return true;
//            }
//        });
//
//        if (localThumb != null) {
//
//            showVideoThumbView(localThumb, holder.iv, videoBody.getThumbnailUrl(), message);
//        }
//        if (videoBody.getLength() > 0) {
//            String time = DateUtils.toTimeBySecond(videoBody.getLength());
//            holder.timeLength.setText(time);
//        }
//        holder.playBtn.setImageResource(R.drawable.video_download_btn_nor);
//
//        if (message.direct == EMMessage.Direct.RECEIVE) {
//            if (videoBody.getVideoFileLength() > 0) {
//                String size = TextFormater.getDataSize(videoBody.getVideoFileLength());
//                holder.size.setText(size);
//            }
//        } else {
//            if (videoBody.getLocalUrl() != null && new File(videoBody.getLocalUrl()).exists()) {
//                String size = TextFormater.getDataSize(new File(videoBody.getLocalUrl()).length());
//                holder.size.setText(size);
//            }
//        }
//
//        if (message.direct == EMMessage.Direct.RECEIVE) {
//
//            // System.err.println("it is receive msg");
//            if (message.status == EMMessage.Status.INPROGRESS) {
//                // System.err.println("!!!! back receive");
//                holder.iv.setImageResource(R.drawable.default_image);
//                showDownloadImageProgress(message, holder);
//
//            } else {
//                // System.err.println("!!!! not back receive, show image directly");
//                holder.iv.setImageResource(R.drawable.default_image);
//                if (localThumb != null) {
//                    showVideoThumbView(localThumb, holder.iv, videoBody.getThumbnailUrl(), message);
//                }
//
//            }
//
//            return;
//        }
//        holder.pb.setTag(position);
//
//        // until here ,deal with send video msg
//        switch (message.status) {
//            case SUCCESS:
//                holder.pb.setVisibility(View.GONE);
//                holder.staus_iv.setVisibility(View.GONE);
//                holder.tv.setVisibility(View.GONE);
//                break;
//            case FAIL:
//                holder.pb.setVisibility(View.GONE);
//                holder.tv.setVisibility(View.GONE);
//                holder.staus_iv.setVisibility(View.VISIBLE);
//                break;
//            case INPROGRESS:
//                if (timers.containsKey(message.getMsgId()))
//                    return;
//                // set a timer
//                final Timer timer = new Timer();
//                timers.put(message.getMsgId(), timer);
//                timer.schedule(new TimerTask() {
//
//                    @Override
//                    public void run() {
//                        activity.runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                holder.pb.setVisibility(View.VISIBLE);
//                                holder.tv.setVisibility(View.VISIBLE);
//                                holder.tv.setText(message.progress + "%");
//                                if (message.status == EMMessage.Status.SUCCESS) {
//                                    holder.pb.setVisibility(View.GONE);
//                                    holder.tv.setVisibility(View.GONE);
//                                    // message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
//                                    timer.cancel();
//                                } else if (message.status == EMMessage.Status.FAIL) {
//                                    holder.pb.setVisibility(View.GONE);
//                                    holder.tv.setVisibility(View.GONE);
//                                    // message.setSendingStatus(Message.SENDING_STATUS_FAIL);
//                                    // message.setProgress(0);
//                                    holder.staus_iv.setVisibility(View.VISIBLE);
////                                    Toast.makeText(activity,
////                                            activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT)
////                                            .show();
//                                    if (activity != null && !activity.isFinishing())
//                                        ToastUtil.getInstance(activity).showToast("呃~好像没找到网络");
//                                    timer.cancel();
//                                }
//
//                            }
//                        });
//
//                    }
//                }, 0, 500);
//                break;
//            default:
//                // sendMsgInBackground(message, holder);
//                sendPictureMessage(message, holder);
//
//        }

    }

    /**
     * 语音消息
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleVoiceMessage(final MessageBean message, final ViewHolder holder, final int position, View convertView) {

        String filepath = (String) getVoiceFilepath(message, "path");
        String durtime = getVoiceFilepath(message, "duration") + "";
        isRead = (boolean) getVoiceFilepath(message, "isRead");
        holder.tv.setText((int) Math.floor(Double.valueOf(durtime)) + "´´");
        holder.rl_voice_content.setOnClickListener(new VoicePlayClickListener(friendId, message, holder.iv, holder.iv_read_status, this, activity, friendId, chatType, isRead, filepath));
        holder.rl_voice_content.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult(
                        (new Intent(activity, ContextMenu.class)).putExtra("position", position)
                        //   .putExtra("type", Type.VOICE.ordinal())
                        , ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });
        if (((ChatActivity) activity).playMsgId != null
                && ((ChatActivity) activity).playMsgId.equals(String.valueOf(message
                .getLocalId())) && VoicePlayClickListener.isPlaying) {
            AnimationDrawable voiceAnimation;
            if (message.getSendType() == TYPE_REV) {
                holder.iv.setImageResource(R.anim.voice_from_icon);
            } else {
                holder.iv.setImageResource(R.anim.voice_to_icon);
            }
            voiceAnimation = (AnimationDrawable) holder.iv.getDrawable();
            voiceAnimation.start();
        } else {
            if (message.getSendType() == TYPE_REV) {
                holder.iv.setImageResource(R.drawable.chatfrom_voice_playing);
            } else {
                holder.iv.setImageResource(R.drawable.chatto_voice_playing);
            }
        }

        if (message.getSendType() == TYPE_REV) {
            if (isRead) {
                // 隐藏语音未读标志
                holder.iv_read_status.setVisibility(View.INVISIBLE);
            } else {
                holder.iv_read_status.setVisibility(View.VISIBLE);
            }
            if (message.getStatus() == 1) {
                holder.pb.setVisibility(View.VISIBLE);
                String url = null;
                try {
                    url = new JSONObject(message.getMessage()).get("url").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (url == null || "".equals(url)) {
                    holder.pb.setVisibility(View.INVISIBLE);
                    return;
                }
                String filename = Config.DownLoadAudio_path + CryptUtils.getMD5String(AccountManager.getCurrentUserId()) + "/" + CryptUtils.getMD5String(url) + ".amr";
                new DownloadVoice(url, filename).download(new DownloadVoice.DownloadListener() {
                    @Override
                    public void onSuccess() {
                        IMClient.getInstance().updateMessage(friendId, message.getLocalId(), null, null, 0, 0, null, Config.AUDIO_MSG);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.pb.setVisibility(View.INVISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress) {

                    }

                    @Override
                    public void onFail() {
                        IMClient.getInstance().updateMessage(friendId, message.getLocalId(), null, null, 0, 2, null, Config.AUDIO_MSG);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.pb.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                });
            } else {
                holder.pb.setVisibility(View.INVISIBLE);
            }
            return;
        }
        switch (message.getStatus()) {
            case 0:
                holder.pb.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.GONE);
                break;
            case 2:
                holder.pb.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.VISIBLE);
                break;
            case 1:
                holder.pb.setVisibility(View.VISIBLE);
                holder.staus_iv.setVisibility(View.GONE);
                IMClient.getInstance().sendAudioMessage(AccountManager.getCurrentUserId(), message, filepath, friendId, new UploadListener() {
                    @Override
                    public void onSucess(String fileUrl) {
                        message.setStatus(0);
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                holder.pb.setVisibility(View.INVISIBLE);
                                updateStatus(message, 0);
                                notifyDataSetChanged();
                            }
                        });

                    }

                    @Override
                    public void onError(int errorCode, String msg) {
                        message.setStatus(2);
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                holder.pb.setVisibility(View.INVISIBLE);
                                updateStatus(message, 2);
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                }, chatType);
                break;
            default:
                break;
        }
    }

    private Object getVoiceFilepath(MessageBean message, String name) {
        try {
            JSONObject object = new JSONObject(message.getMessage());
            return object.get(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件消息
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
//    private void handleFileMessage(final EMMessage message, final ViewHolder holder, int position, View convertView) {
//        final NormalFileMessageBody fileMessageBody = (NormalFileMessageBody) message.getBody();
//        final String filePath = fileMessageBody.getLocalUrl();
//        holder.tv_file_name.setText(fileMessageBody.getFileName());
//        holder.tv_file_size.setText(TextFormater.getDataSize(fileMessageBody.getFileSize()));
//        holder.ll_container.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                File file = new File(filePath);
//                if (file != null && file.exists()) {
//                    // 文件存在，直接打开
//                    FileUtils.openFile(file, (Activity) context);
//                } else {
//                    // 下载
//                    context.startActivity(new Intent(context, ShowNormalFileActivity.class).putExtra("msgbody", fileMessageBody));
//                }
//                if (message.direct == EMMessage.Direct.RECEIVE && !message.isAcked) {
//                    try {
//                        EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
//                        message.isAcked = true;
//                    } catch (EaseMobException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//
//        if (message.direct == EMMessage.Direct.RECEIVE) { // 接收的消息
////            System.err.println("it is receive msg");
//            File file = new File(filePath);
//            if (file != null && file.exists()) {
//                holder.tv_file_download_state.setText("已下载");
//            } else {
//                holder.tv_file_download_state.setText("未下载");
//            }
//            return;
//        }
//
//        // until here, deal with send voice msg
//        switch (message.status) {
//            case SUCCESS:
//                holder.pb.setVisibility(View.INVISIBLE);
//                holder.tv.setVisibility(View.INVISIBLE);
//                holder.staus_iv.setVisibility(View.INVISIBLE);
//                break;
//            case FAIL:
//                holder.pb.setVisibility(View.INVISIBLE);
//                holder.tv.setVisibility(View.INVISIBLE);
//                holder.staus_iv.setVisibility(View.VISIBLE);
//                break;
//            case INPROGRESS:
//                if (timers.containsKey(message.getMsgId()))
//                    return;
//                // set a timer
//                final Timer timer = new Timer();
//                timers.put(message.getMsgId(), timer);
//                timer.schedule(new TimerTask() {
//
//                    @Override
//                    public void run() {
//                        activity.runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                holder.pb.setVisibility(View.VISIBLE);
//                                holder.tv.setVisibility(View.VISIBLE);
//                                holder.tv.setText(message.progress + "%");
//                                if (message.status == EMMessage.Status.SUCCESS) {
//                                    holder.pb.setVisibility(View.INVISIBLE);
//                                    holder.tv.setVisibility(View.INVISIBLE);
//                                    timer.cancel();
//                                } else if (message.status == EMMessage.Status.FAIL) {
//                                    holder.pb.setVisibility(View.INVISIBLE);
//                                    holder.tv.setVisibility(View.INVISIBLE);
//                                    holder.staus_iv.setVisibility(View.VISIBLE);
////                                    Toast.makeText(activity,
////                                            activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT)
////                                            .show();
//                                    ToastUtil.getInstance(activity).showToast("呃~好像没找到网络");
//                                    timer.cancel();
//                                }
//
//                            }
//                        });
//
//                    }
//                }, 0, 500);
//                break;
//            default:
//                // 发送消息
//                sendMsgInBackground(message, holder);
//        }
//
//    }

    /**
     * 处理位置消息
     */
    private void handleLocationMessage(final MessageBean message, final ViewHolder holder, final int position, View convertView) {
        TextView locationView = ((TextView) convertView.findViewById(R.id.tv_location));
        double lat = getDoubleAttr(message, "lat");
        double lng = getDoubleAttr(message, "lng");
        String desc = getStringAttr(message, "address");
        String path = getStringAttr(message, "path");
        String remote = getStringAttr(message, "snapshot");
        locationView.setText(desc);

        Bitmap bitmap = ImageCache.getInstance().get(path);
        if (bitmap != null) {
            locationView.setBackgroundDrawable(new BitmapDrawable(bitmap));
        } else
            new LoadImageTask().execute(path, null, remote, chatType, null, activity, message, locationView);


        locationView.setOnClickListener(new MapClickListener(lat, lng, desc));
        locationView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult(
                        (new Intent(activity, ContextMenu.class)).putExtra("position", position)
                        // .putExtra("type", Type.LOCATION.ordinal())
                        , ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return false;
            }
        });

        if (message.getSendType() == TYPE_REV) {
            return;
        }
        // deal with send message
        switch (message.getStatus()) {
            case 0:
                holder.pb.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.GONE);
                break;
            case 2:
                holder.pb.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.VISIBLE);
                break;
            case 1:
                holder.pb.setVisibility(View.VISIBLE);
                IMClient.getInstance().sendLocationMessage(AccountManager.getCurrentUserId(), message, getStringAttr(message, "path"), friendId, new UploadListener() {
                    @Override
                    public void onSucess(String fileUrl) {
                        message.setStatus(0);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.pb.setVisibility(View.GONE);
                                holder.staus_iv.setVisibility(View.GONE);
                                updateStatus(message, 0);
                            }
                        });
                    }

                    @Override
                    public void onError(int errorCode, String msg) {
                        message.setStatus(2);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.pb.setVisibility(View.GONE);
                                holder.staus_iv.setVisibility(View.VISIBLE);
                                updateStatus(message, 2);
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                }, chatType, lat, lng, desc);
                break;
            default:
                break;
            //  sendMsgInBackground(message, holder);
        }
    }

    /**
     * 发送消息
     */
    public void sendMsgInBackground(final MessageBean message, final ViewHolder holder) {
        holder.staus_iv.setVisibility(View.GONE);
        holder.pb.setVisibility(View.VISIBLE);
        IMClient.getInstance().sendTextMessage(message, friendId, conversation, new HttpCallback() {
            @Override
            public void onSuccess() {
                message.setStatus(0);
                updateSendedView(message, holder);
            }

            @Override
            public void onFailed(int code) {
                message.setStatus(2);
                updateSendedView(message, holder);
            }
        }, chatType);
    }

    private void showDownloadImageProgress(final MessageBean message, final ViewHolder holder) {
        if (holder.pb != null)
            holder.pb.setVisibility(View.VISIBLE);
        if (holder.tv != null)
            holder.tv.setVisibility(View.INVISIBLE);
        String thumburl = getStringAttr(message, "thumb");
        String filename = Config.DownLoadImage_path + CryptUtils.getMD5String(message.getSenderId() + "") + "/" + CryptUtils.getMD5String(thumburl) + ".jpeg";

        new DownloadImage(thumburl, filename).download(new DownloadImage.DownloadListener() {
            @Override
            public void onSuccess() {
                IMClient.getInstance().updateMessage(friendId, message.getLocalId(), null, null, 0, 0, null, Config.IMAGE_MSG);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.pb.setVisibility(View.GONE);
                        holder.tv.setVisibility(View.GONE);
                        message.setStatus(0);
                        notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onProgress(final int progress) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.tv.setText(progress + "%");

                    }
                });
            }

            @Override
            public void onFail() {
                IMClient.getInstance().updateMessage(friendId, message.getLocalId(), null, null, 0, 2, null, Config.IMAGE_MSG);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.pb.setVisibility(View.GONE);
                        holder.tv.setVisibility(View.GONE);
                        message.setStatus(2);
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void sendPictureMessage(final MessageBean message, final ViewHolder holder) {
        try {
            // before send, update ui
            holder.staus_iv.setVisibility(View.GONE);
            holder.pb.setVisibility(View.VISIBLE);
            // holder.tv.setVisibility(View.INVISIBLE);
            holder.tv.setVisibility(View.VISIBLE);
            // holder.tv.setText("0%");
            // if (chatType == ChatActivity.CHATTYPE_SINGLE) {
            IMClient.getInstance().sendImageMessage(AccountManager.getCurrentUserId(), message, friendId, new UploadListener() {
                @Override
                public void onSucess(String fileUrl) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            // send success
                            message.setStatus(0);
                            updateStatus(message, 0);
                            holder.pb.setVisibility(View.GONE);
                            holder.tv.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onError(int errorCode, String msg) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            message.setStatus(2);
                            updateStatus(message, 2);
                            holder.pb.setVisibility(View.GONE);
                            holder.tv.setVisibility(View.GONE);
                            holder.staus_iv.setVisibility(View.VISIBLE);
//                            Toast.makeText(activity,
//                                    activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT).show();
                            ToastUtil.getInstance(activity).showToast(activity.getResources().getString(R.string.request_network_failed));
                        }
                    });
                }

                @Override
                public void onProgress(final int progress) {
//                    activity.runOnUiThread(new Runnable() {
//                        public void run() {
//                            if (progress == 100) {
//                                message.setStatus(0);
//                                holder.pb.setVisibility(View.GONE);
//                                holder.tv.setVisibility(View.GONE);
//                            } else {
//                                //holder.tv.setText(progress + "%");
//                                message.setProgress(progress);
//                            }
//                        }
//                    });
                }
            }, chatType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStatus(final MessageBean messageBean, final int status) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (MessageBean m : ChatActivity.messageList) {
                    if (m.getLocalId() == messageBean.getLocalId()) m.setStatus(status);
                    notifyDataSetChanged();
                    break;
                }
            }
        });
    }

    /**
     * 更新ui上消息发送状态
     *
     * @param message
     * @param holder
     */
    private void updateSendedView(final MessageBean message, final ViewHolder holder) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // send success
                if (message.getType() == VIDEO_MSG) {
                    holder.tv.setVisibility(View.GONE);
                }
                if (message.getStatus() == 0) {
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.GONE);

                } else if (message.getStatus() == 2) {
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
//                    Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT)
//                            .show();
                    if (activity != null && !activity.isFinishing())
                        ToastUtil.getInstance(activity).showToast("呃~好像没找到网络");
                }

                notifyDataSetChanged();
            }
        });
    }

    /**
     * load image into image view
     *
     * @return the image exists or not
     */
    private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath, final String remoteDir,
                                  final MessageBean message) {
        String remote = remoteDir;
        Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
        if (bitmap != null) {
            iv.setImageBitmap(bitmap);
            iv.setClickable(true);
            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ShowBigImage.class);
                    File file = new File(localFullSizePath);
                    if (file.exists()) {
                        Uri uri = Uri.fromFile(file);
                        intent.putExtra("uri", uri);
                        intent.putExtra("downloadFilePath", localFullSizePath);
                    } else {
                        intent.putExtra("downloadFilePath", localFullSizePath);
                        intent.putExtra("remotepath", remoteDir);
                    }
                    ((BaseActivity) activity).startActivityWithNoAnim(intent);
                }
            });
            return true;
        } else {
            new LoadImageTask().execute(thumbernailPath, localFullSizePath, remote, chatType, iv, activity, message, null);
            return true;
        }

    }

    /**
     * 展示视频缩略图
     *
     * @param localThumb   本地缩略图路径
     * @param iv
     * @param thumbnailUrl 远程缩略图路径
     * @param message
     */
    private void showVideoThumbView(String localThumb, ImageView iv, String thumbnailUrl, final MessageBean message) {
        // first check if the thumbnail image already loaded into cache
//        Bitmap bitmap = ImageCache.getInstance().get(localThumb);
//        if (bitmap != null) {
//            // thumbnail image is already loaded, reuse the drawable
//            iv.setImageBitmap(bitmap);
//            iv.setClickable(true);
//            iv.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    VideoMessageBody videoBody = (VideoMessageBody) message.getBody();
////                    System.err.println("video view is on click");
//                    Intent intent = new Intent(activity, ShowVideoActivity.class);
//                    intent.putExtra("localpath", videoBody.getLocalUrl());
//                    intent.putExtra("secret", videoBody.getSecret());
//                    intent.putExtra("remotepath", videoBody.getRemoteUrl());
//                    if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked
//                            && message.getChatType() != ChatType.GroupChat) {
//                        message.isAcked = true;
//                        try {
//                            EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    activity.startActivity(intent);
//
//                }
//            });
//
//        } else {
//            new LoadVideoImageTask().execute(localThumb, thumbnailUrl, iv, activity, message, this);
//        }

    }

    public static class ViewHolder {
        ImageView iv;
        TextView tv;
        ProgressBar pb;
        ImageView staus_iv;
        ImageView head_iv;
        TextView tv_userId;
        TextView tv_type;
        TextView tv_name;
        TextView tv_attr;
        TextView tv_desc;
        ImageView iv_image;
        RelativeLayout rl_content;
        TextView tv_tips;
        ImageView playBtn;
        TextView timeLength;
        TextView size;
        LinearLayout container_status_btn;
        LinearLayout ll_container;
        RelativeLayout rl_voice_content;
        ImageView iv_read_status;
        // 显示已读回执状态
        TextView tv_ack;
        // 显示送达回执状态
        TextView tv_delivered;


        TextView tv_file_name;
        TextView tv_file_size;
        TextView tv_file_download_state;
    }

    /*
     * 点击地图消息listener
     */
    class MapClickListener implements OnClickListener {

        double latitude;
        double longitude;
        String address;

        public MapClickListener(double lat, double lng, String address) {
            latitude = lat;
            longitude = lng;
            this.address = address;

        }

        @Override
        public void onClick(View v) {
            Intent intent;
            intent = new Intent(context, BaiduMapActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            intent.putExtra("address", address);
            activity.startActivity(intent);
        }

    }

    public static MessageBean updateVoiceReadStatus(MessageBean message) {
        try {
            JSONObject object = new JSONObject(message.getMessage());
            object.put("isRead", true);
            message.setMessage(object.toString());
            return message;
        } catch (JSONException e) {
            e.printStackTrace();
            return message;
        }

    }

}