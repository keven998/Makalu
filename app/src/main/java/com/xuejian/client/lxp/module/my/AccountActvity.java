package com.xuejian.client.lxp.module.my;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.LocalDisplay;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.bean.UploadTokenBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.CustomLoadingDialog;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.dialog.MoreDialog;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.IntentUtils;
import com.xuejian.client.lxp.common.utils.SelectPicUtils;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.MainActivity;
import com.xuejian.client.lxp.module.dest.CityPictureActivity;
import com.xuejian.client.lxp.module.dest.StrategyMapActivity;
import com.xuejian.client.lxp.module.toolbox.StrategyListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Rjm on 2014/10/11.
 */
public class AccountActvity extends PeachBaseActivity implements View.OnClickListener {


    @ViewInject(R.id.iv_more_header_frame_gender)
    private ImageView iv_header_frame_gender;
    @ViewInject(R.id.iv_avatar)
    private ImageView avatarIv;

    @ViewInject(R.id.tv_nickname)
    private TextView tv_nickname;
    @ViewInject(R.id.tv_gender)
    private TextView tv_gender;
    @ViewInject(R.id.tv_zodiac)
    private TextView tv_zodiac;
    @ViewInject(R.id.tv_resident)
    private TextView tv_resident;
    @ViewInject(R.id.tv_plan)
    private TextView tv_plan;
    @ViewInject(R.id.tv_foot_print)
    private TextView tv_foot_print;
    @ViewInject(R.id.tv_photo)
    private TextView tv_photo;
    @ViewInject(R.id.tv_bind_phone)
    private TextView tv_bind_phone;
    @ViewInject(R.id.tv_modify_pwd)
    private TextView tv_modify_pwd;
    @ViewInject(R.id.btn_logout)
    private Button btn_logout;

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    private String birthDay;
    private File tempImage;
    private User user;
    DisplayImageOptions options;

    private int RESIDENT = 1;
    private int STATUS = 2;
    private int SEX = 3;
    private ImageView my_pics_cell;
    private ArrayList<String> pics = new ArrayList<String>();
    private ArrayList<String> pic_ids = new ArrayList<String>();
    LinearLayout llPics;
    ArrayList<LocBean> all_foot_print_list = new ArrayList<LocBean>();
    private int FOOTPRINT = 4;
    private int SIGNATURE = 5;
    private int NICKNAME = 6;
    private int BINDPHONE = 7;
    private int RESET_FOOTPRINT = 8;
    private boolean birthTimeFlag;
    private boolean fromReg;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        initView();
      //  refreshUserInfo();
    }

    private void initView() {
        setContentView(R.layout.activity_account);
        ViewUtils.inject(this);
        findViewById(R.id.ll_nickname).setOnClickListener(this);
        findViewById(R.id.ll_gender).setOnClickListener(this);
        findViewById(R.id.ll_resident).setOnClickListener(this);
        findViewById(R.id.ll_zodiac).setOnClickListener(this);
        findViewById(R.id.ll_photo).setOnClickListener(this);
        findViewById(R.id.ll_plan).setOnClickListener(this);
        findViewById(R.id.ll_foot_print).setOnClickListener(this);
        findViewById(R.id.ll_modify_pwd).setOnClickListener(this);
        findViewById(R.id.ll_bind_phone).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);
        findViewById(R.id.iv_avatar).setOnClickListener(this);

        TitleHeaderBar titleBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
        titleBar.getTitleTextView().setText("编辑资料");
        titleBar.enableBackKey(true);
        titleBar.getLeftTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void getUserPics(Long userId) {
        UserApi.getUserPicAlbumn(String.valueOf(userId), new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    if (jsonObject.getInt("code") == 0) {
                        JSONArray object = jsonObject.getJSONArray("result");
                        /*for (int i = 0; i < object.length(); i++) {
                            JSONArray imgArray = object.getJSONObject(i).getJSONArray("image");
                            pics.add(imgArray.getJSONObject(0).getString("url"));
                            pic_ids.add(object.getJSONObject(i).getString("id"));
                        }
                        initScrollView(pics, pic_ids);*/
                        tv_photo.setText(object.length() + "张");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void doFailure(Exception error, String msg, String method) {
                ToastUtil.getInstance(AccountActvity.this).showToast("好像没有网络额~");
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }


    public void initFlDestion(Map<String, ArrayList<LocBean>> tracks) {

        try {
            JSONObject jsonObject = new JSONObject(tracks.toString());
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                for (int i = 0; i < tracks.get(key).size(); i++) {
                    all_foot_print_list.add(tracks.get(key).get(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initFootPrint(all_foot_print_list);
    }

    private void initFootPrint(final ArrayList<LocBean> prints) {
        //  my_destination.removeAllViews();
        for (int j = 0; j < prints.size(); j++) {
            View contentView = View.inflate(AccountActvity.this, R.layout.des_text_style2, null);
            final TextView cityNameTv = (TextView) contentView.findViewById(R.id.tv_cell_name);
            cityNameTv.setText(prints.get(j).zhName);
            //my_destination.addView(contentView);
        }
    }

    public void initScrollView(final ArrayList<String> picList, final ArrayList<String> ids) {
        //  all_pics.removeAllViews();
        llPics = new LinearLayout(this);
        llPics.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        llPics.removeAllViews();
        for (int i = 0; i <= picList.size(); i++) {
            View view = View.inflate(AccountActvity.this, R.layout.my_all_pics_cell, null);
            my_pics_cell = (ImageView) view.findViewById(R.id.my_pics_cell);
            if (i == picList.size()) {
                my_pics_cell.setImageResource(R.drawable.ic_add_selected);
                my_pics_cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSelectPicDialog();
                    }
                });
            } else {
                final String uri = picList.get(i);
                final String id = ids.get(i);
                final int index = i;
                ImageLoader.getInstance().displayImage(picList.get(i), my_pics_cell, options);
                my_pics_cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showChangePicDialog(picList, id, index);
                    }
                });

            }
            llPics.addView(view);
        }
        //all_pics.addView(llPics);
    }


    @Override
    protected void onResume() {
        super.onResume();
        user = AccountManager.getInstance().getLoginAccount(this);
        // initData();
        refreshUserInfo();
        MobclickAgent.onPageStart("page_edit_my_profile");
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
       MobclickAgent.onPageEnd("page_edit_my_profile");
        MobclickAgent.onPause(this);
    }

    private void bindView(final User user,final long userId) {
        tv_nickname.setText(user.getNickName());
        ImageLoader.getInstance().displayImage(user.getAvatar(), avatarIv, new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.messages_bg_useravatar)
                .showImageOnFail(R.drawable.messages_bg_useravatar)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(
                        getResources().getDimensionPixelSize(R.dimen.user_profile_entry_height)))) // 设置成圆角图片
                .build());
        if (user.getGender().equalsIgnoreCase("M")) {
            iv_header_frame_gender.setImageResource(R.drawable.ic_home_header_boy);
            tv_gender.setText("帅锅");
        } else if (user.getGender().equalsIgnoreCase("F")) {
            iv_header_frame_gender.setImageResource(R.drawable.ic_home_header_girl);
            tv_gender.setText("美女");
        }else if (user.getGender().equalsIgnoreCase("S")){
            iv_header_frame_gender.setImageResource(R.drawable.ic_home_header_unlogin);
            tv_gender.setText("保密");
        }else {
            iv_header_frame_gender.setImageResource(R.drawable.ic_home_header_unlogin);
            tv_gender.setText("一言难尽");
        }

//        ImageView constellationIv = (ImageView) findViewById(R.id.iv_constellation);
//        constellationIv.setImageResource(R.drawable.ic_home_constellation_unknown);
        if (TextUtils.isEmpty(user.getResidence())) {
            tv_resident.setText("未设置");
        } else {
            tv_resident.setText(user.getResidence());
        }
        if (TextUtils.isEmpty(user.getBirthday())) {
            tv_zodiac.setText("未设置");
        } else {
            tv_zodiac.setText(user.getBirthday());
        }

        SpannableString planStr = new SpannableString("");
        planStr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_text_iii)), 0, planStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        planStr.setSpan(new AbsoluteSizeSpan(14, true), 0, planStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder spb = new SpannableStringBuilder();
        spb.append(String.format("%d条", user.getGuideCnt())).append(planStr);
        tv_plan.setText(spb);
        tv_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActvity.this, StrategyListActivity.class);
                intent.putExtra("userId", String.valueOf(userId));
                startActivity(intent);
            }
        });

        SpannableString trackStr = new SpannableString("");
        trackStr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_text_iii)), 0, trackStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        trackStr.setSpan(new AbsoluteSizeSpan(14, true), 0, trackStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        final ArrayList<LocBean> trackCitys = new ArrayList<LocBean>();
        ssb.append(String.format("%d国%d城市", user.getCountryCnt(), user.getTrackCnt())).append(trackStr);
        tv_foot_print.setText(ssb);
        tv_foot_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActvity.this, StrategyMapActivity.class);
                intent.putExtra("isMyFootPrint", true);
                intent.putParcelableArrayListExtra("myfootprint", trackCitys);
                intent.putExtra("title", tv_foot_print.getText().toString());
                startActivityForResult(intent,RESET_FOOTPRINT);
            }
        });

        if (!TextUtils.isEmpty(user.getTel())) {
            tv_bind_phone.setText("已绑定");
        }
        tv_photo.setText(user.getAlbumCnt() + "张");
      //  getUserPics(user.getUserId());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_nickname:
                Intent nickNameIntent = new Intent(mContext, ModifyNicknameActivity.class);
                startActivityForResult(nickNameIntent, NICKNAME);
                break;

//            case R.id.ll_sign:
//                MobclickAgent.onEvent(mContext, "event_update_memo");
//                Intent signIntent = new Intent(mContext, ModifySignActivity.class);
//                startActivityForResult(signIntent, SIGNATURE);
//                break;

            case R.id.ll_gender:
                Intent sexIntent = new Intent(mContext, ModifyStatusOrSexActivity.class);
                sexIntent.putExtra("type", "sex");
                startActivityForResult(sexIntent, SEX);
                break;

//            case R.id.ll_status:
//                Intent statusIntent = new Intent(mContext, ModifyStatusOrSexActivity.class);
//                statusIntent.putExtra("type", "status");
//                startActivityForResult(statusIntent, STATUS);
//                break;

            case R.id.iv_avatar:
                showSelectPicDialog();
                break;

            case R.id.ll_foot_print:
                Intent intent = new Intent(AccountActvity.this, MyFootPrinterActivity.class);
                intent.putParcelableArrayListExtra("myfootprint", all_foot_print_list);
                startActivityForResult(intent, FOOTPRINT);
                break;

            case R.id.ll_zodiac:
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                try{
                    Date date = format.parse(user.getBirthday());
                    calendar.setTime(date);

                }catch(Exception ex){

                }

 //               if(Build.BRAND.equals("Meizu")){
                DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                        if (!birthTimeFlag) {
//                            monthOfYear++;
//                            String dateString = year + "-" + monthOfYear + "-" + dayOfMonth;
//                            System.out.println("datastring  "+dateString);
//                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//                            try {
//                                Date date = format.parse(dateString);
//                                if (date.after(new Date())) {
//                                    ToastUtil.getInstance(AccountActvity.this).showToast("无效的生日设置");
//                                } else {
//                                    setBirthDay(dateString);
//                                }
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                            birthTimeFlag = true;
//                        } else {
//                            birthTimeFlag = false;
//                        }
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE));
               final DatePicker datePicker =dialog.getDatePicker();
//                }else {
//                    dialog = makeDatePicker(new DatePickerDialog.OnDateSetListener() {
//                        @Override
//                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                            if (!birthTimeFlag) {
//                                monthOfYear++;
//                                String dateString = year + "-" + monthOfYear + "-" + dayOfMonth;
//                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//                                try {
//                                    Date date = format.parse(dateString);
//                                    if (date.after(new Date())) {
//                                        ToastUtil.getInstance(AccountActvity.this).showToast("无效的生日设置");
//                                    } else {
//                                        setBirthDay(dateString);
//                                    }
//                                } catch (ParseException e) {
//                                    // TODO Auto-generated catch block
//                                    e.printStackTrace();
//                                }
//                                birthTimeFlag = true;
//                            } else {
//                                birthTimeFlag = false;
//                            }
//                        }
//                    }, 1990, 0, 0);
//                }
//                DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                        if (!birthTimeFlag) {
//                            monthOfYear++;
//                            String dateString = year + "-" + monthOfYear + "-" + dayOfMonth;
//                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//                            try {
//                                Date date = format.parse(dateString);
//                                if (date.after(new Date())) {
//                                    ToastUtil.getInstance(AccountActvity.this).showToast("无效的生日设置");
//                                } else {
//                                    setBirthDay(dateString);
//                                }
//                            } catch (ParseException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//                            birthTimeFlag = true;
//                        } else {
//                            birthTimeFlag = false;
//                        }
//                    }
//                }, 1990, 0, 0);
                // dialog.setTitle("设置生日");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String dateString =  datePicker.getYear() + "-" +  (datePicker.getMonth()+1)+ "-" +  datePicker.getDayOfMonth();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        String  submitStr = dateString;
                        try {
                            Date date = format.parse(dateString);
                            submitStr=format.format(date);
                            if (date.after(new Date())) {
                                ToastUtil.getInstance(AccountActvity.this).showToast("无效的生日设置");
                            } else {
                                setBirthDay(submitStr);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        editBirthdayToInterface(submitStr);
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.setCancelable(true);
                dialog.show();
                break;


            case R.id.ll_resident:
                Intent residentIntent = new Intent(mContext, SelectResidentActivity.class);
                startActivityForResult(residentIntent, RESIDENT);
                break;

            case R.id.ll_modify_pwd:
                Intent modifyPwdIntent = new Intent(mContext, ModifyPwdActivity.class);
                startActivity(modifyPwdIntent);
                break;

            case R.id.ll_bind_phone:
                Intent bindPhoneIntent = new Intent(mContext, PhoneBindActivity.class);
                startActivityForResult(bindPhoneIntent, BINDPHONE);
                break;

            case R.id.ll_photo:
                Intent intent2 = new Intent(AccountActvity.this, CityPictureActivity.class);
                intent2.putExtra("id", String.valueOf(user.getUserId()));
                intent2.putExtra("user_name", user.getNickName());
                intent2.putExtra("isUserPics", true);
                startActivity(intent2);
                break;

            case R.id.btn_logout:
                warnLogout();
                break;
        }
    }


    private void refreshUserInfo() {
       final User user = AccountManager.getInstance().getLoginAccount(this);
        if (user != null) {
            if (user.getGender().equalsIgnoreCase("M")) {
                iv_header_frame_gender.setImageResource(R.drawable.ic_home_header_boy);
            } else if (user.getGender().equalsIgnoreCase("F")) {
                iv_header_frame_gender.setImageResource(R.drawable.ic_home_header_girl);
            } else {
                iv_header_frame_gender.setImageResource(R.drawable.ic_home_header_unlogin);
            }

            UserApi.getUserInfo(String.valueOf(user.getUserId()), new HttpCallBack<String>() {
                @Override
                public void doSuccess(String result, String method) {
                    CommonJson<User> userResult = CommonJson.fromJson(result, User.class);
                    if (userResult.code == 0) {
                        AccountManager.getInstance().saveLoginAccount(mContext, userResult.result);
                        bindView(userResult.result,user.getUserId());
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
    }

    private void warnLogout() {
        final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
        dialog.setTitle("提示");
        dialog.setMessage("确定退出登录");
        dialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                try {
                    DialogManager.getInstance().showLoadingDialog(mContext, "正在登出");
                }catch (Exception e){
                    DialogManager.getInstance().dissMissLoadingDialog();
                }
                UserApi.logout(AccountManager.getInstance().getLoginAccount(AccountActvity.this).getUserId(), new HttpCallBack() {
                    @Override
                    public void doSuccess(Object result, String method) {
                        AccountManager.getInstance().logout(mContext);
                        DialogManager.getInstance().dissMissLoadingDialog();
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {

                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method, int code) {

                    }
                });


            }
        });
        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void showChangePicDialog(final ArrayList<String> urls, final String id, final int index) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View contentView = View.inflate(this,
                R.layout.dialog_change_user_pic, null);
        Button changeBtn = (Button) contentView
                .findViewById(R.id.btn_pic_touserpic);
        Button zoomBig = (Button) contentView.findViewById(R.id.btn_pic_zoom_big);
        Button delBtn = (Button) contentView.findViewById(R.id.btn_del_user_pic_album);
        Button cancleBtn = (Button) contentView.findViewById(R.id.btn_user_pic_cancle);
        changeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                changeUserAvatar(urls.get(index));

            }
        });

        zoomBig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                IntentUtils.intentToPicGallery2(AccountActvity.this, urls, 0);

            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //删除接口
                delThisPic(id, index);
                dialog.dismiss();

            }
        });
        cancleBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Window window = dialog.getWindow();
        window.setContentView(contentView);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = display.getWidth(); // 设置宽度
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }


    public void delThisPic(String picId, final int pic_index) {
        if (!CommonUtils.isNetWorkConnected(this)) {
            ToastUtil.getInstance(this).showToast("无网络连接，请检查网络");
            return;
        }
        try {
            DialogManager.getInstance().showLoadingDialog(mContext, "请稍后");
        }catch (Exception e){
            DialogManager.getInstance().dissMissLoadingDialog();
        }
        UserApi.delUserAlbumPic(String.valueOf(user.getUserId()), picId, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                pic_ids.remove(pic_index);
                pics.remove(pic_index);
                initScrollView(pics, pic_ids);
                ToastUtil.getInstance(mContext).showToast("删除成功");
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(mContext).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }

            @Override
            public void onStart() {
            }
        });

    }


    private void showSelectPicDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View contentView = View.inflate(this,
                R.layout.dialog_select_pic, null);
        Button cameraBtn = (Button) contentView
                .findViewById(R.id.btn_camera);
        Button localBtn = (Button) contentView.findViewById(R.id.btn_local);
        Button cancleBtn = (Button) contentView.findViewById(R.id.btn_cancle);
        cameraBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tempImage = SelectPicUtils.getInstance().selectPicFromCamera(AccountActvity.this);
                dialog.dismiss();

            }
        });
        localBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tempImage = SelectPicUtils.getInstance().selectZoomPicFromLocal(AccountActvity.this);
                dialog.dismiss();

            }
        });
        cancleBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        // dialog.setView(contentView);
        // dialog.setContentView(contentView);
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Window window = dialog.getWindow();
        window.setContentView(contentView);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = display.getWidth(); // 设置宽度
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }

    private void modifyGender(final String gender) {
        if (!CommonUtils.isNetWorkConnected(mContext)) {
            ToastUtil.getInstance(mContext).showToast("无网络连接，请检查网络");
            return;
        }
        try {
            DialogManager.getInstance().showLoadingDialog(mContext, "请稍后");
        }catch (Exception e){
            DialogManager.getInstance().dissMissLoadingDialog();
        }
        UserApi.editUserGender(user, gender, new HttpCallBack<String>() {


            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result, ModifyResult.class);
                if (modifyResult.code == 0) {
                    user.setGender(gender);
                    AccountManager.getInstance().saveLoginAccount(mContext, user);
                    if (gender.equalsIgnoreCase("M")) {
                        iv_header_frame_gender.setImageResource(R.drawable.ic_home_header_boy);
                        tv_gender.setText("帅锅");
                    } else if (gender.equalsIgnoreCase("F")) {
                        iv_header_frame_gender.setImageResource(R.drawable.ic_home_header_girl);
                        tv_gender.setText("美女");
                    } else if (user.getGender().equalsIgnoreCase("S")) {
                        iv_header_frame_gender.setImageResource(R.drawable.ic_home_header_unlogin);
                        tv_gender.setText("保密");
                    } else {
                        iv_header_frame_gender.setImageResource(R.drawable.ic_home_header_unlogin);
                        tv_gender.setText("一言难尽");
                    }

//                    ToastUtil.getInstance(mContext).showToast("修改成功");
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(mContext).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }

            @Override
            public void onStart() {
            }
        });
    }

    private void showSelectGenderDialog() {
        String[] names = {"美女", "帅锅", "不告诉你"};
        final MoreDialog dialog = new MoreDialog(AccountActvity.this);
        dialog.setMoreStyle(false, 3, names);

        /*final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View contentView = View.inflate(this, R.layout.dialog_select_gender, null);
        Button ladyBtn = (Button) contentView.findViewById(R.id.gender_lady);
        Button manBtn = (Button) contentView.findViewById(R.id.gender_man);
        Button unknown = (Button) contentView.findViewById(R.id.gender_unknown);
        Button cancel = (Button) contentView.findViewById(R.id.btn_cancle);*/
        dialog.getTv2().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                modifyGender("F");
            }
        });
        dialog.getTv3().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                modifyGender("M");

            }
        });
        dialog.getTv4().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                modifyGender("U");
            }
        });
        dialog.show();

    }

    private void uploadAvatar(final File file) {
        final CustomLoadingDialog progressDialog = DialogManager.getInstance().showLoadingDialog(mContext, "0%");
        OtherApi.getAvatarUploadToken(new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson<UploadTokenBean> tokenResult = CommonJson.fromJson(result, UploadTokenBean.class);
                if (tokenResult.code == 0) {
                    String token = tokenResult.result.uploadToken;
                    String key = tokenResult.result.key;
                    UploadManager uploadManager = new UploadManager();
                    uploadManager.put(file, key, token,
                            new UpCompletionHandler() {
                                @Override
                                public void complete(String key, ResponseInfo info, JSONObject response) {
                                    DialogManager.getInstance().dissMissLoadingDialog();
                                    if (info.isOK()) {
                                        LogUtil.d(response.toString());
                                        try {
                                            String imageUrl = response.getString("url");
                                        /*//很明显这段的代码是更改用户的头像的作用

                                            String urlSmall = response.getString("urlSmall");
                                            user.setAvatar(imageUrl);
                                            //user.avatar = imageUrl;
                                            user.setAvatarSmall(urlSmall);
                                            AccountManager.getInstance().saveLoginAccount(mContext, user);*/


                                            changeUserAvatar(imageUrl);
                                        /*pics.add(Uri.fromFile(file).toString());
                                        try {
                                            pic_ids.add(response.getString("id"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        initScrollView(pics, pic_ids);*/
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }
                            }, new UploadOptions(null, null, false,
                                    new UpProgressHandler() {
                                        public void progress(String key, double percent) {
                                            progressDialog.setContent((int) (percent * 100) + "%");
                                            LogUtil.d("progress", percent + "");
                                        }
                                    }, null));
                } else {
                    DialogManager.getInstance().dissMissLoadingDialog();
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(AccountActvity.this).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }


    private void changeUserAvatar(final String url) {
        if (!CommonUtils.isNetWorkConnected(mContext)) {
            ToastUtil.getInstance(mContext).showToast("无网络连接，请检查网络");
            return;
        }
        try {
            DialogManager.getInstance().showLoadingDialog(mContext, "请稍后");
        }catch (Exception e){
            DialogManager.getInstance().dissMissLoadingDialog();
        }
        UserApi.editUserAvatar(user, url, new HttpCallBack<String>() {


            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result, ModifyResult.class);
                if (modifyResult.code == 0) {
                    user.setAvatar(url);
                    try {
                        AccountManager.getInstance().getLoginAccountInfo().setAvatar(url);
                        AccountManager.getInstance().saveLoginAccount(mContext, user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ImageLoader.getInstance().displayImage(user.getAvatar(), avatarIv, new DisplayImageOptions.Builder()
                            .showImageForEmptyUri(R.drawable.messages_bg_useravatar)
                            .showImageOnFail(R.drawable.messages_bg_useravatar)
                            .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                            .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                            .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(
                                    getResources().getDimensionPixelSize(R.dimen.user_profile_entry_height)))) // 设置成圆角图片
                            .build());
//                    ToastUtil.getInstance(mContext).showToast("修改成功");
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(mContext).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }

            @Override
            public void onStart() {
            }
        });
    }


    /*private void changeUserAvatar(final File file){
        final CustomLoadingDialog progressDialog = DialogManager.getInstance().showLoadingDialog(mContext,"0%");
        OtherApi.getAvatarUploadToken(new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson<UploadTokenBean> tokenResult = CommonJson.fromJson(result, UploadTokenBean.class);
                if (tokenResult.code == 0) {
                    String token = tokenResult.result.uploadToken;
                    String key = tokenResult.result.key;
                    UploadManager uploadManager = new UploadManager();
                    uploadManager.put(file, key, token,
                            new UpCompletionHandler() {
                                @Override
                                public void complete(String key, ResponseInfo info, JSONObject response) {
                                    DialogManager.getInstance().dissMissLoadingDialog();
                                    if (info.isOK()) {
                                        LogUtil.d(response.toString());
                                        try {
                                            //很明显这段的代码是更改用户的头像的作用
                                            String imageUrl = response.getString("url");
                                            String urlSmall = response.getString("urlSmall");
                                            user.avatar = imageUrl;
                                            user.avatarSmall = urlSmall;
                                            AccountManager.getInstance().saveLoginAccount(mContext, user);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }
                            }, new UploadOptions(null, null, false,
                                    new UpProgressHandler() {
                                        public void progress(String key, double percent) {
                                            progressDialog.setContent((int) (percent * 100) + "%");
                                            LogUtil.d("progress", percent + "");
                                        }
                                    }, null));
                } else {
                    DialogManager.getInstance().dissMissLoadingDialog();
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(AccountActvity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == SelectPicUtils.REQUEST_CODE_CAMERA) { // 发送照片
            if (tempImage != null && tempImage.exists()) {
                SelectPicUtils.getInstance().startPhotoZoom(this, Uri.fromFile(tempImage));

            }
        } else if (requestCode == SelectPicUtils.REQUEST_CODE_LOCAL_ZOOM) {
            if (tempImage != null) {
                uploadAvatar(tempImage);
            }
        } else if (requestCode == SelectPicUtils.REQUEST_CODE_ZOOM) {
            if (tempImage != null) {
                uploadAvatar(tempImage);

            }
        } else if (requestCode == RESIDENT) {
            editResidenceToInterface(data.getExtras().getString("result"));
            //接口
        } else if (requestCode == SEX) {
            String sex = data.getExtras().getString("result");
            if (TextUtils.isEmpty(sex)) {
                return;
            } else if (sex.equals("美女")) {
                modifyGender("F");
            } else if (sex.equals("帅锅")) {
                modifyGender("M");
            } else if (sex.equals("一言难尽")) {
                modifyGender("U");
            } else if (sex.equals("保密")) {
                modifyGender("S");
            }
        } else if (requestCode == STATUS) {
            editStatusToInterface(data.getExtras().getString("result"));
        } else if (requestCode == FOOTPRINT) {
            all_foot_print_list = data.getParcelableArrayListExtra("footprint");
            initFootPrint(all_foot_print_list);
        } else if (requestCode == SIGNATURE) {
            //signTv.setText(data.getExtras().getString("signature"));
        } else if (requestCode == NICKNAME) {
            tv_nickname.setText(data.getExtras().getString("nickname"));
        } else if (requestCode == BINDPHONE) {
            //  bindPhoneTv.setText(data.getExtras().getString("bindphone"));
        } else if (requestCode == RESET_FOOTPRINT){
            //updateFootPrint from
        }
    }

    private void editStatusToInterface(final String sstatus) {
        if (!CommonUtils.isNetWorkConnected(mContext)) {
            ToastUtil.getInstance(mContext).showToast("无网络连接，请检查网络");
            return;
        }
        try {
            DialogManager.getInstance().showLoadingDialog(mContext, "请稍后");
        }catch (Exception e){
            DialogManager.getInstance().dissMissLoadingDialog();
        }
        UserApi.editUserStatus(user, sstatus, new HttpCallBack<String>() {


            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result, ModifyResult.class);
                if (modifyResult.code == 0) {
                    user.setTravelStatus(sstatus);
                    AccountManager.getInstance().saveLoginAccount(mContext, user);
                    //status.setText(sstatus);
//                    ToastUtil.getInstance(mContext).showToast("修改成功");
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(mContext).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }

            @Override
            public void onStart() {
            }
        });
    }

    private void editResidenceToInterface(final String residence) {
        if (!CommonUtils.isNetWorkConnected(mContext)) {
            ToastUtil.getInstance(mContext).showToast("无网络连接，请检查网络");
            return;
        }
        try {
            DialogManager.getInstance().showLoadingDialog(mContext, "请稍后");
        }catch (Exception e){
            DialogManager.getInstance().dissMissLoadingDialog();
        }
        UserApi.editUserResidence(user, residence, new HttpCallBack<String>() {


            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result, ModifyResult.class);
                if (modifyResult.code == 0) {
                    user.setResidence(residence);
                    AccountManager.getInstance().saveLoginAccount(mContext, user);
                    tv_resident.setText(residence);
//                    ToastUtil.getInstance(mContext).showToast("修改成功");
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(mContext).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }

            @Override
            public void onStart() {
            }
        });
    }


    private void editBirthdayToInterface(final String birth) {
        if (!CommonUtils.isNetWorkConnected(mContext)) {
            ToastUtil.getInstance(mContext).showToast("无网络连接，请检查网络");
            return;
        }
        try {
            DialogManager.getInstance().showLoadingDialog(mContext, "请稍后");
        }catch (Exception e){
            DialogManager.getInstance().dissMissLoadingDialog();
        }
        UserApi.editUserBirthday(user, birth, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result, ModifyResult.class);
                if (modifyResult.code == 0) {
                    user.setBirthday(birth);
                    AccountManager.getInstance().saveLoginAccount(mContext, user);
                    tv_zodiac.setText(birth);
//                    ToastUtil.getInstance(mContext).showToast("修改成功");
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(mContext).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }

            @Override
            public void onStart() {
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

    public DatePickerDialog makeDatePicker(DatePickerDialog.OnDateSetListener listener, int y, int m, int day) {
        DatePickerDialog newFragment = new DatePickerDialog(this, listener, y, m, day);

        // removes the original topbar:
        newFragment.setTitle("");

        // Divider changing:
        DatePicker dpView = newFragment.getDatePicker();
        LinearLayout llFirst = (LinearLayout) dpView.getChildAt(0);
        LinearLayout llSecond = (LinearLayout) llFirst.getChildAt(0);
        for (int i = 0; i < llSecond.getChildCount(); i++) {
            NumberPicker picker = (NumberPicker) llSecond.getChildAt(i);
            Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for (Field pf : pickerFields) {
                if (pf.getName().equals("mSelectionDivider")) {
                    pf.setAccessible(true);
                    try {
                        pf.set(picker, getResources().getDrawable(R.drawable.divider));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        // New top:
        int titleHeight = 90;
        // Container:
        LinearLayout llTitleBar = new LinearLayout(this);
        llTitleBar.setOrientation(LinearLayout.VERTICAL);
        llTitleBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, titleHeight));

        // TextView Title:
        TextView tvTitle = new TextView(this);
        tvTitle.setText("设置生日");
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setPadding(10, 10, 10, 10);
        tvTitle.setTextSize(18);
        tvTitle.setTextColor(Color.BLACK);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, titleHeight - 2));
        llTitleBar.addView(tvTitle);

        // View line:
        View vTitleDivider = new View(this);
        vTitleDivider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 4));
        vTitleDivider.setBackgroundColor(getResources().getColor(R.color.app_theme_color));
        llTitleBar.addView(vTitleDivider);

        dpView.addView(llTitleBar);
        FrameLayout.LayoutParams lp = (android.widget.FrameLayout.LayoutParams) llFirst.getLayoutParams();
        lp.setMargins(0, titleHeight, 0, 0);
        return newFragment;
    }
}
