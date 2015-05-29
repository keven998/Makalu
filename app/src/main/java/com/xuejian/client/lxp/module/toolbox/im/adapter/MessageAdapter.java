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
package com.xuejian.client.lxp.module.toolbox.im.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.TextUtils;
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
import android.widget.TextView.BufferType;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.utils.GsonTools;
import com.aizou.core.utils.LocalDisplay;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.DateUtils;
import com.easemob.util.EMLog;
import com.easemob.util.FileUtils;
import com.easemob.util.LatLng;
import com.easemob.util.TextFormater;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.BaseActivity;
import com.xuejian.client.lxp.bean.ExtMessageBean;
import com.xuejian.client.lxp.bean.PeachUser;
import com.xuejian.client.lxp.bean.TravelNoteBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.task.LoadImageTask;
import com.xuejian.client.lxp.common.task.LoadVideoImageTask;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.ImageCache;
import com.xuejian.client.lxp.common.utils.ImageUtils;
import com.xuejian.client.lxp.common.utils.IntentUtils;
import com.xuejian.client.lxp.common.utils.SmileUtils;
import com.xuejian.client.lxp.config.Constant;
import com.xuejian.client.lxp.db.IMUser;
import com.xuejian.client.lxp.db.respository.IMUserRepository;
import com.xuejian.client.lxp.module.dest.CityDetailActivity;
import com.xuejian.client.lxp.module.dest.StrategyActivity;
import com.xuejian.client.lxp.module.toolbox.im.BaiduMapActivity;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;
import com.xuejian.client.lxp.module.toolbox.im.ContactDetailActivity;
import com.xuejian.client.lxp.module.toolbox.im.ContextMenu;
import com.xuejian.client.lxp.module.toolbox.im.IMAlertDialog;
import com.xuejian.client.lxp.module.toolbox.im.SeachContactDetailActivity;
import com.xuejian.client.lxp.module.toolbox.im.ShowBigImage;
import com.xuejian.client.lxp.module.toolbox.im.ShowNormalFileActivity;
import com.xuejian.client.lxp.module.toolbox.im.ShowVideoActivity;
import com.xuejian.client.lxp.module.toolbox.im.VoicePlayClickListener;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
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
    private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
    private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
    private static final int MESSAGE_TYPE_SENT_FILE = 10;
    private static final int MESSAGE_TYPE_RECV_FILE = 11;
    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 12;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 13;
    private static final int MESSAGE_TYPE_SENT_EXT = 14;
    private static final int MESSAGE_TYPE_RECV_EXT = 15;
    private static final int MESSAGE_TYPE_TIPS = 16;
    private static final int MESSAGE_TYPE_SENT_UNKOWN = 17;
    private static final int MESSAGE_TYPE_RECV_UNKOWN = 18;

    public static final String IMAGE_DIR = "chat/image/";
    public static final String VOICE_DIR = "chat/audio/";
    public static final String VIDEO_DIR = "chat/video";

    private String username;
    private LayoutInflater inflater;
    private Activity activity;
    private HashMap<String, IMUser> groupMembers = new HashMap<String, IMUser>();
    private DisplayImageOptions picOptions;

    // reference to conversation object in chatsdk
    private EMConversation conversation;

    private Context context;

    private Map<String, Timer> timers = new Hashtable<String, Timer>();

    public MessageAdapter(Context context, String username, int chatType) {
        this.username = username;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (Activity) context;
        this.conversation = EMChatManager.getInstance().getConversation(username);
        picOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .resetViewBeforeLoading(true)
                .showImageOnFail(R.drawable.avatar_placeholder_round)
                .showImageOnLoading(R.drawable.avatar_placeholder_round)
                .showImageForEmptyUri(R.drawable.avatar_placeholder_round)
//				.decodingOptions(D)
//                .displayer(new FadeInBitmapDisplayer(150, true, true, false))
                .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(8)))
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
    }

    // public void setUser(String user) {
    // this.user = user;
    // }

    /**
     * 获取item数
     */
    public int getCount() {
        return conversation.getMsgCount();
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });

    }

    public EMMessage getItem(int position) {
        return conversation.getMessage(position);
    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * 获取item类型
     */
    public int getItemViewType(int position) {
        EMMessage message = conversation.getMessage(position);
        if (message.getType() == Type.TXT) {
            if (!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                int extType = message.getIntAttribute(Constant.EXT_TYPE, 0);
                if (extType == 0) {
                    return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
                } else if (extType == Constant.ExtType.GUIDE) {
                    return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EXT : MESSAGE_TYPE_SENT_EXT;
                } else if (extType == Constant.ExtType.CITY) {
                    return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EXT : MESSAGE_TYPE_SENT_EXT;
                } else if (extType == Constant.ExtType.TRAVELS) {
                    return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EXT : MESSAGE_TYPE_SENT_EXT;
                } else if (extType == Constant.ExtType.SPOT) {
                    return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EXT : MESSAGE_TYPE_SENT_EXT;
                } else if (extType == Constant.ExtType.FOOD || extType == Constant.ExtType.HOTEL || extType == Constant.ExtType.SHOPPING) {
                    return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EXT : MESSAGE_TYPE_SENT_EXT;
                } else if (extType == Constant.ExtType.TIPS) {
                    return MESSAGE_TYPE_TIPS;
                } else {
                    return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_UNKOWN : MESSAGE_TYPE_SENT_UNKOWN;
                }
            }
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
        }
        if (message.getType() == Type.IMAGE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;

        }
        if (message.getType() == Type.LOCATION) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LOCATION : MESSAGE_TYPE_SENT_LOCATION;
        }
        if (message.getType() == Type.VOICE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
        }
        if (message.getType() == Type.VIDEO) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO : MESSAGE_TYPE_SENT_VIDEO;
        }
        if (message.getType() == Type.FILE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FILE : MESSAGE_TYPE_SENT_FILE;
        }

        return -1;// invalid
    }

    public int getViewTypeCount() {
        return 19;
    }

    private View createViewByMessage(EMMessage message, int position) {
        switch (message.getType()) {
            case LOCATION:
                return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_location, null) : inflater.inflate(
                        R.layout.row_sent_location, null);
            case IMAGE:
                return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_picture, null) : inflater.inflate(
                        R.layout.row_sent_picture, null);

            case VOICE:
                return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_voice, null) : inflater.inflate(
                        R.layout.row_sent_voice, null);
            case VIDEO:
                return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_video, null) : inflater.inflate(
                        R.layout.row_sent_video, null);
            case FILE:
                return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_file, null) : inflater.inflate(
                        R.layout.row_sent_file, null);
            default:
                // 语音电话
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_voice_call, null) : inflater
                            .inflate(R.layout.row_sent_voice_call, null);
                } else {
                    int extType = message.getIntAttribute(Constant.EXT_TYPE, 0);
                    if (extType == 0) {
                        return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_message, null) : inflater.inflate(
                                R.layout.row_sent_message, null);
                    } else if (extType == Constant.ExtType.GUIDE || extType == Constant.ExtType.CITY || extType == Constant.ExtType.TRAVELS || extType == Constant.ExtType.SPOT || extType == Constant.ExtType.FOOD || extType == Constant.ExtType.HOTEL || extType == Constant.ExtType.SHOPPING) {
                        return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_ext, null) : inflater.inflate(
                                R.layout.row_sent_ext, null);
                    } else if (extType == Constant.ExtType.TIPS) {
                        return inflater.inflate(R.layout.row_chat_tips, null);
                    } else {
                        return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_message, null) : inflater.inflate(
                                R.layout.row_sent_message, null);
                    }
                }

        }
    }

    @SuppressLint("NewApi")
    public View getView(final int position, View convertView, ViewGroup parent) {
        final EMMessage message = getItem(position);

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = createViewByMessage(message, position);
            if (message.getType() == Type.IMAGE) {
                try {
                    holder.iv = ((ImageView) convertView.findViewById(R.id.iv_sendPicture));
                    holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    holder.tv = (TextView) convertView.findViewById(R.id.percentage);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
                    holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
                } catch (Exception e) {
                }

            } else if (message.getType() == Type.TXT) {

                try {
                    holder.rl_content = (RelativeLayout) convertView.findViewById(R.id.rl_chatcontent);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
                    holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
                    // 这里是文字内容
                    // 语音通话
                    if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                        holder.iv = (ImageView) convertView.findViewById(R.id.iv_call_icon);
                        holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                    } else {
                        int extType = message.getIntAttribute(Constant.EXT_TYPE, 0);
                        holder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                        holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                        holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
                        holder.tv_attr = (TextView) convertView.findViewById(R.id.tv_attr);
                        holder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
                        if (extType == 0) {
                            holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                        } else if (extType == Constant.ExtType.GUIDE) {
                        } else if (extType == Constant.ExtType.CITY) {
                        } else if (extType == Constant.ExtType.TRAVELS) {
                        } else if (extType == Constant.ExtType.SPOT) {
                        } else if (extType == Constant.ExtType.FOOD || extType == Constant.ExtType.HOTEL || extType == Constant.ExtType.SHOPPING) {
                        } else if (extType == Constant.ExtType.TIPS) {
                            holder.tv_tips = (TextView) convertView.findViewById(R.id.tv_tips);
                        } else {
                            holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                        }
                    }
                } catch (Exception e) {
                }


            } else if (message.getType() == Type.VOICE) {
                try {
                    holder.iv = ((ImageView) convertView.findViewById(R.id.iv_voice));
                    holder.rl_voice_content = (RelativeLayout) convertView.findViewById(R.id.rl_voice_content);
                    holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    holder.tv = (TextView) convertView.findViewById(R.id.tv_length);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
                    holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
                    holder.iv_read_status = (ImageView) convertView.findViewById(R.id.iv_unread_voice);
                } catch (Exception e) {
                }
            } else if (message.getType() == Type.LOCATION) {
                try {
                    holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    holder.tv = (TextView) convertView.findViewById(R.id.tv_location);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
                    holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
                } catch (Exception e) {
                }
            } else if (message.getType() == Type.VIDEO) {
                try {
                    holder.iv = ((ImageView) convertView.findViewById(R.id.chatting_content_iv));
                    holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    holder.tv = (TextView) convertView.findViewById(R.id.percentage);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
                    holder.size = (TextView) convertView.findViewById(R.id.chatting_size_iv);
                    holder.timeLength = (TextView) convertView.findViewById(R.id.chatting_length_iv);
                    holder.playBtn = (ImageView) convertView.findViewById(R.id.chatting_status_btn);
                    holder.container_status_btn = (LinearLayout) convertView.findViewById(R.id.container_status_btn);
                    holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);

                } catch (Exception e) {
                }
            } else if (message.getType() == Type.FILE) {
                try {
                    holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    holder.tv_file_name = (TextView) convertView.findViewById(R.id.tv_file_name);
                    holder.tv_file_size = (TextView) convertView.findViewById(R.id.tv_file_size);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
                    holder.tv_file_download_state = (TextView) convertView.findViewById(R.id.tv_file_state);
                    holder.ll_container = (LinearLayout) convertView.findViewById(R.id.ll_file_container);
                    // 这里是进度值
                    holder.tv = (TextView) convertView.findViewById(R.id.percentage);
                } catch (Exception e) {
                }
                try {
                    holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
                } catch (Exception e) {
                }

            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        switch (message.getType()) {
            // 根据消息type显示item
            case IMAGE: // 图片
                handleGroupMessage(position, convertView, message, holder);
                handleImageMessage(message, holder, position, convertView);
                handleCommonMessage(position, convertView, message, holder);
                break;
            case TXT: // 文本
                if (!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    int extType = message.getIntAttribute(Constant.EXT_TYPE, 0);
                    if (extType == 0) {
                        handleGroupMessage(position, convertView, message, holder);
                        handleTextMessage(message, holder, position);
                        handleCommonMessage(position, convertView, message, holder);
                    } else if (extType < 100) {
                        handleGroupMessage(position, convertView, message, holder);
                        handleExtMessage(message, holder, position);
                        handleCommonMessage(position, convertView, message, holder);
                    } else if (extType == Constant.ExtType.TIPS) {
                        handleTipsMessage(message, holder, position);
                    }

                } else {
                    // 语音电话
                    handleGroupMessage(position, convertView, message, holder);
                    handleVoiceCallMessage(message, holder, position);
                    handleCommonMessage(position, convertView, message, holder);
                }

                break;
            case LOCATION: // 位置
                handleGroupMessage(position, convertView, message, holder);
                handleLocationMessage(message, holder, position, convertView);
                handleCommonMessage(position, convertView, message, holder);
                break;
            case VOICE: // 语音
                handleGroupMessage(position, convertView, message, holder);
                handleVoiceMessage(message, holder, position, convertView);
                handleCommonMessage(position, convertView, message, holder);
                break;
            case VIDEO: // 视频
                handleGroupMessage(position, convertView, message, holder);
                handleVideoMessage(message, holder, position, convertView);
                handleCommonMessage(position, convertView, message, holder);
                break;
            case FILE: // 一般文件
                handleGroupMessage(position, convertView, message, holder);
                handleFileMessage(message, holder, position, convertView);
                handleCommonMessage(position, convertView, message, holder);
                break;
            default:
                // not supported
        }


        TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);

        if (position == 0) {
            timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
            timestamp.setVisibility(View.VISIBLE);
        } else {
            // 两条消息时间离得如果稍长，显示时间
            if (DateUtils.isCloseEnough(message.getMsgTime(), conversation.getMessage(position - 1).getMsgTime())) {
                timestamp.setVisibility(View.GONE);
            } else {
                timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                timestamp.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    private void handleTipsMessage(EMMessage message, ViewHolder holder, final int position) {
        int extType = message.getIntAttribute(Constant.EXT_TYPE, 0);
        String conent = message.getStringAttribute(Constant.MSG_CONTENT, "");
        holder.tv_tips.setText(conent);

    }

    private void handleGroupMessage(final int position, View convertView, final EMMessage message, ViewHolder holder) {
        // 群聊时，显示接收的消息的发送人的名称
        ChatType chatType = message.getChatType();
        if (message.direct == EMMessage.Direct.RECEIVE) {
            if (chatType == ChatType.GroupChat) {
                // demo用username代替nick
                IMUser user = groupMembers.get(message.getFrom());
                if (user == null) {
                    user = IMUserRepository.getContactByUserName(context, message.getFrom());
                    if (user == null) {
                        user = IMUtils.getUserInfoFromMessage(context, message);
                    }
                    groupMembers.put(message.getFrom(), user);

                }
                if (user != null) {
                    holder.tv_userId.setText(user.getNick());
                    ImageLoader.getInstance().displayImage(user.getAvatarSmall(), holder.head_iv, picOptions);
                }
            } else {
                IMUser user = AccountManager.getInstance().getContactList(activity).get(username);
                if (user != null) {
                    holder.tv_userId.setText(user.getNick());
                    ImageLoader.getInstance().displayImage(user.getAvatarSmall(), holder.head_iv, picOptions);
                }

            }
        } else {
            PeachUser user = AccountManager.getInstance().getLoginAccount(context);
            if (user != null) {
                ImageLoader.getInstance().displayImage(user.avatarSmall, holder.head_iv, picOptions);
            }
        }

        // 如果是发送的消息并且不是群聊消息，显示已读textview
        if (message.direct == EMMessage.Direct.SEND && chatType != ChatType.GroupChat) {
            holder.tv_ack = (TextView) convertView.findViewById(R.id.tv_ack);
            holder.tv_delivered = (TextView) convertView.findViewById(R.id.tv_delivered);
            if (holder.tv_ack != null) {
                if (message.isAcked) {
                    if (holder.tv_delivered != null) {
                        holder.tv_delivered.setVisibility(View.INVISIBLE);
                    }
                    holder.tv_ack.setVisibility(View.INVISIBLE);
                } else {
                    holder.tv_ack.setVisibility(View.INVISIBLE);

                    // check and display msg delivered ack status
                    if (holder.tv_delivered != null) {
                        if (message.isDelivered) {
                            holder.tv_delivered.setVisibility(View.INVISIBLE);
                        } else {
                            holder.tv_delivered.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        } else {
            // 如果是文本或者地图消息并且不是group messgae，显示的时候给对方发送已读回执
            if ((message.getType() == Type.TXT || message.getType() == Type.LOCATION) && !message.isAcked && chatType != ChatType.GroupChat) {
                // 不是语音通话记录
                if (!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    try {
                        EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                        // 发送已读回执
                        message.isAcked = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private void handleCommonMessage(final int position, View convertView, final EMMessage message, ViewHolder holder) {

        if (message.direct == EMMessage.Direct.SEND) {
            View statusView = convertView.findViewById(R.id.msg_status);
            // 重发按钮点击事件
            statusView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 显示重发消息的自定义alertdialog
                    Intent intent = new Intent(activity, IMAlertDialog.class);
                    intent.putExtra("msg", activity.getString(R.string.confirm_resend));
                    intent.putExtra("title", activity.getString(R.string.resend));
                    intent.putExtra("cancel", true);
                    intent.putExtra("position", position);
                    if (message.getType() == Type.TXT)
                        activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_TEXT);
                    else if (message.getType() == Type.VOICE)
                        activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_VOICE);
                    else if (message.getType() == Type.IMAGE)
                        activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_PICTURE);
                    else if (message.getType() == Type.LOCATION)
                        activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_LOCATION);
                    else if (message.getType() == Type.FILE)
                        activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_FILE);
                    else if (message.getType() == Type.VIDEO)
                        activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_VIDEO);

                }
            });

        } else {
            // 点击头像进入详情
            holder.head_iv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (IMUserRepository.isMyFriend(context, message.getFrom())) {
                        Intent intent = new Intent(context, ContactDetailActivity.class);
                        IMUser user = IMUserRepository.getContactByUserName(context, message.getFrom());
                        intent.putExtra("userId", user.getUserId());
                        intent.putExtra("userNick", user.getNick());
                        context.startActivity(intent);
                    } else {
                        IMUser itemData = IMUserRepository.getContactByUserName(context, message.getFrom());
                        PeachUser user = new PeachUser();
                        if (itemData != null) {
                            user.nickName = itemData.getNick();
                            user.userId = itemData.getUserId();
                            user.easemobUser = itemData.getUsername();
                            user.avatar = itemData.getAvatar();
                            user.avatarSmall = itemData.getAvatarSmall();
                            user.signature = itemData.getSignature();
                            user.gender = itemData.getGender();
                            user.memo = itemData.getMemo();
                        } else {
                            user.easemobUser = message.getFrom();
                        }
                        Intent intent = new Intent(context, SeachContactDetailActivity.class);
                        intent.putExtra("user", user);
                        context.startActivity(intent);


                    }
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
    private void handleTextMessage(EMMessage message, ViewHolder holder, final int position) {
        TextMessageBody txtBody = (TextMessageBody) message.getBody();
        Spannable span = SmileUtils.getSmiledText(context, txtBody.getMessage());
        // 设置内容
        holder.tv.setText(span, BufferType.SPANNABLE);
        // 设置长按事件监听
        holder.tv.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult(
                        (new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type",
                                Type.TXT.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        if (message.direct == EMMessage.Direct.SEND) {
            switch (message.status) {
                case SUCCESS: // 发送成功
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                case FAIL: // 发送失败
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // 发送中
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                default:
                    // 发送消息
                    sendMsgInBackground(message, holder);
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
    private void handleExtMessage(EMMessage message, final ViewHolder holder, final int position) {
        final int extType = message.getIntAttribute(Constant.EXT_TYPE, 0);
        final String conent = message.getStringAttribute(Constant.MSG_CONTENT, "");
        ExtMessageBean bean = null;
        bean = GsonTools.parseJsonToBean(conent, ExtMessageBean.class);
        final ExtMessageBean finalBean = bean;
        holder.tv_attr.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        if (extType == Constant.ExtType.GUIDE) {
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
        } else if (extType == Constant.ExtType.CITY) {
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
        } else if (extType == Constant.ExtType.TRAVELS) {
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

        } else if (extType == Constant.ExtType.SPOT) {
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
        } else if (extType == Constant.ExtType.FOOD || extType == Constant.ExtType.HOTEL || extType == Constant.ExtType.SHOPPING) {
            holder.tv_attr.setVisibility(View.VISIBLE);
            switch (extType) {
                case Constant.ExtType.FOOD:
                    holder.tv_name.setText(bean.name);
                    holder.tv_type.setText("美食");
                    holder.tv_attr.setText(bean.rating + " " + bean.price);
                    break;

                case Constant.ExtType.HOTEL:
                    holder.tv_name.setText(bean.name);
                    holder.tv_type.setText("酒店");
                    holder.tv_attr.setText(bean.rating + " " + bean.price);
                    break;

                case Constant.ExtType.SHOPPING:
                    holder.tv_name.setText(bean.name);
                    holder.tv_type.setText("购物");
                    holder.tv_attr.setText(bean.rating + " ");
                    break;
            }
            if(message.direct== EMMessage.Direct.RECEIVE){
                holder.tv_attr.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_score_start_small, 0, 0, 0);
            }else{
                holder.tv_attr.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_score_start_small_unselect, 0, 0, 0);
            }

            holder.tv_desc.setText(bean.address);
            ImageLoader.getInstance().displayImage(bean.image, holder.iv_image, UILUtils.getRadiusOption(3));
            holder.rl_content.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (extType) {
                        case Constant.ExtType.FOOD:
                            IntentUtils.intentToDetail(activity, TravelApi.PeachType.RESTAURANTS, finalBean.id);
                            break;

                        case Constant.ExtType.HOTEL:
                            IntentUtils.intentToDetail(activity, TravelApi.PeachType.HOTEL, finalBean.id);
                            break;

                        case Constant.ExtType.SHOPPING:
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

        if (message.direct == EMMessage.Direct.SEND) {
            switch (message.status) {
                case SUCCESS: // 发送成功
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                case FAIL: // 发送失败
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // 发送中
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                default:
                    // 发送消息
                    sendMsgInBackground(message, holder);
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
    private void handleVoiceCallMessage(EMMessage message, ViewHolder holder, final int position) {
        TextMessageBody txtBody = (TextMessageBody) message.getBody();
        holder.tv.setText(txtBody.getMessage());

    }

    /**
     * 图片消息
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleImageMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
        holder.pb.setTag(position);
        holder.iv.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult(
                        (new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type",
                                Type.IMAGE.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        // 接收方向的消息
        if (message.direct == EMMessage.Direct.RECEIVE) {
            Bitmap defaultImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_image);
            // "it is receive msg";
            if (message.status == EMMessage.Status.INPROGRESS) {
                // "!!!! back receive";
                holder.iv.setImageBitmap(defaultImage);
                showDownloadImageProgress(message, holder);
                // downloadImage(message, holder);
            } else {
                // "!!!! not back receive, show image directly");
                holder.pb.setVisibility(View.GONE);
                holder.tv.setVisibility(View.GONE);
                holder.iv.setImageBitmap(defaultImage);
                ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
                if (imgBody.getLocalUrl() != null) {
                    // String filePath = imgBody.getLocalUrl();
                    String remotePath = imgBody.getRemoteUrl();
                    String filePath = ImageUtils.getImagePath(remotePath);
                    String thumbRemoteUrl = imgBody.getThumbnailUrl();
                    String thumbnailPath = ImageUtils.getThumbnailImagePath(thumbRemoteUrl);
                    showImageView(thumbnailPath, holder.iv, filePath, imgBody.getRemoteUrl(), message);
                }
            }
            return;
        }

        // 发送的消息
        // process send message
        // send pic, show the pic directly
        ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
        String filePath = imgBody.getLocalUrl();
        if (filePath != null && new File(filePath).exists()) {
            showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv, filePath, null, message);
        } else {
            showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv, filePath, IMAGE_DIR, message);
        }

        switch (message.status) {
            case SUCCESS:
                holder.pb.setVisibility(View.GONE);
                holder.tv.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.GONE);
                break;
            case FAIL:
                holder.pb.setVisibility(View.GONE);
                holder.tv.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.VISIBLE);
                break;
            case INPROGRESS:
                holder.staus_iv.setVisibility(View.GONE);
                holder.pb.setVisibility(View.VISIBLE);
                holder.tv.setVisibility(View.VISIBLE);
                if (timers.containsKey(message.getMsgId()))
                    return;
                // set a timer
                final Timer timer = new Timer();
                timers.put(message.getMsgId(), timer);
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                holder.pb.setVisibility(View.VISIBLE);
                                holder.tv.setVisibility(View.VISIBLE);
                                holder.tv.setText(message.progress + "%");
                                if (message.status == EMMessage.Status.SUCCESS) {
                                    holder.pb.setVisibility(View.GONE);
                                    holder.tv.setVisibility(View.GONE);
                                    // message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
                                    timer.cancel();
                                } else if (message.status == EMMessage.Status.FAIL) {
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
                sendPictureMessage(message, holder);
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
    private void handleVideoMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {

        VideoMessageBody videoBody = (VideoMessageBody) message.getBody();
        // final File image=new File(PathUtil.getInstance().getVideoPath(),
        // videoBody.getFileName());
        String localThumb = videoBody.getLocalThumb();

        holder.iv.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult(
                        new Intent(activity, ContextMenu.class).putExtra("position", position).putExtra("type",
                                Type.VIDEO.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        if (localThumb != null) {

            showVideoThumbView(localThumb, holder.iv, videoBody.getThumbnailUrl(), message);
        }
        if (videoBody.getLength() > 0) {
            String time = DateUtils.toTimeBySecond(videoBody.getLength());
            holder.timeLength.setText(time);
        }
        holder.playBtn.setImageResource(R.drawable.video_download_btn_nor);

        if (message.direct == EMMessage.Direct.RECEIVE) {
            if (videoBody.getVideoFileLength() > 0) {
                String size = TextFormater.getDataSize(videoBody.getVideoFileLength());
                holder.size.setText(size);
            }
        } else {
            if (videoBody.getLocalUrl() != null && new File(videoBody.getLocalUrl()).exists()) {
                String size = TextFormater.getDataSize(new File(videoBody.getLocalUrl()).length());
                holder.size.setText(size);
            }
        }

        if (message.direct == EMMessage.Direct.RECEIVE) {

            // System.err.println("it is receive msg");
            if (message.status == EMMessage.Status.INPROGRESS) {
                // System.err.println("!!!! back receive");
                holder.iv.setImageResource(R.drawable.default_image);
                showDownloadImageProgress(message, holder);

            } else {
                // System.err.println("!!!! not back receive, show image directly");
                holder.iv.setImageResource(R.drawable.default_image);
                if (localThumb != null) {
                    showVideoThumbView(localThumb, holder.iv, videoBody.getThumbnailUrl(), message);
                }

            }

            return;
        }
        holder.pb.setTag(position);

        // until here ,deal with send video msg
        switch (message.status) {
            case SUCCESS:
                holder.pb.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.GONE);
                holder.tv.setVisibility(View.GONE);
                break;
            case FAIL:
                holder.pb.setVisibility(View.GONE);
                holder.tv.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.VISIBLE);
                break;
            case INPROGRESS:
                if (timers.containsKey(message.getMsgId()))
                    return;
                // set a timer
                final Timer timer = new Timer();
                timers.put(message.getMsgId(), timer);
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                holder.pb.setVisibility(View.VISIBLE);
                                holder.tv.setVisibility(View.VISIBLE);
                                holder.tv.setText(message.progress + "%");
                                if (message.status == EMMessage.Status.SUCCESS) {
                                    holder.pb.setVisibility(View.GONE);
                                    holder.tv.setVisibility(View.GONE);
                                    // message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
                                    timer.cancel();
                                } else if (message.status == EMMessage.Status.FAIL) {
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
                // sendMsgInBackground(message, holder);
                sendPictureMessage(message, holder);

        }

    }

    /**
     * 语音消息
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleVoiceMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
        VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
        holder.tv.setText(voiceBody.getLength() + "\"");
        holder.rl_voice_content.setOnClickListener(new VoicePlayClickListener(message, holder.iv, holder.iv_read_status, this, activity, username));
        holder.rl_voice_content.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult(
                        (new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type",
                                Type.VOICE.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });
        if (((ChatActivity) activity).playMsgId != null
                && ((ChatActivity) activity).playMsgId.equals(message
                .getMsgId()) && VoicePlayClickListener.isPlaying) {
            AnimationDrawable voiceAnimation;
            if (message.direct == EMMessage.Direct.RECEIVE) {
                holder.iv.setImageResource(R.anim.voice_from_icon);
            } else {
                holder.iv.setImageResource(R.anim.voice_to_icon);
            }
            voiceAnimation = (AnimationDrawable) holder.iv.getDrawable();
            voiceAnimation.start();
        } else {
            if (message.direct == EMMessage.Direct.RECEIVE) {
                holder.iv.setImageResource(R.drawable.chatfrom_voice_playing);
            } else {
                holder.iv.setImageResource(R.drawable.chatto_voice_playing);
            }
        }


        if (message.direct == EMMessage.Direct.RECEIVE) {
            if (message.isAcked) {
                // 隐藏语音未读标志
                holder.iv_read_status.setVisibility(View.INVISIBLE);
            } else {
                holder.iv_read_status.setVisibility(View.VISIBLE);
            }
//            System.err.println("it is receive msg");
            if (message.status == EMMessage.Status.INPROGRESS) {
                holder.pb.setVisibility(View.VISIBLE);
//                System.err.println("!!!! back receive");
                ((FileMessageBody) message.getBody()).setDownloadCallback(new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                holder.pb.setVisibility(View.INVISIBLE);
                                notifyDataSetChanged();
                            }
                        });

                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String message) {
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

        // until here, deal with send voice msg
        switch (message.status) {
            case SUCCESS:
                holder.pb.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.GONE);
                break;
            case FAIL:
                holder.pb.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.VISIBLE);
                break;
            case INPROGRESS:
                holder.pb.setVisibility(View.VISIBLE);
                holder.staus_iv.setVisibility(View.GONE);
                break;
            default:
                sendMsgInBackground(message, holder);
        }
    }

    /**
     * 文件消息
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleFileMessage(final EMMessage message, final ViewHolder holder, int position, View convertView) {
        final NormalFileMessageBody fileMessageBody = (NormalFileMessageBody) message.getBody();
        final String filePath = fileMessageBody.getLocalUrl();
        holder.tv_file_name.setText(fileMessageBody.getFileName());
        holder.tv_file_size.setText(TextFormater.getDataSize(fileMessageBody.getFileSize()));
        holder.ll_container.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                File file = new File(filePath);
                if (file != null && file.exists()) {
                    // 文件存在，直接打开
                    FileUtils.openFile(file, (Activity) context);
                } else {
                    // 下载
                    context.startActivity(new Intent(context, ShowNormalFileActivity.class).putExtra("msgbody", fileMessageBody));
                }
                if (message.direct == EMMessage.Direct.RECEIVE && !message.isAcked) {
                    try {
                        EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                        message.isAcked = true;
                    } catch (EaseMobException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        if (message.direct == EMMessage.Direct.RECEIVE) { // 接收的消息
//            System.err.println("it is receive msg");
            File file = new File(filePath);
            if (file != null && file.exists()) {
                holder.tv_file_download_state.setText("已下载");
            } else {
                holder.tv_file_download_state.setText("未下载");
            }
            return;
        }

        // until here, deal with send voice msg
        switch (message.status) {
            case SUCCESS:
                holder.pb.setVisibility(View.INVISIBLE);
                holder.tv.setVisibility(View.INVISIBLE);
                holder.staus_iv.setVisibility(View.INVISIBLE);
                break;
            case FAIL:
                holder.pb.setVisibility(View.INVISIBLE);
                holder.tv.setVisibility(View.INVISIBLE);
                holder.staus_iv.setVisibility(View.VISIBLE);
                break;
            case INPROGRESS:
                if (timers.containsKey(message.getMsgId()))
                    return;
                // set a timer
                final Timer timer = new Timer();
                timers.put(message.getMsgId(), timer);
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                holder.pb.setVisibility(View.VISIBLE);
                                holder.tv.setVisibility(View.VISIBLE);
                                holder.tv.setText(message.progress + "%");
                                if (message.status == EMMessage.Status.SUCCESS) {
                                    holder.pb.setVisibility(View.INVISIBLE);
                                    holder.tv.setVisibility(View.INVISIBLE);
                                    timer.cancel();
                                } else if (message.status == EMMessage.Status.FAIL) {
                                    holder.pb.setVisibility(View.INVISIBLE);
                                    holder.tv.setVisibility(View.INVISIBLE);
                                    holder.staus_iv.setVisibility(View.VISIBLE);
//                                    Toast.makeText(activity,
//                                            activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT)
//                                            .show();
                                    ToastUtil.getInstance(activity).showToast("呃~好像没找到网络");
                                    timer.cancel();
                                }

                            }
                        });

                    }
                }, 0, 500);
                break;
            default:
                // 发送消息
                sendMsgInBackground(message, holder);
        }

    }

    /**
     * 处理位置消息
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleLocationMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
        TextView locationView = ((TextView) convertView.findViewById(R.id.tv_location));
        LocationMessageBody locBody = (LocationMessageBody) message.getBody();
        locationView.setText(locBody.getAddress());
        LatLng loc = new LatLng(locBody.getLatitude(), locBody.getLongitude());
        locationView.setOnClickListener(new MapClickListener(loc, locBody.getAddress()));
        locationView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult(
                        (new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type",
                                Type.LOCATION.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return false;
            }
        });

        if (message.direct == EMMessage.Direct.RECEIVE) {
            return;
        }
        // deal with send message
        switch (message.status) {
            case SUCCESS:
                holder.pb.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.GONE);
                break;
            case FAIL:
                holder.pb.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.VISIBLE);
                break;
            case INPROGRESS:
                holder.pb.setVisibility(View.VISIBLE);
                break;
            default:
                sendMsgInBackground(message, holder);
        }
    }

    /**
     * 发送消息
     *
     * @param message
     * @param holder
     */
    public void sendMsgInBackground(final EMMessage message, final ViewHolder holder) {
        holder.staus_iv.setVisibility(View.GONE);
        holder.pb.setVisibility(View.VISIBLE);

        // 调用sdk发送异步发送方法
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

            @Override
            public void onSuccess() {
                updateSendedView(message, holder);
            }

            @Override
            public void onError(int code, String error) {
                updateSendedView(message, holder);
            }

            @Override
            public void onProgress(int progress, String status) {
            }

        });

    }

    /*
     * chat sdk will automatic download thumbnail image for the image message we
     * need to register callback show the download progress
     */
    private void showDownloadImageProgress(final EMMessage message, final ViewHolder holder) {
//        System.err.println("!!! show download image progress");
        // final ImageMessageBody msgbody = (ImageMessageBody)
        // message.getBody();
        final FileMessageBody msgbody = (FileMessageBody) message.getBody();
        if (holder.pb != null)
            holder.pb.setVisibility(View.VISIBLE);
        if (holder.tv != null)
            holder.tv.setVisibility(View.INVISIBLE);

        msgbody.setDownloadCallback(new EMCallBack() {

            @Override
            public void onSuccess() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // message.setBackReceive(false);
                        if (message.getType() == Type.IMAGE) {
                            holder.pb.setVisibility(View.GONE);
                            holder.tv.setVisibility(View.GONE);
                        }
                        notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(int code, String message) {

            }

            @Override
            public void onProgress(final int progress, String status) {
                if (message.getType() == Type.IMAGE) {
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            holder.tv.setText(progress + "%");
//
//                        }
//                    });
                }

            }

        });
    }

    /*
     * send message with new sdk
     */
    private void sendPictureMessage(final EMMessage message, final ViewHolder holder) {
        try {
            String to = message.getTo();

            // before send, update ui
            holder.staus_iv.setVisibility(View.GONE);
            holder.pb.setVisibility(View.VISIBLE);
            holder.tv.setVisibility(View.INVISIBLE);
            holder.tv.setText("0%");
            // if (chatType == ChatActivity.CHATTYPE_SINGLE) {
            EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

                @Override
                public void onSuccess() {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            // send success
                            holder.pb.setVisibility(View.GONE);
                            holder.tv.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onError(int code, String error) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            holder.pb.setVisibility(View.GONE);
                            holder.tv.setVisibility(View.GONE);
                            // message.setSendingStatus(Message.SENDING_STATUS_FAIL);
                            holder.staus_iv.setVisibility(View.VISIBLE);
//                            Toast.makeText(activity,
//                                    activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT).show();
                            ToastUtil.getInstance(activity).showToast(activity.getResources().getString(R.string.request_network_failed));
                        }
                    });
                }

                @Override
                public void onProgress(final int progress, String status) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            holder.tv.setText(progress + "%");
                        }
                    });
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新ui上消息发送状态
     *
     * @param message
     * @param holder
     */
    private void updateSendedView(final EMMessage message, final ViewHolder holder) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // send success
                if (message.getType() == Type.VIDEO) {
                    holder.tv.setVisibility(View.GONE);
                }
                if (message.status == EMMessage.Status.SUCCESS) {
                    if (message.getType() == Type.FILE) {
                        holder.pb.setVisibility(View.INVISIBLE);
                        holder.staus_iv.setVisibility(View.INVISIBLE);
                    } else {
                        holder.pb.setVisibility(View.GONE);
                        holder.staus_iv.setVisibility(View.GONE);
                    }

                } else if (message.status == EMMessage.Status.FAIL) {
                    if (message.getType() == Type.FILE) {
                        holder.pb.setVisibility(View.INVISIBLE);
                    } else {
                        holder.pb.setVisibility(View.GONE);
                    }
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
     * @param thumbernailPath
     * @param iv
     * @return the image exists or not
     */
    private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath, String remoteDir,
                                  final EMMessage message) {
        // String imagename =
        // localFullSizePath.substring(localFullSizePath.lastIndexOf("/") + 1,
        // localFullSizePath.length());
        // final String remote = remoteDir != null ? remoteDir+imagename :
        // imagename;
        final String remote = remoteDir;
        EMLog.d("###", "local = " + localFullSizePath + " remote: " + remote);
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            iv.setImageBitmap(bitmap);
            iv.setClickable(true);
            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    System.err.println("image view on click");
                    Intent intent = new Intent(activity, ShowBigImage.class);
                    File file = new File(localFullSizePath);
                    if (file.exists()) {
                        Uri uri = Uri.fromFile(file);
                        intent.putExtra("uri", uri);
//                        System.err.println("here need to check why download everytime");
                    } else {
                        // The local full size pic does not exist yet.
                        // ShowBigImage needs to download it from the server
                        // first
                        // intent.putExtra("", message.get);
                        ImageMessageBody body = (ImageMessageBody) message.getBody();
                        intent.putExtra("secret", body.getSecret());
                        intent.putExtra("remotepath", remote);
                    }
                    if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked
                            && message.getChatType() != ChatType.GroupChat) {
                        try {
                            EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                            message.isAcked = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ((BaseActivity) activity).startActivityWithNoAnim(intent);
                }
            });
            return true;
        } else {

            new LoadImageTask().execute(thumbernailPath, localFullSizePath, remote, message.getChatType(), iv, activity, message);
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
    private void showVideoThumbView(String localThumb, ImageView iv, String thumbnailUrl, final EMMessage message) {
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = ImageCache.getInstance().get(localThumb);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            iv.setImageBitmap(bitmap);
            iv.setClickable(true);
            iv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    VideoMessageBody videoBody = (VideoMessageBody) message.getBody();
//                    System.err.println("video view is on click");
                    Intent intent = new Intent(activity, ShowVideoActivity.class);
                    intent.putExtra("localpath", videoBody.getLocalUrl());
                    intent.putExtra("secret", videoBody.getSecret());
                    intent.putExtra("remotepath", videoBody.getRemoteUrl());
                    if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked
                            && message.getChatType() != ChatType.GroupChat) {
                        message.isAcked = true;
                        try {
                            EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    activity.startActivity(intent);

                }
            });

        } else {
            new LoadVideoImageTask().execute(localThumb, thumbnailUrl, iv, activity, message, this);
        }

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

        LatLng location;
        String address;

        public MapClickListener(LatLng loc, String address) {
            location = loc;
            this.address = address;

        }

        @Override
        public void onClick(View v) {
            Intent intent;
            intent = new Intent(context, BaiduMapActivity.class);
            intent.putExtra("latitude", location.latitude);
            intent.putExtra("longitude", location.longitude);
            intent.putExtra("address", address);
            activity.startActivity(intent);
        }

    }


}