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
package com.xuejian.client.lxp.module.toolbox.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.utils.GsonTools;
import com.aizou.core.widget.DotView;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.GroupReomveListener;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;
import com.lv.Audio.MediaRecordFunc;
import com.lv.Listener.SendMsgListener;
import com.lv.Listener.UploadListener;
import com.lv.Utils.Config;
import com.lv.Utils.TimeUtils;
import com.lv.bean.Message;
import com.lv.bean.MessageBean;
import com.lv.im.HandleImMessage;
import com.lv.im.IMClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.ChatBaseActivity;
import com.xuejian.client.lxp.bean.CmdDeleteBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.ImageUtils;
import com.xuejian.client.lxp.common.utils.SmileUtils;
import com.xuejian.client.lxp.common.widget.ExpandGridView;
import com.xuejian.client.lxp.common.widget.PasteEditText;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.config.Constant;
import com.xuejian.client.lxp.config.hxconfig.PeachHXSDKModel;
import com.xuejian.client.lxp.db.IMUser;
import com.xuejian.client.lxp.db.respository.IMUserRepository;
import com.xuejian.client.lxp.module.MainActivity;
import com.xuejian.client.lxp.module.dest.SearchAllActivity;
import com.xuejian.client.lxp.module.toolbox.FavListActivity;
import com.xuejian.client.lxp.module.toolbox.StrategyListActivity;
import com.xuejian.client.lxp.module.toolbox.im.adapter.ExpressionAdapter;
import com.xuejian.client.lxp.module.toolbox.im.adapter.ExpressionPagerAdapter;
import com.xuejian.client.lxp.module.toolbox.im.adapter.MessageAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 聊天页面
 * 
 */
public class ChatActivity extends ChatBaseActivity implements OnClickListener,HandleImMessage.MessagerHandler{

	private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
	public static final int REQUEST_CODE_CONTEXT_MENU = 3;
	private static final int REQUEST_CODE_MAP = 4;
	public static final int REQUEST_CODE_TEXT = 5;
	public static final int REQUEST_CODE_VOICE = 6;
	public static final int REQUEST_CODE_PICTURE = 7;
	public static final int REQUEST_CODE_LOCATION = 8;
	public static final int REQUEST_CODE_NET_DISK = 9;
	public static final int REQUEST_CODE_FILE = 10;
	public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
	public static final int REQUEST_CODE_PICK_VIDEO = 12;
	public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
	public static final int REQUEST_CODE_VIDEO = 14;
	public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
	public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
	public static final int REQUEST_CODE_SEND_USER_CARD = 17;
	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;
	public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
	public static final int REQUEST_CODE_GROUP_DETAIL = 21;
	public static final int REQUEST_CODE_SELECT_VIDEO = 23;
	public static final int REQUEST_CODE_SELECT_FILE = 24;
	public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

    public static final int REQUEST_CODE_ADD_USER=26;

	public static final int RESULT_CODE_COPY = 1;
	public static final int RESULT_CODE_DELETE = 2;
	public static final int RESULT_CODE_FORWARD = 3;
	public static final int RESULT_CODE_OPEN = 4;
	public static final int RESULT_CODE_DWONLOAD = 5;
	public static final int RESULT_CODE_TO_CLOUD = 6;
	public static final int RESULT_CODE_EXIT_GROUP = 7;

	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;

	public static final String COPY_IMAGE = "EASEMOBIMG";
    private TitleHeaderBar titleHeaderBar;
    private DrawerLayout drawerLayout;
	private View recordingContainer;
	private ImageView micImage;
	private TextView recordingHint;
	private ListView listView;
	private PasteEditText mEditTextContent;
	private View buttonSetModeKeyboard;
	private View buttonSetModeVoice;
	private View buttonSend;
	private View buttonPressToSpeak;
	private ViewPager expressionViewpager;
	private RelativeLayout expressionContainer;
	private LinearLayout btnContainer;
	private View more;
	private ClipboardManager clipboard;
	private InputMethodManager manager;
	private List<String> reslist;
	private Drawable[] micImages;
	//private int chatType;
	//private EMConversation conversation;
	private NewMessageBroadcastReceiver receiver;
	public static ChatActivity activityInstance = null;
	// 给谁发送消息
	private String toChatUsername;
    private IMUser toChatUser;
	private VoiceRecorder voiceRecorder;
	private MessageAdapter adapter;
	private File cameraFile;
	static int resendPos;

	private GroupListener groupListener;

	private ImageView iv_emoticons_normal;
	private ImageView iv_emoticons_checked;
	private RelativeLayout edittext_layout;
	private ProgressBar loadmorePB;
	private boolean isloading;
	private final int pagesize = 20;
	private boolean haveMoreData = true;
	private ImageView btnMore;
	public String playMsgId;
    private String myUserInfoJson;
    private DotView dots;
    private ImageView[] imageViews;
    private ImageView imageView;
	private String CurrentFriend;
    private String conversation;
    private String chatType;
    public static List<MessageBean> messageList =new LinkedList<>();
	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 切换msg切换图片
			micImage.setImageDrawable(micImages[msg.what]);
		}
	};
	private EMGroup group;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
        Intent intent=getIntent();

        toChatUsername = intent.getStringExtra("friend_id");
        conversation = intent.getStringExtra("conversation");
        chatType = intent.getStringExtra("chatType");
        //test
       // toChatUsername=100006+"";
       // chatType="single";
      //  conversation="0";
		initView();
		setUpView();
        initdata();
	}

    private void initdata() {
        messageList=IMClient.getInstance().getMessages(toChatUsername,0);
        adapter.refresh();
    }


    /**
	 * initView
	 */
	protected void initView() {
        titleHeaderBar = (TitleHeaderBar) findViewById(R.id.title_bar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		recordingContainer = findViewById(R.id.recording_container);
		micImage = (ImageView) findViewById(R.id.mic_image);
		recordingHint = (TextView) findViewById(R.id.recording_hint);
		listView = (ListView) findViewById(R.id.list);
		mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
		buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
		edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
		buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
		buttonSend = findViewById(R.id.btn_send);
		buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
		expressionViewpager = (ViewPager) findViewById(R.id.vPager);
        dots = (DotView) findViewById(R.id.face_dot_view);
		expressionContainer = (RelativeLayout) findViewById(R.id.ll_face_container);
		btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
//		locationImgview = (ImageView) findViewById(R.id.btn_location);
		iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
		iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
		loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
		btnMore = (ImageView) findViewById(R.id.btn_more);
		iv_emoticons_normal.setVisibility(View.VISIBLE);
		iv_emoticons_checked.setVisibility(View.GONE);
		more = findViewById(R.id.more);
//		edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);

		// 动画资源文件,用于录制语音时
		micImages = new Drawable[] { getResources().getDrawable(R.drawable.record_animate_00), getResources().getDrawable(R.drawable.record_animate_01),
				getResources().getDrawable(R.drawable.record_animate_02), getResources().getDrawable(R.drawable.record_animate_03),
				getResources().getDrawable(R.drawable.record_animate_04), getResources().getDrawable(R.drawable.record_animate_05),
				getResources().getDrawable(R.drawable.record_animate_06), getResources().getDrawable(R.drawable.record_animate_07),
				getResources().getDrawable(R.drawable.record_animate_08), getResources().getDrawable(R.drawable.record_animate_09),
				getResources().getDrawable(R.drawable.record_animate_10), getResources().getDrawable(R.drawable.record_animate_11),
				getResources().getDrawable(R.drawable.record_animate_12), getResources().getDrawable(R.drawable.record_animate_13),
				 };
		// 表情list
		reslist = getExpressionRes(35);
		// 初始化表情viewpager
        List<View> views = new ArrayList<View>();
        View gv1 = getGridChildView(1);
        View gv2 = getGridChildView(2);
        views.add(gv1);
        views.add(gv2);
        final int num=views.size();
        dots.setNum(num);
		expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
        expressionViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                dots.setSelected(position % num);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
		edittext_layout.requestFocus();
		voiceRecorder = new VoiceRecorder(micImageHandler);
		buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
//		mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				if (hasFocus) {
//					edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
//				} else {
//					edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
//				}
//
//			}
//		});
		mEditTextContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
				more.setVisibility(View.GONE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.GONE);
				expressionContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.GONE);
			}
		});
		// 监听文字框
		mEditTextContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!TextUtils.isEmpty(s)) {
					btnMore.setVisibility(View.GONE);
					buttonSend.setVisibility(View.VISIBLE);
				} else {
					btnMore.setVisibility(View.VISIBLE);
					buttonSend.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("chatActivity resume");
        HandleImMessage.getInstance().registerMessageListener(this,conversation);
//        MobclickAgent.onPageStart("page_talking");
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        options.setNoticeBySound(false);
        refresh();
    }
    protected void updateGroup() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    group = EMGroupManager.getInstance().getGroupFromServer(group.getGroupId());
                    //更新本地数据
                    EMGroupManager.getInstance().createOrUpdateLocalGroup(group);

                    runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });
                }
            }
        }).start();
    }




    private void setUpView() {
        activityInstance = this;
        titleHeaderBar.enableBackKey(true);

        findViewById(R.id.ly_title_bar_left).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityWithNoAnim(intent);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

		iv_emoticons_normal.setOnClickListener(this);
		iv_emoticons_checked.setOnClickListener(this);
		// position = getIntent().getIntExtra("position", -1);
		clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
		// 判断单聊还是群聊

		if ("single".equals(chatType)) { // 单聊
			//toChatUsername = getIntent().getStringExtra("userId");
           // toChatUser = AccountManager.getInstance().getContactList(mContext).get(toChatUsername);
//            if(toChatUser==null){
//                finish();
//            }
			titleHeaderBar.getTitleTextView().setText(toChatUsername);

			// conversation =
			// EMChatManager.getInstance().getConversation(toChatUsername,false);
		} else {
			// 群聊
           // toChatUsername = getIntent().getStringExtra("groupId");
            titleHeaderBar.setRightViewImageRes(R.drawable.ic_more);
           // group = EMGroupManager.getInstance().getGroup(toChatUsername);

            //if(group!=null){
                titleHeaderBar.getTitleTextView().setText(toChatUsername);
            //}
            Fragment fragment = new GroupDetailFragment();
            Bundle args = new Bundle();
            args.putString("groupId",toChatUsername);
            fragment.setArguments(args); // FragmentActivity将点击的菜单列表标题传递给Fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.menu_frame, fragment).commit();
            titleHeaderBar.setRightOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(mContext,GroupDetailsActivity.class);
//                    intent.putExtra("groupId",toChatUsername);
//                    startActivity(intent);
                    //END即gravity.right 从右向左显示   START即left  从左向右弹出显示
                    if (drawerLayout.isDrawerVisible(GravityCompat.END)) {
                        drawerLayout.closeDrawer(GravityCompat.END);//关闭抽屉
                    } else {
                        drawerLayout.openDrawer(GravityCompat.END);//打开抽屉
                    }

                }
            });

			// conversation =
			// EMChatManager.getInstance().getConversation(toChatUsername,true);
		}
		//conversation = EMChatManager.getInstance().getConversation(toChatUsername);
		// 把此会话的未读数置为0
		//conversation.resetUnsetMsgCount();
		adapter = new MessageAdapter(this, toChatUsername, chatType,conversation);
		// 显示消息
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new ListScrollListener());
		int count = listView.getCount();
		if (count > 0) {
			listView.setSelection(count - 1);
		}

		listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				more.setVisibility(View.GONE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.GONE);
				expressionContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.GONE);
				return false;
			}
		});
        // 注册一个cmd消息的BroadcastReceiver
        IntentFilter cmdIntentFilter = new IntentFilter(EMChatManager.getInstance().getCmdMessageBroadcastAction());
        cmdIntentFilter.setPriority(3);
        mContext.registerReceiver(cmdMessageReceiver, cmdIntentFilter);
		// 注册接收消息广播
		receiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		// 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
		intentFilter.setPriority(5);
		registerReceiver(receiver, intentFilter);

		// 注册一个ack回执消息的BroadcastReceiver
		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(5);
		registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

		// 注册一个消息送达的BroadcastReceiver
		IntentFilter deliveryAckMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getDeliveryAckMessageBroadcastAction());
		deliveryAckMessageIntentFilter.setPriority(5);
		registerReceiver(deliveryAckMessageReceiver, deliveryAckMessageIntentFilter);
		// 监听当前会话的群聊解散被T事件
		groupListener = new GroupListener();
		EMGroupManager.getInstance().addGroupChangeListener(groupListener);

		// show forward message if the message is not null
		String forward_msg_id = getIntent().getStringExtra("forward_msg_id");
		if (forward_msg_id != null) {
			// 显示发送要转发的消息
			forwardMessage(forward_msg_id);
		}

	}

	/**
	 * 转发消息
	 *
	 * @param forward_msg_id
	 */
	protected void forwardMessage(String forward_msg_id) {
		EMMessage forward_msg = EMChatManager.getInstance().getMessage(forward_msg_id);
		EMMessage.Type type = forward_msg.getType();
		switch (type) {
		case TXT:
			// 获取消息内容，发送消息
			String content = ((TextMessageBody) forward_msg.getBody()).getMessage();
            int extType = forward_msg.getIntAttribute(Constant.EXT_TYPE,0);
			sendText(content,extType);
			break;
		case IMAGE:
			// 发送图片
			String filePath = ((ImageMessageBody) forward_msg.getBody()).getLocalUrl();
			if (filePath != null) {
				File file = new File(filePath);
				if (!file.exists()) {
					// 不存在大图发送缩略图
					filePath = ImageUtils.getThumbnailImagePath(filePath);
				}
				sendPicture(filePath);
			}
			break;
		default:
			break;
		}
	}
    /**
	 * onActivityResult
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CODE_EXIT_GROUP) {
			setResult(RESULT_OK);
			finish();
			return;
		}
		if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
			switch (resultCode) {
			case RESULT_CODE_COPY: // 复制消息
//				EMMessage copyMsg = ((EMMessage) adapter.getItem(data.getIntExtra("position", -1)));
//				if (copyMsg.getType() == EMMessage.Type.IMAGE) {
//					ImageMessageBody imageBody = (ImageMessageBody) copyMsg.getBody();
//					// 加上一个特定前缀，粘贴时知道这是要粘贴一个图片
//					clipboard.setText(COPY_IMAGE + imageBody.getLocalUrl());
//				} else {
//					// clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
//					// ((TextMessageBody) copyMsg.getBody()).getMessage()));
//					clipboard.setText(((TextMessageBody) copyMsg.getBody()).getMessage());
//				}
				break;
			case RESULT_CODE_DELETE: // 删除消息
               // MessageBean deleteMsg=adapter.getItem(data.getIntExtra("position", -1));
                messageList.remove(data.getIntExtra("position", -1));
                adapter.refresh();
				listView.setSelection(data.getIntExtra("position", adapter.getCount()) - 1);
//				EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
//				conversation.removeMessage(deleteMsg.getMsgId());
//				adapter.refresh();
//				listView.setSelection(data.getIntExtra("position", adapter.getCount()) - 1);
				break;

			case RESULT_CODE_FORWARD: // 转发消息
//				EMMessage forwardMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", 0));
//				Intent intent = new Intent(this, ForwardMessageActivity.class);
//				intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
//				startActivity(intent);

				break;

			default:
				break;
			}
		}
		if (resultCode == RESULT_OK) { // 清空消息

			if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
				// 清空会话
				EMChatManager.getInstance().clearConversation(toChatUsername);
				adapter.refresh();
			} else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
				if (cameraFile != null && cameraFile.exists())
					sendPicture(cameraFile.getAbsolutePath());
			} else if (requestCode == REQUEST_CODE_SELECT_VIDEO) { // 发送本地选择的视频

				int duration = data.getIntExtra("dur", 0);
				String videoPath = data.getStringExtra("path");
				File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
				Bitmap bitmap = null;
				FileOutputStream fos = null;
				try {
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
					if (bitmap == null) {
						EMLog.d("chatactivity", "problem load video thumbnail bitmap,use default icon");
						bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_panel_video_icon);
					}
					fos = new FileOutputStream(file);

					bitmap.compress(CompressFormat.JPEG, 100, fos);

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						fos = null;
					}
					if (bitmap != null) {
						bitmap.recycle();
						bitmap = null;
					}

				}
				sendVideo(videoPath, file.getAbsolutePath(), duration / 1000);

			} else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						sendPicByUri(selectedImage);
					}
				}
			} else if (requestCode == REQUEST_CODE_SELECT_FILE) { // 发送选择的文件
				if (data != null) {
					Uri uri = data.getData();
					if (uri != null) {
						sendFile(uri);
					}
				}

			} else if (requestCode == REQUEST_CODE_MAP) { // 地图
				double latitude = data.getDoubleExtra("latitude", 0);
				double longitude = data.getDoubleExtra("longitude", 0);
				String locationAddress = data.getStringExtra("address");
				if (locationAddress != null && !locationAddress.equals("")) {
					more(more);
					sendLocationMsg(latitude, longitude, "", locationAddress);
				} else {
//					Toast.makeText(this, "无法获取到您的位置信息！", Toast.LENGTH_SHORT).show();
                    ToastUtil.getInstance(getApplicationContext()).showToast("找不到你在哪");
				}
				// 重发消息
			} else if (requestCode == REQUEST_CODE_TEXT) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_VOICE) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_PICTURE) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_LOCATION) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_VIDEO || requestCode == REQUEST_CODE_FILE) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
				// 粘贴
				if (!TextUtils.isEmpty(clipboard.getText())) {
					String pasteText = clipboard.getText().toString();
					if (pasteText.startsWith(COPY_IMAGE)) {
						// 把图片前缀去掉，还原成正常的path
						sendPicture(pasteText.replace(COPY_IMAGE, ""));
					}

				}
			} else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单
			//	EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
			//	addUserToBlacklist(deleteMsg.getFrom());
			} else if (messageList.size()> 0) {
				adapter.refresh();
				setResult(RESULT_OK);
			} else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
				adapter.refresh();
			}
		}
	}

	/**
	 * 消息图标点击事件
	 *
	 * @param view
	 */
	@Override
	public void onClick(View view) {

		int id = view.getId();
		if (id == R.id.btn_send) {// 点击发送按钮(发文字和表情)
			String s = mEditTextContent.getText().toString();
			sendText(s,0);
            //IMClient.getInstance().sendTextMessage(s)
		}else if (id == R.id.btn_my_guide) {
            MobclickAgent.onEvent(mContext, "event_share_plan_extra");
            Intent intent = new Intent(mContext, StrategyListActivity.class);
            intent.putExtra("chatType", chatType);
            intent.putExtra("toId", toChatUsername);
			intent.putExtra("userId", String.valueOf(AccountManager.getInstance().user.userId));
            intent.putExtra("isShare", true);
            intent.setAction("action.chat");
            startActivity(intent);
        } else if (id == R.id.btn_fav) {
            // 点击我的收藏图标
            MobclickAgent.onEvent(mContext,"event_share_favorite_extra");
            Intent intent = new Intent(mContext, FavListActivity.class);
            intent.putExtra("chatType", chatType);
            intent.putExtra("toId", toChatUsername);
            intent.putExtra("isShare", true);
            intent.setAction("action.chat");
            startActivity(intent);
        }  else if (id == R.id.btn_dest) {
            MobclickAgent.onEvent(mContext,"event_share_search_extra");
            Intent intent = new Intent(mContext, SearchAllActivity.class);
            intent.putExtra("chatType",chatType);
            intent.putExtra("toId",toChatUsername);
            intent.setAction("action.chat");
            startActivityWithNoAnim(intent);
			overridePendingTransition(android.R.anim.fade_in, R.anim.slide_stay);
            // 点击我的目的地图标
//            JSONObject contentJson = new JSONObject();
//            try {
//                contentJson.put("id","1");
//                contentJson.put("desc","我的景点描述");
//                contentJson.put("image","http://img0.bdstatic.com/img/image/shouye/lysxwz-6645354418.jpg");
//                contentJson.put("timeCost","3小时");
//                contentJson.put("name","景点");
//                sendText(contentJson.toString(), Constant.ExtType.SPOT);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        } /*else if (id == R.id.btn_location) {
            ToastUtil.getInstance(this).showToast("发送位置");*/
           /* MobclickAgent.onEvent(mContext,"event_share_travel_notes_extra");
            Intent intent = new Intent(mContext, TravelNoteSearchActivity.class);
            intent.putExtra("chatType",chatType);
            intent.putExtra("toId",toChatUsername);
            intent.setAction("action.chat");
            startActivity(intent);*/
//            // 点击我的目的地图标
//            JSONObject contentJson = new JSONObject();
//            try {
//                contentJson.put("id","1");
//                contentJson.put("desc","我的游记描述");
//                contentJson.put("image","http://img0.bdstatic.com/img/image/shouye/lysxwz-6645354418.jpg");
//                contentJson.put("name","游记");
//                sendText(contentJson.toString(), Constant.ExtType.TRAVELS);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        //}
        else if (id == R.id.btn_take_picture) {
			selectPicFromCamera();// 点击照相图标
		} else if (id == R.id.btn_picture) {
			selectPicFromLocal(); // 点击图片图标
		} else if (id == R.id.btn_location) { // 位置
			startActivityForResult(new Intent(this, BaiduMapActivity.class), REQUEST_CODE_MAP);
		} else if (id == R.id.iv_emoticons_normal) { // 点击显示表情框
			hideKeyboard();

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					more.setVisibility(View.VISIBLE);
					iv_emoticons_normal.setVisibility(View.GONE);
					iv_emoticons_checked.setVisibility(View.VISIBLE);
					btnContainer.setVisibility(View.GONE);
					expressionContainer.setVisibility(View.VISIBLE);
				}
			}, 150);
		} else if (id == R.id.iv_emoticons_checked) { // 点击隐藏表情框
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.GONE);
			btnContainer.setVisibility(View.VISIBLE);
			expressionContainer.setVisibility(View.GONE);
			more.setVisibility(View.GONE);

//		} else if (id == R.id.btn_video) {
//			// 点击摄像图标
//			Intent intent = new Intent(ChatActivity.this, ImageGridActivity.class);
//			startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
//		} else if (id == R.id.btn_file) { // 点击文件图标
//			selectFileFromLocal();
//		} else if (id == R.id.btn_voice_call) { //点击语音电话图标
//			if(!EMChatManager.getInstance().isConnected())
//				Toast.makeText(this, "尚未连接至服务器，请稍后重试", Toast.LENGTH_SHORT).show();
//			else
//				startActivity(new Intent(ChatActivity.this, VoiceCallActivity.class).
//						putExtra("username", toChatUsername).
//						putExtra("isComingCall", false));
		}
	}

	/**
	 * 照相获取图片
	 */
	public void selectPicFromCamera() {
		if (!CommonUtils.isExitsSdcard()) {
//			Toast.makeText(getApplicationContext(), "SD卡不存在，不能拍照", Toast.LENGTH_SHORT).show();
            ToastUtil.getInstance(ChatActivity.this).showToast("系统不支持拍照");
			return;
		}

	//	cameraFile = new File(PathUtil.getInstance().getImagePath(), AccountManager.getInstance().getLoginAccount(this).easemobUser
	//			+ System.currentTimeMillis() + ".jpg");
        cameraFile=new File(Config.imagepath ,TimeUtils.getTimestamp() + "_image.jpeg");
		cameraFile.getParentFile().mkdirs();
		startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				REQUEST_CODE_CAMERA);
	}

	/**
	 * 选择文件
	 */
	private void selectFileFromLocal() {
		Intent intent = null;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");
			intent.addCategory(Intent.CATEGORY_OPENABLE);

		} else {
			intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
	}

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_LOCAL);
	}



	/**
	 * 发送文本消息
	 *
	 * @param content
	 *            message content
	 */
	private void sendText(String content,int extType) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        MessageBean messageBean=IMClient.getInstance().createTextMessage(content,toChatUsername,chatType);
        messageList.add(messageBean);
			// 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
			adapter.refresh();
			listView.setSelection(listView.getCount() - 1);
			mEditTextContent.setText("");
			setResult(RESULT_OK);
	}

	/**
	 * 发送语音
	 *
	 * @param filePath
	 * @param fileName
	 * @param length
	 * @param isResend
	 */
	private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
		if (!(new File(filePath).exists())) {
			return;
		}
		try {
            MessageBean m =IMClient.getInstance().createAudioMessage(filePath,toChatUsername,length,chatType);
            messageList.add(m);
			adapter.refresh();
			listView.setSelection(listView.getCount() - 1);
			setResult(RESULT_OK);
			// send file
			// sendVoiceSub(filePath, fileName, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送图片
	 *
	 * @param filePath
	 */
	private void sendPicture(final String filePath) {
    MessageBean m =IMClient.getInstance().CreateImageMessage(filePath,toChatUsername,chatType);
      if (m!=null) {
          messageList.add(m);
          adapter.refresh();
          listView.setSelection(listView.getCount() - 1);
          setResult(RESULT_OK);
      }
	}

	/**
	 * 发送视频消息
	 */
	private void sendVideo(final String filePath, final String thumbPath, final int length) {
		final File videoFile = new File(filePath);
		if (!videoFile.exists()) {
			return;
		}
			listView.setAdapter(adapter);
			adapter.refresh();
			listView.setSelection(listView.getCount() - 1);
			setResult(RESULT_OK);


	}

	/**
	 * 根据图库图片uri发送图片
	 *
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
		// String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
//				Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
//				toast.setGravity(Gravity.CENTER, 0, 0);
//				toast.show();
                ToastUtil.getInstance(getApplicationContext()).showToast("找不到图片");
				return;
			}
			sendPicture(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
//				Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
//				toast.setGravity(Gravity.CENTER, 0, 0);
//				toast.show();
                ToastUtil.getInstance(ChatActivity.this).showToast("找不到图片");
				return;

			}
			sendPicture(file.getAbsolutePath());
		}

	}
   private void updateStatus(MessageBean m){

   }
	/**
	 * 发送位置信息
	 *
	 * @param latitude
	 * @param longitude
	 * @param imagePath
	 * @param locationAddress
	 */
	private void sendLocationMsg(double latitude, double longitude, String imagePath, String locationAddress) {


		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		listView.setSelection(listView.getCount() - 1);
		setResult(RESULT_OK);

	}

	/**
	 * 发送文件
	 *
	 * @param uri
	 */
	private void sendFile(Uri uri) {
//		String filePath = null;
//		if ("content".equalsIgnoreCase(uri.getScheme())) {
//			String[] projection = { "_data" };
//			Cursor cursor = null;
//
//			try {
//				cursor = getContentResolver().query(uri, projection, null, null, null);
//				int column_index = cursor.getColumnIndexOrThrow("_data");
//				if (cursor.moveToFirst()) {
//					filePath = cursor.getString(column_index);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
//			filePath = uri.getPath();
//		}
//		File file = new File(filePath);
//		if (file == null || !file.exists()) {
////			Toast.makeText(getApplicationContext(), "文件不存在", Toast.LENGTH_SHORT).show();
//            ToastUtil.getInstance(getApplicationContext()).showToast("文件不存在");
//			return;
//		}
//		if (file.length() > 10 * 1024 * 1024) {
////			Toast.makeText(getApplicationContext(), "文件不能大于10M", Toast.LENGTH_SHORT).show();
//            ToastUtil.getInstance(getApplicationContext()).showToast("文件太太太大了");
//			return;
//		}
//
//		// 创建一个文件消息
//		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.FILE);
//		// 如果是群聊，设置chattype,默认是单聊
//		if (chatType == CHATTYPE_GROUP)
//			message.setChatType(ChatType.GroupChat);
//
//		message.setReceipt(toChatUsername);
//		// add message body
//		NormalFileMessageBody body = new NormalFileMessageBody(new File(filePath));
//		message.addBody(body);
//        if(chatType==CHATTYPE_GROUP){
//            IMUtils.setMessageWithTaoziUserInfo(mContext,message);
//        }
//		conversation.addMessage(message);
//		listView.setAdapter(adapter);
//		adapter.refresh();
//		listView.setSelection(listView.getCount() - 1);
//		setResult(RESULT_OK);
	}

	/**
	 * 重发消息
	 */
	private void resendMessage() {
//		EMMessage msg = null;
//		msg = conversation.getMessage(resendPos);
//		// msg.setBackSend(true);
//		msg.status = EMMessage.Status.CREATE;

		adapter.refresh();
		listView.setSelection(resendPos);
	}

	/**
	 * 显示语音图标按钮
	 *
	 * @param view
	 */
	public void setModeVoice(final View view) {
		hideKeyboard();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mEditTextContent.setVisibility(View.GONE);
				more.setVisibility(View.GONE);
				view.setVisibility(View.GONE);
				buttonSetModeKeyboard.setVisibility(View.VISIBLE);
				buttonSend.setVisibility(View.GONE);
				btnMore.setVisibility(View.VISIBLE);
				buttonPressToSpeak.setVisibility(View.VISIBLE);

				iv_emoticons_normal.setVisibility(View.GONE);

				iv_emoticons_checked.setVisibility(View.GONE);
				btnContainer.setVisibility(View.VISIBLE);
				expressionContainer.setVisibility(View.GONE);
			}
		}, 150);

	}

	/**
	 * 显示键盘图标
	 *
	 * @param view
	 */
	public void setModeKeyboard(View view) {
		// mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener()
		// {
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// if(hasFocus){
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		// }
		// }
		// });
        mEditTextContent.setVisibility(View.VISIBLE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeVoice.setVisibility(View.VISIBLE);
		// mEditTextContent.setVisibility(View.VISIBLE);
		mEditTextContent.requestFocus();
		// buttonSend.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.GONE);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
		if (TextUtils.isEmpty(mEditTextContent.getText())) {
			btnMore.setVisibility(View.VISIBLE);
			buttonSend.setVisibility(View.GONE);
		} else {
			btnMore.setVisibility(View.GONE);
			buttonSend.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 点击清空聊天记录
	 *
	 * @param view
	 */
	public void emptyHistory(View view) {
		startActivityForResult(
				new Intent(this, IMAlertDialog.class).putExtra("titleIsCancel", true).putExtra("msg", "是否清空所有聊天记录").putExtra("cancel", true),
				REQUEST_CODE_EMPTY_HISTORY);
	}

	/**
	 * 点击进入群组详情
	 *
	 * @param view
	 */
	public void toGroupDetails(View view) {
		startActivityForResult((new Intent(this, GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
				REQUEST_CODE_GROUP_DETAIL);
	}

	/**
	 * 显示或隐藏图标按钮页
	 *
	 * @param view
	 */
	public void more(View view) {
		if (more.getVisibility() == View.GONE) {
			hideKeyboard();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					more.setVisibility(View.VISIBLE);
					btnContainer.setVisibility(View.VISIBLE);
					expressionContainer.setVisibility(View.GONE);
				}
			}, 150);
		} else {
			if (expressionContainer.getVisibility() == View.VISIBLE) {
				expressionContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.VISIBLE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.GONE);
			} else {
				more.setVisibility(View.GONE);
			}

		}

	}

	/**
	 * 点击文字输入框
	 *
	 * @param v
	 */
	public void editClick(View v) {
		listView.setSelection(listView.getCount() - 1);
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.GONE);
		}

	}

    @Override
    public void onMsgArrive(MessageBean m) {
        if (!toChatUsername.equals(String.valueOf(m.getSenderId()))&&"single".equals(chatType)) {
            m.setSendType(1);
            Toast.makeText(ChatActivity.this, "有新消息！", Toast.LENGTH_SHORT).show();
        } else {
            m.setSendType(1);
            messageList.add(m);
            adapter.refresh();
            int curSelection = listView.getFirstVisiblePosition();
            if (curSelection > listView.getCount() / 2) {
                listView.setSelection(listView.getCount() - 1);
            }
        }
    }

    /**
	 * 消息广播接收者
	 *
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String username = intent.getStringExtra("from");
			String msgid = intent.getStringExtra("msgid");
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			final EMMessage message = EMChatManager.getInstance().getMessage(msgid);
            final String fromUser = message.getStringAttribute(Constant.FROM_USER,"");
            final String finalUsername = username;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(!TextUtils.isEmpty(fromUser)){
                        IMUser imUser= IMUtils.getUserInfoFromMessage(mContext, message);
                        IMUserRepository.saveContact(mContext, imUser);
                    }
                }
            }).start();
			// 如果是群聊消息，获取到group id
			if (message.getChatType() == ChatType.GroupChat) {
				username = message.getTo();
			}
			if (!username.equals(toChatUsername)) {
				// 消息不是发给当前会话，return
				return;
			}
			// conversation =
			// EMChatManager.getInstance().getConversation(toChatUsername);
			// 通知adapter有新消息，更新ui
			adapter.refresh();
            int curSelection = listView.getFirstVisiblePosition();
            if(curSelection>listView.getCount()/2){
                listView.setSelection(listView.getCount() - 1);
            }
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}

	/**
	 * 消息回执BroadcastReceiver
	 */
	private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					msg.isAcked = true;
				}
			}
			abortBroadcast();
			adapter.notifyDataSetChanged();
		}
	};

	/**
	 * 消息送达BroadcastReceiver
	 */
	private BroadcastReceiver deliveryAckMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					msg.isDelivered = true;
				}
			}
			abortBroadcast();
			adapter.notifyDataSetChanged();
		}
	};
	private PowerManager.WakeLock wakeLock;

	/**
	 * 按住说话listener
	 *
	 */
	class PressToSpeakListen implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonUtils.isExitsSdcard()) {
					Toast.makeText(ChatActivity.this, "发送语音需要sdcard支持！", Toast.LENGTH_SHORT).show();
					return false;
				}
				try {
					v.setPressed(true);
					wakeLock.acquire();
					if (VoicePlayClickListener.isPlaying)
						VoicePlayClickListener.currentPlayListener.stopPlayVoice();
					recordingContainer.setVisibility(View.VISIBLE);
					recordingHint.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
                    MediaRecordFunc.getInstance().startRecordAndFile();
					//voiceRecorder.startRecording(null, toChatUsername, getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
					v.setPressed(false);
					if (wakeLock.isHeld())
						wakeLock.release();
					if(voiceRecorder != null)
						voiceRecorder.discardRecording();
					recordingContainer.setVisibility(View.INVISIBLE);
					Toast.makeText(ChatActivity.this, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
					return false;
				}

				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					recordingHint.setText(getString(R.string.release_to_cancel));
					recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
				} else {
					recordingHint.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				recordingContainer.setVisibility(View.INVISIBLE);
				if (wakeLock.isHeld())
					wakeLock.release();
				if (event.getY() < 0) {
					// discard the recorded audio.
					voiceRecorder.discardRecording();

				} else {
					// stop recording and send voice file
					try {
                        final String path = MediaRecordFunc.getInstance().stopRecordAndFile();
                        long time = com.lv.Utils.CommonUtils.getAmrDuration(new File(path));
						//int length = voiceRecorder.stopRecoding();
						//if (length > 0) {
							sendVoice(path, null,Long.toString(time/1000), false);
//						} else {
//                            ToastUtil.getInstance(getApplicationContext()).showToast("录音时间太短了");
//						}
					} catch (Exception e) {
						e.printStackTrace();
//						Toast.makeText(ChatActivity.this, "发送失败，请检测服务器是否连接", Toast.LENGTH_SHORT).show();
                        if (!isFinishing())
                        ToastUtil.getInstance(getApplicationContext()).showToast("呃~好像找不到网络");
					}

				}
				return true;
			default:
				recordingContainer.setVisibility(View.INVISIBLE);
				if(voiceRecorder != null)
					voiceRecorder.discardRecording();
				return false;
			}
		}
	}

	/**
	 * 获取表情的gridview的子view
	 *
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i) {
		View view = View.inflate(this, R.layout.expression_gridview, null);
		ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = reslist.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(reslist.subList(20, reslist.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情
					if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {

						if (filename != "delete_expression") { // 不是删除键，显示表情
							// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
							Class clz = Class.forName("com.xuejian.client.lxp.common.utils.SmileUtils");
							Field field = clz.getField(filename);
							mEditTextContent.append(SmileUtils.getSmiledText(ChatActivity.this, (String) field.get(null)));
						} else { // 删除文字或者表情
							if (!TextUtils.isEmpty(mEditTextContent.getText())) {

								int selectionStart = mEditTextContent.getSelectionStart();// 获取光标的位置
								if (selectionStart > 0) {
									String body = mEditTextContent.getText().toString();
									String tempStr = body.substring(0, selectionStart);
									int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
									if (i != -1) {
										CharSequence cs = tempStr.substring(i, selectionStart);
										if (SmileUtils.containsKey(cs.toString()))
											mEditTextContent.getEditableText().delete(i, selectionStart);
										else
											mEditTextContent.getEditableText().delete(selectionStart - 1, selectionStart);
									} else {
										mEditTextContent.getEditableText().delete(selectionStart - 1, selectionStart);
									}
								}
							}

						}
					}
				} catch (Exception e) {
				}

			}
		});
		return view;
	}

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;
			reslist.add(filename);
		}
		return reslist;

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
        System.out.println("chatActivity destroy");
        HandleImMessage.getInstance().unregisterMessageListener(this, conversation);
		activityInstance = null;
		EMGroupManager.getInstance().removeGroupChangeListener(groupListener);
		// 注销广播
		try {
			unregisterReceiver(receiver);
			receiver = null;
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(ackMessageReceiver);
			ackMessageReceiver = null;
			unregisterReceiver(deliveryAckMessageReceiver);
			deliveryAckMessageReceiver = null;
            unregisterReceiver(cmdMessageReceiver);

		} catch (Exception e) {
		}
	}

    public void refresh(){
        if(adapter!=null){
            adapter.refresh();
        }
    }

	@Override
	protected void onPause() {
		super.onPause();
//        MobclickAgent.onPageEnd("page_talking");
        System.out.println("chatActivity pause");
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        options.setNoticeBySound(new PeachHXSDKModel(mContext).getSettingMsgSound());
		if (wakeLock.isHeld())
			wakeLock.release();
		if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
			// 停止语音播放
			VoicePlayClickListener.currentPlayListener.stopPlayVoice();
		}

		try {
			// 停止录音
			if (voiceRecorder.isRecording()) {
				voiceRecorder.discardRecording();
				recordingContainer.setVisibility(View.INVISIBLE);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null) {
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}

	/**
	 * 加入到黑名单
	 *
	 * @param username
	 */
	private void addUserToBlacklist(String username) {
		try {
			EMContactManager.getInstance().addUserToBlackList(username, true);
//			Toast.makeText(getApplicationContext(), "移入黑名单成功", Toast.LENGTH_SHORT).show();
            if (!isFinishing())
            ToastUtil.getInstance(this).showToast("成功删除她");
		} catch (EaseMobException e) {
			e.printStackTrace();
//			Toast.makeText(getApplicationContext(), "移入黑名单失败", Toast.LENGTH_SHORT).show();
            if (!isFinishing())
            ToastUtil.getInstance(this).showToast("呃～好像找不到网络");
		}
	}

	/**
	 * 返回
	 *
	 * @param view
	 */
	public void back(View view) {
		finish();
	}

	/**
	 * 覆盖手机返回键
	 */
	@Override
	public void onBackPressed() {
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.GONE);
		} else {
            finish();
           /* Intent intent = new Intent(ChatActivity.this, IMMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityWithNoAnim(intent);
            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);*/
		}
	}

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("chatActivity stop");
    }

    /**
	 * listview滑动监听listener
	 *
	 */
	private class ListScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
					loadmorePB.setVisibility(View.VISIBLE);
					// sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
					List<EMMessage> messages=null;
					try {
						// 获取更多messges，调用此方法的时候从db获取的messages
						// sdk会自动存入到此conversation中

					} catch (Exception e1) {
						loadmorePB.setVisibility(View.GONE);
						return;
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
					if (messageList.size() != 0) {
						// 刷新ui
						adapter.notifyDataSetChanged();
						listView.setSelection(messageList.size() - 1);
						if (messageList.size() != pagesize)
							haveMoreData = false;
					} else {
						haveMoreData = false;
					}
					loadmorePB.setVisibility(View.GONE);
					isloading = false;

				}
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// 点击notification bar进入聊天页面，保证只有一个聊天页面
		String username = intent.getStringExtra("userId");
		if (toChatUsername.equals(username))
			super.onNewIntent(intent);
		else {
			startActivity(intent);
            finish();
		}

	}

    /**
     * cmd消息BroadcastReceiver
     */
    private BroadcastReceiver cmdMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取cmd message对象
            String msgId = intent.getStringExtra("msgid");
            EMMessage message = intent.getParcelableExtra("message");
            //获取消息body
            CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
            String aciton = cmdMsgBody.action;//获取自定义action
            //获取扩展属性
            try {
                int cmdType=message.getIntAttribute("CMDType");
                String content = message.getStringAttribute("content");
                //删除好友
                if (cmdType == 3) {
                    CmdDeleteBean deleteBean = GsonTools.parseJsonToBean(content,CmdDeleteBean.class);
                    final IMUser imUser = IMUserRepository.getContactByUserId(mContext, deleteBean.userId);
                    if(imUser!=null){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                //如果正在与此用户的聊天页面
                                if (ChatActivity.activityInstance != null && imUser.getUsername().equals(ChatActivity.activityInstance.getToChatUsername())) {
//                                    Toast.makeText(ChatActivity.this, toChatUser.getNick() + "已把你从他好友列表里移除", Toast.LENGTH_SHORT).show();
                                    ToastUtil.getInstance(ChatActivity.this).showToast("聊天已被中断");
                                    ChatActivity.activityInstance.finish();
                                }
                            }
                        });
                    }

                }

            } catch (EaseMobException e) {
                e.printStackTrace();
            }
        }
    };


    /**
	 * 监测群组解散或者被T事件
	 *
	 */
	class GroupListener extends GroupReomveListener {

		@Override
		public void onUserRemoved(final String groupId, String groupName) {
			runOnUiThread(new Runnable() {
				public void run() {
					if (toChatUsername.equals(groupId)) {
//						Toast.makeText(ChatActivity.this, "你被群创建者从此群中移除", Toast.LENGTH_SHORT).show();
						if (GroupDetailsActivity.instance != null)
							GroupDetailsActivity.instance.finish();
						finish();
					}
				}
			});
		}

		@Override
		public void onGroupDestroy(final String groupId, String groupName) {
			// 群组解散正好在此页面，提示群组被解散，并finish此页面
			runOnUiThread(new Runnable() {
				public void run() {
					if (toChatUsername.equals(groupId)) {
//						Toast.makeText(ChatActivity.this, "当前群聊已被群创建者解散", Toast.LENGTH_SHORT).show();
                        ToastUtil.getInstance(ChatActivity.this).showToast("该群已被群主解散");
						if (GroupDetailsActivity.instance != null)
							GroupDetailsActivity.instance.finish();
						finish();
					}
				}
			});
		}

	}

	public String getToChatUsername() {
		return toChatUsername;
	}

}
