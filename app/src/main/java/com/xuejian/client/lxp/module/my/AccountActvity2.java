package com.xuejian.client.lxp.module.my;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.easemob.EMCallBack;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.bean.PeachUser;
import com.xuejian.client.lxp.bean.UploadTokenBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.CustomLoadingDialog;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.SelectPicUtils;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.module.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/10/11.
 */
public class AccountActvity2 extends PeachBaseActivity implements View.OnClickListener {


    @InjectView(R.id.ly_header_bar_title_wrap)
    TitleHeaderBar lyHeaderBarTitleWrap;
    /*@InjectView(R.id.rv_photos)
    RecyclerView rvPhotos;*/
    @InjectView(R.id.tv_nickname)
    TextView tvNickname;
    @InjectView(R.id.ll_nickname)
    LinearLayout llNickname;
    @InjectView(R.id.tv_status)
    TextView tvStatus;
    @InjectView(R.id.ll_status)
    LinearLayout llStatus;
    @InjectView(R.id.tv_sign)
    TextView tvSign;
    @InjectView(R.id.ll_sign)
    LinearLayout llSign;
    @InjectView(R.id.ll_modify_pwd)
    TextView llModifyPwd;
    @InjectView(R.id.tv_bind_phone)
    TextView tvBindPhone;
    @InjectView(R.id.tv_phone)
    TextView tvPhone;
    @InjectView(R.id.ll_bind_phone)
    LinearLayout llBindPhone;
    @InjectView(R.id.tv_gender)
    TextView tvGender;
    @InjectView(R.id.ll_gender)
    LinearLayout llGender;
    @InjectView(R.id.tv_birthday)
    TextView tvBirthday;
    @InjectView(R.id.ll_birthday)
    LinearLayout llBirthday;
    @InjectView(R.id.tv_resident)
    TextView tvResident;
    @InjectView(R.id.ll_resident)
    LinearLayout llResident;
    @InjectView(R.id.btn_logout)
    Button btnLogout;
    private File tempImage;
    private PeachUser user;
    DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        initView();
        refreshUserInfo();

    }

    private void initView() {
        setContentView(R.layout.activity_account2);
        ButterKnife.inject(this);
        findViewById(R.id.ll_nickname).setOnClickListener(this);
        findViewById(R.id.ll_sign).setOnClickListener(this);
        findViewById(R.id.ll_gender).setOnClickListener(this);
        findViewById(R.id.ll_modify_pwd).setOnClickListener(this);
        findViewById(R.id.ll_bind_phone).setOnClickListener(this);
        llBirthday.setOnClickListener(this);
        llResident.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

        TitleHeaderBar titleBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
        titleBar.getTitleTextView().setText("个人信息");
        titleBar.enableBackKey(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
//        MobclickAgent.onPageStart("page_personal_profile");
    }


    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_personal_profile");
    }

    private void bindView(PeachUser user) {
        tvNickname.setText(user.nickName);
        tvGender.setText(user.getGenderDesc());
        tvSign.setText(user.signature);
        tvPhone.setText(user.tel);
    }

    private void initData() {
        user = AccountManager.getInstance().getLoginAccount(this);
        bindView(user);


//        if(TextUtils.isEmpty(user.tel)){
//            modifPwdLl.setVisibility(View.GONE);
//            bindPhoneTv.setText("绑定手机");
//        }else{
//            modifPwdLl.setVisibility(View.VISIBLE);
//            bindPhoneTv.setText("更改手机");
//            phoneTv.setText(user.tel);
//        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_nickname:
                MobclickAgent.onEvent(mContext, "event_update_nick");
                Intent nickNameIntent = new Intent(mContext, ModifyNicknameActivity.class);
                startActivity(nickNameIntent);
                break;

            case R.id.ll_sign:
                MobclickAgent.onEvent(mContext, "event_update_memo");
                Intent signIntent = new Intent(mContext, ModifySignActivity.class);
                startActivity(signIntent);
                break;

            case R.id.ll_gender:
                MobclickAgent.onEvent(mContext, "event_update_gender");
                showSelectGenderDialog();
                break;

           /* case R.id.ll_avatar:
                MobclickAgent.onEvent(mContext, "event_update_avatar");
                showSelectPicDialog();
                break;*/

            case R.id.ll_modify_pwd:
                MobclickAgent.onEvent(mContext, "event_update_password");
                Intent modifyPwdIntent = new Intent(mContext, ModifyPwdActivity.class);
                startActivity(modifyPwdIntent);
                break;

            case R.id.ll_bind_phone:
                MobclickAgent.onEvent(mContext, "event_update_phone");
                Intent bindPhoneIntent = new Intent(mContext, PhoneBindActivity.class);
                startActivity(bindPhoneIntent);
                break;

            case R.id.ll_birthday:
                DatePickerDialog dialog = new DatePickerDialog(mContext,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    }
                },1990,0,0);
                dialog.show();
                break;

            case R.id.ll_resident:
                break;

            case R.id.btn_logout:
                MobclickAgent.onEvent(mContext, "event_logout");
                warnLogout();
                break;
        }
    }

    private void refreshUserInfo() {
        PeachUser user = AccountManager.getInstance().getLoginAccount(this);
        if (user != null) {
            UserApi.getUserInfo(user.userId + "", new HttpCallBack<String>() {
                @Override
                public void doSucess(String result, String method) {
                    CommonJson<PeachUser> userResult = CommonJson.fromJson(result, PeachUser.class);
                    if (userResult.code == 0) {
                        AccountManager.getInstance().saveLoginAccount(mContext, userResult.result);
                        bindView(userResult.result);
                    }

                }

                @Override
                public void doFailure(Exception error, String msg, String method) {

                }
            });
        }
    }

    private void warnLogout() {
        final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
        dialog.setTitle("提示");
        dialog.setTitleIcon(R.drawable.ic_dialog_tip);
        dialog.setMessage("确定退出已登陆账号吗？");
        dialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DialogManager.getInstance().showLoadingDialog(mContext, "正在登出");
                AccountManager.getInstance().logout(mContext, false, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                Intent intent = new Intent(mContext, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                AccountActvity2.this.finish();

                            }
                        });
                    }

                    @Override
                    public void onError(int i, String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.getInstance(AccountActvity2.this).showToast("呃～网络好像找不到了");
                                dialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {

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
                tempImage = SelectPicUtils.getInstance().selectPicFromCamera(AccountActvity2.this);
                dialog.dismiss();

            }
        });
        localBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tempImage = SelectPicUtils.getInstance().selectZoomPicFromLocal(AccountActvity2.this);
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
        lp.width = (int) (display.getWidth()); // 设置宽度
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }

    private void modifyGender(final String gender) {
        if (!CommonUtils.isNetWorkConnected(mContext)) {
            ToastUtil.getInstance(mContext).showToast("无网络连接，请检查网络");
            return;
        }
        DialogManager.getInstance().showLoadingDialog(mContext, "请稍后");
        UserApi.editUserGender(user, gender, new HttpCallBack<String>() {


            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result, ModifyResult.class);
                if (modifyResult.code == 0) {
                    user.gender = gender;
                    AccountManager.getInstance().saveLoginAccount(mContext, user);
                    tvGender.setText(user.getGenderDesc());
                    ToastUtil.getInstance(mContext).showToast("修改成功");
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(mContext).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void onStart() {
            }
        });
    }

    private void showSelectGenderDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View contentView = View.inflate(this, R.layout.dialog_select_gender, null);
        Button ladyBtn = (Button) contentView.findViewById(R.id.gender_lady);
        Button manBtn = (Button) contentView.findViewById(R.id.gender_man);
        Button unknown = (Button) contentView.findViewById(R.id.gender_unknown);
        Button cancel = (Button) contentView.findViewById(R.id.btn_cancle);
        ladyBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                modifyGender("F");
            }
        });
        manBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                modifyGender("M");

            }
        });
        unknown.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                modifyGender("U");
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {

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
        lp.width = (int) (display.getWidth()); // 设置宽度
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }

    private void uploadAvatar(final File file) {
        final CustomLoadingDialog progressDialog = DialogManager.getInstance().showLoadingDialog(mContext, "0%");
        OtherApi.getAvatarUploadToken(new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
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
                                            String urlSmall = response.getString("urlSmall");
                                            user.avatar = imageUrl;
                                            user.avatarSmall = urlSmall;
                                            AccountManager.getInstance().saveLoginAccount(mContext, user);
//                                            ImageLoader.getInstance().displayImage(Uri.fromFile(file).toString(), avatarIv, options);
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
                    ToastUtil.getInstance(AccountActvity2.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }

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


        }
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
