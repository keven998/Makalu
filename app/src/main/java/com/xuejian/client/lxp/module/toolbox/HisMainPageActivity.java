package com.xuejian.client.lxp.module.toolbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
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
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.dest.CityPictureActivity;
import com.xuejian.client.lxp.module.dest.StrategyMapActivity;
import com.xuejian.client.lxp.module.my.LoginActivity;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hismainpage);
        userId = getIntent().getLongExtra("userId", 0);
        me = AccountManager.getInstance().getLoginAccount(HisMainPageActivity.this);
        imUser = UserDBManager.getInstance().getContactByUserId(userId);
        findViewById(R.id.tv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        isMyFriend = UserDBManager.getInstance().isMyFriend(userId);
        View handleView = findViewById(R.id.tv_handle_action);
        if (me != null) {
            if (userId != 10000 && isMyFriend) {
                handleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showActionDialog(1);
                    }
                });
            } else {
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
        findViewById(R.id.fl_send_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMyFriend) {
                    editMemo("123");
                } else {
                    addToFriend();
                }
            }
        });

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
        });
    }

    private void addToFriend() {
        if (me != null) {
            final PeachEditDialog editDialog = new PeachEditDialog(this);
            editDialog.setTitle("好友验证");
            editDialog.setMessage(String.format("Hi, 我是%s", me.getNickName()));
            editDialog.setPositiveButton("确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editDialog.dismiss();
                    DialogManager.getInstance().showLoadingDialog(HisMainPageActivity.this);
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
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("friend_id", String.valueOf(userId));
            intent.putExtra("chatType", "single");
            startActivity(intent);
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
        if (style==1){
            btn.setText("删除");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MobclickAgent.onEvent(mContext, "event_delete_it");
                    final PeachMessageDialog deleteDialog = new PeachMessageDialog(act);
                    deleteDialog.setTitle("提示");
                    deleteDialog.setMessage("删除确认");
                    deleteDialog.setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteContact(imUser);
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
        }else {
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
                         // deleteContact(imUser);
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

    private void deleteContact(final User tobeDeleteUser) {
        DialogManager.getInstance().showLoadingDialog(this, "正在删除...");
        UserApi.deleteContact(String.valueOf(tobeDeleteUser.getUserId()), new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> deleteResult = CommonJson.fromJson((String) result, ModifyResult.class);
                if (deleteResult.code == 0) {
                    UserDBManager.getInstance().deleteContact(tobeDeleteUser.getUserId());
                    AccountManager.getInstance().getContactList(HisMainPageActivity.this).remove(tobeDeleteUser.getUserId());
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
        });

    }

    public void updateView(final User bean) {
        TextView nameTv = (TextView) findViewById(R.id.tv_title);
        nameTv.setText(bean.getNickName());
        TextView idTv = (TextView) findViewById(R.id.tv_subtitle);
        idTv.setText(String.format("ID：%d", bean.getUserId()));
        ImageView avatarImage = (ImageView) findViewById(R.id.iv_avatar);
        ImageLoader.getInstance().displayImage(bean.getAvatar(), avatarImage, new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.messages_bg_useravatar)
                .showImageOnFail(R.drawable.messages_bg_useravatar)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(
                        getResources().getDimensionPixelSize(R.dimen.user_profile_entry_height)))) // 设置成圆角图片
                .build());

        TextView tvLevel = (TextView) findViewById(R.id.tv_level);
        if (bean.getGender().equalsIgnoreCase("M")) {
            avatarImage.setBackgroundResource(R.drawable.ic_home_avatar_border_boy);
            tvLevel.setBackgroundResource(R.drawable.ic_home_level_bg_boy);
            tvLevel.setText(String.format("LV%s", bean.getLevel()));
        } else if (bean.getGender().equalsIgnoreCase("F")) {
            avatarImage.setBackgroundResource(R.drawable.ic_home_avatar_border_girl);
            tvLevel.setBackgroundResource(R.drawable.ic_home_level_bg_girl);
            tvLevel.setText(String.format("LV%s", bean.getLevel()));
        } else {
            avatarImage.setBackgroundResource(R.drawable.ic_home_avatar_border_unknown);
            tvLevel.setBackgroundResource(R.drawable.ic_home_level_bg_unknown);
        }

        ImageView constellationIv = (ImageView) findViewById(R.id.iv_constellation);
        constellationIv.setImageResource(R.drawable.ic_home_constellation_unknown);

        TextView tvMemo = (TextView) findViewById(R.id.tv_memo);
        if (!TextUtils.isEmpty(bean.getMemo())) {
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
        if (bean.getBirthday() == null) {
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
                Intent intent = new Intent(HisMainPageActivity.this, StrategyListActivity.class);
                intent.putExtra("userId", String.valueOf(userId));
                startActivity(intent);
            }
        });

        TextView tvTrack = (TextView) findViewById(R.id.tv_profile_track);
        SpannableString trackStr = new SpannableString("足迹");
        trackStr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_text_iii)), 0, trackStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        trackStr.setSpan(new AbsoluteSizeSpan(14, true), 0, trackStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        int countries = 0;
        int cityCount = 0;
        final ArrayList<LocBean> trackCitys = new ArrayList<LocBean>();
        try {
            JSONObject jsonObject = new JSONObject(bean.getTracks().toString());
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                countries++;
                String key = (String) iterator.next();
                int size = bean.getTracks().get(key).size();
                cityCount += size;
                for (int i = 0; i < size; i++) {
                    trackCitys.add(bean.getTracks().get(key).get(i));
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ssb.append(String.format("%d国%d城市\n", countries, cityCount)).append(trackStr);
        tvTrack.setText(ssb);
        tvTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HisMainPageActivity.this, StrategyMapActivity.class);
                intent.putExtra("isExpertFootPrint", true);
                intent.putParcelableArrayListExtra("ExpertFootPrintBean", trackCitys);
                startActivity(intent);
            }
        });
        TextView tvNotes = (TextView) findViewById(R.id.tv_profile_travelnotes);
        SpannableString noteStr = new SpannableString("游记");
        noteStr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_text_iii)), 0, noteStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        noteStr.setSpan(new AbsoluteSizeSpan(14, true), 0, noteStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder nsb = new SpannableStringBuilder();
        nsb.append(String.format("%d篇\n", 99)).append(noteStr);
        tvNotes.setText(nsb);
        tvNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    public int getAge(String birth) {
        int age = 0;
        String birthType = birth.substring(0, 4);
        int birthYear = Integer.parseInt(birthType);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String date = sdf.format(new java.util.Date());
        age = Integer.parseInt(date) - birthYear;
        return age;
    }

    public void getUserInfo(long userid) {
        DialogManager.getInstance().showModelessLoadingDialog(mContext);
        UserApi.getUserInfo(String.valueOf(userid), new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                CommonJson<User> expertInfo = CommonJson.fromJson(result, User.class);
                if (expertInfo.code == 0) {
                    updateView(expertInfo.result);
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                ToastUtil.getInstance(HisMainPageActivity.this).showToast("好像没有网络~");
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
                Intent intent2 = new Intent(HisMainPageActivity.this, CityPictureActivity.class);
                intent2.putExtra("id", String.valueOf(userId));
                intent2.putExtra("title", imUser.getNickName());
                intent2.putExtra("isTalentAlbum",true);
                startActivity(intent2);
            }
        });
    }

    public void initFlDestion(List<LocBean> locBeans) {
//        String destinations = "";
//        if (locBeans.size() > 0) {
//            for (int j = 0; j < locBeans.size(); j++) {
//                destinations += (locBeans.get(j).zhName + "  ");
//            }
//            his_destinations.setText(destinations);
//        } else {
//            his_destinations.setText("还没有我的足迹");
//        }
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onClick(View v) {

    }
}
