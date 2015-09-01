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
package com.xuejian.client.lxp.module.toolbox.im;

import android.animation.LayoutTransition;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.core.widget.DotView;
import com.lv.Audio.MediaRecordFunc;
import com.lv.bean.MessageBean;
import com.lv.im.HandleImMessage;
import com.lv.im.IMClient;
import com.lv.utils.Config;
import com.lv.utils.TimeUtils;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.ChatBaseActivity;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.SmileUtils;
import com.xuejian.client.lxp.common.widget.ExpandGridView;
import com.xuejian.client.lxp.common.widget.PasteEditText;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.dest.SearchAllActivity;
import com.xuejian.client.lxp.module.toolbox.StrategyListActivity;
import com.xuejian.client.lxp.module.toolbox.im.adapter.ExpressionAdapter;
import com.xuejian.client.lxp.module.toolbox.im.adapter.ExpressionPagerAdapter;
import com.xuejian.client.lxp.module.toolbox.im.adapter.MessageAdapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 聊天页面
 */
public class ChatActivity extends ChatBaseActivity implements OnClickListener, HandleImMessage.MessageHandler, SensorEventListener {

    private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
    public static final int REQUEST_CODE_CONTEXT_MENU = 3;
    private static final int REQUEST_CODE_MAP = 4;
    public static final int REQUEST_CODE_TEXT = 5;
    public static final int REQUEST_CODE_VOICE = 6;
    public static final int REQUEST_CODE_PICTURE = 7;
    public static final int REQUEST_CODE_LOCATION = 8;
    //    public static final int REQUEST_CODE_NET_DISK = 9;
//    public static final int REQUEST_CODE_FILE = 10;
    public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
    //    public static final int REQUEST_CODE_PICK_VIDEO = 12;
//    public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
//    public static final int REQUEST_CODE_VIDEO = 14;
    public static final int REQUEST_CODE_EXT = 15;
    //    public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
//    public static final int REQUEST_CODE_SEND_USER_CARD = 17;
    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_LOCAL = 19;
    //    public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
//    public static final int REQUEST_CODE_GROUP_DETAIL = 21;
    public static final int REQUEST_CODE_SELECT_VIDEO = 23;
    public static final int REQUEST_CODE_SELECT_FILE = 24;
    public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

//    public static final int REQUEST_CODE_ADD_USER = 26;

    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
    public static final int RESULT_CODE_OPEN = 4;
    public static final int RESULT_CODE_DWONLOAD = 5;
    public static final int RESULT_CODE_TO_CLOUD = 6;
    public static final int RESULT_CODE_EXIT_GROUP = 7;

    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    private int PAGE;
    public static final String COPY_IMAGE = "EASEMOBIMG";
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
    private FrameLayout expressionContainer;
    private LinearLayout btnContainer;
    private FrameLayout mExtraPanel;
    private ClipboardManager clipboard;
    private InputMethodManager manager;
    private List<String> reslist;
    private Drawable[] micImages;
    private String toChatUsername;
    private MessageAdapter adapter;
    private File cameraFile;


    private ImageView iv_emoticons_normal;
    private ImageView iv_emoticons_checked;
    private RelativeLayout edittext_layout;
    private ProgressBar loadmorePB;
    private boolean isloading;
    private final int pagesize = 20;
    private boolean haveMoreData = true;
    private ImageButton btnMore;
    public String playMsgId;
    private DotView dots;
    private String conversation;
    private String chatType;
    public static List<MessageBean> messageList = new LinkedList<>();
    private User user;
    TextView titleView;
    private int currentSize;
    private String changedTitle = null;

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private static class MyHandler extends Handler {

        private final WeakReference<ChatActivity> mActivity;

        public MyHandler(ChatActivity activity) {
            mActivity = new WeakReference<ChatActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ChatActivity activity = mActivity.get();
            if (activity != null) {
                activity.micImage.setImageDrawable(activity.micImages[msg.what]);
            }
        }
    }

    private final MyHandler handler = new MyHandler(this);

    private boolean isRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        toChatUsername = intent.getStringExtra("friend_id");
        conversation = intent.getStringExtra("conversation");
        chatType = intent.getStringExtra("chatType");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(10001);
        user = UserDBManager.getInstance().getContactByUserId(Long.parseLong(toChatUsername));
        initView();
        setUpView();


        if ("single".equals(chatType) && user == null) {
            getUserInfo(Integer.parseInt(toChatUsername));
        }
        initData();
    }


    public void getUserInfo(int userId) {
        try {
            DialogManager.getInstance().showModelessLoadingDialog(mContext);
        } catch (Exception e) {
            DialogManager.getInstance().dissMissModelessLoadingDialog();
        }
        UserApi.getUserInfo(String.valueOf(userId), new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                CommonJson<User> userInfo = CommonJson.fromJson(result, User.class);
                UserDBManager.getInstance().saveContact(userInfo.result);
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void initData() {
        messageList.clear();
        messageList.addAll(IMClient.getInstance().getMessages(toChatUsername, 0));
        currentSize=messageList.size();
        adapter.refresh();
        int count = listView.getCount();
        if (count > 0) {
            listView.setSelection(count - 1);
        }
    }

    /**
     * initView
     */
    protected void initView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        recordingContainer = findViewById(R.id.recording_container);
        micImage = (ImageView) findViewById(R.id.mic_image);
        recordingHint = (TextView) findViewById(R.id.recording_hint);
        listView = (ListView) findViewById(R.id.content_list);
        mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
        buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
        buttonSend = findViewById(R.id.btn_send);
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
        expressionViewpager = (ViewPager) findViewById(R.id.vPager);
        dots = (DotView) findViewById(R.id.face_dot_view);
        expressionContainer = (FrameLayout) findViewById(R.id.ll_face_container);
        btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
        iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
        iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
        loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
        btnMore = (ImageButton) findViewById(R.id.btn_more);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.GONE);
        mExtraPanel = (FrameLayout) findViewById(R.id.fl_extra_panel);

        setPanelAnimation();

        // 动画资源文件,用于录制语音时
        micImages = new Drawable[]{getResources().getDrawable(R.drawable.record_animate_00), getResources().getDrawable(R.drawable.record_animate_01),
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
        final int num = views.size();
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
        buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
        mEditTextContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mExtraPanel.setVisibility(View.GONE);
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
//                    iv_emoticons_normal.setVisibility(View.GONE);
                    buttonSend.setVisibility(View.VISIBLE);
                } else {
                    btnMore.setVisibility(View.VISIBLE);
//                    iv_emoticons_normal.setVisibility(View.VISIBLE);
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
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                GroupDetailFragment fragment = (GroupDetailFragment) getSupportFragmentManager().findFragmentByTag("GroupDrawer");
                if (fragment != null) {
                    fragment.closeDeleteMode();
                }
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("page_lxp_chatting");
        MobclickAgent.onResume(this);
        LogUtil.d("resume");
        HandleImMessage.getInstance().registerMessageListener(this, conversation);
    }

    private void setPanelAnimation() {
        LayoutTransition lt = new LayoutTransition();
        lt.setStagger(LayoutTransition.CHANGE_APPEARING, 10);
        lt.setStagger(LayoutTransition.APPEARING, 20);
        lt.setDuration(LayoutTransition.CHANGE_DISAPPEARING, 0);
        lt.setDuration(LayoutTransition.DISAPPEARING, 0);
        lt.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        lt.setStartDelay(LayoutTransition.DISAPPEARING, 0);
        mExtraPanel.setLayoutTransition(lt);
    }

    private void setUpView() {
        findViewById(R.id.iv_na_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityWithNoAnim(intent);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);*/
                beforeBack();
            }
        });

        iv_emoticons_normal.setOnClickListener(this);
        iv_emoticons_checked.setOnClickListener(this);
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");

        titleView = (TextView) findViewById(R.id.tv_na_title);

        if (user == null) {
            titleView.setText(toChatUsername);
        } else titleView.setText(user.getNickName());
        // 判断单聊还是群聊
        if ("single".equals(chatType)) { // 单聊
            final Fragment fragment = new ChatMenuFragment();
            Bundle args = new Bundle();
            args.putString("userId", toChatUsername);
            args.putString("conversation", conversation);
            fragment.setArguments(args); // FragmentActivity将点击的菜单列表标题传递给Fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(fragment, "ChatMenuDrawer");
            ft.replace(R.id.menu_frame, fragment).commit();
            findViewById(R.id.iv_nav_menu).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //END即gravity.right 从右向左显示   START即left  从左向右弹出显示
                    if (drawerLayout.isDrawerVisible(GravityCompat.END)) {
                        drawerLayout.closeDrawer(GravityCompat.END);//关闭抽屉
                    } else {
                        MobclickAgent.onEvent(ChatActivity.this, "navigation_item_chat_setting");
                        drawerLayout.openDrawer(GravityCompat.END);//打开抽屉
                    }
                }
            });
        } else {
            // 群聊
            final Fragment fragment = new GroupDetailFragment();
            Bundle args = new Bundle();
            args.putString("groupId", toChatUsername);
            args.putString("conversation", conversation);
            fragment.setArguments(args); // FragmentActivity将点击的菜单列表标题传递给Fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(fragment, "GroupDrawer");
            ft.replace(R.id.menu_frame, fragment).commit();
            findViewById(R.id.iv_nav_menu).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //END即gravity.right 从右向左显示   START即left  从左向右弹出显示
                    if (drawerLayout.isDrawerVisible(GravityCompat.END)) {
                        drawerLayout.closeDrawer(GravityCompat.END);//关闭抽屉
                    } else {
                        drawerLayout.openDrawer(GravityCompat.END);//打开抽屉
                    }
                }
            });
        }
        adapter = new MessageAdapter(this, toChatUsername, chatType, conversation);
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
                mExtraPanel.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.GONE);
                expressionContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
                buttonPressToSpeak.setVisibility(View.GONE);
                mEditTextContent.setVisibility(View.VISIBLE);
                return false;
            }
        });
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
                    MessageBean copyMsg = ((MessageBean) adapter.getItem(data.getIntExtra("position", -1)));
                    if (copyMsg.getType() == Config.IMAGE_MSG) {

                    } else {
                        clipboard.setText(copyMsg.getMessage());
                    }
                    break;

                case RESULT_CODE_DELETE: // 删除消息
                    int pos = data.getIntExtra("position", -1);
                    IMClient.getInstance().deleteSingleMessage(toChatUsername, messageList.get(pos).getLocalId());
                    messageList.remove(pos);
                    adapter.refresh();
                    listView.setSelection(data.getIntExtra("position", adapter.getCount()) - 1);
                    break;

                case RESULT_CODE_FORWARD: // 转发消息
                    break;

                default:
                    break;
            }
        }
        if (resultCode == RESULT_OK) { // 清空消息
            switch (requestCode) {
                case REQUEST_CODE_EMPTY_HISTORY:
                    // 清空会话
                    IMClient.getInstance().cleanMessageHistory(toChatUsername);
                    adapter.refresh();
                    break;
                case REQUEST_CODE_CAMERA: // 发送照片
                    if (cameraFile != null && cameraFile.exists())
                        sendPicture(cameraFile.getAbsolutePath());
                    break;
                case REQUEST_CODE_SELECT_VIDEO: // 发送本地选择的视频
                    break;
                case REQUEST_CODE_LOCAL: // 发送本地图片
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            sendPicByUri(selectedImage);
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_FILE: // 发送选择的文件
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            sendFile(uri);
                        }
                    }
                    break;
                case REQUEST_CODE_MAP:// 地图
                    double latitude = data.getDoubleExtra("latitude", 0);
                    double longitude = data.getDoubleExtra("longitude", 0);
                    String locationAddress = data.getStringExtra("address");
                    String path = data.getStringExtra("path");
                    if (locationAddress != null && !locationAddress.equals("")) {
                        more(mExtraPanel);
                        sendLocationMsg(latitude, longitude, locationAddress, path);
                    } else {
                        ToastUtil.getInstance(getApplicationContext()).showToast("找不到你在哪");
                    }
                    break;
                // 重发消息
                case REQUEST_CODE_TEXT:
                case REQUEST_CODE_VOICE:
                case REQUEST_CODE_PICTURE:
                case REQUEST_CODE_LOCATION:
                case REQUEST_CODE_EXT:
//                    resendMessage();
                    break;
                case REQUEST_CODE_COPY_AND_PASTE:
                    // 粘贴
                    if (!TextUtils.isEmpty(clipboard.getText())) {
                        String pasteText = clipboard.getText().toString();
                        if (pasteText.startsWith(COPY_IMAGE)) {
                            // 把图片前缀去掉，还原成正常的path
                            sendPicture(pasteText.replace(COPY_IMAGE, ""));
                        }
                    }
                    break;

                case REQUEST_CODE_ADD_TO_BLACKLIST: // 移入黑名单
                    break;

                default:
                    if (messageList.size() > 0) {
                        adapter.refresh();
                        setResult(RESULT_OK);
                    }
                    break;
            }
        }
    }

    public void setTitleText(String titleText) {
        if ("single".equals(chatType)) {

        } else {
            changedTitle = titleText;
            titleView.setText(titleText);
        }
    }

    /**
     * 消息图标点击事件
     */
    @Override
    public void onClick(View view) {
        if (isFastClick()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.btn_send) {// 点击发送按钮(发文字和表情)
            String s = mEditTextContent.getText().toString();
            sendText(s, 0);
        } else if (id == R.id.btn_my_guide) {
            MobclickAgent.onEvent(ChatActivity.this, "chat_item_lxpplan");
            try {
                Intent intent = new Intent(mContext, StrategyListActivity.class);
                intent.putExtra("chatType", chatType);
                intent.putExtra("toId", toChatUsername);
                intent.putExtra("conversation", conversation);
                intent.putExtra("userId", AccountManager.getCurrentUserId());
                intent.putExtra("isShare", true);
                //  intent.setAction("action.chat");
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.btn_dest) {
            MobclickAgent.onEvent(ChatActivity.this, "chat_item_lxpsearch");
            Intent intent = new Intent(mContext, SearchAllActivity.class);
            intent.putExtra("chatType", chatType);
            intent.putExtra("toId", toChatUsername);
            intent.putExtra("conversation", conversation);
            intent.putExtra("isShare", true);
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
            MobclickAgent.onEvent(ChatActivity.this, "chat_item_lxplocation");
            startActivityForResult(new Intent(this, BaiduMapActivity.class), REQUEST_CODE_MAP);
        } else if (id == R.id.iv_emoticons_normal) { // 点击显示表情框
            hideKeyboard();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mExtraPanel.setVisibility(View.VISIBLE);
                    iv_emoticons_normal.setVisibility(View.GONE);
                    iv_emoticons_checked.setVisibility(View.VISIBLE);
                    btnContainer.setVisibility(View.GONE);
                    expressionContainer.setVisibility(View.VISIBLE);
                }
            }, 100);
        } else if (id == R.id.iv_emoticons_checked) { // 点击隐藏表情框
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.GONE);
            btnContainer.setVisibility(View.GONE);
            expressionContainer.setVisibility(View.GONE);
            mExtraPanel.setVisibility(View.GONE);
            showKeyboard(mEditTextContent);
        }
    }

    /**
     * 照相获取图片
     */

    public void selectPicFromCamera() {
        if (!CommonUtils.isExitsSdcard()) {
            ToastUtil.getInstance(ChatActivity.this).showToast("系统不支持拍照");
            return;
        }
        if (!isCameraCanUse()) {
            ToastUtil.getInstance(ChatActivity.this).showToast("权限被禁止，请开启权限后重试");
            return;
        }
        cameraFile = new File(Config.imagepath, TimeUtils.getTimestamp() + "_image.jpeg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    public boolean isCameraCanUse() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            canUse = false;
        }
        if (canUse) {
            mCamera.release();
            mCamera = null;
        }
        return canUse;
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
     * @param content message content
     */
    private void sendText(String content, int extType) {
        if (TextUtils.isEmpty(content) || content.trim().isEmpty()) {
            return;
        }
        MessageBean messageBean = IMClient.getInstance().createTextMessage(AccountManager.getCurrentUserId(), content, toChatUsername, chatType);
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
            MessageBean m = IMClient.getInstance().createAudioMessage(AccountManager.getCurrentUserId(), filePath, toChatUsername, length, chatType);
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
        MessageBean m = IMClient.getInstance().CreateImageMessage(AccountManager.getCurrentUserId(), filePath, toChatUsername, chatType);
        if (m != null) {
            messageList.add(m);
            adapter.refresh();
            listView.setSelection(listView.getCount() - 1);
            setResult(RESULT_OK);
        } else {
            ToastUtil.getInstance(ChatActivity.this).showToast("图片解析失败");
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
        Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex("_data");
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            if (picturePath == null || picturePath.equals("null")) {
                ToastUtil.getInstance(getApplicationContext()).showToast("找不到图片");
                return;
            }
            sendPicture(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                ToastUtil.getInstance(ChatActivity.this).showToast("找不到图片");
                return;
            }
            sendPicture(file.getAbsolutePath());
        }

    }

    /**
     * 发送位置信息
     */
    private void sendLocationMsg(double latitude, double longitude, String locationAddress, String path) {
        MessageBean m = IMClient.getInstance().CreateLocationMessage(AccountManager.getCurrentUserId(), conversation, toChatUsername, chatType, latitude, longitude, locationAddress, path);
        messageList.add(m);
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
    public void resendMessage(int msgPostion) {
        IMClient.getInstance().changeMessageStatus(toChatUsername, messageList.get(msgPostion).getLocalId(), 1);
        messageList.get(msgPostion).setStatus(1);
        adapter.refresh();
//        listView.setSelection(msgPostion);
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
                mExtraPanel.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
                buttonSetModeKeyboard.setVisibility(View.VISIBLE);
                buttonSend.setVisibility(View.GONE);
                btnMore.setVisibility(View.VISIBLE);
                buttonPressToSpeak.setVisibility(View.VISIBLE);

                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
                expressionContainer.setVisibility(View.GONE);
            }
        }, 100);

    }

    /**
     * 显示键盘图标
     */
    public void setModeKeyboard(View view) {
        mEditTextContent.setVisibility(View.VISIBLE);
        mExtraPanel.setVisibility(View.GONE);
        btnContainer.setVisibility(View.GONE);
        expressionContainer.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        mEditTextContent.requestFocus();
        buttonPressToSpeak.setVisibility(View.GONE);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(mEditTextContent.getText())) {
            btnMore.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.GONE);
        } else {
            btnMore.setVisibility(View.GONE);
            buttonSend.setVisibility(View.VISIBLE);
        }
        showKeyboard(mEditTextContent);
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
     * 显示或隐藏图标按钮页
     *
     * @param view
     */
    public void more(View view) {
        if (mExtraPanel.getVisibility() == View.GONE) {
            hideKeyboard();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mExtraPanel.setVisibility(View.VISIBLE);
                    btnContainer.setVisibility(View.VISIBLE);
                    expressionContainer.setVisibility(View.GONE);

                    buttonSetModeVoice.setVisibility(View.VISIBLE);
                    buttonSetModeKeyboard.setVisibility(View.GONE);

                    buttonPressToSpeak.setVisibility(View.GONE);
                    mEditTextContent.setVisibility(View.VISIBLE);
                    mEditTextContent.requestFocus();

                    iv_emoticons_normal.setVisibility(View.VISIBLE);
                    iv_emoticons_checked.setVisibility(View.GONE);

                }
            }, 100);
        } else {
            if (expressionContainer.getVisibility() == View.VISIBLE) {
                expressionContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.VISIBLE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.GONE);
            } else {
                mExtraPanel.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onMsgArrive(MessageBean m, String groupId) {
        if ("single".equals(chatType) && !groupId.equals(String.valueOf(0))) {
            Toast.makeText(ChatActivity.this, "有新消息！", Toast.LENGTH_SHORT).show();
        } else if (("single".equals(chatType) && !toChatUsername.equals(String.valueOf(m.getSenderId())))) {
            Toast.makeText(ChatActivity.this, "有新消息！", Toast.LENGTH_SHORT).show();
        } else if (("group".equals(chatType) && !toChatUsername.equals(groupId))) {
            Toast.makeText(ChatActivity.this, "有新消息！", Toast.LENGTH_SHORT).show();
        } else {
            m.setSendType(1);
            messageList.add(m);
//            MessageBean messageBean =new MessageBean();
//            messageBean.setMessage("{\"title\":\"title\",\"desc\":\"desc\",\"image\":\"http://7xirnn.com1.z0.glb.clouddn.com/2ed2cb7c-ac84-4720-9aa0-b5bb8dba6795!thumb?e=1439527658&token=jU6KkDZdGYODmrPVh5sbBIkJX65y-Cea991uWpWZ:QmwHGiZqUA-Cg0p4hgxNLY8f6F4=\",\"url\":\"http://m.creatby.com/manage/book/b10qbu/\"}");
//            messageBean.setType(18);
//            messageBean.setSenderId(100014);
//            messageBean.setSendType(1);
//            messageBean.setCreateTime(System.currentTimeMillis());
//            messageList.add(messageBean);
            adapter.refresh();
            int curSelection = listView.getFirstVisiblePosition();
            if (curSelection > listView.getCount() / 2) {
                listView.setSelection(listView.getCount() - 1);
            }
        }
    }

    @Override
    public void onCMDMessageArrive(MessageBean m, String groupId) {
        if (("group".equals(chatType) && toChatUsername.equals(groupId))) {
            GroupDetailFragment fragment = (GroupDetailFragment) getSupportFragmentManager().findFragmentByTag("GroupDrawer");
            if (fragment != null) {
                fragment.setUpGroupMemeber("update");
            }
        }
    }

    private PowerManager.WakeLock wakeLock;

    /**
     * 按住说话listener
     */
    private class PressToSpeakListen implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (isFastClick()) {
                        return false;
                    }
                    if (!CommonUtils.isExitsSdcard()) {
                        Toast.makeText(ChatActivity.this, "发送语音需要sd卡支持！", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    try {

                        MediaRecordFunc.getInstance().startRecordNotFile();
                        final String path = MediaRecordFunc.getInstance().stopRecordAndFile();
                        long time = com.lv.utils.CommonUtils.getAmrDuration(new File(path));
                        if (time <= 0) {
                            ToastUtil.getInstance(ChatActivity.this).showToast("录音权限被禁止，请先开启录音权限");
                            MediaRecordFunc.getInstance().cancleRecord();
                            return false;
                        }
                        v.setPressed(true);
                        wakeLock.acquire();
                        if (VoicePlayClickListener.isPlaying)
                            VoicePlayClickListener.currentPlayListener.stopPlayVoice();
                        recordingContainer.setVisibility(View.VISIBLE);
                        recordingHint.setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        int code = MediaRecordFunc.getInstance().startRecordAndFile(handler);
                        if (code == 1010) {
                            ToastUtil.getInstance(ChatActivity.this).showToast("录音权限被禁止，请先开启录音权限");
                            isRecord = false;
                            recordingContainer.setVisibility(View.INVISIBLE);
                            if (wakeLock.isHeld())
                                wakeLock.release();
                            v.setPressed(false);
                            return false;
                        } else isRecord = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        isRecord = false;
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        MediaRecordFunc.getInstance().cancleRecord();
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
                    isRecord = false;
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {
                        MediaRecordFunc.getInstance().cancleRecord();
                    } else {
                        try {
                            final String path = MediaRecordFunc.getInstance().stopRecordAndFile();
                            long time = com.lv.utils.CommonUtils.getAmrDuration(new File(path));
                            if (time > 1000) {
                                sendVoice(path, null, (Long.valueOf(time).intValue() / 1000.0) + "", false);
                            } else {
                                ToastUtil.getInstance(getApplicationContext()).showToast("录音时间太短", 500);
                                MediaRecordFunc.getInstance().cancleRecord();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            MediaRecordFunc.getInstance().cancleRecord();
                            if (!isFinishing())
                                ToastUtil.getInstance(getApplicationContext()).showToast("呃~好像找不到网络");
                        }

                    }
                    isRecord = false;
                    return true;
                default:
                    recordingContainer.setVisibility(View.INVISIBLE);
                    MediaRecordFunc.getInstance().cancleRecord();
                    return false;
            }
        }
    }

    /**
     * 获取表情的gridView的子view
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
                    e.printStackTrace();
                }

            }
        });
        return view;
    }

    public List<String> getExpressionRes(int getSum) {
        List<String> resList = new ArrayList<String>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "ee_" + x;
            resList.add(filename);
        }
        return resList;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HandleImMessage.getInstance().unregisterMessageListener(this, conversation);
        CommonUtils.fixInputMethodManagerLeak(this);
    }

    public void refresh() {
        if (adapter != null) {
            adapter.refresh();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("page_lxp_chatting");
        MobclickAgent.onPause(this);
        if (wakeLock.isHeld())
            wakeLock.release();
        if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
            // 停止语音播放
            VoicePlayClickListener.currentPlayListener.stopPlayVoice();
        }

        try {
            // 停止录音
            if (isRecord) {
                MediaRecordFunc.getInstance().cancleRecord();
                recordingContainer.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private void showKeyboard(View view) {
        manager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }


    public void back(View view) {
        finish();
    }

    /**
     * 覆盖手机返回键
     */
    @Override
    public void onBackPressed() {
        beforeBack();
    }

    public void beforeBack() {
        if (drawerLayout.isDrawerVisible(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else if (mExtraPanel.getVisibility() == View.VISIBLE) {
            mExtraPanel.setVisibility(View.GONE);
            expressionContainer.setVisibility(View.GONE);
            btnContainer.setVisibility(View.GONE);
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.GONE);
        } else {

            if ("single".equals(chatType)) {

            } else {
                if (changedTitle != null && changedTitle.trim().length() > 0) {
                    user.setNickName(changedTitle);
                    UserDBManager.getInstance().saveContact(user);
                    Intent intent = new Intent();
                    intent.putExtra("changedTitle", changedTitle);
                    setResult(RESULT_OK, intent);
                }

            }

            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * listview滑动监听listener
     */
    private class ListScrollListener implements OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case OnScrollListener.SCROLL_STATE_IDLE:
                    if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                        loadmorePB.setVisibility(View.VISIBLE);
                        try {
                            currentSize = messageList.size();
                            messageList.clear();
                            messageList.addAll(IMClient.getInstance().getMessages(toChatUsername, ++PAGE));
                        } catch (Exception e1) {
                            loadmorePB.setVisibility(View.GONE);
                            return;
                        }
                        if (messageList.size() != 0) {
                            // 刷新ui
                            adapter.notifyDataSetChanged();
                            if (messageList.size()>currentSize){
                                listView.setSelection(messageList.size()-currentSize-1);
                            }
                            if (messageList.size() == currentSize) {
                                haveMoreData = false;
                            }
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
        long username = intent.getLongExtra("userId", 0);
        toChatUsername = intent.getStringExtra("friend_id");
        conversation = intent.getStringExtra("conversation");
        chatType = intent.getStringExtra("chatType");
        if (toChatUsername.equals(String.valueOf(username)))
            super.onNewIntent(intent);
        else {
            startActivity(intent);
            finish();
        }
    }

    private static long mLastClickTime;


    public static boolean isFastClick() {
        // 当前时间
        long currentTime = System.currentTimeMillis();
// 两次点击的时间差
        long time = currentTime - mLastClickTime;
        if (0 < time && time < 700) {
            return true;
        }


        mLastClickTime = currentTime;
        return false;
    }

    public int checkOp() {
        if (Build.VERSION.SDK_INT > 19) {
            int uid = 0;
            try {
                PackageManager pm = getPackageManager();
                ApplicationInfo ai = pm.getApplicationInfo("com.xuejian.client.lxp", PackageManager.GET_ACTIVITIES);
                uid = ai.uid;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            AppOpsManager manager = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
            int result = manager.checkOp("27", uid, "com.xuejian.client.lxp");
            System.out.println(uid + " " + result);
        }
        return 0;
    }

}
