package com.xuejian.client.lxp.module.toolbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.utils.SharePrefUtil;
import com.lv.Listener.HttpCallback;
import com.lv.im.IMClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.dialog.PeachEditDialog;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.ConstellationUtil;
import com.xuejian.client.lxp.common.utils.GuideViewUtils;
import com.xuejian.client.lxp.common.utils.ImageCache;
import com.xuejian.client.lxp.common.utils.IntentUtils;
import com.xuejian.client.lxp.config.SettingConfig;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.dest.CityPictureActivity;
import com.xuejian.client.lxp.module.dest.StrategyMapActivity;
import com.xuejian.client.lxp.module.my.LoginActivity;
import com.xuejian.client.lxp.module.my.ModifyNicknameActivity;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxp_dqm07 on 2015/5/18.
 */
public class HisMainPageActivity extends PeachBaseActivity implements View.OnClickListener {

    private long userId;
    private ArrayList<String> all_pics = new ArrayList<String>();
    User me;
    private User imUser;
    private TextView tv_send_action;
    private boolean isMyFriend;
    private int EDIT_MEMO = 101;
    private TextView nameTv;
    private String newMemo;
    User user;
    View handleView;
    private boolean isFromExperts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hismainpage);
        userId = getIntent().getLongExtra("userId", 0);
        isFromExperts = getIntent().getBooleanExtra("isFromExperts",false);
        me = AccountManager.getInstance().getLoginAccount(HisMainPageActivity.this);
        try {
            if (me != null) {
                imUser = UserDBManager.getInstance().getContactByUserId(userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(R.id.tv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (me != null) {
            isMyFriend = UserDBManager.getInstance().isMyFriend(userId);
        }
        handleView = findViewById(R.id.tv_handle_action);
        if (me != null) {
            if (userId != 10000 && isMyFriend) {
                handleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showActionDialog(1);
                    }
                });
            } else {
               // handleView.setVisibility(View.INVISIBLE);
                handleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showActionDialog(2);
                    }
                });
            }
        } else {
            handleView.setVisibility(View.GONE);
        }

        findViewById(R.id.fl_send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTalk();
            }
        });

        tv_send_action = (TextView) findViewById(R.id.tv_send_action);

        if (isMyFriend) {
            tv_send_action.setText("备注");
        }

        initData(userId);
//        UserApi.editMemo(100000+"", "hihi", new HttpCallBack() {
//            @Override
//            public void doSuccess(Object result, String method) {
//                System.out.println(result.toString());
//            }
//
//            @Override
//            public void doFailure(Exception error, String msg, String method) {
//                System.out.println(msg);
//                error.printStackTrace();
//            }
//        });
    }

    private void editMemo(String memo) {
        UserApi.editMemo(String.valueOf(userId), memo, new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                ToastUtil.getInstance(HisMainPageActivity.this).showToast("修改成功");
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void addToFriend() {
        if (me != null) {
            final PeachEditDialog editDialog = new PeachEditDialog(this);
            editDialog.setTitle("朋友验证");
            editDialog.setMessage(String.format("Hi, 我是%s", me.getNickName()));
            editDialog.setPositiveButton("确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editDialog.dismiss();
                    try {
                        DialogManager.getInstance().showLoadingDialog(HisMainPageActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    UserApi.requestAddContact(String.valueOf(userId), editDialog.getMessage(), new HttpCallBack() {
                        @Override
                        public void doSuccess(Object result, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            ToastUtil.getInstance(getApplicationContext()).showToast("请求已发送，等待对方验证");
                            finish();
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
//                            error.printStackTrace();
                            DialogManager.getInstance().dissMissLoadingDialog();
                            ToastUtil.getInstance(HisMainPageActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method, int code) {

                        }
                    });
                }
            });

            editDialog.show();
        } else {
            Intent intent = new Intent(HisMainPageActivity.this, LoginActivity.class);
            startActivityWithNoAnim(intent);
            overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
        }
    }

    private void startTalk() {
        if (me != null) {
            List<String> uids = new ArrayList<>();
            uids.add(String.valueOf(userId));
            try {
                IMClient.getInstance().getConversationAttrs(AccountManager.getCurrentUserId(), uids, new HttpCallback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject res = new JSONObject(result);
                            JSONArray array = res.getJSONArray("result");
                            SettingConfig.getInstance().setLxpNoticeSetting(HisMainPageActivity.this, String.valueOf(array.getJSONObject(0).getInt("targetId")), array.getJSONObject(0).getBoolean("muted"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        IMClient.getInstance().addToConversation(String.valueOf(userId), "single");
                        Intent intent = new Intent(HisMainPageActivity.this, ChatActivity.class);
                        intent.putExtra("friend_id", String.valueOf(userId));
                        intent.putExtra("chatType", "single");
                        startActivity(intent);
                    }

                    @Override
                    public void onFailed(int code) {
                        System.out.println(code);
                        IMClient.getInstance().addToConversation(String.valueOf(userId), "single");
                        Intent intent = new Intent(HisMainPageActivity.this, ChatActivity.class);
                        intent.putExtra("friend_id", String.valueOf(userId));
                        intent.putExtra("chatType", "single");
                        startActivity(intent);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
                IMClient.getInstance().addToConversation(String.valueOf(userId), "single");
                Intent intent = new Intent(HisMainPageActivity.this, ChatActivity.class);
                intent.putExtra("friend_id", String.valueOf(userId));
                intent.putExtra("chatType", "single");
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(HisMainPageActivity.this, LoginActivity.class);
            startActivityWithNoAnim(intent);
            overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
        }
    }

    public void initData(long id) {
        getUserInfo(id);
    }

    private void showActionDialog(int style) {
        final Activity act = this;
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        View contentView = View.inflate(act, R.layout.dialog_home_confirm_action, null);
        Button btn = (Button) contentView.findViewById(R.id.btn_go_plan);
        btn.setTextColor(getResources().getColor(R.color.color_checked));
        if (style == 1) {
            btn.setText("删除");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final PeachMessageDialog deleteDialog = new PeachMessageDialog(act);
                    deleteDialog.setTitle("提示");
                    deleteDialog.setMessage("删除确认");
                    deleteDialog.setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteContact(userId);
                            deleteDialog.dismiss();
                        }
                    });
                    deleteDialog.setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteDialog.dismiss();
                        }
                    });
                    deleteDialog.show();

                    dialog.dismiss();
                }
            });
        } else {
            btn.setText("屏蔽");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MobclickAgent.onEvent(mContext, "event_delete_it");
                    final PeachMessageDialog deleteDialog = new PeachMessageDialog(act);
                    deleteDialog.setTitle("提示");
                    deleteDialog.setMessage("确认屏蔽");
                    deleteDialog.setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            blockUser(String.valueOf(userId),false);
                            deleteDialog.dismiss();
                        }
                    });
                    deleteDialog.setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteDialog.dismiss();
                        }
                    });
                    deleteDialog.show();

                    dialog.dismiss();
                }
            });
        }
        contentView.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        WindowManager windowManager = act.getWindowManager();
        Window window = dialog.getWindow();
        window.setContentView(contentView);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = display.getWidth(); // 设置宽度
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }

    private void blockUser(String userId,boolean isBlock) {
        if (isBlock){
            UserApi.removeFromBlackList(userId, new HttpCallBack() {
                @Override
                public void doSuccess(Object result, String method) {

                }

                @Override
                public void doFailure(Exception error, String msg, String method) {

                }

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });
        }else {
            UserApi.addToBlackList(userId, new HttpCallBack() {
                @Override
                public void doSuccess(Object result, String method) {

                }

                @Override
                public void doFailure(Exception error, String msg, String method) {

                }

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });
        }
    }

    private void deleteContact(final long userId) {
        try {
            DialogManager.getInstance().showLoadingDialog(mContext, "正在删除...");
        }catch (Exception e){
            DialogManager.getInstance().dissMissLoadingDialog();
        }
        UserApi.deleteContact(String.valueOf(userId), new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> deleteResult = CommonJson.fromJson((String) result, ModifyResult.class);
                if (deleteResult.code == 0) {
                    UserDBManager.getInstance().deleteContact(userId);
                    AccountManager.getInstance().getContactList(HisMainPageActivity.this).remove(userId);
                    finish();
                } else if (!TextUtils.isEmpty(deleteResult.err.message)) {
                    ToastUtil.getInstance(HisMainPageActivity.this).showToast(deleteResult.err.message);
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                ToastUtil.getInstance(HisMainPageActivity.this).showToast("删除失败");
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

    }

    public void updateView(final User bean) {
        user = bean;
        nameTv = (TextView) findViewById(R.id.tv_title);
        try {
            if (isMyFriend && imUser != null) {
                if (TextUtils.isEmpty(imUser.getMemo())) {
                    nameTv.setText(bean.getNickName());
                } else {
                    nameTv.setText(imUser.getMemo() + "(" + bean.getNickName() + ")");
                }
            } else {
                nameTv.setText(bean.getNickName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            nameTv.setText(bean.getNickName());
        }


        findViewById(R.id.fl_send_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMyFriend) {
                    if (!TextUtils.isEmpty(nameTv.getText().toString())) {
                        Intent intent = new Intent(HisMainPageActivity.this, ModifyNicknameActivity.class);
                        intent.putExtra("isEditMemo", true);
                        intent.putExtra("nickname", bean.getNickName());
                        intent.putExtra("userId", String.valueOf(userId));
                        startActivityForResult(intent, EDIT_MEMO);
                    }
                    //editMemo("123");
                } else {
                    addToFriend();
                }
            }
        });


        TextView idTv = (TextView) findViewById(R.id.tv_subtitle);
        idTv.setText(String.format("ID：%d", bean.getUserId()));
        ImageView avatarImage = (ImageView) findViewById(R.id.iv_avatar);
        if (!TextUtils.isEmpty(bean.getAvatar())){
            avatarImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> pics =new ArrayList<>();
                    pics.add(bean.getAvatar());
                    showSelectedPics(pics);
                }
            });
        }else {
            avatarImage.setClickable(false);
        }

        FrameLayout fl_avatar = (FrameLayout) findViewById(R.id.fl_gender_bg);
        ImageLoader.getInstance().displayImage(bean.getAvatar(), avatarImage, new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_home_talklist_default_avatar)
                .showImageOnFail(R.drawable.ic_home_talklist_default_avatar)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(
                        getResources().getDimensionPixelSize(R.dimen.user_profile_entry_height)))) // 设置成圆角图片
                .build());
        TextView tvLevel = (TextView) findViewById(R.id.tv_level);
        if (bean.getGender().equalsIgnoreCase("M")) {
            fl_avatar.setForeground( readBitMap(HisMainPageActivity.this, R.drawable.ic_home_avatar_border_boy));
            tvLevel.setBackgroundResource(R.drawable.ic_home_level_bg_boy);
            tvLevel.setText(String.format("LV%s", bean.getLevel()));
        } else if (bean.getGender().equalsIgnoreCase("F")) {
            fl_avatar.setForeground( readBitMap(HisMainPageActivity.this, R.drawable.ic_home_avatar_border_girl));
            tvLevel.setBackgroundResource(R.drawable.ic_home_level_bg_girl);
            tvLevel.setText(String.format("LV%s", bean.getLevel()));
        } else {
            fl_avatar.setForeground(readBitMap(HisMainPageActivity.this, R.drawable.ic_home_avatar_border_unknown));
            tvLevel.setBackgroundResource(R.drawable.ic_home_level_bg_unknown);
        }

        ImageView constellationIv = (ImageView) findViewById(R.id.iv_constellation);
        int res = ConstellationUtil.calculateConstellation(bean.getBirthday());
        if (res == 0) {
            constellationIv.setImageResource(R.drawable.ic_home_constellation_unknown);
        } else constellationIv.setImageResource(res);


        TextView tvMemo = (TextView) findViewById(R.id.tv_memo);
        if (!TextUtils.isEmpty(bean.getSignature())) {
            tvMemo.setText(bean.getMemo());
        } else {
            tvMemo.setText("~什么都没写~");
        }
        TextView tvLocation = (TextView) findViewById(R.id.tv_location);
        if (TextUtils.isEmpty(bean.getResidence())) {
            tvLocation.setText("未设置");
        } else {
            tvLocation.setText(bean.getResidence());
        }
        TextView tvAge = (TextView) findViewById(R.id.tv_age);
        if (TextUtils.isEmpty(bean.getBirthday())) {
            tvAge.setText("未设置");
        } else {
            tvAge.setText(String.valueOf(getAge(bean.getBirthday())));
        }

        TextView tvPlan = (TextView) findViewById(R.id.tv_profile_plan);
        SpannableString planStr = new SpannableString("计划");
        planStr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_text_iii)), 0, planStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        planStr.setSpan(new AbsoluteSizeSpan(14, true), 0, planStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder spb = new SpannableStringBuilder();
        spb.append(String.format("%d条\n", bean.getGuideCnt())).append(planStr);
        tvPlan.setText(spb);
        tvPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(HisMainPageActivity.this, "button_item_plan");
                Intent intent = new Intent(HisMainPageActivity.this, StrategyListActivity.class);
                intent.putExtra("userId", String.valueOf(userId));
                startActivity(intent);
            }
        });

        final TextView tvTrack = (TextView) findViewById(R.id.tv_profile_track);
        SpannableString trackStr = new SpannableString("足迹");
        trackStr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_text_iii)), 0, trackStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        trackStr.setSpan(new AbsoluteSizeSpan(14, true), 0, trackStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        final ArrayList<LocBean> trackCitys = new ArrayList<LocBean>();
        ssb.append(String.format("%d国%d城市\n", bean.getCountryCnt(), bean.getTrackCnt())).append(trackStr);
        tvTrack.setText(ssb);
        tvTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(HisMainPageActivity.this, "button_item_tracks");
                Intent intent = new Intent(HisMainPageActivity.this, StrategyMapActivity.class);
                intent.putExtra("isExpertFootPrint", true);
                intent.putExtra("title", tvTrack.getText().toString());
                intent.putExtra("id", String.valueOf(userId));
                startActivity(intent);
            }
        });
        TextView tvNotes = (TextView) findViewById(R.id.tv_profile_travelnotes);
        SpannableString noteStr = new SpannableString("游记");
        noteStr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_text_iii)), 0, noteStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        noteStr.setSpan(new AbsoluteSizeSpan(14, true), 0, noteStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder nsb = new SpannableStringBuilder();
        nsb.append(String.format("%d篇\n", bean.getTravelNoteCnt())).append(noteStr);
        tvNotes.setText(nsb);
        tvNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(HisMainPageActivity.this, "button_item_travel_notes");
            }
        });
        tvNotes.setVisibility(View.INVISIBLE);
        if (!SharePrefUtil.getBoolean(this, "expert_guide1", false)&&isFromExperts) {
            GuideViewUtils.getInstance().initGuide(this, "expert_guide1", "有问题可以向达人请教噢", CommonUtils.getScreenHeight(this)-300,CommonUtils.getScreenWidth(this)/2-20,R.drawable.guide_view_bg_down);
        }
    }

    private void showSelectedPics(ArrayList<String> pics) {
        if (pics.size()==0){
            return;
        }
        IntentUtils.intentToPicGallery2(HisMainPageActivity.this, pics, 0);
    }
    public int getAge(String birth) {
        if (TextUtils.isEmpty(birth)) return 0;
        int age = 0;
        String birthType = birth.substring(0, 4);
        int birthYear = Integer.parseInt(birthType);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String date = sdf.format(new java.util.Date());
        age = Integer.parseInt(date) - birthYear;
        return age;
    }

    public void getUserInfo(long userid) {
        try {
            DialogManager.getInstance().showModelessLoadingDialog(mContext);
        }catch (Exception e){
            DialogManager.getInstance().dissMissModelessLoadingDialog();
        }
        UserApi.getUserInfo(String.valueOf(userid), new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                CommonJson<User> expertInfo = CommonJson.fromJson(result, User.class);
                if (expertInfo.code == 0) {
                    updateView(expertInfo.result);
                } else {
                    finish();
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                ToastUtil.getInstance(HisMainPageActivity.this).showToast("好像没有网络~");
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
        UserApi.getUserPicAlbumn(String.valueOf(userId), new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    if (jsonObject.getInt("code") == 0) {
                        JSONArray object = jsonObject.getJSONArray("result");
                        updatePics(object.length());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void updatePics(int num) {
        TextView tvAlbum = (TextView) findViewById(R.id.tv_profile_album);
        SpannableString albumStr = new SpannableString("相册");
        albumStr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_text_iii)), 0, albumStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        albumStr.setSpan(new AbsoluteSizeSpan(14, true), 0, albumStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder asb = new SpannableStringBuilder();
        asb.append(String.format("%d张\n", num)).append(albumStr);
        tvAlbum.setText(asb);
        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(HisMainPageActivity.this, "button_item_album");
                Intent intent2 = new Intent(HisMainPageActivity.this, CityPictureActivity.class);
                intent2.putExtra("id", String.valueOf(userId));
                intent2.putExtra("title", user.getNickName());
                intent2.putExtra("isTalentAlbum", true);
                startActivity(intent2);
            }
        });
    }

    public void initScrollView(int userId) {
        UserApi.getUserPicAlbumn(String.valueOf(userId), new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    if (jsonObject.getInt("code") == 0) {
                        JSONArray object = jsonObject.getJSONArray("result");
                        for (int i = 0; i < object.length(); i++) {
                            JSONArray imgArray = object.getJSONObject(i).getJSONArray("image");
                            all_pics.add(imgArray.getJSONObject(0).getString("url"));
                        }
                        refreshUserPics(all_pics);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void doFailure(Exception error, String msg, String method) {
                ToastUtil.getInstance(HisMainPageActivity.this).showToast("好像没有网络额~");
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    public void refreshUserPics(final ArrayList<String> pics) {
//        his_pics_sv.removeAllViews();
//        LinearLayout llPics = new LinearLayout(this);
//        llPics.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        llPics.removeAllViews();
//        for (int i = 0; i < pics.size(); i++) {
//            View view = View.inflate(HisMainPageActivity.this, R.layout.my_all_pics_cell, null);
//            my_pics_cell = (ImageView) view.findViewById(R.id.my_pics_cell);
//            ImageLoader.getInstance().displayImage(pics.get(i), my_pics_cell, options);
//            my_pics_cell.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    IntentUtils.intentToPicGallery2(HisMainPageActivity.this, pics, 0);
//                                                }
//                                            }
//            );
//
//            llPics.addView(view);
//        }
//        his_pics_sv.addView(llPics);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("page_user_profile");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("page_user_profile");
        MobclickAgent.onPause(this);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == EDIT_MEMO) {
                newMemo = data.getStringExtra("memo");
                if (!TextUtils.isEmpty(newMemo)) {
                    nameTv.setText(newMemo + "(" + user.getNickName() + ")");
                }
            }
        }
    }

    public static Drawable readBitMap(Context context, int resId) {
        try {
            Bitmap bitmap = ImageCache.getInstance().get(String.valueOf(resId));
            if (bitmap==null){
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inPreferredConfig = Bitmap.Config.RGB_565;
                opt.inPurgeable = true;
                opt.inInputShareable = true;
                InputStream is = context.getResources().openRawResource(resId);
                bitmap = BitmapFactory.decodeStream(is, null, opt);
                ImageCache.getInstance().put(String.valueOf(resId),bitmap);
            }
            return new BitmapDrawable(bitmap);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
